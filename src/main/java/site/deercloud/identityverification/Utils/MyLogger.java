package site.deercloud.identityverification.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import site.deercloud.identityverification.IdentityVerification;

public class MyLogger {

    public static void info(String message) {
        IdentityVerification.instance.getLogger().info(ChatColor.GREEN + "| " +message);
    }

    public static void warn(String message) {
        IdentityVerification.instance.getLogger().warning(ChatColor.YELLOW + "| " +message);
    }

    public static void error(String message) {
        IdentityVerification.instance.getLogger().severe(ChatColor.RED + "| " +message);
    }

    public static void debug(String message) {
        if (IdentityVerification.configManager.getDebug()) {
            IdentityVerification.instance.getLogger().info(ChatColor.AQUA + "| " +message);
        }
    }

    public static void debug(Exception e) {
        if (IdentityVerification.configManager.getDebug()) {
            IdentityVerification.instance.getLogger().info(ChatColor.AQUA + "| " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void info(CommandSender sender, String message) {
        IdentityVerification.instance.getLogger().info(ChatColor.GREEN + "[ 来自 " + sender.getName() + " 的消息 ] " + message);
        sender.sendMessage(ChatColor.GREEN + "| " +message);
    }

    public static void warn(CommandSender sender, String message) {
        IdentityVerification.instance.getLogger().warning(ChatColor.YELLOW + "[ 来自 " + sender.getName() + " 的消息 ] " + message);
        sender.sendMessage(ChatColor.YELLOW + "| " +message);
    }

    public static void error(CommandSender sender, String message) {
        IdentityVerification.instance.getLogger().severe(ChatColor.RED + "[ 来自 " + sender.getName() + " 的消息 ] " + message);
        sender.sendMessage(ChatColor.RED + "| " +message);
    }

    public static void debug(CommandSender sender, String message) {
        if (IdentityVerification.configManager.getDebug()) {
            IdentityVerification.instance.getLogger().info(ChatColor.AQUA + "[ 来自 " + sender.getName() + " 的消息 ] " + message);
            sender.sendMessage(ChatColor.AQUA + "| " +message);
        }
    }

}
