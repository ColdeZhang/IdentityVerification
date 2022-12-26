package site.deercloud.identityverification.HttpServer.Api.Profile;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.Controller.EmailCodeCache;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.EmailSender;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.RandomCode;

import java.io.IOException;
import java.sql.SQLException;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.HttpServer.HttpServerManager.jsonResponse;
import static site.deercloud.identityverification.Utils.Utils.getUUIDFromRemote;

public class VerifyName implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!requestHeader(exchange, "POST")) return;
            JSONObject request = getBody(exchange);
            String name = request.getString("profile_name");
            if (name == null || name.equals("")){
                jsonResponse(exchange, 400, "昵称不能为空", null);
                return;
            }
            if (ProfileDAO.selectByName(name) != null) {
                jsonResponse(exchange, 500, "昵称已存在于本服务器。", null);
                return;
            }
            if (getUUIDFromRemote(name) != null) {
                jsonResponse(exchange, 400, "昵称已被一个正版玩家使用，建议不要使用此昵称。", null);
                return;
            }
            jsonResponse(exchange, 200, "你可以使用这个昵称！", null);
        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
