package site.deercloud.identityverification.Controller;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConfigManager {

    public ConfigManager(IdentityVerification plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        m_debug = config.getBoolean("Debug");
        m_ServerName = config.getString("Web.ServerName");
        m_ImplementationName = config.getString("Web.ImplementationName");
        m_ImplementationVersion = config.getString("Web.ImplementationVersion");
        m_HomePageUrl = config.getString("Web.HomePageUrl");
        m_RegisterUrl = m_HomePageUrl + "";
        m_WebHost = config.getString("Web.Host");
        m_WebPort = config.getInt("Web.Port");
        m_YagHost = config.getString("Yggdrasil.Host");
        m_YagPort = config.getInt("Yggdrasil.Port");
        m_SkinDomains = (ArrayList<String>) config.getStringList("Yggdrasil.SkinDomains");
        m_RsaPublicKeyFileName = config.getString("Yggdrasil.RsaPublicKey");
        m_RsaPrivateKeyFileName = config.getString("Yggdrasil.RsaPrivateKey");
        m_EmailHost = config.getString("Email.Host");
        m_EmailPort = config.getString("Email.Port");
        m_EmailUsername = config.getString("Email.Username");
        m_EmailPassword = config.getString("Email.Password");
        m_EmailFrom = config.getString("Email.From");
        m_Description = config.getString("Web.Description");
        m_YagdrasilUrl = config.getString("Web.YagdrasilUrl");
        m_ServerUrl = config.getString("Web.ServerUrl");
        m_WhiteList = config.getBoolean("WhiteList");
        MyLogger.info("配置文件加载完成。");
    }

    public void reload(CommandSender sender) {
        if (sender instanceof Player) {
            if (sender.isOp()) {
                reload();
                MyLogger.info(sender,"配置文件已重新加载。");
            } else {
                MyLogger.warn(sender,"尝试执行未授权的命令。");
            }
        } else {
            reload();
            MyLogger.info("配置文件已重新加载。");
        }
    }

    public String getServerName() {
        return m_ServerName;
    }
    public void setServerName(String serverName) {
        m_ServerName = serverName;
        config.set("Web.ServerName", serverName);
        plugin.saveConfig();
    }

    public String getImplementationName() {
        return m_ImplementationName;
    }
    public void setImplementationName(String implementationName) {
        m_ImplementationName = implementationName;
        config.set("Web.ImplementationName", implementationName);
        plugin.saveConfig();
    }

    public String getImplementationVersion() {
        return m_ImplementationVersion;
    }
    public void setImplementationVersion(String implementationVersion) {
        m_ImplementationVersion = implementationVersion;
        config.set("Web.ImplementationVersion", implementationVersion);
        plugin.saveConfig();
    }

    public String getHomePageUrl() {
        return m_HomePageUrl;
    }
    public void setHomePageUrl(String homePageUrl) {
        m_HomePageUrl = homePageUrl;
        config.set("Web.HomePageUrl", homePageUrl);
        plugin.saveConfig();
    }

    public String getRegisterUrl() {
        return m_RegisterUrl;
    }

    public String getWebHost() {
        return m_WebHost;
    }
    public void setWebHost(String host) {
        m_WebHost = host;
        config.set("Web.Host", host);
        plugin.saveConfig();
    }

    public int getWebPort() {
        return m_WebPort;
    }
    public void setWebPort(int port) {
        m_WebPort = port;
        config.set("Web.Port", port);
        plugin.saveConfig();
    }

    public String getYagHost() {
        return m_YagHost;
    }
    public void setYagHost(String host) {
        m_YagHost = host;
        config.set("Yggdrasil.Host", host);
        plugin.saveConfig();
    }

    public int getYagPort() {
        return m_YagPort;
    }
    public void setYagPort(int port) {
        m_YagPort = port;
        config.set("Yggdrasil.Port", port);
        plugin.saveConfig();
    }

    public ArrayList<String> getSkinDomains() {
        return m_SkinDomains;
    }
    public void setSkinDomains(ArrayList<String> skinDomains) {
        m_SkinDomains = skinDomains;
        config.set("Yggdrasil.SkinDomains", skinDomains);
        plugin.saveConfig();
    }

    public String getSignaturePublicKey() {
        return m_SignaturePublicKey;
    }
    public void setSignaturePublicKey(String signaturePublicKey) {
        m_SignaturePublicKey = signaturePublicKey;
    }
    public void setPublicKeyFileName(String signaturePublicKey) {
        m_RsaPublicKeyFileName = signaturePublicKey;
        config.set("Yggdrasil.RsaPublicKey", signaturePublicKey);
        plugin.saveConfig();
    }
    public String getPublicKeyFileName() {
        return m_RsaPublicKeyFileName;
    }

    public String getSignaturePrivateKey() {
        return m_SignaturePrivateKey;
    }
    public void setSignaturePrivateKey(String signaturePrivateKey) {
        m_SignaturePrivateKey = signaturePrivateKey;
    }
    public void setPrivateKeyFileName(String signaturePrivateKey) {
        m_RsaPrivateKeyFileName = signaturePrivateKey;
        config.set("Yggdrasil.RsaPrivateKey", signaturePrivateKey);
        plugin.saveConfig();
    }
    public String getPrivateKeyFileName() {
        return m_RsaPrivateKeyFileName;
    }

    public Boolean getDebug() {
        return m_debug;
    }
    public void setDebug(Boolean debug) {
        m_debug = debug;
        config.set("Debug", debug);
        plugin.saveConfig();
    }

    public String getEmailHost() {
        return m_EmailHost;
    }
    public void setEmailHost(String emailHost) {
        m_EmailHost = emailHost;
        config.set("Email.Host", emailHost);
        plugin.saveConfig();
    }

    public String getEmailPort() {
        return m_EmailPort;
    }
    public void setEmailPort(String emailPort) {
        m_EmailPort = emailPort;
        config.set("Email.Port", emailPort);
        plugin.saveConfig();
    }

    public String getEmailUsername() {
        return m_EmailUsername;
    }
    public void setEmailUsername(String emailUsername) {
        m_EmailUsername = emailUsername;
        config.set("Email.Username", emailUsername);
        plugin.saveConfig();
    }

    public String getEmailPassword() {
        return m_EmailPassword;
    }
    public void setEmailPassword(String emailPassword) {
        m_EmailPassword = emailPassword;
        config.set("Email.Password", emailPassword);
        plugin.saveConfig();
    }

    public String getEmailFrom() {
        return m_EmailFrom;
    }
    public void setEmailFrom(String emailFrom) {
        m_EmailFrom = emailFrom;
        config.set("Email.From", emailFrom);
        plugin.saveConfig();
    }

    public String getDescription() {
        return m_Description;
    }
    public void setDescription(String description) {
        m_Description = description;
        config.set("Web.Description", description);
        plugin.saveConfig();
    }

    public String getYagdrasilUrl() {
        return m_YagdrasilUrl;
    }
    public void setYagdrasilUrl(String yagdrasilUrl) {
        m_YagdrasilUrl = yagdrasilUrl;
        config.set("Yggdrasil.Url", yagdrasilUrl);
        plugin.saveConfig();
    }

    public String getServerUrl() {
        return m_ServerUrl;
    }
    public void setServerUrl(String serverUrl) {
        m_ServerUrl = serverUrl;
        config.set("Web.ServerUrl", serverUrl);
        plugin.saveConfig();
    }

    public Boolean getWhiteList() {
        return m_WhiteList;
    }
    public void setWhiteList(Boolean whiteList) {
        m_WhiteList = whiteList;
        config.set("WhiteList", whiteList);
        plugin.saveConfig();
    }

    private String m_ServerName;
    private String m_ImplementationName;
    private String m_ImplementationVersion;
    private String m_HomePageUrl;
    private String m_RegisterUrl;
    private String m_WebHost;
    private Integer m_WebPort;
    private String m_YagHost;
    private Integer m_YagPort;

    private ArrayList<String> m_SkinDomains;
    private String m_RsaPublicKeyFileName;
    private String m_RsaPrivateKeyFileName;
    private String m_SignaturePublicKey;
    private String m_SignaturePrivateKey;
    private Boolean m_debug;

    private String m_EmailHost;
    private String m_EmailPort;
    private String m_EmailUsername;
    private String m_EmailPassword;
    private String m_EmailFrom;
    private String m_Description;
    private String m_YagdrasilUrl;
    private String m_ServerUrl;
    private Boolean m_WhiteList;

    IdentityVerification plugin;
    FileConfiguration config;
}
