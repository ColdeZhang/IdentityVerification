package site.deercloud.identityverification.HttpServer.Api;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.SQLite.InviteRelationDAO;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;

public class GetInviter implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (!requestHeader(exchange, "GET")) return;

            Map<String, String> params = getQuery(exchange);

            if (!params.containsKey("uuid")) {
                jsonResponse(exchange, 400, "参数错误", null);
                return;
            }
            String uuid = params.get("uuid");
            String inviter_uuid = InviteRelationDAO.selectInviterByInvitee(uuid);
            if (inviter_uuid == null) {
                jsonResponse(exchange, 400, "找不到邀请者，可能不是通过邀请注册的，或者邀请者注销了账号。", null);
                return;
            }
            Profile inviter_profile = ProfileDAO.selectByUuid(inviter_uuid);
            if (inviter_profile == null) {
                jsonResponse(exchange, 400, "找不到邀请者的资料，可能不是通过邀请注册的，或者邀请者注销了账号。", null);
                return;
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("inviter_uuid", inviter_profile.uuid);
            jsonObject.put("inviter_name", inviter_profile.name);

            jsonResponse(exchange, 200, "OK", jsonObject);

        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }

    }
}
