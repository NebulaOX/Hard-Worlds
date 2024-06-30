package org.plugin.hardworlds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Random;

public class MobSpawningListener implements Listener {
    private Plugin plugin = null;
    private Random random = null;

    public MobSpawningListener(Plugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
        schedulePeriodicMobSpawning();
    }

    public MobSpawningListener() {

    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!event.isNewChunk()) return;

        World world = event.getWorld();
        if (world.getEnvironment() == World.Environment.NORMAL) {
            for (int i = 0; i < 5; i++) { // Increase initial spawn rate
                spawnCustomMob(world, getRandomLocationInChunk(event.getChunk().getX(), event.getChunk().getZ(), world));
            }
        }
    }

    private Location getRandomLocationInChunk(int chunkX, int chunkZ, World world) {
        int x = chunkX * 16 + random.nextInt(16);
        int z = chunkZ * 16 + random.nextInt(16);
        int y = world.getHighestBlockYAt(x, z);
        return new Location(world, x, y, z);
    }

    private void spawnCustomMob(World world, Location location) {
        if (random.nextDouble() < 0.5) { // 50% chance to spawn a custom mob
            Mob mob = (Mob) world.spawnEntity(location, getRandomMobType());
            customizeMob(mob);
        }
    }

    private EntityType getRandomMobType() {
        EntityType[] types = {EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.CREEPER};
        return types[random.nextInt(types.length)];
    }

    private void customizeMob(Mob mob) {
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue() * 2);
        mob.setHealth(Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue() * 1.5);
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).getBaseValue() * 2);

        // Additional customizations based on mob type
        switch (mob.getType()) {
            case ZOMBIE:
                // Example: Zombies with increased health and armor
                Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(40.0);
                mob.setHealth(40.0);
                break;
            case SKELETON:
                // Example: Skeletons with increased speed and damage
                Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.35);
                Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(6.0);
                break;
            case SPIDER:
                // Example: Spiders with poison effect on hit
                mob.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));
                break;
            case CREEPER:
                // Example: Creepers with increased explosion power
                ((Creeper) mob).setExplosionRadius(5);
                break;
        }
    }

    private void schedulePeriodicMobSpawning() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    if (world.getEnvironment() == World.Environment.NORMAL) {
                        for (Player player : world.getPlayers()) {
                            if (isNightTime(world)) {
                                spawnMobsAroundPlayer(player);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 600L); // Every 30 seconds
    }

    private boolean isNightTime(World world) {
        long time = world.getTime();
        return time >= 13000 && time <= 23000;
    }

    private void spawnMobsAroundPlayer(Player player) {
        Location location = player.getLocation();
        for (int i = 0; i < 5; i++) { // Spawn 5 mobs around the player
            Location spawnLocation = location.clone().add(random.nextInt(20) - 10, 0, random.nextInt(20) - 10);
            spawnCustomMob(player.getWorld(), spawnLocation);
        }
    }
}
