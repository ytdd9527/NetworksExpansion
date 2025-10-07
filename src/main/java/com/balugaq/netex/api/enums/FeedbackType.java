package com.balugaq.netex.api.enums;

import com.balugaq.netex.utils.Lang;
import io.github.sefiraat.networks.utils.Keys;
import lombok.Getter;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

@Getter
public enum FeedbackType implements Keyed {
    AFK,
    ALREADY_HAS_ITEM,
    BLOCK_ALREADY_CUT,
    BLOCK_ALREADY_PASTED,
    BLOCK_CANNOT_BE_AIR,
    BLOCK_CANNOT_BE_CUT,
    BLOCK_NOT_MATCH_TEMPLATE,
    CANNOT_OUTPUT_ENERGY,
    DISABLED_BLUEPRINT,
    DISABLED_OUTPUT,
    ERROR_OCCURRED,
    FULL_ENERGY_BUFFER,
    INITIALIZATION,
    INVALID_BLOCK,
    INVALID_BLUEPRINT,
    INVALID_RECIPE,
    INVALID_TEMPLATE,
    LOADING_DATA,
    NO_BLUEPRINT_FOUND,
    NO_BLUEPRINT_INSTANCE_FOUND,
    NO_DIRECTION_SET,
    NO_ENOUGH_SPACE,
    NO_INPUT,
    NO_INVENTORY_FOUND,
    NO_ITEM_FOUND,
    NO_ITEM_REQUEST,
    NO_LINKED_BLOCK_MENU_FOUND,
    NO_LINKED_LOCATION_FOUND,
    NO_NETWORK_FOUND,
    NO_OWNER_FOUND,
    NO_PERMISSION,
    NO_TARGET_BLOCK,
    NO_TEMPLATE_FOUND,
    NO_VANILLA_RECIPE_FOUND,
    NO_VALID_RECIPE_FOUND,
    NOT_ALLOWED_BLOCK,
    NOT_BLUEPRINT,
    NOT_ENOUGH_ITEMS_IN_NETWORK,
    NOT_ENOUGH_POWER,
    NOT_ENOUGH_RESOURCES,
    OUTPUT_FULL,
    PROTECTED_BLOCK,
    RESULT_IS_TOO_LARGE,
    SUCCESS,
    WORKING,
    NOT_ALLOWED_ITEM,
    SOFT_CELL_BANNED,
    TOO_MANY_ENTITIES,
    TICKING,
    TRANSFER_TICKING,
    TRANSFER_TRY_PUSH_ITEM_WITH_COUNTER,
    TRANSFER_TRY_PUSH_ITEM,
    TRANSFER_TRY_GRAB_ITEM_WITH_COUNTER,
    TRANSFER_TRY_GRAB_ITEM,
    ROOT_REQUEST_0,
    ROOT_LIMITING_ACCESS_OUTPUT,
    ROOT_LIMITING_ACCESS_INPUT,
    NO_TARGET_LOCATION,
    NO_TARGET_NETWORK_FOUND,
    SAME_NETWORK,
    NO_MENU;

    private final @NotNull NamespacedKey key;
    private final @NotNull String message;

    FeedbackType() {
        this.key = Keys.newKey(name().toLowerCase());
        this.message = Lang.getString("messages.feedback." + this.key.getKey());
    }

    FeedbackType(@NotNull String key) {
        this.key = Keys.newKey(key);
        this.message = Lang.getString("messages.feedback." + this.key.getKey());
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return this.key;
    }
}
