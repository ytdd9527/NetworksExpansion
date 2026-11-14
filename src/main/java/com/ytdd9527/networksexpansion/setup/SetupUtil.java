package com.ytdd9527.networksexpansion.setup;

import com.balugaq.netex.integrations.logitech.LinkBindingStore;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import com.ytdd9527.networksexpansion.implementation.ExpansionItemsMenus;
import com.ytdd9527.networksexpansion.api.cell.CellTier;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.slimefun.NetworkSlimefunItems;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import net.guizhanss.slimefun4.utils.WikiUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class SetupUtil {

    @SuppressWarnings("deprecation")
    public static void setupItem() {
        NetworkSlimefunItems.setup();

        /* items */
        ExpansionItemsMenus.SUB_MENU_TOOL.addTo(
            ExpansionItems.CARGO_NODE_QUICK_TOOL.registerThis(),
            ExpansionItems.INFO_TOOL.registerThis(),
            ExpansionItems.DUE_MACHINE_CONFIGURATOR.registerThis(),
            ExpansionItems.ITEM_MOVER.registerThis(),
            ExpansionItems.ITEM_FRAME_PASTE_STICK.registerThis(),
            ExpansionItems.STATUS_VIEWER.registerThis());

        /* cargo */
        ExpansionItemsMenus.SUB_MENU_ADVANCED_STORAGE.addTo(ExpansionItems.ADVANCED_QUANTUM_STORAGE.registerThis());
        ExpansionItemsMenus.SUB_MENU_NETWORKS_DRAWERS.addTo(
            ExpansionItems.DRAWER_TIPS.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_1.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_2.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_3.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_4.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_5.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_6.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_7.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_8.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_9.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_10.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_11.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_12.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_13.registerThis());
        ExpansionItemsMenus.SUB_MENU_NETWORKS_DRAWERS.addTo(
            ExpansionItems.CARGO_STORAGE_UNIT_1_MODEL.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_2_MODEL.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_3_MODEL.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_4_MODEL.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_5_MODEL.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_6_MODEL.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_7_MODEL.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_8_MODEL.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_9_MODEL.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_10_MODEL.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_11_MODEL.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_12_MODEL.registerThis(),
            ExpansionItems.CARGO_STORAGE_UNIT_13_MODEL.registerThis());
        ExpansionItemsMenus.SUB_MENU_CARGO.addTo(
            ExpansionItems.NETWORK_INPUT_ONLY_MONITOR.registerThis(),
            ExpansionItems.NETWORK_OUTPUT_ONLY_MONITOR.registerThis());
        ExpansionItemsMenus.SUB_MENU_CARGO.addTo(
            ExpansionItems.LINE_TRANSFER.registerThis(),
            ExpansionItems.LINE_TRANSFER_GRABBER.registerThis(),
            ExpansionItems.LINE_TRANSFER_PUSHER.registerThis(),
            ExpansionItems.LINE_TRANSFER_MORE_PUSHER.registerThis(),
            ExpansionItems.LINE_TRANSFER_BEST_PUSHER.registerThis(),
            ExpansionItems.LINE_TRANSFER_PLUS.registerThis(),
            ExpansionItems.LINE_TRANSFER_PLUS_GRABBER.registerThis(),
            ExpansionItems.LINE_TRANSFER_PLUS_PUSHER.registerThis(),
            ExpansionItems.LINE_TRANSFER_PLUS_MORE_PUSHER.registerThis(),
            ExpansionItems.LINE_TRANSFER_PLUS_BEST_PUSHER.registerThis(),
            ExpansionItems.LINE_TRANSFER_VANILLA_GRABBER.registerThis(),
            ExpansionItems.LINE_TRANSFER_VANILLA_PUSHER.registerThis());
        ExpansionItemsMenus.SUB_MENU_CARGO.addTo(
            ExpansionItems.ADVANCED_LINE_TRANSFER.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_GRABBER.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_PUSHER.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_MORE_PUSHER.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_BEST_PUSHER.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_PLUS.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_PLUS_GRABBER.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_PLUS_PUSHER.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_PLUS_MORE_PUSHER.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_PLUS_BEST_PUSHER.registerThis());
        ExpansionItemsMenus.SUB_MENU_CARGO.addTo(
            ExpansionItems.TRANSFER.registerThis(),
            ExpansionItems.TRANSFER_GRABBER.registerThis(),
            ExpansionItems.TRANSFER_PUSHER.registerThis(),
            ExpansionItems.TRANSFER_MORE_PUSHER.registerThis(),
            ExpansionItems.TRANSFER_BEST_PUSHER.registerThis(),
            ExpansionItems.ADVANCED_TRANSFER.registerThis(),
            ExpansionItems.ADVANCED_TRANSFER_GRABBER.registerThis(),
            ExpansionItems.ADVANCED_TRANSFER_PUSHER.registerThis(),
            ExpansionItems.ADVANCED_TRANSFER_MORE_PUSHER.registerThis(),
            ExpansionItems.ADVANCED_TRANSFER_BEST_PUSHER.registerThis());
        ExpansionItemsMenus.SUB_MENU_CARGO.addTo(
            ExpansionItems.WHITELISTED_TRANSFER_GRABBER.registerThis(),
            ExpansionItems.WHITELISTED_LINE_TRANSFER_GRABBER.registerThis(),
            ExpansionItems.WHITELISTED_TRANSFER_VANILLA_GRABBER.registerThis(),
            ExpansionItems.WHITELISTED_LINE_TRANSFER_VANILLA_GRABBER.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_VANILLA_GRABBER.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_VANILLA_PUSHER.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_VANILLA_PLUS_GRABBER.registerThis(),
            ExpansionItems.ADVANCED_LINE_TRANSFER_VANILLA_PLUS_PUSHER.registerThis()
        );
        ExpansionItemsMenus.SUB_MENU_CARGO.addTo(
            ExpansionItems.SMART_GRABBER.registerThis(), ExpansionItems.SMART_PUSHER.registerThis());

        /* functional machines */
        ExpansionItemsMenus.SUB_MENU_CORE_MACHINE.addTo(
            ExpansionItems.NETWORKS_EXPANSION_WORKBENCH.registerThis(),
            ExpansionItems.STORAGE_UNIT_UPGRADE_TABLE.registerThis());

        ExpansionItemsMenus.SUB_MENU_ADVANCED_NETWORKS.addTo(
            ExpansionItems.ADVANCED_IMPORT.registerThis(),
            ExpansionItems.ADVANCED_EXPORT.registerThis(),
            ExpansionItems.ADVANCED_PURGER.registerThis(),
            ExpansionItems.ADVANCED_GREEDY_BLOCK.registerThis(),
            ExpansionItems.ADVANCED_VACUUM.registerThis(),
            ExpansionItems.ADVANCED_WIRELESS_TRANSMITTER.registerThis(),
            ExpansionItems.NETWORK_CAPACITOR_5.registerThis(),
            ExpansionItems.NETWORK_CAPACITOR_6.registerThis(),
            ExpansionItems.NETWORK_GRID_NEW_STYLE.registerThis(),
            ExpansionItems.NETWORK_CRAFTING_GRID_NEW_STYLE.registerThis(),
            ExpansionItems.NETWORK_BLUEPRINT_DECODER.registerThis(),
            ExpansionItems.LINE_POWER_OUTLET_1.registerThis(),
            ExpansionItems.LINE_POWER_OUTLET_2.registerThis(),
            ExpansionItems.LINE_POWER_OUTLET_3.registerThis(),
            ExpansionItems.LINE_POWER_OUTLET_4.registerThis(),
            ExpansionItems.LINE_POWER_OUTLET_5.registerThis(),
            ExpansionItems.LINE_POWER_OUTLET_6.registerThis(),
            ExpansionItems.LINE_POWER_OUTLET_7.registerThis(),
            ExpansionItems.LINE_POWER_OUTLET_8.registerThis(),
            ExpansionItems.LINE_POWER_OUTLET_9.registerThis(),
            ExpansionItems.LINE_POWER_OUTLET_10.registerThis(),
            ExpansionItems.LINE_POWER_OUTLET_11.registerThis(),
            ExpansionItems.DUE_MACHINE.registerThis(),
            ExpansionItems.OFFSETTER.registerThis(),
            ExpansionItems.QUANTUM_MANAGER.registerThis(),
            ExpansionItems.DRAWER_MANAGER.registerThis(),
            ExpansionItems.CRAFTER_MANAGER.registerThis(),
            ExpansionItems.ITEM_FLOW_VIEWER.registerThis(),
            ExpansionItems.SWITCHING_MONITOR.registerThis(),
            ExpansionItems.HANGING_GRID_NEW_STYLE.registerThis(),
            ExpansionItems.SMART_NETWORK_CRAFTING_GRID_NEW_STYLE.registerThis(),
            ExpansionItems.ITEM_DIFFERENTER.registerThis(),
            ExpansionItems.STORAGE_CARD_CONVERTER.registerThis(),
            ExpansionItems.FACING_PRESETTER.registerThis(),
            ExpansionItems.SUPER_TRASH.registerThis(),
//            ExpansionItems.VISUAL_GRID.registerThis(), // todo: 以后再写
            ExpansionItems.ADVANCED_AUTO_CRAFTING_TABLE.registerThis(),
            ExpansionItems.ADVANCED_AUTO_CRAFTING_TABLE_WITHHOLDING.registerThis());

        ExpansionItemsMenus.SUB_MENU_CELLNET.addTo(
            ExpansionItems.CHAIN_GRABBER.registerThis(),
            ExpansionItems.CHAIN_PUSHER.registerThis(),
            ExpansionItems.CHAIN_TRANSCEIVER.registerThis(),
            ExpansionItems.CHAIN_MODULE_RANGE.registerThis(),
            ExpansionItems.CHAIN_MODULE_CAPACITY.registerThis(),
            ExpansionItems.CHAIN_MODULE_MODE.registerThis(),
            ExpansionItems.CHAIN_MODULE_VANILLA.registerThis(),
            ExpansionItems.CHAIN_MODULE_MULTI_DIRECTION.registerThis(),
            ExpansionItems.CHAIN_MODULE_BINDING.registerThis(),
            ExpansionItems.RUNE_COLLECT.registerThis());

        List<SlimefunItem> cellnetItems = new ArrayList<>();
        cellnetItems.add(ExpansionItems.CELL_DRIVE.registerThis());
        cellnetItems.add(ExpansionItems.CELL_CLEANER.registerThis());
        cellnetItems.add(ExpansionItems.CELL_CONVERTER.registerThis());
        for (CellTier tier : CellTier.values()) {
            cellnetItems.add(tier.register(ExpansionItemsMenus.MENU_CARGO_SYSTEM));
        }
        cellnetItems.add(CellTier.Unlimited.register(ExpansionItemsMenus.MENU_CARGO_SYSTEM));
        cellnetItems.add(ExpansionItems.ASSEMBLY_DRIVE.registerThis());
        cellnetItems.add(ExpansionItems.VOID_CELL.registerThis());
        cellnetItems.add(ExpansionItems.ENDER_DRIVE.registerThis());
        cellnetItems.add(ExpansionItems.ASSEMBLY_CARD.registerThis());
        cellnetItems.add(ExpansionItems.ASSEMBLY_WORKSHOP.registerThis());
        cellnetItems.add(ExpansionItems.CONTAINER_FILLER.registerThis());
        cellnetItems.add(ExpansionItems.CHANNEL_CONFIGURATOR.registerThis());
        cellnetItems.add(ExpansionItems.CHAIN_CONFIGURATOR.registerThis());
        cellnetItems.add(ExpansionItems.CHAIN_BRUSH.registerThis());
        cellnetItems.add(ExpansionItems.OVERCLOCK_CORE.registerThis());
        cellnetItems.add(ExpansionItems.SMART_CORE.registerThis());
        cellnetItems.add(ExpansionItems.ASSEMBLY_MONITOR.registerThis());
        cellnetItems.add(ExpansionItems.DRIVE_MONITOR.registerThis());
        ExpansionItemsMenus.SUB_MENU_CELLNET.addTo(cellnetItems);

        ExpansionItemsMenus.SUB_MENU_BRIDGE.addTo(
            ExpansionItems.NETWORK_BRIDGE_ORDINAL.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_WHITE.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_LIGHT_GRAY.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_GRAY.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_BLACK.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_BROWN.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_RED.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_ORANGE.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_YELLOW.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_LIME.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_GREEN.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_CYAN.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_LIGHT_BLUE.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_BLUE.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_PURPLE.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_MAGENTA.registerThis(),
            ExpansionItems.NETWORK_BRIDGE_PINK.registerThis());

        ExpansionItemsMenus.SUB_MENU_DEPRECATED.addTo(
            ExpansionItems.MAGIC_WORKBENCH_BLUEPRINT.registerThis(),
            ExpansionItems.ARMOR_FORGE_BLUEPRINT.registerThis(),
            ExpansionItems.SMELTERY_BLUEPRINT.registerThis(),
            ExpansionItems.QUANTUM_WORKBENCH_BLUEPRINT.registerThis(),
            ExpansionItems.ANCIENT_ALTAR_BLUEPRINT.registerThis(),
            ExpansionItems.EXPANSION_WORKBENCH_BLUEPRINT.registerThis(),
            ExpansionItems.COMPRESSOR_BLUEPRINT.registerThis(),
            ExpansionItems.GRIND_STONE_BLUEPRINT.registerThis(),
            ExpansionItems.JUICER_BLUEPRINT.registerThis(),
            ExpansionItems.ORE_CRUSHER_BLUEPRINT.registerThis(),
            ExpansionItems.PRESSURE_CHAMBER_BLUEPRINT.registerThis(),
            ExpansionItems.MAGIC_WORKBENCH_RECIPE_ENCODER.registerThis(),
            ExpansionItems.ARMOR_FORGE_RECIPE_ENCODER.registerThis(),
            ExpansionItems.SMELTERY_RECIPE_ENCODER.registerThis(),
            ExpansionItems.QUANTUM_WORKBENCH_RECIPE_ENCODER.registerThis(),
            ExpansionItems.ANCIENT_ALTAR_RECIPE_ENCODER.registerThis(),
            ExpansionItems.EXPANSION_WORKBENCH_RECIPE_ENCODER.registerThis(),
            ExpansionItems.COMPRESSOR_RECIPE_ENCODER.registerThis(),
            ExpansionItems.GRIND_STONE_RECIPE_ENCODER.registerThis(),
            ExpansionItems.JUICER_RECIPE_ENCODER.registerThis(),
            ExpansionItems.ORE_CRUSHER_RECIPE_ENCODER.registerThis(),
            ExpansionItems.PRESSURE_CHAMBER_RECIPE_ENCODER.registerThis(),
            ExpansionItems.AUTO_MAGIC_WORKBENCH.registerThis(),
            ExpansionItems.AUTO_ARMOR_FORGE.registerThis(),
            ExpansionItems.AUTO_SMELTERY.registerThis(),
            ExpansionItems.AUTO_QUANTUM_WORKBENCH.registerThis(),
            ExpansionItems.AUTO_ANCIENT_ALTAR.registerThis(),
            ExpansionItems.AUTO_EXPANSION_WORKBENCH.registerThis(),
            ExpansionItems.AUTO_COMPRESSOR.registerThis(),
            ExpansionItems.AUTO_GRIND_STONE.registerThis(),
            ExpansionItems.AUTO_JUICER.registerThis(),
            ExpansionItems.AUTO_ORE_CRUSHER.registerThis(),
            ExpansionItems.AUTO_PRESSURE_CHAMBER.registerThis(),
            ExpansionItems.AUTO_MAGIC_WORKBENCH_WITHHOLDING.registerThis(),
            ExpansionItems.AUTO_ARMOR_FORGE_WITHHOLDING.registerThis(),
            ExpansionItems.AUTO_SMELTERY_WITHHOLDING.registerThis(),
            ExpansionItems.AUTO_QUANTUM_WORKBENCH_WITHHOLDING.registerThis(),
            ExpansionItems.AUTO_ANCIENT_ALTAR_WITHHOLDING.registerThis(),
            ExpansionItems.AUTO_EXPANSION_WORKBENCH_WITHHOLDING.registerThis(),
            ExpansionItems.AUTO_COMPRESSOR_WITHHOLDING.registerThis(),
            ExpansionItems.AUTO_GRIND_STONE_WITHHOLDING.registerThis(),
            ExpansionItems.AUTO_JUICER_WITHHOLDING.registerThis(),
            ExpansionItems.AUTO_ORE_CRUSHER_WITHHOLDING.registerThis(),
            ExpansionItems.AUTO_PRESSURE_CHAMBER_WITHHOLDING.registerThis(),
            ExpansionItems.ADVANCED_AUTO_MAGIC_WORKBENCH.registerThis(),
            ExpansionItems.ADVANCED_AUTO_ARMOR_FORGE.registerThis(),
            ExpansionItems.ADVANCED_AUTO_SMELTERY.registerThis(),
            ExpansionItems.ADVANCED_AUTO_QUANTUM_WORKBENCH.registerThis(),
            ExpansionItems.ADVANCED_AUTO_ANCIENT_ALTAR.registerThis(),
            ExpansionItems.ADVANCED_AUTO_EXPANSION_WORKBENCH.registerThis(),
            ExpansionItems.ADVANCED_AUTO_COMPRESSOR.registerThis(),
            ExpansionItems.ADVANCED_AUTO_GRIND_STONE.registerThis(),
            ExpansionItems.ADVANCED_AUTO_JUICER.registerThis(),
            ExpansionItems.ADVANCED_AUTO_ORE_CRUSHER.registerThis(),
            ExpansionItems.ADVANCED_AUTO_PRESSURE_CHAMBER.registerThis(),
            ExpansionItems.ADVANCED_AUTO_MAGIC_WORKBENCH_WITHHOLDING.registerThis(),
            ExpansionItems.ADVANCED_AUTO_ARMOR_FORGE_WITHHOLDING.registerThis(),
            ExpansionItems.ADVANCED_AUTO_SMELTERY_WITHHOLDING.registerThis(),
            ExpansionItems.ADVANCED_AUTO_QUANTUM_WORKBENCH_WITHHOLDING.registerThis(),
            ExpansionItems.ADVANCED_AUTO_ANCIENT_ALTAR_WITHHOLDING.registerThis(),
            ExpansionItems.ADVANCED_AUTO_EXPANSION_WORKBENCH_WITHHOLDING.registerThis(),
            ExpansionItems.ADVANCED_AUTO_COMPRESSOR_WITHHOLDING.registerThis(),
            ExpansionItems.ADVANCED_AUTO_GRIND_STONE_WITHHOLDING.registerThis(),
            ExpansionItems.ADVANCED_AUTO_JUICER_WITHHOLDING.registerThis(),
            ExpansionItems.ADVANCED_AUTO_ORE_CRUSHER_WITHHOLDING.registerThis(),
            ExpansionItems.ADVANCED_AUTO_PRESSURE_CHAMBER_WITHHOLDING.registerThis(),
            ExpansionItems.BETTER_GRABBER.registerThis());

        /* authors */
        ExpansionItemsMenus.SUB_MENU_AUTHOR.addTo(
            ExpansionItems.AUTHOR_SEFIRAAT.registerThis(),
            ExpansionItems.AUTHOR_YBW0014.registerThis(),
            ExpansionItems.AUTHOR_YITOUDAIDAI.registerThis(),
            ExpansionItems.AUTHOR_TINALNESS.registerThis());

        /* announce */
        ExpansionItemsMenus.SUB_MENU_ANNOUNCE.addTo(
            ExpansionItems.NTW_EXPANSION_ANNOUNCE_1.registerThis(),
            ExpansionItems.NTW_EXPANSION_ANNOUNCE_2.registerThis(),
            ExpansionItems.NTW_EXPANSION_ANNOUNCE_3.registerThis(),
            ExpansionItems.NTW_EXPANSION_ANNOUNCE_4.registerThis(),
            ExpansionItems.NTW_EXPANSION_ANNOUNCE_5.registerThis(),
            ExpansionItems.NTW_EXPANSION_ANNOUNCE_6.registerThis(),
            ExpansionItems.NTW_EXPANSION_ANNOUNCE_7.registerThis(),
            ExpansionItems.NTW_EXPANSION_ANNOUNCE_8.registerThis(),
            ExpansionItems.NTW_EXPANSION_ANNOUNCE_9.registerThis(),
            ExpansionItems.NTW_EXPANSION_ANNOUNCE_10.registerThis());
    }

    private static void setupMenu() {
        Networks networks = Networks.getInstance();

        ExpansionItemsMenus.MAIN_MENU.setTier(0);

        ExpansionItemsMenus.MENU_ITEMS.setTier(0);
        ExpansionItemsMenus.MENU_CARGO_SYSTEM.setTier(0);
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE.setTier(0);
        ExpansionItemsMenus.MENU_TROPHY.setTier(0);

        ExpansionItemsMenus.MAIN_ITEM_GROUP.addTo(
            ExpansionItemsMenus.MAIN_MENU_ITEM,
            ExpansionItemsMenus.SUB_MENU_TOOL,
            ExpansionItemsMenus.SUB_MENU_BLUEPRINT);
        ExpansionItemsMenus.MAIN_MENU_ITEM.addFrom(
            ExpansionItemsMenus.SUB_MENU_TOOL, ExpansionItemsMenus.SUB_MENU_BLUEPRINT);

        ExpansionItemsMenus.MAIN_ITEM_GROUP.addTo(
            ExpansionItemsMenus.MAIN_MENU_CARGO_SYSTEM,
            ExpansionItemsMenus.SUB_MENU_ADVANCED_STORAGE,
            ExpansionItemsMenus.SUB_MENU_NETWORKS_DRAWERS,
            ExpansionItemsMenus.SUB_MENU_CARGO,
            ExpansionItemsMenus.SUB_MENU_CELLNET);
        ExpansionItemsMenus.MAIN_MENU_CARGO_SYSTEM.addFrom(
            ExpansionItemsMenus.SUB_MENU_ADVANCED_STORAGE,
            ExpansionItemsMenus.SUB_MENU_NETWORKS_DRAWERS,
            ExpansionItemsMenus.SUB_MENU_CARGO,
            ExpansionItemsMenus.SUB_MENU_CELLNET);

        ExpansionItemsMenus.MAIN_ITEM_GROUP.addTo(
            ExpansionItemsMenus.MAIN_MENU_FUNCTIONAL_MACHINE,
            ExpansionItemsMenus.SUB_MENU_CORE_MACHINE,
            ExpansionItemsMenus.SUB_MENU_ADVANCED_NETWORKS,
            ExpansionItemsMenus.SUB_MENU_BRIDGE);
        ExpansionItemsMenus.MAIN_MENU_FUNCTIONAL_MACHINE.addFrom(
            ExpansionItemsMenus.SUB_MENU_CORE_MACHINE,
            ExpansionItemsMenus.SUB_MENU_ADVANCED_NETWORKS,
            ExpansionItemsMenus.SUB_MENU_BRIDGE);

        ExpansionItemsMenus.MAIN_ITEM_GROUP.addTo(
            ExpansionItemsMenus.MAIN_MENU_TROPHY,
            ExpansionItemsMenus.SUB_MENU_AUTHOR,
            ExpansionItemsMenus.SUB_MENU_ANNOUNCE);
        ExpansionItemsMenus.MAIN_MENU_TROPHY.addFrom(
            ExpansionItemsMenus.SUB_MENU_AUTHOR, ExpansionItemsMenus.SUB_MENU_ANNOUNCE);

        ExpansionItemsMenus.MAIN_ITEM_GROUP.addTo(
            ExpansionItemsMenus.SUB_MENU_DEPRECATED);

        ExpansionItemsMenus.MAIN_ITEM_GROUP.setTier(0);
        ExpansionItemsMenus.MAIN_ITEM_GROUP.register(networks);
    }

    public static void setupWiki() {
        WikiUtils.setupJson(Networks.getInstance());
    }

    public static void setupIntegration() {
        Networks.getInstance().setupIntegrations();
    }

    public static void setupAll() {
        setupItem();
        setupWiki();
        setupIntegration();
        Bukkit.getScheduler().runTaskLater(Networks.getInstance(), LinkBindingStore::replayAll, 100L);

        // wait for logitech integration
        Bukkit.getScheduler().runTaskLaterAsynchronously(Networks.getInstance(), SetupUtil::setupMenu, 2L);
    }
}
