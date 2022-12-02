package site.deercloud.identityverification.HttpServer.Api.Register;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.Controller.EmailCodeCache;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.HttpServer.model.Texture;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.SQLite.InviteCodeDAO;
import site.deercloud.identityverification.SQLite.InviteRelationDAO;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.sql.Connection;
import java.util.UUID;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.jsonResponse;
import static site.deercloud.identityverification.SQLite.SqlManager.getConnection;
import static site.deercloud.identityverification.Utils.Utils.*;

public class Registration implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            // 设置响应头
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("POST")){
                jsonResponse(exchange, 405, "Method Not Allowed", null);
                return;
            }

            JSONObject jsonObject = JSONObject.parseObject(exchange.getRequestBody().toString());

            String email        = jsonObject.getString("email");
            String password     = jsonObject.getString("password");
            String active_code  = jsonObject.getString("email_code");   // 邮箱验证码

            String inviteCode   = jsonObject.getString("invite_code");

            String profile_name = jsonObject.getString("profile_name");

            if (EmailCodeCache.isEmailCodeExpired(email)) {
                jsonResponse(exchange, 500, "验证码无效，请重新获取。", null);
                return;
            }
            if (!EmailCodeCache.isEmailCodeValid(email, active_code)) {
                jsonResponse(exchange, 500, "验证码错误！", null);
                return;
            }
            if (getUUIDFromRemote(profile_name) != null){
                jsonResponse(exchange, 400, "此昵称已有正版玩家使用，为避免ID碰撞请改名。", null);
                return;
            }
            Connection connection = getConnection();
            if (!InviteCodeDAO.isValid(connection, inviteCode)) {
                jsonResponse(exchange, 400, "邀请码不存在或已被使用！", null);
                return;
            }
            String new_uuid = UUID.randomUUID().toString();
            String inviteCodeOwner = InviteCodeDAO.getInviterUUID(connection, inviteCode);
            // 创建邀请关系
            InviteRelationDAO.insert(connection, new_uuid, inviteCodeOwner, System.currentTimeMillis());
            // 标记邀请码已使用
            InviteCodeDAO.setUsed(connection, inviteCode, true, System.currentTimeMillis());

            User user = new User();
            user.uuid = new_uuid;
            user.email = email;
            user.password = password;
            UserDAO.insert(connection, user);

            Profile profile = new Profile();
            profile.name = profile_name;
            profile.uuid = UUID.randomUUID().toString();
            profile.belongTo = user.uuid;
            Texture texture = Texture.newDefault(profile.uuid);
            profile.textures = texture.serialWithBase64();
            profile.textures_signature = texture.sign();
            ProfileDAO.insert(connection, profile);

            jsonResponse(exchange, 200, "注册成功！", null);
        } catch (Exception e) {
            exchange.close();
            MyLogger.debug(e);
        }

    }
}
