package com.ytdd9527.networksexpansion.api.drive;

import com.balugaq.netex.utils.Scheduling;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
public final class DriveOwnership {

    public static final String OWNER_KEY = "owner";

    private static final int OWNER_PERSIST_MAX_ATTEMPTS = 40;

    private static final Map<Location, UUID> OWNER_CACHE = new ConcurrentHashMap<>();

    private DriveOwnership() {
    }

    public static void writeOwner(@NotNull Location location, @NotNull UUID ownerUuid) {
        OWNER_CACHE.put(location.clone(), ownerUuid);
        persistOwner(location, ownerUuid, 0);
    }

    private static void persistOwner(@NotNull Location location, @NotNull UUID ownerUuid, int attempt) {
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(location);
        if (blockData != null) {
            blockData.setData(OWNER_KEY, ownerUuid.toString());
            return;
        }
        if (attempt < OWNER_PERSIST_MAX_ATTEMPTS) {
            Scheduling.runLater(() -> persistOwner(location, ownerUuid, attempt + 1), 2L);
        }
    }

    @Nullable
    public static UUID getOwnerUuid(@NotNull Location location) {
        UUID cached = OWNER_CACHE.get(location);
        if (cached != null) {
            return cached;
        }
        String owner = StorageCacheUtils.getData(location, OWNER_KEY);
        UUID uuid = owner == null ? null : parseUuid(owner);
        if (uuid != null) {
            OWNER_CACHE.put(location.clone(), uuid);
        }
        return uuid;
    }

    public static boolean bypasses(@NotNull Player player) {
        return player.isOp() || player.hasPermission("slimefun.inventory.bypass");
    }

    public static boolean passesGates(
            @NotNull Player player,
            @NotNull Location location,
            @NotNull Interaction interaction,
            boolean researchUnlocked) {
        if (!Slimefun.getProtectionManager().hasPermission(player, location, interaction)) {
            return false;
        }
        if (!researchUnlocked) {
            return false;
        }
        return isOwnerOrWhitelisted(player, location);
    }

    public static boolean isOwnerOrWhitelisted(@NotNull Player player, @NotNull Location location) {
        if (bypasses(player)) {
            return true;
        }
        UUID ownerUuid = getOwnerUuid(location);
        if (ownerUuid == null) {
            return false;
        }
        return ownerUuid.equals(player.getUniqueId())
            || WhitelistStore.isWhitelisted(ownerUuid, player.getUniqueId());
    }

    public static void clearOwnerCache(@NotNull Location location) {
        OWNER_CACHE.remove(location);
    }

    @Nullable
    private static UUID parseUuid(@NotNull String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
