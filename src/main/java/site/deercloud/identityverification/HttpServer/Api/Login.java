package site.deercloud.identityverification.HttpServer.Api;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Response;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;

public class Login implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            requestHeader(exchange, "POST");
            JSONObject request = getBody(exchange);
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
            if (!user.password.equals(password)) {
                jsonResponse(exchange, 500, "用户名或密码错误", null);
                return;
            }
            // TODO: 2021/1/17 生成令牌


        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
