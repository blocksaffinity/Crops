package com.blocksaffinity.crops;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utils {
    public static void sendMessage(Player player, String content, NamedTextColor color) {
        // Using predefined Mojang colors
        Component message = Component.text(content, color);

        player.sendMessage(message);
    }

    public static void sendMessage(Player player, String content, TextColor color)
    {
        // Using a precise hex Color
        Component message = Component.text(content, color);

        player.sendMessage(message);
    }

    public static void sendMessage(Player player, String content)
    {
        // No
        Component formated_content = ColorParser.parse(content);
        player.sendMessage(formated_content);
    }

    public static void sendMessage(CommandSender player, String content)
    {
        Component formated_content = ColorParser.parse(content);
        player.sendMessage(formated_content);
    }
}
