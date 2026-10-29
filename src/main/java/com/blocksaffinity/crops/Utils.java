package com.blocksaffinity.crops;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

public class Utils {
    public static void sendMessage(Player player, String content, NamedTextColor color) {
        // Using predefined Mojang colors
        Component message = Component.text(content, color);

        player.sendMessage(message);
    }

    public static void sendMessage(Player player, String content, TextColor color)
    {
        // Using a precise Hex Color
        Component message = Component.text(content, color);

        player.sendMessage(message);
    }

    public static void sendMessage(Player player, String content)
    {
        player.sendMessage(content);
    }


}
