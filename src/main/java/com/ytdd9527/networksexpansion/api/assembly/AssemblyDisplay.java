package com.ytdd9527.networksexpansion.api.assembly;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.implementation.machines.assembly.AssemblyDrive;
import com.ytdd9527.networksexpansion.utils.GhostItems;
import com.ytdd9527.networksexpansion.utils.NumberFormat;
import io.github.sefiraat.networks.network.NetworkRoot;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class AssemblyDisplay {

    private AssemblyDisplay() {
    }

    public static void refreshIdleStatusIcon(
        @NotNull BlockMenu blockMenu,
        @NotNull MachineState state,
        @Nullable NetworkRoot root) {
        boolean hasRoot = root != null;
        if (state.lastStatusEnabled != null && state.lastStatusHadRoot != null
            && state.lastStatusEnabled == state.enabled
            && state.lastStatusHadRoot == hasRoot) {
            return;
        }
        refreshStatusIcon(blockMenu, state.enabled, root);
        state.lastStatusEnabled = state.enabled;
        state.lastStatusHadRoot = hasRoot;
    }

    public static void refreshDisplays(
        @NotNull BlockMenu blockMenu,
        @NotNull MachineState state,
        @Nullable NetworkRoot root,
        @Nullable CachedSnapshot snapshot) {
        if (state.lastStatusEnabled == null || state.lastStatusHadRoot == null
            || state.lastStatusEnabled != state.enabled
            || state.lastStatusHadRoot != (root != null)) {
            refreshStatusIcon(blockMenu, state.enabled, root);
            state.lastStatusEnabled = state.enabled;
            state.lastStatusHadRoot = root != null;
        }
        refreshTaskRows(blockMenu, state, snapshot);
        if (state.lastToggleEnabled == null || state.lastToggleEnabled != state.enabled) {
            refreshToggleIcon(blockMenu, state.enabled);
            state.lastToggleEnabled = state.enabled;
        }
    }

    private static void refreshStatusIcon(
            @NotNull BlockMenu blockMenu,
            boolean enabled,
            @Nullable NetworkRoot root) {
        ItemStack display = new ItemStack(Material.OBSERVER);
        display.editMeta(meta -> {
            meta.setDisplayName(Lang.getString(CellnetText.ASSEMBLY_STATUS_NAME));
            List<String> lore = new ArrayList<>();
            if (enabled) {
                lore.add(Lang.getString(CellnetText.ASSEMBLY_STATUS_PHASE, Lang.getString(root == null
                    ? CellnetText.ASSEMBLY_PHASE_NO_NETWORK
                    : CellnetText.ASSEMBLY_PHASE_ACTIVE)));
            }
            meta.setLore(lore);
        });
        GhostItems.mark(display);
        blockMenu.replaceExistingItem(AssemblyDrive.STATUS_SLOT, display);
    }

    private static void refreshTaskRows(
            @NotNull BlockMenu blockMenu,
            @NotNull MachineState state,
            @Nullable CachedSnapshot snapshot) {
        for (int i = 0; i < AssemblyDrive.RECIPE_SLOTS.length; i++) {
            int slot = AssemblyDrive.RECIPE_SLOTS[i];
            ItemStack blueprint = blockMenu.getItemInSlot(slot);
            Recipe recipe = AssemblyRound.readRecipe(blockMenu, i, blueprint, state);
            boolean hasLine = recipe != null && blueprint != null;
            long target = hasLine ? state.cachedTarget[i] : 0L;
            long mode = hasLine ? state.cachedMode[i] : AssemblyDrive.MODE_CONTINUOUS;
            long remaining = hasLine ? state.cachedRemaining[i] : 0L;
            long keep = hasLine && mode == AssemblyDrive.MODE_REPLENISH ? state.cachedKeep[i] : 0L;
            long lineCrafted = Math.max(0L, state.craftedSince[i]);
            long craftedSig = mode == AssemblyDrive.MODE_REPLENISH && keep > 0L && snapshot != null ? lineCrafted : 0L;
            int gear = hasLine ? state.cachedGear[i] : 1;
            boolean blocked = recipe != null && state.blocked[i];
            boolean empty = recipe == null || target > AssemblyDrive.MAX_TARGET;

            RowDisplay cache = state.rowDisplays[i];
            if (cache == null) {
                cache = new RowDisplay();
                state.rowDisplays[i] = cache;
            }
            if (cache.icon != null
                && cache.empty == empty
                && cache.recipe == recipe
                && cache.snapshotRef == snapshot
                && cache.mode == mode
                && cache.target == target
                && cache.remaining == remaining
                && cache.keep == keep
                && cache.crafted == craftedSig
                && cache.gear == gear
                && cache.blocked == blocked) {
                continue;
            }

            ItemStack display = empty
                ? Icon.ASSEMBLY_TASK_EMPTY
                : buildTaskRowIcon(recipe, mode, target, remaining, keep, lineCrafted, gear, blocked, snapshot);
            GhostItems.mark(display);
            blockMenu.replaceExistingItem(AssemblyDrive.TASK_DISPLAY_SLOTS[i], display);
            cache.recipe = recipe;
            cache.snapshotRef = snapshot;
            cache.mode = mode;
            cache.target = target;
            cache.remaining = remaining;
            cache.keep = keep;
            cache.crafted = craftedSig;
            cache.gear = gear;
            cache.blocked = blocked;
            cache.empty = empty;
            cache.icon = display;
        }
    }

    @NotNull
    private static ItemStack buildTaskRowIcon(
            @NotNull Recipe recipe,
            long mode,
            long target,
            long remaining,
            long keep,
            long lineCrafted,
            int gear,
            boolean blocked,
            @Nullable CachedSnapshot snapshot) {
        ItemStack display = recipe.output().clone();
        display.setAmount(1);
        display.editMeta(meta -> {
            meta.setDisplayName(Lang.getString(CellnetText.ASSEMBLY_TASK_ROW_TITLE));
            List<String> lore = new ArrayList<>();
            lore.add(Lang.getString(CellnetText.ASSEMBLY_TASK_GEAR, gearScale(gear), gearWord(gear)));
            if (gear >= AssemblyRound.GEAR_MAX) {
                lore.add(Lang.getString(CellnetText.ASSEMBLY_TASK_GEAR_MAX_NOTE));
            }
            if (blocked) {
                lore.add(Lang.getString(CellnetText.ASSEMBLY_TASK_BLOCKED));
            } else if (mode == AssemblyDrive.MODE_PLAN) {
                if (target <= 0L) {
                    lore.add(Lang.getString(CellnetText.ASSEMBLY_TASK_PLAN_UNSET));
                } else if (remaining <= 0L) {
                    lore.add(Lang.getString(CellnetText.ASSEMBLY_TASK_PLAN_DONE));
                } else {
                    lore.add(Lang.getString(CellnetText.ASSEMBLY_TASK_PLAN, NumberFormat.formatNumber(remaining)));
                }
            } else if (mode == AssemblyDrive.MODE_REPLENISH) {
                lore.add(Lang.getString(keep > 0L
                    ? CellnetText.ASSEMBLY_TASK_REPLENISH
                    : CellnetText.ASSEMBLY_TASK_REPLENISH_UNSET,
                    NumberFormat.formatNumber(keep)));
            } else {
                lore.add(Lang.getString(CellnetText.ASSEMBLY_TASK_CONTINUOUS));
            }
            lore.add(Lang.getString(CellnetText.ASSEMBLY_TASK_HINT));
            if (mode == AssemblyDrive.MODE_REPLENISH && keep > 0L && snapshot != null) {
                long stock = AssemblyRound.snapshotStock(snapshot, recipe.outputExactKey()) + lineCrafted;
                String status = Lang.getString(stock >= keep
                    ? CellnetText.ASSEMBLY_TASK_REPLENISH_OK
                    : CellnetText.ASSEMBLY_TASK_REPLENISH_DOING);
                lore.add(Lang.getString(CellnetText.ASSEMBLY_TASK_REPLENISH_STOCK,
                    NumberFormat.formatNumber(stock), NumberFormat.formatNumber(keep), status));
            } else if (snapshot != null) {
                long craftable = AssemblyRound.craftableFromSnapshot(recipe, snapshot);
                lore.add(craftable == Long.MAX_VALUE
                    ? Lang.getString(CellnetText.ASSEMBLY_TASK_AVAILABLE_INFINITE)
                    : Lang.getString(CellnetText.ASSEMBLY_TASK_AVAILABLE, NumberFormat.formatNumber(craftable)));
            }
            meta.setLore(lore);
        });
        return display;
    }

    @NotNull
    private static String gearScale(int gear) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < AssemblyRound.GEAR_LADDER.length; i++) {
            sb.append(Lang.getString(i < gear
                ? CellnetText.ASSEMBLY_GEAR_ON
                : CellnetText.ASSEMBLY_GEAR_OFF));
        }
        return sb.toString();
    }

    @NotNull
    private static String gearWord(int gear) {
        if (gear <= AssemblyRound.GEAR_MIN) {
            return Lang.getString(CellnetText.ASSEMBLY_GEAR_STOP);
        }
        if (gear >= AssemblyRound.GEAR_MAX) {
            return Lang.getString(CellnetText.ASSEMBLY_GEAR_MAX);
        }
        return Lang.getString(CellnetText.ASSEMBLY_GEAR_LEVEL, AssemblyRound.GEAR_LADDER[gear - 1]);
    }

    private static void refreshToggleIcon(@NotNull BlockMenu blockMenu, boolean enabled) {
        ItemStack icon = (enabled
            ? Icon.ASSEMBLY_SWITCH_ON
            : Icon.ASSEMBLY_SWITCH_OFF).clone();
        GhostItems.mark(icon);
        blockMenu.replaceExistingItem(AssemblyDrive.TOGGLE_SLOT, icon);
    }
}
