package com.blocksaffinity.crops;

import net.kyori.adventure.text.format.NamedTextColor;
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

import java.util.Arrays;

public class BlockBrokenListener implements Listener {
    private final Crops plugin;

    public BlockBrokenListener(Crops plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBroken(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Inventory playerInventory = player.getInventory();
        Block block = event.getBlock();
        Material material = block.getType();
        Material seed = getSeedFromCrop(material);

        Material[] LISTENED_MATERIALS = {
                Material.WHEAT,
                Material.BEETROOT,
                Material.CARROTS,
                Material.MELON,
                Material.PUMPKIN,
                Material.POTATOES
        };

        if (Arrays.asList(LISTENED_MATERIALS).contains(material))
        {
            Ageable crop = (Ageable) block.getBlockData();

            int currentAge = crop.getAge(); // 0-7
            int maxAge = crop.getMaximumAge();

            if (currentAge == maxAge)
            {
                // fully grown!

                // I hope the second condition isn't ran if the first is false...
                if (seed != null
                        && playerInventory.contains(seed)) {

                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        block.getLocation()
                                .getBlock()
                                .setType(material);

                        ItemStack seedstack = new ItemStack(seed, 1);
                        playerInventory.removeItem(seedstack);

                        Utils.sendMessage(player, "Harvested & replanted crop!", NamedTextColor.DARK_GREEN);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

                    }, 1L);
                }

                else
                {
                    Utils.sendMessage(player, "Harvested crop!", NamedTextColor.GREEN);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                }
            }

            else
            {
                // not fully grown!
                Utils.sendMessage(player, "You can only harvest fully grown crops!", NamedTextColor.RED);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);

                event.setCancelled(true);
            }


            block.setBlockData(crop);
        }
    }

    // jank??
    private Material getSeedFromCrop(Material crop) {
        return switch (crop) {
            case WHEAT -> Material.WHEAT_SEEDS;
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case BEETROOTS -> Material.BEETROOT_SEEDS;
            case MELON_STEM, ATTACHED_MELON_STEM -> Material.MELON_SEEDS;
            case PUMPKIN_STEM, ATTACHED_PUMPKIN_STEM -> Material.PUMPKIN_SEEDS;
            default -> null;
        };
    }
}