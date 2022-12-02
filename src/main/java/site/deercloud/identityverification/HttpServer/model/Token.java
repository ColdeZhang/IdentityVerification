package site.deercloud.identityverification.HttpServer.model;

public class Token {
    public String accessToken;
    public String clientToken;
    public String profileUUID;

    public String userUUID;
    public Long expiresAt;
}
