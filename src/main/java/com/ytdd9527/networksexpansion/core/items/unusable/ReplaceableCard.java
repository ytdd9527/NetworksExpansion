package com.ytdd9527.networksexpansion.core.items.unusable;

import com.balugaq.netex.api.interfaces.RecipeItem;
import com.balugaq.netex.api.interfaces.UnCopiableItem;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * targetMaterial = this.getItem() + sourceMaterial
 * example:
 * WATER_BUCKET = WATER_CARD + BUCKET
 * DIAMOND_PICKAXE = DIAMOND_PICKAXE_CARD + NULL
 *
 * @author Final_ROOT
 * @since 2.4
 */
public class ReplaceableCard extends UnusableSlimefunItem implements RecipeItem, UnCopiableItem {
    private static final Map<Material, ReplaceableCard> MATERIAL_SLIMEFUN_ITEM_MAP = new EnumMap<>(Material.class);

    @Getter
    private final @NotNull Material targetMaterial;

    @Nullable private final Material extraSourceMaterial;

    public ReplaceableCard(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            @NotNull Material targetMaterial,
            @Nullable Material extraSourceMaterial) {
        super(itemGroup, item, recipeType, recipe);
        if (MATERIAL_SLIMEFUN_ITEM_MAP.containsKey(targetMaterial)) {
            throw new IllegalArgumentException("duplicated material while registering " + this.getId());
        }
        MATERIAL_SLIMEFUN_ITEM_MAP.put(targetMaterial, this);
        this.targetMaterial = targetMaterial;
        this.extraSourceMaterial = extraSourceMaterial;
    }

    @Nullable public static ReplaceableCard getByMaterial(@NotNull Material material) {
        return MATERIAL_SLIMEFUN_ITEM_MAP.get(material);
    }

    @Nullable public Material getExtraSourceMaterial() {
        return extraSourceMaterial;
    }

    @Override
    public void registerDefaultRecipes() {

        ItemStack[] inputItemStacks = new ItemStack[] {new ItemStack(this.targetMaterial)};

        ItemStack[] outputItemStacks = new ItemStack[this.extraSourceMaterial == null ? 1 : 2];
        outputItemStacks[0] = this.getItem();
        if (outputItemStacks.length == 2) {
            outputItemStacks[1] = new ItemStack(this.extraSourceMaterial);
        }
        this.registerRecipe(inputItemStacks, outputItemStacks);
    }
}
