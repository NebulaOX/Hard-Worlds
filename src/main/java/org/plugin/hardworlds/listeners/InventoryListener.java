package org.plugin.hardworlds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryListener implements Listener {
    private final Plugin plugin;
    private final Map<UUID, Integer> playerItemCount;

    public InventoryListener(Plugin plugin) {
        this.plugin = plugin;
        this.playerItemCount = new HashMap<>();
        schedulePeriodicInventoryCheck();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePlayerItemCount(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerItemCount.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        updatePlayerItemCount((Player) event.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        updatePlayerItemCount((Player) event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        updatePlayerItemCount((Player) event.getWhoClicked());
    }

    private void updatePlayerItemCount(Player player) {
        int itemCount = countTotalItems(player);
        playerItemCount.put(player.getUniqueId(), itemCount);
        applySlownessEffect(player, itemCount);
    }

    private int countTotalItems(Player player) {
        int totalItems = 0;

        // Count items in the main inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                totalItems += item.getAmount();
            }
        }

        // Count items in the armor slots
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getType() != Material.AIR) {
                totalItems += item.getAmount();
            }
        }

        return totalItems;
    }

    private void applySlownessEffect(Player player, int itemCount) {
        if (itemCount > 2000) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 1), true);
            player.sendMessage("You are carrying too many items and feel slowed down!");
        } else {
            player.removePotionEffect(PotionEffectType.SLOWNESS);
        }
    }

    private void schedulePeriodicInventoryCheck() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updatePlayerItemCount(player);
            }
        }, 0L, 600L); // Check every 30 seconds
    }
}
