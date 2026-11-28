package com.ytdd9527.networksexpansion.api.drive;

import com.balugaq.netex.utils.VersionUtil;
import com.ytdd9527.networksexpansion.implementation.machines.drive.CellDrive;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public final class DriveSlotGuard {

    private DriveSlotGuard() {
    }

    public static boolean isCellSlot(int rawSlot) {
        for (int slot : CellDrive.CELL_SLOTS) {
            if (slot == rawSlot) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static ItemStack resolvePlacedItem(@NotNull InventoryClickEvent event) {
        Inventory top = VersionUtil.getTopInventory(event);
        if (top == null) {
            return null;
        }
        int rawSlot = event.getRawSlot();
        boolean clickedTop = rawSlot >= 0 && rawSlot < top.getSize();
        ClickType click = event.getClick();

        if (click == ClickType.NUMBER_KEY) {
            int hotbar = event.getHotbarButton();
            if (hotbar < 0 || !clickedTop || !isCellSlot(rawSlot)) {
                return null;
            }
            return event.getWhoClicked().getInventory().getItem(hotbar);
        }

        if (click == ClickType.SWAP_OFFHAND) {
            if (!clickedTop || !isCellSlot(rawSlot)) {
                return null;
            }
            return event.getWhoClicked().getInventory().getItemInOffHand();
        }

        if (click == ClickType.DOUBLE_CLICK) {
            return event.getCursor();
        }

        if (click.isShiftClick()) {
            if (clickedTop) {
                return null;
            }
            return event.getCurrentItem();
        }

        if (clickedTop && isCellSlot(rawSlot)) {
            return event.getCursor();
        }

        return null;
    }
}
