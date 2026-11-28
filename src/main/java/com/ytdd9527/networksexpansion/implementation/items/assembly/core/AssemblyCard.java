package com.ytdd9527.networksexpansion.implementation.items.assembly.core;

import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.api.assembly.RecipeLegitimacy;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.utils.CRC32Utils;
import com.ytdd9527.networksexpansion.utils.SerializeUtils;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.utils.Keys;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AssemblyCard extends SpecialSlimefunItem implements NotPlaceable {

    public static final NamespacedKey ASSEMBLY_CARD_GRID =
            new NamespacedKey(Networks.getInstance(), "ASSEMBLY_CARD_GRID");

    public static final int MAX_ENTRIES = 18;
    public static final int MAX_TOTAL_AMOUNT = MAX_ENTRIES * 64;
    private static final int GRID_SLOTS = 9;
    private static final String GRID_FIELD_PREFIX = "g";
    private static final String ENTRY_SEPARATOR = "\u0001";
    private static final String FIELD_SEPARATOR = "\u0002";

    public AssemblyCard(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
    }

    public static boolean isCard(@Nullable ItemStack itemStack) {
        return itemStack != null
                && itemStack.getType() == Material.PAPER
                && SlimefunItem.getByItem(itemStack) instanceof AssemblyCard;
    }

    public record RecipeEntry(
            @NotNull List<ItemStack> ingredients,
            @NotNull ItemStack output,
            @NotNull ItemStack @NotNull [] grid) {
    }

    @Nullable
    public static RecipeEntry readCard(@NotNull ItemStack card) {
        if (!card.hasItemMeta()) {
            return null;
        }
        var meta = card.getItemMeta();
        if (meta == null) {
            return null;
        }
        ItemStack output = null;
        String outputB64 = PersistentDataAPI.getString(meta, Keys.ASSEMBLY_CARD_OUTPUT);
        if (outputB64 != null && !outputB64.isEmpty()) {
            output = SerializeUtils.string2Object(outputB64);
        }
        if (output == null) {
            return null;
        }
        byte[] raw = PersistentDataAPI.getByteArray(meta, Keys.ASSEMBLY_CARD_INGREDIENTS);
        List<ItemStack> ingredients = decodeIngredients(raw);
        if (ingredients.isEmpty()) {
            return null;
        }
        byte[] gridRaw = PersistentDataAPI.getByteArray(meta, ASSEMBLY_CARD_GRID);
        if (gridRaw == null || gridRaw.length == 0) {
            return null;
        }
        ItemStack[] grid = decodeGrid(gridRaw);
        if (grid == null) {
            return null;
        }
        String recordedHash = PersistentDataAPI.getString(meta, Keys.ASSEMBLY_CARD_HASH);
        if (recordedHash == null || !recordedHash.equals(fingerprint(ingredients, output, grid))) {
            return null;
        }
        return new RecipeEntry(ingredients, output, grid);
    }

    public static boolean writeCard(
            @NotNull ItemStack card,
            @NotNull List<ItemStack> mergedInputs,
            @NotNull ItemStack output,
            @NotNull ItemStack @NotNull [] grid) {
        if (!RecipeLegitimacy.isValid(mergedInputs, output)) {
            return false;
        }
        var meta = card.getItemMeta();
        if (meta == null) {
            return false;
        }
        ItemStack[] cleanGrid = sanitizeGrid(grid);
        if (cleanGrid == null) {
            return false;
        }
        PersistentDataAPI.setString(meta, Keys.ASSEMBLY_CARD_OUTPUT, SerializeUtils.object2String(output));
        PersistentDataAPI.setByteArray(meta, Keys.ASSEMBLY_CARD_INGREDIENTS, encodeIngredients(mergedInputs));
        PersistentDataAPI.setByteArray(meta, ASSEMBLY_CARD_GRID, encodeGrid(cleanGrid));
        PersistentDataAPI.setString(meta, Keys.ASSEMBLY_CARD_HASH, fingerprint(mergedInputs, output, cleanGrid));

        List<String> lore = buildLore(mergedInputs, output);
        meta.setLore(lore);
        card.setItemMeta(meta);
        return true;
    }

    @NotNull
    private static List<String> buildLore(@NotNull List<ItemStack> ingredients, @NotNull ItemStack output) {
        List<String> lore = new ArrayList<>();
        lore.add(Lang.getString(CellnetText.ASSEMBLYCARD_LORE_OUTPUT));
        lore.add(Lang.getString(CellnetText.ASSEMBLYCARD_LORE_ENTRY,
                output.getAmount(),
                ItemStackHelper.getDisplayName(output)));
        lore.add(Lang.getString(CellnetText.ASSEMBLYCARD_LORE_INPUTS));
        for (ItemStack ingredient : ingredients) {
            lore.add(Lang.getString(CellnetText.ASSEMBLYCARD_LORE_ENTRY,
                    ingredient.getAmount(),
                    ItemStackHelper.getDisplayName(ingredient)));
        }
        return lore;
    }

    @Nullable
    private static ItemStack @Nullable [] sanitizeGrid(@Nullable ItemStack @Nullable [] grid) {
        if (grid == null || grid.length != GRID_SLOTS) {
            return null;
        }
        ItemStack[] clean = new ItemStack[GRID_SLOTS];
        boolean any = false;
        for (int i = 0; i < GRID_SLOTS; i++) {
            ItemStack slot = grid[i];
            if (slot == null || slot.getType().isAir()) {
                continue;
            }
            clean[i] = ItemStackUtil.getCleanItem(slot);
            any = true;
        }
        return any ? clean : null;
    }

    @NotNull
    public static List<ItemStack> mergeInputs(@NotNull List<ItemStack> inputs) {
        Map<ItemStack, ItemStack> merged = new LinkedHashMap<>();
        for (ItemStack input : inputs) {
            if (input == null || input.getType().isAir()) {
                continue;
            }
            ItemStack probe = input.clone();
            probe.setAmount(1);
            ItemStack existing = merged.get(probe);
            if (existing == null) {
                merged.put(probe, input.clone());
            } else {
                existing.setAmount(existing.getAmount() + input.getAmount());
            }
        }
        return new ArrayList<>(merged.values());
    }

    @NotNull
    private static byte[] encodeIngredients(@NotNull List<ItemStack> ingredients) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack ingredient = ingredients.get(i);
            if (i > 0) {
                builder.append(ENTRY_SEPARATOR);
            }
            builder.append(SerializeUtils.object2String(ingredient))
                    .append(FIELD_SEPARATOR)
                    .append(ingredient.getAmount());
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    @NotNull
    private static List<ItemStack> decodeIngredients(@Nullable byte[] raw) {
        if (raw == null || raw.length == 0) {
            return List.of();
        }
        String encoded = new String(raw, StandardCharsets.UTF_8);
        List<ItemStack> result = new ArrayList<>();
        for (String part : encoded.split(ENTRY_SEPARATOR, -1)) {
            if (result.size() >= MAX_ENTRIES) {
                return List.of();
            }
            if (part.isEmpty()) {
                continue;
            }
            String[] fields = part.split(FIELD_SEPARATOR, -1);
            if (fields.length != 2) {
                return List.of();
            }
            ItemStack decoded = SerializeUtils.string2Object(fields[0]);
            if (decoded == null) {
                return List.of();
            }
            try {
                long amount = Long.parseLong(fields[1]);
                if (amount <= 0 || amount > MAX_TOTAL_AMOUNT) {
                    return List.of();
                }
                decoded.setAmount((int) Math.min(amount, Integer.MAX_VALUE));
                result.add(decoded);
            } catch (NumberFormatException e) {
                return List.of();
            }
        }
        return result;
    }

    @NotNull
    private static String fingerprint(
            @NotNull List<ItemStack> mergedInputs,
            @NotNull ItemStack output,
            @NotNull ItemStack @NotNull [] grid) {
        List<String> parts = new ArrayList<>(mergedInputs.size());
        for (ItemStack input : mergedInputs) {
            parts.add(SerializeUtils.object2String(input) + FIELD_SEPARATOR + input.getAmount());
        }
        Collections.sort(parts);
        StringBuilder builder = new StringBuilder(SerializeUtils.object2String(output));
        for (String part : parts) {
            builder.append(ENTRY_SEPARATOR).append(part);
        }
        for (int i = 0; i < grid.length && i < GRID_SLOTS; i++) {
            ItemStack slot = grid[i];
            builder.append(ENTRY_SEPARATOR)
                    .append(GRID_FIELD_PREFIX).append(i).append(FIELD_SEPARATOR)
                    .append(slot == null || slot.getType().isAir() ? "" : SerializeUtils.object2String(slot));
        }
        return Integer.toHexString(CRC32Utils.compute(builder.toString()));
    }

    @NotNull
    private static byte[] encodeGrid(@NotNull ItemStack @NotNull [] grid) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < GRID_SLOTS; i++) {
            if (i > 0) {
                builder.append(ENTRY_SEPARATOR);
            }
            ItemStack slot = grid[i];
            if (slot != null && !slot.getType().isAir()) {
                builder.append(SerializeUtils.object2String(slot));
            }
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Nullable
    private static ItemStack @Nullable [] decodeGrid(@Nullable byte[] raw) {
        if (raw == null || raw.length == 0) {
            return null;
        }
        String[] parts = new String(raw, StandardCharsets.UTF_8).split(ENTRY_SEPARATOR, -1);
        if (parts.length != GRID_SLOTS) {
            return null;
        }
        ItemStack[] grid = new ItemStack[GRID_SLOTS];
        for (int i = 0; i < GRID_SLOTS; i++) {
            if (parts[i].isEmpty()) {
                continue;
            }
            ItemStack decoded = SerializeUtils.string2Object(parts[i]);
            if (decoded == null) {
                return null;
            }
            grid[i] = decoded;
        }
        return grid;
    }
}