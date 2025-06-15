package io.github.sefiraat.networks.slimefun.tools;

import com.balugaq.netex.api.enums.TransportMode;
import com.balugaq.netex.utils.Lang;
import com.jeff_media.morepersistentdatatypes.DataType;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.core.items.machines.AdvancedDirectional;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.NetworkUtils;
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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class NetworkConfigurator extends SpecialSlimefunItem {

    public NetworkConfigurator(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemHandler((ItemUseHandler) e -> {
            final Player player = e.getPlayer();
            final Optional<Block> optional = e.getClickedBlock();
            if (optional.isPresent()) {
                final Block block = optional.get();
                final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(block.getLocation());

                if (Slimefun.getProtectionManager().hasPermission(player, block, Interaction.INTERACT_BLOCK)) {
                    if (slimefunItem instanceof NetworkDirectional directional) {
                        final BlockMenu blockMenu = StorageCacheUtils.getMenu(block.getLocation());
                        if (blockMenu == null) {
                            return;
                        }
                        if (player.isSneaking()) {
                            if (slimefunItem instanceof AdvancedDirectional advancedDirectional) {
                                ItemMeta itemMeta = e.getItem().getItemMeta();
                                int amount = advancedDirectional.getLimitQuantity(blockMenu.getLocation());
                                DataTypeMethods.setCustom(itemMeta, Keys.AMOUNT, DataType.INTEGER, amount);
                                player.sendMessage(String.format(
                                        Lang.getString(
                                                "messages.completed-operation.configurator.copied_limit_quantity"),
                                        amount));
                                TransportMode transportMode =
                                        advancedDirectional.getCurrentTransportMode(blockMenu.getLocation());
                                DataTypeMethods.setCustom(
                                        itemMeta, Keys.TRANSFER_MODE, DataType.STRING, String.valueOf(transportMode));
                                player.sendMessage(String.format(
                                        Lang.getString(
                                                "messages.completed-operation.configurator.copied_transport_mode"),
                                        transportMode));
                                e.getItem().setItemMeta(itemMeta);
                            }
                            setConfigurator(directional, e.getItem(), blockMenu, player);
                        } else {
                            if (slimefunItem instanceof AdvancedDirectional advancedDirectional) {
                                ItemMeta itemMeta = e.getItem().getItemMeta();
                                Integer amount = DataTypeMethods.getCustom(itemMeta, Keys.AMOUNT, DataType.INTEGER);
                                if (amount != null) {
                                    advancedDirectional.setLimitQuantity(blockMenu.getLocation(), amount);
                                    player.sendMessage(Lang.getString(
                                            "messages.completed-operation.configurator.pasted_limit_quantity"));
                                }
                                String transportMode =
                                        DataTypeMethods.getCustom(itemMeta, Keys.TRANSFER_MODE, DataType.STRING);
                                if (transportMode != null) {
                                    advancedDirectional.setTransportMode(
                                            blockMenu.getLocation(), TransportMode.valueOf(transportMode));
                                    player.sendMessage(Lang.getString(
                                            "messages.completed-operation.configurator.pasted_transport_mode"));
                                }
                                advancedDirectional.updateShowIcon(blockMenu.getLocation());
                                advancedDirectional.updateTransportModeIcon(blockMenu.getLocation());
                            }
                            NetworkUtils.applyConfig(directional, e.getItem(), blockMenu, player);
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

    private void setConfigurator(
            @NotNull NetworkDirectional directional,
            @NotNull ItemStack itemStack,
            @NotNull BlockMenu blockMenu,
            @NotNull Player player) {
        BlockFace blockFace = NetworkDirectional.getSelectedFace(blockMenu.getLocation());
        if (blockFace == null) {
            blockFace = AdvancedDirectional.getSelectedFace(blockMenu.getLocation());
        }
        if (blockFace == null) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.configurator.not_a_copyable_block"));
            return;
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (directional.getItemSlots().length > 0) {
            final ItemStack[] itemStacks = new ItemStack[directional.getItemSlots().length];

            int i = 0;
            for (int slot : directional.getItemSlots()) {
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

        DataTypeMethods.setCustom(itemMeta, Keys.FACE, DataType.STRING, blockFace.name());
        itemStack.setItemMeta(itemMeta);
        player.sendMessage(Lang.getString("messages.completed-operation.configurator.copied"));
    }
}
