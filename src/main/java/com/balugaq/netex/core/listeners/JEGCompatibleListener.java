package com.balugaq.netex.core.listeners;

import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import lombok.SneakyThrows;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class JEGCompatibleListener implements Listener {
    public static final Map<UUID, GuideHistory> GUIDE_HISTORY = new ConcurrentHashMap<>();
    public static final Map<UUID, BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile>> PROFILE_CALLBACKS =
            new ConcurrentHashMap<>();

    public static void addCallback(
            @NotNull UUID uuid, @NotNull BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile> callback) {
        PROFILE_CALLBACKS.put(uuid, callback);
    }

    public static void removeCallback(@NotNull UUID uuid) {
        PROFILE_CALLBACKS.remove(uuid);
    }

    @SneakyThrows
    @NotNull public static PlayerProfile getPlayerProfile(@NotNull OfflinePlayer player) {
        // Shouldn't be null;
        return PlayerProfile.find(player).orElseThrow(() -> new RuntimeException("PlayerProfile not found"));
    }

    public static void tagGuideOpen(@NotNull Player player) {
        if (!PROFILE_CALLBACKS.containsKey(player.getUniqueId())) {
            return;
        }

        PlayerProfile profile = getPlayerProfile(player);
        saveOriginGuideHistory(profile);
        clearGuideHistory(profile);
    }

    private static void saveOriginGuideHistory(@NotNull PlayerProfile profile) {
        GuideHistory oldHistory = profile.getGuideHistory();
        GuideHistory newHistory = new GuideHistory(profile);
        ReflectionUtil.setValue(newHistory, "mainMenuPage", oldHistory.getMainMenuPage());
        LinkedList<?> queue = ReflectionUtil.getValue(oldHistory, "queue", LinkedList.class);
        ReflectionUtil.setValue(newHistory, "queue", queue != null ? queue.clone() : new LinkedList<>());
        GUIDE_HISTORY.put(profile.getUUID(), newHistory);
    }

    private static void clearGuideHistory(@NotNull PlayerProfile profile) {
        ReflectionUtil.setValue(profile, "guideHistory", new GuideHistory(profile));
    }

    @EventHandler
    public void onJEGItemClick(GuideEvents.@NotNull ItemButtonClickEvent event) {
        Player player = event.getPlayer();
        if (!PROFILE_CALLBACKS.containsKey(player.getUniqueId())) {
            return;
        }

        PlayerProfile profile = getPlayerProfile(player);
        rollbackGuideHistory(profile);
        PROFILE_CALLBACKS.get(player.getUniqueId()).accept(event, profile);
        PROFILE_CALLBACKS.remove(player.getUniqueId());
    }

    private void rollbackGuideHistory(@NotNull PlayerProfile profile) {
        GuideHistory originHistory = GUIDE_HISTORY.get(profile.getUUID());
        if (originHistory == null) {
            return;
        }

        ReflectionUtil.setValue(profile, "guideHistory", originHistory);
    }
}
