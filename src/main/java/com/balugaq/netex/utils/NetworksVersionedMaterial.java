package com.balugaq.netex.utils;

import com.balugaq.netex.api.enums.MinecraftVersion;
import io.github.sefiraat.networks.Networks;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;

@UtilityClass
public class NetworksVersionedMaterial {
    public static final Material MUSIC_DISC_CREATOR;

    static {
        MinecraftVersion version = Networks.getInstance().getMinecraftVersion();
        MUSIC_DISC_CREATOR = version.isAtLeast(MinecraftVersion.V1_21)
            ? Material.MUSIC_DISC_CREATOR
            : Material.MUSIC_DISC_RELIC;
    }
}
