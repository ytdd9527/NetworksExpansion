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
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import java.util.HashMap;
import java.util.Map;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NetworkWirelessTransmitter extends NetworkObject {

    public static final int TEMPLATE_SLOT = 13;

    private static final int[] BACKGROUND_SLOTS =
            new int[] {0, 1, 2, 6, 7, 8, 9, 10, 11, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};

    private static final int[] BACKGROUND_SLOTS_TEMPLATE = new int[] {3, 4, 5, 12, 14, 21, 22, 23};

    private static final String LINKED_LOCATION_KEY_X = "linked-location-x";
    private static final String LINKED_LOCATION_KEY_Y = "linked-location-y";
    private static final String LINKED_LOCATION_KEY_Z = "linked-location-z";

    private static final int REQUIRED_POWER = 500;
    private static final int TICKS_PER = 2;

    private final Map<Location, Location> linkedLocations = new HashMap<>();

    public NetworkWirelessTransmitter(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.WIRELESS_TRANSMITTER);
        this.getSlotsToDrop().add(TEMPLATE_SLOT);

        addItemHandler(new BlockTicker() {
            private final Map<Location, Integer> tickMap = new HashMap<>();
            private final Map<Location, Boolean> firstTick = new HashMap<>();

            @Override
            public boolean isSynchronized() {
                return false;
            }

            @Override
            public void tick(@NotNull Block block, SlimefunItem slimefunItem, @NotNull SlimefunBlockData data) {
                BlockMenu blockMenu = data.getBlockMenu();
                if (blockMenu != null) {
                    addToRegistry(block);

                    boolean isFirstTick = firstTick.getOrDefault(block.getLocation(), true);
                    if (isFirstTick) {
                        final String xString = data.getData(LINKED_LOCATION_KEY_X);
                        final String yString = data.getData(LINKED_LOCATION_KEY_Y);
                        final String zString = data.getData(LINKED_LOCATION_KEY_Z);
                        if (xString != null && yString != null && zString != null) {
                            final Location linkedLocation = new Location(
                                    block.getWorld(),
                                    Integer.parseInt(xString),
                                    Integer.parseInt(yString),
                                    Integer.parseInt(zString));
                            linkedLocations.put(block.getLocation(), linkedLocation);
                        }
                        firstTick.put(block.getLocation(), false);
                    }

                    int tick = tickMap.getOrDefault(block.getLocation(), 0);
                    if (tick >= TICKS_PER) {
                        onTick(blockMenu);
                        tickMap.remove(block.getLocation());
                        tick = 0;
                    } else {
                        tick++;
                    }
                    tickMap.put(block.getLocation(), tick + 1);
                }
            }
        });
    }

    private void onTick(@NotNull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        final Location location = blockMenu.getLocation();
        final Location linkedLocation = linkedLocations.get(location);

        if (linkedLocation == null) {
            sendFeedback(location, FeedbackType.NO_LINKED_LOCATION_FOUND);
            return;
        }

        final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(linkedLocation);

        if (!(slimefunItem instanceof NetworkWirelessReceiver)) {
            linkedLocations.remove(location);
            return;
        }

        final BlockMenu linkedBlockMenu = StorageCacheUtils.getMenu(linkedLocation);
        if (linkedBlockMenu == null) {
            sendFeedback(location, FeedbackType.NO_LINKED_BLOCK_MENU_FOUND);
            return;
        }

        final ItemStack itemStack = linkedBlockMenu.getItemInSlot(NetworkWirelessReceiver.RECEIVED_SLOT);

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            final ItemStack templateStack = blockMenu.getItemInSlot(TEMPLATE_SLOT);

            if (templateStack == null || templateStack.getType() == Material.AIR) {
                sendFeedback(location, FeedbackType.NO_TEMPLATE_FOUND);
                return;
            }

            if (definition.getNode().getRoot().getRootPower() < REQUIRED_POWER) {
                sendFeedback(location, FeedbackType.NOT_ENOUGH_POWER);
                return;
            }

            final ItemStack stackToPush = definition
                    .getNode()
                    .getRoot()
                    .getItemStack0(
                            blockMenu.getLocation(),
                            new ItemRequest(templateStack.clone(), templateStack.getMaxStackSize()));

            if (stackToPush != null) {
                definition.getNode().getRoot().removeRootPower(REQUIRED_POWER);
                linkedBlockMenu.pushItem(stackToPush, NetworkWirelessReceiver.RECEIVED_SLOT);
                sendFeedback(location, FeedbackType.WORKING);
                if (definition.getNode().getRoot().isDisplayParticles()) {
                    final Location particleLocation =
                            blockMenu.getLocation().clone().add(0.5, 1.1, 0.5);
                    final Location particleLocation2 =
                            linkedBlockMenu.getLocation().clone().add(0.5, 2.1, 0.5);
                    particleLocation.getWorld().spawnParticle(Particle.WAX_ON, particleLocation, 0, 0, 4, 0);
                    particleLocation2.getWorld().spawnParticle(Particle.WAX_OFF, particleLocation2, 0, 0, -4, 0);
                }
            }
        }
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                drawBackground(BACKGROUND_SLOTS);
                drawBackground(Icon.TRANSMITTER_TEMPLATE_BACKGROUND_STACK, BACKGROUND_SLOTS_TEMPLATE);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                        || (NetworkSlimefunItems.NETWORK_WIRELESS_TRANSMITTER.canUse(player, false)
                                && Slimefun.getProtectionManager()
                                        .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
    }

    @Override
    protected void onBreak(@NotNull BlockBreakEvent event) {
        super.onBreak(event);
        linkedLocations.remove(event.getBlock().getLocation());
    }

    public void addLinkedLocation(@NotNull Block block, @NotNull Location linkedLocation) {
        linkedLocations.put(block.getLocation(), linkedLocation);
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(block.getLocation());
        if (blockData == null) {
            return;
        }
        blockData.setData(LINKED_LOCATION_KEY_X, String.valueOf(linkedLocation.getBlockX()));
        blockData.setData(LINKED_LOCATION_KEY_Y, String.valueOf(linkedLocation.getBlockY()));
        blockData.setData(LINKED_LOCATION_KEY_Z, String.valueOf(linkedLocation.getBlockZ()));
    }
}
