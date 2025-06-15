package com.ytdd9527.networksexpansion.implementation.machines.manual;

import com.balugaq.netex.api.data.StorageUnitData;
import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.enums.StorageUnitType;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.implementation.ExpansionItemStacks;
import com.ytdd9527.networksexpansion.implementation.machines.unit.NetworksDrawer;
import com.ytdd9527.networksexpansion.utils.databases.DataStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.events.NetworkCraftEvent;
import io.github.sefiraat.networks.slimefun.network.AdminDebuggable;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class StorageUnitUpgradeTable extends SpecialSlimefunItem implements AdminDebuggable {
    private static final Map<ItemStack[], ItemStack> recipes = new HashMap<>();
    public static final RecipeType TYPE = new RecipeType(
            Keys.STORAGE_UNIT_UPGRADE_TABLE,
            ExpansionItemStacks.STORAGE_UNIT_UPGRADE_TABLE,
            StorageUnitUpgradeTable::addRecipe);
    private final int[] border = {0, 8, 9, 17, 18, 26};
    private final int[] innerBorder = {1, 5, 6, 7, 10, 14, 16, 19, 23, 24, 25};
    private final int[] inputSlots = {2, 3, 4, 11, 12, 13, 20, 21, 22};
    private final int outputSlot = 15;
    private final int actionBtnSlot = 17;

    public StorageUnitUpgradeTable(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);

        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                for (int slot : border) {
                    addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
                }
                ItemStack innerBorderItem = new CustomItemStack(Material.LIME_STAINED_GLASS_PANE, " ", " ");
                for (int slot : innerBorder) {
                    addItem(slot, innerBorderItem, ChestMenuUtils.getEmptyClickHandler());
                }
                addItem(outputSlot, null, new AdvancedMenuClickHandler() {
                    @Override
                    public boolean onClick(
                            @NotNull InventoryClickEvent e,
                            Player p,
                            int slot,
                            @Nullable ItemStack cursor,
                            ClickAction action) {
                        ItemStack itemInSlot = e.getInventory().getItem(slot);
                        return (cursor == null || cursor.getType() == Material.AIR)
                                && (itemInSlot == null || itemInSlot.getType() != Material.BARRIER);
                    }

                    @Override
                    public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                        return false;
                    }
                });
            }

            @Override
            public void newInstance(@NotNull BlockMenu menu, @NotNull Block b) {
                menu.addMenuClickHandler(actionBtnSlot, (p, slot, item, action) -> {
                    craft(p, menu);
                    return false;
                });
                menu.replaceExistingItem(actionBtnSlot, Icon.ACTION_BUTTON);
            }

            @Override
            public boolean canOpen(@NotNull Block b, @NotNull Player p) {
                return p.hasPermission("slimefun.inventory.bypass")
                        || (canUse(p, false)
                                && Slimefun.getProtectionManager().hasPermission(p, b, Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
    }

    public static void addRecipe(ItemStack[] recipe, ItemStack out) {
        recipes.put(recipe, out);
    }

    private void craft(@NotNull Player p, @NotNull BlockMenu menu) {
        for (Map.Entry<ItemStack[], ItemStack> each : recipes.entrySet()) {
            if (match(menu, each.getKey())) {
                ItemStack itemInSlot = menu.getItemInSlot(outputSlot);
                int id = NetworksDrawer.getBoundId(menu.getItemInSlot(inputSlots[4]));
                ItemStack out = each.getValue().clone();
                if (id != -1) {
                    if (itemInSlot != null && itemInSlot.getType() != Material.AIR) {
                        // Shouldn't output or upgrade any drawer when something taking the place
                        return;
                    }
                    if (DataStorage.isContainerLoaded(id)) {
                        if (DataStorage.getCachedStorageData(id).isPresent()) {
                            StorageUnitData data =
                                    DataStorage.getCachedStorageData(id).get();
                            upgrade(data);
                        }
                    } else {
                        // Schedule to the same thread to ensure that this task is running after data request
                        DataStorage.requestStorageData(id);
                        Networks.getQueryQueue().scheduleQuery(() -> {
                            if (DataStorage.getCachedStorageData(id).isPresent()) {
                                StorageUnitData data =
                                        DataStorage.getCachedStorageData(id).get();
                                upgrade(data);
                            }
                            return true;
                        });
                    }
                    out = NetworksDrawer.bindIdNew(
                            out, id, NetworksDrawer.isLocked(id), NetworksDrawer.isVoidExcess(id));
                }
                SlimefunItemStack sfis = (SlimefunItemStack) out;
                SlimefunItem sfi = SlimefunItem.getById(sfis.getItemId());
                if (sfi != null && sfi.isDisabled()) {
                    return;
                }

                // fire craft event
                ItemStack[] inputs = new ItemStack[inputSlots.length];
                for (int i = 0; i < inputSlots.length; i++) {
                    ItemStack itemStack = menu.getItemInSlot(inputSlots[i]);
                    if (itemStack == null) {
                        inputs[i] = null;
                    } else {
                        inputs[i] = itemStack.clone();
                    }
                }
                NetworkCraftEvent event = new NetworkCraftEvent(p, this, inputs, out);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }
                out = event.getOutput();

                if (itemInSlot == null || itemInSlot.getType() == Material.AIR) {
                    menu.replaceExistingItem(outputSlot, out);
                } else if (StackUtils.itemsMatch(itemInSlot, out)) {
                    if (itemInSlot.getAmount() + (out == null ? 0 : out.getAmount()) <= itemInSlot.getMaxStackSize()) {
                        itemInSlot.setAmount(itemInSlot.getAmount() + (out == null ? 0 : out.getAmount()));
                    } else {
                        return;
                    }
                } else {
                    return;
                }
                for (int slot : inputSlots) {
                    menu.consumeItem(slot);
                }
                sendFeedback(menu.getLocation(), FeedbackType.SUCCESS);

                return;
            }
        }

        p.sendMessage(Lang.getString("messages.unsupported-operation.storage_unit_upgrade_table.no_recipe_match"));
    }

    private boolean match(@NotNull BlockMenu menu, ItemStack[] recipe) {
        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                SlimefunItem sfItem = SlimefunItem.getByItem(menu.getItemInSlot(inputSlots[i]));
                if (sfItem != null && SlimefunUtils.isItemSimilar(recipe[i], sfItem.getItem(), true, false)) {
                    continue;
                } else if (!StackUtils.itemsMatch(recipe[i], menu.getItemInSlot(inputSlots[i]))) {
                    return false;
                }
            }
            if (!StackUtils.itemsMatch(recipe[i], menu.getItemInSlot(inputSlots[i]))) {
                return false;
            }
        }
        return true;
    }

    private void upgrade(@NotNull StorageUnitData d) {
        StorageUnitType next = d.getSizeType().next();
        if (next != null) {
            d.setSizeType(next);
        }
    }

    @Override
    public void preRegister() {
        addItemHandler(getBlockBreakHandler());
    }

    private @NotNull BlockBreakHandler getBlockBreakHandler() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(
                    @NotNull BlockBreakEvent event, @NotNull ItemStack itemStack, @NotNull List<ItemStack> drops) {
                Location l = event.getBlock().getLocation();
                BlockMenu menu = StorageCacheUtils.getMenu(l);
                if (menu != null) {
                    menu.dropItems(menu.getLocation(), inputSlots);
                    menu.dropItems(menu.getLocation(), outputSlot);
                }
                Slimefun.getDatabaseManager().getBlockDataController().removeBlock(l);
            }
        };
    }
}
