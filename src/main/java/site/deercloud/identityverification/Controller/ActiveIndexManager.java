package site.deercloud.identityverification.Controller;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import site.deercloud.identityverification.HttpServer.model.ActiveIndex;
import site.deercloud.identityverification.HttpServer.model.PlayTimeStatics;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.SQLite.ActiveIndexDAO;
import site.deercloud.identityverification.SQLite.PlayTimeDAO;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.UnsignedUUID;

import java.sql.SQLException;
import java.util.ArrayList;

public class ActiveIndexManager {

    public static void updatePlayTimeThreshold() {
        try {
            ArrayList<ActiveIndex> activityRecords = ActiveIndexDAO.selectActivityRecords();
            if (activityRecords.size() == 0) {
                // 没有活跃玩家则默认每周游戏时间2小时
                playtimeMsThreshold = 1000L * 60 * 60 * 2;
            } else {
                // 计算活跃玩家的平均每周游戏时间
                Long sum = 0L;
                for (ActiveIndex record : activityRecords) {
                    sum += record.recentWeek;
                }
                playtimeMsThreshold = sum / activityRecords.size();
            }
        } catch (SQLException e) {
            MyLogger.debug(e);
        }

    }

    private static Long playtimeMsThreshold = 1000L * 60 * 30;

    public static final double VERY_ACTIVE = 3.75;
    public static final double ACTIVE = 3.0;
    public static final double REGULAR = 2.0;
    public static final double IRREGULAR = 1.0;

    public double getActiveIndex(Player player) {
        try {
            String uuid = UnsignedUUID.UnUUIDof(player);
            ActiveIndex activeIndex = ActiveIndexDAO.query(uuid);
            if (activeIndex == null) {
                return 0.0;
            } else {
                return activeIndex.index;
            }
        } catch (SQLException e) {
            MyLogger.debug(e);
        }
        return 0.0;
    }

    public double updateIndex(String uuid) {
        // 更新基数
        updatePlayTimeThreshold();

        long current = System.currentTimeMillis();

        long sevenDays = 1000L * 60 * 60 * 24 * 7;
        long weekAgo = current - sevenDays;
        long twoWeeksAgo = weekAgo - sevenDays;
        long threeWeeksAgo = twoWeeksAgo - sevenDays;

        try {
            if (PlayTimeDAO.countsOfPlayer(uuid, threeWeeksAgo, current) <= 0) {
                return 0.0;
            }

            ArrayList<PlayTimeStatics> weekOne = PlayTimeDAO.selectByTime(uuid, weekAgo, current);
            ArrayList<PlayTimeStatics> weekTwo = PlayTimeDAO.selectByTime(uuid, twoWeeksAgo, weekAgo);
            ArrayList<PlayTimeStatics> weekThree = PlayTimeDAO.selectByTime(uuid, threeWeeksAgo, twoWeeksAgo);

            Long playtime1 = PlayTimeStatics.totalActivityTimeOf(weekOne);
            Long playtime2 = PlayTimeStatics.totalActivityTimeOf(weekTwo);
            Long playtime3 = PlayTimeStatics.totalActivityTimeOf(weekThree);

            double indexW1 = 1.0 / (Math.PI / 2.0 * ((double) playtime1 / playtimeMsThreshold) + 1.0);
            double indexW2 = 1.0 / (Math.PI / 2.0 * ((double) playtime2 / playtimeMsThreshold) + 1.0);
            double indexW3 = 1.0 / (Math.PI / 2.0 * ((double) playtime3 / playtimeMsThreshold) + 1.0);

            double average = (indexW1 + indexW2 + indexW3) / 3.0;

            ActiveIndex activeIndex = new ActiveIndex();
            activeIndex.playerUuid = uuid;
            activeIndex.recentWeek = playtime1;
            activeIndex.oneWeekAgo = playtime2;
            activeIndex.twoWeekAgo = playtime3;
            activeIndex.index = Math.round((5.0 - (5.0 * average)) * 100) / 100.0;

            if (ActiveIndexDAO.query(uuid) == null) {
                ActiveIndexDAO.insert(activeIndex);
            } else {
                ActiveIndexDAO.update(activeIndex);
            }

            return activeIndex.index;
        } catch (SQLException e) {
            MyLogger.debug(e);
        }
        return 0.0;
    }

    public double updateIndex(Player player) {
        // 更新基数
        updatePlayTimeThreshold();

        String uuid = UnsignedUUID.UnUUIDof(player);

        return updateIndex(uuid);
    }

    public void updateAllIndexRecord() {
        if (IdentityVerification.instance.getServer().getOnlinePlayers().size() != 0) {
            return;
        }
        try {
            ArrayList<ActiveIndex> all_records = ActiveIndexDAO.selectActivityRecords();
            for (ActiveIndex record : all_records) {
                updateIndex(record.playerUuid);
            }
        }catch (Exception e) {
            MyLogger.debug(e);
        }
    }

}
