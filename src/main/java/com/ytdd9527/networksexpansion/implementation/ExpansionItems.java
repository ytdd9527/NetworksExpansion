package com.ytdd9527.networksexpansion.implementation;

import com.balugaq.netex.api.enums.StorageUnitType;
import com.ytdd9527.networksexpansion.api.cell.VoidCell;
import com.ytdd9527.networksexpansion.core.items.AuthorHead;
import com.ytdd9527.networksexpansion.core.items.Blueprint;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.core.items.UnusableSlimefunItem;
import com.ytdd9527.networksexpansion.core.machines.AdvancedAutoCrafter;
import com.ytdd9527.networksexpansion.core.machines.AutoCrafter;
import com.ytdd9527.networksexpansion.core.machines.BlueprintEncoder;
import com.ytdd9527.networksexpansion.implementation.items.assembly.core.AssemblyCard;
import com.ytdd9527.networksexpansion.implementation.items.assembly.core.OverclockCore;
import com.ytdd9527.networksexpansion.implementation.items.assembly.core.SmartCore;
import com.ytdd9527.networksexpansion.implementation.items.cell.ChainBrush;
import com.ytdd9527.networksexpansion.implementation.items.cell.ChainConfigurator;
import com.ytdd9527.networksexpansion.implementation.items.chain.ChainModuleItem;
import com.ytdd9527.networksexpansion.implementation.items.collect.CollectRune;
import com.ytdd9527.networksexpansion.implementation.items.drive.ChannelConfigurator;
import com.ytdd9527.networksexpansion.implementation.items.filler.ContainerFiller;
import com.ytdd9527.networksexpansion.implementation.machines.assembly.AssemblyDrive;
import com.ytdd9527.networksexpansion.implementation.machines.assembly.AssemblyMonitor;
import com.ytdd9527.networksexpansion.implementation.machines.assembly.AssemblyWorkshop;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.power.power_outlet.line.LinePowerOutlet;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.advanced.AdvancedLineTransfer;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.advanced.AdvancedLineTransferBestPusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.advanced.AdvancedLineTransferGrabber;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.advanced.AdvancedLineTransferMorePusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.advanced.AdvancedLineTransferPusher;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.advanced.AdvancedLineTransferVanillaGrabber;
import com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.advanced.AdvancedLineTransferVanillaPusher;
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
import com.ytdd9527.networksexpansion.implementation.machines.chain.ChainGrabber;
import com.ytdd9527.networksexpansion.implementation.machines.chain.ChainPusher;
import com.ytdd9527.networksexpansion.implementation.machines.chain.ChainTransceiver;
import com.ytdd9527.networksexpansion.implementation.machines.converter.CellCleaner;
import com.ytdd9527.networksexpansion.implementation.machines.converter.CellConverter;
import com.ytdd9527.networksexpansion.implementation.machines.drive.CellDrive;
import com.ytdd9527.networksexpansion.implementation.machines.drive.DriveMonitor;
import com.ytdd9527.networksexpansion.implementation.machines.drive.EnderDrive;
import com.ytdd9527.networksexpansion.implementation.machines.managers.CrafterManager;
import com.ytdd9527.networksexpansion.implementation.machines.managers.DrawerManager;
import com.ytdd9527.networksexpansion.implementation.machines.managers.QuantumManager;
import com.ytdd9527.networksexpansion.implementation.machines.manual.ExpansionWorkbench;
import com.ytdd9527.networksexpansion.implementation.machines.manual.FacingPresetter;
import com.ytdd9527.networksexpansion.implementation.machines.manual.ItemDifferenter;
import com.ytdd9527.networksexpansion.implementation.machines.manual.StorageCardConverter;
import com.ytdd9527.networksexpansion.implementation.machines.manual.StorageUnitUpgradeTable;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedExport;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedGreedyBlock;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedImport;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedPurger;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedVacuum;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedWirelessTransmitter;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.DueMachine;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.NetworkBlueprintDecoder;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.NetworkInputOnlyMonitor;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.NetworkOutputOnlyMonitor;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.Offsetter;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.SmartGrabber;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.SmartPusher;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.SuperTrash;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.SwitchingMonitor;
import com.ytdd9527.networksexpansion.implementation.machines.networks.grids.HangingGridNewStyle;
import com.ytdd9527.networksexpansion.implementation.machines.networks.grids.NetworkCraftingGridNewStyle;
import com.ytdd9527.networksexpansion.implementation.machines.networks.grids.NetworkGridNewStyle;
import com.ytdd9527.networksexpansion.implementation.machines.networks.grids.SmartNetworkCraftingGridNewStyle;
import com.ytdd9527.networksexpansion.implementation.machines.networks.grids.visual.VisualGrid;
import com.ytdd9527.networksexpansion.implementation.machines.unit.NetworksDrawer;
import com.ytdd9527.networksexpansion.implementation.machines.viewer.ItemFlowViewer;
import com.ytdd9527.networksexpansion.implementation.tools.CargoNodeQuickTool;
import com.ytdd9527.networksexpansion.implementation.tools.DueMachineConfigurator;
import com.ytdd9527.networksexpansion.implementation.tools.ItemFramePasteStick;
import com.ytdd9527.networksexpansion.implementation.tools.ItemMover;
import com.ytdd9527.networksexpansion.implementation.tools.NetworksInfoTool;
import com.ytdd9527.networksexpansion.implementation.tools.StatusViewer;
import io.github.sefiraat.networks.slimefun.network.NetworkBridge;
import io.github.sefiraat.networks.slimefun.network.NetworkPowerNode;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

public class ExpansionItems {
    public static final UnusableSlimefunItem PLACEHOLDER_ITEM = new UnusableSlimefunItem(
        ExpansionItemStacks.PLACEHOLDER_ITEM
    );
    static {
        PLACEHOLDER_ITEM.registerThis();
    }
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
        NetworkQuantumStorage.getSizesLong()[14]);
    static {
        ADVANCED_QUANTUM_STORAGE.setSupportsCustomMaxAmount(true);
    }

    public static final NetworkGridNewStyle NETWORK_GRID_NEW_STYLE = new NetworkGridNewStyle(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_GRID_NEW_STYLE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_GRID_NEW_STYLE);

    // Blueprints
    public static final Blueprint MAGIC_WORKBENCH_BLUEPRINT = new Blueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.MAGIC_WORKBENCH_BLUEPRINT,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final Blueprint ARMOR_FORGE_BLUEPRINT = new Blueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.ARMOR_FORGE_BLUEPRINT,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final Blueprint SMELTERY_BLUEPRINT = new Blueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.SMELTERY_BLUEPRINT,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final Blueprint QUANTUM_WORKBENCH_BLUEPRINT = new Blueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.QUANTUM_WORKBENCH_BLUEPRINT,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final Blueprint ANCIENT_ALTAR_BLUEPRINT = new Blueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.ANCIENT_ALTAR_BLUEPRINT,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final Blueprint EXPANSION_WORKBENCH_BLUEPRINT = new Blueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.EXPANSION_WORKBENCH_BLUEPRINT,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final Blueprint COMPRESSOR_BLUEPRINT = new Blueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.COMPRESSOR_BLUEPRINT,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final Blueprint GRIND_STONE_BLUEPRINT = new Blueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.GRIND_STONE_BLUEPRINT,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final Blueprint JUICER_BLUEPRINT = new Blueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.JUICER_BLUEPRINT,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final Blueprint ORE_CRUSHER_BLUEPRINT = new Blueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.ORE_CRUSHER_BLUEPRINT,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final Blueprint PRESSURE_CHAMBER_BLUEPRINT = new Blueprint(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.PRESSURE_CHAMBER_BLUEPRINT,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    // Encoders
    public static final BlueprintEncoder MAGIC_WORKBENCH_RECIPE_ENCODER = new BlueprintEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.MAGIC_WORKBENCH_RECIPE_ENCODER,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final BlueprintEncoder ARMOR_FORGE_RECIPE_ENCODER = new BlueprintEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ARMOR_FORGE_RECIPE_ENCODER,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final BlueprintEncoder SMELTERY_RECIPE_ENCODER = new BlueprintEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.SMELTERY_RECIPE_ENCODER,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final BlueprintEncoder QUANTUM_WORKBENCH_RECIPE_ENCODER = new BlueprintEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.QUANTUM_WORKBENCH_RECIPE_ENCODER,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final BlueprintEncoder ANCIENT_ALTAR_RECIPE_ENCODER = new BlueprintEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ANCIENT_ALTAR_RECIPE_ENCODER,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final BlueprintEncoder EXPANSION_WORKBENCH_RECIPE_ENCODER = new BlueprintEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.EXPANSION_WORKBENCH_RECIPE_ENCODER,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final BlueprintEncoder COMPRESSOR_RECIPE_ENCODER = new BlueprintEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.COMPRESSOR_RECIPE_ENCODER,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final BlueprintEncoder GRIND_STONE_RECIPE_ENCODER = new BlueprintEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.GRIND_STONE_RECIPE_ENCODER,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final BlueprintEncoder JUICER_RECIPE_ENCODER = new BlueprintEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.JUICER_RECIPE_ENCODER,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final BlueprintEncoder ORE_CRUSHER_RECIPE_ENCODER = new BlueprintEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ORE_CRUSHER_RECIPE_ENCODER,
        RecipeType.NULL,
        ExpansionRecipes.NULL);

    public static final BlueprintEncoder PRESSURE_CHAMBER_RECIPE_ENCODER = new BlueprintEncoder(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.PRESSURE_CHAMBER_RECIPE_ENCODER,
        RecipeType.NULL,
        ExpansionRecipes.NULL);
    
    // Auto Crafters
    public static final AutoCrafter AUTO_MAGIC_WORKBENCH = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        64,
        false);

    public static final AutoCrafter AUTO_MAGIC_WORKBENCH_WITHHOLDING = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        128,
        true);

    public static final AutoCrafter AUTO_ARMOR_FORGE = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_ARMOR_FORGE,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        64,
        false);

    public static final AutoCrafter AUTO_ARMOR_FORGE_WITHHOLDING = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_ARMOR_FORGE_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        128,
        true);

    public static final AutoCrafter AUTO_SMELTERY = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_SMELTERY,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        64,
        false);

    public static final AutoCrafter AUTO_SMELTERY_WITHHOLDING = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_SMELTERY_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        128,
        true);

    public static final AutoCrafter AUTO_QUANTUM_WORKBENCH = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        64,
        false);

    public static final AutoCrafter AUTO_QUANTUM_WORKBENCH_WITHHOLDING = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        128,
        true);

    public static final AutoCrafter AUTO_ANCIENT_ALTAR = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        64,
        false);

    public static final AutoCrafter AUTO_ANCIENT_ALTAR_WITHHOLDING = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        128,
        true);

    public static final AutoCrafter AUTO_EXPANSION_WORKBENCH = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        64,
        false);

    public static final AutoCrafter AUTO_EXPANSION_WORKBENCH_WITHHOLDING = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        128,
        true);

    public static final AutoCrafter AUTO_COMPRESSOR = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_COMPRESSOR,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        64,
        false);

    public static final AutoCrafter AUTO_COMPRESSOR_WITHHOLDING = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_COMPRESSOR_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        128,
        true);

    public static final AutoCrafter AUTO_GRIND_STONE = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_GRIND_STONE,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        64,
        false);

    public static final AutoCrafter AUTO_GRIND_STONE_WITHHOLDING = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_GRIND_STONE_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        128,
        true);

    public static final AutoCrafter AUTO_JUICER = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_JUICER,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        64,
        false);

    public static final AutoCrafter AUTO_JUICER_WITHHOLDING = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_JUICER_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        128,
        true);

    public static final AutoCrafter AUTO_ORE_CRUSHER = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_ORE_CRUSHER,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        64,
        false);

    public static final AutoCrafter AUTO_ORE_CRUSHER_WITHHOLDING = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_ORE_CRUSHER_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        128,
        true);

    public static final AutoCrafter AUTO_PRESSURE_CHAMBER = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        64,
        false);

    public static final AutoCrafter AUTO_PRESSURE_CHAMBER_WITHHOLDING = new AutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        128,
        true);

    // Advanced Auto Crafters
    public static final AdvancedAutoCrafter ADVANCED_AUTO_MAGIC_WORKBENCH = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_MAGIC_WORKBENCH,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        640,
        false);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_MAGIC_WORKBENCH_WITHHOLDING =
        new AdvancedAutoCrafter(
            ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
            ExpansionItemStacks.ADVANCED_AUTO_MAGIC_WORKBENCH_WITHHOLDING,
            RecipeType.NULL,
            ExpansionRecipes.NULL,
            1280,
            true);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_ARMOR_FORGE = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_ARMOR_FORGE,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        640,
        false);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_ARMOR_FORGE_WITHHOLDING = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_ARMOR_FORGE_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        1280,
        true);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_SMELTERY = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_SMELTERY,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        640,
        false);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_SMELTERY_WITHHOLDING = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_SMELTERY_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        1280,
        true);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_QUANTUM_WORKBENCH = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_QUANTUM_WORKBENCH,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        640,
        false);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_QUANTUM_WORKBENCH_WITHHOLDING =
        new AdvancedAutoCrafter(
            ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
            ExpansionItemStacks.ADVANCED_AUTO_QUANTUM_WORKBENCH_WITHHOLDING,
            RecipeType.NULL,
            ExpansionRecipes.NULL,
            1280,
            true);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_ANCIENT_ALTAR = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_ANCIENT_ALTAR,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        640,
        false);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_ANCIENT_ALTAR_WITHHOLDING = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_ANCIENT_ALTAR_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        1280,
        true);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_EXPANSION_WORKBENCH =
        new AdvancedAutoCrafter(
            ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
            ExpansionItemStacks.ADVANCED_AUTO_EXPANSION_WORKBENCH,
            RecipeType.NULL,
            ExpansionRecipes.NULL,
            640,
            false);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_EXPANSION_WORKBENCH_WITHHOLDING =
        new AdvancedAutoCrafter(
            ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
            ExpansionItemStacks.ADVANCED_AUTO_EXPANSION_WORKBENCH_WITHHOLDING,
            RecipeType.NULL,
            ExpansionRecipes.NULL,
            1280,
            true);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_CRAFTING_TABLE = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_CRAFTING_TABLE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_AUTO_CRAFTING_TABLE,
        640,
        false);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_CRAFTING_TABLE_WITHHOLDING =
        new AdvancedAutoCrafter(
            ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
            ExpansionItemStacks.ADVANCED_AUTO_CRAFTING_TABLE_WITHHOLDING,
            ExpansionWorkbench.TYPE,
            ExpansionRecipes.ADVANCED_AUTO_CRAFTING_TABLE_WITHHOLDING,
            1280,
            true);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_COMPRESSOR = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_COMPRESSOR,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        640,
        false);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_COMPRESSOR_WITHHOLDING = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_COMPRESSOR_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        1280,
        true);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_GRIND_STONE = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_GRIND_STONE,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        640,
        false);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_GRIND_STONE_WITHHOLDING = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_GRIND_STONE_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        1280,
        true);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_JUICER = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_JUICER,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        640,
        false);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_JUICER_WITHHOLDING = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_JUICER_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        1280,
        true);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_ORE_CRUSHER = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_ORE_CRUSHER,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        640,
        false);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_ORE_CRUSHER_WITHHOLDING = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_ORE_CRUSHER_WITHHOLDING,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        1280,
        true);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_PRESSURE_CHAMBER = new AdvancedAutoCrafter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_AUTO_PRESSURE_CHAMBER,
        RecipeType.NULL,
        ExpansionRecipes.NULL,
        640,
        false);

    public static final AdvancedAutoCrafter ADVANCED_AUTO_PRESSURE_CHAMBER_WITHHOLDING =
        new AdvancedAutoCrafter(
            ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
            ExpansionItemStacks.ADVANCED_AUTO_PRESSURE_CHAMBER_WITHHOLDING,
            RecipeType.NULL,
            ExpansionRecipes.NULL,
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

    public static final ChainGrabber CHAIN_GRABBER = new ChainGrabber(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CHAIN_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CHAIN_GRABBER);

    public static final ChainPusher CHAIN_PUSHER = new ChainPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CHAIN_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CHAIN_PUSHER);

    public static final ChainTransceiver CHAIN_TRANSCEIVER = new ChainTransceiver(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CHAIN_TRANSCEIVER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CHAIN_TRANSCEIVER);

    public static final ChainModuleItem CHAIN_MODULE_RANGE = new ChainModuleItem(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.CHAIN_MODULE_RANGE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CHAIN_MODULE_RANGE);

    public static final ChainModuleItem CHAIN_MODULE_CAPACITY = new ChainModuleItem(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.CHAIN_MODULE_CAPACITY,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CHAIN_MODULE_CAPACITY);

    public static final ChainModuleItem CHAIN_MODULE_MODE = new ChainModuleItem(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.CHAIN_MODULE_MODE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CHAIN_MODULE_MODE);

    public static final ChainModuleItem CHAIN_MODULE_VANILLA = new ChainModuleItem(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.CHAIN_MODULE_VANILLA,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CHAIN_MODULE_VANILLA);

    public static final ChainModuleItem CHAIN_MODULE_MULTI_DIRECTION = new ChainModuleItem(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.CHAIN_MODULE_MULTI_DIRECTION,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CHAIN_MODULE_MULTI_DIRECTION);

    public static final ChainModuleItem CHAIN_MODULE_BINDING = new ChainModuleItem(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.CHAIN_MODULE_BINDING,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CHAIN_MODULE_BINDING);

    public static final ChainConfigurator CHAIN_CONFIGURATOR = new ChainConfigurator(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.CHAIN_CONFIGURATOR,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CHAIN_CONFIGURATOR);

    public static final ChainBrush CHAIN_BRUSH = new ChainBrush(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.CHAIN_BRUSH,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CHAIN_BRUSH);

    public static final CollectRune RUNE_COLLECT = new CollectRune(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.RUNE_COLLECT,
        RecipeType.NULL,
        ExpansionRecipes.RUNE_COLLECT);

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
        ExpansionRecipes.TRANSFER_PUSHER, 32);

    public static final TransferMorePusher TRANSFER_MORE_PUSHER = new TransferMorePusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.TRANSFER_MORE_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.TRANSFER_MORE_PUSHER, 32);

    public static final TransferBestPusher TRANSFER_BEST_PUSHER = new TransferBestPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.TRANSFER_BEST_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.TRANSFER_BEST_PUSHER, 32);

    public static final TransferGrabber TRANSFER_GRABBER = new TransferGrabber(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.TRANSFER_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.TRANSFER_GRABBER, 32);

    public static final Transfer TRANSFER = new Transfer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.TRANSFER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.TRANSFER, 32);

    public static final AdvancedTransferPusher ADVANCED_TRANSFER_PUSHER = new AdvancedTransferPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_TRANSFER_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_TRANSFER_PUSHER, 32);

    public static final AdvancedTransferMorePusher ADVANCED_TRANSFER_MORE_PUSHER = new AdvancedTransferMorePusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_TRANSFER_MORE_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_TRANSFER_MORE_PUSHER, 32);

    public static final AdvancedTransferBestPusher ADVANCED_TRANSFER_BEST_PUSHER = new AdvancedTransferBestPusher(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_TRANSFER_BEST_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_TRANSFER_BEST_PUSHER, 32);

    public static final AdvancedTransferGrabber ADVANCED_TRANSFER_GRABBER = new AdvancedTransferGrabber(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_TRANSFER_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_TRANSFER_GRABBER, 32);

    public static final AdvancedTransfer ADVANCED_TRANSFER = new AdvancedTransfer(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ADVANCED_TRANSFER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_TRANSFER, 32);

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
    public static final NetworkBridge NETWORK_BRIDGE_ORDINAL = new NetworkBridge(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.NETWORK_BRIDGE_ORDINAL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.NETWORK_BRIDGE_ORDINAL,
        StackUtils.getAsQuantity(ExpansionItemStacks.NETWORK_BRIDGE_ORDINAL, 8));
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

    public static final SpecialSlimefunItem AUTHOR_TINALNESS = new AuthorHead(
        ExpansionItemsMenus.MENU_TROPHY,
        ExpansionItemStacks.AUTHOR_TINALNESS,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.AUTHOR_TINALNESS
    );

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

    public static final SpecialSlimefunItem ITEM_FRAME_PASTE_STICK = new ItemFramePasteStick(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.ITEM_FRAME_PASTE_STICK,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ITEM_FRAME_PASTE_STICK);

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
        RecipeType.NULL,
        ExpansionRecipes.NULL);

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

    public static final AdvancedLineTransferVanillaGrabber ADVANCED_LINE_TRANSFER_VANILLA_GRABBER = new AdvancedLineTransferVanillaGrabber(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_VANILLA_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_LINE_TRANSFER_VANILLA_GRABBER);

    public static final AdvancedLineTransferVanillaPusher ADVANCED_LINE_TRANSFER_VANILLA_PUSHER = new AdvancedLineTransferVanillaPusher(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_VANILLA_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_LINE_TRANSFER_VANILLA_PUSHER);

    public static final AdvancedLineTransferVanillaGrabber ADVANCED_LINE_TRANSFER_VANILLA_PLUS_GRABBER = new AdvancedLineTransferVanillaGrabber(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_VANILLA_PLUS_GRABBER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_LINE_TRANSFER_VANILLA_PLUS_GRABBER);

    public static final AdvancedLineTransferVanillaPusher ADVANCED_LINE_TRANSFER_VANILLA_PLUS_PUSHER = new AdvancedLineTransferVanillaPusher(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_VANILLA_PLUS_PUSHER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ADVANCED_LINE_TRANSFER_VANILLA_PLUS_PUSHER);

    public static final ItemDifferenter ITEM_DIFFERENTER = new ItemDifferenter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.ITEM_DIFFERENTER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ITEM_DIFFERENTER);

    public static final StorageCardConverter STORAGE_CARD_CONVERTER = new StorageCardConverter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.STORAGE_CARD_CONVERTER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.STORAGE_CARD_CONVERTER);

    public static final FacingPresetter FACING_PRESETTER = new FacingPresetter(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.FACING_PRESETTER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.FACING_PRESETTER);

    public static final SuperTrash SUPER_TRASH = new SuperTrash(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.SUPER_TRASH,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.SUPER_TRASH
    );

    // Cellnet Drive
    public static final CellDrive CELL_DRIVE = new CellDrive(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CELL_DRIVE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CELL_DRIVE);

    // Cell Cleaner
    public static final CellCleaner CELL_CLEANER = new CellCleaner(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CELL_CLEANER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CELL_CLEANER);

    // Cell Converter
    public static final CellConverter CELL_CONVERTER = new CellConverter(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CELL_CONVERTER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CELL_CONVERTER);

    public static final AssemblyDrive ASSEMBLY_DRIVE = new AssemblyDrive(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ASSEMBLY_DRIVE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ASSEMBLY_DRIVE);

    public static final VoidCell VOID_CELL = new VoidCell(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.VOID_CELL,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.VOID_CELL);

    public static final EnderDrive ENDER_DRIVE = new EnderDrive(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ENDER_DRIVE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ENDER_DRIVE);

    public static final AssemblyCard ASSEMBLY_CARD = new AssemblyCard(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.ASSEMBLY_CARD,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ASSEMBLY_CARD);

    public static final AssemblyWorkshop ASSEMBLY_WORKSHOP = new AssemblyWorkshop(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ASSEMBLY_WORKSHOP,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ASSEMBLY_WORKSHOP);

    public static final ContainerFiller CONTAINER_FILLER = new ContainerFiller(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.CONTAINER_FILLER,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CONTAINER_FILLER);

    public static final ChannelConfigurator CHANNEL_CONFIGURATOR = new ChannelConfigurator(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.CHANNEL_CONFIGURATOR,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.CHANNEL_CONFIGURATOR);

    public static final OverclockCore OVERCLOCK_CORE = new OverclockCore(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.OVERCLOCK_CORE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.OVERCLOCK_CORE);


    public static final SmartCore SMART_CORE = new SmartCore(
        ExpansionItemsMenus.MENU_ITEMS,
        ExpansionItemStacks.SMART_CORE,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.SMART_CORE);

    public static final AssemblyMonitor ASSEMBLY_MONITOR = new AssemblyMonitor(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.ASSEMBLY_MONITOR,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.ASSEMBLY_MONITOR);

    public static final DriveMonitor DRIVE_MONITOR = new DriveMonitor(
        ExpansionItemsMenus.MENU_CARGO_SYSTEM,
        ExpansionItemStacks.DRIVE_MONITOR,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.DRIVE_MONITOR);

    public static final VisualGrid VISUAL_GRID = new VisualGrid(
        ExpansionItemsMenus.MENU_FUNCTIONAL_MACHINE,
        ExpansionItemStacks.VISUAL_GRID,
        ExpansionWorkbench.TYPE,
        ExpansionRecipes.VISUAL_GRID
    );
}
