package io.github.sefiraat.networks.managers;

import com.balugaq.netex.core.listeners.HangingBlockInteractListener;
import com.balugaq.netex.core.listeners.JEGCompatibleListener;
import com.ytdd9527.networksexpansion.core.listeners.CellMenuListener;
import com.ytdd9527.networksexpansion.core.listeners.CellnetExplosiveToolListener;
import com.ytdd9527.networksexpansion.core.listeners.ChainConfiguratorListener;
import com.ytdd9527.networksexpansion.core.listeners.ChatInputListener;
import com.ytdd9527.networksexpansion.core.listeners.CollectListener;
import com.ytdd9527.networksexpansion.core.listeners.ControllerDisplayListener;
import com.ytdd9527.networksexpansion.core.listeners.DriveCellOpenListener;
import com.ytdd9527.networksexpansion.core.listeners.DriveUniquenessListener;
import com.ytdd9527.networksexpansion.core.listeners.DriveWhitelistListener;
import com.ytdd9527.networksexpansion.core.listeners.EnderVoidGuardListener;
import com.ytdd9527.networksexpansion.core.listeners.NetworkCacheInvalidationListener;
import com.ytdd9527.networksexpansion.core.listeners.PersistenceCleanupListener;
import com.ytdd9527.networksexpansion.utils.GhostItems;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.listeners.ExplosiveToolListener;
import io.github.sefiraat.networks.listeners.SyncListener;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ListenerManager {

    public ListenerManager() {
        addListener(new ExplosiveToolListener());
        addListener(new SyncListener());
        if (Networks.getSupportedPluginManager().isJustEnoughGuide()) {
            // todo: remove and deprecate
            try {
                addListener(new JEGCompatibleListener());
            } catch (Throwable ignored) {
                Networks.getSupportedPluginManager().setJustEnoughGuide(false);
            }
        }
        addListener(new HangingBlockInteractListener());
        addListener(new CellMenuListener());
        addListener(new ChainConfiguratorListener());
        addListener(new DriveUniquenessListener());
        addListener(new DriveWhitelistListener());
        addListener(new CellnetExplosiveToolListener());
        // 元件网络三件套（装配/虚空/末影）基础设施监听
        addListener(new ChatInputListener());
        addListener(new NetworkCacheInvalidationListener());
        addListener(new DriveCellOpenListener());
        addListener(new EnderVoidGuardListener());
        addListener(new PersistenceCleanupListener());
        addListener(new GhostItems());
        addListener(new CollectListener());
        addListener(new ControllerDisplayListener());
    }

    private void addListener(@NotNull Listener listener) {
        Networks.getPluginManager().registerEvents(listener, Networks.getInstance());
    }
}
