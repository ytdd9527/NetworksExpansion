package com.ytdd9527.networksexpansion.implementation.machines.manual;

import com.balugaq.netex.api.data.SuperRecipe;
import com.balugaq.netex.api.helpers.Icon;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractManualCrafter;
import com.ytdd9527.networksexpansion.implementation.ExpansionRecipes;
import io.github.sefiraat.networks.utils.Keys;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExpansionWorkbench extends AbstractManualCrafter {
    public static final List<SuperRecipe> RECIPES = new ArrayList<>();
    public static final RecipeType TYPE = new RecipeType(
            Keys.EXPANSION_WORKBENCH,
            Icon.RECIPE_TYPE_ITEMSTACK_EXPANSION_WORKBENCH_3x3,
            ExpansionWorkbench::addRecipe);
    public static final Map<Integer, ItemStack> BACKGROUNDS = new HashMap<>();
    private static final int[] BACKGROUND_SLOTS = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 14, 15, 16, 17, 18, 22, 24, 26, 27, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44
    };
    private static final int[] RECIPE_SLOTS = {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final int CRAFT_SLOT = 23;
    private static final int OUTPUT_SLOT = 25;

    static {
        for (int slot : BACKGROUND_SLOTS) {
            BACKGROUNDS.put(slot, Icon.BACKGROUND_STACK_3x3);
        }

        BACKGROUNDS.put(CRAFT_SLOT, Icon.CRAFT_BUTTON_STACK_3x3);
    }

    @ParametersAreNonnullByDefault
    public ExpansionWorkbench(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public static void addRecipe(ItemStack[] input, ItemStack output) {
        if (!Arrays.equals(input, ExpansionRecipes.NULL)) {
            RECIPES.add(new SuperRecipe(true, input, output));
        }
    }

    @Override
    public @NotNull List<SuperRecipe> getRecipes() {
        return RECIPES;
    }

    @Override
    public int[] getInputSlots() {
        return RECIPE_SLOTS;
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] {OUTPUT_SLOT};
    }

    @Override
    public int getHandleSlot() {
        return CRAFT_SLOT;
    }

    @Override
    public boolean isTransportable() {
        return true;
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getBackgrounds() {
        return BACKGROUNDS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable Map<Integer, ChestMenu.MenuClickHandler> getMenuClickHandlers() {
        return null;
    }

    @Override
    public @NotNull BlockPlaceHandler getMachineBlockPlaceHandler() {
        return new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@NotNull BlockPlaceEvent e) {}
        };
    }

    @Override
    public @NotNull BlockBreakHandler getMachineBlockBreakHandler() {
        return new BlockBreakHandler(false, false) {
            @Override
            @ParametersAreNonnullByDefault
            public void onPlayerBreak(BlockBreakEvent event, ItemStack itemStack, List<ItemStack> drops) {
                BlockMenu menu = StorageCacheUtils.getMenu(event.getBlock().getLocation());
                if (menu != null) {
                    menu.dropItems(menu.getLocation(), RECIPE_SLOTS);
                    menu.dropItems(menu.getLocation(), OUTPUT_SLOT);
                }
            }
        };
    }

    @Override
    public @Nullable BlockTicker getMachineBlockTicker() {
        return null;
    }

    @Override
    public int getCapacity() {
        return 0;
    }
}
