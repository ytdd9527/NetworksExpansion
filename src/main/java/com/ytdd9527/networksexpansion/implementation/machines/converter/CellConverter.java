package com.ytdd9527.networksexpansion.implementation.machines.converter;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellLedger;
import com.ytdd9527.networksexpansion.api.cell.CellPersistence;
import com.ytdd9527.networksexpansion.api.cell.CellSlotUI;
import com.ytdd9527.networksexpansion.api.cell.CellTier;
import com.ytdd9527.networksexpansion.api.cell.CellTransferLogic;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.cell.StorageCell;
import com.ytdd9527.networksexpansion.core.machines.AbstractCellBrowser;
import com.ytdd9527.networksexpansion.utils.ChatInput;
import com.ytdd9527.networksexpansion.utils.GhostItems;
import com.ytdd9527.networksexpansion.utils.ItemKey;
import com.ytdd9527.networksexpansion.utils.ItemSearch;
import com.ytdd9527.networksexpansion.utils.MenuShells;
import com.ytdd9527.networksexpansion.utils.NumberFormat;
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
    public static final int UPGRADE_SLOT = 50;
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
        46, 47, 49, 51, 52, 53
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
        menu.replaceExistingItem(UPGRADE_SLOT, buildUpgradeButton(cellItem));

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
                    ? Objects.hash(onSlot.getType(), onSlot.getAmount(), qc.getAmountLong())
                    : Objects.hash(onSlot.getType(), onSlot.getAmount(), onSlot.getItemMeta());
            }
            outputHash = 31L * outputHash + slotHash;
        }
        int entriesCount = 0;
        long totalAmount = 0L;
        long currentUnits = 0L;
        if (hasCell) {
            long per = StorageCell.getPerTypeLimit(cellItem);
            CellLedger cache = StorageCell.loadCellCache(cellItem, per);
            List<CellLedger.CellEntry> entries = cache.getStoredItems();
            entriesCount = entries.size();
            for (CellLedger.CellEntry entry : entries) {
                totalAmount += entry.amount;
            }
            currentUnits = Math.min(StorageCell.getCurrentPerTypeLimit(cellItem),
                CellPersistence.getMaxUnitsFor(per));
        }
        return Objects.hash(hasCell, page, exporting, search, outputHash, entriesCount, totalAmount, currentUnits);
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
        menu.addMenuClickHandler(UPGRADE_SLOT, (player, slot, item, action) -> {
            handleUpgrade(menu, player);
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

    private record CellContext(
        @NotNull ItemStack cellItem,
        long per,
        @NotNull CellLedger cache,
        @NotNull CellLedger.CellEntry entry) {
    }

    private record QuantumEntry(
        int slot,
        @NotNull ItemStack stack,
        @Nullable QuantumCache cache,
        @NotNull NetworkQuantumStorage storage) {

        boolean hasItem() {
            return cache != null && cache.getItemStack() != null;
        }

        boolean isValid() {
            return hasItem() && cache.getAmountLong() > 0;
        }

        boolean isEmpty() {
            return !hasItem() || cache.getAmountLong() <= 0;
        }

        long amount() {
            return isValid() ? cache.getAmountLong() : 0L;
        }

        long limit() {
            return cache != null ? cache.getLimitLong() : storage.getMaxAmount();
        }

        long freeCapacity() {
            return Math.max(0L, limit() - amount());
        }
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

    // ---------------------------------------------------------------------
    // 元件升级
    // ---------------------------------------------------------------------

    @NotNull
    private static ItemStack buildUpgradeButton(@Nullable ItemStack cellItem) {
        ItemStack button = Icon.CONVERTER_UPGRADE.clone();
        boolean hasCell = cellItem != null && StorageCell.isStorageCell(cellItem);
        long current = 0L;
        long max = 0L;
        String stateKey = CellnetText.CONVERTER_UPGRADE_NO_CELL;
        boolean unlimited = false;
        if (hasCell) {
            long per = StorageCell.getPerTypeLimit(cellItem);
            unlimited = CellPersistence.isUnlimited(per);
            max = CellPersistence.getMaxUnitsFor(per);
            current = Math.min(StorageCell.getCurrentPerTypeLimit(cellItem), max);
            stateKey = unlimited || current >= max
                ? CellnetText.CONVERTER_UPGRADE_MAXED
                : CellnetText.CONVERTER_UPGRADE_READY;
        }
        long finalCurrent = current;
        long finalMax = max;
        boolean finalUnlimited = unlimited;
        String finalStateKey = stateKey;
        button.editMeta(meta -> {
            meta.setDisplayName(Lang.getString(CellnetText.CONVERTER_UPGRADE_TITLE));
            List<String> lore = new ArrayList<>();
            if (hasCell) {
                String unitText = finalUnlimited ? "∞" : NumberFormat.formatNumber(finalCurrent);
                String maxText = finalUnlimited ? "∞" : NumberFormat.formatNumber(finalMax);
                lore.add(Lang.getString(CellnetText.CONVERTER_UPGRADE_UNITS, unitText, maxText));
            }
            lore.add(Lang.getString(finalStateKey));
            meta.setLore(lore);
        });
        return button;
    }

    private static boolean canUpgrade(@NotNull ItemStack cellItem) {
        long per = StorageCell.getPerTypeLimit(cellItem);
        if (CellPersistence.isUnlimited(per)) {
            return false;
        }
        long maxUnits = CellPersistence.getMaxUnitsFor(per);
        return Math.min(StorageCell.getCurrentPerTypeLimit(cellItem), maxUnits) < maxUnits;
    }

    private void handleUpgrade(@NotNull BlockMenu menu, @NotNull Player player) {
        ItemStack cellItem = menu.getItemInSlot(CELL_SLOT);
        if (cellItem == null || !StorageCell.isStorageCell(cellItem)) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_NO_CELL));
            return;
        }
        long per = StorageCell.getPerTypeLimit(cellItem);
        long maxUnits = CellPersistence.getMaxUnitsFor(per);
        long current = Math.min(StorageCell.getCurrentPerTypeLimit(cellItem), maxUnits);
        if (!canUpgrade(cellItem)) {
            player.sendMessage(Lang.getString(CellnetText.CELL_UPGRADE_MAXED, NumberFormat.formatNumber(maxUnits)));
            return;
        }
        NetworkQuantumStorage required = CellTier.upgradeMaterialOf(per);
        if (required == null) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_NO_MATCHING_STORAGE));
            return;
        }

        int matchedSlot = -1;
        boolean mismatched = false;
        for (int slot : OUTPUT_SLOTS) {
            ItemStack stack = menu.getItemInSlot(slot);
            if (stack == null || stack.getType().isAir()) {
                continue;
            }
            NetworkQuantumStorage storage = asQuantumStorage(stack);
            if (storage == null || !isEmptyStorage(stack)) {
                continue;
            }
            if (storage != required) {
                mismatched = true;
                continue;
            }
            if (matchedSlot < 0 || stack.getAmount() == 1) {
                matchedSlot = slot;
            }
        }

        if (matchedSlot < 0) {
            player.sendMessage(Lang.getString(mismatched
                ? CellnetText.CONVERTER_UPGRADE_TIER_MISMATCH
                : CellnetText.CONVERTER_UPGRADE_NO_SHELL));
            return;
        }

        consumeOneShell(menu, matchedSlot);
        long newUnits = CellTransferLogic.upgradedUnits(current, maxUnits);
        CellPersistence.setCurrentPerTypeLimit(cellItem, newUnits);
        StorageCell.applyLore(cellItem, per, newUnits);
        menu.replaceExistingItem(CELL_SLOT, cellItem);
        player.sendMessage(Lang.getString(CellnetText.CELL_UPGRADE_SUCCESS,
            NumberFormat.formatNumber(newUnits), NumberFormat.formatNumber(maxUnits)));
        refresh(menu);
        CellPersistence.saveAsync();
    }

    private void handleEmptyStorageClick(
        @NotNull Player player,
        @NotNull ItemStack cellItem,
        @NotNull QuantumEntry entry) {
        long per = StorageCell.getPerTypeLimit(cellItem);
        NetworkQuantumStorage required = CellTier.upgradeMaterialOf(per);
        if (required == null || entry.storage() != required) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_UPGRADE_TIER_MISMATCH));
            return;
        }
        if (!canUpgrade(cellItem)) {
            player.sendMessage(Lang.getString(CellnetText.CELL_UPGRADE_MAXED,
                NumberFormat.formatNumber(CellPersistence.getMaxUnitsFor(per))));
            return;
        }
        player.sendMessage(Lang.getString(CellnetText.CONVERTER_UPGRADE_CLICK_HINT));
    }

    private static void consumeOneShell(@NotNull BlockMenu menu, int slot) {
        ItemStack stack = menu.getItemInSlot(slot);
        if (stack == null) {
            return;
        }
        if (stack.getAmount() <= 1) {
            menu.replaceExistingItem(slot, null);
            return;
        }
        ItemStack remaining = stack.clone();
        remaining.setAmount(stack.getAmount() - 1);
        menu.replaceExistingItem(slot, remaining);
    }

    // ---------------------------------------------------------------------
    // 量子储存 -> 元件
    // ---------------------------------------------------------------------

    private void importItem(@NotNull BlockMenu menu, @NotNull Player player, int displaySlot) {
        ItemStack cellItem = menu.getItemInSlot(CELL_SLOT);
        if (cellItem == null || !StorageCell.isStorageCell(cellItem)) {
            return;
        }
        int idx = displayIndex(menu, displaySlot);
        List<QuantumEntry> entries = visibleQuantumEntries(menu);
        if (idx < 0 || idx >= entries.size()) {
            return;
        }
        QuantumEntry entry = entries.get(idx);

        if (!entry.hasItem()) {
            handleEmptyStorageClick(player, cellItem, entry);
            return;
        }

        ItemStack sample = entry.cache().getItemStack();
        long per = StorageCell.getPerTypeLimit(cellItem);
        CellLedger cache = StorageCell.loadCellCache(cellItem, per);

        if (cache.isWhitelistEnabled() && !isInCellWhitelist(cache, sample)) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_WHITELIST_REJECTED));
            return;
        }
        if (StackUtils.isBlacklisted(sample)) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_BLACKLISTED));
            return;
        }
        if (entry.stack().getAmount() != 1) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_SINGLE_STORAGE_ONLY));
            return;
        }
        if (!entry.isValid()) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_INVALID_STORAGE));
            return;
        }

        ItemKey key = new ItemKey(sample);
        long existing = cache.getAmount(key);
        long maxUnits = CellPersistence.getMaxUnitsFor(per);
        long planned = CellTransferLogic.importable(
            entry.amount(), existing, per, cache.typeCount(), maxUnits);
        if (planned <= 0) {
            player.sendMessage(Lang.getString(existing > 0 && existing >= per
                ? CellnetText.CONVERTER_ITEM_CAPACITY_FULL
                : CellnetText.CONVERTER_UNITS_FULL));
            return;
        }

        long moved = cache.pushItemLong(key, planned);
        if (moved <= 0) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_ITEM_CAPACITY_FULL));
            return;
        }

        long remainder = entry.amount() - moved;
        if (remainder <= 0) {
            ItemStack empty = CellQuantumConverter.buildEmptyQuantumStorage(entry.storage());
            empty.setAmount(entry.stack().getAmount());
            menu.replaceExistingItem(entry.slot(), empty);
        } else {
            menu.replaceExistingItem(entry.slot(), CellQuantumConverter.buildQuantumStorage(
                sample, remainder, entry.storage(), entry.limit()));
        }

        refresh(menu);
        CellPersistence.saveAsync();
    }

    // ---------------------------------------------------------------------
    // 元件 -> 量子储存
    // ---------------------------------------------------------------------

    private void transferItem(@NotNull BlockMenu menu, @NotNull Player player, int displaySlot) {
        CellContext ctx = resolveCellContext(menu, displaySlot);
        if (ctx == null || ctx.entry().amount <= 0) {
            return;
        }

        ItemStack sample = ctx.entry().sample;
        long cellAmount = ctx.entry().amount;
        ItemKey key = new ItemKey(sample);

        List<QuantumEntry> candidates = new ArrayList<>();
        for (QuantumEntry entry : listQuantumEntries(menu)) {
            if (entry.stack().getAmount() != 1) {
                continue;
            }
            boolean reusable = entry.isEmpty()
                || (entry.isValid() && new ItemKey(entry.cache().getItemStack()).equals(key));
            if (reusable && entry.freeCapacity() > 0) {
                candidates.add(entry);
            }
        }

        long[] freeCapacities = new long[candidates.size()];
        for (int i = 0; i < candidates.size(); i++) {
            freeCapacities[i] = candidates.get(i).freeCapacity();
        }
        int pick = CellTransferLogic.pickStorage(freeCapacities, cellAmount);
        if (pick >= 0) {
            QuantumEntry target = candidates.get(pick);
            long planned = CellTransferLogic.exportable(cellAmount, target.limit(), target.amount());
            long taken = planned > 0 ? ctx.cache().takeItemAmount(key, planned) : 0L;
            if (taken > 0) {
                ItemStack storedSample = target.isValid() ? target.cache().getItemStack() : sample;
                menu.replaceExistingItem(target.slot(), CellQuantumConverter.buildQuantumStorage(
                    storedSample, target.amount() + taken, target.storage(), target.limit()));
                player.sendMessage(Lang.getString(CellnetText.CONVERTER_TRANSFER_SUCCESS));
                refresh(menu);
                CellPersistence.saveAsync();
                return;
            }
        }

        int freeSlot = firstFreeOutputSlot(menu);
        if (freeSlot < 0) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_OUTPUT_FULL));
            return;
        }
        NetworkQuantumStorage targetStorage = resolveTargetStorage(ctx.per(), cellAmount);
        if (targetStorage == null) {
            player.sendMessage(Lang.getString(CellnetText.CONVERTER_NO_MATCHING_STORAGE));
            return;
        }
        long amount = Math.min(cellAmount, targetStorage.getMaxAmount());
        long taken = amount > 0 ? ctx.cache().takeItemAmount(key, amount) : 0L;
        if (taken <= 0) {
            return;
        }

        consumeStackedShell(menu);
        menu.replaceExistingItem(freeSlot,
            CellQuantumConverter.buildQuantumStorage(sample, taken, targetStorage));
        player.sendMessage(Lang.getString(CellnetText.CONVERTER_TRANSFER_SUCCESS));
        refresh(menu);
        CellPersistence.saveAsync();
    }

    @Nullable
    private static NetworkQuantumStorage resolveTargetStorage(long per, long amount) {
        return CellPersistence.isUnlimited(per)
            ? CellTier.storageForAmount(amount)
            : CellTier.upgradeMaterialOf(per);
    }

    private static void consumeStackedShell(@NotNull BlockMenu menu) {
        for (int slot : OUTPUT_SLOTS) {
            ItemStack stack = menu.getItemInSlot(slot);
            if (stack == null || stack.getType().isAir() || stack.getAmount() <= 1) {
                continue;
            }
            if (asQuantumStorage(stack) == null || !isEmptyStorage(stack)) {
                continue;
            }
            consumeOneShell(menu, slot);
            return;
        }
    }

    private int firstFreeOutputSlot(@NotNull BlockMenu menu) {
        for (int slot : OUTPUT_SLOTS) {
            ItemStack onSlot = menu.getItemInSlot(slot);
            if (onSlot == null || onSlot.getType().isAir()) {
                return slot;
            }
        }
        return -1;
    }

    // ---------------------------------------------------------------------
    // 量子储存扫描与展示
    // ---------------------------------------------------------------------

    @NotNull
    private static List<QuantumEntry> listQuantumEntries(@NotNull BlockMenu menu) {
        List<QuantumEntry> entries = new ArrayList<>();
        for (int slot : OUTPUT_SLOTS) {
            ItemStack stack = menu.getItemInSlot(slot);
            if (stack == null || stack.getType().isAir()) {
                continue;
            }
            NetworkQuantumStorage storage = asQuantumStorage(stack);
            if (storage == null) {
                continue;
            }
            ItemMeta meta = stack.getItemMeta();
            QuantumCache qc = meta == null ? null : Keys.getQuantumCache(meta);
            entries.add(new QuantumEntry(slot, stack, qc, storage));
        }
        return entries;
    }

    @NotNull
    private List<QuantumEntry> visibleQuantumEntries(@NotNull BlockMenu menu) {
        List<QuantumEntry> entries = listQuantumEntries(menu);
        String search = SEARCH_TERMS.get(menu.getLocation());
        if (search == null) {
            return entries;
        }
        List<QuantumEntry> filtered = new ArrayList<>(entries.size());
        for (QuantumEntry entry : entries) {
            ItemStack label = entry.isValid() ? entry.cache().getItemStack() : entry.stack();
            String name = ItemStackHelper.getDisplayName(label);
            if (name != null && ItemSearch.matches(search, name)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    private void renderQuantumItems(@NotNull BlockMenu menu) {
        List<QuantumEntry> entries = visibleQuantumEntries(menu);
        int page = normalizePage(menu, entries.size());
        for (int i = 0; i < DISPLAY_SLOTS.length; i++) {
            int index = page * itemsPerPage() + i;
            if (index < entries.size()) {
                QuantumEntry entry = entries.get(index);
                if (entry.isValid()) {
                    menu.replaceExistingItem(DISPLAY_SLOTS[i], CellSlotUI.displayItem(
                        entry.cache().getItemStack(), entry.amount(),
                        CellnetText.CONVERTER_ITEM_COUNT, CellnetText.CONVERTER_TRANSFER_HINT));
                    continue;
                }
                menu.replaceExistingItem(DISPLAY_SLOTS[i], buildEmptyStorageDisplay(entry));
                continue;
            }
            menu.replaceExistingItem(DISPLAY_SLOTS[i], Icon.CLEANER_DISPLAY);
        }
    }

    @NotNull
    private static ItemStack buildEmptyStorageDisplay(@NotNull QuantumEntry entry) {
        ItemStack display = entry.storage().getItem().clone();
        display.setAmount(1);
        String name = ItemStackHelper.getDisplayName(display);
        display.editMeta(meta -> {
            meta.setDisplayName(Lang.getString(CellnetText.CONVERTER_EMPTY_STORAGE_TITLE,
                name == null || name.isEmpty() ? display.getType().name() : name));
            meta.setLore(List.of(Lang.getString(CellnetText.CONVERTER_EMPTY_STORAGE_HINT)));
        });
        GhostItems.mark(display);
        return display;
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

    private static boolean isEmptyStorage(@NotNull ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return true;
        }
        QuantumCache qc = Keys.getQuantumCache(meta);
        return qc == null || qc.getItemStack() == null || qc.getAmountLong() <= 0;
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
