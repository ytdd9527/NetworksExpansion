package com.ytdd9527.networksexpansion.implementation.machines.managers;

import com.balugaq.netex.api.algorithm.Sorters;
import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.LocationUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractAdvancedAutoCrafter;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractAutoCrafter;
import com.ytdd9527.networksexpansion.core.items.unusable.AbstractBlueprint;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import com.ytdd9527.networksexpansion.utils.ParticleUtil;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.BlueprintInstance;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.sefiraat.networks.utils.datatypes.PersistentCraftingBlueprintType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class CrafterManager extends NetworkObject {
    private static final Map<Location, GridCache> CACHE_MAP = new HashMap<>();
    private static final int[] BACKGROUND_SLOTS = new int[] {8, 17, 26, 35};
    private static final int[] DISPLAY_SLOTS = {
        0, 1, 2, 3, 4, 5, 6, 7,
        9, 10, 11, 12, 13, 14, 15, 16,
        18, 19, 20, 21, 22, 23, 24, 25,
        27, 28, 29, 30, 31, 32, 33, 34,
        36, 37, 38, 39, 40, 41, 42, 43,
        45, 46, 47, 48, 49, 50, 51, 52,
    };
    private static final int PAGE_PREVIOUS = 44;
    private static final int PAGE_NEXT = 53;
    private static final String BS_TOP = "netex-top";
    private static final String BS_NAME = "netex-name";
    private static final String BS_ICON = "netex-icon";
    private static final String BS_TOP_1B = "1";
    private static final String BS_TOP_0B = "0";
    private static final String NAMESPACE_SF = "sf";
    private static final String NAMESPACE_MC = "mc";

    private final @NotNull IntRangeSetting tickRate;

    public CrafterManager(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.CRAFTER_MANAGER);

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
    }

    public static @Nullable String getCrafterName(@NotNull Location crafterLocation) {
        return StorageCacheUtils.getData(crafterLocation, BS_NAME);
    }

    public static void setCrafterIcon(
            @NotNull Player player, @NotNull Location crafterLocation, @NotNull ItemStack cursor) {
        StorageCacheUtils.setData(crafterLocation, BS_ICON, serializeIcon(cursor));
        player.sendMessage(Lang.getString("messages.completed-operation.manager.set_icon"));
    }

    public static @Nullable ItemStack getCrafterIcon(@NotNull Location crafterLocation) {
        String icon = StorageCacheUtils.getData(crafterLocation, BS_ICON);
        if (icon == null) {
            return null;
        }
        return deserializeIcon(icon);
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

    public static void topOrUntopCrafter(@NotNull Player player, @NotNull Location crafterLocation) {
        if (Objects.equals(StorageCacheUtils.getData(crafterLocation, BS_TOP), BS_TOP_1B)) {
            StorageCacheUtils.setData(crafterLocation, BS_TOP, BS_TOP_0B);
            player.sendMessage(Lang.getString("messages.completed-operation.manager.top_storage_off"));
        } else {
            StorageCacheUtils.setData(crafterLocation, BS_TOP, BS_TOP_1B);
            player.sendMessage(Lang.getString("messages.completed-operation.manager.top_storage_on"));
        }
    }

    public static boolean isTopCrafter(@NotNull Location crafterLocation) {
        String top = StorageCacheUtils.getData(crafterLocation, BS_TOP);
        return top != null && top.equals(BS_TOP_1B);
    }

    public static void highlightBlock(@NotNull Player player, @NotNull Location crafterLocation) {
        ParticleUtil.drawLineFrom(player.getEyeLocation().clone().add(0D, -0.5D, 0D), crafterLocation);
        ParticleUtil.highlightBlock(crafterLocation);
    }

    public static @NotNull ItemStack getItemStack(@NotNull CrafterMetaData data) {
        ItemStack raw = null;
        if (data.instance() != null) {
            raw = data.instance().getItemStack();
        }
        if (raw == null) {
            return Icon.BLANK_SLOT_STACK;
        }

        return StackUtils.getAsQuantity(raw, Math.min(raw.getAmount() * data.blueprintAmount(), raw.getMaxStackSize()));
    }

    public void tryInsertBlueprint(
            @NotNull BlockMenu managerMenu, @NotNull ItemStack blueprint, @NotNull BlueprintInstance instance) {
        Location location = managerMenu.getLocation();
        final GridCache gridCache = getCacheMap().get(location);
        NodeDefinition definition = NetworkStorage.getNode(location);
        if (definition == null || definition.getNode() == null) {
            return;
        }

        NetworkRoot root = definition.getNode().getRoot();
        List<CrafterMetaData> datas = CrafterMetaData.getMetaDatas(root);

        final int pages = (int) Math.ceil(datas.size() / (double) getDisplaySlots().length) - 1;

        gridCache.setMaxPages(pages);

        // Set everything to blank and return if there are no pages (no items)
        if (pages < 0) {
            clearDisplay(managerMenu);
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
        final int end = Math.min(start + getDisplaySlots().length, datas.size());

        datas = datas.stream()
                .sorted((a, b) -> isTopCrafter(a.location()) ? -1 : isTopCrafter(b.location()) ? 1 : 0)
                .toList();

        final List<CrafterMetaData> validdatas = datas.subList(start, end);
        CrafterMetaData empty = null;
        for (CrafterMetaData data : validdatas) {
            if (data.instance() != null) {
                ItemStack output = data.instance().getItemStack();
                if (output == null) {
                    continue;
                }
                if (StackUtils.itemsMatch(output, instance.getItemStack(), true, false)) {
                    SlimefunBlockData blockData = StorageCacheUtils.getBlock(data.location());
                    if (blockData == null) {
                        continue;
                    }
                    BlockMenu crafterMenu = blockData.getBlockMenu();
                    if (crafterMenu == null) {
                        continue;
                    }
                    SlimefunItem sf = SlimefunItem.getById(blockData.getSfId());
                    ItemStack existing = crafterMenu.getItemInSlot(AbstractAutoCrafter.BLUEPRINT_SLOT);
                    if (sf instanceof AbstractAutoCrafter && (existing == null || existing.getType() == Material.AIR)) {
                        tryInsertBlueprint(crafterMenu, blueprint, 1);
                        return;
                    } else if (sf instanceof AbstractAdvancedAutoCrafter
                            && (existing == null
                                    || existing.getType() == Material.AIR
                                    || existing.getAmount() < output.getMaxStackSize() / output.getAmount())) {
                        tryInsertBlueprint(
                                crafterMenu,
                                blueprint,
                                Math.min(output.getMaxStackSize() / output.getAmount(), blueprint.getAmount()));
                        return;
                    }
                }
            } else if (empty == null) {
                empty = data;
            }
        }

        if (empty != null) {
            SlimefunBlockData blockData = StorageCacheUtils.getBlock(empty.location());
            if (blockData == null) {
                return;
            }
            BlockMenu crafterMenu = blockData.getBlockMenu();
            if (crafterMenu == null) {
                return;
            }

            SlimefunItem sf = SlimefunItem.getById(blockData.getSfId());
            if (sf instanceof AbstractAutoCrafter) {
                tryInsertBlueprint(crafterMenu, blueprint, 1);
            } else if (sf instanceof AbstractAdvancedAutoCrafter) {
                BlueprintInstance instance2;

                final ItemMeta blueprintMeta = blueprint.getItemMeta();
                Optional<BlueprintInstance> optional;
                optional = DataTypeMethods.getOptionalCustom(
                        blueprintMeta, Keys.BLUEPRINT_INSTANCE, PersistentCraftingBlueprintType.TYPE);

                if (optional.isEmpty()) {
                    optional = DataTypeMethods.getOptionalCustom(
                            blueprintMeta, Keys.BLUEPRINT_INSTANCE2, PersistentCraftingBlueprintType.TYPE);
                }

                if (optional.isEmpty()) {
                    optional = DataTypeMethods.getOptionalCustom(
                            blueprintMeta, Keys.BLUEPRINT_INSTANCE3, PersistentCraftingBlueprintType.TYPE);
                }

                instance2 = optional.orElse(null);

                if (instance2 != null) {
                    ItemStack output = instance.getItemStack();
                    if (output != null) {
                        tryInsertBlueprint(
                                crafterMenu,
                                blueprint,
                                Math.min(output.getMaxStackSize() / output.getAmount(), blueprint.getAmount()));
                    } else {
                        tryInsertBlueprint(crafterMenu, blueprint, blueprint.getAmount());
                    }
                } else {
                    tryInsertBlueprint(crafterMenu, blueprint, blueprint.getAmount());
                }
            }
        }
    }

    public void tryInsertBlueprint(@NotNull BlockMenu crafterMenu, @NotNull ItemStack blueprint, int maxAmount) {
        ItemStack existingBlueprint = crafterMenu.getItemInSlot(AbstractAutoCrafter.BLUEPRINT_SLOT);
        int v;
        if (existingBlueprint == null || existingBlueprint.getType() == Material.AIR) {
            v = Math.min(maxAmount, blueprint.getAmount());
            crafterMenu.replaceExistingItem(AbstractAutoCrafter.BLUEPRINT_SLOT, StackUtils.getAsQuantity(blueprint, v));
        } else {
            v = Math.max(
                    0,
                    Math.min(
                            existingBlueprint.getMaxStackSize() - existingBlueprint.getAmount(),
                            Math.min(maxAmount, blueprint.getAmount())));
            existingBlueprint.setAmount(existingBlueprint.getAmount() + v);
        }

        blueprint.setAmount(blueprint.getAmount() - v);
    }

    @SuppressWarnings("deprecation")
    public void updateDisplay(@Nullable BlockMenu managerMenu) {
        if (managerMenu == null) {
            return;
        }

        if (!managerMenu.hasViewer()) {
            sendFeedback(managerMenu.getLocation(), FeedbackType.AFK);
            return;
        }

        Location location = managerMenu.getLocation();
        NodeDefinition definition = NetworkStorage.getNode(location);
        if (definition == null || definition.getNode() == null) {
            sendFeedback(managerMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        final GridCache gridCache = getCacheMap().get(location);
        NetworkRoot root = definition.getNode().getRoot();
        List<CrafterMetaData> datas = CrafterMetaData.getMetaDatas(root);

        final int pages = (int) Math.ceil(datas.size() / (double) getDisplaySlots().length) - 1;

        gridCache.setMaxPages(pages);

        // Set everything to blank and return if there are no pages (no items)
        if (pages < 0) {
            clearDisplay(managerMenu);
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
        final int end = Math.min(start + getDisplaySlots().length, datas.size());

        datas = datas.stream()
                .sorted((a, b) -> isTopCrafter(a.location()) ? -1 : isTopCrafter(b.location()) ? 1 : 0)
                .toList();

        final List<CrafterMetaData> validdatas = datas.subList(start, end);

        getCacheMap().put(managerMenu.getLocation(), gridCache);

        for (int i = 0; i < getDisplaySlots().length; i++) {
            if (validdatas.size() > i) {
                final CrafterMetaData data = validdatas.get(i);
                final ItemStack rawDisplayStack = getItemStack(data);
                boolean isEmpty = false;
                ItemStack displayStack = null;
                if (rawDisplayStack == null || rawDisplayStack.getType() == Material.AIR) {
                    displayStack = new ItemStack(Material.BARRIER);
                    isEmpty = true;
                }

                Location crafterLocation = data.location();

                final ItemStack custom = getCrafterIcon(crafterLocation);
                if (custom != null) {
                    displayStack = custom;
                } else if (displayStack == null) {
                    displayStack = rawDisplayStack.clone();
                }

                String name = getCrafterName(crafterLocation);
                if (name != null) {
                    displayStack = new CustomItemStack(displayStack, TextUtil.color(name));
                } else if (!isEmpty) {
                    displayStack = new CustomItemStack(
                            displayStack, TextUtil.GRAY + ItemStackHelper.getDisplayName(rawDisplayStack));
                } else {
                    displayStack = new CustomItemStack(displayStack, Sorters.NO_ITEM);
                }

                final ItemMeta itemMeta = displayStack.getItemMeta();
                if (itemMeta == null) {
                    continue;
                }

                List<String> lore = getLoreAddition(data);

                itemMeta.setLore(lore);
                displayStack.setItemMeta(itemMeta);
                managerMenu.replaceExistingItem(getDisplaySlots()[i], displayStack);
                managerMenu.addMenuClickHandler(getDisplaySlots()[i], (player, slot, item, action) -> {
                    handleClick(root, managerMenu, crafterLocation, player, slot, item, action);
                    return false;
                });
            } else {
                managerMenu.replaceExistingItem(getDisplaySlots()[i], getBlankSlotStack());
                managerMenu.addMenuClickHandler(getDisplaySlots()[i], (p, slot, item, action) -> false);
            }
        }

        managerMenu.replaceExistingItem(
                getPagePrevious(),
                Icon.getPageStack(getPagePreviousStack(), gridCache.getPage() + 1, gridCache.getMaxPages() + 1));
        managerMenu.replaceExistingItem(
                getPageNext(),
                Icon.getPageStack(getPageNextStack(), gridCache.getPage() + 1, gridCache.getMaxPages() + 1));

        sendFeedback(managerMenu.getLocation(), FeedbackType.WORKING);
    }

    public @NotNull List<String> getLoreAddition(@NotNull CrafterMetaData data) {
        Location loc = data.location();
        List<String> list = new ArrayList<>();
        list.add("");
        list.add(String.format(
                Lang.getString("messages.normal-operation.manager.location"),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ()));
        list.add("");
        list.addAll(Lang.getStringList("messages.normal-operation.manager.crafter-manager-click-behavior"));

        return list;
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
                        || (ExpansionItems.CRAFTER_MANAGER.canUse(player, false)
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

                for (int displaySlot : getDisplaySlots()) {
                    menu.replaceExistingItem(displaySlot, ChestMenuUtils.getBackground());
                    menu.addMenuClickHandler(displaySlot, (p, slot, item, action) -> false);
                }

                menu.addPlayerInventoryClickHandler((p, s, blueprint, a) -> {
                    if (!a.isShiftClicked()) {
                        return true;
                    }

                    if (blueprint == null || blueprint.getType() == Material.AIR) {
                        return true;
                    }

                    final ItemMeta blueprintMeta = blueprint.getItemMeta();
                    Optional<BlueprintInstance> optional;
                    optional = DataTypeMethods.getOptionalCustom(
                            blueprintMeta, Keys.BLUEPRINT_INSTANCE, PersistentCraftingBlueprintType.TYPE);

                    if (optional.isEmpty()) {
                        optional = DataTypeMethods.getOptionalCustom(
                                blueprintMeta, Keys.BLUEPRINT_INSTANCE2, PersistentCraftingBlueprintType.TYPE);
                    }

                    if (optional.isEmpty()) {
                        optional = DataTypeMethods.getOptionalCustom(
                                blueprintMeta, Keys.BLUEPRINT_INSTANCE3, PersistentCraftingBlueprintType.TYPE);
                    }

                    BlueprintInstance instance = optional.orElse(null);
                    if (instance == null) {
                        return true;
                    }

                    tryInsertBlueprint(menu, blueprint, instance);
                    return false;
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

    protected @NotNull ItemStack getBlankSlotStack() {
        return Icon.DARK_BLANK_SLOT_STACK;
    }

    protected @NotNull ItemStack getPagePreviousStack() {
        return Icon.PAGE_PREVIOUS_STACK;
    }

    protected @NotNull ItemStack getPageNextStack() {
        return Icon.PAGE_NEXT_STACK;
    }

    protected void clearDisplay(@NotNull BlockMenu blockMenu) {
        for (int displaySlot : getDisplaySlots()) {
            blockMenu.replaceExistingItem(displaySlot, getBlankSlotStack());
            blockMenu.addMenuClickHandler(displaySlot, (p, slot, item, action) -> false);
        }
    }

    public void swapItem(@NotNull BlockMenu crafterMenu, @NotNull Player player) {
        ItemStack cursor = player.getItemOnCursor();
        ItemStack existing = crafterMenu.getItemInSlot(AbstractAutoCrafter.BLUEPRINT_SLOT);
        existing = existing == null ? null : existing.clone();
        crafterMenu.replaceExistingItem(AbstractAutoCrafter.BLUEPRINT_SLOT, cursor);
        player.setItemOnCursor(existing);

        AbstractAutoCrafter.updateCache(crafterMenu);
    }

    public void setCrafterName(
            @NotNull BlockMenu managerMenu, @NotNull Player player, @NotNull Location crafterLocation) {
        player.sendMessage(Lang.getString("messages.normal-operation.manager.set_name"));
        player.closeInventory();
        ChatUtils.awaitInput(player, s -> {
            StorageCacheUtils.setData(crafterLocation, BS_NAME, s);
            player.sendMessage(Lang.getString("messages.completed-operation.manager.set_name"));

            SlimefunBlockData data = StorageCacheUtils.getBlock(managerMenu.getLocation());
            if (data == null) {
                return;
            }

            if (managerMenu.getPreset().getID().equals(data.getSfId())) {
                BlockMenu actualMenu = data.getBlockMenu();
                if (actualMenu != null) {
                    updateDisplay(actualMenu);
                    actualMenu.open(player);
                }
            }
        });
    }

    @SuppressWarnings({"deprecation", "unused"})
    @ParametersAreNonnullByDefault
    public void handleClick(
            NetworkRoot root,
            BlockMenu managerMenu,
            Location crafterLocation,
            Player player,
            int clickedSlot,
            ItemStack clickedItemStack,
            ClickAction clickAction) {
        BlockMenu crafterMenu = StorageCacheUtils.getMenu(crafterLocation);
        if (crafterMenu == null) {
            return;
        }

        CrafterMetaData data = CrafterMetaData.getMetaData(root, crafterMenu);
        ItemStack blueprint = crafterMenu.getItemInSlot(AbstractAutoCrafter.BLUEPRINT_SLOT);
        ItemStack cursor = player.getItemOnCursor();

        if (!clickAction.isRightClicked()) {
            if (clickAction.isShiftClicked()) {
                topOrUntopCrafter(player, crafterLocation);
            } else {
                if (blueprint == null) {
                    if (SlimefunItem.getByItem(cursor) instanceof AbstractBlueprint) {
                        swapItem(crafterMenu, player);
                    }
                } else {
                    swapItem(crafterMenu, player);
                }
            }
        } else {
            if (clickAction.isShiftClicked()) {
                if (cursor == null || cursor.getType() == Material.AIR) {
                    setCrafterName(managerMenu, player, data.location());
                } else {
                    setCrafterIcon(player, data.location(), cursor);
                }
            } else {
                highlightBlock(player, data.location());
            }
        }
    }

    public record CrafterMetaData(
            @NotNull Location location,
            @Nullable BlueprintInstance instance,
            @Range(from = 0, to = 64) int blueprintAmount) {
        @NotNull public static List<CrafterMetaData> getMetaDatas(@NotNull NetworkRoot root) {
            List<CrafterMetaData> metaDataList = new ArrayList<>();
            for (BlockMenu blockMenu : root.getCrafterOutputs()) {
                metaDataList.add(getMetaData(root, blockMenu));
            }
            metaDataList = metaDataList.stream()
                    .sorted(Comparator.comparingLong(d -> LocationUtil.toStableHash(d.location())))
                    .toList();

            return metaDataList;
        }

        @SuppressWarnings("unused")
        @NotNull @ParametersAreNonnullByDefault
        public static CrafterMetaData getMetaData(NetworkRoot root, BlockMenu crafterMenu) {
            Location location = crafterMenu.getLocation();
            ItemStack blueprint = crafterMenu.getItemInSlot(AbstractAutoCrafter.BLUEPRINT_SLOT);
            if (blueprint == null || blueprint.getType() == Material.AIR) {
                return new CrafterMetaData(location, null, 0);
            }

            BlueprintInstance instance = AbstractAutoCrafter.INSTANCE_MAP.get(crafterMenu.getLocation());

            if (instance == null) {
                final ItemMeta blueprintMeta = blueprint.getItemMeta();
                Optional<BlueprintInstance> optional;
                optional = DataTypeMethods.getOptionalCustom(
                        blueprintMeta, Keys.BLUEPRINT_INSTANCE, PersistentCraftingBlueprintType.TYPE);

                if (optional.isEmpty()) {
                    optional = DataTypeMethods.getOptionalCustom(
                            blueprintMeta, Keys.BLUEPRINT_INSTANCE2, PersistentCraftingBlueprintType.TYPE);
                }

                if (optional.isEmpty()) {
                    optional = DataTypeMethods.getOptionalCustom(
                            blueprintMeta, Keys.BLUEPRINT_INSTANCE3, PersistentCraftingBlueprintType.TYPE);
                }

                instance = optional.orElse(null);
            }

            if (instance != null) {
                SlimefunItem sf = StorageCacheUtils.getSfItem(location);
                if (sf instanceof AbstractAdvancedAutoCrafter) {
                    return new CrafterMetaData(location, instance, blueprint.getAmount());
                } else {
                    return new CrafterMetaData(location, instance, 1);
                }
            } else {
                return new CrafterMetaData(location, null, 0);
            }
        }
    }
}
