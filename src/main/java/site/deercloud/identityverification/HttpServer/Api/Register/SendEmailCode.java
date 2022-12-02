package site.deercloud.identityverification.HttpServer.Api.Register;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.Controller.EmailCodeCache;
import site.deercloud.identityverification.Utils.EmailSender;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.RandomCode;

import java.io.IOException;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.jsonResponse;

public class SendEmailCode implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("POST")){
                jsonResponse(exchange, 405, "Method Not Allowed", null);
                return;
            }
            JSONObject request = JSONObject.parseObject(exchange.getRequestBody().toString());
            String email = request.getString("email");

            if (!EmailCodeCache.isEmailCodeExpired(email)) {
                jsonResponse(exchange, 500, "禁止频繁操作！", null);
                return;
            }

            String code = RandomCode.NewCodeOnlyNumber(6);
            EmailSender.sendCodeEmail(email, code);
            EmailCodeCache.addEmailCode(email, code);

            jsonResponse(exchange, 200, "发送成功，请在五分钟内完成注册。", null);
        } catch (IOException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
