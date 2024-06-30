package org.plugin.hardworlds;

import org.bukkit.plugin.java.JavaPlugin;
import org.plugin.hardworlds.listeners.*;
import org.plugin.hardworlds.commands.CustomEventsCommand;

import java.util.Objects;

public class HardWorlds extends JavaPlugin {
    @Override
    public void onEnable() {
        // Register event listeners
        getServer().getPluginManager().registerEvents(new EnvironmentalConditionsListener(this), this);
        getServer().getPluginManager().registerEvents(new MobSpawningListener(this), this);
        getServer().getPluginManager().registerEvents(new CustomMobsListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);

        // Register commands
        Objects.requireNonNull(getCommand("customevents")).setExecutor(new CustomEventsCommand(this, new EventScheduler(this)));

        getServer().getPluginManager().registerEvents(new NightEffectsListener(this), this);

        // Schedule custom events
        new EventScheduler(this);

        saveDefaultConfig();
        getLogger().info("HardWorlds plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("HardWorlds plugin has been disabled.");
    }
}
