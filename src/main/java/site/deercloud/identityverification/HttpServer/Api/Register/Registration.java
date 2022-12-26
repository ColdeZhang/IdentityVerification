package site.deercloud.identityverification.HttpServer.Api.Register;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.Controller.EmailCodeCache;
import site.deercloud.identityverification.Controller.WebTokenCache;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.HttpServer.model.Texture;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.HttpServer.model.WebToken;
import site.deercloud.identityverification.SQLite.InviteCodeDAO;
import site.deercloud.identityverification.SQLite.InviteRelationDAO;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.RandomCode;
import site.deercloud.identityverification.Utils.UnsignedUUID;


import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.Utils.Utils.*;

public class Registration implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            if (!requestHeader(exchange, "POST")) return;

            JSONObject jsonObject = getBody(exchange);

            String email        = jsonObject.getString("email");
            String password     = jsonObject.getString("password");
            String active_code  = jsonObject.getString("email_code");   // 邮箱验证码

            // 验证邮箱唯一性
            if (UserDAO.selectByEmail(email) != null) {
                jsonResponse(exchange, 400, "此邮箱已被注册。", null);
                return;
            }
            // 验证邮箱验证码
            if (EmailCodeCache.isEmailCodeExpired(email)) {
                jsonResponse(exchange, 500, "邮箱验证码无效，请重新获取。", null);
                return;
            }
            if (!EmailCodeCache.isEmailCodeValid(email, active_code)) {
                jsonResponse(exchange, 500, "验证码错误！", null);
                return;
            }

            String new_user_uuid = UnsignedUUID.GenerateUUID();

            // 创建用户
            User user = new User();
            user.uuid = new_user_uuid;
            user.email = email;
            user.password = password;
            UserDAO.insert(user);
            MyLogger.debug("用户注册成功：" + user.uuid);

            WebToken webToken = WebToken.newWebToken(user.uuid);
            WebTokenCache.addWebToken(webToken);
            JSONObject response = new JSONObject();
            response.put("token", WebToken.ParseWebTokenToStr(webToken));
            jsonResponse(exchange, 200, "注册成功！", response);
        } catch (Exception e) {
            exchange.close();
            MyLogger.debug(e);
        }

    }
}
