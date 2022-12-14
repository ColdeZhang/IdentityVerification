package site.deercloud.identityverification.HttpServer.Yggdrasil.authserver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.HttpServer.model.Response;
import site.deercloud.identityverification.HttpServer.model.Token;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.SQLite.TokenDAO;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.UnsignedUUID;

import java.util.ArrayList;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.getBody;
import static site.deercloud.identityverification.HttpServer.model.Response.*;

public class Authenticate implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange){
        try {
            MyLogger.debug("Authenticate post 接口触发");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("POST")){
                Response.err_method_not_allowed(exchange);
                return;
            }
            JSONObject body         = getBody(exchange);
            String username         = body.getString("username");       // 邮箱（或其他凭证）
            String password         = body.getString("password");       // 密码
            String clientToken      = body.getString("clientToken");    // 由客户端指定的令牌的 clientToken（可选）
            Boolean requestUser     = body.getBoolean("requestUser");   // 是否在响应中包含用户信息，默认 false
            JSONObject agent        = body.getJSONObject("agent");
            String agent_name       = agent.getString("name");
            String agent_version    = agent.getString("version");


            if (!UserDAO.checkPassword(username, password)) {
                err_password_wrong(exchange, "密码错误，或短时间内多次登录失败而被暂时禁止登录");
            }
            MyLogger.debug("密码验证通过" + username);
            User user = UserDAO.selectByEmail(username);
            MyLogger.debug(user.serialToJSONObject().toString());
            if (clientToken == null) {
                clientToken = UnsignedUUID.GenerateUUID();
            }
            String accessToken = UnsignedUUID.GenerateUUID();

            // 颁发令牌
            Token token = new Token();
            token.accessToken = accessToken;
            token.clientToken = clientToken;
            token.userUUID = user.uuid;
            token.expiresAt = System.currentTimeMillis() + 432000000;

            Profile profile = null;
            // 若用户没有任何角色，则为空；
            // 若用户仅有一个角色，那么通常绑定到该角色；
            // 若用户有多个角色，通常为空，以便客户端进行选择。也就是说如果绑定的角色为空，则需要客户端进行角色选择。
            ArrayList<Profile> profiles = ProfileDAO.selectAllByBelongTo(user.uuid);
            if (profiles.size() == 1) {
                profile = profiles.get(0);
                token.profileUUID = profile.uuid;
            } else {
                token.profileUUID = "";
            }
            TokenDAO.insert(token);

            JSONArray availableProfiles = new JSONArray();
            for (Profile p : profiles) {
                availableProfiles.add(p.serialToJSONObject(false, true));
            }

            JSONObject response = new JSONObject();
            response.put("accessToken", accessToken);
            response.put("clientToken", clientToken);
            response.put("availableProfiles", availableProfiles);
            if (profile != null) {
                response.put("selectedProfile", profile.serialToJSONObject(false, true));
            }
            if (requestUser) {
                response.put("user", user.serialToJSONObject());
            }
            Response.success_200(exchange, response);
        } catch (Exception e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
