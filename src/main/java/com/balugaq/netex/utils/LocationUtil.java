package com.balugaq.netex.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class LocationUtil {
    @NotNull public static String humanize(@NotNull Location location) {
        return String.format(
                "%s (%.2f, %.2f, %.2f)",
                location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    @NotNull public static String humanizeBlock(@NotNull Location location) {
        return String.format(
                "%s (%d, %d, %d)",
                location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @NotNull public static String humanizeFull(@NotNull Location location) {
        return String.format(
                "%s (%.2f, %.2f, %.2f, %.2f, %.2f)",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }

    public static int toStableHash(@NotNull Location location) {
        return location.getBlockX() * location.getBlockY() * location.getBlockZ();
    }
}
