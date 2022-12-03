package site.deercloud.identityverification.HttpServer.Api.Register;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import site.deercloud.identityverification.Controller.EmailCodeCache;
import site.deercloud.identityverification.IdentityVerification;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.EmailSender;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.RandomCode;

import java.io.IOException;
import java.sql.SQLException;

import static site.deercloud.identityverification.HttpServer.HttpServerManager.*;

public class GetEmailCode implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            requestHeader(exchange, "POST");
            JSONObject request = getBody(exchange);
            String email = request.getString("email");
            if (email == null || email.equals("")){
                jsonResponse(exchange, 400, "邮箱不能为空", null);
                return;
            }

            // 验证邮箱唯一性
            if (UserDAO.selectByEmail(email) != null) {
                jsonResponse(exchange, 400, "此邮箱已被注册。", null);
                return;
            }
            if (!EmailCodeCache.isEmailCodeExpired(email) && !IdentityVerification.getInstance().getConfigManager().getDebug()) {
                jsonResponse(exchange, 500, "禁止频繁操作！", null);
                return;
            }

            String code = RandomCode.NewCodeOnlyNumber(6);
            if(!EmailSender.sendCodeEmail(email, code)) {
                jsonResponse(exchange, 500, "邮件发送失败！", null);
                return;
            }
            EmailCodeCache.addEmailCode(email, code);

            jsonResponse(exchange, 200, "发送成功，请在五分钟内完成注册。", null);
        } catch (IOException | SQLException e) {
            exchange.close();
            MyLogger.debug(e);
        }
    }
}
