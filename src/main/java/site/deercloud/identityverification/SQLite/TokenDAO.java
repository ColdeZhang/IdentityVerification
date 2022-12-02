package site.deercloud.identityverification.SQLite;

import site.deercloud.identityverification.HttpServer.model.Token;

import java.sql.*;

public class TokenDAO {
    public static void createTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS token (\n"
                + "	access_token text PRIMARY KEY,\n"
                + "	client_token text NOT NULL,\n"
                + " user_uuid text NOT NULL,\n"
                + "	profile_uuid text NOT NULL,\n"
                + " expires_at integer NOT NULL\n"
                + ");";
        Statement statement = connection.createStatement();
        statement.execute(sql);
    }

    public static void insert(Connection connection, Token token) throws SQLException {
        String sql = "INSERT INTO token(access_token,client_token,user_uuid,profile_uuid,expires_at) VALUES(?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, token.accessToken);
        preparedStatement.setString(2, token.clientToken);
        preparedStatement.setString(3, token.userUUID);
        preparedStatement.setString(4, token.profileUUID);
        preparedStatement.setLong(5, token.expiresAt);
        preparedStatement.executeUpdate();
    }

    public static Boolean isAccessTokenExpired(Connection connection, String accessToken) throws SQLException {
        String sql = "SELECT * FROM token WHERE access_token = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, accessToken);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong("expires_at") < System.currentTimeMillis();
        } else {
            return true;
        }
    }

    public static Boolean isClientTokenExpired(Connection connection, String clientToken) throws SQLException {
        String sql = "SELECT * FROM token WHERE client_token = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, clientToken);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong("expires_at") < System.currentTimeMillis();
        } else {
            return true;
        }
    }

    public static Boolean isTokenBindProfile(Connection connection, String accessToken) throws SQLException {
        String sql = "SELECT * FROM token WHERE access_token = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, accessToken);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("profile_uuid") != null;
        } else {
            return true;
        }
    }

    public static Token selectByAccessToken(Connection connection, String accessToken) throws SQLException {
        String sql = "SELECT * FROM token WHERE access_token = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, accessToken);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            Token token = new Token();
            token.accessToken = resultSet.getString("access_token");
            token.clientToken = resultSet.getString("client_token");
            token.userUUID = resultSet.getString("user_uuid");
            token.profileUUID = resultSet.getString("profile_uuid");
            token.expiresAt = resultSet.getLong("expires_at");
            return token;
        } else {
            return null;
        }
    }

    public static void deleteByAccessToken(Connection connection, String accessToken) throws SQLException {
        String sql = "DELETE FROM token WHERE access_token = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, accessToken);
        preparedStatement.executeUpdate();
    }

    public static void deleteByProfileUUID(Connection connection, String profileUUID) throws SQLException {
        String sql = "DELETE FROM token WHERE profile_uuid = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, profileUUID);
        preparedStatement.executeUpdate();
    }
}
