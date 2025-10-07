package io.github.sefiraat.networks.slimefun.network;

import com.balugaq.netex.api.enums.FeedbackType;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import dev.sefiraat.sefilib.misc.ParticleUtils;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.managers.SupportedPluginManager;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.NetworkSlimefunItems;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
public class NetworkVacuum extends NetworkObject {

    private static final int[] INPUT_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};

    private final @NotNull ItemSetting<Integer> tickRate;
    private final @NotNull ItemSetting<Integer> vacuumRange;

    public NetworkVacuum(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.VACUUM);

        this.tickRate = new IntRangeSetting(this, "tick_rate", 1, 1, 10);
        this.vacuumRange = new IntRangeSetting(this, "vacuum_range", 1, 2, 5);
        addItemSetting(this.tickRate, this.vacuumRange);

        for (int inputSlot : INPUT_SLOTS) {
            this.getSlotsToDrop().add(inputSlot);
        }

        addItemHandler(new BlockTicker() {

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
                    tryAddItem(blockMenu);
                    Bukkit.getScheduler().runTask(Networks.getInstance(), bukkitTask -> findItem(blockMenu));
                }
            }

            @Override
            public void uniqueTick() {
                tick = tick <= 1 ? tickRate.getValue() : tick - 1;
            }
        });

        addItemHandler(new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@NotNull BlockPlaceEvent event) {
                NetworkStorage.removeNode(event.getBlock().getLocation());
                SlimefunBlockData blockData =
                    StorageCacheUtils.getBlock(event.getBlock().getLocation());
                if (blockData == null) {
                    return;
                }
                blockData.setData(
                    NetworkDirectional.OWNER_KEY,
                    event.getPlayer().getUniqueId().toString());
            }
        });
    }

    private void findItem(@NotNull BlockMenu blockMenu) {
        for (int inputSlot : INPUT_SLOTS) {
            final ItemStack inSlot = blockMenu.getItemInSlot(inputSlot);
            if (inSlot == null || inSlot.getType() == Material.AIR) {
                final Location location = blockMenu.getLocation().clone().add(0.5, 0.5, 0.5);
                final int range = this.vacuumRange.getValue();
                Collection<Entity> items =
                    location.getWorld().getNearbyEntities(location, range, range, range, Item.class::isInstance);

                if (items.isEmpty()) {
                    sendFeedback(blockMenu.getLocation(), FeedbackType.NO_ITEM_FOUND);
                    return;
                }

                for (Entity entity : items) {
                    if (!(entity instanceof Item item)) {
                        sendFeedback(blockMenu.getLocation(), FeedbackType.NO_ITEM_FOUND);
                        continue;
                    }

                    final String ownerUUID =
                        StorageCacheUtils.getData(blockMenu.getLocation(), NetworkDirectional.OWNER_KEY);
                    // There's no owner before... but the new ones has owner.
                    if (ownerUUID != null) {
                        final UUID uuid = UUID.fromString(ownerUUID);
                        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                        if (!Slimefun.getProtectionManager()
                            .hasPermission(offlinePlayer, item.getLocation(), Interaction.INTERACT_ENTITY)) {
                            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_PERMISSION);
                            return;
                        }
                    }

                    if (item.getPickupDelay() <= 0 && !SlimefunUtils.hasNoPickupFlag(item)) {
                        final ItemStack itemStack = item.getItemStack().clone();
                        final int amount = SupportedPluginManager.getStackAmount(item);
                        if (amount > itemStack.getMaxStackSize()) {
                            SupportedPluginManager.setStackAmount(item, amount - itemStack.getMaxStackSize());
                            itemStack.setAmount(itemStack.getMaxStackSize());
                        } else {
                            itemStack.setAmount(amount);
                            item.remove();
                        }
                        blockMenu.replaceExistingItem(inputSlot, itemStack);
                        ParticleUtils.displayParticleRandomly(item, 1, 5, new Particle.DustOptions(Color.BLUE, 1));
                        item.remove();
                        return;
                    }
                }
            }
        }
        sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
    }

    private void tryAddItem(@NotNull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        for (int inputSlot : INPUT_SLOTS) {
            final ItemStack itemStack = blockMenu.getItemInSlot(inputSlot);

            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            definition.getNode().getRoot().addItemStack0(blockMenu.getLocation(), itemStack);
        }
        sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                setSize(9);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                    || (NetworkSlimefunItems.NETWORK_VACUUM.canUse(player, false)
                    && Slimefun.getProtectionManager()
                    .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow == ItemTransportFlow.INSERT) {
                    return INPUT_SLOTS;
                }
                return new int[0];
            }
        };
    }
}
