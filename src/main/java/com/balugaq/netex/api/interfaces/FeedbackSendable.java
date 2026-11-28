package com.balugaq.netex.api.interfaces;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.LocationUtil;
import com.balugaq.netex.utils.Scheduling;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public interface FeedbackSendable {
    Map<UUID, Set<Location>> SUBSCRIBED_LOCATIONS = new ConcurrentHashMap<>();

    static void subscribe(Player player, Location location) {
        UUID key = player.getUniqueId();
        if (!SUBSCRIBED_LOCATIONS.containsKey(key)) {
            SUBSCRIBED_LOCATIONS.put(key, ConcurrentHashMap.newKeySet());
        }
        SUBSCRIBED_LOCATIONS.get(key).add(location);
    }

    static void unsubscribe(Player player, Location location) {
        UUID key = player.getUniqueId();
        if (SUBSCRIBED_LOCATIONS.containsKey(key)) {
            SUBSCRIBED_LOCATIONS.get(key).remove(location);
        }
    }

    static boolean hasSubscribed(Player player, Location location) {
        UUID key = player.getUniqueId();
        if (SUBSCRIBED_LOCATIONS.containsKey(key)) {
            return SUBSCRIBED_LOCATIONS.get(key).contains(location);
        }
        return false;
    }

    static void sendFeedback0(@Nullable Location location, FeedbackType type) {
        if (location == null) return;
        for (UUID uuid : SUBSCRIBED_LOCATIONS.keySet()) {
            if (SUBSCRIBED_LOCATIONS.get(uuid).contains(location)) {
                dispatch(uuid, location, type.getMessage());
            }
        }
    }

    // Async machine ticks call this; messaging a player is main-thread-only work
    static void dispatch(UUID uuid, Location location, String message) {
        Runnable task = () -> {
            Player player = Bukkit.getServer().getPlayer(uuid);
            if (player != null) {
                player.sendMessage(String.format(
                    Lang.getString("messages.debug.status_view"), LocationUtil.humanizeBlock(location), message));
            }
        };
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Scheduling.runTask(task);
        }
    }

    default void sendFeedback(Location location, FeedbackType type) {
        for (UUID uuid : SUBSCRIBED_LOCATIONS.keySet()) {
            if (SUBSCRIBED_LOCATIONS.get(uuid).contains(location)) {
                dispatch(uuid, location, type.getMessage());
            }
        }
    }
}
