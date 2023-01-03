package site.deercloud.identityverification.HttpServer.Api.Profile;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.HttpServer.model.Texture;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.HttpServer.model.WebToken;
import site.deercloud.identityverification.SQLite.InviteCodeDAO;
import site.deercloud.identityverification.SQLite.InviteRelationDAO;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.UnsignedUUID;

import java.io.IOException;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.Utils.Utils.getUUIDFromRemote;

// 创建外置登录角色
public class CreateProfile implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (!requestHeader(exchange, "POST")) return;
            WebToken webToken = authorizationCheck(exchange, User.ROLE.USER);
            if (webToken == null) return;
            JSONObject jsonObject = getBody(exchange);

            String profile_name = jsonObject.getString("profile_name");
            String inviteCode   = jsonObject.getString("invite_code");

            // 验证名称唯一性
            if (ProfileDAO.selectByName(profile_name) != null) {
                jsonResponse(exchange, 500, "昵称已存在于本服务器。", null);
                return;
            }
            if (getUUIDFromRemote(profile_name) != null) {
                jsonResponse(exchange, 400, "昵称已被一个正版玩家使用，建议不要使用此昵称。", null);
                return;
            }
            // 验证昵称可用性
            if (getUUIDFromRemote(profile_name) != null){
                jsonResponse(exchange, 400, "此昵称已有正版玩家使用，为避免ID碰撞请改名。", null);
                return;
            }
            // 验证邀请码可用性
            if (InviteCodeDAO.getUsed(inviteCode)) {
                jsonResponse(exchange, 400, "邀请码不存在或已被使用！", null);
                return;
            }

            // 创建一个角色
            Profile profile = new Profile();
            profile.name = profile_name;
            profile.uuid = UnsignedUUID.GenerateUUID();
            profile.belongTo = webToken.userUUID;
            profile.is_genuine = false;
            Texture texture = Texture.newDefault(profile.name);
            profile.textures = texture.serialWithBase64();
            profile.textures_signature = texture.sign();
            ProfileDAO.insert(profile);
            MyLogger.debug("角色创建成功：" + profile.name + " " + profile.uuid);

            // 创建邀请关系
            String inviteCodeOwner = InviteCodeDAO.getInviterUUID(inviteCode);
            InviteRelationDAO.insert(inviteCode, webToken.userUUID, inviteCodeOwner);
            MyLogger.debug("邀请关系已建立，邀请人：" + inviteCodeOwner + "，被邀请人：" + webToken.userUUID);

            // 标记邀请码已使用
            InviteCodeDAO.setUsed(inviteCode);
            MyLogger.debug("邀请码" + inviteCode + "已标记为已使用，" + "使用人" + webToken.userUUID);

            jsonResponse(exchange, 200, "角色创建成功！", null);
        }catch (Exception e){
            MyLogger.debug(e);
            exchange.close();
        }

    }
}
