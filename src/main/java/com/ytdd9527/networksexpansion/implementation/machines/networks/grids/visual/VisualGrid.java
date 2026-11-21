package com.ytdd9527.networksexpansion.implementation.machines.networks.grids.visual;

import com.balugaq.bim.general.BlockPos;
import com.balugaq.bim.grid.ActiveGrid;
import com.balugaq.bim.grid.GridOrientation;
import com.balugaq.netex.api.keybind.Keybindable;
import com.balugaq.netex.api.keybind.Keybinds;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.ytdd9527.networksexpansion.core.machines.AbstractGridNewStyle;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.grid.SlotLayout;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@SuppressWarnings("DuplicatedCode")
public class VisualGrid extends AbstractGridNewStyle implements Keybindable {
    private static final VisualGridPreset PRESET = new VisualGridPreset();
    static final int[] BACKGROUND_SLOTS = new int[]{17, 26, 35};

    private static final int[] DISPLAY_SLOTS = {
        0,  1,  2,  3,  4,  5,  6,  7,
        9,  10, 11, 12, 13, 14, 15, 16,
        18, 19, 20, 21, 22, 23, 24, 25,
        27, 28, 29, 30, 31, 32, 33, 34,
        36, 37, 38, 39, 40, 41, 42, 43,
        45, 46, 47, 48, 49, 50, 51, 52,
        54, 55, 56, 57, 58, 59, 60, 61,
        63, 64, 65, 66, 67, 68, 69, 70,
        72, 73, 74, 75, 76, 77, 78, 79
    };

    static final int KEYBIND_BUTTON_SLOT = 8;

    static final int CHANGE_SORT = 62;
    static final int FILTER = 53;
    static final int PAGE_PREVIOUS = 71;
    static final int PAGE_NEXT = 80;
    static final int TOGGLE_MODE_SLOT = 44;

    public VisualGrid(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.VISUAL_GRID);
        addItemHandler(new BlockTicker() {

            private int tick = 1;

            @Override
            public boolean isSynchronized() {
                return false;
            }

            @Override
            public void tick(Block block, SlimefunItem item, SlimefunBlockData data) {
                if (true) return; // todo
                if (tick > 1) return;
                addToRegistry(block);
                ActiveGrid active = Networks.getBIMLoader().getCache().activeGrids().get(BlockPos.from(block.getLocation()));
                if (active == null || active.getOption() != PRESET) {
                    return;
                }

                tryAddItem(block.getLocation(), active);
                // update 的 ticker 在 {@link VisualGridPreset#tick(ActiveGrid)}
            }

            @Override
            public void uniqueTick() {
                tick = tick <= 1 ? tickRate.getValue() : tick - 1;
            }
        });
    }

    @Override
    protected void onBlockPlace(BlockPlaceEvent e) {
        if (true) { // todo: 实验物品先不开放，不仅是很卡而且还错位了，BIM 的坐标计算有大问题
            e.setCancelled(true);
            return;
        }
        var block = e.getBlockPlaced();
        ActiveGrid active = Networks.getBIMLoader().getCache().activeGrids().get(BlockPos.from(block.getLocation()));
        if (active == null || active.getOption() != PRESET) {
            var pl = e.getPlayer().getLocation();
            active = PRESET.place(BlockPos.from(block.getLocation()), GridOrientation.fromYawPitch(pl.getYaw(), pl.getPitch()).reverse());
        }
    }

    @Override
    protected void onBlockBreak(BlockBreakEvent e) {
        if (true) return; // todo
        var pos = BlockPos.from(e.getBlock().getLocation());
        Networks.getBIMLoader().getGridUtil().removeGrid(pos, PRESET);
    }

    public void tryAddItem(Location location, ActiveGrid active) {
        var node = NetworkStorage.getNode(location);
        if (node == null || node.getNode() == null) {
            return;
        }
        var root = node.getNode().getRoot();
        var items = active.getLocation().getWorld().getNearbyEntitiesByType(Item.class, active.getLocation(), 1, 1, 1);
        for (var e : items) {
            root.addItemStack(location, e.getItemStack());
            if (e.getItemStack().getAmount() == 0) {
                e.remove();
            }
        }
    }

    @Override
    protected @Nullable BlockMenuPreset getPreset() {
        return null;
    }

    @Override
    protected SlotLayout slotLayout() {
        return new SlotLayout(
            BACKGROUND_SLOTS, DISPLAY_SLOTS, -1,
            CHANGE_SORT, PAGE_PREVIOUS, PAGE_NEXT, FILTER);
    }

    public int getKeybindButtonSlot() {
        return KEYBIND_BUTTON_SLOT;
    }

    public int getToggleModeSlot() {
        return TOGGLE_MODE_SLOT;
    }

    @Override
    public List<Keybinds> keybinds() {
        return List.of(displayKeybinds(), outsideKeybinds());
    }
}
