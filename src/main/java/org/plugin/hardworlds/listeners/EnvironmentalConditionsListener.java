package org.plugin.hardworlds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EnvironmentalConditionsListener implements Listener {
    private final Plugin plugin;
    private final boolean enableThunderstorms;
    private final int lightningStrikeInterval;
    private final int intenseHeatDamageInterval;

    public EnvironmentalConditionsListener(Plugin plugin) {
        this.plugin = plugin;
        this.enableThunderstorms = plugin.getConfig().getBoolean("environmental_conditions.enable_thunderstorms");
        this.lightningStrikeInterval = plugin.getConfig().getInt("environmental_conditions.lightning_strike_interval");
        this.intenseHeatDamageInterval = plugin.getConfig().getInt("environmental_conditions.intense_heat_damage_interval");

        // Start the task for managing thunderstorms and lightning strikes
        if (enableThunderstorms) {
            startThunderstormTask();
        }

        // Start the task for managing intense heat damage
        startIntenseHeatTask();
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        World world = event.getWorld();

        // Ensure constant thunderstorms if enabled
        if (enableThunderstorms && event.toWeatherState() && world.hasStorm()) {
            world.setStorm(true);
            world.setThundering(true);
        }
    }

    private void startThunderstormTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    if (world.isThundering()) {
                        world.strikeLightningEffect(world.getSpawnLocation().add(randomOffset(), 0, randomOffset()));
                    }
                }
            }

            private double randomOffset() {
                return (Math.random() * 100) - 50;
            }
        }.runTaskTimer(plugin, 0L, lightningStrikeInterval);
    }

    private void startIntenseHeatTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    if (world.getEnvironment() == World.Environment.NORMAL) {
                        world.getPlayers().forEach(player -> {
                            if (isExposedToSun(player)) {
                                player.damage(1.0);
                            }
                        });
                    }
                }
            }

            private boolean isExposedToSun(Player player) {
                World world = player.getWorld();
                int highestBlockYAtPlayer = world.getHighestBlockYAt(player.getLocation());
                return player.getLocation().getY() >= highestBlockYAtPlayer && world.getTime() > 0 && world.getTime() < 12300;
            }
        }.runTaskTimer(plugin, 0L, intenseHeatDamageInterval);
    }
}
