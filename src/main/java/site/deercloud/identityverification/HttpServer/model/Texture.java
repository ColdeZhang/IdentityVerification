package site.deercloud.identityverification.HttpServer.model;

import com.alibaba.fastjson.JSONObject;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.Utils.SignatureUtil;
import site.deercloud.identityverification.Utils.UnsignedUUID;

import java.util.Base64;

public class Texture {
    public String profileId;
    public String profileName;
    public Long timestamp;
    public String skinUrl;
    public String capeUrl;

    public static Texture newDefault(String Name) {
        Texture texture = new Texture();
        texture.profileName = Name;
        texture.profileId = UnsignedUUID.GenerateUUID();
        texture.timestamp = System.currentTimeMillis();
        texture.skinUrl = "https://textures.minecraft.net/texture/60a5bd016b3c9a1b9272e4929e30827a67be4ebb219017adbbc4a4d22ebd5b1";
        texture.capeUrl = null;
        return texture;
    }

    public String serialWithBase64() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timestamp", timestamp);
        jsonObject.put("profileId", profileId);
        jsonObject.put("profileName", profileName);
        JSONObject textures = new JSONObject();
        JSONObject skin = new JSONObject();
        skin.put("url", skinUrl);
        textures.put("SKIN", skin);
        if (capeUrl != null) {
            JSONObject cape = new JSONObject();
            cape.put("url", capeUrl);
            textures.put("CAPE", cape);
        }
        jsonObject.put("textures", textures);
        return Base64.getEncoder().encodeToString(jsonObject.toJSONString().getBytes());
    }

    public String sign() throws Exception {
        // 使用SHA1withRSA算法签名
        String private_key_str = IdentityVerification.getInstance().getConfigManager().getSignaturePrivateKey();
        return SignatureUtil.sign(serialWithBase64(), private_key_str);
    }
}
