package com.ytdd9527.networksexpansion.core.machines;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellLedger;
import com.ytdd9527.networksexpansion.api.cell.CellSlotUI;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.cell.StorageCell;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.utils.ChatInput;
import com.ytdd9527.networksexpansion.utils.ItemSearch;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCellBrowser extends SpecialSlimefunItem {

    public static final int CELL_SLOT = 4;
    public static final int PREV = 0;
    public static final int NEXT = 8;
    public static final int INFO = 45;
    public static final int SEARCH_SLOT = 53;

    protected static final Map<Location, String> SEARCH_TERMS = new ConcurrentHashMap<>();
    protected final Map<Location, Integer> pageCache = new ConcurrentHashMap<>();

    public AbstractCellBrowser(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public abstract int[] displaySlots();

    public abstract int[] backgroundSlots();

    public abstract @NotNull String langPrefix();

    public abstract @NotNull ChatInput.SearchTarget searchTarget();

    public abstract @NotNull ItemStack createCellSlotMarker();

    public abstract boolean isCellSlotMarker(@Nullable ItemStack item);

    public abstract void onDisplayClick(@NotNull BlockMenu menu, @NotNull Player player, int slot, @Nullable ItemStack item, @NotNull ClickAction action);

    public abstract void onCellInserted(@NotNull BlockMenu menu, @NotNull ItemStack cell, @NotNull Player player);

    public abstract @Nullable ItemStack onCellEjected(@NotNull BlockMenu menu, int slot);

    public abstract void refresh(@NotNull BlockMenu menu);

    protected boolean shouldHandleCellSlotClick(@NotNull ClickAction action) {
        return true;
    }

    protected void setupMachineHandlers(@NotNull BlockMenu menu) {
    }

    protected int itemsPerPage() {
        return displaySlots().length;
    }

    protected void onBreakCleanup(@NotNull Location location) {
        pageCache.remove(location);
        SEARCH_TERMS.remove(location);
    }

    @NotNull
    protected List<CellLedger.CellEntry> visibleEntries(@NotNull BlockMenu menu, @NotNull CellLedger cache) {
        return ItemSearch.filterByDisplayName(SEARCH_TERMS.get(menu.getLocation()),
            cache.getStoredItems(), entry -> ItemStackHelper.getDisplayName(entry.sample));
    }

    protected int normalizePage(@NotNull BlockMenu menu, int total) {
        return CellSlotUI.normalizedPage(pageCache, menu.getLocation(), total, itemsPerPage());
    }

    protected int maxPages(@NotNull BlockMenu menu) {
        ItemStack cellItem = menu.getItemInSlot(CELL_SLOT);
        if (cellItem == null || !StorageCell.isStorageCell(cellItem)) {
            return 1;
        }
        long per = StorageCell.getPerTypeLimit(cellItem);
        CellLedger cache = StorageCell.loadCellCache(cellItem, per);
        int count = visibleEntries(menu, cache).size();
        return Math.max(1, (int) Math.ceil((double) count / itemsPerPage()));
    }

    @NotNull
    protected ItemStack pageButton(boolean enabled, boolean next) {
        String p = langPrefix();
        return CellSlotUI.pageButton(enabled, next,
            p + ".prev_page", p + ".first_page",
            p + ".next_page", p + ".last_page");
    }

    protected void wireSearchButton(@NotNull BlockMenu menu) {
        menu.replaceExistingItem(SEARCH_SLOT,
            ItemSearch.searchIcon(SEARCH_TERMS.get(menu.getLocation())));
        menu.addMenuClickHandler(SEARCH_SLOT, (player, slot, item, action) -> {
            if (action.isRightClicked()) {
                SEARCH_TERMS.remove(menu.getLocation());
                pageCache.put(menu.getLocation(), 0);
                refresh(menu);
                player.sendMessage(Lang.getString(CellnetText.SEARCH_CLEARED));
            } else {
                ItemSearch.requestSearch(player, searchTarget(), menu.getLocation(), null, null);
            }
            return false;
        });
    }

    protected static void applySearchFromChat(
            @NotNull Player player,
            @NotNull Location location,
            @NotNull String term,
            @NotNull java.util.function.Function<Location, BlockMenu> menuGetter,
            @NotNull java.util.function.Predicate<SlimefunItem> typeCheck,
            @NotNull java.util.function.Consumer<BlockMenu> onFound) {
        if (term.isEmpty()) {
            SEARCH_TERMS.remove(location);
        } else {
            SEARCH_TERMS.put(location.clone(), term);
        }
        player.sendMessage(Lang.getString(term.isEmpty()
            ? CellnetText.SEARCH_CLEARED
            : CellnetText.SEARCH_SET, term));
        BlockMenu menu = menuGetter.apply(location);
        var sf = StorageCacheUtils.getSfItem(location);
        if (menu != null && typeCheck.test(sf)) {
            onFound.accept(menu);
            menu.open(player);
        }
    }

    protected void setupSharedHandlers(@NotNull BlockMenu menu) {
        menu.addMenuClickHandler(CELL_SLOT, (player, slot, item, action) -> {
            if (!shouldHandleCellSlotClick(action)) {
                return true;
            }
            return CellSlotUI.handleCellSlotClick(menu, slot, item, player,
                this::isCellSlotMarker,
                (m, placed) -> onCellInserted(m, placed, player),
                this::onCellEjected);
        });

        for (int displaySlot : displaySlots()) {
            menu.addMenuClickHandler(displaySlot, (player, slot, item, action) -> {
                if (!isPlaceholder(item)) {
                    onDisplayClick(menu, player, slot, item, action);
                }
                return false;
            });
        }

        setupMachineHandlers(menu);

        CellSlotUI.wirePageButtons(menu, PREV, NEXT, pageCache, this::maxPages, this::refresh);
        menu.addMenuClickHandler(INFO, (player, slot, item, action) -> false);
    }

    protected static boolean isPlaceholder(@Nullable ItemStack item) {
        return item == null || item.getType().isAir() || item.getType() == Icon.CLEANER_DISPLAY.getType();
    }

    protected int displayIndex(@NotNull BlockMenu menu, int displaySlot) {
        int page = pageCache.getOrDefault(menu.getLocation(), 0);
        return CellSlotUI.pageIndex(displaySlots(), displaySlot, page, itemsPerPage());
    }
}
