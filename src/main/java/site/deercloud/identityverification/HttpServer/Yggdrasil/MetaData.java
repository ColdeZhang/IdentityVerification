package site.deercloud.identityverification.HttpServer.Yggdrasil;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Response;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.Controller.ConfigManager;

import java.io.IOException;

public class MetaData implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        if (!exchange.getRequestMethod().equals("GET")) {
            Response.err_method_not_allowed(exchange);
            return;
        }
        ConfigManager configManager = IdentityVerification.getInstance().getConfigManager();
        JSONObject meta_json = new JSONObject();
        meta_json.put("serverName", configManager.getServerName());
        meta_json.put("implementationName", configManager.getImplementationName());
        meta_json.put("implementationVersion", configManager.getImplementationVersion());
        JSONObject links_json = new JSONObject();
        links_json.put("homepage", configManager.getHomePageUrl());
        links_json.put("register", configManager.getRegisterUrl());
        meta_json.put("links", links_json);
        JsonArray skinDomains_json = new JsonArray();
        for (String domain : configManager.getSkinDomains()) {
            skinDomains_json.add(domain);
        }
        JSONObject response_json = new JSONObject();
        response_json.put("meta", meta_json);
        response_json.put("skinDomains", skinDomains_json);
        response_json.put("signaturePublickey", configManager.getSignaturePublicKey());
        Response.success_200(exchange, response_json);
    }
}
