package com.ytdd9527.networksexpansion.core.items;

import com.balugaq.netex.api.interfaces.FeedbackSendable;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotConfigurable;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

/**
 * We may add something soon
 *
 * @author Final_ROOT
 * @author baluagq
 * @since 2.0
 */
@NullMarked
public abstract class SpecialSlimefunItem extends SlimefunItem implements FeedbackSendable {
    public SpecialSlimefunItem(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        @Nullable ItemStack [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public SpecialSlimefunItem(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        @Nullable ItemStack [] recipe,
        @Nullable ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    public SpecialSlimefunItem(
        ItemGroup itemGroup,
        ItemStack item,
        String id,
        RecipeType recipeType,
        @Nullable ItemStack [] recipe) {
        super(itemGroup, item, id, recipeType, recipe);
    }

    public SpecialSlimefunItem(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        @Nullable ItemStack [] recipe,
        @Range(from = 1, to = 64) int outputAmount
    ) {
        this(itemGroup, item, recipeType, recipe, StackUtils.getAsQuantity(item, outputAmount));
    }

    @Override
    public void register(SlimefunAddon addon) {
        super.register(addon);

        this.enchantable = false;
        this.disenchantable = true;
        if (!(this instanceof NotConfigurable)) {
            Slimefun.getItemCfg().setDefaultValue(getId() + ".allow-enchanting", this.enchantable);
            Slimefun.getItemCfg().setDefaultValue(getId() + ".allow-disenchanting", this.disenchantable);
        }
    }

    public SpecialSlimefunItem registerThis() {
        this.register(Networks.getInstance());
        return this;
    }
}
