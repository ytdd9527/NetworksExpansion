package com.balugaq.netex.api.keybind;

import com.balugaq.netex.api.enums.AmountHandleStrategy;
import com.balugaq.netex.api.interfaces.BaseGrid;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.GridItemRequest;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.sefiraat.networks.utils.Keys;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("deprecation")
public interface Keybind extends Keyed, Comparable<Keybind> {
    Keybind leftClick = Keybind.of(
        Keys.newKey("left-click"),
        (player, slot, item, action, menu) -> !action.isShiftClicked() && !action.isRightClicked()
    );

    Keybind rightClick = Keybind.of(
        Keys.newKey("right-click"),
        (player, slot, item, action, menu) -> !action.isShiftClicked() && action.isRightClicked()
    );

    Keybind shiftClick = Keybind.of(
        Keys.newKey("shift-click"),
        (player, slot, item, action, menu) -> action.isShiftClicked()
    );

    Keybind shiftLeftClick = Keybind.of(
        Keys.newKey("shift-left-click"),
        (player, slot, item, action, menu) -> action.isShiftClicked() && !action.isRightClicked()
    );

    Keybind shiftRightClick = Keybind.of(
        Keys.newKey("shift-right-click"),
        (player, slot, item, action, menu) -> action.isShiftClicked() && action.isRightClicked()
    );

    static Keybind of(NamespacedKey key, KeybindPredicate predicate) {
        return KeybindImpl.of(key, predicate).register();
    }

    @Nullable
    static Keybind get(NamespacedKey key) {
        return Keybinds.getKeybind(key);
    }

    static Action gridActionGenerate(BaseGrid grid, AmountHandleStrategy strategy, boolean toInventory) {
        return Action.of(Keys.newKey(strategy.name().toLowerCase() + "-" + (toInventory ? "inv" : "cursor")), (player, s, i, a, menu) -> {
            NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());
            if (definition == null || definition.getNode() == null)
                return ActionResult.of(MultiActionHandle.CONTINUE, false);
            ItemStack clone = grid.precheck(definition, menu, player, i);
            if (clone == null) return ActionResult.of(MultiActionHandle.CONTINUE, false);

            strategy.handle(menu.getLocation(), player, clone, amount -> {
                final GridItemRequest request = new GridItemRequest(clone, amount, player);

                if (toInventory) {
                    grid.addToInventory(player, definition, request, menu);
                } else {
                    grid.addToCursor(player, definition, request, a.isRightClicked(), menu);
                }

                GridCache gridCache = grid.getCacheMap().get(menu.getLocation());
                if (gridCache.getDisplayMode() == GridCache.DisplayMode.DISPLAY) {
                    gridCache.addPullItemHistory(clone);
                }
                grid.updateDisplay(menu);
            });

            return ActionResult.of(MultiActionHandle.BREAK, false);
        });
    }

    default void set(Location location, Keybinds keybind, Action action) {
        StorageCacheUtils.setData(location, keybind.keybindKey(getKey()), action.getKey().toString());
    }

    default Keybind register() {
        Keybinds.register(this);
        return this;
    }

    default int compareTo(@NotNull Keybind o) {
        return getKey().compareTo(o.getKey());
    }

    boolean test(Player player, int slot, ItemStack item, ClickAction action, BlockMenu menu);

    @EqualsAndHashCode
    @Data
    class KeybindImpl implements Keybind {
        @EqualsAndHashCode.Include
        private final NamespacedKey key;
        @EqualsAndHashCode.Exclude
        private final KeybindPredicate predicate;

        public KeybindImpl(NamespacedKey key, KeybindPredicate predicate) {
            this.key = key;
            this.predicate = predicate;
        }

        public static KeybindImpl of(NamespacedKey key, KeybindPredicate predicate) {
            return new KeybindImpl(key, predicate);
        }

        @Override
        public boolean test(Player player, int slot, ItemStack item, ClickAction action, BlockMenu menu) {
            return predicate.test(player, slot, item, action, menu);
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }
    }
}
