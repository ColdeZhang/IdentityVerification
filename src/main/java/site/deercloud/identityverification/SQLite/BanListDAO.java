package site.deercloud.identityverification.SQLite;

import site.deercloud.identityverification.HttpServer.model.BanRecord;

import java.sql.*;
import java.util.ArrayList;

public class BanListDAO {
    public static void createTable() throws SQLException {
        // ID
        // UUID
        // 玩家名字
        // 封禁时间
        // 封禁原因
        // 数据创建时间
        String sql = """
                CREATE TABLE IF NOT EXISTS ban_list (
                    id integer PRIMARY KEY AUTOINCREMENT,
                	uuid text NOT NULL,
                	ban_time integer NOT NULL,
                	ban_reason text NOT NULL,
                	create_time integer NOT NULL,
                	update_time integer NOT NULL
                );""";
        Statement stat = SqlManager.session.createStatement();
        stat.executeUpdate(sql);
    }

    public static void insert(BanRecord record) throws SQLException {
        String sql = "INSERT INTO ban_list(uuid,ban_time,ban_reason,create_time,update_time) VALUES(?,?,?,?,?)";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, record.uuid);
        prep.setLong(2, record.ban_time);
        prep.setString(3, record.ban_reason);
        prep.setLong(4, System.currentTimeMillis());
        prep.setLong(5, System.currentTimeMillis());
        prep.executeUpdate();
    }

    public static BanRecord isBanned(String uuid) throws SQLException {
        String sql = "SELECT * FROM ban_list WHERE uuid = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, uuid);
        ResultSet res = prep.executeQuery();
        while (res.next()) {
            BanRecord record = new BanRecord();
            record.id = res.getInt("id");
            record.uuid = res.getString("uuid");
            record.ban_time = res.getLong("ban_time");
            record.ban_reason = res.getString("ban_reason");
            record.create_time = res.getLong("create_time");
            record.update_time = res.getLong("update_time");
            if (record.ban_time < System.currentTimeMillis()) {
                continue;
            }
            return record;
        }
        return null;
    }

    public static BanRecord selectById(Integer id) throws SQLException {
        String sql = "SELECT * FROM ban_list WHERE id = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setInt(1, id);
        ResultSet res = prep.executeQuery();
        if (res.next()) {
            BanRecord record = new BanRecord();
            record.id = res.getInt("id");
            record.uuid = res.getString("uuid");
            record.ban_time = res.getLong("ban_time");
            record.ban_reason = res.getString("ban_reason");
            record.create_time = res.getLong("create_time");
            record.update_time = res.getLong("update_time");
            return record;
        }
        return null;
    }

    public static void updateById(Integer id, BanRecord record) throws SQLException {
        String sql = "UPDATE ban_list SET uuid = ?, ban_time = ?, ban_reason = ?, update_time = ? WHERE id = ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setString(1, record.uuid);
        prep.setLong(2, record.ban_time);
        prep.setString(3, record.ban_reason);
        prep.setLong(4, System.currentTimeMillis());
        prep.setInt(5, id);
        prep.executeUpdate();
    }

    public static int selectTotalPage(int limit, boolean is_banning, boolean finished) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ban_list";
        if (is_banning || finished) {
            sql += " WHERE";
        }
        if (is_banning) {
            sql += " ban_time > " + System.currentTimeMillis();
            if (finished) {
                sql += " AND";
            }
        }
        if (finished) {
            sql += " ban_time <= " + System.currentTimeMillis();
        }
        Statement stat = SqlManager.session.createStatement();
        ResultSet res = stat.executeQuery(sql);
        if (res.next()) {
            int total = res.getInt(1);
            return (int) Math.ceil((double) total / limit);
        }
        return 0;
    }

    public static ArrayList<BanRecord> selectByPage(int offset, int limit, boolean is_banning, boolean finished) throws SQLException {
        String sql = "SELECT * FROM ban_list";
        if (is_banning || finished) {
            sql += " WHERE";
        }
        if (is_banning) {
            sql += " ban_time > " + System.currentTimeMillis();
            if (finished) {
                sql += " AND";
            }
        }
        if (finished) {
            sql += " ban_time <= " + System.currentTimeMillis();
        }
        sql += " ORDER BY create_time DESC LIMIT ? OFFSET ?";
        PreparedStatement prep = SqlManager.session.prepareStatement(sql);
        prep.setInt(1, limit);
        prep.setInt(2, offset);
        ResultSet res = prep.executeQuery();
        ArrayList<BanRecord> records = new ArrayList<>();
        while (res.next()) {
            BanRecord record = new BanRecord();
            record.id = res.getInt("id");
            record.uuid = res.getString("uuid");
            record.ban_time = res.getLong("ban_time");
            record.ban_reason = res.getString("ban_reason");
            record.create_time = res.getLong("create_time");
            record.update_time = res.getLong("update_time");
            records.add(record);
        }
        return records;
    }
}
