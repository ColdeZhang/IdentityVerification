package site.deercloud.identityverification.Controller;

import site.deercloud.identityverification.HttpServer.model.WebToken;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// 网页登录的Token管理
public class WebTokenCache {
    private static final Map<String, WebToken> cache = new HashMap<>();

    public static void addWebToken(WebToken webToken) {
        cache.put(webToken.userUUID, webToken);
    }

    public static void removeWebToken(WebToken webToken) {
        cache.remove(webToken.userUUID);
    }

    public static boolean isWebTokenValid(WebToken webToken) {
        if (!cache.containsKey(webToken.userUUID)) {
            return false;
        }
        if (!cache.get(webToken.userUUID).token.equals(webToken.token)) {
            return false;
        }
        if (webToken.expiresAt < System.currentTimeMillis()) {
            removeWebToken(webToken);
            return false;
        }
        return true;
    }
}
