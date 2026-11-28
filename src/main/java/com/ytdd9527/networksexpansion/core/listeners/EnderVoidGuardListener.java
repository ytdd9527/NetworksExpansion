package com.ytdd9527.networksexpansion.core.listeners;

import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.VersionUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.drive.DriveSlotGuard;
import com.ytdd9527.networksexpansion.api.drive.DriveType;
import com.ytdd9527.networksexpansion.api.cell.VoidCellSupport;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnderVoidGuardListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Inventory top = VersionUtil.getTopInventory(event);
        if (top == null || !isEnderDrive(top)) {
            return;
        }
        int rawSlot = event.getRawSlot();
        if (rawSlot < 0 || rawSlot >= top.getSize()) {
            return;
        }
        ItemStack beingPlaced = DriveSlotGuard.resolvePlacedItem(event);
        if (!involvesVoidCell(beingPlaced) && !involvesVoidCell(event.getCursor())) {
            return;
        }
        event.setCancelled(true);
        player.sendTitle("", Lang.getString(CellnetText.ENDER_VOID_REJECTED), 5, 50, 10);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Inventory top = VersionUtil.getTopInventory(event);
        if (top == null || !isEnderDrive(top)) {
            return;
        }
        ItemStack dragged = event.getOldCursor();
        if (!involvesVoidCell(dragged)) {
            return;
        }
        int topSize = top.getSize();
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < topSize) {
                event.setCancelled(true);
                player.sendTitle("", Lang.getString(CellnetText.ENDER_VOID_REJECTED), 5, 50, 10);
                return;
            }
        }
    }

    private static boolean involvesVoidCell(@Nullable ItemStack item) {
        return item != null && !item.getType().isAir() && VoidCellSupport.isVoidCell(item);
    }

    private static boolean isEnderDrive(@NotNull Inventory top) {
        if (!(top.getHolder() instanceof BlockMenu menu)) {
            return false;
        }
        return DriveType.of(StorageCacheUtils.getSfItem(menu.getLocation())) == DriveType.ENDER;
    }
}
