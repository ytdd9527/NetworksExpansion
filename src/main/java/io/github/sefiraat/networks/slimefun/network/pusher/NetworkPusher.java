package io.github.sefiraat.networks.slimefun.network.pusher;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NetworkPusher extends AbstractNetworkPusher {

    private static final int[] BACKGROUND_SLOTS = new int[] {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 15, 17, 18, 20, 22, 23, 27, 28, 30, 31, 33, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44
    };
    private static final int[] TEMPLATE_BACKGROUND = new int[] {16};
    private static final int[] TEMPLATE_SLOTS = new int[] {24, 25, 26, 34};

    public NetworkPusher(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public int @NotNull [] getBackgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    @Override
    public int @NotNull [] getOtherBackgroundSlots() {
        return TEMPLATE_BACKGROUND;
    }

    @Override
    public int @NotNull [] getItemSlots() {
        return TEMPLATE_SLOTS;
    }
}
