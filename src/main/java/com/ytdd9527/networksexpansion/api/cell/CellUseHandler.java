package com.ytdd9527.networksexpansion.api.cell;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class CellUseHandler {

    private CellUseHandler() {
    }

    public interface CellUse {
        boolean prepare(@NotNull Player player, @NotNull ItemStack cell);

        void open(@NotNull Player player, @NotNull ItemStack cell);
    }

    @NotNull
    public static ItemUseHandler create(@NotNull CellUse use) {
        return e -> {
            if (e.getPlayer().isSneaking()) {
                return;
            }
            e.cancel();
            ItemStack cell = e.getItem();
            if (!use.prepare(e.getPlayer(), cell)) {
                return;
            }
            // 写回触发事件的那只手，否则副手右键会把元件复制到主手
            if (e.getHand() == EquipmentSlot.OFF_HAND) {
                e.getPlayer().getInventory().setItemInOffHand(cell);
            } else {
                e.getPlayer().getInventory().setItemInMainHand(cell);
            }
            use.open(e.getPlayer(), cell);
        };
    }
}
