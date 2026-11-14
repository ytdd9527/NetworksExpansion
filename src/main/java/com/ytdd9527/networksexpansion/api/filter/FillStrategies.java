package com.ytdd9527.networksexpansion.api.filter;

import com.balugaq.netex.api.data.ItemContainer;
import com.balugaq.netex.api.data.StorageUnitData;
import com.ytdd9527.networksexpansion.api.cell.StorageCell;
import com.ytdd9527.networksexpansion.api.cell.VoidCellSupport;
import com.ytdd9527.networksexpansion.api.cell.CellLedger;
import com.ytdd9527.networksexpansion.api.cell.CellLore;
import com.ytdd9527.networksexpansion.api.cell.CellAcceptRules;
import com.ytdd9527.networksexpansion.utils.ItemKey;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.implementation.machines.unit.NetworksDrawer;
import com.ytdd9527.networksexpansion.utils.databases.DataStorage;
import io.github.sefiraat.networks.network.stackcaches.QuantumCache;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.sefiraat.networks.utils.datatypes.PersistentQuantumStorageType;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class FillStrategies {

    private static final int MAX_ITEM_BYTES = 128 * 1024;

    private static final Set<Integer> PENDING_LOADS = ConcurrentHashMap.newKeySet();

    private FillStrategies() {
    }

    public record TemplateFill(@NotNull ItemStack template, long request) {
    }

    public interface Strategy {
        boolean singleType();

        @NotNull
        List<TemplateFill> plan(@NotNull List<ItemStack> templates);

        long apply(@NotNull ItemStack template, long obtained);

        boolean hasContent();

        default void refreshDisplay(@NotNull ItemStack container) {
        }
    }

    @Nullable
    public static Strategy create(@NotNull ItemStack container, @NotNull Location accessor, boolean boxMode) {
        if (container.getAmount() != 1 || oversizeNbt(container)) {
            return null;
        }
        if (isShulker(container.getType())) {
            if (container.getItemMeta() instanceof BlockStateMeta meta
                && meta.getBlockState() instanceof ShulkerBox box) {
                return new ShulkerStrategy(container, meta, box, boxMode);
            }
            return null;
        }
        if (VoidCellSupport.isVoidCell(container)) {
            return null;
        }
        if (StorageCell.isStorageCell(container)) {
            return new CellStrategy(StorageCell.loadCellCache(container, StorageCell.getPerTypeLimit(container)));
        }
        SlimefunItem sfItem = SlimefunItem.getByItem(container);
        if (sfItem instanceof NetworkQuantumStorage storage) {
            return new QuantumStrategy(container, storage, Keys.getQuantumCache(container.getItemMeta()));
        }
        if (sfItem instanceof NetworksDrawer) {
            int id = NetworksDrawer.getBoundId(container);
            if (id == -1) {
                return null;
            }
            StorageUnitData data = DataStorage.getCachedStorageData(id).orElse(null);
            if (data == null || data.isPlaced()) {
                return null;
            }
            return new DrawerStrategy(data, NetworksDrawer.getLock(container), accessor);
        }
        return null;
    }

    @Nullable
    public static String containerError(@NotNull ItemStack item) {
        if (oversizeNbt(item)) {
            return CellnetText.STORAGE_ASSEMBLER_CONTAINER_OVERSIZE;
        }
        if (isShulker(item.getType())) {
            return null;
        }
        if (VoidCellSupport.isVoidCell(item)) {
            return CellnetText.STORAGE_ASSEMBLER_VOID_CELL_REJECTED;
        }
        if (StorageCell.isStorageCell(item)) {
            return null;
        }
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (sfItem instanceof NetworkQuantumStorage) {
            return null;
        }
        if (sfItem instanceof NetworksDrawer) {
            int id = NetworksDrawer.getBoundId(item);
            if (id == -1) {
                return CellnetText.STORAGE_ASSEMBLER_DRAWER_UNBOUND;
            }
            if (DataStorage.isContainerLoaded(id)) {
                PENDING_LOADS.remove(id);
                StorageUnitData data = DataStorage.getCachedStorageData(id).orElse(null);
                if (data != null && data.isPlaced()) {
                    return CellnetText.STORAGE_ASSEMBLER_DRAWER_PLACED;
                }
            }
            return null;
        }
        return CellnetText.STORAGE_ASSEMBLER_CONTAINER_REJECTED;
    }

    public static int unloadedDrawerId(@NotNull ItemStack container) {
        if (!(SlimefunItem.getByItem(container) instanceof NetworksDrawer)) {
            return -1;
        }
        int id = NetworksDrawer.getBoundId(container);
        if (id == -1) {
            return -1;
        }
        if (DataStorage.isContainerLoaded(id)) {
            PENDING_LOADS.remove(id);
            return -1;
        }
        return id;
    }

    public static void requestDrawerLoad(int id) {
        if (PENDING_LOADS.add(id)) {
            DataStorage.requestStorageData(id);
        }
    }

    private static boolean isShulker(@NotNull Material material) {
        return material.name().endsWith("SHULKER_BOX");
    }

    private static boolean oversizeNbt(@NotNull ItemStack item) {
        try {
            return item.serializeAsBytes().length > MAX_ITEM_BYTES;
        } catch (Exception e) {
            return true;
        }
    }

    private static final class CellStrategy implements Strategy {

        private final CellLedger cache;

        private CellStrategy(@NotNull CellLedger cache) {
            this.cache = cache;
        }

        @Override
        public boolean singleType() {
            return false;
        }

        @Override
        public @NotNull List<TemplateFill> plan(@NotNull List<ItemStack> templates) {
            long maxTypes = Math.min(cache.getCurrentPerTypeLimit(), cache.getMaxUnits());
            int usedTypes = cache.getAllItems().keySet().size();
            long freeTypes = Math.max(0L, maxTypes - usedTypes);
            long perType = cache.getPerTypeLimit();
            List<ItemStack> whitelist = cache.isWhitelistEnabled() ? cache.getWhitelist() : null;
            List<TemplateFill> plan = new ArrayList<>();
            for (ItemStack template : templates) {
                if (oversizeNbt(template)
                    || CellAcceptRules.isUsedCell(template)
                    || CellAcceptRules.isFilledContainer(template)) {
                    continue;
                }
                ItemKey key = new ItemKey(template);
                long existing = cache.getAmount(key);
                long capacity;
                if (existing > 0L) {
                    capacity = Math.max(0L, perType - existing);
                } else if (freeTypes > 0L && (whitelist == null || inWhitelist(template, whitelist))) {
                    capacity = perType;
                    freeTypes--;
                } else {
                    capacity = 0L;
                }
                if (capacity > 0L) {
                    plan.add(new TemplateFill(template, Math.min(capacity, Integer.MAX_VALUE)));
                }
            }
            return plan;
        }

        private boolean inWhitelist(@NotNull ItemStack template, @NotNull List<ItemStack> whitelist) {
            for (ItemStack item : whitelist) {
                if (StackUtils.itemsMatch(item, template)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public long apply(@NotNull ItemStack template, long obtained) {
            return cache.pushItemLong(new ItemKey(template), obtained);
        }

        @Override
        public boolean hasContent() {
            return cache.getStored() > 0L;
        }

        @Override
        public void refreshDisplay(@NotNull ItemStack container) {
            CellLore.applySpecLore(container, cache.getPerTypeLimit(), cache.getCurrentPerTypeLimit());
        }
    }

    private static final class DrawerStrategy implements Strategy {

        private final StorageUnitData data;
        private final boolean locked;
        private final Location accessor;

        private DrawerStrategy(@NotNull StorageUnitData data, boolean locked, @NotNull Location accessor) {
            this.data = data;
            this.locked = locked;
            this.accessor = accessor;
        }

        @Override
        public boolean singleType() {
            return false;
        }

        @Override
        public @NotNull List<TemplateFill> plan(@NotNull List<ItemStack> templates) {
            int eachMax = data.getSizeType().getEachMaxSize();
            int maxTypes = data.getSizeType().getMaxItemCount();
            List<ItemContainer> stored = data.getStoredItems();
            int freeTypes = Math.max(0, maxTypes - stored.size());
            List<TemplateFill> plan = new ArrayList<>();
            for (ItemStack template : templates) {
                if (oversizeNbt(template)) {
                    continue;
                }
                long capacity = 0L;
                boolean matched = false;
                for (ItemContainer each : stored) {
                    if (each.isSimilar(template)) {
                        capacity = Math.max(0L, eachMax - each.getAmount());
                        matched = true;
                        break;
                    }
                }
                if (!matched && !locked && freeTypes > 0) {
                    capacity = eachMax;
                    freeTypes--;
                }
                if (capacity > 0L) {
                    plan.add(new TemplateFill(template, Math.min(capacity, Integer.MAX_VALUE)));
                }
            }
            return plan;
        }

        @Override
        public long apply(@NotNull ItemStack template, long obtained) {
            ItemStack unit = template.clone();
            unit.setAmount(1);
            return data.addStoredItem(
                accessor, unit, (int) Math.min(obtained, Integer.MAX_VALUE), locked, !locked);
        }

        @Override
        public boolean hasContent() {
            return !data.getStoredItemsDirectly().isEmpty();
        }
    }

    private static final class QuantumStrategy implements Strategy {

        private final ItemStack container;
        private final NetworkQuantumStorage storage;
        private QuantumCache cache;
        private boolean loreInitialized;

        private QuantumStrategy(
                @NotNull ItemStack container,
                @NotNull NetworkQuantumStorage storage,
                @Nullable QuantumCache cache) {
            this.container = container;
            this.storage = storage;
            this.cache = cache;
            this.loreInitialized = cache != null;
        }

        @Override
        public boolean singleType() {
            return true;
        }

        @Override
        public @NotNull List<TemplateFill> plan(@NotNull List<ItemStack> templates) {
            if (cache != null && cache.getItemStack() != null) {
                for (ItemStack template : templates) {
                    if (oversizeNbt(template)) {
                        continue;
                    }
                    if (StackUtils.itemsMatch(cache.getItemStack(), template)) {
                        long capacity = Math.max(0L, cache.getLimitLong() - cache.getAmountLong());
                        if (capacity > 0L) {
                            return List.of(new TemplateFill(template, Math.min(capacity, Integer.MAX_VALUE)));
                        }
                        return List.of();
                    }
                }
                return List.of();
            }
            long capacity = cache != null ? Math.max(0L, cache.getLimitLong() - cache.getAmountLong())
                : storage.getMaxAmount();
            for (ItemStack template : templates) {
                if (!oversizeNbt(template)) {
                    return List.of(new TemplateFill(template, Math.min(capacity, Integer.MAX_VALUE)));
                }
            }
            return List.of();
        }

        @Override
        public long apply(@NotNull ItemStack template, long obtained) {
            if (cache == null || cache.getItemStack() == null) {
                ItemStack stored = template.clone();
                stored.setAmount(1);
                cache = new QuantumCache(stored, 0L, storage.getMaxAmount(), false, storage.supportsCustomMaxAmount());
            }
            int amountIn = (int) Math.min(obtained, Integer.MAX_VALUE);
            int leftover = cache.increaseAmount(amountIn);
            long accepted = amountIn - leftover;
            if (accepted > 0) {
                writeBack();
            }
            return accepted;
        }

        @Override
        public boolean hasContent() {
            return cache != null && cache.getAmountLong() > 0L;
        }

        private void writeBack() {
            ItemMeta meta = container.getItemMeta();
            DataTypeMethods.setCustom(meta, Keys.QUANTUM_STORAGE_INSTANCE, PersistentQuantumStorageType.TYPE, cache);
            if (loreInitialized) {
                cache.updateMetaLore(meta);
            } else {
                cache.addMetaLore(meta);
                loreInitialized = true;
            }
            container.setItemMeta(meta);
        }
    }

    private static final class ShulkerStrategy implements Strategy {

        private final ItemStack container;
        private final BlockStateMeta meta;
        private final ShulkerBox box;
        private final ItemStack[] contents;
        private final boolean boxMode;
        private List<ItemStack> planTemplates;
        private final List<List<Integer>> planSlots = new ArrayList<>();

        private ShulkerStrategy(
                @NotNull ItemStack container,
                @NotNull BlockStateMeta meta,
                @NotNull ShulkerBox box,
                boolean boxMode) {
            this.container = container;
            this.meta = meta;
            this.box = box;
            this.boxMode = boxMode;
            this.contents = box.getInventory().getStorageContents();
        }

        @Override
        public boolean singleType() {
            return !boxMode;
        }

        @Override
        public @NotNull List<TemplateFill> plan(@NotNull List<ItemStack> templates) {
            this.planTemplates = List.copyOf(templates);
            this.planSlots.clear();
            return boxMode ? planBanded(templates) : planSingleType(templates);
        }

        @NotNull
        private List<TemplateFill> planSingleType(@NotNull List<ItemStack> templates) {
            ItemStack dedicated = null;
            for (ItemStack slot : contents) {
                if (slot != null && !slot.getType().isAir()) {
                    dedicated = slot;
                    break;
                }
            }
            ItemStack chosen = null;
            if (dedicated != null) {
                for (ItemStack t : templates) {
                    if (!oversizeNbt(t) && t.isSimilar(dedicated)) {
                        chosen = t;
                        break;
                    }
                }
            } else {
                for (ItemStack t : templates) {
                    if (!oversizeNbt(t)) {
                        chosen = t;
                        break;
                    }
                }
            }
            if (chosen == null) {
                return List.of();
            }
            int max = Math.max(1, chosen.getMaxStackSize());
            long capacity = 0L;
            List<Integer> slots = new ArrayList<>();
            for (int i = 0; i < contents.length; i++) {
                ItemStack slot = contents[i];
                if (slot == null || slot.getType().isAir()) {
                    capacity += max;
                    slots.add(i);
                } else if (slot.isSimilar(chosen) && slot.getAmount() < max) {
                    capacity += max - slot.getAmount();
                    slots.add(i);
                }
            }
            if (capacity <= 0L) {
                return List.of();
            }
            planSlots.add(slots);
            return List.of(new TemplateFill(chosen, Math.min(capacity, Integer.MAX_VALUE)));
        }

        @NotNull
        private List<TemplateFill> planBanded(@NotNull List<ItemStack> templates) {
            List<TemplateFill> plan = new ArrayList<>();
            for (int ti = 0; ti < templates.size(); ti++) {
                ItemStack template = templates.get(ti);
                if (oversizeNbt(template)) {
                    planSlots.add(new ArrayList<>());
                    continue;
                }
                int max = Math.max(1, template.getMaxStackSize());
                long capacity = 0L;
                List<Integer> slots = new ArrayList<>();
                for (int i = 0; i < contents.length; i++) {
                    if (i % templates.size() != ti) {
                        continue;
                    }
                    ItemStack slot = contents[i];
                    if (slot == null || slot.getType().isAir()) {
                        capacity += max;
                        slots.add(i);
                    } else if (slot.isSimilar(template) && slot.getAmount() < max) {
                        capacity += max - slot.getAmount();
                        slots.add(i);
                    }
                }
                planSlots.add(slots);
                if (capacity > 0L) {
                    plan.add(new TemplateFill(template, Math.min(capacity, Integer.MAX_VALUE)));
                }
            }
            return plan;
        }

        @Override
        public long apply(@NotNull ItemStack template, long obtained) {
            int max = Math.max(1, template.getMaxStackSize());
            long left = obtained;
            int ti = boxMode ? planTemplates.indexOf(template) : 0;
            if (ti < 0 || ti >= planSlots.size()) {
                return 0L;
            }
            for (int slotIndex : planSlots.get(ti)) {
                if (left <= 0L) {
                    break;
                }
                ItemStack slot = contents[slotIndex];
                if (slot == null || slot.getType().isAir()) {
                    int put = (int) Math.min(left, max);
                    contents[slotIndex] = template.asQuantity(put);
                    left -= put;
                } else if (slot.isSimilar(template) && slot.getAmount() < max) {
                    int put = (int) Math.min(left, max - slot.getAmount());
                    contents[slotIndex] = slot.asQuantity(slot.getAmount() + put);
                    left -= put;
                }
            }
            long accepted = obtained - left;
            if (accepted > 0) {
                box.getInventory().setStorageContents(contents);
                meta.setBlockState(box);
                container.setItemMeta(meta);
            }
            return accepted;
        }

        @Override
        public boolean hasContent() {
            for (ItemStack slot : contents) {
                if (slot != null && !slot.getType().isAir()) {
                    return true;
                }
            }
            return false;
        }
    }
}
