package com.ytdd9527.networksexpansion.implementation;

import io.github.sefiraat.networks.slimefun.NetworksSlimefunItemStacks;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.ADVANCED_NANOBOTS;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.AI_CORE;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.CRAFTING_BLUEPRINT;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.EMPOWERED_AI_CORE;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.INTERDIMENSIONAL_PRESENCE;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_AUTO_CRAFTER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_AUTO_CRAFTER_WITHHOLDING;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_BEST_PUSHER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_BRIDGE;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_CAPACITOR_1;
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
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_PURGER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_PUSHER;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_0;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_1;
import static io.github.sefiraat.networks.slimefun.NetworkSlimefunItems.NETWORK_QUANTUM_STORAGE_10;
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
    public static final ItemStack hopper = new ItemStack(Material.HOPPER);

    public static final ItemStack[] NULL = new ItemStack[]{
        null, null, null,
        null, null, null,
        null, null, null
    };

    // Workbench
    public static final ItemStack[] NETWORKS_EXPANSION_WORKBENCH = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), SlimefunItems.ADVANCED_CIRCUIT_BOARD, NETWORK_BRIDGE.getItem(),
        OPTIC_CABLE.getItem(), AI_CORE.getItem(), OPTIC_CABLE.getItem(),
        NETWORK_BRIDGE.getItem(), SlimefunItems.ADVANCED_CIRCUIT_BOARD, NETWORK_BRIDGE.getItem()
    };

    // Line Transfers
    public static final ItemStack[] LINE_TRANSFER_PUSHER = new ItemStack[]{
        NETWORK_PUSHER.getItem(), NETWORK_EXPORT.getItem(), AI_CORE.getItem(),
        NETWORK_EXPORT.getItem(), NETWORK_MONITOR.getItem(), NETWORK_EXPORT.getItem(),
        AI_CORE.getItem(), NETWORK_EXPORT.getItem(), NETWORK_PUSHER.getItem()
    };

    public static final ItemStack[] LINE_TRANSFER_MORE_PUSHER = new ItemStack[]{
        NETWORK_MORE_PUSHER.getItem(), NETWORK_EXPORT.getItem(), AI_CORE.getItem(),
        NETWORK_EXPORT.getItem(), NETWORK_MONITOR.getItem(), NETWORK_EXPORT.getItem(),
        AI_CORE.getItem(), NETWORK_EXPORT.getItem(), NETWORK_MORE_PUSHER.getItem()
    };

    public static final ItemStack[] LINE_TRANSFER_BEST_PUSHER = new ItemStack[]{
        NETWORK_BEST_PUSHER.getItem(), NETWORK_EXPORT.getItem(), AI_CORE.getItem(),
        NETWORK_EXPORT.getItem(), NETWORK_MONITOR.getItem(), NETWORK_EXPORT.getItem(),
        AI_CORE.getItem(), NETWORK_EXPORT.getItem(), NETWORK_BEST_PUSHER.getItem()
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
        EMPOWERED_AI_CORE.getItem(), EMPOWERED_AI_CORE.getItem(), EMPOWERED_AI_CORE.getItem(),
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_PUSHER, NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_MORE_PUSHER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_MORE_PUSHER, NETWORK_BRIDGE.getItem(),
        EMPOWERED_AI_CORE.getItem(), EMPOWERED_AI_CORE.getItem(), EMPOWERED_AI_CORE.getItem(),
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_MORE_PUSHER, NETWORK_BRIDGE.getItem()
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_BEST_PUSHER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_BEST_PUSHER, NETWORK_BRIDGE.getItem(),
        EMPOWERED_AI_CORE.getItem(), EMPOWERED_AI_CORE.getItem(), EMPOWERED_AI_CORE.getItem(),
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
        NETWORK_GRABBER.getItem(), NETWORK_IMPORT.getItem(), AI_CORE.getItem(),
        NETWORK_IMPORT.getItem(), NETWORK_MONITOR.getItem(), NETWORK_IMPORT.getItem(),
        AI_CORE.getItem(), NETWORK_IMPORT.getItem(), NETWORK_GRABBER.getItem()
    };
    public static final ItemStack[] LINE_TRANSFER_PLUS_GRABBER = new ItemStack[]{
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem(),
        OPTIC_CABLE.getItem(), ExpansionItemStacks.LINE_TRANSFER_GRABBER, OPTIC_CABLE.getItem(),
        SHRINKING_BASE.getItem(), OPTIC_CABLE.getItem(), SHRINKING_BASE.getItem()
    };
    public static final ItemStack[] ADVANCED_LINE_TRANSFER_GRABBER = new ItemStack[]{
        NETWORK_BRIDGE.getItem(), ExpansionItemStacks.LINE_TRANSFER_GRABBER, NETWORK_BRIDGE.getItem(),
        EMPOWERED_AI_CORE.getItem(), EMPOWERED_AI_CORE.getItem(), EMPOWERED_AI_CORE.getItem(),
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
        AI_CORE.getItem(),
        NETWORK_BRIDGE.getItem(),
        AI_CORE.getItem(),
        AI_CORE.getItem(),
        AI_CORE.getItem(),
        NETWORK_BRIDGE.getItem(),
        AI_CORE.getItem(),
        ExpansionItemStacks.LINE_TRANSFER_PLUS_GRABBER
    };
    public static final ItemStack[] ADVANCED_LINE_TRANSFER = new ItemStack[]{
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PUSHER,
        EMPOWERED_AI_CORE.getItem(),
        AI_CORE.getItem(),
        EMPOWERED_AI_CORE.getItem(),
        EMPOWERED_AI_CORE.getItem(),
        EMPOWERED_AI_CORE.getItem(),
        AI_CORE.getItem(),
        EMPOWERED_AI_CORE.getItem(),
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_GRABBER
    };

    public static final ItemStack[] ADVANCED_LINE_TRANSFER_PLUS = new ItemStack[]{
        ExpansionItemStacks.ADVANCED_LINE_TRANSFER_PLUS_PUSHER,
        EMPOWERED_AI_CORE.getItem(),
        AI_CORE.getItem(),
        EMPOWERED_AI_CORE.getItem(),
        EMPOWERED_AI_CORE.getItem(),
        EMPOWERED_AI_CORE.getItem(),
        AI_CORE.getItem(),
        EMPOWERED_AI_CORE.getItem(),
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
        AI_CORE.getItem(), AI_CORE.getItem(), AI_CORE.getItem(),
        NETWORK_VANILLA_GRABBER.getItem(), OPTIC_CABLE.getItem(), NETWORK_VANILLA_GRABBER.getItem()
    };

    public static final ItemStack[] LINE_TRANSFER_VANILLA_PUSHER = new ItemStack[]{
        NETWORK_VANILLA_PUSHER.getItem(), OPTIC_CABLE.getItem(), NETWORK_VANILLA_PUSHER.getItem(),
        AI_CORE.getItem(), AI_CORE.getItem(), AI_CORE.getItem(),
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

    public static final ItemStack[] MAGIC_WORKBENCH_BLUEPRINT = new ItemStack[]{
        OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(),
        CRAFTING_BLUEPRINT.getItem(),
        OPTIC_CABLE.getItem(),
        new ItemStack(Material.BOOKSHELF),
        new ItemStack(Material.CRAFTING_TABLE),
        new ItemStack(Material.DISPENSER)
    };

    public static final ItemStack[] ARMOR_FORGE_BLUEPRINT = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        CRAFTING_BLUEPRINT.getItem(), new ItemStack(Material.ANVIL), CRAFTING_BLUEPRINT.getItem(),
        OPTIC_CABLE.getItem(), new ItemStack(Material.DISPENSER), OPTIC_CABLE.getItem()
    };

    public static final ItemStack[] SMELTERY_BLUEPRINT = new ItemStack[]{
        CRAFTING_BLUEPRINT.getItem(),
        new ItemStack(Material.NETHER_BRICK_FENCE),
        CRAFTING_BLUEPRINT.getItem(),
        new ItemStack(Material.NETHER_BRICKS),
        new ItemStack(Material.DISPENSER),
        new ItemStack(Material.NETHER_BRICKS),
        CRAFTING_BLUEPRINT.getItem(),
        SlimefunItems.IGNITION_CHAMBER,
        CRAFTING_BLUEPRINT.getItem()
    };

    public static final ItemStack[] QUANTUM_WORKBENCH_BLUEPRINT = new ItemStack[]{
        OPTIC_CABLE.getItem(), SlimefunItems.ADVANCED_CIRCUIT_BOARD, OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), CRAFTING_BLUEPRINT.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_CABLE.getItem(), SlimefunItems.ADVANCED_CIRCUIT_BOARD, OPTIC_CABLE.getItem()
    };

    public static final ItemStack[] ANCIENT_ALTAR_BLUEPRINT = new ItemStack[]{
        SlimefunItems.ANCIENT_PEDESTAL,
        SlimefunItems.ANCIENT_PEDESTAL,
        SlimefunItems.ANCIENT_PEDESTAL,
        CRAFTING_BLUEPRINT.getItem(),
        SlimefunItems.ANCIENT_ALTAR,
        CRAFTING_BLUEPRINT.getItem(),
        SlimefunItems.ANCIENT_PEDESTAL,
        SlimefunItems.ANCIENT_PEDESTAL,
        SlimefunItems.ANCIENT_PEDESTAL
    };

    public static final ItemStack[] EXPANSION_WORKBENCH_BLUEPRINT = new ItemStack[]{
        NetworksSlimefunItemStacks.NETWORK_BRIDGE,
        SlimefunItems.ANCIENT_PEDESTAL,
        NetworksSlimefunItemStacks.NETWORK_BRIDGE,
        CRAFTING_BLUEPRINT.getItem(),
        ExpansionItemStacks.NETWORKS_EXPANSION_WORKBENCH,
        CRAFTING_BLUEPRINT.getItem(),
        NetworksSlimefunItemStacks.NETWORK_BRIDGE,
        SlimefunItems.ANCIENT_PEDESTAL,
        NetworksSlimefunItemStacks.NETWORK_BRIDGE
    };

    public static final ItemStack[] COMPRESSOR_BLUEPRINT = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ANDROID_MEMORY_CORE, SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.CARBON_CHUNK, SlimefunItems.CARBON_PRESS, SlimefunItems.CARBON_CHUNK,
        SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.CARGO_MOTOR, SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] GRIND_STONE_BLUEPRINT = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        new ItemStack(Material.OAK_FENCE),
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        CRAFTING_BLUEPRINT.getItem(),
        new ItemStack(Material.DISPENSER),
        CRAFTING_BLUEPRINT.getItem(),
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        CRAFTING_BLUEPRINT.getItem(),
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] JUICER_BLUEPRINT = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        new ItemStack(Material.GLASS),
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        CRAFTING_BLUEPRINT.getItem(),
        new ItemStack(Material.NETHER_BRICK_FENCE),
        CRAFTING_BLUEPRINT.getItem(),
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        new ItemStack(Material.DISPENSER),
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] ORE_CRUSHER_BLUEPRINT = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        new ItemStack(Material.NETHER_BRICK_FENCE),
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        new ItemStack(Material.IRON_BARS),
        new ItemStack(Material.DISPENSER),
        new ItemStack(Material.IRON_BARS),
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        CRAFTING_BLUEPRINT.getItem(),
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] PRESSURE_CHAMBER_BLUEPRINT = new ItemStack[]{
        CRAFTING_BLUEPRINT.getItem(),
        new ItemStack(Material.DISPENSER),
        CRAFTING_BLUEPRINT.getItem(),
        new ItemStack(Material.PISTON),
        new ItemStack(Material.GLASS),
        new ItemStack(Material.PISTON),
        new ItemStack(Material.PISTON),
        new ItemStack(Material.CAULDRON),
        new ItemStack(Material.PISTON)
    };

    public static final ItemStack[] MAGIC_WORKBENCH_RECIPE_ENCODER = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.ANDROID_MEMORY_CORE,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        ExpansionItemStacks.MAGIC_WORKBENCH_BLUEPRINT,
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH,
        ExpansionItemStacks.MAGIC_WORKBENCH_BLUEPRINT,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.CARGO_MOTOR,
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] ARMOR_FORGE_RECIPE_ENCODER = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.ANDROID_MEMORY_CORE,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        ExpansionItemStacks.ARMOR_FORGE_BLUEPRINT,
        ExpansionItemStacks.AUTO_ARMOR_FORGE,
        ExpansionItemStacks.ARMOR_FORGE_BLUEPRINT,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.CARGO_MOTOR,
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] SMELTERY_RECIPE_ENCODER = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.ANDROID_MEMORY_CORE,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        ExpansionItemStacks.SMELTERY_BLUEPRINT,
        ExpansionItemStacks.AUTO_SMELTERY,
        ExpansionItemStacks.SMELTERY_BLUEPRINT,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.CARGO_MOTOR,
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] QUANTUM_WORKBENCH_RECIPE_ENCODER = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.ANDROID_MEMORY_CORE,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        ExpansionItemStacks.QUANTUM_WORKBENCH_BLUEPRINT,
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH,
        ExpansionItemStacks.QUANTUM_WORKBENCH_BLUEPRINT,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.CARGO_MOTOR,
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] ANCIENT_ALTAR_RECIPE_ENCODER = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.ANDROID_MEMORY_CORE,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        ExpansionItemStacks.ANCIENT_ALTAR_BLUEPRINT,
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR,
        ExpansionItemStacks.ANCIENT_ALTAR_BLUEPRINT,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.CARGO_MOTOR,
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] EXPANSION_WORKBENCH_RECIPE_ENCODER = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.ANDROID_MEMORY_CORE,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        ExpansionItemStacks.EXPANSION_WORKBENCH_BLUEPRINT,
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH,
        ExpansionItemStacks.EXPANSION_WORKBENCH_BLUEPRINT,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.CARGO_MOTOR,
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] COMPRESSOR_RECIPE_ENCODER = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.ANDROID_MEMORY_CORE,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        ExpansionItemStacks.COMPRESSOR_BLUEPRINT,
        ExpansionItemStacks.AUTO_COMPRESSOR,
        ExpansionItemStacks.COMPRESSOR_BLUEPRINT,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.CARGO_MOTOR,
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] GRIND_STONE_RECIPE_ENCODER = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.ANDROID_MEMORY_CORE,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        ExpansionItemStacks.GRIND_STONE_BLUEPRINT,
        ExpansionItemStacks.AUTO_GRIND_STONE,
        ExpansionItemStacks.GRIND_STONE_BLUEPRINT,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.CARGO_MOTOR,
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] JUICER_RECIPE_ENCODER = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ANDROID_MEMORY_CORE, SlimefunItems.BASIC_CIRCUIT_BOARD,
        ExpansionItemStacks.JUICER_BLUEPRINT, ExpansionItemStacks.AUTO_JUICER, ExpansionItemStacks.JUICER_BLUEPRINT,
        SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.CARGO_MOTOR, SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] ORE_CRUSHER_RECIPE_ENCODER = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.ANDROID_MEMORY_CORE,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        ExpansionItemStacks.ORE_CRUSHER_BLUEPRINT,
        ExpansionItemStacks.AUTO_ORE_CRUSHER,
        ExpansionItemStacks.ORE_CRUSHER_BLUEPRINT,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.CARGO_MOTOR,
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    public static final ItemStack[] PRESSURE_CHAMBER_RECIPE_ENCODER = new ItemStack[]{
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.ANDROID_MEMORY_CORE,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        ExpansionItemStacks.PRESSURE_CHAMBER_BLUEPRINT,
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER,
        ExpansionItemStacks.PRESSURE_CHAMBER_BLUEPRINT,
        SlimefunItems.BASIC_CIRCUIT_BOARD,
        SlimefunItems.CARGO_MOTOR,
        SlimefunItems.BASIC_CIRCUIT_BOARD
    };

    // Crafter
    public static final ItemStack[] AUTO_MAGIC_WORKBENCH = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        OPTIC_CABLE.getItem(),
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.MAGIC_WORKBENCH_BLUEPRINT,
        SIMPLE_NANOBOTS.getItem(),
        ExpansionItemStacks.MAGIC_WORKBENCH_BLUEPRINT,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_MAGIC_WORKBENCH_WITHHOLDING = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_ARMOR_FORGE = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.ARMOR_AUTO_CRAFTER,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.ARMOR_FORGE_BLUEPRINT,
        SIMPLE_NANOBOTS.getItem(),
        ExpansionItemStacks.ARMOR_FORGE_BLUEPRINT,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_ARMOR_FORGE_WITHHOLDING = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.AUTO_ARMOR_FORGE,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ARMOR_FORGE,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_SMELTERY = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.SMELTERY_BLUEPRINT,
        OPTIC_GLASS.getItem(),
        SlimefunItems.ELECTRIC_SMELTERY_2,
        SIMPLE_NANOBOTS.getItem(),
        SlimefunItems.ELECTRIC_SMELTERY_2,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_SMELTERY_WITHHOLDING = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.AUTO_SMELTERY,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_SMELTERY,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_QUANTUM_WORKBENCH = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        NETWORK_QUANTUM_WORKBENCH.getItem(),
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.QUANTUM_WORKBENCH_BLUEPRINT,
        SIMPLE_NANOBOTS.getItem(),
        ExpansionItemStacks.QUANTUM_WORKBENCH_BLUEPRINT,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_QUANTUM_WORKBENCH_WITHHOLDING = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_ANCIENT_ALTAR = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.ANCIENT_ALTAR_BLUEPRINT,
        SIMPLE_NANOBOTS.getItem(),
        ExpansionItemStacks.ANCIENT_ALTAR_BLUEPRINT,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_ANCIENT_ALTAR_WITHHOLDING = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_EXPANSION_WORKBENCH = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.EXPANSION_WORKBENCH_BLUEPRINT,
        SIMPLE_NANOBOTS.getItem(),
        ExpansionItemStacks.EXPANSION_WORKBENCH_BLUEPRINT,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_EXPANSION_WORKBENCH_WITHHOLDING = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_COMPRESSOR = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.COMPRESSOR_BLUEPRINT,
        SIMPLE_NANOBOTS.getItem(),
        ExpansionItemStacks.COMPRESSOR_BLUEPRINT,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_COMPRESSOR_WITHHOLDING = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.AUTO_COMPRESSOR,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_COMPRESSOR,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_GRIND_STONE = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.GRIND_STONE_BLUEPRINT,
        SIMPLE_NANOBOTS.getItem(),
        ExpansionItemStacks.GRIND_STONE_BLUEPRINT,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_GRIND_STONE_WITHHOLDING = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.AUTO_GRIND_STONE,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_GRIND_STONE,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_JUICER = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.JUICER_BLUEPRINT,
        SIMPLE_NANOBOTS.getItem(),
        ExpansionItemStacks.JUICER_BLUEPRINT,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_JUICER_WITHHOLDING = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.AUTO_JUICER,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_JUICER,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_ORE_CRUSHER = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.ORE_CRUSHER_BLUEPRINT,
        SIMPLE_NANOBOTS.getItem(),
        ExpansionItemStacks.ORE_CRUSHER_BLUEPRINT,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_ORE_CRUSHER_WITHHOLDING = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.AUTO_ORE_CRUSHER,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ORE_CRUSHER,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_PRESSURE_CHAMBER = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.PRESSURE_CHAMBER_BLUEPRINT,
        SIMPLE_NANOBOTS.getItem(),
        ExpansionItemStacks.PRESSURE_CHAMBER_BLUEPRINT,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER.getItem(),
        OPTIC_GLASS.getItem()
    };

    public static final ItemStack[] AUTO_PRESSURE_CHAMBER_WITHHOLDING = new ItemStack[]{
        OPTIC_GLASS.getItem(),
        SlimefunItems.CRAFTER_SMART_PORT,
        OPTIC_GLASS.getItem(),
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER,
        OPTIC_GLASS.getItem(),
        NETWORK_AUTO_CRAFTER_WITHHOLDING.getItem(),
        OPTIC_GLASS.getItem()
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

    public static final ItemStack[] ADVANCED_AUTO_MAGIC_WORKBENCH = new ItemStack[]{
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH,
        ExpansionItemStacks.MAGIC_WORKBENCH_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH
    };

    public static final ItemStack[] ADVANCED_AUTO_MAGIC_WORKBENCH_WITHHOLDING = new ItemStack[]{
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH_WITHHOLDING,
        ExpansionItemStacks.MAGIC_WORKBENCH_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_MAGIC_WORKBENCH_WITHHOLDING
    };

    public static final ItemStack[] ADVANCED_AUTO_ARMOR_FORGE = new ItemStack[]{
        ExpansionItemStacks.AUTO_ARMOR_FORGE,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ARMOR_FORGE,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ARMOR_FORGE,
        ExpansionItemStacks.ARMOR_FORGE_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_ARMOR_FORGE
    };

    public static final ItemStack[] ADVANCED_AUTO_ARMOR_FORGE_WITHHOLDING = new ItemStack[]{
        ExpansionItemStacks.AUTO_ARMOR_FORGE_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ARMOR_FORGE_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ARMOR_FORGE_WITHHOLDING,
        ExpansionItemStacks.ARMOR_FORGE_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_ARMOR_FORGE_WITHHOLDING
    };

    public static final ItemStack[] ADVANCED_AUTO_SMELTERY = new ItemStack[]{
        ExpansionItemStacks.AUTO_SMELTERY,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_SMELTERY,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_SMELTERY,
        ExpansionItemStacks.SMELTERY_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_SMELTERY
    };

    public static final ItemStack[] ADVANCED_AUTO_SMELTERY_WITHHOLDING = new ItemStack[]{
        ExpansionItemStacks.AUTO_SMELTERY_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_SMELTERY_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_SMELTERY_WITHHOLDING,
        ExpansionItemStacks.SMELTERY_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_SMELTERY_WITHHOLDING
    };

    public static final ItemStack[] ADVANCED_AUTO_QUANTUM_WORKBENCH = new ItemStack[]{
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH,
        ExpansionItemStacks.QUANTUM_WORKBENCH_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH
    };

    public static final ItemStack[] ADVANCED_AUTO_QUANTUM_WORKBENCH_WITHHOLDING = new ItemStack[]{
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH_WITHHOLDING,
        ExpansionItemStacks.QUANTUM_WORKBENCH_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_QUANTUM_WORKBENCH_WITHHOLDING
    };

    public static final ItemStack[] ADVANCED_AUTO_ANCIENT_ALTAR = {
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR,
        ExpansionItemStacks.ANCIENT_ALTAR_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR
    };

    public static final ItemStack[] ADVANCED_AUTO_ANCIENT_ALTAR_WITHHOLDING = {
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR_WITHHOLDING,
        ExpansionItemStacks.ANCIENT_ALTAR_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_ANCIENT_ALTAR_WITHHOLDING
    };

    public static final ItemStack[] ADVANCED_AUTO_EXPANSION_WORKBENCH = {
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH,
        ExpansionItemStacks.EXPANSION_WORKBENCH_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH
    };

    public static final ItemStack[] ADVANCED_AUTO_EXPANSION_WORKBENCH_WITHHOLDING = {
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH_WITHHOLDING,
        ExpansionItemStacks.EXPANSION_WORKBENCH_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_EXPANSION_WORKBENCH_WITHHOLDING
    };

    public static final ItemStack[] ADVANCED_AUTO_COMPRESSOR = {
        ExpansionItemStacks.AUTO_COMPRESSOR,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_COMPRESSOR,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_COMPRESSOR,
        ExpansionItemStacks.COMPRESSOR_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_COMPRESSOR
    };

    public static final ItemStack[] ADVANCED_AUTO_COMPRESSOR_WITHHOLDING = {
        ExpansionItemStacks.AUTO_COMPRESSOR_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_COMPRESSOR_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_COMPRESSOR_WITHHOLDING,
        ExpansionItemStacks.COMPRESSOR_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_COMPRESSOR_WITHHOLDING
    };

    public static final ItemStack[] ADVANCED_AUTO_GRIND_STONE = {
        ExpansionItemStacks.AUTO_GRIND_STONE,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_GRIND_STONE,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_GRIND_STONE,
        ExpansionItemStacks.GRIND_STONE_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_GRIND_STONE
    };

    public static final ItemStack[] ADVANCED_AUTO_GRIND_STONE_WITHHOLDING = {
        ExpansionItemStacks.AUTO_GRIND_STONE_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_GRIND_STONE_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_GRIND_STONE_WITHHOLDING,
        ExpansionItemStacks.GRIND_STONE_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_GRIND_STONE_WITHHOLDING
    };

    public static final ItemStack[] ADVANCED_AUTO_JUICER = {
        ExpansionItemStacks.AUTO_JUICER,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_JUICER,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_JUICER,
        ExpansionItemStacks.JUICER_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_JUICER
    };

    public static final ItemStack[] ADVANCED_AUTO_JUICER_WITHHOLDING = {
        ExpansionItemStacks.AUTO_JUICER_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_JUICER_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_JUICER_WITHHOLDING,
        ExpansionItemStacks.JUICER_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_JUICER_WITHHOLDING
    };

    public static final ItemStack[] ADVANCED_AUTO_ORE_CRUSHER = {
        ExpansionItemStacks.AUTO_ORE_CRUSHER,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ORE_CRUSHER,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ORE_CRUSHER,
        ExpansionItemStacks.ORE_CRUSHER_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_ORE_CRUSHER
    };

    public static final ItemStack[] ADVANCED_AUTO_ORE_CRUSHER_WITHHOLDING = {
        ExpansionItemStacks.AUTO_ORE_CRUSHER_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ORE_CRUSHER_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_ORE_CRUSHER_WITHHOLDING,
        ExpansionItemStacks.ORE_CRUSHER_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_ORE_CRUSHER_WITHHOLDING
    };

    public static final ItemStack[] ADVANCED_AUTO_PRESSURE_CHAMBER = {
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER,
        ExpansionItemStacks.PRESSURE_CHAMBER_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER
    };

    public static final ItemStack[] ADVANCED_AUTO_PRESSURE_CHAMBER_WITHHOLDING = {
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER_WITHHOLDING,
        ADVANCED_NANOBOTS.getItem(),
        INTERDIMENSIONAL_PRESENCE.getItem(),
        ADVANCED_NANOBOTS.getItem(),
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER_WITHHOLDING,
        ExpansionItemStacks.PRESSURE_CHAMBER_RECIPE_ENCODER,
        ExpansionItemStacks.AUTO_PRESSURE_CHAMBER_WITHHOLDING
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
        OPTIC_STAR.getItem(), NETWORK_CONFIGURATOR.getItem(), OPTIC_STAR.getItem(),
        OPTIC_CABLE.getItem(), NETWORK_QUANTUM_STORAGE_8.getItem(), OPTIC_CABLE.getItem(),
        OPTIC_STAR.getItem(), NETWORK_CONFIGURATOR.getItem(), OPTIC_STAR.getItem()
    };
    // Bridges
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
        EMPOWERED_AI_CORE.getItem(), ADVANCED_NANOBOTS.getItem(), EMPOWERED_AI_CORE.getItem(),
        ADVANCED_NANOBOTS.getItem(), NETWORK_QUANTUM_WORKBENCH.getItem(), ADVANCED_NANOBOTS.getItem(),
        EMPOWERED_AI_CORE.getItem(), ADVANCED_NANOBOTS.getItem(), EMPOWERED_AI_CORE.getItem()
    };
    public static final ItemStack[] CARGO_STORAGE_UNIT_1 = new ItemStack[]{
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(),
        NETWORK_QUANTUM_STORAGE_1.getItem(), NETWORK_BRIDGE.getItem(), NETWORK_QUANTUM_STORAGE_1.getItem(),
        OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem(), OPTIC_CABLE.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_2 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_9.getItem(),
        ExpansionItemStacks.ARMOR_FORGE_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_9.getItem(),
        ExpansionItemStacks.ARMOR_FORGE_BLUEPRINT,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_1,
        ExpansionItemStacks.ARMOR_FORGE_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_9.getItem(),
        ExpansionItemStacks.ARMOR_FORGE_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_9.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_3 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_10.getItem(),
        ExpansionItemStacks.SMELTERY_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_10.getItem(),
        ExpansionItemStacks.SMELTERY_BLUEPRINT,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_2,
        ExpansionItemStacks.SMELTERY_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_10.getItem(),
        ExpansionItemStacks.SMELTERY_BLUEPRINT,
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
        ExpansionItemStacks.EXPANSION_WORKBENCH_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_4.getItem(),
        ExpansionItemStacks.EXPANSION_WORKBENCH_BLUEPRINT,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_8,
        ExpansionItemStacks.EXPANSION_WORKBENCH_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_4.getItem(),
        ExpansionItemStacks.EXPANSION_WORKBENCH_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_4.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_10 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_4.getItem(),
        ExpansionItemStacks.QUANTUM_WORKBENCH_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_4.getItem(),
        ExpansionItemStacks.QUANTUM_WORKBENCH_BLUEPRINT,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_9,
        ExpansionItemStacks.QUANTUM_WORKBENCH_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_4.getItem(),
        ExpansionItemStacks.QUANTUM_WORKBENCH_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_4.getItem()
    };

    public static final ItemStack[] CARGO_STORAGE_UNIT_11 = new ItemStack[]{
        NETWORK_QUANTUM_STORAGE_5.getItem(),
        ExpansionItemStacks.ANCIENT_ALTAR_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_5.getItem(),
        ExpansionItemStacks.ANCIENT_ALTAR_BLUEPRINT,
        ExpansionItemStacks.CARGO_STORAGE_UNIT_10,
        ExpansionItemStacks.ANCIENT_ALTAR_BLUEPRINT,
        NETWORK_QUANTUM_STORAGE_5.getItem(),
        ExpansionItemStacks.ANCIENT_ALTAR_BLUEPRINT,
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
        ExpansionItemStacks.NETWORK_INPUT_ONLY_MONITOR,
        NETWORK_EXPORT.getItem(),
        ExpansionItemStacks.NETWORK_OUTPUT_ONLY_MONITOR,
        NETWORK_IMPORT.getItem(),
        RADIOACTIVE_OPTIC_STAR.getItem(),
        NETWORK_IMPORT.getItem(),
        ExpansionItemStacks.NETWORK_OUTPUT_ONLY_MONITOR,
        NETWORK_EXPORT.getItem(),
        ExpansionItemStacks.NETWORK_INPUT_ONLY_MONITOR
    };

    public static final ItemStack[] OFFSETTER = new ItemStack[]{
        null, OPTIC_CABLE.getItem(), null, hopper, NETWORK_MONITOR.getItem(), hopper, null, OPTIC_CABLE.getItem(), null
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
}
