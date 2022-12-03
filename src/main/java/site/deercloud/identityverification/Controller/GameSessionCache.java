package site.deercloud.identityverification.Controller;


import site.deercloud.identityverification.HttpServer.model.GameSession;
import site.deercloud.identityverification.IdentityVerification;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameSessionCache {
    private static final Map<String, GameSession> ACTIVE_SESSIONS = new ConcurrentHashMap<>();

    public static Optional<GameSession> getCacheSession(String playerUUID) {
        return Optional.ofNullable(ACTIVE_SESSIONS.get(playerUUID));
    }

    public static void addSession(String playerUUID) {
        ACTIVE_SESSIONS.put(playerUUID, new GameSession());
    }

    public static void removeSession(String  playerUUID) {
        // TODO: save to database

        ACTIVE_SESSIONS.remove(playerUUID);
    }

    public GameSessionCache(IdentityVerification plugin) {
        this.plugin = plugin;
    }

    private final IdentityVerification plugin;
}
