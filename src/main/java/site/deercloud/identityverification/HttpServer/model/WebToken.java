package site.deercloud.identityverification.HttpServer.model;

public class WebToken {
    public String token;
    public String userUUID;
    public Long expiresAt;

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
