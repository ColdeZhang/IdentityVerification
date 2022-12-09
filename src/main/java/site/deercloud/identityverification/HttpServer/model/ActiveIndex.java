package site.deercloud.identityverification.HttpServer.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ActiveIndex {
    public String playerUuid;

    public Long recentWeek = 0L;
    public Long oneWeekAgo = 0L;
    public Long twoWeekAgo = 0L;

    public Double index = 0.0;

    public Long update_time;

    public static ArrayList<ActiveIndex> fillListWithSqlRes(ResultSet res) throws SQLException {
        ArrayList<ActiveIndex> activeIndexArrayList = new ArrayList<>();
        while (res.next()) {
            ActiveIndex activeIndex = new ActiveIndex();
            activeIndex.playerUuid = res.getString("player_uuid");
            activeIndex.recentWeek = res.getLong("recent_week");
            activeIndex.oneWeekAgo = res.getLong("one_week_ago");
            activeIndex.twoWeekAgo = res.getLong("two_week_ago");
            activeIndex.index = res.getDouble("index");
            activeIndex.update_time = res.getLong("update_time");
            activeIndexArrayList.add(activeIndex);
        }
        return activeIndexArrayList;
    }
}
