package org.plugin.hardworlds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EventScheduler implements Listener {
    private final Plugin plugin;
    private final Random random;
    private final Map<String, Runnable> eventRegistry;

    public EventScheduler(Plugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.eventRegistry = new HashMap<>();
        registerEvents();
        scheduleAutomaticEvents();
    }

    private void registerEvents() {
        eventRegistry.put("monsterSiege", this::monsterSiege);
        eventRegistry.put("lavaPockets", this::lavaPockets);
        eventRegistry.put("cobwebs", this::cobwebs);
        eventRegistry.put("temporaryBuffs", this::temporaryBuffs);
        eventRegistry.put("temporaryDebuffs", this::temporaryDebuffs);
    }

    private void scheduleAutomaticEvents() {
        new BukkitRunnable() {
            @Override
            public void run() {
                String[] events = eventRegistry.keySet().toArray(new String[0]);
                String event = events[random.nextInt(events.length)];
                Bukkit.getLogger().info("Triggering event: " + event);
                eventRegistry.get(event).run();
            }
        }.runTaskTimer(plugin, 0L, 24000L); // Schedule every 20 minutes
    }

    public boolean isEventRegistered(String eventName) {
        return eventRegistry.containsKey(eventName);
    }

    public String[] getRegisteredEvents() {
        return eventRegistry.keySet().toArray(new String[0]);
    }

    public BukkitRunnable createEventTask(String eventName, Player player) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                eventRegistry.get(eventName).run();
                if (player != null) {
                    player.sendMessage("Event " + eventName + " triggered!");
                }
            }
        };
    }

    private void monsterSiege() {
        for (World world : Bukkit.getWorlds()) {
            for (Player player : world.getPlayers()) {
                for (int i = 0; i < 10; i++) { // Spawn 10 mobs around each player
                    world.spawnEntity(player.getLocation().add(random.nextInt(20) - 10, 0, random.nextInt(20) - 10), EntityType.ZOMBIE);
                    world.spawnEntity(player.getLocation().add(random.nextInt(20) - 10, 0, random.nextInt(20) - 10), EntityType.SKELETON);
                }
                player.sendMessage("A monster siege has begun!");
            }
        }
    }

    private void lavaPockets() {
        for (World world : Bukkit.getWorlds()) {
            for (Player player : world.getPlayers()) {
                player.getLocation().getBlock().setType(Material.LAVA);
                player.sendMessage("Lava pockets are appearing around you!");
            }
        }
    }

    private void cobwebs() {
        for (World world : Bukkit.getWorlds()) {
            for (Player player : world.getPlayers()) {
                player.getLocation().getBlock().setType(Material.COBWEB);
                player.sendMessage("Cobwebs are entangling you!");
            }
        }
    }

    private void temporaryBuffs() {
        for (World world : Bukkit.getWorlds()) {
            for (Player player : world.getPlayers()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 6000, 1)); // 5 minutes of increased damage
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 1)); // 5 minutes of speed
                player.sendMessage("You feel a surge of power!");
            }
        }
    }

    private void temporaryDebuffs() {
        for (World world : Bukkit.getWorlds()) {
            for (Player player : world.getPlayers()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 6000, 1)); // 5 minutes of weakness
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 6000, 1)); // 5 minutes of slowness
                player.sendMessage("You feel weak and sluggish!");
            }
        }
    }
}
