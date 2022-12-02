package site.deercloud.identityverification.HttpServer.Api.Register;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.SQLite.InviteCodeDAO;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.getQuery;
import static site.deercloud.identityverification.HttpServer.HttpServerManager.jsonResponse;
public class VerifyCode implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("GET")){
                jsonResponse(exchange, 405, "Method Not Allowed", null);
                return;
            }
            Map<String, String> params = getQuery(exchange);
            String code = params.get("code");
            Connection conn = SqlManager.getConnection();

            if (!InviteCodeDAO.isValid(conn, code)){
                jsonResponse(exchange, 400, "邀请码不可用！", null);
            } else {
                JSONObject jsonObject = new JSONObject();
                String inviter_uuid = InviteCodeDAO.getInviterUUID(conn, code);
                String inviter_name = UserDAO.selectByUuid(conn, inviter_uuid).email;
                // 返回邀请者的名字
                jsonObject.put("inviter_name", inviter_name);
                jsonResponse(exchange, 200, "OK", jsonObject);
            }
        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
