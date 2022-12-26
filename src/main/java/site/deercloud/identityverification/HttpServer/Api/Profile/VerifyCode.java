package site.deercloud.identityverification.HttpServer.Api.Profile;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.SQLite.InviteCodeDAO;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;

public class VerifyCode implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            if (!requestHeader(exchange, "GET")) return;
            Map<String, String> params = getQuery(exchange);
            String code = params.get("code");

            if (InviteCodeDAO.getUsed(code)){
                jsonResponse(exchange, 400, "邀请码不可用！", null);
            } else {
                JSONObject jsonObject = new JSONObject();
                String inviter_uuid = InviteCodeDAO.getInviterUUID(code);
                User inviter = UserDAO.selectByUuid(inviter_uuid);
                if (inviter == null) {
                    jsonResponse(exchange, 500, "严重错误，请联系管理员", null);
                    return;
                }
                String inviter_name = inviter.email;
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
