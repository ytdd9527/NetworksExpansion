package com.balugaq.netex.api.enums;

import com.ytdd9527.networksexpansion.utils.TextUtil;

public enum TransportMode {
    NONE,
    NULL_ONLY,
    NONNULL_ONLY,
    FIRST_ONLY,
    LAST_ONLY,
    FIRST_STOP,
    LAZY;

    public String getName() {
        return TextUtil.colorRandomString(getRawName());
    }

    public String getRawName() {
        return switch (this) {
            case NONE -> "无限制";
            case NULL_ONLY -> "仅空";
            case NONNULL_ONLY -> "仅非空";
            case FIRST_ONLY -> "仅首位";
            case LAST_ONLY -> "仅末位";
            case FIRST_STOP -> "首位阻断";
            case LAZY -> "懒惰模式";
        };
    }

    public TransportMode next() {
        int index = this.ordinal() + 1;
        if (index >= TransportMode.values().length) {
            index = 0;
        }
        return TransportMode.values()[index];
    }

    public TransportMode previous() {
        int index = this.ordinal() - 1;
        if (index < 0) {
            index = TransportMode.values().length - 1;
        }
        return TransportMode.values()[index];
    }
}
