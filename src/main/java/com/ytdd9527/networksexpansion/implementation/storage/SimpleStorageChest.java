package com.ytdd9527.networksexpansion.implementation.storage;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.utils.ReflectionUtil;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.sefiraat.simplestorage.SimpleStorage;
import io.github.sefiraat.simplestorage.configuration.ManagerConfiguration;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * SimpleStorage 箱子（铁/金/钻石/存储单元）的网络侧视图。
 * 按「细胞」语义接入：多格多物品，内容物不在 BlockMenu，
 * 而在 SimpleStorage 的 chest_contents.yml（以 BlockStorage 的 simple-chest-id 索引），
 * 读写走 ManagerConfiguration 的静态入口；SimpleChest 每 tick 会重读配置，外部写入安全。
 */
public final class SimpleStorageChest {

    private static final String CHEST_INFO_KEY = "simple-chest-id";
    private static final int SLOTS_PER_PAGE = 45;

    private final Location location;
    private final int chestId;

    private SimpleStorageChest(Location location, int chestId) {
        this.location = location;
        this.chestId = chestId;
    }

    @Nullable
    public static SimpleStorageChest of(Location location) {
        final String found = BlockStorage.getLocationInfo(location, CHEST_INFO_KEY);
        if (found == null) return null;
        try {
            return new SimpleStorageChest(location, Integer.parseInt(found));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Location getLocation() {
        return location;
    }

    private static FileConfiguration chestConfig() {
        return SimpleStorage.inst().getManagerConfiguration().getChestConfig();
    }

    @Nullable
    private ConfigurationSection section() {
        return chestConfig().getConfigurationSection(ManagerConfiguration.CHESTS + "." + chestId);
    }

    /** 箱内总格数（pages * 45）；pages 反射自 SimpleChest 私有字段，箱子方块已失效时返回 0。 */
    private int totalSlots() {
        final SlimefunItem item = StorageCacheUtils.getSfItem(location);
        if (item == null) return 0;
        final Object pages = ReflectionUtil.getValue(item, "pages");
        return pages instanceof Integer count && count > 0 ? count * SLOTS_PER_PAGE : 0;
    }

    /** 当前实际存有物品的槽位号（升序），仅供取料遍历，不需要总格数。 */
    private List<Integer> occupiedSlots(ConfigurationSection section) {
        final List<Integer> slots = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            if (!key.startsWith(ManagerConfiguration.SLOT)) continue;
            try {
                slots.add(Integer.parseInt(key.substring(ManagerConfiguration.SLOT.length())));
            } catch (NumberFormatException ignored) {
                // 异常键直接跳过
            }
        }
        slots.sort(Integer::compareTo);
        return slots;
    }

    public List<ItemStack> getSlotItems() {
        final ConfigurationSection section = section();
        if (section == null) return List.of();
        final List<ItemStack> items = new ArrayList<>();
        for (int slot : occupiedSlots(section)) {
            final ItemStack item = section.getItemStack(ManagerConfiguration.SLOT + slot);
            if (item != null && !item.getType().isAir()) {
                items.add(item);
            }
        }
        return items;
    }

    public long getAmount(ItemStack template) {
        long total = 0;
        for (ItemStack item : getSlotItems()) {
            if (StackUtils.itemsMatch(item, template)) {
                total += item.getAmount();
            }
        }
        return total;
    }

    /** 还能再吸收多少 template（同格并栈 + 空格），供容量估算。 */
    public long getRoom(ItemStack template) {
        final ConfigurationSection section = section();
        if (section == null) return 0;
        final int totalSlots = totalSlots();
        if (totalSlots <= 0) return 0;
        long room = 0;
        int empty = totalSlots;
        for (int slot = 1; slot <= totalSlots; slot++) {
            final ItemStack current = section.getItemStack(ManagerConfiguration.SLOT + slot);
            if (current == null || current.getType().isAir()) continue;
            empty--;
            if (StackUtils.itemsMatch(current, template)) {
                room += Math.max(0, current.getMaxStackSize() - current.getAmount());
            }
        }
        return room + (long) empty * template.getMaxStackSize();
    }

    /** 存入：先并栈后占空格；直接修改 incoming 的剩余量，返回实际存入数。 */
    public int depositItemStack(ItemStack incoming) {
        final ConfigurationSection section = section();
        if (section == null) return 0;
        final int totalSlots = totalSlots();
        if (totalSlots <= 0) return 0;

        int remaining = incoming.getAmount();
        for (int slot = 1; slot <= totalSlots && remaining > 0; slot++) {
            final ItemStack current = section.getItemStack(ManagerConfiguration.SLOT + slot);
            if (current == null || current.getType().isAir()) continue;
            if (!StackUtils.itemsMatch(current, incoming)) continue;
            final int room = current.getMaxStackSize() - current.getAmount();
            if (room <= 0) continue;
            final int move = Math.min(room, remaining);
            current.setAmount(current.getAmount() + move);
            ManagerConfiguration.setChestSlotItem(chestId, slot, current);
            remaining -= move;
        }
        for (int slot = 1; slot <= totalSlots && remaining > 0; slot++) {
            final ItemStack current = section.getItemStack(ManagerConfiguration.SLOT + slot);
            if (current != null && !current.getType().isAir()) continue;
            final ItemStack stack = incoming.clone();
            stack.setAmount(remaining);
            ManagerConfiguration.setChestSlotItem(chestId, slot, stack);
            remaining = 0;
        }

        final int stored = incoming.getAmount() - remaining;
        incoming.setAmount(remaining);
        return stored;
    }

    /** 取料：按请求从各匹配格扣减，返回实际取到的堆（可能少于请求量），不足时不截断调用方请求。 */
    @Nullable
    public ItemStack requestItem(ItemRequest request) {
        final ConfigurationSection section = section();
        if (section == null) return null;
        int wanted = request.getAmount();
        if (wanted <= 0) return null;

        ItemStack result = null;
        for (int slot : occupiedSlots(section)) {
            if (wanted <= 0) break;
            final ItemStack current = section.getItemStack(ManagerConfiguration.SLOT + slot);
            if (current == null || current.getType().isAir() || !StackUtils.itemsMatch(request, current)) {
                continue;
            }
            if (result == null) {
                result = current.clone();
                result.setAmount(0);
            }
            final int move = Math.min(wanted, current.getAmount());
            final int newAmount = current.getAmount() - move;
            if (newAmount > 0) {
                current.setAmount(newAmount);
                ManagerConfiguration.setChestSlotItem(chestId, slot, current);
            } else {
                ManagerConfiguration.setChestSlotItem(chestId, slot, null);
            }
            result.setAmount(result.getAmount() + move);
            wanted -= move;
        }
        return result != null && result.getAmount() > 0 ? result : null;
    }
}
