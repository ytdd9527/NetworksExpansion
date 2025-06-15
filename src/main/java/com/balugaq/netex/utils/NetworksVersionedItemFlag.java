package com.balugaq.netex.utils;

import com.balugaq.netex.api.enums.MinecraftVersion;
import io.github.sefiraat.networks.Networks;
import java.lang.reflect.Field;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class NetworksVersionedItemFlag {
    public static final @Nullable ItemFlag HIDE_ADDITIONAL_TOOLTIP;

    static {
        MinecraftVersion version = Networks.getInstance().getMCVersion();
        HIDE_ADDITIONAL_TOOLTIP = version.isAtLeast(MinecraftVersion.MC1_20_5)
                ? ItemFlag.HIDE_ADDITIONAL_TOOLTIP
                : getKey("HIDE_POTION_EFFECTS");
    }

    @Nullable private static ItemFlag getKey(@NotNull String key) {
        try {
            Field field = ItemFlag.class.getDeclaredField(key);
            return (ItemFlag) field.get(null);
        } catch (Exception ignored) {
            return null;
        }
    }
}
