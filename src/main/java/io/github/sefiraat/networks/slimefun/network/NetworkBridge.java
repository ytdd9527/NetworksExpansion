package io.github.sefiraat.networks.slimefun.network;

import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.utils.DisplayGroupGenerators;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NodeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetworkBridge extends NetworkObject {

    private static final String KEY_UUID = "display-uuid";

    @Setter
    private boolean useSpecialModel = false;

    private @Nullable Function<Location, DisplayGroup> displayGroupGenerator;

    public NetworkBridge(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput, NodeType.BRIDGE);
    }

    public NetworkBridge(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            ItemStack recipeOutput,
            String itemId) {
        super(itemGroup, item, recipeType, recipe, recipeOutput, NodeType.BRIDGE);
        loadConfigurations(itemId);
    }

    private void loadConfigurations(String itemId) {
        FileConfiguration config = Networks.getInstance().getConfig();

        boolean defaultUseSpecialModel = false;

        this.useSpecialModel =
                config.getBoolean("items." + itemId + ".use-special-model.enable", defaultUseSpecialModel);

        Map<String, Function<Location, DisplayGroup>> generatorMap = new HashMap<>();
        generatorMap.put("bridge_1", DisplayGroupGenerators::generateBridge1);
        generatorMap.put("bridge_2", DisplayGroupGenerators::generateBridge2);
        generatorMap.put("bridge_3", DisplayGroupGenerators::generateBridge3);
        this.displayGroupGenerator = null;

        if (this.useSpecialModel) {
            String generatorKey = config.getString("items." + itemId + ".use-special-model.type");
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
}
