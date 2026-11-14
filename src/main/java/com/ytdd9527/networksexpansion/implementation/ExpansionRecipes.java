package com.ytdd9527.networksexpansion.implementation;

import com.ytdd9527.networksexpansion.api.cell.CellTier;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.ADVANCED_NANOBOTS;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.INTERDIMENSIONAL_PRESENCE;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_AUTO_CRAFTER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_AUTO_CRAFTER_WITHHOLDING;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_BEST_PUSHER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_BRIDGE;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_CAPACITOR_1;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_CAPACITOR_2;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_CAPACITOR_4;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_CONFIGURATOR;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_CRAFTING_GRID;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_EXPORT;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_GRABBER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_GREEDY_BLOCK;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_GRID;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_IMPORT;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_MONITOR;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_MORE_PUSHER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_POWER_OUTLET_1;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_PROBE;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_PURGER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_PUSHER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_0;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_1;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_10;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_14;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_2;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_3;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_4;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_5;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_6;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_7;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_8;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_9;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_WORKBENCH;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_RECIPE_ENCODER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_VACUUM;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_VANILLA_GRABBER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_VANILLA_PUSHER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_WIRELESS_CONFIGURATOR;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_WIRELESS_RECEIVER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_WIRELESS_TRANSMITTER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.OPTIC_CABLE;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.OPTIC_GLASS;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.OPTIC_STAR;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.RADIOACTIVE_OPTIC_STAR;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.SHRINKING_BASE;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.SIMPLE_NANOBOTS;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.SYNTHETIC_EMERALD_SHARD;

public class ExpansionRecipes {
    public static final ItemStack HOPPER = new ItemStack(Material.HOPPER);
    public static final ItemStack CRAFTING_TABLE = new ItemStack(Material.CRAFTING_TABLE);

    public static final ItemStack[] NULL = new ItemStack[]{
        null, null, null,
        null, null, null,
        null, null, null
    };

    public static final ItemStack[] RUNE_COLLECT = NULL;

    // Workbench
    public static final ItemStack[] NETWORKS_EXPANSION_WORKBENCH = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(), CRAFTING_TABLE, OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem()
    };

    // Line Transfers
    public static final ItemStack[] LINE_TRANSFER_PUSHER = new ItemStack[]{
        NETWORK_PUSHER.getItem(), NETWORK_EXPORT.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_EXPORT.getItem(), NETWORK_MONITOR.getItem(), NETWORK_EXPORT.getItem(),
        OPTIC_GLASS.getItem(), NETWORK_EXPORT.getItem(), NETWORK_PUSHER.getItem()
    };

    public static final ItemStack[] LINE_TRANSFER_MORE_PUSHER = new ItemStack[]{
        NETWORK_MORE_PUSHER.getItem(), NETWORK_EXPORT.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_EXPORT.getItem(), NETWORK_MONITOR.getItem(), NETWORK_EXPORT.getItem(),
        OPTIC_GLASS.getItem(), NETWORK_EXPORT.getItem(), NETWORK_MORE_PUSHER.getItem()
    };

    public static final ItemStack[] LINE_TRANSFER_BEST_PUSHER = new ItemStack[]{
        NETWORK_BEST_PUSHER.getItem(), NETWORK_EXPORT.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_EXPORT.getItem(), NETWORK_MONITOR.getItem(), NETWORK_EXPORT.getItem(),
        OPTIC_GLASS.getItem(), NETWORK_EXPORT.getItem(), NETWORK_BEST_PUSHER.getItem()
    };

    public static final ItemStack[] LINE_TRANSFER_PLUS_PUSHER = new ItemStack[]{
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.LINE_TRANSFER_PUSHER, OPTIC_CABLE.getItem(),
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem()
    };

    public static final ItemStack[] LINE_TRANSFER_PLUS_MORE_PUSHER = new ItemStack[]{
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.LINE_TRANSFER_MORE_PUSHER, OPTIC_CABLE.getItem(),
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem()
    };

    public static final ItemStack[] LINE_TRANSFER_PLUS_BEST_PUSHER = new ItemStack[]{
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.LINE_TRANSFER_BEST_PUSHER, OPTIC_CABLE.getItem(),
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_PUSHER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_PUSHER, NETWORK_BRIDGE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_PUSHER, NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_MORE_PUSHER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_MORE_PUSHER, NETWORK_BRIDGE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_MORE_PUSHER, NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_BEST_PUSHER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_BEST_PUSHER, NETWORK_BRIDGE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_BEST_PUSHER, NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_PLUS_PUSHER = new ItemStack[]{
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PUSHER, OPTIC_CABLE.getItem(),
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_PLUS_MORE_PUSHER = new ItemStack[]{
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.ADVANCED_LINE_TRANSFER_MORE_PUSHER, OPTIC_CABLE.getItem(),
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_PLUS_BEST_PUSHER = new ItemStack[]{
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.ADVANCED_LINE_TRANSFER_BEST_PUSHER, OPTIC_CABLE.getItem(),
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem()
    };

    public static final ItemStack[] LINE_TRANSFER_GRABBER = new ItemStack[]{
        NETWORK_GRABBER.getItem(), NETWORK_IMPORT.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_IMPORT.getItem(), NETWORK_MONITOR.getItem(), NETWORK_IMPORT.getItem(),
        OPTIC_GLASS.getItem(), NETWORK_IMPORT.getItem(), NETWORK_GRABBER.getItem()
    };
    public static final ItemStack[] LINE_TRANSFER_PLUS_GRABBER = new ItemStack[]{
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.LINE_TRANSFER_GRABBER, OPTIC_CABLE.getItem(),
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem()
    };
    public static final ItemStack[] ADVANCED_LINE_TRANSFER_GRABBER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_GRABBER, NETWORK_BRIDGE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_GRABBER, NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_PLUS_GRABBER = new ItemStack[]{
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.ADVANCED_LINE_TRANSFER_GRABBER, OPTIC_CABLE.getItem(),
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem()
    };
    public static final ItemStack[] LINE_TRANSFER = new ItemStack[]{
        ExpansionItemStacks.LINE_TRANSFER_PUSHER,
        NETWORK_IMPORT.getItem(),
        NETWORK_BRIDGE.getItem(),
        NETWORK_EXPORT.getItem(),
        NETWORK_MONITOR.getItem(),
        NETWORK_EXPORT.getItem(),
        NETWORK_BRIDGE.getItem(),
        NETWORK_IMPORT.getItem(),
        ExpansionItemStacks.LINE_TRANSFER_GRABBER
    };

    public static final ItemStack[] LINE_TRANSFER_PLUS = new ItemStack[]{
        ExpansionItemStacks.LINE_TRANSFER_PLUS_PUSHER,
        OPTIC_GLASS.getItem(),
        NETWORK_BRIDGE.getItem(),
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        NETWORK_BRIDGE.getItem(),
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.LINE_TRANSFER_PLUS_GRABBER
    };
    public static final ItemStack[] ADVANCED_LINE_TRANSFER = new ItemStack[]{
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PUSHER,
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_GRABBER
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_PLUS = new ItemStack[]{
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PLUS_PUSHER,
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PLUS_GRABBER
    };

    public static final ItemStack[] TRANSFER_PUSHER = new ItemStack[]{
        null, null, null,
        null, ExpansionItemStacks.LINE_TRANSFER_PUSHER, null,
        null, null, null
    };

    public static final ItemStack[] TRANSFER_MORE_PUSHER = new ItemStack[]{
        null, null, null,
        null, ExpansionItemStacks.LINE_TRANSFER_MORE_PUSHER, null,
        null, null, null
    };

    public static final ItemStack[] TRANSFER_BEST_PUSHER = new ItemStack[]{
        null, null, null,
        null, ExpansionItemStacks.LINE_TRANSFER_BEST_PUSHER, null,
        null, null, null
    };

    public static final ItemStack[] TRANSFER_GRABBER = new ItemStack[]{
        null, null, null,
        null, ExpansionItemStacks.LINE_TRANSFER_GRABBER, null,
        null, null, null
    };
    public static final ItemStack[] TRANSFER = new ItemStack[]{
        null, null, null,
        null, ExpansionItemStacks.LINE_TRANSFER, null,
        null, null, null
    };
    public static final ItemStack[] ADVANCED_TRANSFER_PUSHER = new ItemStack[]{
        null, null, null,
        null, ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PUSHER, null,
        null, null, null
    };

    public static final ItemStack[] ADVANCED_TRANSFER_MORE_PUSHER = new ItemStack[]{
        null, null, null,
        null, ExpansionItemStacks.ADVANCED_LINE_TRANSFER_MORE_PUSHER, null,
        null, null, null
    };

    public static final ItemStack[] ADVANCED_TRANSFER_BEST_PUSHER = new ItemStack[]{
        null, null, null,
        null, ExpansionItemStacks.ADVANCED_LINE_TRANSFER_BEST_PUSHER, null,
        null, null, null
    };

    public static final ItemStack[] ADVANCED_TRANSFER_GRABBER = new ItemStack[]{
        null, null, null,
        null, ExpansionItemStacks.ADVANCED_LINE_TRANSFER_GRABBER, null,
        null, null, null
    };
    public static final ItemStack[] ADVANCED_TRANSFER = new ItemStack[]{
        null, null, null,
        null, ExpansionItemStacks.ADVANCED_LINE_TRANSFER, null,
        null, null, null
    };

    public static final ItemStack[] LINE_TRANSFER_VANILLA_GRABBER = new ItemStack[]{
        NETWORK_VANILLA_GRABBER.getItem(), OPTIC_CABLE.getItem(), NETWORK_VANILLA_GRABBER.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_VANILLA_GRABBER.getItem(), OPTIC_CABLE.getItem(), NETWORK_VANILLA_GRABBER.getItem()
    };

    public static final ItemStack[] LINE_TRANSFER_VANILLA_PUSHER = new ItemStack[]{
        NETWORK_VANILLA_PUSHER.getItem(), OPTIC_CABLE.getItem(), NETWORK_VANILLA_PUSHER.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_VANILLA_PUSHER.getItem(), OPTIC_CABLE.getItem(), NETWORK_VANILLA_PUSHER.getItem()
    };

    public static final ItemStack[] ADVANCED_IMPORT = new ItemStack[]{
        NETWORK_IMPORT.getItem(), NETWORK_IMPORT.getItem(), NETWORK_IMPORT.getItem(),
        OPTIC_STAR.getItem(), OPTIC_STAR.getItem(), OPTIC_STAR.getItem(),
        NETWORK_IMPORT.getItem(), NETWORK_IMPORT.getItem(), NETWORK_IMPORT.getItem()
    };

    public static final ItemStack[] ADVANCED_EXPORT = new ItemStack[]{
        NETWORK_EXPORT.getItem(), RADIOACTIVE_OPTIC_STAR.getItem(), NETWORK_EXPORT.getItem(),
        NETWORK_EXPORT.getItem(), RADIOACTIVE_OPTIC_STAR.getItem(), NETWORK_EXPORT.getItem(),
        NETWORK_EXPORT.getItem(), RADIOACTIVE_OPTIC_STAR.getItem(), NETWORK_EXPORT.getItem()
    };

    public static final ItemStack[] ADVANCED_PURGER = new ItemStack[]{
        RADIOACTIVE_OPTIC_STAR.getItem(), RADIOACTIVE_OPTIC_STAR.getItem(), RADIOACTIVE_OPTIC_STAR.getItem(),
        RADIOACTIVE_OPTIC_STAR.getItem(), NETWORK_PURGER.getItem(), RADIOACTIVE_OPTIC_STAR.getItem(),
        RADIOACTIVE_OPTIC_STAR.getItem(), RADIOACTIVE_OPTIC_STAR.getItem(), RADIOACTIVE_OPTIC_STAR.getItem()
    };

    public static final ItemStack[] ADVANCED_GREEDY_BLOCK = new ItemStack[]{
        SYNTHETIC_EMERALD_SHARD.getItem(), NETWORK_GREEDY_BLOCK.getItem(), SYNTHETIC_EMERALD_SHARD.getItem(),
        NETWORK_GREEDY_BLOCK.getItem(), NETWORK_GREEDY_BLOCK.getItem(), NETWORK_GREEDY_BLOCK.getItem(),
        SHRINKING_BASE.getItem(), NETWORK_GREEDY_BLOCK.getItem(), SHRINKING_BASE.getItem()
    };

    public static final ItemStack[] NETWORK_INPUT_ONLY_MONITOR = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_MONITOR.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), SlimefunItems.CARGO_INPUT_NODE, OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] NETWORK_OUTPUT_ONLY_MONITOR = new ItemStack[]{
        OPTIC_GLASS.getItem(), SlimefunItems.CARGO_OUTPUT_NODE_2, OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_MONITOR.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] NETWORK_CAPACITOR_5 = new ItemStack[]{
        NETWORK_CAPACITOR_4.getItem(), NETWORK_CAPACITOR_4.getItem(), NETWORK_CAPACITOR_4.getItem(),
        NETWORK_CAPACITOR_4.getItem(), NETWORK_CAPACITOR_4.getItem(), NETWORK_CAPACITOR_4.getItem(),
        NETWORK_CAPACITOR_4.getItem(), NETWORK_CAPACITOR_4.getItem(), NETWORK_CAPACITOR_4.getItem()
    };

    public static final ItemStack[] NETWORK_CAPACITOR_6 = new ItemStack[]{
        ExpansionItemStacks.NETWORK_CAPACITOR_5,
        ExpansionItemStacks.NETWORK_CAPACITOR_5,
        ExpansionItemStacks.NETWORK_CAPACITOR_5,
        ExpansionItemStacks.NETWORK_CAPACITOR_5,
        ExpansionItemStacks.NETWORK_CAPACITOR_5,
        ExpansionItemStacks.NETWORK_CAPACITOR_5,
        ExpansionItemStacks.NETWORK_CAPACITOR_5,
        ExpansionItemStacks.NETWORK_CAPACITOR_5,
        ExpansionItemStacks.NETWORK_CAPACITOR_5
    };

    // Advanced Auto Crafter
    public static final ItemStack[] ADVANCED_AUTO_CRAFTING_TABLE = new ItemStack[]{
        NETWORK_AUTO_CRAFTER.getItem(), ADVANCED_NANOBOTS.getItem(), NETWORK_AUTO_CRAFTER.getItem(),
        ADVANCED_NANOBOTS.getItem(), INTERDIMENSIONAL_PRESENCE.getItem(), ADVANCED_NANOBOTS.getItem(),
        NETWORK_AUTO_CRAFTER.getItem(), NETWORK_RECIPE_ENCODER.getItem(), NETWORK_AUTO_CRAFTER.getItem()
    };

    public static final ItemStack[] ADVANCED_AUTO_CRAFTING_TABLE_WITHHOLDING = new ItemStack[]{
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        NETWORK_RECIPE_ENCODER.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem()
    };

    public static final ItemStack[] SMART_GRABBER = new ItemStack[]{
        OPTIC_GLASS.getItem(), SIMPLE_NANOBOTS.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_GRABBER.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), SIMPLE_NANOBOTS.getItem(), OPTIC_GLASS.getItem()
    };
    public static final ItemStack[] SMART_PUSHER = new ItemStack[]{
        OPTIC_GLASS.getItem(), ADVANCED_NANOBOTS.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_PUSHER.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), ADVANCED_NANOBOTS.getItem(), OPTIC_GLASS.getItem()
    };
    // Grid
    public static final ItemStack[] NETWORK_GRID_NEW_STYLE = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), OPTIC_CABLE.getItem(), NETWORK_BRIDGE.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_GRID.getItem(), OPTIC_CABLE.getItem(),
        NETWORK_BRIDGE.getItem(), OPTIC_CABLE.getItem(), NETWORK_BRIDGE.getItem()
    };
    // Storages
    public static final ItemStack[] ADVANCED_QUANTUM_STORAGE = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_QUANTUM_STORAGE_14.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem()
    };
    // Bridges
    public static final ItemStack[] NETWORK_BRIDGE_ORDINAL = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.GLASS), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_WHITE = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.WHITE_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_LIGHT_GRAY = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.LIGHT_GRAY_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_GRAY = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.GRAY_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_BLACK = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.BLACK_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_BROWN = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.BROWN_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_RED = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.RED_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_ORANGE = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.ORANGE_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_YELLOW = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.YELLOW_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_LIME = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.LIME_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_GREEN = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.GREEN_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_CYAN = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.CYAN_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_LIGHT_BLUE = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.LIGHT_BLUE_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_BLUE = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.BLUE_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_PURPLE = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.PURPLE_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_MAGENTA = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.MAGENTA_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] NETWORK_BRIDGE_PINK = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), new ItemStack(Material.PINK_DYE), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] CARGO_NODE_QUICK_TOOL = new ItemStack[]{
        new ItemStack(Material.LEATHER),
        SlimefunItems.SOLAR_PANEL,
        new ItemStack(Material.LEATHER),
        new ItemStack(Material.LEATHER),
        SlimefunItems.ANDROID_MEMORY_CORE,
        new ItemStack(Material.LEATHER),
        SlimefunItems.ADVANCED_CIRCUIT_BOARD,
        SlimefunItems.SMALL_CAPACITOR,
        SlimefunItems.ADVANCED_CIRCUIT_BOARD
    };

    public static final ItemStack[] STORAGE_UNIT_UPGRADE_TABLE = new ItemStack[]{
        OPTIC_GLASS.getItem(), SIMPLE_NANOBOTS.getItem(), OPTIC_GLASS.getItem(),
        SIMPLE_NANOBOTS.getItem(), NETWORK_QUANTUM_WORKBENCH.getItem(), SIMPLE_NANOBOTS.getItem(),
        OPTIC_GLASS.getItem(), SIMPLE_NANOBOTS.getItem(), OPTIC_GLASS.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_1 = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        NETWORK_QUANTUM_STORAGE_1.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_QUANTUM_STORAGE_1.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_2 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_9.getItem(),
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_9.getItem(),
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.CARGO_STORAGE_UNIT_1,
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_9.getItem(),
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_9.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_3 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_10.getItem(),
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_10.getItem(),
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.CARGO_STORAGE_UNIT_2,
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_10.getItem(),
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_10.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_4 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_1.getItem(),
        SlimefunItems.BOOSTED_URANIUM,
        NETWORK_QUANTUM_STORAGE_1.getItem(),
        SlimefunItems.BOOSTED_URANIUM,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_3,
        SlimefunItems.BOOSTED_URANIUM,
        NETWORK_QUANTUM_STORAGE_1.getItem(),
        SlimefunItems.BOOSTED_URANIUM,
        NETWORK_QUANTUM_STORAGE_1.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_5 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_2.getItem(),
        SlimefunItems.NETHER_ICE,
        NETWORK_QUANTUM_STORAGE_2.getItem(),
        SlimefunItems.NETHER_ICE,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_4,
        SlimefunItems.NETHER_ICE,
        NETWORK_QUANTUM_STORAGE_2.getItem(),
        SlimefunItems.NETHER_ICE,
        NETWORK_QUANTUM_STORAGE_2.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_6 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_3.getItem(),
        SlimefunItems.FUEL_BUCKET,
        NETWORK_QUANTUM_STORAGE_3.getItem(),
        SlimefunItems.FUEL_BUCKET,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_5,
        SlimefunItems.FUEL_BUCKET,
        NETWORK_QUANTUM_STORAGE_3.getItem(),
        SlimefunItems.FUEL_BUCKET,
        NETWORK_QUANTUM_STORAGE_3.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_7 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_3.getItem(), OPTIC_STAR.getItem(), NETWORK_QUANTUM_STORAGE_3.getItem(),
        OPTIC_STAR.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_6, OPTIC_STAR.getItem(),
        NETWORK_QUANTUM_STORAGE_3.getItem(), OPTIC_STAR.getItem(), NETWORK_QUANTUM_STORAGE_3.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_8 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_3.getItem(), RADIOACTIVE_OPTIC_STAR.getItem(), NETWORK_QUANTUM_STORAGE_3.getItem(),
        RADIOACTIVE_OPTIC_STAR.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_7, RADIOACTIVE_OPTIC_STAR.getItem(),
        NETWORK_QUANTUM_STORAGE_3.getItem(), RADIOACTIVE_OPTIC_STAR.getItem(), NETWORK_QUANTUM_STORAGE_3.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_9 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_4.getItem(),
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_4.getItem(),
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.CARGO_STORAGE_UNIT_8,
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_4.getItem(),
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_4.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_10 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_4.getItem(),
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_4.getItem(),
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.CARGO_STORAGE_UNIT_9,
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_4.getItem(),
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_4.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_11 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_5.getItem(),
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_5.getItem(),
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.CARGO_STORAGE_UNIT_10,
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_5.getItem(),
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_STORAGE_5.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_12 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_6.getItem(),
        NETWORK_QUANTUM_STORAGE_8.getItem(),
        NETWORK_QUANTUM_STORAGE_6.getItem(),
        NETWORK_QUANTUM_STORAGE_8.getItem(),
        ExpansionItemStacks.CARGO_STORAGE_UNIT_11,
        NETWORK_QUANTUM_STORAGE_8.getItem(),
        NETWORK_QUANTUM_STORAGE_6.getItem(),
        NETWORK_QUANTUM_STORAGE_8.getItem(),
        NETWORK_QUANTUM_STORAGE_6.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_13 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_7.getItem(),
        NETWORK_QUANTUM_STORAGE_8.getItem(),
        NETWORK_QUANTUM_STORAGE_7.getItem(),
        NETWORK_QUANTUM_STORAGE_8.getItem(),
        ExpansionItemStacks.CARGO_STORAGE_UNIT_12,
        NETWORK_QUANTUM_STORAGE_8.getItem(),
        NETWORK_QUANTUM_STORAGE_7.getItem(),
        NETWORK_QUANTUM_STORAGE_8.getItem(),
        NETWORK_QUANTUM_STORAGE_7.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_1_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_1, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_2_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_2, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_3_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_3, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_4_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_4, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_5_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_5, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_6_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_6, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_7_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_7, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_8_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_8, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_9_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_9, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_10_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_10, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_11_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_11, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_12_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_12, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_13_MODEL = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_13, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };

    public static final ItemStack[] DUE_MACHINE_CONFIGURATOR = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_0.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_QUANTUM_STORAGE_0.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_CONFIGURATOR.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_QUANTUM_STORAGE_0.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_QUANTUM_STORAGE_0.getItem()
    };

    public static final ItemStack[] ITEM_MOVER = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_8.getItem(),
        ExpansionItemStacks.ADVANCED_IMPORT,
        NETWORK_QUANTUM_STORAGE_8.getItem(),
        ExpansionItemStacks.ADVANCED_EXPORT,
        NETWORK_WIRELESS_CONFIGURATOR.getItem(),
        ExpansionItemStacks.ADVANCED_EXPORT,
        NETWORK_QUANTUM_STORAGE_8.getItem(),
        ExpansionItemStacks.ADVANCED_IMPORT,
        NETWORK_QUANTUM_STORAGE_8.getItem()
    };
    public static final ItemStack[] ITEM_FRAME_PASTE_STICK = new ItemStack[]{
        null, new ItemStack(Material.ITEM_FRAME), null,
        new ItemStack(Material.ITEM_FRAME), NETWORK_QUANTUM_STORAGE_1.getItem(), new ItemStack(Material.ITEM_FRAME),
        null, new ItemStack(Material.BLAZE_ROD), null
    };
    public static final ItemStack[] NETWORK_BLUEPRINT_DECODER = new ItemStack[]{
        NETWORK_RECIPE_ENCODER.getItem(), NETWORK_RECIPE_ENCODER.getItem(), NETWORK_RECIPE_ENCODER.getItem(),
        NETWORK_RECIPE_ENCODER.getItem(), new ItemStack(Material.DIAMOND), NETWORK_RECIPE_ENCODER.getItem(),
        NETWORK_RECIPE_ENCODER.getItem(), NETWORK_RECIPE_ENCODER.getItem(), NETWORK_RECIPE_ENCODER.getItem()
    };

    public static final ItemStack[] LINE_POWER_OUTLET_1 = new ItemStack[]{
        OPTIC_STAR.getItem(), NETWORK_POWER_OUTLET_1.getItem(), OPTIC_STAR.getItem(),
        NETWORK_POWER_OUTLET_1.getItem(), RADIOACTIVE_OPTIC_STAR.getItem(), NETWORK_POWER_OUTLET_1.getItem(),
        OPTIC_STAR.getItem(), NETWORK_POWER_OUTLET_1.getItem(), OPTIC_STAR.getItem()
    };

    public static final ItemStack[] LINE_POWER_OUTLET_2 = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.LINE_POWER_OUTLET_1, OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), NETWORK_CAPACITOR_1.getItem(), OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] LINE_POWER_OUTLET_3 = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.LINE_POWER_OUTLET_2, OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), NETWORK_CAPACITOR_1.getItem(), OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] LINE_POWER_OUTLET_4 = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.LINE_POWER_OUTLET_3, OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), NETWORK_CAPACITOR_1.getItem(), OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] LINE_POWER_OUTLET_5 = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.ALUMINUM_BRONZE_INGOT,
        OPTIC_GLASS.getItem(),
        SlimefunItems.SYNTHETIC_SAPPHIRE,
        ExpansionItemStacks.LINE_POWER_OUTLET_4,
        SlimefunItems.SYNTHETIC_SAPPHIRE,
        OPTIC_GLASS.getItem(),
        SlimefunItems.ALUMINUM_BRONZE_INGOT,
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] LINE_POWER_OUTLET_6 = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.ALUMINUM_BRASS_INGOT,
        OPTIC_GLASS.getItem(),
        SlimefunItems.SYNTHETIC_DIAMOND,
        ExpansionItemStacks.LINE_POWER_OUTLET_5,
        SlimefunItems.SYNTHETIC_DIAMOND,
        OPTIC_GLASS.getItem(),
        SlimefunItems.ALUMINUM_BRASS_INGOT,
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] LINE_POWER_OUTLET_7 = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.HARDENED_METAL_INGOT,
        OPTIC_GLASS.getItem(),
        SlimefunItems.SYNTHETIC_EMERALD,
        ExpansionItemStacks.LINE_POWER_OUTLET_6,
        SlimefunItems.SYNTHETIC_EMERALD,
        OPTIC_GLASS.getItem(),
        SlimefunItems.HARDENED_METAL_INGOT,
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] LINE_POWER_OUTLET_8 = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.REINFORCED_ALLOY_INGOT,
        OPTIC_GLASS.getItem(),
        SlimefunItems.POWER_CRYSTAL,
        ExpansionItemStacks.LINE_POWER_OUTLET_7,
        SlimefunItems.POWER_CRYSTAL,
        OPTIC_GLASS.getItem(),
        SlimefunItems.REINFORCED_ALLOY_INGOT,
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] LINE_POWER_OUTLET_9 = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CARGO_MOTOR,
        OPTIC_GLASS.getItem(),
        SlimefunItems.BLISTERING_INGOT_3,
        ExpansionItemStacks.LINE_POWER_OUTLET_8,
        SlimefunItems.BLISTERING_INGOT_3,
        OPTIC_GLASS.getItem(),
        SlimefunItems.CARGO_MOTOR,
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] LINE_POWER_OUTLET_10 = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CARGO_CONNECTOR_NODE,
        OPTIC_GLASS.getItem(),
        SlimefunItems.BLISTERING_INGOT_3,
        ExpansionItemStacks.LINE_POWER_OUTLET_9,
        SlimefunItems.BLISTERING_INGOT_3,
        OPTIC_GLASS.getItem(),
        SlimefunItems.CARGO_CONNECTOR_NODE,
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] LINE_POWER_OUTLET_11 = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CARGO_MANAGER,
        OPTIC_GLASS.getItem(),
        SlimefunItems.BLISTERING_INGOT_3,
        ExpansionItemStacks.LINE_POWER_OUTLET_10,
        SlimefunItems.BLISTERING_INGOT_3,
        OPTIC_GLASS.getItem(),
        SlimefunItems.CARGO_MANAGER,
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] DUE_MACHINE = new ItemStack[]{
        NETWORK_PUSHER.getItem(),
        NETWORK_GRABBER.getItem(),
        NETWORK_PUSHER.getItem(),
        NETWORK_GRABBER.getItem(),
        NETWORK_BRIDGE.getItem(),
        NETWORK_GRABBER.getItem(),
        NETWORK_PUSHER.getItem(),
        NETWORK_GRABBER.getItem(),
        NETWORK_PUSHER.getItem()
    };

    public static final ItemStack[] OFFSETTER = new ItemStack[]{
        null, OPTIC_CABLE.getItem(), null, HOPPER, NETWORK_MONITOR.getItem(), HOPPER, null, OPTIC_CABLE.getItem(), null
    };

    @Deprecated
    public static final ItemStack[] BETTER_GRABBER = new ItemStack[]{
        OPTIC_STAR.getItem(), NETWORK_PUSHER.getItem(), OPTIC_STAR.getItem(),
        NETWORK_PUSHER.getItem(), NETWORK_GRABBER.getItem(), NETWORK_PUSHER.getItem(),
        OPTIC_STAR.getItem(), NETWORK_PUSHER.getItem(), OPTIC_STAR.getItem()
    };
    public static final ItemStack[] NETWORK_CRAFTING_GRID_NEW_STYLE = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_CRAFTING_GRID.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.NETWORK_GRID_NEW_STYLE, NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_CRAFTING_GRID.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] STATUS_VIEWER = new ItemStack[]{
        SYNTHETIC_EMERALD_SHARD.getItem(), OPTIC_CABLE.getItem(), SYNTHETIC_EMERALD_SHARD.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        SYNTHETIC_EMERALD_SHARD.getItem(), OPTIC_CABLE.getItem(), SYNTHETIC_EMERALD_SHARD.getItem()
    };

    public static final ItemStack[] QUANTUM_MANAGER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_QUANTUM_STORAGE_0.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_QUANTUM_STORAGE_0.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_QUANTUM_STORAGE_0.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] DRAWER_MANAGER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_1, NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_1, NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.CARGO_STORAGE_UNIT_1, NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] ITEM_FLOW_VIEWER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_GRID.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_GRID.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_GRID.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] ADVANCED_VACUUM = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), RADIOACTIVE_OPTIC_STAR.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_VACUUM.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), RADIOACTIVE_OPTIC_STAR.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] CRAFTER_MANAGER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_AUTO_CRAFTER.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_AUTO_CRAFTER.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_AUTO_CRAFTER.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] SWITCHING_MONITOR = new ItemStack[]{
        NETWORK_MONITOR.getItem(), NETWORK_MONITOR.getItem(), NETWORK_MONITOR.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_GRID.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_MONITOR.getItem(), NETWORK_MONITOR.getItem(), NETWORK_MONITOR.getItem()
    };

    public static final ItemStack[] HANGING_GRID_NEW_STYLE = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.NETWORK_GRID_NEW_STYLE, NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.NETWORK_GRID_NEW_STYLE, NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.NETWORK_GRID_NEW_STYLE, NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] WHITELISTED_TRANSFER_GRABBER = new ItemStack[]{
        OPTIC_STAR.getItem(), NETWORK_PUSHER.getItem(), OPTIC_STAR.getItem(),
        NETWORK_PUSHER.getItem(), NETWORK_GRABBER.getItem(), NETWORK_PUSHER.getItem(),
        OPTIC_STAR.getItem(), NETWORK_PUSHER.getItem(), OPTIC_STAR.getItem()
    };

    public static final ItemStack[] WHITELISTED_LINE_TRANSFER_GRABBER = new ItemStack[]{
        OPTIC_STAR.getItem(), ExpansionItemStacks.WHITELISTED_TRANSFER_GRABBER, OPTIC_STAR.getItem(),
        ExpansionItemStacks.WHITELISTED_TRANSFER_GRABBER, NETWORK_GRABBER.getItem(), ExpansionItemStacks.WHITELISTED_TRANSFER_GRABBER,
        OPTIC_STAR.getItem(), ExpansionItemStacks.WHITELISTED_TRANSFER_GRABBER, OPTIC_STAR.getItem()
    };

    public static final ItemStack[] WHITELISTED_TRANSFER_VANILLA_GRABBER = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        new ItemStack(Material.HOPPER), ExpansionItemStacks.WHITELISTED_TRANSFER_GRABBER, new ItemStack(Material.HOPPER),
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
    };

    public static final ItemStack[] WHITELISTED_LINE_TRANSFER_VANILLA_GRABBER = new ItemStack[]{
        OPTIC_STAR.getItem(), ExpansionItemStacks.WHITELISTED_TRANSFER_VANILLA_GRABBER, OPTIC_STAR.getItem(),
        ExpansionItemStacks.WHITELISTED_TRANSFER_VANILLA_GRABBER, NETWORK_GRABBER.getItem(), ExpansionItemStacks.WHITELISTED_TRANSFER_VANILLA_GRABBER,
        OPTIC_STAR.getItem(), ExpansionItemStacks.WHITELISTED_TRANSFER_VANILLA_GRABBER, OPTIC_STAR.getItem()
    };

    public static final ItemStack[] SMART_NETWORK_CRAFTING_GRID_NEW_STYLE = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.NETWORK_CRAFTING_GRID_NEW_STYLE, NETWORK_BRIDGE.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] ADVANCED_WIRELESS_TRANSMITTER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_WIRELESS_TRANSMITTER.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_WIRELESS_RECEIVER.getItem(), NETWORK_WIRELESS_TRANSMITTER.getItem(), NETWORK_WIRELESS_RECEIVER.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_WIRELESS_TRANSMITTER.getItem(), NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_VANILLA_GRABBER = new ItemStack[]{
        OPTIC_STAR.getItem(), ExpansionItemStacks.LINE_TRANSFER_VANILLA_GRABBER, OPTIC_STAR.getItem(),
        ExpansionItemStacks.LINE_TRANSFER_VANILLA_GRABBER, NETWORK_GRABBER.getItem(), ExpansionItemStacks.LINE_TRANSFER_VANILLA_GRABBER,
        OPTIC_STAR.getItem(), ExpansionItemStacks.LINE_TRANSFER_VANILLA_GRABBER, OPTIC_STAR.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_VANILLA_PUSHER = new ItemStack[]{
        OPTIC_STAR.getItem(), ExpansionItemStacks.LINE_TRANSFER_VANILLA_PUSHER, OPTIC_STAR.getItem(),
        ExpansionItemStacks.LINE_TRANSFER_VANILLA_PUSHER, NETWORK_PUSHER.getItem(), ExpansionItemStacks.LINE_TRANSFER_VANILLA_PUSHER,
        OPTIC_STAR.getItem(), ExpansionItemStacks.LINE_TRANSFER_VANILLA_PUSHER, OPTIC_STAR.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_VANILLA_PLUS_GRABBER = new ItemStack[]{
        OPTIC_STAR.getItem(), OPTIC_GLASS.getItem(), OPTIC_STAR.getItem(),
        OPTIC_GLASS.getItem(), ExpansionItemStacks.ADVANCED_LINE_TRANSFER_VANILLA_GRABBER, OPTIC_GLASS.getItem(),
        OPTIC_STAR.getItem(), OPTIC_GLASS.getItem(), OPTIC_STAR.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_VANILLA_PLUS_PUSHER = new ItemStack[]{
        OPTIC_STAR.getItem(), OPTIC_GLASS.getItem(), OPTIC_STAR.getItem(),
        OPTIC_GLASS.getItem(), ExpansionItemStacks.ADVANCED_LINE_TRANSFER_VANILLA_PUSHER, OPTIC_GLASS.getItem(),
        OPTIC_STAR.getItem(), OPTIC_GLASS.getItem(), OPTIC_STAR.getItem()
    };

    public static final ItemStack[] ITEM_DIFFERENTER = new ItemStack[]{
        OPTIC_GLASS.getItem(), NETWORK_BRIDGE.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_BRIDGE.getItem(), OPTIC_GLASS.getItem(), NETWORK_BRIDGE.getItem(),
        OPTIC_GLASS.getItem(), NETWORK_BRIDGE.getItem(), OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] STORAGE_CARD_CONVERTER = new ItemStack[]{
        OPTIC_GLASS.getItem(), ExpansionItemStacks.NETWORKS_EXPANSION_WORKBENCH, OPTIC_GLASS.getItem(),
        ExpansionItemStacks.NETWORKS_EXPANSION_WORKBENCH, ExpansionItemStacks.NETWORKS_EXPANSION_WORKBENCH, ExpansionItemStacks.NETWORKS_EXPANSION_WORKBENCH,
        OPTIC_GLASS.getItem(), ExpansionItemStacks.NETWORKS_EXPANSION_WORKBENCH, OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] FACING_PRESETTER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
        NETWORK_CONFIGURATOR.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_CONFIGURATOR.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_BRIDGE.getItem(),
    };

    public static final ItemStack[] SUPER_TRASH = new ItemStack[]{
        SlimefunItems.PORTABLE_DUSTBIN, SlimefunItems.PORTABLE_DUSTBIN, SlimefunItems.PORTABLE_DUSTBIN,
        SlimefunItems.PORTABLE_DUSTBIN, SlimefunItems.PORTABLE_DUSTBIN, SlimefunItems.PORTABLE_DUSTBIN,
        SlimefunItems.PORTABLE_DUSTBIN, SlimefunItems.PORTABLE_DUSTBIN, SlimefunItems.PORTABLE_DUSTBIN
    };

    public static final ItemStack[] AUTHOR_TINALNESS = new ItemStack[]{
        SYNTHETIC_EMERALD_SHARD.getItem(), NETWORK_PROBE.getItem(), SYNTHETIC_EMERALD_SHARD.getItem(),
        OPTIC_CABLE.getItem(), SIMPLE_NANOBOTS.getItem(), OPTIC_CABLE.getItem(),
        SYNTHETIC_EMERALD_SHARD.getItem(), ExpansionItemStacks.STATUS_VIEWER, SYNTHETIC_EMERALD_SHARD.getItem()
    };

    // Cell Drive recipe
    public static final ItemStack[] CELL_DRIVE = new ItemStack[]{
        NETWORK_CAPACITOR_2.getItem(), NETWORK_CAPACITOR_2.getItem(), NETWORK_CAPACITOR_2.getItem(),
        NETWORK_BRIDGE.getItem(), CellTier.T3.stack(), NETWORK_BRIDGE.getItem(),
        NETWORK_CAPACITOR_2.getItem(), NETWORK_CAPACITOR_2.getItem(), NETWORK_CAPACITOR_2.getItem()
    };

    public static final ItemStack[] CELL_CLEANER = new ItemStack[]{
        OPTIC_GLASS.getItem(), ExpansionItemStacks.CELL_DRIVE, OPTIC_GLASS.getItem(),
        NETWORK_BRIDGE.getItem(), CellTier.T2.stack(), NETWORK_BRIDGE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] CELL_CONVERTER = new ItemStack[]{
        OPTIC_GLASS.getItem(), ExpansionItemStacks.CELL_CLEANER, OPTIC_GLASS.getItem(),
        NETWORK_BRIDGE.getItem(), NETWORK_QUANTUM_STORAGE_0.getItem(), NETWORK_BRIDGE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem()
    };  // 元件转量子储存：工作台换个芯，中间放个量子储存当核心

    // ---- 元件网络三件套（装配驱动器 / 虚空元件 / 末影驱动器） ----

    public static final ItemStack ENDER_PEARL = new ItemStack(org.bukkit.Material.ENDER_PEARL);
    public static final ItemStack WITHER_ROSE = new ItemStack(org.bukkit.Material.WITHER_ROSE);

    /** 装配驱动器 = 蓝图 + 元件驱动器 + 桥梁。 */
    public static final ItemStack[] ASSEMBLY_DRIVE = new ItemStack[]{
        OPTIC_CABLE.getItem(), ExpansionItemStacks.NETWORK_BRIDGE_ORDINAL, OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), ExpansionItemStacks.CELL_DRIVE, OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.NETWORK_BRIDGE_ORDINAL, OPTIC_CABLE.getItem()
    };

    /** 虚空元件 = 枯萎玫瑰 + 基础存储元件。 */
    public static final ItemStack[] VOID_CELL = new ItemStack[]{
        null, WITHER_ROSE, null,
        WITHER_ROSE, CellTier.T1.stack(), WITHER_ROSE,
        null, WITHER_ROSE, null
    };

    public static final ItemStack[] ENDER_DRIVE = new ItemStack[]{
        OPTIC_GLASS.getItem(), ENDER_PEARL, OPTIC_GLASS.getItem(),
        ENDER_PEARL, ExpansionItemStacks.CELL_DRIVE, ENDER_PEARL,
        OPTIC_GLASS.getItem(), ENDER_PEARL, OPTIC_GLASS.getItem()
    };

    /** 装配卡 = 纸 + 墨囊 + 末影珍珠。 */
    public static final ItemStack PAPER_STACK = new ItemStack(org.bukkit.Material.PAPER);
    public static final ItemStack INK_SAC = new ItemStack(org.bukkit.Material.INK_SAC);

    public static final ItemStack[] ASSEMBLY_CARD = new ItemStack[]{
        PAPER_STACK, PAPER_STACK, PAPER_STACK,
        INK_SAC, ENDER_PEARL, INK_SAC,
        PAPER_STACK, PAPER_STACK, PAPER_STACK
    };

    /** 装配工坊 = 装配卡 ×4 + 网络编码器。 */
    public static final ItemStack[] ASSEMBLY_WORKSHOP = new ItemStack[]{
        null, ExpansionItemStacks.ASSEMBLY_CARD, null,
        ExpansionItemStacks.ASSEMBLY_CARD, NETWORK_RECIPE_ENCODER.getItem(), ExpansionItemStacks.ASSEMBLY_CARD,
        null, ExpansionItemStacks.ASSEMBLY_CARD, null
    };

    /** 自动灌装机 = 装配卡 ×4 + 重生锚。 */
    public static final ItemStack[] CONTAINER_FILLER = new ItemStack[]{
        null, ExpansionItemStacks.ASSEMBLY_CARD, null,
        ExpansionItemStacks.ASSEMBLY_CARD, new ItemStack(Material.RESPAWN_ANCHOR), ExpansionItemStacks.ASSEMBLY_CARD,
        null, ExpansionItemStacks.ASSEMBLY_CARD, null
    };

    /** 频道配置器 = 末影珍珠 + 纸。 */
    public static final ItemStack[] CHANNEL_CONFIGURATOR = new ItemStack[]{
        null, null, null,
        null, PAPER_STACK, null,
        null, ENDER_PEARL, null
    };

    /** 装配超频核心 = 红石 + 金锭 + 末影珍珠。 */
    public static final ItemStack[] OVERCLOCK_CORE = new ItemStack[]{
        null, new ItemStack(org.bukkit.Material.REDSTONE), null,
        new ItemStack(org.bukkit.Material.REDSTONE), new ItemStack(org.bukkit.Material.GOLD_INGOT), new ItemStack(org.bukkit.Material.REDSTONE),
        null, ENDER_PEARL, null
    };

    /** 装配智能核心 = 回响碎片 + 金锭 + 末影珍珠。 */
    public static final ItemStack[] SMART_CORE = new ItemStack[]{
        null, new ItemStack(org.bukkit.Material.ECHO_SHARD), null,
        new ItemStack(org.bukkit.Material.ECHO_SHARD), new ItemStack(org.bukkit.Material.GOLD_INGOT), new ItemStack(org.bukkit.Material.ECHO_SHARD),
        null, ENDER_PEARL, null
    };

    /** 装配监控器 = 铁框 + 望远镜 + 红石。 */
    public static final ItemStack[] ASSEMBLY_MONITOR = new ItemStack[]{
        new ItemStack(org.bukkit.Material.IRON_INGOT), new ItemStack(org.bukkit.Material.SPYGLASS), new ItemStack(org.bukkit.Material.IRON_INGOT),
        new ItemStack(org.bukkit.Material.IRON_INGOT), new ItemStack(org.bukkit.Material.REDSTONE), new ItemStack(org.bukkit.Material.IRON_INGOT),
        new ItemStack(org.bukkit.Material.IRON_INGOT), new ItemStack(org.bukkit.Material.IRON_INGOT), new ItemStack(org.bukkit.Material.IRON_INGOT)
    };

    /** 元件驱动器管理器 = 铁框 + 末影之眼 + 红石。 */
    public static final ItemStack[] DRIVE_MONITOR = new ItemStack[]{
        new ItemStack(org.bukkit.Material.IRON_INGOT), new ItemStack(org.bukkit.Material.ENDER_EYE), new ItemStack(org.bukkit.Material.IRON_INGOT),
        new ItemStack(org.bukkit.Material.IRON_INGOT), new ItemStack(org.bukkit.Material.REDSTONE), new ItemStack(org.bukkit.Material.IRON_INGOT),
        new ItemStack(org.bukkit.Material.IRON_INGOT), new ItemStack(org.bukkit.Material.IRON_INGOT), new ItemStack(org.bukkit.Material.IRON_INGOT)
    };

    public static final ItemStack[] CHAIN_GRABBER = new ItemStack[]{
        NETWORK_GRABBER.getItem(), OPTIC_CABLE.getItem(), NETWORK_GRABBER.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_GRABBER.getItem(), OPTIC_CABLE.getItem(), NETWORK_GRABBER.getItem()
    };

    public static final ItemStack[] CHAIN_PUSHER = new ItemStack[]{
        NETWORK_PUSHER.getItem(), OPTIC_CABLE.getItem(), NETWORK_PUSHER.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_PUSHER.getItem(), OPTIC_CABLE.getItem(), NETWORK_PUSHER.getItem()
    };

    public static final ItemStack[] CHAIN_TRANSCEIVER = new ItemStack[]{
        NETWORK_GRABBER.getItem(), OPTIC_CABLE.getItem(), NETWORK_PUSHER.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        NETWORK_PUSHER.getItem(), OPTIC_CABLE.getItem(), NETWORK_GRABBER.getItem()
    };

    public static final ItemStack[] CHAIN_MODULE_RANGE = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_MONITOR.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] CHAIN_MODULE_CAPACITY = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_MORE_PUSHER.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] CHAIN_MODULE_MODE = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_CONFIGURATOR.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] CHAIN_MODULE_VANILLA = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_VANILLA_GRABBER.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] CHAIN_MODULE_MULTI_DIRECTION = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_WIRELESS_TRANSMITTER.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] CHAIN_MODULE_BINDING = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_IMPORT.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] CHAIN_CONFIGURATOR = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_CABLE.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_WIRELESS_CONFIGURATOR.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(), ExpansionItemStacks.CHAIN_MODULE_BINDING, OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] CHAIN_BRUSH = new ItemStack[]{
        null, OPTIC_CABLE.getItem(), null,
        null, NETWORK_WIRELESS_CONFIGURATOR.getItem(), null,
        null, ExpansionItemStacks.CHAIN_MODULE_BINDING, null
    };

    public static final ItemStack[] VISUAL_GRID = new ItemStack[]{
        OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(), NETWORK_GRID.getItem(), OPTIC_GLASS.getItem(),
        OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem(), OPTIC_GLASS.getItem()
    };
}
