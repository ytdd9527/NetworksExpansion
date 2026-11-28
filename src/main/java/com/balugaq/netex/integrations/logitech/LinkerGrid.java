package com.balugaq.netex.integrations.logitech;

import com.balugaq.netex.api.data.ItemFlowRecord;
import com.balugaq.netex.api.data.StorageUnitData;
import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Debug;
import com.balugaq.netex.utils.InventoryUtil;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellHandle;
import com.ytdd9527.networksexpansion.api.drive.DriveType;
import com.ytdd9527.networksexpansion.core.machines.AbstractGridNewStyle;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import com.ytdd9527.networksexpansion.implementation.machines.drive.CellDrive;
import com.ytdd9527.networksexpansion.implementation.machines.unit.NetworksDrawer;
import com.ytdd9527.networksexpansion.utils.ItemKey;
import com.ytdd9527.networksexpansion.utils.ReflectionUtil;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.slimefun.network.NetworkController;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.slimefun.network.grid.AbstractGrid;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.sefiraat.networks.utils.StackUtils;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({"DuplicatedCode"})
public class LinkerGrid extends NetworkObject {
    public static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    private static final Map<Location, GridCache> CACHE_MAP = new HashMap<>();
    private static final int TYPE_SWITCH_SLOT = 17;
    // ! DO NOT REMOVE THIS
    private static final int[] BACKGROUND_SLOTS = new int[]{8, 17};
    private static final int[] DISPLAY_SLOTS = {
        0, 1, 2, 3, 4, 5, 6, 7,
        9, 10, 11, 12, 13, 14, 15, 16,
        18, 19, 20, 21, 22, 23, 24, 25,
        27, 28, 29, 30, 31, 32, 33, 34,
        36, 37, 38, 39, 40, 41, 42, 43,
        45, 46, 47, 48, 49, 50, 51, 52,
    };
    private static final int FILTER = 26;
    private static final int CHANGE_SORT = 35;
    private static final int PAGE_PREVIOUS = 44;
    private static final int PAGE_NEXT = 53;
    private static final String NAMESPACE_SF = "sf";
    private static final String NAMESPACE_MC = "mc";
    private static final String BS_LINKER_TYPE = "LinkerType";
    private static final String BS_LINKER_TARGET = "LinkerTarget";
    private static final String BS_GRID_FILTER = "GridFilter";
    private static final String BS_GRID_SORT = "GridSort";
    private static final int TARGET_SWITCH_SLOT = 8;
    private final @NotNull IntRangeSetting tickRate;
    private final ItemStack HyperLinkStack, QuantumLinkStack;
    private static final Class<?> hyperLinkClass, storageLinkClass;
    public static boolean initialized = false;
    static {
        hyperLinkClass = ReflectionUtil.getClass(
            "me.matl114.logitech.core.Cargo.Links.HyperLink",
            "me.matl114.logitech.SlimefunItem.Cargo.Links.HyperLink"
        );
        storageLinkClass = ReflectionUtil.getClass(
            "me.matl114.logitech.core.Cargo.Links.StorageLink",
            "me.matl114.logitech.SlimefunItem.Cargo.Links.StorageLink"
        );

        initialized = hyperLinkClass != null && storageLinkClass != null;
    }

    public LinkerGrid(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe) {
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
                    GridCache cache = getCacheMap().get(block.getLocation());
                    cache.setEntriesCache(null);
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

        HyperLinkStack = SlimefunItem.getById("LOGITECH_HYPER_LINK").getItem();
        QuantumLinkStack = SlimefunItem.getById("LOGITECH_QUANTUM_LINK").getItem();
    }

    public static @NotNull String serializeIcon(@NotNull ItemStack itemStack) {
        SlimefunItem sf = SlimefunItem.getByItem(itemStack);
        if (sf != null) {
            return NAMESPACE_SF + ":" + sf.getId();
        } else {
            return NAMESPACE_MC + ":" + itemStack.getType().name();
        }
    }

    @Nullable
    public static ItemStack deserializeIcon(@NotNull String icon) {
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

    public static @NotNull List<String> getLoreAddition(@NotNull ItemStack itemStack) {
        List<String> list = new ArrayList<>();
        list.add("");
        list.addAll(Lang.getStringList("messages.normal-operation.viewer.linker-grid-click-behavior"));

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

    @NotNull
    public static ItemStack getIcon(ItemFlowRecord.@NotNull TransportAction action) {
        SlimefunItem sf = StorageCacheUtils.getSfItem(action.accessor());
        if (sf == null) {
            return Icon.UNKNOWN_ITEM.clone();
        } else {
            return sf.getItem().clone();
        }
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
            sendFeedback(blockMenu.getLocation(), FeedbackType.AFK);
            return;
        }

        final GridCache gridCache = getCacheMap().get(location);

        mainMenu(root, blockMenu, gridCache);
    }

    @SuppressWarnings("deprecation")
    public void mainMenu(@NotNull NetworkRoot root, @NotNull BlockMenu blockMenu, @NotNull GridCache gridCache) {
        List<ItemStack> entries = AbstractGridNewStyle.getEntries0(root, gridCache).stream().map(Map.Entry::getKey).toList();

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

        final List<ItemStack> validEntries = entries.subList(start, end);

        getCacheMap().put(blockMenu.getLocation(), gridCache);

        for (int i = 0; i < getDisplaySlots().length; i++) {
            if (validEntries.size() > i) {
                final ItemStack stack = validEntries.get(i);
                if (stack == null) {
                    continue;
                }

                ItemStack displayStack = null;
                if (stack.getType() == Material.AIR) {
                    displayStack = new ItemStack(Material.BARRIER);
                }

                if (displayStack == null) {
                    displayStack = stack.clone();
                }

                displayStack = new CustomItemStack(
                    displayStack, TextUtil.GRAY + ItemStackHelper.getDisplayName(stack));

                final ItemMeta itemMeta = displayStack.getItemMeta();
                if (itemMeta == null) {
                    continue;
                }

                List<String> lore = getLoreAddition(stack);

                itemMeta.setLore(lore);
                displayStack.setItemMeta(itemMeta);
                blockMenu.replaceExistingItem(getDisplaySlots()[i], displayStack);
                blockMenu.addMenuClickHandler(getDisplaySlots()[i], (player, slot, item, action) -> {
                    if (!initialized) {
                        player.sendMessage(Lang.getString("messages.unsupported-operation.viewer.not-initialized"));
                        return false;
                    }
                    tryGetLink(blockMenu, player, stack);
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

    public void tryGetLink(@NotNull BlockMenu blockMenu, @NotNull Player player, @NotNull ItemStack itemStack) {
        NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());
        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        var root = definition.getNode().getRoot();
        var tp = getLinkerType(blockMenu.getLocation());
        Location location = getLocationOf(root, itemStack, tp, getLinkerTarget(blockMenu.getLocation()));
        if (location == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_LOCATION_FOUND);
            player.sendMessage(Lang.getString("messages.unsupported-operation.viewer.location-not-found"));
            return;
        }
        Interaction[] interactions = new Interaction[]{Interaction.INTERACT_BLOCK, Interaction.BREAK_BLOCK, Interaction.PLACE_BLOCK};
        for (var interaction : interactions) {
            if (!Slimefun.getProtectionManager().hasPermission(player, location, interaction)) {
                sendFeedback(blockMenu.getLocation(), FeedbackType.NO_PERMISSION);
                player.sendMessage(Lang.getString("messages.unsupported-operation.viewer.no-permission"));
                return;
            }
        }
        ItemStack link = getLinkStack(blockMenu, root, player);
        if (link == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NOT_ENOUGH_ITEMS);
            player.sendMessage(Lang.getString("messages.unsupported-operation.viewer.link-stack-not-found"));
            return;
        }

        var meta = link.getItemMeta();
        if ((tp == LinkerType.HyperLink && !canLink$HyperLink(meta))
            || (tp == LinkerType.QuantumLink && !canLink$QuantumLink(meta))) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NOT_ENOUGH_ITEMS);
            player.sendMessage(Lang.getString("messages.unsupported-operation.viewer.link-stack-not-found"));
            return;
        }

        Location bindLocation = location;
        if (tp == LinkerType.QuantumLink) {
            SlimefunItem targetItem = StorageCacheUtils.getSfItem(location);
            if (DriveType.of(targetItem) != null) {
                ItemKey key = new ItemKey(itemStack);
                CellVirtualCache.inject(location, key);
                LinkBindingStore.record(location, key.getItemStack());
                bindLocation = CellVirtualCache.virtualLocation(location, key);
            } else if (targetItem instanceof NetworksDrawer) {
                StorageUnitData drawerData = NetworkRoot.getCargoStorageUnitData(location);
                if (drawerData != null) {
                    DrawerVirtualCache.inject(location, itemStack);
                    LinkBindingStore.record(location, itemStack);
                    bindLocation = DrawerVirtualCache.virtualLocation(location, itemStack);
                }
            }
        }

        if (tp == LinkerType.HyperLink) setLink$HyperLink(meta, bindLocation);
        else setLink$QuantumLink(meta, bindLocation);
        link.setItemMeta(meta);
        InventoryUtil.give(player, link);

        if (tp == LinkerType.QuantumLink) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
            player.sendMessage(Lang.getString("messages.completed-operation.viewer.linked-stack"));
        }
    }

    public static Location getLocationOf(NetworkRoot root, ItemStack itemStack, LinkerType type, LinkerTarget target) {
        Location location = null;
        if (type == LinkerType.QuantumLink) {
            if (target == LinkerTarget.AUTO || target == LinkerTarget.QUANTUM_STORAGE) {
                for (var b : root.getBarrels()) {
                    if (StackUtils.itemsMatch(b, itemStack)) {
                        location = b.getLocation();
                        break;
                    }
                }
            }
            if (location == null && (target == LinkerTarget.AUTO || target == LinkerTarget.CELL)) {
                ItemKey key = new ItemKey(itemStack);
                for (BlockMenu driveMenu : root.getOutputAbleCellDriveMenus()) {
                    for (CellHandle cell : CellDrive.getStorage().getCells(driveMenu)) {
                        if (cell.getAmount(key) > 0) {
                            location = driveMenu.getLocation();
                            break;
                        }
                    }
                    if (location != null) {
                        break;
                    }
                }
            }
            if (location == null && (target == LinkerTarget.AUTO || target == LinkerTarget.DRAWER)) {
                for (var c : root.getDrawerData().values()) {
                    for (var ic : c.getStoredItemsDirectly()) {
                        if (StackUtils.itemsMatch(ic, itemStack)) {
                            location = c.getLastLocation();
                            break;
                        }
                    }
                    if (location != null) {
                        break;
                    }
                }
            }
            return location;
        }

        for (var b : root.getBarrels()) {
            if (StackUtils.itemsMatch(b, itemStack)) {
                return b.getLocation();
            }
        }
        for (var c : root.getDrawerData().values()) {
            for (var ic : c.getStoredItemsDirectly()) {
                if (StackUtils.itemsMatch(ic, itemStack)) {
                    return c.getLastLocation();
                }
            }
        }

        for (var g : root.getGreedyBlockMenus()) {
            int[] slots = g.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            final ItemStack s = g.getItemInSlot(slots[0]);
            if (!StackUtils.itemsMatch(s, itemStack)) continue;
            return g.getLocation();
        }

        for (var g : root.getAdvancedGreedyBlockMenus()) {
            int[] slots = g.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack s = g.getItemInSlot(slot);
                if (!StackUtils.itemsMatch(s, itemStack)) continue;
                return g.getLocation();
            }
        }

        for (BlockMenu c : root.getCellMenus()) {
            if (NetworkRoot.isFakeCell(c)) continue;
            for (int slot : NetworkRoot.CELL_AVAILABLE_SLOTS) {
                if (!StackUtils.itemsMatch(c.getItemInSlot(slot), itemStack)) continue;
                return c.getLocation();
            }
        }

        for (var c : root.getCrafterOutputs()) {
            int[] slots = c.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.WITHDRAW);
            for (int slot : slots) {
                final ItemStack s = c.getItemInSlot(slot);
                if (!StackUtils.itemsMatch(s, itemStack)) continue;
                return c.getLocation();
            }
        }

        return null;
    }

    public ItemStack getLink(Location location) {
        if (getLinkerType(location) == LinkerType.HyperLink) {
            return HyperLinkStack;
        } else {
            return QuantumLinkStack;
        }
    }

    @NotNull
    public static LinkerType getLinkerType(Location location) {
        var s = StorageCacheUtils.getData(location, BS_LINKER_TYPE);
        if (s == null) return LinkerType.QuantumLink;
        return LinkerType.valueOf(s);
    }

    @NotNull
    public static LinkerTarget getLinkerTarget(Location location) {
        return LinkerTarget.of(StorageCacheUtils.getData(location, BS_LINKER_TARGET));
    }

    private static @NotNull GridCache.SortOrder restoredSort(@NotNull Location location) {
        String s = StorageCacheUtils.getData(location, BS_GRID_SORT);
        if (s != null) {
            try {
                return GridCache.SortOrder.valueOf(s);
            } catch (IllegalArgumentException e) {
                Debug.debug("排序记录非法，回退默认: " + s);
            }
        }
        return GridCache.SortOrder.ALPHABETICAL;
    }

    private void setLinkerTarget(Location location, LinkerTarget target) {
        StorageCacheUtils.setData(location, BS_LINKER_TARGET, target.name());
    }

    @NotNull
    public ItemStack getLinkerTargetStack(@NotNull Location location) {
        LinkerTarget target = getLinkerTarget(location);
        ItemStack stack = Icon.LINKER_TARGET_SWITCH.clone();
        stack.editMeta(meta -> {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(Lang.getString(
                "messages.normal-operation.viewer.linker-target-current",
                Lang.getString("messages.normal-operation.viewer.linker-target-" + target.name().toLowerCase(Locale.ROOT))));
            meta.setLore(lore);
        });
        return stack;
    }

    @Nullable
    private ItemStack getLinkStack(@NotNull BlockMenu menu, @NotNull NetworkRoot root, @NotNull Player player) {
        ItemStack link = root.getItemStack(menu.getLocation(), new ItemRequest(getLink(menu.getLocation()), 1));
        if (link == null) {
            // get from player inventory
            for (ItemStack stack : player.getInventory()) {
                if (StackUtils.itemsMatch(stack, getLink(menu.getLocation()))) {
                    link = StackUtils.getAsQuantity(stack, 1);
                    stack.setAmount(stack.getAmount() - 1);
                    break;
                }
            }
        }
        return link;
    }

    @Override
    public void postRegister() {
        getPreset();
    }

    @NotNull
    protected BlockMenuPreset getPreset() {
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
                GridCache cache = new GridCache(0, 0, restoredSort(menu.getLocation()));
                String filter = StorageCacheUtils.getData(menu.getLocation(), BS_GRID_FILTER);
                if (filter != null && !filter.isEmpty()) {
                    cache.setFilter(filter);
                }
                getCacheMap().put(menu.getLocation(), cache);

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

                menu.replaceExistingItem(getChangeSort(), getChangeSortStack());
                menu.addMenuClickHandler(getChangeSort(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    AbstractGrid.updateSortOrder(gridCache, action, 4);
                    getCacheMap().put(menu.getLocation(), gridCache);
                    StorageCacheUtils.setData(menu.getLocation(), BS_GRID_SORT, gridCache.getSortOrder().name());
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

                menu.addItem(TYPE_SWITCH_SLOT, getLinkerTypeStack(menu.getLocation()));
                menu.addMenuClickHandler(TYPE_SWITCH_SLOT, (p, slot, item, action) -> {
                    setLinkerType(menu.getLocation(), switch (getLinkerType(menu.getLocation())) {
                        case HyperLink -> LinkerType.QuantumLink;
                        case QuantumLink -> LinkerType.HyperLink;
                    });
                    menu.replaceExistingItem(TYPE_SWITCH_SLOT, getLinkerTypeStack(menu.getLocation()));

                    return false;
                });

                menu.addItem(TARGET_SWITCH_SLOT, getLinkerTargetStack(menu.getLocation()));
                menu.addMenuClickHandler(TARGET_SWITCH_SLOT, (p, slot, item, action) -> {
                    setLinkerTarget(menu.getLocation(), getLinkerTarget(menu.getLocation()).next());
                    menu.replaceExistingItem(TARGET_SWITCH_SLOT, getLinkerTargetStack(menu.getLocation()));

                    return false;
                });
            }
        };
    }

    @NotNull
    public Map<Location, GridCache> getCacheMap() {
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
            getCacheMap().put(blockMenu.getLocation(), gridCache);
            StorageCacheUtils.removeData(blockMenu.getLocation(), BS_GRID_FILTER);
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
                StorageCacheUtils.setData(blockMenu.getLocation(), BS_GRID_FILTER, s);
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

    public ItemStack getLinkerTypeStack(@NotNull Location location) {
        return switch (getLinkerType(location)) {
            case HyperLink -> Icon.LINKER_TYPE_HYPER_LINK;
            case QuantumLink -> Icon.LINKER_TYPE_QUANTUM_LINK;
        };
    }

    public int getChangeSort() {
        return CHANGE_SORT;
    }

    protected @NotNull ItemStack getChangeSortStack() {
        return Icon.CHANGE_SORT_STACK;
    }

    private void setLinkerType(Location location, LinkerType linkerType) {
        StorageCacheUtils.setData(location, BS_LINKER_TYPE, linkerType.name());
    }

    public static boolean canLink$HyperLink(ItemMeta meta) {
        return (boolean) ReflectionUtil.invokeStaticMethod(hyperLinkClass, "canLink", meta);
    }

    public static boolean canLink$QuantumLink(ItemMeta meta) {
        return (boolean) ReflectionUtil.invokeStaticMethod(storageLinkClass, "canLink", meta);
    }

    public static void setLink$HyperLink(ItemMeta meta, Location location) {
        ReflectionUtil.invokeStaticMethod(hyperLinkClass, "setLink", meta, location);
    }

    public static void setLink$QuantumLink(ItemMeta meta, Location location) {
        ReflectionUtil.invokeStaticMethod(storageLinkClass, "setLink", meta, location);
    }
}