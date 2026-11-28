package com.ytdd9527.networksexpansion.implementation.machines.converter;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellLedger;
import com.ytdd9527.networksexpansion.api.cell.CellPersistence;
import com.ytdd9527.networksexpansion.api.cell.CellSlotUI;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.cell.StorageCell;
import com.ytdd9527.networksexpansion.core.machines.AbstractCellBrowser;
import com.ytdd9527.networksexpansion.utils.ChatInput;
import com.ytdd9527.networksexpansion.utils.MenuShells;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CellCleaner extends AbstractCellBrowser {

    public static final int[] DISPLAY_SLOTS = new int[]{
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35,
        36, 37, 38, 39, 40, 41, 42, 43, 44
    };
    public static final int[] BACKGROUND_SLOTS = new int[]{
        1, 2, 3, 5, 6, 7,
        46, 47, 48, 49, 50, 51, 52, 53
    };

    private static final ItemStack CELL_SLOT_MARKER = buildCellSlotMarker();

    public CellCleaner(
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
        return CellnetText.CLEANER;
    }

    @Override
    protected boolean shouldHandleCellSlotClick(@NotNull ClickAction action) {
        return !action.isShiftClicked();
    }

    @Override
    public @NotNull ChatInput.SearchTarget searchTarget() {
        return ChatInput.SearchTarget.CELL_CLEANER;
    }

    @Override
    public @NotNull ItemStack createCellSlotMarker() {
        return CELL_SLOT_MARKER.clone();
    }

    @Override
    public boolean isCellSlotMarker(@Nullable ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(io.github.sefiraat.networks.utils.Keys.CELL_CLEANER_MARKER, PersistentDataType.BOOLEAN);
    }

    @NotNull
    private static ItemStack buildCellSlotMarker() {
        ItemStack marker = Icon.CLEANER_CELL_SLOT.clone();
        ItemMeta meta = marker.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(io.github.sefiraat.networks.utils.Keys.CELL_CLEANER_MARKER, PersistentDataType.BOOLEAN, true);
            marker.setItemMeta(meta);
        }
        return marker;
    }

    @Override
    public void onDisplayClick(@NotNull BlockMenu menu, @NotNull Player player, int slot, @Nullable ItemStack item, @NotNull ClickAction action) {
        ItemStack cursor = player.getItemOnCursor();
        if (cursor != null && !cursor.getType().isAir()) {
            return;
        }
        if (action.isShiftClicked()) {
            deleteItem(menu, slot);
        }
    }

    @Override
    public void onCellInserted(@NotNull BlockMenu menu, @NotNull ItemStack cellItem, @NotNull Player player) {
        if (!CellSlotUI.prepareInsertedCell(player, cellItem)) {
            return;
        }
        pageCache.put(menu.getLocation(), 0);
        menu.replaceExistingItem(CELL_SLOT, cellItem);
        refresh(menu);
    }

    @Override
    public @Nullable ItemStack onCellEjected(@NotNull BlockMenu menu, int slot) {
        return CellSlotUI.ejectCell(menu, slot,
            () -> CELL_SLOT_MARKER.clone(), DISPLAY_SLOTS, pageCache, () -> refresh(menu));
    }

    @Override
    public void refresh(@NotNull BlockMenu menu) {
        ItemStack cellItem = menu.getItemInSlot(CELL_SLOT);

        if (cellItem == null || !StorageCell.isStorageCell(cellItem)) {
            menu.replaceExistingItem(CELL_SLOT, CELL_SLOT_MARKER.clone());
            CellSlotUI.clearDisplay(menu, DISPLAY_SLOTS);
            menu.replaceExistingItem(INFO, buildInfoItem(null, 0));
            menu.replaceExistingItem(PREV, pageButton(false, false));
            menu.replaceExistingItem(NEXT, pageButton(false, true));
            return;
        }

        long per = StorageCell.getPerTypeLimit(cellItem);
        CellLedger cache = StorageCell.loadCellCache(cellItem, per);

        StorageCell.applyLore(cellItem, per, StorageCell.getCurrentPerTypeLimit(cellItem));
        menu.replaceExistingItem(CELL_SLOT, cellItem);

        List<CellLedger.CellEntry> entries = visibleEntries(menu, cache);
        int page = normalizePage(menu, entries.size());
        int maxPages = Math.max(1, (int) Math.ceil((double) entries.size() / itemsPerPage()));

        int start = page * itemsPerPage();
        int end = Math.min(start + itemsPerPage(), entries.size());

        for (int i = 0; i < DISPLAY_SLOTS.length; i++) {
            int slot = DISPLAY_SLOTS[i];
            if (i < end - start) {
                CellLedger.CellEntry entry = entries.get(start + i);
                menu.replaceExistingItem(slot, CellSlotUI.displayItem(
                    entry.sample, entry.amount,
                    CellnetText.CLEANER_ITEM_COUNT, CellnetText.CLEANER_DELETE_HINT));
            } else {
                menu.replaceExistingItem(slot, entries.isEmpty()
                    ? Icon.SEARCH_EMPTY
                    : Icon.CLEANER_DISPLAY);
            }
        }

        menu.replaceExistingItem(PREV, pageButton(page > 0, false));
        menu.replaceExistingItem(NEXT, pageButton(page < maxPages - 1, true));
        menu.replaceExistingItem(INFO, buildInfoItem(cellItem, entries.size()));
        wireSearchButton(menu);
    }

    @Override
    public void preRegister() {
        addItemHandler(
            new BlockBreakHandler(false, false) {
                @Override
                public void onPlayerBreak(@NotNull BlockBreakEvent event, @NotNull ItemStack item, @NotNull List<ItemStack> drops) {
                    onBreak(event);
                }
            });
    }

    @Override
    public void postRegister() {
        MenuShells.create(this, 54, BACKGROUND_SLOTS,
            (block, player) -> player.hasPermission("slimefun.inventory.bypass")
                || (canUse(player, false)
                && Slimefun.getProtectionManager()
                .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK)),
            MenuShells::emptyTransport,
            (menu, block) -> {
                ItemStack existing = menu.getItemInSlot(CELL_SLOT);
                if (existing == null || existing.getType().isAir()) {
                    menu.replaceExistingItem(CELL_SLOT, CELL_SLOT_MARKER.clone());
                }
                for (int slot : DISPLAY_SLOTS) {
                    menu.replaceExistingItem(slot, Icon.CLEANER_DISPLAY);
                }
                setupSharedHandlers(menu);
                refresh(menu);
                menu.addMenuOpeningHandler(p -> refresh(menu));
                menu.addMenuCloseHandler(p -> clearDisplaySlots(menu));
            });
    }

    private void onBreak(@NotNull BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        onBreakCleanup(location);
        BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
        if (blockMenu != null) {
            ItemStack cellItem = blockMenu.getItemInSlot(CELL_SLOT);
            if (cellItem != null && StorageCell.isStorageCell(cellItem)) {
                blockMenu.dropItems(location, CELL_SLOT);
            }
        }
    }

    private void clearDisplaySlots(@NotNull BlockMenu menu) {
        for (int slot : DISPLAY_SLOTS) {
            menu.replaceExistingItem(slot, Icon.CLEANER_DISPLAY);
        }
    }

    private void deleteItem(@NotNull BlockMenu menu, int displaySlot) {
        ItemStack cellItem = menu.getItemInSlot(CELL_SLOT);
        if (cellItem == null || !StorageCell.isStorageCell(cellItem)) {
            return;
        }
        long per = StorageCell.getPerTypeLimit(cellItem);
        CellLedger cache = StorageCell.loadCellCache(cellItem, per);

        int idx = displayIndex(menu, displaySlot);
        if (idx < 0) {
            return;
        }

        List<CellLedger.CellEntry> entries = visibleEntries(menu, cache);
        if (idx >= entries.size()) {
            return;
        }

        CellLedger.CellEntry target = entries.get(idx);
        cache.takeItem(target.sample, target.amount);
        menu.replaceExistingItem(CELL_SLOT, cellItem);
        refresh(menu);

        CellPersistence.saveAsync();
    }

    @NotNull
    private static ItemStack buildInfoItem(@Nullable ItemStack cellItem, int types) {
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta meta = info.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            meta.setDisplayName(Lang.getString(CellnetText.CLEANER_INFO_TITLE));
            if (cellItem != null && StorageCell.isStorageCell(cellItem)) {
                lore.add(Lang.getString(CellnetText.CLEANER_CELL_NAME, ItemStackHelper.getDisplayName(cellItem)));
                lore.add(Lang.getString(CellnetText.CLEANER_ITEM_TYPES, types));
            } else {
                lore.add(Lang.getString(CellnetText.CLEANER_NO_CELL));
            }
            meta.setLore(lore);
            info.setItemMeta(meta);
        }
        return info;
    }

    public static void applySearchFromChat(@NotNull Player player, @NotNull Location location, @NotNull String term) {
        applySearchFromChat(player, location, term,
            StorageCacheUtils::getMenu,
            sf -> sf instanceof CellCleaner,
            menu -> {
                CellCleaner cleaner = (CellCleaner) StorageCacheUtils.getSfItem(location);
                if (cleaner != null) {
                    cleaner.pageCache.put(location, 0);
                    cleaner.refresh(menu);
                }
            });
    }
}
