package com.ytdd9527.networksexpansion.implementation.machines.networks.advanced;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.enums.FilterMode;
import com.balugaq.netex.api.enums.MatchMode;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import dev.sefiraat.sefilib.misc.ParticleUtils;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.managers.SupportedPluginManager;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.NetworkSlimefunItems;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AdvancedVacuum extends NetworkObject {
    public static final Map<Location, FilterMode> CACHE_FILTER_MODE = new HashMap<>();
    public static final Map<Location, MatchMode> CACHE_MATCH_MODE = new HashMap<>();
    public static final String BS_FILTER_MODE = "filter-mode";
    public static final String BS_MATCH_MODE = "match-mode";
    public static final Map<Location, List<ItemStack>> CACHE_FILTER_ITEMS = new ConcurrentHashMap<>();
    private static final int[] BACKGROUND_SLOTS =
            new int[] {38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
    private static final int[] INPUT_SLOTS = new int[] {
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        9, 10, 11, 12, 13, 14, 15, 16, 17
    };
    private static final int[] SPLIT_SLOTS = new int[] {18, 19, 20, 21, 22, 23, 24, 25, 26};
    private static final int[] FILTER_SLOTS = new int[] {27, 28, 29, 30, 31, 32, 33, 34, 35};
    private static final int FILTER_MODE_SLOT = 36;
    private static final int MATCH_MODE_SLOT = 37;
    private final @NotNull ItemSetting<Integer> tickRate;
    private final @NotNull ItemSetting<Integer> vacuumRange;

    public AdvancedVacuum(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.ADVANCED_VACUUM);

        this.tickRate = new IntRangeSetting(this, "tick_rate", 1, 1, 10);
        this.vacuumRange = new IntRangeSetting(this, "vacuum_range", 1, 2, 5);
        addItemSetting(this.tickRate, this.vacuumRange);

        for (int inputSlot : INPUT_SLOTS) {
            this.getSlotsToDrop().add(inputSlot);
        }

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
                    Bukkit.getScheduler().runTask(Networks.getInstance(), bukkitTask -> findItem(blockMenu));
                }
            }

            @Override
            public void uniqueTick() {
                tick = tick <= 1 ? tickRate.getValue() : tick - 1;
            }
        });
        addItemHandler(new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@NotNull BlockPlaceEvent event) {
                NetworkStorage.removeNode(event.getBlock().getLocation());
                SlimefunBlockData blockData =
                        StorageCacheUtils.getBlock(event.getBlock().getLocation());
                if (blockData == null) {
                    return;
                }
                blockData.setData(
                        NetworkDirectional.OWNER_KEY,
                        event.getPlayer().getUniqueId().toString());
            }
        });
        addItemHandler(new BlockBreakHandler(false, false) {
            @Override
            @ParametersAreNonnullByDefault
            public void onPlayerBreak(BlockBreakEvent blockBreakEvent, ItemStack itemStack, List<ItemStack> list) {
                Location location = blockBreakEvent.getBlock().getLocation();
                CACHE_FILTER_ITEMS.remove(location);
                CACHE_FILTER_MODE.remove(location);
                CACHE_MATCH_MODE.remove(location);
            }
        });
    }

    public static void clearFilterItemsCache(@NotNull BlockMenu menu) {
        CACHE_FILTER_ITEMS.remove(menu.getLocation());
    }

    public static @NotNull List<ItemStack> getFilterItemStacksFromMenu(@NotNull BlockMenu menu) {
        List<ItemStack> filterItemStacks = new ArrayList<>();
        for (int slot : FILTER_SLOTS) {
            ItemStack itemStack = menu.getItemInSlot(slot);
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                filterItemStacks.add(itemStack);
            }
        }

        return filterItemStacks;
    }

    public static @NotNull List<ItemStack> getFilterItemStacks(@NotNull BlockMenu menu) {
        List<ItemStack> cache = CACHE_FILTER_ITEMS.get(menu.getLocation());
        if (cache != null) {
            return cache;
        }

        List<ItemStack> filterItemStacks = getFilterItemStacksFromMenu(menu);
        CACHE_FILTER_ITEMS.put(menu.getLocation(), filterItemStacks);
        return filterItemStacks;
    }

    public static boolean isAllowedItemStack(@NotNull BlockMenu menu, @NotNull ItemStack item) {
        List<ItemStack> itemStacks = getFilterItemStacks(menu);
        FilterMode filterMode = getFilterMode(menu);
        MatchMode matchMode = getMatchMode(menu);

        if (filterMode == FilterMode.WHITE_LIST) {
            for (ItemStack itemStack : itemStacks) {
                if (matchMode == MatchMode.MATERIAL_MATCH) {
                    if (itemStack.getType() == item.getType()) {
                        return true;
                    }
                } else if (matchMode == MatchMode.ALL_MATCH) {
                    if (StackUtils.itemsMatch(itemStack, item, true, false)) {
                        return true;
                    }
                } else {
                    return true;
                }
            }

            return false;
        } else if (filterMode == FilterMode.BLACK_LIST) {
            for (ItemStack itemStack : itemStacks) {
                if (matchMode == MatchMode.MATERIAL_MATCH) {
                    if (itemStack.getType() == item.getType()) {
                        return false;
                    }
                } else if (matchMode == MatchMode.ALL_MATCH) {
                    if (StackUtils.itemsMatch(itemStack, item, true, false)) {
                        return false;
                    }
                } else {
                    return true;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static void setFilterMode(@NotNull BlockMenu blockMenu, @NotNull FilterMode filterMode) {
        Location location = blockMenu.getLocation();
        CACHE_FILTER_MODE.put(location, filterMode);
        StorageCacheUtils.setData(location, BS_FILTER_MODE, filterMode.name());
        updateBlockMenu(blockMenu);
    }

    public static void setMatchMode(@NotNull BlockMenu blockMenu, @NotNull MatchMode matchMode) {
        Location location = blockMenu.getLocation();
        CACHE_MATCH_MODE.put(location, matchMode);
        StorageCacheUtils.setData(location, BS_MATCH_MODE, matchMode.name());
        updateBlockMenu(blockMenu);
    }

    public static @NotNull FilterMode getFilterMode(@NotNull BlockMenu blockMenu) {
        Location location = blockMenu.getLocation();
        FilterMode cache = CACHE_FILTER_MODE.get(location);
        if (cache != null) {
            return cache;
        }

        String mode = StorageCacheUtils.getData(location, BS_FILTER_MODE);
        if (mode == null) {
            setFilterMode(blockMenu, FilterMode.BLACK_LIST);
            return FilterMode.BLACK_LIST;
        }

        try {
            return FilterMode.valueOf(mode);
        } catch (IllegalArgumentException e) {
            setFilterMode(blockMenu, FilterMode.BLACK_LIST);
            return FilterMode.BLACK_LIST;
        }
    }

    public static @NotNull MatchMode getMatchMode(@NotNull BlockMenu blockMenu) {
        Location location = blockMenu.getLocation();
        MatchMode cache = CACHE_MATCH_MODE.get(location);
        if (cache != null) {
            return cache;
        }

        String mode = StorageCacheUtils.getData(location, BS_MATCH_MODE);
        if (mode == null) {
            setMatchMode(blockMenu, MatchMode.ALL_MATCH);
            return MatchMode.ALL_MATCH;
        }

        try {
            return MatchMode.valueOf(mode);
        } catch (IllegalArgumentException e) {
            setMatchMode(blockMenu, MatchMode.ALL_MATCH);
            return MatchMode.ALL_MATCH;
        }
    }

    public static @NotNull ItemStack getFilterModeIcon(@NotNull FilterMode filterMode) {
        return switch (filterMode) {
            case BLACK_LIST -> Icon.FILTER_MODE_BLACK_LIST;
            case WHITE_LIST -> Icon.FILTER_MODE_WHITE_LIST;
        };
    }

    public static @NotNull ItemStack getMatchModeIcon(@NotNull MatchMode matchMode) {
        return switch (matchMode) {
            case ALL_MATCH -> Icon.MATCH_MODE_ALL_MATCH;
            case MATERIAL_MATCH -> Icon.MATCH_MODE_MATERIAL_MATCH;
        };
    }

    public static void toggleFilterMode(@NotNull Player player, @NotNull BlockMenu menu) {
        FilterMode currentMode = FilterMode.BLACK_LIST;
        switch (getFilterMode(menu)) {
            case BLACK_LIST -> {
                setFilterMode(menu, FilterMode.WHITE_LIST);
                currentMode = FilterMode.WHITE_LIST;
            }
            case WHITE_LIST -> setFilterMode(menu, FilterMode.BLACK_LIST);
        }

        player.sendMessage(String.format(
                Lang.getString("messages.completed-operation.comprehensive.toggled_filter_mode"),
                currentMode == FilterMode.BLACK_LIST
                        ? Lang.getString("messages.completed-operation.comprehensive.filter_mode_black_list")
                        : Lang.getString("messages.completed-operation.comprehensive.filter_mode_white_list")));
    }

    public static void toggleMatchMode(@NotNull Player player, @NotNull BlockMenu menu) {
        MatchMode currentMode = MatchMode.ALL_MATCH;
        switch (getMatchMode(menu)) {
            case ALL_MATCH -> {
                setMatchMode(menu, MatchMode.MATERIAL_MATCH);
                currentMode = MatchMode.MATERIAL_MATCH;
            }
            case MATERIAL_MATCH -> setMatchMode(menu, MatchMode.ALL_MATCH);
        }

        player.sendMessage(String.format(
                Lang.getString("messages.completed-operation.comprehensive.toggled_match_mode"),
                currentMode == MatchMode.ALL_MATCH
                        ? Lang.getString("messages.completed-operation.comprehensive.match_mode_all_match")
                        : Lang.getString("messages.completed-operation.comprehensive.match_mode_material_match")));
    }

    public static void updateBlockMenu(@NotNull BlockMenu menu) {
        menu.addItem(FILTER_MODE_SLOT, getFilterModeIcon(getFilterMode(menu)));
        menu.addItem(MATCH_MODE_SLOT, getMatchModeIcon(getMatchMode(menu)));
    }

    private void findItem(@NotNull BlockMenu blockMenu) {
        for (int inputSlot : INPUT_SLOTS) {
            final ItemStack inSlot = blockMenu.getItemInSlot(inputSlot);
            if (inSlot == null || inSlot.getType() == Material.AIR) {
                final Location location = blockMenu.getLocation().clone().add(0.5, 0.5, 0.5);
                final int range = this.vacuumRange.getValue();
                Collection<Entity> items =
                        location.getWorld().getNearbyEntities(location, range, range, range, Item.class::isInstance);

                for (Entity optionalEntity : items.stream().toList()) {
                    if (!(optionalEntity instanceof Item item)) {
                        sendFeedback(blockMenu.getLocation(), FeedbackType.NO_ITEM_FOUND);
                        continue;
                    }

                    final String ownerUUID =
                            StorageCacheUtils.getData(blockMenu.getLocation(), NetworkDirectional.OWNER_KEY);
                    if (ownerUUID == null) {
                        sendFeedback(blockMenu.getLocation(), FeedbackType.NO_OWNER_FOUND);
                        return;
                    }
                    final UUID uuid = UUID.fromString(ownerUUID);
                    final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    if (!Slimefun.getProtectionManager()
                            .hasPermission(offlinePlayer, item.getLocation(), Interaction.INTERACT_ENTITY)) {
                        sendFeedback(blockMenu.getLocation(), FeedbackType.NO_PERMISSION);
                        return;
                    }

                    if (item.getPickupDelay() <= 0 && !SlimefunUtils.hasNoPickupFlag(item)) {
                        final ItemStack itemStack = item.getItemStack().clone();
                        if (!isAllowedItemStack(blockMenu, itemStack)) {
                            sendFeedback(blockMenu.getLocation(), FeedbackType.NOT_ALLOWED_ITEM);
                            continue;
                        }

                        final int amount = SupportedPluginManager.getStackAmount(item);
                        if (amount > itemStack.getMaxStackSize()) {
                            SupportedPluginManager.setStackAmount(item, amount - itemStack.getMaxStackSize());
                            itemStack.setAmount(itemStack.getMaxStackSize());
                        } else {
                            itemStack.setAmount(amount);
                            item.remove();
                        }
                        blockMenu.replaceExistingItem(inputSlot, itemStack);
                        ParticleUtils.displayParticleRandomly(item, 1, 5, new Particle.DustOptions(Color.BLUE, 1));
                        return;
                    }
                }
            }
        }
        sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
    }

    private void tryAddItem(@NotNull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        for (int inputSlot : INPUT_SLOTS) {
            final ItemStack itemStack = blockMenu.getItemInSlot(inputSlot);

            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            definition.getNode().getRoot().addItemStack0(blockMenu.getLocation(), itemStack);
        }
        sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                setSize(54);
                drawBackground(BACKGROUND_SLOTS);
                drawBackground(Icon.ADVANCED_VACUUM_SPLIT_BLOCK, SPLIT_SLOTS);
            }

            @Override
            public void newInstance(@NotNull BlockMenu menu, @NotNull Block b) {
                updateBlockMenu(menu);

                menu.addMenuClickHandler(FILTER_MODE_SLOT, (p, s, i, a) -> {
                    toggleFilterMode(p, menu);
                    return false;
                });
                menu.addMenuClickHandler(MATCH_MODE_SLOT, (p, s, i, a) -> {
                    toggleMatchMode(p, menu);
                    return false;
                });

                menu.addMenuOpeningHandler(p -> clearFilterItemsCache(menu));
                menu.addMenuCloseHandler(p -> clearFilterItemsCache(menu));
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                        || (NetworkSlimefunItems.NETWORK_VACUUM.canUse(player, false)
                                && Slimefun.getProtectionManager()
                                        .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow == ItemTransportFlow.INSERT) {
                    return INPUT_SLOTS;
                }
                return new int[0];
            }
        };
    }
}
