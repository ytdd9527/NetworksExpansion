package com.balugaq.netex.api.enums;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NullMarked;

/**
 * Minecraft versions for version judgement
 *
 * @author lijinhong11
 * @author balugaq
 */
@NullMarked
public record MinecraftVersion(int major, int minor, int patch) implements Comparable<MinecraftVersion> {
    public static final MinecraftVersion UNKNOWN = new MinecraftVersion();
    public static final MinecraftVersion V1_20 = MinecraftVersion.of(1, 20);
    public static final MinecraftVersion V1_20_5 = MinecraftVersion.of(1, 20, 5);
    public static final MinecraftVersion V1_21 = MinecraftVersion.of(1, 21);
    public static final MinecraftVersion V1_21_3 = MinecraftVersion.of(1, 21, 3);
    public static final MinecraftVersion V1_21_4 = MinecraftVersion.of(1, 21, 4);
    public static final MinecraftVersion V1_21_5 = MinecraftVersion.of(1, 21, 5);

    public MinecraftVersion() {
        this(999, 999, 999);
    }

    public static MinecraftVersion current() {
        return MinecraftVersion.of(Bukkit.getMinecraftVersion());
    }

    public static MinecraftVersion of(String version) {
        return deserialize(version);
    }

    public boolean isAtLeast(String version) {
        return isAtLeast(of(version));
    }

    public boolean isAtLeast(MinecraftVersion version) {
        return this.major > version.major ||
            this.major == version.major && this.minor > version.minor ||
            this.major == version.major && this.minor == version.minor && this.patch >= version.patch;
    }

    public boolean isBefore(String version) {
        return isBefore(of(version));
    }

    public boolean isBefore(MinecraftVersion version) {
        return !isAtLeast(version) && this != version;
    }

    public static MinecraftVersion deserialize(String s) {
        String[] split = s.split("\\.");
        if (split.length < 2) throw new IllegalArgumentException("Invalid version string: " + s);
        int major = Integer.parseUnsignedInt(split[0]);
        int minor = Integer.parseUnsignedInt(split[1]);
        int patch = split.length == 2 ? 0 : Integer.parseUnsignedInt(split[2]);
        return MinecraftVersion.of(major, minor, patch);
    }

    public static MinecraftVersion of(int major, int minor, int patch) {
        return new MinecraftVersion(major, minor, patch);
    }

    public static MinecraftVersion of(int major, int minor) {
        return of(major, minor, 0);
    }

    public static int toNumber(MinecraftVersion version) {
        return version.major * 1000000 + version.minor * 10000 + version.patch;
    }

    @Override
    public int compareTo(MinecraftVersion o) {
        return Integer.compare(toNumber(this), toNumber(o));
    }

    @Override
    public int hashCode() {
        return toNumber(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MinecraftVersion other)) return false;
        return compareTo(other) == 0;
    }

    public String humanize() {
        if (patch != 0) {
            return major + "." + minor + "." + patch;
        }
        return major + "." + minor;
    }
}
