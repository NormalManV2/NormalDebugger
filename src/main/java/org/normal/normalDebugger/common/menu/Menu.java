package org.normal.normalDebugger.common.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Menu {

    private static final Map<UUID, Menu> openMenus = new HashMap<>();
    protected final JavaPlugin plugin;
    protected final Player viewer;
    protected final Inventory inventory;

    public Menu(JavaPlugin plugin, Player viewer, int size, String title) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.inventory = Bukkit.createInventory(viewer, size, title);
        openMenus.put(viewer.getUniqueId(), this);
    }

    public void open() {
        build();
        viewer.openInventory(inventory);
    }

    public abstract void build();

    public abstract void handleClick(InventoryClickEvent event);

    public void handleClose(InventoryCloseEvent event) {
        openMenus.remove(viewer.getUniqueId());
    }

    public static void handleGlobalClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu menu = openMenus.get(player.getUniqueId());
        if (menu != null) {
            event.setCancelled(true);
            menu.handleClick(event);
        }
    }

    public static void handleGlobalClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Menu menu = openMenus.get(player.getUniqueId());
        if (menu != null) {
            menu.handleClose(event);
        }
    }

    protected void set(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public static Menu getOpenMenu(Player player) {
        return openMenus.get(player.getUniqueId());
    }

    public Inventory getInventory() {
        return inventory;
    }
}
