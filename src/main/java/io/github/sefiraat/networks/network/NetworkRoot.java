package io.github.sefiraat.networks.network;

import com.balugaq.netex.api.data.ItemContainer;
import com.balugaq.netex.api.data.ItemFlowRecord;
import com.balugaq.netex.api.data.StorageUnitData;
import com.balugaq.netex.api.enums.AccessMode;
import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.enums.StorageType;
import com.balugaq.netex.api.events.NetworkRootLocateStorageEvent;
import com.balugaq.netex.api.interfaces.FeedbackSendable;
import com.balugaq.netex.utils.BlockMenuUtil;
import com.balugaq.netex.utils.NetworksVersionedParticle;
import com.balugaq.netex.utils.RootWriteLock;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.implementation.machines.drive.CellDrive;
import com.ytdd9527.networksexpansion.api.drive.DriveCache;
import com.ytdd9527.networksexpansion.api.drive.DriveStorage;
import com.ytdd9527.networksexpansion.utils.ItemHashMap;
import com.ytdd9527.networksexpansion.utils.ItemKey;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedGreedyBlock;
import com.ytdd9527.networksexpansion.implementation.machines.unit.NetworksDrawer;
import com.ytdd9527.networksexpansion.implementation.storage.SimpleStorageChest;
import io.github.mooy1.infinityexpansion.items.storage.StorageCache;
import io.github.mooy1.infinityexpansion.items.storage.StorageUnit;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.barrel.FluffyBarrel;
import io.github.sefiraat.networks.network.barrel.InfinityBarrel;
import io.github.sefiraat.networks.network.barrel.NetworkStorage;
import io.github.sefiraat.networks.network.barrel.TsingshanBarrel;
import io.github.sefiraat.networks.network.stackcaches.BarrelIdentity;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.network.stackcaches.QuantumCache;
import io.github.sefiraat.networks.slimefun.network.NetworkCell;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.sefiraat.networks.slimefun.network.NetworkGreedyBlock;
import io.github.sefiraat.networks.slimefun.network.NetworkPowerNode;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.utils.MatchOption;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings({"deprecation", "IfCanBeSwitch"})
@Getter
@NullMarked
public class NetworkRoot extends NetworkNode {
    public static final int
        persistentThreshold = Networks.getConfigManager().getPersistentThreshold(),
        cacheMissThreshold = Networks.getConfigManager().getCacheMissThreshold(),
        reduceMs = Networks.getConfigManager().getReduceMs(),
        transportMissThreshold = Networks.getConfigManager().getTransportMissThreshold();

    @Getter
    private static final Map<Location, Map<Location, Integer>>
        /* from -> to -> access times */ observingAccessHistory = new ConcurrentHashMap<>(),
        /* from -> to -> cache miss times */ persistentAccessHistory = new ConcurrentHashMap<>();

    @Getter
    private static final Map<Location, Integer>
        /* location -> Transport miss times */ transportMissInputHistory = new ConcurrentHashMap<>(),
        /* location -> Transport miss times */ transportMissOutputHistory = new ConcurrentHashMap<>();

    @Getter
    private static final Map<Location, Long>
        controlledAccessInputHistory = new ConcurrentHashMap<>(),
        controlledAccessOutputHistory = new ConcurrentHashMap<>();

    @Getter
    private final Set<Location> nodeLocations = ConcurrentHashMap.newKeySet();

    public static final int[] CELL_AVAILABLE_SLOTS = NetworkCell.SLOTS.stream().mapToInt(i -> i).toArray();
    public static final int[] ADVANCED_GREEDY_BLOCK_AVAILABLE_SLOTS = AdvancedGreedyBlock.INPUT_SLOTS;
    public static final int GREEDY_BLOCK_AVAILABLE_SLOT = NetworkGreedyBlock.INPUT_SLOT;

    @Getter
    private final int maxNodes;
    @Getter
    private final boolean recordFlow;
    @Getter
    private final @Nullable ItemFlowRecord itemFlowRecord;
    @Getter
    private final Location controller;

    @Getter
    private boolean isOverburdened = false;

    private volatile @Nullable Set<BarrelIdentity>
        barrels = null,
        inputAbleBarrels = null,
        outputAbleBarrels = null;

    private volatile @Nullable Set<SimpleStorageChest>
        inputAbleSimpleStorageChests = null,
        outputAbleSimpleStorageChests = null;

    private volatile @Nullable Map<Location, StorageUnitData>
        drawerData = null,
        inputAbleDrawerData = null,
        outputAbleDrawerData = null;

    private volatile @Nullable Map<Location, BarrelIdentity>
        mapInputAbleBarrels = null,
        mapOutputAbleBarrels = null;

    private volatile @Nullable Set<BlockMenu>
        cellDriveMenus = null,
        inputAbleCellDriveMenus = null,
        outputAbleCellDriveMenus = null;

    private final DriveCache driveCache = new DriveCache();

    /**
     * 聚合物品快照按网络（控制器位置）静态共享：root 实例每刻由 NetworkController 重建，
     * 实例级缓存无法跨刻复用，空闲网络（无写入）借此零重扫。失效条件：
     * 同刻——仅显式失效（{@link #markDirty()}）重建，
     * 刻内普通写沿用既有"至多 1 刻延迟"契约（写纪元不影响同刻复用）；
     * 跨刻——期间无任何写（写纪元不变）且未超 {@link #SNAPSHOT_MAX_AGE_MS} 才复用。
     * 时间上限兜底绕过 root 的直改存储（抽屉/单元 GUI 手改、区块加载、结构变更）——陈旧度至多 500ms。
     */
    private static final Map<Location, ItemSnapshot> SHARED_ITEM_SNAPSHOTS = new ConcurrentHashMap<>();
    private static final Map<Location, AtomicLong> INVALIDATION_EPOCHS = new ConcurrentHashMap<>();
    private static final Map<Location, AtomicLong> WRITE_EPOCHS = new ConcurrentHashMap<>();
    private static final long SNAPSHOT_MAX_AGE_MS = 500L;
    private static final long SNAPSHOT_IDLE_MS = 3_600_000L;

    /**
     * 不可变快照：内容与失效判定依据作为整体经 CHM 原子发布（单引用读），
     * 拆成多个字段会让读线程看到"新纪元 + 旧数据"的组合。
     * keyed 为内部聚合的原始视图（发布后不再变更），供 keyed 消费方零哈希查询；
     * items 为 keyed 的 ItemStack 出口视图，仅首个消费方需要时构建并缓存（keyed-only 消费零克隆）。
     */
    @RequiredArgsConstructor
    private static final class ItemSnapshot {
        private final Map<ItemKey, Long> keyed;
        private final long invalidationEpoch;
        private final long writeEpoch;
        private final int tick;
        private final long publishedAtMs;
        private volatile @Nullable Map<ItemStack, Long> exported;

        private long invalidationEpoch() {
            return invalidationEpoch;
        }

        private long writeEpoch() {
            return writeEpoch;
        }

        private int tick() {
            return tick;
        }

        private long publishedAtMs() {
            return publishedAtMs;
        }

        private Map<ItemKey, Long> keyed() {
            return keyed;
        }

        private Map<ItemStack, Long> items() {
            Map<ItemStack, Long> items = exported;
            if (items != null) {
                return items;
            }
            synchronized (this) {
                items = exported;
                if (items == null) {
                    items = new HashMap<>(keyed.size() * 2);
                    for (Map.Entry<ItemKey, Long> entry : keyed.entrySet()) {
                        items.put(entry.getKey().getItemStack(), entry.getValue());
                    }
                    exported = items;
                }
                return items;
            }
        }
    }

    private final EnumMap<NodeType, NodeIndex> indexes = new EnumMap<>(NodeType.class);

    public static class NodeIndex {
        private final Set<Location> locations = ConcurrentHashMap.newKeySet();
        private @Nullable Set<BlockMenu> blockMenus = null;

        public void addLocation(Location location) {
            locations.add(location);
        }

        public Set<BlockMenu> getMenus() {
            if (blockMenus != null) return blockMenus;
            Set<BlockMenu> menus = new HashSet<>();
            for (var loc : locations) {
                var menu = StorageCacheUtils.getMenu(loc);
                if (menu != null) menus.add(menu);
            }
            blockMenus = menus;
            return menus;
        }
    }

    private final AtomicLong rootPower = new AtomicLong(0);

    @Setter
    private boolean displayParticles = false;

    public NetworkRoot(
        Location location,
        int maxNodes,
        boolean recordFlow,
        @Nullable ItemFlowRecord itemFlowRecord) {
        super(location, NodeType.CONTROLLER);
        this.maxNodes = maxNodes;
        this.controller = location;
        this.root = this;
        this.recordFlow = recordFlow;
        this.itemFlowRecord = itemFlowRecord;

        for (var type : NodeType.values()) {
            indexes.put(type, new NodeIndex());
        }

        registerNode(location, NodeType.CONTROLLER);
    }

    public static void addPersistentAccessHistory(Location location, Location accessLocation) {
        Map<Location, Integer> locations = persistentAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
        locations.put(accessLocation, 0);
        persistentAccessHistory.put(location, locations);
    }

    public static void addCacheMiss(Location location, Location accessLocation) {
        Map<Location, Integer> locations = persistentAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
        int value = locations.getOrDefault(accessLocation, 0) + 1;
        if (value > cacheMissThreshold) {
            removePersistentAccessHistory(location, accessLocation);
            return;
        }
        locations.put(accessLocation, value);
        persistentAccessHistory.put(location, locations);
    }

    public static void minusCacheMiss(Location location, Location accessLocation) {
        Map<Location, Integer> locations = persistentAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
        int value = Math.max(locations.getOrDefault(accessLocation, 0) - 1, 0);
        locations.put(accessLocation, value);
    }

    @Contract("null -> null")
    public static @Nullable Map<Location, Integer> getPersistentAccessHistory(@Nullable Location location) {
        if (location == null) return null;
        return persistentAccessHistory.get(location);
    }

    public static void removePersistentAccessHistory(Location location) {
        persistentAccessHistory.remove(location);
    }

    public static void removePersistentAccessHistory(Location location, Location accessLocation) {
        Map<Location, Integer> locations = persistentAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
        locations.remove(accessLocation);
        persistentAccessHistory.put(location, locations);
    }

    public static void addCountObservingAccessHistory(@Nullable Location accessor, Location accessLocation) {
        if (accessor == null) return;
        Map<Location, Integer> locations = observingAccessHistory.getOrDefault(accessor, new ConcurrentHashMap<>());
        int count = locations.getOrDefault(accessLocation, 0);
        if (count >= persistentThreshold) {
            removeCountObservingAccessHistory(accessor, accessLocation);
            addPersistentAccessHistory(accessor, accessLocation);
            return;
        }
        locations.put(accessLocation, count + 1);
        observingAccessHistory.put(accessor, locations);
    }

    public static Map<Location, Integer> getCountObservingAccessHistory(Location location) {
        return observingAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
    }

    public static void removeCountObservingAccessHistory(Location location) {
        observingAccessHistory.remove(location);
    }

    public static void removeCountObservingAccessHistory(Location location, Location accessLocation) {
        Map<Location, Integer> locations = observingAccessHistory.getOrDefault(location, new ConcurrentHashMap<>());
        locations.remove(accessLocation);
        observingAccessHistory.put(location, locations);
    }

    @Nullable
    public static InfinityBarrel getInfinityBarrel(BlockMenu blockMenu, StorageUnit storageUnit) {
        return getInfinityBarrel(blockMenu, storageUnit, false);
    }

    @Nullable
    public static InfinityBarrel getInfinityBarrel(
        BlockMenu blockMenu, StorageUnit storageUnit, boolean includeEmpty) {
        final ItemStack itemStack = blockMenu.getItemInSlot(16);
        final String storedString = StorageCacheUtils.getData(blockMenu.getLocation(), "stored");
        if (storedString == null) return null;

        final int storedInt = Integer.parseInt(storedString);

        if (!includeEmpty && (itemStack == null || itemStack.getType() == Material.AIR)) {
            return null;
        }

        final StorageCache cache = storageUnit.getCache(blockMenu.getLocation());
        if (cache == null) return null;

        ItemStack clone = null;
        if (itemStack != null) {
            clone = itemStack.clone();
            clone.setAmount(1);
        }

        return new InfinityBarrel(
            blockMenu.getLocation(), clone, storedInt + (itemStack == null ? 0 : itemStack.getAmount()), cache);
    }

    @Nullable
    public static TsingshanBarrel getTsingshanBarrel(
        BlockMenu blockMenu, io.gitee.crankygoudan.tsingshantechnology.items.chucun.StorageUnit storageUnit) {
        return getTsingshanBarrel(blockMenu, storageUnit, false);
    }

    @Nullable
    public static TsingshanBarrel getTsingshanBarrel(
        BlockMenu blockMenu, io.gitee.crankygoudan.tsingshantechnology.items.chucun.StorageUnit storageUnit,
        boolean includeEmpty) {
        final ItemStack itemStack = blockMenu.getItemInSlot(16);
        // TsingshanTechnology 的数量落盘键为 "sstored"（区别于无尽存储的 "stored"）
        final String storedString = StorageCacheUtils.getData(blockMenu.getLocation(), "sstored");
        if (storedString == null) return null;

        final int storedInt;
        try {
            storedInt = Integer.parseInt(storedString);
        } catch (NumberFormatException e) {
            return null;
        }

        if (!includeEmpty && (itemStack == null || itemStack.getType() == Material.AIR)) {
            return null;
        }

        final io.gitee.crankygoudan.tsingshantechnology.items.chucun.StorageCache cache =
            storageUnit.getCache(blockMenu.getLocation());
        if (cache == null) return null;

        ItemStack clone = null;
        if (itemStack != null) {
            clone = itemStack.clone();
            clone.setAmount(1);
        }

        return new TsingshanBarrel(
            blockMenu.getLocation(), clone, storedInt + (itemStack == null ? 0 : itemStack.getAmount()), cache);
    }

    @Nullable
    public static FluffyBarrel getFluffyBarrel(BlockMenu blockMenu, Barrel barrel) {
        return getFluffyBarrel(blockMenu, barrel, false);
    }

    @Nullable
    public static FluffyBarrel getFluffyBarrel(
        BlockMenu blockMenu, Barrel barrel, boolean includeEmpty) {
        Block block = blockMenu.getBlock();
        ItemStack itemStack;
        try {
            itemStack = barrel.getStoredItem(block);
        } catch (NullPointerException ignored) {
            return null;
        }

        if (!includeEmpty && (itemStack == null || itemStack.getType() == Material.AIR)) {
            return null;
        }

        ItemStack clone = null;
        if (itemStack != null) {
            clone = itemStack.clone();
            clone.setAmount(1);
        }

        int stored = barrel.getStored(block);
        if (stored <= 0) return null;

        int limit = barrel.getCapacity(block);
        boolean voidExcess = Boolean.parseBoolean(StorageCacheUtils.getData(blockMenu.getLocation(), "trash"));

        return new FluffyBarrel(blockMenu.getLocation(), clone, stored, limit, voidExcess);
    }

    @Nullable
    public static NetworkStorage getNetworkStorage(BlockMenu blockMenu) {
        return getNetworkStorage(blockMenu, false);
    }

    @Nullable
    public static NetworkStorage getNetworkStorage(BlockMenu blockMenu, boolean includeEmpty) {
        final QuantumCache cache = NetworkQuantumStorage.getCaches().get(blockMenu.getLocation());

        if (cache == null) return null;

        final ItemStack itemStack = cache.getItemStack();
        if ((itemStack == null || itemStack.getType() == Material.AIR) && !includeEmpty) {
            return null;
        }

        final ItemStack output = blockMenu.getItemInSlot(NetworkQuantumStorage.OUTPUT_SLOT);
        long storedInt = cache.getAmountLong();
        if (output != null && output.getType() != Material.AIR && StackUtils.itemsMatch(cache, output)) {
            storedInt = storedInt + output.getAmount();
        }

        ItemStack clone = null;

        if (itemStack != null) {
            clone = itemStack.clone();
            clone.setAmount(1);
        }

        return new NetworkStorage(blockMenu.getLocation(), clone, storedInt);
    }

    @Nullable
    public static BarrelIdentity getBarrel(Location barrelLocation) {
        return getBarrel(barrelLocation, false);
    }

    @Nullable
    public static BarrelIdentity getBarrel(Location barrelLocation, boolean includeEmpty) {
        SlimefunItem item = StorageCacheUtils.getSfItem(barrelLocation);
        BlockMenu menu = StorageCacheUtils.getMenu(barrelLocation);
        if (menu == null) return null;

        // DO NOT use switch case:
        // Class loading of Barrel/StorageUnit happens during pattern testing, before the when guard
        // is evaluated, causing NoClassDefFoundError when FluffyMachines/InfinityExpansion is absent.
        // Use short-circuit conditions so instanceof is never reached when the plugin is missing.
        if (item instanceof NetworkQuantumStorage) {
            return getNetworkStorage(menu, includeEmpty);
        } else if (Networks.getSupportedPluginManager().isFluffyMachines() && item instanceof Barrel barrel) {
            return getFluffyBarrel(menu, barrel, includeEmpty);
        } else if (Networks.getSupportedPluginManager().isInfinityExpansion() && item instanceof StorageUnit storageUnit) {
            return getInfinityBarrel(menu, storageUnit, includeEmpty);
        } else if (Networks.getSupportedPluginManager().isTsingshanTechnology() && item instanceof io.gitee.crankygoudan.tsingshantechnology.items.chucun.StorageUnit unit) {
            return getTsingshanBarrel(menu, unit, includeEmpty);
        }
        return null;
    }

    @Nullable
    public static StorageUnitData getCargoStorageUnitData(BlockMenu blockMenu) {
        return getCargoStorageUnitData(blockMenu.getLocation());
    }

    @Nullable
    public static StorageUnitData getCargoStorageUnitData(Location location) {
        return NetworksDrawer.getStorageData(location);
    }

    public void registerNode(Location location, NodeType type) {
        nodeLocations.add(location);
        switch (type) {
            /*
             * Fix https://github.com/Sefiraat/Networks/issues/211
             */
            case CELL -> {
                BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
                if (blockMenu == null || isFakeCell(location)) return;
            }
            case GREEDY_BLOCK -> {
                BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
                if (blockMenu == null || !(StorageCacheUtils.getSfItem(location) instanceof NetworkGreedyBlock)) return;
            }
            case ADVANCED_GREEDY_BLOCK -> {
                BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
                if (blockMenu == null || !(StorageCacheUtils.getSfItem(location) instanceof AdvancedGreedyBlock)) return;
            }
        }

        var index = indexes.get(type);
        index.addLocation(location);
    }

    public int getNodeCount() {
        return this.nodeLocations.size();
    }

    public int getNodeCount(NodeType type) {
        return indexes.get(type).locations.size();
    }

    /** 显式失效纪元：markDirty 递增，同刻与跨刻复用都会校验。 */
    private void bumpInvalidationEpoch() {
        INVALIDATION_EPOCHS.computeIfAbsent(this.nodePosition, k -> new AtomicLong()).incrementAndGet();
    }

    /** 写纪元：物品存取（addItemStack0 / getItemStack0）递增，仅影响跨刻复用。 */
    private void bumpWriteEpoch() {
        WRITE_EPOCHS.computeIfAbsent(this.nodePosition, k -> new AtomicLong()).incrementAndGet();
    }

    private static long currentInvalidationEpoch(Location networkId) {
        AtomicLong counter = INVALIDATION_EPOCHS.get(networkId);
        return counter == null ? 0L : counter.get();
    }

    private static long currentWriteEpoch(Location networkId) {
        AtomicLong counter = WRITE_EPOCHS.get(networkId);
        return counter == null ? 0L : counter.get();
    }

    /**
     * 外部代码绕过 {@link #addItemStack(Location, ItemStack)} / {@link #getItemStack(Location, ItemRequest)}
     * 直接改动网络存储内容（如机器菜单槽位、元件驱动器内部缓存）后，必须调用本方法使聚合物品缓存失效。
     */
    public void markDirty() {
        bumpInvalidationEpoch();
    }

    public void setOverburdened(boolean overburdened) {
        if (overburdened && !isOverburdened) {
            final Location loc = this.nodePosition.clone();
            for (int x = 0; x <= 1; x++) {
                for (int y = 0; y <= 1; y++) {
                    for (int z = 0; z <= 1; z++) {
                        loc.getWorld().spawnParticle(NetworksVersionedParticle.EXPLOSION, loc.clone().add(x, y, z), 0);
                    }
                }
            }
        }
        this.isOverburdened = overburdened;
    }

    // for display needs, don't operate items based on the return value!
    public Map<ItemStack, Long> getAllNetworkItemsLongTypeView() {
        return getAllNetworkItemsLongType();
    }

    /**
     * 返回网络全量物品的聚合快照。快照按网络静态共享：
     * 同刻内——仅显式失效（{@link {@link #markDirty()}）重建，
     * 普通物品写沿用既有"至多 1 刻延迟"契约；
     * 跨刻——期间无任何写（写纪元不变）且距发布不超过 500ms 时直接复用（空闲网络零重扫），
     * 否则重建。绕过 root 的直改存储（GUI 手改、区块加载、结构变更）由时间上限兜底。
     * <p>
     * 注意：返回的是<b>共享缓存实例</b>，调用方不得修改（含 entrySet/keySet 视图）；
     * 需要独立副本请自行 clone。
     */
    public Map<ItemStack, Long> getAllNetworkItemsLongType() {
        return obtainItemSnapshot().items();
    }

    /**
     * 聚合物品的 keyed 只读视图（{@link ItemKey} 精确身份，amount 归一为 1 的模板）：
     * 与 {@link #getAllNetworkItemsLongType()} 同一份快照、同一套失效校验，
     * 键的哈希在聚合时已算好——查询零重复深度哈希。返回共享实例，不得修改。
     */
    public Map<ItemKey, Long> getAllNetworkItemsKeyedView() {
        return obtainItemSnapshot().keyed();
    }

    private ItemSnapshot obtainItemSnapshot() {
        final Location networkId = this.nodePosition;
        // 读取顺序：先快照、后纪元——若两步之间发生失效递增，会判定不命中而重扫（fail-safe）
        final ItemSnapshot snapshot = SHARED_ITEM_SNAPSHOTS.get(networkId);
        final long invalidationEpoch = currentInvalidationEpoch(networkId);
        final long writeEpoch = currentWriteEpoch(networkId);
        final int currentTick = Bukkit.getCurrentTick();
        if (snapshot != null) {
            if (snapshot.tick == currentTick) {
                if (snapshot.invalidationEpoch == invalidationEpoch) {
                    return snapshot;
                }
            } else if (snapshot.invalidationEpoch == invalidationEpoch
                && snapshot.writeEpoch == writeEpoch
                && System.currentTimeMillis() - snapshot.publishedAtMs <= SNAPSHOT_MAX_AGE_MS) {
                return snapshot;
            }
        }

        final ItemHashMap<Long> itemStacks = new ItemHashMap<>();

        // Barrels
        for (BarrelIdentity barrelIdentity : getOutputAbleBarrels()) {
            addKeyedAmount(itemStacks, barrelIdentity.getItemStack(), barrelIdentity.getAmount());
        }

        // Cargo storage units
        for (StorageUnitData cache : getOutputAbleDrawerData().values()) {
            for (ItemContainer itemContainer : cache.getStoredItems()) {
                addKeyedAmount(itemStacks, itemContainer.getSample(), itemContainer.getAmount());
            }
        }

        // SimpleStorage chests（细胞语义：多格多物品）
        for (SimpleStorageChest chest : getOutputAbleSimpleStorageChests()) {
            for (ItemStack itemStack : chest.getSlotItems()) {
                addKeyedAmount(itemStacks, itemStack, itemStack);
            }
        }

        for (BlockMenu blockMenu : getAdvancedGreedyBlockMenus()) {
            ItemStack template = blockMenu.getItemInSlot(AdvancedGreedyBlock.TEMPLATE_SLOT);
            if (template == null || template.getType() == Material.AIR) continue;
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                final ItemStack identity = itemStack == null || itemStack.getType() == Material.AIR ? template : itemStack;
                addKeyedAmount(itemStacks, identity, itemStack);
            }
        }

        for (BlockMenu blockMenu : getGreedyBlockMenus()) {
            ItemStack template = blockMenu.getItemInSlot(NetworkGreedyBlock.TEMPLATE_SLOT);
            if (template == null || template.getType() == Material.AIR) continue;
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            final ItemStack itemStack = blockMenu.getItemInSlot(slots[0]);
            final ItemStack identity = itemStack == null || itemStack.getType() == Material.AIR ? template : itemStack;
            addKeyedAmount(itemStacks, identity, itemStack);
        }

        for (BlockMenu blockMenu : getCrafterOutputs()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    addKeyedAmount(itemStacks, itemStack, itemStack);
                }
            }
        }

        for (BlockMenu blockMenu : getCellMenus()) {
            if (isFakeCell(blockMenu)) continue;
            for (int slot : CELL_AVAILABLE_SLOTS) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    addKeyedAmount(itemStacks, itemStack, itemStack);
                }
            }
        }

        // Cell Drives：直取 keyed 视图，零身份构造合并
        ItemHashMap<Long> cellItems = CellDrive.getStorage().getAllCellItemsKeyed(driveCache, getOutputAbleCellDriveMenus());
        for (Map.Entry<ItemKey, Long> entry : cellItems.keyEntrySet()) {
            ItemKey key = entry.getKey();
            Long current = itemStacks.getKey(key);
            itemStacks.putKey(key, (current == null ? 0L : current) + entry.getValue());
        }

        // 发布快照：用读取开始前捕获的纪元打戳——扫描中途发生的失效递增会让本快照立即过期重扫；
        // ItemStack 出口视图惰性构建（见 ItemSnapshot.items）
        ItemSnapshot published = new ItemSnapshot(
            itemStacks.keyedView(), invalidationEpoch, writeEpoch, currentTick, System.currentTimeMillis());
        SHARED_ITEM_SNAPSHOTS.put(networkId, published);
        evictStaleSnapshotNetworks();
        return published;
    }

    /** 内部聚合：以精确身份（保留全部 meta，仅数量归一）为键累计数量，每条目一次深度哈希。 */
    private static void addKeyedAmount(ItemHashMap<Long> all, @Nullable ItemStack key, long amount) {
        if (key == null || key.getType() == Material.AIR) return;
        ItemKey itemKey = ItemKey.exact(key);
        Long current = all.getKey(itemKey);
        all.putKey(itemKey, (current == null ? 0L : current) + amount);
    }

    private static void addKeyedAmount(ItemHashMap<Long> all, @Nullable ItemStack key, @Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;
        addKeyedAmount(all, key, item.getAmount());
    }

    /** 清理长期无发布的网络快照与纪元条目，防止网络拆除后残留。 */
    private static void evictStaleSnapshotNetworks() {
        if (SHARED_ITEM_SNAPSHOTS.size() <= 1) {
            return;
        }
        long now = System.currentTimeMillis();
        SHARED_ITEM_SNAPSHOTS.entrySet().removeIf(entry -> {
            if (now - entry.getValue().publishedAtMs() > SNAPSHOT_IDLE_MS) {
                INVALIDATION_EPOCHS.remove(entry.getKey());
                WRITE_EPOCHS.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }

    // fallback api, don't remove
    public Map<ItemStack, Integer> getAllNetworkItems() {
        return getAllNetworkItemsLongType().entrySet()
            .stream()
            .map(e -> Map.entry(e.getKey(), e.getValue() > Integer.MAX_VALUE ? Integer.MAX_VALUE : e.getValue().intValue()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a
            ));
    }

    public Set<BarrelIdentity> getBarrels() {
        if (barrels != null) return barrels;

        barrels = searchBarrels(AccessMode.ALL);
        new NetworkRootLocateStorageEvent(this, StorageType.BARREL, true, true, Bukkit.isPrimaryThread()).callEvent();
        return barrels;
    }

    public Set<Location> getNodes(NodeType type) {
        return indexes.get(type).locations;
    }

    public Set<BlockMenu> getNodeMenus(NodeType type) {
        return indexes.get(type).getMenus();
    }

    private Map<Location, StorageUnitData> searchDrawers(AccessMode mode) {
        final Set<Location> addedLocations = new HashSet<>();
        final Map<Location, StorageUnitData> dataSet = new HashMap<>();

        var monitors = new HashSet<>(getNodes(NodeType.STORAGE_MONITOR));
        if (mode.isInputAccess()) monitors.addAll(getNodes(NodeType.INPUT_ONLY_MONITOR));
        if (mode.isOutputAccess()) monitors.addAll(getNodes(NodeType.OUTPUT_ONLY_MONITOR));

        for (Location cellLocation : monitors) {
            final BlockFace face = NetworkDirectional.getSelectedFace(cellLocation);
            if (face == null) continue;

            final Location testLocation = cellLocation.clone().add(face.getDirection());
            if (!addedLocations.add(testLocation)) continue;

            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(testLocation);
            if (slimefunItem instanceof NetworksDrawer) {
                final StorageUnitData data = getCargoStorageUnitData(testLocation);
                if (data != null) dataSet.put(testLocation, data);
            }
        }

        return dataSet;
    }

    public Map<Location, StorageUnitData> getDrawerData() {
        if (drawerData != null) return drawerData;

        drawerData = searchDrawers(AccessMode.ALL);
        new NetworkRootLocateStorageEvent(this, StorageType.DRAWER, true, true, Bukkit.isPrimaryThread()).callEvent();
        return drawerData;
    }

    public Set<BlockMenu> getCellMenus() {
        return getNodeMenus(NodeType.CELL);
    }

    public Set<BlockMenu> getCellDriveMenus() {
        if (this.cellDriveMenus != null) return this.cellDriveMenus;

        this.cellDriveMenus = searchCellDriveMenus(AccessMode.ALL);
        return this.cellDriveMenus;
    }

    public Set<BlockMenu> getInputAbleCellDriveMenus() {
        if (inputAbleCellDriveMenus != null) return inputAbleCellDriveMenus;

        inputAbleCellDriveMenus = searchCellDriveMenus(AccessMode.INPUT);
        return inputAbleCellDriveMenus;
    }

    public Set<BlockMenu> getOutputAbleCellDriveMenus() {
        if (outputAbleCellDriveMenus != null) return outputAbleCellDriveMenus;

        outputAbleCellDriveMenus = searchCellDriveMenus(AccessMode.OUTPUT);
        return outputAbleCellDriveMenus;
    }

    private Set<BlockMenu> searchCellDriveMenus(AccessMode mode) {
        final Set<BlockMenu> menus = new HashSet<>();
        final Set<Location> addedLocations = new HashSet<>();
        var monitors = new HashSet<>(getNodes(NodeType.STORAGE_MONITOR));
        if (mode.isInputAccess()) monitors.addAll(getNodes(NodeType.INPUT_ONLY_MONITOR));
        if (mode.isOutputAccess()) monitors.addAll(getNodes(NodeType.OUTPUT_ONLY_MONITOR));

        for (Location monitorLocation : monitors) {
            final BlockFace face = NetworkDirectional.getSelectedFace(monitorLocation);
            if (face == null) continue;

            final Location testLocation = monitorLocation.clone().add(face.getDirection());
            if (addedLocations.contains(testLocation)) continue;

            addedLocations.add(testLocation);
            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(testLocation);
            if (slimefunItem instanceof CellDrive) {
                BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu != null) menus.add(menu);
            }
        }
        return menus;
    }

    public Set<BlockMenu> getCrafterOutputs() {
        return getNodeMenus(NodeType.CRAFTER);
    }

    public Set<BlockMenu> getGreedyBlockMenus() {
        return getNodeMenus(NodeType.GREEDY_BLOCK);
    }

    public Set<BlockMenu> getAdvancedGreedyBlockMenus() {
        return getNodeMenus(NodeType.ADVANCED_GREEDY_BLOCK);
    }

    public static void addAmount(Map<ItemStack, Long> all, ItemStack key, @Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;
        long current = all.getOrDefault(key, 0L);
        all.put(key, current + item.getAmount());
    }

    public static void addAmount(Map<ItemStack, Long> all, ItemStack key, long amount) {
        all.put(key, all.getOrDefault(key, 0L) + amount);
    }

    public boolean contains(ItemStack itemStack) {
        return contains(new ItemRequest(itemStack, 1));
    }

    public boolean contains(ItemRequest request) {
        if (request.getItemStack() == null || request.getItemStack().getType().isAir()) return false;

        long found = 0;

        // Barrels
        for (BarrelIdentity barrelIdentity : getOutputAbleBarrels()) {
            if (!StackUtils.itemsMatch(request, barrelIdentity.getItemStack())) continue;

            if (barrelIdentity instanceof InfinityBarrel) {
                found += Math.max(0, barrelIdentity.getAmount() - 2);
            } else {
                found += barrelIdentity.getAmount();
            }

            if (found >= request.getAmount()) return true;
        }

        // Crafters
        for (BlockMenu blockMenu : getCrafterOutputs()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (!StackUtils.itemsMatch(request, itemStack)) continue;

                found += itemStack.getAmount();

                // Escape if found all we need
                if (found >= request.getAmount()) return true;
            }
        }

        for (StorageUnitData cache : getOutputAbleDrawerData().values()) {
            final List<ItemContainer> storedItems = cache.getStoredItems();
            for (ItemContainer itemContainer : storedItems) {
                if (!StackUtils.itemsMatch(request, itemContainer.getItemStack())) continue;

                found += itemContainer.getAmount();
                if (found >= request.getAmount()) return true;
            }
        }

        for (BlockMenu blockMenu : getAdvancedGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (!StackUtils.itemsMatch(request, itemStack)) continue;

                found += itemStack.getAmount();
                if (found >= request.getAmount()) return true;
            }
        }

        // Greedy Blocks
        for (BlockMenu blockMenu : getGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            final ItemStack itemStack = blockMenu.getItemInSlot(slots[0]);
            if (!StackUtils.itemsMatch(request, itemStack)) continue;

            found += itemStack.getAmount();
            if (found >= request.getAmount()) return true;
        }

        // Cells
        for (BlockMenu blockMenu : getCellMenus()) {
            if (isFakeCell(blockMenu)) continue;
            for (int slot : CELL_AVAILABLE_SLOTS) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (!StackUtils.itemsMatch(request, itemStack)) continue;

                found += itemStack.getAmount();
                if (found >= request.getAmount()) return true;
            }
        }

        // SimpleStorage chests
        for (SimpleStorageChest chest : getOutputAbleSimpleStorageChests()) {
            for (ItemStack itemStack : chest.getSlotItems()) {
                if (!StackUtils.itemsMatch(request, itemStack)) continue;

                found += itemStack.getAmount();
                if (found >= request.getAmount()) return true;
            }
        }

        // Cell Drives
        found += CellDrive.getStorage().getAmount(driveCache, getOutputAbleCellDriveMenus(), request.getItemStack());

        return found >= request.getAmount();
    }

    public int getAmount(ItemStack itemStack) {
        long totalAmount = 0;
        for (BlockMenu blockMenu : getAdvancedGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack inputSlotItem = blockMenu.getItemInSlot(slot);
                if (inputSlotItem != null && StackUtils.itemsMatch(inputSlotItem, itemStack)) {
                    totalAmount += inputSlotItem.getAmount();
                }
            }
        }

        for (BlockMenu blockMenu : getGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            ItemStack inputSlotItem = blockMenu.getItemInSlot(slots[0]);
            if (inputSlotItem != null && StackUtils.itemsMatch(inputSlotItem, itemStack)) {
                totalAmount += inputSlotItem.getAmount();
            }
        }

        for (BarrelIdentity barrelIdentity : getOutputAbleBarrels()) {
            if (StackUtils.itemsMatch(barrelIdentity, itemStack)) {
                totalAmount += barrelIdentity.getAmount();
                if (barrelIdentity instanceof InfinityBarrel) {
                    totalAmount -= 2;
                }
            }
        }
        for (StorageUnitData cache : getOutputAbleDrawerData().values()) {
            final List<ItemContainer> storedItems = cache.getStoredItems();
            for (ItemContainer itemContainer : storedItems) {
                if (StackUtils.itemsMatch(itemContainer, itemStack)) {
                    totalAmount += itemContainer.getAmount();
                }
            }
        }

        for (BlockMenu blockMenu : getCellMenus()) {
            if (isFakeCell(blockMenu)) continue;
            for (int slot : CELL_AVAILABLE_SLOTS) {
                final ItemStack cellItem = blockMenu.getItemInSlot(slot);
                if (cellItem != null && StackUtils.itemsMatch(cellItem, itemStack)) {
                    totalAmount += cellItem.getAmount();
                }
            }
        }

        // SimpleStorage chests
        for (SimpleStorageChest chest : getOutputAbleSimpleStorageChests()) {
            totalAmount += chest.getAmount(itemStack);
        }

        // Cell Drives
        totalAmount += CellDrive.getStorage().getAmount(driveCache, getOutputAbleCellDriveMenus(), itemStack);

        if (totalAmount > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return (int) totalAmount;
        }
    }

    /**
     * 估算"此刻"向网络存入 {@code itemStack} 还能被吸收的最大数量（保守上界）
     * 忽略 voidExcess
     */
    public long getLeftCapacityFor(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR || StackUtils.isBlacklisted(itemStack)) {
            return 0L;
        }

        boolean greedyMatched = false;
        long greedyRoom = 0L;
        for (BlockMenu blockMenu : getAdvancedGreedyBlockMenus()) {
            final ItemStack template = blockMenu.getItemInSlot(AdvancedGreedyBlock.TEMPLATE_SLOT);
            if (template != null && !template.getType().isAir() && StackUtils.itemsMatch(template, itemStack)) {
                greedyRoom = slotRoomOf(blockMenu, ADVANCED_GREEDY_BLOCK_AVAILABLE_SLOTS, itemStack);
                greedyMatched = true;
                break;
            }
        }
        if (!greedyMatched) {
            for (BlockMenu blockMenu : getGreedyBlockMenus()) {
                final ItemStack template = blockMenu.getItemInSlot(NetworkGreedyBlock.TEMPLATE_SLOT);
                if (template != null && !template.getType().isAir() && StackUtils.itemsMatch(template, itemStack)) {
                    greedyRoom = slotRoomOf(blockMenu, new int[]{GREEDY_BLOCK_AVAILABLE_SLOT}, itemStack);
                    greedyMatched = true;
                    break;
                }
            }
        }
        if (greedyMatched) {
            return greedyRoom;
        }

        long total = 0L;

        for (BarrelIdentity barrel : getInputAbleBarrels()) {
            if (barrel.getItemStack() == null || !StackUtils.itemsMatch(barrel, itemStack)) {
                continue;
            }
            if (barrel instanceof NetworkStorage) {
                final QuantumCache cache = NetworkQuantumStorage.getCaches().get(barrel.getLocation());
                if (cache == null) continue;
                total += Math.max(0L, cache.getLimitLong() - cache.getAmountLong());
            } else if (Networks.getSupportedPluginManager().isInfinityExpansion() && barrel instanceof InfinityBarrel) {
                total += Math.max(0L, barrel.getLimit() - barrel.getAmount());
            } else if (Networks.getSupportedPluginManager().isTsingshanTechnology() && barrel instanceof TsingshanBarrel) {
                total += Math.max(0L, barrel.getLimit() - barrel.getAmount());
            } else if (Networks.getSupportedPluginManager().isFluffyMachines() && barrel instanceof FluffyBarrel) {
                // 蓬松桶：深层库存靠自身 tick 从输入槽搬运，瞬间可输入的只有输入槽空位
                final BlockMenu menu = StorageCacheUtils.getMenu(barrel.getLocation());
                if (menu == null) continue;
                total += slotRoomOf(menu, barrel.getInputSlot(), itemStack);
            }
        }

        for (SimpleStorageChest chest : getInputAbleSimpleStorageChests()) {
            total += chest.getRoom(itemStack);
        }

        for (StorageUnitData unit : getMapInputAbleCargoStorageUnits().values()) {
            final long eachMax = unit.getSizeType().getEachMaxSize();
            for (ItemContainer container : unit.getStoredItems()) {
                if (container.isSimilar(itemStack)) {
                    total += Math.max(0L, eachMax - container.getAmount());
                }
            }
        }

        if (!getInputAbleCellDriveMenus().isEmpty()) {
            final DriveStorage.CapacityProbe probe = CellDrive.getStorage().probeReceive(
                driveCache, getInputAbleCellDriveMenus(), new ItemKey(itemStack), Long.MAX_VALUE);
            total += probe.capacity();
        }

        for (BlockMenu blockMenu : getCellMenus()) {
            if (isFakeCell(blockMenu)) continue;
            total += slotRoomOf(blockMenu, CELL_AVAILABLE_SLOTS, itemStack);
        }

        return total;
    }

    private static long slotRoomOf(BlockMenu menu, int[] slots, ItemStack template) {
        long room = 0L;
        for (int slot : slots) {
            final ItemStack slotItem = menu.getItemInSlot(slot);
            if (slotItem == null || slotItem.getType() == Material.AIR) {
                room += template.getMaxStackSize();
            } else if (StackUtils.itemsMatch(slotItem, template)) {
                room += Math.max(0, slotItem.getMaxStackSize() - slotItem.getAmount());
            }
        }
        return room;
    }

    public HashMap<ItemStack, Long> getAmount(Set<ItemStack> itemStacks) {
        HashMap<ItemStack, Long> totalAmounts = new HashMap<>();
        for (BlockMenu menu : getAdvancedGreedyBlockMenus()) {
            int[] slots = menu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack inputSlotItem = menu.getItemInSlot(slot);
                if (inputSlotItem != null) {
                    for (ItemStack itemStack : itemStacks) {
                        if (StackUtils.itemsMatch(inputSlotItem, itemStack)) {
                            totalAmounts.put(
                                itemStack, totalAmounts.getOrDefault(itemStack, 0L) + inputSlotItem.getAmount());
                        }
                    }
                }
            }
        }

        for (BlockMenu blockMenu : getGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            ItemStack inputSlotItem = blockMenu.getItemInSlot(slots[0]);
            if (inputSlotItem != null) {
                for (ItemStack itemStack : itemStacks) {
                    if (StackUtils.itemsMatch(inputSlotItem, itemStack)) {
                        totalAmounts.put(
                            itemStack, totalAmounts.getOrDefault(itemStack, 0L) + inputSlotItem.getAmount());
                    }
                }
            }
        }

        for (BarrelIdentity barrelIdentity : getOutputAbleBarrels()) {
            for (ItemStack itemStack : itemStacks) {
                if (StackUtils.itemsMatch(barrelIdentity, itemStack)) {
                    long totalAmount = barrelIdentity.getAmount();
                    if (barrelIdentity instanceof InfinityBarrel) {
                        totalAmount -= 2;
                    }
                    totalAmounts.put(itemStack, totalAmounts.getOrDefault(itemStack, 0L) + totalAmount);
                }
            }
        }
        for (StorageUnitData cache : getOutputAbleDrawerData().values()) {
            final List<ItemContainer> storedItems = cache.getStoredItems();
            for (ItemContainer itemContainer : storedItems) {
                for (ItemStack itemStack : itemStacks) {
                    if (StackUtils.itemsMatch(itemContainer, itemStack)) {
                        long totalAmount = itemContainer.getAmount();
                        totalAmounts.put(itemStack, totalAmounts.getOrDefault(itemStack, 0L) + totalAmount);
                    }
                }
            }
        }

        for (BlockMenu blockMenu : getCellMenus()) {
            if (isFakeCell(blockMenu)) continue;
            for (int slot : CELL_AVAILABLE_SLOTS) {
                final ItemStack cellItem = blockMenu.getItemInSlot(slot);
                if (cellItem != null) {
                    for (ItemStack itemStack : itemStacks) {
                        if (StackUtils.itemsMatch(cellItem, itemStack)) {
                            totalAmounts.put(
                                itemStack, totalAmounts.getOrDefault(itemStack, 0L) + cellItem.getAmount());
                        }
                    }
                }
            }
        }

        // SimpleStorage chests
        for (SimpleStorageChest chest : getOutputAbleSimpleStorageChests()) {
            for (ItemStack stored : chest.getSlotItems()) {
                for (ItemStack itemStack : itemStacks) {
                    if (StackUtils.itemsMatch(stored, itemStack)) {
                        totalAmounts.put(itemStack,
                            totalAmounts.getOrDefault(itemStack, 0L) + stored.getAmount());
                    }
                }
            }
        }

        // Cell Drives
        for (ItemStack itemStack : itemStacks) {
            long cellAmount = CellDrive.getStorage().getAmount(driveCache, getOutputAbleCellDriveMenus(), itemStack);
            if (cellAmount > 0) {
                totalAmounts.put(itemStack, totalAmounts.getOrDefault(itemStack, 0L) + cellAmount);
            }
        }

        return totalAmounts;
    }

    @Override
    public long retrieveBlockCharge() {
        return 0;
    }

    public void addRootPower(long power) {
        rootPower.getAndAdd(power);
    }

    public long getRootPower() {
        return rootPower.get();
    }

    public void removeRootPower(long power) {
        if (power <= 0) {
            addRootPower(-power);
            return;
        }

        long removed = 0;
        for (Location node : getNodes(NodeType.POWER_NODE)) {
            final SlimefunItem item = StorageCacheUtils.getSfItem(node);
            if (item instanceof NetworkPowerNode powerNode) {
                final int charge = powerNode.getCharge(node);
                if (charge <= 0) {
                    continue;
                }
                final int toRemove = (int) Math.min(power - removed, charge);
                powerNode.removeCharge(node, toRemove);
                removed += toRemove;
            }
            if (removed >= power) break;
        }
        rootPower.getAndAdd(-removed);
    }

    public List<ItemStack> getItemStacks(@Nullable Location accesor, List<ItemRequest> itemRequests) {
        List<ItemStack> retrievedItems = new ArrayList<>();
        for (ItemRequest request : itemRequests) {
            ItemStack retrieved = getItemStack(accesor, request);
            if (retrieved != null) retrievedItems.add(retrieved);
        }
        return retrievedItems;
    }

    public List<BarrelIdentity> getBarrels(
        Predicate<BarrelIdentity> filter,
        NetworkRootLocateStorageEvent.Strategy strategy,
        boolean includeEmpty
    ) {
        final Set<Location> addedLocations = ConcurrentHashMap.newKeySet();
        final List<BarrelIdentity> barrelSet = new ArrayList<>();

        final Set<Location> monitors = new HashSet<>(getNodes(NodeType.STORAGE_MONITOR));
        monitors.addAll(getNodes(NodeType.INPUT_ONLY_MONITOR));
        monitors.addAll(getNodes(NodeType.OUTPUT_ONLY_MONITOR));
        for (Location cellLocation : monitors) {
            final BlockFace face = NetworkDirectional.getSelectedFace(cellLocation);
            if (face == null) continue;

            final Location testLocation = cellLocation.clone().add(face.getDirection());
            if (!addedLocations.add(testLocation)) continue;

            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(testLocation);

            if (Networks.getSupportedPluginManager().isInfinityExpansion()
                && slimefunItem instanceof StorageUnit unit) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) continue;

                final InfinityBarrel infinityBarrel = getInfinityBarrel(menu, unit, includeEmpty);
                if (infinityBarrel != null && filter.test(infinityBarrel)) barrelSet.add(infinityBarrel);
            }
            else if (Networks.getSupportedPluginManager().isTsingshanTechnology()
                && slimefunItem instanceof io.gitee.crankygoudan.tsingshantechnology.items.chucun.StorageUnit unit) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) continue;

                final TsingshanBarrel tsingshanBarrel = getTsingshanBarrel(menu, unit, includeEmpty);
                if (tsingshanBarrel != null && filter.test(tsingshanBarrel)) barrelSet.add(tsingshanBarrel);
            }
            else if (Networks.getSupportedPluginManager().isFluffyMachines() && slimefunItem instanceof Barrel barrel) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) continue;

                final FluffyBarrel fluffyBarrel = getFluffyBarrel(menu, barrel, includeEmpty);
                if (fluffyBarrel != null && filter.test(fluffyBarrel)) barrelSet.add(fluffyBarrel);
            }
            else if (slimefunItem instanceof NetworkQuantumStorage) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) continue;

                final NetworkStorage storage = getNetworkStorage(menu, includeEmpty);
                if (storage != null && filter.test(storage)) {
                    barrelSet.add(storage);
                }
            }
        }

        new NetworkRootLocateStorageEvent(this, StorageType.BARREL, strategy, Bukkit.isPrimaryThread()).callEvent();
        return barrelSet;
    }

    public Map<Location, StorageUnitData> getDrawerData(NetworkRootLocateStorageEvent.Strategy strategy) {
        final Set<Location> addedLocations = ConcurrentHashMap.newKeySet();
        final Map<Location, StorageUnitData> dataSet = new HashMap<>();

        final Set<Location> monitor = new HashSet<>(getNodes(NodeType.STORAGE_MONITOR));
        monitor.addAll(getNodes(NodeType.INPUT_ONLY_MONITOR));
        monitor.addAll(getNodes(NodeType.OUTPUT_ONLY_MONITOR));
        for (Location cellLocation : monitor) {
            final BlockFace face = NetworkDirectional.getSelectedFace(cellLocation);
            if (face == null) continue;

            final Location testLocation = cellLocation.clone().add(face.getDirection());
            if (!addedLocations.add(testLocation)) continue;

            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(testLocation);
            if (slimefunItem instanceof NetworksDrawer) {
                final StorageUnitData data = getCargoStorageUnitData(testLocation);
                if (data != null) dataSet.put(testLocation, data);
            }
        }

        new NetworkRootLocateStorageEvent(this, StorageType.DRAWER, strategy, Bukkit.isPrimaryThread()).callEvent();
        return dataSet;
    }

    public Set<BarrelIdentity> getInputAbleBarrels() {
        if (inputAbleBarrels != null) return inputAbleBarrels;

        inputAbleBarrels = searchBarrels(AccessMode.INPUT);
        new NetworkRootLocateStorageEvent(this, StorageType.BARREL, true, false, Bukkit.isPrimaryThread()).callEvent();
        return inputAbleBarrels;
    }

    private Set<BarrelIdentity> searchBarrels(AccessMode mode) {
        final Set<Location> addedLocations = new HashSet<>();
        final Set<BarrelIdentity> barrelSet = new HashSet<>();

        final Set<Location> monitor = new HashSet<>(getNodes(NodeType.STORAGE_MONITOR));
        if (mode.isInputAccess()) monitor.addAll(getNodes(NodeType.INPUT_ONLY_MONITOR));
        if (mode.isOutputAccess()) monitor.addAll(getNodes(NodeType.OUTPUT_ONLY_MONITOR));
        for (Location cellLocation : monitor) {
            final BlockFace face = NetworkDirectional.getSelectedFace(cellLocation);
            if (face == null) continue;

            final Location testLocation = cellLocation.clone().add(face.getDirection());
            if (!addedLocations.add(testLocation)) continue;

            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(testLocation);
            final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
            if (menu == null) continue;
            if (Networks.getSupportedPluginManager().isInfinityExpansion() && slimefunItem instanceof StorageUnit unit) {
                final InfinityBarrel infinityBarrel = getInfinityBarrel(menu, unit);
                if (infinityBarrel != null) barrelSet.add(infinityBarrel);
            }
            else if (Networks.getSupportedPluginManager().isTsingshanTechnology()
                && slimefunItem instanceof io.gitee.crankygoudan.tsingshantechnology.items.chucun.StorageUnit unit) {
                final TsingshanBarrel tsingshanBarrel = getTsingshanBarrel(menu, unit);
                if (tsingshanBarrel != null) barrelSet.add(tsingshanBarrel);
            }
            else if (Networks.getSupportedPluginManager().isFluffyMachines() && slimefunItem instanceof Barrel barrel) {
                final FluffyBarrel fluffyBarrel = getFluffyBarrel(menu, barrel);
                if (fluffyBarrel != null) barrelSet.add(fluffyBarrel);
            }
            else if (slimefunItem instanceof NetworkQuantumStorage) {
                final NetworkStorage storage = getNetworkStorage(menu);
                if (storage != null) barrelSet.add(storage);
            }
        }

        return barrelSet;
    }

    public Set<BarrelIdentity> getOutputAbleBarrels() {
        if (outputAbleBarrels != null) return outputAbleBarrels;

        outputAbleBarrels = searchBarrels(AccessMode.OUTPUT);
        new NetworkRootLocateStorageEvent(this, StorageType.BARREL, false, true, Bukkit.isPrimaryThread()).callEvent();
        return outputAbleBarrels;
    }

    /** 监视器面板所贴的 SimpleStorage 箱子（铁/金/钻石/存储单元），细胞语义接入。 */
    private Set<SimpleStorageChest> searchSimpleStorageChests(AccessMode mode) {
        if (!Networks.getSupportedPluginManager().isSimpleStorage()) return Set.of();

        final Set<Location> addedLocations = new HashSet<>();
        final Set<SimpleStorageChest> chests = new HashSet<>();

        final Set<Location> monitor = new HashSet<>(getNodes(NodeType.STORAGE_MONITOR));
        if (mode.isInputAccess()) monitor.addAll(getNodes(NodeType.INPUT_ONLY_MONITOR));
        if (mode.isOutputAccess()) monitor.addAll(getNodes(NodeType.OUTPUT_ONLY_MONITOR));
        for (Location cellLocation : monitor) {
            final BlockFace face = NetworkDirectional.getSelectedFace(cellLocation);
            if (face == null) continue;

            final Location testLocation = cellLocation.clone().add(face.getDirection());
            if (!addedLocations.add(testLocation)) continue;

            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(testLocation);
            if (!(Networks.getSupportedPluginManager().isSimpleStorage()
                && slimefunItem instanceof io.github.sefiraat.simplestorage.items.chests.SimpleChest)) {
                continue;
            }

            final SimpleStorageChest chest = SimpleStorageChest.of(testLocation);
            if (chest != null) chests.add(chest);
        }
        return chests;
    }

    public Set<SimpleStorageChest> getInputAbleSimpleStorageChests() {
        if (inputAbleSimpleStorageChests != null) return inputAbleSimpleStorageChests;

        inputAbleSimpleStorageChests = searchSimpleStorageChests(AccessMode.INPUT);
        return inputAbleSimpleStorageChests;
    }

    public Set<SimpleStorageChest> getOutputAbleSimpleStorageChests() {
        if (outputAbleSimpleStorageChests != null) return outputAbleSimpleStorageChests;

        outputAbleSimpleStorageChests = searchSimpleStorageChests(AccessMode.OUTPUT);
        return outputAbleSimpleStorageChests;
    }

    public Map<Location, StorageUnitData> getInputAbleDrawerData() {
        if (inputAbleDrawerData != null) return inputAbleDrawerData;

        inputAbleDrawerData = searchDrawers(AccessMode.INPUT);
        new NetworkRootLocateStorageEvent(this, StorageType.DRAWER, true, false, Bukkit.isPrimaryThread()).callEvent();
        return inputAbleDrawerData;
    }

    public Map<Location, StorageUnitData> getOutputAbleDrawerData() {
        if (outputAbleDrawerData != null) return outputAbleDrawerData;

        outputAbleDrawerData = searchDrawers(AccessMode.OUTPUT);
        new NetworkRootLocateStorageEvent(this, StorageType.DRAWER, false, true, Bukkit.isPrimaryThread()).callEvent();
        return outputAbleDrawerData;
    }

    @Nullable
    public BarrelIdentity accessInputAbleBarrel(Location barrelLocation) {
        return getMapInputAbleBarrels().get(barrelLocation);
    }

    @Nullable
    public BarrelIdentity accessOutputAbleBarrel(Location barrelLocation) {
        return getMapOutputAbleBarrels().get(barrelLocation);
    }

    @Nullable
    public StorageUnitData accessInputAbleDrawerData(Location drawerLocation) {
        return accessInputAbleCargoStorageUnitData(drawerLocation);
    }

    @Nullable
    public StorageUnitData accessOutputAbleDrawerData(Location drawerLocation) {
        return accessOutputAbleCargoStorageUnitData(drawerLocation);
    }

    @Nullable
    public StorageUnitData accessInputAbleCargoStorageUnitData(Location storageUnitLocation) {
        return getMapInputAbleCargoStorageUnits().get(storageUnitLocation);
    }

    @Nullable
    public StorageUnitData accessOutputAbleCargoStorageUnitData(Location storageUnitLocation) {
        return getMapOutputAbleCargoStorageUnits().get(storageUnitLocation);
    }

    @Nullable
    public ItemStack requestItem(@Nullable Location accessor, ItemRequest request) {
        return getItemStack(accessor, request);
    }

    @Nullable
    public ItemStack requestItem(@Nullable Location accessor, ItemStack itemStack) {
        return requestItem(accessor, new ItemRequest(itemStack, itemStack.getAmount()));
    }

    public void tryRecord(@Nullable Location accessor, ItemRequest request) {
        if (accessor == null) return;
        if (recordFlow && itemFlowRecord != null) {
            itemFlowRecord.addAction(accessor, request);
        }
    }

    public @Nullable ItemStack getItemStack(@Nullable Location accessor, ItemRequest request) {
        return getItemStack(accessor, request, null);
    }

    /**
     * @param accessor 取物的机器位置
     * @param request  请求（会被消费，{@code receiveAmount} 递减剩余需求）
     * @param option   物品匹配选项（忽略项），为 null 时使用 {@link MatchOption#DEFAULT}
     * @return 匹配 {@code option} 的物品；数量为 0 时返回 null
     */
    public @Nullable ItemStack getItemStack(
        @Nullable Location accessor, ItemRequest request, @Nullable MatchOption option) {
        ItemStack stackToReturn = null;

        if (request.getAmount() <= 0) {
            FeedbackSendable.sendFeedback0(accessor, FeedbackType.ROOT_REQUEST_0);
            return null;
        }

        if (!allowAccessOutput(accessor)) {
            FeedbackSendable.sendFeedback0(accessor, FeedbackType.ROOT_LIMITING_ACCESS_OUTPUT);
            return null;
        }

        synchronized (RootWriteLock.get()) {
            bumpWriteEpoch();

            Map<Location, Integer> m = getPersistentAccessHistory(accessor);
            if (m != null) {
                // Netex - Cache start
                boolean found = false;
                List<Location> misses = new ArrayList<>();
                // Netex - Cache end
                for (Map.Entry<Location, Integer> entry : m.entrySet()) {
                    // try cache first
                    BarrelIdentity barrelIdentity = accessOutputAbleBarrel(entry.getKey());
                    if (barrelIdentity != null) {
                        // <editor-fold desc="do barrel">
                        final ItemStack itemStack = barrelIdentity.getItemStack();
                        if (!StackUtils.itemsMatch(request, itemStack, option)) {
                            // Netex - Cache start
                            misses.add(entry.getKey());
                            // Netex - Cache end
                            continue;
                        }

                        // Netex - Cache start
                        minusCacheMiss(accessor, entry.getKey());
                        found = true;
                        // Netex - Cache end

                        boolean infinity = barrelIdentity instanceof InfinityBarrel;
                        final ItemStack fetched = barrelIdentity.requestItem(request);
                        if (fetched == null
                            || fetched.getType() == Material.AIR
                            || (infinity && fetched.getAmount() == 1)) {
                            continue;
                        }

                        // 复验实际取到的物品：匹配用的是扫描快照，requestItem 读的是活状态，
                        // 两者之间存储内容可能已被更换（如清空后改存其他物品）。
                        // 只读型桶（Infinity/Tsingshan）尚未扣量，跳过即无损。
                        if (!StackUtils.itemsMatch(request, fetched, option)) {
                            continue;
                        }

                        // Stack is null, so we can fill it here
                        if (stackToReturn == null) {
                            stackToReturn = fetched.clone();
                            stackToReturn.setAmount(0);
                        }

                        final int preserveAmount = infinity ? fetched.getAmount() - 1 : fetched.getAmount();

                        int delta = request.getAmount();
                        if (delta <= preserveAmount) {
                            // Netex - Reduce start
                            uncontrolAccessOutput(accessor);
                            // Netex - Reduce end
                            stackToReturn.setAmount(stackToReturn.getAmount() + delta);
                            request.receiveAmount(delta);
                            fetched.setAmount(fetched.getAmount() - delta);
                            // Netex - Record start
                            tryRecord(accessor, request);
                            // Netex - Record end
                            return stackToReturn;
                        } else {
                            stackToReturn.setAmount(stackToReturn.getAmount() + preserveAmount);
                            request.receiveAmount(preserveAmount);
                            fetched.setAmount(fetched.getAmount() - preserveAmount);
                        }
                        // </editor-fold>
                    } else {
                        StorageUnitData data = accessOutputAbleCargoStorageUnitData(entry.getKey());
                        if (data != null) {
                            // <editor-fold desc="do drawer">
                            ItemStack take = data.requestItem(accessor, request);
                            if (take != null) {
                                // Netex - Cache start
                                minusCacheMiss(accessor, entry.getKey());
                                found = true;
                                // Netex - Cache end

                                if (stackToReturn == null) {
                                    stackToReturn = take.clone();
                                } else {
                                    stackToReturn.setAmount(stackToReturn.getAmount() + take.getAmount());
                                }
                                request.receiveAmount(take.getAmount());

                                if (request.getAmount() <= 0) {
                                    // Netex - Reduce start
                                    uncontrolAccessOutput(accessor);
                                    // Netex - Reduce end
                                    // Netex - Record start
                                    tryRecord(accessor, request);
                                    // Netex - Record end
                                    return stackToReturn;
                                }
                            } else {
                                // Netex - Cache start
                                misses.add(entry.getKey());
                                // Netex - Cache end
                            }
                            // </editor-fold>
                        } else {
                            // Netex - Cache start
                            misses.add(entry.getKey());
                            // Netex - Cache end
                        }
                    }
                }

                // Netex - Cache start
                if (!found) {
                    for (Location miss : misses) {
                        minusCacheMiss(accessor, miss);
                    }
                }
                // Netex - Cache end
            }

            // Barrels first
            for (BarrelIdentity barrelIdentity : getOutputAbleBarrels()) {
                // <editor-fold desc="do barrel">
                final ItemStack itemStack = barrelIdentity.getItemStack();

                if (itemStack == null || !StackUtils.itemsMatch(request, itemStack, option)) {
                    continue;
                }

                // Netex - Cache start
                addCountObservingAccessHistory(accessor, barrelIdentity.getLocation());
                // Netex - Cache end

                boolean infinity = barrelIdentity instanceof InfinityBarrel;
                final ItemStack fetched = barrelIdentity.requestItem(request);
                if (fetched == null || fetched.getType() == Material.AIR || (infinity && fetched.getAmount() == 1)) {
                    continue;
                }

                // 复验实际取到的物品：匹配用的是扫描快照，requestItem 读的是活状态，
                // 两者之间存储内容可能已被更换（如清空后改存其他物品）。
                // 只读型桶（Infinity/Tsingshan）尚未扣量，跳过即无损。
                if (!StackUtils.itemsMatch(request, fetched, option)) {
                    continue;
                }

                // Stack is null, so we can fill it here
                if (stackToReturn == null) {
                    stackToReturn = fetched.clone();
                    stackToReturn.setAmount(0);
                }

                final int preserveAmount = infinity ? fetched.getAmount() - 1 : fetched.getAmount();

                int delta = request.getAmount();
                if (delta <= preserveAmount) {
                    // Netex - Reduce start
                    uncontrolAccessOutput(accessor);
                    // Netex - Reduce end
                    stackToReturn.setAmount(stackToReturn.getAmount() + delta);
                    request.receiveAmount(delta);
                    fetched.setAmount(fetched.getAmount() - delta);
                    // Netex - Record start
                    tryRecord(accessor, request);
                    // Netex - Record end
                    return stackToReturn;
                } else {
                    stackToReturn.setAmount(stackToReturn.getAmount() + preserveAmount);
                    request.receiveAmount(preserveAmount);
                    fetched.setAmount(fetched.getAmount() - preserveAmount);
                }
                // </editor-fold>
            }

            // Units
            for (StorageUnitData cache : getOutputAbleDrawerData().values()) {
                // <editor-fold desc="do drawer">
                ItemStack take = cache.requestItem(accessor, request);
                if (take != null) {
                    // Netex - Cache start
                    addCountObservingAccessHistory(accessor, cache.getLastLocation());
                    // Netex - Cache end
                    if (stackToReturn == null) {
                        stackToReturn = take.clone();
                    } else {
                        stackToReturn.setAmount(stackToReturn.getAmount() + take.getAmount());
                    }
                    request.receiveAmount(take.getAmount());

                    if (request.getAmount() <= 0) {
                        // Netex - Reduce start
                        uncontrolAccessOutput(accessor);
                        // Netex - Reduce end
                        // Netex - Record start
                        tryRecord(accessor, request);
                        // Netex - Record end
                        return stackToReturn;
                    }
                }
                // </editor-fold>
            }

            // SimpleStorage chests
            for (SimpleStorageChest chest : getOutputAbleSimpleStorageChests()) {
                final ItemStack chestTake = chest.requestItem(request);
                if (chestTake != null) {
                    if (stackToReturn == null) {
                        stackToReturn = chestTake.clone();
                    } else {
                        stackToReturn.setAmount(stackToReturn.getAmount() + chestTake.getAmount());
                    }
                    request.receiveAmount(chestTake.getAmount());

                    if (request.getAmount() <= 0) {
                        // Netex - Reduce start
                        uncontrolAccessOutput(accessor);
                        // Netex - Reduce end
                        // Netex - Record start
                        tryRecord(accessor, request);
                        // Netex - Record end
                        return stackToReturn;
                    }
                }
            }

            // Cell Drives
            ItemStack take = CellDrive.getStorage().takeItem(driveCache, getOutputAbleCellDriveMenus(), request);
            if (take != null) {
                if (stackToReturn == null) {
                    stackToReturn = take.clone();
                } else {
                    stackToReturn.setAmount(stackToReturn.getAmount() + take.getAmount());
                }
                request.receiveAmount(take.getAmount());

                if (request.getAmount() <= 0) {
                    tryRecord(accessor, request);
                    return stackToReturn;
                }
            }

            // Cells
            for (BlockMenu blockMenu : getCellMenus()) {
                if (isFakeCell(blockMenu)) continue;
                for (int slot : CELL_AVAILABLE_SLOTS) {
                    final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                    if (itemStack == null
                        || itemStack.getType() == Material.AIR
                        || !StackUtils.itemsMatch(request, itemStack, option)) {
                        continue;
                    }

                    // Mark the Cell as dirty otherwise the changes will not save on shutdown
                    blockMenu.markDirty();

                    // If the return stack is null, we need to set it up
                    if (stackToReturn == null) {
                        stackToReturn = itemStack.clone();
                        stackToReturn.setAmount(0);
                    }

                    int delta = request.getAmount();
                    if (delta <= itemStack.getAmount()) {
                        // Netex - Reduce start
                        uncontrolAccessOutput(accessor);
                        // Netex - Reduce end
                        // We can't take more than this stack. Level to request amount, remove items and then return
                        stackToReturn.setAmount(stackToReturn.getAmount() + delta);
                        request.receiveAmount(delta);
                        itemStack.setAmount(itemStack.getAmount() - delta);
                        // Netex - Record start
                        tryRecord(accessor, request);
                        // Netex - Record end
                        return stackToReturn;
                    } else {
                        // We can take more than what is here, consume before trying to take more
                        stackToReturn.setAmount(stackToReturn.getAmount() + itemStack.getAmount());
                        request.receiveAmount(itemStack.getAmount());
                        itemStack.setAmount(0);
                    }
                }
            }

            // Crafters
            for (BlockMenu blockMenu : getCrafterOutputs()) {
                int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
                for (int slot : slots) {
                    final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                    if (itemStack == null
                        || itemStack.getType() == Material.AIR
                        || !StackUtils.itemsMatch(request, itemStack, option)) {
                        continue;
                    }

                    // Stack is null, so we can fill it here
                    if (stackToReturn == null) {
                        stackToReturn = itemStack.clone();
                        stackToReturn.setAmount(0);
                    }

                    int delta = request.getAmount();
                    if (delta <= itemStack.getAmount()) {
                        // Netex - Reduce start
                        uncontrolAccessOutput(accessor);
                        // Netex - Reduce end
                        stackToReturn.setAmount(stackToReturn.getAmount() + delta);
                        request.receiveAmount(delta);
                        itemStack.setAmount(itemStack.getAmount() - delta);
                        // Netex - Record start
                        tryRecord(accessor, request);
                        // Netex - Record end
                        return stackToReturn;
                    } else {
                        stackToReturn.setAmount(stackToReturn.getAmount() + itemStack.getAmount());
                        request.receiveAmount(itemStack.getAmount());
                        itemStack.setAmount(0);
                    }
                }
            }

            for (BlockMenu blockMenu : getAdvancedGreedyBlockMenus()) {
                int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
                for (int slot : slots) {
                    final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                    if (itemStack == null
                        || itemStack.getType() == Material.AIR
                        || !StackUtils.itemsMatch(request, itemStack, option)) {
                        continue;
                    }

                    // Stack is null, so we can fill it here
                    if (stackToReturn == null) {
                        stackToReturn = itemStack.clone();
                        stackToReturn.setAmount(0);
                    }

                    int delta = request.getAmount();
                    if (delta <= itemStack.getAmount()) {
                        // Netex - Reduce start
                        uncontrolAccessOutput(accessor);
                        // Netex - Reduce end
                        stackToReturn.setAmount(stackToReturn.getAmount() + delta);
                        request.receiveAmount(delta);
                        itemStack.setAmount(itemStack.getAmount() - delta);
                        // Netex - Record start
                        tryRecord(accessor, request);
                        // Netex - Record end
                        return stackToReturn;
                    } else {
                        stackToReturn.setAmount(stackToReturn.getAmount() + itemStack.getAmount());
                        request.receiveAmount(itemStack.getAmount());
                        itemStack.setAmount(0);
                    }
                }
            }

            // Greedy Blocks
            for (BlockMenu blockMenu : getGreedyBlockMenus()) {
                int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
                final ItemStack itemStack = blockMenu.getItemInSlot(slots[0]);
                if (itemStack == null
                    || itemStack.getType() == Material.AIR
                    || !StackUtils.itemsMatch(request, itemStack, option)) {
                    continue;
                }

                // Mark the Cell as dirty otherwise the changes will not save on shutdown
                blockMenu.markDirty();

                // If the return stack is null, we need to set it up
                if (stackToReturn == null) {
                    stackToReturn = itemStack.clone();
                    stackToReturn.setAmount(0);
                }

                int delta = request.getAmount();
                if (delta <= itemStack.getAmount()) {
                    // Netex - Reduce start
                    uncontrolAccessOutput(accessor);
                    // Netex - Reduce end
                    // We can't take more than this stack. Level to request amount, remove items and then return
                    stackToReturn.setAmount(stackToReturn.getAmount() + delta);
                    request.receiveAmount(delta);
                    itemStack.setAmount(itemStack.getAmount() - delta);
                    // Netex - Record start
                    tryRecord(accessor, request);
                    // Netex - Record end
                    return stackToReturn;
                } else {
                    // We can take more than what is here, consume before trying to take more
                    stackToReturn.setAmount(stackToReturn.getAmount() + itemStack.getAmount());
                    request.receiveAmount(itemStack.getAmount());
                    itemStack.setAmount(0);
                }
            }

            if (stackToReturn == null || stackToReturn.getAmount() == 0) {
                return null;
            }

            // Netex - Reduce start
            uncontrolAccessOutput(accessor);
            // Netex - Reduce end
            // Netex - Record start
            tryRecord(accessor, request);
            // Netex - Record end

            return stackToReturn;
        }
    }

    /**
     * 批量取料：一次网络走查服务多个请求，走查家族顺序与 {@link #getItemStack(Location, ItemRequest)} 完全一致
     * （访问历史优先 → 桶 → 抽屉 → 元件驱动器 → 细胞 → 合成器 → 高级贪心 → 贪心），
     * 唯一区别是每到访一个存储节点会尝试满足所有未完成请求，全部满足后立即结束走查。
     * <p>
     * 契约：消费各请求（{@link ItemRequest#receiveAmount(int)} 递减至 0）；返回列表与入参按位对齐，
     * 未取到任何物品的请求对应位为 null；相同身份的重复请求不去重、按序逐条尝试；
     * 访问历史命中/衰减与 transport-miss 簿记按请求各自对齐。
     * 与逐请求调用 getItemStack0 的行为差异：各请求在存储节点间的消耗顺序不同（各请求取到总量不变）、
     * recordFlow 记录顺序不同、access-limit 反馈整批至多一条。
     */
    public List<@Nullable ItemStack> getItemStacksBatch(Location accessor, List<ItemRequest> requests) {
        if (!allowAccessOutput(accessor)) {
            FeedbackSendable.sendFeedback0(accessor, FeedbackType.ROOT_LIMITING_ACCESS_OUTPUT);
            return List.of();
        }

        final List<@Nullable ItemStack> results = new ArrayList<>(requests.size());
        final List<BatchTake> pending = new ArrayList<>(requests.size());
        for (int i = 0; i < requests.size(); i++) {
            results.add(null);
            final ItemRequest request = requests.get(i);
            if (request.getAmount() <= 0) {
                FeedbackSendable.sendFeedback0(accessor, FeedbackType.ROOT_REQUEST_0);
                continue;
            }
            pending.add(new BatchTake(request, i));
        }
        if (pending.isEmpty()) return results;

        synchronized (RootWriteLock.get()) {
            bumpWriteEpoch();

            Map<Location, Integer> m = getPersistentAccessHistory(accessor);
            if (m != null) {
                for (Map.Entry<Location, Integer> entry : m.entrySet()) {
                    final Location historyLocation = entry.getKey();
                    final BarrelIdentity barrelIdentity = accessOutputAbleBarrel(historyLocation);
                    if (barrelIdentity != null) {
                        final ItemStack barrelItem = barrelIdentity.getItemStack();
                        if (barrelItem == null) {
                            for (BatchTake take : pending) {
                                take.historyMisses.add(historyLocation);
                            }
                            continue;
                        }
                        final boolean infinity = barrelIdentity instanceof InfinityBarrel;
                        for (Iterator<BatchTake> iterator = pending.iterator(); iterator.hasNext(); ) {
                            final BatchTake take = iterator.next();
                            final ItemRequest request = take.request;
                            if (!StackUtils.itemsMatch(request, barrelItem)) {
                                take.historyMisses.add(historyLocation);
                                continue;
                            }

                            // Netex - Cache start
                            minusCacheMiss(accessor, historyLocation);
                            take.historyFound = true;
                            // Netex - Cache end

                            final ItemStack fetched = barrelIdentity.requestItem(request);
                            if (fetched == null
                                || fetched.getType() == Material.AIR
                                || (infinity && fetched.getAmount() == 1)) {
                                continue;
                            }

                            // 复验实际取到的物品：匹配用扫描快照，requestItem 读活状态，
                            // 两者之间存储内容可能已被更换；只读型桶尚未扣量，跳过即无损。
                            if (!StackUtils.itemsMatch(request, fetched)) {
                                continue;
                            }

                            if (take.collected == null) {
                                take.collected = fetched.clone();
                                take.collected.setAmount(0);
                            }

                            final int preserveAmount = infinity ? fetched.getAmount() - 1 : fetched.getAmount();
                            int delta = request.getAmount();
                            if (delta <= preserveAmount) {
                                // Netex - Reduce start
                                uncontrolAccessOutput(accessor);
                                // Netex - Reduce end
                                take.collected.setAmount(take.collected.getAmount() + delta);
                                request.receiveAmount(delta);
                                fetched.setAmount(fetched.getAmount() - delta);
                                // Netex - Record start
                                tryRecord(accessor, request);
                                // Netex - Record end
                                results.set(take.index, take.collected);
                                iterator.remove();
                            } else {
                                take.collected.setAmount(take.collected.getAmount() + preserveAmount);
                                request.receiveAmount(preserveAmount);
                                fetched.setAmount(fetched.getAmount() - preserveAmount);
                            }
                        }
                    } else {
                        StorageUnitData data = accessOutputAbleCargoStorageUnitData(historyLocation);
                        if (data != null) {
                            for (Iterator<BatchTake> iterator = pending.iterator(); iterator.hasNext(); ) {
                                final BatchTake take = iterator.next();
                                final ItemStack takeStack = data.requestItem(accessor, take.request);
                                if (takeStack != null) {
                                    // Netex - Cache start
                                    minusCacheMiss(accessor, historyLocation);
                                    take.historyFound = true;
                                    // Netex - Cache end

                                    if (take.collected == null) {
                                        take.collected = takeStack.clone();
                                    } else {
                                        take.collected.setAmount(take.collected.getAmount() + takeStack.getAmount());
                                    }
                                    take.request.receiveAmount(takeStack.getAmount());

                                    if (take.request.getAmount() <= 0) {
                                        // Netex - Reduce start
                                        uncontrolAccessOutput(accessor);
                                        // Netex - Reduce end
                                        // Netex - Record start
                                        tryRecord(accessor, take.request);
                                        // Netex - Record end
                                        results.set(take.index, take.collected);
                                        iterator.remove();
                                    }
                                } else {
                                    // Netex - Cache start
                                    take.historyMisses.add(historyLocation);
                                    // Netex - Cache end
                                }
                            }
                        } else {
                            for (BatchTake take : pending) {
                                take.historyMisses.add(historyLocation);
                            }
                        }
                    }
                    if (pending.isEmpty()) {
                        break;
                    }
                }

                // Netex - Cache start
                for (BatchTake take : pending) {
                    if (!take.historyFound) {
                        for (Location miss : take.historyMisses) {
                            minusCacheMiss(accessor, miss);
                        }
                    }
                }
                // Netex - Cache end
            }
            if (pending.isEmpty()) {
                return results;
            }

            // Barrels first
            for (BarrelIdentity barrelIdentity : getOutputAbleBarrels()) {
                final ItemStack barrelItem = barrelIdentity.getItemStack();
                if (barrelItem == null) {
                    continue;
                }
                final boolean infinity = barrelIdentity instanceof InfinityBarrel;
                for (Iterator<BatchTake> iterator = pending.iterator(); iterator.hasNext(); ) {
                    final BatchTake take = iterator.next();
                    final ItemRequest request = take.request;
                    if (!StackUtils.itemsMatch(request, barrelItem)) {
                        continue;
                    }

                    // Netex - Cache start
                    addCountObservingAccessHistory(accessor, barrelIdentity.getLocation());
                    // Netex - Cache end

                    final ItemStack fetched = barrelIdentity.requestItem(request);
                    if (fetched == null || fetched.getType() == Material.AIR || (infinity && fetched.getAmount() == 1)) {
                        continue;
                    }

                    // 复验实际取到的物品：匹配用扫描快照，requestItem 读活状态，
                    // 两者之间存储内容可能已被更换；只读型桶尚未扣量，跳过即无损。
                    if (!StackUtils.itemsMatch(request, fetched)) {
                        continue;
                    }

                    if (take.collected == null) {
                        take.collected = fetched.clone();
                        take.collected.setAmount(0);
                    }

                    final int preserveAmount = infinity ? fetched.getAmount() - 1 : fetched.getAmount();
                    int delta = request.getAmount();
                    if (delta <= preserveAmount) {
                        // Netex - Reduce start
                        uncontrolAccessOutput(accessor);
                        // Netex - Reduce end
                        take.collected.setAmount(take.collected.getAmount() + delta);
                        request.receiveAmount(delta);
                        fetched.setAmount(fetched.getAmount() - delta);
                        // Netex - Record start
                        tryRecord(accessor, request);
                        // Netex - Record end
                        results.set(take.index, take.collected);
                        iterator.remove();
                    } else {
                        take.collected.setAmount(take.collected.getAmount() + preserveAmount);
                        request.receiveAmount(preserveAmount);
                        fetched.setAmount(fetched.getAmount() - preserveAmount);
                    }
                }
                if (pending.isEmpty()) {
                    return results;
                }
            }

            // Units
            for (StorageUnitData cache : getOutputAbleDrawerData().values()) {
                for (Iterator<BatchTake> iterator = pending.iterator(); iterator.hasNext(); ) {
                    final BatchTake take = iterator.next();
                    final ItemStack takeStack = cache.requestItem(accessor, take.request);
                    if (takeStack != null) {
                        // Netex - Cache start
                        addCountObservingAccessHistory(accessor, cache.getLastLocation());
                        // Netex - Cache end
                        if (take.collected == null) {
                            take.collected = takeStack.clone();
                        } else {
                            take.collected.setAmount(take.collected.getAmount() + takeStack.getAmount());
                        }
                        take.request.receiveAmount(takeStack.getAmount());

                        if (take.request.getAmount() <= 0) {
                            // Netex - Reduce start
                            uncontrolAccessOutput(accessor);
                            // Netex - Reduce end
                            // Netex - Record start
                            tryRecord(accessor, take.request);
                            // Netex - Record end
                            results.set(take.index, take.collected);
                            iterator.remove();
                        }
                    }
                }
                if (pending.isEmpty()) {
                    return results;
                }
            }

            // SimpleStorage chests
            for (SimpleStorageChest chest : getOutputAbleSimpleStorageChests()) {
                for (Iterator<BatchTake> iterator = pending.iterator(); iterator.hasNext(); ) {
                    final BatchTake take = iterator.next();
                    final ItemStack takeStack = chest.requestItem(take.request);
                    if (takeStack != null) {
                        if (take.collected == null) {
                            take.collected = takeStack.clone();
                        } else {
                            take.collected.setAmount(take.collected.getAmount() + takeStack.getAmount());
                        }
                        take.request.receiveAmount(takeStack.getAmount());

                        if (take.request.getAmount() <= 0) {
                            // Netex - Reduce start
                            uncontrolAccessOutput(accessor);
                            // Netex - Reduce end
                            // Netex - Record start
                            tryRecord(accessor, take.request);
                            // Netex - Record end
                            results.set(take.index, take.collected);
                            iterator.remove();
                        }
                    }
                }
                if (pending.isEmpty()) {
                    return results;
                }
            }

            // Cell Drives
            for (Iterator<BatchTake> iterator = pending.iterator(); iterator.hasNext(); ) {
                final BatchTake take = iterator.next();
                final ItemStack takeStack =
                    CellDrive.getStorage().takeItem(driveCache, getOutputAbleCellDriveMenus(), take.request);
                if (takeStack != null) {
                    if (take.collected == null) {
                        take.collected = takeStack.clone();
                    } else {
                        take.collected.setAmount(take.collected.getAmount() + takeStack.getAmount());
                    }
                    take.request.receiveAmount(takeStack.getAmount());

                    if (take.request.getAmount() <= 0) {
                        tryRecord(accessor, take.request);
                        results.set(take.index, take.collected);
                        iterator.remove();
                    }
                }
            }
            if (pending.isEmpty()) {
                return results;
            }

            // Cells
            for (BlockMenu blockMenu : getCellMenus()) {
                if (isFakeCell(blockMenu)) continue;
                for (int slot : CELL_AVAILABLE_SLOTS) {
                    final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }
                    for (Iterator<BatchTake> iterator = pending.iterator(); iterator.hasNext(); ) {
                        final BatchTake take = iterator.next();
                        if (!StackUtils.itemsMatch(take.request, itemStack)) {
                            continue;
                        }

                        // Mark the Cell as dirty otherwise the changes will not save on shutdown
                        blockMenu.markDirty();

                        // If the return stack is null, we need to set it up
                        if (take.collected == null) {
                            take.collected = itemStack.clone();
                            take.collected.setAmount(0);
                        }

                        int delta = take.request.getAmount();
                        if (delta <= itemStack.getAmount()) {
                            // Netex - Reduce start
                            uncontrolAccessOutput(accessor);
                            // Netex - Reduce end
                            // We can't take more than this stack. Level to request amount, remove items and then return
                            take.collected.setAmount(take.collected.getAmount() + delta);
                            take.request.receiveAmount(delta);
                            itemStack.setAmount(itemStack.getAmount() - delta);
                            // Netex - Record start
                            tryRecord(accessor, take.request);
                            // Netex - Record end
                            results.set(take.index, take.collected);
                            iterator.remove();
                        } else {
                            // We can take more than what is here, consume before trying to take more
                            take.collected.setAmount(take.collected.getAmount() + itemStack.getAmount());
                            take.request.receiveAmount(itemStack.getAmount());
                            itemStack.setAmount(0);
                        }
                    }
                }
                if (pending.isEmpty()) {
                    return results;
                }
            }

            // Crafters
            for (BlockMenu blockMenu : getCrafterOutputs()) {
                int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
                for (int slot : slots) {
                    final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }
                    for (Iterator<BatchTake> iterator = pending.iterator(); iterator.hasNext(); ) {
                        final BatchTake take = iterator.next();
                        if (!StackUtils.itemsMatch(take.request, itemStack)) {
                            continue;
                        }

                        if (take.collected == null) {
                            take.collected = itemStack.clone();
                            take.collected.setAmount(0);
                        }

                        int delta = take.request.getAmount();
                        if (delta <= itemStack.getAmount()) {
                            // Netex - Reduce start
                            uncontrolAccessOutput(accessor);
                            // Netex - Reduce end
                            take.collected.setAmount(take.collected.getAmount() + delta);
                            take.request.receiveAmount(delta);
                            itemStack.setAmount(itemStack.getAmount() - delta);
                            // Netex - Record start
                            tryRecord(accessor, take.request);
                            // Netex - Record end
                            results.set(take.index, take.collected);
                            iterator.remove();
                        } else {
                            take.collected.setAmount(take.collected.getAmount() + itemStack.getAmount());
                            take.request.receiveAmount(itemStack.getAmount());
                            itemStack.setAmount(0);
                        }
                    }
                }
                if (pending.isEmpty()) {
                    return results;
                }
            }

            for (BlockMenu blockMenu : getAdvancedGreedyBlockMenus()) {
                int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
                for (int slot : slots) {
                    final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }
                    for (Iterator<BatchTake> iterator = pending.iterator(); iterator.hasNext(); ) {
                        final BatchTake take = iterator.next();
                        if (!StackUtils.itemsMatch(take.request, itemStack)) {
                            continue;
                        }

                        if (take.collected == null) {
                            take.collected = itemStack.clone();
                            take.collected.setAmount(0);
                        }

                        int delta = take.request.getAmount();
                        if (delta <= itemStack.getAmount()) {
                            // Netex - Reduce start
                            uncontrolAccessOutput(accessor);
                            // Netex - Reduce end
                            take.collected.setAmount(take.collected.getAmount() + delta);
                            take.request.receiveAmount(delta);
                            itemStack.setAmount(itemStack.getAmount() - delta);
                            // Netex - Record start
                            tryRecord(accessor, take.request);
                            // Netex - Record end
                            results.set(take.index, take.collected);
                            iterator.remove();
                        } else {
                            take.collected.setAmount(take.collected.getAmount() + itemStack.getAmount());
                            take.request.receiveAmount(itemStack.getAmount());
                            itemStack.setAmount(0);
                        }
                    }
                }
                if (pending.isEmpty()) {
                    return results;
                }
            }

            // Greedy Blocks
            for (BlockMenu blockMenu : getGreedyBlockMenus()) {
                int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
                final ItemStack itemStack = blockMenu.getItemInSlot(slots[0]);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }
                for (Iterator<BatchTake> iterator = pending.iterator(); iterator.hasNext(); ) {
                    final BatchTake take = iterator.next();
                    if (!StackUtils.itemsMatch(take.request, itemStack)) {
                        continue;
                    }

                    // Mark the Cell as dirty otherwise the changes will not save on shutdown
                    blockMenu.markDirty();

                    // If the return stack is null, we need to set it up
                    if (take.collected == null) {
                        take.collected = itemStack.clone();
                        take.collected.setAmount(0);
                    }

                    int delta = take.request.getAmount();
                    if (delta <= itemStack.getAmount()) {
                        // Netex - Reduce start
                        uncontrolAccessOutput(accessor);
                        // Netex - Reduce end
                        // We can't take more than this stack. Level to request amount, remove items and then return
                        take.collected.setAmount(take.collected.getAmount() + delta);
                        take.request.receiveAmount(delta);
                        itemStack.setAmount(itemStack.getAmount() - delta);
                        // Netex - Record start
                        tryRecord(accessor, take.request);
                        // Netex - Record end
                        results.set(take.index, take.collected);
                        iterator.remove();
                    } else {
                        // We can take more than what is here, consume before trying to take more
                        take.collected.setAmount(take.collected.getAmount() + itemStack.getAmount());
                        take.request.receiveAmount(itemStack.getAmount());
                        itemStack.setAmount(0);
                    }
                }
            }

            for (BatchTake take : pending) {
                if (take.collected != null && take.collected.getAmount() != 0) {
                    // Netex - Reduce start
                    uncontrolAccessOutput(accessor);
                    // Netex - Reduce end
                    // Netex - Record start
                    tryRecord(accessor, take.request);
                    // Netex - Record end
                    results.set(take.index, take.collected);
                }
            }

            return results;
        }
    }

    /** getItemStacksBatch 的单请求运行态：入参索引对齐、聚合结果与访问历史段簿记。 */
    private static final class BatchTake {
        private final ItemRequest request;
        private final int index;
        @Nullable
        private ItemStack collected;
        private boolean historyFound;
        private final List<Location> historyMisses = new ArrayList<>();

        private BatchTake(ItemRequest request, int index) {
            this.request = request;
            this.index = index;
        }
    }

    public void addItem(@Nullable Location accessor, ItemStack incoming) {
        addItemStack(accessor, incoming);
    }

    public void tryRecord(@Nullable Location accessor, @Nullable ItemStack before, int after) {
        if (accessor == null) return;
        if (recordFlow && itemFlowRecord != null && before != null) {
            itemFlowRecord.addAction(accessor, before, after);
        }
    }

    public void addItemStack(@Nullable Location accessor, ItemStack incoming) {
        if (!allowAccessInput(accessor)) {
            FeedbackSendable.sendFeedback0(accessor, FeedbackType.ROOT_LIMITING_ACCESS_INPUT);
            return;
        }

        if (StackUtils.isBlacklisted(incoming)) return;

        bumpWriteEpoch();

        ItemStack beforeItemStack = null;
        if (recordFlow && itemFlowRecord != null) {
            beforeItemStack = incoming.clone();
        }

        int before = incoming.getAmount();

        Map<Location, Integer> m = getPersistentAccessHistory(accessor);
        if (m != null) {
            // Netex - Cache start
            boolean found = false;
            List<Location> misses = new ArrayList<>();
            // Netex - Cache end
            for (Map.Entry<Location, Integer> entry : m.entrySet()) {
                BarrelIdentity barrelIdentity = accessInputAbleBarrel(entry.getKey());
                if (barrelIdentity != null) {
                    // <editor-fold desc="do barrel">
                    if (StackUtils.itemsMatch(barrelIdentity, incoming)) {
                        // Netex - Cache start
                        minusCacheMiss(accessor, entry.getKey());
                        found = true;
                        // Netex - Cache end

                        barrelIdentity.depositItemStack(incoming);

                        // All distributed, can escape
                        if (incoming.getAmount() == 0) {
                            // Netex - Reduce start
                            uncontrolAccessInput(accessor);
                            // Netex - Reduce end
                            // Netex - Record start
                            tryRecord(accessor, beforeItemStack, 0);
                            // Netex - Record end
                            return;
                        }
                    } else {
                        // Netex - Cache start
                        misses.add(entry.getKey());
                        // Netex - Cache end
                    }
                    // </editor-fold>
                } else {
                    StorageUnitData data = accessInputAbleCargoStorageUnitData(entry.getKey());
                    if (data != null) {
                        // Netex - Cache start
                        int before2 = incoming.getAmount();
                        // Netex - Cache end
                        data.depositItemStack(accessor, incoming, true);

                        // Netex - Cache start
                        if (incoming.getAmount() != before2) {
                            minusCacheMiss(accessor, entry.getKey());
                            found = true;
                        } else {
                            misses.add(entry.getKey());
                        }
                        // Netex - Cache end

                        if (incoming.getAmount() == 0) {
                            // Netex - Reduce start
                            uncontrolAccessInput(accessor);
                            // Netex - Reduce end
                            // Netex - Record start
                            tryRecord(accessor, beforeItemStack, 0);
                            // Netex - Record end
                            return;
                        }
                    }
                }
            }

            // Netex - Cache start
            if (!found) {
                for (Location miss : misses) {
                    addCacheMiss(accessor, miss);
                }
            }
            // Netex - Cache end
        }

        for (BlockMenu blockMenu : getAdvancedGreedyBlockMenus()) {
            final ItemStack template = blockMenu.getItemInSlot(AdvancedGreedyBlock.TEMPLATE_SLOT);

            if (!StackUtils.itemsMatch(incoming, template)) continue;

            blockMenu.markDirty();
            BlockMenuUtil.pushItem(blockMenu, incoming, ADVANCED_GREEDY_BLOCK_AVAILABLE_SLOTS);
            // Netex - Reduce start
            uncontrolAccessInput(accessor);
            // Netex - Reduce end
            // Netex - Record start
            tryRecord(accessor, beforeItemStack, incoming.getAmount());
            // Netex - Record end
            // Given we have found a match, it doesn't matter if the item moved or not, we will not bring it in
            return;
        }

        // Run for matching greedy blocks
        for (BlockMenu blockMenu : getGreedyBlockMenus()) {
            final ItemStack template = blockMenu.getItemInSlot(NetworkGreedyBlock.TEMPLATE_SLOT);

            if (!StackUtils.itemsMatch(incoming, template)) continue;

            blockMenu.markDirty();
            BlockMenuUtil.pushItem(blockMenu, incoming, GREEDY_BLOCK_AVAILABLE_SLOT);
            // Netex - Reduce start
            uncontrolAccessInput(accessor);
            // Netex - Reduce end
            // Netex - Record start
            tryRecord(accessor, beforeItemStack, incoming.getAmount());
            // Netex - Record end
            // Given we have found a match, it doesn't matter if the item moved or not, we will not bring it in
            return;
        }

        // Run for matching barrels
        for (BarrelIdentity barrelIdentity : getInputAbleBarrels()) {
            // <editor-fold desc="do barrel">
            if (StackUtils.itemsMatch(barrelIdentity, incoming)) {
                // Netex - Cache start
                addCountObservingAccessHistory(accessor, barrelIdentity.getLocation());
                // Netex - Cache end

                barrelIdentity.depositItemStack(incoming);

                // All distributed, can escape
                if (incoming.getAmount() == 0) {
                    // Netex - Reduce start
                    uncontrolAccessInput(accessor);
                    // Netex - Reduce end
                    // Netex - Record start
                    tryRecord(accessor, beforeItemStack, 0);
                    // Netex - Record end
                    return;
                }
            }
            // </editor-fold>
        }

        for (StorageUnitData cache : getMapInputAbleCargoStorageUnits().values()) {
            // Netex - Cache start
            int before2 = incoming.getAmount();
            // Netex - Cache end

            cache.depositItemStack(accessor, incoming, true);

            // Netex - Cache start
            if (incoming.getAmount() != before2) {
                // Netex - Reduce start
                uncontrolAccessInput(accessor);
                // Netex - Reduce end
                addCountObservingAccessHistory(accessor, cache.getLastLocation());
            }
            // Netex - Cache end

            if (incoming.getAmount() == 0) {
                // Netex - Reduce start
                uncontrolAccessInput(accessor);
                // Netex - Reduce end
                // Netex - Record start
                tryRecord(accessor, beforeItemStack, 0);
                // Netex - Record end
                return;
            }
        }

        // SimpleStorage chests
        for (SimpleStorageChest chest : getInputAbleSimpleStorageChests()) {
            chest.depositItemStack(incoming);

            if (incoming.getAmount() == 0) {
                // Netex - Reduce start
                uncontrolAccessInput(accessor);
                // Netex - Reduce end
                // Netex - Record start
                tryRecord(accessor, beforeItemStack, 0);
                // Netex - Record end
                return;
            }
        }

        // Cell Drives
        long cellRemaining = CellDrive.getStorage().pushSingle(
            driveCache, getInputAbleCellDriveMenus(), incoming, incoming.getAmount());
        incoming.setAmount((int) Math.min(cellRemaining, Integer.MAX_VALUE));
        if (incoming.getAmount() == 0) {
            tryRecord(accessor, beforeItemStack, 0);
            return;
        }

        for (BlockMenu blockMenu : getCellMenus()) {
            if (isFakeCell(blockMenu)) continue;
            blockMenu.markDirty();
            BlockMenuUtil.pushItem(blockMenu, incoming, CELL_AVAILABLE_SLOTS);
            if (incoming.getAmount() == 0) {
                // Netex - Reduce start
                uncontrolAccessInput(accessor);
                // Netex - Reduce end
                // Netex - Record start
                tryRecord(accessor, beforeItemStack, 0);
                // Netex - Record end
                return;
            }
        }

        // Netex - Reduce start
        if (before == incoming.getAmount()) {
            // No item moved, limit the accessor
            addTransportInputMiss(accessor);
        } else {
            uncontrolAccessInput(accessor);
        }
        // Netex - Reduce end
        // Netex - Record start
        tryRecord(accessor, beforeItemStack, incoming.getAmount());
        // Netex - Record end
    }

    /**
     * 批量入库：一批物品按原有优先级走一遍存储，每种仓库只访问一次。
     * 未收完的物品保留剩余数量，由调用方回扣来源容器。
     */
    public void addItemStacks(@Nullable Location accessor, List<ItemStack> incomings) {
        if (incomings.isEmpty()) return;
        for (var item : incomings) {
            addItemStack(accessor, item);
        }
    }

    public Map<Location, BarrelIdentity> getMapInputAbleBarrels() {
        if (mapInputAbleBarrels != null) return mapInputAbleBarrels;

        final Map<Location, BarrelIdentity> map = new ConcurrentHashMap<>();
        for (BarrelIdentity barrel : getInputAbleBarrels()) {
            map.put(barrel.getLocation(), barrel);
        }
        mapInputAbleBarrels = map;
        return map;
    }

    public Map<Location, BarrelIdentity> getMapOutputAbleBarrels() {
        if (mapOutputAbleBarrels != null) return mapOutputAbleBarrels;

        final Map<Location, BarrelIdentity> map = new ConcurrentHashMap<>();
        for (BarrelIdentity barrel : getOutputAbleBarrels()) {
            map.put(barrel.getLocation(), barrel);
        }
        this.mapOutputAbleBarrels = map;
        return map;
    }

    public Map<Location, StorageUnitData> getMapInputAbleCargoStorageUnits() {
        return getInputAbleDrawerData();
    }

    public Map<Location, StorageUnitData> getMapOutputAbleCargoStorageUnits() {
        return getOutputAbleDrawerData();
    }

    public boolean allowAccessInput(@Nullable Location accessor) {
        if (accessor == null) return true; // fallback
        Long lastTime = controlledAccessInputHistory.get(accessor);
        if (lastTime == null) return true;

        return System.currentTimeMillis() - lastTime > reduceMs;
    }

    public boolean allowAccessOutput(@Nullable Location accessor) {
        if (accessor == null) return true; // fallback
        Long lastTime = controlledAccessOutputHistory.get(accessor);
        if (lastTime == null) return true;
        return System.currentTimeMillis() - lastTime > reduceMs;
    }

    public void addTransportInputMiss(@Nullable Location accessor) {
        if (accessor == null) return;
        transportMissInputHistory.merge(accessor, 1, (a, b) -> {
            if (a + b > transportMissThreshold) {
                controlAccessInput(accessor);
                return transportMissThreshold;
            } else {
                return a + b;
            }
        });
    }

    public void addTransportOutputMiss(@Nullable Location accessor) {
        if (accessor == null) return;
        transportMissOutputHistory.merge(accessor, 1, (a, b) -> {
            if (a + b > transportMissThreshold) {
                controlAccessOutput(accessor);
                return transportMissThreshold;
            } else {
                return a + b;
            }
        });
    }

    public void reduceTransportInputMiss(@Nullable Location accessor) {
        if (accessor == null) return;
        transportMissInputHistory.merge(accessor, -1, (a, b) -> Math.max(a + b, 0));
    }

    public void reduceTransportOutputMiss(@Nullable Location accessor) {
        if (accessor == null) return;
        transportMissOutputHistory.merge(accessor, -1, (a, b) -> Math.max(a + b, 0));
    }

    public void controlAccessInput(@Nullable Location accessor) {
        if (accessor == null) return;
        controlledAccessInputHistory.put(accessor, System.currentTimeMillis());
    }

    public void controlAccessOutput(@Nullable Location accessor) {
        if (accessor == null) return;
        controlledAccessOutputHistory.put(accessor, System.currentTimeMillis());
    }

    public void uncontrolAccessInput(@Nullable Location accessor) {
        if (accessor == null) return;
        controlledAccessInputHistory.remove(accessor);
        reduceTransportInputMiss(accessor);
    }

    public void uncontrolAccessOutput(@Nullable Location accessor) {
        if (accessor == null) return;
        controlledAccessOutputHistory.remove(accessor);
        reduceTransportOutputMiss(accessor);
    }

    public int getCellsSize() {
        return getNodes(NodeType.CELL).size();
    }

    /**
     * 说实话这个治标不治本
     */
    public static boolean isFakeCell(BlockMenu menu) {
        return !isRealCell(menu.getLocation());
    }

    public static boolean isRealCell(BlockMenu menu) {
        return isRealCell(menu.getLocation());
    }

    public static boolean isFakeCell(Location location) {
        return !isRealCell(location);
    }

    public static boolean isRealCell(Location location) {
        return StorageCacheUtils.getSfItem(location) instanceof NetworkCell;
    }

    private <K, V> HashMap<V, K> reverseMap(Map<K, V> map) {
        HashMap<V, K> newMap = new HashMap<>();
        for (var e : map.entrySet()) newMap.put(e.getValue(), e.getKey());
        return newMap;
    }

    // ============= Fallback methods =============

    /**
     * deprecated, use {@link #getItemStack(Location, ItemRequest)} instead.
     */
    @Deprecated
    @Nullable
    public ItemStack getItemStack(ItemRequest request) {
        return getItemStack(null, request);
    }

    /**
     * deprecated, use {@link #addItemStack(Location, ItemStack)} instead.
     */
    @Deprecated
    public void addItemStack(ItemStack incoming) {
        addItemStack(null, incoming);
    }

    @Deprecated
    public Map<StorageUnitData, Location> getCargoStorageUnitDatas() {
        return reverseMap(getDrawerData());
    }

    @Deprecated
    public Map<StorageUnitData, Location> getInputAbleCargoStorageUnitDatas() {
        return reverseMap(getInputAbleDrawerData());
    }

    @Deprecated
    public Map<StorageUnitData, Location> getOutputAbleCargoStorageUnitDatas() {
        return reverseMap(getOutputAbleDrawerData());
    }

    @Deprecated
    public List<ItemStack> getItemStacks(List<ItemRequest> itemRequests) {
        return getItemStacks(null, itemRequests);
    }

    @Deprecated
    public List<ItemStack> getItemStacks0(@Nullable Location accesor, List<ItemRequest> itemRequests) {
        return getItemStacks(accesor, itemRequests);
    }

    @Deprecated
    public @Nullable ItemStack getItemStack0(@Nullable Location accessor, ItemRequest request) {
        return getItemStack(accessor, request, null);
    }

    @Deprecated
    public @Nullable ItemStack getItemStack0(
        @Nullable Location accessor, ItemRequest request, @Nullable MatchOption option) {
        return getItemStack(accessor, request, option);
    }

    @Deprecated
    @Nullable
    public ItemStack requestItem(ItemRequest request) {
        return getItemStack(null, request);
    }

    @Deprecated
    @Nullable
    public ItemStack requestItem(ItemStack itemStack) {
        return requestItem(null, new ItemRequest(itemStack, itemStack.getAmount()));
    }

    @Deprecated
    public void addItemStack0(@Nullable Location accessor, ItemStack incoming) {
        addItemStack(accessor, incoming);
    }

    @Deprecated
    public void addItemStacks0(@Nullable Location accessor, List<ItemStack> incomings) {
        addItemStacks(accessor, incomings);
    }

    @Deprecated
    public List<@Nullable ItemStack> getItemStacksBatch0(Location accessor, List<ItemRequest> requests) {
        return getItemStacksBatch(accessor, requests);
    }
}
