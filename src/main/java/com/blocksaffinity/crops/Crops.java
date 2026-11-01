package com.blocksaffinity.crops;

import io.papermc.paper.command.brigadier.BasicCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Crops extends JavaPlugin {
    public Logger logger;
    public FileConfiguration config;
    public String prefix;
    public CropListener cropListener;
    public BasicCommand cropsCommand;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveResource("config.yml", false);

        logger = this.getLogger();
        config = this.getConfig();
        prefix = config.getString("messages.prefix", "&a&lCrops &7» &f");

        // set up and register listeners
        cropListener = new CropListener(this);
        this.getServer().getPluginManager().registerEvents(cropListener, this);

        // set up and register commands
        cropsCommand = new CropsCommand(this);
        this.registerCommand("crops", cropsCommand);

        reload();

        logger.log(Level.INFO, "Enabled plugin.");
    }

    public void reload()
    {
        this.reloadConfig();
        cropListener.reloadConfig();
        config = getConfig();
        prefix = config.getString("messages.prefix", "&a[Crops] &7> &f");

        // display config warnings
        if (config.getString("messages.reload_no_permission") != null)
        {
            logger.log(Level.WARNING, "messages.reload_no_permission is not used, please remove from your config when possible to avoid accidental misconfigurations.");
        }
    }

//    @Override
//    public boolean onCommand(@NonNull CommandSender sender, Command command, @NonNull String label, String @NonNull [] args)
//    {
//        // Reload the config from disk
//        reloadConfig();
//
//        config = getConfig();
//        prefix = config.getString("messages.prefix", "&a&lCrops &7» &f");
//
//        if (!command.getName().equalsIgnoreCase("crops")) {
//            return false;
//        }
//
//        if (!sender.hasPermission(config.getString("config.reload_permission", "crops.reload"))) {
//            Utils.sendMessage((Player)sender, prefix + config.getString("messages.reload_no_permission", "&cYou do not have permission to do this."));
//            return true;
//        }
//
//        // tell listener to reload its cached values
//        cropListener.reloadConfig();
//
//        Utils.sendMessage(sender, prefix + config.getString("messages.reload_success", "Reloaded configuration files."));
//
//        logger.log(Level.INFO, "Reloaded plugin.");
//        return true;
//    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
