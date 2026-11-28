package com.ytdd9527.networksexpansion.core.listeners;

import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.Scheduling;
import com.ytdd9527.networksexpansion.api.assembly.AssemblyMonitorBridge;
import com.ytdd9527.networksexpansion.api.cell.CellMenu;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.implementation.machines.assembly.AssemblyMonitor;
import com.ytdd9527.networksexpansion.implementation.machines.converter.CellCleaner;
import com.ytdd9527.networksexpansion.implementation.machines.converter.CellConverter;
import com.ytdd9527.networksexpansion.implementation.machines.drive.CellDrive;
import com.ytdd9527.networksexpansion.implementation.machines.drive.DriveMonitor;
import com.ytdd9527.networksexpansion.utils.ChatInput;
import com.ytdd9527.networksexpansion.utils.Limits;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChatInputListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(@NotNull AsyncChatEvent e) {
        Player player = e.getPlayer();
        ChatInput.Pending pending = ChatInput.pollPending(player.getUniqueId());
        if (pending == null) {
            return;
        }
        e.setCancelled(true);

        String raw = PlainTextComponentSerializer.plainText().serialize(e.message()).trim();

        switch (pending.type()) {
            case TARGET -> handleTarget(player, pending, raw);
            case DRIVE_NAME -> handleDriveName(player, pending, raw);
            case MANAGER_DRIVE_NAME -> handleManagerDriveName(player, pending, raw);
            case MONITOR_SEARCH -> handleMonitorSearch(player, pending, raw);
        }
    }

    private void handleMonitorSearch(@NotNull Player player, @NotNull ChatInput.Pending pending, @NotNull String raw) {
        if (!(pending.context() instanceof ChatInput.SearchContext searchContext)) {
            return;
        }
        String term = raw.toLowerCase(java.util.Locale.ROOT).trim();
        if (term.length() > Limits.MAX_CHANNEL_CODEPOINTS) {
            player.sendMessage(Lang.getString(CellnetText.SEARCH_INVALID));
            return;
        }
        Location finalLocation = searchContext.location();
        java.util.UUID finalCellUuid = searchContext.cellUuid();
        ChatInput.SearchTarget target = searchContext.target();
        Scheduling.runTask(() -> {
            if (!player.isOnline()) {
                return;
            }
            switch (target) {
                case MONITOR -> AssemblyMonitor.applySearchFromChat(player, finalLocation, term);
                case DRIVE_BROWSER -> CellDrive.applyBrowserSearchFromChat(player, finalLocation, term);
                case DRIVE_MANAGER -> DriveMonitor.applySearchFromChat(player, finalLocation, term);
                case CELL_MENU -> CellMenu.applySearchFromChat(player, finalCellUuid, term);
                case CELL_CLEANER -> CellCleaner.applySearchFromChat(player, finalLocation, term);
                case CELL_CONVERTER -> CellConverter.applySearchFromChat(player, finalLocation, term);
            }
        });
    }

    private void handleDriveName(@NotNull Player player, @NotNull ChatInput.Pending pending, @NotNull String raw) {
        if (!(pending.context() instanceof ChatInput.DriveNameContext nameContext)) {
            return;
        }
        String name = raw.trim();
        if (name.isEmpty() || name.length() > Limits.MAX_CHANNEL_CODEPOINTS) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NAME_INVALID));
            return;
        }
        Location finalLocation = nameContext.location();
        Scheduling.runTask(() -> {
            if (!player.isOnline()) {
                return;
            }
            AssemblyMonitorBridge.setNameFromMonitor(player, finalLocation, name);
        });
    }

    private void handleManagerDriveName(@NotNull Player player, @NotNull ChatInput.Pending pending, @NotNull String raw) {
        if (!(pending.context() instanceof ChatInput.DriveNameContext nameContext)) {
            return;
        }
        String name = raw.trim();
        if (name.length() > Limits.MAX_CHANNEL_CODEPOINTS) {
            player.sendMessage(Lang.getString(CellnetText.MONITOR_NAME_INVALID));
            return;
        }
        Location finalLocation = nameContext.location();
        Scheduling.runTask(() -> {
            if (!player.isOnline()) {
                return;
            }
            DriveMonitor.applyNameFromChat(player, finalLocation, name);
        });
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        ChatInput.clear(e.getPlayer().getUniqueId());
    }

    private void handleTarget(@NotNull Player player, @NotNull ChatInput.Pending pending, @NotNull String raw) {
        if (!(pending.context() instanceof ChatInput.TargetContext targetContext)) {
            return;
        }
        Long value = parseNonNegativeLong(raw);
        if (value == null || value > Limits.MAX_CRAFT_TARGET) {
            player.sendMessage(Lang.getString(CellnetText.INPUT_TARGET_INVALID));
            return;
        }
        Location finalLocation = targetContext.location();
        int finalSlot = targetContext.blueprintSlot();
        boolean finalKeep = targetContext.keep();
        long target = value;
        Scheduling.runTask(() -> {
            if (!player.isOnline()) {
                return;
            }
            AssemblyMonitorBridge.handleTargetInput(finalLocation, finalSlot, target, finalKeep);
        });
    }

    @Nullable
    private static Long parseNonNegativeLong(@Nullable String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c < '0' || c > '9') {
                return null;
            }
        }
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
