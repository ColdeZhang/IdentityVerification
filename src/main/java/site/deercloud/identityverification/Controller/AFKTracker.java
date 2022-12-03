package site.deercloud.identityverification.Controller;

import site.deercloud.identityverification.HttpServer.model.GameSession;
import site.deercloud.identityverification.IdentityVerification;

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
            afkThresholdMs = 1000L * 10L;
        }
        return afkThresholdMs;
    }

    public void hasIgnorePermission(String playerUUID) {
        storeLastMovement(playerUUID, IGNORES_AFK);
    }

    private void storeLastMovement(String playerUUID, long time) {
        GameSessionCache.getCacheSession(playerUUID)
                .ifPresent(gameSession -> gameSession.setLastMovement(time));
    }

    private long getLastMovement(String playerUUID, long time) {
        return getLastMovement(playerUUID)
                .orElse(time);
    }

    private Optional<Long> getLastMovement(String playerUUID) {
        return GameSessionCache.getCacheSession(playerUUID)
                .map(GameSession::getLastMovement);
    }

    public void usedAfkCommand(String playerUUID, long time) {
        long lastMoved = getLastMovement(playerUUID, time);
        if (lastMoved == IGNORES_AFK) {
            return;
        }
        usedAFKCommand.add(playerUUID);
        storeLastMovement(playerUUID, time - getAfkThreshold());
    }

    public long performedAction(String playerUUID, long time) {
        long lastMoved = getLastMovement(playerUUID, time);
        // Ignore afk permission
        if (lastMoved == IGNORES_AFK) {
            return 0L;
        }
        storeLastMovement(playerUUID, time);

        try {
            if (time - lastMoved < getAfkThreshold()) {
                // Threshold not crossed, no action required.
                return 0L;
            }

            long removeAfkCommandEffect = usedAFKCommand.contains(playerUUID) ? getAfkThreshold() : 0;
            long timeAFK = time - lastMoved - removeAfkCommandEffect;

            GameSessionCache.getCacheSession(playerUUID)
                    .ifPresent(gameSession -> gameSession.addAfkTime(timeAFK));
            return timeAFK;
        } finally {
            usedAFKCommand.remove(playerUUID);
        }
    }

    public long loggedOut(String uuid, long time) {
        long timeAFK = performedAction(uuid, time);
        usedAFKCommand.remove(uuid);
        return timeAFK;
    }

    public boolean isAfk(String playerUUID) {
        long time = System.currentTimeMillis();

        Optional<Long> lastMoved = getLastMovement(playerUUID);
        if (lastMoved.isEmpty() || lastMoved.get() == IGNORES_AFK) {
            return false;
        }
        return time - lastMoved.get() > getAfkThreshold();
    }
}
