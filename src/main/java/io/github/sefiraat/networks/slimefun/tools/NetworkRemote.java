package io.github.sefiraat.networks.slimefun.tools;

import com.jeff_media.morepersistentdatatypes.DataType;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.NetworkGridNewStyle;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.slimefun.network.grid.NetworkCraftingGrid;
import io.github.sefiraat.networks.slimefun.network.grid.NetworkGrid;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.Theme;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Optional;

public class NetworkRemote extends SlimefunItem {

    private static final String WIKI_PAGE = "tools/network-remote";

    private static final NamespacedKey KEY = Keys.newKey("location");
    private static final int[] RANGES = new int[]{
            150,
            500,
            0,
            -1
    };

    private final int range;

    public NetworkRemote(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int range) {
        super(itemGroup, item, recipeType, recipe);
        this.range = range;
        addItemHandler(
                (ItemUseHandler) e -> {
                    final Player player = e.getPlayer();
                    if (player.isSneaking()) {
                        final Optional<Block> optional = e.getClickedBlock();
                        if (optional.isPresent()) {
                            final Block block = optional.get();
                            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(block.getLocation());
                            if (Slimefun.getProtectionManager().hasPermission(player, block, Interaction.INTERACT_BLOCK)
                                    && (slimefunItem instanceof NetworkGrid ||
                                    slimefunItem instanceof NetworkCraftingGrid ||
                                    slimefunItem instanceof NetworkGridNewStyle
                            )) {
                                setGrid(e.getItem(), block, player);
                            } else {
                                player.sendMessage(Networks.getLocalizationService().getString("messages.unsupported-operation.remote.must_connect_to_grid"));
                            }
                        }
                    } else {
                        tryOpenGrid(e.getItem(), player, NetworkRemote.this.range);
                    }
                    e.cancel();
                }
        );
    }

    public static void setGrid(@Nonnull ItemStack itemStack, @Nonnull Block block, @Nonnull Player player) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        DataTypeMethods.setCustom(itemMeta, KEY, DataType.LOCATION, block.getLocation());
        itemStack.setItemMeta(itemMeta);
        player.sendMessage(Networks.getLocalizationService().getString("messages.completed-operation.remote.bound_to_grid"));
    }

    public static void tryOpenGrid(@Nonnull ItemStack itemStack, @Nonnull Player player, int range) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final Location location = DataTypeMethods.getCustom(itemMeta, KEY, DataType.LOCATION);

        if (location != null) {

            if (!location.getWorld().isChunkLoaded(location.getBlockX() / 16, location.getBlockZ() / 16)) {
                player.sendMessage(Networks.getLocalizationService().getString("messages.unsupported-operation.remote.grid_not_loaded"));
                return;
            }

            final boolean sameDimension = location.getWorld().equals(player.getWorld());

            if (range == -1
                    || range == 0 && sameDimension
                    || sameDimension && player.getLocation().distance(location) <= range
            ) {
                openGrid(location, player);
            } else {
                player.sendMessage(Networks.getLocalizationService().getString("messages.unsupported-operation.remote.grid_not_in_range"));
            }
        } else {
            player.sendMessage(Networks.getLocalizationService().getString("messages.unsupported-operation.remote.no_grid_bound"));
        }
    }

    public static void openGrid(@Nonnull Location location, @Nonnull Player player) {
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(location);
        SlimefunItem item = SlimefunItem.getById(blockData.getSfId());
        StorageCacheUtils.executeAfterLoad(blockData, () -> {
            if ((item instanceof NetworkGrid || item instanceof NetworkCraftingGrid || item instanceof NetworkGridNewStyle)
                    && (player.hasPermission("slimefun.inventory.bypass") || Slimefun.getProtectionManager().hasPermission(player, location, Interaction.INTERACT_BLOCK))) {
                BlockMenu blockMenu = blockData.getBlockMenu();
                if (blockMenu != null) {
                    blockMenu.open(player);
                }
            } else {
                player.sendMessage(Networks.getLocalizationService().getString("messages.unsupported-operation.remote.not_a_grid_found"));
            }
        }, false);
    }

    public static int[] getRanges() {
        return RANGES;
    }

    public int getRange() {
        return this.range;
    }

    @Override
    public void postRegister() {
        addWikiPage(WIKI_PAGE);
    }
}
