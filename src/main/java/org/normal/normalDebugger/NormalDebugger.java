package org.normal.normalDebugger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Event;
import org.normal.normalDebugger.event.EventTracer;
import org.normal.normalDebugger.event.config.TracedEventConfig;
import org.normal.normalDebugger.event.listener.GlobalEventListener;
import org.normal.normalDebugger.event.scanner.BukkitEventScanner;
import org.normal.normalDebugger.menu.EventListMenu;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import static org.bukkit.Bukkit.getPluginManager;

public final class NormalDebugger extends JavaPlugin {

    private final EventTracer tracer = new EventTracer();
    private final TracedEventConfig tracedConfig = new TracedEventConfig();

    public void onEnable() {
        this.initTracedConfig();
        CauseResolverRegistry.registerDefaults();

        this.registerCommands();
        this.initListeners();
    }

    private void initTracedConfig() {
        Path configPath = getDataFolder().toPath().resolve("traced_events.config");
        try {
            this.tracedConfig.loadFromFile(configPath);
        } catch (IOException e) {
            getLogger().warning("Failed to load traced events config.");
        }
    }

    private void registerCommands() {
        getCommand("debuggui").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            new EventListMenu(this, player, this.tracer).open();
            return true;
        });


        getCommand("toggleevent").setExecutor((sender, command, label, args) -> {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            if (args.length != 1) {
                player.sendMessage("Usage: /toggleevent <EventSimpleName>");
                return true;
            }

            String name = args[0];

            this.tracedConfig.toggle(name);
            player.sendMessage("Toggled " + name + ": " + this.tracedConfig.isEnabled(name));
            return true;
        });
    }

    private void initListeners() {
        GlobalEventListener listener = new GlobalEventListener(event -> {
            if (this.tracedConfig.isEnabled(event)) {
                this.tracer.trace(event);
                getLogger().info("Traced: " + event.getEventName());
            }
        });

        Set<Class<? extends Event>> events = BukkitEventScanner.getAllEvents(this);
        PluginManager pm = getServer().getPluginManager();

        for (Class<? extends Event> eventClass : events) {
            try {
                pm.registerEvent(
                        eventClass,
                        listener,
                        EventPriority.MONITOR,
                        (l, e) -> {
                            if (eventClass.isInstance(e)) {
                                ((GlobalEventListener) l).handle(e);
                            }
                        },
                        this,
                        true
                );
            } catch (Throwable thrown) {
                getLogger().warning("Failed to register: " + eventClass.getSimpleName());
            }
        }

        this.registerListeners(listener);
    }

    private void registerListeners(Listener listener) {
        getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        try {
            this.tracedConfig.saveToFile(getDataFolder().toPath().resolve("traced_events.txt"));
        } catch (IOException e) {
            getLogger().warning("Failed to save traced events config.");
        }
    }
}
