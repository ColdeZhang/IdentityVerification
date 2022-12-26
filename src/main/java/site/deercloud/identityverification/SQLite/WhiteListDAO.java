package site.deercloud.identityverification.SQLite;

import javax.xml.transform.Result;
import java.sql.*;

public class WhiteListDAO {
    public static void createTable() throws SQLException {
        // UUID
        // 身份证号哈希值
        // 数据创建时间
        String sql = """
                CREATE TABLE IF NOT EXISTS white_list (
                	uuid text PRIMARY KEY,
                	id_hash integer NOT NULL,
                	create_time integer NOT NULL
                );""";
        Statement stat;
        stat = SqlManager.session.createStatement();
        stat.executeUpdate(sql);
    }

    public static void insert(String uuid, Integer idHash) throws SQLException {
        String sql = "INSERT INTO white_list(uuid,id_hash,create_time) VALUES(?,?,?)";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, uuid);
        prep.setInt(2, idHash);
        prep.setLong(3, System.currentTimeMillis());
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
        ResultSet result = prep.executeQuery();
        if (result.next()) {
            return result.getString("uuid");
        } else {
            return null;
        }
    }
}
