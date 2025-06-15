package com.balugaq.netex.api.data;

import com.balugaq.netex.api.interfaces.SuperRecipeHandler;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Data
@Getter
@ToString
public class SuperRecipe {
    private final ItemStack[] input;
    private final ItemStack[] output;
    private final int consumeEnergy;
    private final @Nullable SuperRecipeHandler handler;
    private final boolean isShaped;

    public SuperRecipe(boolean isShaped, ItemStack[] input, ItemStack[] output, int consumeEnergy) {
        this.isShaped = isShaped;
        this.input = input;
        this.output = output;
        this.consumeEnergy = consumeEnergy;
        this.handler = null;
    }

    public SuperRecipe(boolean isShaped, ItemStack[] input, ItemStack[] output) {
        this.isShaped = isShaped;
        this.input = input;
        this.output = output;
        this.consumeEnergy = 0;
        this.handler = null;
    }

    public SuperRecipe(boolean isShaped, ItemStack[] input, ItemStack output, int consumeEnergy) {
        this.isShaped = isShaped;
        this.input = input;
        this.output = new ItemStack[] {output};
        this.consumeEnergy = consumeEnergy;
        this.handler = null;
    }

    public SuperRecipe(boolean isShaped, ItemStack[] input, ItemStack output) {
        this.isShaped = isShaped;
        this.input = input;
        this.output = new ItemStack[] {output};
        this.consumeEnergy = 0;
        this.handler = null;
    }

    public SuperRecipe(
            boolean isShaped,
            ItemStack[] input,
            ItemStack[] output,
            int consumeEnergy,
            @Nullable SuperRecipeHandler handler) {
        this.isShaped = isShaped;
        this.input = input;
        this.output = output;
        this.consumeEnergy = consumeEnergy;
        this.handler = handler;
    }

    public SuperRecipe(boolean isShaped, ItemStack[] input, ItemStack[] output, @Nullable SuperRecipeHandler handler) {
        this.isShaped = isShaped;
        this.input = input;
        this.output = output;
        this.consumeEnergy = 0;
        this.handler = handler;
    }

    public SuperRecipe(
            boolean isShaped,
            ItemStack[] input,
            ItemStack output,
            int consumeEnergy,
            @Nullable SuperRecipeHandler handler) {
        this.isShaped = isShaped;
        this.input = input;
        this.output = new ItemStack[] {output};
        this.consumeEnergy = consumeEnergy;
        this.handler = handler;
    }

    public SuperRecipe(boolean isShaped, ItemStack[] input, ItemStack output, @Nullable SuperRecipeHandler handler) {
        this.isShaped = isShaped;
        this.input = input;
        this.output = new ItemStack[] {output};
        this.consumeEnergy = 0;
        this.handler = handler;
    }

    public SuperRecipe(
            boolean isShaped,
            ItemStack input,
            ItemStack[] output,
            int consumeEnergy,
            @Nullable SuperRecipeHandler handler) {
        this.isShaped = isShaped;
        this.input = new ItemStack[] {input};
        this.output = output;
        this.consumeEnergy = consumeEnergy;
        this.handler = handler;
    }

    public SuperRecipe(boolean isShaped, ItemStack input, ItemStack[] output, @Nullable SuperRecipeHandler handler) {
        this.isShaped = isShaped;
        this.input = new ItemStack[] {input};
        this.output = output;
        this.consumeEnergy = 0;
        this.handler = handler;
    }

    public SuperRecipe(
            boolean isShaped,
            ItemStack input,
            ItemStack output,
            int consumeEnergy,
            @Nullable SuperRecipeHandler handler) {
        this.isShaped = isShaped;
        this.input = new ItemStack[] {input};
        this.output = new ItemStack[] {output};
        this.consumeEnergy = consumeEnergy;
        this.handler = handler;
    }

    public SuperRecipe(boolean isShaped, ItemStack input, ItemStack output, @Nullable SuperRecipeHandler handler) {
        this.isShaped = isShaped;
        this.input = new ItemStack[] {input};
        this.output = new ItemStack[] {output};
        this.consumeEnergy = 0;
        this.handler = handler;
    }
}
