package com.balugaq.netex.utils;

import com.balugaq.netex.api.data.VanillaInventoryWrapper;
import com.balugaq.netex.api.enums.TransportMode;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import lombok.experimental.UtilityClass;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("DuplicatedCode")
@UtilityClass
public class LineOperationUtil {
    public static final Location UNKNOWN_LOCATION = new Location(null, 0, 0, 0);

    public static void doOperation(
        @NotNull Location startLocation,
        @NotNull BlockFace direction,
        int limit,
        @NotNull Consumer<BlockMenu> consumer) {
        doOperation(startLocation, direction, limit, false, true, consumer);
    }

    public static void doOperation(
        @NotNull Location startLocation,
        @NotNull BlockFace direction,
        int limit,
        boolean skipNoMenu,
        @NotNull Consumer<BlockMenu> consumer) {
        doOperation(startLocation, direction, limit, skipNoMenu, true, consumer);
    }

    public static void doOperation(
        @NotNull Location startLocation,
        @NotNull BlockFace direction,
        int limit,
        boolean skipNoMenu,
        boolean optimizeExperience,
        @NotNull Consumer<BlockMenu> consumer) {
        Location location = startLocation.clone();
        int finalLimit = limit;
        if (optimizeExperience) {
            finalLimit += 1;
        }
        for (int i = 0; i < finalLimit; i++) {
            switch (direction) {
                case NORTH -> location.setZ(location.getZ() - 1);
                case SOUTH -> location.setZ(location.getZ() + 1);
                case EAST -> location.setX(location.getX() + 1);
                case WEST -> location.setX(location.getX() - 1);
                case UP -> location.setY(location.getY() + 1);
                case DOWN -> location.setY(location.getY() - 1);
            }
            final BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
            if (blockMenu == null) {
                if (skipNoMenu) {
                    continue;
                } else {
                    return;
                }
            }
            consumer.accept(blockMenu);
        }
    }

    public static void doVanillaOperation(
        @NotNull Location startLocation,
        @NotNull BlockFace direction,
        int limit,
        @NotNull Consumer<BlockMenu> consumer) {
        doVanillaOperation(startLocation, direction, limit, false, false, consumer);
    }

    public static void doVanillaOperation(
        @NotNull Location startLocation,
        @NotNull BlockFace direction,
        int limit,
        boolean skipNoInventory,
        @NotNull Consumer<BlockMenu> consumer) {
        doVanillaOperation(startLocation, direction, limit, skipNoInventory, false, consumer);
    }

    public static void doVanillaOperation(
        @NotNull Location startLocation,
        @NotNull BlockFace direction,
        int limit,
        boolean skipNoInventory,
        boolean optimizeExperience,
        @NotNull Consumer<BlockMenu> consumer) {
        Location location = startLocation.clone();
        int finalLimit = limit;
        if (optimizeExperience) {
            finalLimit += 1;
        }
        for (int i = 0; i < finalLimit; i++) {
            switch (direction) {
                case NORTH -> location.setZ(location.getZ() - 1);
                case SOUTH -> location.setZ(location.getZ() + 1);
                case EAST -> location.setX(location.getX() + 1);
                case WEST -> location.setX(location.getX() - 1);
                case UP -> location.setY(location.getY() + 1);
                case DOWN -> location.setY(location.getY() - 1);
            }
            BlockState state = location.getBlock().getState(false);
            if (state instanceof InventoryHolder holder) {
                Inventory inv = holder.getInventory();
                if (inv != null) {
                    var wrapper = new VanillaInventoryWrapper(inv, state);
                    consumer.accept(wrapper);
                }
            } else {
                if (skipNoInventory) {
                    continue;
                } else {
                    return;
                }
            }
        }
    }

    public static void doEnergyOperation(
        @NotNull Location startLocation,
        @NotNull BlockFace direction,
        int limit,
        @NotNull Consumer<Location> consumer) {
        doEnergyOperation(startLocation, direction, limit, true, true, consumer);
    }

    public static void doEnergyOperation(
        @NotNull Location startLocation,
        @NotNull BlockFace direction,
        int limit,
        boolean allowNoMenu,
        @NotNull Consumer<Location> consumer) {
        doEnergyOperation(startLocation, direction, limit, allowNoMenu, true, consumer);
    }

    public static void doEnergyOperation(
        @NotNull Location startLocation,
        @NotNull BlockFace direction,
        int limit,
        boolean allowNoMenu,
        boolean optimizeExperience,
        @NotNull Consumer<Location> consumer) {
        Location location = startLocation.clone();
        int finalLimit = limit;
        if (optimizeExperience) {
            finalLimit += 1;
        }
        for (int i = 0; i < finalLimit; i++) {
            switch (direction) {
                case NORTH -> location.setZ(location.getZ() - 1);
                case SOUTH -> location.setZ(location.getZ() + 1);
                case EAST -> location.setX(location.getX() + 1);
                case WEST -> location.setX(location.getX() - 1);
                case UP -> location.setY(location.getY() + 1);
                case DOWN -> location.setY(location.getY() - 1);
            }
            final BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
            if (blockMenu == null) {
                if (!allowNoMenu) {
                    return;
                }
            }
            consumer.accept(location);
        }
    }

    @Deprecated
    public static void grabItem(
        @NotNull NetworkRoot root,
        @NotNull BlockMenu blockMenu,
        @NotNull TransportMode transportMode,
        int limitQuantity) {
        grabItem(UNKNOWN_LOCATION, root, blockMenu, transportMode, limitQuantity);
    }

    /**
     * @param accessor      the target menu's location
     * @param root          the root
     * @param blockMenu     the target menu
     * @param transportMode the transport mode
     * @param limitQuantity the max amount to transport
     */
    public static void grabItem(
        @NotNull Location accessor,
        @NotNull NetworkRoot root,
        @NotNull BlockMenu blockMenu,
        @NotNull TransportMode transportMode,
        int limitQuantity) {
        final int[] slots =
            blockMenu.getPreset().getSlotsAccessedByItemTransport(blockMenu, ItemTransportFlow.WITHDRAW, null);

        int limit = limitQuantity;
        switch (transportMode) {
            case NONE, NONNULL_ONLY -> {
                /*
                 * Grab all the items.
                 */
                for (int slot : slots) {
                    final ItemStack item = blockMenu.getItemInSlot(slot);
                    if (item != null && item.getType() != Material.AIR) {
                        final int exceptedReceive = Math.min(item.getAmount(), limit);
                        final ItemStack clone = StackUtils.getAsQuantity(item, exceptedReceive);
                        root.addItemStack0(accessor, clone);
                        item.setAmount(item.getAmount() - (exceptedReceive - clone.getAmount()));
                        limit -= exceptedReceive - clone.getAmount();
                        if (limit <= 0) {
                            break;
                        }
                    }
                }
            }
            case NULL_ONLY -> {
                /*
                 * Nothing to do.
                 */
            }
            case FIRST_ONLY -> {
                /*
                 * Grab the first item only.
                 */
                if (slots.length > 0) {
                    final ItemStack item = blockMenu.getItemInSlot(slots[0]);
                    if (item != null && item.getType() != Material.AIR) {
                        final int exceptedReceive = Math.min(item.getAmount(), limit);
                        final ItemStack clone = StackUtils.getAsQuantity(item, exceptedReceive);
                        root.addItemStack0(accessor, clone);
                        item.setAmount(item.getAmount() - (exceptedReceive - clone.getAmount()));
                        clone.getAmount();
                    }
                }
            }
            case LAST_ONLY -> {
                /*
                 * Grab the last item only.
                 */
                if (slots.length > 0) {
                    final ItemStack item = blockMenu.getItemInSlot(slots[slots.length - 1]);
                    if (item != null && item.getType() != Material.AIR) {
                        final int exceptedReceive = Math.min(item.getAmount(), limit);
                        final ItemStack clone = StackUtils.getAsQuantity(item, exceptedReceive);
                        root.addItemStack0(accessor, clone);
                        item.setAmount(item.getAmount() - (exceptedReceive - clone.getAmount()));
                        clone.getAmount();
                    }
                }
            }
            case FIRST_STOP -> {
                /*
                 * Grab the first non-null item only.
                 */
                for (int slot : slots) {
                    final ItemStack item = blockMenu.getItemInSlot(slot);
                    if (item != null && item.getType() != Material.AIR) {
                        final int exceptedReceive = Math.min(item.getAmount(), limit);
                        final ItemStack clone = StackUtils.getAsQuantity(item, exceptedReceive);
                        root.addItemStack0(accessor, clone);
                        item.setAmount(item.getAmount() - (exceptedReceive - clone.getAmount()));
                        clone.getAmount();
                        break;
                    }
                }
            }
            case LAZY -> {
                /*
                 * When it's first item is non-null, we will grab all the items.
                 */
                if (slots.length > 0) {
                    final ItemStack delta = blockMenu.getItemInSlot(slots[0]);
                    if (delta != null && delta.getType() != Material.AIR) {
                        for (int slot : slots) {
                            ItemStack item = blockMenu.getItemInSlot(slot);
                            if (item != null && item.getType() != Material.AIR) {
                                final int exceptedReceive = Math.min(item.getAmount(), limit);
                                final ItemStack clone = StackUtils.getAsQuantity(item, exceptedReceive);
                                root.addItemStack0(accessor, clone);
                                item.setAmount(item.getAmount() - (exceptedReceive - clone.getAmount()));
                                limit -= exceptedReceive - clone.getAmount();
                                if (limit <= 0) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            case VOID -> {
                /*
                 * Grab all the items or trash it
                 */
                for (int slot : slots) {
                    final ItemStack item = blockMenu.getItemInSlot(slot);
                    if (item != null && item.getType() != Material.AIR) {
                        final int exceptedReceive = Math.min(item.getAmount(), limit);
                        final ItemStack clone = StackUtils.getAsQuantity(item, exceptedReceive);
                        root.addItemStack0(accessor, clone);
                        limit -= exceptedReceive - clone.getAmount();
                        item.setAmount(0);
                        if (limit <= 0) {
                            break;
                        }
                    }
                }
            }
            case SPECIFIED_QUANTITY -> {
                java.util.Map<Integer, ItemStack> itemSamples = new java.util.LinkedHashMap<>();
                java.util.Map<Integer, Integer> itemTotals = new java.util.LinkedHashMap<>();
                int typeIndex = 0;
                for (int slot : slots) {
                    final ItemStack item = blockMenu.getItemInSlot(slot);
                    if (item == null || item.getType() == Material.AIR) {
                        continue;
                    }
                    boolean found = false;
                    for (var entry : itemSamples.entrySet()) {
                        if (StackUtils.itemsMatch(entry.getValue(), item)) {
                            itemTotals.merge(entry.getKey(), item.getAmount(), Integer::sum);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        itemSamples.put(typeIndex, StackUtils.getAsQuantity(item, 1));
                        itemTotals.put(typeIndex, item.getAmount());
                        typeIndex++;
                    }
                }
                for (var entry : itemSamples.entrySet()) {
                    final int total = itemTotals.get(entry.getKey());
                    if (total <= limitQuantity) {
                        continue;
                    }
                    int toRemove = total - limitQuantity;
                    for (int i = slots.length - 1; i >= 0 && toRemove > 0; i--) {
                        final ItemStack item = blockMenu.getItemInSlot(slots[i]);
                        if (item == null || item.getType() == Material.AIR) {
                            continue;
                        }
                        if (!StackUtils.itemsMatch(entry.getValue(), item)) {
                            continue;
                        }
                        final int grabFromSlot = Math.min(item.getAmount(), toRemove);
                        final int beforeAmount = item.getAmount();
                        item.setAmount(grabFromSlot);
                        root.addItemStack0(accessor, item);
                        final int afterAmount = item.getAmount();
                        final int actualGrabbed = grabFromSlot - afterAmount;
                        item.setAmount(beforeAmount - actualGrabbed);
                        toRemove -= actualGrabbed;
                    }
                }
            }
        }
    }

    @Deprecated
    public static void pushItem(
        @NotNull NetworkRoot root,
        @NotNull BlockMenu blockMenu,
        @NotNull List<ItemStack> clones,
        @NotNull TransportMode transportMode,
        int limitQuantity) {
        pushItem(UNKNOWN_LOCATION, root, blockMenu, clones, transportMode, limitQuantity);
    }

    /**
     * @param accessor      the target menu's location
     * @param root          the root
     * @param blockMenu     the target menu
     * @param transportMode the transport mode
     * @param limitQuantity the max amount to transport
     */
    public static void pushItem(
        @NotNull Location accessor,
        @NotNull NetworkRoot root,
        @NotNull BlockMenu blockMenu,
        @NotNull List<ItemStack> clones,
        @NotNull TransportMode transportMode,
        int limitQuantity) {
        for (ItemStack clone : clones) {
            pushItem(accessor, root, blockMenu, clone, transportMode, limitQuantity);
        }
    }

    @Deprecated
    public static void pushItem(
        @NotNull NetworkRoot root,
        @NotNull BlockMenu blockMenu,
        @NotNull ItemStack clone,
        @NotNull TransportMode transportMode,
        int limitQuantity) {
        pushItem(UNKNOWN_LOCATION, root, blockMenu, clone, transportMode, limitQuantity);
    }

    public static void pushItem(
        @NotNull Location accessor,
        @NotNull NetworkRoot root,
        @NotNull BlockMenu blockMenu,
        @NotNull ItemStack clone,
        @NotNull TransportMode transportMode,
        int limitQuantity) {
        final ItemRequest itemRequest = new ItemRequest(clone, clone.getMaxStackSize());

        final int[] slots =
            blockMenu.getPreset().getSlotsAccessedByItemTransport(blockMenu, ItemTransportFlow.INSERT, clone);
        switch (transportMode) {
            case NONE -> {
                int freeSpace = 0;
                for (int slot : slots) {
                    final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        freeSpace += clone.getMaxStackSize();
                    } else {
                        if (itemStack.getAmount() >= clone.getMaxStackSize()) {
                            continue;
                        }
                        if (StackUtils.itemsMatch(itemRequest, itemStack)) {
                            final int availableSpace = itemStack.getMaxStackSize() - itemStack.getAmount();
                            if (availableSpace > 0) {
                                freeSpace += availableSpace;
                            }
                        }
                    }
                }
                if (freeSpace <= 0) {
                    return;
                }
                itemRequest.setAmount(Math.min(freeSpace, limitQuantity));

                final ItemStack retrieved = root.getItemStack0(accessor, itemRequest);
                if (retrieved != null && retrieved.getType() != Material.AIR) {
                    BlockMenuUtil.pushItem(blockMenu, retrieved, slots);
                }
            }

            case NULL_ONLY -> {
                int free = limitQuantity;
                for (int slot : slots) {
                    final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        itemRequest.setAmount(clone.getMaxStackSize());
                    } else {
                        continue;
                    }
                    itemRequest.setAmount(Math.min(itemRequest.getAmount(), free));

                    final ItemStack retrieved = root.getItemStack0(accessor, itemRequest);
                    if (retrieved != null && retrieved.getType() != Material.AIR) {
                        free -= retrieved.getAmount();
                        BlockMenuUtil.pushItem(blockMenu, retrieved, slot);
                        if (free <= 0) {
                            break;
                        }
                    }
                }
            }

            case NONNULL_ONLY -> {
                int free = limitQuantity;
                for (int slot : slots) {
                    final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }
                    if (itemStack.getAmount() >= clone.getMaxStackSize()) {
                        continue;
                    }
                    if (StackUtils.itemsMatch(itemRequest, itemStack)) {
                        final int space = itemStack.getMaxStackSize() - itemStack.getAmount();
                        if (space > 0) {
                            itemRequest.setAmount(space);
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                    itemRequest.setAmount(Math.min(itemRequest.getAmount(), free));

                    final ItemStack retrieved = root.getItemStack0(accessor, itemRequest);
                    if (retrieved != null && retrieved.getType() != Material.AIR) {
                        free -= retrieved.getAmount();
                        BlockMenuUtil.pushItem(blockMenu, retrieved, slot);
                        if (free <= 0) {
                            break;
                        }
                    }
                }
            }
            case FIRST_ONLY -> {
                if (slots.length == 0) {
                    break;
                }
                final int slot = slots[0];
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    itemRequest.setAmount(clone.getMaxStackSize());
                } else {
                    if (itemStack.getAmount() >= clone.getMaxStackSize()) {
                        return;
                    }
                    if (StackUtils.itemsMatch(itemRequest, itemStack)) {
                        final int space = itemStack.getMaxStackSize() - itemStack.getAmount();
                        if (space > 0) {
                            itemRequest.setAmount(space);
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                itemRequest.setAmount(Math.min(itemRequest.getAmount(), limitQuantity));

                final ItemStack retrieved = root.getItemStack0(accessor, itemRequest);
                if (retrieved != null && retrieved.getType() != Material.AIR) {
                    retrieved.getAmount();
                    BlockMenuUtil.pushItem(blockMenu, retrieved, slot);
                }
            }
            case LAST_ONLY -> {
                if (slots.length == 0) {
                    break;
                }
                final int slot = slots[slots.length - 1];
                final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    itemRequest.setAmount(clone.getMaxStackSize());
                } else {
                    if (itemStack.getAmount() >= clone.getMaxStackSize()) {
                        return;
                    }
                    if (StackUtils.itemsMatch(itemRequest, itemStack)) {
                        final int space = itemStack.getMaxStackSize() - itemStack.getAmount();
                        if (space > 0) {
                            itemRequest.setAmount(space);
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                itemRequest.setAmount(Math.min(itemRequest.getAmount(), limitQuantity));

                final ItemStack retrieved = root.getItemStack0(accessor, itemRequest);
                if (retrieved != null && retrieved.getType() != Material.AIR) {
                    retrieved.getAmount();
                    BlockMenuUtil.pushItem(blockMenu, retrieved, slot);
                }
            }
            case FIRST_STOP -> {
                int freeSpace = 0;
                for (int slot : slots) {
                    final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        freeSpace += clone.getMaxStackSize();
                        break;
                    } else {
                        if (itemStack.getAmount() >= clone.getMaxStackSize()) {
                            continue;
                        }
                        if (StackUtils.itemsMatch(itemRequest, itemStack)) {
                            final int availableSpace = itemStack.getMaxStackSize() - itemStack.getAmount();
                            if (availableSpace > 0) {
                                freeSpace += availableSpace;
                            }
                        }
                        break;
                    }
                }
                if (freeSpace <= 0) {
                    return;
                }
                itemRequest.setAmount(Math.min(freeSpace, limitQuantity));

                final ItemStack retrieved = root.getItemStack0(accessor, itemRequest);
                if (retrieved != null && retrieved.getType() != Material.AIR) {
                    BlockMenuUtil.pushItem(blockMenu, retrieved, slots);
                }
            }
            case LAZY -> {
                if (slots.length > 0) {
                    final ItemStack delta = blockMenu.getItemInSlot(slots[0]);
                    if (delta == null || delta.getType() == Material.AIR) {
                        int freeSpace = 0;
                        for (int slot : slots) {
                            final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                            if (itemStack == null || itemStack.getType() == Material.AIR) {
                                freeSpace += clone.getMaxStackSize();
                            } else {
                                if (itemStack.getAmount() >= clone.getMaxStackSize()) {
                                    continue;
                                }
                                if (StackUtils.itemsMatch(itemRequest, itemStack)) {
                                    final int availableSpace = itemStack.getMaxStackSize() - itemStack.getAmount();
                                    if (availableSpace > 0) {
                                        freeSpace += availableSpace;
                                    }
                                }
                            }
                        }
                        if (freeSpace <= 0) {
                            return;
                        }
                        itemRequest.setAmount(Math.min(freeSpace, limitQuantity));

                        final ItemStack retrieved = root.getItemStack0(accessor, itemRequest);
                        if (retrieved != null && retrieved.getType() != Material.AIR) {
                            BlockMenuUtil.pushItem(blockMenu, retrieved, slots);
                        }
                    }
                }
            }
            case VOID -> {
                itemRequest.setAmount(limitQuantity);

                final ItemStack retrieved = root.getItemStack0(accessor, itemRequest);
                if (retrieved != null && retrieved.getType() != Material.AIR) {
                    BlockMenuUtil.pushItem(blockMenu, retrieved, slots);
                }
            }
            case SPECIFIED_QUANTITY -> {
                int existingCount = 0;
                for (int slot : slots) {
                    final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                    if (itemStack != null && itemStack.getType() != Material.AIR) {
                        if (StackUtils.itemsMatch(itemRequest, itemStack)) {
                            existingCount += itemStack.getAmount();
                        }
                    }
                }
                if (existingCount < limitQuantity) {
                    final int deficit = limitQuantity - existingCount;
                    int availableSpace = 0;
                    for (int slot : slots) {
                        final ItemStack itemStack = blockMenu.getItemInSlot(slot);
                        if (itemStack == null || itemStack.getType() == Material.AIR) {
                            availableSpace += clone.getMaxStackSize();
                        } else if (StackUtils.itemsMatch(itemRequest, itemStack)) {
                            availableSpace += Math.max(0, itemStack.getMaxStackSize() - itemStack.getAmount());
                        }
                    }
                    if (availableSpace <= 0) {
                        return;
                    }
                    final int toRequest = Math.min(deficit, availableSpace);
                    itemRequest.setAmount(toRequest);
                    final ItemStack retrieved = root.getItemStack0(accessor, itemRequest);
                    if (retrieved != null && retrieved.getType() != Material.AIR) {
                        BlockMenuUtil.pushItem(blockMenu, retrieved, slots);
                    }
                }
            }
        }
    }

    public static void outPower(@NotNull Location location, @NotNull NetworkRoot root, int rate) {
        final SlimefunBlockData blockData = StorageCacheUtils.getBlock(location);
        if (blockData == null) {
            return;
        }

        if (!blockData.isDataLoaded()) {
            StorageCacheUtils.requestLoad(blockData);
            return;
        }

        final SlimefunItem slimefunItem = SlimefunItem.getById(blockData.getSfId());
        if (!(slimefunItem instanceof EnergyNetComponent component) || slimefunItem instanceof NetworkObject) {
            return;
        }

        int existingCharge = component.getCharge(location);

        final int capacity = component.getCapacity();
        final int space = capacity - existingCharge;

        if (space <= 0) {
            return;
        }

        final int possibleGeneration = Math.min(rate, space);
        final long power = root.getRootPower();

        if (power <= 0) {
            return;
        }

        final int gen = power < possibleGeneration ? (int) power : possibleGeneration;

        component.addCharge(location, gen);
        root.removeRootPower(gen);
    }
}
