package com.ytdd9527.networksexpansion.implementation.machines.viewer;

import com.balugaq.netex.api.data.ItemFlowRecord;
import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import com.ytdd9527.networksexpansion.utils.ParticleUtil;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkController;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemFlowViewer extends NetworkObject {
    public static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    private static final Map<Location, GridCache> CACHE_MAP = new HashMap<>();
    private static final int BACK_SLOT = 8;
    private static final int FORCE_CLEAN_SLOT = 17;
    // ! DO NOT REMOVE THIS
    private static final int[] BACKGROUND_SLOTS = new int[] {8, 17, 35};
    private static final int[] DISPLAY_SLOTS = {
        0, 1, 2, 3, 4, 5, 6, 7,
        9, 10, 11, 12, 13, 14, 15, 16,
        18, 19, 20, 21, 22, 23, 24, 25,
        27, 28, 29, 30, 31, 32, 33, 34,
        36, 37, 38, 39, 40, 41, 42, 43,
        45, 46, 47, 48, 49, 50, 51, 52,
    };
    private static final int FILTER = 26;
    private static final int PAGE_PREVIOUS = 44;
    private static final int PAGE_NEXT = 53;
    private static final String BS_SUB_MENU = "sub-menu";
    private static final String NAMESPACE_SF = "sf";
    private static final String NAMESPACE_MC = "mc";
    private final @NotNull IntRangeSetting tickRate;

    public ItemFlowViewer(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.FLOW_VIEWER);

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
                    updateDisplay(blockMenu);
                }
            }

            @Override
            public void uniqueTick() {
                tick = tick <= 1 ? tickRate.getValue() : tick - 1;
            }
        });

        addItemHandler(new BlockBreakHandler(false, false) {
            @Override
            @ParametersAreNonnullByDefault
            public void onPlayerBreak(BlockBreakEvent blockBreakEvent, ItemStack itemStack, List<ItemStack> list) {
                NodeDefinition definition =
                        NetworkStorage.getNode(blockBreakEvent.getBlock().getLocation());
                if (definition != null && definition.getNode() != null) {
                    NetworkController.disableRecord(
                            definition.getNode().getRoot().getController());
                }
            }
        });
    }

    public static @NotNull String serializeIcon(@NotNull ItemStack itemStack) {
        SlimefunItem sf = SlimefunItem.getByItem(itemStack);
        if (sf != null) {
            return NAMESPACE_SF + ":" + sf.getId();
        } else {
            return NAMESPACE_MC + ":" + itemStack.getType().name();
        }
    }

    @Nullable public static ItemStack deserializeIcon(@NotNull String icon) {
        if (icon.startsWith(NAMESPACE_SF)) {
            String id = icon.split(":")[1];
            SlimefunItem sf = SlimefunItem.getById(id);
            if (sf != null) {
                return sf.getItem();
            }
        } else if (icon.startsWith(NAMESPACE_MC)) {
            Material type = Material.valueOf(icon.split(":")[1]);
            return new ItemStack(type);
        }

        return null;
    }

    public static void highlightBlock(@NotNull Player player, @NotNull Location barrelLocation) {
        ParticleUtil.drawLineFrom(player.getEyeLocation().clone().add(0D, -0.5D, 0D), barrelLocation);
        ParticleUtil.highlightBlock(barrelLocation);
    }

    @NotNull public static List<DisplayEntry> getRecords(@NotNull NetworkRoot root, @NotNull GridCache cache) {
        if (!root.isRecordFlow() || root.getItemFlowRecord() == null) {
            return new ArrayList<>();
        }

        return root.getItemFlowRecord().getActions().entrySet().stream()
                .filter(entry -> {
                    if (cache.getFilter() == null) {
                        return true;
                    }

                    ItemStack itemStack = entry.getKey();
                    if (itemStack == null) {
                        return false;
                    }

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
                .map(entry -> new DisplayEntry(entry.getKey(), entry.getValue()))
                .toList();
    }

    @NotNull public static List<ItemFlowRecord.TransportAction> getSubMenu(
            @NotNull NetworkRoot root, @NotNull GridCache cache, @Nullable ItemStack itemStack) {
        if (!root.isRecordFlow() || root.getItemFlowRecord() == null) {
            return new ArrayList<>();
        }

        return root.getItemFlowRecord().getActions().getOrDefault(itemStack, new ArrayList<>()).stream()
                .filter(action -> {
                    if (cache.getFilter() == null) {
                        return true;
                    }

                    if (itemStack == null) {
                        return false;
                    }

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
                .toList();
    }

    public static @NotNull List<String> getLoreAddition(@NotNull DisplayEntry entry) {
        long change = entry.actions().stream()
                .map(ItemFlowRecord.TransportAction::amount)
                .mapToLong(i -> i)
                .sum();

        List<String> list = new ArrayList<>();
        list.add("");
        list.add((change > 0 ? TextUtil.GREEN : change < 0 ? TextUtil.RED : TextUtil.GRAY)
                + String.format(
                        Lang.getString("messages.normal-operation.viewer.change"), change > 0 ? "+" + change : change));
        list.add("");
        list.addAll(Lang.getStringList("messages.normal-operation.viewer.item-flow-viewer-click-behavior"));

        return list;
    }

    public static @NotNull List<String> getLoreAddition(ItemFlowRecord.@NotNull TransportAction entry) {
        Location loc = entry.accessor();
        long change = entry.amount();
        List<String> list = new ArrayList<>();
        list.add("");
        list.add(String.format(
                Lang.getString("messages.normal-operation.viewer.location"),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ()));
        list.add(String.format(
                Lang.getString("messages.normal-operation.viewer.when"), humanizeTime(entry.milliSecond())));
        list.add((change > 0 ? TextUtil.GREEN : change < 0 ? TextUtil.RED : TextUtil.GRAY)
                + String.format(
                        Lang.getString("messages.normal-operation.viewer.change"), change > 0 ? "+" + change : change));
        list.add("");
        list.addAll(Lang.getStringList("messages.normal-operation.viewer.item-flow-viewer-sub-click-behavior"));

        return list;
    }

    public static @NotNull String humanizeTime(long milliSecond) {
        // milliSecond is System.currentTimeMillis()
        // we should transform it to the Date
        Date date = new Date(milliSecond);
        return DATE_FORMAT.format(date);
    }

    @NotNull public static ItemStack getIcon(ItemFlowRecord.@NotNull TransportAction action) {
        SlimefunItem sf = StorageCacheUtils.getSfItem(action.accessor());
        if (sf == null) {
            return Icon.UNKNOWN_ITEM.clone();
        } else {
            return sf.getItem().clone();
        }
    }

    public void setSubMenu(@NotNull BlockMenu menu, @NotNull GridCache cache, @Nullable ItemStack itemStack) {
        SlimefunBlockData data = StorageCacheUtils.getBlock(menu.getLocation());
        if (data == null) {
            return;
        }

        cache.setFilter(null);
        if (itemStack != null) {
            data.setData(BS_SUB_MENU, serializeIcon(itemStack));
        } else {
            data.removeData(BS_SUB_MENU);
        }
    }

    public @Nullable String getSubMenu(@NotNull BlockMenu menu) {
        SlimefunBlockData data = StorageCacheUtils.getBlock(menu.getLocation());
        if (data == null) {
            return null;
        }

        return data.getData(BS_SUB_MENU);
    }

    public void updateDisplay(@Nullable BlockMenu blockMenu) {
        if (blockMenu == null) {
            return;
        }

        Location location = blockMenu.getLocation();
        NodeDefinition definition = NetworkStorage.getNode(location);
        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        NetworkRoot root = definition.getNode().getRoot();

        if (!blockMenu.hasViewer()) {
            ItemFlowRecord flow = root.getItemFlowRecord();
            if (flow != null) {
                if (System.currentTimeMillis() - flow.getLastChangeTime() > ItemFlowRecord.DEADLINE) {
                    NetworkController.disableRecord(root.getController());
                    return;
                }
            }

            sendFeedback(blockMenu.getLocation(), FeedbackType.AFK);
            return;
        }

        final GridCache gridCache = getCacheMap().get(location);

        String subMenu = getSubMenu(blockMenu);
        if (subMenu != null) {
            subMenu(root, blockMenu, gridCache, subMenu);
        } else {
            mainMenu(root, blockMenu, gridCache);
        }
    }

    @SuppressWarnings("deprecation")
    public void subMenu(
            @NotNull NetworkRoot root,
            @NotNull BlockMenu blockMenu,
            @NotNull GridCache gridCache,
            @NotNull String subMenu) {
        List<ItemFlowRecord.TransportAction> entries = getSubMenu(root, gridCache, deserializeIcon(subMenu));

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

        int start = gridCache.getPage() * getDisplaySlots().length;
        if (start < 0) {
            start = 0;
        }
        final int end = Math.min(start + getDisplaySlots().length, entries.size());

        final List<ItemFlowRecord.TransportAction> validActions = entries.subList(start, end);

        getCacheMap().put(blockMenu.getLocation(), gridCache);

        for (int i = 0; i < getDisplaySlots().length; i++) {
            if (validActions.size() > i) {
                final ItemFlowRecord.TransportAction action = validActions.get(i);
                final ItemStack displayItemStack = getIcon(action);
                ItemStack displayStack = new CustomItemStack(
                        displayItemStack.clone(), TextUtil.GRAY + ItemStackHelper.getDisplayName(displayItemStack));

                final ItemMeta itemMeta = displayStack.getItemMeta();
                if (itemMeta == null) {
                    continue;
                }

                List<String> lore = getLoreAddition(action);

                itemMeta.setLore(lore);
                displayStack.setItemMeta(itemMeta);
                blockMenu.replaceExistingItem(getDisplaySlots()[i], displayStack);
                blockMenu.addMenuClickHandler(getDisplaySlots()[i], (player, slot, item, a) -> {
                    highlightBlock(player, action.accessor());
                    return false;
                });
            } else {
                blockMenu.replaceExistingItem(getDisplaySlots()[i], getBlankSlotStack());
                blockMenu.addMenuClickHandler(getDisplaySlots()[i], (p, slot, item, action) -> false);
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

    @SuppressWarnings("deprecation")
    public void mainMenu(@NotNull NetworkRoot root, @NotNull BlockMenu blockMenu, @NotNull GridCache gridCache) {
        List<DisplayEntry> entries = getRecords(root, gridCache);

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

        int start = gridCache.getPage() * getDisplaySlots().length;
        if (start < 0) {
            start = 0;
        }
        final int end = Math.min(start + getDisplaySlots().length, entries.size());

        final List<DisplayEntry> validEntries = entries.subList(start, end);

        getCacheMap().put(blockMenu.getLocation(), gridCache);

        for (int i = 0; i < getDisplaySlots().length; i++) {
            if (validEntries.size() > i) {
                final DisplayEntry entry = validEntries.get(i);
                final ItemStack displayItemStack = entry.itemStack();
                if (displayItemStack == null) {
                    continue;
                }

                ItemStack displayStack = null;
                if (displayItemStack.getType() == Material.AIR) {
                    displayStack = new ItemStack(Material.BARRIER);
                }

                if (displayStack == null) {
                    displayStack = displayItemStack.clone();
                }

                displayStack = new CustomItemStack(
                        displayStack, TextUtil.GRAY + ItemStackHelper.getDisplayName(displayItemStack));

                final ItemMeta itemMeta = displayStack.getItemMeta();
                if (itemMeta == null) {
                    continue;
                }

                List<String> lore = getLoreAddition(entry);

                itemMeta.setLore(lore);
                displayStack.setItemMeta(itemMeta);
                blockMenu.replaceExistingItem(getDisplaySlots()[i], displayStack);
                blockMenu.addMenuClickHandler(getDisplaySlots()[i], (player, slot, item, action) -> {
                    setSubMenu(blockMenu, gridCache, displayItemStack);
                    return false;
                });
            } else {
                blockMenu.replaceExistingItem(getDisplaySlots()[i], getBlankSlotStack());
                blockMenu.addMenuClickHandler(getDisplaySlots()[i], (p, slot, item, action) -> false);
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

    @Override
    public void postRegister() {
        getPreset();
    }

    @NotNull protected BlockMenuPreset getPreset() {
        return new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                drawBackground(getBackgroundSlots());
                drawBackground(getDisplaySlots());
                setSize(54);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                        || (ExpansionItems.ITEM_FLOW_VIEWER.canUse(player, false)
                                && Slimefun.getProtectionManager()
                                        .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public void newInstance(@NotNull BlockMenu menu, @NotNull Block b) {
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
                    updateDisplay(menu);
                    return false;
                });

                menu.replaceExistingItem(getFilterSlot(), getFilterStack());
                menu.addMenuClickHandler(getFilterSlot(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    setFilter(p, menu, gridCache, action);
                    return false;
                });

                for (int displaySlot : getDisplaySlots()) {
                    menu.replaceExistingItem(displaySlot, ChestMenuUtils.getBackground());
                    menu.addMenuClickHandler(displaySlot, (p, slot, item, action) -> false);
                }

                menu.addItem(BACK_SLOT, Icon.ITEM_FLOW_VIEWER_BACK_TO_MAIN);
                menu.addMenuClickHandler(BACK_SLOT, (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    setSubMenu(menu, gridCache, null);
                    return false;
                });

                menu.addItem(FORCE_CLEAN_SLOT, Icon.ITEM_FLOW_VIEWER_FORCE_CLEAN);
                menu.addMenuClickHandler(FORCE_CLEAN_SLOT, (p, slot, item, action) -> {
                    NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());
                    if (definition != null && definition.getNode() != null) {
                        forceCleanHistory(definition.getNode().getRoot());
                    }

                    return false;
                });

                menu.addMenuCloseHandler(p -> {
                    NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());
                    if (definition != null && definition.getNode() != null) {
                        NetworkController.disableRecord(
                                definition.getNode().getRoot().getController());
                    }
                });

                menu.addMenuOpeningHandler(p -> {
                    NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());
                    if (definition != null && definition.getNode() != null) {
                        NetworkController.enableRecord(
                                definition.getNode().getRoot().getController());
                    }
                });
            }
        };
    }

    @NotNull public Map<Location, GridCache> getCacheMap() {
        return CACHE_MAP;
    }

    public int[] getBackgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    public int[] getDisplaySlots() {
        return DISPLAY_SLOTS;
    }

    public int getPagePrevious() {
        return PAGE_PREVIOUS;
    }

    public int getPageNext() {
        return PAGE_NEXT;
    }

    protected int getFilterSlot() {
        return FILTER;
    }

    @SuppressWarnings("deprecation")
    protected void setFilter(
            @NotNull Player player,
            @NotNull BlockMenu blockMenu,
            @NotNull GridCache gridCache,
            @NotNull ClickAction action) {
        if (action.isRightClicked()) {
            gridCache.setFilter(null);
        } else {
            player.closeInventory();
            player.sendMessage(Lang.getString("messages.normal-operation.grid.waiting_for_filter"));
            ChatUtils.awaitInput(player, s -> {
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
                    BlockMenu actualMenu = data.getBlockMenu();
                    if (actualMenu != null) {
                        updateDisplay(actualMenu);
                        actualMenu.open(player);
                    }
                }
            });
        }
    }

    protected @NotNull ItemStack getBlankSlotStack() {
        return Icon.BLANK_SLOT_STACK;
    }

    protected @NotNull ItemStack getPagePreviousStack() {
        return Icon.PAGE_PREVIOUS_STACK;
    }

    protected @NotNull ItemStack getPageNextStack() {
        return Icon.PAGE_NEXT_STACK;
    }

    protected @NotNull ItemStack getFilterStack() {
        return Icon.FILTER_STACK;
    }

    protected void clearDisplay(@NotNull BlockMenu blockMenu) {
        for (int displaySlot : getDisplaySlots()) {
            blockMenu.replaceExistingItem(displaySlot, getBlankSlotStack());
            blockMenu.addMenuClickHandler(displaySlot, (p, slot, item, action) -> false);
        }
    }

    public void forceCleanHistory(@NotNull NetworkRoot root) {
        ItemFlowRecord r = NetworkController.getRecords().get(root.getController());
        if (r == null) {
            return;
        }

        r.forceGC();
    }

    public record DisplayEntry(ItemStack itemStack, List<ItemFlowRecord.TransportAction> actions) {}
}
