package site.deercloud.identityverification.Controller;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.deercloud.identityverification.HttpServer.model.InviteCode;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.SQLite.InviteCodeDAO;
import site.deercloud.identityverification.SQLite.SqlManager;
import site.deercloud.identityverification.SQLite.UserDAO;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.RandomCode;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Set;

public class InviteCodeManager {
    // TODO: 邀请码管理器
    // TODO: 1.生成邀请码
    // TODO: 2.资格验证
    // TODO: 3.把邀请码验证功能从数据移到这里

    public static void CreateCode(CommandSender sender) {
        String code = RandomCode.NewCodeWithAlphabet(8);
        if (sender instanceof Player) {
            if (sender.isOp()) {
                Connection connection = SqlManager.getConnection();
                try {
                    User console = UserDAO.selectByEmail(connection, "console@mc.com");
                    InviteCodeDAO.insert(connection, code, console.uuid, false, 0);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                MyLogger.info(sender, "邀请码为：" + code);
            } else {
                MyLogger.warn(sender,"你没有的达到申请邀请码的要求！");
            }
        } else {
            Connection connection = SqlManager.getConnection();
            try {
                User console = UserDAO.selectByEmail(connection, "console@mc.com");

                InviteCodeDAO.insert(connection, code, console.uuid, false, 0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            MyLogger.info("邀请码为：" + code);
        }
    }

    public static void CreateCode(User user) {

    }

    public static void ListCodesOf(CommandSender sender) {
        try {
            String uuid;
            Connection connection = SqlManager.getConnection();
            if (sender instanceof Player) {
                uuid = ((Player) sender).getUniqueId().toString();
            } else {
                    uuid = Objects.requireNonNull(UserDAO.selectByEmail(connection, "console@mc.com")).uuid;
            }
            Set<InviteCode> codes = InviteCodeDAO.selectByInviter(connection, uuid);

            sender.sendMessage("|     邀请码     |      创建时间     ｜");
            for (InviteCode code : codes) {
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(code.createTime);
                sender.sendMessage("| " + code.code + " | " + date + " |");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
