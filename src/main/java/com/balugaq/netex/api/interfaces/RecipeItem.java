package com.balugaq.netex.api.interfaces;

import com.balugaq.netex.api.data.RandomMachineRecipe;
import com.balugaq.netex.api.factories.MachineRecipeFactory;
import com.balugaq.netex.api.helpers.Icon;
import com.ytdd9527.networksexpansion.core.items.unusable.ReplaceableCard;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.RecipeUtil;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import java.util.ArrayList;
import java.util.List;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link SlimefunItem} that will show its working-recipe in {@link SlimefunGuide}.
 *
 * @author Final_ROOT
 * @since 1.0
 */
public interface RecipeItem extends RecipeDisplayItem {
    @NotNull @Override
    default List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes =
                new ArrayList<>(this.getMachineRecipes().size() * 2);
        for (MachineRecipe recipe : this.getMachineRecipes()) {
            int inputLength = recipe.getInput().length;
            int outputLength = recipe instanceof RandomMachineRecipe
                    ? ((RandomMachineRecipe) recipe).getAllOutput().length
                    : recipe.getOutput().length;
            for (int i = 0; i < inputLength; i++) {
                displayRecipes.add(ItemStackUtil.getCleanItem(recipe.getInput()[i]));
                if (i < inputLength - 1) {
                    displayRecipes.add(new ItemStack(Material.AIR));
                }
            }
            if (inputLength == 0) {
                displayRecipes.add(new ItemStack(Material.AIR));
            }
            for (int i = 0; i < outputLength; i++) {
                if (i != 0) {
                    displayRecipes.add(new ItemStack(Material.AIR));
                }
                displayRecipes.add(ItemStackUtil.getCleanItem(
                        recipe instanceof RandomMachineRecipe
                                ? ((RandomMachineRecipe) recipe).getAllOutput()[i]
                                : recipe.getOutput()[i]));
            }
        }
        return displayRecipes;
    }

    @NotNull default List<MachineRecipe> getMachineRecipes() {
        return MachineRecipeFactory.getInstance().getRecipe(this.getId());
    }

    default void registerRecipe(@NotNull MachineRecipe recipe) {
        this.getMachineRecipes().add(recipe);
    }

    @Deprecated
    default void registerRecipe(int seconds, @NotNull ItemStack[] input, @NotNull ItemStack[] output) {
        this.registerRecipe(new MachineRecipe(seconds, input, output));
    }

    default void registerRecipe(@NotNull ItemStack input, @NotNull ItemStack output) {
        this.registerRecipe(new MachineRecipe(0, new ItemStack[] {input}, new ItemStack[] {output}));
    }

    default void registerRecipe(@NotNull ItemStack input, @NotNull Material output) {
        this.registerRecipe(new MachineRecipe(0, new ItemStack[] {input}, new ItemStack[] {new ItemStack(output)}));
    }

    default void registerRecipe(@NotNull Material input, @NotNull ItemStack output) {
        this.registerRecipe(new MachineRecipe(0, new ItemStack[] {new ItemStack(input)}, new ItemStack[] {output}));
    }

    default void registerRecipe(@NotNull Material input, @NotNull Material output) {
        this.registerRecipe(
                new MachineRecipe(0, new ItemStack[] {new ItemStack(input)}, new ItemStack[] {new ItemStack(output)}));
    }

    default void registerRecipe(@NotNull ItemStack[] input, @NotNull ItemStack[] output) {
        this.registerRecipe(new MachineRecipe(0, input, output));
    }

    @Deprecated
    default void registerRecipe(int seconds, @NotNull ItemStack input, @NotNull ItemStack output) {
        this.registerRecipe(new MachineRecipe(seconds, new ItemStack[] {input}, new ItemStack[] {output}));
    }

    /**
     * register a {@link SlimefunItem} whit it's recipe as a {@link MachineRecipe}.
     * if the slimefun-item's recipe contains liquid-bucket(water bucket, lava bucket e.g.),
     * this method will also register another similar machine-recipe that replace liquid-bucket with {@link ReplaceableCard}
     */
    default void registerRecipeInCard(int seconds, @NotNull SlimefunItem slimefunItem) {
        this.registerRecipeInCard(seconds, slimefunItem.getRecipe(), new ItemStack[] {slimefunItem.getRecipeOutput()});
    }

    default void registerRecipeInCard(
            int seconds, @NotNull ItemStack @NotNull [] input, @NotNull ItemStack @NotNull [] output) {
        List<ItemStack> inputList1 = new ArrayList<>(input.length);
        List<ItemStack> inputList2 = new ArrayList<>(input.length);
        List<ItemStack> outputList1 = new ArrayList<>(output.length);
        List<ItemStack> outputList2 = new ArrayList<>(output.length);
        for (ItemStack item : output) {
            if (!ItemStackUtil.isItemNull(item) && !StackUtils.itemsMatch(item, new CustomItemStack(Material.BUCKET))) {
                outputList1.add(item);
            }
        }
        outputList2.addAll(outputList1);
        int extraRecipe = 0;
        int inputSize = 0;
        for (ItemStack item : input) {
            if (ItemStackUtil.isItemNull(item)) {
                continue;
            }
            inputSize++;
            ReplaceableCard replaceableCard = RecipeUtil.getReplaceableCard(item);
            if (replaceableCard == null) {
                inputList1.add(item);
                inputList2.add(item);
            } else {
                extraRecipe++;
                inputList1.add(item);
                inputList2.add(replaceableCard.getItem());
                if (replaceableCard.getExtraSourceMaterial() != null) {
                    outputList1.add(new ItemStack(replaceableCard.getExtraSourceMaterial()));
                }
            }
        }
        if (extraRecipe != inputSize && !outputList2.isEmpty()) {
            this.registerRecipe(
                    seconds,
                    ItemStackUtil.getNoNullItemArray(ItemStackUtil.calMergeItems(inputList1)),
                    ItemStackUtil.getNoNullItemArray(ItemStackUtil.calMergeItems(outputList1)));
            this.registerRecipe(
                    seconds,
                    ItemStackUtil.getNoNullItemArray(ItemStackUtil.calMergeItems(inputList2)),
                    ItemStackUtil.getNoNullItemArray(ItemStackUtil.calMergeItems(outputList2)));
        } else {
            this.registerRecipe(seconds, ItemStackUtil.calMergeItems(input), ItemStackUtil.calMergeItems(output));
        }
    }

    /**
     * Register a {@link MachineRecipe} that will only be used to show info to player.
     *
     * @param item The item to consume
     */
    default void registerDescriptiveRecipe(@NotNull ItemStack item) {
        this.registerRecipe(new MachineRecipe(0, new ItemStack[] {item}, new ItemStack[] {ItemStackUtil.AIR}));
    }

    default void registerDescriptiveRecipe(@NotNull String name, @NotNull String... lore) {
        this.registerDescriptiveRecipe(new CustomItemStack(Material.BOOK, name, lore));
    }

    default void registerDescriptiveRecipe(@NotNull ItemStack item, @NotNull String name, @NotNull String... lore) {
        this.registerRecipe(0, new CustomItemStack(Material.BOOK, name, lore), item);
    }

    default void registerBorder() {
        this.registerRecipe(0, Icon.BORDER_ICON, Icon.BORDER_ICON);
    }

    default void clearRecipe() {
        this.getMachineRecipes().clear();
    }

    default int getRegisterRecipeDelay() {
        return 1;
    }

    void registerDefaultRecipes();
}
