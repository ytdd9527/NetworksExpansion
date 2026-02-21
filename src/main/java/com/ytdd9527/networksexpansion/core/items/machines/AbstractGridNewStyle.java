package com.ytdd9527.networksexpansion.core.items.machines;

import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.netex.api.algorithm.Sorters;
import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.api.interfaces.BaseGrid;
import com.balugaq.netex.api.keybind.Keybindable;
import com.balugaq.netex.utils.InventoryUtil;
import com.balugaq.netex.utils.Lang;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.SmartNetworkCraftingGridNewStyle;
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
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
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
    private static final Map<GridCache.SortOrder, Comparator<? super Entry<ItemStack, Long>>> SORT_MAP =
        new HashMap<>();

    static {
        SORT_MAP.put(GridCache.SortOrder.ALPHABETICAL, Sorters.ITEMSTACK_ALPHABETICAL_SORT);
        SORT_MAP.put(GridCache.SortOrder.NUMBER, Sorters.ITEMSTACK_NUMERICAL_SORT.reversed());
        SORT_MAP.put(GridCache.SortOrder.NUMBER_REVERSE, Sorters.ITEMSTACK_NUMERICAL_SORT);
        SORT_MAP.put(GridCache.SortOrder.ADDON, Sorters.ITEMSTACK_ADDON_SORT);
    }

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
    public ItemStack precheck(NodeDefinition definition, BlockMenu blockMenu, Player player, ItemStack itemStack) {
        if (definition == null || definition.getNode() == null) {
            clearDisplay(blockMenu);
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

        cloneLore.remove(cloneLore.size() - 1);
        cloneLore.remove(cloneLore.size() - 1);
        cloneMeta.setLore(cloneLore);
        clone.setItemMeta(cloneMeta);

        NetworkRoot root = definition.getNode().getRoot();
        boolean success = root.refreshRootItems();
        if (!success) {
            return null;
        }

        return clone;
    }

    @Override
    public void addToInventory(
        Player player, NodeDefinition definition, GridItemRequest request, BlockMenu menu) {
        ItemStack requestingStack = definition.getNode().getRoot().getItemStack0(menu.getLocation(), request);

        if (requestingStack == null) {
            return;
        }

        HashMap<Integer, ItemStack> remnant = InventoryUtil.addItem(player, requestingStack);
        remnant.values().stream().findFirst().ifPresent(r2 -> Bukkit.getScheduler().runTask(
            Networks.getInstance(), () -> {
                if (player.getWorld().getNearbyEntities(player.getLocation(), 5, 5, 5).size() > SmartNetworkCraftingGridNewStyle.THRESHOLD) {
                    player.getWorld().dropItem(player.getLocation(), r2);
                } else {
                    definition.getNode().getRoot().addItemStack(r2);
                }
            }
        ));
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

        ItemStack requestingStack = definition.getNode().getRoot().getItemStack0(blockMenu.getLocation(), request);
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
    public void updateDisplay(BlockMenu blockMenu) {
        // No viewer - lets not bother updating
        if (!blockMenu.hasViewer()) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.AFK);
            return;
        }

        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        // No node located, weird
        if (definition == null || definition.getNode() == null) {
            clearDisplay(blockMenu);
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        // Update Screen
        final NetworkRoot root = definition.getNode().getRoot();

        final GridCache gridCache = getCacheMap().get(blockMenu.getLocation().clone());

        SlimefunBlockData data = StorageCacheUtils.getBlock(blockMenu.getLocation());
        if (data == null) {
            return;
        }

        String filter = data.getData(BS_FILTER_KEY);
        if (filter != null) {
            gridCache.setFilter(filter);
        }

        blockMenu.replaceExistingItem(getFilterSlot(), getFilterStack(gridCache.getFilter()));

        // Deprecated feature
        // autoSetFilter(blockMenu, gridCache);

        if (gridCache.getDisplayMode() == DisplayMode.DISPLAY) {
            final List<Entry<ItemStack, Long>> entries = getEntries(root, gridCache);
            final int pages = (int) Math.ceil(entries.size() / (double) getDisplaySlots().length) - 1;

            gridCache.setMaxPages(pages);

            // Set everything to blank and return if there are no pages (no items)
            if (pages < 0) {
                clearDisplay(blockMenu);
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

            getCacheMap().put(blockMenu.getLocation(), gridCache);

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
                    blockMenu.replaceExistingItem(getDisplaySlots()[i], displayStack);
                    blockMenu.addMenuClickHandler(getDisplaySlots()[i], displayKeybinds());
                } else {
                    blockMenu.replaceExistingItem(getDisplaySlots()[i], getBlankSlotStack());
                    blockMenu.addMenuClickHandler(
                        getDisplaySlots()[i], (p, slot, item, action) -> {
                            receiveItem(p, action, blockMenu);
                            return false;
                        }
                    );
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
                clearDisplay(blockMenu);
                return;
            }

            // Rolldown selected page if it no longer exists due to items being removed
            if (gridCache.getPage() > pages) {
                gridCache.setPage(pages);
            }

            final int start = gridCache.getPage() * getDisplaySlots().length;
            final int end = Math.min(start + getDisplaySlots().length, history.size());
            final List<ItemStack> validHistory = history.subList(start, end);

            getCacheMap().put(blockMenu.getLocation(), gridCache);

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
                    blockMenu.replaceExistingItem(getDisplaySlots()[i], displayStack);
                    blockMenu.addMenuClickHandler(getDisplaySlots()[i], displayKeybinds());
                } else {
                    blockMenu.replaceExistingItem(getDisplaySlots()[i], getBlankSlotStack());
                    blockMenu.addMenuClickHandler(
                        getDisplaySlots()[i], (p, slot, item, action) -> {
                            receiveItem(p, action, blockMenu);
                            return false;
                        }
                    );
                }
            }
        }

        blockMenu.replaceExistingItem(
            getPagePrevious(),
            Icon.getPageStack(getPagePreviousStack(), gridCache.getPage() + 1, gridCache.getMaxPages() + 1)
        );
        blockMenu.replaceExistingItem(
            getPageNext(),
            Icon.getPageStack(getPageNextStack(), gridCache.getPage() + 1, gridCache.getMaxPages() + 1)
        );

        sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
    }

    @Override
    protected List<Entry<ItemStack, Long>> getEntries(NetworkRoot networkRoot, GridCache cache) {
        HashSet<SlimefunItem> pass = new HashSet<>();
        String searchTerm = cache.getFilter();
        if (searchTerm != null && Networks.getSupportedPluginManager().isJustEnoughGuide()) {
            Bukkit.getOnlinePlayers().stream().findFirst().ifPresent(player -> {
                List<SlimefunItem> sfs = new ArrayList<>();
                for (ItemStack item : networkRoot.getAllNetworkItemsLongType().keySet()) {
                    if (item != null && item.getType() != Material.AIR) {
                        var sf = SlimefunItem.getByItem(item);
                        if (sf != null) {
                            sfs.add(sf);
                        }
                    }
                }
                List<FilterType> types = new ArrayList<>(Arrays.stream(FilterType.values()).toList());
                types.remove(FilterType.BY_DISPLAY_ITEM_NAME);
                types.remove(FilterType.BY_RECIPE_ITEM_NAME);
                for (FilterType type : types) {
                    pass.addAll(SearchGroup.filterItems(player, type, searchTerm, true, sfs));
                }
            });
        }
        return networkRoot.getAllNetworkItemsLongType().entrySet().stream()
            .filter(entry -> {
                if (searchTerm == null) {
                    return true;
                }

                final ItemStack itemStack = entry.getKey();
                if (pass != null) {
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
                    final String pinyinName = PinyinHelper.toPinyin(name, PinyinStyleEnum.INPUT, "");
                    final String pinyinFirstLetter = PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "");
                    return name.contains(searchTerm)
                        || pinyinName.contains(searchTerm)
                        || pinyinFirstLetter.contains(searchTerm);
                } else {
                    return name.contains(searchTerm);
                }
            })
            .sorted(SORT_MAP.get(cache.getSortOrder()))
            .toList();
    }

    @Override
    public void postRegister() {
        getPreset();
    }


    protected abstract BlockMenuPreset getPreset();

    public abstract Map<Location, GridCache> getCacheMap();

    protected abstract int[] getBackgroundSlots();

    protected abstract int[] getDisplaySlots();

    protected abstract int getChangeSort();

    protected abstract int getPagePrevious();

    protected abstract int getPageNext();

    protected abstract int getFilterSlot();

    @Override
    public void receiveItem(Player player, ClickAction action, BlockMenu blockMenu) {
        NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());
        if (definition == null || definition.getNode() == null) {
            clearDisplay(blockMenu);
            blockMenu.close();
            Networks.getInstance()
                .getLogger()
                .warning(String.format(
                    Lang.getString("messages.unsupported-operation.grid.may_duping"),
                    player.getName(),
                    blockMenu.getLocation()
                ));
            return;
        }

        ItemStack cursor = player.getItemOnCursor();
        receiveItem(definition.getNode().getRoot(), player, cursor, action, blockMenu);
    }

    @Override
    public void receiveItem(
        Player player, ItemStack itemStack, ClickAction action, BlockMenu blockMenu) {
        NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());
        if (definition == null || definition.getNode() == null) {
            clearDisplay(blockMenu);
            blockMenu.close();
            Networks.getInstance()
                .getLogger()
                .warning(String.format(
                    Lang.getString("messages.unsupported-operation.grid.may_duping"),
                    player.getName(),
                    blockMenu.getLocation()
                ));
            return;
        }

        receiveItem(definition.getNode().getRoot(), player, itemStack, action, blockMenu);
    }

    @Override
    public void receiveItem(
        NetworkRoot root,
        Player player,
        @Nullable ItemStack itemStack,
        ClickAction action,
        BlockMenu blockMenu) {
        if (itemStack != null && itemStack.getType() != Material.AIR && !StackUtils.isBlacklisted(itemStack)) {
            root.addItemStack(itemStack);
        }
    }

    @Override
    protected int getInputSlot() {
        return -1;
    }
}
