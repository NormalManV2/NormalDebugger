package org.normal.normalDebugger.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.normal.normalDebugger.common.menu.Menu;
import org.normal.normalDebugger.event.TracedEvent;

import java.util.Map;

public class EventDetailMenu extends Menu {

    private final TracedEvent tracedEvent;

    public EventDetailMenu(JavaPlugin plugin, Player viewer, TracedEvent tracedEvent) {
        super(plugin, viewer, 54, "Inspect: " + tracedEvent.eventName());
        this.tracedEvent = tracedEvent;
    }

    private ItemStack createInfo(Material mat, String name, String value) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a" + name);
        meta.setLore(java.util.List.of("§7" + value));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void build() {
        inventory.clear();

        inventory.setItem(10, createInfo(Material.PAPER, "Event Name", tracedEvent.eventName()));
        inventory.setItem(11, createInfo(Material.COMMAND_BLOCK, "Plugin Source", tracedEvent.pluginSource()));
        inventory.setItem(13, createInfo(Material.FIRE_CHARGE, "Cause", String.valueOf(tracedEvent.cause())));
        inventory.setItem(15, createInfo(tracedEvent.cancelled() ? Material.BARRIER : Material.LIME_DYE,
                "Cancelled", tracedEvent.cancelled() ? "Yes" : "No"));
        inventory.setItem(16, createInfo(Material.CLOCK, "Timestamp", String.valueOf(tracedEvent.timestamp())));
        int slot = 27;
        for (Map.Entry<String, Object> entry : tracedEvent.metadata().entrySet()) {
            inventory.setItem(slot++, createInfo(Material.BOOK, entry.getKey(), (String) entry.getValue()));
            if (slot >= 54) break;
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
