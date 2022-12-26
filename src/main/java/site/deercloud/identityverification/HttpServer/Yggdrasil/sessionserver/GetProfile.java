package site.deercloud.identityverification.HttpServer.Yggdrasil.sessionserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.HttpServer.model.Response;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.getQuery;

public class GetProfile implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            MyLogger.debug("GetProfile get 接口触发");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("GET")) {
                Response.err_method_not_allowed(exchange);
                return;
            }
            String uuid = exchange.getRequestURI().getPath().split("/")[5];
            MyLogger.debug("GetProfile Path: " + exchange.getRequestURI().getPath());
            Map<String, String> request = getQuery(exchange);
            boolean unsigned = true;
            if (request.containsKey("unsigned")) {
                unsigned = Boolean.parseBoolean(request.get("unsigned"));
            }
            MyLogger.debug("uuid: " + uuid);
            MyLogger.debug("unsigned: " + unsigned);
            Profile profile = ProfileDAO.selectByUuid(uuid);
            if (profile == null) {
                Response.success_no_content(exchange);
                return;
            }
            // 如果是绑定的是正版账号信息，为了安全考虑不允许通过外置登录进入服务器
            if (profile.is_genuine) {
                Response.success_no_content(exchange);
                return;
            }
            MyLogger.debug("Profile 信息: " + profile.serialToJSONObject(true, unsigned).toString());
            Response.success_200(exchange, profile.serialToJSONObject(true, unsigned));
        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
