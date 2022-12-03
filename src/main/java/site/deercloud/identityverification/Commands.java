package site.deercloud.identityverification;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import site.deercloud.identityverification.Controller.InviteCodeManager;
import site.deercloud.identityverification.SQLite.SqlManager;

import java.util.Collections;
import java.util.List;

public class Commands implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args[0]) {
            case "reload":
                IdentityVerification.getInstance().getConfigManager().reload(sender);
                break;
            case "genCode":
                // TODO: 需要校验玩家是否符合活跃度要求
                InviteCodeManager.CreateCode(sender);
                break;
            case "myCodes":
                InviteCodeManager.ListCodesOf(sender);
                break;
            case "admin":
                SqlManager.updateAdmin(sender, args[1], args[2]);
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("reload", "genCode", "myCodes", "admin");
        } else if (args.length == 2) {
            if (args[0].equals("admin")) {
                return Collections.singletonList("<用户名> <密码>");
            }
        }
        return null;
    }
}
