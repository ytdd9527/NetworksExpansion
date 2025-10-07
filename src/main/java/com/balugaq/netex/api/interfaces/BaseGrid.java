package com.balugaq.netex.api.interfaces;

import com.balugaq.netex.api.enums.AmountHandleStrategy;
import com.balugaq.netex.api.keybind.Action;
import com.balugaq.netex.api.keybind.ActionResult;
import com.balugaq.netex.api.keybind.Keybind;
import com.balugaq.netex.api.keybind.Keybinds;
import com.balugaq.netex.api.keybind.MultiActionHandle;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.GridItemRequest;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.StackUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
@NullMarked
public interface BaseGrid {
    Map<String, Keybinds> KEYBINDS = new HashMap<>();

    default Keybinds displayKeybinds() {
        String key = "display-keybinds";
        if (KEYBINDS.containsKey(key)) {
            return KEYBINDS.get(key);
        }

        Keybinds keybinds = Keybinds.create(Keys.newKey(key), it -> {
                Keybind clickOnWithCursor = Keybind.of(Keys.newKey("clicked-item-not-equals-to-cursor"), (player, s, i, a, menu) -> {
                    NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());
                    if (definition == null || definition.getNode() == null) return false;
                    ItemStack clone = precheck(definition, menu, player, i);
                    if (clone == null) return false;
                    final ItemStack cursor = player.getItemOnCursor();
                    return cursor.getType() != Material.AIR
                        && !StackUtils.itemsMatch(clone, player.getItemOnCursor(), true, false);
                });

                Action storeCursor = Action.of(Keys.newKey("store-cursor"), (player, s, i, a, menu) -> {
                    NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());
                    if (definition == null || definition.getNode() == null)
                        return ActionResult.of(MultiActionHandle.CONTINUE, false);
                    definition.getNode().getRoot().addItemStack(player.getItemOnCursor());
                    return ActionResult.of(MultiActionHandle.BREAK, false);
                });

                Action strategy1 = Keybind.gridActionGenerate(this, AmountHandleStrategy.ONE, false);
                Action strategy2 = Keybind.gridActionGenerate(this, AmountHandleStrategy.STACK, false);
                Action strategy3 = Keybind.gridActionGenerate(this, AmountHandleStrategy.STACK, true);
                Action strategy4 = Keybind.gridActionGenerate(this, AmountHandleStrategy.ONE, true);
                Action strategy5 = Keybind.gridActionGenerate(this, AmountHandleStrategy.CUSTOM, false);
                Action strategy6 = Keybind.gridActionGenerate(this, AmountHandleStrategy.CUSTOM, true);
                Action strategy7 = Keybind.gridActionGenerate(this, AmountHandleStrategy.ASK, false);
                Action strategy8 = Keybind.gridActionGenerate(this, AmountHandleStrategy.ASK, true);

                it.usableKeybinds(
                    Keybind.leftClick,
                    Keybind.rightClick,
                    Keybind.shiftLeftClick,
                    Keybind.shiftRightClick,
                    Keybind.shiftClick,
                    clickOnWithCursor
                );
                it.usableActions(storeCursor, strategy1, strategy2, strategy3, strategy4, strategy5, strategy6, strategy7, strategy8);
                it.defaultKeybinds(
                    clickOnWithCursor, storeCursor,
                    Keybind.leftClick, strategy1,
                    Keybind.rightClick, strategy2,
                    Keybind.shiftLeftClick, strategy3,
                    Keybind.shiftRightClick, strategy4
                );
                it.defaultActionResult(ActionResult.of(MultiActionHandle.CONTINUE, false));
            })
            .generate();

        KEYBINDS.put(key, keybinds);
        return keybinds;
    }

    default Keybinds outsideKeybinds() {
        String key = "outside-keybinds";
        if (KEYBINDS.containsKey(key)) {
            return KEYBINDS.get(key);
        }

        Keybinds keybinds = Keybinds.create(Keys.newKey(key), it -> {
                it.usableKeybinds(
                    Keybind.leftClick,
                    Keybind.rightClick,
                    Keybind.shiftLeftClick,
                    Keybind.shiftRightClick,
                    Keybind.shiftClick
                );

                Action storeItem = Action.of(Keys.newKey("store-item"), (p, s, i, a, menu) -> {
                    receiveItem(p, i, a, menu);
                    return ActionResult.of(MultiActionHandle.BREAK, false);
                });

                it.usableActions(
                    storeItem,
                    Keybind.gridActionGenerate(this, AmountHandleStrategy.ONE, true),
                    Keybind.gridActionGenerate(this, AmountHandleStrategy.STACK, true),
                    Keybind.gridActionGenerate(this, AmountHandleStrategy.CUSTOM, true),
                    Keybind.gridActionGenerate(this, AmountHandleStrategy.ASK, true)
                );
                it.defaultKeybinds(
                    Keybind.shiftLeftClick, storeItem
                );
                it.defaultActionResult(ActionResult.of(MultiActionHandle.CONTINUE, true));
            })
            .generate();

        KEYBINDS.put(key, keybinds);
        return keybinds;
    }

    @Nullable
    ItemStack precheck(NodeDefinition definition, BlockMenu blockMenu, Player player, ItemStack itemStack);

    void addToInventory(Player player, NodeDefinition definition, GridItemRequest request, BlockMenu menu);

    void addToCursor(Player player, NodeDefinition definition, GridItemRequest request, boolean rightClick, BlockMenu blockMenu);

    Map<Location, GridCache> getCacheMap();

    void updateDisplay(BlockMenu blockMenu);

    void receiveItem(Player player, ItemStack itemStack, ClickAction action, BlockMenu blockMenu);
}
