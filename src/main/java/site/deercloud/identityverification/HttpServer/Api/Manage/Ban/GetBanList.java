package site.deercloud.identityverification.HttpServer.Api.Manage.Ban;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.BanRecord;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.HttpServer.model.WebToken;
import site.deercloud.identityverification.SQLite.BanListDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.util.ArrayList;
import java.util.Map;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.HttpServer.HttpServerManager.jsonResponse;

public class GetBanList implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            if (!requestHeader(exchange, "GET")) return;
            WebToken webToken = authorizationCheck(exchange, User.ROLE.ADMIN);
            if (webToken == null) return;

            Map<String, String> query = getQuery(exchange);
            int page = 1;
            int limit = 10;
            if (query.containsKey("page")) {
                page = Integer.parseInt(query.get("page"));
                if (page < 1) {
                    page = 1;
                }
            }
            if (query.containsKey("limit")) {
                limit = Integer.parseInt(query.get("limit"));
                if (limit < 1) {
                    limit = 10;
                }
            }
            int offset = (page - 1) * limit;
            boolean is_banning = true;
            if (query.containsKey("is_banning")) {
                is_banning = Boolean.parseBoolean(query.get("is_banning"));
            }
            boolean finished = true;
            if (query.containsKey("finished")) {
                finished = Boolean.parseBoolean(query.get("finished"));
            }

            int total_page = BanListDAO.selectTotalPage(limit, is_banning, finished);
            if (page > total_page) {
                page = total_page;
            }

            JSONArray res = new JSONArray();
            ArrayList<BanRecord> records = BanListDAO.selectByPage(offset, limit, is_banning, finished);
            for (BanRecord record : records) {
                res.add(record.toJSONObject());
            }

            JSONObject data = new JSONObject();
            data.put("page", page);
            data.put("limit", limit);
            data.put("total_page", total_page);
            data.put("res", res);

            jsonResponse(exchange, 200, "操作成功", data);
        } catch (Exception e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
