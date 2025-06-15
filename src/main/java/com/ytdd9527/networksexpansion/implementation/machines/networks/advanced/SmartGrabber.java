package com.ytdd9527.networksexpansion.implementation.machines.networks.advanced;

import com.balugaq.netex.api.enums.FeedbackType;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.slimefun.network.AdminDebuggable;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * not a {@link NetworkObject} and has no {@link BlockMenu}
 *
 * @author balugaq
 */
public class SmartGrabber extends SpecialSlimefunItem implements AdminDebuggable {
    private static final Map<Location, BlockFace> DIRECTIONS = new HashMap<>();
    private static final Set<BlockFace> VALID_FACES =
            EnumSet.of(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    public SmartGrabber(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            @NotNull ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Nullable public static BlockFace getDirection(Location location) {
        return DIRECTIONS.get(location);
    }

    public static void setDirection(Location location, BlockFace face) {
        DIRECTIONS.put(location, face);
    }

    public static void removeDirection(Location location) {
        DIRECTIONS.remove(location);
    }

    @Override
    public void preRegister() {
        addItemHandler(
                new BlockTicker() {
                    @Override
                    public boolean isSynchronized() {
                        return false;
                    }

                    @Override
                    public void tick(
                            @NotNull Block block, SlimefunItem slimefunItem, SlimefunBlockData slimefunBlockData) {
                        final Location location = block.getLocation();
                        final BlockFace cachedFace = getDirection(location);
                        if (cachedFace != null && VALID_FACES.contains(cachedFace)) {
                            onTick(block, cachedFace);
                        } else if (block.getBlockData() instanceof Directional directional) {
                            final BlockFace bridgeFace = directional.getFacing();
                            setDirection(location, bridgeFace);
                            sendFeedback(block.getLocation(), FeedbackType.INITIALIZATION);
                        } else {
                            Slimefun.getDatabaseManager()
                                    .getBlockDataController()
                                    .removeBlock(location);
                            sendFeedback(block.getLocation(), FeedbackType.INVALID_BLOCK);
                        }
                    }
                },
                new BlockBreakHandler(false, false) {
                    @Override
                    public void onPlayerBreak(
                            @NotNull BlockBreakEvent blockBreakEvent,
                            @NotNull ItemStack itemStack,
                            @NotNull List<ItemStack> list) {
                        removeDirection(blockBreakEvent.getBlock().getLocation());
                    }
                },
                new BlockPlaceHandler(false) {
                    @Override
                    public void onPlayerPlace(@NotNull BlockPlaceEvent blockPlaceEvent) {
                        if (blockPlaceEvent.getBlock().getBlockData() instanceof Directional directional) {
                            final BlockFace face = directional.getFacing();
                            setDirection(blockPlaceEvent.getBlock().getLocation(), face);
                        }
                    }
                });
    }

    public void onTick(@NotNull Block thisBlock, @NotNull BlockFace bridgeFace) {
        final BlockFace containerFace = bridgeFace.getOppositeFace();
        final Block bridge = thisBlock.getRelative(bridgeFace);
        final Block container = thisBlock.getRelative(containerFace);
        final NodeDefinition definition = NetworkStorage.getNode(bridge.getLocation());
        if (definition != null && definition.getNode() != null) {
            final BlockMenu targetMenu = StorageCacheUtils.getMenu(container.getLocation());
            if (targetMenu != null) {
                final NetworkRoot root = definition.getNode().getRoot();
                final int[] slots = targetMenu
                        .getPreset()
                        .getSlotsAccessedByItemTransport(targetMenu, ItemTransportFlow.WITHDRAW, null);
                int limit = getLimitQuantity();
                if (slots.length > 0) {
                    final ItemStack delta = targetMenu.getItemInSlot(slots[0]);
                    if (delta != null && delta.getType() != Material.AIR) {
                        for (int slot : slots) {
                            ItemStack item = targetMenu.getItemInSlot(slot);
                            if (item != null && item.getType() != Material.AIR) {
                                final int exceptedReceive = Math.min(item.getAmount(), limit);
                                final ItemStack clone = StackUtils.getAsQuantity(item, exceptedReceive);
                                root.addItemStack0(thisBlock.getLocation(), clone);
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
            sendFeedback(thisBlock.getLocation(), FeedbackType.WORKING);
        } else {
            sendFeedback(thisBlock.getLocation(), FeedbackType.NO_NETWORK_FOUND);
        }
    }

    public int getLimitQuantity() {
        return 3456;
    }
}
