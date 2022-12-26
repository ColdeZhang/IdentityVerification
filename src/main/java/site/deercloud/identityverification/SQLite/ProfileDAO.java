package site.deercloud.identityverification.SQLite;

import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.Utils.MyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProfileDAO {
    public static void createTable() throws SQLException {
        // UUID
        // 所属User
        // 玩家名字
        // 材质
        // 材质签名
        // 可上传的材质
        // 可上传的材质签名
        // 数据更新时间
        // 是否是正版材质
        String sql = """
                CREATE TABLE IF NOT EXISTS profile (
                    uuid text PRIMARY KEY,
                    belong_to text NOT NULL,
                	name text NOT NULL,
                	textures text NOT NULL,
                	textures_signature text NOT NULL,
                    uploadableTextures text ,
                    uploadableTextures_signature text ,
                	update_time integer NOT NULL,
                    is_genuine integer NOT NULL
                );""";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.execute();
    }

    public static void insert(Profile profile) throws SQLException {
        String sql = "INSERT INTO profile(uuid,belong_to,name,textures,textures_signature,uploadableTextures,uploadableTextures_signature,update_time,is_genuine) VALUES(?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, profile.uuid);
        preparedStatement.setString(2, profile.belongTo);
        preparedStatement.setString(3, profile.name);
        preparedStatement.setString(4, profile.textures);
        preparedStatement.setString(5, profile.textures_signature);
        preparedStatement.setString(6, profile.uploadableTextures);
        preparedStatement.setString(7, profile.uploadableTextures_signature);
        preparedStatement.setLong(8, System.currentTimeMillis());
        preparedStatement.setBoolean(9, profile.is_genuine);
        preparedStatement.executeUpdate();
    }

    public static void update(Profile profile) throws SQLException {
        String sql = "UPDATE profile SET name = ?, textures = ?, textures_signature = ?, uploadableTextures = ?, uploadableTextures_signature = ?, update_time = ?, is_genuine = ? WHERE uuid = ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, profile.name);
        preparedStatement.setString(2, profile.textures);
        preparedStatement.setString(3, profile.textures_signature);
        preparedStatement.setString(4, profile.uploadableTextures);
        preparedStatement.setString(5, profile.uploadableTextures_signature);
        preparedStatement.setLong(6, System.currentTimeMillis());
        preparedStatement.setBoolean(7, profile.is_genuine);
        preparedStatement.setString(8, profile.uuid);
        preparedStatement.executeUpdate();
    }

    public static Profile selectByUuid(String uuid) throws SQLException {
        String sql = "SELECT * FROM profile WHERE uuid = ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, uuid);
        ResultSet resultSet = preparedStatement.executeQuery();
        Profile profile = new Profile();
        if (resultSet.next()) {
            profile.uuid = resultSet.getString("uuid");
            profile.belongTo = resultSet.getString("belong_to");
            profile.name = resultSet.getString("name");
            profile.textures = resultSet.getString("textures");
            profile.textures_signature = resultSet.getString("textures_signature");
            profile.uploadableTextures = resultSet.getString("uploadableTextures");
            profile.uploadableTextures_signature = resultSet.getString("uploadableTextures_signature");
            profile.is_genuine = resultSet.getBoolean("is_genuine");
            return profile;
        }
        return null;
    }

    public static ArrayList<Profile> selectAllByBelongTo(String belongTo) throws SQLException {
        String sql = "SELECT * FROM profile WHERE belong_to = ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, belongTo);
        ArrayList<Profile> profiles = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Profile profile = new Profile();
            profile.uuid = resultSet.getString("uuid");
            profile.belongTo = belongTo;
            profile.name = resultSet.getString("name");
            profile.textures = resultSet.getString("textures");
            profile.textures_signature = resultSet.getString("textures_signature");
            profile.uploadableTextures = resultSet.getString("uploadableTextures");
            profile.uploadableTextures_signature = resultSet.getString("uploadableTextures_signature");
            profile.is_genuine = resultSet.getBoolean("is_genuine");
            profiles.add(profile);
            MyLogger.debug(profile.serialToJSONObject(true, true).toString());
        }
        return profiles;
    }

    public static Profile selectByName(String name) throws SQLException {
        String sql = "SELECT * FROM profile WHERE name = ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, name);
        Profile profile = new Profile();
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            profile.uuid = resultSet.getString("uuid");
            profile.belongTo = resultSet.getString("belong_to");
            profile.name = name;
            profile.textures = resultSet.getString("textures");
            profile.textures_signature = resultSet.getString("textures_signature");
            profile.uploadableTextures = resultSet.getString("uploadableTextures");
            profile.uploadableTextures_signature = resultSet.getString("uploadableTextures_signature");
            profile.is_genuine = resultSet.getBoolean("is_genuine");
            return profile;
        }
        return null;
    }

    public static void deleteByUUID(String uuid) throws SQLException {
        String sql = "DELETE FROM profile WHERE uuid = ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, uuid);
        preparedStatement.executeUpdate();
    }
}
