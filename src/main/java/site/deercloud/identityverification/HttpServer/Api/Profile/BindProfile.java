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
import static site.deercloud.identityverification.Utils.Utils.getProfileFromRemote;
import static site.deercloud.identityverification.Utils.Utils.getUUIDFromRemote;

// 绑定正版账号角色
public class BindProfile implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (!requestHeader(exchange, "POST")) return;
            WebToken webToken = authorizationCheck(exchange, User.ROLE.USER);
            if (webToken == null) return;

            JSONObject body = getBody(exchange);
            String name = body.getString("profile_name");

            String uuid = getUUIDFromRemote(name);
            if (uuid == null) {
                jsonResponse(exchange, 400, "仅正版玩家可绑定。", null);
                return;
            }
            JSONObject profile_json = getProfileFromRemote(uuid);
            if (profile_json == null) {
                jsonResponse(exchange, 400, "网络波动，与Mojiang服务器通信失败，重试。", null);
                return;
            }
            Profile profile =  new Profile(profile_json);
            profile.belongTo = webToken.userUUID;
            profile.is_genuine = true;
            ProfileDAO.insert(profile);
            MyLogger.debug("用户绑定角色成功：" + profile.name);

            jsonResponse(exchange, 200, "绑定成功", null);
        }catch (Exception e) {
            MyLogger.debug(e);
            exchange.close();
        }
    }
}
