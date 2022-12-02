package site.deercloud.identityverification.HttpServer.model;

public class InviteCode {
    public String code;
    public String inviter;
    public Long createTime;
    public Boolean isUsed;
    public Long usedTime;
}
