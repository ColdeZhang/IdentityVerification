package site.deercloud.identityverification;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import site.deercloud.identityverification.Controller.ActiveIndexManager;
import site.deercloud.identityverification.HttpServer.HttpServerManager;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.Controller.AFKTracker;
import site.deercloud.identityverification.Controller.ConfigManager;
import site.deercloud.identityverification.Utils.FileToString;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Controller.GameSessionCache;

import java.util.Objects;

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
        sqlManager = new SqlManager();
        // 初始化活跃度管理器
        activeIndexManager = new ActiveIndexManager();

        // 初始化RSA
        File publicKeyPath = new File(this.getDataFolder(), configManager.getPublicKeyFileName());
        File privateKeyPath = new File(this.getDataFolder(), configManager.getPrivateKeyFileName());
        String pubKeyContent = FileToString.Read(publicKeyPath);
        String priKeyContent = FileToString.Read(privateKeyPath);
        if (pubKeyContent == null || priKeyContent == null) {
            MyLogger.error("RSA文件不存在, 插件退出。");
            this.getServer().getPluginManager().disablePlugin(this);
        } else {
            priKeyContent = priKeyContent.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("\n", "");
            MyLogger.debug("publicKey: " + pubKeyContent);
            MyLogger.debug("privateKey: " + priKeyContent);
            configManager.setSignaturePublicKey(pubKeyContent);
            configManager.setSignaturePrivateKey(priKeyContent);
        }

        // 注册事件
        getServer().getPluginManager().registerEvents(new Events(), this);
        // 注册命令
        Objects.requireNonNull(getCommand("identityverification")).setExecutor(new Commands());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (httpServerManager != null) {
            httpServerManager.stopHttpServer();
        }
    }

    public static HttpServerManager httpServerManager;
    public static ConfigManager configManager;
    public static SqlManager sqlManager;
    public static GameSessionCache gameSessionCache;
    public static AFKTracker afkTracker;
    public static IdentityVerification instance;
    public static ActiveIndexManager activeIndexManager;
}
