package site.deercloud.identityverification.HttpServer.Web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.io.InputStream;

public class WebTest implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            String uri = exchange.getRequestURI().toString();
            // 按照uri从resource中读取文件返回

            InputStream file =  IdentityVerification.instance.getResource("web" + uri);
            if (file == null){
                MyLogger.debug("File not found: " + uri);
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
                return;
            }
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().write(file.readAllBytes());
            exchange.close();
        } catch (IOException e) {
            MyLogger.debug(e);
        }
    }
}
