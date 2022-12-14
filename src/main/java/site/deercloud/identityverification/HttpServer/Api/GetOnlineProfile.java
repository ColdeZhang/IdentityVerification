package site.deercloud.identityverification.HttpServer.Api;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.*;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.util.Map;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.Utils.Utils.*;

public class GetOnlineProfile implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            if (!requestHeader(exchange, "GET")) return;

            Map<String, String> params = getQuery(exchange);

            String name = params.get("name");
            String UUID = getUUIDFromRemote(name);
            if (UUID == null){
                jsonResponse(exchange, 400, "ID不存在，请检查输入。", null);
                return;
            }
            JSONObject profile = getProfileFromRemote(UUID);

            jsonResponse(exchange, 200, "获取正版信息成功！", profile);

        } catch (IOException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
