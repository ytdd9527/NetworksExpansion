package com.ytdd9527.networksexpansion.core.items.machines;

import com.balugaq.netex.api.helpers.Icon;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.GridItemRequest;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache.DisplayMode;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("deprecation")
public abstract class AbstractGridNewStyle extends NetworkObject {

    private static final Comparator<? super Entry<ItemStack, Long>> ALPHABETICAL_SORT = Comparator.comparing(
            itemStackIntegerEntry -> {
                ItemStack itemStack = itemStackIntegerEntry.getKey();
                SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
                if (slimefunItem != null) {
                    return ChatColor.stripColor(slimefunItem.getItemName());
                } else {
                    return ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack));
                }
            },
            Collator.getInstance(Locale.CHINA)::compare
    );

    private static final Comparator<Entry<ItemStack, Long>> NUMERICAL_SORT = Entry.comparingByValue();
    private static final Comparator<Entry<ItemStack, Long>> ADDON_SORT = Comparator.comparing(
            itemStackIntegerEntry -> {
                ItemStack itemStack = itemStackIntegerEntry.getKey();
                SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
                if (slimefunItem != null) {
                    return ChatColor.stripColor(slimefunItem.getAddon().getName());
                } else {
                    return "Minecraft";
                }
            },
            Collator.getInstance(Locale.CHINA)::compare
    );
    private static final Map<GridCache.SortOrder, Comparator<? super Entry<ItemStack, Long>>> SORT_MAP = new HashMap<>();

    static {
        SORT_MAP.put(GridCache.SortOrder.ALPHABETICAL, ALPHABETICAL_SORT);
        SORT_MAP.put(GridCache.SortOrder.NUMBER, NUMERICAL_SORT.reversed());
        SORT_MAP.put(GridCache.SortOrder.ADDON, ADDON_SORT);
    }

    private final ItemSetting<Integer> tickRate;

    protected AbstractGridNewStyle(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.GRID);

        this.getSlotsToDrop().add(getAutoFilterSlot());

        this.tickRate = new IntRangeSetting(this, "tick_rate", 1, 1, 10);
        addItemSetting(this.tickRate);

        addItemHandler(
                new BlockTicker() {

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
                            updateDisplay(blockMenu);
                        }
                    }

                    @Override
                    public void uniqueTick() {
                        tick = tick <= 1 ? tickRate.getValue() : tick - 1;
                    }
                }
        );
    }

    @Nonnull
    private static List<String> getLoreAddition(Long long1) {
        final MessageFormat format = new MessageFormat("{0}数量: {1}{2}", Locale.ROOT);
        return List.of(
                "",
                format.format(new Object[]{Theme.CLICK_INFO.getColor(), Theme.PASSIVE.getColor(), long1}, new StringBuffer(), null).toString()
        );
    }

    @Nonnull
    private static List<String> getHistoryLoreAddition() {
        return List.of(
                " ",
                Theme.PASSIVE.getColor() + "点击取出物品"
        );
    }

    protected void updateDisplay(@Nonnull BlockMenu blockMenu) {
        // No viewer - lets not bother updating
        if (!blockMenu.hasViewer()) {
            return;
        }

        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        // No node located, weird
        if (definition == null || definition.getNode() == null) {
            clearDisplay(blockMenu);
            return;
        }

        // Update Screen
        final NetworkRoot root = definition.getNode().getRoot();
        final GridCache gridCache = getCacheMap().get(blockMenu.getLocation().clone());

        autoSetFilter(blockMenu, gridCache);

        // 显示物品模式
        if (gridCache.getDisplayMode() == DisplayMode.DISPLAY) {
            final List<Entry<ItemStack, Long>> entries = getEntries(root, gridCache);
            final int pages = (int) Math.ceil(entries.size() / (double) getDisplaySlots().length) - 1;

            gridCache.setMaxPages(pages);

            // Set everything to blank and return if there are no pages (no items)
            if (pages < 0) {
                clearDisplay(blockMenu);
                return;
            }

            // Reset selected page if it no longer exists due to items being removed
            if (gridCache.getPage() > pages) {
                gridCache.setPage(0);
            }

            final int start = gridCache.getPage() * getDisplaySlots().length;
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
                    blockMenu.addMenuClickHandler(getDisplaySlots()[i], (player, slot, item, action) -> {
                        retrieveItem(player, item, action, blockMenu);
                        return false;
                    });
                } else {
                    blockMenu.replaceExistingItem(getDisplaySlots()[i], getBlankSlotStack());
                    blockMenu.addMenuClickHandler(getDisplaySlots()[i], (p, slot, item, action) -> false);
                }
            }
            // 历史记录模式
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

            // Reset selected page if it no longer exists due to items being removed
            if (gridCache.getPage() > pages) {
                gridCache.setPage(0);
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
                    blockMenu.addMenuClickHandler(getDisplaySlots()[i], (player, slot, item, action) -> {
                        retrieveItem(player, item, action, blockMenu);
                        return false;
                    });
                } else {
                    blockMenu.replaceExistingItem(getDisplaySlots()[i], getBlankSlotStack());
                    blockMenu.addMenuClickHandler(getDisplaySlots()[i], (p, slot, item, action) -> false);
                }
            }
        }
    }

    protected void clearDisplay(BlockMenu blockMenu) {
        for (int displaySlot : getDisplaySlots()) {
            blockMenu.replaceExistingItem(displaySlot, getBlankSlotStack());
            blockMenu.addMenuClickHandler(displaySlot, (p, slot, item, action) -> false);
        }
    }

    @Nonnull
    protected List<Entry<ItemStack, Long>> getEntries(@Nonnull NetworkRoot networkRoot, @Nonnull GridCache cache) {
        return networkRoot.getAllNetworkItemsLongType()
                .entrySet()
                .stream()
                .filter(entry -> {
                    if (cache.getFilter() == null) {
                        return true;
                    }

                    final ItemStack itemStack = entry.getKey();
                    String name = ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack).toLowerCase(Locale.ROOT));
                    final String pinyinName = PinyinHelper.toPinyin(name, PinyinStyleEnum.INPUT, "");
                    final String pinyinFirstLetter = PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "");
                    return name.contains(cache.getFilter()) || pinyinName.contains(cache.getFilter()) || pinyinFirstLetter.contains(cache.getFilter());
                })
                .sorted(SORT_MAP.get(cache.getSortOrder()))
                .toList();
    }

    protected void setFilter(@Nonnull Player player, @Nonnull BlockMenu blockMenu, @Nonnull GridCache gridCache, @Nonnull ClickAction action) {
        if (action.isRightClicked()) {
            gridCache.setFilter(null);
        } else {
            player.closeInventory();
            player.sendMessage(Networks.getLocalizationService().getString("messages.normal-operation.grid.waiting_for_filter"));
            ChatUtils.awaitInput(player, s -> {
                if (s.isBlank()) {
                    return;
                }
                s = s.toLowerCase(Locale.ROOT);
                gridCache.setFilter(s);
                player.sendMessage(Networks.getLocalizationService().getString("messages.completed-operation.grid.filter_set"));

                SlimefunBlockData data = StorageCacheUtils.getBlock(blockMenu.getLocation());
                if (data == null) {
                    return;
                }

                if (blockMenu.getPreset().getID().equals(data.getSfId())) {
                    BlockMenu actualMenu = data.getBlockMenu();
                    if (actualMenu != null) {
                        actualMenu.open(player);
                    }
                }
            });
        }
    }

    protected void autoSetFilter(@Nonnull BlockMenu blockMenu, @Nonnull GridCache gridCache) {
        final ItemStack itemStack = blockMenu.getItemInSlot(getAutoFilterSlot());
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
            String itemName;
            if (slimefunItem != null) {
                itemName = ChatColor.stripColor(slimefunItem.getItemName());
            } else {
                itemName = ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack));
            }

            gridCache.setFilter(itemName.toLowerCase(Locale.ROOT));
        }
    }

    @ParametersAreNonnullByDefault
    protected void retrieveItem(Player player, @Nullable ItemStack itemStack, ClickAction action, BlockMenu blockMenu) {
        NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());
        if (definition == null || definition.getNode() == null) {
            clearDisplay(blockMenu);
            blockMenu.close();
            Networks.getInstance().getLogger().warning(String.format(Networks.getLocalizationService().getString("messages.unsupported-operation.grid.may_duping"), player.getName(), blockMenu.getLocation()));
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

        final ItemStack cursor = player.getItemOnCursor();
        if (cursor.getType() != Material.AIR && !StackUtils.itemsMatch(clone, StackUtils.getAsQuantity(player.getItemOnCursor(), 1))) {
            definition.getNode().getRoot().addItemStack(player.getItemOnCursor());
            return;
        }

        int amount = 1;

        if (action.isRightClicked()) {
            amount = clone.getMaxStackSize();
        }

        final GridItemRequest request = new GridItemRequest(clone, amount, player);

        if (action.isShiftClicked()) {
            addToInventory(player, definition, request, action);
        } else {
            addToCursor(player, definition, request, action);
        }
        GridCache gridCache = getCacheMap().get(blockMenu.getLocation());
        if (gridCache.getDisplayMode() == DisplayMode.DISPLAY) {
            gridCache.addPullItemHistory(clone);
        }
        updateDisplay(blockMenu);
    }

    @ParametersAreNonnullByDefault
    private void addToInventory(Player player, NodeDefinition definition, GridItemRequest request, ClickAction action) {
        ItemStack requestingStack = definition.getNode().getRoot().getItemStack(request);

        if (requestingStack == null) {
            return;
        }

        HashMap<Integer, ItemStack> remnant = player.getInventory().addItem(requestingStack);
        requestingStack = remnant.values().stream().findFirst().orElse(null);
        if (requestingStack != null) {
            definition.getNode().getRoot().addItemStack(requestingStack);
        }
    }

    @ParametersAreNonnullByDefault
    private void addToCursor(Player player, NodeDefinition definition, GridItemRequest request, ClickAction action) {
        final ItemStack cursor = player.getItemOnCursor();

        // Quickly check if the cursor has an item and if we can add more to it
        if (cursor.getType() != Material.AIR && !canAddMore(action, cursor, request)) {
            return;
        }

        ItemStack requestingStack = definition.getNode().getRoot().getItemStack(request);
        setCursor(player, cursor, requestingStack);
    }

    private void setCursor(Player player, ItemStack cursor, ItemStack requestingStack) {
        if (requestingStack != null) {
            if (cursor.getType() != Material.AIR) {
                requestingStack.setAmount(cursor.getAmount() + 1);
            }
            player.setItemOnCursor(requestingStack);
        }
    }

    private boolean canAddMore(@Nonnull ClickAction action, @Nonnull ItemStack cursor, @Nonnull GridItemRequest request) {
        return !action.isRightClicked()
                && request.getAmount() == 1
                && cursor.getAmount() < cursor.getMaxStackSize()
                && StackUtils.itemsMatch(request, cursor);
    }

    @Override
    public void postRegister() {
        getPreset();
    }

    @Nonnull
    protected abstract BlockMenuPreset getPreset();

    @Nonnull
    protected abstract Map<Location, GridCache> getCacheMap();

    protected abstract int[] getBackgroundSlots();

    protected abstract int[] getDisplaySlots();


    protected abstract int getChangeSort();

    protected abstract int getPagePrevious();

    protected abstract int getPageNext();

    protected abstract int getFilterSlot();

    protected abstract int getAutoFilterSlot();

    protected abstract int getToggleModeSlot();

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
}
