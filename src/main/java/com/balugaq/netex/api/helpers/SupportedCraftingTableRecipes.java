package com.balugaq.netex.api.helpers;

import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("JavaExistingMethodCanBeUsed")
@UtilityClass
@NullMarked
public final class SupportedCraftingTableRecipes {

    private static final Map<@Nullable ItemStack[], ItemStack> RECIPES = new HashMap<>();

    static {
        String id = SlimefunItems.ENHANCED_CRAFTING_TABLE.getItemId();
        SlimefunItem recipeTypeItem = SlimefunItem.getById(id);
        if (recipeTypeItem instanceof MultiBlockMachine mb) {
            boolean isInput = true;
            @Nullable ItemStack @Nullable [] input = null;
            ItemStack[] output;
            for (ItemStack[] recipe : mb.getRecipes()) {
                if (isInput) {
                    input = recipe;
                } else {
                    output = recipe;
                    if (input.length != 9) {
                        @Nullable ItemStack[] newInput = new ItemStack[9];
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
            if ((recipeType == RecipeType.ENHANCED_CRAFTING_TABLE) && allowedRecipe(item)) {
                @Nullable ItemStack[] itemStacks = new ItemStack[9];
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
                SupportedCraftingTableRecipes.addRecipe(itemStacks, item.getRecipeOutput());
            }
        }
    }

    public static Map<@Nullable ItemStack[], ItemStack> getRecipes() {
        return RECIPES;
    }

    public static void addRecipe(@Nullable ItemStack[] input, ItemStack output) {
        RECIPES.put(input, output);
    }

    public static boolean testRecipe(@Nullable ItemStack[] input, ItemStack [] recipe) {
        for (int test = 0; test < recipe.length; test++) {
            if (!StackUtils.itemsMatch(input[test], recipe[test])) {
                return false;
            }
        }
        return true;
    }

    public static boolean allowedRecipe(SlimefunItem item) {
        return !(item instanceof SlimefunBackpack);
    }
}
