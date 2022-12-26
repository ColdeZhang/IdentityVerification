package site.deercloud.identityverification.HttpServer.Api.Profile;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.HttpServer.model.WebToken;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.util.Objects;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;

public class RemoveProfile implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (!requestHeader(exchange, "POST")) return;
            WebToken webToken = authorizationCheck(exchange, User.ROLE.USER);
            if (webToken == null) return;

            JSONObject body = getBody(exchange);
            String profile_uuid = body.getString("profile_uuid");
            Profile profile = ProfileDAO.selectByUuid(profile_uuid);
            if (profile == null) {
                jsonResponse(exchange, 400, "角色不存在", null);
                return;
            }
            if (!Objects.equals(profile.belongTo, webToken.userUUID)) {
                jsonResponse(exchange, 400, "非法的操作", null);
                return;
            }

            ProfileDAO.deleteByUUID(profile_uuid);

            jsonResponse(exchange, 200, "删除绑定信息成功。", null);
        }catch (Exception e) {
            MyLogger.debug(e);
            exchange.close();
        }
    }
}
