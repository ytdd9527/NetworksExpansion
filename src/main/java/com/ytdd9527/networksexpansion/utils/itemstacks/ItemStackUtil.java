package com.ytdd9527.networksexpansion.utils.itemstacks;

import com.balugaq.netex.api.data.ItemAmountWrapper;
import com.balugaq.netex.api.data.ItemWrapper;
import com.balugaq.netex.utils.Debug;
import com.balugaq.netex.utils.NetworksVersionedEnchantment;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.nms.ItemNameAdapter;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Final_ROOT
 */
@SuppressWarnings({"deprecation", "unused"})
public final class ItemStackUtil {
    public static final ItemStack AIR = new ItemStack(Material.AIR);
    public static final @Nullable ItemNameAdapter itemNameAdapter = ItemNameAdapter.get();

    /**
     * Clone an #{@link ItemStack}
     *
     * @param item to be cloned
     * @return a cloned #{@link ItemStack}
     */
    public static @NotNull ItemStack cloneItem(@NotNull ItemStack item) {
        return item instanceof ItemStackWrapper ? ItemStackUtil.getCleanItem(item) : item.clone();
    }

    public static @NotNull ItemStack getCleanItem(@Nullable ItemStack item) {
        if (item == null) {
            return new ItemStack(Material.AIR);
        }

        ItemStack cleanItem = new ItemStack(item.getType());
        cleanItem.setAmount(item.getAmount());
        if (item.hasItemMeta()) {
            cleanItem.setItemMeta(item.getItemMeta());
        }

        return cleanItem;
    }

    /**
     * Clone an #{@link ItemStack}
     *
     * @param item   to be cloned
     * @param amount the amount of the result item
     * @return a cloned #{@link ItemStack}
     */
    public static @NotNull ItemStack cloneItem(@NotNull ItemStack item, int amount) {
        ItemStack itemStack = item instanceof ItemStackWrapper ? ItemStackUtil.getCleanItem(item) : item.clone();
        itemStack.setAmount(amount);
        return itemStack;
    }

    /**
     * @return Whether the item seems to be null
     */
    public static boolean isItemNull(@Nullable ItemStack item) {
        return item == null || item.getType() == Material.AIR || item.getAmount() == 0;
    }

    /**
     * @param item1 The first item
     * @param item2 The second item
     * @return Whether item1 is similar to item2
     */
    public static boolean isItemSimilar(@Nullable ItemStack item1, @Nullable ItemStack item2) {
        if (item1 == item2) {
            return true;
        }
        boolean itemNull1 = ItemStackUtil.isItemNull(item1);
        boolean itemNull2 = ItemStackUtil.isItemNull(item2);
        if (itemNull1 && itemNull2) {
            return true;
        } else if (itemNull1 || itemNull2) {
            return false;
        }
        if (!item1.getType().equals(item2.getType())) {
            return false;
        }
        if (item1.hasItemMeta() && item2.hasItemMeta()) {
            return ItemStackUtil.isItemMetaSame(item1.getItemMeta(), item2.getItemMeta());
        }
        return !item1.hasItemMeta() && !item2.hasItemMeta();
    }

    public static boolean isItemSimilar(@Nullable ItemStack item1, @NotNull ItemWrapper item2) {
        if (item1 == item2.getItemStack()) {
            return true;
        }
        if (ItemStackUtil.isItemNull(item1)) {
            return false;
        }
        if (!item1.getType().equals(item2.getItemStack().getType())) {
            return false;
        }
        if (item1.hasItemMeta() && item2.hasItemMeta()) {
            return ItemStackUtil.isItemMetaSame(item1.getItemMeta(), item2.getItemMeta());
        }
        return !item1.hasItemMeta() && !item2.hasItemMeta();
    }

    public static boolean isItemSimilar(@NotNull ItemWrapper item1, @Nullable ItemStack item2) {
        if (item1.getItemStack() == item2) {
            return true;
        }
        if (ItemStackUtil.isItemNull(item2)) {
            return false;
        }
        if (!item1.getItemStack().getType().equals(item2.getType())) {
            return false;
        }
        if (item1.hasItemMeta() && item2.hasItemMeta()) {
            return ItemStackUtil.isItemMetaSame(item1.getItemMeta(), item2.getItemMeta());
        }
        return !item1.hasItemMeta() && !item2.hasItemMeta();
    }

    public static boolean isItemSimilar(@NotNull ItemWrapper item1, @NotNull ItemWrapper item2) {
        if (item1.getItemStack() == item2.getItemStack()) {
            return true;
        }
        if (!item1.getItemStack().getType().equals(item2.getItemStack().getType())) {
            return false;
        }
        if (item1.hasItemMeta() && item2.hasItemMeta()) {
            return ItemStackUtil.isItemMetaSame(item1.getItemMeta(), item2.getItemMeta());
        }
        return !item1.hasItemMeta() && !item2.hasItemMeta();
    }

    /**
     * @return Whether two #{@link ItemMeta} is same.
     */
    public static boolean isItemMetaSame(@Nullable ItemMeta itemMeta1, @Nullable ItemMeta itemMeta2) {
        if (itemMeta1 == null || itemMeta2 == null) {
            return itemMeta1 == itemMeta2;
        }

        if (itemMeta1.hasDisplayName() && itemMeta2.hasDisplayName()) {
            if (!itemMeta1.getDisplayName().equals(itemMeta2.getDisplayName())) {
                return false;
            }
        } else if (itemMeta1.hasDisplayName() || itemMeta2.hasDisplayName()) {
            return false;
        }
        return ItemStackUtil.isLoreSame(itemMeta1, itemMeta2) && ItemStackUtil.isContainerSame(itemMeta1, itemMeta2);
    }

    /**
     * @return Whether two #{@link ItemMeta} have same lore.
     * @see ItemMeta#getLore()
     */
    public static boolean isLoreSame(@NotNull ItemMeta itemMeta1, @NotNull ItemMeta itemMeta2) {
        if (itemMeta1.hasLore() && itemMeta2.hasLore()) {
            List<String> lore1 = itemMeta1.getLore();
            List<String> lore2 = itemMeta2.getLore();
            if (lore1 == null || lore2 == null) {
                return lore1 == lore2;
            }
            if (lore1.size() != lore2.size()) {
                return false;
            }
            for (int i = 0; i < lore1.size(); i++) {
                if (!lore1.get(i).equals(lore2.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return !itemMeta1.hasLore() && !itemMeta2.hasLore();
    }

    /**
     * @return Whether two #{@link ItemMeta} have same #{@link PersistentDataContainer}
     * @see ItemMeta#getPersistentDataContainer()
     */
    public static boolean isContainerSame(@NotNull ItemMeta itemMeta1, @NotNull ItemMeta itemMeta2) {
        PersistentDataContainer persistentDataContainer1 = itemMeta1.getPersistentDataContainer();
        PersistentDataContainer persistentDataContainer2 = itemMeta2.getPersistentDataContainer();
        Set<NamespacedKey> keys1 = persistentDataContainer1.getKeys();
        Set<NamespacedKey> keys2 = persistentDataContainer2.getKeys();
        if (keys1.size() != keys2.size()) {
            return false;
        }
        for (NamespacedKey namespacedKey : keys1) {
            if (!persistentDataContainer2.has(namespacedKey, PersistentDataType.STRING)) {
                return false;
            }
            if (!Objects.equals(
                    persistentDataContainer1.get(namespacedKey, PersistentDataType.STRING),
                    persistentDataContainer2.get(namespacedKey, PersistentDataType.STRING))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEnchantmentSame(@NotNull ItemStack itemStack1, @NotNull ItemStack itemStack2) {
        if (itemStack1 == itemStack2) {
            return true;
        }
        Map<Enchantment, Integer> enchantments1 = itemStack1.getEnchantments();
        Map<Enchantment, Integer> enchantments2 = itemStack2.getEnchantments();
        if (enchantments1.size() != enchantments2.size()) {
            return false;
        }
        for (Map.Entry<Enchantment, Integer> entry : enchantments1.entrySet()) {
            if (!enchantments2.containsKey(entry.getKey())
                    || !Objects.equals(enchantments2.get(entry.getKey()), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return Same #{@link ItemStack} but nonnull.
     */
    @NotNull public static ItemStack[] getNoNullItemArray(@NotNull List<ItemStack> itemList) {
        List<ItemStack> noNullItemList = new ArrayList<>(itemList.size());
        for (ItemStack item : itemList) {
            if (!ItemStackUtil.isItemNull(item)) {
                noNullItemList.add(item);
            }
        }
        return ItemStackUtil.getItemArray(noNullItemList);
    }

    /**
     * @return Same #{@link ItemStack} but in array.
     */
    @NotNull public static ItemStack @NotNull [] getItemArray(@NotNull List<ItemStack> itemList) {
        ItemStack[] items = new ItemStack[itemList.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = itemList.get(i);
        }
        return items;
    }

    /**
     * @return Same #{@link ItemStack} but nonnull.
     */
    @NotNull public static List<ItemStack> getNoNullItemList(@NotNull ItemStack @NotNull [] items) {
        List<ItemStack> itemList = new ArrayList<>(items.length);
        for (ItemStack item : items) {
            if (!ItemStackUtil.isItemNull(item)) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    /**
     * Merge item to a new List.
     */
    @NotNull public static List<ItemStack> calMergeItems(@NotNull List<ItemStack> itemList) {
        List<ItemWrapper> itemWrapperList = new ArrayList<>(itemList.size());
        ItemWrapper itemWrapper = new ItemWrapper();
        int amount;
        for (ItemStack item : itemList) {
            if (ItemStackUtil.isItemNull(item)) {
                continue;
            }
            itemWrapper.newWrap(item);
            amount = item.getAmount();
            for (ItemWrapper resultItem : itemWrapperList) {
                ItemStack itemStack = resultItem.getItemStack();
                if (itemStack.getAmount() >= itemStack.getMaxStackSize()) {
                    continue;
                }
                if (StackUtils.itemsMatch(itemWrapper.getItemStack(), resultItem.getItemStack())) {
                    int count = Math.min(amount, itemStack.getMaxStackSize() - itemStack.getAmount());
                    itemStack.setAmount(itemStack.getAmount() + count);
                    amount -= count;
                    if (amount == 0) {
                        break;
                    }
                }
            }
            if (amount != 0) {
                ItemStack itemstack = ItemStackUtil.cloneItem(item);
                itemstack.setAmount(amount);
                itemWrapperList.add(new ItemWrapper(itemstack, itemWrapper.getItemMeta()));
            }
        }
        return ItemWrapper.getItemList(itemWrapperList);
    }

    /**
     * Merge item to a new Array.
     */
    @NotNull public static ItemStack[] calMergeItems(@NotNull ItemStack @NotNull [] items) {
        List<ItemWrapper> itemWrapperList = new ArrayList<>(items.length);
        ItemWrapper itemWrapper = new ItemWrapper();
        int amount;
        for (ItemStack item : items) {
            if (ItemStackUtil.isItemNull(item)) {
                continue;
            }
            itemWrapper.newWrap(item);
            amount = item.getAmount();
            for (ItemWrapper resultItem : itemWrapperList) {
                ItemStack itemStack = resultItem.getItemStack();
                if (itemStack.getAmount() >= itemStack.getMaxStackSize()) {
                    continue;
                }
                if (StackUtils.itemsMatch(itemWrapper.getItemStack(), resultItem.getItemStack())) {
                    int count = Math.min(amount, itemStack.getMaxStackSize() - itemStack.getAmount());
                    itemStack.setAmount(itemStack.getAmount() + count);
                    amount -= count;
                    if (amount == 0) {
                        break;
                    }
                }
            }
            if (amount != 0) {
                ItemStack itemstack = ItemStackUtil.cloneItem(item);
                itemstack.setAmount(amount);
                itemWrapperList.add(new ItemWrapper(itemstack, itemWrapper.getItemMeta()));
            }
        }
        return ItemWrapper.getItemArray(itemWrapperList);
    }

    /**
     * @param items  The #{@link ItemStack} to be enlarged.
     * @param amount The amount that the #{@link ItemStack} will be enlarged.
     * @return A new array that multiply the amount to the items.
     */
    @NotNull public static ItemStack @NotNull [] calEnlargeItemArray(@NotNull ItemStack @NotNull [] items, int amount) {
        int slot = 0;
        for (ItemStack item : items) {
            slot = slot + 1 + item.getAmount() * amount / item.getMaxStackSize();
            if (item.getAmount() * amount % item.getMaxStackSize() == 0) {
                slot--;
            }
        }
        ItemStack[] result = new ItemStack[slot];
        int pointer = 0;
        for (ItemStack item : items) {
            int resultAmount = item.getAmount() * amount;
            while (resultAmount > item.getMaxStackSize()) {
                result[pointer] = ItemStackUtil.cloneItem(item);
                result[pointer++].setAmount(item.getMaxStackSize());
                resultAmount -= item.getMaxStackSize();
            }
            if (resultAmount != 0) {
                result[pointer] = ItemStackUtil.cloneItem(item);
                result[pointer++].setAmount(resultAmount);
            }
        }
        return result;
    }

    /**
     * @param itemAmountWrapperList The #{@link ItemAmountWrapper} to be enlarged.
     * @param amount                The amount that the #{@link ItemStack} will be enlarged.
     * @return A new array that multiply the amount to the items.
     */
    @NotNull public static ItemStack @NotNull [] calEnlargeItemArray(
            @NotNull List<ItemAmountWrapper> itemAmountWrapperList, int amount) {
        int slot = 0;
        for (ItemAmountWrapper itemAmountWrapper : itemAmountWrapperList) {
            slot = slot
                    + 1
                    + itemAmountWrapper.getAmount()
                            * amount
                            / itemAmountWrapper.getItemStack().getMaxStackSize();
            if (itemAmountWrapper.getAmount()
                            * amount
                            % itemAmountWrapper.getItemStack().getMaxStackSize()
                    == 0) {
                slot--;
            }
        }
        ItemStack[] result = new ItemStack[slot];
        int pointer = 0;
        for (ItemAmountWrapper itemAmountWrapper : itemAmountWrapperList) {
            int resultAmount = itemAmountWrapper.getAmount() * amount;
            while (resultAmount > itemAmountWrapper.getItemStack().getMaxStackSize()) {
                result[pointer] = ItemStackUtil.cloneItem(itemAmountWrapper.getItemStack());
                result[pointer++].setAmount(itemAmountWrapper.getItemStack().getMaxStackSize());
                resultAmount -= itemAmountWrapper.getItemStack().getMaxStackSize();
            }
            if (resultAmount != 0) {
                result[pointer] = ItemStackUtil.cloneItem(itemAmountWrapper.getItemStack());
                result[pointer++].setAmount(resultAmount);
            }
        }
        return result;
    }

    /**
     * @param itemAmountWrapperList The #{@link ItemAmountWrapper} to be enlarged.
     * @param amount                The amount that the #{@link ItemStack} will be enlarged.
     * @return A new array that multiply the amount to the items.
     */
    @NotNull public static ItemStack @NotNull [] calEnlargeItemArray(
            @NotNull ItemAmountWrapper @NotNull [] itemAmountWrapperList, int amount) {
        int slot = 0;
        for (ItemAmountWrapper itemAmountWrapper : itemAmountWrapperList) {
            slot = slot
                    + 1
                    + itemAmountWrapper.getAmount()
                            * amount
                            / itemAmountWrapper.getItemStack().getMaxStackSize();
            if (itemAmountWrapper.getAmount()
                            * amount
                            % itemAmountWrapper.getItemStack().getMaxStackSize()
                    == 0) {
                slot--;
            }
        }
        ItemStack[] result = new ItemStack[slot];
        int pointer = 0;
        for (ItemAmountWrapper itemAmountWrapper : itemAmountWrapperList) {
            int resultAmount = itemAmountWrapper.getAmount() * amount;
            while (resultAmount > itemAmountWrapper.getItemStack().getMaxStackSize()) {
                result[pointer] = ItemStackUtil.cloneItem(itemAmountWrapper.getItemStack());
                result[pointer++].setAmount(itemAmountWrapper.getItemStack().getMaxStackSize());
                resultAmount -= itemAmountWrapper.getItemStack().getMaxStackSize();
            }
            if (resultAmount != 0) {
                result[pointer] = ItemStackUtil.cloneItem(itemAmountWrapper.getItemStack());
                result[pointer++].setAmount(resultAmount);
            }
        }
        return result;
    }

    /**
     * Transfer #{@link ItemStack} to A List of #{@link ItemAmountWrapper}.
     * All returned #{@link ItemAmountWrapper} will contain different #{@link ItemStack} with its amount in items.
     */
    @NotNull public static List<ItemAmountWrapper> calItemListWithAmount(@NotNull ItemStack @NotNull [] items) {
        List<ItemAmountWrapper> itemWithWrapperList = new ArrayList<>(items.length);
        ItemAmountWrapper itemAmountWrapper = new ItemAmountWrapper();
        for (ItemStack item : items) {
            if (ItemStackUtil.isItemNull(item)) {
                continue;
            }
            itemAmountWrapper.newWrap(ItemStackUtil.cloneItem(item));
            boolean find = false;
            for (ItemAmountWrapper existedItemWrapper : itemWithWrapperList) {
                if (StackUtils.itemsMatch(itemAmountWrapper.getItemStack(), existedItemWrapper.getItemStack())) {
                    existedItemWrapper.addAmount(item.getAmount());
                    find = true;
                    break;
                }
            }
            if (!find) {
                itemWithWrapperList.add(itemAmountWrapper.shallowClone());
            }
        }
        return itemWithWrapperList;
    }

    /**
     * Transfer #{@link ItemStack} to A List of #{@link ItemAmountWrapper}.
     * All returned #{@link ItemAmountWrapper} will contain different #{@link ItemStack} with its amount in items.
     */
    @NotNull public static List<ItemAmountWrapper> calItemListWithAmount(@NotNull List<ItemStack> itemList) {
        List<ItemAmountWrapper> itemWithWrapperList = new ArrayList<>(itemList.size());
        ItemAmountWrapper itemAmountWrapper = new ItemAmountWrapper();
        for (ItemStack item : itemList) {
            if (ItemStackUtil.isItemNull(item)) {
                continue;
            }
            itemAmountWrapper.newWrap(ItemStackUtil.cloneItem(item));
            boolean find = false;
            for (ItemAmountWrapper existedItemWrapper : itemWithWrapperList) {
                if (StackUtils.itemsMatch(itemAmountWrapper.getItemStack(), existedItemWrapper.getItemStack())) {
                    existedItemWrapper.addAmount(item.getAmount());
                    find = true;
                    break;
                }
            }
            if (!find) {
                itemWithWrapperList.add(itemAmountWrapper.shallowClone());
            }
        }
        return itemWithWrapperList;
    }

    /**
     * Transfer #{@link ItemStack} to A List of #{@link ItemAmountWrapper}.
     * All returned #{@link ItemAmountWrapper} will contain different #{@link ItemStack} with its amount in items.
     */
    @NotNull public static ItemAmountWrapper @NotNull [] calItemArrayWithAmount(@NotNull ItemStack @NotNull [] items) {
        return ItemStackUtil.calItemListWithAmount(items).toArray(new ItemAmountWrapper[0]);
    }

    /**
     * Transfer #{@link ItemStack} to A List of #{@link ItemAmountWrapper}.
     * All returned #{@link ItemAmountWrapper} will contain different #{@link ItemStack} with its amount in items.
     */
    @NotNull public static ItemAmountWrapper @NotNull [] calItemArrayWithAmount(@NotNull List<ItemStack> itemList) {
        return ItemStackUtil.calItemListWithAmount(itemList).toArray(new ItemAmountWrapper[0]);
    }

    /**
     * Try stack one #{@link ItemStack} to another #{@link ItemStack}
     *
     * @param input  #{@link ItemStack} to stack to
     * @param output #{@link ItemStack} to be stacked
     * @return The number that truly stacked.
     */
    public static int stack(@Nullable ItemStack input, @Nullable ItemStack output) {
        if (!ItemStackUtil.isItemNull(output)
                && output.getAmount() < output.getMaxStackSize()
                && StackUtils.itemsMatch(input, output)) {
            if (input == null) {
                return output.getAmount();
            }
            int amount = Math.min(input.getAmount(), output.getMaxStackSize() - output.getAmount());
            input.setAmount(input.getAmount() - amount);
            output.setAmount(output.getAmount() + amount);
            return amount;
        }
        return 0;
    }

    public static int stack(@NotNull ItemWrapper input, @Nullable ItemStack output) {
        if (!ItemStackUtil.isItemNull(output)
                && output.getAmount() < output.getMaxStackSize()
                && StackUtils.itemsMatch(input.getItemStack(), output)) {
            int amount = Math.min(input.getItemStack().getAmount(), output.getMaxStackSize() - output.getAmount());
            input.getItemStack().setAmount(input.getItemStack().getAmount() - amount);
            output.setAmount(output.getAmount() + amount);
            return amount;
        }
        return 0;
    }

    public static int stack(@Nullable ItemStack input, @NotNull ItemWrapper output) {
        if (!ItemStackUtil.isItemNull(input)
                && output.getItemStack().getAmount() < output.getItemStack().getMaxStackSize()
                && StackUtils.itemsMatch(input, output.getItemStack())) {
            int amount = Math.min(
                    input.getAmount(),
                    output.getItemStack().getMaxStackSize()
                            - output.getItemStack().getAmount());
            input.setAmount(input.getAmount() - amount);
            output.getItemStack().setAmount(output.getItemStack().getAmount() + amount);
            return amount;
        }
        return 0;
    }

    public static int stack(@NotNull ItemWrapper input, @NotNull ItemWrapper output) {
        if (output.getItemStack().getAmount() < output.getItemStack().getMaxStackSize()
                && StackUtils.itemsMatch(input.getItemStack(), output.getItemStack())) {
            int amount = Math.min(
                    input.getItemStack().getAmount(),
                    output.getItemStack().getMaxStackSize()
                            - output.getItemStack().getAmount());
            input.getItemStack().setAmount(input.getItemStack().getAmount() - amount);
            output.getItemStack().setAmount(output.getItemStack().getAmount() + amount);
            return amount;
        }
        return 0;
    }

    /**
     * Try stack one #{@link ItemStack} to another #{@link ItemStack} in limited amount
     *
     * @param input     #{@link ItemStack} to stack to
     * @param output    #{@link ItemStack} to be stacked
     * @param maxAmount Stack amount to be limited.
     * @return The number that truly stacked.
     */
    public static int stack(@Nullable ItemStack input, @Nullable ItemStack output, int maxAmount) {
        if (!ItemStackUtil.isItemNull(output)
                && output.getMaxStackSize() > output.getAmount()
                && StackUtils.itemsMatch(input, output)) {
            if (input == null) {
                return output.getAmount();
            }
            int amount =
                    Math.min(maxAmount, Math.min(input.getAmount(), output.getMaxStackSize() - output.getAmount()));
            input.setAmount(input.getAmount() - amount);
            output.setAmount(output.getAmount() + amount);
            return amount;
        }
        return 0;
    }

    public static int stack(@NotNull ItemWrapper input, @Nullable ItemStack output, int maxAmount) {
        if (!ItemStackUtil.isItemNull(output)
                && output.getAmount() < output.getMaxStackSize()
                && StackUtils.itemsMatch(input.getItemStack(), output)) {
            int amount = Math.min(
                    maxAmount,
                    Math.min(input.getItemStack().getAmount(), output.getMaxStackSize() - output.getAmount()));
            input.getItemStack().setAmount(input.getItemStack().getAmount() - amount);
            output.setAmount(output.getAmount() + amount);
            return amount;
        }
        return 0;
    }

    public static int stack(@Nullable ItemStack input, @NotNull ItemWrapper output, int maxAmount) {
        if (!ItemStackUtil.isItemNull(input)
                && output.getItemStack().getAmount() < output.getItemStack().getMaxStackSize()
                && StackUtils.itemsMatch(input, output.getItemStack())) {
            int amount = Math.min(
                    maxAmount,
                    Math.min(
                            input.getAmount(),
                            output.getItemStack().getMaxStackSize()
                                    - output.getItemStack().getAmount()));
            input.setAmount(input.getAmount() - amount);
            output.getItemStack().setAmount(output.getItemStack().getAmount() + amount);
            return amount;
        }
        return 0;
    }

    public static int stack(@NotNull ItemWrapper input, @NotNull ItemWrapper output, int maxAmount) {
        if (output.getItemStack().getAmount() < output.getItemStack().getMaxStackSize()
                && StackUtils.itemsMatch(input.getItemStack(), output.getItemStack())) {
            int amount = Math.min(
                    maxAmount,
                    Math.min(
                            input.getItemStack().getAmount(),
                            output.getItemStack().getMaxStackSize()
                                    - output.getItemStack().getAmount()));
            input.getItemStack().setAmount(input.getItemStack().getAmount() - amount);
            output.getItemStack().setAmount(output.getItemStack().getAmount() + amount);
            return amount;
        }
        return 0;
    }

    /**
     * @return Name of the given #{@link ItemStack}.
     */
    @NotNull public static String getItemName(@Nullable ItemStack item) {
        if (ItemStackUtil.isItemNull(item)) {
            return "null";
        }
        item = ItemStackUtil.getCleanItem(item);
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.hasDisplayName()) {
                return itemMeta.getDisplayName();
            }
        } else {
            try {
                if (itemNameAdapter != null) {
                    return itemNameAdapter.getName(item);
                } else {
                    return "unknown";
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                Debug.trace(e);
                return "unknown";
            }
        }
        return "unknown";
    }

    public static void setItemName(@NotNull ItemStack item, @NotNull String itemName) {
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(itemName);
        item.setItemMeta(itemMeta);
    }

    public static void addLoreToFirst(@Nullable ItemStack item, @NotNull String s) {
        if (ItemStackUtil.isItemNull(item)) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>(8);
            lore.add(s);
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        } else {
            List<String> newLore = new ArrayList<>(lore.size() + 1);
            newLore.add(s);
            newLore.addAll(lore);
            itemMeta.setLore(newLore);
            item.setItemMeta(itemMeta);
        }
    }

    public static void addLoreToLast(@Nullable ItemStack item, @NotNull String s) {
        if (ItemStackUtil.isItemNull(item)) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>(8);
        }
        lore.add(s);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
    }

    public static void addLoreToLast(@NotNull ItemMeta itemMeta, @NotNull String s) {
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>(8);
        }
        lore.add(s);
        itemMeta.setLore(lore);
    }

    public static void addLoresToLast(@Nullable ItemStack item, @NotNull String @NotNull ... s) {
        if (ItemStackUtil.isItemNull(item)) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>(8);
        }
        lore.addAll(Arrays.stream(s).toList());
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
    }

    public static void addLoresToLast(@NotNull ItemMeta itemMeta, @NotNull String @NotNull ... s) {
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>(8);
        }
        lore.addAll(Arrays.stream(s).toList());
        itemMeta.setLore(lore);
    }

    public static void removeLastLore(@Nullable ItemStack item) {
        if (ItemStackUtil.isItemNull(item)) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null || lore.isEmpty()) {
            return;
        }
        lore = lore.subList(0, Math.max(lore.size() - 1, 0));
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
    }

    public static void removeLastLore(@NotNull ItemMeta itemMeta) {
        List<String> lore = itemMeta.getLore();
        if (lore == null || lore.isEmpty()) {
            return;
        }
        lore = lore.subList(0, Math.max(lore.size() - 1, 0));
        itemMeta.setLore(lore);
    }

    public static void setLastLore(@NotNull ItemStack item, @NotNull String s) {
        if (ItemStackUtil.isItemNull(item)) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null || lore.isEmpty()) {
            lore = new ArrayList<>();
            lore.add(s);
        } else {
            lore.set(lore.size() - 1, s);
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
    }

    public static void setLastLore(@NotNull ItemMeta itemMeta, @NotNull String s) {
        List<String> lore = itemMeta.getLore();
        if (lore == null || lore.isEmpty()) {
            lore = new ArrayList<>();
            lore.add(s);
        } else {
            lore.set(lore.size() - 1, s);
        }
        itemMeta.setLore(lore);
    }

    @Nullable public static String getLastLore(@NotNull ItemStack item) {
        if (ItemStackUtil.isItemNull(item)) {
            return null;
        }
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null || lore.isEmpty()) {
            return null;
        }
        return lore.get(lore.size() - 1);
    }

    @Nullable public static String getLastLore(@NotNull ItemMeta itemMeta) {
        List<String> lore = itemMeta.getLore();
        if (lore == null || lore.isEmpty()) {
            return null;
        }
        return lore.get(lore.size() - 1);
    }

    public static void setLore(@NotNull ItemStack item, @NotNull String @NotNull ... lore) {
        if (ItemStackUtil.isItemNull(item)) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(Arrays.stream(lore).toList());
        item.setItemMeta(itemMeta);
    }

    public static void setLore(@NotNull ItemStack item, @NotNull List<String> lore) {
        if (ItemStackUtil.isItemNull(item)) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
    }

    public static void replaceLore(@NotNull ItemStack item, int loreOffset, @NotNull String @NotNull ... lore) {
        if (loreOffset < 0) {
            ItemStackUtil.setLore(item, lore);
            return;
        }
        if (ItemStackUtil.isItemNull(item)) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        List<String> oldLore = itemMeta.getLore();
        if (oldLore == null) {
            oldLore = new ArrayList<>();
        }
        while (oldLore.size() < loreOffset) {
            oldLore.add("");
        }
        for (int i = 0; i < lore.length; i++) {
            if (oldLore.size() <= loreOffset + i) {
                oldLore.add(loreOffset + i, lore[i]);
            } else {
                oldLore.set(loreOffset + i, lore[i]);
            }
        }
        itemMeta.setLore(oldLore);
        item.setItemMeta(itemMeta);
    }

    public static void replaceLore(@NotNull ItemStack item, int loreOffset, @NotNull List<String> lore) {
        if (loreOffset < 0) {
            ItemStackUtil.setLore(item, lore);
            return;
        }
        if (ItemStackUtil.isItemNull(item)) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        List<String> oldLore = itemMeta.getLore();
        if (oldLore == null) {
            oldLore = new ArrayList<>();
        }
        while (oldLore.size() < loreOffset) {
            oldLore.add("");
        }
        for (int i = 0; i < lore.size(); i++) {
            if (oldLore.size() <= loreOffset + i) {
                oldLore.add(loreOffset + i, lore.get(i));
            } else {
                oldLore.set(loreOffset + i, lore.get(i));
            }
        }
        itemMeta.setLore(oldLore);
        item.setItemMeta(itemMeta);
    }

    public static void addItemFlag(@NotNull ItemStack item, @NotNull ItemFlag itemFlag) {
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(itemFlag);
        item.setItemMeta(itemMeta);
    }

    public static void addNBT(@NotNull ItemStack item, @NotNull NamespacedKey namespacedKey, @NotNull String value) {
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        if (!persistentDataContainer.has(namespacedKey, PersistentDataType.STRING)) {
            persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, value);
            item.setItemMeta(itemMeta);
        }
    }

    public static void setNBT(@NotNull ItemStack item, @NotNull NamespacedKey namespacedKey, @NotNull String value) {
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, value);
        item.setItemMeta(itemMeta);
    }

    public static void clearNBT(@NotNull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        for (NamespacedKey namespacedKey : persistentDataContainer.getKeys()) {
            persistentDataContainer.remove(namespacedKey);
        }
        itemStack.setItemMeta(itemMeta);
    }

    @Nullable public static ItemStack cloneWithoutNBT(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        if (!itemStack.hasItemMeta()) {
            return ItemStackUtil.getCleanItem(itemStack);
        }
        ItemStack result = ItemStackUtil.getCleanItem(itemStack);
        ItemStackUtil.clearNBT(result);
        return result;
    }

    public static @Nullable ItemStack getDried(@NotNull ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return null;
        }
        return switch (item.getType()) {
            case POTION, DRAGON_BREATH, HONEY_BOTTLE -> new ItemStack(Material.GLASS_BOTTLE, 1);
            case WATER_BUCKET, LAVA_BUCKET, MILK_BUCKET -> new ItemStack(Material.BUCKET, 1);
            default -> null;
        };
    }

    /**
     * Transfer #{@link ItemStack} to #{@link String}
     */
    @NotNull public static String itemStackToString(@NotNull ItemStack itemStack) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("item", itemStack);
        return yamlConfiguration.saveToString();
    }

    /**
     * Transfer #{@link String} to #{@link ItemStack}
     */
    @Nullable public static ItemStack stringToItemStack(@NotNull String local) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.loadFromString(local);
        } catch (InvalidConfigurationException e) {
            Debug.trace(e);
        }
        return yamlConfiguration.getItemStack("item");
    }

    public static String color(@Nullable String str) {
        if (str == null) {
            return null;
        }
        return TextUtil.color(str);
    }

    public static void giveOrDropItem(@NotNull Player p, @NotNull ItemStack toGive) {
        for (int i = 0; i < 64; i++) {
            if (toGive.getAmount() <= 0) {
                return;
            }
            ItemStack incoming = toGive.clone();
            incoming.setAmount(Math.min(toGive.getMaxStackSize(), toGive.getAmount()));
            toGive.setAmount(toGive.getAmount() - incoming.getAmount());
            Collection<ItemStack> leftover = p.getInventory().addItem(incoming).values();
            for (ItemStack itemStack : leftover) {
                p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
            }
        }
    }

    public static void send(@NotNull CommandSender p, String message) {
        p.sendMessage(color(TextUtil.GRAY + "[" + TextUtil.GOLD + "NetworksExpansion" + TextUtil.GRAY + "] "
                + TextUtil.RESET + message));
    }

    public static @NotNull ItemStack getPreEnchantedItemStack(@NotNull Material material) {
        return getPreEnchantedItemStack(material, true);
    }

    public static @NotNull ItemStack getPreEnchantedItemStack(@NotNull Material material, boolean hide) {
        return getPreEnchantedItemStack(material, hide, new Pair<>(NetworksVersionedEnchantment.GLOW, 1));
    }

    @NotNull @SafeVarargs
    public static ItemStack getPreEnchantedItemStack(
            @NotNull Material material, boolean hide, @NotNull Pair<Enchantment, Integer> @NotNull ... enchantments) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        for (Pair<Enchantment, Integer> pair : enchantments) {
            Enchantment v1 = pair.getFirstValue();
            Integer v2 = pair.getSecondValue();
            if (v1 == null || v2 == null) {
                continue;
            }
            itemMeta.addEnchant(v1, v2, true);
        }
        if (hide) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
