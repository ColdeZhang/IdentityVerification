package site.deercloud.identityverification.HttpServer.Api;

import com.alibaba.fastjson.*;
import com.sun.net.httpserver.*;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.util.Map;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.Utils.Utils.*;

public class Ban implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            requestHeader(exchange, "POST");

            JSONObject jsonObject = getBody(exchange);
            String uuid = jsonObject.getString("uuid");
            String reason = jsonObject.getString("reason");
            Integer time = jsonObject.getInteger("time");
            Boolean with_inviter = jsonObject.getBoolean("with_inviter");

        } catch (IOException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
