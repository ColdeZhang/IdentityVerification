package site.deercloud.identityverification.HttpServer.model;

import site.deercloud.identityverification.Utils.RandomCode;

public class InviteCode {
    public String code;
    public String inviter;
    public String invitee;
    public Long createTime;
    public Boolean isUsed = false;
    public Long usedTime;

    public static InviteCode CreateWith(String ownerUuid) {
        InviteCode code = new InviteCode();
        code.code = RandomCode.NewCodeWithAlphabet(8);
        code.inviter = ownerUuid;
        code.isUsed = false;
        code.invitee = "";
        code.usedTime = 0L;
        return code;
    }
}
