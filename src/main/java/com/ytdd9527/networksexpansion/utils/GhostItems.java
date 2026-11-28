package com.ytdd9527.networksexpansion.utils;

import com.balugaq.netex.utils.VersionUtil;
import com.google.common.collect.MapMaker;
import io.github.sefiraat.networks.Networks;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public final class GhostItems implements Listener {

    private static final NamespacedKey GHOST_KEY =
        new NamespacedKey(Networks.getInstance(), "ghost_item");
    private static final Set<ItemStack> GHOSTS =
        Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());

    public GhostItems() {
    }

    public static void mark(@NotNull ItemStack item) {
        GHOSTS.add(item);
        item.editMeta(meta -> meta.getPersistentDataContainer()
            .set(GHOST_KEY, PersistentDataType.BYTE, (byte) 1));
    }

    public static boolean is(@Nullable ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        if (GHOSTS.contains(item)) {
            return true;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(GHOST_KEY, PersistentDataType.BYTE);
    }

    private static boolean slotIsGhost(@Nullable ChestMenu menu, int slot) {
        return menu != null && slot >= 0 && slot < menu.getSize() && is(menu.getItemInSlot(slot));
    }

    private static boolean hasGhost(@Nullable ChestMenu menu) {
        if (menu == null) {
            return false;
        }
        for (int slot = 0; slot < menu.getSize(); slot++) {
            if (is(menu.getItemInSlot(slot))) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static ChestMenu menuOf(@NotNull Inventory top) {
        return top.getHolder() instanceof ChestMenu menu ? menu : null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(@NotNull InventoryClickEvent event) {
        ChestMenu menu = menuOf(VersionUtil.getTopInventory(event));
        if (menu == null) {
            cleanLooseGhost(event);
            return;
        }
        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            if (hasGhost(menu)) {
                event.setCancelled(true);
            }
            return;
        }
        if (slotIsGhost(menu, event.getRawSlot())) {
            event.setCancelled(true);
            return;
        }
        if (event.getHotbarButton() >= 0
            && is(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()))) {
            event.setCancelled(true);
            return;
        }
        if (event.getRawSlot() >= VersionUtil.getTopInventory(event).getSize()) {
            cleanLooseGhost(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrag(@NotNull InventoryDragEvent event) {
        if (is(event.getOldCursor())) {
            event.setCancelled(true);
            VersionUtil.setCursor(event, null);
            return;
        }
        ChestMenu menu = menuOf(VersionUtil.getTopInventory(event));
        if (menu == null) {
            return;
        }
        for (int rawSlot : event.getRawSlots()) {
            if (slotIsGhost(menu, rawSlot)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreative(@NotNull InventoryCreativeEvent event) {
        ChestMenu menu = menuOf(VersionUtil.getTopInventory(event));
        if (slotIsGhost(menu, event.getRawSlot())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(@NotNull PlayerDropItemEvent event) {
        if (is(event.getItemDrop().getItemStack())) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(@NotNull BlockPlaceEvent event) {
        if (is(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onUse(@NotNull PlayerInteractEvent event) {
        if (is(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(@NotNull InventoryMoveItemEvent event) {
        if (is(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemSpawn(@NotNull ItemSpawnEvent event) {
        if (is(event.getEntity().getItemStack())) {
            event.getEntity().remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        if (is(event.getPlayer().getInventory().getItem(event.getHand()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOpen(@NotNull InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof ChestMenu)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        for (ItemStack item : player.getInventory().getContents()) {
            if (is(item)) {
                player.getInventory().remove(item);
            }
        }
    }

    private static void cleanLooseGhost(@NotNull InventoryClickEvent event) {
        if (is(event.getCurrentItem())) {
            event.setCurrentItem(null);
            event.setCancelled(true);
            return;
        }
        if (is(event.getCursor())) {
            VersionUtil.setCursor(event, null);
            event.setCancelled(true);
        }
    }
}
