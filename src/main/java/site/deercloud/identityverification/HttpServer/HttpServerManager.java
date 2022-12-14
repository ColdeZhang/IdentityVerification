package site.deercloud.identityverification.HttpServer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import site.deercloud.identityverification.Controller.WebTokenCache;
import site.deercloud.identityverification.HttpServer.Api.Register.GetEmailCode;
import site.deercloud.identityverification.HttpServer.Api.GetOnlineProfile;
import site.deercloud.identityverification.HttpServer.Api.Register.Registration;
import site.deercloud.identityverification.HttpServer.Api.Profile.VerifyCode;
import site.deercloud.identityverification.HttpServer.Api.Profile.VerifyName;
import site.deercloud.identityverification.HttpServer.Api.Manage.Ban.NewBan;
import site.deercloud.identityverification.HttpServer.Web.*;
import site.deercloud.identityverification.HttpServer.Api.*;
import site.deercloud.identityverification.HttpServer.Yggdrasil.MetaData;
import site.deercloud.identityverification.HttpServer.Yggdrasil.authserver.*;
import site.deercloud.identityverification.HttpServer.Yggdrasil.profiles.GetProfiles;
import site.deercloud.identityverification.HttpServer.Yggdrasil.sessionserver.GetProfile;
import site.deercloud.identityverification.HttpServer.Yggdrasil.sessionserver.HasJoined;
import site.deercloud.identityverification.HttpServer.Yggdrasil.sessionserver.Join;
import site.deercloud.identityverification.HttpServer.Yggdrasil.sessionserver.SessionTokenCache;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.HttpServer.model.WebToken;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.Controller.ConfigManager;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpServerManager {

    public HttpServerManager(IdentityVerification plugin) {
        configManager = IdentityVerification.configManager;
        // ????????????
        try {

            String web_host = configManager.getWebHost();
            int web_port = configManager.getWebPort();
            String yag_host = configManager.getYagHost();
            int yag_port = configManager.getYagPort();

            webServer = HttpServer.create(new InetSocketAddress(web_host, web_port), 0);
            yagServer = HttpServer.create(new InetSocketAddress(yag_host, yag_port), 0);

            webServer.createContext("/", new WebServer());

            webServer.createContext("/api/webmeta", new WebMeta());

            webServer.createContext("/api/test", new Test());
            // ????????????????????????
            webServer.createContext("/api/getOnlineProfile", new GetOnlineProfile());
            // ??????????????? ?????????????????????????????????
            webServer.createContext("/api/verifyCode", new VerifyCode());
            // ????????????????????????
            webServer.createContext("/api/verifyName", new VerifyName());
            // ????????????
            webServer.createContext("/api/ban", new NewBan());
            // ??????????????????
            webServer.createContext("/api/registration", new Registration());
            // ?????????????????????
            webServer.createContext("/api/getInviter", new GetInviter());
            // ???????????????
            webServer.createContext("/api/signWhiteList", new SignWhiteList());
            // ?????????????????????
            webServer.createContext("/api/getEmailCode", new GetEmailCode());
            // ??????????????????
            webServer.createContext("/api/getPlayerHead", new GetFace());

            // Yggdrasil API ?????????
            yagServer.createContext("/", new MetaData());
            // ?????? - ????????????????????????????????????????????????????????????
            yagServer.createContext("/authserver/authenticate", new Authenticate());
            // ?????? - ?????????????????????????????????????????????
            yagServer.createContext("/authserver/refresh", new Refresh());
            // ?????? - ????????????????????????
            yagServer.createContext("/authserver/validate", new Validate());
            // ?????? - ????????????
            yagServer.createContext("/authserver/invalidate", new Invalidate());

            // ???????????????????????? - ?????????????????????????????????
            yagServer.createContext("/sessionserver/session/minecraft/join", new Join());
            // ???????????????????????? - ????????????????????????????????????
            yagServer.createContext("/sessionserver/session/minecraft/hasJoined", new HasJoined());
            // ?????????????????? - ???????????????????????????
            yagServer.createContext("/sessionserver/session/minecraft/profile/", new GetProfile());
            // ??????????????????????????? - ??????????????? ??????5
            yagServer.createContext("/api/profiles/minecraft", new GetProfiles());
        } catch (IOException e) {
            MyLogger.debug(e);
        }
        this.startHttpServer();
    }

    public void startHttpServer(){
        // ???????????????
        Executor executor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new java.util.concurrent.ArrayBlockingQueue<>(10));
        webServer.setExecutor(executor);
        yagServer.setExecutor(executor);
        webServer.start();
        yagServer.start();
        MyLogger.info("?????????????????????: " + configManager.getWebHost() + ":" + configManager.getWebPort());
        MyLogger.info("?????????????????????: " + configManager.getYagHost() + ":" + configManager.getYagPort());
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
        return JSONObject.parseObject(extractBodyStringFromExchange(exchange).toString());
    }

    public static JSONArray getBodyArray(HttpExchange exchange) throws IOException {
        return JSONObject.parseArray(extractBodyStringFromExchange(exchange).toString());
    }

    private static StringBuilder extractBodyStringFromExchange(HttpExchange exchange) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        StringBuilder requestBodyContent = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            requestBodyContent.append(line);
        }
        MyLogger.debug(requestBodyContent.toString());
        return  requestBodyContent;
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
                final String key = URLDecoder.decode( keyAndVal[0], StandardCharsets.UTF_8);
                final String val = URLDecoder.decode( keyAndVal[1], StandardCharsets.UTF_8);
                MyLogger.debug(key + " : " + val);
                result.put(key,val);
            }
        });
        return result;
    }

    // ?????????????????????????????????
    public static WebToken authorizationCheck(HttpExchange exchange, User.ROLE role) {
        try {
            String token_str = exchange.getRequestHeaders().getFirst("Authorization");
            if (token_str == null) {
                jsonResponse(exchange, 401, "????????????????????????????????????????????????", null);
                return null;
            }
            WebToken webToken = WebToken.ParseWebTokenFromStr(token_str);
            if (webToken == null) {
                jsonResponse(exchange, 401, "?????????????????????????????????????????????????????????????????????", null);
                return null;
            }
            if (!WebTokenCache.isWebTokenValid(webToken)) {
                jsonResponse(exchange, 401, "?????????????????????????????????????????????????????????????????????", null);
                return null;
            }
            User user = UserDAO.selectByUuid(webToken.userUUID);
            if (user == null) {
                jsonResponse(exchange, 403, "???????????????", null);
                return null;
            }
            if (user.role.ordinal() < role.ordinal()) {
                jsonResponse(exchange, 403, "???????????????????????????????????????????????????", null);
                return null;
            }
            return webToken;
        } catch (Exception e) {
            MyLogger.debug(e);
            return null;
        }
    }

    public static SessionTokenCache getSessionCache() {
        return sessionTokenCache;
    }

    private HttpServer webServer;
    private HttpServer yagServer;
    private final ConfigManager configManager;
    private static final SessionTokenCache sessionTokenCache = new SessionTokenCache();
}
