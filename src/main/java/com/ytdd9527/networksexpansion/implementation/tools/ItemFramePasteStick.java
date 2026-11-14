package com.ytdd9527.networksexpansion.implementation.tools;

import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import io.github.sefiraat.networks.network.barrel.NetworkStorage;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示框贴物品棒：右键批量生效——对玩家附近 5x5x5 内每个量子存储，
 * 若该存储方块上恰好贴了 1 个展示框（挂在方块任一面上），从存储取 1 个物品贴上去。
 *
 * @author ytdd9527
 */
public class ItemFramePasteStick extends SpecialSlimefunItem {
    // 以玩家为中心的 5x5x5（半径 2）
    private static final int SEARCH_RADIUS = 2;

    public ItemFramePasteStick(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        @NotNull ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemHandler((ItemUseHandler) e -> {
            e.cancel();
            pasteAll(e.getPlayer());
        });
        // 右键展示框本身时也要批量生效，同时取消事件防止把棒子塞进框里
        addItemHandler((EntityInteractHandler) (event, itemStack, offHand) -> {
            if (offHand || !(event.getRightClicked() instanceof ItemFrame)) {
                return;
            }
            event.setCancelled(true);
            pasteAll(event.getPlayer());
        });
    }

    private void pasteAll(@NotNull Player player) {
        final Location base = player.getLocation();

        final List<Location> storages = new ArrayList<>();
        for (Location location : NetworkQuantumStorage.getCaches().keySet()) {
            if (location.getWorld() == null || !location.getWorld().equals(base.getWorld())) {
                continue;
            }
            if (Math.abs(location.getBlockX() - base.getBlockX()) > SEARCH_RADIUS
                || Math.abs(location.getBlockY() - base.getBlockY()) > SEARCH_RADIUS
                || Math.abs(location.getBlockZ() - base.getBlockZ()) > SEARCH_RADIUS) {
                continue;
            }
            storages.add(location);
        }

        if (storages.isEmpty()) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.item_frame_paste_stick.no_quantum_storage"));
            return;
        }

        int pasted = 0;
        for (Location storageLocation : storages) {
            final List<ItemFrame> frames = getAttachedFrames(storageLocation);
            // 只处理恰好贴了 1 个展示框的存储，贴了多个的不动
            if (frames.size() != 1) {
                continue;
            }
            final ItemFrame frame = frames.get(0);
            if (tryPaste(player, storageLocation, frame)) {
                pasted++;
            }
        }

        if (pasted > 0) {
            player.sendMessage(String.format(
                Lang.getString("messages.completed-operation.item_frame_paste_stick.success"), pasted));
        } else {
            player.sendMessage(Lang.getString("messages.unsupported-operation.item_frame_paste_stick.nothing_pasted"));
        }
    }

    @NotNull
    private static List<ItemFrame> getAttachedFrames(@NotNull Location storageLocation) {
        final Block storage = storageLocation.getBlock();
        final List<ItemFrame> frames = new ArrayList<>();
        // 展示框贴在存储方块任一面上，框体必然落在存储方块外扩 1 格的范围内
        for (Entity entity : storageLocation.getWorld().getNearbyEntities(
            BoundingBox.of(storageLocation.clone().add(0.5, 0.5, 0.5), 1.5, 1.5, 1.5),
            e -> e instanceof ItemFrame)) {
            final ItemFrame frame = (ItemFrame) entity;
            final Block support = frame.getLocation().getBlock().getRelative(frame.getFacing().getOppositeFace());
            if (support.getLocation().equals(storage.getLocation())) {
                frames.add(frame);
            }
        }
        return frames;
    }

    private static boolean tryPaste(@NotNull Player player, @NotNull Location storageLocation, @NotNull ItemFrame frame) {
        // 量子存储侧沿用物品转移棒的权限检查（区块保护 + Slimefun 物品权限），展示框侧按实体交互权限
        if (!ItemMover.hasPermission(player, storageLocation)) {
            return false;
        }
        if (!Slimefun.getProtectionManager().hasPermission(player, frame.getLocation(), Interaction.INTERACT_ENTITY)) {
            return false;
        }

        final NetworkStorage barrel = ItemMover.getNetworkStorage(storageLocation);
        if (barrel == null || barrel.getItemStack() == null || barrel.getAmount() <= 0) {
            return false;
        }

        final ItemStack displayed = frame.getItem();
        // 框上已有物品一律跳过，不覆盖玩家手动贴的内容
        if (displayed != null && !displayed.getType().isAir()) {
            return false;
        }

        final ItemStack fetched = barrel.requestItem(new ItemRequest(barrel.getItemStack(), 1));
        if (fetched == null || fetched.getType().isAir() || fetched.getAmount() < 1) {
            return false;
        }

        fetched.setAmount(1);
        frame.setItem(fetched, true);
        return true;
    }
}
