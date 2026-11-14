package com.balugaq.netex.api.data;

import com.balugaq.netex.api.enums.StorageUnitType;
import com.ytdd9527.networksexpansion.implementation.machines.unit.NetworksDrawer;
import com.ytdd9527.networksexpansion.utils.databases.DataStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.utils.StackUtils;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"DuplicatedCode", "MathClampMigration"})
@ToString
@NullMarked
public class StorageUnitData {
    public static final Map<Location, Map<Integer, Integer>>
        /* from -> to slot -> Access times */ observingAccessHistory = new ConcurrentHashMap<>(),
        /* from -> to slot -> Cache miss times */ persistentAccessHistory = new ConcurrentHashMap<>();

    @Getter
    private final int id;

    @Getter
    private final OfflinePlayer owner;

    private final ConcurrentHashMap<Integer, ItemContainer> storedItems;

    @Getter
    private boolean isPlaced;

    @Getter
    private StorageUnitType sizeType;

    @Getter
    private Location lastLocation;

    public StorageUnitData(
        int id, String ownerUUID, StorageUnitType sizeType, boolean isPlaced, Location lastLocation) {
        this(
            id,
            Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID)),
            sizeType,
            isPlaced,
            lastLocation,
            new ConcurrentHashMap<>());
    }

    public StorageUnitData(
        int id,
        String ownerUUID,
        StorageUnitType sizeType,
        boolean isPlaced,
        Location lastLocation,
        ConcurrentHashMap<Integer, ItemContainer> storedItems) {
        this(id, Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID)), sizeType, isPlaced, lastLocation, storedItems);
    }

    public StorageUnitData(
        int id,
        OfflinePlayer owner,
        StorageUnitType sizeType,
        boolean isPlaced,
        Location lastLocation,
        ConcurrentHashMap<Integer, ItemContainer> storedItems) {
        this.id = id;
        this.owner = owner;
        this.sizeType = sizeType;
        this.isPlaced = isPlaced;
        this.lastLocation = lastLocation;
        this.storedItems = storedItems; //!! DO NOT USE `new` keyword to create a new ConcurrentHashMap, this will cause a deep copy, leading to data lose!!
    }

    public static void addPersistentAccessHistory(@Nullable Location accessor, int accessLocation) {
        if (accessor == null) return;
        var locations = persistentAccessHistory.getOrDefault(accessor, new ConcurrentHashMap<>());
        locations.put(accessLocation, 0);
        persistentAccessHistory.put(accessor, locations);
    }

    public static void addCacheMiss(@Nullable Location accessor, int accessLocation) {
        if (accessor == null) return;
        var locations = persistentAccessHistory.getOrDefault(accessor, new ConcurrentHashMap<>());
        int value = locations.getOrDefault(accessLocation, 0) + 1;
        if (value > NetworkRoot.cacheMissThreshold) {
            removePersistentAccessHistory(accessor, accessLocation);
            return;
        }
        locations.put(accessLocation, value);
        persistentAccessHistory.put(accessor, locations);
    }

    public static void minusCacheMiss(Location accessor, int accessLocation) {
        var locations = persistentAccessHistory.getOrDefault(accessor, new ConcurrentHashMap<>());
        int value = Math.max(locations.getOrDefault(accessLocation, 0) - 1, 0);
        locations.put(accessLocation, value);
    }

    @Nullable
    public static Map<Integer, Integer> getPersistentAccessHistory(@Nullable Location accessor) {
        if (accessor == null) return null;
        return persistentAccessHistory.getOrDefault(accessor, new ConcurrentHashMap<>());
    }

    public static void removePersistentAccessHistory(Location accessor) {
        persistentAccessHistory.remove(accessor);
    }

    public static void removePersistentAccessHistory(@Nullable Location accessor, int accessLocation) {
        if (accessor == null) return;
        var locations = persistentAccessHistory.getOrDefault(accessor, new ConcurrentHashMap<>());
        locations.remove(accessLocation);
        persistentAccessHistory.put(accessor, locations);
    }

    public static void addCountObservingAccessHistory(@Nullable Location accessor, int accessLocation) {
        if (accessor == null) return;
        var locations = observingAccessHistory.getOrDefault(accessor, new HashMap<>());
        int count = locations.getOrDefault(accessLocation, 0);
        if (count >= NetworkRoot.persistentThreshold) {
            removeCountObservingAccessHistory(accessor, accessLocation);
            addPersistentAccessHistory(accessor, accessLocation);
            return;
        }
        locations.put(accessLocation, count + 1);
        observingAccessHistory.put(accessor, locations);
    }

    public static Map<Integer, Integer> getCountObservingAccessHistory(Location location) {
        return observingAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
    }

    public static void removeCountObservingAccessHistory(Location location) {
        observingAccessHistory.remove(location);
    }

    public static void removeCountObservingAccessHistory(Location location, Integer accessLocation) {
        var locations = observingAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
        locations.remove(accessLocation);
        observingAccessHistory.put(location, locations);
    }

    public static boolean isBlacklisted(ItemStack itemStack) {
        return StackUtils.isBlacklisted(itemStack);
    }

    public synchronized void setPlaced(boolean isPlaced) {
        if (this.isPlaced != isPlaced) {
            this.isPlaced = isPlaced;
            DataStorage.setContainerStatus(id, isPlaced);
        }
    }

    public synchronized void setSizeType(StorageUnitType sizeType) {
        if (this.sizeType != sizeType) {
            this.sizeType = sizeType;
            DataStorage.setContainerSizeType(id, sizeType);
        }
    }

    public synchronized void setLastLocation(Location lastLocation) {
        if (this.lastLocation != lastLocation) {
            this.lastLocation = lastLocation;
            DataStorage.setContainerLocation(id, lastLocation);
        }
    }

    public void removeItem(int itemId) {
        if (storedItems.remove(itemId) != null) {
            DataStorage.deleteStoredItem(id, itemId);
        }
    }

    public synchronized void setItemAmount(int itemId, int amount) {
        if (amount < 0) {
            // Directly remove
            removeItem(itemId);
            return;
        }
        ItemContainer container = storedItems.get(itemId);
        if (container != null) {
            container.setAmount(amount);
            DataStorage.setStoredAmount(id, itemId, amount);
        }
    }

    public synchronized void removeAmount(int itemId, int amount) {
        ItemContainer container = storedItems.get(itemId);
        if (container != null) {
            container.removeAmount(amount);
            if (container.getAmount() <= 0 && !NetworksDrawer.isLocked(getLastLocation())) {
                removeItem(itemId);
                return;
            }
            DataStorage.setStoredAmount(id, itemId, container.getAmount());
        }
    }

    public int getStoredTypeCount() {
        return storedItems.size();
    }

    public int getTotalAmount() {
        int re = 0;
        for (ItemContainer each : storedItems.values()) {
            re += each.getAmount();
        }
        return re;
    }

    public long getTotalAmountLong() {
        long re = 0;
        for (ItemContainer each : storedItems.values()) {
            re += each.getAmount();
        }
        return re;
    }

    public List<ItemContainer> copyStoredItems() {
        return new ArrayList<>(storedItems.values());
    }

    public Collection<ItemContainer> getStoredItemsDirectly() {
        return storedItems.values();
    }

    public Map<Integer, ItemContainer> copyStoredItemsMap() {
        return new ConcurrentHashMap<>(storedItems);
    }

    public Map<Integer, ItemContainer> getStoredItemsMap() {
        return storedItems;
    }

    @Nullable
    public ItemStack requestItem(@Nullable Location accessor, ItemRequest itemRequest) {
        return requestItem(accessor, itemRequest, true);
    }

    @Nullable
    public synchronized ItemStack requestItem(@Nullable Location accessor, ItemRequest itemRequest, boolean contentLocked) {
        ItemStack item = itemRequest.getItemStack();
        if (item == null) {
            return null;
        }

        int amount = itemRequest.getAmount();

        List<ItemContainer> stored = getStoredItems();

        Map<Integer, Integer> m = getPersistentAccessHistory(accessor);
        if (m != null) {
            // Netex - Cache start
            List<Integer> misses = new ArrayList<>();
            // Netex - Cache end
            for (Integer i : m.keySet()) {
                // Netex - Cache start
                if (i >= stored.size()) {
                    removePersistentAccessHistory(accessor, i);
                    continue;
                }
                // Netex - Cache end

                ItemContainer itemContainer = stored.get(i);
                int containerAmount = itemContainer.getAmount();
                if (StackUtils.itemsMatch(itemRequest, itemContainer.getSampleDirectly())) {
                    int take = Math.min(amount, containerAmount);
                    if (take <= 0) {
                        if (!contentLocked) {
                            removeItem(itemContainer.getId());
                            removePersistentAccessHistory(accessor, i);
                        }
                        break;
                    }
                    itemContainer.removeAmount(take);

                    DataStorage.setStoredAmount(id, itemContainer.getId(), itemContainer.getAmount());
                    // Netex - Cache start
                    minusCacheMiss(accessor, i);
                    // Netex - Cache end

                    ItemStack clone = item.clone();
                    clone.setAmount(take);
                    return clone;
                } else {
                    // Netex - Cache start
                    misses.add(i);
                    // Netex - Cache end
                }
            }

            // Netex - Cache start
            for (int miss : misses) {
                minusCacheMiss(accessor, miss);
            }
            // Netex - Cache end
        }

        for (int i = 0; i < stored.size(); i++) {
            ItemContainer itemContainer = stored.get(i);
            int containerAmount = itemContainer.getAmount();
            if (StackUtils.itemsMatch(itemRequest, itemContainer.getSampleDirectly())) {
                int take = Math.min(amount, containerAmount);
                if (take <= 0) {
                    if (!contentLocked) {
                        removeItem(itemContainer.getId());
                    }
                    break;
                }

                itemContainer.removeAmount(take);

                // Netex - Cache start
                addCountObservingAccessHistory(accessor, i);
                // Netex - Cache end
                DataStorage.setStoredAmount(id, itemContainer.getId(), itemContainer.getAmount());

                ItemStack clone = item.clone();
                clone.setAmount(take);
                return clone;
            }
        }
        return null;
    }

    public synchronized void depositItemStacks(
        @Nullable Location accessor, Map<ItemStack, Long> itemsToDeposit, boolean contentLocked) {
        for (Map.Entry<ItemStack, Long> entry : itemsToDeposit.entrySet()) {
            if (entry.getValue() > Integer.MAX_VALUE) {
                // rollback to MAX_VALUE
                long before = entry.getValue();
                ItemStack item = StackUtils.getAsQuantity(entry.getKey(), Integer.MAX_VALUE);
                depositItemStack(accessor, item, contentLocked);
                long leftover = item.getAmount();
                entry.setValue(before - Integer.MAX_VALUE + leftover);
            } else {
                ItemStack item = StackUtils.getAsQuantity(entry.getKey(), Math.toIntExact(entry.getValue()));
                depositItemStack(accessor, item, contentLocked);
                long rest = item.getAmount();
                entry.setValue(rest);
            }
        }
    }

    public synchronized void depositItemStack(
        @Nullable Location accessor, Map.Entry<ItemStack, Integer> entry, boolean contentLocked) {
        ItemStack item = StackUtils.getAsQuantity(entry.getKey(), entry.getValue());
        depositItemStack(accessor, item, contentLocked);
        int leftover = item.getAmount();
        entry.setValue(leftover);
    }

    public void depositItemStack(
        @Nullable Location accessor, Map<ItemStack, Integer> itemsToDeposit, boolean contentLocked) {
        for (Map.Entry<ItemStack, Integer> entry : itemsToDeposit.entrySet()) {
            depositItemStack(accessor, entry, contentLocked);
        }
    }

    public void depositItemStack(
        @Nullable Location accessor, @Nullable ItemStack [] itemsToDeposit, boolean contentLocked) {
        for (ItemStack item : itemsToDeposit) {
            depositItemStack(accessor, item, contentLocked);
        }
    }

    public synchronized void depositItemStack(
        @Nullable Location accessor, @Nullable ItemStack itemsToDeposit, boolean contentLocked, boolean force) {
        if (itemsToDeposit == null || isBlacklisted(itemsToDeposit)) {
            return;
        }
        int actualAdded = addStoredItem(accessor, itemsToDeposit, itemsToDeposit.getAmount(), contentLocked, force);
        if (actualAdded > 0) {
            itemsToDeposit.setAmount(itemsToDeposit.getAmount() - actualAdded);
        }
    }

    public void depositItemStack(@Nullable Location accessor, @Nullable ItemStack item, boolean contentLocked) {
        depositItemStack(accessor, item, contentLocked, false);
    }

    /**
     * Add item to unit
     *
     * @param accessor: accessor
     * @param item:     item will be added
     * @param amount:   amount will be added
     * @return the amount actual added
     */
    public synchronized int addStoredItem(
        @Nullable Location accessor, ItemStack item, int amount, boolean contentLocked, boolean force) {
        int add = 0;
        boolean isVoidExcess = NetworksDrawer.isVoidExcess(getLastLocation());
        List<ItemContainer> stored = getStoredItems();

        Map<Integer, Integer> m = getPersistentAccessHistory(accessor);
        if (m != null) {
            List<Integer> misses = new ArrayList<>();
            for (Integer i : m.keySet()) {
                // Netex - Cache start
                if (i >= stored.size()) {
                    removePersistentAccessHistory(accessor, i);
                    continue;
                }
                // Netex - Cache end

                ItemContainer each = stored.get(i);
                if (each.isSimilar(item)) {
                    // Found existing one, add amount
                    int raw = sizeType.getEachMaxSize() - each.getAmount();
                    if (raw < 0) {
                        // If over-full, no more add and roll back to normal amount
                        each.setAmount(sizeType.getEachMaxSize());
                        return 0;
                    }
                    add = Math.max(0, Math.min(amount, raw));
                    if (isVoidExcess) {
                        if (add > 0) {
                            each.addAmount(add);
                            DataStorage.setStoredAmount(id, each.getId(), each.getAmount());
                        } else {
                            item.setAmount(0);
                        }
                    } else {
                        each.addAmount(add);
                        DataStorage.setStoredAmount(id, each.getId(), each.getAmount());
                    }

                    // Netex - Cache start
                    minusCacheMiss(accessor, i);
                    // Netex - Cache end
                    return add;
                } else {
                    // Netex - Cache start
                    misses.add(i);
                    // Netex - Cache end
                }
            }

            for (int i : misses) {
                addCacheMiss(accessor, i);
            }
        }

        for (int i = 0; i < stored.size(); i++) {
            ItemContainer each = stored.get(i);
            if (each.isSimilar(item)) {
                // Found existing one, add amount
                int raw = sizeType.getEachMaxSize() - each.getAmount();
                if (raw < 0) {
                    // If over-full, no more add and roll back to normal amount
                    each.setAmount(sizeType.getEachMaxSize());
                    return 0;
                }
                add = Math.max(0, Math.min(amount, raw));
                if (isVoidExcess) {
                    if (add > 0) {
                        each.addAmount(add);
                        DataStorage.setStoredAmount(id, each.getId(), each.getAmount());
                    } else {
                        item.setAmount(0);
                    }
                } else {
                    each.addAmount(add);
                    DataStorage.setStoredAmount(id, each.getId(), each.getAmount());
                }

                // Netex - Cache start
                addCountObservingAccessHistory(accessor, i);
                // Netex - Cache end
                return add;
            }
        }

        // isforce?
        if (!force) {
            // If in content locked mode, no new input allowed
            if (contentLocked || NetworksDrawer.isLocked(getLastLocation())) return 0;
        }
        // Not found, new one
        if (storedItems.size() < sizeType.getMaxItemCount()) {
            add = Math.min(amount, sizeType.getEachMaxSize());
            int itemId = DataStorage.getItemId(item);
            storedItems.put(itemId, new ItemContainer(itemId, item, add));
            DataStorage.addStoredItem(id, itemId, add);
            return add;
        }
        return add;
    }

    /**
     * Add item to unit, the amount will be the item stack amount
     *
     * @param accessor: accessor
     * @param item:     item will be added
     * @return the amount actual added
     */
    public int addStoredItem(@Nullable Location accessor, ItemStack item, boolean contentLocked) {
        return addStoredItem(accessor, item, item.getAmount(), contentLocked, false);
    }

    public int addStoredItem(@Nullable Location accessor, ItemStack item, boolean contentLocked, boolean force) {
        return addStoredItem(accessor, item, item.getAmount(), contentLocked, force);
    }

    public int addStoredItem(@Nullable Location accessor, ItemStack item, int amount, boolean contentLocked) {
        return addStoredItem(accessor, item, amount, contentLocked, false);
    }

    // ============= Fallback methods =============

    @Deprecated
    public int addStoredItem0(
        @Nullable Location accessor, ItemStack item, int amount, boolean contentLocked, boolean force) {
        return addStoredItem(accessor, item, amount, contentLocked, force);
    }

    @Deprecated
    public int addStoredItem0(Location accessor, ItemStack item, boolean contentLocked) {
        return addStoredItem(accessor, item, contentLocked);
    }

    @Deprecated
    public int addStoredItem0(Location accessor, ItemStack item, boolean contentLocked, boolean force) {
        return addStoredItem(accessor, item, contentLocked, force);
    }

    @Deprecated
    public int addStoredItem0(Location accessor, ItemStack item, int amount, boolean contentLocked) {
        return addStoredItem(accessor, item, amount, contentLocked);
    }

    @Deprecated
    public int addStoredItem(ItemStack item, boolean contentLocked) {
        return addStoredItem(null, item, item.getAmount(), contentLocked, false);
    }

    @Deprecated
    public int addStoredItem(ItemStack item, boolean contentLocked, boolean force) {
        return addStoredItem(null, item, item.getAmount(), contentLocked, force);
    }

    @Deprecated
    public int addStoredItem(ItemStack item, int amount, boolean contentLocked) {
        return addStoredItem(null, item, amount, contentLocked, false);
    }

    @Deprecated
    public int addStoredItem0(ItemStack item, boolean contentLocked) {
        return addStoredItem(null, item, contentLocked);
    }

    @Deprecated
    public int addStoredItem0(ItemStack item, boolean contentLocked, boolean force) {
        return addStoredItem(null, item, contentLocked, force);
    }

    @Deprecated
    public int addStoredItem0(ItemStack item, int amount, boolean contentLocked) {
        return addStoredItem(null, item, amount, contentLocked);
    }

    @Deprecated
    @Nullable
    public ItemStack requestItem(ItemRequest itemRequest) {
        return requestItem(null, itemRequest);
    }

    @Deprecated
    @Nullable
    public ItemStack requestItem0(@Nullable Location accessor, ItemRequest itemRequest) {
        return requestItem(accessor, itemRequest);
    }

    @Deprecated
    @Nullable
    public ItemStack requestItem0(@Nullable Location accessor, ItemRequest itemRequest, boolean contentLocked) {
        return requestItem(accessor, itemRequest, contentLocked);
    }

    @Deprecated
    public void depositItemStacks0(
        @Nullable Location accessor, Map<ItemStack, Long> itemsToDeposit, boolean contentLocked) {
        depositItemStacks(accessor, itemsToDeposit, contentLocked);
    }

    @Deprecated
    public void depositItemStack0(
        @Nullable Location accessor, Map.Entry<ItemStack, Integer> entry, boolean contentLocked) {
        depositItemStack(accessor, entry, contentLocked);
    }

    @Deprecated
    public void depositItemStack0(
        @Nullable Location accessor, Map<ItemStack, Integer> itemsToDeposit, boolean contentLocked) {
        depositItemStack(accessor, itemsToDeposit, contentLocked);
    }

    @Deprecated
    public void depositItemStack0(
        @Nullable Location accessor, ItemStack[] itemsToDeposit, boolean contentLocked) {
        depositItemStack(accessor, itemsToDeposit, contentLocked);
    }

    @Deprecated
    public void depositItemStack0(
        @Nullable Location accessor, @Nullable ItemStack itemsToDeposit, boolean contentLocked, boolean force) {
        depositItemStack(accessor, itemsToDeposit, contentLocked, force);
    }

    @Deprecated
    public void depositItemStack0(@Nullable Location accessor, ItemStack item, boolean contentLocked) {
        depositItemStack(accessor, item, contentLocked);
    }

    @Deprecated
    public void depositItemStack0(@Nullable ItemStack item, boolean contentLocked) {
        depositItemStack(null, item, contentLocked, false);
    }

    @Deprecated
    public void depositItemStacks(
        Map<ItemStack, Long> itemsToDeposit, boolean contentLocked) {
        depositItemStacks(null, itemsToDeposit, contentLocked);
    }

    @Deprecated
    public void depositItemStack(
        Map.Entry<ItemStack, Integer> entry, boolean contentLocked) {
        depositItemStack(null, entry, contentLocked);
    }

    @Deprecated
    public void depositItemStack(
        Map<ItemStack, Integer> itemsToDeposit, boolean contentLocked) {
        depositItemStack(null, itemsToDeposit, contentLocked);
    }

    @Deprecated
    public void depositItemStack(
        @Nullable ItemStack [] itemsToDeposit, boolean contentLocked) {
        depositItemStack(null, itemsToDeposit, contentLocked);
    }

    @Deprecated
    public void depositItemStack(
        @Nullable ItemStack itemsToDeposit, boolean contentLocked, boolean force) {
        depositItemStack(null, itemsToDeposit, contentLocked, force);
    }

    @Deprecated
    public void depositItemStack(@Nullable ItemStack item, boolean contentLocked) {
        depositItemStack(null, item, contentLocked, false);
    }

    @Deprecated
    public List<ItemContainer> getStoredItems() {
        return copyStoredItems();
    }
}