package site.deercloud.identityverification.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class WhiteListDAO {
    public static void createTable(Connection con) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS white_list (\n"
                + "	uuid text PRIMARY KEY,\n"       // UUID
                + "	is_genuine integer NOT NULL,\n" // 是否为正版玩家
                + "	id_hash integer NOT NULL,\n"       // 身份证号哈希值
                + "	create_time integer NOT NULL\n" // 数据创建时间
                + ");";
        Statement stat;
        stat = con.createStatement();
        stat.executeUpdate(sql);
    }

    public static void insert(Connection con, String uuid, boolean isGenuine, Integer idHash) throws SQLException {
        String sql = "INSERT INTO white_list(uuid,is_genuine,id_hash,create_time) VALUES(?,?,?,?)";
        PreparedStatement prep = con.prepareStatement(sql);
        prep.setString(1, uuid);
        prep.setBoolean(2, isGenuine);
        prep.setInt(3, idHash);
        prep.setLong(4, System.currentTimeMillis());
        prep.executeUpdate();
    }

    public static boolean isUuidInWhiteList(Connection con, String uuid) throws SQLException {
        String sql = "SELECT * FROM white_list WHERE uuid = ?";
        PreparedStatement prep = con.prepareStatement(sql);
        prep.setString(1, uuid);
        return prep.executeQuery().next();
    }

    public static boolean isIdInWhiteList(Connection con, Integer idHash) throws SQLException {
        String sql = "SELECT * FROM white_list WHERE id_hash = ?";
        PreparedStatement prep = con.prepareStatement(sql);
        prep.setInt(1, idHash);
        return prep.executeQuery().next();
    }
}
