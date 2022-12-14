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

import static site.deercloud.identityverification.HttpServer.HttpServerManager.getBody;

public class Join implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            MyLogger.debug("Join post 接口触发");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("POST")){
                Response.err_method_not_allowed(exchange);
                return;
            }
            JSONObject request = getBody(exchange);
            String accessToken = request.getString("accessToken");
            String selectedProfile = request.getString("selectedProfile");
            String serverId = request.getString("serverId");
            MyLogger.debug("获取到的信息: accessToken: " + accessToken + " selectedProfile: " + selectedProfile + " serverId: " + serverId);

            Token token = TokenDAO.selectByAccessToken(accessToken);
            if (token == null || token.profileUUID == null || !token.profileUUID.equals(selectedProfile)){
                MyLogger.debug("令牌无效:" + selectedProfile);
                Response.err_invalid_token(exchange, "无效的令牌", "无效的令牌");
                return;
            }
            // 缓存到内存中
            HttpServerManager.getSessionCache().add(serverId, token.accessToken, exchange.getRemoteAddress().getHostString());
            MyLogger.debug("缓存令牌:" + token.accessToken + " serverID:" + serverId);

            MyLogger.debug("准备返回204");
            Response.success_no_content(exchange);
        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
