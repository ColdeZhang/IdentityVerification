package site.deercloud.identityverification.SQLite;

import site.deercloud.identityverification.HttpServer.model.Profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProfileDAO {
    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS profile (\n"
                + "	uuid text PRIMARY KEY,\n"                           // UUID
                + " belong_to text NOT NULL,\n"                         // 所属User
                + "	name text NOT NULL,\n"                              // 玩家名字
                + "	textures text NOT NULL,\n"                          // 材质
                + "	textures_signature text NOT NULL,\n"                // 材质签名
                + " uploadableTextures text ,\n"                // 可上传的材质
                + " uploadableTextures_signature text ,\n"      // 可上传的材质签名
                + "	update_time integer NOT NULL\n"                     // 数据更新时间
                + ");";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.execute();
    }

    public static void insert(Profile profile) throws SQLException {
        String sql = "INSERT INTO profile(uuid,belong_to,name,textures,textures_signature,uploadableTextures,uploadableTextures_signature,update_time) VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, profile.uuid);
        preparedStatement.setString(2, profile.belongTo);
        preparedStatement.setString(3, profile.name);
        preparedStatement.setString(4, profile.textures);
        preparedStatement.setString(5, profile.textures_signature);
        preparedStatement.setString(6, profile.uploadableTextures);
        preparedStatement.setString(7, profile.uploadableTextures_signature);
        preparedStatement.setLong(8, System.currentTimeMillis());
        preparedStatement.executeUpdate();
    }

    public static void update(Profile profile) throws SQLException {
        String sql = "UPDATE profile SET name = ?, textures = ?, textures_signature = ?, uploadableTextures = ?, uploadableTextures_signature = ?, update_time = ? WHERE uuid = ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, profile.name);
        preparedStatement.setString(2, profile.textures);
        preparedStatement.setString(3, profile.textures_signature);
        preparedStatement.setString(4, profile.uploadableTextures);
        preparedStatement.setString(5, profile.uploadableTextures_signature);
        preparedStatement.setLong(6, System.currentTimeMillis());
        preparedStatement.setString(7, profile.uuid);
        preparedStatement.executeUpdate();
    }

    public static Profile selectByUuid(String uuid) throws SQLException {
        String sql = "SELECT * FROM profile WHERE uuid = ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, uuid);
        Profile profile = new Profile();
        if (preparedStatement.executeQuery().next()) {
            profile.uuid = preparedStatement.getResultSet().getString("uuid");
            profile.belongTo = preparedStatement.getResultSet().getString("belong_to");
            profile.name = preparedStatement.getResultSet().getString("name");
            profile.textures = preparedStatement.getResultSet().getString("textures");
            profile.textures_signature = preparedStatement.getResultSet().getString("textures_signature");
            profile.uploadableTextures = preparedStatement.getResultSet().getString("uploadableTextures");
            profile.uploadableTextures_signature = preparedStatement.getResultSet().getString("uploadableTextures_signature");
            return profile;
        }
        return null;
    }

    public static ArrayList<Profile> selectAllByBelongTo(String belongTo) throws SQLException {
        String sql = "SELECT * FROM profile WHERE belong_to = ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, belongTo);
        ArrayList<Profile> profiles = new ArrayList<>();
        while (preparedStatement.executeQuery().next()) {
            Profile profile = new Profile();
            profile.uuid = preparedStatement.executeQuery().getString("uuid");
            profile.belongTo = belongTo;
            profile.name = preparedStatement.executeQuery().getString("name");
            profile.textures = preparedStatement.executeQuery().getString("textures");
            profile.textures_signature = preparedStatement.executeQuery().getString("textures_signature");
            profile.uploadableTextures = preparedStatement.executeQuery().getString("uploadableTextures");
            profile.uploadableTextures_signature = preparedStatement.executeQuery().getString("uploadableTextures_signature");
            profiles.add(profile);
        }
        return profiles;
    }

    public static Profile selectByName(String name) throws SQLException {
        String sql = "SELECT * FROM profile WHERE name = ?";
        PreparedStatement preparedStatement = SqlManager.session.prepareStatement(sql);
        preparedStatement.setString(1, name);
        Profile profile = new Profile();
        if (preparedStatement.executeQuery().next()) {
            profile.uuid = preparedStatement.getResultSet().getString("uuid");
            profile.belongTo = preparedStatement.getResultSet().getString("belong_to");
            profile.name = name;
            profile.textures = preparedStatement.getResultSet().getString("textures");
            profile.textures_signature = preparedStatement.getResultSet().getString("textures_signature");
            profile.uploadableTextures = preparedStatement.getResultSet().getString("uploadableTextures");
            profile.uploadableTextures_signature = preparedStatement.getResultSet().getString("uploadableTextures_signature");
            return profile;
        }
        return null;
    }
}
