package io.github.sefiraat.networks.slimefun.network;

import com.balugaq.netex.api.interfaces.HangingBlock;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@NullMarked
@Getter
public abstract class NetworkObject extends SpecialSlimefunItem implements AdminDebuggable {
    public static final Queue<Location> scheduledHangingTick = new ConcurrentLinkedQueue<>();
    protected static final Set<BlockFace> CHECK_FACES =
        Set.of(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

    static {
        Bukkit.getScheduler()
            .runTaskTimer(
                Networks.getInstance(),
                () -> {
                    while (!scheduledHangingTick.isEmpty()) {
                        final Location location = scheduledHangingTick.poll();
                        final Block block = location.getBlock();
                        HangingBlock.tickHangingBlocks(block);
                    }
                },
                1L,
                Slimefun.getTickerTask().getTickRate());
    }

    private final NodeType nodeType;
    private final List<Integer> slotsToDrop = new ArrayList<>();
    private final Set<Location> firstTickLocations = new HashSet<>();

    protected NetworkObject(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        ItemStack [] recipe,
        @Range(from = 1, to = 64) int outputAmount,
        NodeType type
    ) {
        this(itemGroup, item, recipeType, recipe, StackUtils.getAsQuantity(item, outputAmount), type);
    }

    protected NetworkObject(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        ItemStack [] recipe,
        NodeType type) {
        this(itemGroup, item, recipeType, recipe, null, type);
    }

    protected NetworkObject(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        ItemStack [] recipe,
        @Nullable ItemStack recipeOutput,
        NodeType type) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
        this.nodeType = type;
        addItemHandler(
            new BlockTicker() {

                @Override
                public boolean isSynchronized() {
                    return runSync();
                }

                @Override
                public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
                    if (!firstTickLocations.contains(b.getLocation())) {
                        // Netex - Hanging patch start
                        Bukkit.getScheduler().runTask(Networks.getInstance(), () -> {
                            HangingBlock.loadHangingBlocks(data);
                            HangingBlock.doFirstTick(data);
                        });
                        // Netex - Hanging patch end
                        firstTickLocations.add(b.getLocation());
                        return;
                    }

                    addToRegistry(b);
                    tickHangingBlocks(b);
                }

                // no exception
                @Override
                public Optional<IncompatibleItemHandlerException> validate(SlimefunItem slimefunItem) {
                    return Optional.empty();
                }
            },
            new BlockBreakHandler(false, false) {
                @Override
                public void onPlayerBreak(BlockBreakEvent event, ItemStack item, List<ItemStack> drops) {
                    preBreak(event);
                    onBreak(event);
                    postBreak(event);
                }
            },
            new BlockPlaceHandler(false) {
                @Override
                public void onPlayerPlace(BlockPlaceEvent event) {
                    prePlace(event);
                    onPlace(event);
                    postPlace(event);
                }
            });
    }

    protected void addToRegistry(Block block) {
        if (!NetworkStorage.containsKey(block.getLocation())) {
            final NodeDefinition nodeDefinition = new NodeDefinition(nodeType);
            NetworkStorage.registerNode(block.getLocation(), nodeDefinition);
        }
    }

    protected void tickHangingBlocks(Block block) {
        scheduledHangingTick.add(block.getLocation());
    }

    @OverridingMethodsMustInvokeSuper
    protected void preBreak(BlockBreakEvent event) {
        NetworkRoot.removePersistentAccessHistory(event.getBlock().getLocation());
        NetworkRoot.removeCountObservingAccessHistory(event.getBlock().getLocation());
    }

    @OverridingMethodsMustInvokeSuper
    protected void onBreak(BlockBreakEvent event) {
        final Location location = event.getBlock().getLocation();
        final BlockMenu blockMenu = StorageCacheUtils.getMenu(location);

        if (blockMenu != null) {
            for (int i : getSlotsToDrop()) {
                blockMenu.dropItems(location, i);
            }
        }
    }

    protected void postBreak(BlockBreakEvent event) {
    }

    protected void prePlace(BlockPlaceEvent event) {
    }

    protected void cancelPlace(BlockPlaceEvent event) {
        event.getPlayer().sendMessage(Lang.getString("messages.unsupported-operation.comprehensive.cancel_place"));
        event.setCancelled(true);
    }

    protected void onPlace(BlockPlaceEvent event) {
    }

    protected void postPlace(BlockPlaceEvent event) {
    }

    public boolean isAdminDebuggable() {
        return false;
    }

    public boolean runSync() {
        return false;
    }
}
