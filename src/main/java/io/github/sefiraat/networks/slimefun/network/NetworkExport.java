package io.github.sefiraat.networks.slimefun.network;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.helpers.Icon;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.slimefun.NetworkSlimefunItems;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import java.util.List;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NetworkExport extends NetworkObject {

    private static final int[] BACKGROUND_SLOTS = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 17, 18, 22, 26, 27, 31, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44
    };
    private static final int TEST_ITEM_SLOT = 20;
    private static final int[] TEST_ITEM_BACKDROP = {10, 11, 12, 19, 21, 28, 29, 30};
    private static final int OUTPUT_ITEM_SLOT = 24;
    private static final int[] OUTPUT_ITEM_BACKDROP = {14, 15, 16, 23, 25, 32, 33, 34};

    private final @NotNull ItemSetting<Integer> tickRate;

    public NetworkExport(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.EXPORT);
        this.tickRate = new IntRangeSetting(this, "tick_rate", 1, 1, 10);
        addItemSetting(this.tickRate);

        this.getSlotsToDrop().add(TEST_ITEM_SLOT);
        this.getSlotsToDrop().add(OUTPUT_ITEM_SLOT);

        addItemHandler(
                new BlockTicker() {

                    private int tick = 1;

                    @Override
                    public boolean isSynchronized() {
                        return false;
                    }

                    @Override
                    public void tick(@NotNull Block block, SlimefunItem item, @NotNull SlimefunBlockData data) {
                        if (tick <= 1) {
                            final BlockMenu blockMenu = data.getBlockMenu();
                            if (blockMenu == null) {
                                return;
                            }
                            addToRegistry(block);
                            tryFetchItem(blockMenu);
                        }
                    }

                    @Override
                    public void uniqueTick() {
                        tick = tick <= 1 ? tickRate.getValue() : tick - 1;
                    }
                },
                new BlockBreakHandler(true, true) {
                    @Override
                    public void onPlayerBreak(
                            @NotNull BlockBreakEvent e, @NotNull ItemStack item, @NotNull List<ItemStack> drops) {
                        BlockMenu blockMenu =
                                StorageCacheUtils.getMenu(e.getBlock().getLocation());
                        if (blockMenu == null) {
                            return;
                        }
                        blockMenu.dropItems(blockMenu.getLocation(), TEST_ITEM_SLOT);
                        blockMenu.dropItems(blockMenu.getLocation(), OUTPUT_ITEM_SLOT);
                    }
                });
    }

    private void tryFetchItem(@NotNull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        ItemStack testItem = blockMenu.getItemInSlot(TEST_ITEM_SLOT);
        ItemStack itemInOutput = blockMenu.getItemInSlot(OUTPUT_ITEM_SLOT);

        if (testItem == null || (itemInOutput != null && itemInOutput.getType() != Material.AIR)) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_TEMPLATE_FOUND);
            return;
        }

        ItemStack clone = testItem.clone();

        ItemRequest itemRequest = new ItemRequest(clone, clone.getMaxStackSize());
        ItemStack retrieved = definition.getNode().getRoot().getItemStack0(blockMenu.getLocation(), itemRequest);
        if (retrieved != null) {
            blockMenu.pushItem(retrieved, OUTPUT_ITEM_SLOT);
        }
        sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                drawBackground(BACKGROUND_SLOTS);
                drawBackground(Icon.EXPORT_TEMPLATE_BACKGROUND_STACK, TEST_ITEM_BACKDROP);
                drawBackground(Icon.EXPORT_OUTPUT_BACKGROUND_STACK, OUTPUT_ITEM_BACKDROP);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                        || (NetworkSlimefunItems.NETWORK_EXPORT.canUse(player, false)
                                && Slimefun.getProtectionManager()
                                        .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow == ItemTransportFlow.WITHDRAW) {
                    return new int[] {OUTPUT_ITEM_SLOT};
                }
                return new int[0];
            }
        };
    }
}
