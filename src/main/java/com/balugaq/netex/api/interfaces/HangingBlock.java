package com.balugaq.netex.api.interfaces;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.Keys;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HangingBlock {
    Map<String, HangingBlock> REGISTRY = new HashMap<>();
    Map<Location, Map<BlockFace, HangingBlock>> hangingBlocks = new HashMap<>();
    String BS_NORTH = "netex-hanging-north";
    String BS_SOUTH = "netex-hanging-south";
    String BS_EAST = "netex-hanging-east";
    String BS_WEST = "netex-hanging-west";
    String BS_UP = "netex-hanging-up";
    String BS_DOWN = "netex-hanging-down";

    static void registerHangingBlock(HangingBlock block) {
        REGISTRY.put(block.getId(), block);
    }

    @Contract("null -> null")
    static HangingBlock getHangingBlock(String id) {
        if (id == null) {
            return null;
        }
        return REGISTRY.get(id);
    }

    String HANGING_PLACEHOLDER_BLOCK_ID = "NTW_EXPANSION_HANGING_PLACEHOLDER_BLOCK";
    NamespacedKey NETEX_HANGING_KEY = Keys.newKey("netex_hanging");

    static void tagItemFrame(ItemFrame itemFrame, String id) {
        itemFrame.getPersistentDataContainer().set(NETEX_HANGING_KEY, PersistentDataType.STRING, id);
    }

    static HangingBlock getByItemFrame(ItemFrame itemFrame) {
        return getHangingBlock(
                itemFrame.getPersistentDataContainer().get(NETEX_HANGING_KEY, PersistentDataType.STRING));
    }

    static void loadHangingBlocks(SlimefunBlockData placeholder) {
        var map = getHangingBlocks(placeholder.getLocation(), NetworkDirectional.VALID_FACES);
        for (var entry : map.entrySet()) {
            placeHangingBlock(placeholder, entry.getValue(), entry.getKey(), getByItemFrame(entry.getValue()));
        }
    }

    static Map<BlockFace, ItemFrame> getHangingBlocks(Location placeholder, Set<BlockFace> attachSides) {
        Map<BlockFace, ItemFrame> hangingBlocks = new HashMap<>();
        for (BlockFace attachSide : attachSides) {
            var v = getItemFrame(placeholder, attachSide);
            if (v != null) {
                hangingBlocks.put(attachSide, v);
            }
        }

        return hangingBlocks;
    }

    static String getHangingBlockId(SlimefunBlockData placeholder, BlockFace attachSide) {
        return placeholder.getData(getHangingBlockKey(attachSide));
    }

    static String getHangingBlockKey(BlockFace attachSide) {
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

    static void placeHangingBlock(
            SlimefunBlockData placeholder, ItemFrame entityBlock, BlockFace attachSide, HangingBlock hangingBlock) {
        switch (attachSide) {
            case NORTH -> placeholder.setData(BS_NORTH, hangingBlock.getId());
            case SOUTH -> placeholder.setData(BS_SOUTH, hangingBlock.getId());
            case EAST -> placeholder.setData(BS_EAST, hangingBlock.getId());
            case WEST -> placeholder.setData(BS_WEST, hangingBlock.getId());
            case UP -> placeholder.setData(BS_UP, hangingBlock.getId());
            case DOWN -> placeholder.setData(BS_DOWN, hangingBlock.getId());
        }

        var e = hangingBlocks.get(placeholder.getLocation());
        if (e == null) {
            e = new HashMap<>();
        }
        e.put(attachSide, hangingBlock);
        hangingBlocks.put(placeholder.getLocation(), e);
        tagItemFrame(entityBlock, hangingBlock.getId());
    }

    static void breakHangingBlock(Location placeholder, ItemFrame entityBlock) {
        var e = hangingBlocks.get(placeholder);
        if (e != null) {
            e.remove(entityBlock.getAttachedFace());
            if (e.isEmpty()) {
                hangingBlocks.remove(placeholder);
            }
        }
        ItemStack item = entityBlock.getItem();
        Location location = entityBlock.getLocation();
        entityBlock.remove();
        Slimefun.getDatabaseManager().getBlockDataController().removeBlock(placeholder);
        location.getWorld().dropItemNaturally(location, item);
        location.getWorld().dropItemNaturally(location, ExpansionItems.SWITCHING_MONITOR.getItem());
    }

    @NotNull static Map<BlockFace, HangingBlock> getHangingBlocks(Location placeholder) {
        return Optional.ofNullable(hangingBlocks.get(placeholder)).orElse(new HashMap<>());
    }

    @OverridingMethodsMustInvokeSuper
    default void onPlace(HangingPlaceEvent event, Location placeholder, ItemFrame entityBlock, BlockFace attachSide) {
        var data = StorageCacheUtils.getBlock(placeholder);
        if (data == null) {
            event.setCancelled(true);
            return;
        }

        var sf = SlimefunItem.getById(data.getSfId());
        if (!(sf instanceof NetworkObject)) {
            event.setCancelled(true);
            return;
        }

        placeHangingBlock(data, entityBlock, attachSide, this);
    }

    @OverridingMethodsMustInvokeSuper
    default void onBreak(Location placeholder, ItemFrame entityBlock) {
        breakHangingBlock(placeholder, entityBlock);
    }

    void onInteract(Location placeholder, PlayerItemFrameChangeEvent event);

    void onTick(Location placeholder, ItemFrame entityBlock);

    @Nullable static ItemFrame getItemFrame(Location placeholder, BlockFace attachSide) {
        var center = placeholder.toBlockLocation().add(0.5, 0.5, 0.5);
        var es = center.getWorld().getNearbyEntities(center, 0.5, 0.5, 0.5);
        for (var e : es) {
            if (e instanceof ItemFrame itemFrame) {
                if (itemFrame.getAttachedFace() == attachSide) {
                    return itemFrame;
                }
            }
        }

        return null;
    }

    String getId();
}
