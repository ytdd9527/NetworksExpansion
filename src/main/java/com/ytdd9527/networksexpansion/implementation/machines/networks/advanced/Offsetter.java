package com.ytdd9527.networksexpansion.implementation.machines.networks.advanced;

import com.balugaq.netex.api.helpers.Icon;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.balugaq.netex.api.enums.TransportFacing;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.balugaq.netex.utils.BlockMenuUtil;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.slimefun.network.AdminDebuggable;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Offsetter extends SpecialSlimefunItem implements AdminDebuggable {
    private static final Map<Location, TransportFacing> facingMap = new HashMap<>();
    private static final Map<Location, Integer> offsetMap = new HashMap<>();
    private static final String BS_OFFSET = "offset";
    private static final int OFFSET_DECREASE_SLOT = 3;
    private static final int OFFSET_SHOW_SLOT = 4;
    private static final int OFFSET_INCREASE_SLOT = 5;

    public Offsetter(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item, @Nonnull RecipeType recipeType, @Nonnull ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    private static void onTick(@Nonnull Block block) {
        final Location location = block.getLocation();
        final BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
        if (blockMenu == null) {
            return;
        }

        final TransportFacing facing = facingMap.get(location);
        if (facing == null) {
            return;
        }

        final BlockFace from;
        final BlockFace to;
        switch (facing) {
            case UP_TO_DOWN -> {
                from = BlockFace.UP;
                to = BlockFace.DOWN;
            }
            case DOWN_TO_UP -> {
                from = BlockFace.DOWN;
                to = BlockFace.UP;
            }
            case EAST_TO_WEST -> {
                from = BlockFace.EAST;
                to = BlockFace.WEST;
            }
            case WEST_TO_EAST -> {
                from = BlockFace.WEST;
                to = BlockFace.EAST;
            }
            case SOUTH_TO_NORTH -> {
                from = BlockFace.SOUTH;
                to = BlockFace.NORTH;
            }
            case NORTH_TO_SOUTH -> {
                from = BlockFace.NORTH;
                to = BlockFace.SOUTH;
            }
            default -> {
                from = null;
                to = null;
            }
        }

        if (from == null || to == null) {
            return;
        }

        final Block fromBlock = block.getRelative(from);
        final Block toBlock = block.getRelative(to);
        final Location fromLocation = fromBlock.getLocation();
        final Location toLocation = toBlock.getLocation();

        final BlockMenu fromMenu = StorageCacheUtils.getMenu(fromLocation);
        final BlockMenu toMenu = StorageCacheUtils.getMenu(toLocation);
        if (fromMenu == null || toMenu == null) {
            return;
        }

        final int[] fromSlot = fromMenu.getPreset().getSlotsAccessedByItemTransport(fromMenu, ItemTransportFlow.WITHDRAW, null);
        for (int i = 0; i < fromSlot.length; i++) {
            ItemStack fromItem = fromMenu.getItemInSlot(fromSlot[i]);
            if (fromItem == null || fromItem.getType() == Material.AIR) {
                continue;
            }
            final int[] toSlot = toMenu.getPreset().getSlotsAccessedByItemTransport(toMenu, ItemTransportFlow.INSERT, fromItem);
            final int offset = getOffset(location);
            final int offseti = i + offset;
            if (offseti >= toSlot.length || offseti < 0) {
                continue;
            }

            if (BlockMenuUtil.fits(toMenu, fromItem, toSlot[offseti])) {
                BlockMenuUtil.pushItem(toMenu, fromItem, toSlot[offseti]);
            }
        }
    }

    private static void increaseOffset(@Nonnull Location location, int amount) {
        int offset = getOffset(location);
        if (offset + amount > 53 || offset + amount < -53) {
            return;
        }

        setOffset(location, offset + amount);
    }

    private static void decreaseOffset(@Nonnull Location location, int amount) {
        int offset = getOffset(location);
        if (offset - amount > 53 || offset - amount < -53) {
            return;
        }

        setOffset(location, offset - amount);
    }

    private static void setOffset(@Nonnull Location location, int offset) {
        offsetMap.put(location, offset);
        StorageCacheUtils.setData(location, BS_OFFSET, String.valueOf(offset));
        updateOffsetShowIcon(location, offset);
    }

    private static int getOffset(@Nonnull Location location) {
        Integer offset = offsetMap.get(location);
        if (offset == null) {
            String data = StorageCacheUtils.getData(location, BS_OFFSET);
            if (data == null) {
                setOffset(location, 0);
                offset = 0;
            } else {
                try {
                    offset = Integer.parseInt(data);
                } catch (NumberFormatException e) {
                    setOffset(location, 0);
                    offset = 0;
                }
            }
        }
        return offset;
    }

    private static void updateOffsetShowIcon(@Nonnull Location location, int offset) {
        ItemStack newIcon = Icon.OFFSET_SHOW_ICON.clone();
        ItemMeta meta = newIcon.getItemMeta();
        if (meta == null) {
            return;
        }
        List<String> newLore = new ArrayList<>();
        newLore.add(String.format(Networks.getLocalizationService().getString("icons.offset_show_icon.lore"), offset));
        meta.setLore(newLore);
        newIcon.setItemMeta(meta);

        BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
        if (blockMenu == null) {
            return;
        }
        blockMenu.replaceExistingItem(OFFSET_SHOW_SLOT, newIcon);
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@Nonnull BlockPlaceEvent blockPlaceEvent) {

            }
        });

        addItemHandler(new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(BlockBreakEvent blockBreakEvent, ItemStack itemStack, List<ItemStack> list) {

            }
        });

        addItemHandler(new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return false;
            }

            @Override
            public void tick(Block block, SlimefunItem slimefunItem, SlimefunBlockData data) {
                onTick(block);
            }
        });
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {
            @Override
            public void init() {
                for (int i = 0; i < 9; i++) {
                    addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
                }

                addItem(OFFSET_INCREASE_SLOT, Icon.OFFSET_INCREASE_ICON, ChestMenuUtils.getEmptyClickHandler());
                addItem(OFFSET_SHOW_SLOT, Icon.OFFSET_SHOW_ICON, ChestMenuUtils.getEmptyClickHandler());
                addItem(OFFSET_DECREASE_SLOT, Icon.OFFSET_DECREASE_ICON, ChestMenuUtils.getEmptyClickHandler());
            }

            @Override
            public boolean canOpen(@Nonnull Block block, @Nonnull Player player) {
                return player.hasPermission("slimefun.inventory.bypass") || (Slimefun.getPermissionsService().hasPermission(player, this.getSlimefunItem()) && Slimefun.getProtectionManager().hasPermission(player, block, Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
                return new int[0];
            }

            @Override
            public void newInstance(@Nonnull BlockMenu blockMenu, @Nonnull Block block) {
                // Texture should be Grindstone
                BlockData blockData = block.getBlockData();
                BlockFace blockFace = null;
                FaceAttachable.AttachedFace attachedFace = null;
                if (blockData instanceof Directional directional) {
                    blockFace = directional.getFacing();
                }

                if (blockFace == null) {
                    return;
                }

                if (blockData instanceof FaceAttachable faceAttachable) {
                    attachedFace = faceAttachable.getAttachedFace();
                }

                if (attachedFace == null) {
                    return;
                }

                Location location = block.getLocation();
                switch (attachedFace) {
                    case FLOOR -> {
                        facingMap.put(location, TransportFacing.DOWN_TO_UP);
                    }
                    case CEILING -> {
                        facingMap.put(location, TransportFacing.UP_TO_DOWN);
                    }
                    case WALL -> {
                        switch (blockFace) {
                            case WEST -> {
                                facingMap.put(location, TransportFacing.EAST_TO_WEST);
                            }
                            case EAST -> {
                                facingMap.put(location, TransportFacing.WEST_TO_EAST);
                            }
                            case NORTH -> {
                                facingMap.put(location, TransportFacing.SOUTH_TO_NORTH);
                            }
                            case SOUTH -> {
                                facingMap.put(location, TransportFacing.NORTH_TO_SOUTH);
                            }
                        }
                    }
                }

                setOffset(location, 0);
                updateOffsetShowIcon(location, getOffset(location));

                // Click handler
                blockMenu.addMenuClickHandler(OFFSET_INCREASE_SLOT, (player, slot, item, clickAction) -> {
                    increaseOffset(location, 1);
                    return false;
                });

                blockMenu.addMenuClickHandler(OFFSET_DECREASE_SLOT, (player, slot, item, clickAction) -> {
                    decreaseOffset(location, 1);
                    return false;
                });
            }
        };
    }

}
