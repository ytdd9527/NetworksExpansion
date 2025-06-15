package com.ytdd9527.networksexpansion.implementation.machines.networks.advanced;

import com.balugaq.netex.api.interfaces.HangingBlock;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import com.ytdd9527.networksexpansion.utils.databases.DataSource;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Placeable;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.SneakyThrows;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class SwitchingMonitor extends NetworkObject implements HangingBlock, Placeable {
    public static final NamespacedKey ORIGINAL = Keys.newKey("switching-monitor-original");
    public static final String SLASH = TextUtil.GRAY + " - " + TextUtil.GREEN + "x";
    public static final long K = 1000L;
    public static final long M = K * K;
    public static final long B = M * K;
    public static final long T = B * K;
    public static final long Q = T * K;
    public static final double FIX_OFFSET = HangingBlock.ITEM_FRAME_OFFSET - HangingBlock.CENTER_OFFSET;

    public SwitchingMonitor(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.SWITCHING_MONITOR);
        HangingBlock.registerHangingBlock(this);
    }

    @SneakyThrows
    @Contract("null -> null")
    public static ItemStack uniconize(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return item;
        }
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta == null) {
            return clone;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String base64 = pdc.get(ORIGINAL, PersistentDataType.STRING);
        if (base64 == null) {
            pdc.remove(ORIGINAL);
            meta.setDisplayName(removeSuffixAmount(meta.getDisplayName()));
            clone.setItemMeta(meta);
            return clone;
        } else {
            return DataSource.getItemStack(base64);
        }
    }

    @SneakyThrows
    @Contract("null, _ -> null")
    public static ItemStack iconize(@Nullable ItemStack template, long amount) {
        if (template == null || template.getType() == Material.AIR) {
            return template;
        }

        ItemStack clone = template.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta == null) {
            return clone;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (pdc.has(ORIGINAL, PersistentDataType.STRING)) {
            return template;
        }

        pdc.set(ORIGINAL, PersistentDataType.STRING, DataSource.getBase64String(template));
        meta.setDisplayName(addSuffixAmount(ItemStackHelper.getDisplayName(template), amount));
        clone.setItemMeta(meta);
        return clone;
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    public static String addSuffixAmount(@Nullable String original, long amount) {
        if (original == null) {
            return null;
        }

        if (amount < K) {
            return original + SLASH + amount;
        }
        if (amount < M) {
            return original + SLASH + amount / K + "k";
        }
        if (amount < B) {
            return original + SLASH + amount / M + "m";
        }
        if (amount < T) {
            return original + SLASH + amount / B + "b";
        }
        if (amount < Q) {
            return original + SLASH + amount / T + "t";
        }
        return original + SLASH + amount;
    }

    @Contract("null -> null; !null -> !null")
    public static String removeSuffixAmount(@Nullable String original) {
        if (original == null) {
            return null;
        }
        return original.replaceAll(SLASH + "[0-9]+[kmbt]?", "");
    }

    @Override
    public void onInteract(@NotNull Location attachon, @NotNull PlayerItemFrameChangeEvent event) {
        Player player = event.getPlayer();

        if (!player.isOp()
                && !Slimefun.getProtectionManager().hasPermission(player, attachon, Interaction.INTERACT_ENTITY)) {
            event.setCancelled(true);
            return;
        }

        if (event.getAction() == PlayerItemFrameChangeEvent.ItemFrameChangeAction.ROTATE) {
            event.setCancelled(true);
            handleItemsAction(attachon, player, event);
        } else if (event.getAction() == PlayerItemFrameChangeEvent.ItemFrameChangeAction.REMOVE) {
            event.setCancelled(true);
            ItemFrame itemFrame = event.getItemFrame();
            ItemStack template = itemFrame.getItem();
            itemFrame.setItem(null, false);
            Location fixedLocation = itemFrame.getLocation().clone();
            BlockFace attachedFace = itemFrame.getAttachedFace();
            switch (attachedFace.getOppositeFace()) {
                case NORTH -> fixedLocation.add(0D, 0D, -FIX_OFFSET);
                case SOUTH -> fixedLocation.add(0D, 0D, FIX_OFFSET);
                case EAST -> fixedLocation.add(FIX_OFFSET, 0D, 0D);
                case WEST -> fixedLocation.add(-FIX_OFFSET, 0D, 0D);
                case UP -> fixedLocation.add(0D, FIX_OFFSET, 0D);
                case DOWN -> fixedLocation.add(0D, -FIX_OFFSET, 0D);
            }
            fixedLocation.getWorld().dropItemNaturally(fixedLocation, uniconize(template));
        }
    }

    private void handleItemsAction(
            @NotNull Location attachon, @NotNull Player player, @NotNull PlayerItemFrameChangeEvent event) {
        NodeDefinition definition = NetworkStorage.getNode(attachon);
        if (definition == null || definition.getNode() == null) {
            return;
        }

        NetworkRoot root = definition.getNode().getRoot();
        ItemStack template = event.getItemFrame().getItem();
        if (template == null || template.getType() == Material.AIR) {
            return;
        }

        template = uniconize(template);

        ItemStack hand = player.getInventory().getItemInMainHand();
        boolean takeItem = hand == null || hand.getType() == Material.AIR;
        boolean shift = player.isSneaking();
        if (!takeItem && !StackUtils.itemsMatch(hand, template, true, false)) {
            return;
        }

        if (takeItem) {
            int amount = calculateSpace(player, template);
            if (amount == 0) {
                return;
            }

            if (shift) {
                ItemStack result = root.getItemStack0(attachon, new ItemRequest(template, amount));
                if (result != null) {
                    player.getInventory().addItem(result).values().forEach(item -> root.addItemStack0(attachon, item));
                    player.updateInventory();
                }
            } else {
                ItemStack result = root.getItemStack0(
                        attachon, new ItemRequest(template, Math.min(amount, template.getMaxStackSize())));
                if (result != null) {
                    player.getInventory().addItem(result).values().forEach(item -> root.addItemStack0(attachon, item));
                    player.updateInventory();
                }
            }
        } else {
            if (shift) {
                for (ItemStack item : player.getInventory().getStorageContents()) {
                    if (item != null
                            && item.getType() != Material.AIR
                            && StackUtils.itemsMatch(item, template, true, false)) {
                        int before = item.getAmount();
                        root.addItemStack0(attachon, item);
                        int after = item.getAmount();
                        if (before == after) {
                            break;
                        }
                    }
                }
            } else {
                root.addItemStack0(attachon, hand);
            }
        }
    }

    public int calculateSpace(@NotNull Player player, ItemStack template) {
        int amount = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType() == Material.AIR) {
                amount += template.getMaxStackSize();
            } else if (StackUtils.itemsMatch(item, template, true, false)) {
                amount += Math.max(0, template.getMaxStackSize() - item.getAmount());
            }
        }
        return amount;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onTick(Location attachon, BlockFace attachSide, ItemFrame entityBlock) {
        NodeDefinition definition = NetworkStorage.getNode(attachon);
        if (definition == null || definition.getNode() == null) {
            return;
        }

        NetworkRoot root = definition.getNode().getRoot();
        ItemStack template = entityBlock.getItem();
        if (template == null || template.getType() == Material.AIR) {
            return;
        }

        template = uniconize(template);

        long amount = root.getAllNetworkItemsLongType().getOrDefault(template, 0L);
        entityBlock.setItem(iconize(template, amount), false);
    }

    @Override
    @ParametersAreNonnullByDefault
    public <T extends HangingBlock> void onBreak(Location attachon, ItemFrame entityBlock, T hangingBlock) {
        HangingBlock.super.onBreak(attachon, entityBlock, hangingBlock);
        Location location = entityBlock.getLocation();
        location.getWorld().dropItemNaturally(location, SwitchingMonitor.uniconize(entityBlock.getItem()));
        location.getWorld().dropItemNaturally(location, ExpansionItems.SWITCHING_MONITOR.getItem());
    }
}
