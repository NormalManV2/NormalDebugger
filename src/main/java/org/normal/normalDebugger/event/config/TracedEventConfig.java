package org.normal.normalDebugger.event.config;

import org.bukkit.event.Event;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class TracedEventConfig {

    private final Set<String> enabled = ConcurrentHashMap.newKeySet();
    private final Object lock = new Object();

    public void enable(String name) {
        this.enabled.add(name);
    }

    public void disable(String name) {
        this.enabled.remove(name);
    }

    public boolean isEnabled(Event event) {
        return this.enabled.contains(event.getClass().getSimpleName());
    }

    public boolean isEnabled(String name) {
        return this.enabled.contains(name);
    }

    public Set<String> getEnabled() {
        synchronized (this.lock) {
            return new HashSet<>(this.enabled);
        }
    }

    public void toggle(String name) {
        synchronized (this.lock) {
            if (this.enabled.contains(name)) {
                this.enabled.remove(name);
            } else {
                this.enabled.add(name);
            }
        }
    }

    public void loadFromFile(Path path) throws IOException {
        synchronized (this.lock) {
            this.enabled.clear();
            if (!Files.exists(path)) return;
            try (Stream<String> lines = Files.lines(path)) {
                lines.map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .forEach(this.enabled::add);
            }

        }
    }

    public void saveToFile(Path path) throws IOException {
        synchronized (this.lock) {
            Files.createDirectories(path.getParent());
            Files.write(path, this.enabled, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}