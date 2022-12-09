package site.deercloud.identityverification.Controller;

import site.deercloud.identityverification.HttpServer.model.EmailCode;
import site.deercloud.identityverification.IdentityVerification;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class EmailCodeCache {
    public static Map<String, EmailCode> EMAIL_CODE_CACHE = new ConcurrentHashMap<>();

    public static void addEmailCode(String email, String code) {
        EMAIL_CODE_CACHE.put(email, new EmailCode(email, code));
    }

    public static Optional<EmailCode> getEmailCode(String email) {
        return Optional.ofNullable(EMAIL_CODE_CACHE.get(email));
    }

    public static void removeEmailCode(String email) {
        EMAIL_CODE_CACHE.remove(email);
    }

    public static boolean isEmailCodeExpired(String email) {
        if (IdentityVerification.configManager.getDebug()) return false;
        return getEmailCode(email).map(EmailCode::isExpired).orElse(true);
    }

    public static boolean isEmailCodeValid(String email, String code) {
        if (IdentityVerification.configManager.getDebug()) return true;
        return getEmailCode(email).map(emailCode -> emailCode.code.equals(code)).orElse(false);
    }
}
