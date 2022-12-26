package site.deercloud.identityverification.HttpServer.model;

import com.alibaba.fastjson.JSONObject;
import site.deercloud.identityverification.Controller.WebTokenCache;
import site.deercloud.identityverification.Utils.RandomCode;

public class WebToken {
    public String token;
    public String userUUID;
    public Long expiresAt;

    public static WebToken newWebToken(String userUUID) {
        WebToken webToken = new WebToken();
        webToken.userUUID = userUUID;
        webToken.token = RandomCode.NewCodeWithAlphabet(32);
        webToken.expiresAt = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7;
        return webToken;
    }

    public static String ParseWebTokenToStr(WebToken webToken) {
        return "Bearer " + webToken.token + ";" + webToken.userUUID + ";" + webToken.expiresAt.toString() + ";";
    }

    public static WebToken ParseWebTokenFromStr(String str) {
        String[] split = str.replace("Bearer ", "").split(";");
        WebToken webToken = new WebToken();
        webToken.token = split[0];
        webToken.userUUID = split[1];
        webToken.expiresAt = Long.parseLong(split[2]);
        if (webToken.token == null || webToken.userUUID == null) {
            return null;
        }
        return webToken;
    }
}
