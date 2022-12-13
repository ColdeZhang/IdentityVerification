package site.deercloud.identityverification.SQLite;

import site.deercloud.identityverification.HttpServer.model.User;

import java.sql.*;

public class UserDAO {
    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS user (\n"
                + "	uuid text PRIMARY KEY,\n"           // UUID
                + "	email text NOT NULL,\n"             // 邮箱
                + "	password integer NOT NULL,\n"       // 密码
                + " role integer NOT NULL,\n"           // 用户类型 0 普通用户 1 管理员 2 控制台用户
                + "	create_time integer NOT NULL,\n"    // 数据创建时间
                + " update_time integer NOT NULL\n"     // 数据更新时间
                + ");";
        Statement stat;
        stat = SqlManager.session.createStatement();
        stat.executeUpdate(sql);
    }

    public static void insert(User user) throws SQLException {
        String sql = "INSERT INTO user(uuid,email,password,role,create_time,update_time) VALUES(?,?,?,?,?,?)";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, user.uuid);
        prep.setString(2, user.email);
        prep.setLong(3, user.password.hashCode());
        prep.setInt(4, user.role.ordinal());
        prep.setLong(5, System.currentTimeMillis());
        prep.setLong(6, System.currentTimeMillis());
        prep.executeUpdate();
    }

    public static void update(User user) throws SQLException {
        String sql = "UPDATE user SET email = ?, password = ?, role = ? WHERE uuid = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, user.email);
        prep.setLong(2, user.password.hashCode());
        prep.setInt(3, user.role.ordinal());
        prep.setString(4, user.uuid);
        prep.executeUpdate();
    }

    public static void delete(String uuid) throws SQLException {
        String sql = "DELETE FROM user WHERE uuid = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, uuid);
        prep.executeUpdate();
    }

    public static User selectByUuid(String uuid) throws SQLException {
        String sql = "SELECT * FROM user WHERE uuid = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, uuid);
        User user = new User();
        user.uuid = uuid;
        user.email = prep.executeQuery().getString("email");
        user.role = User.ROLE.values()[prep.executeQuery().getInt("role")];
        user.createTime = prep.executeQuery().getLong("create_time");
        user.updateTime = prep.executeQuery().getLong("update_time");
        return user;
    }

    public static User selectByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, email);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            User user = new User();
            user.uuid = rs.getString("uuid");
            user.email = email;
            user.role = User.ROLE.values()[rs.getInt("role")];
            user.createTime = rs.getLong("create_time");
            user.updateTime = rs.getLong("update_time");
            return user;
        } else {
            return null;
        }
    }

    public static User selectByRole(User.ROLE role) throws SQLException {
        String sql = "SELECT * FROM user WHERE role = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setInt(1, role.ordinal());
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            User user = new User();
            user.uuid = rs.getString("uuid");
            user.email = rs.getString("email");
            user.role = role;
            user.createTime = rs.getLong("create_time");
            user.updateTime = rs.getLong("update_time");
            return user;
        } else {
            return null;
        }
    }

    public static Boolean checkPassword(String email, String password) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, email);
        if (prep.executeQuery().next()) {
            return prep.executeQuery().getLong("password") == password.hashCode();
        } else {
            return false;
        }
    }

    public static Boolean isEmailExist(String email) throws SQLException {
        String sql = "SELECT count(*) FROM user WHERE email = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, email);
        return prep.executeQuery().getInt(1) > 0;
    }

}
