package com.ytdd9527.networksexpansion.api.cell;

import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.NetworksVersionedMaterial;
import com.ytdd9527.networksexpansion.utils.NumberFormat;
import com.ytdd9527.networksexpansion.implementation.machines.manual.ExpansionWorkbench;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.slimefun.NetworkSlimefunItems;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public enum CellTier {

    T1("1", "64", 64L, Material.MUSIC_DISC_11, NetworkSlimefunItems.OPTIC_GLASS::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_0),
    T2("2", "256", 256L, Material.MUSIC_DISC_13, NetworkSlimefunItems.OPTIC_CABLE::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_9),
    T3("3", "1K", 1_024L, Material.MUSIC_DISC_CAT, NetworkSlimefunItems.OPTIC_STAR::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_10),
    T4("4", "4K", 4_096L, Material.MUSIC_DISC_BLOCKS, NetworkSlimefunItems.OPTIC_STAR::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_1),
    T5("5", "32K", 32_768L, Material.MUSIC_DISC_CHIRP, NetworkSlimefunItems.RADIOACTIVE_OPTIC_STAR::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_2),
    T6("6", "262K", 262_144L, Material.MUSIC_DISC_FAR, NetworkSlimefunItems.RADIOACTIVE_OPTIC_STAR::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_3),
    T7("7", "2M", 2_097_152L, Material.MUSIC_DISC_MALL, NetworkSlimefunItems.SYNTHETIC_EMERALD_SHARD::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_4),
    T8("8", "16M", 16_777_216L, Material.MUSIC_DISC_MELLOHI, NetworkSlimefunItems.SYNTHETIC_EMERALD_SHARD::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_5),
    T9("9", "134M", 134_217_728L, Material.MUSIC_DISC_STAL, NetworkSlimefunItems.SIMPLE_NANOBOTS::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_6),
    T10("10", "1B", 1_073_741_824L, Material.MUSIC_DISC_STRAD, NetworkSlimefunItems.SIMPLE_NANOBOTS::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_7),
    T11("11", "2.1B", 2_147_483_647L, Material.MUSIC_DISC_WARD, NetworkSlimefunItems.ADVANCED_NANOBOTS::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_8),
    T12("12", "34.4B", 34_359_738_352L, NetworksVersionedMaterial.MUSIC_DISC_CREATOR, NetworkSlimefunItems.ADVANCED_NANOBOTS::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_11),
    T13("13", "549.8B", 549_755_813_888L, Material.MUSIC_DISC_PIGSTEP, NetworkSlimefunItems.SHRINKING_BASE::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_12),
    T14("14", "8.8T", 8_796_093_022_208L, Material.MUSIC_DISC_5, NetworkSlimefunItems.INTERDIMENSIONAL_PRESENCE::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_13),
    T15("15", "140.7T", 140_737_488_355_328L, Material.MUSIC_DISC_RELIC, NetworkSlimefunItems.INTERDIMENSIONAL_PRESENCE::getItem, NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_14);

    private static final CellTier[] VALUES = values();
    private static final Map<Long, CellTier> BY_AMOUNT = new HashMap<>();

    static {
        for (CellTier tier : VALUES) {
            BY_AMOUNT.put(tier.perTypeLimit, tier);
        }
    }

    private final String id;
    private final String label;
    private final long perTypeLimit;
    private final Material icon;
    private final Supplier<ItemStack> ringMaterial;
    private final NetworkQuantumStorage upgradeMaterial;

    private SlimefunItemStack cachedStack;
    private ItemStack[] cachedRecipe;
    private StorageCell instance;

    CellTier(
        @NotNull String suffix,
        @NotNull String label,
        long perTypeLimit,
        @NotNull Material icon,
        @NotNull Supplier<ItemStack> ringMaterial,
        @NotNull NetworkQuantumStorage upgradeMaterial) {
        this.id = "NTW_EXPANSION_CELL_" + suffix;
        this.label = label;
        this.perTypeLimit = perTypeLimit;
        this.icon = icon;
        this.ringMaterial = ringMaterial;
        this.upgradeMaterial = upgradeMaterial;
    }

    @NotNull
    public String id() {
        return id;
    }

    @NotNull
    public String label() {
        return label;
    }

    public long perTypeLimit() {
        return perTypeLimit;
    }

    @NotNull
    public Material icon() {
        return icon;
    }

    @NotNull
    public NetworkQuantumStorage upgradeMaterial() {
        return upgradeMaterial;
    }

    @NotNull
    public synchronized SlimefunItemStack stack() {
        if (cachedStack == null) {
            SlimefunItemStack template = Lang.getItem("NTW_EXPANSION_CELL", icon);
            String plainName = MessageFormat.format(template.getDisplayName(), label);
            List<String> lore = new ArrayList<>();
            if (template.getLore() != null) {
                for (String line : template.getLore()) {
                    lore.add(MessageFormat.format(line, NumberFormat.formatNumber(perTypeLimit)));
                }
            }
            cachedStack = buildStack(this.id, icon, plainName,
                lore.toArray(new String[0]));
        }
        return cachedStack;
    }

    public ItemStack @NotNull [] recipe() {
        if (cachedRecipe == null) {
            ItemStack ring = ringMaterial.get();
            ItemStack center = this == T1
                ? NetworkSlimefunItems.NETWORK_CELL.getItem()
                : VALUES[ordinal() - 1].stack();
            cachedRecipe = new ItemStack[]{ring, ring, ring, ring, center, ring, ring, ring, ring};
        }
        return cachedRecipe;
    }

    @NotNull
    public StorageCell register(@NotNull ItemGroup group) {
        StorageCell cell = new StorageCell(group, stack(), ExpansionWorkbench.TYPE, recipe(), perTypeLimit);
        cell.register(Networks.getInstance());
        instance = cell;
        return cell;
    }

    @Nullable
    public StorageCell instance() {
        return instance;
    }

    @Nullable
    public static CellTier fromAmount(long maxAmount) {
        return BY_AMOUNT.get(maxAmount);
    }

    private static SlimefunItemStack buildStack(String id, Material icon, String plainName, String[] lore) {
        String stripped = ChatColor.stripColor(plainName);
        if (stripped == null) {
            stripped = plainName;
        }
        int split = stripped.indexOf(' ');
        String base = split < 0 ? stripped : stripped.substring(0, split);
        String tier = split < 0 ? "" : stripped.substring(split + 1);
        String name = TextUtil.colorPseudorandomString(base)
            + (tier.isEmpty() ? "" : Theme.PASSIVE.getColor() + " \u00b7 " + Theme.AQUA.getColor() + tier);
        return new SlimefunItemStack(id, icon, name, lore);
    }

    @Nullable
    public static NetworkQuantumStorage upgradeMaterialOf(long perTypeLimit) {
        CellTier tier = fromAmount(perTypeLimit);
        return tier == null ? null : tier.upgradeMaterial();
    }

    @Nullable
    public static NetworkQuantumStorage storageForAmount(long amount) {
        for (CellTier tier : VALUES) {
            if (tier.perTypeLimit >= amount) {
                return tier.upgradeMaterial;
            }
        }
        return null;
    }

    public static final class Unlimited {

        public static final String ID = "NTW_EXPANSION_CELL_INFINITY";
        public static final long PER_TYPE_LIMIT = Long.MAX_VALUE;

        private static SlimefunItemStack cachedStack;
        private static StorageCell instance;

        private Unlimited() {
        }

        @NotNull
        public static synchronized SlimefunItemStack stack() {
            if (cachedStack == null) {
                SlimefunItemStack template = Lang.getItem(ID, ItemStackUtil.getPreEnchantedItemStack(Material.MUSIC_DISC_OTHERSIDE));
                List<String> lore = template.getLore();
                cachedStack = buildStack(ID, Material.MUSIC_DISC_OTHERSIDE, template.getDisplayName(),
                    lore == null ? new String[0] : lore.toArray(new String[0]));
            }
            return cachedStack;
        }

        public static ItemStack @NotNull [] recipe() {
            ItemStack corner = NetworkSlimefunItems.RADIOACTIVE_OPTIC_STAR.getItem();
            ItemStack edge = NetworkSlimefunItems.INTERDIMENSIONAL_PRESENCE.getItem();
            return new ItemStack[]{corner, edge, corner, edge, T15.stack(), edge, corner, edge, corner};
        }

        @NotNull
        public static StorageCell register(@NotNull ItemGroup group) {
            StorageCell cell = new StorageCell(group, stack(), ExpansionWorkbench.TYPE, recipe(), PER_TYPE_LIMIT);
            cell.register(Networks.getInstance());
            instance = cell;
            return cell;
        }

        @Nullable
        public static StorageCell instance() {
            return instance;
        }
    }
}
