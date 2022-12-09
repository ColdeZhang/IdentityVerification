package site.deercloud.identityverification.Controller;

import org.bukkit.entity.Player;
import site.deercloud.identityverification.HttpServer.model.GameSession;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.UnsignedUUID;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AFKTracker {


    public static final long IGNORES_AFK = -1L;

    private final Set<String> usedAFKCommand;

    private Long afkThresholdMs;
    private final IdentityVerification plugin;

    public AFKTracker(IdentityVerification plugin) {
       this.plugin = plugin;
       usedAFKCommand = new HashSet<>();
    }

    public long getAfkThreshold() {
        if (afkThresholdMs == null) {
            afkThresholdMs = 1000L * 30L;
        }
        return afkThresholdMs;
    }

    public void hasIgnorePermission(Player player) {
        storeLastMovement(player, IGNORES_AFK);
    }

    private void storeLastMovement(Player player, long time) {
        String playerUUID = UnsignedUUID.UnUUIDof(player);
        GameSessionCache.getCacheSession(playerUUID).ifPresent(gameSession -> gameSession.setLastMovement(time));
    }

    private long getLastMovement(Player player, long time) {
        return getLastMovement(player).orElse(time);
    }

    private Optional<Long> getLastMovement(Player player) {
        String playerUUID = UnsignedUUID.UnUUIDof(player);
        return GameSessionCache.getCacheSession(playerUUID).map(GameSession::getLastMovement);
    }

    public void usedAfkCommand(Player player, long time) {
        String playerUUID = UnsignedUUID.UnUUIDof(player);
        long lastMoved = getLastMovement(player, time);
        if (lastMoved == IGNORES_AFK) {
            return;
        }
        usedAFKCommand.add(playerUUID);
        storeLastMovement(player, time - getAfkThreshold());
    }

    public long performedAction(Player player, long time) {
        String playerUUID = UnsignedUUID.UnUUIDof(player);
        long lastMoved = getLastMovement(player, time);
        // Ignore afk permission
        if (lastMoved == IGNORES_AFK) {
            return 0L;
        }
        storeLastMovement(player, time);

        try {
            if (time - lastMoved < getAfkThreshold()) {
                return 0L;
            }

            long removeAfkCommandEffect = usedAFKCommand.contains(playerUUID) ? getAfkThreshold() : 0;
            long timeAFK = time - lastMoved - removeAfkCommandEffect;

            MyLogger.debug(player.getName() + " 本次挂机时间：" + timeAFK / 1000 / 60 + "分" + time / 1000 % 60 + "秒");
            GameSessionCache.getCacheSession(playerUUID).ifPresent(gameSession -> gameSession.addAfkTime(timeAFK));
            return timeAFK;
        } finally {
            usedAFKCommand.remove(playerUUID);
        }
    }

    public long loggedOut(Player player, long time) {
        long timeAFK = performedAction(player, time);
        String playerUUID = UnsignedUUID.UnUUIDof(player);
        usedAFKCommand.remove(playerUUID);
        return timeAFK;
    }

    public boolean isAfk(Player player) {
        long time = System.currentTimeMillis();

        Optional<Long> lastMoved = getLastMovement(player);
        if (lastMoved.isEmpty() || lastMoved.get() == IGNORES_AFK) {
            return false;
        }
        return time - lastMoved.get() > getAfkThreshold();
    }
}
