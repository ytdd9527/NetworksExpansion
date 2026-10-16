package com.ytdd9527.networksexpansion.core.items.machines;

import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.netex.api.algorithm.Sorters;
import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.api.interfaces.BaseGrid;
import com.balugaq.netex.api.keybind.Keybindable;
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
import io.github.sefiraat.networks.slimefun.network.grid.AbstractGrid;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache.DisplayMode;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

@NullMarked
@SuppressWarnings({"deprecation", "DuplicatedCode"})
public abstract class AbstractGridNewStyle extends AbstractGrid implements Keybindable, BaseGrid {
    protected AbstractGridNewStyle(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public AbstractGridNewStyle(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        ItemStack[] recipe,
        NodeType nodeType) {
        super(itemGroup, item, recipeType, recipe, nodeType);
    }

    @Override
    @Nullable
    public ItemStack precheck(@Nullable NodeDefinition definition, BlockMenu blockMenu, Player player, @Nullable ItemStack itemStack) {
        if (definition == null || definition.getNode() == null) {
            clearDisplay(Screen.of(blockMenu));
            blockMenu.close();
            Networks.getInstance()
                .getLogger()
                .warning(String.format(
                    Lang.getString("messages.unsupported-operation.grid.may_duping"),
                    player.getName(),
                    blockMenu.getLocation()
                ));
            return null;
        }

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }

        final ItemStack clone = itemStack.clone();

        final ItemMeta cloneMeta = clone.getItemMeta();
        if (cloneMeta == null) {
            return null;
        }
        final List<String> cloneLore = cloneMeta.getLore();

        if (cloneLore == null || cloneLore.size() < 2) {
            return null;
        }

        cloneLore.removeLast();
        cloneLore.removeLast();
        cloneMeta.setLore(cloneLore);
        clone.setItemMeta(cloneMeta);

        return clone;
    }

    @Override
    public void addToInventory(
        Player player, NodeDefinition definition, GridItemRequest request, BlockMenu menu) {
        InventoryUtil.give(player, definition.getNode().getRoot().getItemStack(menu.getLocation(), request));
    }

    @Override
    public void addToCursor(
        Player player,
        NodeDefinition definition,
        GridItemRequest request,
        boolean rightClick,
        BlockMenu blockMenu) {
        final ItemStack cursor = player.getItemOnCursor();

        // Quickly check if the cursor has an item and if we can add more to it
        if (cursor.getType() != Material.AIR && !canAddMore(rightClick, cursor, request)) {
            return;
        }

        ItemStack requestingStack = definition.getNode().getRoot().getItemStack(blockMenu.getLocation(), request);
        setCursor(player, cursor, requestingStack);
    }

    protected static List<String> getLoreAddition(Long long1) {
        final MessageFormat format =
            new MessageFormat(Lang.getString("messages.normal-operation.grid.item_amount"), Locale.ROOT);
        return List.of(
            "",
            format.format(
                    new Object[] {Theme.CLICK_INFO.getColor(), Theme.PASSIVE.getColor(), long1},
                    new StringBuffer(),
                    null
                )
                .toString()
        );
    }

    private boolean canAddMore(
        boolean rightClick, ItemStack cursor, GridItemRequest request) {
        return !rightClick
            && request.getAmount() == 1
            && cursor.getAmount() < cursor.getMaxStackSize()
            && StackUtils.itemsMatch(request, cursor);
    }

    private void setCursor(Player player, ItemStack cursor, @Nullable ItemStack requestingStack) {
        if (requestingStack != null) {
            if (cursor.getType() != Material.AIR) {
                requestingStack.setAmount(cursor.getAmount() + 1);
            }
            player.setItemOnCursor(requestingStack);
        }
    }

    protected abstract int getKeybindButtonSlot();

    protected static List<String> getHistoryLoreAddition() {
        return List.of(" ", Lang.getString("messages.normal-operation.grid_new_style.click_to_withdraw"));
    }

    protected abstract int getToggleModeSlot();

    public ItemStack getModeStack(GridCache gridCache) {
        return getModeStack(gridCache.getDisplayMode());
    }

    public ItemStack getModeStack(DisplayMode displayMode) {
        if (displayMode == DisplayMode.DISPLAY) {
            return Icon.DISPLAY_MODE_STACK;
        } else {
            return Icon.HISTORY_MODE_STACK;
        }
    }

    @Override
    public void updateDisplay(Screen screen) {
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

        final GridCache gridCache = getCacheMap().get(screen.getLocation());

        SlimefunBlockData data = StorageCacheUtils.getBlock(screen.getLocation());
        if (data == null) return;

        String filter = data.getData(BS_FILTER_KEY);
        if (filter != null) {
            gridCache.setFilter(filter);
        }

        screen.setItem(getChangeSort(), getSortOrderStack(gridCache.getSortOrder()));
        screen.setItem(getFilterSlot(), getFilterStack(gridCache.getFilter()));

        // Deprecated feature
        // autoSetFilter(blockMenu, gridCache);

        if (gridCache.getDisplayMode() == DisplayMode.DISPLAY) {
            final List<Entry<ItemStack, Long>> entries = getEntries(root, gridCache);
            final int pages = (int) Math.ceil(entries.size() / (double) getDisplaySlots().length) - 1;

            gridCache.setMaxPages(pages);

            // Set everything to blank and return if there are no pages (no items)
            if (pages < 0) {
                clearDisplay(screen);
                return;
            }

            // Rolldown selected page if it no longer exists due to items being removed
            if (gridCache.getPage() > pages) {
                gridCache.setPage(pages);
            }

            int start = gridCache.getPage() * getDisplaySlots().length;
            if (start < 0) {
                start = 0;
            }
            final int end = Math.min(start + getDisplaySlots().length, entries.size());
            final List<Entry<ItemStack, Long>> validEntries = entries.subList(start, end);

            getCacheMap().put(screen.getLocation(), gridCache);

            for (int i = 0; i < getDisplaySlots().length; i++) {
                if (validEntries.size() > i) {
                    final Entry<ItemStack, Long> entry = validEntries.get(i);
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
                    screen.setItem(getDisplaySlots()[i], displayStack, displayKeybinds());
                } else {
                    screen.setItem(getDisplaySlots()[i], getBlankSlotStack(), (p, slot, item, action) -> {
                        receiveItem(p, p.getItemOnCursor(), action, screen);
                        return false;
                    });
                }
            }
        } else {
            final List<ItemStack> history = new ArrayList<>();
            for (ItemStack i : gridCache.getPullItemHistory()) {
                history.add(i.clone());
            }

            final int pages = (int) Math.ceil(history.size() / (double) getDisplaySlots().length) - 1;

            gridCache.setMaxPages(pages);

            // Set everything to blank and return if there are no pages (no items)
            if (pages < 0) {
                clearDisplay(screen);
                return;
            }

            // Rolldown selected page if it no longer exists due to items being removed
            if (gridCache.getPage() > pages) {
                gridCache.setPage(pages);
            }

            final int start = gridCache.getPage() * getDisplaySlots().length;
            final int end = Math.min(start + getDisplaySlots().length, history.size());
            final List<ItemStack> validHistory = history.subList(start, end);

            getCacheMap().put(screen.getLocation(), gridCache);

            for (int i = 0; i < getDisplaySlots().length; i++) {
                if (validHistory.size() > i) {
                    final ItemStack displayStack = validHistory.get(i);
                    final ItemMeta itemMeta = displayStack.getItemMeta();
                    if (itemMeta == null) {
                        continue;
                    }
                    List<String> lore = itemMeta.getLore();

                    if (lore == null) {
                        lore = getHistoryLoreAddition();
                    } else {
                        lore.addAll(getHistoryLoreAddition());
                    }

                    itemMeta.setLore(lore);
                    displayStack.setItemMeta(itemMeta);
                    screen.setItem(getDisplaySlots()[i], displayStack, displayKeybinds());
                } else {
                    screen.setItem(getDisplaySlots()[i], getBlankSlotStack(), (p, slot, item, action) -> {
                        receiveItem(p, p.getItemOnCursor(), action, screen);
                        return false;
                    });
                }
            }
        }

        screen.setItem(
            getPagePrevious(),
            Icon.getPageStack(getPagePreviousStack(), gridCache.getPage() + 1, gridCache.getMaxPages() + 1)
        );
        screen.setItem(
            getPageNext(),
            Icon.getPageStack(getPageNextStack(), gridCache.getPage() + 1, gridCache.getMaxPages() + 1)
        );

        sendFeedback(screen.getLocation(), FeedbackType.WORKING);
    }

    @Override
    protected List<Entry<ItemStack, Long>> getEntries(NetworkRoot networkRoot, GridCache cache) {
        return getEntries0(networkRoot, cache);
    }

    public static List<Entry<ItemStack, Long>> getEntries0(NetworkRoot networkRoot, GridCache cache) {
        if (cache.getEntriesCache() != null) {
            return cache.getEntriesCache();
        }
        HashSet<SlimefunItem> pass = new HashSet<>();
        String searchTerm = cache.getFilter();
        if (searchTerm != null && Networks.getSupportedPluginManager().isJustEnoughGuide()) {
            try {
                Bukkit.getOnlinePlayers().stream().findFirst().ifPresent(player -> {
                    List<SlimefunItem> sfs = new ArrayList<>();
                    for (ItemStack item : networkRoot.getAllNetworkItemsLongTypeView().keySet()) {
                        var sf = SlimefunItem.getByItem(item);
                        if (sf != null) {
                            sfs.add(sf);
                        }
                    }
                    List<FilterType> types = new ArrayList<>(Arrays.stream(FilterType.values()).toList());
                    types.remove(FilterType.BY_DISPLAY_ITEM_NAME);
                    types.remove(FilterType.BY_RECIPE_ITEM_NAME);
                    for (FilterType type : types) {
                        pass.addAll(SearchGroup.filterItems(player, type, searchTerm, true, sfs));
                    }
                });
            } catch (Exception ignored) {
            }
        }
        var result = networkRoot.getAllNetworkItemsLongTypeView().entrySet().stream()
            .filter(entry -> {
                if (searchTerm == null) return true;

                final ItemStack itemStack = entry.getKey();
                if (!pass.isEmpty()) {
                    final SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
                    if (slimefunItem != null) {
                        if (pass.contains(slimefunItem)) {
                            return true;
                        }
                    }
                }
                String name = TextUtil.stripColor(
                    ItemStackHelper.getDisplayName(itemStack).toLowerCase(Locale.ROOT));
                if (searchTerm.matches("^[a-zA-Z]+$")) {
                    final boolean pinyinEnabled = Networks.getConfigManager().isPinyinSearchEnabled();
                    final String pinyinName = pinyinEnabled ? PinyinHelper.toPinyin(name, PinyinStyleEnum.INPUT, "") : "";
                    final String pinyinFirstLetter = pinyinEnabled ? PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "") : "";
                    return name.contains(searchTerm)
                        || pinyinName.contains(searchTerm)
                        || pinyinFirstLetter.contains(searchTerm);
                } else {
                    return name.contains(searchTerm);
                }
            })
            .sorted(cache.getSortOrder().comparator())
            .toList();
        cache.setEntriesCache(result);
        return result;
    }

    @Override
    public void receiveItem(Player player, ItemStack itemStack, ClickAction action, Screen screen) {
        receiveItem(player, itemStack, screen);
    }

    @Override
    protected int getInputSlot() {
        return -1;
    }

    @Override
    public int getSortOrderLimit() {
        return 4;
    }
}
