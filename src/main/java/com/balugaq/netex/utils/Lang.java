package com.balugaq.netex.utils;

import com.ytdd9527.networksexpansion.core.services.LocalizationService;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.MessageFormat;
import java.util.List;

@NullMarked
public class Lang {
    public static LocalizationService get() {
        return Networks.getLocalizationService();
    }

    public static ItemStack getMechanism(String key) {
        return get().getMechanism(key);
    }

    public static SlimefunItemStack getItem(String key, Material material) {
        return get().getItem(key, material);
    }

    public static SlimefunItemStack getItem(String id, Material material, Object... args) {
        SlimefunItemStack stack = get().getItem(id, material);
        if (args.length > 0) {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(MessageFormat.format(meta.getDisplayName(), args));
            List<String> lore = meta.getLore();
            if (lore != null) {
                lore.replaceAll(line -> MessageFormat.format(line, args));
            }
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public static SlimefunItemStack getItem(String id, String texture, String... extraLore) {
        return get().getItem(id, texture, extraLore);
    }

    public static SlimefunItemStack getItem(String id, ItemStack itemStack, String... extraLore) {
        return get().getItem(id, itemStack, extraLore);
    }

    public static ItemStack getIcon(String key, Material material) {
        return get().getIcon(key, material);
    }

    public static String getString(String key) {
        return get().getString(key);
    }

    public static String getString(String key, Object... args) {
        return get().getString(key, args);
    }

    public static List<String> getStringList(String key) {
        return get().getStringList(key);
    }

    public static String[] getStringArray(String key) {
        return get().getStringArray(key);
    }
}
