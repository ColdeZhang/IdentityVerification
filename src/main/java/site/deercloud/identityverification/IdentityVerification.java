package site.deercloud.identityverification;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import site.deercloud.identityverification.HttpServer.HttpServerManager;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Controller.AFKTracker;
import site.deercloud.identityverification.Controller.ConfigManager;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Controller.GameSessionCache;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public final class IdentityVerification extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        configManager = new ConfigManager(this);
        httpServerManager = new HttpServerManager(this);
        gameSessionCache = new GameSessionCache(this);
        afkTracker = new AFKTracker(this);

        // 初始化数据表
        try {
            Connection connection = SqlManager.getConnection();
            SqlManager.createTables(connection);
            if (!UserDAO.isEmailExist(connection, "console@mc.com")) {
                // 生成控制台用户
                User consoleUser = new User();
                consoleUser.email = "console@mc.com";
                consoleUser.uuid = UUID.randomUUID().toString();
                consoleUser.password = String.valueOf("123456".hashCode());
                UserDAO.insert(connection, consoleUser);
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // 初始化RSA
        String publicKey = new File(this.getDataFolder(), configManager.getPublicKeyFileName()).toString();
        String privateKey = new File(this.getDataFolder(), configManager.getPrivateKeyFileName()).toString();
        if (publicKey == null || privateKey == null) {
            MyLogger.error("RSA文件不存在, 插件退出。");
            this.getServer().getPluginManager().disablePlugin(this);
        }else {
            privateKey = privateKey.replace("-----BEGIN OPENSSH PRIVATE KEY-----", "");
            privateKey = privateKey.replace("-----END OPENSSH PRIVATE KEY-----", "");
            configManager.setSignaturePublicKey(publicKey);
            configManager.setSignaturePrivateKey(privateKey);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (httpServerManager != null) {
            httpServerManager.stopHttpServer();
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
    public static IdentityVerification getInstance() {
        return instance;
    }
    public static GameSessionCache getSessionCache() {
        return gameSessionCache;
    }
    public static AFKTracker getAfkTracker() {
        return afkTracker;
    }

    private HttpServerManager httpServerManager;
    private ConfigManager configManager;
    private static GameSessionCache gameSessionCache;
    private static AFKTracker afkTracker;
    private static IdentityVerification instance;
}
