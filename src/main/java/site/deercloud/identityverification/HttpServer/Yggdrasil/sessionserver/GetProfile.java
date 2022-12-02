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

import static site.deercloud.identityverification.Utils.Utils.ParseQueryString;

public class GetProfile implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("GET")) {
                Response.err_method_not_allowed(exchange);
                return;
            }
            String uuid = exchange.getRequestURI().getPath().split("/")[4];
            Map<String, String> request = ParseQueryString(exchange.getRequestURI().getQuery());
            boolean unsigned = true;
            if (request.containsKey("unsigned")) {
                unsigned = Boolean.parseBoolean(request.get("unsigned"));
            }
            Connection connection = SqlManager.getConnection();
            Profile profile = ProfileDAO.selectByUuid(connection, uuid);
            if (profile == null) {
                Response.success_no_content(exchange);
                return;
            }
            Response.success_200(exchange, profile.serialToJSONObject(true, unsigned));
        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
