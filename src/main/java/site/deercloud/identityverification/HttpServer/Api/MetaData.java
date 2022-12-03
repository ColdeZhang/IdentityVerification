package site.deercloud.identityverification.HttpServer.Api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.jsonResponse;
import static site.deercloud.identityverification.HttpServer.HttpServerManager.requestHeader;

public class MetaData implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            requestHeader(exchange, "GET");
            String title = "A Minecraft Server";
            String description = "Minecraft is a 3D sandbox game developed by Mojang Studios where players interact with a fully modifiable three-dimensional environment made of blocks and entities. ";
            Boolean use_yag = true;         // 是否使用外置登录
            Boolean use_genuine = true;     // 是否使用正版验证
            Boolean need_invite = true;     // 是否需要邀请码
            Boolean use_whitelist = true;   // 是否使用实名认证白名单

            JSONArray links = new JSONArray();
            JSONObject link = new JSONObject();
            link.put("key", "author");
            link.put("value", "https://blog.deercloud.site");
            links.add(link);
            link.put("key", "github");
            link.put("value", "https://github.com/ColdeZhang");
            links.add(link);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", title);
            jsonObject.put("description", description);
            jsonObject.put("links", links);
            jsonObject.put("use_yag", use_yag);
            jsonObject.put("use_genuine", use_genuine);
            jsonObject.put("need_invite", need_invite);
            jsonObject.put("use_whitelist", use_whitelist);

            jsonResponse(exchange, 200, "OK", jsonObject);
        } catch (IOException e) {
            exchange.close();

        }
    }
}
