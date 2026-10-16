package com.ytdd9527.networksexpansion.implementation.machines.networks.advanced;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import com.ytdd9527.networksexpansion.utils.ReflectionUtil;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SuperTrash extends NetworkObject implements RecipeDisplayItem {
    private static final int[] BACKGROUND_SLOTS = new int[] {
        0, 1, 2, 3, 4, 5, 6, 7, 8
    };
    private static final int[] INPUT_SLOTS = new int[] {
        999, 999, 999, 999, 999, 999, 999, 999, 999,
        999, 999, 999, 999, 999, 999, 999, 999, 999,
        999, 999, 999, 999, 999, 999, 999, 999, 999,
        999, 999, 999, 999, 999, 999, 999, 999, 999,
        999, 999, 999, 999, 999, 999, 999, 999, 999,
        999, 999, 999, 999, 999, 999, 999, 999, 999,
        999, 999, 999, 999, 999, 999, 999, 999, 999,
        999, 999, 999, 999, 999, 999, 999, 999, 999,
        999, 999, 999, 999, 999, 999, 999, 999, 999,
        999, 999, 999, 999, 999, 999, 999, 999, 999,
        999, 999, 999, 999, 999, 999, 999, 999, 999,
        999, 999, 999, 999, 999, 999, 999, 999, 999,
    };
    private final @NotNull ItemSetting<Integer> tickRate;

    public SuperTrash(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.PURGER);

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
                    replaceMenu(block.getLocation());
                }
            }

            @Override
            public void uniqueTick() {
                tick = tick <= 1 ? tickRate.getValue() : tick - 1;
            }
        });
    }

    @Override
    public void onPlace(@NonNull BlockPlaceEvent event) {
        replaceMenu(event.getBlock().getLocation());
    }

    public static void replaceMenu(Location location) {
        var data = StorageCacheUtils.getBlock(location);
        if (data == null) return;
        ReflectionUtil.setValue(data, "menu", createMenu(location));
    }

    private static SuperTrashBlockMenu createMenu(Location location) {
        return new SuperTrashBlockMenu(location);
    }

    @SuppressWarnings("deprecation")
    public static class SuperTrashBlockMenu extends BlockMenu {
        public SuperTrashBlockMenu(final Location l) {
            super(ExpansionItems.SUPER_TRASH.PRESET, l);
            addPlayerInventoryClickHandler((p, s, i, a) -> {
                i.setAmount(0);
                return false;
            });
            this.inventory = Bukkit.createInventory(this, 9);
        }

        @Override
        public ItemStack getItemInSlot(int slot) {
            return null;
        }

        @Override
        public ChestMenu addItem(int slot, ItemStack item) {
            return this;
        }

        @Override
        public void replaceExistingItem(int slot, ItemStack item) {
        }
    }

    public BlockMenuPreset PRESET = new BlockMenuPreset(this.getId(), this.getItemName()) {

        @Override
        public void init() {
            setSize(9);
        }

        @Override
        public boolean canOpen(@NotNull Block block, @NotNull Player player) {
            return player.hasPermission("slimefun.inventory.bypass")
                || (SuperTrash.this.canUse(player, false)
                && Slimefun.getProtectionManager()
                .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
        }

        @Override
        public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
            if (flow == ItemTransportFlow.INSERT) {
                return INPUT_SLOTS;
            }
            return new int[0];
        }
    };

    @NotNull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        return new ArrayList<>();
    }
}
