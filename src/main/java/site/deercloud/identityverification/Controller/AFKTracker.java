package site.deercloud.identityverification.Controller;

import site.deercloud.identityverification.HttpServer.model.GameSession;
import site.deercloud.identityverification.IdentityVerification;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class AFKTracker {


    public static final long IGNORES_AFK = -1L;

    private final Set<UUID> usedAFKCommand;

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

    public void hasIgnorePermission(UUID playerUUID) {
        storeLastMovement(playerUUID, IGNORES_AFK);
    }

    private void storeLastMovement(UUID playerUUID, long time) {
        GameSessionCache.getCacheSession(playerUUID)
                .ifPresent(gameSession -> gameSession.setLastMovement(time));
    }

    private long getLastMovement(UUID playerUUID, long time) {
        return getLastMovement(playerUUID)
                .orElse(time);
    }

    private Optional<Long> getLastMovement(UUID playerUUID) {
        return GameSessionCache.getCacheSession(playerUUID)
                .map(GameSession::getLastMovement);
    }

    public void usedAfkCommand(UUID playerUUID, long time) {
        long lastMoved = getLastMovement(playerUUID, time);
        if (lastMoved == IGNORES_AFK) {
            return;
        }
        usedAFKCommand.add(playerUUID);
        storeLastMovement(playerUUID, time - getAfkThreshold());
    }

    public long performedAction(UUID playerUUID, long time) {
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

    public long loggedOut(UUID uuid, long time) {
        long timeAFK = performedAction(uuid, time);
        usedAFKCommand.remove(uuid);
        return timeAFK;
    }

    public boolean isAfk(UUID playerUUID) {
        long time = System.currentTimeMillis();

        Optional<Long> lastMoved = getLastMovement(playerUUID);
        if (lastMoved.isEmpty() || lastMoved.get() == IGNORES_AFK) {
            return false;
        }
        return time - lastMoved.get() > getAfkThreshold();
    }
}
