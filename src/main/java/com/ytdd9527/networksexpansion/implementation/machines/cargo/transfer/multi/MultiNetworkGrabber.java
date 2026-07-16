package com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.multi;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.interfaces.SoftCellBannable;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@SuppressWarnings("DuplicatedCode")
public class MultiNetworkGrabber extends NetworkMultiDirectional implements SoftCellBannable {

    public MultiNetworkGrabber(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.GRABBER);
    }

    @Override
    protected void onTick(@Nullable BlockMenu blockMenu, @NotNull Block block) {
        super.onTick(blockMenu, block);
        if (blockMenu != null) {
            tryGrabItems(blockMenu);
        }
    }

    private void tryGrabItems(@NotNull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        if (checkSoftCellBan(blockMenu.getLocation(), definition.getNode().getRoot())) {
            return;
        }

        final Set<BlockFace> directions = this.getCurrentDirections(blockMenu);
        boolean hasWorked = false;

        for (BlockFace direction : directions) {
            final BlockMenu targetMenu = StorageCacheUtils.getMenu(
                blockMenu.getBlock().getRelative(direction).getLocation());

            if (targetMenu == null) {
                continue;
            }

            int[] slots = targetMenu.getPreset().getSlotsAccessedByItemTransport(targetMenu, ItemTransportFlow.WITHDRAW, null);

            for (int slot : slots) {
                final ItemStack itemStack = targetMenu.getItemInSlot(slot);

                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    int before = itemStack.getAmount();
                    definition.getNode().getRoot().addItemStack0(blockMenu.getLocation(), itemStack);
                    if (itemStack.getAmount() < before) {
                        hasWorked = true;
                        if (definition.getNode().getRoot().isDisplayParticles()) {
                            showParticle(blockMenu.getLocation(), direction);
                        }
                        break;
                    }
                }
            }
        }

        if (hasWorked) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
        } else if (!directions.isEmpty()) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_ITEMS);
        }
    }

    @Override
    protected Particle.DustOptions getDustOptions() {
        return new Particle.DustOptions(Color.FUCHSIA, 1);
    }
}