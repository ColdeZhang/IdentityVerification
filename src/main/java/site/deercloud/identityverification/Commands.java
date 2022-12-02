package site.deercloud.identityverification;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import site.deercloud.identityverification.Controller.InviteCodeManager;

import java.util.List;

public class Commands implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args[0]) {
            case "reload":
                IdentityVerification.getInstance().getConfigManager().reload(sender);
            case "genCode":
                // TODO: 需要校验玩家是否符合活跃度要求
                InviteCodeManager.CreateCode(sender);
            case "myCodes":
                InviteCodeManager.ListCodesOf(sender);
            default:
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
