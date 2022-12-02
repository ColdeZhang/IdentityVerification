package site.deercloud.identityverification.SQLite;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class SqlManager {

    public static Connection getConnection(){
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.setSharedCache(true);
            config.enableRecursiveTriggers(true);
            SQLiteDataSource ds = new SQLiteDataSource(config);
            String url = System.getProperty("user.dir"); // 获取工作目录
            ds.setUrl("jdbc:sqlite:"+url+"/plugins/IdentityVerification/"+"IV-Database.db");
            return ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createTables(Connection connection) {
        try {
            BanListDAO.createTable(connection);
            InviteCodeDAO.createTable(connection);
            UserDAO.createTable(connection);
            InviteRelationDAO.createTable(connection);
            ProfileDAO.createTable(connection);
            WhiteListDAO.createTable(connection);
            TokenDAO.createTable(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
