package com.ytdd9527.networksexpansion.api.cell;

import com.balugaq.netex.api.enums.TransportMode;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class LineState {
    public final Set<BlockFace> directions = ConcurrentHashMap.newKeySet();
    public final Map<BlockFace, long[]> targetSignatures = new ConcurrentHashMap<>();
    public final Map<BlockFace, Integer> cursors = new ConcurrentHashMap<>();
    public volatile long nextWorkMs;
    public volatile boolean multiDirection;
    public volatile int distance = 16;
    public volatile TransportMode transportMode = TransportMode.NONE;
    public volatile boolean vanilla;
    public volatile int rangeModules;
    public volatile int capacityModules;
    public volatile boolean modeModule;
    public volatile boolean bindingModule;
    public volatile boolean wired;
    public volatile Map<BlockFace, Map<Integer, List<ItemStack>>> bindings;
    public volatile Map<BlockFace, Map<Integer, Integer>> negCache;
    public volatile Map<BlockFace, TransportMode> dirModes;
    public volatile List<ItemStack> templateRefs;
    public volatile List<ItemRequest> templateRequests;
    public final Map<List<ItemStack>, List<ItemRequest>> boundRequestCache =
        Collections.synchronizedMap(new IdentityHashMap<>());

    public @NotNull Set<BlockFace> effectiveDirections() {
        if (multiDirection) {
            return Set.copyOf(directions);
        }
        for (BlockFace face : NetworkDirectional.VALID_FACES) {
            if (directions.contains(face)) {
                return Set.of(face);
            }
        }
        return Set.of();
    }
}
