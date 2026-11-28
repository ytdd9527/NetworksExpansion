package com.ytdd9527.networksexpansion.core.listeners;

import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.Scheduling;
import com.ytdd9527.networksexpansion.api.cell.CellLore;
import com.ytdd9527.networksexpansion.api.cell.CellMenu;
import com.ytdd9527.networksexpansion.api.cell.CellMenuCommon;
import com.ytdd9527.networksexpansion.api.cell.CellPersistence;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.cell.StorageCell;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CellMenuListener implements Listener {

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        CellMenu.stopOpening(e.getPlayer().getUniqueId());
        CellMenu.stopRenaming(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (isOpeningCell(player, e.getCurrentItem())) {
            closeCellView(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryDrag(@NotNull InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (isOpeningCell(player, e.getOldCursor())) {
            closeCellView(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if (isOpeningCell(player, e.getItemDrop().getItemStack())) {
            closeCellView(player);
        }
    }

    private static boolean isOpeningCell(@NotNull Player player, @Nullable ItemStack item) {
        UUID cellUuid = CellMenu.getOpeningCell(player.getUniqueId());
        return cellUuid != null && cellUuid.equals(StorageCell.getCellUUID(item));
    }

    private static void closeCellView(@NotNull Player player) {
        Scheduling.runTask(() -> {
            CellMenu.stopOpening(player.getUniqueId());
            player.closeInventory();
        });
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(@NotNull AsyncChatEvent e) {
        Player player = e.getPlayer();
        UUID cellUuid = CellMenu.getRenamingCell(player.getUniqueId());
        if (cellUuid == null) {
            return;
        }
        e.setCancelled(true);
        CellMenu.stopRenaming(player.getUniqueId());

        String name = PlainTextComponentSerializer.plainText().serialize(e.message()).trim();
        if (name.isEmpty()) {
            player.sendMessage(Lang.getString(CellnetText.CELL_RENAME_CANCELLED));
            return;
        }

        Scheduling.runTask(() -> {
            ItemStack cell = CellMenuCommon.findCellInPlayer(player, cellUuid);
            if (cell == null) {
                player.sendMessage(Lang.getString(CellnetText.CELL_RENAME_FAILED));
                return;
            }
            CellPersistence.setCustomName(cell, name);
            long per = StorageCell.getPerTypeLimit(cell);
            long cur = StorageCell.getCurrentPerTypeLimit(cell);
            CellLore.applySpecLore(cell, per, cur);
            CellMenuCommon.writeBackLocated(player, cell);
            CellMenu.open(player, cell);
            player.sendMessage(Lang.getString(CellnetText.CELL_RENAME_SUCCESS, name));
        });
    }
}
