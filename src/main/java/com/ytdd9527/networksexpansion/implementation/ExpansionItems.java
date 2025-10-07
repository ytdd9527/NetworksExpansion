package com.ytdd9527.networksexpansion.implementation;

import com.balugaq.netex.api.enums.StorageUnitType;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.core.items.unusable.AuthorHead;
import com.ytdd9527.networksexpansion.core.items.unusable.UnusableSlimefunItem;
import com.ytdd9527.networksexpansion.implementation.blueprints.AncientAltarBlueprint;
import com.ytdd9527.networksexpansion.implementation.blueprints.ArmorForgeBlueprint;
import com.ytdd9527.networksexpansion.implementation.blueprints.CompressorBlueprint;
import com.ytdd9527.networksexpansion.implementation.blueprints.ExpansionWorkbenchBlueprint;
import com.ytdd9527.networksexpansion.implementation.blueprints.GrindStoneBlueprint;
import com.ytdd9527.networksexpansion.implementation.blueprints.JuicerBlueprint;
import com.ytdd9527.networksexpansion.implementation.blueprints.MagicWorkbenchBlueprint;
import com.ytdd9527.networksexpansion.implementation.blueprints.OreCrusherBlueprint;
import com.ytdd9527.networksexpansion.implementation.blueprints.PressureChamberBlueprint;
import com.ytdd9527.networksexpansion.implementation.blueprints.QuantumWorkbenchBlueprint;
import com.ytdd9527.networksexpansion.implementation.blueprints.SmelteryBlueprint;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced.AdvancedAutoAncientAltar;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced.AdvancedAutoArmorForge;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced.AdvancedAutoCompressor;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced.AdvancedAutoCraftingTable;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced.AdvancedAutoExpansionWorkbench;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced.AdvancedAutoGrindStone;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced.AdvancedAutoJuicer;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced.AdvancedAutoMagicWorkbench;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced.AdvancedAutoOreCrusher;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced.AdvancedAutoPressureChamber;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced.AdvancedAutoQuantumWorkbench;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced.AdvancedAutoSmeltery;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic.AutoAncientAltar;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic.AutoArmorForge;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic.AutoCompressor;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic.AutoExpansionWorkbench;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic.AutoGrindStone;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic.AutoJuicer;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic.AutoMagicWorkbench;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic.AutoOreCrusher;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic.AutoPressureChamber;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic.AutoQuantumWorkbench;
import com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic.AutoSmeltery;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.power.power_outlet.line.LinePowerOutlet;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.advanced.AdvancedLineTransfer;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.advanced.AdvancedLineTransferBestPusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.advanced.AdvancedLineTransferGrabber;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.advanced.AdvancedLineTransferMorePusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.advanced.AdvancedLineTransferPusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.basic.LineTransfer;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.basic.LineTransferBestPusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.basic.LineTransferGrabber;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.basic.LineTransferMorePusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.basic.LineTransferPusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.basic.LineTransferVanillaGrabber;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.basic.LineTransferVanillaPusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.advanced.AdvancedTransfer;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.advanced.AdvancedTransferBestPusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.advanced.AdvancedTransferGrabber;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.advanced.AdvancedTransferMorePusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.advanced.AdvancedTransferPusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.basic.Transfer;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.basic.TransferBestPusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.basic.TransferGrabber;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.basic.TransferMorePusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.basic.TransferPusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.basic.WhitelistedTransferGrabber;
import com.ytdd9527.networksexpansion.implementation.machines.encoders.AncientAltarEncoder;
import com.ytdd9527.networksexpansion.implementation.machines.encoders.ArmorForgeEncoder;
import com.ytdd9527.networksexpansion.implementation.machines.encoders.CompressorEncoder;
import com.ytdd9527.networksexpansion.implementation.machines.encoders.ExpansionWorkbenchEncoder;
import com.ytdd9527.networksexpansion.implementation.machines.encoders.GrindStoneEncoder;
import com.ytdd9527.networksexpansion.implementation.machines.encoders.JuicerEncoder;
import com.ytdd9527.networksexpansion.implementation.machines.encoders.MagicWorkbenchEncoder;
import com.ytdd9527.networksexpansion.implementation.machines.encoders.OreCrusherEncoder;
import com.ytdd9527.networksexpansion.implementation.machines.encoders.PressureChamberEncoder;
import com.ytdd9527.networksexpansion.implementation.machines.encoders.QuantumWorkbenchEncoder;
import com.ytdd9527.networksexpansion.implementation.machines.encoders.SmelteryEncoder;
import com.ytdd9527.networksexpansion.implementation.machines.managers.CrafterManager;
import com.ytdd9527.networksexpansion.implementation.machines.managers.DrawerManager;
import com.ytdd9527.networksexpansion.implementation.machines.managers.QuantumManager;
import com.ytdd9527.networksexpansion.implementation.machines.manual.ExpansionWorkbench;
import com.ytdd9527.networksexpansion.implementation.machines.manual.StorageUnitUpgradeTable;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedExport;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedGreedyBlock;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedImport;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedPurger;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedVacuum;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedWirelessTransmitter;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.DueMachine;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.HangingGridNewStyle;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.NetworkBlueprintDecoder;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.NetworkCraftingGridNewStyle;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.NetworkGridNewStyle;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.NetworkInputOnlyMonitor;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.NetworkOutputOnlyMonitor;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.Offsetter;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.SmartGrabber;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.SmartNetworkCraftingGridNewStyle;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.SmartPusher;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.SwitchingMonitor;
import com.ytdd9527.networksexpansion.implementation.machines.unit.NetworksDrawer;
import com.ytdd9527.networksexpansion.implementation.machines.viewer.ItemFlowViewer;
import com.ytdd9527.networksexpansion.implementation.tools.CargoNodeQuickTool;
import com.ytdd9527.networksexpansion.implementation.tools.DueMachineConfigurator;
import com.ytdd9527.networksexpansion.implementation.tools.ItemMover;
import com.ytdd9527.networksexpansion.implementation.tools.NetworksExpansionWorldEditAxe;
import com.ytdd9527.networksexpansion.implementation.tools.NetworksInfoTool;
import com.ytdd9527.networksexpansion.implementation.tools.StatusViewer;
import io.github.sefiraat.networks.slimefun.network.NetworkBridge;
import io.github.sefiraat.networks.slimefun.network.NetworkPowerNode;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

public class ExpansionItems {
    public static final ExpansionWorkbench NETWORKS_EXPANSION_WORKBENCH = new ExpansionWorkbench(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORKS_EXPANSION_WORKBENCH,
        RecipeType.ENHANCED_CRAFTING_TABLE,
        ExpansionRecipes.NETWORKS_EXPANSION_WORKBENCH);

    public static final AdvancedImport ADVANCED_IMPORT = new AdvancedImport(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_IMPORT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_IMPORT);

    public static final AdvancedExport ADVANCED_EXPORT = new AdvancedExport(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_EXPORT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_EXPORT);

    public static final AdvancedPurger ADVANCED_PURGER = new AdvancedPurger(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_PURGER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_PURGER);

    public static final AdvancedGreedyBlock ADVANCED_GREEDY_BLOCK = new AdvancedGreedyBlock(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_GREEDY_BLOCK,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_GREEDY_BLOCK);

    public static final NetworkPowerNode NETWORK_CAPACITOR_5 = new NetworkPowerNode(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_CAPACITOR_5,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_CAPACITOR_5,
        100000000);

    public static final NetworkPowerNode NETWORK_CAPACITOR_6 = new NetworkPowerNode(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_CAPACITOR_6,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_CAPACITOR_6,
        Integer.MAX_VALUE);

    public static final NetworkInputOnlyMonitor NETWORK_INPUT_ONLY_MONITOR = new NetworkInputOnlyMonitor(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_INPUT_ONLY_MONITOR,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_INPUT_ONLY_MONITOR);

    public static final NetworkOutputOnlyMonitor NETWORK_OUTPUT_ONLY_MONITOR = new NetworkOutputOnlyMonitor(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_OUTPUT_ONLY_MONITOR,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_OUTPUT_ONLY_MONITOR);

    public static final NetworkQuantumStorage ADVANCED_QUANTUM_STORAGE = new NetworkQuantumStorage(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_QUANTUM_STORAGE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_QUANTUM_STORAGE,
        NetworkQuantumStorage.getSizes()[10]);

    public static final NetworkGridNewStyle NETWORK_GRID_NEW_STYLE = new NetworkGridNewStyle(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_GRID_NEW_STYLE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_GRID_NEW_STYLE);

    // Blueprints
    public static final MagicWorkbenchBlueprint MAGIC_WORKBENCH_BLUEPRINT = new MagicWorkbenchBlueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.MAGIC_WORKBENCH_BLUEPRINT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.MAGIC_WORKBENCH_BLUEPRINT);

    public static final ArmorForgeBlueprint ARMOR_FORGE_BLUEPRINT = new ArmorForgeBlueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.ARMOR_FORGE_BLUEPRINT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ARMOR_FORGE_BLUEPRINT);

    public static final SmelteryBlueprint SMELTERY_BLUEPRINT = new SmelteryBlueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.SMELTERY_BLUEPRINT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.SMELTERY_BLUEPRINT);

    public static final QuantumWorkbenchBlueprint QUANTUM_WORKBENCH_BLUEPRINT = new QuantumWorkbenchBlueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.QUANTUM_WORKBENCH_BLUEPRINT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.QUANTUM_WORKBENCH_BLUEPRINT);

    public static final AncientAltarBlueprint ANCIENT_ALTAR_BLUEPRINT = new AncientAltarBlueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.ANCIENT_ALTAR_BLUEPRINT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ANCIENT_ALTAR_BLUEPRINT);

    public static final ExpansionWorkbenchBlueprint EXPANSION_WORKBENCH_BLUEPRINT = new ExpansionWorkbenchBlueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.EXPANSION_WORKBENCH_BLUEPRINT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.EXPANSION_WORKBENCH_BLUEPRINT);

    public static final CompressorBlueprint COMPRESSOR_BLUEPRINT = new CompressorBlueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.COMPRESSOR_BLUEPRINT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.COMPRESSOR_BLUEPRINT);

    public static final GrindStoneBlueprint GRIND_STONE_BLUEPRINT = new GrindStoneBlueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.GRIND_STONE_BLUEPRINT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.GRIND_STONE_BLUEPRINT);

    public static final JuicerBlueprint JUICER_BLUEPRINT = new JuicerBlueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.JUICER_BLUEPRINT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.JUICER_BLUEPRINT);

    public static final OreCrusherBlueprint ORE_CRUSHER_BLUEPRINT = new OreCrusherBlueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.ORE_CRUSHER_BLUEPRINT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ORE_CRUSHER_BLUEPRINT);

    public static final PressureChamberBlueprint PRESSURE_CHAMBER_BLUEPRINT = new PressureChamberBlueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.PRESSURE_CHAMBER_BLUEPRINT,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.PRESSURE_CHAMBER_BLUEPRINT);

    // Encoders
    public static final MagicWorkbenchEncoder MAGIC_WORKBENCH_RECIPE_ENCODER = new MagicWorkbenchEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.MAGIC_WORKBENCH_RECIPE_ENCODER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.MAGIC_WORKBENCH_RECIPE_ENCODER);

    public static final ArmorForgeEncoder ARMOR_FORGE_RECIPE_ENCODER = new ArmorForgeEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ARMOR_FORGE_RECIPE_ENCODER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ARMOR_FORGE_RECIPE_ENCODER);

    public static final SmelteryEncoder SMELTERY_RECIPE_ENCODER = new SmelteryEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.SMELTERY_RECIPE_ENCODER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.SMELTERY_RECIPE_ENCODER);

    public static final QuantumWorkbenchEncoder QUANTUM_WORKBENCH_RECIPE_ENCODER = new QuantumWorkbenchEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.QUANTUM_WORKBENCH_RECIPE_ENCODER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.QUANTUM_WORKBENCH_RECIPE_ENCODER);

    public static final AncientAltarEncoder ANCIENT_ALTAR_RECIPE_ENCODER = new AncientAltarEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ANCIENT_ALTAR_RECIPE_ENCODER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ANCIENT_ALTAR_RECIPE_ENCODER);

    public static final ExpansionWorkbenchEncoder EXPANSION_WORKBENCH_RECIPE_ENCODER = new ExpansionWorkbenchEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.EXPANSION_WORKBENCH_RECIPE_ENCODER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.EXPANSION_WORKBENCH_RECIPE_ENCODER);

    public static final CompressorEncoder COMPRESSOR_RECIPE_ENCODER = new CompressorEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.COMPRESSOR_RECIPE_ENCODER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.COMPRESSOR_RECIPE_ENCODER);

    public static final GrindStoneEncoder GRIND_STONE_RECIPE_ENCODER = new GrindStoneEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.GRIND_STONE_RECIPE_ENCODER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.GRIND_STONE_RECIPE_ENCODER);

    public static final JuicerEncoder JUICER_RECIPE_ENCODER = new JuicerEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.JUICER_RECIPE_ENCODER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.JUICER_RECIPE_ENCODER);

    public static final OreCrusherEncoder ORE_CRUSHER_RECIPE_ENCODER = new OreCrusherEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ORE_CRUSHER_RECIPE_ENCODER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ORE_CRUSHER_RECIPE_ENCODER);

    public static final PressureChamberEncoder PRESSURE_CHAMBER_RECIPE_ENCODER = new PressureChamberEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.PRESSURE_CHAMBER_RECIPE_ENCODER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.PRESSURE_CHAMBER_RECIPE_ENCODER);
    // Auto Crafters
    public static final AutoMagicWorkbench AUTO_MAGIC_WORKBENCH = new AutoMagicWorkbench(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_MAGIC_WORKBENCH,
        64,
        false);

    public static final AutoMagicWorkbench AUTO_MAGIC_WORKBENCH_WITHHOLDING = new AutoMagicWorkbench(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_MAGIC_WORKBENCH_WITHHOLDING,
        128,
        true);

    public static final AutoArmorForge AUTO_ARMOR_FORGE = new AutoArmorForge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_ARMOR_FORGE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_ARMOR_FORGE,
        64,
        false);

    public static final AutoArmorForge AUTO_ARMOR_FORGE_WITHHOLDING = new AutoArmorForge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_ARMOR_FORGE_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_ARMOR_FORGE_WITHHOLDING,
        128,
        true);

    public static final AutoSmeltery AUTO_SMELTERY = new AutoSmeltery(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_SMELTERY,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_SMELTERY,
        64,
        false);

    public static final AutoSmeltery AUTO_SMELTERY_WITHHOLDING = new AutoSmeltery(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_SMELTERY_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_SMELTERY_WITHHOLDING,
        128,
        true);

    public static final AutoQuantumWorkbench AUTO_QUANTUM_WORKBENCH = new AutoQuantumWorkbench(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_QUANTUM_WORKBENCH,
        64,
        false);

    public static final AutoQuantumWorkbench AUTO_QUANTUM_WORKBENCH_WITHHOLDING = new AutoQuantumWorkbench(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_QUANTUM_WORKBENCH_WITHHOLDING,
        128,
        true);

    public static final AutoAncientAltar AUTO_ANCIENT_ALTAR = new AutoAncientAltar(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_ANCIENT_ALTAR,
        64,
        false);

    public static final AutoAncientAltar AUTO_ANCIENT_ALTAR_WITHHOLDING = new AutoAncientAltar(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_ANCIENT_ALTAR_WITHHOLDING,
        128,
        true);

    public static final AutoExpansionWorkbench AUTO_EXPANSION_WORKBENCH = new AutoExpansionWorkbench(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_EXPANSION_WORKBENCH,
        64,
        false);

    public static final AutoExpansionWorkbench AUTO_EXPANSION_WORKBENCH_WITHHOLDING = new AutoExpansionWorkbench(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_EXPANSION_WORKBENCH_WITHHOLDING,
        128,
        true);

    public static final AutoCompressor AUTO_COMPRESSOR = new AutoCompressor(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_COMPRESSOR,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_COMPRESSOR,
        64,
        false);

    public static final AutoCompressor AUTO_COMPRESSOR_WITHHOLDING = new AutoCompressor(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_COMPRESSOR_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_COMPRESSOR_WITHHOLDING,
        128,
        true);

    public static final AutoGrindStone AUTO_GRIND_STONE = new AutoGrindStone(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_GRIND_STONE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_GRIND_STONE,
        64,
        false);

    public static final AutoGrindStone AUTO_GRIND_STONE_WITHHOLDING = new AutoGrindStone(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_GRIND_STONE_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_GRIND_STONE_WITHHOLDING,
        128,
        true);

    public static final AutoJuicer AUTO_JUICER = new AutoJuicer(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_JUICER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_JUICER,
        64,
        false);

    public static final AutoJuicer AUTO_JUICER_WITHHOLDING = new AutoJuicer(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_JUICER_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_JUICER_WITHHOLDING,
        128,
        true);

    public static final AutoOreCrusher AUTO_ORE_CRUSHER = new AutoOreCrusher(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_ORE_CRUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_ORE_CRUSHER,
        64,
        false);

    public static final AutoOreCrusher AUTO_ORE_CRUSHER_WITHHOLDING = new AutoOreCrusher(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_ORE_CRUSHER_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_ORE_CRUSHER_WITHHOLDING,
        128,
        true);

    public static final AutoPressureChamber AUTO_PRESSURE_CHAMBER = new AutoPressureChamber(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_PRESSURE_CHAMBER,
        64,
        false);

    public static final AutoPressureChamber AUTO_PRESSURE_CHAMBER_WITHHOLDING = new AutoPressureChamber(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTO_PRESSURE_CHAMBER_WITHHOLDING,
        128,
        true);

    // Advanced Auto Crafters
    public static final AdvancedAutoMagicWorkbench ADVANCED_AUTO_MAGIC_WORKBENCH = new AdvancedAutoMagicWorkbench(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_MAGIC_WORKBENCH,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_MAGIC_WORKBENCH,
        640,
        false);

    public static final AdvancedAutoMagicWorkbench ADVANCED_AUTO_MAGIC_WORKBENCH_WITHHOLDING =
        new AdvancedAutoMagicWorkbench(
            ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
            ExpansionItemStacks.ADVANCED_AUTO_MAGIC_WORKBENCH_WITHHOLDING,
            ExpansionWorkbench.TYPE,
            ExpansionRecipes.ADVANCED_AUTO_MAGIC_WORKBENCH_WITHHOLDING,
            1280,
            true);

    public static final AdvancedAutoArmorForge ADVANCED_AUTO_ARMOR_FORGE = new AdvancedAutoArmorForge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_ARMOR_FORGE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_ARMOR_FORGE,
        640,
        false);

    public static final AdvancedAutoArmorForge ADVANCED_AUTO_ARMOR_FORGE_WITHHOLDING = new AdvancedAutoArmorForge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_ARMOR_FORGE_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_ARMOR_FORGE_WITHHOLDING,
        1280,
        true);

    public static final AdvancedAutoSmeltery ADVANCED_AUTO_SMELTERY = new AdvancedAutoSmeltery(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_SMELTERY,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_SMELTERY,
        640,
        false);

    public static final AdvancedAutoSmeltery ADVANCED_AUTO_SMELTERY_WITHHOLDING = new AdvancedAutoSmeltery(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_SMELTERY_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_SMELTERY_WITHHOLDING,
        1280,
        true);

    public static final AdvancedAutoQuantumWorkbench ADVANCED_AUTO_QUANTUM_WORKBENCH = new AdvancedAutoQuantumWorkbench(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_QUANTUM_WORKBENCH,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_QUANTUM_WORKBENCH,
        640,
        false);

    public static final AdvancedAutoQuantumWorkbench ADVANCED_AUTO_QUANTUM_WORKBENCH_WITHHOLDING =
        new AdvancedAutoQuantumWorkbench(
            ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
            ExpansionItemStacks.ADVANCED_AUTO_QUANTUM_WORKBENCH_WITHHOLDING,
            ExpansionWorkbench.TYPE,
            ExpansionRecipes.ADVANCED_AUTO_QUANTUM_WORKBENCH_WITHHOLDING,
            1280,
            true);

    public static final AdvancedAutoAncientAltar ADVANCED_AUTO_ANCIENT_ALTAR = new AdvancedAutoAncientAltar(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_ANCIENT_ALTAR,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_ANCIENT_ALTAR,
        640,
        false);

    public static final AdvancedAutoAncientAltar ADVANCED_AUTO_ANCIENT_ALTAR_WITHHOLDING = new AdvancedAutoAncientAltar(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_ANCIENT_ALTAR_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_ANCIENT_ALTAR_WITHHOLDING,
        1280,
        true);

    public static final AdvancedAutoExpansionWorkbench ADVANCED_AUTO_EXPANSION_WORKBENCH =
        new AdvancedAutoExpansionWorkbench(
            ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
            ExpansionItemStacks.ADVANCED_AUTO_EXPANSION_WORKBENCH,
            ExpansionWorkbench.TYPE,
            ExpansionRecipes.ADVANCED_AUTO_EXPANSION_WORKBENCH,
            640,
            false);

    public static final AdvancedAutoExpansionWorkbench ADVANCED_AUTO_EXPANSION_WORKBENCH_WITHHOLDING =
        new AdvancedAutoExpansionWorkbench(
            ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
            ExpansionItemStacks.ADVANCED_AUTO_EXPANSION_WORKBENCH_WITHHOLDING,
            ExpansionWorkbench.TYPE,
            ExpansionRecipes.ADVANCED_AUTO_EXPANSION_WORKBENCH_WITHHOLDING,
            1280,
            true);

    public static final AdvancedAutoCraftingTable ADVANCED_AUTO_CRAFTING_TABLE = new AdvancedAutoCraftingTable(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_CRAFTING_TABLE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_CRAFTING_TABLE,
        640,
        false);

    public static final AdvancedAutoCraftingTable ADVANCED_AUTO_CRAFTING_TABLE_WITHHOLDING =
        new AdvancedAutoCraftingTable(
            ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
            ExpansionItemStacks.ADVANCED_AUTO_CRAFTING_TABLE_WITHHOLDING,
            ExpansionWorkbench.TYPE,
            ExpansionRecipes.ADVANCED_AUTO_CRAFTING_TABLE_WITHHOLDING,
            1280,
            true);

    public static final AdvancedAutoCompressor ADVANCED_AUTO_COMPRESSOR = new AdvancedAutoCompressor(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_COMPRESSOR,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_COMPRESSOR,
        640,
        false);

    public static final AdvancedAutoCompressor ADVANCED_AUTO_COMPRESSOR_WITHHOLDING = new AdvancedAutoCompressor(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_COMPRESSOR_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_COMPRESSOR_WITHHOLDING,
        1280,
        true);

    public static final AdvancedAutoGrindStone ADVANCED_AUTO_GRIND_STONE = new AdvancedAutoGrindStone(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_GRIND_STONE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_GRIND_STONE,
        640,
        false);

    public static final AdvancedAutoGrindStone ADVANCED_AUTO_GRIND_STONE_WITHHOLDING = new AdvancedAutoGrindStone(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_GRIND_STONE_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_GRIND_STONE_WITHHOLDING,
        1280,
        true);

    public static final AdvancedAutoJuicer ADVANCED_AUTO_JUICER = new AdvancedAutoJuicer(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_JUICER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_JUICER,
        640,
        false);

    public static final AdvancedAutoJuicer ADVANCED_AUTO_JUICER_WITHHOLDING = new AdvancedAutoJuicer(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_JUICER_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_JUICER_WITHHOLDING,
        1280,
        true);

    public static final AdvancedAutoOreCrusher ADVANCED_AUTO_ORE_CRUSHER = new AdvancedAutoOreCrusher(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_ORE_CRUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_ORE_CRUSHER,
        640,
        false);

    public static final AdvancedAutoOreCrusher ADVANCED_AUTO_ORE_CRUSHER_WITHHOLDING = new AdvancedAutoOreCrusher(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_ORE_CRUSHER_WITHHOLDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_ORE_CRUSHER_WITHHOLDING,
        1280,
        true);

    public static final AdvancedAutoPressureChamber ADVANCED_AUTO_PRESSURE_CHAMBER = new AdvancedAutoPressureChamber(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_PRESSURE_CHAMBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_PRESSURE_CHAMBER,
        640,
        false);

    public static final AdvancedAutoPressureChamber ADVANCED_AUTO_PRESSURE_CHAMBER_WITHHOLDING =
        new AdvancedAutoPressureChamber(
            ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
            ExpansionItemStacks.ADVANCED_AUTO_PRESSURE_CHAMBER_WITHHOLDING,
            ExpansionWorkbench.TYPE,
            ExpansionRecipes.ADVANCED_AUTO_PRESSURE_CHAMBER_WITHHOLDING,
            1280,
            true);

    // Transfer
    public static final LineTransferPusher LINE_TRANSFER_PUSHER = new LineTransferPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.LINE_TRANSFER_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_TRANSFER_PUSHER);

    public static final LineTransferMorePusher LINE_TRANSFER_MORE_PUSHER = new LineTransferMorePusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.LINE_TRANSFER_MORE_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_TRANSFER_MORE_PUSHER);

    public static final LineTransferBestPusher LINE_TRANSFER_BEST_PUSHER = new LineTransferBestPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.LINE_TRANSFER_BEST_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_TRANSFER_BEST_PUSHER);

    public static final LineTransferGrabber LINE_TRANSFER_GRABBER = new LineTransferGrabber(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.LINE_TRANSFER_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_TRANSFER_GRABBER);
    public static final LineTransfer LINE_TRANSFER = new LineTransfer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.LINE_TRANSFER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_TRANSFER);

    public static final LineTransferPusher LINE_TRANSFER_PLUS_PUSHER = new LineTransferPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.LINE_TRANSFER_PLUS_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_TRANSFER_PLUS_PUSHER);

    public static final LineTransferMorePusher LINE_TRANSFER_PLUS_MORE_PUSHER = new LineTransferMorePusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.LINE_TRANSFER_PLUS_MORE_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_TRANSFER_PLUS_MORE_PUSHER);

    public static final LineTransferBestPusher LINE_TRANSFER_PLUS_BEST_PUSHER = new LineTransferBestPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.LINE_TRANSFER_PLUS_BEST_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_TRANSFER_PLUS_BEST_PUSHER);

    public static final LineTransferGrabber LINE_TRANSFER_PLUS_GRABBER = new LineTransferGrabber(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.LINE_TRANSFER_PLUS_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_TRANSFER_PLUS_GRABBER);
    public static final LineTransfer LINE_TRANSFER_PLUS = new LineTransfer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.LINE_TRANSFER_PLUS,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_TRANSFER_PLUS);

    public static final LineTransferVanillaPusher LINE_TRANSFER_VANILLA_PUSHER = new LineTransferVanillaPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.LINE_TRANSFER_VANILLA_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_TRANSFER_VANILLA_PUSHER);

    public static final LineTransferVanillaGrabber LINE_TRANSFER_VANILLA_GRABBER = new LineTransferVanillaGrabber(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.LINE_TRANSFER_VANILLA_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_TRANSFER_VANILLA_GRABBER);

    public static final AdvancedLineTransferPusher ADVANCED_LINE_TRANSFER_PUSHER = new AdvancedLineTransferPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_LINE_TRANSFER_PUSHER);

    public static final AdvancedLineTransferMorePusher ADVANCED_LINE_TRANSFER_MORE_PUSHER =
        new AdvancedLineTransferMorePusher(
            ExpansionItemsMenus.MENU_CARGO_SYSTEM,
            ExpansionItemStacks.ADVANCED_LINE_TRANSFER_MORE_PUSHER,
            ExpansionWorkbench.TYPE,
            ExpansionRecipes.ADVANCED_LINE_TRANSFER_MORE_PUSHER);

    public static final AdvancedLineTransferBestPusher ADVANCED_LINE_TRANSFER_BEST_PUSHER =
        new AdvancedLineTransferBestPusher(
            ExpansionItemsMenus.MENU_CARGO_SYSTEM,
            ExpansionItemStacks.ADVANCED_LINE_TRANSFER_BEST_PUSHER,
            ExpansionWorkbench.TYPE,
            ExpansionRecipes.ADVANCED_LINE_TRANSFER_BEST_PUSHER);

    public static final AdvancedLineTransferGrabber ADVANCED_LINE_TRANSFER_GRABBER = new AdvancedLineTransferGrabber(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_LINE_TRANSFER_GRABBER);
    public static final AdvancedLineTransfer ADVANCED_LINE_TRANSFER = new AdvancedLineTransfer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_LINE_TRANSFER);

    public static final AdvancedLineTransferPusher ADVANCED_LINE_TRANSFER_PLUS_PUSHER = new AdvancedLineTransferPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PLUS_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_LINE_TRANSFER_PLUS_PUSHER);

    public static final AdvancedLineTransferMorePusher ADVANCED_LINE_TRANSFER_PLUS_MORE_PUSHER =
        new AdvancedLineTransferMorePusher(
            ExpansionItemsMenus.MENU_CARGO_SYSTEM,
            ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PLUS_MORE_PUSHER,
            ExpansionWorkbench.TYPE,
            ExpansionRecipes.ADVANCED_LINE_TRANSFER_PLUS_MORE_PUSHER);

    public static final AdvancedLineTransferBestPusher ADVANCED_LINE_TRANSFER_PLUS_BEST_PUSHER =
        new AdvancedLineTransferBestPusher(
            ExpansionItemsMenus.MENU_CARGO_SYSTEM,
            ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PLUS_BEST_PUSHER,
            ExpansionWorkbench.TYPE,
            ExpansionRecipes.ADVANCED_LINE_TRANSFER_PLUS_BEST_PUSHER);
    public static final AdvancedLineTransferGrabber ADVANCED_LINE_TRANSFER_PLUS_GRABBER =
        new AdvancedLineTransferGrabber(
            ExpansionItemsMenus.MENU_CARGO_SYSTEM,
            ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PLUS_GRABBER,
            ExpansionWorkbench.TYPE,
            ExpansionRecipes.ADVANCED_LINE_TRANSFER_PLUS_GRABBER);
    public static final AdvancedLineTransfer ADVANCED_LINE_TRANSFER_PLUS = new AdvancedLineTransfer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PLUS,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_LINE_TRANSFER_PLUS);

    public static final TransferPusher TRANSFER_PUSHER = new TransferPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.TRANSFER_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.TRANSFER_PUSHER, 6);

    public static final TransferMorePusher TRANSFER_MORE_PUSHER = new TransferMorePusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.TRANSFER_MORE_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.TRANSFER_MORE_PUSHER, 6);

    public static final TransferBestPusher TRANSFER_BEST_PUSHER = new TransferBestPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.TRANSFER_BEST_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.TRANSFER_BEST_PUSHER, 6);

    public static final TransferGrabber TRANSFER_GRABBER = new TransferGrabber(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.TRANSFER_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.TRANSFER_GRABBER, 6);

    public static final Transfer TRANSFER = new Transfer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.TRANSFER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.TRANSFER, 6);

    public static final AdvancedTransferPusher ADVANCED_TRANSFER_PUSHER = new AdvancedTransferPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_TRANSFER_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_TRANSFER_PUSHER, 6);

    public static final AdvancedTransferMorePusher ADVANCED_TRANSFER_MORE_PUSHER = new AdvancedTransferMorePusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_TRANSFER_MORE_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_TRANSFER_MORE_PUSHER, 6);

    public static final AdvancedTransferBestPusher ADVANCED_TRANSFER_BEST_PUSHER = new AdvancedTransferBestPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_TRANSFER_BEST_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_TRANSFER_BEST_PUSHER, 6);

    public static final AdvancedTransferGrabber ADVANCED_TRANSFER_GRABBER = new AdvancedTransferGrabber(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_TRANSFER_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_TRANSFER_GRABBER, 6);

    public static final AdvancedTransfer ADVANCED_TRANSFER = new AdvancedTransfer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_TRANSFER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_TRANSFER, 6);

    public static final SmartGrabber SMART_GRABBER = new SmartGrabber(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.SMART_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.SMART_GRABBER);

    public static final SmartPusher SMART_PUSHER = new SmartPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.SMART_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.SMART_PUSHER);

    public static final CargoNodeQuickTool CARGO_NODE_QUICK_TOOL = new CargoNodeQuickTool(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.CARGO_NODE_QUICK_TOOL,
        RecipeType.ENHANCED_CRAFTING_TABLE,
        ExpansionRecipes.CARGO_NODE_QUICK_TOOL);

    @SuppressWarnings("deprecation")
    public static final NetworksExpansionWorldEditAxe WORLDEDIT_AXE =
        new NetworksExpansionWorldEditAxe(ExpansionItemsMenus.MENU_ITEMS, ExpansionItemStacks.WORLDEDIT_AXE);

    public static final NetworksInfoTool INFO_TOOL =
        new NetworksInfoTool(ExpansionItemsMenus.MENU_ITEMS, ExpansionItemStacks.INFO_TOOL);

    public static final StorageUnitUpgradeTable STORAGE_UNIT_UPGRADE_TABLE = new StorageUnitUpgradeTable(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.STORAGE_UNIT_UPGRADE_TABLE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.STORAGE_UNIT_UPGRADE_TABLE);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_1 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_1,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_1,
        StorageUnitType.TINY);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_2 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_2,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_2,
        StorageUnitType.MINI);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_3 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_3,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_3,
        StorageUnitType.SMALL);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_4 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_4,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_4,
        StorageUnitType.MEDIUM);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_5 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_5,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_5,
        StorageUnitType.LARGE);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_6 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_6,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_6,
        StorageUnitType.ENHANCED);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_7 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_7,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_7,
        StorageUnitType.ADVANCED);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_8 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_8,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_8,
        StorageUnitType.EXTRA);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_9 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_9,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_9,
        StorageUnitType.ULTRA);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_10 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_10,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_10,
        StorageUnitType.END_GAME_BASIC);
    public static final NetworksDrawer CARGO_STORAGE_UNIT_11 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_11,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_11,
        StorageUnitType.END_GAME_INTERMEDIATE);
    public static final NetworksDrawer CARGO_STORAGE_UNIT_12 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_12,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_12,
        StorageUnitType.END_GAME_ADVANCED);
    public static final NetworksDrawer CARGO_STORAGE_UNIT_13 = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_13,
        StorageUnitUpgradeTable.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_13,
        StorageUnitType.END_GAME_MAX);
    public static final NetworksDrawer CARGO_STORAGE_UNIT_1_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_1_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_1_MODEL,
        StorageUnitType.TINY);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_2_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_2_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_2_MODEL,
        StorageUnitType.MINI);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_3_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_3_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_3_MODEL,
        StorageUnitType.SMALL);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_4_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_4_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_4_MODEL,
        StorageUnitType.MEDIUM);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_5_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_5_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_5_MODEL,
        StorageUnitType.LARGE);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_6_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_6_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_6_MODEL,
        StorageUnitType.ENHANCED);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_7_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_7_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_7_MODEL,
        StorageUnitType.ADVANCED);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_8_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_8_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_8_MODEL,
        StorageUnitType.EXTRA);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_9_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_9_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_9_MODEL,
        StorageUnitType.ULTRA);

    public static final NetworksDrawer CARGO_STORAGE_UNIT_10_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_10_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_10_MODEL,
        StorageUnitType.END_GAME_BASIC);
    public static final NetworksDrawer CARGO_STORAGE_UNIT_11_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_11_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_11_MODEL,
        StorageUnitType.END_GAME_INTERMEDIATE);
    public static final NetworksDrawer CARGO_STORAGE_UNIT_12_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_12_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_12_MODEL,
        StorageUnitType.END_GAME_ADVANCED);
    public static final NetworksDrawer CARGO_STORAGE_UNIT_13_MODEL = new NetworksDrawer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_13_MODEL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CARGO_STORAGE_UNIT_13_MODEL,
        StorageUnitType.END_GAME_MAX);

    // Bridges
    public static final NetworkBridge NETWORK_BRIDGE_WHITE = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_WHITE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_WHITE,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_WHITE, 8));
    public static final NetworkBridge NETWORK_BRIDGE_LIGHT_GRAY = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_LIGHT_GRAY,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_LIGHT_GRAY,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_LIGHT_GRAY, 8));
    public static final NetworkBridge NETWORK_BRIDGE_GRAY = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_GRAY,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_GRAY,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_GRAY, 8));
    public static final NetworkBridge NETWORK_BRIDGE_BLACK = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_BLACK,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_BLACK,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_BLACK, 8));
    public static final NetworkBridge NETWORK_BRIDGE_BROWN = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_BROWN,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_BROWN,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_BROWN, 8));
    public static final NetworkBridge NETWORK_BRIDGE_RED = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_RED,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_RED,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_RED, 8));
    public static final NetworkBridge NETWORK_BRIDGE_ORANGE = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_ORANGE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_ORANGE,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_ORANGE, 8));
    public static final NetworkBridge NETWORK_BRIDGE_YELLOW = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_YELLOW,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_YELLOW,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_YELLOW, 8));
    public static final NetworkBridge NETWORK_BRIDGE_LIME = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_LIME,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_LIME,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_LIME, 8));
    public static final NetworkBridge NETWORK_BRIDGE_GREEN = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_GREEN,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_GREEN,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_GREEN, 8));
    public static final NetworkBridge NETWORK_BRIDGE_CYAN = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_CYAN,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_CYAN,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_CYAN, 8));
    public static final NetworkBridge NETWORK_BRIDGE_LIGHT_BLUE = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_LIGHT_BLUE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_LIGHT_BLUE,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_LIGHT_BLUE, 8));
    public static final NetworkBridge NETWORK_BRIDGE_BLUE = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_BLUE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_BLUE,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_BLUE, 8));
    public static final NetworkBridge NETWORK_BRIDGE_PURPLE = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_PURPLE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_PURPLE,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_PURPLE, 8));
    public static final NetworkBridge NETWORK_BRIDGE_MAGENTA = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_MAGENTA,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_MAGENTA,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_MAGENTA, 8));
    public static final NetworkBridge NETWORK_BRIDGE_PINK = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_PINK,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_PINK,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_PINK, 8));

    public static final SpecialSlimefunItem AUTHOR_SEFIRAAT =
        new AuthorHead(ExpansionItemsMenus.MENU_TROPHY, ExpansionItemStacks.AUTHOR_SEFIRAAT);

    public static final SpecialSlimefunItem AUTHOR_YBW0014 =
        new AuthorHead(ExpansionItemsMenus.MENU_TROPHY, ExpansionItemStacks.AUTHOR_YBW0014);

    public static final SpecialSlimefunItem AUTHOR_YITOUDAIDAI =
        new AuthorHead(ExpansionItemsMenus.MENU_TROPHY, ExpansionItemStacks.AUTHOR_YITOUDAIDAI);

    public static final SpecialSlimefunItem AUTHOR_TINALNESS =
        new AuthorHead(ExpansionItemsMenus.MENU_TROPHY, ExpansionItemStacks.AUTHOR_TINALNESS);

    public static final DueMachineConfigurator DUE_MACHINE_CONFIGURATOR = new DueMachineConfigurator(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.DUE_MACHINE_CONFIGURATOR,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.DUE_MACHINE_CONFIGURATOR);

    public static final SpecialSlimefunItem ITEM_MOVER = new ItemMover(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.ITEM_MOVER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ITEM_MOVER);

    public static final NetworkBlueprintDecoder NETWORK_BLUEPRINT_DECODER = new NetworkBlueprintDecoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BLUEPRINT_DECODER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BLUEPRINT_DECODER);

    public static final LinePowerOutlet LINE_POWER_OUTLET_1 = new LinePowerOutlet(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.LINE_POWER_OUTLET_1,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_POWER_OUTLET_1);

    public static final LinePowerOutlet LINE_POWER_OUTLET_2 = new LinePowerOutlet(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.LINE_POWER_OUTLET_2,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_POWER_OUTLET_2);

    public static final LinePowerOutlet LINE_POWER_OUTLET_3 = new LinePowerOutlet(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.LINE_POWER_OUTLET_3,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_POWER_OUTLET_3);

    public static final LinePowerOutlet LINE_POWER_OUTLET_4 = new LinePowerOutlet(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.LINE_POWER_OUTLET_4,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_POWER_OUTLET_4);

    public static final LinePowerOutlet LINE_POWER_OUTLET_5 = new LinePowerOutlet(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.LINE_POWER_OUTLET_5,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_POWER_OUTLET_5);

    public static final LinePowerOutlet LINE_POWER_OUTLET_6 = new LinePowerOutlet(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.LINE_POWER_OUTLET_6,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_POWER_OUTLET_6);

    public static final LinePowerOutlet LINE_POWER_OUTLET_7 = new LinePowerOutlet(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.LINE_POWER_OUTLET_7,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_POWER_OUTLET_7);

    public static final LinePowerOutlet LINE_POWER_OUTLET_8 = new LinePowerOutlet(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.LINE_POWER_OUTLET_8,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_POWER_OUTLET_8);

    public static final LinePowerOutlet LINE_POWER_OUTLET_9 = new LinePowerOutlet(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.LINE_POWER_OUTLET_9,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_POWER_OUTLET_9);

    public static final LinePowerOutlet LINE_POWER_OUTLET_10 = new LinePowerOutlet(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.LINE_POWER_OUTLET_10,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_POWER_OUTLET_10);

    public static final LinePowerOutlet LINE_POWER_OUTLET_11 = new LinePowerOutlet(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.LINE_POWER_OUTLET_11,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.LINE_POWER_OUTLET_11);

    public static final DueMachine DUE_MACHINE = new DueMachine(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.DUE_MACHINE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.DUE_MACHINE);

    public static final Offsetter OFFSETTER = new Offsetter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.OFFSETTER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.OFFSETTER);

    @Deprecated
    public static final WhitelistedTransferGrabber BETTER_GRABBER = new WhitelistedTransferGrabber(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.BETTER_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.BETTER_GRABBER);

    public static final UnusableSlimefunItem NTW_EXPANSION_ANNOUNCE_1 = new UnusableSlimefunItem(
        ExpansionItemsMenus.MENU_TROPHY,
        ExpansionItemStacks.NTW_EXPANSION_ANNOUNCE_1,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final UnusableSlimefunItem NTW_EXPANSION_ANNOUNCE_2 = new UnusableSlimefunItem(
        ExpansionItemsMenus.MENU_TROPHY,
        ExpansionItemStacks.NTW_EXPANSION_ANNOUNCE_2,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final UnusableSlimefunItem NTW_EXPANSION_ANNOUNCE_3 = new UnusableSlimefunItem(
        ExpansionItemsMenus.MENU_TROPHY,
        ExpansionItemStacks.NTW_EXPANSION_ANNOUNCE_3,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final UnusableSlimefunItem NTW_EXPANSION_ANNOUNCE_4 = new UnusableSlimefunItem(
        ExpansionItemsMenus.MENU_TROPHY,
        ExpansionItemStacks.NTW_EXPANSION_ANNOUNCE_4,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final UnusableSlimefunItem NTW_EXPANSION_ANNOUNCE_5 = new UnusableSlimefunItem(
        ExpansionItemsMenus.MENU_TROPHY,
        ExpansionItemStacks.NTW_EXPANSION_ANNOUNCE_5,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final UnusableSlimefunItem NTW_EXPANSION_ANNOUNCE_6 = new UnusableSlimefunItem(
        ExpansionItemsMenus.MENU_TROPHY,
        ExpansionItemStacks.NTW_EXPANSION_ANNOUNCE_6,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final UnusableSlimefunItem NTW_EXPANSION_ANNOUNCE_7 = new UnusableSlimefunItem(
        ExpansionItemsMenus.MENU_TROPHY,
        ExpansionItemStacks.NTW_EXPANSION_ANNOUNCE_7,
        RecipeType.NULL,
        ExpansionRecipes.NULL);
    public static final UnusableSlimefunItem NTW_EXPANSION_ANNOUNCE_8 = new UnusableSlimefunItem(
        ExpansionItemsMenus.MENU_TROPHY,
        ExpansionItemStacks.NTW_EXPANSION_ANNOUNCE_8,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final UnusableSlimefunItem NTW_EXPANSION_ANNOUNCE_9 = new UnusableSlimefunItem(
        ExpansionItemsMenus.MENU_TROPHY,
        ExpansionItemStacks.NTW_EXPANSION_ANNOUNCE_9,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final UnusableSlimefunItem NTW_EXPANSION_ANNOUNCE_10 = new UnusableSlimefunItem(
        ExpansionItemsMenus.MENU_TROPHY,
        ExpansionItemStacks.NTW_EXPANSION_ANNOUNCE_10,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final NetworkCraftingGridNewStyle NETWORK_CRAFTING_GRID_NEW_STYLE = new NetworkCraftingGridNewStyle(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_CRAFTING_GRID_NEW_STYLE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_CRAFTING_GRID_NEW_STYLE);

    public static final StatusViewer STATUS_VIEWER = new StatusViewer(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.STATUS_VIEWER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.STATUS_VIEWER);

    public static final UnusableSlimefunItem DRAWER_TIPS = new UnusableSlimefunItem(
        ExpansionItemsMenus.MENU_ITEMS, ExpansionItemStacks.DRAWER_TIPS, RecipeType.NULL, ExpansionRecipes.NULL);

    public static final QuantumManager QUANTUM_MANAGER = new QuantumManager(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.QUANTUM_MANAGER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.QUANTUM_MANAGER);

    public static final DrawerManager DRAWER_MANAGER = new DrawerManager(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.DRAWER_MANAGER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.DRAWER_MANAGER);

    public static final ItemFlowViewer ITEM_FLOW_VIEWER = new ItemFlowViewer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ITEM_FLOW_VIEWER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ITEM_FLOW_VIEWER);

    public static final AdvancedVacuum ADVANCED_VACUUM = new AdvancedVacuum(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_VACUUM,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_VACUUM);

    public static final CrafterManager CRAFTER_MANAGER = new CrafterManager(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CRAFTER_MANAGER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CRAFTER_MANAGER);

    public static final SwitchingMonitor SWITCHING_MONITOR = new SwitchingMonitor(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.SWITCHING_MONITOR,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.SWITCHING_MONITOR);

    public static final HangingGridNewStyle HANGING_GRID_NEW_STYLE = new HangingGridNewStyle(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.HANGING_GRID_NEW_STYLE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.HANGING_GRID_NEW_STYLE);

    public static final WhitelistedTransferGrabber WHITELISTED_TRANSFER_GRABBER = new WhitelistedTransferGrabber(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.WHITELISTED_TRANSFER_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.WHITELISTED_TRANSFER_GRABBER);

    public static final WhitelistedTransferGrabber WHITELISTED_LINE_TRANSFER_GRABBER = new WhitelistedTransferGrabber(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.WHITELISTED_LINE_TRANSFER_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.WHITELISTED_LINE_TRANSFER_GRABBER);

    public static final WhitelistedTransferGrabber WHITELISTED_TRANSFER_VANILLA_GRABBER = new WhitelistedTransferGrabber(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.WHITELISTED_TRANSFER_VANILLA_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.WHITELISTED_TRANSFER_VANILLA_GRABBER);

    public static final WhitelistedTransferGrabber WHITELISTED_LINE_TRANSFER_VANILLA_GRABBER = new WhitelistedTransferGrabber(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.WHITELISTED_LINE_TRANSFER_VANILLA_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.WHITELISTED_LINE_TRANSFER_VANILLA_GRABBER);

    public static final SmartNetworkCraftingGridNewStyle SMART_NETWORK_CRAFTING_GRID_NEW_STYLE = new SmartNetworkCraftingGridNewStyle(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.SMART_NETWORK_CRAFTING_GRID_NEW_STYLE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.SMART_NETWORK_CRAFTING_GRID_NEW_STYLE);

    public static final AdvancedWirelessTransmitter ADVANCED_WIRELESS_TRANSMITTER = new AdvancedWirelessTransmitter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_WIRELESS_TRANSMITTER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_WIRELESS_TRANSMITTER);
}
