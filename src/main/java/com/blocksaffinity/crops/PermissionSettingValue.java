package com.blocksaffinity.crops;


import org.bukkit.entity.Player;

// We'll call the weird ALWAYS/permission/NEVER thing in config a PermissionSettingValue
// Pretty buns name
public class PermissionSettingValue {
    public static Boolean parseStringAs(Player player, String string) {
        if (string.equalsIgnoreCase("ALWAYS"))
        {
            return true;
        }
        else if (string.equalsIgnoreCase("NEVER"))
        {
            return false;
        }
        else return player.hasPermission(string);
    }
}
