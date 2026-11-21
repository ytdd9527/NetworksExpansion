package com.ytdd9527.networksexpansion.core.listeners;

import com.balugaq.netex.utils.Scheduling;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.drive.DriveSlotGuard;
import com.ytdd9527.networksexpansion.api.drive.DriveType;
import com.ytdd9527.networksexpansion.api.cell.StorageCell;
import com.ytdd9527.networksexpansion.api.drive.CellUniqueness;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DriveUniquenessListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        BlockMenu menu = resolveDriveMenu(event);
        if (menu == null) {
            return;
        }
        ItemStack beingPlaced = DriveSlotGuard.resolvePlacedItem(event);
        if (beingPlaced != null && StorageCell.isStorageCell(beingPlaced)) {
            if (!CellUniqueness.isDuplicate(menu, beingPlaced)) {
                CellUniqueness.registerCell(menu.getLocation(), beingPlaced);
                Scheduling.runTask(() -> CellUniqueness.registerDrive(menu));
                return;
            }
            event.setCancelled(true);
            CellUniqueness.notifyDuplicateRejected(player);
            CellUniqueness.scanAndEjectDuplicates(menu, player);
            return;
        }
        if (isRemovalFromCellSlot(event)) {
            Scheduling.runTask(() -> CellUniqueness.registerDrive(menu));
        }
    }

    private static boolean isRemovalFromCellSlot(@NotNull InventoryClickEvent event) {
        if (event.getClick() == ClickType.DOUBLE_CLICK) {
            return isCell(event.getCursor());
        }
        Inventory top = DriveSlotGuard.topInventory(event);
        if (top == null) {
            return false;
        }
        int rawSlot = event.getRawSlot();
        return rawSlot >= 0 && rawSlot < top.getSize()
            && DriveSlotGuard.isCellSlot(rawSlot)
            && isCell(event.getCurrentItem());
    }

    private static boolean isCell(@Nullable ItemStack item) {
        return item != null && !item.getType().isAir() && StorageCell.isStorageCell(item);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Inventory top = DriveSlotGuard.topInventory(event);
        if (top == null) {
            return;
        }
        BlockMenu menu = resolveDriveMenu(top);
        if (menu == null) {
            return;
        }
        ItemStack dragged = event.getOldCursor();
        if (dragged == null || !StorageCell.isStorageCell(dragged)) {
            return;
        }
        int topSize = top.getSize();
        boolean placed = false;
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot >= topSize || !DriveSlotGuard.isCellSlot(rawSlot)) {
                continue;
            }
            placed = true;
            if (CellUniqueness.isDuplicate(menu, dragged)) {
                event.setCancelled(true);
                CellUniqueness.notifyDuplicateRejected(player);
                CellUniqueness.scanAndEjectDuplicates(menu, player);
                return;
            }
        }
        if (placed) {
            CellUniqueness.registerCell(menu.getLocation(), dragged);
        }
    }

    @Nullable
    private static BlockMenu resolveDriveMenu(@NotNull InventoryClickEvent event) {
        Inventory top = DriveSlotGuard.topInventory(event);
        return top == null ? null : resolveDriveMenu(top);
    }

    @Nullable
    private static BlockMenu resolveDriveMenu(@NotNull Inventory topInventory) {
        if (!(topInventory.getHolder() instanceof BlockMenu menu)) {
            return null;
        }
        SlimefunItem sf = StorageCacheUtils.getSfItem(menu.getLocation());
        if (DriveType.of(sf) != DriveType.STANDARD) {
            return null;
        }
        return menu;
    }
}
