package com.ytdd9527.networksexpansion.implementation.tools;

import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.LocationUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class StatusViewer extends SpecialSlimefunItem {
    public StatusViewer(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            @NotNull ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        addItemHandler((ItemUseHandler) this::onUse);
    }

    protected void onUse(@NotNull PlayerRightClickEvent e) {
        final Optional<Block> optional = e.getClickedBlock();
        if (optional.isPresent()) {
            final Block block = optional.get();
            final Player player = e.getPlayer();
            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(block.getLocation());
            final Location location = block.getLocation();
            if (slimefunItem != null) {
                if (SpecialSlimefunItem.hasSubscribed(player, location)) {
                    SpecialSlimefunItem.unsubscribe(player, location);
                    player.sendMessage(String.format(
                            Lang.getString("messages.completed-operation.status_viewer.unsubscribed"),
                            LocationUtil.humanizeBlock(location)));
                } else {
                    SpecialSlimefunItem.subscribe(player, location);
                    if (slimefunItem instanceof NetworkObject) {
                        player.sendMessage(
                                Lang.getString("messages.completed-operation.status_viewer.is_networks_object"));
                        final NodeDefinition definition = NetworkStorage.getNode(location);
                        if (definition != null && definition.getNode() != null) {
                            player.sendMessage(
                                    Lang.getString("messages.completed-operation.status_viewer.connected_to_network"));
                        } else {
                            player.sendMessage(Lang.getString(
                                    "messages.completed-operation.status_viewer.not_connected_to_network"));
                        }
                    } else {
                        player.sendMessage(
                                Lang.getString("messages.completed-operation.status_viewer.not_networks_object"));
                    }
                    player.sendMessage(String.format(
                            Lang.getString("messages.completed-operation.status_viewer.subscribed"),
                            LocationUtil.humanizeBlock(location)));
                }
                e.cancel();
            }
        }
    }
}
