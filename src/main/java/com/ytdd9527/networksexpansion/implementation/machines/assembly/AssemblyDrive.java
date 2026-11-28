package com.ytdd9527.networksexpansion.implementation.machines.assembly;

import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.Scheduling;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.assembly.AssemblyDisplay;
import com.ytdd9527.networksexpansion.api.assembly.AssemblyMenu;
import com.ytdd9527.networksexpansion.api.assembly.AssemblyRound;
import com.ytdd9527.networksexpansion.api.assembly.CachedSnapshot;
import com.ytdd9527.networksexpansion.api.assembly.DriveRuntimeState;
import com.ytdd9527.networksexpansion.api.assembly.MachineState;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.drive.DriveOwnership;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.core.items.assembly.core.GearCore;
import com.ytdd9527.networksexpansion.implementation.items.assembly.core.SmartCore;
import com.ytdd9527.networksexpansion.utils.Limits;
import com.ytdd9527.networksexpansion.utils.NetworkUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class AssemblyDrive extends SpecialSlimefunItem {

    public static final int STATUS_SLOT = 4;
    public static final int[] STATUS_BACKGROUND = new int[]{0, 1, 2, 3, 5, 6, 7, 8};
    public static final int[] RECIPE_SLOTS = new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17};
    public static final int[] TASK_DISPLAY_SLOTS = new int[]{18, 19, 20, 21, 22, 23, 24, 25, 26};
    public static final int TOGGLE_SLOT = 31;
    public static final int UPGRADE_SLOT = 32;
    public static final int SMART_SLOT = 33;
    public static final int CLEAR_SLOT = 44;
    public static final int[] MAIN_BACKGROUND = new int[]{
        27, 28, 29, 30, 34, 35,
        36, 37, 38, 39, 40, 41, 42, 43
    };

    public static final long MAX_TARGET = Limits.MAX_CRAFT_TARGET;

    public static final long MODE_CONTINUOUS = 0L;

    public static final long MODE_PLAN = 1L;

    public static final long MODE_REPLENISH = 2L;

    private static final String ENABLED_KEY = "assembly_enabled";
    public static final String NAME_KEY = "assembly_name";

    public AssemblyDrive(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@NotNull BlockPlaceEvent event) {
                Location location = event.getBlock().getLocation();
                DriveOwnership.writeOwner(location, event.getPlayer().getUniqueId());
                ensureNetworkNode(location);
            }
        });

        addItemHandler(new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return false;
            }

            @Override
            public void tick(@NotNull Block b, SlimefunItem item, SlimefunBlockData data) {
                tickBody(b);
            }

            private void tickBody(@NotNull Block b) {
                Location location = b.getLocation();
                ensureNetworkNode(location);
                MachineState state = AssemblyRound.runtimeState(location).machineState;
                if (state == null || state.nextRun > System.currentTimeMillis()) {
                    return;
                }
                state.nextRun = System.currentTimeMillis() + AssemblyRound.MIN_INTERVAL_MS;
                Scheduling.runTask(() -> {
                    BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
                    if (blockMenu != null) {
                        tickMain(location, blockMenu, state);
                    } else {
                        AssemblyRound.RUNTIME_STATES.remove(location);
                        AssemblyRound.ACTIVE_DRIVES.remove(location);
                    }
                });
            }
        });

        addItemHandler(new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(@NotNull BlockBreakEvent event, @NotNull ItemStack item, @NotNull List<ItemStack> drops) {
                Player breaker = event.getPlayer();
                Location location = event.getBlock().getLocation();
                if (!DriveOwnership.bypasses(breaker)
                        && !DriveOwnership.passesGates(breaker, location,
                            Interaction.BREAK_BLOCK, canUse(breaker, false))) {
                    event.setCancelled(true);
                    breaker.sendMessage(Lang.getString(CellnetText.DRIVE_BREAK_NOT_ALLOWED));
                    return;
                }
                onAssemblyBreak(event);
            }
        });
    }

    @Override
    public void postRegister() {
        AssemblyMenu.createPreset(this);
    }

    private static void tickMain(
            @NotNull Location location,
            @NotNull BlockMenu blockMenu,
            @NotNull MachineState state) {
        NetworkRoot root = state.enabled ? NetworkUtil.findRoot(location) : null;
        boolean viewer = blockMenu.hasViewer();
        boolean active = false;
        if (root != null) {
            if (!AssemblyRound.flushPendingOutputs(root, location)) {
                state.nextRun = System.currentTimeMillis() + AssemblyRound.BACKOFF_INTERVAL_MS;
                if (viewer) {
                    AssemblyDisplay.refreshDisplays(blockMenu, state, root, null);
                } else {
                    AssemblyDisplay.refreshIdleStatusIcon(blockMenu, state, root);
                }
                return;
            }
            CachedSnapshot snapshot = null;
            if (viewer || state.needsStockSnapshot) {
                CachedSnapshot cached = state.snapshot;
                if (cached == null || cached.expiresAtMs() <= System.currentTimeMillis()) {
                    snapshot = AssemblyRound.sharedSnapshot(root);
                    state.snapshot = snapshot;
                    Arrays.fill(state.craftedSince, 0L);
                } else {
                    snapshot = cached;
                }
            }
            if (Networks.getConfigManager().isAssemblyBatchFetch()) {
                active |= AssemblyRound.craftRound(blockMenu, root, snapshot, state);
            } else {
                for (int i = 0; i < RECIPE_SLOTS.length; i++) {
                    active |= AssemblyRound.craftSlot(blockMenu, root, i, snapshot, state);
                }
            }
            if (viewer) {
                AssemblyDisplay.refreshDisplays(blockMenu, state, root, snapshot);
            } else {
                AssemblyDisplay.refreshIdleStatusIcon(blockMenu, state, root);
            }
        } else if (viewer) {
            AssemblyDisplay.refreshDisplays(blockMenu, state, null, null);
        } else {
            AssemblyDisplay.refreshIdleStatusIcon(blockMenu, state, null);
        }
        state.nextRun = System.currentTimeMillis()
            + (active ? AssemblyRound.MIN_INTERVAL_MS : AssemblyRound.BACKOFF_INTERVAL_MS);
    }

    public static boolean isEnabled(@NotNull Location location) {
        String raw = StorageCacheUtils.getData(location, ENABLED_KEY);
        return raw == null || raw.isEmpty() || Boolean.parseBoolean(raw);
    }

    public static void setEnabled(@NotNull Location location, boolean enabled) {
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(location);
        if (blockData != null) {
            blockData.setData(ENABLED_KEY, Boolean.toString(enabled));
        }
    }

    private static void ensureNetworkNode(@NotNull Location location) {
        if (!NetworkStorage.containsKey(location)) {
            NetworkStorage.registerNode(location, new NodeDefinition(NodeType.BRIDGE));
        }
    }

    private static void onAssemblyBreak(@NotNull BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        NetworkRoot root = NetworkUtil.findRoot(location);
        NetworkStorage.removeNode(location);
        NetworkUtil.invalidate(location);
        BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
        if (blockMenu == null) {
            event.setCancelled(true);
            return;
        }
        DriveRuntimeState removed = AssemblyRound.RUNTIME_STATES.remove(location);
        AssemblyRound.ACTIVE_DRIVES.remove(location);
        if (removed != null) {
            AssemblyRound.dropPending(location, root, removed.pendingOutputs);
        }
        DriveOwnership.clearOwnerCache(location);
        for (int slot : RECIPE_SLOTS) {
            ItemStack item = blockMenu.getItemInSlot(slot);
            if (item == null || item.getType().isAir()) {
                continue;
            }
            blockMenu.replaceExistingItem(slot, null);
            if (!AssemblyRound.isSlotMarker(item) && location.getWorld() != null) {
                location.getWorld().dropItemNaturally(location.clone().add(0.5, 1.0, 0.5), item);
            }
        }
        ItemStack core = blockMenu.getItemInSlot(UPGRADE_SLOT);
        if (core != null && !core.getType().isAir()
            && GearCore.isUpgradeCore(core) && location.getWorld() != null) {
            blockMenu.replaceExistingItem(UPGRADE_SLOT, null);
            location.getWorld().dropItemNaturally(location.clone().add(0.5, 1.0, 0.5), core);
        }
        ItemStack smart = blockMenu.getItemInSlot(SMART_SLOT);
        if (smart != null && !smart.getType().isAir()
            && SmartCore.isSmartCore(smart) && location.getWorld() != null) {
            blockMenu.replaceExistingItem(SMART_SLOT, null);
            location.getWorld().dropItemNaturally(location.clone().add(0.5, 1.0, 0.5), smart);
        }
    }
}
