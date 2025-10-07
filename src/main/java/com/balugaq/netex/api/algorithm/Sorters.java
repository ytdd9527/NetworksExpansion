package com.balugaq.netex.api.algorithm;

import com.balugaq.netex.api.data.StorageUnitData;
import com.balugaq.netex.api.helpers.Icon;
import com.ytdd9527.networksexpansion.implementation.machines.managers.DrawerManager;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.network.stackcaches.BarrelIdentity;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

@NullMarked
public class Sorters {
    public static final String NO_ITEM = ItemStackHelper.getDisplayName(Icon.QUANTUM_STORAGE_NO_ITEM);

    public static final Comparator<Map.Entry<ItemStack, Long>> ITEMSTACK_ALPHABETICAL_SORT = Comparator.comparing(
        entry -> {
            ItemStack itemStack = entry.getKey();
            SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
            if (slimefunItem != null) {
                return TextUtil.stripColor(slimefunItem.getItemName());
            } else {
                return TextUtil.stripColor(ItemStackHelper.getDisplayName(itemStack));
            }
        },
        Collator.getInstance(Locale.CHINA)::compare);
    public static final Comparator<Map.Entry<ItemStack, Long>> ITEMSTACK_NUMERICAL_SORT = Map.Entry.comparingByValue();
    public static final Comparator<Map.Entry<ItemStack, Long>> ITEMSTACK_ADDON_SORT = Comparator.comparing(
        entry -> {
            ItemStack itemStack = entry.getKey();
            SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
            if (slimefunItem != null) {
                return TextUtil.stripColor(slimefunItem.getAddon().getName());
            } else {
                return "Minecraft";
            }
        },
        Collator.getInstance(Locale.CHINA)::compare);

    public static final Comparator<? super BarrelIdentity> BARREL_ALPHABETICAL_SORT = Comparator.comparing(
        barrel -> {
            ItemStack itemStack = barrel.getItemStack();
            if (itemStack == null) {
                return NO_ITEM;
            }
            SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
            if (slimefunItem != null) {
                return TextUtil.stripColor(slimefunItem.getItemName());
            } else {
                return TextUtil.stripColor(ItemStackHelper.getDisplayName(itemStack));
            }
        },
        Collator.getInstance(Locale.CHINA)::compare);
    public static final Comparator<BarrelIdentity> BARREL_NUMERICAL_SORT =
        Comparator.comparingLong(BarrelIdentity::getAmount);
    public static final Comparator<? super StorageUnitData> DRAWER_ALPHABETICAL_SORT = Comparator.comparing(
        data -> {
            ItemStack itemStack = DrawerManager.getItemStack(data);
            if (itemStack == null) {
                return NO_ITEM;
            }
            SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
            if (slimefunItem != null) {
                return TextUtil.stripColor(slimefunItem.getItemName());
            } else {
                return TextUtil.stripColor(ItemStackHelper.getDisplayName(itemStack));
            }
        },
        Collator.getInstance(Locale.CHINA)::compare);
    public static final Comparator<StorageUnitData> DRAWER_NUMERICAL_SORT =
        Comparator.comparingLong(StorageUnitData::getTotalAmountLong);
}
