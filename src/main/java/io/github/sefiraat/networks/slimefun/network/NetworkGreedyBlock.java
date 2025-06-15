package io.github.sefiraat.networks.slimefun.network;

import com.balugaq.netex.api.helpers.Icon;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.NetworkSlimefunItems;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NetworkGreedyBlock extends NetworkObject {

    public static final int TEMPLATE_SLOT = 10;
    public static final int INPUT_SLOT = 16;
    private static final int[] BACKGROUND_SLOTS = new int[] {3, 4, 5, 12, 13, 14, 21, 22, 23};
    private static final int[] BACKGROUND_SLOTS_TEMPLATE = new int[] {0, 1, 2, 9, 11, 18, 19, 20};
    private static final int[] BACKGROUND_SLOTS_INPUT = new int[] {6, 7, 8, 15, 17, 24, 25, 26};

    public NetworkGreedyBlock(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.GREEDY_BLOCK);
        this.getSlotsToDrop().add(INPUT_SLOT);
        this.getSlotsToDrop().add(TEMPLATE_SLOT);
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                drawBackground(BACKGROUND_SLOTS);
                drawBackground(Icon.GREEDY_TEMPLATE_BACKGROUND_STACK, BACKGROUND_SLOTS_TEMPLATE);
                drawBackground(Icon.STORAGE_BACKGROUND_STACK, BACKGROUND_SLOTS_INPUT);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                        || (NetworkSlimefunItems.NETWORK_GREEDY_BLOCK.canUse(player, false)
                                && Slimefun.getProtectionManager()
                                        .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow == ItemTransportFlow.WITHDRAW) {
                    return new int[] {INPUT_SLOT};
                }
                return new int[0];
            }
        };
    }
}
