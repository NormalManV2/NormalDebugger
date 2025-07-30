package org.normal.normalDebugger.common.resolver;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.normal.normalDebugger.common.event.EventCause;

public class PlayerInteractResolver implements EventCauseResolver<PlayerInteractEvent> {

    @Override
    public boolean matches(Event event) {
        return event instanceof PlayerInteractEvent;
    }

    @Override
    public EventCause resolve(PlayerInteractEvent event) {
        Player actor = event.getPlayer();
        Block target = event.getClickedBlock();
        EquipmentSlot slot = event.getHand();
        ItemStack source = slot == EquipmentSlot.HAND ? actor.getInventory().getItemInMainHand() : actor.getInventory().getItemInOffHand();

        String desc = actor.getName() + " " + event.getAction().name().toLowerCase() +
                (target != null ? " " + target.getType() : "");

        return new EventCause(actor, target, source, "interaction", desc);
    }
}
