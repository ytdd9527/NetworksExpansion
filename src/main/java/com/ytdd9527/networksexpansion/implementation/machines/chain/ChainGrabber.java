package com.ytdd9527.networksexpansion.implementation.machines.chain;

import com.balugaq.netex.api.enums.TransferType;
import com.ytdd9527.networksexpansion.api.chain.WorkMode;
import com.ytdd9527.networksexpansion.core.machines.AbstractChainMachine;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ChainGrabber extends AbstractChainMachine {

    public ChainGrabber(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public @NotNull TransferType getTransferType() {
        return TransferType.CHAIN_GRABBER;
    }

    @Override
    public @NotNull WorkMode getWorkMode() {
        return WorkMode.GRAB;
    }
}
