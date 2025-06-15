package io.github.sefiraat.networks.slimefun.network;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.helpers.Icon;
import com.gmail.nossr50.mcMMO;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import dev.sefiraat.sefilib.misc.ParticleUtils;
import dev.sefiraat.sefilib.world.LocationUtils;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.managers.SupportedPluginManager;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetworkControlV extends NetworkDirectional {

    private static final int[] BACKGROUND_SLOTS = new int[] {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 15, 17, 18, 20, 22, 23, 24, 26, 27, 28, 30, 31, 33, 34, 35, 36, 37,
        38, 39, 40, 41, 42, 43, 44
    };
    private static final int[] TEMPLATE_BACKGROUND = new int[] {16};
    private static final int TEMPLATE_SLOT = 25;
    private static final int NORTH_SLOT = 11;
    private static final int SOUTH_SLOT = 29;
    private static final int EAST_SLOT = 21;
    private static final int WEST_SLOT = 19;
    private static final int UP_SLOT = 14;
    private static final int DOWN_SLOT = 32;
    private static final int REQUIRED_POWER = 100;
    private final Set<BlockPosition> blockCache = new HashSet<>();

    public NetworkControlV(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.PASTER);
        this.getSlotsToDrop().add(TEMPLATE_SLOT);
    }

    @Override
    protected void onTick(@Nullable BlockMenu blockMenu, @NotNull Block block) {
        super.onTick(blockMenu, block);
        if (blockMenu != null) {
            tryPasteBlock(blockMenu);
        }
    }

    @Override
    protected void onUniqueTick() {
        blockCache.clear();
    }

    @SuppressWarnings({"deprecation", "removal"})
    private void tryPasteBlock(@NotNull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        if (definition.getNode().getRoot().getRootPower() < REQUIRED_POWER) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NOT_ENOUGH_POWER);
            return;
        }

        final BlockFace direction = getCurrentDirection(blockMenu);

        if (direction == BlockFace.SELF) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_DIRECTION_SET);
            return;
        }

        final Block targetBlock = blockMenu.getBlock().getRelative(direction);
        final BlockPosition targetPosition = new BlockPosition(targetBlock);

        if (this.blockCache.contains(targetPosition)) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.BLOCK_ALREADY_PASTED);
            return;
        }

        final String owner = StorageCacheUtils.getData(blockMenu.getLocation(), OWNER_KEY);
        if (owner == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_OWNER_FOUND);
            return;
        }

        final UUID uuid = UUID.fromString(owner);
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!Slimefun.getProtectionManager().hasPermission(offlinePlayer, targetBlock, Interaction.PLACE_BLOCK)) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_PERMISSION);
            return;
        }

        final Material material = targetBlock.getType();

        if (material != Material.AIR) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.BLOCK_CANNOT_BE_AIR);
            return;
        }

        final ItemStack templateStack = blockMenu.getItemInSlot(TEMPLATE_SLOT);

        if (templateStack == null) {
            return;
        }

        final Material templateMaterial = templateStack.getType();

        if (!templateMaterial.isBlock() || SlimefunTag.SENSITIVE_MATERIALS.isTagged(templateMaterial)) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.INVALID_TEMPLATE);
            return;
        }

        final SlimefunItem slimefunItem = SlimefunItem.getByItem(templateStack);

        if (slimefunItem != null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.INVALID_TEMPLATE);
            return;
        }

        final ItemRequest request = new ItemRequest(templateStack.clone(), 1);
        final ItemStack fetchedStack = definition.getNode().getRoot().getItemStack0(blockMenu.getLocation(), request);

        if (fetchedStack == null || fetchedStack.getAmount() < 1) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NOT_ENOUGH_RESOURCES);
            return;
        }

        this.blockCache.add(targetPosition);
        Bukkit.getScheduler().runTask(Networks.getInstance(), bukkitTask -> {
            targetBlock.setType(fetchedStack.getType(), true);
            if (SupportedPluginManager.getInstance().isMcMMO()) {
                try {
                    mcMMO.getChunkManager().setTrue(targetBlock);
                } catch (NoClassDefFoundError e) {
                    mcMMO.getPlaceStore().setTrue(targetBlock);
                }
            }
            ParticleUtils.displayParticleRandomly(
                    LocationUtils.centre(targetBlock.getLocation()), Particle.ELECTRIC_SPARK, 1, 5);
            sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
        });
    }

    @Override
    protected int @NotNull [] getBackgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    @Override
    protected int @Nullable [] getOtherBackgroundSlots() {
        return TEMPLATE_BACKGROUND;
    }

    @Nullable @Override
    protected ItemStack getOtherBackgroundStack() {
        return Icon.CONTROL_V_TEMPLATE_BACKGROUND_STACK;
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
    public int[] getItemSlots() {
        return new int[] {TEMPLATE_SLOT};
    }

    @Override
    protected Particle.@NotNull DustOptions getDustOptions() {
        return new Particle.DustOptions(Color.MAROON, 1);
    }
}
