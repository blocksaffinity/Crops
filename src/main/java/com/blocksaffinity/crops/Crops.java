package com.blocksaffinity.crops;

import org.bukkit.plugin.java.JavaPlugin;

public final class Crops extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(new BlockBrokenListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
