package com.ytdd9527.networksexpansion.api.cell;

import com.balugaq.netex.utils.Scheduling;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.implementation.machines.drive.CellDrive;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class VoidCellMenu {

    public static final int INFO_SLOT = 0;
    public static final int CLOSE_SLOT = 8;
    public static final int[] FILTER_SLOTS = new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17};
    public static final int[] BACKGROUND_SLOTS = new int[]{
        1, 2, 3, 4, 5, 6, 7,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35,
        36, 37, 38, 39, 40, 41, 42, 43, 44
    };

    private static final ItemStack ICON_INFO = Icon.VOID_INFO;
    private static final ItemStack ICON_CLOSE = Icon.VOID_CLOSE;

    private static final Map<UUID, Session> SESSIONS = new ConcurrentHashMap<>();

    private record Anchor(@NotNull Location driveLocation, int driveSlot) {
    }

    private static final class Session {
        final UUID playerUuid;
        final ChestMenu menu;
        final UUID cellUuid;
        @Nullable final Anchor anchor;
        boolean sanitizePending;
        boolean closing;

        Session(
                @NotNull UUID playerUuid,
                @NotNull ChestMenu menu,
                @NotNull UUID cellUuid,
                @Nullable Anchor anchor) {
            this.playerUuid = playerUuid;
            this.menu = menu;
            this.cellUuid = cellUuid;
            this.anchor = anchor;
        }
    }

    private VoidCellMenu() {
    }

    public static void openFromHand(@NotNull Player player, @NotNull ItemStack cellItem) {
        VoidCellSupport.ensureInitialized(cellItem);
        open(player, cellItem.clone(), null);
    }

    public static void openFromDrive(@NotNull BlockMenu driveMenu, int slot, @NotNull Player player) {
        ItemStack cell = driveMenu.getItemInSlot(slot);
        if (!VoidCellSupport.isVoidCell(cell)) {
            player.sendMessage(Lang.getString(CellnetText.VOID_NOT_VOID_CELL));
            return;
        }
        VoidCellSupport.ensureInitialized(cell);
        open(player, cell.clone(), new Anchor(driveMenu.getLocation(), slot));
    }

    private static void open(@NotNull Player player, @NotNull ItemStack workingTemplate, @Nullable Anchor anchor) {
        UUID cellUuid = CellPersistence.getCellUUID(workingTemplate);
        if (cellUuid == null) {
            return;
        }
        UUID playerUuid = player.getUniqueId();
        for (Session s : SESSIONS.values()) {
            if (s.cellUuid.equals(cellUuid) && !s.playerUuid.equals(playerUuid)) {
                player.sendMessage(Lang.getString(CellnetText.VOID_EDITING_BY_OTHER));
                return;
            }
        }
        SESSIONS.remove(playerUuid);

        List<ItemStack> savedFilters = VoidCellSupport.filtersView(workingTemplate);

        ChestMenu menu = new ChestMenu(Lang.getString(CellnetText.VOID_TITLE));
        menu.setPlayerInventoryClickable(true);

        for (int slot : BACKGROUND_SLOTS) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), (p, s, i, a) -> false);
        }
        menu.addItem(INFO_SLOT, ICON_INFO.clone(), (p, s, i, a) -> false);
        menu.addItem(CLOSE_SLOT, ICON_CLOSE.clone(), (p, s, i, a) -> {
            p.closeInventory();
            return false;
        });

        Session session = new Session(playerUuid, menu, cellUuid, anchor);

        for (int i = 0; i < FILTER_SLOTS.length; i++) {
            ItemStack saved = i < savedFilters.size() ? savedFilters.get(i) : null;
            menu.addItem(FILTER_SLOTS[i], saved != null ? saved : air(), (p, s, ix, a) -> {
                scheduleSanitize(p, session);
                return true;
            });
        }

        menu.addPlayerInventoryClickHandler((p, s, i, a) -> {
            scheduleSanitize(p, session);
            return true;
        });

        menu.addMenuCloseHandler(p -> handleClose(playerUuid, session));
        SESSIONS.put(playerUuid, session);
        menu.open(player);
    }

    private static void scheduleSanitize(@NotNull Player player, @NotNull Session session) {
        if (session.sanitizePending || session.closing) {
            return;
        }
        session.sanitizePending = true;
        runNextTick(() -> {
            session.sanitizePending = false;
            Player online = Bukkit.getPlayer(session.playerUuid);
            if (online == null || session.closing || SESSIONS.get(session.playerUuid) != session) {
                return;
            }
            if (sanitize(online, session)) {
                persistSession(online, session, false);
            }
        });
    }

    private static void runNextTick(@NotNull Runnable task) {
        if (Networks.getInstance().isEnabled()) {
            Scheduling.runTask(task);
        } else {
            task.run();
        }
    }

    private static boolean sanitize(@NotNull Player player, @NotNull Session session) {
        ChestMenu menu = session.menu;
        boolean changed = false;
        boolean rejectedShown = false;
        for (int slot : FILTER_SLOTS) {
            ItemStack current = menu.getItemInSlot(slot);
            if (current == null || current.getType().isAir()) {
                continue;
            }
            if (VoidCellSupport.isVoidCell(current) || StackUtils.isBlacklisted(current)) {
                menu.replaceExistingItem(slot, air());
                ItemStackUtil.giveOrDropItem(player, current);
                if (!rejectedShown) {
                    rejectedShown = true;
                    player.sendMessage(Lang.getString(CellnetText.VOID_FILTER_REJECTED));
                }
                changed = true;
                continue;
            }
            if (current.getAmount() > 1) {
                ItemStack excess = current.clone();
                excess.setAmount(current.getAmount() - 1);
                current.setAmount(1);
                stripTemplateDisplay(current);
                menu.replaceExistingItem(slot, current);
                ItemStackUtil.giveOrDropItem(player, excess);
                changed = true;
            } else if (hasCustomDisplay(current)) {
                stripTemplateDisplay(current);
                menu.replaceExistingItem(slot, current);
                changed = true;
            }
        }
        return changed;
    }

    private static void handleClose(@NotNull UUID playerUuid, @NotNull Session session) {
        session.closing = true;
        Player current = Bukkit.getPlayer(playerUuid);
        if (current != null) {
            sanitize(current, session);
            persistSession(current, session, false);
        }
        runNextTick(() -> {
            SESSIONS.remove(playerUuid, session);
            Player player = Bukkit.getPlayer(playerUuid);
            if (player != null) {
                sanitize(player, session);
                persistSession(player, session, true);
            }
        });
    }

    private static void persistSession(@NotNull Player player, @NotNull Session session, boolean finalSave) {
        List<ItemStack> ordered = collectFilters(session);
        if (session.anchor != null) {
            persistToDrive(player, session, ordered, finalSave);
        } else {
            persistToHand(player, session, ordered, finalSave);
        }
    }

    private static void persistToHand(
            @NotNull Player player,
            @NotNull Session session,
            @NotNull List<ItemStack> ordered,
            boolean finalSave) {
        boolean savedOk = CellMenuCommon.refreshAndWriteBack(player, session.cellUuid,
            live -> {
                VoidCellSupport.writeVoidMeta(live, ordered);
                renderLoreOn(live);
            },
            CellMenuCommon::writeBackToHand);
        if (!savedOk) {
            if (finalSave) {
                player.sendMessage(Lang.getString(CellnetText.VOID_SAVE_TARGET_MISSING));
            }
            return;
        }
        if (finalSave) {
            player.sendMessage(Lang.getString(CellnetText.VOID_SAVED));
        }
    }

    private static void persistToDrive(
            @NotNull Player player,
            @NotNull Session session,
            @NotNull List<ItemStack> ordered,
            boolean finalSave) {
        BlockMenu driveMenu = StorageCacheUtils.getMenu(session.anchor.driveLocation());
        if (driveMenu == null) {
            if (finalSave) {
                player.sendMessage(Lang.getString(CellnetText.VOID_SAVE_TARGET_MISSING));
            }
            return;
        }
        int slot = CellMenuCommon.indexOfCellInDrive(driveMenu, session.cellUuid, session.anchor.driveSlot());
        if (slot < 0) {
            if (finalSave) {
                player.sendMessage(Lang.getString(CellnetText.VOID_SAVE_TARGET_MISSING));
            }
            return;
        }
        CellMenuCommon.refreshDriveCell(driveMenu, slot, updated -> {
            VoidCellSupport.writeVoidMeta(updated, ordered);
            renderLoreOn(updated);
        });
        CellDrive.getStorage().invalidateCellCache(session.anchor.driveLocation());
        if (finalSave) {
            player.sendMessage(Lang.getString(CellnetText.VOID_SAVED));
        }
    }

    @NotNull
    private static List<ItemStack> collectFilters(@NotNull Session session) {
        List<ItemStack> ordered = new ArrayList<>(FILTER_SLOTS.length);
        for (int slot : FILTER_SLOTS) {
            ItemStack filter = normalizeOrNull(session.menu.getItemInSlot(slot));
            if (filter != null) {
                ordered.add(filter);
            }
        }
        return ordered;
    }

    private static void renderLoreOn(@NotNull ItemStack cellItem) {
        SlimefunItem sf = SlimefunItem.getByItem(cellItem);
        if (sf instanceof VoidCell voidCell) {
            voidCell.renderLore(cellItem);
        }
    }

    @Nullable
    private static ItemStack normalizeOrNull(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return null;
        }
        if (VoidCellSupport.isVoidCell(itemStack) || StackUtils.isBlacklisted(itemStack)) {
            return null;
        }
        ItemStack copy = itemStack.clone();
        copy.setAmount(1);
        stripTemplateDisplay(copy);
        return copy;
    }

    private static boolean hasCustomDisplay(@NotNull ItemStack itemStack) {
        var meta = itemStack.getItemMeta();
        return meta != null && meta.hasDisplayName();
    }

    private static void stripTemplateDisplay(@NotNull ItemStack itemStack) {
        itemStack.editMeta(meta -> meta.displayName(null));
    }

    @NotNull
    private static ItemStack air() {
        return new ItemStack(Material.AIR);
    }

}
