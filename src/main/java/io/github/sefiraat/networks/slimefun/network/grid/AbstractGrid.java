package io.github.sefiraat.networks.slimefun.network.grid;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.api.visual.Screen;
import com.balugaq.netex.utils.InventoryUtil;
import com.balugaq.netex.utils.Lang;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.GridItemRequest;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@NullMarked
@SuppressWarnings({"DuplicatedCode", "deprecation"})
public abstract class AbstractGrid extends NetworkObject {
    private static final Map<Location, GridCache> CACHE_MAP = new HashMap<>();
    protected static final String BS_FILTER_KEY = "filter";

    protected final ItemSetting<Integer> tickRate;

    protected AbstractGrid(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        ItemStack [] recipe) {
        this(itemGroup, item, recipeType, recipe, NodeType.GRID);
    }

    protected AbstractGrid(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        ItemStack [] recipe,
        NodeType type) {
        super(itemGroup, item, recipeType, recipe, type);

        if (getInputSlot() != -1) {
            this.getSlotsToDrop().add(getInputSlot());
        }

        this.tickRate = new IntRangeSetting(this, "tick_rate", 1, 1, 10);
        addItemSetting(this.tickRate);

        addItemHandler(new BlockTicker() {
            private int tick = 1;

            @Override
            public boolean isSynchronized() {
                return false;
            }

            @Override
            public void tick(Block block, SlimefunItem item, SlimefunBlockData data) {
                if (tick <= 1) {
                    final BlockMenu blockMenu = data.getBlockMenu();
                    if (blockMenu == null) {
                        return;
                    }
                    addToRegistry(block);
                    syncSkin(block);
                    tryAddItem(blockMenu);
                    GridCache cache = getCacheMap().get(block.getLocation());
                    if (cache != null) {
                        cache.setEntriesCache(null);
                        updateDisplay(Screen.of(blockMenu));
                    }
                }
            }

            @Override
            public void uniqueTick() {
                tick = tick <= 1 ? tickRate.getValue() : tick - 1;
            }

            @Override
            public Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
                return Optional.empty();
            }
        });
    }

    protected void syncSkin(Block block) {
    }

    private static List<String> getLoreAddition(long amount) {
        final MessageFormat format =
            new MessageFormat(Lang.getString("messages.normal-operation.grid.item_amount"), Locale.ROOT);
        return List.of(
            "",
            format.format(
                    new Object[]{Theme.CLICK_INFO.getColor(), Theme.PASSIVE.getColor(), amount},
                    new StringBuffer(),
                    null)
                .toString());
    }

    protected void tryAddItem(BlockMenu blockMenu) {
        if (getInputSlot() == -1) return;

        final ItemStack itemStack = blockMenu.getItemInSlot(getInputSlot());

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());
        if (definition == null || definition.getNode() == null) {
            return;
        }

        definition.getNode().getRoot().addItemStack(blockMenu.getLocation(), itemStack);
    }

    public ItemStack getFilterStack(@Nullable String filter) {
        if (filter == null) return getFilterStack();
        ItemStack clone = getFilterStack().clone();
        clone.setLore(List.of(String.format(Lang.getString("messages.normal-operation.grid.filter"), filter)));
        return clone;
    }

    public ItemStack getSortOrderStack(GridCache.SortOrder sortOrder) {
        ItemStack clone = getChangeSortStack().clone();
        clone.setLore(List.of(sortOrder.getTranslationName()));
        return clone;
    }

    protected void updateDisplay(BlockMenu menu) {
        updateDisplay(Screen.of(menu));
    }

    @SuppressWarnings("deprecation")
    protected void updateDisplay(Screen screen) {
        Location location = screen.getLocation();

        // No viewer - lets not bother updating
        if (!screen.hasViewer()) {
            sendFeedback(location, FeedbackType.AFK);
            return;
        }

        final NodeDefinition definition = NetworkStorage.getNode(location);

        // No node located, weird
        if (definition == null || definition.getNode() == null) {
            clearDisplay(screen);
            sendFeedback(location, FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        // Update Screen
        final BlockMenu bm = StorageCacheUtils.getMenu(location);
        if (bm == null) {
            return;
        }

        final NetworkRoot root = definition.getNode().getRoot();

        final GridCache gridCache = getCacheMap().get(screen.getLocation().clone());
        final List<Map.Entry<ItemStack, Long>> entries = getEntries(root, gridCache);
        final int pages = (int) Math.ceil(entries.size() / (double) getDisplaySlots().length) - 1;

        gridCache.setMaxPages(pages);

        // Set everything to blank and return if there are no pages (no items)
        if (pages < 0) {
            clearDisplay(screen);
            return;
        }

        screen.setItem(getChangeSort(), getSortOrderStack(gridCache.getSortOrder()));
        screen.setItem(getFilterSlot(), getFilterStack(gridCache.getFilter()));

        // Rolldown selected page if it no longer exists due to items being removed
        if (gridCache.getPage() > pages) {
            gridCache.setPage(pages);
        }

        int start = gridCache.getPage() * getDisplaySlots().length;
        if (start < 0) {
            start = 0;
        }
        final int end = Math.min(start + getDisplaySlots().length, entries.size());
        final List<Map.Entry<ItemStack, Long>> validEntries = entries.subList(start, end);

        getCacheMap().put(screen.getLocation(), gridCache);

        for (int i = 0; i < getDisplaySlots().length; i++) {
            if (validEntries.size() > i) {
                final Map.Entry<ItemStack, Long> entry = validEntries.get(i);
                final ItemStack displayStack = entry.getKey().clone();
                final ItemMeta itemMeta = displayStack.getItemMeta();
                if (itemMeta == null) {
                    continue;
                }
                List<String> lore = itemMeta.getLore();

                if (lore == null) {
                    lore = getLoreAddition(entry.getValue());
                } else {
                    lore.addAll(getLoreAddition(entry.getValue()));
                }

                itemMeta.setLore(lore);
                displayStack.setItemMeta(itemMeta);
                screen.setItem(getDisplaySlots()[i], displayStack, (player, slot, item, action) -> {
                    takeItem(player, item, action, screen);
                    return false;
                });
            } else {
                screen.setItem(getDisplaySlots()[i], Icon.BLANK_SLOT_STACK, (p, slot, item, action) -> {
                    receiveItem(p, p.getItemOnCursor(), screen);
                    return false;
                });
            }
        }
        screen.setItem(
            getPagePrevious(),
            Icon.getPageStack(getPagePreviousStack(), gridCache.getPage() + 1, gridCache.getMaxPages() + 1));
        screen.setItem(
            getPageNext(),
            Icon.getPageStack(getPageNextStack(), gridCache.getPage() + 1, gridCache.getMaxPages() + 1));

        sendFeedback(screen.getLocation(), FeedbackType.WORKING);
    }

    protected void clearDisplay(Screen blockMenu) {
        for (int displaySlot : getDisplaySlots()) {
            blockMenu.setItem(displaySlot, getBlankSlotStack(), (p, slot, item, action) -> false);
        }
    }

    protected List<Map.Entry<ItemStack, Long>> getEntries(NetworkRoot networkRoot, GridCache cache) {
        if (cache.getEntriesCache() != null) {
            return cache.getEntriesCache();
        }
        var entries = filterAndSort(networkRoot.getAllNetworkItemsLongTypeView(), cache.getFilter(), cache.getSortOrder());
        cache.setEntriesCache(entries);
        return entries;
    }

    public static List<Map.Entry<ItemStack, Long>> filterAndSort(
        Map<ItemStack, Long> items,
        @Nullable String filter,
        GridCache.SortOrder sortOrder) {
        return items.entrySet().stream()
            .filter(entry -> {
                if (filter == null) {
                    return true;
                }

                final ItemStack itemStack = entry.getKey();
                String name = TextUtil.stripColor(
                    ItemStackHelper.getDisplayName(itemStack).toLowerCase(Locale.ROOT));
                if (filter.matches("^[a-zA-Z]+$")) {
                    final boolean pinyinEnabled = Networks.getConfigManager().isPinyinSearchEnabled();
                    final String pinyinName = pinyinEnabled ? PinyinHelper.toPinyin(name, PinyinStyleEnum.INPUT, "") : "";
                    final String pinyinFirstLetter = pinyinEnabled ? PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "") : "";
                    return name.contains(filter)
                        || pinyinName.contains(filter)
                        || pinyinFirstLetter.contains(filter);
                } else {
                    return name.contains(filter);
                }
            })
            .sorted(sortOrder.comparator())
            .toList();
    }

    public void setFilter(Player player, BlockMenu menu, GridCache gridCache, ClickAction action) {
        setFilter(player, Screen.of(menu), gridCache, action);
    }

    public void setFilter(Player player, Screen screen, GridCache gridCache, ClickAction action) {
        if (action.isRightClicked()) {
            gridCache.setFilter(null);
            SlimefunBlockData data = StorageCacheUtils.getBlock(screen.getLocation());
            if (data == null) {
                return;
            }

            data.removeData(BS_FILTER_KEY);
            updateDisplay(screen);
            return;
        }

        player.closeInventory();
        player.sendMessage(Lang.getString("messages.normal-operation.grid.waiting_for_filter"));
        ChatUtils.awaitInput(player, s -> {
            if (s.isBlank()) return;

            s = s.toLowerCase(Locale.ROOT);
            gridCache.setFilter(s);
            getCacheMap().put(screen.getLocation(), gridCache);
            player.sendMessage(Lang.getString("messages.completed-operation.grid.filter_set"));

            StorageCacheUtils.setData(screen.getLocation(), BS_FILTER_KEY, s);
            if (screen.isAlive()) {
                screen.open(player);
                if (gridCache.getMaxPages() >= 1) {
                    gridCache.setPage(1);
                }
                updateDisplay(screen);
            }
        });
    }

    @SuppressWarnings("deprecation")
    protected synchronized void takeItem(
        Player player, @Nullable ItemStack itemStack, ClickAction action, Screen screen) {
        NodeDefinition definition = NetworkStorage.getNode(screen.getLocation());
        if (definition == null || definition.getNode() == null) {
            clearDisplay(screen);
            screen.close();
            Networks.getInstance()
                .getLogger()
                .warning(String.format(
                    Lang.getString("messages.unsupported-operation.grid.may_duping"),
                    player.getName(),
                    screen.getLocation()));
            return;
        }

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        final ItemStack clone = itemStack.clone();
        final ItemMeta cloneMeta = clone.getItemMeta();
        if (cloneMeta == null) {
            return;
        }

        final List<String> cloneLore = cloneMeta.getLore();
        if (cloneLore == null || cloneLore.size() < 2) {
            return;
        }

        cloneLore.removeLast();
        cloneLore.removeLast();
        cloneMeta.setLore(cloneLore);
        clone.setItemMeta(cloneMeta);

        NetworkRoot root = definition.getNode().getRoot();

        final ItemStack cursor = player.getItemOnCursor();
        if (cursor.getType() != Material.AIR
            && !StackUtils.itemsMatch(clone, StackUtils.getAsQuantity(player.getItemOnCursor(), 1))) {
            root.addItemStack(screen.getLocation(), player.getItemOnCursor());
            return;
        }

        int amount = 1;
        if (action.isRightClicked()) {
            amount = clone.getMaxStackSize();
        }

        final GridItemRequest request = new GridItemRequest(clone, amount, player);

        if (action.isShiftClicked()) {
            addToInventory(player, definition, request, action, screen);
        } else {
            addToCursor(player, definition, request, action, screen);
        }

        updateDisplay(screen);
    }

    @SuppressWarnings({"deprecation", "unused"})
    private void addToInventory(
        Player player, NodeDefinition definition, GridItemRequest request, ClickAction action, Screen screen) {
        ItemStack received = definition.getNode().getRoot().getItemStack(screen.getLocation(), request);

        if (received == null) {
            return;
        }

        InventoryUtil.addItem(player, received);
        if (received.getAmount() > 0) {
            definition.getNode().getRoot().addItemStack(screen.getLocation(), received);
        }
    }

    @SuppressWarnings("deprecation")
    private void addToCursor(
        Player player,
        NodeDefinition definition,
        GridItemRequest request,
        ClickAction action,
        Screen screen) {
        final ItemStack cursor = player.getItemOnCursor();

        // Quickly check if the cursor has an item and if we can add more to it
        if (cursor.getType() != Material.AIR && !canAddMore(action, cursor, request)) {
            return;
        }

        ItemStack requestingStack = definition.getNode().getRoot().getItemStack(screen.getLocation(), request);
        setCursor(player, cursor, requestingStack);
    }

    private void setCursor(Player player, ItemStack cursor, @Nullable ItemStack requestingStack) {
        if (requestingStack != null) {
            if (cursor.getType() != Material.AIR) {
                requestingStack.setAmount(cursor.getAmount() + 1);
            }
            player.setItemOnCursor(requestingStack);
        }
    }

    @SuppressWarnings("deprecation")
    private boolean canAddMore(
        ClickAction action, ItemStack cursor, GridItemRequest request) {
        return !action.isRightClicked()
            && request.getAmount() == 1
            && cursor.getAmount() < cursor.getMaxStackSize()
            && StackUtils.itemsMatch(request, cursor);
    }

    @Override
    public void postRegister() {
        getPreset();
    }
    
    @Nullable
    protected BlockMenuPreset getPreset() {
        return new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                drawBackground(getBackgroundSlots());
                drawBackground(getDisplaySlots());
                setSize(54);
            }

            @Override
            public boolean canOpen(Block block, Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                    || (getSlimefunItem().canUse(player, false)
                    && Slimefun.getProtectionManager()
                    .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {
                getCacheMap().put(menu.getLocation(), new GridCache(0, 0, GridCache.SortOrder.ALPHABETICAL));

                menu.replaceExistingItem(getPagePrevious(), getPagePreviousStack());
                menu.addMenuClickHandler(getPagePrevious(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    gridCache.setPage(gridCache.getPage() <= 0 ? 0 : gridCache.getPage() - 1);
                    getCacheMap().put(menu.getLocation(), gridCache);
                    updateDisplay(menu);
                    return false;
                });

                menu.replaceExistingItem(getPageNext(), getPageNextStack());
                menu.addMenuClickHandler(getPageNext(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    gridCache.setPage(
                        gridCache.getPage() >= gridCache.getMaxPages()
                            ? gridCache.getMaxPages()
                            : gridCache.getPage() + 1);
                    getCacheMap().put(menu.getLocation(), gridCache);
                    updateDisplay(Screen.of(menu));
                    return false;
                });

                menu.replaceExistingItem(getChangeSort(), getChangeSortStack());
                menu.addMenuClickHandler(getChangeSort(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    AbstractGrid.updateSortOrder(gridCache, action, getSortOrderLimit());
                    getCacheMap().put(menu.getLocation(), gridCache);
                    updateDisplay(Screen.of(menu));
                    return false;
                });

                menu.replaceExistingItem(getFilterSlot(), getFilterStack());
                menu.addMenuClickHandler(getFilterSlot(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    setFilter(p, menu, gridCache, action);
                    getCacheMap().put(menu.getLocation(), gridCache);
                    return false;
                });

                for (int displaySlot : getDisplaySlots()) {
                    menu.replaceExistingItem(displaySlot, ChestMenuUtils.getBackground());
                    menu.addMenuClickHandler(displaySlot, (p, slot, item, action) -> false);
                }

                postNewInstance(menu, b);
            }
        };
    }
    
    public void postNewInstance(BlockMenu menu, Block b) {
        menu.addPlayerInventoryClickHandler(defaultOutsideKeybind(menu));
    }
    
    public ChestMenu.MenuClickHandler defaultOutsideKeybind(BlockMenu menu) {
        return (p, s, i, a) -> {
            if (!a.isShiftClicked() || a.isRightClicked()) {
                return true;
            }

            // Shift+Left-click
            receiveItem(p, i, Screen.of(menu));
            return false;
        };
    }

    public Map<Location, GridCache> getCacheMap() {
        return CACHE_MAP;
    }

    protected abstract int[] getBackgroundSlots();

    protected abstract int[] getDisplaySlots();

    protected abstract int getInputSlot();

    protected abstract int getChangeSort();

    protected abstract int getPagePrevious();

    protected abstract int getPageNext();

    protected abstract int getFilterSlot();

    @SuppressWarnings("unused")
    protected ItemStack getBlankSlotStack() {
        return Icon.BLANK_SLOT_STACK;
    }

    protected ItemStack getPagePreviousStack() {
        return Icon.PAGE_PREVIOUS_STACK;
    }

    protected ItemStack getPageNextStack() {
        return Icon.PAGE_NEXT_STACK;
    }

    protected ItemStack getChangeSortStack() {
        return Icon.CHANGE_SORT_STACK;
    }

    protected ItemStack getFilterStack() {
        return Icon.FILTER_STACK;
    }

    public void receiveItem(
        Player player, ItemStack itemStack, Screen screen) {
        NodeDefinition definition = NetworkStorage.getNode(screen.getLocation());
        if (definition == null || definition.getNode() == null) {
            clearDisplay(screen);
            screen.close();
            Networks.getInstance()
                .getLogger()
                .warning(String.format(
                    Lang.getString("messages.unsupported-operation.grid.may_duping"),
                    player.getName(),
                    screen.getLocation()));
            return;
        }

        if (!StackUtils.isBlacklisted(itemStack)) {
            definition.getNode().getRoot().addItemStack(screen.getLocation(), itemStack);
        }
    }

    public static void updateSortOrder(GridCache gridCache, ClickAction action, @Range(from = 1, to = 4) int limit) {
        if (action.isShiftClicked() && !action.isRightClicked()) {
            gridCache.setSortOrder(GridCache.SortOrder.ALPHABETICAL);
        } else {
            if (action.isRightClicked()) {
                gridCache.setSortOrder(gridCache.getSortOrder().previous(limit));
            } else {
                gridCache.setSortOrder(gridCache.getSortOrder().next(limit));
            }
        }
    }

    public int getSortOrderLimit() {
        return 2;
    }
}
