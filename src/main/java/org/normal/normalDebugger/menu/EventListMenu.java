package org.normal.normalDebugger.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.normal.normalDebugger.common.menu.Menu;
import org.normal.normalDebugger.event.EventTracer;
import org.normal.normalDebugger.event.TracedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class EventListMenu extends Menu {

    private final EventTracer tracer;
    protected int page = 0;
    protected final int pageSize = 45;
    private final Map<String, Consumer<Player>> controlActions;

    public EventListMenu(JavaPlugin plugin, Player viewer, EventTracer tracer) {
        super(plugin, viewer, 54, "Event Trace Viewer");
        this.tracer = tracer;
        this.controlActions = new HashMap<>();
    }

    @Override
    public void build() {
        this.inventory.clear();
        List<TracedEvent> snapshot = this.tracer.getSnapshot();

        int start = this.page * this.pageSize;
        int end = Math.min(snapshot.size(), start + this.pageSize);

        for (int i = start; i < end; i++) {
            TracedEvent e = snapshot.get(i);
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(e.eventName());
            meta.setLore(List.of(
                    "§7Source: §f" + e.pluginSource(),
                    "§7Cancelled: §f" + e.cancelled(),
                    "§7Cause: §f" + e.cause()
            ));
            item.setItemMeta(meta);

            this.inventory.setItem(i - start, item);
        }

        addControlButtons();
    }

    private void buildAndOpen(Player player) {
        this.build();
        player.openInventory(this.inventory);
    }

    private void addControlButtons() {
        this.inventory.setItem(45, createControl(Material.ARROW, "§7Previous Page", this::prevPage));
        this.inventory.setItem(49, createControl(Material.COMPARATOR, "§7Toggle Live View", this::toggleLive));
        this.inventory.setItem(53, createControl(Material.ARROW, "§7Next Page", this::nextPage));
    }

    private ItemStack createControl(Material mat, String name, Consumer<Player> action) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        this.controlActions.put(name, action);
        return item;
    }

    protected void prevPage(Player player) {
        if (this.page > 0) this.page--;
        buildAndOpen(player);
    }

    protected void nextPage(Player player) {
        this.page++;
        buildAndOpen(player);
    }

    protected void setPage(int page) {
        this.page = page;
        build();
        this.viewer.openInventory(this.inventory);
    }

    private void toggleLive(Player player) {
        // TODO: Implement live watch toggle
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType().isAir()) return;

        for (Map.Entry<String, Consumer<Player>> entry : this.controlActions.entrySet()) {
            if (Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().equals(entry.getKey())) {
                entry.getValue().accept(player);
                return;
            }
        }

        int clickedSlot = event.getRawSlot();
        int index = this.page * this.pageSize + clickedSlot;
        List<TracedEvent> snapshot = this.tracer.getSnapshot();

        if (index < snapshot.size()) {
            TracedEvent selected = snapshot.get(index);
            new EventDetailMenu(this.plugin, player, selected).open();
        }
    }
}

