package com.balugaq.netex.api.helpers;


import com.ytdd9527.networksexpansion.utils.TextUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


/**
 * @author Final_ROOT
 */
public class Icon {
    public static final ItemStack BORDER_ICON = ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground());
    public static final ItemStack ERROR_ICON = ItemStackUtil.getCleanItem(new ItemStack(Material.BARRIER));
    public static final ItemStack RECIPE_ICON = ItemStackUtil.getCleanItem(new ItemStack(Material.PAPER));
    public static final ItemStack ERROR_BORDER = ItemStackUtil.getCleanItem(new CustomItemStack(Material.BARRIER, " ", " ", " ", " "));
    public static final ItemStack RECIPE_TYPE_ITEMSTACK_EXPANSION_WORKBENCH_6x6 = Theme.themedItemStack(
            Material.LAPIS_BLOCK,
            Theme.MACHINE,
            Networks.getLocalizationService().getString("icons.recipe-types.expansion_workbench_6x6.name"),
            Networks.getLocalizationService().getString("icons.recipe-types.expansion_workbench_6x6.lore")
    );
    public static final ItemStack RECIPE_TYPE_ITEMSTACK_EXPANSION_WORKBENCH_3x3 = Theme.themedItemStack(
            Material.BAMBOO_BLOCK,
            Theme.MACHINE,
            Networks.getLocalizationService().getString("icons.recipe-types.expansion_workbench_3x3.name"),
            Networks.getLocalizationService().getString("icons.recipe-types.expansion_workbench_3x3.lore")
    );
    public static final ItemStack RECIPE_TYPE_ITEMSTACK_QUANTUM_WORKBENCH = Theme.themedItemStack(
            Material.BRAIN_CORAL_BLOCK,
            Theme.MACHINE,
            Networks.getLocalizationService().getString("icons.recipe-types.quantum_workbench.name"),
            Networks.getLocalizationService().getString("icons.recipe-types.quantum_workbench.lore")
    );
    public static final ItemStack OUTPUT_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "output-background-stack",
            Material.GREEN_STAINED_GLASS_PANE 
    );
    public static final ItemStack BLUEPRINT_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "blueprint-background-stack",
            Material.BLUE_STAINED_GLASS_PANE 
    );
    public static final ItemStack BLUEPRINT_BACK_STACK = Networks.getLocalizationService().getIcon(
            "blueprint-back-stack",
            Material.BLUE_STAINED_GLASS_PANE 
    );
    public static final ItemStack ENCODE_STACK = Networks.getLocalizationService().getIcon(
            "encode-stack",
            Material.BLUE_STAINED_GLASS_PANE 
    );
    public static final ItemStack BLANK_SLOT_STACK = Networks.getLocalizationService().getIcon(
            "blank-slot-stack",
            Material.LIGHT_GRAY_STAINED_GLASS_PANE 
    );
    public static final ItemStack PAGE_PREVIOUS_STACK = Networks.getLocalizationService().getIcon(
            "page-previous-stack",
            Material.RED_STAINED_GLASS_PANE 
    );
    public static final ItemStack PAGE_NEXT_STACK = Networks.getLocalizationService().getIcon(
            "page-next-stack",
            Material.GREEN_STAINED_GLASS_PANE 
    );
    public static final ItemStack CHANGE_SORT_STACK = Networks.getLocalizationService().getIcon(
            "change-sort-stack",
            Material.BLUE_STAINED_GLASS_PANE 
    );
    public static final ItemStack FILTER_STACK = Networks.getLocalizationService().getIcon(
            "filter-stack",
            Material.NAME_TAG 
    );
    public static final ItemStack DISPLAY_MODE_STACK = Networks.getLocalizationService().getIcon(
            "display-mode-stack",
            Material.KNOWLEDGE_BOOK 
    );
    public static final ItemStack HISTORY_MODE_STACK = Networks.getLocalizationService().getIcon(
            "history-mode-stack",
            Material.BOOK 
    );
    public static final ItemStack MINUS_ICON = Networks.getLocalizationService().getIcon(
            "minus-icon",
            Material.RED_CONCRETE 
    );
    public static final ItemStack SHOW_ICON = Networks.getLocalizationService().getIcon(
            "show-icon",
            Material.GOLD_BLOCK 
    );
    public static final ItemStack ADD_ICON = Networks.getLocalizationService().getIcon(
            "add-icon",
            Material.GREEN_CONCRETE 
    );
    public static final ItemStack TRANSPORT_MODE_ICON = Networks.getLocalizationService().getIcon(
            "transport-mode-icon",
            Material.FURNACE_MINECART 
    );
    public static final ItemStack PUSHER_TEMPLATE_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "pusher-template-background-stack",
            Material.BLUE_STAINED_GLASS_PANE 
    );
    public static final ItemStack BACKGROUND_STACK_6x6 = Networks.getLocalizationService().getIcon(
            "background-stack-6x6",
            Material.PURPLE_STAINED_GLASS_PANE 
    );
    public static final ItemStack BACKGROUND_STACK_3x3 = Networks.getLocalizationService().getIcon(
            "background-stack-3x3",
            Material.WHITE_STAINED_GLASS_PANE 
    );
    public static final ItemStack GREEDY_TEMPLATE_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "greedy-template-background-stack",
            Material.GREEN_STAINED_GLASS_PANE 
    );
    public static final ItemStack STORAGE_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "storage-background-stack",
            Material.ORANGE_STAINED_GLASS_PANE 
    );
    public static final ItemStack PURGER_TEMPLATE_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "purger-template-background-stack",
            Material.GREEN_STAINED_GLASS_PANE 
    );
    public static final ItemStack GRABBER_TEMPLATE_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "grabber-template-background-stack",
            Material.BLUE_STAINED_GLASS_PANE 
    );
    public static final ItemStack DUE_BORDER_ICON = Networks.getLocalizationService().getIcon(
            "due-border-icon",
            Material.LIGHT_BLUE_STAINED_GLASS_PANE 
    );
    public static final ItemStack DUE_INPUT_SPLIT_ICON = Networks.getLocalizationService().getIcon(
            "due-input-split-icon",
            Material.YELLOW_STAINED_GLASS_PANE 
    );
    public static final ItemStack INPUT_OUTPUT_SPLIT_ICON = Networks.getLocalizationService().getIcon(
            "input-output-split-icon",
            Material.ORANGE_STAINED_GLASS_PANE 
    );
    public static final ItemStack DECODE_ITEM = Networks.getLocalizationService().getIcon(
            "decode-item",
            Material.KNOWLEDGE_BOOK 
    );
    public static final ItemStack OFFSET_DECREASE_ICON = Networks.getLocalizationService().getIcon(
            "offset-decrease-icon",
            Material.RED_DYE 
    );
    public static final ItemStack OFFSET_SHOW_ICON = Networks.getLocalizationService().getIcon(
            "offset-show-icon",
            Material.TARGET
    );
    public static final ItemStack OFFSET_INCREASE_ICON = Networks.getLocalizationService().getIcon(
            "offset-increase-icon",
            Material.LIME_DYE 
    );
    public static final ItemStack CONTROL_V_TEMPLATE_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "control-v-template-background-stack",
            Material.BLUE_STAINED_GLASS_PANE 
    );
    public static final ItemStack CONTROL_X_TEMPLATE_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "control-x-template-background-stack",
            Material.BLUE_STAINED_GLASS_PANE 
    );
    public static final ItemStack EXPORT_TEMPLATE_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "export-template-background-stack",
            Material.GREEN_STAINED_GLASS_PANE 
    );
    public static final ItemStack EXPORT_OUTPUT_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "export-output-background-stack",
            Material.ORANGE_STAINED_GLASS_PANE 
    );
    public static final ItemStack POWER_DISPLAY_EMPTY = Networks.getLocalizationService().getIcon(
            "power-display-empty",
            Material.RED_STAINED_GLASS_PANE 
    );
    public static final ItemStack QUANTUM_STORAGE_BACK_OUTPUT = Networks.getLocalizationService().getIcon(
            "quantum-storage-back-output",
            Material.ORANGE_STAINED_GLASS_PANE 
    );
    public static final ItemStack QUANTUM_STORAGE_SET_ITEM_SUPPORTING_CUSTOM_MAX = Networks.getLocalizationService().getIcon(
            "quantum-storage-set-item-supporting-custom-max",
            Material.LIME_STAINED_GLASS_PANE 
    );
    public static final ItemStack QUANTUM_STORAGE_SET_ITEM = Networks.getLocalizationService().getIcon(
            "quantum-storage-set-item",
            Material.LIME_STAINED_GLASS_PANE 
    );
    public static final ItemStack QUANTUM_STORAGE_BACK_INPUT = Networks.getLocalizationService().getIcon(
            "quantum-storage-back-input",
            Material.GREEN_STAINED_GLASS_PANE 
    );
    public static final ItemStack QUANTUM_STORAGE_NO_ITEM = Networks.getLocalizationService().getIcon(
            "quantum-storage-no-item",
            Material.RED_STAINED_GLASS_PANE 
    );
    public static final ItemStack QUANTUM_STORAGE_BACK_ITEM = Networks.getLocalizationService().getIcon(
            "quantum-storage-back-item",
            Material.BLUE_STAINED_GLASS_PANE 
    );
    public static final ItemStack QUANTUM_WORKBENCH_CRAFT_BUTTON_STACK = Networks.getLocalizationService().getIcon(
            "quantum-workbench-craft-button-stack",
            Material.CRAFTING_TABLE 
    );
    public static final ItemStack RECEIVED_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "received-background-stack",
            Material.GREEN_STAINED_GLASS_PANE 
    );
    public static final ItemStack TRANSMITTER_TEMPLATE_BACKGROUND_STACK = Networks.getLocalizationService().getIcon(
            "transmitter-template-background-stack",
            Material.GREEN_STAINED_GLASS_PANE 
    );
    public static final ItemStack QUICK_INPUT = Networks.getLocalizationService().getIcon(
            "quick-input",
            Material.PINK_STAINED_GLASS_PANE
    );
    public static final ItemStack QUICK_OUTPUT = Networks.getLocalizationService().getIcon(
            "quick-output",
            Material.RED_STAINED_GLASS_PANE
    );
}
