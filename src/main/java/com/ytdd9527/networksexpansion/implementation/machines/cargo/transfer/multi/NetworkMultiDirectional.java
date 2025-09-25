package com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.multi;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.NetworksVersionedEnchantment;
import com.balugaq.netex.utils.NetworksVersionedParticle;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.sefiraat.networks.utils.NetworkUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import net.guizhanss.guizhanlib.minecraft.helper.MaterialHelper;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"deprecation", "DuplicatedCode"})
public abstract class NetworkMultiDirectional extends NetworkDirectional {

    private static final Map<Location, Set<BlockFace>> SELECTED_DIRECTIONS_MAP = new HashMap<>();

    private static final int NORTH_SLOT = 12;
    private static final int SOUTH_SLOT = 30;
    private static final int EAST_SLOT = 22;
    private static final int WEST_SLOT = 20;
    private static final int UP_SLOT = 15;
    private static final int DOWN_SLOT = 33;

    public NetworkMultiDirectional(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe,
        NodeType type) {
        super(itemGroup, item, recipeType, recipe, type);
    }

    public NetworkMultiDirectional(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe,
        @Range(from = 1, to = 64) int outputAmount,
        NodeType type) {
        super(itemGroup, item, recipeType, recipe, outputAmount, type);
    }

    @NotNull
    public static ItemStack getMultiDirectionalSlotPane(
        @NotNull BlockFace blockFace, @NotNull SlimefunItem slimefunItem, boolean active) {
        final ItemStack displayStack = ItemStackUtil.getCleanItem(new CustomItemStack(
            new CustomItemStack(slimefunItem.getItem(), meta -> {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                for (NamespacedKey key : container.getKeys()) {
                    container.remove(key);
                }
            }),
            String.format(
                Lang.getString("messages.normal-operation.directional.display_name"),
                blockFace.name(),
                TextUtil.stripColor(slimefunItem.getItemName()))));
        final ItemMeta itemMeta = displayStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(Lang.getString("messages.normal-operation.directional.display_lore"));
        if (active) {
            lore.add(Lang.getString("messages.normal-operation.directional.selected"));
            itemMeta.addEnchant(NetworksVersionedEnchantment.LUCK_OF_THE_SEA, 1, true);
        } else {
            lore.add(Lang.getString("messages.normal-operation.directional.not_selected"));
        }
        lore.add(Lang.getString("messages.normal-operation.directional.click_to_toggle"));
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        displayStack.setItemMeta(itemMeta);
        return displayStack;
    }

    @NotNull
    public static ItemStack getMultiDirectionalSlotPane(
        @NotNull BlockFace blockFace, @NotNull Material blockMaterial, boolean active) {
        if (blockMaterial.isItem() && blockMaterial != Material.AIR) {
            final ItemStack displayStack = new CustomItemStack(
                blockMaterial,
                String.format(
                    Lang.getString("messages.normal-operation.directional.display_name"),
                    blockFace.name(),
                    MaterialHelper.getName(blockMaterial)));
            final ItemMeta itemMeta = displayStack.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(Lang.getString("messages.normal-operation.directional.display_lore"));
            if (active) {
                lore.add(Lang.getString("messages.normal-operation.directional.selected"));
                itemMeta.addEnchant(NetworksVersionedEnchantment.LUCK_OF_THE_SEA, 1, true);
            } else {
                lore.add(Lang.getString("messages.normal-operation.directional.not_selected"));
            }
            lore.add(Lang.getString("messages.normal-operation.directional.click_to_toggle"));
            itemMeta.setLore(lore);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            displayStack.setItemMeta(itemMeta);
            return displayStack;
        } else {
            Material material = active ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
            ItemStack displayStack = new CustomItemStack(
                material,
                String.format(Lang.getString("messages.normal-operation.directional.display_empty"), blockFace.name()));
            ItemMeta itemMeta = displayStack.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(Lang.getString("messages.normal-operation.directional.empty_block"));
            if (active) {
                lore.add(Lang.getString("messages.normal-operation.directional.selected"));
            } else {
                lore.add(Lang.getString("messages.normal-operation.directional.not_selected"));
            }
            lore.add(Lang.getString("messages.normal-operation.directional.click_to_toggle"));
            itemMeta.setLore(lore);
            displayStack.setItemMeta(itemMeta);
            return displayStack;
        }
    }

    @Nullable
    public static Set<BlockFace> getSelectedFaces(@NotNull Location location) {
        return SELECTED_DIRECTIONS_MAP.get(location);
    }

    @Override
    public void updateGui(@Nullable BlockMenu blockMenu) {
        if (blockMenu == null || !blockMenu.hasViewer()) {
            return;
        }

        Set<BlockFace> directions = getCurrentDirections(blockMenu);

        for (BlockFace blockFace : VALID_FACES) {
            final Block block = blockMenu.getBlock().getRelative(blockFace);
            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(block.getLocation());
            boolean isSelected = directions.contains(blockFace);

            if (slimefunItem != null) {
                switch (blockFace) {
                    case NORTH -> blockMenu.replaceExistingItem(
                        getNorthSlot(), getMultiDirectionalSlotPane(blockFace, slimefunItem, isSelected));
                    case SOUTH -> blockMenu.replaceExistingItem(
                        getSouthSlot(), getMultiDirectionalSlotPane(blockFace, slimefunItem, isSelected));
                    case EAST -> blockMenu.replaceExistingItem(
                        getEastSlot(), getMultiDirectionalSlotPane(blockFace, slimefunItem, isSelected));
                    case WEST -> blockMenu.replaceExistingItem(
                        getWestSlot(), getMultiDirectionalSlotPane(blockFace, slimefunItem, isSelected));
                    case UP -> blockMenu.replaceExistingItem(
                        getUpSlot(), getMultiDirectionalSlotPane(blockFace, slimefunItem, isSelected));
                    case DOWN -> blockMenu.replaceExistingItem(
                        getDownSlot(), getMultiDirectionalSlotPane(blockFace, slimefunItem, isSelected));
                    default -> throw new IllegalStateException(String.format(
                        Lang.getString("messages.unsupported-operation.directional.unexcepted_value"), blockFace));
                }
            } else {
                final Material material = block.getType();
                switch (blockFace) {
                    case NORTH -> blockMenu.replaceExistingItem(
                        getNorthSlot(), getMultiDirectionalSlotPane(blockFace, material, isSelected));
                    case SOUTH -> blockMenu.replaceExistingItem(
                        getSouthSlot(), getMultiDirectionalSlotPane(blockFace, material, isSelected));
                    case EAST -> blockMenu.replaceExistingItem(
                        getEastSlot(), getMultiDirectionalSlotPane(blockFace, material, isSelected));
                    case WEST -> blockMenu.replaceExistingItem(
                        getWestSlot(), getMultiDirectionalSlotPane(blockFace, material, isSelected));
                    case UP -> blockMenu.replaceExistingItem(
                        getUpSlot(), getMultiDirectionalSlotPane(blockFace, material, isSelected));
                    case DOWN -> blockMenu.replaceExistingItem(
                        getDownSlot(), getMultiDirectionalSlotPane(blockFace, material, isSelected));
                    default -> throw new IllegalStateException(String.format(
                        Lang.getString("messages.unsupported-operation.directional.unexcepted_value"), blockFace));
                }
            }
        }
    }

    @NotNull
    protected Set<BlockFace> getCurrentDirections(@NotNull BlockMenu blockMenu) {
        Location location = blockMenu.getLocation().clone();
        Set<BlockFace> directions = SELECTED_DIRECTIONS_MAP.get(location);

        if (directions == null) {
            directions = new HashSet<>();
            String directionData = StorageCacheUtils.getData(location, DIRECTION);

            if (directionData != null && !directionData.isEmpty()) {
                if (directionData.contains(",")) {
                    for (String faceName : directionData.split(",")) {
                        try {
                            BlockFace face = BlockFace.valueOf(faceName.trim());
                            if (VALID_FACES.contains(face)) {
                                directions.add(face);
                            }
                        } catch (IllegalArgumentException e) {
                        }
                    }
                } else {
                    try {
                        BlockFace face = BlockFace.valueOf(directionData);
                        if (VALID_FACES.contains(face)) {
                            directions.add(face);
                        }
                    } catch (IllegalArgumentException e) {
                    }
                }
            }
            SELECTED_DIRECTIONS_MAP.put(location, directions);
        }
        return directions;
    }

    @Override
    public void onPlace(@NotNull BlockPlaceEvent event) {
        NetworkStorage.removeNode(event.getBlock().getLocation());
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(event.getBlock().getLocation());
        if (blockData == null) {
            return;
        }
        blockData.setData(OWNER_KEY, event.getPlayer().getUniqueId().toString());
        blockData.setData(DIRECTION, "");
        BlockMenu blockMenu = blockData.getBlockMenu();
        if (blockMenu != null) {
            NetworkUtils.applyConfig(NetworkMultiDirectional.this, blockMenu, event.getPlayer());
        }
    }

    @Override
    protected void onTick(@Nullable BlockMenu blockMenu, @NotNull Block block) {
        sendFeedback(block.getLocation(), FeedbackType.TICKING);
        addToRegistry(block);
        updateGui(blockMenu);
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
                    getMultiDirectionalSlotPane(BlockFace.NORTH, Material.AIR, false),
                    (player, i, itemStack, clickAction) -> false);
                addItem(
                    getSouthSlot(),
                    getMultiDirectionalSlotPane(BlockFace.SOUTH, Material.AIR, false),
                    (player, i, itemStack, clickAction) -> false);
                addItem(
                    getEastSlot(),
                    getMultiDirectionalSlotPane(BlockFace.EAST, Material.AIR, false),
                    (player, i, itemStack, clickAction) -> false);
                addItem(
                    getWestSlot(),
                    getMultiDirectionalSlotPane(BlockFace.WEST, Material.AIR, false),
                    (player, i, itemStack, clickAction) -> false);
                addItem(
                    getUpSlot(),
                    getMultiDirectionalSlotPane(BlockFace.UP, Material.AIR, false),
                    (player, i, itemStack, clickAction) -> false);
                addItem(
                    getDownSlot(),
                    getMultiDirectionalSlotPane(BlockFace.DOWN, Material.AIR, false),
                    (player, i, itemStack, clickAction) -> false);
            }

            @Override
            public void newInstance(@NotNull BlockMenu blockMenu, @NotNull Block b) {
                // 方向按钮点击处理器
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

    @ParametersAreNonnullByDefault
    public boolean directionClick(Player player, ClickAction action, BlockMenu blockMenu, BlockFace blockFace) {
        if (action.isShiftClicked()) {
            openDirection(player, blockMenu, blockFace);
        } else {
            toggleDirection(blockMenu, blockFace);
        }
        return false;
    }

    @ParametersAreNonnullByDefault
    public void toggleDirection(BlockMenu blockMenu, BlockFace blockFace) {
        Set<BlockFace> directions = getCurrentDirections(blockMenu);

        if (directions.contains(blockFace)) {
            directions.remove(blockFace);
        } else {
            directions.add(blockFace);
        }

        saveDirections(blockMenu, directions);
        updateGui(blockMenu);
    }

    @ParametersAreNonnullByDefault
    private void saveDirections(BlockMenu blockMenu, Set<BlockFace> directions) {
        Location location = blockMenu.getLocation().clone();
        SELECTED_DIRECTIONS_MAP.put(location, directions);

        // 将方向集合保存为逗号分隔的字符串
        String directionString = directions.stream()
            .map(BlockFace::name)
            .collect(Collectors.joining(","));

        StorageCacheUtils.setData(blockMenu.getBlock().getLocation(), DIRECTION, directionString);
    }

    @Override
    protected int @NotNull [] getBackgroundSlots() {
        return new int[]{
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 16, 17, 18, 19, 21, 23, 24, 25, 26, 27, 28, 29, 31, 32,
            34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44
        };
    }

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

    protected Particle.DustOptions getDustOptions() {
        return new Particle.DustOptions(Color.RED, 1);
    }

    protected void showParticle(@NotNull Location location, @NotNull BlockFace blockFace) {
        final Vector faceVector = blockFace.getDirection().clone().multiply(-1);
        final Vector pushVector = faceVector.clone().multiply(2);
        final Location displayLocation = location.clone().add(0.5, 0.5, 0.5).add(faceVector);
        location.getWorld()
            .spawnParticle(
                NetworksVersionedParticle.DUST,
                displayLocation,
                0,
                pushVector.getX(),
                pushVector.getY(),
                pushVector.getZ(),
                getDustOptions());
    }
}