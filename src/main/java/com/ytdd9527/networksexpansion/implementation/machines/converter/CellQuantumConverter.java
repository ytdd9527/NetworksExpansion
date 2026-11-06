package com.ytdd9527.networksexpansion.implementation.machines.converter;

import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.api.cell.CellTier;
import com.ytdd9527.networksexpansion.api.cell.StorageCell;
import com.ytdd9527.networksexpansion.api.cell.CellLedger;
import com.ytdd9527.networksexpansion.api.cell.CellPersistence;
import io.github.sefiraat.networks.network.stackcaches.QuantumCache;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.sefiraat.networks.utils.datatypes.PersistentQuantumStorageType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class CellQuantumConverter {

    private CellQuantumConverter() {
    }

    @NotNull
    public static ItemStack buildQuantumStorage(@NotNull ItemStack sample, long amount, @NotNull NetworkQuantumStorage storage) {
        return buildQuantumStorage(sample, amount, storage, storage.getMaxAmount());
    }

    @NotNull
    public static ItemStack buildQuantumStorage(
        @NotNull ItemStack sample,
        long amount,
        @NotNull NetworkQuantumStorage storage,
        long limit) {
        ItemStack qsItem = storage.getItem().clone();
        ItemMeta meta = qsItem.getItemMeta();
        QuantumCache quantumCache = new QuantumCache(
            sample.clone(), amount, limit, false, storage.supportsCustomMaxAmount());
        DataTypeMethods.setCustom(meta, Keys.QUANTUM_STORAGE_INSTANCE, PersistentQuantumStorageType.TYPE, quantumCache);
        quantumCache.addMetaLore(meta);
        qsItem.setItemMeta(meta);
        return qsItem;
    }

    @NotNull
    public static ItemStack buildEmptyQuantumStorage(@NotNull NetworkQuantumStorage storage) {
        return storage.getItem().clone();
    }

    public static void convert(@NotNull Player player) {
        ItemStack cell = player.getInventory().getItemInMainHand();
        if (cell.getType() == Material.AIR || !StorageCell.isStorageCell(cell)) {
            player.sendMessage(Lang.getString("messages.commands.celltoquantum.need-cell"));
            return;
        }

        long perTypeLimit = StorageCell.getPerTypeLimit(cell);
        NetworkQuantumStorage storage = CellTier.upgradeMaterialOf(perTypeLimit);
        if (storage == null) {
            player.sendMessage(Lang.getString("messages.commands.celltoquantum.none-converted"));
            return;
        }

        CellLedger cache = StorageCell.loadCellCache(cell, perTypeLimit);
        if (cache.getStoredItems().isEmpty()) {
            player.sendMessage(Lang.getString("messages.commands.celltoquantum.empty"));
            return;
        }

        int converted = convertEntries(player, cache, storage);

        if (converted > 0) {
            long newUnits = StorageCell.getCurrentPerTypeLimit(cell) - converted;
            CellPersistence.setCurrentPerTypeLimit(cell, newUnits);
        }

        CellPersistence.saveAsync();
        StorageCell.applyLore(cell, perTypeLimit, StorageCell.getCurrentPerTypeLimit(cell));
        player.getInventory().setItemInMainHand(cell);

        if (converted == 0) {
            player.sendMessage(Lang.getString("messages.commands.celltoquantum.none-converted"));
        } else {
            player.sendMessage(Lang.getString("messages.commands.celltoquantum.converted"));
        }
    }

    private static int convertEntries(
            @NotNull Player player,
            @NotNull CellLedger cache,
            @NotNull NetworkQuantumStorage storage) {
        int converted = 0;
        for (CellLedger.CellEntry entry : new ArrayList<>(cache.getStoredItems())) {
            if (cache.takeItem(entry.sample, entry.amount) == null) {
                continue;
            }
            converted++;

            ItemStack qsItem = buildQuantumStorage(entry.sample, entry.amount, storage);

            if (!player.getInventory().addItem(qsItem).isEmpty()) {
                player.getWorld().dropItem(player.getLocation(), qsItem);
            }
        }
        return converted;
    }
}
