package site.deercloud.identityverification.SQLite;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class SqlManager {

    public SqlManager() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
        config.setSharedCache(true);
        config.enableRecursiveTriggers(true);
        config.setBusyTimeout(10000);
        SQLiteDataSource ds = new SQLiteDataSource(config);
        String url = System.getProperty("user.dir"); // 获取工作目录
        ds.setUrl("jdbc:sqlite:"+url+"/plugins/IdentityVerification/"+"IV-Database.db");
        session = ds.getConnection();
        createTables();
    }

    public static Connection session;

    public static void createTables() {
        try {
            BanListDAO.createTable();
            InviteCodeDAO.createTable();
            UserDAO.createTable();
            InviteRelationDAO.createTable();
            ProfileDAO.createTable();
            WhiteListDAO.createTable();
            TokenDAO.createTable();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
