package site.deercloud.identityverification.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;

// 作者: https://www.jianshu.com/p/c7d09ed656b1
public class SignatureUtil {
    private final static String SIGN_TYPE_RSA = "RSA";
    private final static String SIGN_ALGORITHMS = "SHA1WithRSA";
    private final static String CHARSETTING = "UTF-8";

    /**
     * 获取私钥PKCS8格式（需base64）
     * @param algorithm
     * @param priKey
     * @return PrivateKey
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm, String priKey) throws Exception {
        if (algorithm == null || "".equals(algorithm) || priKey == null || "".equals(priKey))
            return null;

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);  // 获取密钥工厂

        byte[] encodedKey = StreamUtil.readText(new ByteArrayInputStream(priKey.getBytes())).getBytes();
        encodedKey = Base64.decodeBase64(priKey.getBytes());

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    /**
     * 通过证书获取公钥（需BASE64，X509为通用证书标准）
     * @param algorithm
     * @param pubKey
     * @return PublicKey
     * @throws Exception
     */
    public static PublicKey getPublicKeyFromX509(String algorithm, String pubKey) throws Exception {

        if (algorithm == null || "".equals(algorithm) || pubKey == null || "".equals(pubKey))
            return null;

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(new ByteArrayInputStream(pubKey.getBytes())), writer);

        byte[] encodeByte = writer.toString().getBytes();
        encodeByte = Base64.decodeBase64(pubKey.getBytes());

        return keyFactory.generatePublic(new X509EncodedKeySpec(encodeByte));
    }

    /**
     * 使用私钥对字符进行签名
     * @param plain  内容体
     * @param prikey  私钥
     * @return String
     * @throws Exception
     */
    public static String sign(String plain, String prikey) throws Exception {
        if (plain == null || "".equals(plain) || prikey == null || "".equals(prikey))
            return null;

        PrivateKey privatekey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA, prikey);
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initSign(privatekey);
        signature.update(plain.getBytes(CHARSETTING));
        byte[] signed = signature.sign();

        return new String(Base64.encodeBase64(signed));
    }

    /**
     * 将内容体、签名信息、及对方公钥进行验签
     * @param plain  内容体
     * @param sign   签名信息
     * @param pubkey  对方公钥
     * @return boolean
     * @throws Exception
     */
    public static boolean virefy(String plain, String sign, String pubkey) throws Exception {
        if (plain == null || "".equals(plain) || sign == null || "".equals(sign) || pubkey == null || "".equals(pubkey))
            return false;

        PublicKey publicKey = getPublicKeyFromX509(SIGN_TYPE_RSA, pubkey);
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initVerify(publicKey);
        signature.update(plain.getBytes(CHARSETTING));

        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }
}
