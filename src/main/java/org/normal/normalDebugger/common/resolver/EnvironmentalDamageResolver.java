package org.normal.normalDebugger.common.resolver;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.normal.normalDebugger.common.event.EventCause;

public class EnvironmentalDamageResolver implements EventCauseResolver<EntityDamageEvent> {

    @Override
    public boolean matches(Event event) {
        return event instanceof EntityDamageEvent && !(event instanceof EntityDamageByEntityEvent);
    }

    @Override
    public EventCause resolve(EntityDamageEvent event) {
        Entity target = event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();

        String category = switch (cause) {
            case FIRE, FIRE_TICK, LAVA, DROWNING -> "environment";
            case FALL, SUFFOCATION -> "physics";
            default -> "unknown";
        };

        return new EventCause(null, target, cause, category, "Environmental damage: " + cause.name());
    }
}
