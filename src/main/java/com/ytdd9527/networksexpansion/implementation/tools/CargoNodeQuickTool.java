package com.ytdd9527.networksexpansion.implementation.tools;

import com.balugaq.netex.utils.Debug;
import com.balugaq.netex.utils.Lang;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CargoNodeQuickTool extends SpecialSlimefunItem {
    private final @NotNull NamespacedKey listKey, configKey, cargoKey;
    private final int[] listSlots = {19, 20, 21, 28, 29, 30, 37, 38, 39};
    private final Gson gson = new Gson();

    public CargoNodeQuickTool(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
        listKey = Keys.newKey("item_list");
        configKey = Keys.newKey("config");
        cargoKey = Keys.newKey("cargo_type");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void preRegister() {
        addItemHandler((ItemUseHandler) e -> {
            e.cancel();
            Block target = e.getClickedBlock().orElse(null);
            // No target, return
            if (target == null) return;
            ItemStack tool = e.getItem();
            Player p = e.getPlayer();
            if (!isTool(tool)) {
                // Not holding a valid tool, return
                p.sendMessage(Lang.getString("messages.unsupported-operation.cargo_node_quick_tool.invalid_tool"));
                return;
            }
            Location bLoc = target.getLocation();
            // If not cargo node block, return.
            SlimefunBlockData blockData = StorageCacheUtils.getBlock(bLoc);
            if (blockData == null || !blockData.getSfId().startsWith("CARGO_NODE_")) {
                p.sendMessage(Lang.getString("messages.unsupported-operation.cargo_node_quick_tool.invalid_node"));
                return;
            }
            ItemMeta meta = tool.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (p.isSneaking()) {
                // process to load config from target
                switch (blockData.getSfId()) {
                    case "CARGO_NODE_INPUT":
                    case "CARGO_NODE_OUTPUT_ADVANCED":
                        YamlConfiguration itemConfig = new YamlConfiguration();
                        BlockMenu inv = blockData.getBlockMenu();
                        if (inv == null) {
                            return;
                        }
                        for (int slot : listSlots) {
                            ItemStack itemInSlot = inv.getItemInSlot(slot);
                            if (itemInSlot != null) itemConfig.set(String.valueOf(slot), itemInSlot);
                        }
                        container.set(listKey, PersistentDataType.STRING, itemConfig.saveToString());
                    case "CARGO_NODE_OUTPUT":
                        // save cargo type and config
                        container.set(cargoKey, PersistentDataType.STRING, blockData.getSfId());
                        container.set(configKey, PersistentDataType.STRING, gson.toJson(blockData.getAllData()));
                        // update lore
                        List<String> lore = meta.getLore();
                        if (lore == null) {
                            lore = new ArrayList<>(1);
                        }
                        SlimefunItem sf = SlimefunItem.getById(blockData.getSfId());
                        if (sf != null) {
                            lore.set(
                                    lore.size() - 1,
                                    String.format(
                                            Lang.getString(
                                                    "messages.completed-operation.cargo_node_quick_tool.node_set"),
                                            sf.getItemName()));
                        }
                        meta.setLore(lore);
                        tool.setItemMeta(meta);
                        p.sendMessage(
                                Lang.getString("messages.completed-operation.cargo_node_quick_tool.config_saved"));
                        return;
                    default:
                        p.sendMessage(
                                Lang.getString("messages.unsupported-operation.cargo_node_quick_tool.invalid_node"));
                }
            } else {
                // process to set config to target
                String storedId = container.get(cargoKey, PersistentDataType.STRING);
                if (storedId == null) {
                    p.sendMessage(Lang.getString("messages.unsupported-operation.cargo_node_quick_tool.no-config"));
                    return;
                }
                if (!storedId.equalsIgnoreCase(blockData.getSfId())) {
                    p.sendMessage(
                            Lang.getString("messages.unsupported-operation.cargo_node_quick_tool.nod-type-not-same"));
                    return;
                }
                BlockMenu inv = blockData.getBlockMenu();
                if (inv == null) {
                    return;
                }

                switch (blockData.getSfId()) {
                    case "CARGO_NODE_INPUT":
                    case "CARGO_NODE_OUTPUT_ADVANCED":
                        YamlConfiguration itemConfig = new YamlConfiguration();
                        try {
                            String cfg = container.get(listKey, PersistentDataType.STRING);
                            if (cfg == null) {
                                return;
                            }
                            itemConfig.loadFromString(cfg);
                        } catch (Exception ex) {
                            Debug.trace(ex);
                            return;
                        }
                        if (!itemConfig.getKeys(false).isEmpty()) {
                            Map<ItemStack, Boolean> itemList = new HashMap<>();
                            Map<ItemStack, Integer> consumeSlots = new HashMap<>();
                            // init item check list
                            for (String each : itemConfig.getKeys(false)) {
                                itemList.put(itemConfig.getItemStack(each), false);
                            }
                            PlayerInventory pInv = p.getInventory();
                            for (int i = 0; i <= 35; i++) {
                                ItemStack includeItem = isInclude(pInv.getItem(i), itemList);
                                if (includeItem != null) consumeSlots.put(includeItem, i);
                                // if all included, stop checking
                                if (isAllTrue(itemList.values())) break;
                            }
                            if (isAllTrue(itemList.values())) {
                                // replace items in target inv
                                for (int slot : listSlots) {
                                    ItemStack item = itemConfig.getItemStack(String.valueOf(slot));
                                    if (item != null) {
                                        int consumeSlot = consumeSlots.get(item);
                                        ItemStack itemInInv = pInv.getItem(consumeSlot);
                                        if (itemInInv == null) {
                                            continue;
                                        }
                                        ItemStack itemInTarget = inv.getItemInSlot(slot);
                                        if (SlimefunUtils.isItemSimilar(itemInTarget, item, true, false)) {
                                            if (itemInTarget.getAmount() < item.getAmount()) {
                                                itemInInv.setAmount(Math.max(
                                                        itemInInv.getAmount()
                                                                - (item.getAmount() - itemInTarget.getAmount()),
                                                        0));
                                            }
                                        } else {
                                            itemInInv.setAmount(Math.max(itemInInv.getAmount() - item.getAmount(), 0));
                                        }
                                        pInv.setItem(consumeSlot, itemInInv);
                                        inv.replaceExistingItem(slot, item);
                                    } else {
                                        inv.replaceExistingItem(slot, null);
                                    }
                                }
                            } else {
                                p.sendMessage(Lang.getString(
                                        "messages.unsupported-operation.cargo_node_quick_tool.not_enough_items"));
                                for (ItemStack item : itemList.keySet()) {
                                    if (!itemList.get(item)) {
                                        p.sendMessage(TextUtil.color("- &e" + ItemStackHelper.getDisplayName(item) + "x"
                                                + item.getAmount()));
                                    } else {
                                        for (int slot : listSlots) {
                                            inv.replaceExistingItem(slot, null);
                                        }
                                    }
                                    return;
                                }
                            }
                        }

                    case "CARGO_NODE_OUTPUT":
                        Map<String, String> config = gson.fromJson(
                                container.get(configKey, PersistentDataType.STRING),
                                new TypeToken<Map<String, String>>() {}.getType());
                        if (config != null) {
                            config.forEach(blockData::setData);
                        }
                        inv.getPreset().newInstance(inv, bLoc);
                        p.sendMessage(
                                Lang.getString("messages.completed-operation.cargo_node_quick_tool.pasted_config"));
                        return;
                    default:
                        p.sendMessage(
                                Lang.getString("messages.unsupported-operation.cargo_node_quick_tool.invalid_node"));
                }
            }
        });
    }

    private @Nullable ItemStack isInclude(@Nullable ItemStack item, @NotNull Map<ItemStack, Boolean> checkList) {
        for (ItemStack each : checkList.keySet()) {
            if (item != null
                    && StackUtils.itemsMatch(each, item, true, false)
                    && item.getAmount() >= each.getAmount()) {
                checkList.put(each, true);
                return each;
            }
        }
        return null;
    }

    private boolean isAllTrue(@NotNull Collection<Boolean> set) {
        for (boolean each : set) {
            if (!each) return false;
        }
        return true;
    }

    private boolean isTool(@Nullable ItemStack tool) {
        if (tool != null && tool.getItemMeta() != null) {
            Slimefun sf = Slimefun.instance();
            if (sf != null) {
                NamespacedKey idKey = Keys.customNewKey(sf, "slimefun_item");
                PersistentDataContainer container = tool.getItemMeta().getPersistentDataContainer();
                if (container.has(idKey, PersistentDataType.STRING)) {
                    return getId().equalsIgnoreCase(container.get(idKey, PersistentDataType.STRING))
                            && (tool.getAmount() == 1);
                }
            }
        }
        return false;
    }
}
