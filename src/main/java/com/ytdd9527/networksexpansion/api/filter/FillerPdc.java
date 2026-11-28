package com.ytdd9527.networksexpansion.api.filter;

import com.balugaq.netex.utils.Debug;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.utils.SerializeUtils;
import io.github.sefiraat.networks.Networks;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class FillerPdc {

    private static final String BUFFER_KEY = "container_filler_buffer";
    private static final String PAUSED_KEY = "container_filler_paused";
    private static final String BOX_MODE_KEY = "container_filler_box_mode";

    private static final Set<String> CORRUPT_WARNED = ConcurrentHashMap.newKeySet();

    private FillerPdc() {
    }

    @Nullable
    public static ItemStack loadBuffer(@NotNull Location location) {
        String data = StorageCacheUtils.getData(location, BUFFER_KEY);
        if (data == null || data.isEmpty()) {
            return null;
        }
        ItemStack buffer = SerializeUtils.string2Object(data);
        if (buffer == null) {
            warnCorrupted(location, "buffer");
            removeBuffer(location);
            return null;
        }
        return buffer;
    }

    public static void saveBuffer(@NotNull Location location, @NotNull ItemStack buffer) {
        StorageCacheUtils.setData(location, BUFFER_KEY, SerializeUtils.object2String(buffer));
    }

    public static void removeBuffer(@NotNull Location location) {
        StorageCacheUtils.removeData(location, BUFFER_KEY);
    }

    public static boolean isPausedPersisted(@NotNull Location location) {
        String data = StorageCacheUtils.getData(location, PAUSED_KEY);
        if (data == null || data.isEmpty()) {
            return false;
        }
        try {
            return Integer.parseInt(data) != 0;
        } catch (NumberFormatException e) {
            Debug.trace(e, "storage assembler paused flag parse");
            return false;
        }
    }

    public static void writePaused(@NotNull Location location, boolean paused) {
        StorageCacheUtils.setData(location, PAUSED_KEY, paused ? "1" : "0");
    }

    public static boolean isBoxModePersisted(@NotNull Location location) {
        String data = StorageCacheUtils.getData(location, BOX_MODE_KEY);
        return data != null && !data.isEmpty() && data.charAt(0) == '1';
    }

    public static void writeBoxMode(@NotNull Location location, boolean boxMode) {
        StorageCacheUtils.setData(location, BOX_MODE_KEY, boxMode ? "1" : "0");
    }

    private static void warnCorrupted(@NotNull Location location, @NotNull String category) {
        if (!CORRUPT_WARNED.add(coordsKey(location) + "|" + category)) {
            return;
        }
        Networks.getInstance().getLogger().warning(
            Lang.getString(CellnetText.STORAGE_ASSEMBLER_PREFIX + category + "_corrupted", coordsKey(location)));
    }

    @NotNull
    private static String coordsKey(@NotNull Location location) {
        return "world="
            + (location.getWorld() == null ? "null" : location.getWorld().getName())
            + " x=" + location.getBlockX()
            + " y=" + location.getBlockY()
            + " z=" + location.getBlockZ();
    }
}
