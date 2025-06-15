package com.balugaq.netex.api.data;

import com.balugaq.netex.api.enums.StorageUnitType;
import com.ytdd9527.networksexpansion.implementation.machines.unit.NetworksDrawer;
import com.ytdd9527.networksexpansion.utils.databases.DataStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.utils.StackUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ToString
public class StorageUnitData {
    public static final Map<Location, Map<Integer, Integer /* Access times */>> observingAccessHistory =
            new ConcurrentHashMap<>();
    public static final Map<Location, Map<Integer, Integer /* Cache miss times */>> persistentAccessHistory =
            new ConcurrentHashMap<>();

    @Getter
    private final int id;

    @Getter
    private final OfflinePlayer owner;

    private final Map<Integer, ItemContainer> storedItems;
    private boolean isPlaced;

    @Getter
    private StorageUnitType sizeType;

    @Getter
    private Location lastLocation;

    public StorageUnitData(
            int id, @NotNull String ownerUUID, StorageUnitType sizeType, boolean isPlaced, Location lastLocation) {
        this(
                id,
                Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID)),
                sizeType,
                isPlaced,
                lastLocation,
                new HashMap<>());
    }

    public StorageUnitData(
            int id,
            @NotNull String ownerUUID,
            StorageUnitType sizeType,
            boolean isPlaced,
            Location lastLocation,
            Map<Integer, ItemContainer> storedItems) {
        this(id, Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID)), sizeType, isPlaced, lastLocation, storedItems);
    }

    public StorageUnitData(
            int id,
            OfflinePlayer owner,
            StorageUnitType sizeType,
            boolean isPlaced,
            Location lastLocation,
            Map<Integer, ItemContainer> storedItems) {
        this.id = id;
        this.owner = owner;
        this.sizeType = sizeType;
        this.isPlaced = isPlaced;
        this.lastLocation = lastLocation;
        this.storedItems = storedItems;
    }

    public static void addPersistentAccessHistory(Location location, Integer accessLocation) {
        Map<Integer, Integer> locations = persistentAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
        locations.put(accessLocation, 0);
        persistentAccessHistory.put(location, locations);
    }

    public static void addCacheMiss(Location location, Integer accessLocation) {
        Map<Integer, Integer> locations = persistentAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
        int value = locations.getOrDefault(accessLocation, 0) + 1;
        if (value > NetworkRoot.cacheMissThreshold) {
            removePersistentAccessHistory(location, accessLocation);
            return;
        }
        locations.put(accessLocation, value);
        persistentAccessHistory.put(location, locations);
    }

    public static void minusCacheMiss(Location location, Integer accessLocation) {
        Map<Integer, Integer> locations = persistentAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
        int value = Math.max(locations.getOrDefault(accessLocation, 0) - 1, 0);
        locations.put(accessLocation, value);
    }

    public static Map<Integer, Integer> getPersistentAccessHistory(Location location) {
        return persistentAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
    }

    public static void removePersistentAccessHistory(Location location) {
        persistentAccessHistory.remove(location);
    }

    public static void removePersistentAccessHistory(Location location, Integer accessLocation) {
        Map<Integer, Integer> locations = persistentAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
        locations.remove(accessLocation);
        persistentAccessHistory.put(location, locations);
    }

    public static void addCountObservingAccessHistory(Location location, Integer accessLocation) {
        Map<Integer, Integer> locations = observingAccessHistory.getOrDefault(location, new HashMap<>());
        Integer count = locations.getOrDefault(accessLocation, 0);
        if (count >= NetworkRoot.persistentThreshold) {
            removeCountObservingAccessHistory(location, accessLocation);
            addPersistentAccessHistory(location, accessLocation);
            return;
        }
        locations.put(accessLocation, count + 1);
        observingAccessHistory.put(location, locations);
    }

    public static Map<Integer, Integer> getCountObservingAccessHistory(Location location) {
        return observingAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
    }

    public static void removeCountObservingAccessHistory(Location location) {
        observingAccessHistory.remove(location);
    }

    public static void removeCountObservingAccessHistory(Location location, Integer accessLocation) {
        Map<Integer, Integer> locations = observingAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
        locations.remove(accessLocation);
        observingAccessHistory.put(location, locations);
    }

    public static boolean isBlacklisted(@NotNull ItemStack itemStack) {
        // if item is air, it's blacklisted
        if (itemStack.getType() == Material.AIR) {
            return true;
        }
        // if item has invalid durability, it's blacklisted
        if (itemStack.getType().getMaxDurability() < 0) {
            return true;
        }
        // if item is a shulker box, it's blacklisted
        if (Tag.SHULKER_BOXES.isTagged(itemStack.getType())) {
            return true;
        }
        // if item is a bundle, it's blacklisted
        return itemStack.getType() == Material.BUNDLE;
    }

    /**
     * Add item to unit, the amount will be the item stack amount
     *
     * @param item: item will be added
     * @return the amount actual added
     */
    @Deprecated
    public int addStoredItem(@NotNull ItemStack item, boolean contentLocked) {
        return addStoredItem(item, item.getAmount(), contentLocked, false);
    }

    @Deprecated
    public int addStoredItem(@NotNull ItemStack item, boolean contentLocked, boolean force) {
        return addStoredItem(item, item.getAmount(), contentLocked, force);
    }

    @Deprecated
    public int addStoredItem(@NotNull ItemStack item, int amount, boolean contentLocked) {
        return addStoredItem(item, amount, contentLocked, false);
    }

    /**
     * Add item to unit
     *
     * @param item:   item will be added
     * @param amount: amount will be added
     * @return the amount actual added
     */
    @Deprecated
    public int addStoredItem(@NotNull ItemStack item, int amount, boolean contentLocked, boolean force) {
        int add = 0;
        boolean isVoidExcess = NetworksDrawer.isVoidExcess(getLastLocation());
        for (ItemContainer each : storedItems.values()) {
            if (each.isSimilar(item)) {
                // Found existing one, add amount
                int raw = sizeType.getEachMaxSize() - each.getAmount();
                if (raw < 0) {
                    // If super-full, no more add and roll back to normal amount
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
                        return add;
                    }
                } else {
                    each.addAmount(add);
                    DataStorage.setStoredAmount(id, each.getId(), each.getAmount());
                }
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

    public boolean isPlaced() {
        return isPlaced;
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

    public void setItemAmount(int itemId, int amount) {
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

    public void removeAmount(int itemId, int amount) {
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

    @Deprecated
    public @NotNull List<ItemContainer> getStoredItems() {
        return copyStoredItems();
    }

    public @NotNull List<ItemContainer> copyStoredItems() {
        return new ArrayList<>(storedItems.values());
    }

    public @NotNull Collection<ItemContainer> getStoredItemsDirectly() {
        return storedItems.values();
    }

    public @NotNull Map<Integer, ItemContainer> copyStoredItemsMap() {
        return new HashMap<>(storedItems);
    }

    public Map<Integer, ItemContainer> getStoredItemsMap() {
        return storedItems;
    }

    @Deprecated
    @Nullable public ItemStack requestItem(@NotNull ItemRequest itemRequest) {
        ItemStack item = itemRequest.getItemStack();
        if (item == null) {
            return null;
        }

        int amount = itemRequest.getAmount();
        for (ItemContainer itemContainer : getStoredItems()) {
            int containerAmount = itemContainer.getAmount();
            if (StackUtils.itemsMatch(itemContainer.getSampleDirectly(), item)) {
                int take = Math.min(amount, containerAmount);
                if (take <= 0) {
                    break;
                }
                itemContainer.removeAmount(take);
                DataStorage.setStoredAmount(id, itemContainer.getId(), itemContainer.getAmount());
                ItemStack clone = item.clone();
                clone.setAmount(take);
                return clone;
            }
        }
        return null;
    }

    @Deprecated
    public void depositItemStacks(@NotNull Map<ItemStack, Long> itemsToDeposit, boolean contentLocked) {
        for (Map.Entry<ItemStack, Long> entry : itemsToDeposit.entrySet()) {
            if (entry.getValue() > Integer.MAX_VALUE) {
                // rollback to MAX_VALUE
                long before = entry.getValue();
                ItemStack item = StackUtils.getAsQuantity(entry.getKey(), Integer.MAX_VALUE);
                depositItemStack(item, contentLocked);
                long leftover = item.getAmount();
                entry.setValue(before - Integer.MAX_VALUE + leftover);
            } else {
                ItemStack item = StackUtils.getAsQuantity(entry.getKey(), Math.toIntExact(entry.getValue()));
                depositItemStack(item, contentLocked);
                long rest = item.getAmount();
                entry.setValue(rest);
            }
        }
    }

    @Deprecated
    public void depositItemStack(@NotNull Map.Entry<ItemStack, Integer> entry, boolean contentLocked) {
        ItemStack item = StackUtils.getAsQuantity(entry.getKey(), entry.getValue());
        depositItemStack(item, contentLocked);
        int leftover = item.getAmount();
        entry.setValue(leftover);
    }

    @Deprecated
    public void depositItemStack(@NotNull Map<ItemStack, Integer> itemsToDeposit, boolean contentLocked) {
        for (Map.Entry<ItemStack, Integer> entry : itemsToDeposit.entrySet()) {
            depositItemStack(entry, contentLocked);
        }
    }

    @Deprecated
    public void depositItemStack(@NotNull ItemStack @NotNull [] itemsToDeposit, boolean contentLocked) {
        for (ItemStack item : itemsToDeposit) {
            depositItemStack(item, contentLocked);
        }
    }

    @Deprecated
    public void depositItemStack(@Nullable ItemStack itemsToDeposit, boolean contentLocked, boolean force) {
        if (itemsToDeposit == null || isBlacklisted(itemsToDeposit)) {
            return;
        }
        int actualAdded = addStoredItem(itemsToDeposit, itemsToDeposit.getAmount(), contentLocked, force);
        if (actualAdded > 0) {
            itemsToDeposit.setAmount(itemsToDeposit.getAmount() - actualAdded);
        }
    }

    @Deprecated
    public void depositItemStack(ItemStack item, boolean contentLocked) {
        depositItemStack(item, contentLocked, false);
    }

    @Nullable public ItemStack requestItem0(@NotNull Location accessor, @NotNull ItemRequest itemRequest) {
        return requestItem0(accessor, itemRequest, true);
    }

    @Nullable public ItemStack requestItem0(@NotNull Location accessor, @NotNull ItemRequest itemRequest, boolean contentLocked) {
        ItemStack item = itemRequest.getItemStack();
        if (item == null) {
            return null;
        }

        int amount = itemRequest.getAmount();

        List<ItemContainer> stored = getStoredItems();

        Map<Integer, Integer> m = getPersistentAccessHistory(accessor);
        if (m != null) {
            for (Integer i : m.keySet()) {
                // Netex - Cache start
                if (i >= stored.size()) {
                    removePersistentAccessHistory(accessor, i);
                    continue;
                }
                // Netex - Cache end

                ItemContainer itemContainer = stored.get(i);
                int containerAmount = itemContainer.getAmount();
                if (StackUtils.itemsMatch(itemContainer.getSampleDirectly(), item)) {
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
                    addCacheMiss(accessor, i);
                    // Netex - Cache end
                }
            }
        }

        for (int i = 0; i < stored.size(); i++) {
            ItemContainer itemContainer = stored.get(i);
            int containerAmount = itemContainer.getAmount();
            if (StackUtils.itemsMatch(itemContainer.getSampleDirectly(), item)) {
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

    public void depositItemStacks0(
            @NotNull Location accessor, @NotNull Map<ItemStack, Long> itemsToDeposit, boolean contentLocked) {
        for (Map.Entry<ItemStack, Long> entry : itemsToDeposit.entrySet()) {
            if (entry.getValue() > Integer.MAX_VALUE) {
                // rollback to MAX_VALUE
                long before = entry.getValue();
                ItemStack item = StackUtils.getAsQuantity(entry.getKey(), Integer.MAX_VALUE);
                depositItemStack0(accessor, item, contentLocked);
                long leftover = item.getAmount();
                entry.setValue(before - Integer.MAX_VALUE + leftover);
            } else {
                ItemStack item = StackUtils.getAsQuantity(entry.getKey(), Math.toIntExact(entry.getValue()));
                depositItemStack0(accessor, item, contentLocked);
                long rest = item.getAmount();
                entry.setValue(rest);
            }
        }
    }

    public void depositItemStack0(
            @NotNull Location accessor, @NotNull Map.Entry<ItemStack, Integer> entry, boolean contentLocked) {
        ItemStack item = StackUtils.getAsQuantity(entry.getKey(), entry.getValue());
        depositItemStack0(accessor, item, contentLocked);
        int leftover = item.getAmount();
        entry.setValue(leftover);
    }

    public void depositItemStack0(
            @NotNull Location accessor, @NotNull Map<ItemStack, Integer> itemsToDeposit, boolean contentLocked) {
        for (Map.Entry<ItemStack, Integer> entry : itemsToDeposit.entrySet()) {
            depositItemStack0(accessor, entry, contentLocked);
        }
    }

    public void depositItemStack0(
            @NotNull Location accessor, @NotNull ItemStack @NotNull [] itemsToDeposit, boolean contentLocked) {
        for (ItemStack item : itemsToDeposit) {
            depositItemStack0(accessor, item, contentLocked);
        }
    }

    public void depositItemStack0(
            @NotNull Location accessor, @Nullable ItemStack itemsToDeposit, boolean contentLocked, boolean force) {
        if (itemsToDeposit == null || isBlacklisted(itemsToDeposit)) {
            return;
        }
        int actualAdded = addStoredItem0(accessor, itemsToDeposit, itemsToDeposit.getAmount(), contentLocked, force);
        if (actualAdded > 0) {
            itemsToDeposit.setAmount(itemsToDeposit.getAmount() - actualAdded);
        }
    }

    public void depositItemStack0(@NotNull Location accessor, ItemStack item, boolean contentLocked) {
        depositItemStack0(accessor, item, contentLocked, false);
    }

    /**
     * Add item to unit
     *
     * @param accessor: accessor
     * @param item:     item will be added
     * @param amount:   amount will be added
     * @return the amount actual added
     */
    public int addStoredItem0(
            Location accessor, @NotNull ItemStack item, int amount, boolean contentLocked, boolean force) {
        int add = 0;
        boolean isVoidExcess = NetworksDrawer.isVoidExcess(getLastLocation());
        List<ItemContainer> stored = getStoredItems();

        Map<Integer, Integer> m = getPersistentAccessHistory(accessor);
        if (m != null) {
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
                        // If super-full, no more add and roll back to normal amount
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
                    addCacheMiss(accessor, i);
                    // Netex - Cache end
                }
            }
        }

        for (int i = 0; i < stored.size(); i++) {
            ItemContainer each = stored.get(i);
            if (each.isSimilar(item)) {
                // Found existing one, add amount
                int raw = sizeType.getEachMaxSize() - each.getAmount();
                if (raw < 0) {
                    // If super-full, no more add and roll back to normal amount
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
    public int addStoredItem0(Location accessor, @NotNull ItemStack item, boolean contentLocked) {
        return addStoredItem0(accessor, item, item.getAmount(), contentLocked, false);
    }

    public int addStoredItem0(Location accessor, @NotNull ItemStack item, boolean contentLocked, boolean force) {
        return addStoredItem0(accessor, item, item.getAmount(), contentLocked, force);
    }

    public int addStoredItem0(Location accessor, @NotNull ItemStack item, int amount, boolean contentLocked) {
        return addStoredItem0(accessor, item, amount, contentLocked, false);
    }
}
