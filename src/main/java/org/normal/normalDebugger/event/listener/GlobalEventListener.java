package org.normal.normalDebugger.event.listener;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.function.Consumer;

public class GlobalEventListener implements Listener {

    private final Consumer<Event> handler;

    public GlobalEventListener(Consumer<Event> handler) {
        this.handler = handler;
    }

    public void handle(Event event) {
        this.handler.accept(event);
    }
}
