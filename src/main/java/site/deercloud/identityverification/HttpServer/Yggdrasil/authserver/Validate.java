package site.deercloud.identityverification.HttpServer.Yggdrasil.authserver;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Response;
import site.deercloud.identityverification.HttpServer.model.Token;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.SQLite.TokenDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.getBody;

public class Validate implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            MyLogger.debug("Validate post 接口触发");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("POST")){
                Response.err_method_not_allowed(exchange);
                return;
            }
            JSONObject request = getBody(exchange);
            String accessToken = request.getString("accessToken");
            String clientToken = request.getString("clientToken");
            Token token = TokenDAO.selectByAccessToken(accessToken);
            // 检查令牌是否存在
            if (token == null){
                Response.err_invalid_token(exchange, "无效的令牌", "无效的令牌");
                return;
            }
            // 检查 accessToken 和 clientToken 是否匹配
            if (clientToken != null && !Objects.equals(token.clientToken, clientToken)){
                Response.err_invalid_token(exchange, "无效的令牌", "无效的令牌");
                return;
            }
            // 检查令牌是否过期
            if (TokenDAO.isAccessTokenExpired(accessToken)) {
                TokenDAO.deleteByAccessToken(accessToken);  // 如果过期则删除令牌
                Response.err_invalid_token(exchange, "令牌过期", "令牌过期，请重新登录");
                return;
            }
            Response.success_no_content(exchange);
        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
