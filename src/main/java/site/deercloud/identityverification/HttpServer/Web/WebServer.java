package site.deercloud.identityverification.HttpServer.Web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.io.InputStream;

public class WebServer implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            String uri = exchange.getRequestURI().toString();
            if (uri.equals("/")){
                uri = "/index.html";
            }
            String file_type = uri.substring(uri.lastIndexOf(".") + 1);

            InputStream file =  IdentityVerification.getInstance().getResource("web" + uri);
            if (file == null){
                MyLogger.debug("File not found: " + uri);
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
                return;
            }

            switch (file_type) {
                case "js" -> exchange.getResponseHeaders().add("Content-Type", "application/javascript; charset=UTF-8");
                case "css" -> exchange.getResponseHeaders().add("Content-Type", "text/css; charset=UTF-8");
                case "html" -> exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                case "png" -> exchange.getResponseHeaders().add("Content-Type", "image/png; charset=UTF-8");
                case "jpg" -> exchange.getResponseHeaders().add("Content-Type", "image/jpeg; charset=UTF-8");
                case "gif" -> exchange.getResponseHeaders().add("Content-Type", "image/gif; charset=UTF-8");
                default -> exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            }

            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().write(file.readAllBytes());
            exchange.close();
        } catch (IOException e) {
            MyLogger.debug(e);
        }

    }
}
