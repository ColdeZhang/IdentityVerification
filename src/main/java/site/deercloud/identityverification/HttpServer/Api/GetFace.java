package site.deercloud.identityverification.HttpServer.Api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;
import static site.deercloud.identityverification.Utils.Utils.sendGet;

public class GetFace implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().add("Content-Type", "image/png");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            if (!exchange.getRequestMethod().equals("GET")){
                jsonResponse(exchange, 405, "Method Not Allowed", null);
                return;
            }
            Map<String, String> params = getQuery(exchange);
            String uuid = params.get("uuid");
            String url = "https://crafatar.com/avatars/" + uuid + "?size=128&overlay";
            String image = sendGet(url);

            byte[] respContents = image.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, respContents.length);
            exchange.getResponseBody().write(respContents);
            exchange.close();
        } catch (Exception e) {
            MyLogger.debug(e);
        }
    }
}
