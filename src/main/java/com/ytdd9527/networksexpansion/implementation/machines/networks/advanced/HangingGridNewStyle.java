package com.ytdd9527.networksexpansion.implementation.machines.networks.advanced;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.api.interfaces.HangingBlock;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.MapUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.GridItemRequest;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Placeable;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HangingGridNewStyle extends NetworkGridNewStyle implements HangingBlock, Placeable {
    public static final String layer = "/textures/layer/terminal.png";
    public static final Map<Location, BlockMenu> menus = new HashMap<>();

    public BlockMenu getOrCreateMenu(@NotNull Location fixedLocation) {
        BlockMenu menu = menus.get(fixedLocation);
        if (menu != null) {
            return menu;
        }

        menu = new BlockMenu(getPreset(), fixedLocation);
        menus.put(fixedLocation, menu);
        return menu;
    }

    public HangingGridNewStyle(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.HANGING_GRID);
        HangingBlock.registerHangingBlock(this);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onPlace(HangingPlaceEvent event, Location attachon, ItemFrame entityBlock, BlockFace attachSide) {
        HangingBlock.super.onPlace(event, attachon, entityBlock, attachSide);
        onFirstTick0(attachon, entityBlock);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onInteract(Location attachon, PlayerItemFrameChangeEvent event) {
        // Don't rotate my icon!
        event.setCancelled(true);

        if (event.getAction() == PlayerItemFrameChangeEvent.ItemFrameChangeAction.ROTATE) {
            Location fixed = getFixedLocation(attachon, event.getItemFrame().getAttachedFace());
            BlockMenu menu = getOrCreateMenu(fixed);
            menu.open(event.getPlayer());
        } else if (event.getAction() == PlayerItemFrameChangeEvent.ItemFrameChangeAction.REMOVE) {
            breakBlock(attachon, event.getItemFrame());
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onTick(Location attachon, BlockFace attachSide, ItemFrame entityBlock) {
        Location fixed = getFixedLocation(attachon, attachSide);
        BlockMenu menu = getOrCreateMenu(fixed);
        updateDisplay(menu, attachon, attachSide);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onFirstTick(
            Location attachon, SlimefunBlockData attachonData, BlockFace attachSide, ItemFrame entityBlock) {
        onFirstTick0(attachon, entityBlock);
    }

    @ParametersAreNonnullByDefault
    public void onFirstTick0(Location attachon, ItemFrame entityBlock) {
        // Set image
        entityBlock.setItem(MapUtil.getImageItem(layer).getFirstValue());
        // grid cache
        getCacheMap()
                .put(
                        getFixedLocation(attachon, entityBlock.getAttachedFace()),
                        new GridCache(0, 0, GridCache.SortOrder.ALPHABETICAL));
    }

    @SuppressWarnings("deprecation")
    protected void updateDisplay(
            @NotNull BlockMenu blockMenu, @NotNull Location attachon, @NotNull BlockFace attachSide) {
        // No viewer - lets not bother updating
        if (!blockMenu.hasViewer()) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.AFK);
            return;
        }

        final NodeDefinition definition = NetworkStorage.getNode(attachon);

        // No node located, weird
        if (definition == null || definition.getNode() == null) {
            clearDisplay(blockMenu);
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        // Update Screen
        final NetworkRoot root = definition.getNode().getRoot();

        final GridCache gridCache = getCacheMap().get(blockMenu.getLocation().clone());

        SlimefunBlockData data = StorageCacheUtils.getBlock(attachon);
        if (data == null) {
            return;
        }

        String filter = data.getData(fixedKey(BS_FILTER_KEY, attachSide));
        if (filter != null) {
            gridCache.setFilter(filter);
        }

        // Deprecated feature
        // autoSetFilter(blockMenu, gridCache);

        if (gridCache.getDisplayMode() == GridCache.DisplayMode.DISPLAY) {
            final List<Map.Entry<ItemStack, Long>> entries = getEntries(root, gridCache);
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
                        retrieveItem(player, item, action, blockMenu, attachon, attachSide);
                        return false;
                    });
                } else {
                    blockMenu.replaceExistingItem(getDisplaySlots()[i], getBlankSlotStack());
                    blockMenu.addMenuClickHandler(getDisplaySlots()[i], (p, slot, item, action) -> {
                        receiveItem(p, action, blockMenu);
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
                        retrieveItem(player, item, action, blockMenu, attachon, attachSide);
                        return false;
                    });
                } else {
                    blockMenu.replaceExistingItem(getDisplaySlots()[i], getBlankSlotStack());
                    blockMenu.addMenuClickHandler(getDisplaySlots()[i], (p, slot, item, action) -> {
                        receiveItem(p, action, blockMenu, attachon, attachSide);
                        return false;
                    });
                }
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
    protected void setFilter(
            @NotNull Player player,
            @NotNull BlockMenu blockMenu,
            @NotNull GridCache gridCache,
            @NotNull ClickAction action,
            @NotNull Location attachon,
            @NotNull BlockFace attachSide) {
        if (action.isRightClicked()) {
            gridCache.setFilter(null);
            SlimefunBlockData data = StorageCacheUtils.getBlock(attachon);
            if (data == null) {
                return;
            }

            data.removeData(fixedKey(BS_FILTER_KEY, attachSide));
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

                SlimefunBlockData data = StorageCacheUtils.getBlock(attachon);
                if (data == null) {
                    return;
                }

                data.setData(fixedKey(BS_FILTER_KEY, attachSide), s);
                updateDisplay(blockMenu, attachon, attachSide);
                blockMenu.open(player);
            });
        }
    }

    public static String fixedKey(String key, BlockFace attachSide) {
        return "netex-hanging-" + attachSide.name().toLowerCase() + "-" + key;
    }

    public BlockMenuPreset preset = null;

    @Override
    @NotNull protected BlockMenuPreset getPreset() {
        if (preset != null) {
            return preset;
        }

        preset = new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                // drawBackground(getBackgroundSlots());
                drawBackground(getDisplaySlots());
                setSize(54);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                        || (ExpansionItems.HANGING_GRID_NEW_STYLE.canUse(player, false)
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
                    Location attachon = getAttachon(menu.getLocation());
                    BlockFace attachSide = getAttachSideFromFixed(menu.getLocation());
                    updateDisplay(menu, attachon, attachSide);
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
                    Location attachon = getAttachon(menu.getLocation());
                    BlockFace attachSide = getAttachSideFromFixed(menu.getLocation());
                    updateDisplay(menu, attachon, attachSide);
                    return false;
                });

                menu.replaceExistingItem(getChangeSort(), getChangeSortStack());
                menu.addMenuClickHandler(getChangeSort(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    gridCache.setSortOrder(gridCache.getSortOrder().next());
                    getCacheMap().put(menu.getLocation(), gridCache);
                    Location attachon = getAttachon(menu.getLocation());
                    BlockFace attachSide = getAttachSideFromFixed(menu.getLocation());
                    updateDisplay(menu, attachon, attachSide);
                    return false;
                });

                menu.replaceExistingItem(getFilterSlot(), getFilterStack());
                menu.addMenuClickHandler(getFilterSlot(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    setFilter(
                            p, menu, gridCache, action, menu.getLocation(), getAttachSideFromFixed(menu.getLocation()));
                    return false;
                });

                menu.replaceExistingItem(getToggleModeSlot(), getModeStack(GridCache.DisplayMode.DISPLAY));
                menu.addMenuClickHandler(getToggleModeSlot(), (p, slot, item, action) -> {
                    if (!action.isRightClicked()) {
                        GridCache gridCache = getCacheMap().get(menu.getLocation());
                        gridCache.toggleDisplayMode();
                        menu.replaceExistingItem(getToggleModeSlot(), getModeStack(gridCache));
                        Location attachon = getAttachon(menu.getLocation());
                        BlockFace attachSide = getAttachSideFromFixed(menu.getLocation());
                        updateDisplay(menu, attachon, attachSide);
                    }
                    return false;
                });

                for (int displaySlot : getDisplaySlots()) {
                    menu.replaceExistingItem(displaySlot, ChestMenuUtils.getBackground());
                    menu.addMenuClickHandler(displaySlot, (p, slot, item, action) -> false);
                }

                for (int backgroundSlot : getBackgroundSlots()) {
                    menu.replaceExistingItem(backgroundSlot, ChestMenuUtils.getBackground());
                    menu.addMenuClickHandler(backgroundSlot, (p, slot, item, action) -> false);
                }

                menu.addPlayerInventoryClickHandler((p, s, i, a) -> {
                    if (!a.isShiftClicked() || a.isRightClicked()) {
                        return true;
                    }

                    Location fixedLocation = menu.getLocation();
                    Location attachon = getAttachon(fixedLocation);
                    BlockFace attachSide = getAttachSideFromFixed(fixedLocation);
                    // Shift+Left-click
                    receiveItem(p, i, a, menu, attachon, attachSide);
                    return false;
                });
            }
        };

        return preset;
    }

    @Override
    @ParametersAreNonnullByDefault
    public <T extends HangingBlock> void onBreak(Location attachon, ItemFrame entityBlock, T hangingBlock) {
        HangingBlock.super.onBreak(attachon, entityBlock, hangingBlock);
        SlimefunBlockData data = StorageCacheUtils.getBlock(attachon);
        if (data == null) {
            return;
        }

        BlockFace attachSide = entityBlock.getAttachedFace();
        data.removeData(fixedKey(BS_FILTER_KEY, attachSide));
        Location location = entityBlock.getLocation();
        location.getWorld().dropItemNaturally(location, ExpansionItems.HANGING_GRID_NEW_STYLE.getItem());
    }

    @NotNull @ParametersAreNonnullByDefault
    public BlockFace getAttachSideFromFixed(Location fixedLocation) {
        for (BlockFace side : NetworkDirectional.VALID_FACES) {
            if (side.getDirection().equals(fixedLocation.getDirection())) {
                return side;
            }
        }

        return BlockFace.SELF;
    }

    @NotNull @ParametersAreNonnullByDefault
    public Location getAttachon(Location fixedLocation) {
        Location loc = fixedLocation.clone();
        loc.setYaw(0.0F);
        loc.setPitch(0.0F);
        return loc;
    }

    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    protected synchronized void retrieveItem(
            Player player,
            @Nullable ItemStack itemStack,
            ClickAction action,
            BlockMenu blockMenu,
            Location attachon,
            BlockFace attachSide) {
        NodeDefinition definition = NetworkStorage.getNode(attachon);
        if (definition == null || definition.getNode() == null) {
            clearDisplay(blockMenu);
            blockMenu.close();
            Networks.getInstance()
                    .getLogger()
                    .warning(String.format(
                            Lang.getString("messages.unsupported-operation.grid.may_duping"),
                            player.getName(),
                            attachon));
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
            definition.getNode().getRoot().addItemStack0(blockMenu.getLocation(), player.getItemOnCursor());
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
        GridCache gridCache = getCacheMap().get(blockMenu.getLocation());
        if (gridCache.getDisplayMode() == GridCache.DisplayMode.DISPLAY) {
            gridCache.addPullItemHistory(clone);
        }
        updateDisplay(blockMenu, attachon, attachSide);
    }

    @SuppressWarnings("deprecation")
    public void receiveItem(
            @NotNull Player player,
            ClickAction action,
            @NotNull BlockMenu blockMenu,
            Location attachon,
            BlockFace attachSide) {
        NodeDefinition definition = NetworkStorage.getNode(attachon);
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
        receiveItem(definition.getNode().getRoot(), player, cursor, action, blockMenu, attachon, attachSide);
    }

    @SuppressWarnings("deprecation")
    public void receiveItem(
            @NotNull Player player,
            ItemStack itemStack,
            ClickAction action,
            @NotNull BlockMenu blockMenu,
            @NotNull Location attachon,
            @NotNull BlockFace attachSide) {
        NodeDefinition definition = NetworkStorage.getNode(attachon);
        if (definition == null || definition.getNode() == null) {
            clearDisplay(blockMenu);
            blockMenu.close();
            Networks.getInstance()
                    .getLogger()
                    .warning(String.format(
                            Lang.getString("messages.unsupported-operation.grid.may_duping"),
                            player.getName(),
                            attachon));
            return;
        }

        receiveItem(definition.getNode().getRoot(), player, itemStack, action, blockMenu, attachon, attachSide);
    }

    @SuppressWarnings({"deprecation", "unused"})
    public void receiveItem(
            @NotNull NetworkRoot root,
            Player player,
            @Nullable ItemStack itemStack,
            ClickAction action,
            @NotNull BlockMenu blockMenu,
            @NotNull Location attachon,
            BlockFace attachSide) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            root.addItemStack0(blockMenu.getLocation(), itemStack);
        }
    }
}
