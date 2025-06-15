package com.balugaq.netex.api.interfaces;

import com.balugaq.netex.utils.Debug;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.Keys;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HangingBlock {
    Map<BlockFace, Function<Location, Location>> locationFixer = new HashMap<>() {
        {
            put(BlockFace.NORTH, loc -> loc.clone().setDirection(BlockFace.NORTH.getDirection()));
            put(BlockFace.SOUTH, loc -> loc.clone().setDirection(BlockFace.SOUTH.getDirection()));
            put(BlockFace.EAST, loc -> loc.clone().setDirection(BlockFace.EAST.getDirection()));
            put(BlockFace.WEST, loc -> loc.clone().setDirection(BlockFace.WEST.getDirection()));
            put(BlockFace.UP, loc -> loc.clone().setDirection(BlockFace.UP.getDirection()));
            put(BlockFace.DOWN, loc -> loc.clone().setDirection(BlockFace.DOWN.getDirection()));
        }
    };

    default Location getFixedLocation(@NotNull Location location, @NotNull BlockFace attachSide) {
        return locationFixer.get(attachSide).apply(location);
    }

    Map<String, HangingBlock> REGISTRY = new HashMap<>();
    Map<Location, Map<BlockFace, HangingBlock>> hangingBlocks = new HashMap<>();
    String BS_NORTH = "netex-hanging-north";
    String BS_SOUTH = "netex-hanging-south";
    String BS_EAST = "netex-hanging-east";
    String BS_WEST = "netex-hanging-west";
    String BS_UP = "netex-hanging-up";
    String BS_DOWN = "netex-hanging-down";
    NamespacedKey NETEX_HANGING_KEY = Keys.newKey("netex_hanging");
    double CENTER_OFFSET = 0.5D;
    double ITEM_FRAME_OFFSET = 0.675D;

    static <T extends HangingBlock> void registerHangingBlock(@NotNull T block) {
        REGISTRY.put(block.getId(), block);
    }

    @Contract("null -> null")
    static HangingBlock getHangingBlock(@Nullable String id) {
        if (id == null) {
            return null;
        }
        return REGISTRY.get(id);
    }

    static void tagItemFrame(@NotNull ItemFrame itemFrame, @NotNull String id) {
        itemFrame.getPersistentDataContainer().set(NETEX_HANGING_KEY, PersistentDataType.STRING, id);
    }

    static @Nullable HangingBlock getByItemFrame(@NotNull ItemFrame itemFrame) {
        return getHangingBlock(
                itemFrame.getPersistentDataContainer().get(NETEX_HANGING_KEY, PersistentDataType.STRING));
    }

    static void tickHangingBlocks(@NotNull Block attachon) {
        Location loc = attachon.getLocation();
        Map<BlockFace, HangingBlock> hs = getHangingBlocks(loc);
        getHangingBlocks(loc, hs.keySet()).forEach((attachSide, itemFrame) -> {
            try {
                hs.get(attachSide).onTick(loc, attachSide, itemFrame);
            } catch (Exception e) {
                Debug.trace(e);
            }
        });
    }

    static void loadHangingBlocks(@NotNull SlimefunBlockData attachon) {
        Map<BlockFace, ItemFrame> map = getHangingBlocks(attachon.getLocation(), NetworkDirectional.VALID_FACES);
        for (Map.Entry<BlockFace, ItemFrame> entry : map.entrySet()) {
            HangingBlock hangingBlock = getByItemFrame(entry.getValue());
            if (hangingBlock != null) {
                placeHangingBlock(attachon, entry.getValue(), entry.getKey(), hangingBlock);
            }
        }
    }

    static @NotNull Map<BlockFace, ItemFrame> getHangingBlocks(
            @NotNull Location attachon, @NotNull Set<BlockFace> attachSides) {
        Map<BlockFace, ItemFrame> hangingBlocks = new HashMap<>();
        for (BlockFace attachSide : attachSides) {
            ItemFrame v = getItemFrame(attachon, attachSide);
            if (v != null) {
                hangingBlocks.put(attachSide, v);
            }
        }

        return hangingBlocks;
    }

    static @Nullable String getHangingBlockId(@NotNull SlimefunBlockData attachon, @NotNull BlockFace attachSide) {
        return attachon.getData(getHangingBlockKey(attachSide));
    }

    static @Nullable String getHangingBlockKey(@NotNull BlockFace attachSide) {
        return switch (attachSide) {
            case NORTH -> BS_NORTH;
            case SOUTH -> BS_SOUTH;
            case EAST -> BS_EAST;
            case WEST -> BS_WEST;
            case UP -> BS_UP;
            case DOWN -> BS_DOWN;
            default -> null;
        };
    }

    static <T extends HangingBlock> void placeHangingBlock(
            @NotNull SlimefunBlockData attachon,
            @NotNull ItemFrame entityBlock,
            @NotNull BlockFace attachSide,
            @NotNull T hangingBlock) {
        switch (attachSide) {
            case NORTH -> attachon.setData(BS_NORTH, hangingBlock.getId());
            case SOUTH -> attachon.setData(BS_SOUTH, hangingBlock.getId());
            case EAST -> attachon.setData(BS_EAST, hangingBlock.getId());
            case WEST -> attachon.setData(BS_WEST, hangingBlock.getId());
            case UP -> attachon.setData(BS_UP, hangingBlock.getId());
            case DOWN -> attachon.setData(BS_DOWN, hangingBlock.getId());
        }

        Map<BlockFace, HangingBlock> e = hangingBlocks.get(attachon.getLocation());
        if (e == null) {
            e = new HashMap<>();
        }
        e.put(attachSide, hangingBlock);
        hangingBlocks.put(attachon.getLocation(), e);
        tagItemFrame(entityBlock, hangingBlock.getId());
    }

    @NotNull static Map<BlockFace, HangingBlock> getHangingBlocks(Location attachon) {
        return Optional.ofNullable(hangingBlocks.get(attachon)).orElse(new HashMap<>());
    }

    @Nullable static ItemFrame getItemFrame(@NotNull Location attachon, BlockFace attachSide) {
        Location center = attachon.toBlockLocation().add(CENTER_OFFSET, CENTER_OFFSET, CENTER_OFFSET);
        Collection<Entity> es =
                center.getWorld().getNearbyEntities(center, ITEM_FRAME_OFFSET, ITEM_FRAME_OFFSET, ITEM_FRAME_OFFSET);
        for (Entity e : es) {
            if (e instanceof ItemFrame itemFrame) {
                if (itemFrame.getAttachedFace() == attachSide) {
                    return itemFrame;
                }
            }
        }

        return null;
    }

    @OverridingMethodsMustInvokeSuper
    default void onPlace(
            @NotNull HangingPlaceEvent event,
            @NotNull Location attachon,
            @NotNull ItemFrame entityBlock,
            @NotNull BlockFace attachSide) {
        SlimefunBlockData data = StorageCacheUtils.getBlock(attachon);
        if (data == null) {
            event.setCancelled(true);
            return;
        }

        SlimefunItem sf = SlimefunItem.getById(data.getSfId());
        if (!(sf instanceof NetworkObject)) {
            event.setCancelled(true);
            return;
        }

        placeHangingBlock(data, entityBlock, attachSide, this);
    }

    default void breakBlock(Location attachon, @NotNull ItemFrame entityBlock) {
        HangingBlock hangingBlock = getByItemFrame(entityBlock);
        if (hangingBlock != null) {
            hangingBlock.onBreak(attachon, entityBlock, hangingBlock);
        }
    }

    static void doFirstTick(SlimefunBlockData attachon) {
        Location loc = attachon.getLocation();
        Map<BlockFace, HangingBlock> hs = getHangingBlocks(loc);
        getHangingBlocks(loc, hs.keySet()).forEach((attachSide, itemFrame) -> {
            try {
                hs.get(attachSide).onFirstTick(loc, attachon, attachSide, itemFrame);
            } catch (Exception e) {
                Debug.trace(e);
            }
        });
    }

    @OverridingMethodsMustInvokeSuper
    @ParametersAreNonnullByDefault
    default <T extends HangingBlock> void onBreak(Location attachon, ItemFrame entityBlock, T hangingBlock) {
        Map<BlockFace, HangingBlock> e = hangingBlocks.get(attachon);
        if (e != null) {
            e.remove(entityBlock.getAttachedFace());
            if (e.isEmpty()) {
                hangingBlocks.remove(attachon);
            }
        }
        ItemStack item = entityBlock.getItem();
        Location location = entityBlock.getLocation();
        entityBlock.remove();
        SlimefunBlockData data = StorageCacheUtils.getBlock(attachon);
        if (data != null) {
            data.removeData(getHangingBlockKey(entityBlock.getAttachedFace()));
        }
    }

    @ParametersAreNonnullByDefault
    void onInteract(Location attachon, PlayerItemFrameChangeEvent event);

    @ParametersAreNonnullByDefault
    void onTick(Location attachon, BlockFace attachSide, ItemFrame entityBlock);

    @ParametersAreNonnullByDefault
    default void onFirstTick(
            Location attachon, SlimefunBlockData attachonData, BlockFace attachSide, ItemFrame entityBlock) {}

    @NotNull String getId();
}
