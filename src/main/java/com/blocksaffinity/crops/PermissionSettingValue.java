package com.blocksaffinity.crops;


import org.bukkit.entity.Player;

// We'll call the weird ALWAYS/permission/NEVER thing in config a PermissionSettingValue
// Pretty buns name
public class PermissionSettingValue {
    public static Boolean parseStringAs(Player player, String string, String permission) {
        if (string.equalsIgnoreCase("ALWAYS"))
        {
            return true;
        }
        else if (string.equalsIgnoreCase("NEVER"))
        {
            return false;
        }
        else if (string.equalsIgnoreCase("PERMISSION"))
        {
            return player.hasPermission(permission);
        }
        return false;
    }
}
