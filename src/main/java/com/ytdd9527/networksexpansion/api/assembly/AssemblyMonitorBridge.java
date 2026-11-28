package com.ytdd9527.networksexpansion.api.assembly;

import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellSlotUI;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.drive.DriveOwnership;
import com.ytdd9527.networksexpansion.implementation.items.assembly.core.OverclockCore;
import com.ytdd9527.networksexpansion.implementation.items.assembly.core.SmartCore;
import com.ytdd9527.networksexpansion.implementation.machines.assembly.AssemblyDrive;
import com.ytdd9527.networksexpansion.utils.ChatInput;
import com.ytdd9527.networksexpansion.utils.Limits;
import com.ytdd9527.networksexpansion.utils.NetworkUtil;
import io.github.sefiraat.networks.utils.Keys;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class AssemblyMonitorBridge {

    public static final long MODE_CONTINUOUS = AssemblyDrive.MODE_CONTINUOUS;
    public static final long MODE_PLAN = AssemblyDrive.MODE_PLAN;
    public static final long MODE_REPLENISH = AssemblyDrive.MODE_REPLENISH;

    private AssemblyMonitorBridge() {
    }

    @Nullable
    public static DriveOverview readOverview(@NotNull Location location) {
        BlockMenu menu = StorageCacheUtils.getMenu(location);
        if (menu == null) {
            return null;
        }
        MachineState state = AssemblyRound.runtimeState(location).machineState;
        boolean enabled = state != null && state.enabled;
        int maxGear = state != null ? state.maxGear : AssemblyRound.GEAR_MAX - 1;
        boolean overclock = OverclockCore.isOverclockCore(menu.getItemInSlot(AssemblyDrive.UPGRADE_SLOT));
        boolean smart = SmartCore.isSmartCore(menu.getItemInSlot(AssemblyDrive.SMART_SLOT));
        List<MonitorLine> lines = new ArrayList<>();
        for (int i = 0; i < AssemblyDrive.RECIPE_SLOTS.length; i++) {
            ItemStack blueprint = menu.getItemInSlot(AssemblyDrive.RECIPE_SLOTS[i]);
            Recipe recipe = AssemblyRound.readRecipe(menu, i, blueprint, state);
            if (recipe == null || blueprint == null) {
                continue;
            }
            long mode = state != null ? state.cachedMode[i]
                : BlueprintMeta.getMetaLong(blueprint, Keys.CRAFT_MODE, AssemblyDrive.MODE_CONTINUOUS);
            int gear = state != null ? state.cachedGear[i] : Math.min(AssemblyRound.gearOf(blueprint), maxGear);
            long target = mode == AssemblyDrive.MODE_PLAN
                ? (state != null ? state.cachedTarget[i]
                    : BlueprintMeta.getMetaLong(blueprint, Keys.CRAFT_TARGET, 0L))
                : 0L;
            long remaining = target > 0
                ? (state != null ? state.cachedRemaining[i]
                    : BlueprintMeta.getMetaLong(blueprint, Keys.CRAFT_REMAINING, target))
                : 0L;
            long keep = mode == AssemblyDrive.MODE_REPLENISH
                ? (state != null ? state.cachedKeep[i]
                    : BlueprintMeta.getMetaLong(blueprint, Keys.CRAFT_KEEP, 0L))
                : 0L;
            boolean blocked = state != null && state.blocked[i];
            lines.add(new MonitorLine(recipe.output().clone(), mode, gear, remaining, keep, blocked,
                mode == AssemblyDrive.MODE_PLAN && target > 0L));
        }
        return new DriveOverview(location, displayName(location), enabled, overclock, smart, lines);
    }

    @NotNull
    public static List<MonitorLine> readLines(@NotNull Location location) {
        DriveOverview overview = readOverview(location);
        return overview == null ? List.of() : overview.lines();
    }

    @NotNull
    public static Set<Location> activeDrives() {
        return AssemblyRound.ACTIVE_DRIVES;
    }

    @Nullable
    public static String displayName(@NotNull Location location) {
        String stored = StorageCacheUtils.getData(location, AssemblyDrive.NAME_KEY);
        return stored == null || stored.isEmpty() ? null : stored;
    }

    public static long gearMultiplier(int gear) {
        if (gear <= AssemblyRound.GEAR_MIN) {
            return 0L;
        }
        if (gear >= AssemblyRound.GEAR_MAX) {
            return -1L;
        }
        return AssemblyRound.GEAR_LADDER[gear - 1];
    }

    public static void setNameFromMonitor(@NotNull Player player, @NotNull Location location, @NotNull String name) {
        if (!DriveOwnership.bypasses(player) && !DriveOwnership.isOwnerOrWhitelisted(player, location)) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NO_PERMISSION));
            return;
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty() || "-".equals(trimmed)) {
            clearNameFromMonitor(player, location);
            return;
        }
        if (trimmed.length() > Limits.MAX_CHANNEL_CODEPOINTS) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NAME_INVALID));
            return;
        }
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(location);
        if (blockData == null) {
            return;
        }
        if (isNameTaken(trimmed, location)) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NAME_DUPLICATE, trimmed));
            return;
        }
        blockData.setData(AssemblyDrive.NAME_KEY, trimmed);
        player.sendMessage(Lang.getString(CellnetText.MONITOR_NAME_SET, trimmed));
    }

    public static void clearNameFromMonitor(@NotNull Player player, @NotNull Location location) {
        if (!DriveOwnership.bypasses(player) && !DriveOwnership.isOwnerOrWhitelisted(player, location)) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NO_PERMISSION));
            return;
        }
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(location);
        if (blockData == null) {
            return;
        }
        blockData.setData(AssemblyDrive.NAME_KEY, "");
        player.sendMessage(Lang.getString(CellnetText.MONITOR_NAME_CLEARED));
    }

    private static boolean isNameTaken(@NotNull String name, @NotNull Location self) {
        for (Location other : AssemblyRound.ACTIVE_DRIVES) {
            if (other.equals(self)) {
                continue;
            }
            String existing = displayName(other);
            if (name.equals(existing)) {
                return true;
            }
        }
        return false;
    }

    public static void cycleLineFromMonitor(@NotNull Player player, @NotNull Location location, int index) {
        BlockMenu menu = StorageCacheUtils.getMenu(location);
        MachineState state = AssemblyRound.runtimeState(location).machineState;
        if (menu == null || state == null || index < 0 || index >= AssemblyDrive.RECIPE_SLOTS.length) {
            return;
        }
        if (!DriveOwnership.bypasses(player) && !DriveOwnership.isOwnerOrWhitelisted(player, location)) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NO_PERMISSION));
            return;
        }
        ItemStack blueprint = menu.getItemInSlot(AssemblyDrive.RECIPE_SLOTS[index]);
        if (blueprint == null || blueprint.getType().isAir() || !AssemblyRound.isAcceptableRecipeItem(blueprint)) {
            return;
        }
        toggleMode(player, menu, index, blueprint, state);
    }

    public static void requestQuantityFromMonitor(@NotNull Player player, @NotNull Location location, int index) {
        BlockMenu menu = StorageCacheUtils.getMenu(location);
        if (menu == null || index < 0 || index >= AssemblyDrive.RECIPE_SLOTS.length) {
            return;
        }
        if (!DriveOwnership.bypasses(player) && !DriveOwnership.isOwnerOrWhitelisted(player, location)) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NO_PERMISSION));
            return;
        }
        ItemStack blueprint = menu.getItemInSlot(AssemblyDrive.RECIPE_SLOTS[index]);
        if (blueprint == null || blueprint.getType().isAir() || !AssemblyRound.isAcceptableRecipeItem(blueprint)) {
            return;
        }
        long mode = BlueprintMeta.getMetaLong(blueprint, Keys.CRAFT_MODE, AssemblyDrive.MODE_CONTINUOUS);
        if (mode == AssemblyDrive.MODE_CONTINUOUS) {
            return;
        }
        handleTargetRequest(player, menu, index, mode == AssemblyDrive.MODE_REPLENISH);
    }

    public static void changeGearFromMonitor(@NotNull Player player, @NotNull Location location, int index, int delta) {
        BlockMenu menu = StorageCacheUtils.getMenu(location);
        MachineState state = AssemblyRound.runtimeState(location).machineState;
        if (menu == null || state == null || index < 0 || index >= AssemblyDrive.RECIPE_SLOTS.length) {
            return;
        }
        if (!DriveOwnership.bypasses(player) && !DriveOwnership.isOwnerOrWhitelisted(player, location)) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NO_PERMISSION));
            return;
        }
        ItemStack blueprint = menu.getItemInSlot(AssemblyDrive.RECIPE_SLOTS[index]);
        if (blueprint == null || blueprint.getType().isAir() || !AssemblyRound.isAcceptableRecipeItem(blueprint)) {
            return;
        }
        int gear = AssemblyRound.gearOf(blueprint);
        int next = delta > 0
            ? Math.min(state.maxGear, gear + 1)
            : Math.max(AssemblyRound.GEAR_MIN, gear - 1);
        if (next == gear) {
            return;
        }
        BlueprintMeta.setMetaLong(blueprint, Keys.CRAFT_GEAR, next);
        AssemblyRound.updateStockFlag(state, index, blueprint);
        menu.replaceExistingItem(AssemblyDrive.RECIPE_SLOTS[index], blueprint);
        AssemblyDisplay.refreshDisplays(menu, state, NetworkUtil.findRoot(location), state.snapshot);
    }

    public static void toggleMode(
        @NotNull Player player,
        @NotNull BlockMenu menu,
        int rowIndex,
        @NotNull ItemStack blueprint,
        @NotNull MachineState state) {
        long mode = BlueprintMeta.getMetaLong(blueprint, Keys.CRAFT_MODE, AssemblyDrive.MODE_CONTINUOUS);
        long next = (mode + 1L) % 3L;
        BlueprintMeta.setMetaLong(blueprint, Keys.CRAFT_MODE, next);
        if (next == AssemblyDrive.MODE_PLAN) {
            long remaining = BlueprintMeta.getMetaLong(blueprint, Keys.CRAFT_REMAINING, 0L);
            if (remaining <= 0L) {
                BlueprintMeta.setMetaLong(blueprint, Keys.CRAFT_TARGET, 0L);
                BlueprintMeta.setMetaLong(blueprint, Keys.CRAFT_REMAINING, 0L);
            }
        }
        AssemblyRound.updateStockFlag(state, rowIndex, blueprint);
        player.sendMessage(Lang.getString(switch ((int) next) {
            case 1 -> CellnetText.ASSEMBLY_PLAN_ON;
            case 2 -> CellnetText.ASSEMBLY_REPLENISH_ON;
            default -> CellnetText.ASSEMBLY_CONTINUOUS_ON;
        }));
        menu.replaceExistingItem(AssemblyDrive.RECIPE_SLOTS[rowIndex], blueprint);
        AssemblyDisplay.refreshDisplays(menu, state, NetworkUtil.findRoot(menu.getLocation()), state.snapshot);
    }

    public static void applyGear(
        @NotNull BlockMenu menu,
        int rowIndex,
        @NotNull ItemStack blueprint,
        @NotNull MachineState state,
        int gear) {
        BlueprintMeta.setMetaLong(blueprint, Keys.CRAFT_GEAR, gear);
        AssemblyRound.updateStockFlag(state, rowIndex, blueprint);
        menu.replaceExistingItem(AssemblyDrive.RECIPE_SLOTS[rowIndex], blueprint);
        AssemblyDisplay.refreshDisplays(menu, state, NetworkUtil.findRoot(menu.getLocation()), state.snapshot);
    }

    public static void handleTargetRequest(
        @NotNull Player player,
        @NotNull BlockMenu menu,
        int rowIndex,
        boolean keep) {
        int blueprintSlot = AssemblyDrive.RECIPE_SLOTS[rowIndex];
        ItemStack blueprint = menu.getItemInSlot(blueprintSlot);
        if (blueprint == null || blueprint.getType().isAir() || !AssemblyRound.isAcceptableRecipeItem(blueprint)) {
            return;
        }
        boolean replaced = ChatInput.request(player,
            ChatInput.InputType.TARGET,
            new ChatInput.TargetContext(menu.getLocation().clone(), blueprintSlot, keep));
        if (replaced) {
            player.sendMessage(Lang.getString(CellnetText.INPUT_PREVIOUS_CANCELLED));
        }
        player.sendMessage(Lang.getString(keep
            ? CellnetText.INPUT_KEEP_HINT
            : CellnetText.INPUT_TARGET_HINT));
    }

    public static void handleTargetInput(@NotNull Location location, int slot, long target, boolean keep) {
        BlockMenu menu = StorageCacheUtils.getMenu(location);
        if (menu == null) {
            return;
        }
        ItemStack blueprint = menu.getItemInSlot(slot);
        if (blueprint == null || blueprint.getType().isAir() || !AssemblyRound.isAcceptableRecipeItem(blueprint)) {
            for (var human : menu.getInventory().getViewers()) {
                if (human instanceof Player p) {
                    p.sendMessage(Lang.getString(CellnetText.ASSEMBLY_TARGET_SLOT_LOST));
                }
            }
            return;
        }
        long capped = Math.max(0L, Math.min(target, AssemblyDrive.MAX_TARGET));
        MachineState state = AssemblyRound.runtimeState(location).machineState;
        if (keep) {
            BlueprintMeta.setMetaLong(blueprint, Keys.CRAFT_KEEP, capped);
        } else {
            BlueprintMeta.setMetaLong(blueprint, Keys.CRAFT_TARGET, capped);
            BlueprintMeta.setMetaLong(blueprint, Keys.CRAFT_REMAINING, capped);
            if (capped <= 0L) {
                BlueprintMeta.setMetaLong(blueprint, Keys.CRAFT_MODE, AssemblyDrive.MODE_CONTINUOUS);
            }
        }
        if (state != null) {
            AssemblyRound.updateStockFlag(state, CellSlotUI.indexOf(AssemblyDrive.RECIPE_SLOTS, slot), blueprint);
        }
        menu.replaceExistingItem(slot, blueprint);
        for (var human : menu.getInventory().getViewers()) {
            if (human instanceof Player p) {
                p.sendMessage(Lang.getString(keep
                    ? CellnetText.ASSEMBLY_KEEP_SET
                    : CellnetText.ASSEMBLY_TARGET_SET, capped));
            }
        }
    }
}
