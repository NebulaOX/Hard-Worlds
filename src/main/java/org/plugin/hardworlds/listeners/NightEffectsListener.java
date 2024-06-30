package org.plugin.hardworlds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class NightEffectsListener implements Listener {
    private final Plugin plugin;
    private final int slownessLevel;
    private final int blindnessLevel;
    private final long nightStartTime;
    private final long nightEndTime;
    private final int nightCheckInterval;

    public NightEffectsListener(Plugin plugin) {
        this.plugin = plugin;
        boolean enableNightEffects = plugin.getConfig().getBoolean("night_effects.enable", true);
        this.slownessLevel = plugin.getConfig().getInt("night_effects.slowness_level", 2);
        this.blindnessLevel = plugin.getConfig().getInt("night_effects.blindness_level", 1);
        this.nightStartTime = plugin.getConfig().getLong("night_effects.night_start_time", 13000L);
        this.nightEndTime = plugin.getConfig().getLong("night_effects.night_end_time", 23000L);
        this.nightCheckInterval = plugin.getConfig().getInt("night_effects.check_interval", 200);

        if (enableNightEffects) {
            startNightCheckTask();
        }
    }

    private void startNightCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isNight(player.getWorld().getTime())) {
                        applyNightEffects(player);
                    } else {
                        removeNightEffects(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, nightCheckInterval);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        removeNightEffects(player);
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        if (isNight(player.getWorld().getTime())) {
            applyNightEffects(player);
        }
    }

    private boolean isNight(long time) {
        return time >= nightStartTime && time <= nightEndTime;
    }

    private void applyNightEffects(Player player) {
        applyEffectIfNotPresent(player, PotionEffectType.SLOWNESS, slownessLevel - 1);
        applyEffectIfNotPresent(player, PotionEffectType.BLINDNESS, blindnessLevel - 1);
    }

    private void removeNightEffects(Player player) {
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
    }

    private void applyEffectIfNotPresent(Player player, PotionEffectType type, int level) {
        if (player.hasPotionEffect(type)) {
            PotionEffect currentEffect = player.getPotionEffect(type);
            if (currentEffect != null && currentEffect.getAmplifier() < level) {
                player.removePotionEffect(type);
            } else {
                return;
            }
        }
        player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, level));
    }
}
