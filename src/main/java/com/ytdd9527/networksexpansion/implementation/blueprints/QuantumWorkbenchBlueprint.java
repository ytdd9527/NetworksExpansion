package com.ytdd9527.networksexpansion.implementation.blueprints;

import com.ytdd9527.networksexpansion.core.items.unusable.AbstractBlueprint;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

public class QuantumWorkbenchBlueprint extends AbstractBlueprint {

    public QuantumWorkbenchBlueprint(
            ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }
}
