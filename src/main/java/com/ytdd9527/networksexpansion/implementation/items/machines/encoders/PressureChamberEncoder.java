package com.ytdd9527.networksexpansion.implementation.items.machines.encoders;

import com.ytdd9527.networksexpansion.api.helpers.SupportedPressureChamberRecipes;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractEncoder;
import com.ytdd9527.networksexpansion.implementation.items.blueprints.PressureChamberBlueprint;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class PressureChamberEncoder extends AbstractEncoder {

    public PressureChamberEncoder(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public void blueprintSetter(ItemStack itemStack, ItemStack[] inputs, ItemStack crafted) {
        PressureChamberBlueprint.setBlueprint(itemStack, inputs, crafted);
    }

    public boolean isValidBlueprint(ItemStack blueprint) {
        return SlimefunItem.getByItem(blueprint) instanceof PressureChamberBlueprint;
    }

    public Set<Map.Entry<ItemStack[], ItemStack>> getRecipeEntries() {
        return SupportedPressureChamberRecipes.getRecipes().entrySet();
    }

    public boolean getRecipeTester(ItemStack[] inputs, ItemStack[] recipe) {
        return SupportedPressureChamberRecipes.testRecipe(inputs, recipe);
    }
}