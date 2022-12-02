package site.deercloud.identityverification.HttpServer.Api;

import com.alibaba.fastjson.JSONArray;
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
import java.util.ArrayList;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.getBody;
import static site.deercloud.identityverification.HttpServer.HttpServerManager.getBodyArray;

public class GetProfiles implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("POST")) {
                Response.err_method_not_allowed(exchange);
                return;
            }
            JSONArray profiles_json = getBodyArray(exchange);
            if (profiles_json.size() > 5) {
                Response.success_no_content(exchange);
                return;
            }
            ArrayList<Profile> profiles = new ArrayList<>();
            for (int i = 0; i < profiles_json.size(); i++) {
                Profile profile = ProfileDAO.selectByName(profiles_json.getString(i));
                if (profile != null) {
                    profiles.add(profile);
                }
            }
            JSONArray response = new JSONArray();
            for (Profile profile : profiles) {
                response.add(profile.serialToJSONObject(false, false));
            }
            Response.success_200(exchange, response);

        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }

    }
}
