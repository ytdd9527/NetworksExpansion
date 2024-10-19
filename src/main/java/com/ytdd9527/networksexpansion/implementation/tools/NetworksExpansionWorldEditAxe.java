package com.ytdd9527.networksexpansion.implementation.tools;

import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.commands.NetworksMain;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class NetworksExpansionWorldEditAxe extends SpecialSlimefunItem {

    public NetworksExpansionWorldEditAxe(ItemGroup itemGroup, SlimefunItemStack item) {
        super(itemGroup, item, RecipeType.NULL, new ItemStack[]{});
        addItemHandler(
                (ItemUseHandler) e -> {
                    final Player player = e.getPlayer();
                    if (!player.isOp()) {
                        player.sendMessage(Networks.getLocalizationService().getString("messages.unsupported-operation.no_permission"));
                        return;
                    }
                    final Optional<Block> optional = e.getClickedBlock();
                    if (optional.isPresent()) {
                        final Location location = optional.get().getLocation();
                        NetworksMain.worldeditPos1(player, location);
                    }
                    e.cancel();
                },
                (ToolUseHandler) (e, t, f, d) -> {
                    final Player player = e.getPlayer();
                    if (!player.isOp()) {
                        player.sendMessage(Networks.getLocalizationService().getString("messages.unsupported-operation.no_permission"));
                        return;
                    }

                    final Location location = e.getBlock().getLocation();
                    NetworksMain.worldeditPos2(player, location);

                    e.setCancelled(true);
                }
        );
    }
}