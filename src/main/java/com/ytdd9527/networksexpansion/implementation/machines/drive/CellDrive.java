package com.ytdd9527.networksexpansion.implementation.machines.drive;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.integrations.logitech.CellVirtualCache;
import com.balugaq.netex.integrations.logitech.LinkBindingStore;
import com.balugaq.netex.integrations.logitech.LinkerGrid;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.Scheduling;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellHandle;
import com.ytdd9527.networksexpansion.api.cell.CellMenuCommon;
import com.ytdd9527.networksexpansion.api.cell.CellPersistence;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.drive.CellUniqueness;
import com.ytdd9527.networksexpansion.api.drive.DriveCellSlots;
import com.ytdd9527.networksexpansion.api.drive.DriveOwnership;
import com.ytdd9527.networksexpansion.api.drive.DriveStorage;
import com.ytdd9527.networksexpansion.api.drive.DriveWhitelistMenu;
import com.ytdd9527.networksexpansion.api.filter.BrowseUI;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.utils.ChatInput;
import com.ytdd9527.networksexpansion.utils.ItemHashMap;
import com.ytdd9527.networksexpansion.utils.ItemKey;
import com.ytdd9527.networksexpansion.utils.ItemSearch;
import com.ytdd9527.networksexpansion.utils.MenuShells;
import com.ytdd9527.networksexpansion.utils.NumberFormat;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CellDrive extends SpecialSlimefunItem {

    public static final int[] CELL_SLOTS = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
    public static final int CELL_SLOT_COUNT = CELL_SLOTS.length;
    public static final int DISPLAY_SLOT = 4;
    public static final int ACCESS_SLOT = 13;
    public static final int[] MAIN_BACKGROUND_SLOTS = new int[]{
        0, 1, 2, 3, 5, 6, 7, 8, 9, 14, 15, 16, 17, 18, 22, 23, 24, 25, 26, 27, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44
    };
    public static final int[] PREVIEW_BACKGROUND_SLOTS = new int[]{1, 2, 3, 5, 6, 7, 46, 47, 48, 49, 50, 52, 53};
    public static final int PREVIEW_BACK = 45;

    public static final int WHITELIST_BUTTON_SLOT = 39;

    private static final long DISPLAY_REFRESH_INTERVAL_MS = 2000L;

    static final Map<ItemKey, String> ITEM_DISPLAY_NAMES = new ConcurrentHashMap<>();
    private static final Map<ChestMenu, Map<Integer, ItemKey>> PREVIEW_KEYS =
        Collections.synchronizedMap(new WeakHashMap<>());

    static final String CELL_COUNT_TEMPLATE = Lang.getString(CellnetText.DRIVE_CELL_COUNT);
    static final String TOTAL_ITEMS_TEMPLATE = Lang.getString(CellnetText.DRIVE_TOTAL_ITEMS);
    static final String ITEM_TYPES_TEMPLATE = Lang.getString(CellnetText.DRIVE_ITEM_TYPES);
    static final String ITEM_ENTRY_TEMPLATE = Lang.getString(CellnetText.DRIVE_ITEM_ENTRY);
    static final String MORE_ITEMS_TEXT = Lang.getString(CellnetText.DRIVE_MORE_ITEMS);
    private static final String DISPLAY_NAME_TEXT = Lang.getString(CellnetText.DRIVE_DISPLAY_NAME);

    private static final DriveStorage storage = new DriveStorage();

    private static final Map<Location, DriveState> STATES = new ConcurrentHashMap<>();

    private static final class DriveState {
        int page;
        String search;
        long displayRefreshTimestamp;
    }

    private static DriveState state(@NotNull Location loc) {
        return STATES.computeIfAbsent(loc, k -> new DriveState());
    }

    @NotNull
    public static DriveStorage getStorage() {
        return storage;
    }

    public CellDrive(
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
                DriveOwnership.writeOwner(event.getBlock().getLocation(), event.getPlayer().getUniqueId());
            }
        });
        addItemHandler(
            new BlockTicker() {

                @Override
                public boolean isSynchronized() {
                    return false;
                }

                @Override
                public void tick(@NotNull Block b, SlimefunItem item, SlimefunBlockData data) {
                    final Location location = b.getLocation();
                    final long now = System.currentTimeMillis();
                    final DriveState driveState = state(location);
                    final long last = driveState.displayRefreshTimestamp;
                    final boolean shouldRefresh = last == 0L || now - last >= DISPLAY_REFRESH_INTERVAL_MS;
                    if (shouldRefresh) {
                        driveState.displayRefreshTimestamp = now;
                    }
                    if (!shouldRefresh) {
                        return;
                    }
                    Scheduling.runTask(() -> {
                        final BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
                        if (blockMenu == null || !blockMenu.hasViewer()) {
                            return;
                        }
                        updateMainDisplay(blockMenu);
                    });
                }
            },
            new BlockBreakHandler(false, false) {

                @Override
                public void onPlayerBreak(@NotNull BlockBreakEvent event, @NotNull ItemStack item, @NotNull List<ItemStack> drops) {
                    if (!canBreak(event.getPlayer(), event.getBlock())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Lang.getString(CellnetText.DRIVE_BREAK_NOT_ALLOWED));
                        return;
                    }
                    onBreak(event);
                }
            });
    }

    @Override
    public void postRegister() {
        MenuShells.create(this, 45, MAIN_BACKGROUND_SLOTS,
            (block, player) -> DriveOwnership.bypasses(player)
                || DriveOwnership.passesGates(player, block.getLocation(),
                    Interaction.INTERACT_BLOCK, canUse(player, false)),
            MenuShells::emptyTransport,
            (menu, block) -> {
                warmOwnerCache(block.getLocation());
                LinkBindingStore.replayDrive(block.getLocation());
                setupMainMenuHandlers(menu, block);
                updateMainDisplay(menu);
            });
    }

    private static void warmOwnerCache(@NotNull Location location) {
        getOwnerUuid(location);
    }

    private void onBreak(@NotNull BlockBreakEvent event) {
        final Location location = event.getBlock().getLocation();
        clearLocalState(location);
        CellUniqueness.unregisterDrive(location);
        storage.dropCellCache(location);
        CellVirtualCache.removeDrive(location);
        final BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
        if (blockMenu != null) {
            blockMenu.dropItems(location, CELL_SLOTS);
        }
    }

    static void clearLocalState(@NotNull Location location) {
        STATES.remove(location);
        DriveWhitelistMenu.invalidateButton(location);
        DriveOwnership.clearOwnerCache(location);
        LinkBindingStore.forget(location);
    }

    public static void saveAllDriveCells() {
        Networks.getInstance().getLogger().info(Lang.getString(CellnetText.DRIVE_SAVING));
        CellPersistence.saveAsync();
    }

    private void setupMainMenuHandlers(@NotNull BlockMenu menu, @NotNull Block block) {
        menu.addMenuClickHandler(DISPLAY_SLOT, (p, s, i, a) -> false);

        menu.addMenuClickHandler(ACCESS_SLOT, (player, clickedSlot, item, action) -> {
            openItemAccessMenu(menu, block.getLocation(), player);
            return false;
        });

        menu.addMenuClickHandler(WHITELIST_BUTTON_SLOT, (player, clickedSlot, item, action) -> {
            DriveWhitelistMenu.open(menu, block.getLocation(), player);
            return false;
        });

        menu.addMenuCloseHandler(p -> {
            storage.invalidateCellCache(menu.getLocation());
            CellUniqueness.registerDrive(menu);
        });
        menu.addMenuOpeningHandler(player -> {
            for (int slot : CELL_SLOTS) {
                DriveCellSlots.refreshCellLore(menu, slot);
            }
            CellUniqueness.scanAndEjectDuplicates(menu, player);
        });
    }

    public static void openItemAccessMenu(@NotNull BlockMenu driveMenu, @NotNull Location location, @NotNull Player player) {
        state(location).page = 0;

        ChestMenu menu = new ChestMenu(Lang.getString(CellnetText.DRIVE_PREVIEW_TITLE));
        menu.setPlayerInventoryClickable(true);

        for (int slot : PREVIEW_BACKGROUND_SLOTS) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), (p, s, i, a) -> false);
        }
        menu.addItem(PREVIEW_BACK, BrowseUI.backButton(), (p, s, i, a) -> {
            BlockMenu dm = StorageCacheUtils.getMenu(location);
            if (dm != null) {
                dm.open(p);
            }
            return false;
        });

        renderBrowser(menu, location);
        menu.setEmptySlotsClickable(false);
        menu.open(player);
    }

    private static void renderBrowser(@NotNull ChestMenu menu, @NotNull Location location) {
        BlockMenu driveMenu = StorageCacheUtils.getMenu(location);
        List<CellHandle> cells = driveMenu != null ? storage.getCells(driveMenu) : List.of();
        Map<ItemStack, Long> allItems = storage.getAllCellItems(cells);
        DriveState driveState = state(location);
        List<Map.Entry<ItemStack, Long>> itemList = filterBrowserEntries(new ArrayList<>(allItems.entrySet()),
            driveState.search);

        int page = driveState.page;
        int totalPages = BrowseUI.totalPages(itemList.size());
        if (page >= totalPages) {
            page = totalPages - 1;
            driveState.page = page;
        }

        int start = page * BrowseUI.PAGE_SIZE;
        int end = Math.min(start + BrowseUI.PAGE_SIZE, itemList.size());

        Map<Integer, ItemKey> slotKeys = new ConcurrentHashMap<>();
        for (int i = 0; i < end - start; i++) {
            slotKeys.put(BrowseUI.LIST_SLOTS[i], new ItemKey(itemList.get(start + i).getKey()));
        }
        PREVIEW_KEYS.put(menu, slotKeys);
        BrowseUI.renderEntries(menu, itemList, start, end,
            entry -> CellMenuCommon.browseEntry(cells, entry.getKey(), entry.getValue()),
            (p, s, it, a) -> handleEntryToggle(p, s, a, driveMenu, menu, location));
        BrowseUI.wirePager(menu, page, totalPages, target -> {
            driveState.page = target;
            renderBrowser(menu, location);
        });
        wireBrowserSearch(menu, location);
    }

    private static void handleEntryToggle(
            @NotNull Player player,
            int slot,
            @NotNull ClickAction action,
            @Nullable BlockMenu driveMenu,
            @NotNull ChestMenu menu,
            @NotNull Location location) {
        if (!action.isShiftClicked() || driveMenu == null) {
            return;
        }
        ItemKey key = PREVIEW_KEYS.getOrDefault(menu, Map.of()).get(slot);
        if (key == null) {
            return;
        }
        CellHandle owner = CellMenuCommon.findCellHolding(storage.getCells(driveMenu), key);
        if (owner != null && CellMenuCommon.toggleEntry(player, driveMenu, owner.getUuid(), key, action.isRightClicked())) {
            renderBrowser(menu, location);
        }
    }

    @NotNull
    private static List<Map.Entry<ItemStack, Long>> filterBrowserEntries(
            @NotNull List<Map.Entry<ItemStack, Long>> entries, @Nullable String search) {
        return ItemSearch.filterByDisplayName(search, entries,
            entry -> ItemStackHelper.getDisplayName(entry.getKey()));
    }

    private static void wireBrowserSearch(@NotNull ChestMenu menu, @NotNull Location location) {
        DriveState driveState = state(location);
        menu.replaceExistingItem(BrowseUI.SEARCH, ItemSearch.searchIcon(driveState.search));
        menu.addMenuClickHandler(BrowseUI.SEARCH, (player, s, i, a) -> {
            if (a.isRightClicked()) {
                driveState.search = null;
                driveState.page = 0;
                renderBrowser(menu, location);
                player.sendMessage(Lang.getString(CellnetText.SEARCH_CLEARED));
            } else {
                ItemSearch.requestSearch(player,
                    ChatInput.SearchTarget.DRIVE_BROWSER, location, null, null);
            }
            return false;
        });
    }

    public static void applyBrowserSearchFromChat(@NotNull Player player, @NotNull Location location, @NotNull String term) {
        if (term.isEmpty()) {
            state(location).search = null;
        } else {
            state(location).search = term;
        }
        player.sendMessage(Lang.getString(term.isEmpty()
            ? CellnetText.SEARCH_CLEARED
            : CellnetText.SEARCH_SET, term));
        BlockMenu driveMenu = StorageCacheUtils.getMenu(location);
        if (driveMenu != null) {
            openItemAccessMenu(driveMenu, location, player);
        }
    }

    private void updateMainDisplay(@NotNull BlockMenu menu) {
        long totalStored = 0;
        int cellCount = 0;

        List<CellHandle> handles = storage.getCells(menu);
        for (CellHandle cell : handles) {
            totalStored += cell.getStoredCount();
            cellCount++;
        }

        ItemHashMap<Long> aggregatedItems = storage.getAllCellItemsKeyed(handles);

        menu.replaceExistingItem(DISPLAY_SLOT, buildDisplayItem(cellCount, totalStored, aggregatedItems));
        menu.replaceExistingItem(ACCESS_SLOT, buildAccessButton());
        menu.replaceExistingItem(WHITELIST_BUTTON_SLOT, DriveWhitelistMenu.whitelistButton(menu.getLocation()));
    }

    private static ItemStack buildDisplayItem(int cellCount, long totalStored, @NotNull ItemHashMap<Long> aggregatedItems) {
        ItemStack display = new ItemStack(Material.PAPER);
        ItemMeta meta = display.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add(MessageFormat.format(CELL_COUNT_TEMPLATE, cellCount, CELL_SLOT_COUNT));
            lore.add(MessageFormat.format(TOTAL_ITEMS_TEMPLATE, NumberFormat.formatNumber(totalStored)));
            lore.add(MessageFormat.format(ITEM_TYPES_TEMPLATE, aggregatedItems.size()));
            if (LinkerGrid.initialized) {
                lore.add(Lang.getString(CellnetText.DRIVE_LINK_SUPPORT));
            }
            lore.add("");

            int count = 0;
            for (Map.Entry<ItemKey, Long> entry : aggregatedItems.keyEntrySet()) {
                if (count >= 8) {
                    lore.add(MORE_ITEMS_TEXT);
                    break;
                }
                String name = ITEM_DISPLAY_NAMES.computeIfAbsent(entry.getKey(),
                    k -> ItemStackHelper.getDisplayName(k.getItemStack()));
                lore.add(MessageFormat.format(ITEM_ENTRY_TEMPLATE, name, NumberFormat.formatNumber(entry.getValue())));
                count++;
            }

            meta.setLore(lore);
            meta.setDisplayName(DISPLAY_NAME_TEXT);
            display.setItemMeta(meta);
        }
        return display;
    }

    @NotNull
    private static ItemStack buildAccessButton() {
        return Icon.DRIVE_BROWSE;
    }

    @Nullable
    public static UUID getOwnerUuid(@NotNull Location location) {
        return DriveOwnership.getOwnerUuid(location);
    }

    private boolean canBreak(@NotNull Player player, @NotNull Block block) {
        return DriveOwnership.bypasses(player)
                || DriveOwnership.passesGates(player, block.getLocation(),
                    Interaction.BREAK_BLOCK, canUse(player, false));
    }
}
