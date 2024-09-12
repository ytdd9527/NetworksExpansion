package com.ytdd9527.networksexpansion.core.event;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetworksExpansionGuideOpenEvent extends PlayerInteractEvent {

    public NetworksExpansionGuideOpenEvent(@NotNull Player who, @NotNull Action action, @Nullable ItemStack item, @Nullable Block clickedBlock, @NotNull BlockFace clickedFace) {
        super(who, action, item, clickedBlock, clickedFace);
    }
}