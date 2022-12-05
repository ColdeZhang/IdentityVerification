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

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.Utils.Utils.*;

public class SignWhiteList implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            requestHeader(exchange, "POST");
            JSONObject jsonObject = getBody(exchange);

            String id = jsonObject.getString("id");                     // 身份证（实名认证用）
            String name = jsonObject.getString("name");                 // 真实姓名（实名认证用）
            String profile_name = jsonObject.getString("username");     // 昵称
            Boolean is_genuine = jsonObject.getBoolean("is_genuine");   // 是否正版
            if (id.length() != 18) {
                jsonResponse(exchange, 400, "身份证号码格式不正确。", null);
                return;
            }
            if (name.length() > 10 || name.length() < 2) {
                jsonResponse(exchange, 400, "姓名长度不正确。", null);
                return;
            }
            if (profile_name.length() < 2) {
                jsonResponse(exchange, 400, "昵称长度不正确。", null);
                return;
            }
            String uuid;
            if (is_genuine) {
                // 正版玩家 - 从Mojiang服务器获取uuid
                uuid = getUUIDFromRemote(profile_name);
            } else {
                // 离线玩家 - 从本地数据库获取uuid
                Profile profile = ProfileDAO.selectByName(profile_name);
                if (profile == null) {
                    jsonResponse(exchange, 400, "你还没有注册外置登录，查不到你的游戏信息。", null);
                    return;
                }
                uuid = profile.uuid;
            }
            if (uuid == null) {
                jsonResponse(exchange, 400, "没有查到这个昵称的游戏信息，如果你确定昵称是对的，请检查账户类型。", null);
                return;
            }
            if (WhiteListDAO.isIdInWhiteList(id.hashCode())) {
                String exist_name;
                String uuidInWhiteList = WhiteListDAO.selectByID(id.hashCode());
                if (is_genuine) {
                    exist_name = Objects.requireNonNull(getProfileFromRemote(uuidInWhiteList)).getString("profileName");
                } else {
                    exist_name = Objects.requireNonNull(ProfileDAO.selectByUuid(uuidInWhiteList)).name;
                }
                if (exist_name == null) {
                    jsonResponse(exchange, 400, "严重的内部错误，请联系服主处理。(库内身份验证与玩家信息不匹配)", null);
                    return;
                }
                jsonResponse(exchange, 400, "此身份信息已被 "+exist_name+" 使用,请勿重复添加白名单，本服一人一号。如果有人冒用了你的身份，请联系服主处理。", null);
                return;
            }
            // TODO: 调实名认证接口 不通过直接返回错误
            //
            //
            WhiteListDAO.insert(uuid, is_genuine, id.hashCode());
            jsonResponse(exchange, 200, "添加白名单成功！", null);
        } catch (Exception e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
