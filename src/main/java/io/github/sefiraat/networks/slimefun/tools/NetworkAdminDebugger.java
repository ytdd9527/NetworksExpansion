package io.github.sefiraat.networks.slimefun.tools;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.slimefun.network.AdminDebuggable;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@SuppressWarnings("deprecation")
public class NetworkAdminDebugger extends SlimefunItem {

    public NetworkAdminDebugger(ItemGroup itemGroup,
                                SlimefunItemStack item,
                                RecipeType recipeType,
                                ItemStack[] recipe
    ) {
        super(itemGroup, item, recipeType, recipe);

    }

    @Override
    public void preRegister() {
        addItemHandler((ItemUseHandler) this::onUse);
    }

    protected void onUse(PlayerRightClickEvent e) {
        final Optional<Block> optional = e.getClickedBlock();
        if (optional.isPresent()) {
            final Block block = optional.get();
            final Player player = e.getPlayer();
            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(block.getLocation());
            if (!player.isOp()) {
                player.sendMessage(Networks.getLocalizationService().getString("messages.unsupported-operation.debugger.player_is_not_op"));
                return;
            }
            if (slimefunItem instanceof AdminDebuggable debuggable) {
                debuggable.toggle(block.getLocation(), player);
                e.cancel();
            }
        }
    }
}
