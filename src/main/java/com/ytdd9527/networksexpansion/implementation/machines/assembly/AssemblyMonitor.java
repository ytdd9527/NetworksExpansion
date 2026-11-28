package com.ytdd9527.networksexpansion.implementation.machines.assembly;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.Scheduling;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.assembly.AssemblyMonitorBridge;
import com.ytdd9527.networksexpansion.api.assembly.DriveOverview;
import com.ytdd9527.networksexpansion.api.assembly.MonitorLine;
import com.ytdd9527.networksexpansion.api.cell.CellSlotUI;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.drive.DriveOwnership;
import com.ytdd9527.networksexpansion.api.filter.BrowseUI;
import com.ytdd9527.networksexpansion.utils.ChatInput;
import com.ytdd9527.networksexpansion.utils.GhostItems;
import com.ytdd9527.networksexpansion.utils.ItemSearch;
import com.ytdd9527.networksexpansion.utils.NetworkUtil;
import com.ytdd9527.networksexpansion.utils.NumberFormat;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;

public class AssemblyMonitor extends NetworkObject {

    private static final int PAGE_SIZE = 45;
    private static final int VIEW_SLOT = 45;
    private static final int PREV_SLOT = 47;
    private static final int REFRESH_BACK_SLOT = 49;
    private static final int NEXT_SLOT = 51;
    private static final int SEARCH_SLOT = 52;
    private static final int DETAIL_OPEN = 4;
    private static final int DETAIL_NAME = 2;
    private static final int[] LINE_SLOTS = new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17};
    private static final int[] GRID_SLOTS = new int[]{
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35,
        36, 37, 38, 39, 40, 41, 42, 43, 44
    };

    private static final class Session {
        int page;
        int view;
        @Nullable Location detail;
        @Nullable String search;
    }

    private static final Map<Location, Session> SESSIONS = new ConcurrentHashMap<>();

    private record LineEntry(@NotNull Location drive, @NotNull String driveName, int index,
                             @NotNull MonitorLine line) {
    }

    private record DriveView(@NotNull Location location, @Nullable String name, boolean enabled,
                             boolean overclock, boolean smart, int activeLines, int blockedLines) {
    }

    public AssemblyMonitor(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.BRIDGE);
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                setSize(54);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                    || (AssemblyMonitor.this.canUse(player, false)
                    && Slimefun.getProtectionManager()
                    .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public void newInstance(@NotNull BlockMenu menu, @NotNull Block block) {
                SESSIONS.put(block.getLocation(), new Session());
                setupHandlers(menu);
                render(menu);
                menu.addMenuOpeningHandler(p -> render(menu));
                menu.addMenuCloseHandler(p -> SESSIONS.remove(menu.getLocation()));
            }
        };
    }

    private static void nextTick(@NotNull Runnable action) {
        Scheduling.runTask(action);
    }

    private void setupHandlers(@NotNull BlockMenu menu) {
        for (int slot = 0; slot < PAGE_SIZE; slot++) {
            final int pos = slot;
            menu.addMenuClickHandler(pos, (player, s, clicked, action) -> {
                nextTick(() -> handleGridClick(player, menu, pos,
                    action.isRightClicked(), action.isShiftClicked()));
                return false;
            });
        }
        wireNavigationHandlers(menu);
        for (int slot = 0; slot < 54; slot++) {
            if (slot < PAGE_SIZE || slot == VIEW_SLOT || slot == PREV_SLOT
                || slot == REFRESH_BACK_SLOT || slot == NEXT_SLOT || slot == SEARCH_SLOT) {
                continue;
            }
            menu.addMenuClickHandler(slot, (p, s, i, a) -> false);
        }
    }

    private void wireNavigationHandlers(@NotNull BlockMenu menu) {
        menu.addMenuClickHandler(VIEW_SLOT, (player, s, clicked, action) -> {
            nextTick(() -> {
                Session session = session(menu);
                if (session.detail == null) {
                    session.view = (session.view + 1) % 2;
                    session.page = 0;
                    render(menu);
                }
            });
            return false;
        });
        menu.addMenuClickHandler(PREV_SLOT, (player, s, clicked, action) -> {
            nextTick(() -> {
                Session session = session(menu);
                if (session.page > 0) {
                    session.page--;
                    render(menu);
                }
            });
            return false;
        });
        menu.addMenuClickHandler(NEXT_SLOT, (player, s, clicked, action) -> {
            nextTick(() -> {
                Session session = session(menu);
                session.page++;
                render(menu);
            });
            return false;
        });
        menu.addMenuClickHandler(REFRESH_BACK_SLOT, (player, s, clicked, action) -> {
            nextTick(() -> {
                Session session = session(menu);
                if (session.detail != null) {
                    session.detail = null;
                    session.page = 0;
                }
                render(menu);
            });
            return false;
        });
        menu.addMenuClickHandler(SEARCH_SLOT, (player, s, clicked, action) -> {
            nextTick(() -> handleSearchClick(player, menu, action.isRightClicked()));
            return false;
        });
    }

    private void handleSearchClick(@NotNull Player player, @NotNull BlockMenu menu, boolean rightClick) {
        Session session = session(menu);
        if (session.detail != null) {
            return;
        }
        if (rightClick) {
            if (session.search != null) {
                session.search = null;
                session.page = 0;
                render(menu);
                player.sendMessage(Lang.getString(CellnetText.MONITOR_SEARCH_CLEARED));
            }
            return;
        }
        ItemSearch.requestSearch(player,
            ChatInput.SearchTarget.MONITOR, menu.getLocation(), null, null);
    }

    public static void applySearchFromChat(@NotNull Player player, @NotNull Location location, @NotNull String term) {
        BlockMenu menu = StorageCacheUtils.getMenu(location);
        if (menu == null) {
            return;
        }
        Session session = session(menu);
        session.search = term.isEmpty() ? null : term;
        session.page = 0;
        render(menu);
        player.sendMessage(Lang.getString(term.isEmpty()
            ? CellnetText.MONITOR_SEARCH_CLEARED
            : CellnetText.MONITOR_SEARCH_SET, term));
        menu.open(player);
    }

    private static void lineAction(@NotNull Player player, @NotNull Location drive, int index,
                                   boolean rightClick, boolean shift) {
        if (shift) {
            if (rightClick) {
                AssemblyMonitorBridge.requestQuantityFromMonitor(player, drive, index);
            } else {
                AssemblyMonitorBridge.cycleLineFromMonitor(player, drive, index);
            }
        } else {
            AssemblyMonitorBridge.changeGearFromMonitor(player, drive, index, rightClick ? -1 : 1);
        }
    }

    private void handleGridClick(@NotNull Player player, @NotNull BlockMenu menu,
                                 int pos, boolean rightClick, boolean shift) {
        Session session = session(menu);
        if (session.detail != null) {
            if (pos == DETAIL_OPEN) {
                openDrive(player, session.detail);
                return;
            }
            if (pos == DETAIL_NAME) {
                if (rightClick) {
                    AssemblyMonitorBridge.clearNameFromMonitor(player, session.detail);
                    render(menu);
                    return;
                }
                boolean replaced = ChatInput.request(player,
                    ChatInput.InputType.DRIVE_NAME,
                    new ChatInput.DriveNameContext(session.detail));
                if (replaced) {
                    player.sendMessage(Lang.getString(CellnetText.INPUT_PREVIOUS_CANCELLED));
                }
                player.sendMessage(Lang.getString(CellnetText.MONITOR_NAME_HINT));
                return;
            }
            int line = CellSlotUI.indexOf(LINE_SLOTS, pos);
            if (line >= 0) {
                List<MonitorLine> lines = AssemblyMonitorBridge.readLines(session.detail);
                if (line < lines.size()) {
                    lineAction(player, session.detail, line, rightClick, shift);
                }
            }
            render(menu);
            return;
        }
        if (session.view == 1) {
            List<LineEntry> entries = collectLines(menu);
            int index = session.page * PAGE_SIZE + pos;
            if (index >= 0 && index < entries.size()) {
                LineEntry entry = entries.get(index);
                lineAction(player, entry.drive(), entry.index(), rightClick, shift);
            }
            render(menu);
            return;
        }
        List<DriveView> drives = collectDriveViews(menu);
        int index = session.page * PAGE_SIZE + pos;
        if (index < 0 || index >= drives.size()) {
            return;
        }
        Location drive = drives.get(index).location();
        if (rightClick) {
            openDrive(player, drive);
        } else {
            session.detail = drive;
            session.page = 0;
            render(menu);
        }
    }

    private static void render(@NotNull BlockMenu menu) {
        Session session = session(menu);
        if (session.detail != null) {
            renderDetail(menu, session);
        } else if (session.view == 1) {
            renderAllLines(menu, session);
        } else {
            renderOverview(menu, session);
        }
    }

    private static void renderOverview(@NotNull BlockMenu menu, @NotNull Session session) {
        List<DriveView> drives = collectDriveViews(menu);
        if (session.search != null) {
            drives = filterDrives(drives, session.search);
        }
        final List<DriveView> shownDrives = drives;
        int pages = Math.max(1, (drives.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        session.page = Math.min(session.page, pages - 1);
        session.page = Math.max(session.page, 0);

        menu.replaceExistingItem(VIEW_SLOT, simpleIcon(Material.COMPASS,
            Lang.getString(CellnetText.MONITOR_VIEW_ALL)));
        menu.replaceExistingItem(PREV_SLOT, BrowseUI.pageButton(
            Lang.getString(session.page > 0 ? CellnetText.MONITOR_PREV_PAGE : CellnetText.MONITOR_FIRST_PAGE)));
        menu.replaceExistingItem(REFRESH_BACK_SLOT, simpleIcon(Material.SUNFLOWER,
            Lang.getString(CellnetText.MONITOR_REFRESH_NAME)));
        menu.replaceExistingItem(NEXT_SLOT, BrowseUI.pageButton(
            Lang.getString(session.page < pages - 1 ? CellnetText.MONITOR_NEXT_PAGE : CellnetText.MONITOR_LAST_PAGE)));
        menu.replaceExistingItem(SEARCH_SLOT, searchIcon(session.search));

        fillGrid(menu, session.page, shownDrives.size(), i -> driveIcon(shownDrives.get(i)));
    }

    private static void renderAllLines(@NotNull BlockMenu menu, @NotNull Session session) {
        List<LineEntry> entries = collectLines(menu);
        if (session.search != null) {
            entries = filterLines(entries, session.search);
        }
        final List<LineEntry> shownEntries = entries;
        int pages = Math.max(1, (entries.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        session.page = Math.min(session.page, pages - 1);
        session.page = Math.max(session.page, 0);

        menu.replaceExistingItem(VIEW_SLOT, simpleIcon(Material.WRITABLE_BOOK,
            Lang.getString(CellnetText.MONITOR_VIEW_LINES)));
        menu.replaceExistingItem(PREV_SLOT, BrowseUI.pageButton(
            Lang.getString(session.page > 0 ? CellnetText.MONITOR_PREV_PAGE : CellnetText.MONITOR_FIRST_PAGE)));
        menu.replaceExistingItem(REFRESH_BACK_SLOT, simpleIcon(Material.SUNFLOWER,
            Lang.getString(CellnetText.MONITOR_REFRESH_NAME)));
        menu.replaceExistingItem(NEXT_SLOT, BrowseUI.pageButton(
            Lang.getString(session.page < pages - 1 ? CellnetText.MONITOR_NEXT_PAGE : CellnetText.MONITOR_LAST_PAGE)));
        menu.replaceExistingItem(SEARCH_SLOT, searchIcon(session.search));

        if (entries.isEmpty()) {
            menu.replaceExistingItem(GRID_SLOTS[4], simpleIcon(Material.BARRIER,
                Lang.getString(session.search != null
                    ? CellnetText.MONITOR_SEARCH_EMPTY
                    : CellnetText.MONITOR_ALL_NONE)));
        }
        fillGrid(menu, session.page, shownEntries.size(), i -> {
            LineEntry entry = shownEntries.get(i);
            return lineItem(entry.line(), entry.driveName());
        });
    }

    private static void renderDetail(@NotNull BlockMenu menu, @NotNull Session session) {
        Location driveLocation = session.detail;
        DriveOverview overview = driveLocation == null ? null : AssemblyMonitorBridge.readOverview(driveLocation);
        if (overview == null) {
            session.detail = null;
            renderOverview(menu, session);
            return;
        }
        for (int slot = 0; slot < 54; slot++) {
            if (slot != DETAIL_OPEN && slot != DETAIL_NAME
                && (slot < 9 || slot > 17) && slot != REFRESH_BACK_SLOT) {
                menu.replaceExistingItem(slot, ChestMenuUtils.getBackground());
            }
        }
        String title = overview.name() != null ? overview.name()
            : Lang.getString(CellnetText.MONITOR_DRIVE_NAME,
                driveLocation.getBlockX(), driveLocation.getBlockY(), driveLocation.getBlockZ());
        ItemStack header = simpleIcon(Material.ENDER_EYE, title);
        header.editMeta(meta -> {
            List<String> lore = new ArrayList<>();
            lore.add(Lang.getString(overview.enabled()
                ? CellnetText.MONITOR_DRIVE_STATUS_ON
                : CellnetText.MONITOR_DRIVE_STATUS_OFF));
            if (overview.overclock()) {
                lore.add(Lang.getString(CellnetText.MONITOR_CORE_OVERCLOCK));
            }
            if (overview.smart()) {
                lore.add(Lang.getString(CellnetText.MONITOR_CORE_SMART));
            }
            if (!overview.overclock() && !overview.smart()) {
                lore.add(Lang.getString(CellnetText.MONITOR_CORE_NONE));
            }
            meta.setLore(lore);
        });
        menu.replaceExistingItem(DETAIL_OPEN, header);
        menu.replaceExistingItem(DETAIL_NAME, buildNameButton(overview.name()));
        menu.replaceExistingItem(REFRESH_BACK_SLOT, BrowseUI.pageButton(Lang.getString(CellnetText.MONITOR_BACK)));

        List<MonitorLine> lines = overview.lines();
        for (int i = 0; i < LINE_SLOTS.length; i++) {
            int slot = LINE_SLOTS[i];
            if (i < lines.size()) {
                menu.replaceExistingItem(slot, lineItem(lines.get(i), overview.name()));
            } else {
                menu.replaceExistingItem(slot, ChestMenuUtils.getBackground());
            }
        }
    }

    private static void fillGrid(@NotNull BlockMenu menu, int page, int size, @NotNull IntFunction<@NotNull ItemStack> factory) {
        for (int i = 0; i < PAGE_SIZE; i++) {
            int slot = GRID_SLOTS[i];
            int index = page * PAGE_SIZE + i;
            menu.replaceExistingItem(slot, index < size ? factory.apply(index) : ChestMenuUtils.getBackground());
        }
    }

    @NotNull
    private static ItemStack buildNameButton(@Nullable String name) {
        ItemStack icon = simpleIcon(Material.NAME_TAG, Lang.getString(CellnetText.MONITOR_NAME_BUTTON));
        icon.editMeta(meta -> {
            List<String> lore = new ArrayList<>();
            lore.add(name != null
                ? Lang.getString(CellnetText.MONITOR_NAME_CURRENT, name)
                : Lang.getString(CellnetText.MONITOR_NAME_NONE));
            lore.add(Lang.getString(CellnetText.MONITOR_NAME_ACTION_GEAR));
            lore.add(Lang.getString(CellnetText.MONITOR_NAME_ACTION_CLEAR));
            meta.setLore(lore);
        });
        return icon;
    }

    @NotNull
    private static ItemStack searchIcon(@Nullable String search) {
        ItemStack icon = simpleIcon(Material.SPYGLASS, Lang.getString(CellnetText.MONITOR_SEARCH_NAME));
        icon.editMeta(meta -> {
            List<String> lore = new ArrayList<>();
            lore.add(Lang.getString(search == null
                ? CellnetText.MONITOR_SEARCH_NONE
                : CellnetText.MONITOR_SEARCH_CURRENT, search));
            lore.add(Lang.getString(CellnetText.MONITOR_SEARCH_HINT_BUTTON));
            meta.setLore(lore);
        });
        return icon;
    }

    @NotNull
    private static List<DriveView> filterDrives(@NotNull List<DriveView> drives, @NotNull String search) {
        List<DriveView> filtered = new ArrayList<>(drives.size());
        for (DriveView view : drives) {
            Location location = view.location();
            String coords = location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
            if (matchesSearch(search, view.name(), coords)) {
                filtered.add(view);
            }
        }
        return filtered;
    }

    @NotNull
    private static List<LineEntry> filterLines(@NotNull List<LineEntry> entries, @NotNull String search) {
        List<LineEntry> filtered = new ArrayList<>(entries.size());
        for (LineEntry entry : entries) {
            String itemName = ItemStackHelper.getDisplayName(entry.line().output());
            if (matchesSearch(search, entry.driveName(), itemName)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    private static boolean matchesSearch(@NotNull String search, @Nullable String... candidates) {
        return ItemSearch.matches(search, candidates);
    }    @NotNull
    private static ItemStack lineItem(@NotNull MonitorLine line, @Nullable String driveName) {
        ItemStack icon = line.output().clone();
        icon.setAmount(1);
        icon.editMeta(meta -> {
            meta.setDisplayName(ItemStackHelper.getDisplayName(line.output()));
            List<String> lore = new ArrayList<>();
            if (line.mode() == AssemblyMonitorBridge.MODE_PLAN) {
                if (!line.planArmed()) {
                    lore.add(Lang.getString(CellnetText.MONITOR_LINE_MODE_PLAN_UNSET));
                } else {
                    lore.add(Lang.getString(line.remaining() > 0
                        ? CellnetText.MONITOR_LINE_MODE_PLAN
                        : CellnetText.MONITOR_LINE_MODE_PLAN_DONE,
                        NumberFormat.formatNumber(Math.max(0L, line.remaining()))));
                }
            } else if (line.mode() == AssemblyMonitorBridge.MODE_REPLENISH) {
                lore.add(Lang.getString(line.keep() > 0
                    ? CellnetText.MONITOR_LINE_MODE_REPLENISH
                    : CellnetText.MONITOR_LINE_MODE_REPLENISH_UNSET,
                    NumberFormat.formatNumber(Math.max(0L, line.keep()))));
            } else {
                lore.add(Lang.getString(CellnetText.MONITOR_LINE_MODE_CONTINUOUS));
            }
            lore.add(Lang.getString(line.gear() > 0
                ? CellnetText.MONITOR_LINE_GEAR
                : CellnetText.MONITOR_LINE_GEAR_STOP, speedText(line.gear())));
            if (line.blocked()) {
                lore.add(Lang.getString(CellnetText.MONITOR_LINE_BLOCKED));
            }
            if (driveName != null) {
                lore.add(Lang.getString(CellnetText.MONITOR_LINE_BELONGS, driveName));
            }
            lore.add(Lang.getString(CellnetText.MONITOR_LINE_HINT_GEAR));
            lore.add(Lang.getString(CellnetText.MONITOR_LINE_HINT_MODE));
            meta.setLore(lore);
        });
        GhostItems.mark(icon);
        return icon;
    }

    @NotNull
    private static String speedText(int gear) {
        long multiplier = AssemblyMonitorBridge.gearMultiplier(gear);
        return multiplier < 0 ? "×∞" : "×" + NumberFormat.formatNumber(multiplier);
    }

    @NotNull
    private static ItemStack driveIcon(@NotNull DriveView view) {
        ItemStack icon = Icon.DRIVE_BROWSE.clone();
        icon.editMeta(meta -> {
            String title = view.name() != null ? view.name()
                : Lang.getString(CellnetText.MONITOR_DRIVE_NAME,
                    view.location.getBlockX(), view.location.getBlockY(), view.location.getBlockZ());
            meta.setDisplayName(title);
            List<String> lore = new ArrayList<>();
            lore.add(Lang.getString(view.enabled
                ? CellnetText.MONITOR_DRIVE_STATUS_ON
                : CellnetText.MONITOR_DRIVE_STATUS_OFF));
            List<String> cores = new ArrayList<>();
            if (view.overclock) {
                cores.add(Lang.getString(CellnetText.MONITOR_CORE_OVERCLOCK));
            }
            if (view.smart) {
                cores.add(Lang.getString(CellnetText.MONITOR_CORE_SMART));
            }
            lore.add(cores.isEmpty()
                ? Lang.getString(CellnetText.MONITOR_CORE_NONE)
                : String.join(" · ", cores));
            lore.add(Lang.getString(CellnetText.MONITOR_DRIVE_LINES, view.activeLines()));
            if (view.blockedLines() > 0) {
                lore.add(Lang.getString(CellnetText.MONITOR_DRIVE_BLOCKED,
                    NumberFormat.formatNumber(view.blockedLines())));
            }
            lore.add(Lang.getString(CellnetText.MONITOR_DRIVE_HINT));
            meta.setLore(lore);
        });
        return icon;
    }

    @NotNull
    private static ItemStack simpleIcon(@NotNull Material material, @NotNull String name) {
        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> meta.setDisplayName(name));
        GhostItems.mark(item);
        return item;
    }

    @NotNull
    private static Session session(@NotNull BlockMenu menu) {
        return SESSIONS.computeIfAbsent(menu.getLocation(), k -> new Session());
    }

    @NotNull
    private static List<DriveOverview> collectOverviews(@NotNull BlockMenu menu) {
        List<DriveOverview> list = new ArrayList<>();
        NetworkRoot root = NetworkUtil.findRoot(menu.getLocation());
        if (root == null) {
            return list;
        }
        Set<Location> visited = new HashSet<>();
        for (Location nodeLocation : root.getNodeLocations()) {
            for (BlockFace face : BlockFace.values()) {
                if (!face.isCartesian()) {
                    continue;
                }
                Location neighbor = nodeLocation.clone().add(face.getDirection());
                if (!AssemblyMonitorBridge.activeDrives().contains(neighbor) || !visited.add(neighbor)) {
                    continue;
                }
                DriveOverview overview = AssemblyMonitorBridge.readOverview(neighbor);
                if (overview != null) {
                    list.add(overview);
                }
            }
        }
        return list;
    }

    @NotNull
    private static List<DriveView> collectDriveViews(@NotNull BlockMenu menu) {
        List<DriveView> list = new ArrayList<>();
        for (DriveOverview overview : collectOverviews(menu)) {
            int active = 0;
            int blocked = 0;
            for (MonitorLine line : overview.lines()) {
                if (line.gear() > 0) {
                    active++;
                }
                if (line.blocked()) {
                    blocked++;
                }
            }
            list.add(new DriveView(overview.location(), overview.name(), overview.enabled(),
                overview.overclock(), overview.smart(), active, blocked));
        }
        list.sort((a, b) -> Integer.compare(b.blockedLines() * 2 + (b.enabled() ? 0 : 1),
            a.blockedLines() * 2 + (a.enabled() ? 0 : 1)));
        return list;
    }

    @NotNull
    private static List<LineEntry> collectLines(@NotNull BlockMenu menu) {
        List<LineEntry> entries = new ArrayList<>();
        for (DriveOverview overview : collectOverviews(menu)) {
            if (overview.name() == null) {
                continue;
            }
            List<MonitorLine> lines = overview.lines();
            for (int i = 0; i < lines.size(); i++) {
                entries.add(new LineEntry(overview.location(), overview.name(), i, lines.get(i)));
            }
        }
        entries.sort((a, b) -> a.driveName().compareToIgnoreCase(b.driveName()));
        return entries;
    }

    private static void openDrive(@NotNull Player player, @NotNull Location drive) {
        if (!DriveOwnership.bypasses(player) && !DriveOwnership.isOwnerOrWhitelisted(player, drive)) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NO_PERMISSION));
            return;
        }
        BlockMenu menu = StorageCacheUtils.getMenu(drive);
        if (menu == null) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_DRIVE_NOT_LOADED));
            return;
        }
        if (!menu.getPreset().canOpen(drive.getBlock(), player)) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NO_PERMISSION));
            return;
        }
        menu.open(player);
    }
}
