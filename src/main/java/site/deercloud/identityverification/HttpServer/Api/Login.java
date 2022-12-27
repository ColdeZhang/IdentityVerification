package site.deercloud.identityverification.HttpServer.Api;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.Controller.WebTokenCache;
import site.deercloud.identityverification.HttpServer.model.Response;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.HttpServer.model.WebToken;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.RandomCode;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.SQLite.UserDAO.checkPassword;

public class Login implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!requestHeader(exchange, "POST")) return;
            JSONObject request = getBody(exchange);
            if (!request.containsKey("username") || !request.containsKey("password")) {
                jsonResponse(exchange, 400, "参数错误", null);
                return;
            }
            String username = request.getString("username");
            String password = request.getString("password");

            if (username == null || password == null) {
                jsonResponse(exchange, 500, "用户名或密码不能为空", null);
                return;
            }

            User user = UserDAO.selectByEmail(username);
            if (user == null) {
                jsonResponse(exchange, 500, "用户不存在", null);
                return;
            }
            if (!checkPassword(username, password)) {
                jsonResponse(exchange, 500, "用户名或密码错误", null);
                return;
            }
            // 生成令牌
            WebToken webToken = WebToken.newWebToken(user.uuid);
            WebTokenCache.addWebToken(webToken);
            JSONObject response = new JSONObject();
            response.put("token", WebToken.ParseWebTokenToStr(webToken));
            jsonResponse(exchange, 200, "登录成功", response);
        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
