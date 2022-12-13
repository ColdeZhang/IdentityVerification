package site.deercloud.identityverification.Controller;

import site.deercloud.identityverification.HttpServer.model.WebToken;

import java.util.HashSet;
import java.util.Set;

public class WebTokenCache {
    private static final Set<WebToken> cache = new HashSet<>();

    public static void addWebToken(WebToken webToken) {
        cache.add(webToken);
    }

    public static void removeWebToken(WebToken webToken) {
        cache.remove(webToken);
    }

    public static boolean isWebTokenValid(WebToken webToken) {
        if (!cache.contains(webToken)) {
            return false;
        }
        if (webToken.expiresAt < System.currentTimeMillis()) {
            removeWebToken(webToken);
            return false;
        }
        return true;
    }
}
