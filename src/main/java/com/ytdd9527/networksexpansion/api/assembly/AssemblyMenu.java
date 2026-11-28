package com.ytdd9527.networksexpansion.api.assembly;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.api.cell.CellSlotUI;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.drive.DriveOwnership;
import com.ytdd9527.networksexpansion.implementation.items.assembly.core.OverclockCore;
import com.ytdd9527.networksexpansion.implementation.items.assembly.core.SmartCore;
import com.ytdd9527.networksexpansion.implementation.items.drive.ChannelConfigurator;
import com.ytdd9527.networksexpansion.implementation.machines.assembly.AssemblyDrive;
import com.ytdd9527.networksexpansion.implementation.machines.drive.EnderDrive;
import com.ytdd9527.networksexpansion.utils.NetworkUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.utils.Keys;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class AssemblyMenu {

    private AssemblyMenu() {
    }

    public static void createPreset(@NotNull AssemblyDrive drive) {
        new BlockMenuPreset(drive.getId(), drive.getItemName()) {

            @Override
            public void init() {
                setSize(45);
                setPlayerInventoryClickable(true);
                drawBackground(AssemblyDrive.STATUS_BACKGROUND);
                drawBackground(AssemblyDrive.MAIN_BACKGROUND);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return DriveOwnership.bypasses(player)
                        || DriveOwnership.passesGates(player, block.getLocation(),
                            Interaction.INTERACT_BLOCK, drive.canUse(player, false));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public void newInstance(@NotNull BlockMenu menu, @NotNull Block block) {
                DriveRuntimeState rs = AssemblyRound.runtimeState(block.getLocation());
                if (rs.machineState == null) {
                    rs.machineState = new MachineState();
                    rs.machineState.enabled = AssemblyDrive.isEnabled(block.getLocation());
                }
                MachineState state = rs.machineState;
                for (int slot : AssemblyDrive.RECIPE_SLOTS) {
                    ItemStack existing = menu.getItemInSlot(slot);
                    if (existing == null || existing.getType().isAir()) {
                        menu.replaceExistingItem(slot, buildSlotMarker());
                    }
                }
                restoreCoreSlot(menu, AssemblyDrive.UPGRADE_SLOT, Icon.ASSEMBLY_UPGRADE_SLOT, OverclockCore::isOverclockCore);
                restoreCoreSlot(menu, AssemblyDrive.SMART_SLOT, Icon.ASSEMBLY_SMART_SLOT, SmartCore::isSmartCore);
                state.maxGear = OverclockCore.isOverclockCore(menu.getItemInSlot(AssemblyDrive.UPGRADE_SLOT))
                    ? AssemblyRound.GEAR_MAX : AssemblyRound.GEAR_MAX - 1;
                state.nextRun = System.currentTimeMillis()
                    + ThreadLocalRandom.current().nextLong(AssemblyRound.MIN_INTERVAL_MS);
                for (int i = 0; i < AssemblyDrive.RECIPE_SLOTS.length; i++) {
                    AssemblyRound.updateStockFlag(state, i, menu.getItemInSlot(AssemblyDrive.RECIPE_SLOTS[i]));
                }
                AssemblyRound.ACTIVE_DRIVES.add(block.getLocation());
                setupHandlers(menu, block.getLocation(), state);
                Arrays.fill(state.rowDisplays, null);
                state.lastStatusEnabled = null;
                state.lastStatusHadRoot = null;
                state.lastToggleEnabled = null;
                AssemblyDisplay.refreshDisplays(menu, state, NetworkUtil.findRoot(block.getLocation()), null);
            }
        };
    }

    private static void restoreCoreSlot(
            @NotNull BlockMenu menu, int slot, @NotNull ItemStack marker, @NotNull Predicate<ItemStack> coreTest) {
        ItemStack current = menu.getItemInSlot(slot);
        if (current == null || current.getType().isAir() || !coreTest.test(current)) {
            menu.replaceExistingItem(slot, marker);
        }
    }

    private static void setupHandlers(@NotNull BlockMenu menu, @NotNull Location location, @NotNull MachineState state) {
        menu.addMenuClickHandler(AssemblyDrive.STATUS_SLOT, (p, s, i, a) -> false);

        for (int slot : AssemblyDrive.RECIPE_SLOTS) {
            menu.addMenuClickHandler(slot, (player, s, clicked, action) -> {
                handleRecipeSlotClick(player, menu, state, s);
                return false;
            });
        }

        menu.replaceExistingItem(AssemblyDrive.CLEAR_SLOT, buildClearSlotsButton());
        menu.addMenuClickHandler(AssemblyDrive.CLEAR_SLOT, (player, slot, clicked, action) -> {
            clearRecipeSlots(player, menu, state);
            return false;
        });

        for (int i = 0; i < AssemblyDrive.TASK_DISPLAY_SLOTS.length; i++) {
            final int row = i;
            menu.addMenuClickHandler(AssemblyDrive.TASK_DISPLAY_SLOTS[row], (player, slot, clicked, action) -> {
                handleTaskRowClick(player, menu, state, row, action.isRightClicked(), action.isShiftClicked());
                AssemblyDisplay.refreshDisplays(menu, state, NetworkUtil.findRoot(location), null);
                return false;
            });
        }

        menu.addMenuClickHandler(AssemblyDrive.TOGGLE_SLOT, (player, slot, clicked, action) -> {
            boolean newValue = !AssemblyDrive.isEnabled(location);
            AssemblyDrive.setEnabled(location, newValue);
            state.enabled = newValue;
            player.sendMessage(Lang.getString(newValue
                ? CellnetText.ASSEMBLY_SWITCH_ON
                : CellnetText.ASSEMBLY_SWITCH_OFF));
            AssemblyDisplay.refreshDisplays(menu, state, newValue ? NetworkUtil.findRoot(location) : null, null);
            return false;
        });

        menu.addMenuClickHandler(AssemblyDrive.UPGRADE_SLOT, (player, slot, clicked, action) -> {
            handleCoreSlotClick(player, menu, location, state, AssemblyDrive.UPGRADE_SLOT,
                Icon.ASSEMBLY_UPGRADE_SLOT, OverclockCore::isOverclockCore,
                CellnetText.ASSEMBLY_UPGRADE_INVALID);
            return false;
        });

        menu.addMenuClickHandler(AssemblyDrive.SMART_SLOT, (player, slot, clicked, action) -> {
            handleSmartSlotClick(player, menu, location, state);
            return false;
        });

        menu.addMenuOpeningHandler(p -> {
            AssemblyDisplay.refreshDisplays(menu, state, NetworkUtil.findRoot(location), null);
            menu.replaceExistingItem(AssemblyDrive.CLEAR_SLOT, buildClearSlotsButton());
        });
        menu.addMenuCloseHandler(p -> {
            menu.replaceExistingItem(AssemblyDrive.STATUS_SLOT, null);
            for (int slot : AssemblyDrive.TASK_DISPLAY_SLOTS) {
                menu.replaceExistingItem(slot, null);
            }
            menu.replaceExistingItem(AssemblyDrive.TOGGLE_SLOT, null);
            for (int i = 0; i < state.rowDisplays.length; i++) {
                state.rowDisplays[i] = null;
            }
            state.lastStatusEnabled = null;
            state.lastStatusHadRoot = null;
            state.lastToggleEnabled = null;
        });
    }

    private static void handleCoreSlotClick(
            @NotNull Player player,
            @NotNull BlockMenu menu,
            @NotNull Location location,
            @NotNull MachineState state,
            int slot,
            @NotNull ItemStack marker,
            @NotNull Predicate<ItemStack> filter,
            @NotNull String invalidKey) {
        ItemStack cursor = player.getItemOnCursor();
        ItemStack current = menu.getItemInSlot(slot);
        boolean cursorCore = cursor != null && !cursor.getType().isAir() && filter.test(cursor);
        boolean slotCore = current != null && !current.getType().isAir() && filter.test(current);
        if (cursorCore) {
            if (slotCore) {
                ItemStackUtil.giveOrDropItem(player, current);
            }
            CellSlotUI.placeOneFromCursor(player, menu, slot, cursor);
        } else if (cursor == null || cursor.getType().isAir()) {
            if (slotCore) {
                menu.replaceExistingItem(slot, marker);
                player.setItemOnCursor(current);
            }
        } else {
            player.sendMessage(Lang.getString(invalidKey));
        }
        state.maxGear = OverclockCore.isOverclockCore(menu.getItemInSlot(AssemblyDrive.UPGRADE_SLOT))
            ? AssemblyRound.GEAR_MAX : AssemblyRound.GEAR_MAX - 1;
        for (int i = 0; i < AssemblyDrive.RECIPE_SLOTS.length; i++) {
            AssemblyRound.updateStockFlag(state, i, menu.getItemInSlot(AssemblyDrive.RECIPE_SLOTS[i]));
        }
        AssemblyDisplay.refreshDisplays(menu, state, NetworkUtil.findRoot(location), null);
    }

    private static void handleSmartSlotClick(
            @NotNull Player player,
            @NotNull BlockMenu menu,
            @NotNull Location location,
            @NotNull MachineState state) {
        ItemStack cursor = player.getItemOnCursor();
        ItemStack current = menu.getItemInSlot(AssemblyDrive.SMART_SLOT);
        if (ChannelConfigurator.isConfigurator(cursor)) {
            if (current == null || !SmartCore.isSmartCore(current)) {
                player.sendMessage(Lang.getString(CellnetText.ASSEMBLY_SMART_SLOT_NO_CORE));
                return;
            }
            String channel = ChannelConfigurator.getStoredChannel(cursor);
            boolean fresh = channel == null || channel.isEmpty();
            if (fresh) {
                channel = EnderDrive.generateChannel();
                ChannelConfigurator.setStoredChannel(cursor, channel);
            }
            SmartCore.setChannel(current, channel);
            player.sendMessage(Lang.getString(fresh
                ? CellnetText.CONFIGURATOR_BOUND_NEW
                : CellnetText.CONFIGURATOR_BOUND, channel));
            AssemblyDisplay.refreshDisplays(menu, state, NetworkUtil.findRoot(location), null);
            return;
        }
        handleCoreSlotClick(player, menu, location, state, AssemblyDrive.SMART_SLOT,
            Icon.ASSEMBLY_SMART_SLOT, SmartCore::isSmartCore,
            CellnetText.ASSEMBLY_SMART_INVALID);
    }

    private static void clearRecipeSlots(
            @NotNull Player player,
            @NotNull BlockMenu menu,
            @NotNull MachineState state) {
        boolean hasCard = false;
        for (int slot : AssemblyDrive.RECIPE_SLOTS) {
            ItemStack item = menu.getItemInSlot(slot);
            if (item != null && !item.getType().isAir() && !AssemblyRound.isSlotMarker(item)) {
                hasCard = true;
                break;
            }
        }
        if (!hasCard) {
            player.sendMessage(Lang.getString(CellnetText.ASSEMBLY_CLEAR_SLOTS_EMPTY));
            return;
        }
        for (int i = 0; i < AssemblyDrive.RECIPE_SLOTS.length; i++) {
            ItemStack item = menu.getItemInSlot(AssemblyDrive.RECIPE_SLOTS[i]);
            if (item != null && !item.getType().isAir() && !AssemblyRound.isSlotMarker(item)) {
                menu.replaceExistingItem(AssemblyDrive.RECIPE_SLOTS[i], null);
                ItemStackUtil.giveOrDropItem(player, item);
            }
            menu.replaceExistingItem(AssemblyDrive.RECIPE_SLOTS[i], buildSlotMarker());
            AssemblyRound.updateStockFlag(state, i, menu.getItemInSlot(AssemblyDrive.RECIPE_SLOTS[i]));
        }
        AssemblyDisplay.refreshDisplays(menu, state, NetworkUtil.findRoot(menu.getLocation()), null);
        player.sendMessage(Lang.getString(CellnetText.ASSEMBLY_SLOTS_CLEARED));
    }

    private static void handleRecipeSlotClick(@NotNull Player player, @NotNull BlockMenu menu, @NotNull MachineState state, int slot) {
        ItemStack cursor = player.getItemOnCursor();
        ItemStack slotItem = menu.getItemInSlot(slot);
        boolean slotHasCard = slotItem != null && !slotItem.getType().isAir() && !AssemblyRound.isSlotMarker(slotItem);
        if (cursor != null && !cursor.getType().isAir()) {
            if (!AssemblyRound.isAcceptableRecipeItem(cursor)) {
                player.sendMessage(Lang.getString(CellnetText.ASSEMBLY_BLUEPRINT_INVALID));
                return;
            }
            if (slotHasCard) {
                menu.replaceExistingItem(slot, null);
                ItemStackUtil.giveOrDropItem(player, slotItem);
            } else {
                menu.replaceExistingItem(slot, null);
            }
            ItemStack placed = CellSlotUI.placeOneFromCursor(player, menu, slot, cursor);
            AssemblyRound.updateStockFlag(state, CellSlotUI.indexOf(AssemblyDrive.RECIPE_SLOTS, slot), placed);
            return;
        }
        if (slotHasCard) {
            menu.replaceExistingItem(slot, buildSlotMarker());
            player.setItemOnCursor(slotItem);
            AssemblyRound.updateStockFlag(state, CellSlotUI.indexOf(AssemblyDrive.RECIPE_SLOTS, slot), null);
        }
    }

    @NotNull
    private static ItemStack buildSlotMarker() {
        ItemStack marker = Icon.ASSEMBLY_SLOT_MARKER.clone();
        var meta = marker.getItemMeta();
        if (meta != null) {
            PersistentDataAPI.setByte(meta, Keys.ASSEMBLY_SLOT_MARKER, (byte) 1);
            marker.setItemMeta(meta);
        }
        return marker;
    }

    @NotNull
    private static ItemStack buildClearSlotsButton() {
        return Icon.ASSEMBLY_DRIVE_CLEAR_SLOTS;
    }

    private static void handleTaskRowClick(
            @NotNull Player player,
            @NotNull BlockMenu menu,
            @NotNull MachineState state,
            int rowIndex,
            boolean rightClick,
            boolean shift) {
        int slot = AssemblyDrive.RECIPE_SLOTS[rowIndex];
        ItemStack blueprint = menu.getItemInSlot(slot);
        if (blueprint == null || blueprint.getType().isAir() || !AssemblyRound.isAcceptableRecipeItem(blueprint)) {
            return;
        }
        if (shift && rightClick) {
            long mode = BlueprintMeta.getMetaLong(blueprint, Keys.CRAFT_MODE, AssemblyDrive.MODE_CONTINUOUS);
            if (mode == AssemblyDrive.MODE_CONTINUOUS) {
                return;
            }
            AssemblyMonitorBridge.handleTargetRequest(player, menu, rowIndex, mode == AssemblyDrive.MODE_REPLENISH);
            return;
        }
        if (shift) {
            AssemblyMonitorBridge.toggleMode(player, menu, rowIndex, blueprint, state);
            return;
        }
        int current = Math.min(AssemblyRound.gearOf(blueprint), state.maxGear);
        if (rightClick) {
            AssemblyMonitorBridge.applyGear(menu, rowIndex, blueprint, state,
                Math.max(AssemblyRound.GEAR_MIN, current - 1));
            return;
        }
        AssemblyMonitorBridge.applyGear(menu, rowIndex, blueprint, state, Math.min(state.maxGear, current + 1));
    }
}
