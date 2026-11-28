package com.ytdd9527.networksexpansion.core.services;

import com.balugaq.netex.utils.Debug;
import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.utils.NetworkUtil;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class CollectService {

    private static final NamespacedKey KEY_MARK = new NamespacedKey(Networks.getInstance(), "collect_mark");
    private static final NamespacedKey KEY_NET = new NamespacedKey(Networks.getInstance(), "collect_net");
    private static final NamespacedKey KEY_MODE = new NamespacedKey(Networks.getInstance(), "collect_mode");

    private CollectService() {
    }

    public static boolean isEnabled() {
        return Networks.getConfigManager().isCollectEnabled();
    }

    public static boolean hasMark(@Nullable ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(KEY_MARK, PersistentDataType.BYTE);
    }

    public static void markTool(@NotNull ItemStack tool) {
        tool.editMeta(meta -> {
            meta.getPersistentDataContainer().set(KEY_MARK, PersistentDataType.BYTE, (byte) 1);
            meta.getPersistentDataContainer().set(KEY_MODE, PersistentDataType.STRING, "I");
        });
        appendLore(tool);
    }

    public static void bind(@NotNull ItemStack weapon, @NotNull Location controller) {
        weapon.editMeta(meta -> meta.getPersistentDataContainer().set(KEY_NET, PersistentDataType.STRING,
            controller.getWorld().getName() + ";" + controller.getBlockX() + ";"
                + controller.getBlockY() + ";" + controller.getBlockZ()));
    }

    public static void unbind(@NotNull ItemStack weapon) {
        weapon.editMeta(meta -> meta.getPersistentDataContainer().remove(KEY_NET));
    }

    public static void toggleMode(@NotNull ItemStack weapon, @NotNull Player player) {
        boolean direct = isDirect(weapon);
        weapon.editMeta(meta -> meta.getPersistentDataContainer().set(
            KEY_MODE, PersistentDataType.STRING, direct ? "I" : "N"));
        player.sendMessage(Lang.getString(direct
            ? CellnetText.COLLECT_MODE_INVENTORY
            : CellnetText.COLLECT_MODE_DIRECT));
    }

    public static void collectFromKill(@NotNull Player killer, @NotNull EntityDeathEvent event) {
        ItemStack weapon = killer.getInventory().getItemInMainHand();
        if (!hasMark(weapon)) {
            return;
        }
        List<ItemStack> drops = event.getDrops();
        List<ItemStack> leftovers = collectDrops(killer, weapon, drops);
        if (leftovers == null) {
            return;
        }
        drops.clear();
        drops.addAll(leftovers);
    }

    public static void collectFromBlock(@NotNull Player player, @NotNull List<Item> dropEntities) {
        if (dropEntities.isEmpty()) {
            return;
        }
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!hasMark(tool)) {
            return;
        }
        List<ItemStack> stacks = new ArrayList<>(dropEntities.size());
        for (Item entity : dropEntities) {
            stacks.add(entity.getItemStack());
        }
        List<ItemStack> leftovers = collectDrops(player, tool, stacks);
        if (leftovers == null) {
            return;
        }
        for (int i = 0; i < dropEntities.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (stack.getAmount() <= 0) {
                dropEntities.get(i).remove();
            } else {
                dropEntities.get(i).setItemStack(stack);
            }
        }
    }

    @Nullable
    private static List<ItemStack> collectDrops(@NotNull Player player, @NotNull ItemStack weapon, @NotNull List<ItemStack> drops) {
        Location netLocation = getBoundNetwork(weapon);
        if (netLocation == null) {
            Debug.debug("collect skip: tool not bound to a network");
            return null;
        }
        if (!isNetworkLoaded(netLocation)) {
            Debug.debug("collect skip: network chunk not loaded");
            return null;
        }
        NetworkRoot root = NetworkUtil.findRoot(netLocation);
        if (root == null) {
            Debug.debug("collect skip: no live network root at " + netLocation);
            return null;
        }
        if (drops.isEmpty()) {
            return new ArrayList<>();
        }
        boolean direct = isDirect(weapon);
        Debug.debug("collect: mode=" + (direct ? "direct" : "inventory") + " drops=" + drops.size());
        List<ItemStack> batch = new ArrayList<>();
        if (direct) {
            batch.addAll(drops);
        } else {
            for (ItemStack drop : drops) {
                batch.addAll(player.getInventory().addItem(drop).values());
            }
        }
        root.addItemStacks(netLocation, batch);
        List<ItemStack> leftovers = new ArrayList<>();
        for (ItemStack stack : batch) {
            if (stack.getAmount() > 0) {
                leftovers.add(stack);
            }
        }
        Debug.debug("collect: overflow back: " + leftovers.size() + " stack(s)");
        return leftovers;
    }

    private static boolean isNetworkLoaded(@NotNull Location net) {
        return net.isWorldLoaded() && net.getWorld().isChunkLoaded(net.getBlockX() >> 4, net.getBlockZ() >> 4);
    }

    private static boolean isDirect(@NotNull ItemStack weapon) {
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) {
            return false;
        }
        String mode = meta.getPersistentDataContainer().get(KEY_MODE, PersistentDataType.STRING);
        return "N".equals(mode);
    }

    @Nullable
    private static Location getBoundNetwork(@NotNull ItemStack weapon) {
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) {
            return null;
        }
        String data = meta.getPersistentDataContainer().get(KEY_NET, PersistentDataType.STRING);
        if (data == null) {
            return null;
        }
        String[] parts = data.split(";");
        if (parts.length != 4) {
            return null;
        }
        World world = Bukkit.getWorld(parts[0]);
        if (world == null) {
            return null;
        }
        try {
            return new Location(world, Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void appendLore(@NotNull ItemStack stack) {
        stack.editMeta(meta -> {
            List<String> lore = meta.hasLore() && meta.getLore() != null
                ? new ArrayList<>(meta.getLore())
                : new ArrayList<>();
            String mark = Lang.getString(CellnetText.COLLECT_MARK_LORE);
            if (lore.stream().noneMatch(mark::equals)) {
                lore.add(mark);
            }
            meta.setLore(lore);
        });
    }
}
