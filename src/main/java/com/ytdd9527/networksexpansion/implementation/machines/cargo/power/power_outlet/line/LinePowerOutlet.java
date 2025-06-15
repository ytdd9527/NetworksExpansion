package com.ytdd9527.networksexpansion.implementation.machines.cargo.power.power_outlet.line;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.interfaces.Configurable;
import com.balugaq.netex.utils.LineOperationUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LinePowerOutlet extends NetworkDirectional implements Configurable {
    private static final int DEFAULT_RATE = 2000;
    private static final int DEFAULT_MAX_DISTANCE = 32;
    private int maxDistance;
    private int rate;

    public LinePowerOutlet(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            @NotNull ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.LINE_POWER_OUTLET);
        loadConfigurations();
    }

    @Override
    public void loadConfigurations() {
        String configKey = getId();
        FileConfiguration config = Networks.getInstance().getConfig();
        this.rate = config.getInt("items." + configKey + ".rate", DEFAULT_RATE);
        this.maxDistance = config.getInt("items." + configKey + ".max_distance", DEFAULT_MAX_DISTANCE);
    }

    @Override
    public void onTick(@Nullable BlockMenu blockMenu, @NotNull Block b) {
        super.onTick(blockMenu, b);
        if (blockMenu == null) {
            sendFeedback(b.getLocation(), FeedbackType.INVALID_BLOCK);
            return;
        }

        outPower(blockMenu);
    }

    private void outPower(@NotNull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        final NetworkRoot root = definition.getNode().getRoot();
        final BlockFace blockFace = getCurrentDirection(blockMenu);
        LineOperationUtil.doEnergyOperation(
                blockMenu.getLocation(),
                blockFace,
                this.maxDistance,
                true,
                false,
                (location) -> LineOperationUtil.outPower(location, root, this.rate));
    }
}
