package com.ytdd9527.networksexpansion.implementation.items.drive;

import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.drive.DriveOwnership;
import com.ytdd9527.networksexpansion.api.drive.DriveType;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.implementation.machines.drive.EnderDrive;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ChannelConfigurator extends SpecialSlimefunItem {

    private static final NamespacedKey CHANNEL_KEY = new NamespacedKey(Networks.getInstance(), "ender_config_channel");

    public ChannelConfigurator(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        addItemHandler((ItemUseHandler) e -> {
            e.cancel();
            Player player = e.getPlayer();
            Optional<Block> optional = e.getClickedBlock();
            if (optional.isEmpty()) {
                if (player.isSneaking()) {
                    setStoredChannel(player.getInventory().getItemInMainHand(), null);
                    player.sendMessage(Lang.getString(CellnetText.CONFIGURATOR_CLEARED));
                }
                return;
            }
            Block block = optional.get();
            SlimefunItem target = StorageCacheUtils.getSfItem(block.getLocation());
            if (DriveType.of(target) != DriveType.ENDER) {
                player.sendMessage(Lang.getString(CellnetText.CONFIGURATOR_NOT_ENDER_DRIVE));
                return;
            }
            if (!Slimefun.getProtectionManager().hasPermission(player, block, Interaction.INTERACT_BLOCK)) {
                player.sendMessage(Lang.getString("messages.unsupported-operation.comprehensive.no_permission"));
                return;
            }
            if (!DriveOwnership.isOwnerOrWhitelisted(player, block.getLocation())) {
                player.sendMessage(Lang.getString(CellnetText.CONFIGURATOR_NOT_OWNER));
                return;
            }
            ItemStack held = player.getInventory().getItemInMainHand();
            if (player.isSneaking()) {
                copyFromDrive(player, held, block.getLocation());
            } else {
                pasteToDrive(player, held, block.getLocation());
            }
        });
    }

    private static void copyFromDrive(@NotNull Player player, @NotNull ItemStack configurator, @NotNull Location location) {
        String channel = EnderDrive.getChannel(location);
        if (channel == null) {
            player.sendMessage(Lang.getString(CellnetText.CONFIGURATOR_DRIVE_NO_CHANNEL));
            return;
        }
        setStoredChannel(configurator, channel);
        player.sendMessage(Lang.getString(CellnetText.CONFIGURATOR_COPIED, channel));
    }

    private static void pasteToDrive(@NotNull Player player, @NotNull ItemStack configurator, @NotNull Location location) {
        String stored = getStoredChannel(configurator);
        boolean fresh = stored == null || stored.isEmpty();
        if (fresh) {
            stored = EnderDrive.generateChannel();
            setStoredChannel(configurator, stored);
        }
        EnderDrive.handleChannelInput(location, stored);
        player.sendMessage(Lang.getString(fresh
            ? CellnetText.CONFIGURATOR_BOUND_NEW
            : CellnetText.CONFIGURATOR_BOUND, stored));
    }

    public static boolean isConfigurator(@Nullable ItemStack itemStack) {
        return itemStack != null && !itemStack.getType().isAir()
            && SlimefunItem.getByItem(itemStack) instanceof ChannelConfigurator;
    }

    public static void setStoredChannel(@NotNull ItemStack itemStack, @Nullable String channel) {
        if (itemStack.getType().isAir()) {
            return;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }
        if (channel == null || channel.isEmpty()) {
            PersistentDataAPI.remove(meta, CHANNEL_KEY);
        } else {
            PersistentDataAPI.setString(meta, CHANNEL_KEY, channel);
        }
        itemStack.setItemMeta(meta);
    }

    @Nullable
    public static String getStoredChannel(@NotNull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = itemStack.getItemMeta();
        return meta == null ? null : PersistentDataAPI.getString(meta, CHANNEL_KEY);
    }
}
