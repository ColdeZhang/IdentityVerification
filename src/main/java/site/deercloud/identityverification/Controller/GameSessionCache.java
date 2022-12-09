package site.deercloud.identityverification.Controller;


import site.deercloud.identityverification.HttpServer.model.GameSession;
import site.deercloud.identityverification.HttpServer.model.PlayTimeStatics;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.SQLite.PlayTimeDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameSessionCache {
    private static final Map<String, GameSession> ACTIVE_SESSIONS = new ConcurrentHashMap<>();

    public static Optional<GameSession> getCacheSession(String playerUUID) {
        return Optional.ofNullable(ACTIVE_SESSIONS.get(playerUUID));
    }

    public static void addSession(String playerUUID) {
        MyLogger.debug("添加缓存会话,开始记录活动状态:" + playerUUID);
        ACTIVE_SESSIONS.put(playerUUID, new GameSession());
    }

    public static void removeSession(String  playerUUID) {
        // 存库 (活跃时间、挂机时间)
        PlayTimeStatics statics = new PlayTimeStatics();
        if (getCacheSession(playerUUID).isEmpty()) {
            return;
        }
        GameSession session = getCacheSession(playerUUID).get();
        statics.joinTime = session.joinTime;
        statics.quitTime = System.currentTimeMillis();
        statics.onlineTime = statics.quitTime - statics.joinTime;
        statics.afkTime = session.afkTime;
        statics.activeTime = statics.onlineTime - statics.afkTime;
        statics.playerUuid = playerUUID;
        MyLogger.debug(playerUUID + "\n\t本次游戏在线时间: " + statics.onlineTime / 1000 / 60 + "min" + statics.onlineTime / 1000 % 60 + "s"
                            + "\n\t本次游戏挂机时间: " + statics.afkTime / 1000 / 60 + "min" + statics.afkTime / 1000 % 60 + "s"
                            + "\n\t本次游戏活跃时间: " + statics.activeTime / 1000 / 60 + "min" + statics.activeTime / 1000 % 60 + "s"
                            + "\n\t本次游戏挂机率: " + (double) Math.round((double) statics.afkTime / statics.onlineTime * 10000) / 100 + "%");
        try {
            PlayTimeDAO.insert(statics);
        } catch (SQLException e) {
            MyLogger.debug(e);
        }
        ACTIVE_SESSIONS.remove(playerUUID);
    }

    public GameSessionCache(IdentityVerification plugin) {
        this.plugin = plugin;
    }

    private final IdentityVerification plugin;
}
