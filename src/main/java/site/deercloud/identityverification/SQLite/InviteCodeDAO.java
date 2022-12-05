package site.deercloud.identityverification.SQLite;

import site.deercloud.identityverification.HttpServer.model.InviteCode;
import site.deercloud.identityverification.Utils.MyLogger;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class InviteCodeDAO {
    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS invite_code (\n"
                + "	code text PRIMARY KEY,\n"              // 邀请码
                + "	inviter_uuid text NOT NULL,\n"         // 所有者
                + "	create_time integer NOT NULL,\n"       // 创建时间
                + "	is_used integer NOT NULL,\n"           // 是否被使用
                + "	use_time integer,\n"                   // 使用时间
                + " invitee text \n"                       // 使用者
                + ");";
        Statement stat = null;
        stat = SqlManager.session.createStatement();
        stat.executeUpdate(sql);
    }

    public static void insert(InviteCode code) throws SQLException {
        String sql = "INSERT INTO invite_code(code,inviter_uuid,create_time,is_used,use_time,invitee) VALUES(?,?,?,?,?,?)";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, code.code);
        prep.setString(2, code.inviter);
        prep.setLong(3, System.currentTimeMillis());
        prep.setBoolean(4, code.isUsed);
        prep.setLong(5, code.usedTime);
        prep.setString(6, code.invitee);
        prep.executeUpdate();
    }

    public static String getInviterUUID(String code) throws SQLException {
        String sql = "SELECT inviter_uuid FROM invite_code WHERE code = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, code);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            return rs.getString("inviter_uuid");
        }
        return null;
    }

    public static boolean isUsed(String code) throws SQLException {
        String sql = "SELECT is_used FROM invite_code WHERE code = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, code);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            return rs.getBoolean("is_used");
        }
        return true;
    }

    public static void useBy(String code, String invitee) throws SQLException {
        String sql = "UPDATE invite_code SET invitee = ?,is_used = ?,use_time = ? WHERE code = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, invitee);
        prep.setInt(2, 1);
        prep.setString(3, code);
        prep.setLong(4, System.currentTimeMillis());
        prep.executeUpdate();
    }

    public static Set<InviteCode> selectByInviter(String inviterUUID) throws SQLException {
        String sql = "SELECT * FROM invite_code WHERE inviter_uuid = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, inviterUUID);
        ResultSet rs = prep.executeQuery();
        Set<InviteCode> inviteCodes = new HashSet<>();
        while (rs.next()) {
            InviteCode inviteCode = new InviteCode();
            inviteCode.code = rs.getString("code");
            inviteCode.inviter = rs.getString("inviter_uuid");
            inviteCode.createTime = rs.getLong("create_time");
            inviteCode.isUsed = rs.getBoolean("is_used");
            inviteCode.usedTime = rs.getLong("use_time");
            inviteCode.invitee = rs.getString("invitee");
            inviteCodes.add(inviteCode);
        }
        return inviteCodes;
    }
}
