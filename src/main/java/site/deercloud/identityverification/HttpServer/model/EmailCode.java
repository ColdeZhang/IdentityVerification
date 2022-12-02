package site.deercloud.identityverification.HttpServer.model;

public class EmailCode {
    public String email;
    public String code;
    public Long expireTime;

    public EmailCode(String email, String code) {
        this.email = email;
        this.code = code;
        this.expireTime = System.currentTimeMillis() + 1000 * 60 * 5;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }
}
