package site.deercloud.identityverification.HttpServer.Api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class Test implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        /*
         * PS: 必须按顺序设置响应: 添加响应头, 发送响应码和内容长度, 写出响应内容, 关闭处理器
         */
        // 响应内容
        byte[] respContents = "Hello World".getBytes("UTF-8");

        // 设置响应头
        httpExchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        // 设置响应code和内容长度
        httpExchange.sendResponseHeaders(200, respContents.length);
        // 设置响应内容
        httpExchange.getResponseBody().write(respContents);

        // 关闭处理器, 同时将关闭请求和响应的输入输出流（如果还没关闭）
        httpExchange.close();
    }
}