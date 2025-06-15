package com.ytdd9527.networksexpansion.core.items;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.factories.MachineRecipeFactory;
import com.balugaq.netex.api.interfaces.RecipeItem;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.LocationUtil;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * We may add something soon
 *
 * @author Final_ROOT
 * @author baluagq
 * @since 2.0
 */
public abstract class SpecialSlimefunItem extends SlimefunItem {
    protected static final Map<UUID, Set<Location>> subscribedLocations = new HashMap<>();

    public SpecialSlimefunItem(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            @NotNull ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public SpecialSlimefunItem(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            @NotNull ItemStack @NotNull [] recipe,
            @Nullable ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    protected SpecialSlimefunItem(
            @NotNull ItemGroup itemGroup,
            @NotNull ItemStack item,
            @NotNull String id,
            @NotNull RecipeType recipeType,
            @NotNull ItemStack @NotNull [] recipe) {
        super(itemGroup, item, id, recipeType, recipe);
    }

    public static void subscribe(@NotNull Player player, Location location) {
        UUID key = player.getUniqueId();
        if (!subscribedLocations.containsKey(key)) {
            subscribedLocations.put(key, new HashSet<>());
        }
        subscribedLocations.get(key).add(location);
    }

    public static void unsubscribe(@NotNull Player player, Location location) {
        UUID key = player.getUniqueId();
        if (subscribedLocations.containsKey(key)) {
            subscribedLocations.get(key).remove(location);
        }
    }

    public static boolean hasSubscribed(@NotNull Player player, Location location) {
        UUID key = player.getUniqueId();
        if (subscribedLocations.containsKey(key)) {
            return subscribedLocations.get(key).contains(location);
        }
        return false;
    }

    @Override
    public void register(@NotNull SlimefunAddon addon) {
        super.register(addon);
        if (this instanceof RecipeItem recipeItem) {
            int delay = recipeItem.getRegisterRecipeDelay();
            if (delay > 0) {
                this.getAddon()
                        .getJavaPlugin()
                        .getServer()
                        .getScheduler()
                        .runTaskLater(
                                (Plugin) addon,
                                () -> {
                                    (recipeItem).registerDefaultRecipes();
                                    MachineRecipeFactory.getInstance().initAdvancedRecipeMap(this.getId());
                                },
                                delay);
            } else {
                (recipeItem).registerDefaultRecipes();
                MachineRecipeFactory.getInstance().initAdvancedRecipeMap(this.getId());
            }
        }
    }

    @NotNull public SpecialSlimefunItem registerThis() {
        this.register(Networks.getInstance());
        return this;
    }

    public void sendFeedback(@NotNull Location location, @NotNull FeedbackType type) {
        for (UUID uuid : subscribedLocations.keySet()) {
            if (subscribedLocations.get(uuid).contains(location)) {
                Player player = Bukkit.getServer().getPlayer(uuid);
                if (player != null) {
                    sendFeedback(player, location, type.getMessage());
                }
            }
        }
    }

    public void sendFeedback(@NotNull Player player, @NotNull Location location, String message) {
        player.sendMessage(String.format(
                Lang.getString("messages.debug.status_view"), LocationUtil.humanizeBlock(location), message));
    }
}
