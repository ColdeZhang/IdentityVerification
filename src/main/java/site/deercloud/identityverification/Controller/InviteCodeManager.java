package site.deercloud.identityverification.Controller;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.deercloud.identityverification.HttpServer.model.InviteCode;
import site.deercloud.identityverification.HttpServer.model.InviteRelation;
import site.deercloud.identityverification.HttpServer.model.User;
import site.deercloud.identityverification.SQLite.*;
import site.deercloud.identityverification.Utils.MyLogger;
import site.deercloud.identityverification.Utils.RandomCode;
import site.deercloud.identityverification.Utils.UnsignedUUID;

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
        try {
            if (sender instanceof Player) {
                if (sender.isOp()) {
                    String uuid = UnsignedUUID.UnUUIDof((Player) sender);
                    InviteCode code = InviteCode.CreateWith(uuid);
                    InviteCodeDAO.insert(code);
                    MyLogger.info(sender, "邀请码为：" + code.code);
                } else {
                    MyLogger.warn(sender,"你没有的达到申请邀请码的要求！");
                }
            } else {
                User console = UserDAO.selectByRole(User.ROLE.CONSOLE);
                if (console == null) {
                    MyLogger.warn(sender,"控制台用户不存在，请重启插件以生成");
                    return;
                }
                InviteCode code = InviteCode.CreateWith(console.uuid);
                InviteCodeDAO.insert(code);
                MyLogger.info(sender, "邀请码为：" + code.code);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void CreateCode(User user) {

    }

    public static void ListCodesOf(CommandSender sender) {
        try {
            String uuid;
            if (sender instanceof Player) {
                uuid = ((Player) sender).getUniqueId().toString();
            } else {
                uuid = Objects.requireNonNull(UserDAO.selectByRole(User.ROLE.CONSOLE)).uuid;
            }
            Set<InviteCode> codes = InviteCodeDAO.selectByInviter(uuid);

            sender.sendMessage("|邀请码\t|创建时间\t|使用状态\t|使用者\t|使用时间\t|");
            for (InviteCode code : codes) {
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(code.createTime);
                String msg = "|" + code.code +
                        "\t|" + date +
                        "\t|" + (code.isUsed ? "已使用" : "未使用");
                if (code.isUsed) {
                    InviteRelation relation = InviteRelationDAO.selectByCode(code.code);
                    if (relation == null) {
                        continue;
                    }
                    msg += "\t|" + Objects.requireNonNull(ProfileDAO.selectByUuid(relation.invitee)).name +
                            "\t|" + sdf.format(relation.createTime);
                }
                sender.sendMessage(msg);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
