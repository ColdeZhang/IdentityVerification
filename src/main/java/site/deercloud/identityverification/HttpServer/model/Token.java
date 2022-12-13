package site.deercloud.identityverification.HttpServer.model;

// 外置登录token
public class Token {
    public String accessToken;
    public String clientToken;
    public String profileUUID;

    public String userUUID;
    public Long expiresAt;
}
