package com.ytdd9527.networksexpansion.implementation.machines.unit;

import com.balugaq.netex.api.data.ItemContainer;
import com.balugaq.netex.api.data.StorageUnitData;
import com.balugaq.netex.api.enums.QuickTransferMode;
import com.balugaq.netex.api.enums.StorageUnitType;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.api.interfaces.Configurable;
import com.balugaq.netex.api.interfaces.ModelledItem;
import com.balugaq.netex.utils.Lang;
import com.jeff_media.morepersistentdatatypes.DataType;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.implementation.tools.ItemMover;
import com.ytdd9527.networksexpansion.utils.DisplayGroupGenerators;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import com.ytdd9527.networksexpansion.utils.databases.DataStorage;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.network.stackcaches.QuantumCache;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.sefiraat.networks.utils.datatypes.PersistentQuantumStorageType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DistinctiveItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import me.ddggdd135.guguslimefunlib.GuguSlimefunLib;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class NetworksDrawer extends SpecialSlimefunItem implements DistinctiveItem, ModelledItem, Configurable {
    private static final boolean DEFAULT_USE_SPECIAL_MODEL = false;
    private static final Map<Location, StorageUnitData> storages = new HashMap<>();
    private static final Map<Location, QuickTransferMode> quickTransferModes = new HashMap<>();
    private static final Set<Location> locked = new HashSet<>();
    private static final Set<Location> voidExcesses = new HashSet<>();
    private static final String KEY_UUID = "display-uuid";
    private static final int[] DISPLAY_SLOTS = {
        10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43,
        46, 47, 48, 49, 50, 51, 52
    };
    private static final int STORAGE_INFO_SLOT = 4;
    private static final NamespacedKey idKey = Keys.newKey("CONTAINER_ID");
    private static final NamespacedKey lockKey = Keys.newKey("CONTAINER_LOCK");
    private static final NamespacedKey voidExcessKey = Keys.newKey("CONTAINER_VOID_EXCESS");
    private static final NamespacedKey serverKey = Keys.newKey("CONTAINER_SERVER");
    private static final int QUANTUM_SLOT = 9;
    private static final int QUICK_TRANSFER_SLOT = 18;
    private static final int ITEM_CHOOSE_SLOT = 27;
    private final StorageUnitType sizeType;
    private final int[] BORDER = {0, 1, 2, 3, 5, 6, 17, 26, 35, 36, 44, 45, 53};
    private final int VOID_MODE_SLOT = 7;
    private final int LOCK_MODE_SLOT = 8;
    private @Nullable Function<Location, DisplayGroup> displayGroupGenerator;
    private boolean useSpecialModel;

    public NetworksDrawer(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe,
            StorageUnitType sizeType) {
        super(itemGroup, item, recipeType, recipe);

        this.sizeType = sizeType;

        new BlockMenuPreset(this.getId(), this.getItemName()) {
            @Override
            public void init() {
                for (int slot : BORDER) {
                    addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
                }
                addItem(STORAGE_INFO_SLOT, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
                addItem(LOCK_MODE_SLOT, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
                addItem(VOID_MODE_SLOT, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }

            @Override
            public void newInstance(@NotNull BlockMenu menu, @NotNull Block b) {
                Location l = b.getLocation();
                requestData(l, getContainerId(l));
                // Restore mode
                SlimefunBlockData blockData = StorageCacheUtils.getBlock(l);
                String lock = null;
                String voidExcess = null;
                String quickModeStr = null;
                if (blockData != null) {
                    lock = blockData.getData("locked");
                    voidExcess = blockData.getData("voidExcess");
                    quickModeStr = blockData.getData("quickTransferMode");
                }
                QuickTransferMode quickTransferMode =
                        quickModeStr == null ? QuickTransferMode.FROM_QUANTUM : QuickTransferMode.valueOf(quickModeStr);
                quickTransferModes.put(l, quickTransferMode);
                if (lock != null) {
                    locked.add(l);
                    menu.replaceExistingItem(LOCK_MODE_SLOT, getContentLockItem(true));
                } else {
                    menu.replaceExistingItem(LOCK_MODE_SLOT, getContentLockItem(false));
                }

                if (voidExcess != null) {
                    voidExcesses.add(l);
                    menu.replaceExistingItem(VOID_MODE_SLOT, getVoidExcessItem(true));
                } else {
                    menu.replaceExistingItem(VOID_MODE_SLOT, getVoidExcessItem(false));
                }

                menu.replaceExistingItem(QUICK_TRANSFER_SLOT, getQuickTransferItem(quickTransferMode));

                // Add lock mode switcher
                menu.addMenuClickHandler(LOCK_MODE_SLOT, (p, slot, item1, action) -> {
                    switchLock(menu, l);
                    return false;
                });

                menu.addMenuClickHandler(VOID_MODE_SLOT, (p, slot, item1, action) -> {
                    switchVoidExcess(menu, l);
                    return false;
                });

                menu.addMenuClickHandler(QUICK_TRANSFER_SLOT, (p, slot, item1, action) -> {
                    if (action.isRightClicked()) {
                        switchQuickTransferMode(menu, l);
                    } else {
                        quickTransfer(menu, l, p);
                    }
                    return false;
                });

                StorageUnitData data = storages.get(l);
                if (data != null) {
                    update(l, true);
                }
            }

            @Override
            public boolean canOpen(@NotNull Block b, @NotNull Player p) {
                return p.hasPermission("slimefun.inventory.bypass")
                        || (canUse(p, false)
                                && Slimefun.getProtectionManager().hasPermission(p, b, Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };

        if (item.getItemId().endsWith("MODEL")) {
            loadConfigurations();
        }
    }

    @Nullable public static StorageUnitData getStorageData(Location l) {
        return storages.get(l);
    }

    @NotNull public static Map<Location, StorageUnitData> getAllStorageData() {
        return storages;
    }

    public static void setStorageData(Location l, StorageUnitData data) {
        storages.put(l, data);
    }

    public static void update(@NotNull Location l, boolean force) {
        BlockMenu menu = StorageCacheUtils.getMenu(l);
        if (menu != null && (force || menu.hasViewer())) {

            StorageUnitData data = storages.get(l);
            if (data == null) {
                requestData(l, getContainerId(l));
                return;
            }
            StorageUnitType sizeType = data.getSizeType();
            int maxEach = sizeType.getEachMaxSize();

            // Update information
            menu.replaceExistingItem(
                    STORAGE_INFO_SLOT,
                    getStorageInfoItem(
                            data.getId(),
                            data.getStoredTypeCount(),
                            sizeType.getMaxItemCount(),
                            maxEach,
                            isLocked(l),
                            isVoidExcess(l)));

            // Update item display
            List<ItemContainer> itemStored = storages.get(l).getStoredItems();
            for (int i = 0; i < DISPLAY_SLOTS.length; i++) {
                if (i < itemStored.size()) {
                    ItemContainer each = itemStored.get(i);
                    menu.replaceExistingItem(
                            DISPLAY_SLOTS[i], getDisplayItem(each.getSampleDirectly(), each.getAmount(), maxEach));
                } else {
                    menu.replaceExistingItem(DISPLAY_SLOTS[i], Icon.ERROR_BORDER);
                }
            }
        }
    }

    public static boolean contains(Location l, ItemStack itemStack) {
        for (ItemContainer each : storages.get(l).getStoredItems()) {
            if (StackUtils.itemsMatch(each.getSampleDirectly(), itemStack)) {
                return true;
            }
        }
        return false;
    }

    public static int getBoundId(@NotNull ItemStack item) {
        // Get meta
        final ItemMeta meta = item.getItemMeta();
        Integer id = null;
        // Check if meta has bound id
        if (meta != null && meta.getPersistentDataContainer().has(idKey, PersistentDataType.INTEGER)) {
            id = meta.getPersistentDataContainer().get(idKey, PersistentDataType.INTEGER);
        }
        if (id == null) {
            id = -1;
        }
        return id;
    }

    public static boolean getLock(@NotNull ItemStack item) {
        // Get meta
        final ItemMeta meta = item.getItemMeta();
        Boolean lock = null;
        // Check if meta has lock
        if (meta != null && meta.getPersistentDataContainer().has(lockKey, PersistentDataType.BOOLEAN)) {
            lock = meta.getPersistentDataContainer().get(lockKey, PersistentDataType.BOOLEAN);
        }
        if (lock == null) {
            lock = false;
        }
        return lock;
    }

    public static boolean getVoidExcess(@NotNull ItemStack item) {
        // Get meta
        final ItemMeta meta = item.getItemMeta();
        Boolean voidExcess = null;
        // Check if meta has void excess
        if (meta != null && meta.getPersistentDataContainer().has(voidExcessKey, PersistentDataType.BOOLEAN)) {
            voidExcess = meta.getPersistentDataContainer().get(voidExcessKey, PersistentDataType.BOOLEAN);
        }
        if (voidExcess == null) {
            voidExcess = false;
        }
        return voidExcess;
    }

    public static @NotNull ItemStack bindId(@NotNull ItemStack itemSample, int id) {
        final ItemStack item = itemSample.clone();
        final ItemMeta meta = item.getItemMeta();
        List<String> lore;
        if (meta != null) {
            lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(String.format(Lang.getString("messages.completed-operation.drawer.bound_id"), id));
            if (Networks.getSupportedPluginManager().isGuguSlimefunLib()) {
                lore.add(String.format(
                        Lang.getString("messages.completed-operation.drawer.server-uuid"),
                        GuguSlimefunLib.getServerUUID()));
            }
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(idKey, PersistentDataType.INTEGER, id);
            if (Networks.getSupportedPluginManager().isGuguSlimefunLib()) {
                meta.getPersistentDataContainer().set(serverKey, DataType.UUID, GuguSlimefunLib.getServerUUID());
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack bindIdNew(
            @NotNull ItemStack itemSample, int id, boolean lock, boolean voidExcess) {
        final ItemStack item = itemSample.clone();
        final ItemMeta meta = item.getItemMeta();
        List<String> lore;
        if (meta != null) {
            lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(String.format(Lang.getString("messages.completed-operation.drawer.bound_id"), id));
            if (Networks.getSupportedPluginManager().isGuguSlimefunLib()) {
                lore.add(String.format(
                        Lang.getString("messages.completed-operation.drawer.server-uuid"),
                        GuguSlimefunLib.getServerUUID()));
            }
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(idKey, PersistentDataType.INTEGER, id);
            meta.getPersistentDataContainer().set(lockKey, PersistentDataType.BOOLEAN, lock);
            meta.getPersistentDataContainer().set(voidExcessKey, PersistentDataType.BOOLEAN, voidExcess);
            if (Networks.getSupportedPluginManager().isGuguSlimefunLib()) {
                meta.getPersistentDataContainer().set(serverKey, DataType.UUID, GuguSlimefunLib.getServerUUID());
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    public static void addBlockInfo(@NotNull Location l, int id, boolean lock, boolean voidExcess) {
        // Save id
        StorageCacheUtils.setData(l, "containerId", String.valueOf(id));
        // Save mode
        setLock(l, lock);
        setVoidExcess(l, voidExcess);
    }

    public static boolean isLocked(Location l) {
        return locked.contains(l);
    }

    public static boolean isLocked(int containerId) {
        for (Location l : locked) {
            if (getContainerId(l) == containerId) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVoidExcess(Location l) {
        return voidExcesses.contains(l);
    }

    public static boolean isVoidExcess(int containerId) {
        for (Location l : voidExcesses) {
            if (getContainerId(l) == containerId) {
                return true;
            }
        }
        return false;
    }

    private static @NotNull ItemStack getDisplayItem(@Nullable ItemStack item, int amount, int max) {
        if (item == null) {
            return Icon.ERROR_BORDER;
        }
        try {
            return new CustomItemStack(
                    item, (String) null, "", String.format(Lang.getString("icons.drawer.stored_item"), amount, max));
        } catch (NullPointerException e) {
            return item.clone();
        }
    }

    public static void requestData(@NotNull Location l, int id) {
        if (id == -1) return;
        if (DataStorage.isContainerLoaded(id)) {
            DataStorage.getCachedStorageData(id).ifPresent(data -> storages.put(l, data));
        } else {
            DataStorage.requestStorageData(id);
        }
        addClickHandler(l);
    }

    private static void addClickHandler(@NotNull Location l) {
        final BlockMenu blockMenu = StorageCacheUtils.getMenu(l);
        if (blockMenu == null) {
            return;
        }
        final StorageUnitData data = storages.get(l);
        for (int s : DISPLAY_SLOTS) {
            blockMenu.addMenuClickHandler(s, (player, slot, clickItem, action) -> {
                final ItemStack itemOnCursor = player.getItemOnCursor();
                if (StackUtils.itemsMatch(clickItem, Icon.ERROR_BORDER)) {
                    if (itemOnCursor.getType() != Material.AIR) {
                        data.depositItemStack0(l, itemOnCursor, false, true);
                    }
                } else {
                    List<Integer> a = new ArrayList<>();
                    for (int i : DISPLAY_SLOTS) {
                        a.add(i);
                    }
                    int index = a.indexOf(slot);
                    final ItemStack take =
                            storages.get(l).getStoredItems().get(index).getSampleDirectly();

                    final ItemRequest itemRequest = new ItemRequest(take, 1);

                    if (!action.isShiftClicked() || !action.isRightClicked()) {
                        if (action.isRightClicked()) {
                            itemRequest.setAmount(take.getMaxStackSize());
                        } else if (action.isShiftClicked()) {
                            itemRequest.setAmount(take.getMaxStackSize() * 36);
                        }

                        final ItemStack requestedItemStack = data.requestItem0(l, itemRequest, false);
                        if (requestedItemStack != null) {
                            do {
                                int max =
                                        Math.min(requestedItemStack.getAmount(), requestedItemStack.getMaxStackSize());
                                final ItemStack clone = StackUtils.getAsQuantity(requestedItemStack, max);
                                requestedItemStack.setAmount(requestedItemStack.getAmount() - max);
                                final HashMap<Integer, ItemStack> remnant =
                                        player.getInventory().addItem(clone);
                                remnant.values().stream()
                                        .findFirst()
                                        .ifPresent(leftOver -> data.depositItemStack0(l, leftOver, false));
                            } while (requestedItemStack.getAmount() > 0);
                        }
                    } else {
                        for (ItemStack each : player.getInventory().getStorageContents()) {
                            if (StackUtils.itemsMatch(each, take)) {
                                data.depositItemStack0(l, each, true);
                            }
                        }
                    }
                }
                return false;
            });
        }
    }

    private static @NotNull ItemStack getStorageInfoItem(
            int id, int typeCount, int maxType, int maxEach, boolean locked, boolean voidExcess) {
        return new CustomItemStack(
                Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                Lang.getString("icons.drawer.storage_info.name"),
                "",
                String.format(Lang.getString("icons.drawer.storage_info.id"), id),
                String.format(Lang.getString("icons.drawer.storage_info.type_count"), typeCount, maxType),
                String.format(Lang.getString("icons.drawer.storage_info.max_each"), maxType, maxEach),
                String.format(
                        Lang.getString("icons.drawer.storage_info.locked"),
                        locked
                                ? Lang.getString("icons.drawer.storage_info.enabled")
                                : Lang.getString("icons.drawer.storage_info.disabled")),
                String.format(
                        Lang.getString("icons.drawer.storage_info.void_excess"),
                        voidExcess
                                ? Lang.getString("icons.drawer.storage_info.enabled")
                                : Lang.getString("icons.drawer.storage_info.disabled")));
    }

    private static void setLock(@NotNull Location l, boolean lock) {
        if (lock) {
            locked.add(l);
            StorageCacheUtils.setData(l, "locked", "enable");
        }
    }

    private static void setVoidExcess(@NotNull Location l, boolean voidExcess) {
        if (voidExcess) {
            voidExcesses.add(l);
            StorageCacheUtils.setData(l, "voidExcess", "enable");
        }
    }

    private static void switchQuickTransferMode(@NotNull BlockMenu blockMenu, @NotNull Location location) {
        QuickTransferMode mode = quickTransferModes.get(location);
        if (mode == null || mode == QuickTransferMode.TO_QUANTUM) {
            mode = QuickTransferMode.FROM_QUANTUM;
        } else {
            mode = QuickTransferMode.TO_QUANTUM;
        }
        quickTransferModes.put(location, mode);
        blockMenu.replaceExistingItem(QUICK_TRANSFER_SLOT, getQuickTransferItem(mode));
        StorageCacheUtils.setData(location, "quickTransferMode", mode.name());
    }

    private static void quickTransfer(
            @NotNull BlockMenu blockMenu, @NotNull Location location, @NotNull Player player) {
        final ItemStack itemStack = blockMenu.getItemInSlot(QUANTUM_SLOT);
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.drawer.invalid_container"));
            return;
        }

        if (itemStack.getAmount() != 1) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.drawer.invalid_container_amount"));
            return;
        }

        final ItemStack toTransfer = blockMenu.getItemInSlot(ITEM_CHOOSE_SLOT);
        if (toTransfer == null || toTransfer.getType() == Material.AIR) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.drawer.invalid_chosen_item"));
            return;
        }

        boolean isQuantum = false;
        boolean isMover = false;
        final SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (slimefunItem instanceof NetworkQuantumStorage) {
            isQuantum = true;
        } else if (slimefunItem instanceof ItemMover) {
            isMover = true;
        }

        final StorageUnitData thisStorage = storages.get(location);
        final QuickTransferMode mode = quickTransferModes.get(location);

        if (isQuantum) {
            for (ItemContainer each : thisStorage.getStoredItems()) {
                final ItemStack sample = each.getSampleDirectly();
                if (StackUtils.itemsMatch(sample, toTransfer)) {
                    final ItemMeta meta = itemStack.getItemMeta();
                    QuantumCache quantumCache = DataTypeMethods.getCustom(
                            meta, Keys.QUANTUM_STORAGE_INSTANCE, PersistentQuantumStorageType.TYPE);

                    if (quantumCache == null) {
                        quantumCache = DataTypeMethods.getCustom(
                                meta, Keys.QUANTUM_STORAGE_INSTANCE2, PersistentQuantumStorageType.TYPE);
                    }

                    if (quantumCache == null) {
                        quantumCache = DataTypeMethods.getCustom(
                                meta, Keys.QUANTUM_STORAGE_INSTANCE3, PersistentQuantumStorageType.TYPE);
                    }

                    switch (mode) {
                        case FROM_QUANTUM -> {
                            if (quantumCache == null
                                    || quantumCache.getItemStack() == null
                                    || quantumCache.getAmount() <= 0) {
                                player.sendMessage(Lang.getString(
                                        "messages.unsupported-operation.drawer.invalid_quantum_storage"));
                                return;
                            }
                            if (!StackUtils.itemsMatch(quantumCache.getItemStack(), sample)) {
                                player.sendMessage(Lang.getString(
                                        "messages.unsupported-operation.drawer.quantum_storage_item_mismatch"));
                                return;
                            }
                            final long quantumAmount = quantumCache.getAmount();
                            final int canAdd = (int) Math.min(
                                    quantumAmount, thisStorage.getSizeType().getEachMaxSize() - each.getAmount());
                            if (canAdd <= 0) {
                                player.sendMessage(
                                        Lang.getString("messages.unsupported-operation.drawer.quantum_storage_full"));
                                return;
                            }

                            final int left = (int) quantumAmount - canAdd;
                            if (left > 0) {
                                quantumCache.setAmount(left);
                                DataTypeMethods.setCustom(
                                        meta,
                                        Keys.QUANTUM_STORAGE_INSTANCE,
                                        PersistentQuantumStorageType.TYPE,
                                        quantumCache);
                                quantumCache.updateMetaLore(meta);
                                itemStack.setItemMeta(meta);
                            } else {
                                blockMenu.replaceExistingItem(QUANTUM_SLOT, slimefunItem.getItem());
                            }
                            final ItemStack clone = quantumCache.getItemStack().clone();
                            clone.setAmount(canAdd);
                            thisStorage.depositItemStack0(location, clone, true);
                            player.sendMessage(Lang.getString("messages.completed-operation.drawer.deposited_item"));
                            return;
                        }

                        case TO_QUANTUM -> {
                            if (each.getAmount() == 0 && locked.contains(location)) {
                                player.sendMessage(
                                        Lang.getString("messages.unsupported-operation.drawer.not_enough_item"));
                                return;
                            }

                            if (quantumCache == null) {
                                final NetworkQuantumStorage nqs = (NetworkQuantumStorage) slimefunItem;
                                final int quantumLimit = nqs.getMaxAmount();

                                final int unitAmount = each.getAmount();
                                final int canAdd = Math.min(unitAmount, quantumLimit);
                                if (canAdd <= 0) {
                                    player.sendMessage(Lang.getString(
                                            "messages.unsupported-operation.drawer.each_not_enough_item"));
                                    return;
                                }
                                final ItemStack clone = sample.clone();

                                thisStorage.requestItem0(location, new ItemRequest(clone, canAdd), false);
                                storages.put(location, thisStorage);

                                quantumCache = new QuantumCache(clone, canAdd, quantumLimit, false, false);
                                DataTypeMethods.setCustom(
                                        meta,
                                        Keys.QUANTUM_STORAGE_INSTANCE,
                                        PersistentQuantumStorageType.TYPE,
                                        quantumCache);
                                quantumCache.updateMetaLore(meta);
                                itemStack.setItemMeta(meta);

                                player.sendMessage(Lang.getString(
                                        "messages.completed-operation.drawer.transferred_to_quantum_storage"));
                                return;
                            } else if (StackUtils.itemsMatch(quantumCache.getItemStack(), sample)) {
                                final int quantumLimit = quantumCache.getLimit();
                                final int quantumAmount = (int) quantumCache.getAmount();
                                final int unitAmount = each.getAmount();
                                final int canAdd = Math.min(unitAmount, quantumLimit - quantumAmount);
                                if (canAdd <= 0) {
                                    player.sendMessage(Lang.getString(
                                            "messages.unsupported-operation.drawer.each_not_enough_item"));
                                    return;
                                }
                                final ItemStack clone = sample.clone();

                                thisStorage.requestItem0(location, new ItemRequest(clone, canAdd));
                                storages.put(location, thisStorage);

                                quantumCache =
                                        new QuantumCache(clone, quantumAmount + canAdd, quantumLimit, false, false);
                                DataTypeMethods.setCustom(
                                        meta,
                                        Keys.QUANTUM_STORAGE_INSTANCE,
                                        PersistentQuantumStorageType.TYPE,
                                        quantumCache);
                                quantumCache.updateMetaLore(meta);
                                itemStack.setItemMeta(meta);
                                player.sendMessage(Lang.getString(
                                        "messages.completed-operation.drawer.transferred_to_quantum_storage"));
                                return;
                            } else {
                                return;
                            }
                        }
                    }
                }
            }
            player.sendMessage(String.format(
                    Lang.getString("messages.unsupported-operation.drawer.not_found_chosen_item"),
                    ItemStackHelper.getDisplayName(toTransfer)));
        } else if (isMover) {
            ItemStack moverStored = ItemMover.getStoredItemStack(itemStack);
            if (mode == QuickTransferMode.FROM_QUANTUM) {
                if (moverStored == null) {
                    player.sendMessage(Lang.getString("messages.unsupported-operation.drawer.item_mover_empty"));
                    return;
                }
                if (!StackUtils.itemsMatch(moverStored, toTransfer)) {
                    player.sendMessage(
                            Lang.getString("messages.unsupported-operation.drawer.item_mover_item_mismatch"));
                    return;
                }
            }
            if (mode == QuickTransferMode.TO_QUANTUM) {
                if (moverStored != null && !StackUtils.itemsMatch(moverStored, toTransfer)) {
                    player.sendMessage(
                            Lang.getString("messages.unsupported-operation.drawer.item_mover_item_mismatch"));
                    return;
                }
            }

            for (ItemContainer each : thisStorage.getStoredItems()) {
                final ItemStack sample = each.getSampleDirectly();
                if (StackUtils.itemsMatch(sample, toTransfer)) {
                    switch (mode) {
                        case FROM_QUANTUM -> {
                            ItemStack stored = StackUtils.getAsQuantity(
                                    ItemMover.getStoredItemStack(itemStack), ItemMover.getStoredAmount(itemStack));
                            if (stored == null || stored.getType() == Material.AIR) {
                                player.sendMessage(
                                        Lang.getString("messages.unsupported-operation.drawer.item_mover_empty"));
                            }
                            int before = stored.getAmount();
                            String name = ItemStackHelper.getDisplayName(stored);
                            thisStorage.depositItemStack0(location, stored, true);
                            int left = stored.getAmount();
                            ItemMover.setStoredAmount(itemStack, left);
                            player.sendMessage(String.format(
                                    Lang.getString("messages.completed-operation.drawer.transferred_to_drawer"),
                                    name,
                                    before - left));
                        }
                        case TO_QUANTUM -> {
                            ItemRequest itemRequest = new ItemRequest(sample, each.getAmount());
                            int before = each.getAmount();
                            ItemStack fetched = thisStorage.requestItem0(location, itemRequest, false);
                            if (fetched != null) {
                                String name = ItemStackHelper.getDisplayName(fetched);
                                ItemMover.depositItem(itemStack, fetched);
                                int left = fetched.getAmount();
                                if (fetched.getAmount() > 0) {
                                    thisStorage.depositItemStack0(location, fetched, false);
                                }
                                player.sendMessage(String.format(
                                        Lang.getString("messages.completed-operation.drawer.transferred_to_item_mover"),
                                        name,
                                        before - left));
                            }
                        }
                    }
                    ItemMover.updateLore(itemStack);
                    return;
                }
            }
            player.sendMessage(String.format(
                    Lang.getString("messages.unsupported-operation.drawer.not_found_chosen_item"),
                    ItemStackHelper.getDisplayName(toTransfer)));
        } else {
            player.sendMessage(Lang.getString("messages.unsupported-operation.drawer.invalid_container"));
        }
    }

    private static int getContainerId(@NotNull Location l) {
        final String str = StorageCacheUtils.getData(l, "containerId");
        return str == null ? -1 : Integer.parseInt(str);
    }

    private static @NotNull ItemStack getQuickTransferItem(QuickTransferMode mode) {
        List<String> lore = new ArrayList<>(Lang.getStringList("icons.drawer.quick_transfer.lore_before_status"));
        lore.add(String.format(
                Lang.getString("icons.drawer.quick_transfer.status"),
                mode == QuickTransferMode.FROM_QUANTUM
                        ? Lang.getString("icons.drawer.quick_transfer.from_quantum")
                        : Lang.getString("icons.drawer.quick_transfer.to_quantum")));
        lore.addAll(Lang.getStringList("icons.drawer.quick_transfer.lore_after_status"));
        CustomItemStack cis = new CustomItemStack(
                mode == QuickTransferMode.FROM_QUANTUM ? Material.GREEN_CONCRETE_POWDER : Material.BLUE_CONCRETE_POWDER,
                Lang.getString("icons.drawer.quick_transfer.name"),
                lore);

        return ItemStackUtil.getCleanItem(cis);
    }

    @Contract("null -> null")
    public static UUID getServerUUID(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        }

        return meta.getPersistentDataContainer().get(serverKey, DataType.UUID);
    }

    public void loadConfigurations() {
        final String configKey = this.getId();
        FileConfiguration config = Networks.getInstance().getConfig();

        this.useSpecialModel =
                config.getBoolean("items." + configKey + ".use-special-model.enable", DEFAULT_USE_SPECIAL_MODEL);

        Map<String, Function<Location, DisplayGroup>> generatorMap = new HashMap<>();
        generatorMap.put("1", DisplayGroupGenerators::generateStorageUnit_1);
        generatorMap.put("2", DisplayGroupGenerators::generateStorageUnit_2);
        generatorMap.put("3", DisplayGroupGenerators::generateStorageUnit_3);
        generatorMap.put("4", DisplayGroupGenerators::generateStorageUnit_4);
        generatorMap.put("5", DisplayGroupGenerators::generateStorageUnit_5);
        generatorMap.put("6", DisplayGroupGenerators::generateStorageUnit_6);
        generatorMap.put("7", DisplayGroupGenerators::generateStorageUnit_7);
        generatorMap.put("8", DisplayGroupGenerators::generateStorageUnit_8);
        generatorMap.put("9", DisplayGroupGenerators::generateStorageUnit_9);
        generatorMap.put("10", DisplayGroupGenerators::generateStorageUnit_10);
        generatorMap.put("11", DisplayGroupGenerators::generateStorageUnit_11);
        generatorMap.put("12", DisplayGroupGenerators::generateStorageUnit_12);
        generatorMap.put("13", DisplayGroupGenerators::generateStorageUnit_13);

        this.displayGroupGenerator = null;

        if (this.useSpecialModel) {
            String generatorKey = config.getString("items." + configKey + ".use-special-model.type");
            this.displayGroupGenerator = generatorMap.get(generatorKey);
            if (this.displayGroupGenerator == null) {
                Networks.getInstance()
                        .getLogger()
                        .warning(String.format(
                                Lang.getString("messages.unsupported-operation.display.unknown_type"), generatorKey));
                this.useSpecialModel = false;
            }
        }
    }

    public void onPlace(@NotNull BlockPlaceEvent e) {
        Location l = e.getBlock().getLocation();
        ItemStack itemInHand = e.getItemInHand();
        Player p = e.getPlayer();
        if (!(p.hasPermission("slimefun.inventory.bypass")
                || (canUse(p, false)
                        && Slimefun.getProtectionManager().hasPermission(p, e.getBlock(), Interaction.PLACE_BLOCK)))) {
            return;
        }
        boolean a = false;
        boolean b = false;
        UUID suuid = getServerUUID(itemInHand);
        if (Networks.getSupportedPluginManager().isGuguSlimefunLib()) {
            if (suuid != null && !p.isOp() && !suuid.equals(GuguSlimefunLib.getServerUUID())) {
                p.sendMessage(String.format(
                        Lang.getString("messages.unsupported-operation.drawer.wrong_server"),
                        suuid,
                        GuguSlimefunLib.getServerUUID()));
                e.setCancelled(true);
                if (useSpecialModel) {
                    removeDisplay(l);
                }
                Slimefun.getDatabaseManager().getBlockDataController().removeBlock(l);
                return;
            }
        }

        int id = getBoundId(itemInHand);
        if (id != -1) {
            StorageUnitData data = DataStorage.getCachedStorageData(id).orElse(null);
            if (data != null && data.isPlaced() && !l.equals(data.getLastLocation())) {
                // This container already exists and placed in another location
                p.sendMessage(Lang.getString("messages.unsupported-operation.drawer.already_exists"));
                Location currLoc = data.getLastLocation();
                p.sendMessage(TextUtil.YELLOW
                        + (currLoc.getWorld() == null
                                ? "Unknown"
                                : currLoc.getWorld().getName()) + " " + TextUtil.GRAY + "| " + TextUtil.YELLOW
                        + currLoc.getBlockX() + TextUtil.GRAY + "/" + TextUtil.YELLOW
                        + currLoc.getBlockY() + TextUtil.GRAY + "/" + TextUtil.YELLOW + currLoc.getBlockZ()
                        + TextUtil.GRAY + ";");
                e.setCancelled(true);
                if (useSpecialModel) {
                    removeDisplay(l);
                }
                Slimefun.getDatabaseManager().getBlockDataController().removeBlock(l);
                return;
            }
            // Request data
            Location lastLoc;
            if (data != null) {
                lastLoc = data.getLastLocation();
                a = locked.contains(lastLoc);
                if (!a) {
                    a = getLock(itemInHand);
                }
                b = voidExcesses.contains(lastLoc);
                if (!b) {
                    b = getVoidExcess(itemInHand);
                }
                if (a) {
                    locked.remove(lastLoc);
                }
                if (b) {
                    voidExcesses.remove(lastLoc);
                }
            }
            requestData(l, id);
            // This prevents creative player from getting too many same id item
            if (p.getGameMode() == GameMode.CREATIVE) {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
                p.getEquipment().setItem(e.getHand(), itemInHand);
            }
        } else {
            StorageUnitData data = DataStorage.createStorageUnitData(p, sizeType, l);
            id = data.getId();
            storages.put(l, data);
        }

        BlockMenu menu = StorageCacheUtils.getMenu(l);
        if (menu != null) {
            update(l, true);
            addClickHandler(l);
        }
        if (useSpecialModel) {
            e.getBlock().setType(Material.BARRIER);
            setupDisplay(e.getBlock().getLocation());
        }
        // Save to block storage
        addBlockInfo(l, id, a, b);
    }

    public void onBreak(@NotNull BlockBreakEvent e) {
        Block b = e.getBlock();
        Location l = b.getLocation();

        if (useSpecialModel) {
            removeDisplay(l);
        }

        // Fix display didn't remove when break
        e.setCancelled(true);
        BlockMenu menu = StorageCacheUtils.getMenu(l);
        if (menu != null) {
            menu.dropItems(l, QUANTUM_SLOT);
            menu.dropItems(l, ITEM_CHOOSE_SLOT);
        }

        // Remove data cache
        StorageUnitData data = storages.remove(l);
        b.setType(Material.AIR);
        // Drop custom item if data exists
        if (data != null) {
            data.setPlaced(false);
            int id = data.getId();
            b.getWorld().dropItemNaturally(l, bindIdNew(getItem(), id, isLocked(id), isVoidExcess(id)));
        } else {
            // Data not loaded, just drop with the stored one.
            int id = getContainerId(l);
            if (id != -1) {
                DataStorage.setContainerStatus(id, false);
                b.getWorld().dropItemNaturally(l, bindIdNew(getItem(), id, isLocked(id), isVoidExcess(id)));
            }
        }
        Slimefun.getDatabaseManager().getBlockDataController().removeBlock(l);
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return false;
            }

            @Override
            public void tick(@NotNull Block block, SlimefunItem item, SlimefunBlockData blockData) {
                onTick(block);
            }
        });

        addItemHandler(new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@NotNull BlockPlaceEvent blockPlaceEvent) {
                onPlace(blockPlaceEvent);
            }
        });

        addItemHandler(new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(
                    @NotNull BlockBreakEvent blockBreakEvent,
                    @NotNull ItemStack itemStack,
                    @NotNull List<ItemStack> list) {
                onBreak(blockBreakEvent);
            }
        });
    }

    private void onTick(@NotNull Block block) {
        Location l = block.getLocation();
        StorageUnitData data = storages.get(l);
        if (data == null) {
            requestData(l, getContainerId(l));
            return;
        }
        if (!isLocked(l)) {
            for (ItemContainer each : data.getStoredItems()) {
                if (each.getAmount() == 0) {
                    data.removeItem(each.getId());
                }
            }
        }
        BlockMenu menu = StorageCacheUtils.getMenu(l);
        if (menu == null) {
            return;
        }
        if (!l.equals(data.getLastLocation())) {
            ItemStack itemInBorder = menu.getItemInSlot(0);
            if (data.isPlaced() && itemInBorder != null) {
                menu.replaceExistingItem(STORAGE_INFO_SLOT, getLocationErrorItem(data.getId(), data.getLastLocation()));

                for (int slot : BORDER) {
                    menu.replaceExistingItem(slot, Icon.ERROR_BORDER);
                }
                return;
            }
            // Not placed, update state
            data.setPlaced(true);
            data.setLastLocation(l);
        }
        if (menu.hasViewer()) {
            // Update display item
            update(l, false);
        }
    }

    private @NotNull ItemStack getLocationErrorItem(int id, @Nullable Location lastLoc) {
        List<String> lore = new ArrayList<>(Lang.getStringList("icons.drawer.location_error.lore_before_info"));
        lore.add(String.format(Lang.getString("icons.drawer.location_error.id"), id));
        lore.add(
                lastLoc == null
                        ? Lang.getString("icons.drawer.location_error.unknown")
                        : String.format(
                                Lang.getString("icons.drawer.location_error.world"),
                                lastLoc.getWorld().getName()));
        lore.add(
                lastLoc == null
                        ? Lang.getString("icons.drawer.location_error.unknown")
                        : String.format(
                                Lang.getString("icons.drawer.location_error.location"),
                                lastLoc.getBlockX(),
                                lastLoc.getBlockY(),
                                lastLoc.getBlockZ()));
        return new CustomItemStack(Material.REDSTONE_TORCH, Lang.getString("icons.drawer.location_error.name"), lore);
    }

    private void switchLock(@NotNull BlockMenu menu, @NotNull Location l) {
        if (locked.contains(l)) {
            StorageCacheUtils.removeData(l, "locked");
            locked.remove(l);
            menu.replaceExistingItem(LOCK_MODE_SLOT, getContentLockItem(false));
        } else {
            StorageCacheUtils.setData(l, "locked", "enable");
            locked.add(l);
            menu.replaceExistingItem(LOCK_MODE_SLOT, getContentLockItem(true));
        }
    }

    private void switchVoidExcess(@NotNull BlockMenu menu, @NotNull Location l) {
        if (voidExcesses.contains(l)) {
            StorageCacheUtils.removeData(l, "voidExcess");
            voidExcesses.remove(l);
            menu.replaceExistingItem(VOID_MODE_SLOT, getVoidExcessItem(false));
        } else {
            StorageCacheUtils.setData(l, "voidExcess", "enable");
            voidExcesses.add(l);
            menu.replaceExistingItem(VOID_MODE_SLOT, getVoidExcessItem(true));
        }
    }

    private @NotNull ItemStack getContentLockItem(boolean locked) {
        List<String> lore = new ArrayList<>(Lang.getStringList("icons.drawer.content_lock.lore_before_status"));
        lore.add(String.format(
                Lang.getString("icons.drawer.content_lock.status"),
                locked
                        ? Lang.getString("icons.drawer.content_lock.locked")
                        : Lang.getString("icons.drawer.content_lock.unlocked")));
        lore.addAll(Lang.getStringList("icons.drawer.content_lock.lore_after_status"));
        lore.add(
                locked
                        ? Lang.getString("icons.drawer.click_to_disable")
                        : Lang.getString("icons.drawer.click_to_enable"));
        return new CustomItemStack(
                locked ? Material.RED_STAINED_GLASS_PANE : Material.LIME_STAINED_GLASS_PANE,
                Lang.getString("icons.drawer.content_lock.name"),
                lore);
    }

    private @NotNull ItemStack getVoidExcessItem(boolean voidExcess) {
        List<String> lore = new ArrayList<>(Lang.getStringList("icons.drawer.void_excess.lore_before_status"));
        lore.add(String.format(
                Lang.getString("icons.drawer.void_excess.status"),
                voidExcess
                        ? Lang.getString("icons.drawer.void_excess.enabled")
                        : Lang.getString("icons.drawer.void_excess.disabled")));
        lore.addAll(Lang.getStringList("icons.drawer.void_excess.lore_after_status"));
        lore.add(
                voidExcess
                        ? Lang.getString("icons.drawer.click_to_disable")
                        : Lang.getString("icons.drawer.click_to_enable"));
        return new CustomItemStack(
                voidExcess ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE,
                Lang.getString("icons.drawer.void_excess.name"),
                lore);
    }

    private void setupDisplay(@NotNull Location location) {
        if (this.displayGroupGenerator != null) {
            DisplayGroup displayGroup =
                    this.displayGroupGenerator.apply(location.clone().add(0.5, 0, 0.5));
            StorageCacheUtils.setData(
                    location, KEY_UUID, displayGroup.getParentUUID().toString());
        }
    }

    private void removeDisplay(@NotNull Location location) {
        DisplayGroup group = getDisplayGroup(location);
        if (group != null) {
            group.remove();
        }
    }

    @Nullable private UUID getDisplayGroupUUID(@NotNull Location location) {
        String uuid = StorageCacheUtils.getData(location, KEY_UUID);
        if (uuid == null) {
            return null;
        }
        return UUID.fromString(uuid);
    }

    @Nullable private DisplayGroup getDisplayGroup(@NotNull Location location) {
        UUID uuid = getDisplayGroupUUID(location);
        if (uuid == null) {
            return null;
        }
        return DisplayGroup.fromUUID(uuid);
    }

    @Override
    public boolean canStack(@NotNull ItemMeta meta1, @NotNull ItemMeta meta2) {
        return meta1.getPersistentDataContainer().equals(meta2.getPersistentDataContainer());
    }
}
