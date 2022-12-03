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
import site.deercloud.identityverification.Utils.UnsignedUUID;


import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.Utils.Utils.*;

public class Registration implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            requestHeader(exchange, "POST");

            JSONObject jsonObject = getBody(exchange);

            String email        = jsonObject.getString("email");
            String password     = jsonObject.getString("password");
            String active_code  = jsonObject.getString("email_code");   // 邮箱验证码

            String inviteCode   = jsonObject.getString("invite_code");

            String profile_name = jsonObject.getString("profile_name");

            // 验证名称唯一性
            if (ProfileDAO.selectByName(profile_name) != null) {
                jsonResponse(exchange, 500, "昵称已存在于本服务器。", null);
                return;
            }
            if (getUUIDFromRemote(profile_name) != null) {
                jsonResponse(exchange, 400, "昵称已被一个正版玩家使用，建议不要使用此昵称。", null);
                return;
            }
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
            // 验证昵称可用性
            if (getUUIDFromRemote(profile_name) != null){
                jsonResponse(exchange, 400, "此昵称已有正版玩家使用，为避免ID碰撞请改名。", null);
                return;
            }
            // 验证邀请码可用性
            if (InviteCodeDAO.isUsed(inviteCode)) {
                jsonResponse(exchange, 400, "邀请码不存在或已被使用！", null);
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

            // 创建一个默认角色
            Profile profile = new Profile();
            profile.name = profile_name;
            profile.uuid = UnsignedUUID.GenerateUUID();
            profile.belongTo = user.uuid;
            Texture texture = Texture.newDefault(profile.name);
            profile.textures = texture.serialWithBase64();
            profile.textures_signature = texture.sign();
            ProfileDAO.insert(profile);
            MyLogger.debug("角色创建成功：" + profile.uuid);

            // 创建邀请关系
            String inviteCodeOwner = InviteCodeDAO.getInviterUUID(inviteCode);
            InviteRelationDAO.insert(profile.uuid, inviteCodeOwner, System.currentTimeMillis());
            MyLogger.debug("邀请关系已建立，邀请人：" + inviteCodeOwner + "，被邀请人：" + profile.uuid);
            // 标记邀请码已使用
            InviteCodeDAO.useBy(inviteCode, profile.uuid);
            MyLogger.debug("邀请码已标记为已使用：" + inviteCode);

            jsonResponse(exchange, 200, "注册成功！", null);
        } catch (Exception e) {
            exchange.close();
            MyLogger.debug(e);
        }

    }
}
