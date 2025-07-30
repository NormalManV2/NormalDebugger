package org.normal.normalDebugger.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.normal.normalDebugger.CauseResolverRegistry;

import java.util.ArrayList;
import java.util.List;

public class EventTracer {

    private final List<TracedEvent> buffer = new ArrayList<>();

    public void trace(Event event) {
        String pluginName;
        try {
            pluginName = JavaPlugin.getProvidingPlugin(event.getClass()).getName();
        } catch (IllegalArgumentException ex) {
            pluginName = "Unknown";
        }

        TracedEvent traced = new TracedEvent(
                event.getEventName(),
                event.getClass().getName(),
                pluginName,
                System.currentTimeMillis(),
                this.isCancelled(event),
                CauseResolverRegistry.resolve(event).orElse(null),
                EventFieldExtractor.extractFields(event)
        );

        synchronized (this.buffer) {
            int maxSize = 500;
            if (this.buffer.size() >= maxSize) this.buffer.removeFirst();
            this.buffer.add(traced);
        }
    }

    public List<TracedEvent> getSnapshot() {
        synchronized (this.buffer) {
            return new ArrayList<>(this.buffer);
        }
    }

    private String findPluginOwner(Class<?> eventClass) {
        Plugin plugin = JavaPlugin.getProvidingPlugin(eventClass);
        return plugin.getName();
    }

    private boolean isCancelled(Event event) {
        return event instanceof Cancellable cancellable && cancellable.isCancelled();
    }
}
