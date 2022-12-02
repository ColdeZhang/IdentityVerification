package site.deercloud.identityverification.SQLite;

import java.sql.*;

public class BanListDAO {
    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS ban_list (\n"
                + " id integer PRIMARY KEY AUTOINCREMENT ,\n"   // ID
                + "	uuid text NOT NULL ,\n"                     // UUID
                + "	name text NOT NULL,\n"                      // 玩家名字
                + "	ban_time integer NOT NULL,\n"               // 封禁时间
                + "	ban_reason text NOT NULL,\n"                // 封禁原因
                + "	create_time integer NOT NULL\n"             // 数据创建时间
                + ");";
        Statement stat = null;
        stat = SqlManager.session.createStatement();
        stat.executeUpdate(sql);
    }

    public static void insert(String uuid, String name, long banTime, String banReason, long createTime) throws SQLException {
        String sql = "INSERT INTO ban_list(uuid,name,ban_time,ban_reason,create_time) VALUES(?,?,?,?,?)";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, uuid);
        prep.setString(2, name);
        prep.setLong(3, banTime);
        prep.setString(4, banReason);
        prep.setLong(5, createTime);
        prep.executeUpdate();
    }

    public static Integer isBanned(String uuid) throws SQLException {
        String sql = "SELECT ban_time FROM ban_list WHERE uuid = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, uuid);
        ResultSet rs = prep.executeQuery();
        while (rs.next()) {
            if(rs.getLong("ban_time") > System.currentTimeMillis()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    public static String getBanReasonById(Integer id) throws SQLException {
        String sql = "SELECT ban_reason FROM ban_list WHERE id = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setInt(1, id);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            return rs.getString("ban_reason");
        }
        return null;
    }

    public static long getBanTimeById( Integer id) throws SQLException {
        String sql = "SELECT ban_time FROM ban_list WHERE id = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setInt(1, id);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            return rs.getLong("ban_time");
        }
        return -1;
    }

    public static String getNameById(Integer id) throws SQLException {
        String sql = "SELECT name FROM ban_list WHERE id = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setInt(1, id);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            return rs.getString("name");
        }
        return null;
    }
}
