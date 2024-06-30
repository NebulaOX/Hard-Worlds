package org.plugin.hardworlds.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.plugin.hardworlds.listeners.EventScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomEventsCommand implements CommandExecutor {
    private final Plugin plugin;
    private final EventScheduler eventScheduler;
    private final Map<UUID, BukkitRunnable> activeEvents;

    public CustomEventsCommand(Plugin plugin, EventScheduler eventScheduler) {
        this.plugin = plugin;
        this.eventScheduler = eventScheduler;
        this.activeEvents = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /customevents <start|stop|list> [event]");
            return false;
        }

        String subcommand = args[0].toLowerCase();
        switch (subcommand) {
            case "start":
                return handleStartCommand(sender, args);
            case "stop":
                return handleStopCommand(sender, args);
            case "list":
                return handleListCommand(sender);
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Usage: /customevents <start|stop|list> [event]");
                return false;
        }
    }

    private boolean handleStartCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /customevents start <event>");
            return false;
        }

        String eventName = args[1].toLowerCase();
        if (!eventScheduler.isEventRegistered(eventName)) {
            sender.sendMessage(ChatColor.RED + "Event not found: " + eventName);
            return false;
        }

        if (sender instanceof Player && !sender.hasPermission("customevents.start")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to start events.");
            return false;
        }

        Player player = sender instanceof Player ? (Player) sender : null;
        startEvent(eventName, player);
        sender.sendMessage(ChatColor.GREEN + "Event " + eventName + " started successfully.");

        return true;
    }

    private boolean handleStopCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /customevents stop <event>");
            return false;
        }

        String eventName = args[1].toLowerCase();
        if (!activeEvents.containsKey(eventName)) {
            sender.sendMessage(ChatColor.RED + "Event not currently active: " + eventName);
            return false;
        }

        if (sender instanceof Player && !sender.hasPermission("customevents.stop")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to stop events.");
            return false;
        }

        stopEvent(eventName);
        sender.sendMessage(ChatColor.GREEN + "Event " + eventName + " stopped successfully.");

        return true;
    }

    private boolean handleListCommand(CommandSender sender) {
        if (sender instanceof Player && !sender.hasPermission("customevents.list")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to list events.");
            return false;
        }

        sender.sendMessage(ChatColor.GOLD + "Registered Events:");
        for (String eventName : eventScheduler.getRegisteredEvents()) {
            sender.sendMessage(ChatColor.YELLOW + " - " + eventName);
        }

        sender.sendMessage(ChatColor.GOLD + "Active Events:");
        for (UUID uuid : activeEvents.keySet()) {
            sender.sendMessage(ChatColor.YELLOW + " - " + uuid);
        }

        return true;
    }

    private void startEvent(String eventName, Player player) {
        BukkitRunnable eventTask = eventScheduler.createEventTask(eventName, player);
        eventTask.runTask(plugin); // Run immediately
        activeEvents.put(player != null ? player.getUniqueId() : UUID.randomUUID(), eventTask);
    }

    private void stopEvent(String eventName) {
        UUID eventId = activeEvents.entrySet().stream()
                .filter(entry -> entry.getValue().getTaskId() == eventName.hashCode())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (eventId != null) {
            activeEvents.get(eventId).cancel();
            activeEvents.remove(eventId);
        }
    }
}
