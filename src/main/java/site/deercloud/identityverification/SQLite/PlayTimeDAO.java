package site.deercloud.identityverification.SQLite;

import site.deercloud.identityverification.HttpServer.model.PlayTimeStatics;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlayTimeDAO {

    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS play_time_static (\n"
                + " id integer PRIMARY KEY,\n"              // ID
                + " player_uuid text NOT NULL,\n"           // 玩家UUID
                + " afk_time integer NOT NULL,\n"           // AFK时间
                + " active_time integer NOT NULL,\n"        // 活跃时间
                + " online_time integer NOT NULL,\n"        // 在线时间
                + " join_time integer NOT NULL,\n"          // 加入时间
                + " quit_time integer NOT NULL,\n"          // 离开时间
                + " record_time integer NOT NULL\n"         // 记录创建时间
                + ");";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.execute();
    }

    public static void insert(PlayTimeStatics statics) throws SQLException {
        String sql = "INSERT INTO play_time_static(player_uuid,afk_time,active_time,online_time,join_time,quit_time,record_time) VALUES(?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, statics.playerUuid);
        preparedStatement.setLong(2, statics.afkTime);
        preparedStatement.setLong(3, statics.activeTime);
        preparedStatement.setLong(4, statics.onlineTime);
        preparedStatement.setLong(5, statics.joinTime);
        preparedStatement.setLong(6, statics.quitTime);
        preparedStatement.setLong(7, System.currentTimeMillis());
        preparedStatement.execute();
    }

    public static ArrayList<PlayTimeStatics> selectByTime(String uuid, Long from, Long to) throws SQLException {
        String sql = "SELECT * FROM play_time_static WHERE player_uuid = ? AND record_time >= ? AND record_time <= ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, uuid);
        preparedStatement.setLong(2, from);
        preparedStatement.setLong(3, to);
        ResultSet res = preparedStatement.executeQuery();
        ArrayList<PlayTimeStatics> returnList = new ArrayList<>();
        while (res.next()) {
            PlayTimeStatics statics = new PlayTimeStatics();
            statics.id = res.getInt("id");
            statics.playerUuid = res.getString("player_uuid");
            statics.afkTime = res.getLong("afk_time");
            statics.activeTime = res.getLong("active_time");
            statics.onlineTime = res.getLong("online_time");
            statics.joinTime = res.getLong("join_time");
            statics.quitTime = res.getLong("quit_time");
            statics.recordTime = res.getLong("record_time");
            returnList.add(statics);
        }
        return returnList;
    }

    public static Integer countsOfPlayer(String uuid, Long from, Long to) throws SQLException {
        String sql = "SELECT COUNT(*) FROM play_time_static WHERE player_uuid = ? AND record_time >= ? AND record_time <= ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, uuid);
        preparedStatement.setLong(2, from);
        preparedStatement.setLong(3, to);
        ResultSet res = preparedStatement.executeQuery();
        return res.getInt(1);
    }

}
