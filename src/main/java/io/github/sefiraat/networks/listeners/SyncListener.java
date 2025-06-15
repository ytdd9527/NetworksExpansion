package io.github.sefiraat.networks.listeners;

import com.balugaq.netex.api.events.NetworksBlockBreakEvent;
import com.balugaq.netex.api.events.NetworksBlockPlaceEvent;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.utils.NetworkUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import java.text.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jetbrains.annotations.NotNull;

/*
 * Fix https://github.com/Sefiraat/Networks/issues/188
 * Fix https://github.com/Sefiraat/Networks/issues/192
 * Fix https://github.com/ytdd9527/NetworksExpansion/issues/119
 */
public class SyncListener implements Listener {
    private static final String S1 = "Listened BlockBreakEvent at {0}";
    private static final String S2 = "Listened BlockPlaceEvent at {0}";
    private static final String S3 = "Listened ChunkUnloadEvent at world: {0}, x: {1}, z: {2}";

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent e) {
        Networks.getInstance().debug(MessageFormat.format(S1, e.getBlock().getLocation()));
        NetworkUtils.clearNetwork(e.getBlock().getLocation());
        SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(e.getBlock().getLocation());
        if (slimefunItem != null && slimefunItem.getAddon() instanceof Networks) {
            NetworksBlockBreakEvent event = new NetworksBlockBreakEvent(e.getBlock(), e.getPlayer());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(@NotNull BlockPlaceEvent e) {
        Networks.getInstance().debug(MessageFormat.format(S2, e.getBlock().getLocation()));
        NetworkUtils.clearNetwork(e.getBlock().getLocation());
        SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(e.getBlock().getLocation());
        if (slimefunItem != null && slimefunItem.getAddon() instanceof Networks) {
            NetworksBlockPlaceEvent event = new NetworksBlockPlaceEvent(e.getBlock(), e.getPlayer());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChunkUnload(@NotNull ChunkUnloadEvent e) {
        Networks.getInstance()
                .debug(MessageFormat.format(
                        S3,
                        e.getWorld().getName(),
                        e.getChunk().getX(),
                        e.getChunk().getZ()));
        NetworkStorage.unregisterChunk(e.getChunk());
    }
}
