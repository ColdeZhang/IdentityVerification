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
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("GET")) {
                Response.err_method_not_allowed(exchange);
                return;
            }
            Map<String, String> query = getQuery(exchange);
            String username = query.get("username");
            String serverId = query.get("serverId");
            String ip = query.get("ip");

            Connection connection = SqlManager.getConnection();

            // 验证缓存令牌有效性
            if (HttpServerManager.getSessionCache().verifyIsExpire(serverId, ip)) {
                Response.success_no_content(exchange);
                return;
            }
            // 获取缓存令牌对应令牌
            String accessToken = HttpServerManager.getSessionCache().getToken(serverId);
            Token token = TokenDAO.selectByAccessToken(connection, accessToken);
            if (token == null || token.profileUUID == null || !token.profileUUID.equals(username)) {
                Response.success_no_content(exchange);
                return;
            }
            // 获取令牌对应的角色
            Profile profile = ProfileDAO.selectByUuid(connection, token.profileUUID);
            if (profile == null) {
                Response.success_no_content(exchange);
                return;
            }
            // 检查角色名是否一致
            if (!Objects.equals(username, profile.name)) {
                Response.success_no_content(exchange);
                return;
            }
            Response.success_200(exchange, profile.serialToJSONObject(true, true));
        } catch (Exception e) {
            exchange.close();
            MyLogger.debug(e);
        }

    }
}
