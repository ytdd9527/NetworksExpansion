package com.balugaq.netex.integrations.logitech;

import com.balugaq.netex.utils.Debug;
import com.balugaq.netex.utils.Scheduling;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.drive.DriveType;
import com.ytdd9527.networksexpansion.implementation.machines.unit.NetworksDrawer;
import com.ytdd9527.networksexpansion.utils.ItemKey;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * 量子纠缠绑定记录：绑定样本持久化在目标方块自己的方块数据（键 {@link #KEY}），
 * 目标位置另存全局索引（linker-bindings.yml）；启动时 {@link #replayAll} 按索引
 * 全量重放虚拟缓存注册。启动初期 Slimefun 方块数据未就绪的条目挂起重试
 * （每 100 tick 一轮，上限 {@link #MAX_REPLAY_ROUNDS} 轮），只有数据已加载
 * 且类型不符的条目才判死清除。
 */
public final class LinkBindingStore {

    private static final String KEY = "LinkBindings";
    private static final int MAX_ENTRIES = 64;
    private static final String INDEX_FILE = "linker-bindings.yml";
    private static final String INDEX_KEY = "targets";

    private LinkBindingStore() {
    }

    static void record(@NotNull Location target, @NotNull ItemStack sample) {
        List<String> encoded = readEntries(target);
        String entry = Base64.getEncoder().encodeToString(sample.serializeAsBytes());
        if (encoded.contains(entry)) {
            indexAdd(target);
            return;
        }
        if (encoded.size() >= MAX_ENTRIES) {
            encoded.remove(0);
        }
        encoded.add(entry);
        StorageCacheUtils.setData(target, KEY, String.join(",", encoded));
        indexAdd(target);
    }

    static int replay(@NotNull Location target, @NotNull Consumer<ItemStack> injector) {
        int injected = 0;
        for (String entry : readEntries(target)) {
            try {
                ItemStack sample = ItemStack.deserializeBytes(Base64.getDecoder().decode(entry));
                injector.accept(sample);
                injected++;
            } catch (Throwable e) {
                Debug.debug("纠缠绑定记录损坏，跳过: " + e.getMessage());
            }
        }
        return injected;
    }

    private static @NotNull List<String> readEntries(@NotNull Location target) {
        String data;
        try {
            data = StorageCacheUtils.getData(target, KEY);
        } catch (IllegalStateException e) {
            return new ArrayList<>();
        }
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> entries = new ArrayList<>(List.of(data.split(",", -1)));
        entries.removeIf(String::isEmpty);
        return entries;
    }

    public static void replayDrive(@NotNull Location driveLocation) {
        if (!LinkerGrid.initialized) {
            return;
        }
        if (replay(driveLocation, sample -> CellVirtualCache.inject(driveLocation, new ItemKey(sample))) > 0) {
            indexAdd(driveLocation);
        }
    }

    public static void replayDrawer(@NotNull Location drawerLocation) {
        if (!LinkerGrid.initialized) {
            return;
        }
        if (replay(drawerLocation, sample -> DrawerVirtualCache.inject(drawerLocation, sample)) > 0) {
            indexAdd(drawerLocation);
        }
    }

    public static void forget(@NotNull Location target) {
        List<String> entries = new ArrayList<>(readIndex());
        if (entries.remove(encode(target))) {
            writeIndex(entries);
        }
    }

    private static int replayRounds;
    private static final int MAX_REPLAY_ROUNDS = 600;

    public static void replayAll() {
        if (!LinkerGrid.initialized) {
            return;
        }
        List<String> entries = readIndex();
        if (entries.isEmpty()) {
            return;
        }
        List<String> alive = new ArrayList<>();
        List<String> deferred = new ArrayList<>();
        for (String entry : entries) {
            Location target = decode(entry);
            if (target == null) {
                deferred.add(entry);
                continue;
            }
            SlimefunItem item;
            try {
                item = StorageCacheUtils.getSfItem(target);
            } catch (IllegalStateException e) {
                deferred.add(entry);
                continue;
            }
            if (item == null) {
                deferred.add(entry);
                continue;
            }
            if (DriveType.of(item) != null) {
                replay(target, sample -> CellVirtualCache.inject(target, new ItemKey(sample)));
                alive.add(entry);
            } else if (item instanceof NetworksDrawer) {
                replay(target, sample -> DrawerVirtualCache.inject(target, sample));
                alive.add(entry);
            }
        }
        alive.addAll(deferred);
        if (alive.size() != entries.size()) {
            writeIndex(alive);
        }
        if (deferred.isEmpty() || ++replayRounds >= MAX_REPLAY_ROUNDS) {
            return;
        }
        Scheduling.runLater(LinkBindingStore::replayAll, 100L);
    }

    private static void indexAdd(@NotNull Location target) {
        List<String> entries = new ArrayList<>(readIndex());
        String entry = encode(target);
        if (entries.contains(entry)) {
            return;
        }
        entries.add(entry);
        writeIndex(entries);
    }

    private static @NotNull List<String> readIndex() {
        File file = indexFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(YamlConfiguration.loadConfiguration(file).getStringList(INDEX_KEY));
    }

    private static void writeIndex(@NotNull List<String> entries) {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set(INDEX_KEY, entries);
        try {
            yaml.save(indexFile());
        } catch (IOException e) {
            Networks.getInstance().getLogger().log(Level.WARNING, "保存量子纠缠绑定索引失败", e);
        }
    }

    private static @NotNull File indexFile() {
        return new File(Networks.getInstance().getDataFolder(), INDEX_FILE);
    }

    private static @NotNull String encode(@NotNull Location location) {
        World world = location.getWorld();
        return world.getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    private static Location decode(@NotNull String entry) {
        String[] parts = entry.split(";");
        if (parts.length != 4) {
            return null;
        }
        World world = Bukkit.getWorld(parts[0]);
        if (world == null) {
            return null;
        }
        try {
            return new Location(world, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
