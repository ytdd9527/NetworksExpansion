package io.github.sefiraat.networks.slimefun.network;

import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.NetworksVersionedEnchantment;
import com.balugaq.netex.utils.NetworksVersionedParticle;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NodeType;
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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;
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

@SuppressWarnings("deprecation")
public abstract class NetworkDirectional extends NetworkObject {

    public static final String DIRECTION = "direction";
    public static final String OWNER_KEY = "uuid";
    public static final Set<BlockFace> VALID_FACES =
            EnumSet.of(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    private static final int NORTH_SLOT = 12;
    private static final int SOUTH_SLOT = 30;
    private static final int EAST_SLOT = 22;
    private static final int WEST_SLOT = 20;
    private static final int UP_SLOT = 15;
    private static final int DOWN_SLOT = 33;
    private static final Set<Location> locked = new HashSet<>();
    private static final Map<Location, BlockFace> SELECTED_DIRECTION_MAP = new HashMap<>();

    private final @NotNull ItemSetting<Integer> tickRate;

    protected NetworkDirectional(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe,
            NodeType type) {
        super(itemGroup, item, recipeType, recipe, type);
        this.tickRate = new IntRangeSetting(this, "tick_rate", 1, 1, 10);
        addItemSetting(this.tickRate);

        addItemHandler(new BlockTicker() {

            private int tick = 1;

            @Override
            public boolean isSynchronized() {
                return runSync();
            }

            @Override
            public void tick(@NotNull Block block, SlimefunItem slimefunItem, @NotNull SlimefunBlockData data) {
                if (tick <= 1) {
                    onTick(data.getBlockMenu(), block);
                }
            }

            @Override
            public void uniqueTick() {
                tick = tick <= 1 ? tickRate.getValue() : tick - 1;
                if (tick <= 1) {
                    onUniqueTick();
                }
            }
        });
    }

    @NotNull public static ItemStack getDirectionalSlotPane(
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

    @NotNull public static ItemStack getDirectionalSlotPane(
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

    @Nullable public static BlockFace getSelectedFace(@NotNull Location location) {
        return SELECTED_DIRECTION_MAP.get(location);
    }

    private void updateGui(@Nullable BlockMenu blockMenu) {
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

    @NotNull protected BlockFace getCurrentDirection(@NotNull BlockMenu blockMenu) {
        BlockFace direction = SELECTED_DIRECTION_MAP.get(blockMenu.getLocation().clone());

        if (direction == null) {
            direction = BlockFace.valueOf(StorageCacheUtils.getData(blockMenu.getLocation(), DIRECTION));
            SELECTED_DIRECTION_MAP.put(blockMenu.getLocation().clone(), direction);
        }
        return direction;
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
        BlockMenu blockMenu = blockData.getBlockMenu();
        if (blockMenu != null) {
            NetworkUtils.applyConfig(NetworkDirectional.this, blockMenu, event.getPlayer());
        }
    }

    @OverridingMethodsMustInvokeSuper
    protected void onTick(@Nullable BlockMenu blockMenu, @NotNull Block block) {
        addToRegistry(block);
        updateGui(blockMenu);
    }

    protected void onUniqueTick() {}

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
            }

            @Override
            public void newInstance(@NotNull BlockMenu blockMenu, @NotNull Block b) {
                final BlockFace direction;
                final String string = StorageCacheUtils.getData(blockMenu.getLocation(), DIRECTION);

                if (string == null) {
                    // This likely means a block was placed before I made it directional
                    direction = BlockFace.SELF;
                    StorageCacheUtils.setData(blockMenu.getLocation(), DIRECTION, BlockFace.SELF.name());
                } else {
                    direction = BlockFace.valueOf(string);
                }
                SELECTED_DIRECTION_MAP.put(blockMenu.getLocation().clone(), direction);
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
            setDirection(blockMenu, blockFace);
        }
        return false;
    }

    @ParametersAreNonnullByDefault
    public void openDirection(Player player, BlockMenu blockMenu, BlockFace blockFace) {
        final BlockMenu targetMenu = StorageCacheUtils.getMenu(
                blockMenu.getBlock().getRelative(blockFace).getLocation());
        if (targetMenu != null) {
            final Location location = targetMenu.getLocation();
            final SlimefunItem item = StorageCacheUtils.getSfItem(location);
            if (item != null
                    && item.canUse(player, true)
                    && Slimefun.getProtectionManager()
                            .hasPermission(player, blockMenu.getLocation(), Interaction.INTERACT_BLOCK)) {
                targetMenu.open(player);
            }
        }
    }

    @ParametersAreNonnullByDefault
    public void setDirection(BlockMenu blockMenu, BlockFace blockFace) {
        SELECTED_DIRECTION_MAP.put(blockMenu.getLocation().clone(), blockFace);
        StorageCacheUtils.setData(blockMenu.getBlock().getLocation(), DIRECTION, blockFace.name());
    }

    protected int @NotNull [] getBackgroundSlots() {
        return new int[] {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 16, 17, 18, 19, 21, 23, 24, 25, 26, 27, 28, 29, 21, 31, 32,
            34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44
        };
    }

    protected int @Nullable [] getOtherBackgroundSlots() {
        return null;
    }

    @Nullable protected ItemStack getOtherBackgroundStack() {
        return null;
    }

    public int getNorthSlot() {
        return NORTH_SLOT;
    }

    public int getSouthSlot() {
        return SOUTH_SLOT;
    }

    public int getEastSlot() {
        return EAST_SLOT;
    }

    public int getWestSlot() {
        return WEST_SLOT;
    }

    public int getUpSlot() {
        return UP_SLOT;
    }

    public int getDownSlot() {
        return DOWN_SLOT;
    }

    public int[] getItemSlots() {
        return new int[] {};
    }

    public int[] getInputSlots() {
        return new int[0];
    }

    public int[] getOutputSlots() {
        return new int[0];
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
