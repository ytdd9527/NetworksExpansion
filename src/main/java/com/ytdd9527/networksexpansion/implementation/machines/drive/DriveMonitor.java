package com.ytdd9527.networksexpansion.implementation.machines.drive;

import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.Scheduling;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellHandle;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.drive.DriveGuide;
import com.ytdd9527.networksexpansion.api.drive.DriveOwnership;
import com.ytdd9527.networksexpansion.api.drive.DriveType;
import com.ytdd9527.networksexpansion.api.filter.BrowseUI;
import com.ytdd9527.networksexpansion.utils.ChatInput;
import com.ytdd9527.networksexpansion.utils.GhostItems;
import com.ytdd9527.networksexpansion.utils.ItemSearch;
import com.ytdd9527.networksexpansion.utils.Limits;
import com.ytdd9527.networksexpansion.utils.NetworkUtil;
import com.ytdd9527.networksexpansion.utils.NumberFormat;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
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

public class DriveMonitor extends NetworkObject {

    private static final String NAME_KEY = "drive_name";
    private static final int PAGE_SIZE = 45;
    private static final int PREV_SLOT = 47;
    private static final int REFRESH_SLOT = 49;
    private static final int NEXT_SLOT = 51;
    private static final int SEARCH_SLOT = 52;
    private static final int[] GRID_SLOTS = new int[]{
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35,
        36, 37, 38, 39, 40, 41, 42, 43, 44
    };

    private static final class Session {
        int page;
        @Nullable String search;
    }

    private static final Map<Location, Session> SESSIONS = new ConcurrentHashMap<>();

    private record DriveView(@NotNull Location location, @Nullable String name, @NotNull DriveType type,
                             int cells, long stored, @NotNull ItemStack icon) {
    }

    public DriveMonitor(
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
                    || (DriveMonitor.this.canUse(player, false)
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
        menu.addMenuClickHandler(REFRESH_SLOT, (player, s, clicked, action) -> {
            nextTick(() -> render(menu));
            return false;
        });
        menu.addMenuClickHandler(SEARCH_SLOT, (player, s, clicked, action) -> {
            nextTick(() -> handleSearchClick(player, menu, action.isRightClicked()));
            return false;
        });
        for (int slot = 45; slot < 54; slot++) {
            if (slot == PREV_SLOT || slot == REFRESH_SLOT || slot == NEXT_SLOT || slot == SEARCH_SLOT) {
                continue;
            }
            menu.addMenuClickHandler(slot, (p, s, i, a) -> false);
        }
    }

    private void handleSearchClick(@NotNull Player player, @NotNull BlockMenu menu, boolean rightClick) {
        Session session = session(menu);
        if (rightClick) {
            if (session.search != null) {
                session.search = null;
                session.page = 0;
                render(menu);
                player.sendMessage(Lang.getString(CellnetText.MANAGER_SEARCH_CLEARED));
            }
            return;
        }
        ItemSearch.requestSearch(player,
            ChatInput.SearchTarget.DRIVE_MANAGER, menu.getLocation(), null, null);
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
            ? CellnetText.MANAGER_SEARCH_CLEARED
            : CellnetText.MANAGER_SEARCH_SET, term));
        menu.open(player);
    }

    public static void applyNameFromChat(@NotNull Player player, @NotNull Location location, @NotNull String name) {
        if (!DriveOwnership.bypasses(player) && !DriveOwnership.isOwnerOrWhitelisted(player, location)) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NO_PERMISSION));
            return;
        }
        String trimmed = name.trim();
        if (trimmed.length() > Limits.MAX_CHANNEL_CODEPOINTS) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NAME_INVALID));
            return;
        }
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(location);
        if (blockData == null) {
            return;
        }
        if (trimmed.isEmpty() || "-".equals(trimmed)) {
            blockData.setData(NAME_KEY, "");
            player.sendMessage(Lang.getString(CellnetText.MANAGER_NAME_CLEARED));
            return;
        }
        blockData.setData(NAME_KEY, trimmed);
        player.sendMessage(Lang.getString(CellnetText.MANAGER_NAME_SET, trimmed));
    }

    @Nullable
    public static String displayName(@NotNull Location location) {
        String stored = StorageCacheUtils.getData(location, NAME_KEY);
        return stored == null || stored.isEmpty() ? null : stored;
    }

    private void handleGridClick(@NotNull Player player, @NotNull BlockMenu menu,
                                 int pos, boolean rightClick, boolean shift) {
        Session session = session(menu);
        List<DriveView> drives = collectDriveViews(menu);
        if (session.search != null) {
            drives = filterDrives(drives, session.search);
        }
        int index = session.page * PAGE_SIZE + pos;
        if (index < 0 || index >= drives.size()) {
            return;
        }
        Location drive = drives.get(index).location();
        if (shift) {
            if (rightClick) {
                clearName(player, drive);
            } else {
                requestRename(player, drive);
            }
            return;
        }
        if (rightClick) {
            DriveGuide.start(player, drive);
            return;
        }
        openDrive(player, drive);
    }

    private void requestRename(@NotNull Player player, @NotNull Location drive) {
        if (!DriveOwnership.bypasses(player) && !DriveOwnership.isOwnerOrWhitelisted(player, drive)) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NO_PERMISSION));
            return;
        }
        player.closeInventory();
        boolean replaced = ChatInput.request(player,
            ChatInput.InputType.MANAGER_DRIVE_NAME,
            new ChatInput.DriveNameContext(drive));
        if (replaced) {
            player.sendMessage(Lang.getString(CellnetText.INPUT_PREVIOUS_CANCELLED));
        }
        player.sendMessage(Lang.getString(CellnetText.MONITOR_NAME_HINT));
    }

    private void clearName(@NotNull Player player, @NotNull Location drive) {
        if (!DriveOwnership.bypasses(player) && !DriveOwnership.isOwnerOrWhitelisted(player, drive)) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NO_PERMISSION));
            return;
        }
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(drive);
        if (blockData == null) {
            return;
        }
        blockData.setData(NAME_KEY, "");
        player.sendMessage(Lang.getString(CellnetText.MANAGER_NAME_CLEARED));
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

    private static void render(@NotNull BlockMenu menu) {
        Session session = session(menu);
        List<DriveView> drives = collectDriveViews(menu);
        if (session.search != null) {
            drives = filterDrives(drives, session.search);
        }
        final List<DriveView> shownDrives = drives;
        int pages = Math.max(1, (drives.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        session.page = Math.min(session.page, pages - 1);
        session.page = Math.max(session.page, 0);

        menu.replaceExistingItem(PREV_SLOT, BrowseUI.pageButton(
            Lang.getString(session.page > 0 ? CellnetText.MONITOR_PREV_PAGE : CellnetText.MONITOR_FIRST_PAGE)));
        menu.replaceExistingItem(REFRESH_SLOT, simpleIcon(Material.SUNFLOWER,
            Lang.getString(CellnetText.MONITOR_REFRESH_NAME)));
        menu.replaceExistingItem(NEXT_SLOT, BrowseUI.pageButton(
            Lang.getString(session.page < pages - 1 ? CellnetText.MONITOR_NEXT_PAGE : CellnetText.MONITOR_LAST_PAGE)));
        menu.replaceExistingItem(SEARCH_SLOT, searchIcon(session.search));
        for (int slot = 45; slot < 54; slot++) {
            if (slot == PREV_SLOT || slot == REFRESH_SLOT || slot == NEXT_SLOT || slot == SEARCH_SLOT) {
                continue;
            }
            menu.replaceExistingItem(slot, ChestMenuUtils.getBackground());
        }

        if (shownDrives.isEmpty()) {
            menu.replaceExistingItem(GRID_SLOTS[22], simpleIcon(Material.BARRIER,
                Lang.getString(session.search != null
                    ? CellnetText.MANAGER_SEARCH_EMPTY
                    : CellnetText.MANAGER_EMPTY)));
        }
        fillGrid(menu, session.page, shownDrives.size(), i -> driveIcon(shownDrives.get(i)));
    }

    private static void fillGrid(@NotNull BlockMenu menu, int page, int size, @NotNull IntFunction<@NotNull ItemStack> factory) {
        for (int i = 0; i < PAGE_SIZE; i++) {
            int slot = GRID_SLOTS[i];
            int index = page * PAGE_SIZE + i;
            menu.replaceExistingItem(slot, index < size ? factory.apply(index) : ChestMenuUtils.getBackground());
        }
    }

    @NotNull
    private static ItemStack driveIcon(@NotNull DriveView view) {
        ItemStack icon = view.icon().clone();
        icon.setAmount(1);
        icon.editMeta(meta -> {
            String title = view.name() != null ? view.name()
                : Lang.getString(CellnetText.MANAGER_DRIVE_NAME,
                    view.location().getBlockX(), view.location().getBlockY(), view.location().getBlockZ());
            meta.setDisplayName(title);
            List<String> lore = new ArrayList<>();
            lore.add(Lang.getString(view.type() == DriveType.ENDER
                ? CellnetText.MANAGER_TYPE_ENDER
                : CellnetText.MANAGER_TYPE_STANDARD));
            lore.add(Lang.getString(CellnetText.MANAGER_CELLS, view.cells()));
            lore.add(Lang.getString(CellnetText.MANAGER_STORED,
                NumberFormat.formatNumber(view.stored())));
            lore.add(Lang.getString(CellnetText.MANAGER_DRIVE_HINT));
            lore.add(Lang.getString(CellnetText.MANAGER_DRIVE_HINT_NAME));
            meta.setLore(lore);
        });
        GhostItems.mark(icon);
        return icon;
    }

    @NotNull
    private static ItemStack searchIcon(@Nullable String search) {
        ItemStack icon = simpleIcon(Material.SPYGLASS, Lang.getString(CellnetText.MANAGER_SEARCH_NAME));
        icon.editMeta(meta -> {
            List<String> lore = new ArrayList<>();
            lore.add(Lang.getString(search == null
                ? CellnetText.MANAGER_SEARCH_NONE
                : CellnetText.MANAGER_SEARCH_CURRENT, search));
            lore.add(Lang.getString(CellnetText.MANAGER_SEARCH_HINT_BUTTON));
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
            if (ItemSearch.matches(search, view.name(), coords)) {
                filtered.add(view);
            }
        }
        return filtered;
    }

    @NotNull
    private static List<DriveView> collectDriveViews(@NotNull BlockMenu menu) {
        List<DriveView> list = new ArrayList<>();
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
                if (!visited.add(neighbor)) {
                    continue;
                }
                SlimefunItem sfItem = StorageCacheUtils.getSfItem(neighbor);
                DriveType type = DriveType.of(sfItem);
                if (type == null) {
                    continue;
                }
                int cells = 0;
                long stored = 0L;
                BlockMenu driveMenu = StorageCacheUtils.getMenu(neighbor);
                if (driveMenu != null) {
                    List<CellHandle> handles = CellDrive.getStorage().getCells(driveMenu);
                    cells = handles.size();
                    for (long amount : CellDrive.getStorage().getAllCellItems(handles).values()) {
                        stored += amount;
                    }
                }
                list.add(new DriveView(neighbor, displayName(neighbor), type, cells, stored,
                    sfItem.getItem()));
            }
        }
        list.sort((a, b) -> {
            if (a.name() == null != (b.name() == null)) {
                return a.name() == null ? 1 : -1;
            }
            if (a.name() != null) {
                return a.name().compareToIgnoreCase(b.name());
            }
            Location la = a.location();
            Location lb = b.location();
            int cmp = Integer.compare(la.getBlockX(), lb.getBlockX());
            if (cmp != 0) {
                return cmp;
            }
            cmp = Integer.compare(la.getBlockY(), lb.getBlockY());
            return cmp != 0 ? cmp : Integer.compare(la.getBlockZ(), lb.getBlockZ());
        });
        return list;
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
}
