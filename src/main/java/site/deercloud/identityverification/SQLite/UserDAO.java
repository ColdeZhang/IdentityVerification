package site.deercloud.identityverification.SQLite;

import site.deercloud.identityverification.HttpServer.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {
    public static void createTable(Connection con) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS user (\n"
                + "	uuid text PRIMARY KEY,\n"           // UUID
                + "	email text NOT NULL,\n"             // 邮箱
                + "	password text NOT NULL,\n"          // 密码
                + "	create_time integer NOT NULL,\n"    // 数据创建时间
                + " update_time integer NOT NULL\n"     // 数据更新时间
                + ");";
        Statement stat;
        stat = con.createStatement();
        stat.executeUpdate(sql);
    }

    public static void insert(Connection con, User user) throws SQLException {
        String sql = "INSERT INTO user(uuid, email, password, create_time, update_time) VALUES(?,?,?,?,?)";
        PreparedStatement prep = con.prepareStatement(sql);
        prep.setString(1, user.uuid);
        prep.setString(2, user.email);
        prep.setString(3, user.password);
        prep.setLong(4, System.currentTimeMillis());
        prep.setLong(5, System.currentTimeMillis());
        prep.executeUpdate();
    }

    public static void update(Connection con, User user) throws SQLException {
        String sql = "UPDATE user SET email = ?, password = ?, update_time = ? WHERE uuid = ?";
        PreparedStatement prep = con.prepareStatement(sql);
        prep.setString(1, user.email);
        prep.setString(2, user.password);
        prep.setLong(3, System.currentTimeMillis());
        prep.setString(4, user.uuid);
        prep.executeUpdate();
    }

    public static void delete(Connection con, String uuid) throws SQLException {
        String sql = "DELETE FROM user WHERE uuid = ?";
        PreparedStatement prep = con.prepareStatement(sql);
        prep.setString(1, uuid);
        prep.executeUpdate();
    }

    public static User selectByUuid(Connection con, String uuid) throws SQLException {
        String sql = "SELECT * FROM user WHERE uuid = ?";
        PreparedStatement prep = con.prepareStatement(sql);
        prep.setString(1, uuid);
        User user = new User();
        user.uuid = uuid;
        user.email = prep.executeQuery().getString("email");
        user.password = prep.executeQuery().getString("password");
        user.createTime = prep.executeQuery().getLong("create_time");
        user.updateTime = prep.executeQuery().getLong("update_time");
        return user;
    }

    public static User selectByEmail(Connection con, String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";
        PreparedStatement prep = con.prepareStatement(sql);
        prep.setString(1, email);
        if (prep.executeQuery().next()) {
            User user = new User();
            user.uuid = prep.executeQuery().getString("uuid");
            user.email = email;
            user.password = prep.executeQuery().getString("password");
            user.createTime = prep.executeQuery().getLong("create_time");
            user.updateTime = prep.executeQuery().getLong("update_time");
            return user;
        } else {
            return null;
        }
    }

    public static Boolean checkPassword(Connection con, String email, String password) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";
        PreparedStatement prep = con.prepareStatement(sql);
        if (prep.executeQuery().next()) {
            return prep.executeQuery().getString("password").equals(password);
        } else {
            return false;
        }
    }

    public static Boolean isEmailExist(Connection con, String email) throws SQLException {
        String sql = "SELECT count(*) FROM user WHERE email = ?";
        PreparedStatement prep = con.prepareStatement(sql);
        prep.setString(1, email);
        return prep.executeQuery().getInt(1) > 0;
    }

}
