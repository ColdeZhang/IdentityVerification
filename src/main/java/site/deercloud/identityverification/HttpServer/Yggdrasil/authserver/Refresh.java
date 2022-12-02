package site.deercloud.identityverification.HttpServer.Yggdrasil.authserver;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.HttpServer.model.Response;
import site.deercloud.identityverification.HttpServer.model.Token;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.SQLite.TokenDAO;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class Refresh implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            // 设置响应头
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            if (!exchange.getRequestMethod().equals("POST")) {
                Response.err_method_not_allowed(exchange);
                return;
            }
            Connection connection = SqlManager.getConnection();
            JSONObject body = JSONObject.parseObject(exchange.getRequestBody().toString());
            String accessToken = body.getString("accessToken");
            String clientToken = body.getString("clientToken");
            Boolean requestUser = body.getBoolean("requestUser");

            if (TokenDAO.isAccessTokenExpired(connection, accessToken)) {
                Response.err_invalid_token(exchange, "无效的令牌", "无效的令牌");
                return;
            }
            if (clientToken != null && TokenDAO.isClientTokenExpired(connection, clientToken)) {
                Response.err_invalid_token(exchange, "无效的令牌", "无效的令牌");
                return;
            }
            Token new_token = TokenDAO.selectByAccessToken(connection, accessToken);
            if (new_token == null) {
                Response.err_invalid_token(exchange, "无效的令牌", "无效的令牌");
                return;
            }

            // 删除原先的令牌 并生成新的令牌
            TokenDAO.deleteByAccessToken(connection, accessToken);
            new_token.accessToken = UUID.randomUUID().toString();
            new_token.expiresAt = System.currentTimeMillis() + 432000000;
            // 选择角色的操作 要求原令牌所绑定的角色为空
            if (!body.getJSONObject("selectedProfile").isEmpty()) {
                if (!Objects.equals(new_token.profileUUID, "")) {
                    Response.err_token_has_bind(exchange);
                    return;
                }
                // TODO: 校验角色是否存在
                // 如果为空则绑定角色
                new_token.profileUUID = body.getJSONObject("selectedProfile").getString("id");
            }
            // 新令牌存库
            TokenDAO.insert(connection, new_token);
            JSONObject response = new JSONObject();
            response.put("accessToken", new_token.accessToken);
            response.put("clientToken", new_token.clientToken);
            if (requestUser) {
                User user = UserDAO.selectByUuid(connection, new_token.userUUID);
                response.put("user", user.serialToJSONObject());
            }
            if (!body.getJSONObject("selectedProfile").isEmpty()) {
                response.put("selectedProfile", body.getJSONObject("selectedProfile"));
            }
            Response.success_200(exchange, response);
        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }

    }
}
