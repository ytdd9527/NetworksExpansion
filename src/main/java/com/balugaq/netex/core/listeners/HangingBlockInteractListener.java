package com.balugaq.netex.core.listeners;

import com.balugaq.netex.api.interfaces.HangingBlock;
import com.balugaq.netex.utils.Debug;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class HangingBlockInteractListener implements Listener {
    @EventHandler
    public void onInteract(PlayerItemFrameChangeEvent event) {
        var hangingBlock = HangingBlock.getByItemFrame(event.getItemFrame());
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

    public static Location toBlockLocation(Location location) {
        var c = location.toBlockLocation();
        c.setPitch(0);
        c.setYaw(0);
        return c;
    }

    @EventHandler
    public void onBreak(EntityDeathEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame) {
            var hangingBlock = HangingBlock.getByItemFrame(itemFrame);
            if (hangingBlock != null) {
                hangingBlock.onBreak(itemFrame.getLocation().toBlockLocation(), itemFrame);
            }
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
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
    public void onHangingBreak(HangingBreakEvent event) {
        if (!(event.getEntity() instanceof ItemFrame itemFrame)) {
            return;
        }

        var hangingBlock = HangingBlock.getByItemFrame(itemFrame);
        if (hangingBlock != null) {
            hangingBlock.onBreak(itemFrame.getLocation().toBlockLocation(), itemFrame);
        }
    }
}
