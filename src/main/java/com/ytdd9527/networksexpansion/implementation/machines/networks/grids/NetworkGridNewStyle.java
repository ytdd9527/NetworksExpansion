package com.ytdd9527.networksexpansion.implementation.machines.networks.grids;

import com.balugaq.netex.api.keybind.Keybindable;
import com.balugaq.netex.api.keybind.Keybinds;
import com.balugaq.netex.api.visual.Screen;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractGridNewStyle;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache.DisplayMode;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@SuppressWarnings("DuplicatedCode")
public class NetworkGridNewStyle extends AbstractGridNewStyle implements Keybindable {
    private static final int[] BACKGROUND_SLOTS = new int[]{8};

    private static final int[] DISPLAY_SLOTS = {
        0, 1, 2, 3, 4, 5, 6, 7,
        9, 10, 11, 12, 13, 14, 15, 16,
        18, 19, 20, 21, 22, 23, 24, 25,
        27, 28, 29, 30, 31, 32, 33, 34,
        36, 37, 38, 39, 40, 41, 42, 43,
        45, 46, 47, 48, 49, 50, 51, 52,
    };

    private static final int KEYBIND_BUTTON_SLOT = 8;

    private static final int CHANGE_SORT = 35;
    private static final int FILTER = 26;
    private static final int PAGE_PREVIOUS = 44;
    private static final int PAGE_NEXT = 53;
    private static final int TOGGLE_MODE_SLOT = 17;

    public NetworkGridNewStyle(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemHandler(new BlockBreakHandler(false, false) {

            @Override
            public void onPlayerBreak(BlockBreakEvent event, ItemStack item, List<ItemStack> drops) {
                onBreak(event);
            }
        });
    }

    public NetworkGridNewStyle(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        ItemStack[] recipe,
        NodeType nodeType) {
        super(itemGroup, item, recipeType, recipe, nodeType);
    }

    @Override
    public void postNewInstance(BlockMenu menu, Block b) {
        menu.replaceExistingItem(getToggleModeSlot(), getModeStack(DisplayMode.DISPLAY));
        menu.addMenuClickHandler(getToggleModeSlot(), (p, slot, item, action) -> {
            if (!action.isRightClicked()) {
                GridCache gridCache = getCacheMap().get(menu.getLocation());
                gridCache.toggleDisplayMode();
                menu.replaceExistingItem(getToggleModeSlot(), getModeStack(gridCache));
                updateDisplay(Screen.of(menu));
            }
            return false;
        });

        menu.addPlayerInventoryClickHandler(outsideKeybinds());
        addKeybindSettingsButton(menu, getKeybindButtonSlot());
    }

    public int[] getBackgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    public int[] getDisplaySlots() {
        return DISPLAY_SLOTS;
    }

    public int getChangeSort() {
        return CHANGE_SORT;
    }

    public int getPagePrevious() {
        return PAGE_PREVIOUS;
    }

    public int getPageNext() {
        return PAGE_NEXT;
    }

    public int getKeybindButtonSlot() {
        return KEYBIND_BUTTON_SLOT;
    }

    public int getToggleModeSlot() {
        return TOGGLE_MODE_SLOT;
    }

    @Override
    protected int getFilterSlot() {
        return FILTER;
    }

    @Override
    public List<Keybinds> keybinds() {
        return List.of(displayKeybinds(), outsideKeybinds());
    }
}
