package com.balugaq.netex.api.keybind;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface Keybindable {
    default ItemStack icon() {
        return getItem();
    }

    ItemStack getItem();

    List<Keybinds> keybinds();

    default void addKeybindSettingsButton(BlockMenu menu, int slot) {
        menu.addItem(slot, Icon.KEYBIND_SETTINGS, (p, s, i, a) -> {
            openMenu(menu.getLocation(), p, keybinds());
            return false;
        });
    }

    default void openMenu(Location location, Player player, List<Keybinds> keybinds) {
        openMenu(location, player, keybinds, 1);
    }

    @SuppressWarnings("deprecation")
    default void openMenu(Location location, Player player, List<Keybinds> keybinds, int page) {
        ChestMenu menu = new ChestMenu(Lang.getString("messages.keybind.title"));

        int[] backgroundSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 17, 18, 22, 26, 27, 31, 35, 36, 40, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
        int[] keybindsSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        int previousSlot = 47;
        int nextSlot = 52;

        for (int slot : backgroundSlots) {
            menu.addItem(slot, Icon.BLUE_BACKGROUND, (p, s, i, a) -> false);
        }

        for (int slot = 0; slot < keybindsSlots.length; slot++) {
            if (slot >= keybinds.size()) {
                menu.addItem(keybindsSlots[slot], Icon.LIGHT_GRAY_BACKGROUND, (p, s, i, a) -> false);
            } else {
                Keybinds keybind = keybinds.get(slot + (page - 1) * keybindsSlots.length);
                menu.addItem(keybindsSlots[slot], keybind.icon(), (p, s, i, a) -> {
                    keybind.openMenu(location, p);
                    return false;
                });
            }
        }

        int maxPage = (keybinds.size() - 1) / keybindsSlots.length + 1;
        if (page > 1) {
            menu.addItem(previousSlot, Icon.getPageStack(Icon.PAGE_PREVIOUS_STACK, page, maxPage), (p, s, i, a) -> {
                if (page <= 1) return false;
                openMenu(location, player, keybinds, page - 1);
                return false;
            });
        }

        if (page < maxPage) {
            menu.addItem(nextSlot, Icon.getPageStack(Icon.PAGE_NEXT_STACK, page, maxPage), (p, s, i, a) -> {
                if (page >= maxPage) return false;
                openMenu(location, player, keybinds, page + 1);
                return false;
            });
        }

        menu.open(player);
    }
}
