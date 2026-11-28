package com.balugaq.netex.utils;

import io.github.sefiraat.networks.utils.StackUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@SuppressWarnings("DuplicatedCode")
@UtilityClass
public class InventoryUtil {
    public static void addItem(Player player, @Nullable ItemStack... toAdd) {
        addItem(player.getInventory(), toAdd);
        player.updateInventory();
    }

    public static void addItem(InventoryHolder holder, @Nullable ItemStack... toAdds) {
        addItem(holder.getInventory(), toAdds);
    }

    public static void addItem(Inventory inventory, @Nullable ItemStack ... toAdds) {
        @Nullable ItemStack[] storage = inventory.getStorageContents();
        if (storage == null) return;

        for (ItemStack toAdd : toAdds) {
            if (toAdd == null || toAdd.getType() == Material.AIR || toAdd.getAmount() <= 0) {
                continue;
            }

            int remaining = toAdd.getAmount();

            // 1. 先尝试放入已有的相同物品堆
            while (remaining > 0) {
                int index = firstSimilar(storage, toAdd);
                if (index == -1) {
                    break;
                }

                ItemStack exist = storage[index];
                // assert exist != null;
                int canAdd = Math.min(exist.getMaxStackSize() - exist.getAmount(), remaining);
                if (canAdd <= 0) {
                    // 理论上 firstSimilar 不会返回已满的，但以防万一
                    break;
                }

                exist.setAmount(exist.getAmount() + canAdd);
                remaining -= canAdd;
            }

            // 2. 剩余部分放入空槽位
            while (remaining > 0) {
                int index = firstEmpty(storage);
                if (index == -1) {
                    // 背包满了
                    break;
                }

                int putAmount = Math.min(remaining, toAdd.getMaxStackSize());
                storage[index] = toAdd.asQuantity(putAmount);
                remaining -= putAmount;
            }

            // 如果物品用完，更新原对象的数量为 0，保持与原逻辑一致
            toAdd.setAmount(remaining);
        }

        inventory.setStorageContents(storage);
    }

    public static int firstSimilar(@Nullable ItemStack [] storage, ItemStack item) {
        return firstSimilar(storage, item, true);
    }

    public static int firstSimilar(@Nullable ItemStack[] storage, ItemStack item, boolean withoutAmount) {
        for (int i = 0; i < storage.length; i++) {
            var stack = storage[i];
            if (stack != null && stack.getAmount() < stack.getMaxStackSize() && StackUtils.itemsMatch(stack, item, true, !withoutAmount)) {
                return i;
            }
        }

        return -1;
    }

    public static int firstEmpty(@Nullable ItemStack[] storage) {
        for (int i = 0; i < storage.length; i++) {
            var stack = storage[i];
            if (stack == null || stack.getType() == Material.AIR) {
                return i;
            }
        }

        return -1;
    }

    public static void give(Player player, @Nullable ItemStack stack) {
        if (stack == null) return;
        InventoryUtil.addItem(player, stack);
        if (stack.getAmount() > 0) {
            Scheduling.runTask(() -> {
                player.getWorld().dropItem(player.getLocation(), stack);
            });
        }
    }
}
