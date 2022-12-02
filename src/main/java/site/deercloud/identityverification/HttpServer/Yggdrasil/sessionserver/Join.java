package site.deercloud.identityverification.HttpServer.Yggdrasil.sessionserver;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.HttpServerManager;
import site.deercloud.identityverification.HttpServer.model.Response;
import site.deercloud.identityverification.HttpServer.model.Token;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.SQLite.TokenDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Join implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("POST")){
                Response.err_method_not_allowed(exchange);
                return;
            }
            JSONObject request = JSONObject.parseObject(exchange.getRequestBody().toString());
            String accessToken = request.getString("accessToken");
            String selectedProfile = request.getString("selectedProfile");
            String serverId = request.getString("serverId");

            Connection connection = SqlManager.getConnection();
            Token token = TokenDAO.selectByAccessToken(connection, accessToken);
            if (token == null || token.profileUUID == null || !token.profileUUID.equals(selectedProfile)){
                Response.err_invalid_token(exchange, "无效的令牌", "无效的令牌");
                return;
            }
            // 缓存到内存中
            HttpServerManager.getSessionCache().add(serverId, token.accessToken, exchange.getRemoteAddress().getHostString());

            Response.success_no_content(exchange);
        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
