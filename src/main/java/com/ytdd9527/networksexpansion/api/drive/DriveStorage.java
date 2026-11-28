package com.ytdd9527.networksexpansion.api.drive;

import com.balugaq.netex.utils.Debug;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellAcceptRules;
import com.ytdd9527.networksexpansion.api.cell.CellHandle;
import com.ytdd9527.networksexpansion.api.cell.CellLedger;
import com.ytdd9527.networksexpansion.api.cell.VoidCellHandle;
import com.ytdd9527.networksexpansion.api.cell.VoidCellSupport;
import com.ytdd9527.networksexpansion.implementation.items.drive.EnderChannelController;
import com.ytdd9527.networksexpansion.implementation.machines.drive.EnderDrive;
import com.ytdd9527.networksexpansion.utils.ItemHashMap;
import com.ytdd9527.networksexpansion.utils.ItemKey;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.DistinctiveItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DriveStorage {

    private static final Map<Material, ItemKey> PLAIN_KEY_CACHE = new ConcurrentHashMap<>();

    private static final Map<SlimefunItem, ItemKey> SLIMEFUN_KEY_CACHE = new ConcurrentHashMap<>();

    private static final Map<ItemStack, ItemKey> REQUEST_KEY_CACHE =
        Collections.synchronizedMap(new IdentityHashMap<>());

    private static final int REQUEST_KEY_CACHE_LIMIT = 512;

    private static final Object INDEX_LOCK = new Object();

    private final Map<Location, CellCacheEntry> cellCache = new ConcurrentHashMap<>();
    private final AtomicLong generationCounter = new AtomicLong();

    private static final class CellCacheEntry {
        List<CellHandle> handles;
        long generation;
    }

    @NotNull
    private static ItemKey getKey(@NotNull ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            final SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
            if (sfItem != null && !(sfItem instanceof DistinctiveItem)) {
                return SLIMEFUN_KEY_CACHE.computeIfAbsent(sfItem, s -> new ItemKey(itemStack));
            }
            return new ItemKey(itemStack);
        }
        return PLAIN_KEY_CACHE.computeIfAbsent(itemStack.getType(), m -> new ItemKey(new ItemStack(m)));
    }

    @NotNull
    private static ItemKey cachedKey(@NotNull ItemStack itemStack) {
        final ItemKey cached = REQUEST_KEY_CACHE.get(itemStack);
        if (cached != null) {
            return cached;
        }
        final ItemKey key = getKey(itemStack);
        if (REQUEST_KEY_CACHE.size() >= REQUEST_KEY_CACHE_LIMIT) {
            REQUEST_KEY_CACHE.clear();
        }
        REQUEST_KEY_CACHE.put(itemStack, key);
        return key;
    }

    @NotNull
    public List<CellHandle> getCells(@NotNull BlockMenu menu) {
        Location location = menu.getLocation();
        SlimefunItem sfItem = StorageCacheUtils.getSfItem(location);
        boolean isEnderDrive = DriveType.of(sfItem) == DriveType.ENDER;
        if (!isEnderDrive) {
            CellCacheEntry entry = cellCache.get(location);
            if (entry != null && entry.handles != null) {
                return entry.handles;
            }
        }
        List<CellHandle> cells = isEnderDrive ? collectWithRemoteMembers(menu) : DriveCellSlots.collectCells(menu);
        if (!isEnderDrive) {
            cellCache.computeIfAbsent(location, k -> new CellCacheEntry()).handles = cells;
            CellUniqueness.registerDrive(menu);
        }
        return cells;
    }

    @NotNull
    private static List<CellHandle> collectWithRemoteMembers(@NotNull BlockMenu menu) {
        Set<UUID> seen = new HashSet<>();
        List<CellHandle> cells = new ArrayList<>();
        for (CellHandle local : DriveCellSlots.collectCells(menu)) {
            if (seen.add(local.getUuid())) {
                cells.add(local);
            }
        }
        String channel = EnderDrive.getChannel(menu.getLocation());
        if (channel == null) {
            return cells;
        }
        for (String uuidString : EnderChannelController.getInstance().getCellUuidsOfChannel(channel)) {
            UUID remoteUuid;
            try {
                remoteUuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException e) {
                Debug.debug("末影频道成员 uuid 非法，跳过: " + uuidString);
                continue;
            }
            if (!seen.add(remoteUuid)) {
                continue;
            }
            CellLedger remoteCache = CellLedger.getActiveCaches().get(remoteUuid);
            if (remoteCache != null && !remoteCache.isVoidCell()) {
                cells.add(CellHandle.wrap(remoteCache));
            }
        }
        return cells;
    }

    public void invalidateCellCache(@NotNull Location location) {
        CellCacheEntry entry = cellCache.computeIfAbsent(location, k -> new CellCacheEntry());
        entry.handles = null;
        entry.generation = generationCounter.incrementAndGet();
    }

    public void dropCellCache(@NotNull Location location) {
        cellCache.remove(location);
    }

    @NotNull
    private FlatView getFlatView(
            @NotNull DriveCache cache, @NotNull Collection<BlockMenu> menus, @NotNull DriveViewRole role) {
        FlatView cached = cache.getFlatView(role);
        if (isFlatViewCurrent(cached, menus)) {
            return cached;
        }
        return rebuildFlatView(cache, menus, role);
    }

    private boolean isFlatViewCurrent(@Nullable FlatView cached, @NotNull Collection<BlockMenu> menus) {
        if (cached == null || cached.getLocations().size() != menus.size()) {
            return false;
        }
        if (cached.getLastMenus() == menus) {
            return true;
        }
        long fingerprint = 0L;
        for (BlockMenu menu : menus) {
            Location location = menu.getLocation();
            if (!cached.getLocations().contains(location)) {
                return false;
            }
            CellCacheEntry cacheEntry = cellCache.get(location);
            long gen = cacheEntry != null ? cacheEntry.generation : 0L;
            fingerprint += gen;
        }
        if (fingerprint == cached.getGenerationFingerprint()) {
            cached.setLastMenus(menus);
            return true;
        }
        return false;
    }

    @NotNull
    private FlatView rebuildFlatView(
            @NotNull DriveCache cache,
            @NotNull Collection<BlockMenu> menus,
            @NotNull DriveViewRole role) {
        Set<Location> driveLocations = new HashSet<>();
        for (BlockMenu menu : menus) {
            driveLocations.add(menu.getLocation());
        }
        long fingerprint = 0L;
        for (Location location : driveLocations) {
            CellCacheEntry cacheEntry = cellCache.get(location);
            long gen = cacheEntry != null ? cacheEntry.generation : 0L;
            fingerprint += gen;
        }

        List<CellHandle> cells = new ArrayList<>();
        Map<UUID, CellHandle> byUuid = new HashMap<>();
        Set<UUID> seenUuids = new HashSet<>();
        for (BlockMenu menu : menus) {
            for (CellHandle cell : getCells(menu)) {
                if (!seenUuids.add(cell.getUuid())) {
                    continue;
                }
                cells.add(cell);
                byUuid.put(cell.getUuid(), cell);
            }
        }

        cache.clearItemCaches();
        FlatView view = new FlatView(driveLocations, cells, byUuid, fingerprint, hasVoidSink(cells));
        cache.setFlatView(role, view);
        return view;
    }

    public long pushSingle(
            @NotNull DriveCache cache,
            @NotNull Collection<BlockMenu> menus,
            @NotNull ItemStack template,
            long amount) {
        if (amount <= 0) {
            return amount;
        }
        FlatView view = getFlatView(cache, menus, DriveViewRole.INPUT);
        List<CellHandle> cells = view.cells;
        if (cells.isEmpty()) {
            return amount;
        }
        boolean voidActive = view.hasVoidSink && !VoidCellSupport.isSuspended();
        return pushSingleEntry(cells, voidActive, cache, cache.getItemToStorageIndex(), template, amount);
    }

    public void pushMany(
            @NotNull DriveCache cache,
            @NotNull Collection<BlockMenu> menus,
            @NotNull List<ItemStack> items) {
        if (items.isEmpty()) {
            return;
        }
        FlatView view = getFlatView(cache, menus, DriveViewRole.INPUT);
        List<CellHandle> cells = view.cells;
        if (cells.isEmpty()) {
            return;
        }
        boolean voidActive = view.hasVoidSink && !VoidCellSupport.isSuspended();
        Map<ItemKey, List<UUID>> index = cache.getItemToStorageIndex();
        for (ItemStack item : items) {
            int amount = item.getAmount();
            if (amount <= 0) {
                continue;
            }
            long remaining = pushSingleEntry(cells, voidActive, cache, index, item, amount);
            item.setAmount((int) Math.min(remaining, Integer.MAX_VALUE));
        }
    }

    private static long pushSingleEntry(
            @NotNull List<CellHandle> cells,
            boolean voidActive,
            @NotNull DriveCache cache,
            @Nullable Map<ItemKey, List<UUID>> index,
            @NotNull ItemStack template,
            long amount) {
        long remaining = amount;
        if (remaining <= 0) {
            return remaining;
        }

        if (template.getType().isAir()) {
            return remaining;
        }

        ItemKey itemKey = getKey(template);
        if (itemKey.hasMeta()
            && (CellAcceptRules.isUsedCell(template)
            || CellAcceptRules.isFilledContainer(template)
            || itemKey.isOversized())) {
            return remaining;
        }
        cache.getNotIncluded().remove(itemKey);

        long pushedTotal = 0L;
        UUID lastProvider = null;
        for (CellHandle cell : cells) {
            if (remaining <= 0) {
                break;
            }
            if (!cell.canReceiveItem(itemKey)) {
                continue;
            }
            int toPush = (int) Math.min(remaining, Integer.MAX_VALUE);
            int pushed = cell.pushItem(itemKey, toPush);
            if (pushed > 0) {
                remaining -= pushed;
                pushedTotal += pushed;
                lastProvider = cell.getUuid();
                indexAdd(index, itemKey, cell.getUuid());
            }
        }

        if (lastProvider != null) {
            cache.getPushCache().put(itemKey, lastProvider);
            cache.adjust(itemKey, pushedTotal);
        }

        if (voidActive && remaining > 0) {
            long destroyed = destroyViaVoidSinks(cells, itemKey, remaining);
            if (destroyed > 0) {
                remaining -= destroyed;
                cache.adjust(itemKey, -destroyed);
            }
        }

        return remaining;
    }

    private static long destroyViaVoidSinks(@NotNull List<CellHandle> cells, @NotNull ItemKey itemKey, long amount) {
        for (CellHandle cell : cells) {
            if (!(cell instanceof VoidCellHandle voidSink)) {
                continue;
            }
            if (voidSink.acceptsForDestroy(itemKey.getItemStack())) {
                return amount;
            }
        }
        return 0;
    }

    @Nullable
    public ItemStack takeItem(@NotNull DriveCache cache, @NotNull Collection<BlockMenu> menus, @NotNull ItemRequest request) {
        ItemStack requested = request.getItemStack();
        if (requested == null) {
            return null;
        }

        int requestedAmount = request.getAmount();
        if (requestedAmount <= 0) {
            return null;
        }

        return takeByKey(cache, menus, cachedKey(requested), requestedAmount, DriveViewRole.OUTPUT);
    }

    @Nullable
    public ItemStack takeItemDirect(
            @Nullable DriveCache cache,
            @NotNull Collection<BlockMenu> menus,
            @NotNull ItemKey itemKey,
            int amount) {
        if (cache == null || amount <= 0) {
            return null;
        }
        return takeByKey(cache, menus, itemKey, amount, DriveViewRole.INPUT);
    }

    public long takeItemDirectAmount(
            @Nullable DriveCache cache,
            @NotNull Collection<BlockMenu> menus,
            @NotNull ItemKey itemKey,
            long amount) {
        if (cache == null || amount <= 0) {
            return 0L;
        }
        if (cache.getNotIncluded().contains(itemKey)) {
            return 0L;
        }
        FlatView view = getFlatView(cache, menus, DriveViewRole.INPUT);
        List<CellHandle> cells = view.cells;
        if (cells.isEmpty()) {
            cache.getNotIncluded().add(itemKey);
            return 0L;
        }
        long taken = 0L;
        CellHandle lastProvider = null;
        CellHandle first = resolveSingleCandidate(cache, view, itemKey);
        if (first != null) {
            long got = first.takeItemAmount(itemKey, amount);
            if (got > 0) {
                taken += got;
                lastProvider = first;
            }
        }
        for (CellHandle cell : cells) {
            if (taken >= amount) {
                break;
            }
            if (cell == first) {
                continue;
            }
            long got = cell.takeItemAmount(itemKey, amount - taken);
            if (got > 0) {
                taken += got;
                lastProvider = cell;
            }
        }
        if (lastProvider != null) {
            cache.getTakeCache().put(itemKey, lastProvider.getUuid());
            cache.adjust(itemKey, taken);
            cache.getNotIncluded().remove(itemKey);
        } else {
            cache.getNotIncluded().add(itemKey);
        }
        return taken;
    }

    @Nullable
    private ItemStack takeByKey(
            @NotNull DriveCache cache,
            @NotNull Collection<BlockMenu> menus,
            @NotNull ItemKey itemKey,
            int requestedAmount,
            @NotNull DriveViewRole role) {
        if (cache.getNotIncluded().contains(itemKey)) {
            return null;
        }

        FlatView view = getFlatView(cache, menus, role);
        List<CellHandle> cells = view.cells;
        if (cells.isEmpty()) {
            cache.getNotIncluded().add(itemKey);
            return null;
        }

        TakeProgress progress = new TakeProgress(requestedAmount);
        Map<ItemKey, List<UUID>> index = cache.getItemToStorageIndex();

        CellHandle singleCandidate = resolveSingleCandidate(cache, view, itemKey);
        if (singleCandidate != null) {
            takeFromCell(singleCandidate, itemKey, progress, index);
            if (progress.remaining > 0) {
                takeFromCells(cells, itemKey, progress, index, Set.of(singleCandidate), true);
            }
        } else {
            Set<CellHandle> candidates = collectCandidateCells(cache, view, itemKey);
            takeFromCells(candidates, itemKey, progress, index, null, false);
            if (progress.remaining > 0) {
                takeFromCells(cells, itemKey, progress, index, candidates, true);
            }
        }

        if (progress.lastProvider != null) {
            cache.getTakeCache().put(itemKey, progress.lastProvider.getUuid());
            cache.adjust(itemKey, requestedAmount - progress.remaining);
            cache.getNotIncluded().remove(itemKey);
        } else {
            cache.getNotIncluded().add(itemKey);
        }

        return progress.result;
    }

    @Nullable
    private static CellHandle resolveSingleCandidate(
            @NotNull DriveCache cache, @NotNull FlatView view, @NotNull ItemKey itemKey) {
        UUID cachedUuid = cache.getTakeCache().get(itemKey);
        if (cachedUuid == null) {
            return null;
        }
        CellHandle cell = view.byUuid.get(cachedUuid);
        if (cell == null) {
            return null;
        }
        Map<ItemKey, List<UUID>> index = cache.getItemToStorageIndex();
        if (index != null) {
            List<UUID> uuids = index.get(itemKey);
            if (uuids != null) {
                for (UUID uuid : uuids) {
                    if (!uuid.equals(cachedUuid)) {
                        return null;
                    }
                }
            }
        }
        return cell;
    }

    private static void takeFromCell(
            @NotNull CellHandle cell,
            @NotNull ItemKey itemKey,
            @NotNull TakeProgress progress,
            @Nullable Map<ItemKey, List<UUID>> index) {
        ItemStack taken = cell.takeItem(itemKey, progress.remaining);
        if (taken != null) {
            if (progress.result == null) {
                progress.result = taken;
            } else {
                progress.result.setAmount(progress.result.getAmount() + taken.getAmount());
            }
            progress.remaining -= taken.getAmount();
            progress.lastProvider = cell;
            indexAdd(index, itemKey, cell.getUuid());
        }
    }

    @NotNull
    private static Set<CellHandle> collectCandidateCells(
            @NotNull DriveCache cache, @NotNull FlatView view, @NotNull ItemKey itemKey) {
        Map<UUID, CellHandle> byUuid = view.byUuid;
        Set<CellHandle> candidates = new LinkedHashSet<>();

        UUID cachedUuid = cache.getTakeCache().get(itemKey);
        if (cachedUuid != null) {
            CellHandle cell = byUuid.get(cachedUuid);
            if (cell != null && cell.contains(itemKey)) {
                candidates.add(cell);
            }
        }

        Map<ItemKey, List<UUID>> index = cache.getItemToStorageIndex();
        if (index != null) {
            List<UUID> uuids = index.get(itemKey);
            if (uuids != null) {
                for (UUID uuid : uuids) {
                    CellHandle cell = byUuid.get(uuid);
                    if (cell != null && cell.contains(itemKey)) {
                        candidates.add(cell);
                    }
                }
            }
        }
        return candidates;
    }

    private static void takeFromCells(
            @NotNull Iterable<CellHandle> cells,
            @NotNull ItemKey itemKey,
            @NotNull TakeProgress progress,
            @Nullable Map<ItemKey, List<UUID>> index,
            @Nullable Set<CellHandle> skip,
            boolean checkContains) {
        for (CellHandle cell : cells) {
            if (progress.remaining <= 0) {
                return;
            }
            if ((skip != null && skip.contains(cell)) || (checkContains && !cell.contains(itemKey))) {
                continue;
            }
            ItemStack taken = cell.takeItem(itemKey, progress.remaining);
            if (taken != null) {
                if (progress.result == null) {
                    progress.result = taken;
                } else {
                    progress.result.setAmount(progress.result.getAmount() + taken.getAmount());
                }
                progress.remaining -= taken.getAmount();
                progress.lastProvider = cell;
                indexAdd(index, itemKey, cell.getUuid());
            }
        }
    }

    private static final class TakeProgress {
        @Nullable ItemStack result;
        long remaining;
        @Nullable CellHandle lastProvider;

        TakeProgress(long requestedAmount) {
            this.remaining = requestedAmount;
        }
    }

    @NotNull
    public CapacityProbe probeReceive(
            @NotNull DriveCache cache,
            @NotNull Collection<BlockMenu> menus,
            @NotNull ItemKey key,
            long amount) {
        FlatView view = getFlatView(cache, menus, DriveViewRole.INPUT);
        long total = 0L;
        for (CellHandle cell : view.cells) {
            if (total >= amount) {
                break;
            }
            if (!(cell instanceof VoidCellHandle)) {
                total += cell.receiveCapacity(key);
            }
        }
        if (total >= amount || !view.hasVoidSink) {
            return new CapacityProbe(total, false);
        }
        ItemStack template = key.getItemStack();
        for (CellHandle cell : view.cells) {
            if (cell instanceof VoidCellHandle voidSink && voidSink.acceptsForDestroy(template)) {
                return new CapacityProbe(total, true);
            }
        }
        return new CapacityProbe(total, false);
    }

    public record CapacityProbe(long capacity, boolean voidAccepts) {
    }

    private static void indexAdd(@Nullable Map<ItemKey, List<UUID>> index, @NotNull ItemKey key, @NotNull UUID uuid) {
        if (index == null) {
            return;
        }
        synchronized (INDEX_LOCK) {
            List<UUID> uuids = index.computeIfAbsent(key, k -> new ArrayList<>());
            if (!uuids.contains(uuid)) {
                uuids.add(uuid);
            }
        }
    }

    @NotNull
    public ItemHashMap<Long> getAllCellItemsKeyed(@NotNull DriveCache cache, @NotNull Collection<BlockMenu> menus) {
        FlatView view = getFlatView(cache, menus, DriveViewRole.OUTPUT);
        return cache.getStorage(view.cells);
    }

    @NotNull
    public Map<ItemStack, Long> getAllCellItems(@NotNull List<CellHandle> cells) {
        ItemHashMap<Long> snapshot = getAllCellItemsKeyed(cells);
        Map<ItemStack, Long> result = new HashMap<>();
        for (Map.Entry<ItemKey, Long> entry : snapshot.keyEntrySet()) {
            result.put(entry.getKey().getItemStack(), entry.getValue());
        }
        return result;
    }

    @NotNull
    public ItemHashMap<Long> getAllCellItemsKeyed(@NotNull List<CellHandle> cells) {
        ItemHashMap<Long> snapshot = new ItemHashMap<>();
        for (CellHandle cell : cells) {
            cell.accumulateInto(snapshot, null);
        }
        return snapshot;
    }

    public int getAmount(@NotNull DriveCache cache, @NotNull Collection<BlockMenu> menus, @NotNull ItemStack itemStack) {
        ItemKey itemKey = cachedKey(itemStack);
        if (cache.getNotIncluded().contains(itemKey)) {
            return 0;
        }
        FlatView view = getFlatView(cache, menus, DriveViewRole.OUTPUT);
        long total = 0;
        for (CellHandle cell : view.cells) {
            total += cell.getAmount(itemKey);
        }
        if (total > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) total;
    }

    public enum DriveViewRole {
        INPUT,
        OUTPUT
    }

    @Getter
    @RequiredArgsConstructor
    public static final class FlatView {
        private final Set<Location> locations;
        private final List<CellHandle> cells;
        private final Map<UUID, CellHandle> byUuid;
        private final long generationFingerprint;
        private final boolean hasVoidSink;
        @Setter
        private volatile Collection<BlockMenu> lastMenus;
    }

    private static boolean hasVoidSink(@NotNull List<CellHandle> cells) {
        for (CellHandle cell : cells) {
            if (cell instanceof VoidCellHandle) {
                return true;
            }
        }
        return false;
    }
}
