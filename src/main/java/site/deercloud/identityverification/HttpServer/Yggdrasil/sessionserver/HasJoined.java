package site.deercloud.identityverification.HttpServer.Yggdrasil.sessionserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.HttpServerManager;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.HttpServer.model.Response;
import site.deercloud.identityverification.HttpServer.model.Token;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.SQLite.TokenDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.sql.Connection;
import java.util.Map;
import java.util.Objects;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.getQuery;

public class HasJoined implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        try {
            MyLogger.debug("HasJoined get 接口触发");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("GET")) {
                Response.err_method_not_allowed(exchange);
                return;
            }
            Map<String, String> query = getQuery(exchange);
            String username = query.get("username");
            String serverId = query.get("serverId");
            String ip = query.get("ip");
            if (username == null || serverId == null) {
                Response.success_no_content(exchange);
                return;
            }

            // 验证缓存令牌有效性
            if (HttpServerManager.getSessionCache().verifyIsExpire(serverId, ip)) {
                MyLogger.debug("缓存令牌已过期");
                Response.success_no_content(exchange);
                return;
            }
            // 获取缓存令牌对应令牌
            String accessToken = HttpServerManager.getSessionCache().getToken(serverId);
            Token token = TokenDAO.selectByAccessToken(accessToken);
            if (token == null) {
                MyLogger.debug("找不到令牌:" + accessToken);
                Response.success_no_content(exchange);
                return;
            }
            // 获取令牌对应的角色
            Profile profile = ProfileDAO.selectByUuid(token.profileUUID);
            if (profile == null) {
                MyLogger.debug("找不到角色:" + token.profileUUID);
                Response.success_no_content(exchange);
                return;
            }
            if (!profile.name.equals(username)) {
                MyLogger.debug("角色名不匹配:" + profile.name + " " + username);
                Response.success_no_content(exchange);
                return;
            }
            MyLogger.debug("HasJoined 信息: " + profile.serialToJSONObject(true, true).toString());
            Response.success_200(exchange, profile.serialToJSONObject(true, true));
        } catch (Exception e) {
            exchange.close();
            MyLogger.debug(e);
        }

    }
}
