package site.deercloud.identityverification.SQLite;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.UnsignedUUID;

import java.sql.*;

public class SqlManager {

    public SqlManager(){
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.setSharedCache(true);
            config.enableRecursiveTriggers(true);
            config.setBusyTimeout(10000);
            SQLiteDataSource ds = new SQLiteDataSource(config);
            String url = System.getProperty("user.dir"); // 获取工作目录
            ds.setUrl("jdbc:sqlite:"+url+"/plugins/IdentityVerification/"+"IV-Database.db");
            session = ds.getConnection();
            createTables();

            User admin = UserDAO.selectByRole(3);
            if (admin == null) {
                // 生成控制台用户
                User consoleUser = new User();
                consoleUser.email = "admin";
                consoleUser.uuid = UnsignedUUID.GenerateUUID();
                consoleUser.password = "123456";
                consoleUser.role = 3;
                UserDAO.insert(consoleUser);
                MyLogger.info("控制台用户已生成，默认用户名：admin，密码：123456（控制台用户仅可登录网页后台，不可加入游戏。）");
                MyLogger.warn("请尽快在控制台使用 /iv admin <用户名> <密码> 修改用户名密码");
            }
        } catch (SQLException e) {
            MyLogger.debug(e);
        }
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
            MyLogger.debug(e);
        }
    }

    public static void updateAdmin(CommandSender sender, String Username, String Password) {
        try {
            if (sender instanceof Player && !((Player)sender).isOp()) {
                MyLogger.error(sender, "你没有权限执行此操作");
                return;
            }
            User admin = UserDAO.selectByRole(3);
            if (admin == null) {
                MyLogger.error("控制台用户不存在，请重启插件以生成。");
                return;
            }
            admin.email = Username;
            admin.password = Password;
            UserDAO.update(admin);
            MyLogger.info(sender, "控制台用户信息已更新");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void noUse() {

    }


}
