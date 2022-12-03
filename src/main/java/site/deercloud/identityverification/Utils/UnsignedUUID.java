package site.deercloud.identityverification.Utils;

import org.bukkit.entity.Player;

public class UnsignedUUID {
    public static String GenerateUUID() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    public static String UnUUIDof(Player player) {
        return player.getUniqueId().toString().replace("-", "");
    }
}
