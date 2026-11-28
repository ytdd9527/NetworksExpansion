package com.ytdd9527.networksexpansion.api.assembly;

import com.balugaq.netex.api.enums.CraftType;
import com.balugaq.netex.utils.Debug;
import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.implementation.items.assembly.core.AssemblyCard;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public final class AssemblyCardRecipes {

    private static final int GRID_SLOTS = 9;

    private AssemblyCardRecipes() {
    }

    public record MatchedRecipe(@NotNull ItemStack output, @NotNull ItemStack @NotNull [] canonicalInputs) {
    }

    @Nullable
    public static MatchedRecipe resolveRecipe(@NotNull Player player, @NotNull ItemStack @NotNull [] grid) {
        MatchedRecipe matched = matchCraftTypeRecipe(grid);
        if (matched != null) {
            SlimefunItem result = SlimefunItem.getByItem(matched.output());
            if (result != null && result.isDisabled()) {
                player.sendMessage(Lang.getString(CellnetText.ASSEMBLYCARD_SAVE_FAILED_DISABLED));
                return null;
            }
            return matched.output().getType() == Material.AIR ? null : matched;
        }
        return matchVanillaRecipe(player, grid);
    }

    @Nullable
    private static MatchedRecipe matchCraftTypeRecipe(@NotNull ItemStack @NotNull [] grid) {
        try {
            for (Map.Entry<CraftType, java.util.Set<Map.Entry<ItemStack[], ItemStack>>> craftEntry : CraftType.map().entrySet()) {
                for (Map.Entry<ItemStack[], ItemStack> recipe : craftEntry.getValue()) {
                    if (craftEntry.getKey().testRecipe(grid, recipe.getKey())) {
                        return new MatchedRecipe(recipe.getValue(), canonicalize(recipe.getKey()));
                    }
                }
            }
        } catch (Exception e) {
            Debug.trace(e, "装配卡配方识别(CraftType)异常，回退原版识别");
        }
        return null;
    }

    @Nullable
    private static MatchedRecipe matchVanillaRecipe(@NotNull Player player, @NotNull ItemStack @NotNull [] grid) {
        ItemStack vanilla = Bukkit.craftItem(grid.clone(), player.getWorld(), player);
        if (vanilla == null || vanilla.getType() == Material.AIR) {
            return null;
        }
        ItemStack[] canonicalInputs = new ItemStack[GRID_SLOTS];
        for (int k = 0; k < GRID_SLOTS && k < grid.length; k++) {
            if (grid[k] != null) {
                canonicalInputs[k] = StackUtils.getAsQuantity(grid[k], 1);
            }
        }
        return new MatchedRecipe(vanilla, canonicalInputs);
    }

    @NotNull
    private static ItemStack[] canonicalize(@NotNull ItemStack @NotNull [] recipeInputs) {
        ItemStack[] canonicalInputs = recipeInputs.clone();
        for (int k = 0; k < canonicalInputs.length; k++) {
            if (canonicalInputs[k] != null) {
                canonicalInputs[k] = ItemStackUtil.getCleanItem(canonicalInputs[k]);
            }
        }
        return canonicalInputs;
    }

    public static boolean passesSaveLimits(@NotNull Player player, @NotNull List<ItemStack> merged, @NotNull ItemStack crafted) {
        ItemStack outOne = crafted.clone();
        outOne.setAmount(1);
        for (ItemStack input : merged) {
            ItemStack inOne = input.clone();
            inOne.setAmount(1);
            if (StackUtils.itemsMatch(outOne, inOne)) {
                player.sendMessage(Lang.getString(CellnetText.ASSEMBLYCARD_SAVE_FAILED_SAME));
                return false;
            }
        }
        int total = 0;
        for (ItemStack item : merged) {
            total += item.getAmount();
        }
        if (total > AssemblyCard.MAX_TOTAL_AMOUNT || merged.size() > AssemblyCard.MAX_ENTRIES) {
            player.sendMessage(Lang.getString(CellnetText.ASSEMBLYCARD_SAVE_FAILED_OVERFLOW,
                    AssemblyCard.MAX_ENTRIES, AssemblyCard.MAX_TOTAL_AMOUNT));
            return false;
        }
        return true;
    }

    @NotNull
    public static ItemStack[] padToSlots(@Nullable ItemStack @Nullable [] canonicalInputs) {
        ItemStack[] padded = new ItemStack[GRID_SLOTS];
        if (canonicalInputs == null) {
            return padded;
        }
        int target = 0;
        for (ItemStack input : canonicalInputs) {
            if (input != null && !input.getType().isAir() && target < GRID_SLOTS) {
                padded[target++] = input.clone();
            }
        }
        return padded;
    }
}
