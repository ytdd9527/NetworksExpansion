package com.balugaq.netex.core.listeners;

import com.balugaq.netex.api.interfaces.HangingBlock;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class HangingBlockInteractListener implements Listener {
    public static @NotNull Location toBlockLocation(@NotNull Location location) {
        Location c = location.toBlockLocation();
        c.setPitch(0);
        c.setYaw(0);
        return c;
    }

    @EventHandler
    public void onInteract(@NotNull PlayerItemFrameChangeEvent event) {
        HangingBlock hangingBlock = HangingBlock.getByItemFrame(event.getItemFrame());
        if (hangingBlock != null) {
            hangingBlock.onInteract(
                    toBlockLocation(event.getItemFrame()
                            .getLocation()
                            .getBlock()
                            .getRelative(event.getItemFrame().getAttachedFace())
                            .getLocation()),
                    event);
        }
    }

    @EventHandler
    public void onBreak(@NotNull EntityDeathEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame) {
            HangingBlock hangingBlock = HangingBlock.getByItemFrame(itemFrame);
            if (hangingBlock != null) {
                hangingBlock.breakBlock(itemFrame.getLocation().toBlockLocation(), itemFrame);
            }
        }
    }

    @EventHandler
    public void onHangingPlace(@NotNull HangingPlaceEvent event) {
        SlimefunItem sf = SlimefunItem.getByItem(event.getItemStack());
        if (sf instanceof HangingBlock hangingBlock && event.getEntity() instanceof ItemFrame itemFrame) {
            hangingBlock.onPlace(
                    event,
                    toBlockLocation(itemFrame
                            .getLocation()
                            .getBlock()
                            .getRelative(itemFrame.getAttachedFace())
                            .getLocation()),
                    itemFrame,
                    itemFrame.getAttachedFace());
        }
    }

    @EventHandler
    public void onHangingBreak(@NotNull HangingBreakEvent event) {
        if (!(event.getEntity() instanceof ItemFrame itemFrame)) {
            return;
        }

        HangingBlock hangingBlock = HangingBlock.getByItemFrame(itemFrame);
        if (hangingBlock != null) {
            hangingBlock.breakBlock(itemFrame.getLocation().toBlockLocation(), itemFrame);
        }
    }
}
