package com.ytdd9527.networksexpansion.implementation.tools;

import com.balugaq.netex.utils.Lang;
import com.jeff_media.morepersistentdatatypes.DataType;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.DueMachine;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import java.util.Optional;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class DueMachineConfigurator extends SpecialSlimefunItem {
    public DueMachineConfigurator(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemHandler((ItemUseHandler) e -> {
            final Player player = e.getPlayer();
            final Optional<Block> optional = e.getClickedBlock();
            if (optional.isPresent()) {
                final Block block = optional.get();
                final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(block.getLocation());

                if (Slimefun.getProtectionManager().hasPermission(player, block, Interaction.INTERACT_BLOCK)) {
                    if (slimefunItem instanceof DueMachine dueMachine) {
                        final BlockMenu blockMenu = StorageCacheUtils.getMenu(block.getLocation());
                        if (blockMenu == null) {
                            return;
                        }

                        if (player.isSneaking()) {
                            setConfigurator(dueMachine, e.getItem(), blockMenu, player);
                        } else {
                            applyConfig(dueMachine, e.getItem(), blockMenu, player);
                        }
                    } else {
                        player.sendMessage(
                                Lang.getString("messages.unsupported-operation.configurator.not_a_pasteable_block"));
                    }
                }
            }
            e.cancel();
        });
    }

    public static void applyConfig(
            @NotNull DueMachine dueMachine,
            @NotNull ItemStack itemStack,
            @NotNull BlockMenu blockMenu,
            @NotNull Player player) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final ItemStack[] templateStacks = DataTypeMethods.getCustom(itemMeta, Keys.ITEM, DataType.ITEM_STACK_ARRAY);

        dueMachine.getItemSlots();
        for (int slot : dueMachine.getItemSlots()) {
            final ItemStack stackToDrop = blockMenu.getItemInSlot(slot);
            if (stackToDrop != null && stackToDrop.getType() != Material.AIR) {
                blockMenu.getLocation().getWorld().dropItem(blockMenu.getLocation(), stackToDrop.clone());
                stackToDrop.setAmount(0);
            }
        }

        if (templateStacks != null) {
            int i = 0;
            for (ItemStack templateStack : templateStacks) {
                if (templateStack != null && templateStack.getType() != Material.AIR) {
                    boolean worked = false;
                    for (ItemStack stack : player.getInventory()) {
                        if (StackUtils.itemsMatch(stack, templateStack)) {
                            final ItemStack stackClone = StackUtils.getAsQuantity(stack, 1);
                            stack.setAmount(stack.getAmount() - 1);
                            blockMenu.replaceExistingItem(dueMachine.getItemSlots()[i], stackClone);
                            player.sendMessage(String.format(
                                    Lang.getString("messages.completed-operation.configurator.pasted_item"), i));
                            worked = true;
                            break;
                        }
                    }
                    if (!worked) {
                        player.sendMessage(String.format(
                                Lang.getString("messages.unsupported-operation.configurator.not_enough_items"), i));
                    }
                }
                i++;
            }
        } else {
            player.sendMessage(Lang.getString("messages.unsupported-operation.configurator.no_item_configured"));
        }
    }

    private void setConfigurator(
            @NotNull DueMachine dueMachine,
            @NotNull ItemStack itemStack,
            @NotNull BlockMenu blockMenu,
            @NotNull Player player) {
        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (dueMachine.getItemSlots().length > 0) {
            final ItemStack[] itemStacks = new ItemStack[dueMachine.getItemSlots().length];

            int i = 0;
            for (int slot : dueMachine.getItemSlots()) {
                final ItemStack possibleStack = blockMenu.getItemInSlot(slot);
                if (possibleStack != null) {
                    itemStacks[i] = StackUtils.getAsQuantity(blockMenu.getItemInSlot(slot), 1);
                }
                i++;
            }
            DataTypeMethods.setCustom(itemMeta, Keys.ITEM, DataType.ITEM_STACK_ARRAY, itemStacks);
        } else {
            PersistentDataAPI.remove(itemMeta, Keys.ITEM);
        }

        itemStack.setItemMeta(itemMeta);
        player.sendMessage(Lang.getString("messages.completed-operation.configurator.copied"));
    }
}
