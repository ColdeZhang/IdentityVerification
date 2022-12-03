package site.deercloud.identityverification.HttpServer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import site.deercloud.identityverification.HttpServer.Api.Register.GetEmailCode;
import site.deercloud.identityverification.HttpServer.Api.Register.GetOnlineProfile;
import site.deercloud.identityverification.HttpServer.Api.Register.Registration;
import site.deercloud.identityverification.HttpServer.Api.Register.VerifyCode;
import site.deercloud.identityverification.HttpServer.Web.*;
import site.deercloud.identityverification.HttpServer.Api.*;
import site.deercloud.identityverification.HttpServer.Yggdrasil.MetaData;
import site.deercloud.identityverification.HttpServer.Yggdrasil.authserver.*;
import site.deercloud.identityverification.HttpServer.Yggdrasil.sessionserver.GetProfile;
import site.deercloud.identityverification.HttpServer.Yggdrasil.sessionserver.HasJoined;
import site.deercloud.identityverification.HttpServer.Yggdrasil.sessionserver.Join;
import site.deercloud.identityverification.HttpServer.Yggdrasil.sessionserver.SessionTokenCache;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.Controller.ConfigManager;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpServerManager {

    public HttpServerManager(IdentityVerification plugin) {
        configManager = plugin.getConfigManager();
        // 注册路由
        try {

            String web_host = configManager.getWebHost();
            int web_port = configManager.getWebPort();
            String yag_host = configManager.getYagHost();
            int yag_port = configManager.getYagPort();

            webServer = HttpServer.create(new InetSocketAddress(web_host, web_port), 0);
            yagServer = HttpServer.create(new InetSocketAddress(yag_host, yag_port), 0);

            webServer.createContext("/", new WebServer());

            webServer.createContext("/api/test", new Test());
            // 获取正版用户信息
            webServer.createContext("/api/getOnlineProfile", new GetOnlineProfile());
            // 验证邀请码 返回邀请者信息用于确认
            webServer.createContext("/api/verifyCode", new VerifyCode());
            // 封禁玩家
            webServer.createContext("/api/ban", new Ban());
            // 离线用户注册
            webServer.createContext("/api/registration", new Registration());
            // 获取邀请者信息
            webServer.createContext("/api/getInviter", new GetInviter());
            // 登记白名单
            webServer.createContext("/api/signWhiteList", new SignWhiteList());
            // 获取邮箱验证码
            webServer.createContext("/api/getEmailCode", new GetEmailCode());

            // Yggdrasil API 元数据
            yagServer.createContext("/", new MetaData());
            // 登录 - 使用密码进行身份验证，并分配一个新的令牌
            yagServer.createContext("/authserver/authenticate", new Authenticate());
            // 刷新 - 吊销原令牌，并颁发一个新的令牌
            yagServer.createContext("/authserver/refresh", new Refresh());
            // 验证 - 验证令牌是否有效
            yagServer.createContext("/authserver/validate", new Validate());
            // 吊销 - 吊销令牌
            yagServer.createContext("/authserver/invalidate", new Invalidate());

            // 客户端进入服务器 - 客户端使用令牌加入游戏
            yagServer.createContext("/sessionserver/session/minecraft/join", new Join());
            // 服务端验证客户端 - 服务端使用令牌验证客户端
            yagServer.createContext("/sessionserver/session/minecraft/hasJoined", new HasJoined());
            // 查询角色属性 - 查询指定的角色属性
            yagServer.createContext("/sessionserver/session/minecraft/profile/", new GetProfile());
            // 按名称批量查询角色 - 不包含属性 最大5
            yagServer.createContext("/api/profiles/minecraft", new GetProfiles());
        } catch (IOException e) {
            MyLogger.debug(e);
        }
        this.startHttpServer();
    }

    public void startHttpServer(){
        // 设置线程池
        Executor executor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new java.util.concurrent.ArrayBlockingQueue<>(10));
        webServer.setExecutor(executor);
        yagServer.setExecutor(executor);
        webServer.start();
        yagServer.start();
        MyLogger.info("网页服务启动在: " + configManager.getWebHost() + ":" + configManager.getWebPort());
        MyLogger.info("外置登录启动在: " + configManager.getYagHost() + ":" + configManager.getYagPort());
    }

    public void stopHttpServer(){
        if (webServer != null){
            webServer.stop(0);
        }
        if (yagServer != null){
            yagServer.stop(0);
        }
    }

    public static void jsonResponse(HttpExchange exchange, int code, String message, JSONObject data) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", message);
        jsonObject.put("data", data);
        exchange.sendResponseHeaders(200, jsonObject.toString().getBytes().length);
        exchange.getResponseBody().write(jsonObject.toString().getBytes());
        exchange.getResponseBody().close();
    }

    public static boolean requestHeader(HttpExchange exchange, String Method) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            if (!exchange.getRequestMethod().equals(Method)){
                jsonResponse(exchange, 405, "Method Not Allowed", null);
                return false;
            }
        } catch (IOException e) {
            MyLogger.debug(e);
        }
        return true;
    }

    public static JSONObject getBody(HttpExchange exchange) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "utf-8"));
        StringBuilder requestBodyContent = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            requestBodyContent.append(line);
        }
        MyLogger.debug(requestBodyContent.toString());
        return JSONObject.parseObject(requestBodyContent.toString());
    }

    public static JSONArray getBodyArray(HttpExchange exchange) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "utf-8"));
        StringBuilder requestBodyContent = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            requestBodyContent.append(line);
        }
        MyLogger.debug(requestBodyContent.toString());
        return JSONObject.parseArray(requestBodyContent.toString());
    }

    public static Map<String,String> getQuery(HttpExchange exchange){
        String query_string = exchange.getRequestURI().getQuery();
        MyLogger.debug("Query String: " + query_string);
        Map<String,String> result = new HashMap<>();
        if(query_string== null || query_string.trim().length() == 0) {
            return result;
        }
        final String[] items = query_string.split("&");
        Arrays.stream(items).forEach(item ->{
            final String[] keyAndVal = item.split("=");
            if( keyAndVal.length == 2) {
                try{
                    final String key = URLDecoder.decode( keyAndVal[0],"utf8");
                    final String val = URLDecoder.decode( keyAndVal[1],"utf8");
                    MyLogger.debug(key + " : " + val);
                    result.put(key,val);
                }catch (UnsupportedEncodingException ignored) {}
            }
        });
        return result;
    }

    public static SessionTokenCache getSessionCache() {
        return sessionTokenCache;
    }

    private HttpServer webServer;
    private HttpServer yagServer;
    private ConfigManager configManager;
    private static SessionTokenCache sessionTokenCache = new SessionTokenCache();
}
