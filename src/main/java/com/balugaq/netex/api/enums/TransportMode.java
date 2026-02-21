package com.balugaq.netex.api.enums;

import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import org.jetbrains.annotations.NotNull;

public enum TransportMode {
    NONE,
    NULL_ONLY,
    NONNULL_ONLY,
    FIRST_ONLY,
    LAST_ONLY,
    FIRST_STOP,
    LAZY,
    VOID,
    SPECIFIED_QUANTITY;

    public @NotNull String getName() {
        return TextUtil.colorRandomString(getRawName());
    }

    public @NotNull String getRawName() {
        return switch (this) {
            case NONE -> Lang.getString("icons.transport_mode.none");
            case NULL_ONLY -> Lang.getString("icons.transport_mode.null_only");
            case NONNULL_ONLY -> Lang.getString("icons.transport_mode.nonnull_only");
            case FIRST_ONLY -> Lang.getString("icons.transport_mode.first_only");
            case LAST_ONLY -> Lang.getString("icons.transport_mode.last_only");
            case FIRST_STOP -> Lang.getString("icons.transport_mode.first_stop");
            case LAZY -> Lang.getString("icons.transport_mode.lazy");
            case VOID -> Lang.getString("icons.transport_mode.void");
            case SPECIFIED_QUANTITY -> Lang.getString("icons.transport_mode.specified_quantity");
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
