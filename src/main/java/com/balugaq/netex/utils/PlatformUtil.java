/*
 * Copyright (c) 2024-2026 balugaq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.balugaq.netex.utils;

import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
@UtilityClass
@NullMarked
public class PlatformUtil {
    @Getter
    private static final boolean bukkit;
    @Getter
    private static final boolean spigot;
    @Getter
    private static final boolean paper;
    @Getter
    private static final boolean pufferfish;
    @Getter
    private static final boolean purpur;
    @Getter
    private static final boolean folia;
    @Getter
    private static final boolean leaf;
    @Getter
    private static final boolean leaves;

    static {
        bukkit = true;
        spigot = PaperLib.isSpigot(); // > bukkit
        paper = PaperLib.isPaper(); // > spigot
        pufferfish = classExist("gg.pufferfish.pufferfish.simd.SIMDChecker"); // > paper
        purpur = classExist("org.purpurmc.purpur.util.permissions.PurpurPermissions"); // > pufferfish
        folia = classExist("io.papermc.paper.threadedregions.RegionizedServer"); // > paper
        leaf = classExist("org.leavesmc.leaves.entity.photographer.PhotographerManager"); // > paper
        leaves = classExist("org.leavesmc.leaves.config.LeavesConfigValue"); // > paper
    }

    public static boolean classExist(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("EmptyMethod")
    public static void initialize() {
    }
}