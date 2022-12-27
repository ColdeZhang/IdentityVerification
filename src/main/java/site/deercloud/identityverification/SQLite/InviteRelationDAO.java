package site.deercloud.identityverification.SQLite;

import site.deercloud.identityverification.HttpServer.model.InviteRelation;

import java.sql.*;

public class InviteRelationDAO {
    public static void createTable() throws SQLException {
        // 邀请码
        // 被邀请人UUID
        // 邀请人UUID
        // 邀请时间
        String sql = """
                CREATE TABLE IF NOT EXISTS invite_relation (
                	code text PRIMARY KEY,
                	invitee_uuid text NOT NULL,
                	inviter_uuid text NOT NULL,
                	invite_time integer NOT NULL
                );""";
        Statement stat;
        stat = SqlManager.session.createStatement();
        stat.executeUpdate(sql);
    }

    public static void insert(String code, String inviteeUUID, String inviterUUID) throws SQLException {
        String sql = "INSERT INTO invite_relation(code,invitee_uuid,inviter_uuid,invite_time) VALUES(?,?,?,?)";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, code);
        prep.setString(2, inviteeUUID);
        prep.setString(3, inviterUUID);
        prep.setLong(4, System.currentTimeMillis());
        prep.executeUpdate();
    }

    public static String selectInviterByInvitee(String inviteeUUID) throws SQLException {
        String sql = "SELECT inviter_uuid FROM invite_relation WHERE invitee_uuid = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, inviteeUUID);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            return rs.getString("inviter_uuid");
        }
        return null;
    }

    public static InviteRelation selectByCode(String code) throws SQLException {
        String sql = "SELECT * FROM invite_relation WHERE code = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, code);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            InviteRelation relation = new InviteRelation();
            relation.code = rs.getString("code");
            relation.invitee = rs.getString("invitee_uuid");
            relation.inviter = rs.getString("inviter_uuid");
            relation.createTime = rs.getLong("invite_time");
            return relation;
        }
        return null;
    }

}

