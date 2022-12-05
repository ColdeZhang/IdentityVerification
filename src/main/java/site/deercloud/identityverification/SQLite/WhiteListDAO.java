package site.deercloud.identityverification.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class WhiteListDAO {
    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS white_list (\n"
                + "	uuid text PRIMARY KEY,\n"       // UUID
                + "	is_genuine integer NOT NULL,\n" // 是否为正版玩家
                + "	id_hash integer NOT NULL,\n"       // 身份证号哈希值
                + "	create_time integer NOT NULL\n" // 数据创建时间
                + ");";
        Statement stat;
        stat = SqlManager.session.createStatement();
        stat.executeUpdate(sql);
    }

    public static void insert(String uuid, boolean isGenuine, Integer idHash) throws SQLException {
        String sql = "INSERT INTO white_list(uuid,is_genuine,id_hash,create_time) VALUES(?,?,?,?)";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, uuid);
        prep.setBoolean(2, isGenuine);
        prep.setInt(3, idHash);
        prep.setLong(4, System.currentTimeMillis());
        prep.executeUpdate();
    }

    public static boolean isUuidInWhiteList(String uuid) throws SQLException {
        String sql = "SELECT * FROM white_list WHERE uuid = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, uuid);
        return prep.executeQuery().next();
    }

    public static boolean isIdInWhiteList(Integer idHash) throws SQLException {
        String sql = "SELECT * FROM white_list WHERE id_hash = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setInt(1, idHash);
        return prep.executeQuery().next();
    }

    public static String selectByID(Integer idHash) throws SQLException {
        String sql = "SELECT * FROM white_list WHERE id_hash = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setInt(1, idHash);
        return prep.executeQuery().getString("uuid");
    }
}
