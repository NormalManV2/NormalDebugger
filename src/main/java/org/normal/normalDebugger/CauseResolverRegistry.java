package org.normal.normalDebugger;

import org.bukkit.event.Event;
import org.normal.normalDebugger.common.resolver.EventCauseResolver;
import org.normal.normalDebugger.common.event.EventCause;
import org.normal.normalDebugger.common.resolver.EntityDamageByEntityResolver;
import org.normal.normalDebugger.common.resolver.EnvironmentalDamageResolver;
import org.normal.normalDebugger.common.resolver.PlayerInteractResolver;

import java.util.*;

public final class CauseResolverRegistry {

    private static final List<EventCauseResolver<? extends Event>> RESOLVERS = new ArrayList<>();

    public static <E extends Event> void register(EventCauseResolver<E> resolver) {
        RESOLVERS.add(resolver);
    }

    public static Optional<EventCause> resolve(Event event) {

        return RESOLVERS.stream()
                .sorted(Comparator.comparingInt((EventCauseResolver<?> r) -> r.priority()).reversed())
                .filter(r -> r.matches(event))
                .findFirst()
                .map(r -> {
                    try {
                        EventCauseResolver<Event> typed = (EventCauseResolver<Event>) r;
                        return typed.resolve(event);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        return null;
                    }
                });
    }

    public static List<EventCauseResolver<?>> getResolvers() {
        return Collections.unmodifiableList(RESOLVERS);
    }

    public static void registerDefaults() {
        register(new EntityDamageByEntityResolver());
        register(new EnvironmentalDamageResolver());
        register(new PlayerInteractResolver());
    }
}
