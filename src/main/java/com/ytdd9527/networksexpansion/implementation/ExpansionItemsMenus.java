package com.ytdd9527.networksexpansion.implementation;

import com.balugaq.netex.api.groups.MainItemGroup;
import com.balugaq.netex.api.groups.SubFlexItemGroup;
import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.utils.GroupConfigUtil;
import io.github.sefiraat.networks.utils.Keys;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Final_ROOT
 */
public final class ExpansionItemsMenus {
    /* Slimefun item group */
    public static final NestedItemGroup MAIN_MENU =
            new NestedItemGroup(
                    getKey("NTW_EXPANSION_CATEGORY_MAIN"),
                    new CustomItemStack(
                            Material.CHEST_MINECART, Lang.getString("groups.expansion.custom.main_item_group"))) {
                @Override
                public boolean isVisible(
                        @NotNull Player p, @NotNull PlayerProfile profile, @NotNull SlimefunGuideMode mode) {
                    return false;
                }
            };

    public static final SubItemGroup MENU_ITEMS = new SubItemGroup(
            getKey("NTW_EXPANSION_ITEMS"),
            MAIN_MENU,
            new CustomItemStack(Material.AMETHYST_SHARD, Lang.getString("groups.expansion.slimefun.items")));
    public static final SubItemGroup MENU_CARGO_SYSTEM = new SubItemGroup(
            getKey("NTW_EXPANSION_SYSTEM"),
            MAIN_MENU,
            new CustomItemStack(Material.FURNACE_MINECART, Lang.getString("groups.expansion.slimefun.cargo_system")));
    public static final SubItemGroup MENU_FUNCTIONAL_MACHINE = new SubItemGroup(
            getKey("NTW_EXPANSION_FUNCTIONAL_MACHINE"),
            MAIN_MENU,
            new CustomItemStack(Material.LECTERN, Lang.getString("groups.expansion.slimefun.functional_machine")));
    public static final SubItemGroup MENU_TROPHY = new SubItemGroup(
            getKey("NTW_EXPANSION_TROPHY"),
            MAIN_MENU,
            new CustomItemStack(Material.RAW_GOLD_BLOCK, Lang.getString("groups.expansion.slimefun.trophy")));

    /* My item group */
    public static final MainItemGroup MAIN_ITEM_GROUP = GroupConfigUtil.getMainItemGroup(
            "NTW_EXPANSION_ITEM_GROUP",
            Material.CHEST_MINECART,
            Lang.getString("groups.expansion.custom.main_item_group"));
    // item
    public static final SubFlexItemGroup MAIN_MENU_ITEM = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_MAIN_MENU_ITEM",
            Material.AMETHYST_SHARD,
            Lang.getString("groups.expansion.custom.main_menu_item"));
    public static final SubFlexItemGroup SUB_MENU_TOOL = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_SUB_MENU_TOOL", Material.SPYGLASS, Lang.getString("groups.expansion.custom.sub_menu_tool"));
    public static final SubFlexItemGroup SUB_MENU_BLUEPRINT = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_SUB_MENU_BLUEPRINT",
            Material.BLUE_DYE,
            Lang.getString("groups.expansion.custom.sub_menu_blueprint"));

    // cargo system
    public static final SubFlexItemGroup MAIN_MENU_CARGO_SYSTEM = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_MAIN_MENU_CARGO_SYSTEM",
            Material.FURNACE_MINECART,
            Lang.getString("groups.expansion.custom.main_menu_cargo_system"));
    public static final SubFlexItemGroup SUB_MENU_ADVANCED_STORAGE = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_SUB_MENU_ADVANCED_STORAGE",
            Material.BOOKSHELF,
            Lang.getString("groups.expansion.custom.sub_menu_advanced_storage"));
    public static final SubFlexItemGroup SUB_MENU_NETWORKS_DRAWERS = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_SUB_MENU_NETWORKS_DRAWERS",
            Material.CHISELED_BOOKSHELF,
            Lang.getString("groups.expansion.custom.sub_menu_networks_drawers"));
    public static final SubFlexItemGroup SUB_MENU_CARGO = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_SUB_MENU_CARGO", Material.END_ROD, Lang.getString("groups.expansion.custom.sub_menu_cargo"));

    // functional machine
    public static final SubFlexItemGroup MAIN_MENU_FUNCTIONAL_MACHINE = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_MAIN_MENU_FUNCTIONAL_MACHINE",
            Material.LECTERN,
            Lang.getString("groups.expansion.custom.main_menu_functional_machine"));
    public static final SubFlexItemGroup SUB_MENU_CORE_MACHINE = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_SUB_MENU_CORE_MACHINE",
            Material.AMETHYST_BLOCK,
            Lang.getString("groups.expansion.custom.sub_menu_core_machine"));
    public static final SubFlexItemGroup SUB_MENU_ADVANCED_NETWORKS = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_SUB_ADVANCED_NETWORKS",
            Material.BLACK_STAINED_GLASS,
            Lang.getString("groups.expansion.custom.sub_menu_advanced_networks"));
    public static final SubFlexItemGroup SUB_MENU_BRIDGE = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_SUB_MENU_BRIDGE",
            Material.WHITE_STAINED_GLASS,
            Lang.getString("groups.expansion.custom.sub_menu_bridge"));
    public static final SubFlexItemGroup SUB_MENU_ENCODER = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_SUB_MENU_ENCODER",
            Material.TARGET,
            Lang.getString("groups.expansion.custom.sub_menu_encoder"));
    public static final SubFlexItemGroup SUB_MENU_CRAFTER_MACHINE = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_SUB_MENU_CRAFTER_MACHINE",
            Material.CRAFTING_TABLE,
            Lang.getString("groups.expansion.custom.sub_menu_crafter_machine"));

    // trophy
    public static final SubFlexItemGroup MAIN_MENU_TROPHY = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_MAIN_MENU_TROPHY",
            Material.RAW_GOLD_BLOCK,
            Lang.getString("groups.expansion.custom.main_menu_trophy"));
    public static final SubFlexItemGroup SUB_MENU_AUTHOR = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_SUB_MENU_AUTHOR",
            Material.PLAYER_HEAD,
            Lang.getString("groups.expansion.custom.sub_menu_author"));
    public static final SubFlexItemGroup SUB_MENU_ANNOUNCE = GroupConfigUtil.getSubFlexItemGroup(
            "NTW_EXPANSION_SUB_MENU_ANNOUNCE",
            Material.KNOWLEDGE_BOOK,
            Lang.getString("groups.expansion.custom.sub_menu_announce"));

    private static @NotNull NamespacedKey getKey(@NotNull String key) {
        return Keys.newKey(key);
    }
}
