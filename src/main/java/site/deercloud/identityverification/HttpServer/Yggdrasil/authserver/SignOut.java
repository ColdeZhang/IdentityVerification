package site.deercloud.identityverification.HttpServer.Yggdrasil.authserver;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.HttpServer.model.Response;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.SQLite.TokenDAO;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.getBody;

public class SignOut implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("POST")) {
                Response.err_method_not_allowed(exchange);
                return;
            }
            JSONObject request = getBody(exchange);
            String username = request.getString("username");
            String password = request.getString("password");

            if (!UserDAO.checkPassword(username, password)) {
                Response.err_password_wrong(exchange, null);
                return;
            }
            User user = UserDAO.selectByEmail(username);
            // 吊销用户的所有令牌
            ArrayList<Profile> profiles = ProfileDAO.selectAllByBelongTo(user.uuid);
            for (Profile profile : profiles) {
                TokenDAO.deleteByProfileUUID(profile.uuid);
            }
            Response.success_no_content(exchange);
        } catch(SQLException | IOException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}