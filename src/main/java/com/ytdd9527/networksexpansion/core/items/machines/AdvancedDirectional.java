package com.ytdd9527.networksexpansion.core.items.machines;

import com.balugaq.netex.api.algorithm.Calculator;
import com.balugaq.netex.api.enums.TransportMode;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.NetworksVersionedEnchantment;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.sefiraat.networks.utils.NetworkUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import net.guizhanss.guizhanlib.minecraft.helper.MaterialHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("DuplicatedCode")
public abstract class AdvancedDirectional extends NetworkDirectional {
    public static final @NotNull TransportMode DEFAULT_TRANSPORT_MODE = TransportMode.FIRST_STOP;
    protected static final String DIRECTION = "direction";
    protected static final String OWNER_KEY = "uuid";
    protected static final String LIMIT_KEY = "transport_limit";
    protected static final String TRANSPORT_MODE_KEY = "transport_mode";
    private static final int NORTH_SLOT = 12;
    private static final int SOUTH_SLOT = 30;
    private static final int EAST_SLOT = 22;
    private static final int WEST_SLOT = 20;
    private static final int UP_SLOT = 15;
    private static final int DOWN_SLOT = 33;
    private static final Set<BlockFace> VALID_FACES =
        EnumSet.of(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    private static final Map<Location, BlockFace> SELECTED_DIRECTION_MAP = new HashMap<>();
    protected static final Map<Location, Integer> NETWORK_LIMIT_QUANTITY_MAP = new HashMap<>();
    private static final Map<Location, TransportMode> NETWORK_TRANSPORT_MODE_MAP = new HashMap<>();
    final NetworkDirectional instance = this;
    private final @NotNull ItemStack CARGO_NUMBER_ICON_CLONE;
    private final @NotNull ItemStack TRANSPORT_MODE_ICON_CLONE;

    protected AdvancedDirectional(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe,
        NodeType type) {
        this(itemGroup, item, recipeType, recipe, 1, type);
    }

    protected AdvancedDirectional(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe,
        @Range(from = 1, to = 64) int outputAmount,
        NodeType type) {
        super(itemGroup, item, recipeType, recipe, type);
        this.CARGO_NUMBER_ICON_CLONE = Icon.SHOW_ICON.clone();
        this.TRANSPORT_MODE_ICON_CLONE = Icon.TRANSPORT_MODE_ICON.clone();

        addItemHandler(new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return runSync();
            }

            @Override
            public void tick(@NotNull Block block, SlimefunItem slimefunItem, @NotNull SlimefunBlockData data) {
                onTick(data.getBlockMenu(), block);
            }

            @Override
            public void uniqueTick() {
                onUniqueTick();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @NotNull
    public static ItemStack getDirectionalSlotPane(
        @NotNull BlockFace blockFace, @NotNull SlimefunItem slimefunItem, boolean active) {
        final ItemStack displayStack = ItemStackUtil.getCleanItem(new CustomItemStack(
            slimefunItem.getItem(),
            String.format(
                Lang.getString("messages.normal-operation.directional.display_name"),
                blockFace.name(),
                TextUtil.stripColor(slimefunItem.getItemName()))));
        final ItemMeta itemMeta = displayStack.getItemMeta();
        itemMeta.setLore(Lang.getStringList("messages.normal-operation.directional.display_lore"));
        if (active) {
            List<String> lore = itemMeta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(Lang.getString("messages.normal-operation.directional.set_facing"));
            itemMeta.setLore(lore);
            itemMeta.addEnchant(NetworksVersionedEnchantment.LUCK_OF_THE_SEA, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        displayStack.setItemMeta(itemMeta);
        return displayStack;
    }

    @SuppressWarnings("deprecation")
    @NotNull
    public static ItemStack getDirectionalSlotPane(
        @NotNull BlockFace blockFace, @NotNull Material blockMaterial, boolean active) {
        if (blockMaterial.isItem() && blockMaterial != Material.AIR) {
            final ItemStack displayStack = new CustomItemStack(
                blockMaterial,
                String.format(
                    Lang.getString("messages.normal-operation.directional.display_name"),
                    blockFace.name(),
                    MaterialHelper.getName(blockMaterial)));
            final ItemMeta itemMeta = displayStack.getItemMeta();
            itemMeta.setLore(Lang.getStringList("messages.normal-operation.directional.display_lore"));
            if (active) {
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                lore.add(Lang.getString("messages.normal-operation.directional.set_facing"));
                itemMeta.setLore(lore);
                itemMeta.addEnchant(NetworksVersionedEnchantment.LUCK_OF_THE_SEA, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            displayStack.setItemMeta(itemMeta);
            return displayStack;
        } else {
            Material material = active ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
            return ItemStackUtil.getCleanItem(new CustomItemStack(
                material,
                String.format(Lang.getString("messages.normal-operation.directional.display_empty"), blockFace)));
        }
    }

    @Nullable
    public static BlockFace getSelectedFace(@NotNull Location location) {
        return SELECTED_DIRECTION_MAP.get(location);
    }

    @Override
    public void updateGui(@Nullable BlockMenu blockMenu) {
        if (blockMenu == null || !blockMenu.hasViewer()) {
            return;
        }

        BlockFace direction = getCurrentDirection(blockMenu);

        for (BlockFace blockFace : VALID_FACES) {
            final Block block = blockMenu.getBlock().getRelative(blockFace);
            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(block.getLocation());
            if (slimefunItem != null) {
                switch (blockFace) {
                    case NORTH -> blockMenu.replaceExistingItem(
                        getNorthSlot(), getDirectionalSlotPane(blockFace, slimefunItem, blockFace == direction));
                    case SOUTH -> blockMenu.replaceExistingItem(
                        getSouthSlot(), getDirectionalSlotPane(blockFace, slimefunItem, blockFace == direction));
                    case EAST -> blockMenu.replaceExistingItem(
                        getEastSlot(), getDirectionalSlotPane(blockFace, slimefunItem, blockFace == direction));
                    case WEST -> blockMenu.replaceExistingItem(
                        getWestSlot(), getDirectionalSlotPane(blockFace, slimefunItem, blockFace == direction));
                    case UP -> blockMenu.replaceExistingItem(
                        getUpSlot(), getDirectionalSlotPane(blockFace, slimefunItem, blockFace == direction));
                    case DOWN -> blockMenu.replaceExistingItem(
                        getDownSlot(), getDirectionalSlotPane(blockFace, slimefunItem, blockFace == direction));
                    default -> throw new IllegalStateException(String.format(
                        Lang.getString("messages.unsupported-operation.directional.unexcepted_value"), blockFace));
                }
            } else {
                final Material material = block.getType();
                switch (blockFace) {
                    case NORTH -> blockMenu.replaceExistingItem(
                        getNorthSlot(), getDirectionalSlotPane(blockFace, material, blockFace == direction));
                    case SOUTH -> blockMenu.replaceExistingItem(
                        getSouthSlot(), getDirectionalSlotPane(blockFace, material, blockFace == direction));
                    case EAST -> blockMenu.replaceExistingItem(
                        getEastSlot(), getDirectionalSlotPane(blockFace, material, blockFace == direction));
                    case WEST -> blockMenu.replaceExistingItem(
                        getWestSlot(), getDirectionalSlotPane(blockFace, material, blockFace == direction));
                    case UP -> blockMenu.replaceExistingItem(
                        getUpSlot(), getDirectionalSlotPane(blockFace, material, blockFace == direction));
                    case DOWN -> blockMenu.replaceExistingItem(
                        getDownSlot(), getDirectionalSlotPane(blockFace, material, blockFace == direction));
                    default -> throw new IllegalStateException(String.format(
                        Lang.getString("messages.unsupported-operation.directional.unexcepted_value"), blockFace));
                }
            }
        }
    }

    @NotNull
    protected BlockFace getCurrentDirection(@NotNull BlockMenu blockMenu) {
        BlockFace direction = SELECTED_DIRECTION_MAP.get(blockMenu.getLocation().clone());

        if (direction == null) {
            direction = BlockFace.valueOf(StorageCacheUtils.getData(blockMenu.getLocation(), DIRECTION));
            SELECTED_DIRECTION_MAP.put(blockMenu.getLocation().clone(), direction);
        }
        return direction;
    }

    protected void onUniqueTick() {
        super.onUniqueTick();
    }

    @Override
    public void onPlace(@NotNull BlockPlaceEvent event) {
        NetworkStorage.removeNode(event.getBlock().getLocation());
        SlimefunBlockData blockData =
            StorageCacheUtils.getBlock(event.getBlock().getLocation());
        if (blockData == null) {
            return;
        }
        blockData.setData(OWNER_KEY, event.getPlayer().getUniqueId().toString());
        blockData.setData(DIRECTION, BlockFace.SELF.name());
        blockData.setData(LIMIT_KEY, String.valueOf(getMaxLimit()));
        blockData.setData(TRANSPORT_MODE_KEY, String.valueOf(TransportMode.FIRST_STOP));
        BlockMenu menu = blockData.getBlockMenu();
        if (menu != null) {
            NetworkUtils.applyConfig(instance, menu, event.getPlayer());
        }
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                drawBackground(getBackgroundSlots());

                if (getOtherBackgroundSlots() != null && getOtherBackgroundStack() != null) {
                    drawBackground(getOtherBackgroundStack(), getOtherBackgroundSlots());
                }

                addItem(
                    getNorthSlot(),
                    getDirectionalSlotPane(BlockFace.NORTH, Material.AIR, false),
                    (player, i, itemStack, clickAction) -> false);
                addItem(
                    getSouthSlot(),
                    getDirectionalSlotPane(BlockFace.SOUTH, Material.AIR, false),
                    (player, i, itemStack, clickAction) -> false);
                addItem(
                    getEastSlot(),
                    getDirectionalSlotPane(BlockFace.EAST, Material.AIR, false),
                    (player, i, itemStack, clickAction) -> false);
                addItem(
                    getWestSlot(),
                    getDirectionalSlotPane(BlockFace.WEST, Material.AIR, false),
                    (player, i, itemStack, clickAction) -> false);
                addItem(
                    getUpSlot(),
                    getDirectionalSlotPane(BlockFace.UP, Material.AIR, false),
                    (player, i, itemStack, clickAction) -> false);
                addItem(
                    getDownSlot(),
                    getDirectionalSlotPane(BlockFace.DOWN, Material.AIR, false),
                    (player, i, itemStack, clickAction) -> false);
                if (getAddSlot() != -1) {
                    addItem(getAddSlot(), getAddIcon(), (p, i, itemStack, clickAction) -> false);
                }
                if (getMinusSlot() != -1) {
                    addItem(getMinusSlot(), getMinusIcon(), (p, i, itemStack, clickAction) -> false);
                }
                if (getCargoNumberSlot() != -1) {
                    addItem(getCargoNumberSlot(), getCargoNumberIcon(), (p, i, itemStack, clickAction) -> false);
                }
                if (getTransportModeSlot() != -1) {
                    addItem(getTransportModeSlot(), getTransportModeIcon(), (p, i, itemStack, clickAction) -> false);
                }
            }

            @Override
            public void newInstance(@NotNull BlockMenu blockMenu, @NotNull Block b) {
                final BlockFace direction;
                final Location location = blockMenu.getLocation();
                final String string = StorageCacheUtils.getData(location, DIRECTION);
                final String rawLimit = StorageCacheUtils.getData(location, LIMIT_KEY);
                final String rawMode = StorageCacheUtils.getData(location, TRANSPORT_MODE_KEY);

                if (string == null) {
                    // This likely means a block was placed before I made it directional
                    direction = BlockFace.SELF;
                    StorageCacheUtils.setData(location, DIRECTION, BlockFace.SELF.name());
                } else {
                    direction = BlockFace.valueOf(string);
                }
                SELECTED_DIRECTION_MAP.put(location.clone(), direction);

                int limit;
                if (rawLimit == null) {
                    limit = getMaxLimit();
                } else {
                    limit = Integer.parseInt(rawLimit);
                }
                NETWORK_LIMIT_QUANTITY_MAP.put(location.clone(), limit);

                TransportMode mode;
                if (rawMode == null) {
                    mode = TransportMode.FIRST_STOP;
                    StorageCacheUtils.setData(location, TRANSPORT_MODE_KEY, String.valueOf(mode));
                } else {
                    mode = TransportMode.valueOf(rawMode);
                }
                NETWORK_TRANSPORT_MODE_MAP.put(location.clone(), mode);

                blockMenu.addMenuClickHandler(
                    getNorthSlot(),
                    (player, i, itemStack, clickAction) ->
                        directionClick(player, clickAction, blockMenu, BlockFace.NORTH));
                blockMenu.addMenuClickHandler(
                    getSouthSlot(),
                    (player, i, itemStack, clickAction) ->
                        directionClick(player, clickAction, blockMenu, BlockFace.SOUTH));
                blockMenu.addMenuClickHandler(
                    getEastSlot(),
                    (player, i, itemStack, clickAction) ->
                        directionClick(player, clickAction, blockMenu, BlockFace.EAST));
                blockMenu.addMenuClickHandler(
                    getWestSlot(),
                    (player, i, itemStack, clickAction) ->
                        directionClick(player, clickAction, blockMenu, BlockFace.WEST));
                blockMenu.addMenuClickHandler(
                    getUpSlot(),
                    (player, i, itemStack, clickAction) ->
                        directionClick(player, clickAction, blockMenu, BlockFace.UP));
                blockMenu.addMenuClickHandler(
                    getDownSlot(),
                    (player, i, itemStack, clickAction) ->
                        directionClick(player, clickAction, blockMenu, BlockFace.DOWN));

                if (getCargoNumberSlot() != -1) {
                    blockMenu.addMenuClickHandler(getCargoNumberSlot(), (player, i, itemStack, clickAction) -> {
                        player.sendMessage(ChatColors.color("&e输入数量"));
                        ChatUtils.awaitInput(player, input -> {
                            try {
                                int value = Calculator.calculate(input).intValue();
                                if (value <= 0 || value > getMaxLimit()) {
                                    player.sendMessage("请输入 1 ~ " + getMaxLimit() + " 之间的正整数");
                                    BlockMenu menu = StorageCacheUtils.getMenu(location);
                                    if (menu != null) menu.open(player);
                                    return;
                                }

                                setLimitQuantity(location, value);
                                updateShowIcon(location);
                                BlockMenu menu = StorageCacheUtils.getMenu(location);
                                if (menu != null) menu.open(player);
                            } catch (NumberFormatException e) {
                                player.sendMessage(e.getMessage());
                            }
                        });
                        return false;
                    });
                }

                if (getAddSlot() != -1) {
                    blockMenu.addMenuClickHandler(getAddSlot(), (p, slot, item, action) -> addClick(location, action));
                }
                if (getMinusSlot() != -1) {
                    blockMenu.addMenuClickHandler(
                        getMinusSlot(), (p, slot, item, action) -> minusClick(location, action));
                }

                if (getTransportModeSlot() != -1) {
                    blockMenu.addMenuClickHandler(
                        getTransportModeSlot(), (p, slot, item, action) -> toggleTransportMode(location, action));
                }

                updateShowIcon(location);
                updateTransportModeIcon(location);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                    || (this.getSlimefunItem().canUse(player, false)
                    && Slimefun.getProtectionManager()
                    .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow == ItemTransportFlow.INSERT) {
                    return getInputSlots();
                } else {
                    return getOutputSlots();
                }
            }
        };
    }

    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public boolean directionClick(Player player, ClickAction action, BlockMenu blockMenu, BlockFace blockFace) {
        if (action.isShiftClicked()) {
            openDirection(player, blockMenu, blockFace);
        } else {
            setDirection(blockMenu, blockFace);
        }
        return false;
    }

    @ParametersAreNonnullByDefault
    public void openDirection(Player player, BlockMenu blockMenu, BlockFace blockFace) {
        super.openDirection(player, blockMenu, blockFace);
    }

    @ParametersAreNonnullByDefault
    public void setDirection(BlockMenu blockMenu, BlockFace blockFace) {
        SELECTED_DIRECTION_MAP.put(blockMenu.getLocation().clone(), blockFace);
        StorageCacheUtils.setData(blockMenu.getBlock().getLocation(), DIRECTION, blockFace.name());
    }

    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public boolean minusClick(Location location, ClickAction action) {
        int n = 1;
        if (action.isRightClicked()) {
            n = 8;
        }
        if (action.isShiftClicked()) {
            n = 64;
        }
        minusLimitQuantity(location, n);
        return false;
    }

    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public boolean addClick(Location location, ClickAction action) {
        int n = 1;
        if (action.isRightClicked()) {
            n = 8;
        }
        if (action.isShiftClicked()) {
            n = 64;
        }
        addLimitQuantity(location, n);
        return false;
    }

    protected abstract int @NotNull [] getBackgroundSlots();

    protected int @Nullable [] getOtherBackgroundSlots() {
        return super.getOtherBackgroundSlots();
    }

    @Nullable
    protected ItemStack getOtherBackgroundStack() {
        return super.getOtherBackgroundStack();
    }

    @Range(from = 0, to = 53)
    public int getNorthSlot() {
        return NORTH_SLOT;
    }

    @Range(from = 0, to = 53)
    public int getSouthSlot() {
        return SOUTH_SLOT;
    }

    @Range(from = 0, to = 53)
    public int getEastSlot() {
        return EAST_SLOT;
    }

    @Range(from = 0, to = 53)
    public int getWestSlot() {
        return WEST_SLOT;
    }

    @Range(from = 0, to = 53)
    public int getUpSlot() {
        return UP_SLOT;
    }

    @Range(from = 0, to = 53)
    public int getDownSlot() {
        return DOWN_SLOT;
    }

    public @NotNull ItemStack getCargoNumberIcon() {
        return this.CARGO_NUMBER_ICON_CLONE;
    }

    public @NotNull ItemStack getMinusIcon() {
        return Icon.MINUS_ICON;
    }

    public @NotNull ItemStack getAddIcon() {
        return Icon.ADD_ICON;
    }

    public @NotNull ItemStack getTransportModeIcon() {
        return Icon.TRANSPORT_MODE_ICON;
    }

    public int getLimitQuantity(@NotNull Location location) {
        Integer quantity = NETWORK_LIMIT_QUANTITY_MAP.get(location.clone());
        if (quantity == null) {
            String squantity = StorageCacheUtils.getData(location, LIMIT_KEY);
            if (squantity == null) {
                quantity = 64;
            } else {
                quantity = Integer.parseInt(squantity);
            }
            NETWORK_LIMIT_QUANTITY_MAP.put(location.clone(), quantity);
        }
        return quantity;
    }

    public void setLimitQuantity(@NotNull Location location, int quantity) {
        NETWORK_LIMIT_QUANTITY_MAP.put(location.clone(), quantity);
        StorageCacheUtils.setData(location, LIMIT_KEY, Integer.toString(quantity));
    }

    public @NotNull TransportMode getCurrentTransportMode(@NotNull Location location) {
        TransportMode mode = NETWORK_TRANSPORT_MODE_MAP.get(location.clone());
        if (mode == null) {
            mode = TransportMode.valueOf(StorageCacheUtils.getData(location, TRANSPORT_MODE_KEY));
            NETWORK_TRANSPORT_MODE_MAP.put(location.clone(), mode);
        }
        return mode;
    }

    public void setTransportMode(@NotNull Location location, TransportMode mode) {
        NETWORK_TRANSPORT_MODE_MAP.put(location.clone(), mode);
        StorageCacheUtils.setData(location, TRANSPORT_MODE_KEY, String.valueOf(mode));
    }

    @SuppressWarnings("deprecation")
    public boolean toggleTransportMode(@NotNull Location location, @NotNull ClickAction action) {
        TransportMode mode = getCurrentTransportMode(location);
        if (action.isRightClicked()) {
            setTransportMode(location, mode.previous());
        } else {
            setTransportMode(location, mode.next());
        }
        updateTransportModeIcon(location);
        return false;
    }

    public void minusLimitQuantity(@NotNull Location location, int quantity) {
        int limitQuantity = getLimitQuantity(location);
        if (limitQuantity - quantity >= 1) {
            setLimitQuantity(location, (limitQuantity - quantity));
        } else {
            setLimitQuantity(location, getMaxLimit() - (quantity - limitQuantity));
        }
        updateShowIcon(location);
    }

    public void addLimitQuantity(@NotNull Location location, int quantity) {
        int limitQuantity = getLimitQuantity(location);
        int newQuantity = limitQuantity + quantity;
        if (isExceedLimit(newQuantity)) {
            newQuantity = newQuantity - getMaxLimit();
        }
        setLimitQuantity(location, newQuantity);
        updateShowIcon(location);
    }

    public abstract boolean isExceedLimit(int quantity);

    public abstract int getMaxLimit();

    @SuppressWarnings("deprecation")
    public void updateShowIcon(@NotNull Location location) {
        ItemMeta itemMeta = this.CARGO_NUMBER_ICON_CLONE.getItemMeta();
        List<String> lore = new ArrayList<>();
        List<String> old = itemMeta.getLore();
        if (old != null) {
            lore.addAll(old);
        }
        lore.set(
            0,
            String.format(
                Lang.getString("messages.normal-operation.directional.limit_quantity"),
                getLimitQuantity(location)));
        itemMeta.setLore(lore);
        this.CARGO_NUMBER_ICON_CLONE.setItemMeta(itemMeta);

        BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
        if (blockMenu != null) {
            int slot = getCargoNumberSlot();
            if (slot != -1) {
                blockMenu.replaceExistingItem(slot, this.CARGO_NUMBER_ICON_CLONE);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void updateTransportModeIcon(@NotNull Location location) {
        ItemMeta itemMeta = this.TRANSPORT_MODE_ICON_CLONE.getItemMeta();
        List<String> lore = new ArrayList<>();
        List<String> old = itemMeta.getLore();
        if (old != null) {
            lore.addAll(old);
        }
        lore.set(
            0,
            String.format(
                Lang.getString("messages.normal-operation.directional.transport_mode"),
                getCurrentTransportMode(location).getName()));
        itemMeta.setLore(lore);
        this.TRANSPORT_MODE_ICON_CLONE.setItemMeta(itemMeta);

        BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
        if (blockMenu != null) {
            int slot = getTransportModeSlot();
            if (slot != -1) {
                blockMenu.replaceExistingItem(slot, this.TRANSPORT_MODE_ICON_CLONE);
            }
        }
    }

    @Range(from = -1, to = 53)
    protected abstract int getMinusSlot();

    @Range(from = -1, to = 53)
    protected abstract int getCargoNumberSlot();

    @Range(from = -1, to = 53)
    protected abstract int getAddSlot();

    @Range(from = -1, to = 53)
    protected abstract int getTransportModeSlot();
}
