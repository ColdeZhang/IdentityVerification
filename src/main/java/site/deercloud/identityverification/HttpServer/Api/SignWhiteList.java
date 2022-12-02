package site.deercloud.identityverification.HttpServer.Api;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.SQLite.WhiteListDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.getBody;
import static site.deercloud.identityverification.HttpServer.HttpServerManager.jsonResponse;
import static site.deercloud.identityverification.SQLite.SqlManager.getConnection;
import static site.deercloud.identityverification.Utils.Utils.*;

public class SignWhiteList implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("POST")){
                jsonResponse(exchange, 405, "Method Not Allowed", null);
                return;
            }
            JSONObject jsonObject = getBody(exchange);
            Connection connection = getConnection();

            String id = jsonObject.getString("id");                     // 身份证（实名认证用）
            String name = jsonObject.getString("name");                 // 真实姓名（实名认证用）
            String profile_name = jsonObject.getString("username");         // 昵称
            Boolean is_genuine = jsonObject.getBoolean("is_genuine");   // 是否正版
            String uuid;
            if (is_genuine) {
                // 正版玩家 - 从Mojiang服务器获取uuid
                uuid = getUUIDFromRemote(profile_name);
            } else {
                // 离线玩家 - 从本地数据库获取uuid
                Profile profile = ProfileDAO.selectByName(connection, profile_name);
                if (profile == null) {
                    jsonResponse(exchange, 400, "玩家不存在", null);
                    return;
                }
                uuid = UserDAO.selectByUuid(connection, profile.belongTo).uuid;
            }
            if (uuid == null) {
                jsonResponse(exchange, 400, "UUID校验不通过，请检查后输入。", null);
                return;
            }
            if (WhiteListDAO.isIdInWhiteList(connection, id.hashCode())) {
                jsonResponse(exchange, 400, "请勿重复添加白名单，本服一人一号。", null);
                return;
            }
            // TODO: 调实名认证接口 不通过直接返回错误
            //
            //
            WhiteListDAO.insert(connection, uuid, is_genuine, id.hashCode());
            jsonResponse(exchange, 200, "添加白名单成功！", null);
        } catch (Exception e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
