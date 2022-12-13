package site.deercloud.identityverification.HttpServer.Api;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.SQLite.InviteRelationDAO;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.Utils.Utils.*;

public class GetInviter implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (!requestHeader(exchange, "GET")) return;

            Map<String, String> params = getQuery(exchange);

            String uuid = params.get("uuid");
            String inviter_uuid = InviteRelationDAO.getInviterUUID(uuid);
            if (inviter_uuid == null) {
                jsonResponse(exchange, 400, "找不到邀请者，这可能是个正版玩家。", null);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("inviter_uuid", inviter_uuid);
            jsonObject.put("inviter_name", UserDAO.selectByUuid(inviter_uuid).uuid);

            jsonResponse(exchange, 200, "OK", jsonObject);

        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }

    }
}
