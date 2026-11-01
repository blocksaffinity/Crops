package com.blocksaffinity.crops;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

public class CropsCommand implements BasicCommand {

    private Crops plugin;

    public CropsCommand(Crops cropsPlugin)
    {
        plugin = cropsPlugin;
    }

    @Override
    public void execute(@NonNull CommandSourceStack source, final String @NonNull [] args)
    {
        CommandSender player = source.getSender();

        if (args.length == 0 || args[0].equals("help") || args[0].equals("usage"))
        {
            player.sendMessage(ColorParser.parse(
                    """
                            
                            &a&l&nCrops&f by &6blocksaffinity
                            
                            &8- &a/crops &7[&2help&7|&2usage&7] &f=> Displays this help menu
                            &8- &a/crops &2reload &f => Reloads plugin configuration
                            &r"""));
        }

        else if (args.length == 1 && args[0].equals("reload"))
        {
            if (player.hasPermission(plugin.getConfig().getString("config.reload_permission", "crops.reload"))) {
                plugin.reload();
                final String message = plugin.prefix + plugin.getConfig().getString("messages.reload_success", "&aSuccessfully reloaded configuration!");
                player.sendMessage(ColorParser.parse(message));
            }
            else
            {
                final String message = plugin.prefix + plugin.getConfig().getString("messages.command_no_permission", "&cYou do not have permission to do this.");
                player.sendMessage(ColorParser.parse(message));
            }
        }

        else
        {
            final String message = plugin.prefix + plugin.getConfig().getString("messages.command_invalid", "&cThis command is not valid.");
            player.sendMessage(ColorParser.parse(message));
        }
    }
}
