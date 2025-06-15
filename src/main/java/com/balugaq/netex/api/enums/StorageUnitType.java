package com.balugaq.netex.api.enums;

import org.jetbrains.annotations.Nullable;

public enum StorageUnitType {
    TINY(32, 256),
    MINI(32, 1024),
    SMALL(32, 4096),
    MEDIUM(32, 32768),
    LARGE(32, 262144),
    ENHANCED(32, 2097152),
    ADVANCED(32, 4194304),
    EXTRA(32, 8388608),
    ULTRA(32, 16777216),
    END_GAME_BASIC(32, 33554432),
    END_GAME_INTERMEDIATE(32, 134217728),
    END_GAME_ADVANCED(35, 1073741824),
    END_GAME_MAX(35, Integer.MAX_VALUE);

    private final int maxItemStored;
    private final int maxStoredAmountEach;

    StorageUnitType(int maxItem, int maxEachAmount) {
        this.maxItemStored = maxItem;
        this.maxStoredAmountEach = maxEachAmount;
    }

    public int getMaxItemCount() {
        return maxItemStored;
    }

    public int getEachMaxSize() {
        return maxStoredAmountEach;
    }

    @Nullable public StorageUnitType next() {
        int index = this.ordinal() + 1;
        if (index >= values().length) {
            return null;
        }
        return values()[index];
    }

    @Nullable public StorageUnitType previous() {
        int index = this.ordinal() - 1;
        if (index < 0) {
            return null;
        }
        return values()[index];
    }
}
