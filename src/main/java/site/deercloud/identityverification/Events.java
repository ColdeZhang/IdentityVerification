package site.deercloud.identityverification;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import site.deercloud.identityverification.Controller.AFKTracker;
import site.deercloud.identityverification.Controller.GameSessionCache;
import site.deercloud.identityverification.SQLite.BanListDAO;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.SQLite.WhiteListDAO;
import site.deercloud.identityverification.Controller.ConfigManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class Events implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (!WhiteListDAO.isUuidInWhiteList(uuid)) {
            player.kickPlayer("你没有完成白名单实名认证，请前往 " + configManager.getHomePageUrl() + " 进行认证。");
        }
        Integer ban_record_id = BanListDAO.isBanned(uuid);
        if (ban_record_id > 0) {
            String ban_reason = BanListDAO.getBanReasonById(ban_record_id);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String ban_time = sdf.format(BanListDAO.getBanTimeById(ban_record_id));
            player.kickPlayer("你已被封禁，请联系管理员。原因：[ " + ban_reason + " ]" + " 至：" + ban_time);
        }
        GameSessionCache.addSession(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerJoinEvent event) throws SQLException {
        GameSessionCache.removeSession(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) throws SQLException {
        Player player = event.getPlayer();
        IdentityVerification.getAfkTracker().performedAction(player.getUniqueId(), System.currentTimeMillis());
    }

    ConfigManager configManager = IdentityVerification.getInstance().getConfigManager();
}
