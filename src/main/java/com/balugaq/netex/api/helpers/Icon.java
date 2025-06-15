package com.balugaq.netex.api.helpers;

import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * @author Final_ROOT
 * @author balugaq
 */
public class Icon {
    public static final ItemStack BORDER_ICON = ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground());
    public static final ItemStack ERROR_ICON = ItemStackUtil.getCleanItem(new ItemStack(Material.BARRIER));
    public static final ItemStack RECIPE_ICON = ItemStackUtil.getCleanItem(new ItemStack(Material.PAPER));
    public static final ItemStack ERROR_BORDER =
            ItemStackUtil.getCleanItem(new CustomItemStack(Material.BARRIER, " ", " ", " ", " "));
    public static final ItemStack RECIPE_TYPE_ITEMSTACK_EXPANSION_WORKBENCH_6x6 = Theme.themedItemStack(
            Material.LAPIS_BLOCK,
            Theme.MACHINE,
            Lang.getString("icons.recipe-types.expansion_workbench_6x6.name"),
            Lang.getStringArray("icons.recipe-types.expansion_workbench_6x6.lore"));
    public static final ItemStack RECIPE_TYPE_ITEMSTACK_EXPANSION_WORKBENCH_3x3 = Theme.themedItemStack(
            Material.BAMBOO_BLOCK,
            Theme.MACHINE,
            Lang.getString("icons.recipe-types.expansion_workbench_3x3.name"),
            Lang.getStringArray("icons.recipe-types.expansion_workbench_3x3.lore"));
    public static final ItemStack RECIPE_TYPE_ITEMSTACK_QUANTUM_WORKBENCH = Theme.themedItemStack(
            Material.BRAIN_CORAL_BLOCK,
            Theme.MACHINE,
            Lang.getString("icons.recipe-types.quantum_workbench.name"),
            Lang.getStringArray("icons.recipe-types.quantum_workbench.lore"));
    public static final ItemStack OUTPUT_BACKGROUND_STACK =
            Lang.getIcon("output-background-stack", Material.GREEN_STAINED_GLASS_PANE);
    public static final ItemStack BLUEPRINT_BACKGROUND_STACK =
            Lang.getIcon("blueprint-background-stack", Material.BLUE_STAINED_GLASS_PANE);
    public static final ItemStack BLUEPRINT_BACK_STACK =
            Lang.getIcon("blueprint-back-stack", Material.BLUE_STAINED_GLASS_PANE);
    public static final ItemStack ENCODE_STACK = Lang.getIcon("encode-stack", Material.BLUE_STAINED_GLASS_PANE);
    public static final ItemStack BLANK_SLOT_STACK =
            Lang.getIcon("blank-slot-stack", Material.LIGHT_GRAY_STAINED_GLASS_PANE);
    public static final ItemStack DARK_BLANK_SLOT_STACK =
            Lang.getIcon("blank-slot-stack", Material.BLACK_STAINED_GLASS_PANE);
    public static final ItemStack PAGE_PREVIOUS_STACK =
            Lang.getIcon("page-previous-stack", Material.RED_STAINED_GLASS_PANE);
    public static final ItemStack PAGE_NEXT_STACK = Lang.getIcon("page-next-stack", Material.GREEN_STAINED_GLASS_PANE);
    public static final ItemStack CHANGE_SORT_STACK =
            Lang.getIcon("change-sort-stack", Material.BLUE_STAINED_GLASS_PANE);
    public static final ItemStack FILTER_STACK = Lang.getIcon("filter-stack", Material.NAME_TAG);
    public static final ItemStack DISPLAY_MODE_STACK = Lang.getIcon("display-mode-stack", Material.KNOWLEDGE_BOOK);
    public static final ItemStack HISTORY_MODE_STACK = Lang.getIcon("history-mode-stack", Material.BOOK);
    public static final ItemStack MINUS_ICON = Lang.getIcon("minus-icon", Material.RED_CONCRETE);
    public static final ItemStack SHOW_ICON = Lang.getIcon("show-icon", Material.GOLD_BLOCK);
    public static final ItemStack ADD_ICON = Lang.getIcon("add-icon", Material.GREEN_CONCRETE);
    public static final ItemStack TRANSPORT_MODE_ICON = Lang.getIcon("transport-mode-icon", Material.FURNACE_MINECART);
    public static final ItemStack PUSHER_TEMPLATE_BACKGROUND_STACK =
            Lang.getIcon("pusher-template-background-stack", Material.BLUE_STAINED_GLASS_PANE);
    public static final ItemStack BACKGROUND_STACK_6x6 =
            Lang.getIcon("background-stack-6x6", Material.PURPLE_STAINED_GLASS_PANE);
    public static final ItemStack BACKGROUND_STACK_3x3 =
            Lang.getIcon("background-stack-3x3", Material.WHITE_STAINED_GLASS_PANE);
    public static final ItemStack GREEDY_TEMPLATE_BACKGROUND_STACK =
            Lang.getIcon("greedy-template-background-stack", Material.GREEN_STAINED_GLASS_PANE);
    public static final ItemStack STORAGE_BACKGROUND_STACK =
            Lang.getIcon("storage-background-stack", Material.ORANGE_STAINED_GLASS_PANE);
    public static final ItemStack PURGER_TEMPLATE_BACKGROUND_STACK =
            Lang.getIcon("purger-template-background-stack", Material.GREEN_STAINED_GLASS_PANE);
    public static final ItemStack GRABBER_TEMPLATE_BACKGROUND_STACK =
            Lang.getIcon("grabber-template-background-stack", Material.BLUE_STAINED_GLASS_PANE);
    public static final ItemStack DUE_BORDER_ICON =
            Lang.getIcon("due-border-icon", Material.LIGHT_BLUE_STAINED_GLASS_PANE);
    public static final ItemStack DUE_INPUT_SPLIT_ICON =
            Lang.getIcon("due-input-split-icon", Material.YELLOW_STAINED_GLASS_PANE);
    public static final ItemStack INPUT_OUTPUT_SPLIT_ICON =
            Lang.getIcon("input-output-split-icon", Material.ORANGE_STAINED_GLASS_PANE);
    public static final ItemStack DECODE_ITEM = Lang.getIcon("decode-item", Material.KNOWLEDGE_BOOK);
    public static final ItemStack OFFSET_DECREASE_ICON = Lang.getIcon("offset-decrease-icon", Material.RED_DYE);
    public static final ItemStack OFFSET_SHOW_ICON = Lang.getIcon("offset-show-icon", Material.TARGET);
    public static final ItemStack OFFSET_INCREASE_ICON = Lang.getIcon("offset-increase-icon", Material.LIME_DYE);
    public static final ItemStack CONTROL_V_TEMPLATE_BACKGROUND_STACK =
            Lang.getIcon("control-v-template-background-stack", Material.BLUE_STAINED_GLASS_PANE);
    public static final ItemStack CONTROL_X_TEMPLATE_BACKGROUND_STACK =
            Lang.getIcon("control-x-template-background-stack", Material.BLUE_STAINED_GLASS_PANE);
    public static final ItemStack EXPORT_TEMPLATE_BACKGROUND_STACK =
            Lang.getIcon("export-template-background-stack", Material.GREEN_STAINED_GLASS_PANE);
    public static final ItemStack EXPORT_OUTPUT_BACKGROUND_STACK =
            Lang.getIcon("export-output-background-stack", Material.ORANGE_STAINED_GLASS_PANE);
    public static final ItemStack POWER_DISPLAY_EMPTY =
            Lang.getIcon("power-display-empty", Material.RED_STAINED_GLASS_PANE);
    public static final ItemStack QUANTUM_STORAGE_BACK_OUTPUT =
            Lang.getIcon("quantum-storage-back-output", Material.ORANGE_STAINED_GLASS_PANE);
    public static final ItemStack QUANTUM_STORAGE_SET_ITEM_SUPPORTING_CUSTOM_MAX =
            Lang.getIcon("quantum-storage-set-item-supporting-custom-max", Material.LIME_STAINED_GLASS_PANE);
    public static final ItemStack QUANTUM_STORAGE_SET_ITEM =
            Lang.getIcon("quantum-storage-set-item", Material.LIME_STAINED_GLASS_PANE);
    public static final ItemStack QUANTUM_STORAGE_BACK_INPUT =
            Lang.getIcon("quantum-storage-back-input", Material.GREEN_STAINED_GLASS_PANE);
    public static final ItemStack QUANTUM_STORAGE_NO_ITEM =
            Lang.getIcon("quantum-storage-no-item", Material.RED_STAINED_GLASS_PANE);
    public static final ItemStack QUANTUM_STORAGE_BACK_ITEM =
            Lang.getIcon("quantum-storage-back-item", Material.BLUE_STAINED_GLASS_PANE);
    public static final ItemStack QUANTUM_WORKBENCH_CRAFT_BUTTON_STACK =
            Lang.getIcon("quantum-workbench-craft-button-stack", Material.CRAFTING_TABLE);
    public static final ItemStack RECEIVED_BACKGROUND_STACK =
            Lang.getIcon("received-background-stack", Material.GREEN_STAINED_GLASS_PANE);
    public static final ItemStack TRANSMITTER_TEMPLATE_BACKGROUND_STACK =
            Lang.getIcon("transmitter-template-background-stack", Material.GREEN_STAINED_GLASS_PANE);
    public static final ItemStack QUICK_INPUT = Lang.getIcon("quick-input", Material.PINK_STAINED_GLASS_PANE);
    public static final ItemStack QUICK_OUTPUT = Lang.getIcon("quick-output", Material.RED_STAINED_GLASS_PANE);
    public static final ItemStack CRAFT_BUTTON = Lang.getIcon("craft-button", Material.CRAFTING_TABLE);
    public static final ItemStack CRAFT_BUTTON_STACK_6x6 =
            Lang.getIcon("craft-button-stack-6x6", Material.KNOWLEDGE_BOOK);
    public static final ItemStack CRAFT_BUTTON_STACK_3x3 = Lang.getIcon("craft-button-stack-3x3", Material.BOOK);
    public static final ItemStack ACTION_BUTTON = Lang.getIcon("action-button", Material.REDSTONE_TORCH);
    public static final ItemStack CRAFT_BUTTON_NEW_STYLE =
            Lang.getIcon("craft-button-new-style", Material.CRAFTING_TABLE);

    public static final ItemStack UNKNOWN_ITEM = Lang.getIcon("unknown-item", Material.BARRIER);

    public static final ItemStack ITEM_FLOW_VIEWER_BACK_TO_MAIN =
            Lang.getIcon("item-flow-viewer-back-to-main", Material.RED_STAINED_GLASS_PANE);

    public static final ItemStack ITEM_FLOW_VIEWER_FORCE_CLEAN =
            Lang.getIcon("item-flow-viewer-force-clean", Material.YELLOW_STAINED_GLASS_PANE);

    public static final ItemStack JEG_BUTTON = Lang.getIcon("jeg-button", Material.KNOWLEDGE_BOOK);

    public static final ItemStack FILTER_MODE_BLACK_LIST = Lang.getIcon("filter-mode-black-list", Material.BLACK_WOOL);

    public static final ItemStack FILTER_MODE_WHITE_LIST = Lang.getIcon("filter-mode-white-list", Material.WHITE_WOOL);

    public static final ItemStack MATCH_MODE_ALL_MATCH = Lang.getIcon("match-mode-all-match", Material.FILLED_MAP);

    public static final ItemStack MATCH_MODE_MATERIAL_MATCH = Lang.getIcon("match-mode-material-match", Material.MAP);

    public static final ItemStack ADVANCED_VACUUM_SPLIT_BLOCK =
            Lang.getIcon("advanced-vacuum-split-block", Material.CYAN_STAINED_GLASS_PANE);

    // pages are 1-based
    @SuppressWarnings("deprecation")
    @NotNull public static ItemStack getPageStack(@NotNull ItemStack origin, int currentPage, int maxPage) {
        ItemStack clone = origin.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta != null) {
            List<String> lore = Optional.ofNullable(meta.getLore()).orElse(new ArrayList<>());
            lore.add(TextUtil.GRAY + Lang.getString("messages.normal-operation.common.page") + " " + TextUtil.GREEN
                    + currentPage + " / " + maxPage);
            meta.setLore(lore);
            clone.setItemMeta(meta);
        }
        return clone;
    }
}
