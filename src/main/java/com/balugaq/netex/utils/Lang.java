package com.balugaq.netex.utils;

import com.ytdd9527.networksexpansion.core.services.LocalizationService;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Lang {
    public static LocalizationService get() {
        return Networks.getLocalizationService();
    }

    @NotNull public static ItemStack getMechanism(@NotNull String key) {
        return get().getMechanism(key);
    }

    @NotNull public static SlimefunItemStack getItem(@NotNull String key, @NotNull Material material) {
        return get().getItem(key, material);
    }

    @NotNull @ParametersAreNonnullByDefault
    public static SlimefunItemStack getItem(String id, String texture, String... extraLore) {
        return get().getItem(id, texture, extraLore);
    }

    @NotNull @ParametersAreNonnullByDefault
    public static SlimefunItemStack getItem(String id, ItemStack itemStack, String... extraLore) {
        return get().getItem(id, itemStack, extraLore);
    }

    @NotNull public static ItemStack getIcon(@NotNull String key, @NotNull Material material) {
        return get().getIcon(key, material);
    }

    @NotNull public static String getString(@NotNull String key) {
        return get().getString(key);
    }

    @ParametersAreNonnullByDefault
    @NotNull public String getString(String key, Object... args) {
        return get().getString(key, args);
    }

    @NotNull public static List<String> getStringList(@NotNull String key) {
        return get().getStringList(key);
    }

    @NotNull public static String[] getStringArray(@NotNull String key) {
        return get().getStringArray(key);
    }
}
