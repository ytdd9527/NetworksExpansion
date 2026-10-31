package com.ytdd9527.networksexpansion.implementation.machines.converter;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellTier;
import com.ytdd9527.networksexpansion.api.cell.StorageCell;
import com.ytdd9527.networksexpansion.api.cell.CellLedger;
import com.ytdd9527.networksexpansion.api.cell.CellPersistence;
import com.ytdd9527.networksexpansion.api.cell.CellTransferLogic;
import com.ytdd9527.networksexpansion.core.machines.AbstractCellBrowser;
import com.ytdd9527.networksexpansion.utils.ChatInput;
import com.ytdd9527.networksexpansion.utils.ItemKey;
import com.ytdd9527.networksexpansion.utils.ItemSearch;
import com.ytdd9527.networksexpansion.api.cell.CellSlotUI;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.utils.MenuShells;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.stackcaches.QuantumCache;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CellConverter extends AbstractCellBrowser {

    public static final int MODE_SLOT = 48;
    public static final int[] DISPLAY_SLOTS = new int[]{
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26
    };
    public static final int[] OUTPUT_SLOTS = new int[]{
        27, 28, 29, 30, 31, 32, 33, 34, 35,
        36, 37, 38
    };
    public static final int[] BACKGROUND_SLOTS = new int[]{
        1, 2, 3, 5, 6, 7,
        39, 40, 41, 42, 43, 44,
        46, 47, 49, 50, 51, 52, 53
    };

    private static final NamespacedKey CELL_SLOT_MARKER_KEY =
        new NamespacedKey("networks", "converter_cell_slot_marker");

    private final Map<Location, Boolean> exportMode = new ConcurrentHashMap<>();
    private final Map<Location, Long> refreshSignature = new ConcurrentHashMap<>();

    public CellConverter(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public int[] displaySlots() {
        return DISPLAY_SLOTS;
    }

    @Override
    public int[] backgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    @Override
    public @NotNull String langPrefix() {
        return CellnetText.CONVERTER;
    }

    @Override
    public @NotNull ChatInput.SearchTarget searchTarget() {
        return ChatInput.SearchTarget.CELL_CONVERTER;
    }

    @Override
    public @NotNull ItemStack createCellSlotMarker() {
        ItemStack marker = Icon.CONVERTER_CELL_SLOT.clone();
        ItemMeta meta = marker.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(CELL_SLOT_MARKER_KEY, PersistentDataType.BOOLEAN, true);
            marker.setItemMeta(meta);
        }
        return marker;
    }

    @Override
    public boolean isCellSlotMarker(@Nullable ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(CELL_SLOT_MARKER_KEY, PersistentDataType.BOOLEAN);
    }

    @Override
    public void onDisplayClick(@NotNull BlockMenu menu, @NotNull Player player, int slot, @Nullable ItemStack item, @NotNull ClickAction action) {
        if (isExportMode(menu)) {
            transferItem(menu, player, slot);
        } else {
            importItem(menu, player, slot);
        }
    }

    @Override
    public void onCellInserted(@NotNull BlockMenu menu, @NotNull ItemStack cellItem, @NotNull Player player) {
        if (!CellSlotUI.prepareInsertedCell(player, cellItem)) {
            return;
        }
        pageCache.put(menu.getLocation(), 0);
        refresh(menu);
    }

    @Override
    public @Nullable ItemStack onCellEjected(@NotNull BlockMenu menu, int slot) {
        return CellSlotUI.ejectCell(menu, slot,
            this::createCellSlotMarker, DISPLAY_SLOTS, pageCache, () -> refresh(menu));
    }

    @Override
    public void refresh(@NotNull BlockMenu menu) {
        long sig = computeSignature(menu);
        if (sig == refreshSignature.getOrDefault(menu.getLocation(), 0L)) {
            return;
        }
        refreshSignature.put(menu.getLocation(), sig);
        boolean exporting = isExportMode(menu);
        ItemStack cellItem = menu.getItemInSlot(CELL_SLOT);

        menu.replaceExistingItem(MODE_SLOT, buildModeButton(exporting));

        if (cellItem == null || !StorageCell.isStorageCell(cellItem)) {
            menu.replaceExistingItem(CELL_SLOT, createCellSlotMarker());
            CellSlotUI.clearDisplay(menu, DISPLAY_SLOTS);
            menu.replaceExistingItem(INFO, buildInfoItem(null, 0, 0));
            menu.replaceExistingItem(PREV, pageButton(false, false));
            menu.replaceExistingItem(NEXT, pageButton(false, true));
            return;
        }

        long per = StorageCell.getPerTypeLimit(cellItem);
        CellLedger cache = StorageCell.loadCellCache(cellItem, per);

        StorageCell.applyLore(cellItem, per, StorageCell.getCurrentPerTypeLimit(cellItem));
        menu.replaceExistingItem(CELL_SLOT, cellItem);

        if (exporting) {
            renderCellItems(menu, cache);
        } else {
            renderQuantumItems(menu);
        }

        menu.replaceExistingItem(PREV, pageButton(pageCache.getOrDefault(menu.getLocation(), 0) > 0, false));
        menu.replaceExistingItem(NEXT, pageButton(
            pageCache.getOrDefault(menu.getLocation(), 0) < maxPages(menu) - 1, true));
        menu.replaceExistingItem(INFO, buildInfoItem(cellItem, cache.getStoredItems().size(),
            CellSlotUI.countOccupied(menu, OUTPUT_SLOTS)));
        wireSearchButton(menu);
    }

    private long computeSignature(@NotNull BlockMenu menu) {
        ItemStack cellItem = menu.getItemInSlot(CELL_SLOT);
        boolean hasCell = cellItem != null && StorageCell.isStorageCell(cellItem);
        int page = pageCache.getOrDefault(menu.getLocation(), 0);
        boolean exporting = isExportMode(menu);
        String search = SEARCH_TERMS.get(menu.getLocation());
        long outputHash = 0L;
        for (int slot : OUTPUT_SLOTS) {
            ItemStack onSlot = menu.getItemInSlot(slot);
            long slotHash = 0L;
            if (onSlot != null && !onSlot.getType().isAir()) {
                QuantumCache qc = Keys.getQuantumCache(onSlot.getItemMeta());
                slotHash = qc != null
                    ? Objects.hash(onSlot.getType(), qc.getAmountLong())
                    : Objects.hash(onSlot.getType(), onSlot.getAmount(), onSlot.getItemMeta());
            }
            outputHash = 31L * outputHash + slotHash;
        }
        int entriesCount = 0;
        long totalAmount = 0L;
        if (hasCell) {
            long per = StorageCell.getPerTypeLimit(cellItem);
            CellLedger cache = StorageCell.loadCellCache(cellItem, per);
            List<CellLedger.CellEntry> entries = cache.getStoredItems();
            entriesCount = entries.size();
            for (CellLedger.CellEntry entry : entries) {
                totalAmount += entry.amount;
            }
        }
        return Objects.hash(hasCell, page, exporting, search, outputHash, entriesCount, totalAmount);
    }

    @Override
    protected void setupMachineHandlers(@NotNull BlockMenu menu) {
        menu.addMenuClickHandler(MODE_SLOT, (player, slot, item, action) -> {
            boolean exporting = isExportMode(menu);
            exportMode.put(menu.getLocation(), !exporting);
            pageCache.put(menu.getLocation(), 0);
            refresh(menu);
            return false;
        });
    }

    @Override
    protected int maxPages(@NotNull BlockMenu menu) {
        ItemStack cellItem = menu.getItemInSlot(CELL_SLOT);
        if (cellItem == null || !StorageCell.isStorageCell(cellItem)) {
            return 1;
        }
        if (!isExportMode(menu)) {
            return 1;
        }
        long per = StorageCell.getPerTypeLimit(cellItem);
        CellLedger cache = StorageCell.loadCellCache(cellItem, per);
        int count = visibleEntries(menu, cache).size();
        return Math.max(1, (int) Math.ceil((double) count / itemsPerPage()));
    }

    @Override
    public void preRegister() {
        addItemHandler(
            new BlockTicker() {
                @Override
                public boolean isSynchronized() {
                    return false;
                }

                @Override
                public void tick(@NotNull Block b, SlimefunItem item, SlimefunBlockData data) {
                    // Async tick must not touch inventories; the refresh goes back to the main thread
                    BlockMenu blockMenu = data.getBlockMenu();
                    if (blockMenu == null || !blockMenu.hasViewer()) {
                        return;
                    }
                    Bukkit.getScheduler().runTask(Networks.getInstance(), () -> {
                        if (blockMenu.hasViewer()) {
                            refresh(blockMenu);
                        }
                    });
                }
            },
            new BlockBreakHandler(false, false) {
                @Override
                public void onPlayerBreak(@NotNull BlockBreakEvent event, @NotNull ItemStack item, @NotNull List<ItemStack> drops) {
                    onBreak(event);
                }
            });
    }

    @Override
    public void postRegister() {
        MenuShells.create(this, 54, BACKGROUND_SLOTS, Icon.CONVERTER_BORDER,
            (block, player) -> player.hasPermission("slimefun.inventory.bypass")
                || (canUse(player, false)
                && Slimefun.getProtectionManager()
                .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK)),
            flow -> flow == ItemTransportFlow.WITHDRAW ? OUTPUT_SLOTS : new int[0],
            (menu, block) -> {
                menu.replaceExistingItem(CELL_SLOT, createCellSlotMarker());
                for (int slot : DISPLAY_SLOTS) {
                    menu.replaceExistingItem(slot, Icon.CLEANER_DISPLAY);
                }
                setupSharedHandlers(menu);
                refresh(menu);
                menu.addMenuOpeningHandler(p -> refresh(menu));
                menu.addMenuCloseHandler(p -> {
                    for (int slot : DISPLAY_SLOTS) {
                        menu.replaceExistingItem(slot, Icon.CLEANER_DISPLAY);
                    }
                });
            });
    }

    private void onBreak(@NotNull BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        onBreakCleanup(location);
        exportMode.remove(location);
        refreshSignature.remove(location);
        BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
        if (blockMenu != null) {
            ItemStack cellItem = blockMenu.getItemInSlot(CELL_SLOT);
            if (cellItem != null && !isCellSlotMarker(cellItem)) {
                blockMenu.dropItems(location, CELL_SLOT);
            }
            blockMenu.dropItems(location, OUTPUT_SLOTS);
        }
    }

    private boolean isExportMode(@NotNull BlockMenu menu) {
        return exportMode.getOrDefault(menu.getLocation(), true);
    }

    private record QuantumImport(
            @NotNull ItemStack cellItem,
            @NotNull CellLedger cache,
            long per,
            int sourceSlot,
            @NotNull QuantumCache qc,
            @NotNull NetworkQuantumStorage storage) {
    }

    private record CellContext(
        @NotNull ItemStack cellItem,
        long per,
        @NotNull CellLedger cache,
        @NotNull CellLedger.CellEntry entry) {
    }

    @Nullable
    private CellContext resolveCellContext(@NotNull BlockMenu menu, int displaySlot) {
        ItemStack cellItem = menu.getItemInSlot(CELL_SLOT);
        if (cellItem == null || !StorageCell.isStorageCell(cellItem)) {
            return null;
        }
        long per = StorageCell.getPerTypeLimit(cellItem);
        CellLedger cache = StorageCell.loadCellCache(cellItem, per);
        int idx = displayIndex(menu, displaySlot);
        if (idx < 0) {
            return null;
        }
        List<CellLedger.CellEntry> entries = visibleEntries(menu, cache);
        return idx < entries.size() ? new CellContext(cellItem, per, cache, entries.get(idx)) : null;
    }

    @Nullable
    private static NetworkQuantumStorage resolveTargetStorage(long amount, long per) {
        return CellPersistence.isUnlimited(per)
            ? findStorageForAmount(amount)
            : CellTier.upgradeMaterialOf(per);
    }

    private void transferItem(@NotNull BlockMenu menu, @NotNull Player player, int displaySlot) {
        CellContext ctx = resolveCellContext(menu, displaySlot);
        if (ctx == null) {
            return;
        }

        NetworkQuantumStorage required = resolveTargetStorage(ctx.entry().amount, ctx.per());
        if (required == null) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_NO_MATCHING_STORAGE));
            return;
        }

        // 转出为量子储存需要消耗一个玩家放入的空量子储存壳，且壳的等级必须与
        // 元件需要的量子储存等级一致；不一致的壳不能用于存入
        int shellSlot = -1;
        long shellAmount = 0;
        boolean hasMismatchedShell = false;
        for (int slot : OUTPUT_SLOTS) {
            ItemStack onSlot = menu.getItemInSlot(slot);
            if (onSlot == null || onSlot.getType().isAir()) {
                continue;
            }
            NetworkQuantumStorage shell = asQuantumStorage(onSlot);
            if (shell == null) {
                continue;
            }
            QuantumCache shellData = Keys.getQuantumCache(onSlot.getItemMeta());
            boolean empty = shellData == null
                || shellData.getItemStack() == null
                || shellData.getAmountLong() <= 0;
            if (!empty) {
                continue;
            }
            if (shell != required) {
                hasMismatchedShell = true;
                continue;
            }
            shellSlot = slot;
            shellAmount = onSlot.getAmount();
            break;
        }

        if (shellSlot < 0) {
            player.sendMessage(Lang.getString(hasMismatchedShell
                ? CellnetText.CONVERTER_TIER_MISMATCH
                : CellnetText.CONVERTER_NO_QUANTUM_STORAGE));
            return;
        }

        // 同槽叠放多个空壳时只消耗 1 个：成品需放到其他空槽
        CellTransferLogic.ExportPlan plan = CellTransferLogic.planExport(
            shellAmount, firstFreeOutputSlot(menu, shellSlot) >= 0);
        if (!plan.accepted()) {
            player.sendMessage(Lang.getString(plan.result() == CellTransferLogic.ExportResult.REJECT_NO_SLOT
                ? CellnetText.CONVERTER_NO_FREE_SLOT
                : CellnetText.CONVERTER_NO_QUANTUM_STORAGE));
            return;
        }

        ItemStack qsItem = CellQuantumConverter.buildQuantumStorage(ctx.entry().sample, ctx.entry().amount, required);

        if (plan.result() == CellTransferLogic.ExportResult.ACCEPTED_WITH_EXTRA_SLOT) {
            ItemStack shells = menu.getItemInSlot(shellSlot);
            shells.setAmount((int) (shellAmount - 1));
            menu.replaceExistingItem(shellSlot, shells);
            menu.replaceExistingItem(firstFreeOutputSlot(menu, shellSlot), qsItem);
        } else {
            // 消耗空壳：原槽位被灌成带内容的量子储存
            menu.replaceExistingItem(shellSlot, qsItem);
        }
        ctx.cache().takeItem(ctx.entry().sample, ctx.entry().amount);
        refresh(menu);

        CellPersistence.saveAsync();
    }

    @Nullable
    private static NetworkQuantumStorage findStorageForAmount(long amount) {
        return CellTier.storageForAmount(amount);
    }

    @Nullable
    private QuantumImport resolveQuantumImport(@NotNull BlockMenu menu, @NotNull Player player, int displaySlot) {
        ItemStack cellItem = menu.getItemInSlot(CELL_SLOT);
        if (cellItem == null || !StorageCell.isStorageCell(cellItem)) {
            return null;
        }
        long per = StorageCell.getPerTypeLimit(cellItem);
        CellLedger cache = StorageCell.loadCellCache(cellItem, per);

        int idx = displayIndex(menu, displaySlot);
        if (idx < 0) {
            return null;
        }
        List<Integer> qsSlots = visibleQuantumSlots(menu, listQuantumSlots(menu));
        if (idx >= qsSlots.size()) {
            return null;
        }
        int sourceSlot = qsSlots.get(idx);
        ItemStack qsItem = menu.getItemInSlot(sourceSlot);
        QuantumCache qc = Keys.getQuantumCache(qsItem.getItemMeta());
        if (qc == null || qc.getItemStack() == null || qc.getAmountLong() <= 0) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_NO_QUANTUM_STORAGE));
            return null;
        }
        NetworkQuantumStorage storage = asQuantumStorage(qsItem);
        if (storage == null) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_TIER_MISMATCH));
            return null;
        }
        // 量子储存档位必须与元件档位需要的量子储存一致，不一致拒绝转入
        // （无限档元件没有固定档位要求，不在此拦截）
        if (!CellPersistence.isUnlimited(per) && storage != CellTier.upgradeMaterialOf(per)) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_TIER_MISMATCH));
            return null;
        }
        return new QuantumImport(cellItem, cache, per, sourceSlot, qc, storage);
    }

    private void importItem(@NotNull BlockMenu menu, @NotNull Player player, int displaySlot) {
        QuantumImport target = resolveQuantumImport(menu, player, displaySlot);
        if (target == null) {
            return;
        }
        CellLedger cache = target.cache();
        long per = target.per();

        ItemKey targetKey = new ItemKey(target.qc().getItemStack());
        long existing = cache.getAmount(targetKey);
        long maxUnits = CellPersistence.getMaxUnitsFor(per);

        if (cache.isWhitelistEnabled() && !isInCellWhitelist(cache, target.qc().getItemStack())) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_WHITELIST_REJECTED));
            return;
        }

        ItemStack qsItem = menu.getItemInSlot(target.sourceSlot());
        CellTransferLogic.ImportPlan plan = CellTransferLogic.planImport(
            qsItem.getAmount(), target.qc().getAmountLong(),
            existing, per, cache.typeCount(), maxUnits);
        if (!plan.accepted()) {
            player.sendMessage(Lang.getString(switch (plan.result()) {
                case REJECT_UNITS_FULL -> CellnetText.CONVERTER_UNITS_FULL;
                case REJECT_TYPE_FULL -> CellnetText.CONVERTER_ITEM_CAPACITY_FULL;
                case REJECT_SHELL_OVERFLOW -> CellnetText.CONVERTER_SHELL_OVERFLOW;
                case REJECT_NO_SLOT -> CellnetText.CONVERTER_NO_FREE_SLOT;
                default -> CellnetText.CONVERTER_NO_QUANTUM_STORAGE;
            }));
            return;
        }

        if (plan.needExtraSlot()) {
            int extraSlot = firstFreeOutputSlot(menu, target.sourceSlot());
            if (extraSlot < 0) {
                player.sendMessage(Lang.getString(CellnetText.CONVERTER_NO_FREE_SLOT));
                return;
            }
            menu.replaceExistingItem(extraSlot, CellQuantumConverter.buildQuantumStorage(
                target.qc().getItemStack(), plan.remainder(), target.storage()));
        }

        cache.pushItemLong(targetKey, plan.transferAmount());

        if (plan.consumeAll()) {
            menu.replaceExistingItem(target.sourceSlot(), null);
        } else if (plan.fullShells() <= 0) {
            menu.replaceExistingItem(target.sourceSlot(), CellQuantumConverter.buildQuantumStorage(
                target.qc().getItemStack(), plan.remainder(), target.storage()));
        } else {
            ItemStack fullShells = CellQuantumConverter.buildQuantumStorage(
                target.qc().getItemStack(), target.qc().getAmountLong(), target.storage());
            fullShells.setAmount((int) plan.fullShells());
            menu.replaceExistingItem(target.sourceSlot(), fullShells);
        }

        refresh(menu);

        CellPersistence.saveAsync();
    }

    private int firstFreeOutputSlot(@NotNull BlockMenu menu, int excludeSlot) {
        for (int slot : OUTPUT_SLOTS) {
            if (slot == excludeSlot) {
                continue;
            }
            ItemStack onSlot = menu.getItemInSlot(slot);
            if (onSlot == null || onSlot.getType().isAir()) {
                return slot;
            }
        }
        return -1;
    }

    private static List<Integer> listQuantumSlots(@NotNull BlockMenu menu) {
        List<Integer> slots = new ArrayList<>();
        for (int slot : OUTPUT_SLOTS) {
            ItemStack onSlot = menu.getItemInSlot(slot);
            if (onSlot == null || onSlot.getType().isAir()) {
                continue;
            }
            QuantumCache qc = Keys.getQuantumCache(onSlot.getItemMeta());
            if (qc != null && qc.getAmountLong() > 0) {
                slots.add(slot);
            }
        }
        return slots;
    }

    private static boolean isInCellWhitelist(@NotNull CellLedger cache, @NotNull ItemStack sample) {
        for (ItemStack template : cache.getWhitelist()) {
            if (StackUtils.itemsMatch(template, sample)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static NetworkQuantumStorage asQuantumStorage(@NotNull ItemStack item) {
        SlimefunItem sf = SlimefunItem.getByItem(item);
        return sf instanceof NetworkQuantumStorage storage ? storage : null;
    }

    @NotNull
    private List<Integer> visibleQuantumSlots(@NotNull BlockMenu menu, @NotNull List<Integer> qsSlots) {
        String search = SEARCH_TERMS.get(menu.getLocation());
        if (search == null) {
            return qsSlots;
        }
        List<Integer> filtered = new ArrayList<>(qsSlots.size());
        for (int slot : qsSlots) {
            ItemStack qsItem = menu.getItemInSlot(slot);
            QuantumCache qc = qsItem == null ? null : Keys.getQuantumCache(qsItem.getItemMeta());
            if (qc != null && qc.getItemStack() != null
                && ItemSearch.matches(search, ItemStackHelper.getDisplayName(qc.getItemStack()))) {
                filtered.add(slot);
            }
        }
        return filtered;
    }

    private void renderCellItems(@NotNull BlockMenu menu, @NotNull CellLedger cache) {
        List<CellLedger.CellEntry> entries = visibleEntries(menu, cache);
        int page = normalizePage(menu, entries.size());
        int start = page * itemsPerPage();
        int end = Math.min(start + itemsPerPage(), entries.size());

        for (int i = 0; i < DISPLAY_SLOTS.length; i++) {
            int slot = DISPLAY_SLOTS[i];
            if (i < end - start) {
                CellLedger.CellEntry entry = entries.get(start + i);
                menu.replaceExistingItem(slot, CellSlotUI.displayItem(
                    entry.sample, entry.amount,
                    CellnetText.CONVERTER_ITEM_COUNT, CellnetText.CONVERTER_TRANSFER_HINT));
            } else {
                menu.replaceExistingItem(slot, entries.isEmpty()
                    ? Icon.SEARCH_EMPTY
                    : Icon.CLEANER_DISPLAY);
            }
        }
    }

    private void renderQuantumItems(@NotNull BlockMenu menu) {
        List<Integer> qsSlots = visibleQuantumSlots(menu, listQuantumSlots(menu));
        int page = normalizePage(menu, qsSlots.size());
        for (int i = 0; i < DISPLAY_SLOTS.length; i++) {
            int index = page * itemsPerPage() + i;
            if (index < qsSlots.size()) {
                ItemStack qsItem = menu.getItemInSlot(qsSlots.get(index));
                QuantumCache qc = qsItem == null ? null : Keys.getQuantumCache(qsItem.getItemMeta());
                if (qc != null && qc.getItemStack() != null && qc.getAmountLong() > 0) {
                    menu.replaceExistingItem(DISPLAY_SLOTS[i], CellSlotUI.displayItem(
                        qc.getItemStack(), qc.getAmountLong(),
                        CellnetText.CONVERTER_ITEM_COUNT, CellnetText.CONVERTER_TRANSFER_HINT));
                    continue;
                }
            }
            menu.replaceExistingItem(DISPLAY_SLOTS[i], Icon.CLEANER_DISPLAY);
        }
    }

    @NotNull
    private static ItemStack buildInfoItem(@Nullable ItemStack cellItem, int types, int outputUsed) {
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta meta = info.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            meta.setDisplayName(Lang.getString(CellnetText.CONVERTER_INFO_TITLE));
            if (cellItem != null && StorageCell.isStorageCell(cellItem)) {
                lore.add(Lang.getString(CellnetText.CONVERTER_CELL_NAME, ItemStackHelper.getDisplayName(cellItem)));
                lore.add(Lang.getString(CellnetText.CONVERTER_ITEM_TYPES, types));
            } else {
                lore.add(Lang.getString(CellnetText.CONVERTER_NO_CELL));
            }
            lore.add(Lang.getString(CellnetText.CONVERTER_OUTPUT_COUNT, outputUsed, OUTPUT_SLOTS.length));
            meta.setLore(lore);
            info.setItemMeta(meta);
        }
        return info;
    }

    @NotNull
    private static ItemStack buildModeButton(boolean exporting) {
        return exporting ? Icon.CONVERTER_MODE_EXPORT : Icon.CONVERTER_MODE_IMPORT;
    }

    public static void applySearchFromChat(@NotNull Player player, @NotNull Location location, @NotNull String term) {
        applySearchFromChat(player, location, term,
            StorageCacheUtils::getMenu,
            sf -> sf instanceof CellConverter,
            menu -> {
                CellConverter converter = (CellConverter) StorageCacheUtils.getSfItem(location);
                if (converter != null) {
                    converter.pageCache.put(location, 0);
                    converter.refresh(menu);
                }
            });
    }
}
