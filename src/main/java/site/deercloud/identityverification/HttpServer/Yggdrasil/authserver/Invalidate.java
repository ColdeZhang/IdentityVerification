package site.deercloud.identityverification.HttpServer.Yggdrasil.authserver;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Response;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.SQLite.TokenDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.getBody;

public class Invalidate implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            MyLogger.debug("Invalidate post 接口触发");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("POST")) {
                Response.err_method_not_allowed(exchange);
                return;
            }
            JSONObject request = getBody(exchange);
            String accessToken = request.getString("accessToken");
            String clientToken = request.getString("clientToken");


            TokenDAO.deleteByAccessToken(accessToken);

            Response.success_no_content(exchange);
        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }

    }
}
