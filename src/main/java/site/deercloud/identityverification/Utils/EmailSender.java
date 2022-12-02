package site.deercloud.identityverification.Utils;

import site.deercloud.identityverification.IdentityVerification;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class EmailSender {

    static String host = IdentityVerification.getInstance().getConfigManager().getEmailHost(); // smtp服务器
    static String port = IdentityVerification.getInstance().getConfigManager().getEmailPort(); // 端口
    static String from = IdentityVerification.getInstance().getConfigManager().getEmailFrom(); // 发件人的email
    static String account = IdentityVerification.getInstance().getConfigManager().getEmailUsername(); // 账号
    static String password = IdentityVerification.getInstance().getConfigManager().getEmailPassword(); // 密码

    public static boolean sendTestEmail(String to) {
        String subject = "测试邮件";
        String content = "这是一封测试邮件";
        return sendEmail(to, subject, content);
    }

    public static boolean sendResetPasswordEmail(String to, String password) {
        String subject = "[IV] 重置密码";
        String content = "您的新密码为：" + password + "，请尽快登录并修改密码";
        return sendEmail(to, subject, content);
    }

    public static boolean sendCodeEmail(String to, String code) {
        String subject = "[IV] 验证码";
        String content = "您的验证码为：" + code + "，请在5分钟内输入";
        return sendEmail(to, subject, content);
    }

    private static boolean sendEmail(String to, String subject, String content) {
        try {
            // 1. 创建参数配置, 用于连接邮件服务器的参数配置
            Properties props = new Properties(); // 参数配置
            props.setProperty("mail.transport.protocol", "smtp"); // 使用的协议（JavaMail规范要求）
            props.setProperty("mail.smtp.host", host); // 发件人的邮箱的 SMTP 服务器地址
            props.setProperty("mail.smtp.auth", "true"); // 需要请求认证
            props.setProperty("mail.smtp.port", port);
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.socketFactory.port", port);

            // 2. 根据配置创建会话对象, 用于和邮件服务器交互
            Session session = Session.getInstance(props);
            session.setDebug(IdentityVerification.getInstance().getConfigManager().getDebug()); // 设置为debug模式, 可以查看详细的发送 log

            // 3. 创建一封邮件
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "IdentityVerification", "UTF-8")); // 发件人
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to, "no-reply", "UTF-8")); // 收件人
            message.setSubject(subject, "UTF-8"); // 邮件主题
            message.setContent(content, "text/html;charset=UTF-8"); // 邮件正文（可以使用html标签）
            message.setSentDate(new Date()); // 设置发件时间
            message.saveChanges(); // 保存设置

            // 4. 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport();

            // 5. 使用 邮箱账号 和 密码 连接邮件服务器
            //    这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
            transport.connect(account, password);

            // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(message, message.getAllRecipients());

            // 7. 关闭连接
            transport.close();
            return true;
        } catch (Exception e) {
            MyLogger.debug(e);
            return false;
        }

    }
}
