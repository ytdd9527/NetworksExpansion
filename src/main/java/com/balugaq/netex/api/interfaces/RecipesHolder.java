package com.balugaq.netex.api.interfaces;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface RecipesHolder {
    @Deprecated
    static @NotNull Map<ItemStack[], ItemStack> getRecipes() {
        return new HashMap<>();
    }
}
