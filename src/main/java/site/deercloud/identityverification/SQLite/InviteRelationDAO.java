package site.deercloud.identityverification.SQLite;

import java.sql.*;

public class InviteRelationDAO {
    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS invite_relation (\n"
                + "	invitee_uuid text PRIMARY KEY,\n" // 被邀请人UUID
                + "	inviter_uuid text NOT NULL,\n" // 邀请人UUID
                + "	invite_time integer NOT NULL\n" // 邀请时间
                + ");";
        Statement stat = null;
        stat = SqlManager.session.createStatement();
        stat.executeUpdate(sql);
    }

    public static void insert(String inviteeUUID, String inviterUUID, long inviteTime) throws SQLException {
        String sql = "INSERT INTO invite_relation(invitee_uuid,inviter_uuid,invite_time) VALUES(?,?,?)";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, inviteeUUID);
        prep.setString(2, inviterUUID);
        prep.setLong(3, inviteTime);
        prep.executeUpdate();
    }

    public static String getInviterUUID(String inviteeUUID) throws SQLException {
        String sql = "SELECT inviter_uuid FROM invite_relation WHERE invitee_uuid = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, inviteeUUID);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            return rs.getString("inviter_uuid");
        }
        return null;
    }

}

