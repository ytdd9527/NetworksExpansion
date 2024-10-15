package io.github.sefiraat.networks.slimefun.tools;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.slimefun.network.NetworkController;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;

public class NetworkCrayon extends SlimefunItem {

    public NetworkCrayon(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemHandler(
                (ItemUseHandler) e -> {
                    final Optional<Block> optional = e.getClickedBlock();
                    if (optional.isPresent()) {
                        final Block block = optional.get();
                        final Player player = e.getPlayer();
                        final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(block.getLocation());
                        if (slimefunItem instanceof NetworkController) {
                            toggleCrayon(block, player);
                            e.cancel();
                        }
                    }
                }
        );
    }

    public void toggleCrayon(@Nonnull Block block, @Nonnull Player player) {
        if (NetworkController.hasCrayon(block.getLocation())) {
            NetworkController.removeCrayon(block.getLocation());
            player.sendMessage(Networks.getLocalizationService().getString("messages.completed-operation.crayon.disabled"));
        } else {
            NetworkController.addCrayon(block.getLocation());
            player.sendMessage(Networks.getLocalizationService().getString("messages.completed-operation.crayon.enabled"));
        }
    }
}
