package site.deercloud.identityverification.SQLite;

import site.deercloud.identityverification.HttpServer.model.ActiveIndex;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ActiveIndexDAO {
    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS active_index (\n"
                + "	player_uuid text NOT NULL,\n"
                + "	recent_week integer NOT NULL,\n"
                + "	one_week_ago integer NOT NULL,\n"
                + "	two_week_ago integer NOT NULL,\n"
                + "	'index' double NOT NULL,\n"
                + "	update_time integer NOT NULL,\n"
                + "	PRIMARY KEY(player_uuid)\n"
                + ");";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.execute();
    }

    public static void insert(ActiveIndex activeIndex) throws SQLException {
        String sql = "INSERT INTO active_index(player_uuid,recent_week,one_week_ago,two_week_ago,'index',update_time) VALUES(?,?,?,?,?,?)";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, activeIndex.playerUuid);
        preparedStatement.setLong(2, activeIndex.recentWeek);
        preparedStatement.setLong(3, activeIndex.oneWeekAgo);
        preparedStatement.setLong(4, activeIndex.twoWeekAgo);
        preparedStatement.setDouble(5, activeIndex.index);
        preparedStatement.setLong(6, System.currentTimeMillis());
        preparedStatement.execute();
    }

    public static ActiveIndex query(String playerUuid) throws SQLException {
        String sql = "SELECT * FROM active_index WHERE player_uuid = ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, playerUuid);
        ResultSet res = preparedStatement.executeQuery();
        ArrayList<ActiveIndex> list = ActiveIndex.fillListWithSqlRes(res);
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public static void update(ActiveIndex activeIndex) throws SQLException {
        String sql = "UPDATE active_index SET recent_week = ?,one_week_ago = ?,two_week_ago = ?,'index' = ?,update_time = ? WHERE player_uuid = ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setLong(1, activeIndex.recentWeek);
        preparedStatement.setLong(2, activeIndex.oneWeekAgo);
        preparedStatement.setLong(3, activeIndex.twoWeekAgo);
        preparedStatement.setDouble(4, activeIndex.index);
        preparedStatement.setLong(5, System.currentTimeMillis());
        preparedStatement.setString(6, activeIndex.playerUuid);
        preparedStatement.execute();
    }

    public static ArrayList<ActiveIndex> selectActivityRecords() throws SQLException {
        long current_time = System.currentTimeMillis();
        String sql = "SELECT * FROM active_index WHERE 'index' < 3.75 AND 'index' > 3.0 AND update_time > ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setLong(1, current_time - 1000 * 60 * 60 * 24 * 7);
        ResultSet res = preparedStatement.executeQuery();
        return ActiveIndex.fillListWithSqlRes(res);
    }
}
