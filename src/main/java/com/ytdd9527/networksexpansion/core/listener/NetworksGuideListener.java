package com.ytdd9527.networksexpansion.core.listener;

import com.balugaq.netex.api.event.NetworksExpansionGuideOpenEvent;
import com.ytdd9527.networksexpansion.implementation.ExpansionItemStacks;
import com.balugaq.netex.utils.GuideUtil;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class NetworksGuideListener implements Listener {
    @EventHandler
    public void onInteract(PlayerRightClickEvent e) {
        ItemStack item = e.getItem();
        if (item != null && item.getType() != Material.AIR) {
            if (!e.getPlayer().isOp()) {
                return;
            }

            if (Objects.equals(ItemStackHelper.getDisplayName(item), ExpansionItemStacks.NETWORKS_EXPANSION_SURVIVAL_GUIDE.getDisplayName())) {
                NetworksExpansionGuideOpenEvent event = new NetworksExpansionGuideOpenEvent(e.getPlayer(), SlimefunGuideMode.SURVIVAL_MODE);
                Bukkit.getPluginManager().callEvent(event);
            }

            if (e.getPlayer().isOp() && Objects.equals(ItemStackHelper.getDisplayName(item), ExpansionItemStacks.NTW_EXPANSION_CREATIVE_GUIDE.getDisplayName())) {
                NetworksExpansionGuideOpenEvent event = new NetworksExpansionGuideOpenEvent(e.getPlayer(), SlimefunGuideMode.CHEAT_MODE);
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }

    @EventHandler
    public void onGuide(NetworksExpansionGuideOpenEvent e) {
        if (!e.isCancelled()) {
            e.setCancelled(true);

            openGuide(e.getPlayer(), e.getMode());
        }
    }

    public void openGuide(Player player, SlimefunGuideMode mode) {
        Optional<PlayerProfile> optional = PlayerProfile.find(player);

        if (optional.isPresent()) {
            PlayerProfile profile = optional.get();
            SlimefunGuideImplementation guide = GuideUtil.getGuide(player, mode);
            profile.getGuideHistory().openLastEntry(guide);
        } else {
            GuideUtil.openMainMenuAsync(player, mode, 1);
        }
    }
}
