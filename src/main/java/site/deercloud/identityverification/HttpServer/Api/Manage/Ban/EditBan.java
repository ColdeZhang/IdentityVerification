package site.deercloud.identityverification.HttpServer.Api.Manage.Ban;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.BanRecord;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.HttpServer.model.WebToken;
import site.deercloud.identityverification.SQLite.BanListDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.HttpServer.HttpServerManager.jsonResponse;

public class EditBan implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            if (!requestHeader(exchange, "PUT")) return;
            WebToken webToken = authorizationCheck(exchange, User.ROLE.ADMIN);
            if (webToken == null) return;

            JSONObject body = getBody(exchange);
            Integer record_id = body.getInteger("record_id");
            BanRecord record = BanListDAO.selectById(record_id);
            if (record == null) {
                jsonResponse(exchange, 400, "记录不存在。", null);
                return;
            }
            if (record.ban_time < System.currentTimeMillis()) {
                jsonResponse(exchange, 400, "此封禁已结束，原则上不允许再次修改。", null);
                return;
            }
            record.ban_time = body.getLong("ban_time");
            record.ban_reason = body.getString("ban_reason");
            BanListDAO.updateById(record.id, record);
            jsonResponse(exchange, 200, "更新成功。", record.toJSONObject());
        } catch (Exception e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
