package site.deercloud.identityverification;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import site.deercloud.identityverification.Controller.GameSessionCache;
import site.deercloud.identityverification.HttpServer.model.BanRecord;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.SQLite.BanListDAO;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.SQLite.WhiteListDAO;
import site.deercloud.identityverification.Controller.ConfigManager;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.UnsignedUUID;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class Events implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        String uuid = UnsignedUUID.UnUUIDof(player);
        Profile profile = ProfileDAO.selectByUuid(uuid);
        if (IdentityVerification.configManager.getWhiteList()) {
            if (profile == null) {
                player.kickPlayer("请前往我们的官方网站 " + configManager.getHomePageUrl() + " 注册并绑定账号。");
                return;
            }
            MyLogger.debug(profile.belongTo);
            if (!WhiteListDAO.isUuidInWhiteList(profile.belongTo) && IdentityVerification.configManager.getWhiteList()) {
                player.kickPlayer("你没有完成白名单实名认证，请前往 " + configManager.getHomePageUrl() + " 进行认证。");
                return;
            }
        }
        BanRecord record = BanListDAO.isBanned(uuid);
        if (record != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String ban_until_string = sdf.format(record.ban_time);
            player.kickPlayer("你已被封禁，请联系管理员。原因：[ " + record.ban_reason + " ]" + " 至：" + ban_until_string);
            return;
        }
        GameSessionCache.addSession(UnsignedUUID.UnUUIDof(event.getPlayer()));
        double activeIndex = IdentityVerification.activeIndexManager.updateIndex(player);
        MyLogger.debug("\t" + player.getName() + "当前的活跃度为" + activeIndex + "/5.00");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        IdentityVerification.afkTracker.performedAction(event.getPlayer(), System.currentTimeMillis());
        GameSessionCache.removeSession(UnsignedUUID.UnUUIDof(event.getPlayer()));
        IdentityVerification.activeIndexManager.updateAllIndexRecord();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        IdentityVerification.afkTracker.performedAction(event.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        IdentityVerification.afkTracker.performedAction(event.getPlayer(), System.currentTimeMillis());
    }

    ConfigManager configManager = IdentityVerification.configManager;
}
