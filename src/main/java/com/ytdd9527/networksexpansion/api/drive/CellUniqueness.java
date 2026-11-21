package com.ytdd9527.networksexpansion.api.drive;

import com.balugaq.netex.utils.Scheduling;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.NetworksVersionedParticle;
import com.ytdd9527.networksexpansion.api.cell.CellPersistence;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.implementation.machines.drive.CellDrive;
import io.github.sefiraat.networks.Networks;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class CellUniqueness {

    private static final Map<String, Set<Location>> UUID_TO_DRIVES = new ConcurrentHashMap<>();

    private CellUniqueness() {
    }

    @Nullable
    public static String getUuidString(@Nullable ItemStack itemStack) {
        return CellPersistence.getCellUUIDString(itemStack);
    }

    @NotNull
    public static Set<String> collectUuidStrings(@NotNull BlockMenu menu) {
        Set<String> uuids = new HashSet<>();
        for (int slot : CellDrive.CELL_SLOTS) {
            ItemStack item = menu.getItemInSlot(slot);
            if (item == null || item.getType().isAir()) {
                continue;
            }
            String uuid = getUuidString(item);
            if (uuid != null) {
                uuids.add(uuid);
            }
        }
        return uuids;
    }

    public static boolean isDuplicateUuid(@NotNull BlockMenu menu, @NotNull ItemStack candidate) {
        String uuid = getUuidString(candidate);
        if (uuid == null) {
            return false;
        }
        return collectUuidStrings(menu).contains(uuid);
    }

    public static synchronized void registerCell(@NotNull Location driveLocation, @NotNull ItemStack cell) {
        String uuid = getUuidString(cell);
        if (uuid == null) {
            return;
        }
        UUID_TO_DRIVES.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet()).add(driveLocation);
    }

    public static synchronized void registerDrive(@NotNull BlockMenu menu) {
        Location location = menu.getLocation();
        unregisterDrive(location);
        for (int slot : CellDrive.CELL_SLOTS) {
            ItemStack item = menu.getItemInSlot(slot);
            String uuid = getUuidString(item);
            if (uuid != null) {
                UUID_TO_DRIVES.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet()).add(location);
            }
        }
    }

    public static synchronized void unregisterDrive(@NotNull Location driveLocation) {
        if (UUID_TO_DRIVES.isEmpty()) {
            return;
        }
        List<String> empty = new ArrayList<>();
        for (Map.Entry<String, Set<Location>> entry : UUID_TO_DRIVES.entrySet()) {
            Set<Location> drives = entry.getValue();
            drives.remove(driveLocation);
            if (drives.isEmpty()) {
                empty.add(entry.getKey());
            }
        }
        for (String uuid : empty) {
            UUID_TO_DRIVES.remove(uuid);
        }
    }

    private static boolean isDuplicateAcrossDrives(@NotNull Location currentDrive, @NotNull ItemStack candidate) {
        String uuid = getUuidString(candidate);
        return uuid != null && isDuplicateAcrossDrives(currentDrive, uuid);
    }

    private static boolean isDuplicateAcrossDrives(@NotNull Location currentDrive, @NotNull String uuid) {
        Set<Location> drives = UUID_TO_DRIVES.get(uuid);
        if (drives == null || drives.isEmpty()) {
            return false;
        }
        for (Location drive : drives) {
            if (!drive.equals(currentDrive)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDuplicate(@NotNull BlockMenu menu, @NotNull ItemStack candidate) {
        return isDuplicateUuid(menu, candidate) || isDuplicateAcrossDrives(menu.getLocation(), candidate);
    }

    public static void scanAndEjectDuplicates(@NotNull BlockMenu menu, @NotNull Player player) {
        registerDrive(menu);
        Location location = menu.getLocation();
        Set<String> seen = new HashSet<>();
        for (int slot : CellDrive.CELL_SLOTS) {
            ItemStack item = menu.getItemInSlot(slot);
            String uuid = getUuidString(item);
            if (uuid == null) {
                continue;
            }
            if (!seen.add(uuid) || isDuplicateAcrossDrives(location, uuid)) {
                ejectDuplicate(menu, player, slot);
            }
        }
    }

    public static void ejectDuplicate(@NotNull BlockMenu menu, @NotNull Player player, int slot) {
        ItemStack item = menu.getItemInSlot(slot);
        if (item == null || item.getType().isAir()) {
            return;
        }
        menu.replaceExistingItem(slot, null);
        String uuid = getUuidString(item);
        if (uuid != null) {
            Set<Location> drives = UUID_TO_DRIVES.get(uuid);
            if (drives != null && !collectUuidStrings(menu).contains(uuid)) {
                drives.remove(menu.getLocation());
                if (drives.isEmpty()) {
                    UUID_TO_DRIVES.remove(uuid);
                }
            }
        }
        dropItem(menu.getLocation().clone().add(0, 1, 0), item);
    }

    public static void notifyDuplicateRejected(@NotNull Player player) {
        String message = Lang.getString(CellnetText.DRIVE_CELL_UUID_DUPLICATE);
        player.sendTitle("", message, 5, 55, 20);
    }

    private static void dropItem(@NotNull Location location, @NotNull ItemStack item) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        Location center = location.clone().add(0.5, 0.15, 0.5);
        Item entity = world.dropItem(center, item);
        if (entity == null) {
            return;
        }
        entity.setVelocity(new Vector(0, 0, 0));
        flashEntity(entity);
    }

    private static void flashEntity(@NotNull Item entity) {
        WrappedTask[] taskRef = new WrappedTask[1];
        int[] ticks = {0};
        taskRef[0] = Scheduling.runTimer(() -> {
            if (!entity.isValid()) {
                taskRef[0].cancel();
                return;
            }
            entity.setGlowing(ticks[0] % 2 == 0);
            World world = entity.getWorld();
            if (world != null) {
                Location loc = entity.getLocation();
                world.spawnParticle(NetworksVersionedParticle.DUST, loc, 8, 0.3, 0.3, 0.3, new Particle.DustOptions(Color.fromRGB(0xFF5A5A), 1.2f));
            }
            if (++ticks[0] >= 4) {
                entity.setGlowing(false);
                taskRef[0].cancel();
            }
        }, 0L, 4L);
    }
}