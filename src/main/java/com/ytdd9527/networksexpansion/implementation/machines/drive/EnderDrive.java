package com.ytdd9527.networksexpansion.implementation.machines.drive;

import com.balugaq.netex.utils.Scheduling;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.integrations.logitech.CellVirtualCache;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.StorageCell;
import com.ytdd9527.networksexpansion.api.cell.CellLedger;
import com.ytdd9527.networksexpansion.api.drive.CellUniqueness;
import com.ytdd9527.networksexpansion.api.drive.DriveCellSlots;
import com.ytdd9527.networksexpansion.api.drive.DriveOwnership;
import com.ytdd9527.networksexpansion.implementation.items.drive.EnderChannelController;
import com.ytdd9527.networksexpansion.api.drive.DriveWhitelistMenu;
import com.ytdd9527.networksexpansion.utils.ItemHashMap;
import com.ytdd9527.networksexpansion.utils.ItemKey;
import com.ytdd9527.networksexpansion.utils.Limits;
import com.ytdd9527.networksexpansion.utils.NumberFormat;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.utils.MenuShells;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class EnderDrive extends CellDrive {

    public static final String CHANNEL_KEY = "ender_channel";

    private static final long DISPLAY_REFRESH_INTERVAL_MS = 2000L;

    private static final String ENDER_DISPLAY_NAME_TEMPLATE = Lang.getString(CellnetText.ENDER_DISPLAY_NAME);
    private static final String CHANNEL_NONE_TEXT = Lang.getString(CellnetText.ENDER_CHANNEL_NONE);
    private static final String DISPLAY_SHARED_TEXT = Lang.getString(CellnetText.ENDER_DISPLAY_SHARED);
    private static final String DISPLAY_INDEPENDENT_TEXT = Lang.getString(CellnetText.ENDER_DISPLAY_INDEPENDENT);
    private static final String DISPLAY_MEMBERS_TEMPLATE = Lang.getString(CellnetText.ENDER_DISPLAY_MEMBERS);
    private static final String SNEAK_HINT_TEXT = Lang.getString(CellnetText.ENDER_DISPLAY_SNEAK_HINT);
    private static final long SYNC_PERIOD_TICKS = 20L;
    private static final int MAX_STATUS_ENTRIES = 6;

    private static final class EnderDriveState {
        volatile String channel;
        volatile Set<String> syncedMembers = Set.of();
        volatile long displayStamp;
        final ItemStack[] slotRefs = new ItemStack[CELL_SLOTS.length];
    }

    private static final Map<Location, EnderDriveState> STATES = new ConcurrentHashMap<>();
    private static final Map<String, Integer> CHANNEL_MEMBER_COUNTS = new ConcurrentHashMap<>();
    private static final AtomicBoolean SYNC_TIMER_STARTED = new AtomicBoolean();

    private static EnderDriveState state(@NotNull Location location) {
        return STATES.computeIfAbsent(location.clone(), k -> new EnderDriveState());
    }

    private static void dropState(@NotNull Location location) {
        STATES.remove(location);
    }

    public EnderDrive(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Nullable
    public static String getChannel(@NotNull Location location) {
        EnderDriveState state = STATES.get(location);
        if (state != null && state.channel != null) {
            return state.channel;
        }
        String stored = StorageCacheUtils.getData(location, CHANNEL_KEY);
        if (stored == null || stored.isEmpty()) {
            return null;
        }
        state(location).channel = stored;
        return stored;
    }

    public static long channelStock(@NotNull String channel, @NotNull ItemStack sample) {
        ItemKey key = new ItemKey(sample);
        long total = 0L;
        for (Location location : List.copyOf(STATES.keySet())) {
            if (!channel.equals(getChannel(location))) {
                continue;
            }
            BlockMenu menu = StorageCacheUtils.getMenu(location);
            if (menu == null) {
                continue;
            }
            for (int slot : CELL_SLOTS) {
                ItemStack cell = menu.getItemInSlot(slot);
                if (cell == null || cell.getType().isAir() || !StorageCell.isStorageCell(cell)) {
                    continue;
                }
                CellLedger cache = StorageCell.loadCellCache(cell, StorageCell.getPerTypeLimit(cell));
                total += Math.max(0L, cache.getAmount(key));
            }
        }
        return total;
    }

    @NotNull
    public static String generateChannel() {
        return "ch-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static void handleChannelInput(@NotNull Location location, @NotNull String rawInput) {
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(location);
        BlockMenu menu = StorageCacheUtils.getMenu(location);
        if (blockData == null) {
            if (menu != null) {
                broadcastViewers(menu, Lang.getString(CellnetText.ENDER_BIND_FAILED));
            }
            return;
        }
        EnderDriveState state = state(location);
        if (rawInput.isEmpty() || "-".equals(rawInput)) {
            String previous = getChannel(location);
            state.channel = null;
            blockData.setData(CHANNEL_KEY, "");
            EnderChannelController controller = EnderChannelController.getInstance();
            for (String uuid : state.syncedMembers) {
                controller.unbindCell(uuid);
            }
            state.syncedMembers = Set.of();
            if (previous != null) {
                CHANNEL_MEMBER_COUNTS.put(previous, controller.getCellUuidsOfChannel(previous).size());
            }
            if (menu != null) {
                CellDrive.getStorage().invalidateCellCache(location);
                notifyResult(menu, Lang.getString(CellnetText.ENDER_CHANNEL_UNBOUND));
                enqueueImmediateSync(location);
            }
            invalidateChannelPeers(previous);
            return;
        }
        String normalized = rawInput.trim();
        if (normalized.isEmpty() || normalized.codePoints().count() > Limits.MAX_CHANNEL_CODEPOINTS) {
            if (menu != null) {
                broadcastViewers(menu, Lang.getString(CellnetText.INPUT_CHANNEL_INVALID,
                    Limits.MAX_CHANNEL_CODEPOINTS));
            }
            return;
        }
        blockData.setData(CHANNEL_KEY, normalized);
        state.channel = normalized;
        state.syncedMembers = Set.of();
        if (menu != null) {
            CellDrive.getStorage().invalidateCellCache(location);
            notifyResult(menu, Lang.getString(CellnetText.ENDER_CHANNEL_BOUND, normalized));
            enqueueImmediateSync(location);
        }
        invalidateChannelPeers(normalized);
    }

    private static void notifyResult(@NotNull BlockMenu menu, @NotNull String message) {
        for (var human : menu.getInventory().getViewers()) {
            if (human instanceof Player player) {
                player.sendTitle("", message, 5, 60, 10);
                player.sendMessage(message);
            }
        }
    }

    private static void broadcastViewers(@NotNull BlockMenu menu, @NotNull String message) {
        for (var human : menu.getInventory().getViewers()) {
            if (human instanceof Player player) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void preRegister() {
        addItemHandler(placeHandler());
        addItemHandler(displayTicker());
        addItemHandler(breakHandler());
    }

    @NotNull
    private BlockPlaceHandler placeHandler() {
        return new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@NotNull BlockPlaceEvent event) {
                Location location = event.getBlock().getLocation();
                DriveOwnership.writeOwner(location, event.getPlayer().getUniqueId());
                SlimefunBlockData blockData = StorageCacheUtils.getBlock(location);
                if (blockData != null) {
                    String restored = blockData.getData(CHANNEL_KEY);
                    if (restored != null && !restored.isEmpty()) {
                        state(location).channel = restored;
                    }
                }
            }
        };
    }

    @NotNull
    private BlockTicker displayTicker() {
        return new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return false;
            }

            @Override
            public void tick(@NotNull Block b, SlimefunItem item, SlimefunBlockData data) {
                final Location location = b.getLocation();
                final long now = System.currentTimeMillis();
                EnderDriveState state = state(location);
                long last = state.displayStamp;
                if (last != 0L && now - last < DISPLAY_REFRESH_INTERVAL_MS) {
                    return;
                }
                state.displayStamp = now;
                Scheduling.runTask(() -> {
                    BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
                    if (blockMenu == null || !blockMenu.hasViewer()) {
                        return;
                    }
                    refreshMainDisplay(blockMenu);
                });
            }
        };
    }

    @NotNull
    private BlockBreakHandler breakHandler() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(@NotNull BlockBreakEvent event, @NotNull ItemStack item, @NotNull List<ItemStack> drops) {
                Player breaker = event.getPlayer();
                Location location = event.getBlock().getLocation();
                if (!DriveOwnership.bypasses(breaker)
                        && !DriveOwnership.passesGates(breaker, location,
                            Interaction.BREAK_BLOCK, canUse(breaker, false))) {
                    event.setCancelled(true);
                    breaker.sendMessage(Lang.getString(CellnetText.DRIVE_BREAK_NOT_ALLOWED));
                    return;
                }
                String channel = getChannel(location);
                dropState(location);
                CellDrive.clearLocalState(location);
                CellUniqueness.unregisterDrive(location);
                CellDrive.getStorage().dropCellCache(location);
                CellVirtualCache.removeDrive(location);
                BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
                if (blockMenu != null) {
                    EnderChannelController controller = EnderChannelController.getInstance();
                    for (int slot : CELL_SLOTS) {
                        ItemStack cellItem = blockMenu.getItemInSlot(slot);
                        String cellUuid = cellItem == null ? null : CellUniqueness.getUuidString(cellItem);
                        if (cellUuid != null) {
                            controller.unbindCell(cellUuid);
                        }
                    }
                    blockMenu.dropItems(blockMenu.getLocation(), CELL_SLOTS);
                }
                if (channel != null) {
                    CHANNEL_MEMBER_COUNTS.put(channel,
                        EnderChannelController.getInstance().getCellUuidsOfChannel(channel).size());
                }
                invalidateChannelPeers(channel);
            }
        };
    }

    @Override
    public void postRegister() {
        MenuShells.create(this, 45, CellDrive.MAIN_BACKGROUND_SLOTS,
            (block, player) -> DriveOwnership.bypasses(player)
                || DriveOwnership.passesGates(player, block.getLocation(),
                    Interaction.INTERACT_BLOCK, canUse(player, false)),
            MenuShells::emptyTransport,
            (menu, block) -> {
                setupHandlers(menu, block.getLocation());
                refreshMainDisplay(menu);
                startSyncTimerOnce();
            });
    }

    private void setupHandlers(@NotNull BlockMenu menu, @NotNull Location location) {
        menu.addMenuClickHandler(DISPLAY_SLOT, (player, slot, item, action) -> {
            if (getChannel(location) == null) {
                player.sendMessage(Lang.getString(CellnetText.ENDER_UNBIND_NOTHING));
                return false;
            }
            if (!DriveOwnership.bypasses(player)
                && !player.getUniqueId().equals(DriveOwnership.getOwnerUuid(location))) {
                player.sendMessage(Lang.getString(CellnetText.ENDER_UNBIND_NOT_OWNER));
                return false;
            }
            handleChannelInput(location, "-");
            refreshMainDisplay(menu);
            return false;
        });

        menu.addMenuClickHandler(ACCESS_SLOT, (player, clickedSlot, item, action) -> {
            openItemAccessMenu(menu, location, player);
            return false;
        });

        menu.addMenuClickHandler(WHITELIST_BUTTON_SLOT, (player, clickedSlot, item, action) -> {
            DriveWhitelistMenu.open(menu, location, player);
            return false;
        });

        for (int slot : CELL_SLOTS) {
            menu.addMenuClickHandler(slot, (player, clickedSlot, item, action) -> {
                enqueueImmediateSync(location);
                return true;
            });
        }

        menu.addMenuOpeningHandler(player -> {
            for (int slot : CELL_SLOTS) {
                DriveCellSlots.refreshCellLore(menu, slot);
            }
            enqueueImmediateSync(location);
        });

        menu.addMenuCloseHandler(p -> {
            CellDrive.getStorage().invalidateCellCache(location);
            enqueueImmediateSync(location);
        });
    }

    private static void enqueueImmediateSync(@NotNull Location location) {
        Scheduling.runTask(() -> {
            BlockMenu menu = StorageCacheUtils.getMenu(location);
            if (menu != null) {
                syncMenuMembers(menu);
            }
        });
    }

    private static void invalidateChannelPeers(@Nullable String channel) {
        if (channel == null) {
            return;
        }
        var storage = CellDrive.getStorage();
        for (Map.Entry<Location, EnderDriveState> entry : STATES.entrySet()) {
            if (channel.equals(entry.getValue().channel)) {
                storage.invalidateCellCache(entry.getKey());
            }
        }
    }

    private static void startSyncTimerOnce() {
        if (!SYNC_TIMER_STARTED.compareAndSet(false, true)) {
            return;
        }
        Scheduling.runTimer(() -> {
            if (!Networks.getInstance().isEnabled() || STATES.isEmpty()) {
                return;
            }
            for (Location location : List.copyOf(STATES.keySet())) {
                BlockMenu menu = StorageCacheUtils.getMenu(location);
                if (menu == null) {
                    dropState(location);
                    continue;
                }
                syncMenuMembers(menu);
            }
        }, SYNC_PERIOD_TICKS, SYNC_PERIOD_TICKS);
    }

    static void syncMenuMembers(@NotNull BlockMenu menu) {
        Location location = menu.getLocation();
        EnderDriveState state = state(location);
        if (!slotsChanged(state, menu)) {
            return;
        }
        Set<String> current = collectCellUuids(menu, getChannel(location));
        if (current.equals(state.syncedMembers)) {
            return;
        }
        applyMemberDiff(state, location, current);
    }

    private static boolean slotsChanged(@NotNull EnderDriveState state, @NotNull BlockMenu menu) {
        for (int i = 0; i < CELL_SLOTS.length; i++) {
            if (menu.getItemInSlot(CELL_SLOTS[i]) != state.slotRefs[i]) {
                for (int j = 0; j < CELL_SLOTS.length; j++) {
                    state.slotRefs[j] = menu.getItemInSlot(CELL_SLOTS[j]);
                }
                return true;
            }
        }
        return false;
    }

    @NotNull
    private static Set<String> collectCellUuids(@NotNull BlockMenu menu, @Nullable String channel) {
        Set<String> current = new HashSet<>();
        for (int slot : CELL_SLOTS) {
            ItemStack item = menu.getItemInSlot(slot);
            if (item == null || item.getType().isAir()) {
                continue;
            }
            String uuid = CellUniqueness.getUuidString(item);
            if (uuid == null) {
                continue;
            }
            if (channel != null) {
                StorageCell.loadCellCache(item, StorageCell.getPerTypeLimit(item));
            }
            current.add(uuid);
        }
        return current;
    }

    private static void applyMemberDiff(@NotNull EnderDriveState state, @NotNull Location location, @NotNull Set<String> current) {
        Set<String> syncedBefore = state.syncedMembers;
        String channel = getChannel(location);

        EnderChannelController controller = EnderChannelController.getInstance();
        boolean allOk = true;
        if (channel == null) {
            for (String uuid : syncedBefore) {
                allOk &= controller.unbindCell(uuid);
            }
        } else {
            for (String uuid : current) {
                if (!syncedBefore.contains(uuid)) {
                    allOk &= controller.bind(channel, uuid);
                }
            }
            for (String uuid : syncedBefore) {
                if (!current.contains(uuid)) {
                    allOk &= controller.unbindCell(uuid);
                }
            }
        }
        if (allOk) {
            state.syncedMembers = Set.copyOf(current);
            CellDrive.getStorage().invalidateCellCache(location);
            if (channel != null) {
                CHANNEL_MEMBER_COUNTS.put(channel,
                    EnderChannelController.getInstance().getCellUuidsOfChannel(channel).size());
                invalidateChannelPeers(channel);
            }
        }
    }

    private static void refreshMainDisplay(@NotNull BlockMenu menu) {
        String channel = getChannel(menu.getLocation());
        long totalStored = 0;
        int cellCount = 0;
        var handles = CellDrive.getStorage().getCells(menu);
        for (var cell : handles) {
            totalStored += cell.getStoredCount();
            cellCount++;
        }
        ItemHashMap<Long> aggregated = CellDrive.getStorage().getAllCellItemsKeyed(handles);
        menu.replaceExistingItem(DISPLAY_SLOT, buildStatusDisplay(cellCount, totalStored, aggregated, channel));
        menu.replaceExistingItem(ACCESS_SLOT, Icon.DRIVE_BROWSE);
        menu.replaceExistingItem(WHITELIST_BUTTON_SLOT, DriveWhitelistMenu.whitelistButton(menu.getLocation()));
    }

    @NotNull
    private static ItemStack buildStatusDisplay(
        int cellCount,
        long totalStored,
        @Nullable ItemHashMap<Long> aggregated,
        @Nullable String channel) {
        ItemStack display = new ItemStack(Material.PAPER);
        display.editMeta(meta -> {
            meta.setDisplayName(MessageFormat.format(ENDER_DISPLAY_NAME_TEMPLATE,
                channel != null ? channel : CHANNEL_NONE_TEXT));
            List<String> lore = new ArrayList<>();
            lore.add(MessageFormat.format(CellDrive.CELL_COUNT_TEMPLATE, cellCount, CELL_SLOT_COUNT));
            lore.add(MessageFormat.format(CellDrive.TOTAL_ITEMS_TEMPLATE, NumberFormat.formatNumber(totalStored)));
            if (aggregated != null) {
                lore.add(MessageFormat.format(CellDrive.ITEM_TYPES_TEMPLATE, aggregated.size()));
                int shown = 0;
                for (Map.Entry<ItemKey, Long> entry : aggregated.keyEntrySet()) {
                    if (shown >= MAX_STATUS_ENTRIES) {
                        lore.add(CellDrive.MORE_ITEMS_TEXT);
                        break;
                    }
                    String name = CellDrive.ITEM_DISPLAY_NAMES.computeIfAbsent(entry.getKey(),
                        k -> ItemStackHelper.getDisplayName(k.getItemStack()));
                    lore.add(MessageFormat.format(CellDrive.ITEM_ENTRY_TEMPLATE, name, NumberFormat.formatNumber(entry.getValue())));
                    shown++;
                }
            }
            lore.add("");
            lore.add(channel != null
                ? DISPLAY_SHARED_TEXT
                : DISPLAY_INDEPENDENT_TEXT);
            if (channel != null) {
                Integer members = CHANNEL_MEMBER_COUNTS.get(channel);
                if (members == null) {
                    members = EnderChannelController.getInstance().getCellUuidsOfChannel(channel).size();
                    CHANNEL_MEMBER_COUNTS.put(channel, members);
                }
                lore.add(MessageFormat.format(DISPLAY_MEMBERS_TEMPLATE, members));
            }
            lore.add(SNEAK_HINT_TEXT);
            meta.setLore(lore);
        });
        return display;
    }
}
