package io.github.sefiraat.networks.slimefun.network.grid;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;


@SuppressWarnings("DuplicatedCode")
@NullMarked
public class NetworkGrid extends AbstractGrid {

    private static final int[] BACKGROUND_SLOTS = {17, 26};

    private static final int[] DISPLAY_SLOTS = {
        0, 1, 2, 3, 4, 5, 6, 7,
        9, 10, 11, 12, 13, 14, 15, 16,
        18, 19, 20, 21, 22, 23, 24, 25,
        27, 28, 29, 30, 31, 32, 33, 34,
        36, 37, 38, 39, 40, 41, 42, 43,
        45, 46, 47, 48, 49, 50, 51, 52
    };

    private static final int INPUT_SLOT = 8;

    private static final int CHANGE_SORT = 26;
    private static final int FILTER = 35;
    private static final int PAGE_PREVIOUS = 44;
    private static final int PAGE_NEXT = 53;

    public NetworkGrid(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    protected SlotLayout slotLayout() {
        return new SlotLayout(
            BACKGROUND_SLOTS, DISPLAY_SLOTS, INPUT_SLOT,
            CHANGE_SORT, PAGE_PREVIOUS, PAGE_NEXT, FILTER);
    }
}
