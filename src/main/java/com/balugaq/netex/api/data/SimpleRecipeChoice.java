package com.balugaq.netex.api.data;

import io.github.sefiraat.networks.utils.StackUtils;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

public class SimpleRecipeChoice extends RecipeChoice.ExactChoice implements RecipeChoice {
    public SimpleRecipeChoice(@NotNull ItemStack choice) {
        super(choice);
    }

    public SimpleRecipeChoice(@NotNull ItemStack... choices) {
        super(choices);
    }

    public SimpleRecipeChoice(@NotNull List<ItemStack> choices) {
        super(choices);
    }

    @Override
    public boolean test(@NotNull ItemStack other) {
        for (ItemStack choice : getChoices()) {
            if (StackUtils.itemsMatch(choice, other, true, false)) {
                return true;
            }
        }

        return false;
    }
}
