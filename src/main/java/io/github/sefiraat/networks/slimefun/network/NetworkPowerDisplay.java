package io.github.sefiraat.networks.slimefun.network;

import com.balugaq.netex.api.helpers.Icon;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.NetworkSlimefunItems;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public class NetworkPowerDisplay extends NetworkObject {

    private static final int[] BACKGROUND_SLOTS = new int[]{
            0, 1, 2, 3, 5, 6, 7, 8
    };
    private static final int DISPLAY_SLOT = 4;

    public NetworkPowerDisplay(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.POWER_DISPLAY);
        addItemHandler(
                new BlockTicker() {
                    @Override
                    public boolean isSynchronized() {
                        return false;
                    }

                    @Override
                    public void tick(Block block, SlimefunItem slimefunItem, SlimefunBlockData data) {
                        BlockMenu blockMenu = data.getBlockMenu();
                        if (blockMenu != null) {
                            addToRegistry(block);
                            setDisplay(blockMenu);
                        }
                    }
                }
        );
    }

    private static ItemStack getChargeStack(long charge) {
        return ItemStackUtil.getCleanItem(new CustomItemStack(
                Material.GREEN_STAINED_GLASS_PANE,
                Networks.getLocalizationService().getString("icons.power_display.name"),
                String.format(Networks.getLocalizationService().getString("icons.power_display.charge"), String.valueOf(charge))
        ));
    }

    private void setDisplay(BlockMenu blockMenu) {
        if (blockMenu.hasViewer()) {
            final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

            if (definition.getNode() == null) {
                blockMenu.replaceExistingItem(DISPLAY_SLOT, Icon.POWER_DISPLAY_EMPTY);
                return;
            }

            final NetworkRoot root = definition.getNode().getRoot();
            final long networkCharge = root.getRootPower();
            blockMenu.replaceExistingItem(DISPLAY_SLOT, getChargeStack(networkCharge));
        }
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                drawBackground(BACKGROUND_SLOTS);
                addItem(DISPLAY_SLOT, getChargeStack(0), (player, i, itemStack, clickAction) -> false);
            }

            @Override
            public boolean canOpen(@Nonnull Block block, @Nonnull Player player) {
                return player.hasPermission("slimefun.inventory.bypass") || (NetworkSlimefunItems.NETWORK_POWER_DISPLAY.canUse(player, false)
                        && Slimefun.getProtectionManager().hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
    }
}
