package com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.line.basic;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.enums.MinecraftVersion;
import com.balugaq.netex.api.interfaces.Configurable;
import com.balugaq.netex.utils.Lang;
import com.bgsoftware.wildchests.api.WildChestsAPI;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LineTransferVanillaGrabber extends NetworkDirectional implements RecipeDisplayItem, Configurable {
    private static final int DEFAULT_MAX_DISTANCE = 32;
    private static final int DEFAULT_GRAB_ITEM_TICK = 1;
    private static final int[] BACKGROUND_SLOTS = new int[] {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 15, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27, 28, 30, 31, 33, 34, 35,
        36, 37, 38, 39, 40, 41, 42, 43, 44
    };
    private static final int NORTH_SLOT = 11;
    private static final int SOUTH_SLOT = 29;
    private static final int EAST_SLOT = 21;
    private static final int WEST_SLOT = 19;
    private static final int UP_SLOT = 14;
    private static final int DOWN_SLOT = 32;

    private static int maxDistance;
    private static int grabItemTick;
    private final HashMap<Location, Integer> TICKER_MAP = new HashMap<>();

    public LineTransferVanillaGrabber(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.LINE_TRANSFER_VANILLA_GRABBER);
        loadConfigurations();
    }

    public void loadConfigurations() {
        String configKey = getId();
        FileConfiguration config = Networks.getInstance().getConfig();

        maxDistance = config.getInt("items." + configKey + ".max-distance", DEFAULT_MAX_DISTANCE);
        grabItemTick = config.getInt("items." + configKey + ".grabitem-tick", DEFAULT_GRAB_ITEM_TICK);
    }

    @Override
    protected void onTick(@Nullable BlockMenu blockMenu, @NotNull Block block) {
        super.onTick(blockMenu, block);

        if (blockMenu == null) {
            sendFeedback(block.getLocation(), FeedbackType.INVALID_BLOCK);
            return;
        }
        final Location location = blockMenu.getLocation();
        if (grabItemTick != 1) {
            int tickCounter = getTickCounter(location);
            tickCounter = (tickCounter + 1) % grabItemTick;

            if (tickCounter == 0) {
                tryGrabItem(blockMenu);
            }

            updateTickCounter(location, tickCounter);
        } else {
            tryGrabItem(blockMenu);
        }
    }

    private int getTickCounter(Location location) {
        final Integer ticker = TICKER_MAP.get(location);
        if (ticker == null) {
            TICKER_MAP.put(location, 0);
            return 0;
        }
        return ticker;
    }

    private void updateTickCounter(Location location, int tickCounter) {
        TICKER_MAP.put(location, tickCounter);
    }

    @SuppressWarnings("removal")
    private void tryGrabItem(@NotNull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        final NetworkRoot root = definition.getNode().getRoot();
        final BlockFace direction = getCurrentDirection(blockMenu);

        // Fix for early vanilla pusher release
        final Block block = blockMenu.getBlock();
        final String ownerUUID = StorageCacheUtils.getData(block.getLocation(), OWNER_KEY);
        if (ownerUUID == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_OWNER_FOUND);
            return;
        }
        final UUID uuid = UUID.fromString(ownerUUID);
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        // dirty fix
        Block targetBlock = block.getRelative(direction);
        for (int d = 0; d <= maxDistance; d++) {
            try {
                if (!Slimefun.getProtectionManager()
                        .hasPermission(offlinePlayer, targetBlock, Interaction.INTERACT_BLOCK)) {
                    sendFeedback(blockMenu.getLocation(), FeedbackType.NO_PERMISSION);
                    break;
                }
            } catch (NullPointerException ex) {
                sendFeedback(blockMenu.getLocation(), FeedbackType.ERROR_OCCURRED);
                break;
            }

            final BlockState blockState = targetBlock.getState();

            if (!(blockState instanceof InventoryHolder holder)) {
                sendFeedback(blockMenu.getLocation(), FeedbackType.NO_INVENTORY_FOUND);
                return;
            }

            boolean wildChests = Networks.getSupportedPluginManager().isWildChests();
            boolean isChest = wildChests && WildChestsAPI.getChest(targetBlock.getLocation()) != null;

            if (wildChests && isChest) {
                sendFeedback(blockMenu.getLocation(), FeedbackType.PROTECTED_BLOCK);
                continue;
            }

            final Inventory inventory = holder.getInventory();

            if (inventory instanceof FurnaceInventory furnaceInventory) {
                final ItemStack furnaceInventoryResult = furnaceInventory.getResult();
                final ItemStack furnaceInventoryFuel = furnaceInventory.getFuel();
                grabItem(root, blockMenu, furnaceInventoryResult);

                if (furnaceInventoryFuel != null && furnaceInventoryFuel.getType() == Material.BUCKET) {
                    grabItem(root, blockMenu, furnaceInventoryFuel);
                }

            } else if (inventory instanceof BrewerInventory brewerInventory) {
                for (int i = 0; i < 3; i++) {
                    final ItemStack stack = brewerInventory.getContents()[i];
                    if (stack != null && stack.getType() == Material.POTION) {
                        final PotionMeta potionMeta = (PotionMeta) stack.getItemMeta();
                        if (Networks.getInstance().getMCVersion().isAtLeast(MinecraftVersion.MC1_20_5)) {
                            if (potionMeta.getBasePotionType() != PotionType.WATER) {
                                grabItem(root, blockMenu, stack);
                                break;
                            }
                        } else {
                            PotionData bpd = potionMeta.getBasePotionData();
                            if (bpd != null && bpd.getType() != PotionType.WATER) {
                                grabItem(root, blockMenu, stack);
                                break;
                            }
                        }
                    }
                }
            } else {
                for (ItemStack stack : inventory.getContents()) {
                    if (grabItem(root, blockMenu, stack)) {
                        break;
                    }
                }
            }
            targetBlock = targetBlock.getRelative(direction);
        }
        sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
    }

    private boolean grabItem(@NotNull NetworkRoot root, @NotNull BlockMenu blockMenu, @Nullable ItemStack stack) {
        if (stack != null && stack.getType() != Material.AIR) {
            root.addItemStack0(blockMenu.getLocation(), stack);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected int @NotNull [] getBackgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    @Override
    public int getNorthSlot() {
        return NORTH_SLOT;
    }

    @Override
    public int getSouthSlot() {
        return SOUTH_SLOT;
    }

    @Override
    public int getEastSlot() {
        return EAST_SLOT;
    }

    @Override
    public int getWestSlot() {
        return WEST_SLOT;
    }

    @Override
    public int getUpSlot() {
        return UP_SLOT;
    }

    @Override
    public int getDownSlot() {
        return DOWN_SLOT;
    }

    @Override
    public boolean runSync() {
        return true;
    }

    @Override
    protected Particle.@NotNull DustOptions getDustOptions() {
        return new Particle.DustOptions(Color.MAROON, 1);
    }

    public @NotNull List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>(6);
        displayRecipes.add(new CustomItemStack(
                Material.BOOK,
                Lang.getString("icons.mechanism.transfers.data_title"),
                "",
                String.format(Lang.getString("icons.mechanism.transfers.max_distance"), maxDistance),
                String.format(Lang.getString("icons.mechanism.transfers.grab_item_tick"), grabItemTick)));
        return displayRecipes;
    }
}
