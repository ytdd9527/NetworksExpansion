package com.ytdd9527.networksexpansion.implementation.machines.networks.advanced;

import com.balugaq.netex.api.interfaces.HangingBlock;
import com.balugaq.netex.utils.Debug;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Placeable;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SwitchingMonitor extends NetworkObject implements HangingBlock, Placeable {
    public SwitchingMonitor(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.SWITCHING_MONITOR);
        HangingBlock.registerHangingBlock(this);
    }

    @Override
    public void onInteract(Location placeholder, PlayerItemFrameChangeEvent event) {
        Player player = event.getPlayer();

        if (!player.isOp()
                && !Slimefun.getProtectionManager().hasPermission(player, placeholder, Interaction.INTERACT_ENTITY)) {
            event.setCancelled(true);
            return;
        }

        if (event.getAction() == PlayerItemFrameChangeEvent.ItemFrameChangeAction.ROTATE) {
            event.setCancelled(true);
            handleItemsAction(placeholder, player, event);
        }
    }

    private void handleItemsAction(Location placeholder, Player player, PlayerItemFrameChangeEvent event) {
        NodeDefinition definition = NetworkStorage.getNode(placeholder);
        if (definition == null || definition.getNode() == null) {
            return;
        }

        NetworkRoot root = definition.getNode().getRoot();
        ItemStack template = event.getItemFrame().getItem();
        if (template == null || template.getType() == Material.AIR) {
            return;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        boolean takeItem = hand == null || hand.getType() == Material.AIR;
        boolean shift = player.isSneaking();
        if (!takeItem && !StackUtils.itemsMatch(hand, template, true, false)) {
            return;
        }

        if (takeItem) {
            if (shift) {
                int stacks = calculateSpace(player, template);
                ItemStack result =
                        root.getItemStack0(placeholder, new ItemRequest(template, stacks * template.getMaxStackSize()));
                if (result != null) {
                    player.getInventory().addItem(result);
                    player.updateInventory();
                }
            } else {
                ItemStack result =
                        root.getItemStack0(placeholder, new ItemRequest(template, template.getMaxStackSize()));
                if (result != null) {
                    player.getInventory().addItem(result);
                    player.updateInventory();
                }
            }
        } else {
            if (shift) {
                for (var item : player.getInventory().getStorageContents()) {
                    if (item != null
                            && item.getType() != Material.AIR
                            && StackUtils.itemsMatch(item, template, true, false)) {
                        int before = item.getAmount();
                        root.addItemStack0(placeholder, item);
                        int after = item.getAmount();
                        if (before == after) {
                            break;
                        }
                    }
                }
            } else {
                root.addItemStack0(placeholder, hand);
            }
        }
    }

    public int calculateSpace(Player player, ItemStack template) {
        int stacks = 0;
        for (var item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType() == Material.AIR) {
                stacks++;
            }
        }
        return stacks;
    }

    @Override
    public void onTick(Location placeholder, ItemFrame entityBlock) {}
}
