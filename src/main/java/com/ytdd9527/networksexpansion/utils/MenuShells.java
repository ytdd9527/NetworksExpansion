package com.ytdd9527.networksexpansion.utils;

import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class MenuShells {

    @FunctionalInterface
    public interface CanOpen {
        boolean test(Block block, Player player);
    }

    @FunctionalInterface
    public interface TransportSlots {
        int[] get(ItemTransportFlow flow);
    }

    @FunctionalInterface
    public interface OnReady {
        void ready(BlockMenu menu, Block block);
    }

    private MenuShells() {
    }

    public static void create(
            SpecialSlimefunItem owner,
            int size,
            int [] backgroundSlots,
            @Nullable ItemStack backgroundItem,
            CanOpen canOpen,
            TransportSlots transport,
            OnReady onReady) {
        new BlockMenuPreset(owner.getId(), owner.getItemName()) {
            @Override
            public void init() {
                setSize(size);
                if (backgroundItem != null) {
                    for (int slot : backgroundSlots) {
                        addItem(slot, backgroundItem.clone());
                    }
                } else {
                    drawBackground(backgroundSlots);
                }
            }

            @Override
            public boolean canOpen(Block block, Player player) {
                return canOpen.test(block, player);
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return transport.get(flow);
            }

            @Override
            public void newInstance(BlockMenu menu, Block block) {
                for (int slot : backgroundSlots) {
                    menu.addMenuClickHandler(slot, (p, s, i, a) -> false);
                }
                onReady.ready(menu, block);
            }
        };
    }

    public static void create(
            SpecialSlimefunItem owner,
            int size,
            int [] backgroundSlots,
            CanOpen canOpen,
            TransportSlots transport,
            OnReady onReady) {
        create(owner, size, backgroundSlots, null, canOpen, transport, onReady);
    }

    public static int[] emptyTransport(ItemTransportFlow flow) {
        return new int[0];
    }
}
