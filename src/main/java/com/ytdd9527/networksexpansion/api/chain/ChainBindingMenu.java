package com.ytdd9527.networksexpansion.api.chain;

import com.balugaq.netex.api.enums.TransportMode;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellAcceptRules;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.cell.LineState;
import com.ytdd9527.networksexpansion.api.filter.BrowseUI;
import com.ytdd9527.networksexpansion.core.machines.AbstractChainMachine;
import com.ytdd9527.networksexpansion.utils.GhostItems;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ChainBindingMenu {

    private static final int INFO_SLOT = 4;
    private static final int BACK_SLOT = 45;
    public static final int[] DIRECTION_SLOTS = {10, 11, 12, 14, 15, 16};
    private static final int[] MODE_SLOTS = {19, 20, 21, 23, 24, 25};
    public static final BlockFace[] SLOT_FACES = {
        BlockFace.WEST, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN, BlockFace.SOUTH, BlockFace.EAST
    };
    private static final int[] EDIT_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16, 17};
    private static final int EDIT_INFO_SLOT = 0;
    private static final int EDIT_BACK_SLOT = 8;
    private static final int LIST_PREVIEW_LIMIT = 3;

    private static final class Session {
        final Location machine;
        final BlockFace face;
        final int distance;
        int page;

        Session(@NotNull Location machine, @Nullable BlockFace face, int distance) {
            this.machine = machine;
            this.face = face;
            this.distance = distance;
        }
    }

    private ChainBindingMenu() {
    }

    public static boolean in(int @NotNull [] slots, int slot) {
        for (int s : slots) {
            if (s == slot) {
                return true;
            }
        }
        return false;
    }

    public static void openDirections(@NotNull Player player, @NotNull Location machine) {
        Session session = new Session(machine, null, 0);
        ChestMenu menu = new ChestMenu(Lang.getString(CellnetText.BINDING_TITLE_DIRECTIONS));
        menu.setPlayerInventoryClickable(false);
        menu.setEmptySlotsClickable(false);
        for (int slot = 0; slot < 54; slot++) {
            if (slot == INFO_SLOT || slot == BACK_SLOT || in(DIRECTION_SLOTS, slot) || in(MODE_SLOTS, slot)) {
                continue;
            }
            menu.addItem(slot, ChestMenuUtils.getBackground(), (p, s, i, a) -> false);
        }
        menu.addItem(INFO_SLOT, infoIcon(), (p, s, i, a) -> false);
        menu.addItem(BACK_SLOT, BrowseUI.backButton(), (p, s, i, a) -> {
            BlockMenu machineMenu = StorageCacheUtils.getMenu(machine);
            if (machineMenu != null) {
                machineMenu.open(p);
            }
            return false;
        });
        renderDirections(menu, session);
        menu.open(player);
    }

    private static void renderDirections(@NotNull ChestMenu menu, @NotNull Session session) {
        AbstractChainMachine machine = AbstractChainMachine.machineAt(session.machine);
        for (int i = 0; i < SLOT_FACES.length; i++) {
            BlockFace face = SLOT_FACES[i];
            boolean enabled = machine != null && isEnabled(session.machine, face);
            int boundCount = ChainBindingStore.load(session.machine, face).size();
            int lineLength = machine == null ? 0 : targetLine(machine, session.machine, face).size();
            ItemStack icon = directionIcon(session.machine, face, enabled);
            icon.editMeta(meta -> {
                meta.setDisplayName(Lang.getString(CellnetText.BINDING_DIRECTION_NAME, directionName(face)));
                List<String> lore = new ArrayList<>();
                lore.add(Lang.getString(CellnetText.BINDING_DIRECTION_LORE, lineLength, boundCount));
                lore.add("");
                lore.add(Lang.getString(CellnetText.BINDING_HINT_EDIT));
                lore.add(Lang.getString(CellnetText.BRUSH_DIR_HINT_DRAG));
                meta.setLore(lore);
            });
            GhostItems.mark(icon);
            menu.replaceExistingItem(DIRECTION_SLOTS[i], icon);
            menu.addMenuClickHandler(DIRECTION_SLOTS[i], (p, s, it, a) -> {
                if (!enabled) {
                    p.sendMessage(Lang.getString(CellnetText.BINDING_DIRECTION_CLOSED));
                    return false;
                }
                if (a.isRightClicked()) {
                    openDrag(p, session.machine, face);
                } else {
                    openTargets(p, session.machine, face);
                }
                return false;
            });

            boolean modeUnlocked = AbstractChainMachine.hasModule(session.machine, ChainModule.MODE);
            TransportMode override = modeUnlocked
                ? ChainBindingStore.loadDirMode(session.machine, face) : null;
            ItemStack modeIcon = (override != null ? Icon.CHAIN_BINDING_MODE_ON : Icon.LINE_MODE_OFF).clone();
            modeIcon.editMeta(meta -> {
                meta.setDisplayName(Lang.getString(CellnetText.BINDING_MODE_NAME));
                List<String> lore = new ArrayList<>();
                lore.add(modeRow(override == null,
                    Lang.getString(CellnetText.BINDING_MODE_FOLLOW)));
                for (TransportMode mode : TransportMode.values()) {
                    lore.add(modeRow(override == mode,
                        Lang.getString(CellnetText.LINE_MODES_PREFIX + mode.name())));
                }
                lore.add(Lang.getString(modeUnlocked
                    ? CellnetText.BINDING_MODE_HINT
                    : CellnetText.LINE_MODE_LOCKED));
                meta.setLore(lore);
            });
            GhostItems.mark(modeIcon);
            menu.replaceExistingItem(MODE_SLOTS[i], modeIcon);
            menu.addMenuClickHandler(MODE_SLOTS[i], (p, s, it, a) -> {
                cycleDirMode(p, session, menu, face, !a.isRightClicked());
                return false;
            });
        }
    }

    private static void cycleDirMode(
        @NotNull Player player, @NotNull Session session, @NotNull ChestMenu menu,
        @NotNull BlockFace face, boolean forward) {
        if (!validate(player, session, false)) {
            renderDirections(menu, session);
            return;
        }
        if (!AbstractChainMachine.hasModule(session.machine, ChainModule.MODE)) {
            player.sendMessage(Lang.getString(CellnetText.LINE_MODE_LOCKED));
            renderDirections(menu, session);
            return;
        }
        TransportMode current = ChainBindingStore.loadDirMode(session.machine, face);
        TransportMode next = cycleMode(current, forward);
        ChainBindingStore.saveDirMode(session.machine, face, next);
        LineState state = AbstractChainMachine.state(session.machine);
        Map<BlockFace, TransportMode> overrides = state.dirModes;
        if (overrides != null) {
            if (next == null) {
                overrides.remove(face);
            } else {
                overrides.put(face, next);
            }
        }
        renderDirections(menu, session);
    }

    @Nullable
    private static TransportMode cycleMode(@Nullable TransportMode current, boolean forward) {
        TransportMode[] modes = TransportMode.values();
        if (current == null) {
            return forward ? modes[0] : modes[modes.length - 1];
        }
        if (forward) {
            return current.ordinal() + 1 >= modes.length ? null : modes[current.ordinal() + 1];
        }
        return current.ordinal() == 0 ? null : modes[current.ordinal() - 1];
    }

    @NotNull
    private static String modeRow(boolean current, @NotNull String name) {
        return Lang.getString(current
            ? CellnetText.LINE_MODE_ROW_CURRENT
            : CellnetText.LINE_MODE_ROW_OTHER, name);
    }

    public static void openTargets(@NotNull Player player, @NotNull Location machine, @NotNull BlockFace face) {
        Session session = new Session(machine, face, 0);
        ChestMenu menu = new ChestMenu(Lang.getString(
            CellnetText.BINDING_TITLE_TARGETS, directionName(face)));
        menu.setPlayerInventoryClickable(false);
        menu.setEmptySlotsClickable(false);
        for (int slot = 0; slot < 54; slot++) {
            if (slot == BrowseUI.PREV || slot == BrowseUI.NEXT || slot == INFO_SLOT
                || slot == BACK_SLOT || in(BrowseUI.LIST_SLOTS, slot)) {
                continue;
            }
            menu.addItem(slot, ChestMenuUtils.getBackground(), (p, s, i, a) -> false);
        }
        menu.addItem(BACK_SLOT, BrowseUI.backButton(), (p, s, i, a) -> {
            openDirections(p, machine);
            return false;
        });
        renderTargets(menu, session);
        menu.open(player);
    }

    private static void renderTargets(@NotNull ChestMenu menu, @NotNull Session session) {
        AbstractChainMachine machine = AbstractChainMachine.machineAt(session.machine);
        if (machine == null) {
            return;
        }
        List<Location> targets = targetLine(machine, session.machine, session.face);
        Map<Integer, List<ItemStack>> bindings = ChainBindingStore.load(session.machine, session.face);
        int total = targets.size();
        int effDistance = machine.effectiveDistance(AbstractChainMachine.state(session.machine));

        ItemStack info = infoIcon();
        info.editMeta(meta -> {
            meta.setDisplayName(Lang.getString(CellnetText.BINDING_DIRECTION_NAME, directionName(session.face)));
            meta.setLore(List.of(Lang.getString(
                CellnetText.BINDING_DIRECTION_LORE, targets.size(), bindings.size())));
        });
        GhostItems.mark(info);
        menu.replaceExistingItem(INFO_SLOT, info);
        menu.addMenuClickHandler(INFO_SLOT, (p, s, i, a) -> false);

        int totalPages = BrowseUI.totalPages(Math.max(1, total));
        if (session.page >= totalPages) {
            session.page = totalPages - 1;
        }
        int start = session.page * BrowseUI.PAGE_SIZE;
        for (int i = 0; i < BrowseUI.LIST_SLOTS.length; i++) {
            int slot = BrowseUI.LIST_SLOTS[i];
            int distance = start + i + 1;
            if (distance > total) {
                menu.replaceExistingItem(slot, total == 0 && i == 0 ? Icon.SEARCH_EMPTY : Icon.PREVIEW_FILL);
                menu.addMenuClickHandler(slot, (p, s, it, a) -> false);
                continue;
            }
            List<ItemStack> bound = bindings.get(distance);
            menu.replaceExistingItem(slot,
                targetIcon(session, targets, distance, bound, effDistance));
            int captured = distance;
            menu.addMenuClickHandler(slot, (p, s, it, a) -> {
                handleTargetClick(p, menu, session, captured, a.isRightClicked(), a.isShiftClicked());
                return false;
            });
        }
        BrowseUI.wirePager(menu, session.page, totalPages, page -> {
            session.page = page;
            renderTargets(menu, session);
        });
    }

    private static void handleTargetClick(
        @NotNull Player player, @NotNull ChestMenu menu, @NotNull Session session,
        int distance, boolean rightClick, boolean shiftClick) {
        AbstractChainMachine machine = AbstractChainMachine.machineAt(session.machine);
        if (machine == null) {
            return;
        }
        List<Location> targets = targetLine(machine, session.machine, session.face);
        if (rightClick && shiftClick) {
            if (!validate(player, session, false)) {
                renderTargets(menu, session);
                return;
            }
            LineState state = AbstractChainMachine.state(session.machine);
            if (state.modeModule) {
                TransportMode next = cycleMode(
                    ChainBindingStore.loadDirMode(session.machine, session.face), true);
                ChainBindingStore.saveDirMode(session.machine, session.face, next);
                Map<BlockFace, TransportMode> overrides = state.dirModes;
                if (overrides != null) {
                    if (next == null) {
                        overrides.remove(session.face);
                    } else {
                        overrides.put(session.face, next);
                    }
                }
                AbstractChainMachine.invalidateBindings(state, session.face);
            }
            renderTargets(menu, session);
            return;
        }
        if (rightClick) {
            if (distance > targets.size()) {
                renderTargets(menu, session);
                return;
            }
            openTarget(player, targets.get(distance - 1));
            return;
        }
        if (shiftClick) {
            clearBinding(player, session.machine, session.face, distance);
            renderTargets(menu, session);
            return;
        }
        if (distance > targets.size()) {
            renderTargets(menu, session);
            return;
        }
        openEdit(player, session.machine, session.face, distance);
    }

    @NotNull
    private static ItemStack targetIcon(
        @NotNull Session session, @NotNull List<Location> targets, int distance,
        @Nullable List<ItemStack> bound, int effDistance) {
        Location target = targets.get(distance - 1);
        SlimefunItem sfItem = StorageCacheUtils.getSfItem(target);
        ItemStack icon;
        Component machineName;
        if (sfItem != null) {
            icon = sfItem.getItem().clone();
            machineName = displayComponent(icon);
        } else {
            Material material = target.getBlock().getType();
            icon = new ItemStack(material.isItem() && material != Material.AIR
                ? material
                : Material.COMPASS);
            machineName = Component.translatable(icon.getType().translationKey());
        }
        icon.setAmount(Math.max(1, Math.min(distance, 64)));
        boolean hasBinding = bound != null;
        icon.editMeta(meta -> {
            if (hasBinding) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.displayName(machineName);
            List<Component> lore = new ArrayList<>();
            lore.add(TextUtil.legacy(Lang.getString(
                CellnetText.BINDING_TARGET_POSITION, distance, directionName(session.face))));
            lore.add(TextUtil.legacy(statusLine(session, distance, targets.size(), effDistance, bound)));
            LineState state = AbstractChainMachine.state(session.machine);
            TransportMode override = state.modeModule
                ? ChainBindingStore.loadDirMode(session.machine, session.face)
                : null;
            String modeName = override == null
                ? Lang.getString(CellnetText.BINDING_MODE_FOLLOW)
                : Lang.getString(CellnetText.LINE_MODES_PREFIX + override.name());
            lore.add(TextUtil.legacy(Lang.getString(CellnetText.BINDING_MODE_LINE, modeName)));
            if (bound != null) {
                int shown = Math.min(LIST_PREVIEW_LIMIT, bound.size());
                for (int i = 0; i < shown; i++) {
                    lore.add(TextUtil.legacy(Lang.getString(CellnetText.BINDING_LIST_ITEM))
                        .append(displayComponent(bound.get(i))));
                }
                if (bound.size() > shown) {
                    lore.add(TextUtil.legacy(Lang.getString(CellnetText.BINDING_AND_MORE, bound.size() - shown)));
                }
            }
            lore.add(Component.empty());
            lore.add(TextUtil.legacy(Lang.getString(CellnetText.BINDING_HINT_EDIT)));
            lore.add(TextUtil.legacy(Lang.getString(CellnetText.BINDING_HINT_OPEN)));
            lore.add(TextUtil.legacy(Lang.getString(CellnetText.BINDING_HINT_UNBIND)));
            if (state.modeModule) {
                lore.add(TextUtil.legacy(Lang.getString(CellnetText.BINDING_HINT_CYCLE_MODE)));
            }
            meta.lore(lore);
        });
        GhostItems.mark(icon);
        return icon;
    }

    @NotNull
    public static Component displayComponent(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return TextUtil.LEGACY_HEX.deserialize(meta.getDisplayName());
        }
        return Component.translatable(item.getType().translationKey());
    }

    @NotNull
    private static String statusLine(
        @NotNull Session session, int distance, int lineLength, int effDistance,
        @Nullable List<ItemStack> bound) {
        if (distance > lineLength) {
            return Lang.getString(CellnetText.BINDING_STATUS_UNREACHABLE);
        }
        if (distance > effDistance) {
            return Lang.getString(CellnetText.BINDING_STATUS_DISTANCE);
        }
        if (bound == null) {
            return Lang.getString(CellnetText.BINDING_STATUS_SHARED);
        }
        if (bound.isEmpty()) {
            return Lang.getString(CellnetText.BINDING_STATUS_EMPTY);
        }
        if (isNegCached(session, distance)) {
            return Lang.getString(CellnetText.BINDING_STATUS_STARVING);
        }
        return Lang.getString(CellnetText.BINDING_STATUS_OK);
    }

    private static boolean isNegCached(@NotNull Session session, int distance) {
        LineState state = AbstractChainMachine.state(session.machine);
        Map<BlockFace, Map<Integer, Integer>> neg = state.negCache;
        if (neg == null) {
            return false;
        }
        Map<Integer, Integer> rounds = neg.get(session.face);
        return rounds != null && rounds.containsKey(distance);
    }

    public static void openEdit(
        @NotNull Player player, @NotNull Location machine, @NotNull BlockFace face, int distance) {
        Session session = new Session(machine, face, distance);
        ChestMenu menu = new ChestMenu(Lang.getString(
            CellnetText.BINDING_TITLE_EDIT, directionName(face), distance));
        menu.setPlayerInventoryClickable(true);
        for (int slot = 0; slot < 27; slot++) {
            if (slot == EDIT_INFO_SLOT || slot == EDIT_BACK_SLOT
                || in(EDIT_SLOTS, slot)) {
                continue;
            }
            menu.addItem(slot, ChestMenuUtils.getBackground(), (p, s, i, a) -> false);
        }
        menu.addItem(EDIT_BACK_SLOT, BrowseUI.backButton(), (p, s, i, a) -> {
            openTargets(p, machine, face);
            return false;
        });
        renderEdit(menu, session);
        menu.open(player);
    }

    private static void renderEdit(@NotNull ChestMenu menu, @NotNull Session session) {
        Map<Integer, List<ItemStack>> bindings = ChainBindingStore.load(session.machine, session.face);
        List<ItemStack> list = bindings.getOrDefault(session.distance, List.of());

        ItemStack info = infoIcon();
        info.editMeta(meta -> {
            meta.setDisplayName(Lang.getString(
                CellnetText.BINDING_TARGET_NAME, directionName(session.face), session.distance));
            List<String> lore = new ArrayList<>();
            lore.add(Lang.getString(CellnetText.BINDING_INFO_LORE, list.size(), ChainBindingStore.MAX_LIST_SIZE));
            lore.add("");
            lore.add(Lang.getString(CellnetText.BINDING_EDIT_HINT_ADD));
            lore.add(Lang.getString(CellnetText.BINDING_EDIT_HINT_REMOVE));
            meta.setLore(lore);
        });
        GhostItems.mark(info);
        menu.replaceExistingItem(EDIT_INFO_SLOT, info);
        menu.addMenuClickHandler(EDIT_INFO_SLOT, (p, s, i, a) -> false);

        for (int i = 0; i < EDIT_SLOTS.length; i++) {
            int slot = EDIT_SLOTS[i];
            if (i < list.size()) {
                ItemStack ghost = list.get(i).clone();
                ghost.setAmount(1);
                GhostItems.mark(ghost);
                menu.replaceExistingItem(slot, ghost);
                int index = i;
                menu.addMenuClickHandler(slot, (p, s, it, a) -> {
                    ItemStack cursor = p.getItemOnCursor();
                    if (cursor != null && !cursor.getType().isAir()) {
                        addSample(p, menu, session, cursor);
                    } else {
                        removeSample(p, menu, session, index);
                    }
                    return false;
                });
            } else {
                menu.replaceExistingItem(slot, Icon.PREVIEW_FILL);
                menu.addMenuClickHandler(slot, (p, s, it, a) -> {
                    ItemStack cursor = p.getItemOnCursor();
                    if (cursor != null && !cursor.getType().isAir()) {
                        addSample(p, menu, session, cursor);
                    }
                    return false;
                });
            }
        }
    }

    private static void addSample(
        @NotNull Player player, @NotNull ChestMenu menu, @NotNull Session session, @Nullable ItemStack clicked) {
        if (clicked == null || clicked.getType().isAir() || GhostItems.is(clicked)) {
            return;
        }
        tryAddBinding(player, session.machine, session.face, session.distance, clicked);
        renderEdit(menu, session);
    }

    static boolean tryAddBinding(
        @NotNull Player player, @NotNull Location machine, @NotNull BlockFace face, int distance,
        @NotNull ItemStack clicked) {
        AbstractChainMachine m = AbstractChainMachine.machineAt(machine);
        if (m == null) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_MACHINE_GONE));
            return false;
        }
        if (!AbstractChainMachine.hasModule(machine, ChainModule.BINDING)) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_MODULE_REQUIRED));
            return false;
        }
        List<Location> targets = targetLine(m, machine, face);
        if (distance > targets.size()) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_TARGET_GONE));
            return false;
        }
        ItemStack sample = clicked.asOne();
        if (StackUtils.isBlacklisted(sample)) {
            return false;
        }
        if (CellAcceptRules.isNbtOversized(sample)) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_SAVE_REJECTED));
            return false;
        }
        Map<Integer, List<ItemStack>> bindings = ChainBindingStore.load(machine, face);
        List<ItemStack> list = bindings.computeIfAbsent(distance, d -> new ArrayList<>());
        if (ChainBindingStore.containsSimilar(list, sample)) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_DUPLICATE));
            return false;
        }
        if (list.size() >= ChainBindingStore.MAX_LIST_SIZE) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_FULL));
            return false;
        }
        list.add(sample);
        if (!ChainBindingStore.save(machine, face, bindings)) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_SAVE_REJECTED));
            return false;
        }
        AbstractChainMachine.invalidateBindings(AbstractChainMachine.state(machine), face);
        return true;
    }

    public static boolean clearBinding(
        @NotNull Player player, @NotNull Location machine, @NotNull BlockFace face, int distance) {
        AbstractChainMachine m = AbstractChainMachine.machineAt(machine);
        if (m == null) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_MACHINE_GONE));
            return false;
        }
        if (!AbstractChainMachine.hasModule(machine, ChainModule.BINDING)) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_MODULE_REQUIRED));
            return false;
        }
        Map<Integer, List<ItemStack>> bindings = ChainBindingStore.load(machine, face);
        if (bindings.remove(distance) == null) {
            return false;
        }
        if (!ChainBindingStore.save(machine, face, bindings)) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_SAVE_REJECTED));
            return false;
        }
        AbstractChainMachine.invalidateBindings(AbstractChainMachine.state(machine), face);
        return true;
    }

    private static void removeSample(
        @NotNull Player player, @NotNull ChestMenu menu, @NotNull Session session, int index) {
        if (!validate(player, session, true)) {
            renderEdit(menu, session);
            return;
        }
        Map<Integer, List<ItemStack>> bindings = ChainBindingStore.load(session.machine, session.face);
        List<ItemStack> list = bindings.get(session.distance);
        if (list == null || index >= list.size()) {
            renderEdit(menu, session);
            return;
        }
        list.remove(index);
        if (!ChainBindingStore.save(session.machine, session.face, bindings)) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_SAVE_REJECTED));
            renderEdit(menu, session);
            return;
        }
        AbstractChainMachine.invalidateBindings(AbstractChainMachine.state(session.machine), session.face);
        player.sendMessage(Lang.getString(CellnetText.BINDING_UNBIND_ITEM_REMOVED));
        renderEdit(menu, session);
    }

    public static void openDrag(@NotNull Player player, @NotNull Location machine, @NotNull BlockFace face) {
        Session session = new Session(machine, face, 0);
        ChestMenu menu = new ChestMenu(Lang.getString(
            CellnetText.BRUSH_DRAG_TITLE, directionName(face)));
        menu.setPlayerInventoryClickable(true);
        menu.setEmptySlotsClickable(false);
        for (int slot = 0; slot < 54; slot++) {
            if (slot == BrowseUI.PREV || slot == BrowseUI.NEXT || slot == INFO_SLOT
                || slot == BACK_SLOT || in(BrowseUI.LIST_SLOTS, slot)) {
                continue;
            }
            menu.addItem(slot, ChestMenuUtils.getBackground(), (p, s, i, a) -> false);
        }
        menu.addItem(BACK_SLOT, BrowseUI.backButton(), (p, s, i, a) -> {
            openDirections(p, machine);
            return false;
        });
        renderDrag(menu, session);
        menu.open(player);
    }

    private static void renderDrag(@NotNull ChestMenu menu, @NotNull Session session) {
        AbstractChainMachine machine = AbstractChainMachine.machineAt(session.machine);
        if (machine == null) {
            return;
        }
        List<Location> targets = targetLine(machine, session.machine, session.face);
        Map<Integer, List<ItemStack>> bindings = ChainBindingStore.load(session.machine, session.face);
        int total = targets.size();

        ItemStack info = infoIcon();
        info.editMeta(meta -> {
            meta.setDisplayName(Lang.getString(CellnetText.BINDING_DIRECTION_NAME, directionName(session.face)));
            meta.setLore(List.of(
                Lang.getString(CellnetText.BINDING_DIRECTION_LORE, total, bindings.size()),
                Lang.getString(CellnetText.BRUSH_DRAG_HINT_ADD),
                Lang.getString(CellnetText.BRUSH_DRAG_HINT_CLEAR),
                Lang.getString(CellnetText.BRUSH_DRAG_HINT_EDIT)));
        });
        GhostItems.mark(info);
        menu.replaceExistingItem(INFO_SLOT, info);
        menu.addMenuClickHandler(INFO_SLOT, (p, s, i, a) -> false);

        int totalPages = BrowseUI.totalPages(Math.max(1, total));
        if (session.page >= totalPages) {
            session.page = totalPages - 1;
        }
        int start = session.page * BrowseUI.PAGE_SIZE;
        for (int i = 0; i < BrowseUI.LIST_SLOTS.length; i++) {
            int slot = BrowseUI.LIST_SLOTS[i];
            int distance = start + i + 1;
            if (distance > total) {
                menu.replaceExistingItem(slot, total == 0 && i == 0 ? Icon.SEARCH_EMPTY : Icon.PREVIEW_FILL);
                menu.addMenuClickHandler(slot, (p, s, it, a) -> false);
                continue;
            }
            List<ItemStack> bound = bindings.get(distance);
            menu.replaceExistingItem(slot, dragIcon(session, distance, bound));
            int captured = distance;
            menu.addMenuClickHandler(slot, (p, s, it, a) -> {
                handleDragClick(p, menu, session, captured, a.isRightClicked(), a.isShiftClicked());
                return false;
            });
        }
        BrowseUI.wirePager(menu, session.page, totalPages, page -> {
            session.page = page;
            renderDrag(menu, session);
        });
    }

    private static void handleDragClick(
        @NotNull Player player, @NotNull ChestMenu menu, @NotNull Session session, int distance,
        boolean rightClick, boolean shiftClick) {
        if (rightClick) {
            AbstractChainMachine machine = AbstractChainMachine.machineAt(session.machine);
            if (machine == null) {
                return;
            }
            List<Location> targets = targetLine(machine, session.machine, session.face);
            if (distance > targets.size()) {
                renderDrag(menu, session);
                return;
            }
            openEdit(player, session.machine, session.face, distance);
            return;
        }
        if (shiftClick) {
            clearBinding(player, session.machine, session.face, distance);
            renderDrag(menu, session);
            return;
        }
        ItemStack cursor = player.getItemOnCursor();
        if (cursor != null && !cursor.getType().isAir()) {
            tryAddBinding(player, session.machine, session.face, distance, cursor);
        }
        renderDrag(menu, session);
    }

    @NotNull
    private static ItemStack dragIcon(
        @NotNull Session session, int distance, @Nullable List<ItemStack> bound) {
        ItemStack icon;
        if (bound != null && !bound.isEmpty()) {
            icon = bound.get(0).clone();
            icon.setAmount(1);
        } else {
            icon = Icon.PREVIEW_FILL.clone();
        }
        icon.editMeta(meta -> {
            meta.setDisplayName(Lang.getString(
                CellnetText.BINDING_TARGET_POSITION, distance, directionName(session.face)));
            List<String> lore = new ArrayList<>();
            if (bound == null) {
                lore.add(Lang.getString(CellnetText.BINDING_STATUS_SHARED));
            } else if (bound.isEmpty()) {
                lore.add(Lang.getString(CellnetText.BINDING_STATUS_EMPTY));
            } else {
                lore.add(Lang.getString(CellnetText.BINDING_INFO_LORE, bound.size(), ChainBindingStore.MAX_LIST_SIZE));
            }
            lore.add("");
            lore.add(Lang.getString(CellnetText.BRUSH_DRAG_HINT_ADD));
            lore.add(Lang.getString(CellnetText.BRUSH_DRAG_HINT_CLEAR));
            lore.add(Lang.getString(CellnetText.BRUSH_DRAG_HINT_EDIT));
            meta.setLore(lore);
        });
        GhostItems.mark(icon);
        return icon;
    }

    private static boolean validate(@NotNull Player player, @NotNull Session session, boolean requireTargetOnline) {
        AbstractChainMachine machine = AbstractChainMachine.machineAt(session.machine);
        if (machine == null) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_MACHINE_GONE));
            return false;
        }
        if (!AbstractChainMachine.hasModule(session.machine, ChainModule.BINDING)) {
            player.sendMessage(Lang.getString(CellnetText.BINDING_MODULE_REQUIRED));
            return false;
        }
        if (requireTargetOnline && session.face != null) {
            List<Location> targets = targetLine(machine, session.machine, session.face);
            if (session.distance > targets.size()) {
                player.sendMessage(Lang.getString(CellnetText.BINDING_TARGET_GONE));
                return false;
            }
        }
        return true;
    }

    private static boolean isEnabled(@NotNull Location machine, @NotNull BlockFace face) {
        LineState state = AbstractChainMachine.state(machine);
        return state.effectiveDirections().contains(face);
    }

    @NotNull
    private static List<Location> targetLine(
        @NotNull AbstractChainMachine machine, @NotNull Location location, @NotNull BlockFace face) {
        return ChainTargetCache.targets(location, face, machine.maxDistance(),
            AbstractChainMachine.state(location).vanilla, machine.workTtlTicks(), machine.idleTtlTicks());
    }

    public static void openTarget(@NotNull Player player, @NotNull Location target) {
        if (!Slimefun.getProtectionManager()
            .hasPermission(player, target, Interaction.INTERACT_BLOCK)) {
            player.sendMessage(Lang.getString(CellnetText.LINE_DIRECTION_NO_PERMISSION));
            return;
        }
        BlockMenu targetMenu = StorageCacheUtils.getMenu(target);
        if (targetMenu != null) {
            targetMenu.open(player);
            return;
        }
        if (target.getBlock().getState() instanceof Container container) {
            player.openInventory(container.getInventory());
            return;
        }
        player.sendMessage(Lang.getString(CellnetText.LINE_DIRECTION_NO_TARGET));
    }

    @NotNull
    public static ItemStack directionIcon(@NotNull Location machine, @NotNull BlockFace face, boolean enabled) {
        return enabled ? directionPane(machine, face, true) : Icon.LINE_DIRECTION_OFF.clone();
    }

    @NotNull
    public static ItemStack directionPane(@NotNull Location machine, @NotNull BlockFace face, boolean selected) {
        Block neighbor = machine.getBlock().getRelative(face);
        SlimefunItem sfItem = StorageCacheUtils.getSfItem(neighbor.getLocation());
        ItemStack pane;
        if (sfItem != null) {
            pane = NetworkDirectional.getDirectionalSlotPane(face, sfItem, selected);
        } else {
            Material material = neighbor.getType();
            if (material.isItem() && material != Material.AIR) {
                pane = NetworkDirectional.getDirectionalSlotPane(face, material, selected);
            } else {
                pane = (selected ? Icon.LINE_DIRECTION_ON : Icon.LINE_DIRECTION_OFF).clone();
                pane.editMeta(meta -> meta.setDisplayName(String.format(
                    Lang.getString("messages.normal-operation.directional.display_empty"), face.name())));
            }
        }
        localizeDirectionName(pane, face);
        GhostItems.mark(pane);
        return pane;
    }

    private static void localizeDirectionName(@NotNull ItemStack pane, @NotNull BlockFace face) {
        String english = face.name();
        pane.editMeta(meta -> {
            String display = meta.getDisplayName();
            int index = display.indexOf(english);
            if (index >= 0) {
                meta.setDisplayName(display.substring(0, index) + directionName(face)
                    + display.substring(index + english.length()));
            }
        });
    }

    @NotNull
    private static ItemStack infoIcon() {
        ItemStack icon = Icon.CHAIN_BINDING_INFO.clone();
        icon.editMeta(meta -> {
            meta.setDisplayName(Lang.getString(CellnetText.BINDING_INFO_NAME));
            meta.setLore(List.of());
        });
        return icon;
    }

    @NotNull
    public static String directionName(@NotNull BlockFace face) {
        return Lang.getString(CellnetText.LINE_DIRECTION_PREFIX + face.name().toLowerCase(Locale.ROOT));
    }
}
