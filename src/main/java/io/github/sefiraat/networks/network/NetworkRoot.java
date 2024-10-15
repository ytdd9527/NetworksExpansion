package io.github.sefiraat.networks.network;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.balugaq.netex.api.data.ItemContainer;
import com.balugaq.netex.api.data.StorageUnitData;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedGreedyBlock;
import com.ytdd9527.networksexpansion.implementation.machines.unit.CargoStorageUnit;
import com.balugaq.netex.utils.NetworksVersionedParticle;
import io.github.mooy1.infinityexpansion.items.storage.StorageCache;
import io.github.mooy1.infinityexpansion.items.storage.StorageUnit;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.barrel.FluffyBarrel;
import io.github.sefiraat.networks.network.barrel.InfinityBarrel;
import io.github.sefiraat.networks.network.barrel.NetworkStorage;
import io.github.sefiraat.networks.network.stackcaches.BarrelIdentity;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.network.stackcaches.QuantumCache;
import io.github.sefiraat.networks.slimefun.network.NetworkCell;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.sefiraat.networks.slimefun.network.NetworkGreedyBlock;
import io.github.sefiraat.networks.slimefun.network.NetworkPowerNode;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class NetworkRoot extends NetworkNode {
    @Getter
    private final Set<Location> nodeLocations = new HashSet<>();
    private final int[] CELL_AVAILABLE_SLOTS = NetworkCell.SLOTS.stream().mapToInt(i -> i).toArray();
    private final int[] GREEDY_BLOCK_AVAILABLE_SLOTS = new int[]{NetworkGreedyBlock.INPUT_SLOT};
    private final int[] ADVANCED_GREEDY_BLOCK_AVAILABLE_SLOTS = AdvancedGreedyBlock.INPUT_SLOTS;
    @Getter
    private final Set<Location> bridges = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> monitors = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> importers = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> exporters = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> grids = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> cells = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> grabbers = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> pushers = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> purgers = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> crafters = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> powerNodes = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> powerDisplays = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> encoders = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> greedyBlocks = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> cutters = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> pasters = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> vacuums = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> wirelessTransmitters = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> wirelessReceivers = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> powerOutlets = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> transferPushers = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> transferGrabbers = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> transfers = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> advancedImporters = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> advancedExporters = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> advancedGreedyBlocks = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> advancedPurgers = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> lineTransferVanillaPushers = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> lineTransferVanillaGrabbers = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> inputOnlyMonitors = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<Location> outputOnlyMonitors = ConcurrentHashMap.newKeySet();
    @Getter
    private Location controller = null;
    private boolean progressing = false;
    @Getter
    private int maxNodes;
    private boolean isOverburdened = false;
    private Set<BarrelIdentity> barrels = null;
    private Set<BarrelIdentity> inputAbleBarrels = null;
    private Set<BarrelIdentity> outputAbleBarrels = null;

    private Map<StorageUnitData, Location> cargoStorageUnitDatas = null;
    private Map<StorageUnitData, Location> inputAbleCargoStorageUnitDatas = null;
    private Map<StorageUnitData, Location> outputAbleCargoStorageUnitDatas = null;

    @Getter
    private long rootPower = 0;

    @Getter
    private boolean displayParticles = false;

    public NetworkRoot(@Nonnull Location location, @Nonnull NodeType type, int maxNodes) {
        super(location, type);
        this.maxNodes = maxNodes;
        this.root = this;

        registerNode(location, type);
    }

    public void registerNode(@Nonnull Location location, @Nonnull NodeType type) {
        nodeLocations.add(location);
        switch (type) {
            case CONTROLLER -> this.controller = location;
            case BRIDGE -> bridges.add(location);
            case STORAGE_MONITOR -> monitors.add(location);
            case IMPORT -> importers.add(location);
            case EXPORT -> exporters.add(location);
            case GRID -> grids.add(location);
            case CELL -> {
                /*
                 * Fix https://github.com/Sefiraat/Networks/issues/211
                 */
                BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
                if (blockMenu == null) {
                    return;
                }
                if (Arrays.equals(blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW), CELL_AVAILABLE_SLOTS)) {
                    cells.add(location);
                }
            }
            case GRABBER -> grabbers.add(location);
            case PUSHER -> pushers.add(location);
            case PURGER -> purgers.add(location);
            case CRAFTER -> crafters.add(location);
            case POWER_NODE -> powerNodes.add(location);
            case POWER_DISPLAY -> powerDisplays.add(location);
            case ENCODER -> encoders.add(location);
            case GREEDY_BLOCK -> {
                /*
                 * Fix https://github.com/Sefiraat/Networks/issues/211
                 */
                BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
                if (blockMenu == null) {
                    return;
                }
                if (Arrays.equals(blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW), GREEDY_BLOCK_AVAILABLE_SLOTS)) {
                    greedyBlocks.add(location);
                }
            }
            case CUTTER -> cutters.add(location);
            case PASTER -> pasters.add(location);
            case VACUUM -> vacuums.add(location);
            case WIRELESS_TRANSMITTER -> wirelessTransmitters.add(location);
            case WIRELESS_RECEIVER -> wirelessReceivers.add(location);
            case POWER_OUTLET -> powerOutlets.add(location);
            // from networks expansion
            case ADVANCED_IMPORT -> advancedImporters.add(location);
            case ADVANCED_EXPORT -> advancedExporters.add(location);
            case ADVANCED_GREEDY_BLOCK -> {
                /*
                 * Fix https://github.com/Sefiraat/Networks/issues/211
                 */
                BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
                if (blockMenu == null) {
                    return;
                }
                if (Arrays.equals(blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW), ADVANCED_GREEDY_BLOCK_AVAILABLE_SLOTS)) {
                    advancedGreedyBlocks.add(location);
                }
            }
            case ADVANCED_PURGER -> advancedPurgers.add(location);
            case TRANSFER -> transfers.add(location);
            case TRANSFER_PUSHER -> transferPushers.add(location);
            case TRANSFER_GRABBER -> transferGrabbers.add(location);
            case LINE_TRANSFER_VANILLA_GRABBER -> lineTransferVanillaGrabbers.add(location);
            case LINE_TRANSFER_VANILLA_PUSHER -> lineTransferVanillaPushers.add(location);
            case INPUT_ONLY_MONITOR -> inputOnlyMonitors.add(location);
            case OUTPUT_ONLY_MONITOR -> outputOnlyMonitors.add(location);
        }
    }

    public int getNodeCount() {
        return this.nodeLocations.size();
    }

    public boolean isOverburdened() {
        return isOverburdened;
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

    @Nonnull
    public Map<ItemStack, Long> getAllNetworkItemsLongType() {
        final Map<ItemStack, Long> itemStacks = new HashMap<>();

        // Barrels
        for (BarrelIdentity barrelIdentity : getOutputAbleBarrels()) {
            final Long currentAmount = itemStacks.get(barrelIdentity.getItemStack());
            final long newAmount;
            if (currentAmount == null) {
                newAmount = barrelIdentity.getAmount();
            } else {
                long newLong = currentAmount + barrelIdentity.getAmount();
                if (newLong < 0) {
                    newAmount = 0;
                } else {
                    newAmount = currentAmount + barrelIdentity.getAmount();
                }
            }
            itemStacks.put(barrelIdentity.getItemStack(), newAmount);
        }

        // Cargo storage units
        Map<StorageUnitData, Location> cacheMap = getOutputAbleCargoStorageUnitDatas();
        for (StorageUnitData cache : cacheMap.keySet()) {
            for (ItemContainer itemContainer : cache.getStoredItems()) {
                final Long currentAmount = itemStacks.get(itemContainer.getSample());
                long newAmount;
                if (currentAmount == null) {
                    newAmount = itemContainer.getAmount();
                } else {
                    long newLong = currentAmount + (long) itemContainer.getAmount();
                    if (newLong < 0) {
                        newAmount = 0;
                    } else {
                        newAmount = currentAmount + itemContainer.getAmount();
                    }
                }
                itemStacks.put(itemContainer.getSample(), newAmount);
            }
        }

        for (BlockMenu blockMenu : getAdvancedGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }
                final ItemStack clone = StackUtils.getAsQuantity(itemStack, 1);
                final Long currentAmount = itemStacks.get(clone);
                final long newAmount;
                if (currentAmount == null) {
                    newAmount = itemStack.getAmount();
                } else {
                    long newLong = currentAmount + (long) itemStack.getAmount();
                    if (newLong < 0) {
                        newAmount = 0;
                    } else {
                        newAmount = currentAmount + itemStack.getAmount();
                    }
                }
                itemStacks.put(clone, newAmount);
            }
        }

        for (BlockMenu blockMenu : getGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            final ItemStack itemStack = blockMenu.getItemInSlot(slots[0]);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            final ItemStack clone = StackUtils.getAsQuantity(itemStack, 1);
            final Long currentAmount = itemStacks.get(clone);
            final long newAmount;
            if (currentAmount == null) {
                newAmount = itemStack.getAmount();
            } else {
                long newLong = currentAmount + (long) itemStack.getAmount();
                if (newLong < 0) {
                    newAmount = 0;
                } else {
                    newAmount = currentAmount + itemStack.getAmount();
                }
            }
            itemStacks.put(clone, newAmount);
        }

        for (BlockMenu blockMenu : getCrafterOutputs()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }
                final ItemStack clone = StackUtils.getAsQuantity(itemStack, 1);
                final Long currentAmount = itemStacks.get(clone);
                final long newAmount;
                if (currentAmount == null) {
                    newAmount = itemStack.getAmount();
                } else {
                    long newLong = currentAmount + (long) itemStack.getAmount();
                    if (newLong < 0) {
                        newAmount = 0;
                    } else {
                        newAmount = currentAmount + itemStack.getAmount();
                    }
                }
                itemStacks.put(clone, newAmount);
            }
        }

        for (BlockMenu blockMenu : getCellMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    final ItemStack clone = itemStack.clone();

                    clone.setAmount(1);

                    final Long currentAmount = itemStacks.get(clone);
                    long newAmount;

                    if (currentAmount == null) {
                        newAmount = itemStack.getAmount();
                    } else {
                        long newLong = currentAmount + (long) itemStack.getAmount();
                        if (newLong < 0) {
                            newAmount = 0;
                        } else {
                            newAmount = currentAmount + itemStack.getAmount();
                        }
                    }

                    itemStacks.put(clone, newAmount);
                }
            }
        }
        return itemStacks;
    }

    public Map<ItemStack, Integer> getAllNetworkItems() {
        final Map<ItemStack, Integer> itemStacks = new HashMap<>();

        // Barrels
        for (BarrelIdentity barrelIdentity : getOutputAbleBarrels()) {
            final Integer currentAmount = itemStacks.get(barrelIdentity.getItemStack());
            final long newAmount;
            if (currentAmount == null) {
                newAmount = barrelIdentity.getAmount();
            } else {
                long newLong = (long) currentAmount + barrelIdentity.getAmount();
                if (newLong > Integer.MAX_VALUE) {
                    newAmount = Integer.MAX_VALUE;
                } else {
                    newAmount = currentAmount + barrelIdentity.getAmount();
                }
            }
            itemStacks.put(barrelIdentity.getItemStack(), (int) newAmount);
        }

        for (BlockMenu blockMenu : getGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            final ItemStack itemStack = blockMenu.getItemInSlot(slots[0]);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            final ItemStack clone = StackUtils.getAsQuantity(itemStack, 1);
            final Integer currentAmount = itemStacks.get(clone);
            final int newAmount;
            if (currentAmount == null) {
                newAmount = itemStack.getAmount();
            } else {
                long newLong = (long) currentAmount + (long) itemStack.getAmount();
                if (newLong > Integer.MAX_VALUE) {
                    newAmount = Integer.MAX_VALUE;
                } else {
                    newAmount = currentAmount + itemStack.getAmount();
                }
            }
            itemStacks.put(clone, newAmount);
        }

        for (BlockMenu blockMenu : getCrafterOutputs()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }
                final ItemStack clone = StackUtils.getAsQuantity(itemStack, 1);
                final Integer currentAmount = itemStacks.get(clone);
                final int newAmount;
                if (currentAmount == null) {
                    newAmount = itemStack.getAmount();
                } else {
                    long newLong = (long) currentAmount + (long) itemStack.getAmount();
                    if (newLong > Integer.MAX_VALUE) {
                        newAmount = Integer.MAX_VALUE;
                    } else {
                        newAmount = currentAmount + itemStack.getAmount();
                    }
                }
                itemStacks.put(clone, newAmount);
            }
        }

        for (BlockMenu blockMenu : getCellMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    final ItemStack clone = itemStack.clone();

                    clone.setAmount(1);

                    final Integer currentAmount = itemStacks.get(clone);
                    int newAmount;

                    if (currentAmount == null) {
                        newAmount = itemStack.getAmount();
                    } else {
                        long newLong = (long) currentAmount + (long) itemStack.getAmount();
                        if (newLong > Integer.MAX_VALUE) {
                            newAmount = Integer.MAX_VALUE;
                        } else {
                            newAmount = currentAmount + itemStack.getAmount();
                        }
                    }

                    itemStacks.put(clone, newAmount);
                }
            }
        }

        for (BlockMenu blockMenu : getAdvancedGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }
                final ItemStack clone = StackUtils.getAsQuantity(itemStack, 1);
                final Integer currentAmount = itemStacks.get(clone);
                final int newAmount;
                if (currentAmount == null) {
                    newAmount = itemStack.getAmount();
                } else {
                    long newLong = (long) currentAmount + (long) itemStack.getAmount();
                    if (newLong > Integer.MAX_VALUE) {
                        newAmount = Integer.MAX_VALUE;
                    } else {
                        newAmount = currentAmount + itemStack.getAmount();
                    }
                }
                itemStacks.put(clone, newAmount);
            }
        }

        Map<StorageUnitData, Location> cacheMap = getOutputAbleCargoStorageUnitDatas();
        for (StorageUnitData cache : cacheMap.keySet()) {
            for (ItemContainer itemContainer : cache.getStoredItems()) {
                final Integer currentAmount = itemStacks.get(itemContainer.getSample());
                int newAmount;
                if (currentAmount == null) {
                    newAmount = itemContainer.getAmount();
                } else {
                    long newLong = (long) currentAmount + (long) itemContainer.getAmount();
                    if (newLong > Integer.MAX_VALUE) {
                        newAmount = Integer.MAX_VALUE;
                    } else {
                        newAmount = currentAmount + itemContainer.getAmount();
                    }
                }
                itemStacks.put(itemContainer.getSample(), newAmount);
            }
        }
        return itemStacks;
    }

    @Nonnull
    public Set<BarrelIdentity> getBarrels() {

        if (this.barrels != null) {
            return this.barrels;
        }

        final Set<Location> addedLocations = ConcurrentHashMap.newKeySet();
        final Set<BarrelIdentity> barrelSet = ConcurrentHashMap.newKeySet();

        for (Location cellLocation : this.monitors) {
            final BlockFace face = NetworkDirectional.getSelectedFace(cellLocation);

            if (face == null) {
                continue;
            }

            final Location testLocation = cellLocation.clone().add(face.getDirection());

            if (addedLocations.contains(testLocation)) {
                continue;
            } else {
                addedLocations.add(testLocation);
            }

            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(testLocation);

            if (Networks.getSupportedPluginManager()
                    .isInfinityExpansion() && slimefunItem instanceof StorageUnit unit) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) {
                    continue;
                }
                final InfinityBarrel infinityBarrel = getInfinityBarrel(menu, unit);
                if (infinityBarrel != null) {
                    barrelSet.add(infinityBarrel);
                }
                continue;
            } else if (Networks.getSupportedPluginManager().isFluffyMachines() && slimefunItem instanceof Barrel barrel) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) {
                    continue;
                }
                final FluffyBarrel fluffyBarrel = getFluffyBarrel(menu, barrel);
                if (fluffyBarrel != null) {
                    barrelSet.add(fluffyBarrel);
                }
                continue;
            } else if (slimefunItem instanceof NetworkQuantumStorage) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) {
                    continue;
                }
                final NetworkStorage storage = getNetworkStorage(menu);
                if (storage != null) {
                    barrelSet.add(storage);
                }
            }
        }

        this.barrels = barrelSet;
        return barrelSet;
    }

    @Nonnull
    public Map<StorageUnitData, Location> getCargoStorageUnitDatas() {
        if (this.cargoStorageUnitDatas != null) {
            return this.cargoStorageUnitDatas;
        }

        final Set<Location> addedLocations = ConcurrentHashMap.newKeySet();
        final Map<StorageUnitData, Location> dataSet = new HashMap<>();

        for (Location cellLocation : this.monitors) {
            final BlockFace face = NetworkDirectional.getSelectedFace(cellLocation);

            if (face == null) {
                continue;
            }

            final Location testLocation = cellLocation.clone().add(face.getDirection());

            if (addedLocations.contains(testLocation)) {
                continue;
            } else {
                addedLocations.add(testLocation);
            }

            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(testLocation);

            if (slimefunItem instanceof CargoStorageUnit) {
                final StorageUnitData data = getCargoStorageUnitData(testLocation);
                if (data != null) {
                    dataSet.put(data, testLocation);
                }
            }
        }

        this.cargoStorageUnitDatas = dataSet;
        return dataSet;
    }

    @Nullable
    private InfinityBarrel getInfinityBarrel(@Nonnull BlockMenu blockMenu, @Nonnull StorageUnit storageUnit) {
        final ItemStack itemStack = blockMenu.getItemInSlot(16);
        final var data = StorageCacheUtils.getBlock(blockMenu.getLocation());
        if (data == null) {
            return null;
        }
        final String storedString = data.getData("stored");

        if (storedString == null) {
            return null;
        }

        final int storedInt = Integer.parseInt(storedString);

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }


        final StorageCache cache = storageUnit.getCache(blockMenu.getLocation());

        if (cache == null) {
            return null;
        }

        final ItemStack clone = itemStack.clone();
        clone.setAmount(1);

        return new InfinityBarrel(
                blockMenu.getLocation(),
                clone,
                storedInt + itemStack.getAmount(),
                cache
        );
    }

    @Nullable
    private FluffyBarrel getFluffyBarrel(@Nonnull BlockMenu blockMenu, @Nonnull Barrel barrel) {
        Block block = blockMenu.getBlock();
        ItemStack itemStack;
        try {
            itemStack = barrel.getStoredItem(block);
        } catch (NullPointerException ignored) {
            return null;
        }

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }

        final ItemStack clone = itemStack.clone();

        int stored = barrel.getStored(block);

        if (stored <= 0) {
            return null;
        }
        int limit = barrel.getCapacity(block);
        boolean voidExcess = Boolean.parseBoolean(StorageCacheUtils.getData(blockMenu.getLocation(), "trash"));

        return new FluffyBarrel(
                blockMenu.getLocation(),
                clone,
                stored,
                limit,
                voidExcess
        );
    }

    @Nullable
    private NetworkStorage getNetworkStorage(@Nonnull BlockMenu blockMenu) {

        final QuantumCache cache = NetworkQuantumStorage.getCaches().get(blockMenu.getLocation());

        if (cache == null || cache.getItemStack() == null) {
            return null;
        }

        final ItemStack output = blockMenu.getItemInSlot(NetworkQuantumStorage.OUTPUT_SLOT);
        final ItemStack itemStack = cache.getItemStack();
        long storedInt = cache.getAmount();
        if (output != null && output.getType() != Material.AIR && StackUtils.itemsMatch(cache, output)) {
            storedInt = storedInt + output.getAmount();
        }

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }

        final ItemStack clone = itemStack.clone();
        clone.setAmount(1);

        return new NetworkStorage(
                blockMenu.getLocation(),
                clone,
                storedInt
        );
    }

    @Nullable
    private StorageUnitData getCargoStorageUnitData(@Nonnull BlockMenu blockMenu) {
        return CargoStorageUnit.getStorageData(blockMenu.getLocation());
    }

    @Nullable
    private StorageUnitData getCargoStorageUnitData(@Nonnull Location location) {
        return CargoStorageUnit.getStorageData(location);
    }

    @Nonnull
    public Set<BlockMenu> getCellMenus() {
        final Set<BlockMenu> menus = new HashSet<>();
        for (Location cellLocation : this.cells) {
            BlockMenu menu = StorageCacheUtils.getMenu(cellLocation);
            if (menu != null) {
                menus.add(menu);
            }
        }
        return menus;
    }

    @Nonnull
    public Set<BlockMenu> getCrafterOutputs() {
        final Set<BlockMenu> menus = new HashSet<>();
        for (Location location : this.crafters) {
            BlockMenu menu = StorageCacheUtils.getMenu(location);
            if (menu != null) {
                menus.add(menu);
            }
        }
        return menus;
    }

    @Nonnull
    public Set<BlockMenu> getGreedyBlockMenus() {
        final Set<BlockMenu> menus = new HashSet<>();
        for (Location location : this.greedyBlocks) {
            BlockMenu menu = StorageCacheUtils.getMenu(location);
            if (menu != null) {
                menus.add(menu);
            }
        }
        return menus;
    }

    @Nonnull
    public Set<BlockMenu> getAdvancedGreedyBlockMenus() {
        final Set<BlockMenu> menus = new HashSet<>();
        for (Location location : this.advancedGreedyBlocks) {
            BlockMenu menu = StorageCacheUtils.getMenu(location);
            if (menu != null) {
                menus.add(menu);
            }
        }
        return menus;
    }

    @Nullable
    public ItemStack getItemStack(@Nonnull ItemRequest request) {
        ItemStack stackToReturn = null;

        if (request.getAmount() <= 0) {
            return null;
        }

        // Barrels first
        for (BarrelIdentity barrelIdentity : getOutputAbleBarrels()) {

            final ItemStack itemStack = barrelIdentity.getItemStack();

            if (itemStack == null || !StackUtils.itemsMatch(request, itemStack)) {
                continue;
            }

            boolean infinity = barrelIdentity instanceof InfinityBarrel;
            final ItemStack fetched = barrelIdentity.requestItem(request);
            if (fetched == null || fetched.getType() == Material.AIR || (infinity && fetched.getAmount() == 1)) {
                continue;
            }

            // Stack is null, so we can fill it here
            if (stackToReturn == null) {
                stackToReturn = fetched.clone();
                stackToReturn.setAmount(0);
            }

            final int preserveAmount = infinity ? fetched.getAmount() - 1 : fetched.getAmount();

            if (request.getAmount() <= preserveAmount) {
                stackToReturn.setAmount(stackToReturn.getAmount() + request.getAmount());
                fetched.setAmount(fetched.getAmount() - request.getAmount());
                return stackToReturn;
            } else {
                stackToReturn.setAmount(stackToReturn.getAmount() + preserveAmount);
                request.receiveAmount(preserveAmount);
                fetched.setAmount(fetched.getAmount() - preserveAmount);
            }
        }

        // Units
        for (StorageUnitData cache : getOutputAbleCargoStorageUnitDatas().keySet()) {
            ItemStack take = cache.requestItem(request);
            if (take != null) {
                if (stackToReturn == null) {
                    stackToReturn = take.clone();
                } else {
                    stackToReturn.setAmount(stackToReturn.getAmount() + take.getAmount());
                }
                request.receiveAmount(stackToReturn.getAmount());

                if (request.getAmount() <= 0) {
                    return stackToReturn;
                }
            }
        }

        // Cells
        for (BlockMenu blockMenu : getCellMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack == null
                        || itemStack.getType() == Material.AIR
                        || !StackUtils.itemsMatch(request, itemStack)
                ) {
                    continue;
                }

                // Mark the Cell as dirty otherwise the changes will not save on shutdown
                blockMenu.markDirty();

                // If the return stack is null, we need to set it up
                if (stackToReturn == null) {
                    stackToReturn = itemStack.clone();
                    stackToReturn.setAmount(0);
                }

                if (request.getAmount() <= itemStack.getAmount()) {
                    // We can't take more than this stack. Level to request amount, remove items and then return
                    stackToReturn.setAmount(stackToReturn.getAmount() + request.getAmount());
                    itemStack.setAmount(itemStack.getAmount() - request.getAmount());
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
                if (itemStack == null || itemStack.getType() == Material.AIR || !StackUtils.itemsMatch(
                        request,
                        itemStack
                )) {
                    continue;
                }

                // Stack is null, so we can fill it here
                if (stackToReturn == null) {
                    stackToReturn = itemStack.clone();
                    stackToReturn.setAmount(0);
                }

                if (request.getAmount() <= itemStack.getAmount()) {
                    stackToReturn.setAmount(stackToReturn.getAmount() + request.getAmount());
                    itemStack.setAmount(itemStack.getAmount() - request.getAmount());
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
                if (itemStack == null || itemStack.getType() == Material.AIR || !StackUtils.itemsMatch(
                        request,
                        itemStack
                )) {
                    continue;
                }

                // Stack is null, so we can fill it here
                if (stackToReturn == null) {
                    stackToReturn = itemStack.clone();
                    stackToReturn.setAmount(0);
                }

                if (request.getAmount() <= itemStack.getAmount()) {
                    stackToReturn.setAmount(stackToReturn.getAmount() + request.getAmount());
                    itemStack.setAmount(itemStack.getAmount() - request.getAmount());
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
                    || !StackUtils.itemsMatch(request, itemStack)
            ) {
                continue;
            }

            // Mark the Cell as dirty otherwise the changes will not save on shutdown
            blockMenu.markDirty();

            // If the return stack is null, we need to set it up
            if (stackToReturn == null) {
                stackToReturn = itemStack.clone();
                stackToReturn.setAmount(0);
            }

            if (request.getAmount() <= itemStack.getAmount()) {
                // We can't take more than this stack. Level to request amount, remove items and then return
                stackToReturn.setAmount(stackToReturn.getAmount() + request.getAmount());
                itemStack.setAmount(itemStack.getAmount() - request.getAmount());
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

        return stackToReturn;
    }

    public boolean contains(@Nonnull ItemRequest request) {

        long found = 0;

        // Barrels
        for (BarrelIdentity barrelIdentity : getOutputAbleBarrels()) {
            final ItemStack itemStack = barrelIdentity.getItemStack();

            if (itemStack == null || !StackUtils.itemsMatch(request, itemStack)) {
                continue;
            }

            if (barrelIdentity instanceof InfinityBarrel) {
                if (barrelIdentity.getItemStack().getMaxStackSize() > 1) {
                    found += barrelIdentity.getAmount() - 2;
                }
            } else {
                found += barrelIdentity.getAmount();
            }

            // Escape if found all we need
            if (found >= request.getAmount()) {
                return true;
            }
        }

        // Crafters
        for (BlockMenu blockMenu : getCrafterOutputs()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack == null
                        || itemStack.getType() == Material.AIR
                        || !StackUtils.itemsMatch(request, itemStack)
                ) {
                    continue;
                }

                found += itemStack.getAmount();

                // Escape if found all we need
                if (found >= request.getAmount()) {
                    return true;
                }
            }
        }

        Map<StorageUnitData, Location> cacheMap = getOutputAbleCargoStorageUnitDatas();
        for (StorageUnitData cache : cacheMap.keySet()) {
            final List<ItemContainer> storedItems = cache.getStoredItems();
            for (ItemContainer itemContainer : storedItems) {
                final ItemStack itemStack = itemContainer.getSample();
                if (itemStack == null
                        || itemStack.getType() == Material.AIR
                        || !StackUtils.itemsMatch(request, itemStack)
                ) {
                    continue;
                }

                int amount = itemContainer.getAmount();
                found += amount;


                // Escape if found all we need
                if (found >= request.getAmount()) {
                    return true;
                }
            }
        }

        for (BlockMenu blockMenu : getAdvancedGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack == null
                        || itemStack.getType() == Material.AIR
                        || !StackUtils.itemsMatch(request, itemStack)
                ) {
                    continue;
                }

                found += itemStack.getAmount();

                // Escape if found all we need
                if (found >= request.getAmount()) {
                    return true;
                }
            }
        }

        // Greedy Blocks
        for (BlockMenu blockMenu : getGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            final ItemStack itemStack = blockMenu.getItemInSlot(slots[0]);
            if (itemStack == null
                    || itemStack.getType() == Material.AIR
                    || !StackUtils.itemsMatch(request, itemStack)
            ) {
                continue;
            }

            found += itemStack.getAmount();

            // Escape if found all we need
            if (found >= request.getAmount()) {
                return true;
            }
        }

        // Cells
        for (BlockMenu blockMenu : getCellMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack == null
                        || itemStack.getType() == Material.AIR
                        || !StackUtils.itemsMatch(request, itemStack)
                ) {
                    continue;
                }

                found += itemStack.getAmount();

                // Escape if found all we need
                if (found >= request.getAmount()) {
                    return true;
                }
            }
        }

        return false;
    }

    public int getAmount(@Nonnull ItemStack itemStack) {
        long totalAmount = 0;
        for (BlockMenu menu : getAdvancedGreedyBlockMenus()) {
            int[] slots = menu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack inputSlotItem = menu.getItemInSlot(slot);
                if (inputSlotItem != null && StackUtils.itemsMatch(inputSlotItem, itemStack)) {
                    totalAmount += inputSlotItem.getAmount();
                }
            }
        }
        // 遍历所有贪婪方块
        for (BlockMenu blockMenu : getGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            ItemStack inputSlotItem = blockMenu.getItemInSlot(slots[0]);
            if (inputSlotItem != null && StackUtils.itemsMatch(inputSlotItem, itemStack)) {
                totalAmount += inputSlotItem.getAmount();
            }
        }
        // 遍历所有桶
        for (BarrelIdentity barrelIdentity : getOutputAbleBarrels()) {
            if (StackUtils.itemsMatch(barrelIdentity.getItemStack(), itemStack)) {
                totalAmount += barrelIdentity.getAmount();
                if (barrelIdentity instanceof InfinityBarrel) {
                    totalAmount -= 2;
                }
            }
        }
        Map<StorageUnitData, Location> cacheMap = getOutputAbleCargoStorageUnitDatas();
        for (StorageUnitData cache : cacheMap.keySet()) {
            final List<ItemContainer> storedItems = cache.getStoredItems();
            for (ItemContainer itemContainer : storedItems) {
                if (StackUtils.itemsMatch(itemContainer.getSample(), itemStack)) {
                    totalAmount += itemContainer.getAmount();
                }
            }
        }
        // 遍历所有单元格菜单
        for (BlockMenu blockMenu : getCellMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack cellItem = blockMenu.getItemInSlot(slot);
                if (cellItem != null && StackUtils.itemsMatch(cellItem, itemStack)) {
                    totalAmount += cellItem.getAmount();
                }
            }
        }
        if (totalAmount > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return (int) totalAmount;
        }
    }

    public HashMap<ItemStack, Long> getAmount(@Nonnull Set<ItemStack> itemStacks) {
        HashMap<ItemStack, Long> totalAmounts = new HashMap<>();
        for (BlockMenu menu : getAdvancedGreedyBlockMenus()) {
            int[] slots = menu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack inputSlotItem = menu.getItemInSlot(slot);
                if (inputSlotItem != null) {
                    for (ItemStack itemStack : itemStacks) {
                        if (StackUtils.itemsMatch(inputSlotItem, itemStack)) {
                            totalAmounts.put(itemStack, totalAmounts.getOrDefault(itemStack, 0L) + inputSlotItem.getAmount());
                        }
                    }
                }
            }
        }
        // 遍历所有贪婪方块
        for (BlockMenu blockMenu : getGreedyBlockMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            ItemStack inputSlotItem = blockMenu.getItemInSlot(slots[0]);
            if (inputSlotItem != null) {
                for (ItemStack itemStack : itemStacks) {
                    if (StackUtils.itemsMatch(inputSlotItem, itemStack)) {
                        totalAmounts.put(itemStack, totalAmounts.getOrDefault(itemStack, 0L) + inputSlotItem.getAmount());
                    }
                }
            }
        }
        // 遍历所有桶
        for (BarrelIdentity barrelIdentity : getOutputAbleBarrels()) {
            for (ItemStack itemStack : itemStacks) {
                if (StackUtils.itemsMatch(barrelIdentity.getItemStack(), itemStack)) {
                    long totalAmount = barrelIdentity.getAmount();
                    if (barrelIdentity instanceof InfinityBarrel) {
                        totalAmount -= 2;
                    }
                    totalAmounts.put(itemStack, totalAmounts.getOrDefault(itemStack, 0L) + totalAmount);
                }
            }
        }
        Map<StorageUnitData, Location> cacheMap = getOutputAbleCargoStorageUnitDatas();
        for (StorageUnitData cache : cacheMap.keySet()) {
            final List<ItemContainer> storedItems = cache.getStoredItems();
            for (ItemContainer itemContainer : storedItems) {
                for (ItemStack itemStack : itemStacks) {
                    if (StackUtils.itemsMatch(itemContainer.getSample(), itemStack)) {
                        long totalAmount = itemContainer.getAmount();
                        totalAmounts.put(itemStack, totalAmounts.getOrDefault(itemStack, 0L) + totalAmount);
                    }
                }
            }
        }
        // 遍历所有单元格菜单
        for (BlockMenu blockMenu : getCellMenus()) {
            int[] slots = blockMenu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack cellItem = blockMenu.getItemInSlot(slot);
                if (cellItem != null) {
                    for (ItemStack itemStack : itemStacks) {
                        if (StackUtils.itemsMatch(cellItem, itemStack)) {
                            totalAmounts.put(itemStack, totalAmounts.getOrDefault(itemStack, 0L) + cellItem.getAmount());
                        }
                    }
                }
            }
        }

        return totalAmounts;
    }

    public void addItemStack(@Nonnull ItemStack incoming) {
        BlockMenu fallbackBlockMenu = null;
        int fallBackSlot = 0;
        for (BlockMenu menu : getAdvancedGreedyBlockMenus()) {
            if (StackUtils.itemsMatch(menu.getItemInSlot(AdvancedGreedyBlock.TEMPLATE_SLOT), incoming)) {
                for (int slot : ADVANCED_GREEDY_BLOCK_AVAILABLE_SLOTS) {
                    final ItemStack itemStack = menu.getItemInSlot(slot);
                    // If this is an empty slot - move on, if it's our first, store it for later.
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        if (fallbackBlockMenu == null) {
                            fallbackBlockMenu = menu;
                            fallBackSlot = slot;
                        }
                        continue;
                    }

                    final int itemStackAmount = itemStack.getAmount();
                    final int incomingStackAmount = incoming.getAmount();
                    if (itemStackAmount < itemStack.getMaxStackSize() && StackUtils.itemsMatch(incoming, itemStack)) {
                        final int maxCanAdd = itemStack.getMaxStackSize() - itemStackAmount;
                        final int amountToAdd = Math.min(maxCanAdd, incomingStackAmount);

                        itemStack.setAmount(itemStackAmount + amountToAdd);
                        incoming.setAmount(incomingStackAmount - amountToAdd);

                        // Mark dirty otherwise changes will not save
                        menu.markDirty();

                        // All distributed, can escape
                        if (incomingStackAmount == 0) {
                            return;
                        }
                    }
                }

                if (fallbackBlockMenu != null) {
                    fallbackBlockMenu.replaceExistingItem(fallBackSlot, incoming.clone());
                    incoming.setAmount(0);
                }

                return;
            }
        }

        // Run for matching greedy blocks
        for (BlockMenu blockMenu : getGreedyBlockMenus()) {
            final ItemStack template = blockMenu.getItemInSlot(NetworkGreedyBlock.TEMPLATE_SLOT);

            if (template == null || template.getType() == Material.AIR || !StackUtils.itemsMatch(incoming, template)) {
                continue;
            }

            final ItemStack itemStack = blockMenu.getItemInSlot(GREEDY_BLOCK_AVAILABLE_SLOTS[0]);

            if (itemStack == null || itemStack.getType() == Material.AIR) {
                blockMenu.replaceExistingItem(GREEDY_BLOCK_AVAILABLE_SLOTS[0], incoming.clone());
                incoming.setAmount(0);
                return;
            }

            final int itemStackAmount = itemStack.getAmount();
            final int incomingStackAmount = incoming.getAmount();
            if (itemStackAmount < itemStack.getMaxStackSize() && StackUtils.itemsMatch(itemStack, incoming)) {
                final int maxCanAdd = itemStack.getMaxStackSize() - itemStackAmount;
                final int amountToAdd = Math.min(maxCanAdd, incomingStackAmount);

                itemStack.setAmount(itemStackAmount + amountToAdd);
                incoming.setAmount(incomingStackAmount - amountToAdd);
            }
            // Given we have found a match, it doesn't matter if the item moved or not, we will not bring it in
            return;
        }


        // Run for matching barrels
        for (BarrelIdentity barrelIdentity : getInputAbleBarrels()) {
            if (StackUtils.itemsMatch(barrelIdentity, incoming)) {
                barrelIdentity.depositItemStack(incoming);

                // All distributed, can escape
                if (incoming.getAmount() == 0) {
                    return;
                }
            }
        }

        for (StorageUnitData cache : getInputAbleCargoStorageUnitDatas().keySet()) {
            cache.depositItemStack(incoming, true);

            // 填充完成
            if (incoming.getAmount() == 0) {
                return;
            }
        }

        // Then run for matching items in cells
        BlockMenu fallbackBlockMenu2 = null;
        int fallBackSlot2 = 0;

        for (BlockMenu blockMenu : getCellMenus()) {
            for (int slot : CELL_AVAILABLE_SLOTS) {
                ItemStack itemStack = blockMenu.getItemInSlot(slot);
                // If this is an empty slot - move on, if it's our first, store it for later.
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    if (fallbackBlockMenu2 == null) {
                        fallbackBlockMenu2 = blockMenu;
                        fallBackSlot2 = slot;
                    }
                    continue;
                }

                final int itemStackAmount = itemStack.getAmount();
                final int incomingStackAmount = incoming.getAmount();

                if (itemStackAmount < itemStack.getMaxStackSize() && StackUtils.itemsMatch(incoming, itemStack)) {
                    final int maxCanAdd = itemStack.getMaxStackSize() - itemStackAmount;
                    final int amountToAdd = Math.min(maxCanAdd, incomingStackAmount);

                    itemStack.setAmount(itemStackAmount + amountToAdd);
                    incoming.setAmount(incomingStackAmount - amountToAdd);

                    // Mark dirty otherwise changes will not save
                    blockMenu.markDirty();

                    // All distributed, can escape
                    if (incomingStackAmount == 0) {
                        return;
                    }
                }
            }
        }

        // Add to fallback slot
        if (fallbackBlockMenu2 != null) {
            fallbackBlockMenu2.replaceExistingItem(fallBackSlot2, incoming.clone());
            incoming.setAmount(0);
        }
    }

    @Override
    public long retrieveBlockCharge() {
        return 0;
    }

    public void setRootPower(long power) {
        this.rootPower = power;
    }

    public void addRootPower(long power) {
        this.rootPower += power;
    }

    public void removeRootPower(long power) {
        int removed = 0;
        for (Location node : powerNodes) {
            final SlimefunItem item = StorageCacheUtils.getSfItem(node);
            if (item instanceof NetworkPowerNode powerNode) {
                final int charge = powerNode.getCharge(node);
                if (charge <= 0) {
                    continue;
                }
                final int toRemove = (int) Math.min(power - removed, charge);
                powerNode.removeCharge(node, toRemove);
                this.rootPower -= power;
                removed = removed + toRemove;
            }
            if (removed >= power) {
                return;
            }
        }
    }

    public void setDisplayParticles(boolean displayParticles) {
        this.displayParticles = displayParticles;
    }

    @Nonnull
    public List<ItemStack> getItemStacks(@Nonnull List<ItemRequest> itemRequests) {
        List<ItemStack> retrievedItems = new ArrayList<>();

        for (ItemRequest request : itemRequests) {
            ItemStack retrieved = getItemStack(request);
            if (retrieved != null) {
                retrievedItems.add(retrieved);
            }
        }
        return retrievedItems;
    }

    @Nonnull
    public Set<BarrelIdentity> getInputAbleBarrels() {

        if (this.inputAbleBarrels != null) {
            return this.inputAbleBarrels;
        }

        final Set<Location> addedLocations = ConcurrentHashMap.newKeySet();
        final Set<BarrelIdentity> barrelSet = ConcurrentHashMap.newKeySet();

        final Set<Location> monitor = new HashSet<>();
        monitor.addAll(this.inputOnlyMonitors);
        monitor.addAll(this.monitors);
        for (Location cellLocation : monitor) {
            final BlockFace face = NetworkDirectional.getSelectedFace(cellLocation);

            if (face == null) {
                continue;
            }

            final Location testLocation = cellLocation.clone().add(face.getDirection());

            if (addedLocations.contains(testLocation)) {
                continue;
            } else {
                addedLocations.add(testLocation);
            }

            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(testLocation);

            if (Networks.getSupportedPluginManager().isInfinityExpansion() && slimefunItem instanceof StorageUnit unit) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) {
                    continue;
                }
                final InfinityBarrel infinityBarrel = getInfinityBarrel(menu, unit);
                if (infinityBarrel != null) {
                    barrelSet.add(infinityBarrel);
                }
                continue;
            }
            if (Networks.getSupportedPluginManager().isFluffyMachines() && slimefunItem instanceof Barrel barrel) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) {
                    continue;
                }
                final FluffyBarrel fluffyBarrel = getFluffyBarrel(menu, barrel);
                if (fluffyBarrel != null) {
                    barrelSet.add(fluffyBarrel);
                }
                continue;
            }
            if (slimefunItem instanceof NetworkQuantumStorage) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) {
                    continue;
                }
                final NetworkStorage storage = getNetworkStorage(menu);
                if (storage != null) {
                    barrelSet.add(storage);
                }
            }
        }

        this.inputAbleBarrels = barrelSet;
        return barrelSet;
    }

    @Nonnull
    public Set<BarrelIdentity> getOutputAbleBarrels() {

        if (this.outputAbleBarrels != null) {
            return this.outputAbleBarrels;
        }

        final Set<Location> addedLocations = ConcurrentHashMap.newKeySet();
        final Set<BarrelIdentity> barrelSet = ConcurrentHashMap.newKeySet();

        final Set<Location> monitor = new HashSet<>();
        monitor.addAll(this.outputOnlyMonitors);
        monitor.addAll(this.monitors);
        for (Location cellLocation : monitor) {
            final BlockFace face = NetworkDirectional.getSelectedFace(cellLocation);

            if (face == null) {
                continue;
            }

            final Location testLocation = cellLocation.clone().add(face.getDirection());

            if (addedLocations.contains(testLocation)) {
                continue;
            } else {
                addedLocations.add(testLocation);
            }

            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(testLocation);

            if (Networks.getSupportedPluginManager().isInfinityExpansion() && slimefunItem instanceof StorageUnit unit) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) {
                    continue;
                }
                final InfinityBarrel infinityBarrel = getInfinityBarrel(menu, unit);
                if (infinityBarrel != null) {
                    barrelSet.add(infinityBarrel);
                }
                continue;
            }
            if (Networks.getSupportedPluginManager().isFluffyMachines() && slimefunItem instanceof Barrel barrel) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) {
                    continue;
                }
                final FluffyBarrel fluffyBarrel = getFluffyBarrel(menu, barrel);
                if (fluffyBarrel != null) {
                    barrelSet.add(fluffyBarrel);
                }
                continue;
            }
            if (slimefunItem instanceof NetworkQuantumStorage) {
                final BlockMenu menu = StorageCacheUtils.getMenu(testLocation);
                if (menu == null) {
                    continue;
                }
                final NetworkStorage storage = getNetworkStorage(menu);
                if (storage != null) {
                    barrelSet.add(storage);
                }
            }
        }

        this.outputAbleBarrels = barrelSet;
        return barrelSet;
    }

    @Nonnull
    public Map<StorageUnitData, Location> getInputAbleCargoStorageUnitDatas() {
        if (this.inputAbleCargoStorageUnitDatas != null) {
            return this.inputAbleCargoStorageUnitDatas;
        }

        final Set<Location> addedLocations = ConcurrentHashMap.newKeySet();
        final Map<StorageUnitData, Location> dataSet = new HashMap<>();

        final Set<Location> monitor = new HashSet<>();
        monitor.addAll(this.inputOnlyMonitors);
        monitor.addAll(this.monitors);
        for (Location cellLocation : monitor) {
            final BlockFace face = NetworkDirectional.getSelectedFace(cellLocation);

            if (face == null) {
                continue;
            }

            final Location testLocation = cellLocation.clone().add(face.getDirection());

            if (addedLocations.contains(testLocation)) {
                continue;
            } else {
                addedLocations.add(testLocation);
            }

            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(testLocation);

            if (slimefunItem instanceof CargoStorageUnit) {
                final StorageUnitData data = getCargoStorageUnitData(testLocation);
                if (data != null) {
                    dataSet.put(data, testLocation);
                }
            }
        }

        this.inputAbleCargoStorageUnitDatas = dataSet;
        return dataSet;
    }

    @Nonnull
    public Map<StorageUnitData, Location> getOutputAbleCargoStorageUnitDatas() {
        if (this.outputAbleCargoStorageUnitDatas != null) {
            return this.outputAbleCargoStorageUnitDatas;
        }

        final Set<Location> addedLocations = ConcurrentHashMap.newKeySet();
        final Map<StorageUnitData, Location> dataSet = new HashMap<>();

        final Set<Location> monitor = new HashSet<>();
        monitor.addAll(this.outputOnlyMonitors);
        monitor.addAll(this.monitors);
        for (Location cellLocation : monitor) {
            final BlockFace face = NetworkDirectional.getSelectedFace(cellLocation);

            if (face == null) {
                continue;
            }

            final Location testLocation = cellLocation.clone().add(face.getDirection());

            if (addedLocations.contains(testLocation)) {
                continue;
            } else {
                addedLocations.add(testLocation);
            }

            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(testLocation);

            if (slimefunItem instanceof CargoStorageUnit) {
                final StorageUnitData data = getCargoStorageUnitData(testLocation);
                if (data != null) {
                    dataSet.put(data, testLocation);
                }
            }
        }

        this.outputAbleCargoStorageUnitDatas = dataSet;
        return dataSet;
    }
}