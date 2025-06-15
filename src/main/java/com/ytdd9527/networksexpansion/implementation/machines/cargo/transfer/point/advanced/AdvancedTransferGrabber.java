package com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.advanced;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.enums.TransportMode;
import com.balugaq.netex.api.interfaces.Configurable;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.LineOperationUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.machines.AdvancedDirectional;
import com.ytdd9527.networksexpansion.utils.DisplayGroupGenerators;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancedTransferGrabber extends AdvancedDirectional implements RecipeDisplayItem, Configurable {
    private static final int DEFAULT_GRAB_ITEM_TICK = 1;
    private static final boolean DEFAULT_USE_SPECIAL_MODEL = false;

    private static final String KEY_UUID = "display-uuid";
    private static final int TRANSPORT_LIMIT = 3456;
    private static final int[] BACKGROUND_SLOTS = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 16, 17, 18, 19, 21, 23, 24, 25, 26, 28, 29, 21, 31, 32, 34, 35,
        39, 40, 41, 42, 43, 44
    };
    private static final int TRANSPORT_MODE_SLOT = 27;
    private static final int MINUS_SLOT = 36;
    private static final int SHOW_SLOT = 37;
    private static final int ADD_SLOT = 38;
    private static final Map<Location, Integer> GRAB_TICKER_MAP = new HashMap<>();
    private boolean useSpecialModel;
    private @Nullable Function<Location, DisplayGroup> displayGroupGenerator;
    private int grabItemTick;

    public AdvancedTransferGrabber(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.TRANSFER_GRABBER);
        loadConfigurations();
    }

    @Override
    public boolean isExceedLimit(int quantity) {
        return quantity > TRANSPORT_LIMIT;
    }

    @Override
    public int getMaxLimit() {
        return TRANSPORT_LIMIT;
    }

    public void loadConfigurations() {
        String configKey = getId();
        FileConfiguration config = Networks.getInstance().getConfig();

        this.grabItemTick = config.getInt("items." + configKey + ".grabitem-tick", DEFAULT_GRAB_ITEM_TICK);
        this.useSpecialModel =
                config.getBoolean("items." + configKey + ".use-special-model.enable", DEFAULT_USE_SPECIAL_MODEL);

        Map<String, Function<Location, DisplayGroup>> generatorMap = new HashMap<>();
        generatorMap.put("cloche", DisplayGroupGenerators::generateCloche);

        this.displayGroupGenerator = null;

        if (this.useSpecialModel) {
            String generatorKey = config.getString("items." + configKey + ".use-special-model.type");
            this.displayGroupGenerator = generatorMap.get(generatorKey);
            if (this.displayGroupGenerator == null) {
                Networks.getInstance()
                        .getLogger()
                        .warning(String.format(
                                Lang.getString("messages.unsupported-operation.display.unknown_type"), generatorKey));
                this.useSpecialModel = false;
            }
        }
    }

    @Override
    protected void onTick(@Nullable BlockMenu blockMenu, @NotNull Block block) {
        super.onTick(blockMenu, block);
        if (blockMenu == null) {
            sendFeedback(block.getLocation(), FeedbackType.INVALID_BLOCK);
            return;
        }

        final Location location = block.getLocation();
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
        final Integer tickCounter = GRAB_TICKER_MAP.get(location);
        if (tickCounter == null) {
            GRAB_TICKER_MAP.put(location, 0);
            return 0;
        } else {
            return tickCounter;
        }
    }

    private void updateTickCounter(Location location, int tickCounter) {
        GRAB_TICKER_MAP.put(location, tickCounter);
    }

    private void tryGrabItem(@NotNull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        final BlockFace direction = getCurrentDirection(blockMenu);
        if (direction == BlockFace.SELF) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_DIRECTION_SET);
            return;
        }

        final NetworkRoot root = definition.getNode().getRoot();
        final int limitQuantity = getLimitQuantity(blockMenu.getLocation());
        final TransportMode mode = getCurrentTransportMode(blockMenu.getLocation());

        LineOperationUtil.doOperation(
                blockMenu.getLocation(),
                direction,
                1,
                false,
                false,
                (targetMenu) ->
                        LineOperationUtil.grabItem(blockMenu.getLocation(), root, targetMenu, mode, limitQuantity));
        sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
    }

    @Override
    protected Particle.@NotNull DustOptions getDustOptions() {
        return new Particle.DustOptions(Color.LIME, 5);
    }

    @Override
    public void onPlace(@NotNull BlockPlaceEvent e) {
        super.onPlace(e);
        if (useSpecialModel) {
            e.getBlock().setType(Material.BARRIER);
            setupDisplay(e.getBlock().getLocation());
        }
    }

    @Override
    public void postBreak(@NotNull BlockBreakEvent e) {
        super.postBreak(e);
        Location location = e.getBlock().getLocation();
        removeDisplay(location);
        e.getBlock().setType(Material.AIR);
    }

    private void setupDisplay(@NotNull Location location) {
        if (this.displayGroupGenerator != null) {
            DisplayGroup displayGroup =
                    this.displayGroupGenerator.apply(location.clone().add(0.5, 0, 0.5));
            StorageCacheUtils.setData(
                    location, KEY_UUID, displayGroup.getParentUUID().toString());
        }
    }

    private void removeDisplay(@NotNull Location location) {
        DisplayGroup group = getDisplayGroup(location);
        if (group != null) {
            group.remove();
        }
    }

    @Nullable private UUID getDisplayGroupUUID(@NotNull Location location) {
        String uuid = StorageCacheUtils.getData(location, KEY_UUID);
        if (uuid == null) {
            return null;
        }
        return UUID.fromString(uuid);
    }

    @Nullable private DisplayGroup getDisplayGroup(@NotNull Location location) {
        UUID uuid = getDisplayGroupUUID(location);
        if (uuid == null) {
            return null;
        }
        return DisplayGroup.fromUUID(uuid);
    }

    @Override
    protected int @NotNull [] getBackgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    protected int getMinusSlot() {
        return MINUS_SLOT;
    }

    protected int getShowSlot() {
        return SHOW_SLOT;
    }

    protected int getAddSlot() {
        return ADD_SLOT;
    }

    @NotNull @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>(6);
        displayRecipes.add(new CustomItemStack(
                Material.BOOK,
                Lang.getString("icons.mechanism.transfers.data_title"),
                "",
                String.format(Lang.getString("icons.mechanism.transfers.grab_item_tick"), grabItemTick)));
        return displayRecipes;
    }

    @Override
    protected int getTransportModeSlot() {
        return TRANSPORT_MODE_SLOT;
    }
}
