package site.deercloud.identityverification.HttpServer.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;

public class Response {
    public static void err_invalid_token(HttpExchange exchange, String msg, String cause) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", "ForbiddenOperationException");
        jsonObject.put("errorMessage", "Invalid token. " + msg);
        jsonObject.put("cause", cause);
        exchange.sendResponseHeaders(403, jsonObject.toString().getBytes().length);
        exchange.getResponseBody().write(jsonObject.toString().getBytes());
        exchange.getResponseBody().close();
    }

    public static void err_password_wrong(HttpExchange exchange, String cause) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", "ForbiddenOperationException");
        jsonObject.put("errorMessage", "密码错误，或短时间内多次登录失败而被暂时禁止登录");
        jsonObject.put("cause", cause);
        exchange.sendResponseHeaders(403, jsonObject.toString().getBytes().length);
        exchange.getResponseBody().write(jsonObject.toString().getBytes());
        exchange.getResponseBody().close();
    }

    public static void success_200(HttpExchange exchange, JSONObject data) throws IOException {
        exchange.sendResponseHeaders(200, data.toString().getBytes().length);
        exchange.getResponseBody().write(data.toString().getBytes());
        exchange.getResponseBody().close();
    }

    public static void success_200(HttpExchange exchange, JSONArray data) throws IOException {
        exchange.sendResponseHeaders(200, data.toString().getBytes().length);
        exchange.getResponseBody().write(data.toString().getBytes());
        exchange.getResponseBody().close();
    }

    public static void err_method_not_allowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, "Method Not Allowed".getBytes().length);
        exchange.getResponseBody().write("Method Not Allowed".getBytes());
        exchange.getResponseBody().close();
    }

    public static void err_token_has_bind(HttpExchange exchange) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", "IllegalArgumentException");
        jsonObject.put("errorMessage", "IAccess token already has a profile assigned.");
        jsonObject.put("cause", "试图向一个已经绑定了角色的令牌指定其要绑定的角色");
        exchange.sendResponseHeaders(400, jsonObject.toString().getBytes().length);
        exchange.getResponseBody().write(jsonObject.toString().getBytes());
        exchange.getResponseBody().close();
    }

    public static void success_no_content(HttpExchange exchange) throws IOException {
        MyLogger.debug("返回204");
        exchange.sendResponseHeaders(204, -1);
        exchange.getResponseBody().close();
    }

    public static void err_invalid_profile(HttpExchange exchange) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", "ForbiddenOperationException");
        jsonObject.put("errorMessage", "Invalid token.");
        jsonObject.put("cause", "试图使用一个不存在的角色UUID来登录");
        exchange.sendResponseHeaders(403, jsonObject.toString().getBytes().length);
        exchange.getResponseBody().write(jsonObject.toString().getBytes());
        exchange.getResponseBody().close();
    }


}
