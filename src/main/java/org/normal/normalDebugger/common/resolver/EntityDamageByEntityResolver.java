package org.normal.normalDebugger.common.resolver;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.normal.normalDebugger.common.event.EventCause;

public class EntityDamageByEntityResolver implements EventCauseResolver<EntityDamageByEntityEvent> {

    @Override
    public boolean matches(Event event) {
        return event instanceof EntityDamageByEntityEvent;
    }

    @Override
    public EventCause resolve(EntityDamageByEntityEvent event) {
        Entity actor = event.getDamager();
        Entity target = event.getEntity();
        Object source = actor;

        if (actor instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) {
            actor = shooter;
        }

        String description = actor.getType() + " damaged " + target.getType();
        return new EventCause(actor, target, source, "combat", description);
    }
}
