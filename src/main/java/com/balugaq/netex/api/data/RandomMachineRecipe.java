package com.balugaq.netex.api.data;

import com.ytdd9527.networksexpansion.utils.itemstacks.CompareUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link MachineRecipe} that its output item is random.
 *
 * @author Final_ROOT
 * @since 2.0
 */
public class RandomMachineRecipe extends MachineRecipe {
    @NotNull private RandomOutput[] randomOutputs;

    private int[] weightBeginValues;
    private int weightSum = 0;

    public RandomMachineRecipe(@NotNull MachineRecipe machineRecipe, @NotNull RandomOutput @NotNull [] randomOutputs) {
        super(machineRecipe.getTicks(), machineRecipe.getInput(), new ItemStack[0]);
        this.randomOutputs = randomOutputs;
        this.weightBeginValues = new int[randomOutputs.length];
        for (int i = 0; i < this.randomOutputs.length; i++) {
            this.weightBeginValues[i] = this.weightSum;
            this.weightSum += this.randomOutputs[i].getWeight();
        }
    }

    public RandomMachineRecipe(@NotNull ItemStack[] input, @NotNull RandomOutput @NotNull [] randomOutputs) {
        super(0, input, new ItemStack[0]);
        this.randomOutputs = randomOutputs;
        this.weightBeginValues = new int[randomOutputs.length];
        for (int i = 0; i < this.randomOutputs.length; i++) {
            this.weightBeginValues[i] = this.weightSum;
            this.weightSum += this.randomOutputs[i].getWeight();
        }
    }

    public RandomMachineRecipe(@NotNull ItemStack[] input, @NotNull List<RandomOutput> randomOutputs) {
        super(0, input, new ItemStack[0]);
        this.randomOutputs = randomOutputs.toArray(new RandomOutput[0]);
        this.weightBeginValues = new int[randomOutputs.size()];
        for (int i = 0; i < this.randomOutputs.length; i++) {
            this.weightBeginValues[i] = this.weightSum;
            this.weightSum += this.randomOutputs[i].getWeight();
        }
    }

    public RandomMachineRecipe(@NotNull ItemStack input, @NotNull List<RandomOutput> randomOutputs) {
        super(0, new ItemStack[] {input}, new ItemStack[0]);
        this.randomOutputs = randomOutputs.toArray(new RandomOutput[0]);
        this.weightBeginValues = new int[randomOutputs.size()];
        for (int i = 0; i < this.randomOutputs.length; i++) {
            this.weightBeginValues[i] = this.weightSum;
            this.weightSum += this.randomOutputs[i].getWeight();
        }
    }

    @NotNull @Override
    public ItemStack[] getOutput() {
        int r = (int) (Math.random() * this.weightSum);
        return this.randomOutputs[CompareUtil.getIntSmallFuzzyIndex(this.weightBeginValues, r)].outputItem;
    }

    @NotNull public ItemStack[] getAllOutput() {
        List<ItemStack> itemList = new ArrayList<>();
        for (RandomOutput randomOutput : this.randomOutputs) {
            itemList.addAll(Arrays.asList(randomOutput.outputItem));
        }
        return ItemStackUtil.getItemArray(itemList);
    }

    @NotNull public RandomOutput[] getRandomOutputs() {
        return this.randomOutputs;
    }

    @NotNull public RandomMachineRecipe addRandomOutput(@NotNull RandomOutput @NotNull ... randomOutputs) {
        RandomOutput[] newRandomOutput = new RandomOutput[this.randomOutputs.length + randomOutputs.length];
        int[] newWeightBeginValues = new int[this.weightBeginValues.length + randomOutputs.length];
        System.arraycopy(this.randomOutputs, 0, newRandomOutput, 0, this.randomOutputs.length);
        System.arraycopy(this.weightBeginValues, 0, newWeightBeginValues, 0, this.weightBeginValues.length);
        int newWeightSum = this.weightSum;
        for (int i = 0; i < randomOutputs.length; i++) {
            RandomOutput randomOutput = randomOutputs[i];
            newRandomOutput[i + this.randomOutputs.length] = randomOutputs[i];
            newWeightBeginValues[i + this.weightBeginValues.length] = newWeightSum;
            newWeightSum += randomOutput.weight;
        }

        this.randomOutputs = newRandomOutput;
        this.weightBeginValues = newWeightBeginValues;
        this.weightSum = newWeightSum;
        return this;
    }

    /**
     * @author Final_ROOT
     * @since 2.0
     */
    public static class RandomOutput {
        @NotNull private final ItemStack[] outputItem;

        @Getter
        private final int weight;

        public RandomOutput(@NotNull List<ItemStack> outputItem, int weight) {
            this.outputItem = outputItem.toArray(new ItemStack[0]);
            this.weight = weight;
        }

        public RandomOutput(@NotNull ItemStack[] outputItem, int weight) {
            this.outputItem = outputItem;
            this.weight = weight;
        }

        public RandomOutput(@NotNull ItemStack outputItem, int weight) {
            this.outputItem = new ItemStack[] {outputItem};
            this.weight = weight;
        }

        public RandomOutput(@NotNull Material outputItem, int weight) {
            this.outputItem = new ItemStack[] {new ItemStack(outputItem)};
            this.weight = weight;
        }

        @NotNull public ItemStack[] getOutputItem() {
            return outputItem;
        }
    }
}
