package site.deercloud.identityverification.HttpServer.Api.Manage.Ban;

import com.alibaba.fastjson.*;
import com.sun.net.httpserver.*;
import site.deercloud.identityverification.HttpServer.model.BanRecord;
import site.deercloud.identityverification.HttpServer.model.Profile;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.HttpServer.model.WebToken;
import site.deercloud.identityverification.SQLite.BanListDAO;
import site.deercloud.identityverification.SQLite.InviteRelationDAO;
import site.deercloud.identityverification.SQLite.ProfileDAO;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.util.ArrayList;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;

public class NewBan implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            if (!requestHeader(exchange, "POST")) return;
            WebToken webToken = authorizationCheck(exchange, User.ROLE.ADMIN);
            if (webToken == null) return;

            JSONObject jsonObject = getBody(exchange);
            BanRecord record = new BanRecord();
            record.uuid = jsonObject.getString("uuid");         // user uuid
            User invitee = UserDAO.selectByUuid(record.uuid);
            if (invitee == null) {
                jsonResponse(exchange, 400, "用户不存在，可能已经被注销。", null);
                return;
            }
            record.ban_reason   = jsonObject.getString("reason");   // 封禁原因
            record.ban_time     = jsonObject.getLong("time");       // 封禁时间
            BanListDAO.insert(record);

            ArrayList<Profile> invitee_profile = ProfileDAO.selectAllByBelongTo(record.uuid);
            String invitee_name;
            if(invitee_profile.size() == 1) {
                invitee_name = invitee_profile.get(0).name;
            }else {
                invitee_name = "未定档案用户" + invitee.email;
            }
            // 检查是否需要连带处罚
            Boolean with_inviter = jsonObject.getBoolean("with_inviter");
            if (!with_inviter) {
                jsonResponse(exchange, 200, "成功封禁[" + invitee_name + "]。", record.toJSONObject());
                return;
            }
            // 连带处罚
            String inviter_uuid;
            inviter_uuid = InviteRelationDAO.selectInviterByInvitee(record.uuid);
            if (inviter_uuid == null) {
                jsonResponse(exchange, 200, "操作失败，不存在邀请者，可能已经注销。", null);
                return;
            }
            User inviter;
            inviter = UserDAO.selectByUuid(inviter_uuid);
            if (inviter == null) {
                jsonResponse(exchange, 200, "操作失败，不存在邀请者，可能已经注销。", null);
                return;
            }
            BanRecord inviter_record = new BanRecord();
            inviter_record.uuid = inviter_uuid;
            inviter_record.ban_time = record.ban_time;

            ArrayList<Profile> inviter_profile = ProfileDAO.selectAllByBelongTo(inviter_uuid);
            String inviter_name;

            if (inviter_profile.size() == 1) {
                inviter_name = inviter_profile.get(0).name;
            } else {
                inviter_name = "未定档案用户" + inviter.email;
            }
            inviter_record.ban_reason = "由于邀请的玩家["+invitee_name+"]因为["+record.ban_reason+"]被处罚而被连带处罚。";
            BanListDAO.insert(inviter_record);
            jsonResponse(exchange, 200, "操作成功，连带处罚了["+inviter_name+"]。", null);
        } catch (Exception e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
