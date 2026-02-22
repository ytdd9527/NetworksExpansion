package io.github.sefiraat.networks.slimefun.network.grid;

import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.netex.api.algorithm.Sorters;
import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.InventoryUtil;
import com.balugaq.netex.utils.Lang;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.managers.SupportedPluginManager;
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
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"DuplicatedCode", "deprecation"})
public abstract class AbstractGrid extends NetworkObject {
    protected static final String BS_FILTER_KEY = "filter";
    private static final Map<GridCache.SortOrder, Comparator<? super Map.Entry<ItemStack, Long>>> SORT_MAP =
        new HashMap<>();

    static {
        SORT_MAP.put(GridCache.SortOrder.ALPHABETICAL, Sorters.ITEMSTACK_ALPHABETICAL_SORT);
        SORT_MAP.put(GridCache.SortOrder.NUMBER, Sorters.ITEMSTACK_NUMERICAL_SORT.reversed());
        SORT_MAP.put(GridCache.SortOrder.NUMBER_REVERSE, Sorters.ITEMSTACK_NUMERICAL_SORT);
        SORT_MAP.put(GridCache.SortOrder.ADDON, Sorters.ITEMSTACK_ADDON_SORT);
    }

    private final @NotNull ItemSetting<Integer> tickRate;

    protected AbstractGrid(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe) {
        this(itemGroup, item, recipeType, recipe, NodeType.GRID);
    }

    protected AbstractGrid(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe,
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
            public void tick(@NotNull Block block, SlimefunItem item, @NotNull SlimefunBlockData data) {
                if (tick <= 1) {
                    final BlockMenu blockMenu = data.getBlockMenu();
                    if (blockMenu == null) {
                        return;
                    }
                    addToRegistry(block);
                    tryAddItem(blockMenu);
                    updateDisplay(blockMenu);
                }
            }

            @Override
            public void uniqueTick() {
                tick = tick <= 1 ? tickRate.getValue() : tick - 1;
            }

            @Override
            public @NotNull Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
                return Optional.empty();
            }
        });
    }

    @NotNull
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

    protected void tryAddItem(@NotNull BlockMenu blockMenu) {
        if (getInputSlot() == -1) return;

        final ItemStack itemStack = blockMenu.getItemInSlot(getInputSlot());

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());
        if (definition == null || definition.getNode() == null) {
            return;
        }

        definition.getNode().getRoot().addItemStack(itemStack);
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

    @SuppressWarnings("deprecation")
    protected void updateDisplay(@NotNull BlockMenu blockMenu) {
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
        final List<Map.Entry<ItemStack, Long>> entries = getEntries(root, gridCache);
        final int pages = (int) Math.ceil(entries.size() / (double) getDisplaySlots().length) - 1;

        gridCache.setMaxPages(pages);

        // Set everything to blank and return if there are no pages (no items)
        if (pages < 0) {
            clearDisplay(blockMenu);
            return;
        }

        blockMenu.replaceExistingItem(getChangeSort(), getSortOrderStack(gridCache.getSortOrder()));
        blockMenu.replaceExistingItem(getFilterSlot(), getFilterStack(gridCache.getFilter()));

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

        getCacheMap().put(blockMenu.getLocation(), gridCache);

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
                blockMenu.replaceExistingItem(getDisplaySlots()[i], displayStack);
                blockMenu.addMenuClickHandler(getDisplaySlots()[i], (player, slot, item, action) -> {
                    retrieveItem(player, item, action, blockMenu);
                    return false;
                });
            } else {
                blockMenu.replaceExistingItem(getDisplaySlots()[i], Icon.BLANK_SLOT_STACK);
                blockMenu.addMenuClickHandler(getDisplaySlots()[i], (p, slot, item, action) -> {
                    receiveItem(p, action, blockMenu);
                    return false;
                });
            }
        }
        blockMenu.replaceExistingItem(
            getPagePrevious(),
            Icon.getPageStack(getPagePreviousStack(), gridCache.getPage() + 1, gridCache.getMaxPages() + 1));
        blockMenu.replaceExistingItem(
            getPageNext(),
            Icon.getPageStack(getPageNextStack(), gridCache.getPage() + 1, gridCache.getMaxPages() + 1));

        sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
    }

    protected void clearDisplay(@NotNull BlockMenu blockMenu) {
        for (int displaySlot : getDisplaySlots()) {
            blockMenu.replaceExistingItem(displaySlot, getBlankSlotStack());
            blockMenu.addMenuClickHandler(displaySlot, (p, slot, item, action) -> false);
        }
    }

    @NotNull
    protected List<Map.Entry<ItemStack, Long>> getEntries(@NotNull NetworkRoot networkRoot, @NotNull GridCache cache) {
        return networkRoot.getAllNetworkItemsLongType().entrySet().stream()
            .filter(entry -> {
                if (cache.getFilter() == null) {
                    return true;
                }

                final ItemStack itemStack = entry.getKey();
                String name = TextUtil.stripColor(
                    ItemStackHelper.getDisplayName(itemStack).toLowerCase(Locale.ROOT));
                if (cache.getFilter().matches("^[a-zA-Z]+$")) {
                    final String pinyinName = PinyinHelper.toPinyin(name, PinyinStyleEnum.INPUT, "");
                    final String pinyinFirstLetter = PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "");
                    return name.contains(cache.getFilter())
                        || pinyinName.contains(cache.getFilter())
                        || pinyinFirstLetter.contains(cache.getFilter());
                } else {
                    return name.contains(cache.getFilter());
                }
            })
            .sorted(SORT_MAP.get(cache.getSortOrder()))
            .toList();
    }

    protected void setFilter(
        Player player,
        BlockMenu blockMenu,
        GridCache gridCache,
        ClickAction action) {
        if (action.isRightClicked()) {
            gridCache.setFilter(null);
            SlimefunBlockData data = StorageCacheUtils.getBlock(blockMenu.getLocation());
            if (data == null) {
                return;
            }

            data.removeData(BS_FILTER_KEY);
            updateDisplay(blockMenu);
        } else {
            player.closeInventory();
            player.sendMessage(Lang.getString("messages.normal-operation.grid.waiting_for_filter"));
            ChatUtils.awaitInput(
                player, s -> {
                    if (s.isBlank()) {
                        return;
                    }
                    s = s.toLowerCase(Locale.ROOT);
                    gridCache.setFilter(s);
                    getCacheMap().put(blockMenu.getLocation(), gridCache);
                    player.sendMessage(Lang.getString("messages.completed-operation.grid.filter_set"));

                    SlimefunBlockData data = StorageCacheUtils.getBlock(blockMenu.getLocation());
                    if (data == null) {
                        return;
                    }

                    if (blockMenu.getPreset().getID().equals(data.getSfId())) {
                        data.setData(BS_FILTER_KEY, s);
                        BlockMenu actualMenu = data.getBlockMenu();
                        if (actualMenu != null) {
                            actualMenu.open(player);
                            if (gridCache.getMaxPages() >= 1) {
                                gridCache.setPage(1);
                            }
                            updateDisplay(actualMenu);
                        }
                    }
                }
            );
        }
    }

    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    protected synchronized void retrieveItem(
        Player player, @Nullable ItemStack itemStack, ClickAction action, BlockMenu blockMenu) {
        NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());
        if (definition == null || definition.getNode() == null) {
            clearDisplay(blockMenu);
            blockMenu.close();
            Networks.getInstance()
                .getLogger()
                .warning(String.format(
                    Lang.getString("messages.unsupported-operation.grid.may_duping"),
                    player.getName(),
                    blockMenu.getLocation()));
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

        cloneLore.remove(cloneLore.size() - 1);
        cloneLore.remove(cloneLore.size() - 1);
        cloneMeta.setLore(cloneLore);
        clone.setItemMeta(cloneMeta);

        NetworkRoot root = definition.getNode().getRoot();
        boolean success = root.refreshRootItems();
        if (!success) {
            return;
        }

        final ItemStack cursor = player.getItemOnCursor();
        if (cursor.getType() != Material.AIR
            && !StackUtils.itemsMatch(clone, StackUtils.getAsQuantity(player.getItemOnCursor(), 1))) {
            root.addItemStack(player.getItemOnCursor());
            return;
        }

        int amount = 1;

        if (action.isRightClicked()) {
            amount = clone.getMaxStackSize();
        }

        final GridItemRequest request = new GridItemRequest(clone, amount, player);

        if (action.isShiftClicked()) {
            addToInventory(player, definition, request, action, blockMenu);
        } else {
            addToCursor(player, definition, request, action, blockMenu);
        }

        updateDisplay(blockMenu);
    }

    @SuppressWarnings({"deprecation", "unused"})
    @ParametersAreNonnullByDefault
    private void addToInventory(
        Player player, NodeDefinition definition, GridItemRequest request, ClickAction action, BlockMenu menu) {
        ItemStack requestingStack = definition.getNode().getRoot().getItemStack0(menu.getLocation(), request);

        if (requestingStack == null) {
            return;
        }

        HashMap<Integer, ItemStack> remnant = InventoryUtil.addItem(player, requestingStack);
        requestingStack = remnant.values().stream().findFirst().orElse(null);
        if (requestingStack != null) {
            definition.getNode().getRoot().addItemStack(requestingStack);
        }
    }

    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    private void addToCursor(
        Player player,
        NodeDefinition definition,
        GridItemRequest request,
        ClickAction action,
        @NotNull BlockMenu menu) {
        final ItemStack cursor = player.getItemOnCursor();

        // Quickly check if the cursor has an item and if we can add more to it
        if (cursor.getType() != Material.AIR && !canAddMore(action, cursor, request)) {
            return;
        }

        ItemStack requestingStack = definition.getNode().getRoot().getItemStack0(menu.getLocation(), request);
        setCursor(player, cursor, requestingStack);
    }

    private void setCursor(@NotNull Player player, @NotNull ItemStack cursor, @Nullable ItemStack requestingStack) {
        if (requestingStack != null) {
            if (cursor.getType() != Material.AIR) {
                requestingStack.setAmount(cursor.getAmount() + 1);
            }
            player.setItemOnCursor(requestingStack);
        }
    }

    @SuppressWarnings("deprecation")
    private boolean canAddMore(
        @NotNull ClickAction action, @NotNull ItemStack cursor, @NotNull GridItemRequest request) {
        return !action.isRightClicked()
            && request.getAmount() == 1
            && cursor.getAmount() < cursor.getMaxStackSize()
            && StackUtils.itemsMatch(request, cursor);
    }

    @Override
    public void postRegister() {
        getPreset();
    }

    @NotNull
    protected abstract BlockMenuPreset getPreset();

    @NotNull
    protected abstract Map<Location, GridCache> getCacheMap();

    protected abstract int[] getBackgroundSlots();

    protected abstract int[] getDisplaySlots();

    protected abstract int getInputSlot();

    protected abstract int getChangeSort();

    protected abstract int getPagePrevious();

    protected abstract int getPageNext();

    protected abstract int getFilterSlot();

    @SuppressWarnings("unused")
    protected @NotNull ItemStack getBlankSlotStack() {
        return Icon.BLANK_SLOT_STACK;
    }

    protected @NotNull ItemStack getPagePreviousStack() {
        return Icon.PAGE_PREVIOUS_STACK;
    }

    protected @NotNull ItemStack getPageNextStack() {
        return Icon.PAGE_NEXT_STACK;
    }

    protected @NotNull ItemStack getChangeSortStack() {
        return Icon.CHANGE_SORT_STACK;
    }

    protected @NotNull ItemStack getFilterStack() {
        return Icon.FILTER_STACK;
    }

    @SuppressWarnings("deprecation")
    public void receiveItem(@NotNull Player player, ClickAction action, @NotNull BlockMenu blockMenu) {
        NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());
        if (definition == null || definition.getNode() == null) {
            clearDisplay(blockMenu);
            blockMenu.close();
            Networks.getInstance()
                .getLogger()
                .warning(String.format(
                    Lang.getString("messages.unsupported-operation.grid.may_duping"),
                    player.getName(),
                    blockMenu.getLocation()));
            return;
        }

        ItemStack cursor = player.getItemOnCursor();
        receiveItem(definition.getNode().getRoot(), player, cursor, action, blockMenu);
    }

    @SuppressWarnings("deprecation")
    public void receiveItem(
        @NotNull Player player, ItemStack itemStack, ClickAction action, @NotNull BlockMenu blockMenu) {
        NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());
        if (definition == null || definition.getNode() == null) {
            clearDisplay(blockMenu);
            blockMenu.close();
            Networks.getInstance()
                .getLogger()
                .warning(String.format(
                    Lang.getString("messages.unsupported-operation.grid.may_duping"),
                    player.getName(),
                    blockMenu.getLocation()));
            return;
        }

        receiveItem(definition.getNode().getRoot(), player, itemStack, action, blockMenu);
    }

    @SuppressWarnings({"deprecation", "unused"})
    public void receiveItem(
        @NotNull NetworkRoot root,
        Player player,
        @Nullable ItemStack itemStack,
        ClickAction action,
        @NotNull BlockMenu blockMenu) {
        if (itemStack != null && itemStack.getType() != Material.AIR && !StackUtils.isBlacklisted(itemStack)) {
            root.addItemStack(itemStack);
        }
    }

    @SuppressWarnings("deprecation")
    public void receiveItem(
        @NotNull NetworkRoot root,
        Player player,
        @Nullable ItemStack itemStack,
        ClickAction action,
        @NotNull BlockMenu blockMenu,
        boolean doubleClick) {
        if (!doubleClick) {
            receiveItem(root, player, itemStack, action, blockMenu);
        }
    }

    protected static void updateSortOrder(GridCache gridCache, ClickAction action, @Range(from = 1, to = 4) int limit) {
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
}
