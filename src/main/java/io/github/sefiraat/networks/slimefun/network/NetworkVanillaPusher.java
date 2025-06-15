package io.github.sefiraat.networks.slimefun.network;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.enums.MinecraftVersion;
import com.balugaq.netex.utils.Lang;
import com.bgsoftware.wildchests.api.WildChestsAPI;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import java.util.UUID;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.CrafterInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetworkVanillaPusher extends NetworkDirectional {

    private static final int[] BACKGROUND_SLOTS = new int[] {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 15, 16, 17, 18, 20, 22, 23, 24, 26, 27, 28, 30, 31, 33, 34, 35, 36,
        37, 38, 39, 40, 41, 42, 43, 44
    };
    private static final int INPUT_SLOT = 25;
    private static final int NORTH_SLOT = 11;
    private static final int SOUTH_SLOT = 29;
    private static final int EAST_SLOT = 21;
    private static final int WEST_SLOT = 19;
    private static final int UP_SLOT = 14;
    private static final int DOWN_SLOT = 32;

    public NetworkVanillaPusher(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.PUSHER);
        this.getSlotsToDrop().add(INPUT_SLOT);
    }

    @Override
    protected void onTick(@Nullable BlockMenu blockMenu, @NotNull Block block) {
        super.onTick(blockMenu, block);
        if (blockMenu != null) {
            tryPushItem(blockMenu);
        }
    }

    private void tryPushItem(@NotNull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        final BlockFace direction = getCurrentDirection(blockMenu);
        final Block block = blockMenu.getBlock();
        final Block targetBlock = blockMenu.getBlock().getRelative(direction);
        // Fix for early vanilla pusher release
        final String ownerUUID = StorageCacheUtils.getData(block.getLocation(), OWNER_KEY);
        if (ownerUUID == null) {
            sendFeedback(block.getLocation(), FeedbackType.NO_OWNER_FOUND);
            return;
        }
        final UUID uuid = UUID.fromString(ownerUUID);
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        // dirty fix
        try {
            if (!Slimefun.getProtectionManager()
                    .hasPermission(offlinePlayer, targetBlock, Interaction.INTERACT_BLOCK)) {
                sendFeedback(block.getLocation(), FeedbackType.NO_PERMISSION);
                return;
            }
        } catch (NullPointerException ex) {
            sendFeedback(block.getLocation(), FeedbackType.ERROR_OCCURRED);
            return;
        }

        final BlockState blockState = targetBlock.getState();

        if (!(blockState instanceof InventoryHolder holder)) {
            sendFeedback(block.getLocation(), FeedbackType.NO_INVENTORY_FOUND);
            return;
        }

        if (Networks.getInstance().getMCVersion().isAtLeast(MinecraftVersion.MC1_21)) {
            if (blockState instanceof CrafterInventory) {
                sendFeedback(block.getLocation(), FeedbackType.NOT_ALLOWED_BLOCK);
                return;
            }
        }

        final Inventory inventory = holder.getInventory();
        final ItemStack stack = blockMenu.getItemInSlot(INPUT_SLOT);

        if (stack == null || stack.getType() == Material.AIR) {
            sendFeedback(block.getLocation(), FeedbackType.NO_ITEM_FOUND);
            return;
        }

        boolean wildChests = Networks.getSupportedPluginManager().isWildChests();
        boolean isChest = wildChests && WildChestsAPI.getChest(targetBlock.getLocation()) != null;

        sendDebugMessage(block.getLocation(), String.format(Lang.getString("messages.debug.wildchests"), wildChests));
        sendDebugMessage(block.getLocation(), String.format(Lang.getString("messages.debug.ischest"), isChest));

        if (inventory instanceof FurnaceInventory furnace) {
            handleFurnace(blockMenu, stack, furnace);
        } else if (inventory instanceof BrewerInventory brewer) {
            handleBrewingStand(blockMenu, stack, brewer);
        } else if (wildChests && isChest) {
            sendDebugMessage(block.getLocation(), Lang.getString("messages.debug.wildchests_test_failed"));
        } else if (InvUtils.fits(holder.getInventory(), stack)) {
            sendDebugMessage(block.getLocation(), Lang.getString("messages.debug.wildchests_test_success"));
            holder.getInventory().addItem(stack);
            stack.setAmount(0);
        }
    }

    private void handleFurnace(
            @NotNull BlockMenu blockMenu, @NotNull ItemStack stack, @NotNull FurnaceInventory furnace) {
        if (stack.getType().isFuel()
                && (furnace.getFuel() == null || furnace.getFuel().getType() == Material.AIR)) {
            furnace.setFuel(stack.clone());
            stack.setAmount(0);
            sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
        } else if (!stack.getType().isFuel()
                && (furnace.getSmelting() == null || furnace.getSmelting().getType() == Material.AIR)) {
            furnace.setSmelting(stack.clone());
            stack.setAmount(0);
            sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
        }
    }

    private void handleBrewingStand(
            @NotNull BlockMenu blockMenu, @NotNull ItemStack stack, @NotNull BrewerInventory brewer) {
        if (stack.getType() == Material.BLAZE_POWDER) {
            if (brewer.getFuel() == null || brewer.getFuel().getType() == Material.AIR) {
                brewer.setFuel(stack.clone());
                stack.setAmount(0);
                sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
            } else if (brewer.getIngredient() == null || brewer.getIngredient().getType() == Material.AIR) {
                brewer.setIngredient(stack.clone());
                stack.setAmount(0);
                sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
            }
        } else if (stack.getType() == Material.POTION) {
            for (int i = 0; i < 3; i++) {
                final ItemStack stackInSlot = brewer.getContents()[i];
                if (stackInSlot == null || stackInSlot.getType() == Material.AIR) {
                    final ItemStack[] contents = brewer.getContents();
                    contents[i] = stack.clone();
                    brewer.setContents(contents);
                    stack.setAmount(0);
                    sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
                    return;
                }
            }
        } else if (brewer.getIngredient() == null || brewer.getIngredient().getType() == Material.AIR) {
            brewer.setIngredient(stack.clone());
            stack.setAmount(0);
            sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
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
    public int[] getInputSlots() {
        return new int[] {INPUT_SLOT};
    }

    @Override
    protected Particle.@NotNull DustOptions getDustOptions() {
        return new Particle.DustOptions(Color.MAROON, 1);
    }
}
