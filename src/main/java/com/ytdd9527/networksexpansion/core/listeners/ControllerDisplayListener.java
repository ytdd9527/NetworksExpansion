package com.ytdd9527.networksexpansion.core.listeners;

import com.balugaq.netex.utils.Scheduling;

import com.balugaq.netex.api.events.NetworkRootReadyEvent;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerDisplayListener implements Listener {

    private enum DisplayState {
        OFFLINE,
        ONLINE,
        FULL
    }

    private final Map<Location, DisplayState> states = new ConcurrentHashMap<>();
    private final Map<Location, Long> lastSeen = new ConcurrentHashMap<>();
    private final long timeoutMillis;

    public ControllerDisplayListener() {
        this.timeoutMillis = (Slimefun.getTickerTask().getTickRate() * 4L + 20L) * 50L;
        Scheduling.runTimer(this::heartbeat, 20L, 20L);
    }

    @EventHandler
    public void onRootReady(@NotNull NetworkRootReadyEvent event) {
        if (!Networks.getInstance().isEnabled()) {
            return;
        }
        NetworkRoot root = event.getRoot();
        DisplayState state = resolve(root);
        Location location = root.getNodePosition();
        lastSeen.put(location, System.currentTimeMillis());
        states.put(location, state);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (!states.containsKey(location)) {
            return;
        }
        states.remove(location);
        lastSeen.remove(location);
    }

    private static DisplayState resolve(@NotNull NetworkRoot root) {
        if (root.isOverburdened() || root.getNodeCount() >= root.getMaxNodes()) {
            return DisplayState.FULL;
        }
        if (root.getNodeCount() <= 1) {
            return DisplayState.OFFLINE;
        }
        return DisplayState.ONLINE;
    }

    private void heartbeat() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Location, Long>> iterator = lastSeen.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Location, Long> entry = iterator.next();
            if (now - entry.getValue() > timeoutMillis) {
                iterator.remove();
                states.remove(entry.getKey());
            }
        }
    }
}
