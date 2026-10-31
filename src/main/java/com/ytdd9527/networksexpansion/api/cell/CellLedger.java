package com.ytdd9527.networksexpansion.api.cell;

import com.ytdd9527.networksexpansion.api.storage.CellStorageDatabase;
import com.ytdd9527.networksexpansion.utils.ItemHashMap;
import com.ytdd9527.networksexpansion.utils.ItemKey;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.utils.StackUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CellLedger {

    private static volatile long cachedMaxUnitCount = 0L;

    private static final Map<UUID, CellLedger> activeCellCaches = new ConcurrentHashMap<>();

    @Getter
    private final UUID uuid;
    @Getter
    private final long perTypeLimit;
    private final ItemHashMap<Long> storage = new ItemHashMap<>();
    private final List<ItemStack> whitelist = new ArrayList<>();

    private Map<ItemStack, Long> allItemsView = null;

    private long totalStored;
    @Getter
    @Setter
    private volatile long currentPerTypeLimit;
    private boolean whitelistEnabled;
    private volatile String customName;

    private final Set<ItemKey> reservedUnits = ConcurrentHashMap.newKeySet();

    private final Set<ItemKey> voidExcessUnits = ConcurrentHashMap.newKeySet();

    @Setter
    private volatile boolean unlimited;

    @Getter
    @Setter
    private volatile boolean voidCell;

    public CellLedger(@NotNull UUID uuid, long perTypeLimit, long currentPerTypeLimit) {
        this.uuid = uuid;
        this.perTypeLimit = perTypeLimit;
        this.currentPerTypeLimit = currentPerTypeLimit;
        this.totalStored = 0;
    }

    @NotNull
    public static CellLedger getOrCreate(@NotNull UUID uuid, long perTypeLimit, long currentPerTypeLimit) {
        return activeCellCaches.computeIfAbsent(uuid, k -> new CellLedger(uuid, perTypeLimit, currentPerTypeLimit));
    }

    @NotNull
    public static Map<UUID, CellLedger> getActiveCaches() {
        return activeCellCaches;
    }

    public static long getMaxUnitCount() {
        long cached = cachedMaxUnitCount;
        if (cached <= 0) {
            cached = Math.max(1L, Math.min(64L, Networks.getConfigManager().getCellnetMaxItemTypes()));
            cachedMaxUnitCount = cached;
        }
        return cached;
    }

    public long getMaxUnits() {
        if (unlimited) {
            return Long.MAX_VALUE;
        }
        return Math.min(perTypeLimit, getMaxUnitCount());
    }

    public static boolean isUnlimitedPerType(long perTypeLimit) {
        return perTypeLimit >= Long.MAX_VALUE / 2;
    }

    public synchronized long getStored() {
        return totalStored;
    }

    @Nullable
    public synchronized ItemStack takeItem(@NotNull ItemStack itemStack, long amount) {
        return takeItem(new ItemKey(itemStack), amount);
    }

    @NotNull
    public synchronized List<CellEntry> getStoredItems() {
        List<CellEntry> result = new ArrayList<>(storage.size());
        for (Map.Entry<ItemKey, Long> entry : storage.keyEntrySet()) {
            if (entry.getValue() <= 0 && !reservedUnits.contains(entry.getKey())) {
                continue;
            }
            result.add(new CellEntry(entry.getKey().getItemStack(), entry.getValue()));
        }
        return result;
    }

    /** 实际占用的种类数（含保留的空单元），作为转运判满依据，避免依赖只增不减的计数器 */
    public synchronized int typeCount() {
        return storage.size();
    }

    @NotNull
    public synchronized Map<ItemStack, Long> getAllItems() {
        if (allItemsView == null) {
            Map<ItemStack, Long> result = new HashMap<>();
            for (Map.Entry<ItemKey, Long> entry : storage.keyEntrySet()) {
                result.put(entry.getKey().getItemStack(), entry.getValue());
            }
            allItemsView = result;
        }
        return new HashMap<>(allItemsView);
    }

    private void invalidateItemsView() {
        allItemsView = null;
    }

    public synchronized void accumulateInto(@NotNull ItemHashMap<Long> target, @Nullable Map<ItemKey, List<UUID>> index) {
        for (Map.Entry<ItemKey, Long> entry : storage.keyEntrySet()) {
            if (entry.getValue() <= 0 && !reservedUnits.contains(entry.getKey())) {
                continue;
            }
            ItemKey key = entry.getKey();
            Long existing = target.getKey(key);
            target.putKey(key, (existing != null ? existing : 0L) + entry.getValue());
            if (index != null) {
                index.computeIfAbsent(key, k -> new ArrayList<>()).add(uuid);
            }
        }
    }

    public synchronized void loadItemSilently(@NotNull ItemStack sample, long amount) {
        if (amount <= 0 || sample.getType().isAir()) {
            return;
        }
        ItemKey key = new ItemKey(sample);
        long existing = storage.getOrDefault(key, 0L);
        storage.putKey(key, existing + amount);
        totalStored += amount;
        invalidateItemsView();
    }

    public synchronized void loadMetaSilently(boolean enabled, @NotNull List<ItemStack> whitelistItems) {
        this.whitelistEnabled = enabled;
        this.whitelist.clear();
        for (ItemStack item : whitelistItems) {
            ItemStack normalized = item.clone();
            normalized.setAmount(1);
            this.whitelist.add(normalized);
        }
    }

    public synchronized boolean isWhitelistEnabled() {
        return whitelistEnabled;
    }

    public synchronized void updateWhitelist(boolean whitelistEnabled, @NotNull List<ItemStack> items) {
        this.whitelistEnabled = whitelistEnabled;
        whitelist.clear();
        for (ItemStack item : items) {
            ItemStack normalized = item.clone();
            normalized.setAmount(1);
            whitelist.add(normalized);
        }
        saveWhitelist();
    }

    @NotNull
    public synchronized List<ItemStack> getWhitelist() {
        List<ItemStack> result = new ArrayList<>(whitelist.size());
        for (ItemStack item : whitelist) {
            result.add(item.clone());
        }
        return result;
    }

    @Nullable
    public String getCustomName() {
        return customName;
    }

    public void setCustomName(@Nullable String customName) {
        this.customName = customName;
    }

    private void markItemDirty(@NotNull ItemKey key, long finalAmount) {
        CellStorageDatabase db = Networks.getCellStorageDatabase();
        if (db != null) {
            db.getStorageController().markDirty(uuid, key, finalAmount);
        }
    }

    private void saveWhitelist() {
        CellStorageDatabase db = Networks.getCellStorageDatabase();
        if (db != null) {
            db.getStorageController().saveWhitelist(uuid, isWhitelistEnabled(), getWhitelist());
        }
    }

    private boolean inWhitelist(@NotNull ItemStack sample) {
        for (ItemStack template : whitelist) {
            if (StackUtils.itemsMatch(template, sample)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean canReceiveItem(@NotNull ItemKey key) {
        Long existing = storage.getKey(key);
        if (existing != null) {
            if (voidExcessUnits.contains(key)) {
                return true;
            }
            return existing < perTypeLimit;
        }
        if (whitelistEnabled && !inWhitelist(key.getItemStack())) {
            return false;
        }
        return storage.size() < Math.min(currentPerTypeLimit, getMaxUnits());
    }

    public synchronized long receiveCapacity(@NotNull ItemKey key) {
        Long existing = storage.getKey(key);
        if (existing == null || existing <= 0) {
            return 0L;
        }
        return Math.max(0L, perTypeLimit - existing);
    }

    public synchronized int pushItem(@NotNull ItemKey key, int amount) {
        return (int) Math.min(pushItemLong(key, amount), Integer.MAX_VALUE);
    }

    public synchronized long pushItemLong(@NotNull ItemKey key, long amount) {
        long existing = storage.getOrDefault(key, 0L);
        if (existing > 0) {
            if (voidExcessUnits.contains(key)) {
                topUp(key, existing, amount);
                return amount;
            }
            return topUp(key, existing, amount);
        }

        if (key.hasMeta()) {
            ItemStack sample = key.getItemStack();
            if (key.isOversized()
                || CellAcceptRules.isUsedCell(sample)
                || CellAcceptRules.isFilledContainer(sample)) {
                return 0;
            }
        }
        if (reservedUnits.contains(key)) {
            return topUp(key, existing, amount);
        }
        if (whitelistEnabled && !inWhitelist(key.getItemStack())) {
            return 0;
        }
        if (storage.size() >= Math.min(currentPerTypeLimit, getMaxUnits())) {
            return 0;
        }

        return topUp(key, existing, amount);
    }

    private long topUp(@NotNull ItemKey key, long existing, long amount) {
        long toAdd = Math.min(amount, Math.max(0L, perTypeLimit - existing));
        if (toAdd <= 0) {
            return 0;
        }
        storage.putKey(key, existing + toAdd);
        totalStored += toAdd;
        markItemDirty(key, existing + toAdd);
        invalidateItemsView();
        return toAdd;
    }

    @Nullable
    public synchronized ItemStack takeItem(@NotNull ItemKey key, long amount) {
        long take = takeItemInternal(key, amount);
        if (take <= 0) {
            return null;
        }
        ItemStack result = key.getItemStack();
        result.setAmount((int) Math.min(take, Integer.MAX_VALUE));
        return result;
    }

    public synchronized long takeItemAmount(@NotNull ItemKey key, long amount) {
        return takeItemInternal(key, amount);
    }

    private long takeItemInternal(@NotNull ItemKey key, long amount) {
        Long existing = storage.getKey(key);
        if (existing == null || existing <= 0 || amount <= 0) {
            return 0L;
        }
        long take = Math.min(amount, existing);
        long remaining = existing - take;
        if (remaining > 0) {
            storage.putKey(key, remaining);
        } else if (reservedUnits.contains(key)) {
            storage.putKey(key, 0L);
        } else {
            storage.removeKey(key);
        }
        totalStored -= take;
        markItemDirty(key, remaining);
        invalidateItemsView();
        return take;
    }

    public synchronized boolean contains(@NotNull ItemKey key, long amount) {
        Long existing = storage.getKey(key);
        return existing != null && existing >= amount;
    }

    public synchronized long getAmount(@NotNull ItemKey key) {
        Long existing = storage.getKey(key);
        return existing == null ? 0 : existing;
    }

    public synchronized boolean isReserved(@NotNull ItemKey key) {
        return reservedUnits.contains(key);
    }

    public synchronized void setReserved(@NotNull ItemKey key, boolean reserved) {
        if (reserved) {
            if (CellAcceptRules.isNbtOversized(key.getItemStack())) {
                return;
            }
            reservedUnits.add(key);
            if (!storage.containsKey(key)) {
                storage.putKey(key, 0L);
            }
        } else {
            reservedUnits.remove(key);
            Long existing = storage.getKey(key);
            if (existing != null && existing <= 0) {
                storage.removeKey(key);
            }
        }
        invalidateItemsView();
    }

    public synchronized void restoreReserved(@NotNull ItemKey key) {
        reservedUnits.add(key);
        if (!storage.containsKey(key)) {
            storage.putKey(key, 0L);
        }
        invalidateItemsView();
    }

    @NotNull
    public synchronized List<ItemStack> getReservedTemplates() {
        List<ItemStack> result = new ArrayList<>(reservedUnits.size());
        for (ItemKey key : reservedUnits) {
            result.add(key.getItemStack());
        }
        return result;
    }

    public synchronized boolean isVoidExcessUnit(@NotNull ItemKey key) {
        return voidExcessUnits.contains(key);
    }

    public synchronized void setVoidExcessUnit(@NotNull ItemKey key, boolean voidExcess) {
        if (voidExcess) {
            if (CellAcceptRules.isNbtOversized(key.getItemStack())) {
                return;
            }
            voidExcessUnits.add(key);
        } else {
            voidExcessUnits.remove(key);
        }
    }

    public synchronized void restoreVoidExcessUnit(@NotNull ItemKey key) {
        voidExcessUnits.add(key);
    }

    public synchronized void migrateLegacyVoidExcess() {
        for (Map.Entry<ItemKey, Long> entry : storage.keyEntrySet()) {
            voidExcessUnits.add(entry.getKey());
        }
    }

    @NotNull
    public synchronized List<ItemStack> getVoidExcessTemplates() {
        List<ItemStack> result = new ArrayList<>(voidExcessUnits.size());
        for (ItemKey key : voidExcessUnits) {
            result.add(key.getItemStack());
        }
        return result;
    }

    public static class CellEntry {
        public final ItemStack sample;
        public final long amount;

        public CellEntry(@NotNull ItemStack sample, long amount) {
            this.sample = ItemStackUtil.getCleanItem(sample);
            this.amount = amount;
        }
    }
}
