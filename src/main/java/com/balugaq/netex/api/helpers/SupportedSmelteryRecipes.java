package com.balugaq.netex.api.helpers;

import com.balugaq.netex.api.interfaces.CanTestRecipe;
import com.balugaq.netex.api.interfaces.RecipesHolder;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class SupportedSmelteryRecipes implements RecipesHolder, CanTestRecipe {

    private static final Map<ItemStack[], ItemStack> RECIPES = new HashMap<>();

    static {
        String id = SlimefunItems.SMELTERY.getItemId();
        SlimefunItem recipeTypeItem = SlimefunItem.getById(id);
        if (recipeTypeItem instanceof MultiBlockMachine mb) {
            boolean isInput = true;
            ItemStack[] input = null;
            ItemStack[] output;
            for (ItemStack[] recipe : mb.getRecipes()) {
                if (isInput) {
                    input = recipe;
                } else {
                    output = recipe;
                    if (input.length != 9) {
                        ItemStack[] newInput = new ItemStack[9];
                        for (int i = 0; i < 9; i++) {
                            if (i < input.length) {
                                newInput[i] = input[i];
                            } else {
                                newInput[i] = null;
                            }
                        }
                        input = newInput;
                    }
                    RECIPES.put(input, output[0]);
                }
                isInput = !isInput;
            }
        }
        for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            RecipeType recipeType = item.getRecipeType();
            if ((recipeType == RecipeType.SMELTERY) && allowedRecipe(item)) {
                ItemStack[] itemStacks = new ItemStack[9];
                int i = 0;
                for (ItemStack itemStack : item.getRecipe()) {
                    if (itemStack == null) {
                        itemStacks[i] = null;
                    } else {
                        itemStacks[i] = new ItemStack(itemStack.clone());
                    }
                    if (++i >= 9) {
                        break;
                    }
                }
                SupportedSmelteryRecipes.addRecipe(itemStacks, item.getRecipeOutput());
            }
        }
    }

    public static @NotNull Map<ItemStack[], ItemStack> getRecipes() {
        return RECIPES;
    }

    public static void addRecipe(@NotNull ItemStack[] input, @NotNull ItemStack output) {
        RECIPES.put(input, output);
    }

    public static boolean testRecipe(@NotNull ItemStack[] input, @NotNull ItemStack @NotNull [] recipe) {
        for (int test = 0; test < recipe.length; test++) {
            if (!StackUtils.itemsMatch(input[test], recipe[test], false, true)) {
                return false;
            }
        }
        return true;
    }

    public static boolean allowedRecipe(@NotNull SlimefunItem item) {
        return !(item instanceof SlimefunBackpack);
    }
}
