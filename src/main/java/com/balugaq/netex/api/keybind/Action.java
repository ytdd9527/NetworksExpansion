package com.balugaq.netex.api.keybind;

import com.balugaq.netex.api.interfaces.functions.Function5;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("deprecation")
public interface Action extends Keyed, Comparable<Action> {
    static Action of(NamespacedKey key, Function5<Player, Integer, @Nullable ItemStack, ClickAction, BlockMenu, ActionResult> function) {
        return new Action() {
            @Override
            public ActionResult apply(Player player, int slot, @Nullable ItemStack item, ClickAction action, BlockMenu menu) {
                return function.apply(player, slot, item, action, menu);
            }

            @Override
            public NamespacedKey getKey() {
                return key;
            }
        }.register();
    }

    @Nullable
    static Action get(NamespacedKey key) {
        return Keybinds.getAction(key);
    }

    default int compareTo(Action o) {
        return getKey().compareTo(o.getKey());
    }

    default Action register() {
        Keybinds.register(this);
        return this;
    }

    ActionResult apply(Player player, int slot, ItemStack item, ClickAction action, BlockMenu menu);
}
