package io.github.sefiraat.networks.slimefun.tools;

import com.balugaq.netex.utils.Lang;
import com.jeff_media.morepersistentdatatypes.DataType;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedWirelessTransmitter;
import io.github.sefiraat.networks.slimefun.network.NetworkWirelessReceiver;
import io.github.sefiraat.networks.slimefun.network.NetworkWirelessTransmitter;
import io.github.sefiraat.networks.utils.Keys;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class NetworkWirelessConfigurator extends SpecialSlimefunItem {

    public NetworkWirelessConfigurator(
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
                    final ItemStack heldItem = player.getInventory().getItemInMainHand();
                    final BlockMenu blockMenu = StorageCacheUtils.getMenu(block.getLocation());
                    if (blockMenu == null) {
                        player.sendMessage(Lang.getString(
                            "messages.unsupported-operation.wireless_configurator.not_network_wireless_block"));
                        return;
                    }
                    if (slimefunItem instanceof NetworkWirelessTransmitter transmitter && player.isSneaking()) {
                        setTransmitter(transmitter, heldItem, blockMenu, player);
                    } else if (slimefunItem instanceof NetworkWirelessReceiver && !player.isSneaking()) {
                        setReceiver(heldItem, blockMenu, player);
                    } else if (slimefunItem instanceof AdvancedWirelessTransmitter w) {
                        final ItemMeta itemMeta = heldItem.getItemMeta();
                        Location location = PersistentDataAPI.get(itemMeta, Keys.TARGET_LOCATION, DataType.LOCATION);
                        if (location == null) {
                            location = PersistentDataAPI.get(itemMeta, Keys.TARGET_LOCATION2, DataType.LOCATION);
                        }

                        if (location == null) {
                            location = PersistentDataAPI.get(itemMeta, Keys.TARGET_LOCATION3, DataType.LOCATION);
                        }

                        if (location == null) {
                            player.sendMessage(
                                Lang.getString("messages.unsupported-operation.wireless_configurator.no_target_location"));
                            return;
                        }

                        w.setTargetLocation(block.getLocation(), player, location);
                        player.sendMessage(String.format(Lang.getString("messages.completed-operation.wireless_configurator.set_location"), AdvancedWirelessTransmitter.string(block.getLocation()), AdvancedWirelessTransmitter.string(location)));
                    } else {
                        player.sendMessage(Lang.getString(
                            "messages.unsupported-operation.wireless_configurator.not_network_wireless_block"));
                    }
                } else {
                    player.sendMessage(Lang.getString("messages.unsupported-operation.comprehensive.no_permission"));
                }
            }
            e.cancel();
        });
    }

    private void setTransmitter(
        @NotNull NetworkWirelessTransmitter transmitter,
        @NotNull ItemStack itemStack,
        @NotNull BlockMenu blockMenu,
        @NotNull Player player) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        Location location = PersistentDataAPI.get(itemMeta, Keys.TARGET_LOCATION, DataType.LOCATION);
        if (location == null) {
            location = PersistentDataAPI.get(itemMeta, Keys.TARGET_LOCATION2, DataType.LOCATION);
        }

        if (location == null) {
            location = PersistentDataAPI.get(itemMeta, Keys.TARGET_LOCATION3, DataType.LOCATION);
        }

        if (location == null) {
            player.sendMessage(
                Lang.getString("messages.unsupported-operation.wireless_configurator.no_target_location"));
            return;
        }

        if (location.getWorld() != blockMenu.getLocation().getWorld()) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.wireless_configurator.not_same_world"));
            return;
        }

        transmitter.addLinkedLocation(blockMenu.getBlock(), location);
        player.sendMessage(Lang.getString("messages.completed-operation.wireless_configurator.transmitter_linked"));
    }

    private void setReceiver(@NotNull ItemStack itemStack, @NotNull BlockMenu blockMenu, @NotNull Player player) {
        final Location location = blockMenu.getLocation();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataAPI.set(itemMeta, Keys.TARGET_LOCATION, DataType.LOCATION, location);
        itemStack.setItemMeta(itemMeta);
        player.sendMessage(Lang.getString("messages.completed-operation.wireless_configurator.receiver_set"));
    }
}
