package site.deercloud.identityverification.SQLite;

import site.deercloud.identityverification.HttpServer.model.InviteCode;
import site.deercloud.identityverification.Utils.MyLogger;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class InviteCodeDAO {
    public static void createTable() throws SQLException {
        // 邀请码
        // 所有者
        // 创建时间
        // 是否被使用
        String sql = """
                CREATE TABLE IF NOT EXISTS invite_code (
                	code text PRIMARY KEY,
                	inviter_uuid text NOT NULL,
                	create_time integer NOT NULL,
                	is_used integer NOT NULL
                );""";
        Statement stat;
        stat = SqlManager.session.createStatement();
        stat.executeUpdate(sql);
    }

    public static void insert(InviteCode code) throws SQLException {
        String sql = "INSERT INTO invite_code(code,inviter_uuid,create_time,is_used) VALUES(?,?,?,?)";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, code.code);
        prep.setString(2, code.inviter);
        prep.setLong(3, System.currentTimeMillis());
        prep.setBoolean(4, code.isUsed);
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

    public static boolean getUsed(String code) throws SQLException {
        String sql = "SELECT is_used FROM invite_code WHERE code = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, code);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            return rs.getBoolean("is_used");
        }
        return true;
    }

    public static void setUsed(String code) throws SQLException {
        String sql = "UPDATE invite_code SET is_used = ? WHERE code = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setBoolean(1, true);
        prep.setString(2, code);
        prep.executeUpdate();
    }

    public static  Set<InviteCode> selectByInviter(String inviterUUID) throws SQLException {
        String sql = "SELECT * FROM invite_code WHERE inviter_uuid = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, inviterUUID);
        ResultSet rs = prep.executeQuery();
        Set<InviteCode> codes = new HashSet<>();
        while (rs.next()) {
            InviteCode code = new InviteCode();
            code.code = rs.getString("code");
            code.inviter = rs.getString("inviter_uuid");
            code.createTime = rs.getLong("create_time");
            code.isUsed = rs.getBoolean("is_used");
            codes.add(code);
        }
        return codes;
    }

}
