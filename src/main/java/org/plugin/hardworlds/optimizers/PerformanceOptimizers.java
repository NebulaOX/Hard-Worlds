package org.plugin.hardworlds.optimizers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mob;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PerformanceOptimizers {
    private Plugin plugin;
    private Map<UUID, Integer> entityCounts;

    public void PerformanceOptimizer(Plugin plugin) {
        this.plugin = plugin;
        this.entityCounts = new HashMap<>();
        schedulePerformanceTasks();
    }

    private void schedulePerformanceTasks() {
        // Task to remove excess entities
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    removeExcessEntities(world);
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L); // Every minute

        // Task to unload unused chunks
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    unloadUnusedChunks(world);
                }
            }
        }.runTaskTimer(plugin, 0L, 6000L); // Every 5 minutes

        // Task to manage entity counts
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    manageEntityCounts(world);
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L); // Every minute

        // Task to reduce server load during peak times
        new BukkitRunnable() {
            @Override
            public void run() {
                reduceServerLoad();
            }
        }.runTaskTimer(plugin, 0L, 200L); // Every 10 seconds
    }

    private void removeExcessEntities(World world) {
        int mobLimit = 200; // Example limit
        int itemLimit = 100; // Example limit
        int mobCount = 0;
        int itemCount = 0;

        for (Entity entity : world.getEntities()) {
            if (entity instanceof Mob) {
                mobCount++;
                if (mobCount > mobLimit) {
                    entity.remove();
                }
            } else if (entity instanceof Item) {
                itemCount++;
                if (itemCount > itemLimit) {
                    entity.remove();
                }
            }
        }
    }

    private void unloadUnusedChunks(World world) {
        world.getLoadedChunks();
        for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
            if (chunk.getEntities().length == 0 && world.isChunkLoaded(chunk.getX(), chunk.getZ())) {
                world.unloadChunkRequest(chunk.getX(), chunk.getZ());
            }
        }
    }

    private void manageEntityCounts(World world) {
        entityCounts.clear();
        for (Entity entity : world.getEntities()) {
            entityCounts.put(entity.getUniqueId(), entityCounts.getOrDefault(entity.getUniqueId(), 0) + 1);
        }

        // Log the entity counts for monitoring
        for (Map.Entry<UUID, Integer> entry : entityCounts.entrySet()) {
            plugin.getLogger().info("Entity " + entry.getKey() + " has count " + entry.getValue());
        }
    }

    private void reduceServerLoad() {
        // Example: Reduce the activation range of mobs to lower the server load
        int activationRange = 32; // Default is 32 blocks
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Mob) {
                    Mob mob = (Mob) entity;
                    mob.setAware(false); // Temporarily disable AI for optimization
                    Bukkit.getScheduler().runTaskLater(plugin, () -> mob.setAware(true), activationRange);
                }
            }
        }

        // Example: Limit the number of entities processed per tick
        int maxEntitiesPerTick = 100;
        int processedEntities = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (processedEntities >= maxEntitiesPerTick) {
                    break;
                }
                // Perform lightweight operations on entities
                Objects.requireNonNull(entity.getLocation().getWorld()).getName(); // Just a dummy operation for demonstration
                processedEntities++;
            }
        }
    }
}
