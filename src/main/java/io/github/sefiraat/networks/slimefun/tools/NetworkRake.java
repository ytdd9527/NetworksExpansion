package io.github.sefiraat.networks.slimefun.tools;

import com.balugaq.netex.api.interfaces.ModelledItem;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.Keys;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.LimitedUseItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NetworkRake extends LimitedUseItem {

    private static final String WIKI_PAGE = "Network-Rake";

    private static final NamespacedKey key = Keys.newKey("uses");

    public NetworkRake(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe,
            int amount) {
        super(itemGroup, item, recipeType, recipe);
        setMaxUseCount(amount);
    }

    @Override
    public void preRegister() {
        addItemHandler((ItemUseHandler) this::onUse);
    }

    /**
     * This returns the {@link ItemHandler} that will be added to this {@link SlimefunItem}.
     *
     * @return The {@link ItemHandler} that should be added to this {@link SlimefunItem}
     */
    @NotNull @Override
    public ItemUseHandler getItemHandler() {
        return this::onUse;
    }

    protected void onUse(@NotNull PlayerRightClickEvent e) {
        e.cancel();
        final Optional<Block> optional = e.getClickedBlock();
        if (optional.isPresent()) {
            final Block block = optional.get();
            final Player player = e.getPlayer();
            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(block.getLocation());
            if ((slimefunItem instanceof NetworkObject || slimefunItem instanceof ModelledItem)
                    && Slimefun.getProtectionManager().hasPermission(player, block, Interaction.BREAK_BLOCK)) {
                final BlockBreakEvent event = new BlockBreakEvent(block, player);
                Networks.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }

                block.setType(Material.AIR);
                Slimefun.getDatabaseManager().getBlockDataController().removeBlock(block.getLocation());
                damageItem(e.getPlayer(), e.getItem());
            }
        }
    }

    @Override
    protected @NotNull NamespacedKey getStorageKey() {
        return key;
    }

    @Override
    public void postRegister() {
        addWikiPage(WIKI_PAGE);
    }
}
