package com.balugaq.netex.api.helpers;

import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.utils.GhostItems;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedEnchantment;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public static final ItemStack RECIPE_TYPE_ITEMSTACK_EXPANSION_WORKBENCH_3x3 = Theme.themedItemStack(
        Material.BAMBOO_BLOCK,
        Theme.MACHINE,
        Lang.getString("icons.recipe-types.expansion_workbench_3x3.name"),
        Lang.getStringArray("icons.recipe-types.expansion_workbench_3x3.lore"));
    public static final ItemStack RECIPE_TYPE_ITEMSTACK_QUANTUM_WORKBENCH = Theme.themedItemStack(
        Material.DRIED_KELP_BLOCK,
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
    public static final ItemStack ITEM_FLOW_VIEWER_SORT =
        Lang.getIcon("item-flow-viewer-sort", Material.BLUE_STAINED_GLASS_PANE);

    public static final ItemStack JEG_BUTTON = Lang.getIcon("jeg-button", Material.KNOWLEDGE_BOOK);

    public static final ItemStack FILTER_MODE_BLACK_LIST = Lang.getIcon("filter-mode-black-list", Material.BLACK_WOOL);

    public static final ItemStack FILTER_MODE_WHITE_LIST = Lang.getIcon("filter-mode-white-list", Material.WHITE_WOOL);

    public static final ItemStack MATCH_MODE_ALL_MATCH = Lang.getIcon("match-mode-all-match", Material.FILLED_MAP);

    public static final ItemStack MATCH_MODE_MATERIAL_MATCH = Lang.getIcon("match-mode-material-match", Material.MAP);

    public static final ItemStack ADVANCED_VACUUM_SPLIT_BLOCK =
        Lang.getIcon("advanced-vacuum-split-block", Material.CYAN_STAINED_GLASS_PANE);

    public static final ItemStack BLUE_BACKGROUND =
        Lang.getIcon("blue-background", Material.BLUE_STAINED_GLASS_PANE);

    public static final ItemStack KEYBIND_SETTINGS =
        Lang.getIcon("keybind-settings", Material.CLOCK);

    public static final ItemStack LIGHT_GRAY_BACKGROUND =
        Lang.getIcon("light-gray-background", Material.LIGHT_GRAY_STAINED_GLASS_PANE);

    public static final ItemStack YELLOW_BORDER =
        Lang.getIcon("yellow-border", Material.YELLOW_STAINED_GLASS_PANE);

    public static final ItemStack UPLOAD_KEYBIND_SCRIPT =
        Lang.getIcon("upload-keybind-script", Material.PAPER);

    public static final ItemStack GRAY_BACKGROUND =
        Lang.getIcon("gray-background", Material.GRAY_STAINED_GLASS_PANE);

    public static final ItemStack SCRIPT_CENTER =
        Lang.getIcon("script-center", Material.COMPASS);

    public static final ItemStack MATCH_OPTION_SETTINGS =
        Lang.getIcon("match-option-settings", Material.COMPASS);

    public static final ItemStack MATCH_OPTION_CLOSE =
        Lang.getIcon("match-option-close", Material.RED_CONCRETE);

    public static final ItemStack RETURN_INGREDIENT =
        Lang.getIcon("return-ingredient", Material.FLOWER_POT);

    public static final ItemStack RETURN_OUTPUT =
        Lang.getIcon("return-output", Material.DECORATED_POT);

    public static final ItemStack CRAFT =
        Lang.getIcon("craft", Material.CRAFTING_TABLE);

    public static final ItemStack DIFF_BUTTON =
        Lang.getIcon("diff-button", Material.COMPASS);
    public static final ItemStack DIFF_RESULT_ICON =
        Lang.getIcon("diff-result-icon", Material.LIGHT);
    public static final ItemStack STORAGE_CONVERT_BUTTON =
        Lang.getIcon("storage-convert-button", Material.MUSIC_DISC_BLOCKS);

    public static final ItemStack FACING_PRESETTER_HELP_ICON =
        Lang.getIcon("facing-presetter-help-icon", Material.KNOWLEDGE_BOOK);

    public static final ItemStack ITEM_TARGET_DESC_STACK =
        Lang.getIcon("item-target-desc-stack", Material.ORANGE_STAINED_GLASS_PANE);

    public static final ItemStack LINKER_TYPE_HYPER_LINK =
        Lang.getIcon("linker-type-hyper-link", Material.NETHER_STAR);

    public static final ItemStack LINKER_TYPE_QUANTUM_LINK =
        Lang.getIcon("linker-type-quantum-link", Material.NETHER_STAR);

    public static final ItemStack LINKER_TARGET_SWITCH =
        Lang.getIcon("linker-target-switch", Material.TARGET);
    public static final ItemStack BACK = of("cellnet-back-button", Material.BARRIER);
    public static final ItemStack DRIVE_BROWSE = of("cellnet-drive-browse", Material.CHEST);
    public static final ItemStack DRIVE_WHITELIST_ADD = of("cellnet-drive-whitelist-add", Material.NAME_TAG);
    public static final ItemStack ASSEMBLY_SLOT_MARKER = of("cellnet-assembly-slot-marker", Material.LIME_STAINED_GLASS_PANE);
    public static final ItemStack ASSEMBLY_UPGRADE_SLOT = of("cellnet-assembly-upgrade-slot", Material.REDSTONE);
    public static final ItemStack ASSEMBLY_SMART_SLOT = of("cellnet-assembly-smart-slot", Material.AMETHYST_SHARD);
    public static final ItemStack ASSEMBLY_SWITCH_ON = of("cellnet-assembly-switch-on", Material.LIME_STAINED_GLASS_PANE);
    public static final ItemStack ASSEMBLY_SWITCH_OFF = of("cellnet-assembly-switch-off", Material.GRAY_STAINED_GLASS_PANE);
    public static final ItemStack ASSEMBLY_TASK_EMPTY = glint(of("cellnet-assembly-task-empty", Material.BLUE_STAINED_GLASS_PANE));
    public static final ItemStack CARD_SAVE = of("cellnet-assemblycard-save", Material.LIME_DYE);
    public static final ItemStack CARD_CLEAR = of("cellnet-assemblycard-clear", Material.GRAY_DYE);
    public static final ItemStack CARD_ARROW = of("cellnet-assemblycard-arrow", Material.ARROW);
    public static final ItemStack CARD_HINT = of("cellnet-assemblycard-hint", Material.BOOK);
    public static final ItemStack VOID_INFO = of("cellnet-void-info", Material.BOOK);
    public static final ItemStack VOID_CLOSE = of("cellnet-void-close", Material.BARRIER);
    public static final ItemStack CLEANER_CELL_SLOT = of("cellnet-cleaner-cell-slot", Material.LIGHT_GRAY_STAINED_GLASS_PANE);
    public static final ItemStack CLEANER_DISPLAY = of("cellnet-display-placeholder", Material.GREEN_STAINED_GLASS_PANE);
    public static final ItemStack CONVERTER_BORDER = of("cellnet-converter-border", Material.BLUE_STAINED_GLASS_PANE);
    public static final ItemStack CONVERTER_CELL_SLOT = of("cellnet-converter-cell-slot", Material.LIGHT_GRAY_STAINED_GLASS_PANE);
    public static final ItemStack CONVERTER_CLOSE = of("cellnet-converter-close", Material.BARRIER);
    public static final ItemStack CELL_SETTING_SLOT = of("cellnet-setting-slot", Material.GREEN_STAINED_GLASS_PANE);
    public static final ItemStack SEARCH_EMPTY = of("cellnet-search-empty", Material.BARRIER);
    public static final ItemStack ASSEMBLER_TEMPLATE_SLOT = of("cellnet-template-slot-marker", Material.LIGHT_BLUE_STAINED_GLASS_PANE);
    public static final ItemStack ASSEMBLER_STATUS = of("cellnet-assembler-status", Material.OBSERVER);
    public static final ItemStack ASSEMBLER_PAUSE = of("cellnet-assembler-pause", Material.GRAY_DYE);
    public static final ItemStack ASSEMBLER_RESUME = of("cellnet-assembler-resume", Material.LIME_DYE);
    public static final ItemStack ASSEMBLER_BOX_OFF = of("cellnet-assembler-box-off", Material.GRAY_DYE);
    public static final ItemStack ASSEMBLER_BOX_ON = of("cellnet-assembler-box-on", Material.LIME_DYE);
    public static final ItemStack ASSEMBLY_DRIVE_CLEAR_SLOTS = of("cellnet-assembly-drive-clear-slots", Material.LAVA_BUCKET);
    public static final ItemStack ASSEMBLY_WORKSHOP_ENCODE = of("cellnet-workshop-encode", Material.ANVIL);
    public static final ItemStack ASSEMBLY_WORKSHOP_CLEAR = of("cellnet-workshop-clear", Material.LAVA_BUCKET);
    public static final ItemStack CONVERTER_MODE_EXPORT = of("cellnet-converter-mode-export", Material.REPEATER);
    public static final ItemStack CONVERTER_MODE_IMPORT = of("cellnet-converter-mode-import", Material.REPEATER);
    public static final ItemStack CONVERTER_UPGRADE = of("cellnet-converter-upgrade", Material.NETHER_STAR);
    public static final ItemStack CELL_TOGGLE_ON = of("cellnet-cell-toggle-on", Material.GREEN_DYE);
    public static final ItemStack CELL_TOGGLE_OFF = of("cellnet-cell-toggle-off", Material.RED_DYE);
    public static final ItemStack CELL_UPGRADE_MAX = of("cellnet-cell-upgrade-max", Material.BARRIER);
    public static final ItemStack PAGE_ARROW = of("cellnet-page-arrow", Material.ARROW);
    public static final ItemStack SEARCH_ICON = of("cellnet-search-icon", Material.SPYGLASS);
    public static final ItemStack PREVIEW_FILL = of("cellnet-preview-fill", Material.WHITE_STAINED_GLASS_PANE);
    public static final ItemStack LINE_MODULE_SLOT = of("cellnet-line-module-slot", Material.LIME_STAINED_GLASS_PANE);
    public static final ItemStack LINE_TEMPLATE_LOCKED = of("cellnet-line-template-locked", Material.RED_STAINED_GLASS_PANE);
    public static final ItemStack LINE_DISTANCE = of("cellnet-line-distance", Material.RECOVERY_COMPASS);
    public static final ItemStack LINE_MODE_ON = of("cellnet-line-mode-on", Material.REPEATER);
    public static final ItemStack LINE_MODE_OFF = of("cellnet-line-mode-off", Material.GRAY_DYE);
    public static final ItemStack LINE_DIRECTION_ON = of("cellnet-line-direction-on", Material.GREEN_STAINED_GLASS_PANE);
    public static final ItemStack LINE_DIRECTION_OFF = of("cellnet-line-direction-off", Material.BLUE_STAINED_GLASS_PANE);
    public static final ItemStack LINE_DIRECTION_FALLBACK = of("cellnet-line-direction-fallback", Material.COMPASS);
    public static final ItemStack CHAIN_BINDING_ENTRY = of("cellnet-chain-binding-entry", Material.LEAD);
    public static final ItemStack CHAIN_BINDING_ENTRY_LOCKED = of("cellnet-chain-binding-entry-locked", Material.GRAY_DYE);
    public static final ItemStack CHAIN_BINDING_INFO = of("cellnet-chain-binding-info", Material.BOOK);
    public static final ItemStack CHAIN_BINDING_MODE_ON = glint(of("cellnet-chain-binding-mode-on", Material.REPEATER));

    static {
        GhostItems.mark(BACK);
        GhostItems.mark(CLEANER_DISPLAY);
        GhostItems.mark(SEARCH_EMPTY);
        GhostItems.mark(PREVIEW_FILL);
    }

    // pages are 1-based
    @SuppressWarnings("deprecation")
    @NotNull
    public static ItemStack getPageStack(@NotNull ItemStack origin, int currentPage, int maxPage) {
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

    @NotNull
    private static ItemStack of(@NotNull String key, @NotNull Material fallback) {
        ItemStack icon = Lang.getIcon(key, fallback);
        return icon != null ? icon : new ItemStack(fallback);
    }

    @NotNull
    private static ItemStack glint(@NotNull ItemStack icon) {
        ItemStack glinted = icon.clone();
        glinted.editMeta(meta -> {
            meta.addEnchant(VersionedEnchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        });
        return glinted;
    }
}
