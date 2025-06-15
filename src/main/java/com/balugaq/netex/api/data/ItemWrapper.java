package com.balugaq.netex.api.data;

import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Final_ROOT
 * @since 2.0
 */
public class ItemWrapper {
    public static final ItemStack AIR = new ItemStack(Material.AIR);

    @NotNull private ItemStack itemStack;

    @Nullable private ItemMeta itemMeta;

    public ItemWrapper() {
        this.itemStack = AIR;
    }

    public ItemWrapper(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = this.itemStack.hasItemMeta() ? this.itemStack.getItemMeta() : null;
    }

    public ItemWrapper(@NotNull ItemStack itemStack, @Nullable ItemMeta itemMeta) {
        this.itemStack = itemStack;
        this.itemMeta = itemMeta;
    }

    @NotNull public static ItemStack @NotNull [] getItemArray(@NotNull ItemWrapper @NotNull [] itemWrappers) {
        ItemStack[] itemStacks = new ItemStack[itemWrappers.length];
        for (int i = 0, length = itemStacks.length; i < length; i++) {
            itemStacks[i] = itemWrappers[i].getItemStack();
        }
        return itemStacks;
    }

    @NotNull public static ItemStack @NotNull [] getItemArray(@NotNull List<? extends ItemWrapper> itemWrapperList) {
        ItemStack[] itemStacks = new ItemStack[itemWrapperList.size()];
        for (int i = 0, length = itemStacks.length; i < length; i++) {
            itemStacks[i] = itemWrapperList.get(i).getItemStack();
        }
        return itemStacks;
    }

    @NotNull public static ItemStack @NotNull [] getCopiedItemArray(@NotNull List<? extends ItemWrapper> itemWrapperList) {
        ItemStack[] itemStacks = new ItemStack[itemWrapperList.size()];
        for (int i = 0, length = itemStacks.length; i < length; i++) {
            itemStacks[i] = ItemStackUtil.cloneItem(itemWrapperList.get(i).getItemStack());
        }
        return itemStacks;
    }

    @NotNull public static List<ItemStack> getItemList(@NotNull ItemWrapper @NotNull [] itemWrappers) {
        List<ItemStack> itemStackList = new ArrayList<>(itemWrappers.length);
        for (ItemWrapper itemWrapper : itemWrappers) {
            itemStackList.add(itemWrapper.getItemStack());
        }
        return itemStackList;
    }

    @NotNull public static List<ItemStack> getItemList(@NotNull List<? extends ItemWrapper> itemWrapperList) {
        List<ItemStack> itemStackList = new ArrayList<>(itemWrapperList.size());
        for (ItemWrapper itemWrapper : itemWrapperList) {
            itemStackList.add(itemWrapper.getItemStack());
        }
        return itemStackList;
    }

    @NotNull public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void setItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Nullable public ItemMeta getItemMeta() {
        return this.itemMeta;
    }

    public void setItemMeta(@Nullable ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
    }

    public boolean hasItemMeta() {
        return this.itemMeta != null;
    }

    public void updateItemMeta() {
        this.itemMeta = this.itemStack.hasItemMeta() ? this.itemStack.getItemMeta() : null;
    }

    public void newWrap(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = this.itemStack.hasItemMeta() ? this.itemStack.getItemMeta() : null;
    }

    public void newWrap(@NotNull ItemStack itemStack, @Nullable ItemMeta itemMeta) {
        this.itemStack = itemStack;
        this.itemMeta = itemMeta;
    }

    @NotNull public ItemWrapper shallowClone() {
        return new ItemWrapper(this.itemStack, this.itemMeta);
    }

    @NotNull public ItemWrapper deepClone() {
        return new ItemWrapper(ItemStackUtil.getCleanItem(this.itemStack));
    }

    @SuppressWarnings("deprecation")
    @Override
    public int hashCode() {
        int hash = 31 + this.itemStack.getType().hashCode();
        hash = hash * 31 + this.itemStack.getAmount();
        hash = hash * 31 + (this.itemStack.getDurability() & 0xffff);
        hash = hash * 31 + (this.itemMeta != null ? (this.itemMeta.hashCode()) : 0);
        return hash;
    }

    @Override
    public boolean equals(@NotNull Object obj) {
        if (this.itemStack instanceof ItemStackWrapper) {
            return ItemStackUtil.getCleanItem(this.itemStack).equals(obj);
        } else {
            return this.itemStack.equals(obj);
        }
    }
}
