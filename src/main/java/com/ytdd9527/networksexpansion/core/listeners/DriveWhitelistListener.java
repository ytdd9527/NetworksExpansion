package com.ytdd9527.networksexpansion.core.listeners;

import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.Scheduling;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.drive.DriveWhitelistMenu;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DriveWhitelistListener implements Listener {

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        DriveWhitelistMenu.stopAdding(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(@NotNull AsyncChatEvent e) {
        Player player = e.getPlayer();
        UUID playerUuid = player.getUniqueId();
        Location location = DriveWhitelistMenu.getAddingLocation(playerUuid);
        if (location == null) {
            return;
        }
        e.setCancelled(true);
        DriveWhitelistMenu.stopAdding(playerUuid);

        String name = PlainTextComponentSerializer.plainText().serialize(e.message()).trim();
        if (name.isEmpty()) {
            player.sendMessage(Lang.getString(CellnetText.DRIVE_WHITELIST_NAME_INVALID));
            return;
        }
        if (name.length() > 16) {
            player.sendMessage(Lang.getString(CellnetText.DRIVE_WHITELIST_NAME_TOO_LONG));
            return;
        }

        Scheduling.runTask(() -> {
            if (!player.isOnline()) {
                return;
            }
            DriveWhitelistMenu.addWhitelistPlayer(player, location, name);
        });
    }
}