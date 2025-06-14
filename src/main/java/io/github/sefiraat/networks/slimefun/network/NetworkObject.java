package io.github.sefiraat.networks.slimefun.network;

import com.balugaq.netex.api.interfaces.HangingBlock;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
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
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class NetworkObject extends SpecialSlimefunItem implements AdminDebuggable {

    protected static final Set<BlockFace> CHECK_FACES =
            Set.of(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
    private final NodeType nodeType;
    private final List<Integer> slotsToDrop = new ArrayList<>();
    private final Set<Location> firstTickLocations = new HashSet<>();

    protected NetworkObject(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe,
            NodeType type) {
        this(itemGroup, item, recipeType, recipe, null, type);
    }

    protected NetworkObject(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe,
            ItemStack recipeOutput,
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
                    public void tick(@NotNull Block b, SlimefunItem item, SlimefunBlockData data) {
                        if (!firstTickLocations.contains(b.getLocation())) {
                            // Netex - Hanging patch start
                            Bukkit.getScheduler().runTask(Networks.getInstance(), () -> HangingBlock.loadHangingBlocks(data));
                            // Netex - Hanging patch end
                            firstTickLocations.add(b.getLocation());
                            return;
                        }

                        addToRegistry(b);
                    }

                    @Override
                    @NotNull public Optional<IncompatibleItemHandlerException> validate(@NotNull SlimefunItem slimefunItem) {
                        return Optional.empty();
                    }
                },
                new BlockBreakHandler(false, false) {
                    @Override
                    @ParametersAreNonnullByDefault
                    public void onPlayerBreak(BlockBreakEvent event, ItemStack item, List<ItemStack> drops) {
                        preBreak(event);
                        onBreak(event);
                        postBreak(event);
                    }
                },
                new BlockPlaceHandler(false) {
                    @Override
                    @ParametersAreNonnullByDefault
                    public void onPlayerPlace(BlockPlaceEvent event) {
                        prePlace(event);
                        onPlace(event);
                        postPlace(event);
                    }
                });
    }

    protected void addToRegistry(@NotNull Block block) {
        if (!NetworkStorage.containsKey(block.getLocation())) {
            final NodeDefinition nodeDefinition = new NodeDefinition(nodeType);
            NetworkStorage.registerNode(block.getLocation(), nodeDefinition);
        }
    }

    protected void preBreak(@NotNull BlockBreakEvent event) {
        NetworkRoot.removePersistentAccessHistory(event.getBlock().getLocation());
        NetworkRoot.removeCountObservingAccessHistory(event.getBlock().getLocation());
    }

    protected void onBreak(@NotNull BlockBreakEvent event) {
        final Location location = event.getBlock().getLocation();
        final BlockMenu blockMenu = StorageCacheUtils.getMenu(location);

        if (blockMenu != null) {
            for (int i : getSlotsToDrop()) {
                blockMenu.dropItems(location, i);
            }
        }

        Slimefun.getDatabaseManager().getBlockDataController().removeBlock(location);
    }

    protected void postBreak(@NotNull BlockBreakEvent event) {}

    @SuppressWarnings("unused")
    protected void prePlace(@NotNull BlockPlaceEvent event) {}

    protected void cancelPlace(@NotNull BlockPlaceEvent event) {
        event.getPlayer().sendMessage(Theme.ERROR.getColor() + "This placement would connect two controllers!");
        event.setCancelled(true);
    }

    protected void onPlace(@NotNull BlockPlaceEvent event) {}

    @SuppressWarnings("unused")
    protected void postPlace(@NotNull BlockPlaceEvent event) {}

    public boolean isAdminDebuggable() {
        return false;
    }

    public boolean runSync() {
        return false;
    }
}
