
package com.blocksaffinity.crops;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CropListener implements Listener {
    private final Crops plugin;

    // Behaviours from config
    private String allowFullygrownReplantingStr;
    private String allowPartgrownReplantingStr;
    private String allowFullygrownHarvestingStr;
    private String allowPartgrownHarvestingStr;

    // Messages from config
    private String replantMessage;
    private String harvestMessage;
    private String notGrownMessage;

    // Sounds from config
    private String replantSoundValue;
    private String harvestSoundValue;
    private String notGrownSoundValue;
    private Sound replantSound;
    private Sound harvestSound;
    private Sound notGrownSound;

    public CropListener(Crops plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        // behaviour
        allowFullygrownReplantingStr = plugin.config.getString(
                "behaviour.replanting.allow_fullygrown_replanting", "crops.replant");
        allowPartgrownReplantingStr = plugin.config.getString(
                "behaviour.replanting.allow_partgrown_replanting", "crops.replant");
        allowFullygrownHarvestingStr = plugin.config.getString(
                "behaviour.harvesting.allow_fullygrown_harvesting", "ALWAYS");
        allowPartgrownHarvestingStr = plugin.config.getString(
                "behaviour.harvesting.allow_partgrown_harvesting", "ALWAYS");

        // messages
        replantMessage = plugin.config.getString(
                "messages.harvested_replanted", "&2Harvested & replanted crop!");
        harvestMessage = plugin.config.getString(
                "messages.harvested", "&aHarvested crop!");
        notGrownMessage = plugin.config.getString(
                "messages.not_fully_grown", "&cThis crop is not fully grown!");

        // sounds
        replantSoundValue = plugin.config.getString(
                "sounds.harvested_replanted", "OFF");
        harvestSoundValue = plugin.config.getString(
                "sounds.harvested", "OFF");
        notGrownSoundValue = plugin.config.getString(
                "sounds.not_fully_grown", "OFF");

        replantSound = SoundParser.parseSound(replantSoundValue, "entity.villager.no");
        harvestSound = SoundParser.parseSound(harvestSoundValue, "entity.villager.no");
        notGrownSound = SoundParser.parseSound(notGrownSoundValue, "entity.villager.no");
    }

    @EventHandler
    public void onBlockBroken(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Inventory playerInventory = player.getInventory();
        Block block = event.getBlock();
        Material material = block.getType();
        Material seed = getSeedFromCrop(material);

        // Only handle crops that have a seed mapping and can age.
        if (seed == null || !(block.getBlockData() instanceof Ageable crop)) {
            return;
        }

        boolean fullyGrown = crop.getAge() == crop.getMaximumAge();

        boolean allowReplanting;
        boolean allowHarvesting;

        if (fullyGrown) {
            allowReplanting = PermissionSettingValue.parseStringAs(
                    player, allowFullygrownReplantingStr);
            allowHarvesting = PermissionSettingValue.parseStringAs(
                    player, allowFullygrownHarvestingStr);
        } else {
            allowReplanting = PermissionSettingValue.parseStringAs(
                    player, allowPartgrownReplantingStr);
            allowHarvesting = PermissionSettingValue.parseStringAs(
                    player, allowPartgrownHarvestingStr);
        }

        // Replant if permitted and the player has the required item.
        if (allowReplanting && playerInventory.contains(seed)) {
            // Remove one seed/item before the block's drops can be collected.
            playerInventory.removeItem(new ItemStack(seed, 1));

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                block.setType(material);

                Utils.sendMessage(player, replantMessage);

                if (!replantSoundValue.equalsIgnoreCase("OFF")) {
                    player.playSound(player.getLocation(), replantSound, 1.0f, 1.0f);
                }
            }, 1L);

            return;
        }

        // Harvest normally if permitted.
        if (allowHarvesting) {
            Utils.sendMessage(player, harvestMessage);

            if (!harvestSoundValue.equalsIgnoreCase("OFF")) {
                player.playSound(player.getLocation(), harvestSound, 1.0f, 1.0f);
            }

            return;
        }

        // Neither replanting nor harvesting is permitted.
        event.setCancelled(true);

        if (!fullyGrown) {
            Utils.sendMessage(player, notGrownMessage);

            if (!notGrownSoundValue.equalsIgnoreCase("OFF")) {
                player.playSound(player.getLocation(), notGrownSound, 1.0f, 1.0f);
            }
        }
    }

    private Material getSeedFromCrop(Material crop) {
        return switch (crop) {
            case WHEAT -> Material.WHEAT_SEEDS;
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case BEETROOTS -> Material.BEETROOT_SEEDS;
            case MELON_STEM -> Material.MELON_SEEDS;
            case PUMPKIN_STEM -> Material.PUMPKIN_SEEDS;
            default -> null;
        };
    }
}
