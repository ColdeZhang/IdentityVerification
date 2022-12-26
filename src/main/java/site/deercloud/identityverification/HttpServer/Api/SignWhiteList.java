package site.deercloud.identityverification.HttpServer.Api;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.HttpServer.model.WebToken;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.SQLite.WhiteListDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.Utils.Utils.*;

public class SignWhiteList implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!requestHeader(exchange, "POST")) return;
            WebToken webToken = authorizationCheck(exchange, User.ROLE.USER);
            if (webToken == null) return;

            JSONObject jsonObject = getBody(exchange);
            String id = jsonObject.getString("id");                     // 身份证（实名认证用）
            String name = jsonObject.getString("name");                 // 真实姓名（实名认证用）
            if (id.length() != 18) {
                jsonResponse(exchange, 400, "身份证号码格式不正确。", null);
                return;
            }
            if (name.length() > 10 || name.length() < 2) {
                jsonResponse(exchange, 400, "姓名长度不正确。", null);
                return;
            }

            if (WhiteListDAO.isIdInWhiteList(id.hashCode())) {
                String uuidInWhiteList = WhiteListDAO.selectByID(id.hashCode());
                User user = UserDAO.selectByUuid(uuidInWhiteList);
                if (user == null) {
                    jsonResponse(exchange, 400, "严重的内部错误，请联系服主处理。(库内身份验证与玩家信息不匹配)", null);
                    return;
                }
                jsonResponse(exchange, 400, "此身份信息已被 "+user.email+" 使用,请勿重复添加白名单，本服一人一号。如果有人冒用了你的身份，请联系服主处理。", null);
                return;
            }

            // TODO: 调实名认证接口 不通过直接返回错误
            //
            //

            // 将身份证号码的哈希值存库（不直接保存身份证号码，防止数据库泄漏）
            WhiteListDAO.insert(webToken.userUUID, id.hashCode());
            jsonResponse(exchange, 200, "添加白名单成功！", null);
        } catch (Exception e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
