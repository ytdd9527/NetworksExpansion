package com.ytdd9527.networksexpansion.implementation.machines.networks.advanced;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.api.helpers.SupportedCraftingTableRecipes;
import com.balugaq.netex.api.interfaces.RecipeCompletableWithGuide;
import com.balugaq.netex.utils.BlockMenuUtil;
import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractGridNewStyle;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.events.NetworkCraftEvent;
import io.github.sefiraat.networks.network.GridItemRequest;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache.DisplayMode;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NetworkCraftingGridNewStyle extends AbstractGridNewStyle implements RecipeCompletableWithGuide {

    private static final int[] BACKGROUND_SLOTS = {5, 14, 23, 32, 41, 43, 50, 51};

    private static final int[] DISPLAY_SLOTS = {
        0, 1, 2, 3, 4,
        9, 10, 11, 12, 13,
        18, 19, 20, 21, 22,
        27, 28, 29, 30, 31,
        36, 37, 38, 39, 40,
        45, 46, 47, 48, 49
    };

    @Deprecated
    private static final int AUTO_FILTER_SLOT = 43;

    private static final int CHANGE_SORT = 35;
    private static final int FILTER = 42;
    private static final int PAGE_PREVIOUS = 44;
    private static final int PAGE_NEXT = 53;
    private static final int TOGGLE_MODE_SLOT = 52;
    private static final int JEG_SLOT = 32;
    private static final int CRAFT_BUTTON_SLOT = 33;
    private static final int OUTPUT_SLOT = 34;
    private static final int[] INTEGRATION_SLOTS = {6, 7, 8, 15, 16, 17, 24, 25, 26};
    private static final Map<Location, GridCache> CACHE_MAP = new HashMap<>();

    public NetworkCraftingGridNewStyle(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    @NotNull protected BlockMenuPreset getPreset() {
        return new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                // drawBackground(getBackgroundSlots());
                drawBackground(getDisplaySlots());
                setSize(54);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                        || (ExpansionItems.NETWORK_CRAFTING_GRID_NEW_STYLE.canUse(player, false)
                                && Slimefun.getProtectionManager()
                                        .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public void newInstance(@NotNull BlockMenu menu, @NotNull Block b) {
                getCacheMap().put(menu.getLocation(), new GridCache(0, 0, GridCache.SortOrder.ALPHABETICAL));

                menu.replaceExistingItem(getPagePrevious(), getPagePreviousStack());
                menu.addMenuClickHandler(getPagePrevious(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    gridCache.setPage(gridCache.getPage() <= 0 ? 0 : gridCache.getPage() - 1);
                    getCacheMap().put(menu.getLocation(), gridCache);
                    updateDisplay(menu);
                    return false;
                });

                menu.replaceExistingItem(getPageNext(), getPageNextStack());
                menu.addMenuClickHandler(getPageNext(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    gridCache.setPage(
                            gridCache.getPage() >= gridCache.getMaxPages()
                                    ? gridCache.getMaxPages()
                                    : gridCache.getPage() + 1);
                    getCacheMap().put(menu.getLocation(), gridCache);
                    updateDisplay(menu);
                    return false;
                });

                menu.replaceExistingItem(getChangeSort(), getChangeSortStack());
                menu.addMenuClickHandler(getChangeSort(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    if (gridCache.getSortOrder() == GridCache.SortOrder.ALPHABETICAL) {
                        gridCache.setSortOrder(GridCache.SortOrder.NUMBER);
                    } else if (gridCache.getSortOrder() == GridCache.SortOrder.NUMBER) {
                        gridCache.setSortOrder(GridCache.SortOrder.NUMBER_REVERSE);
                    } else if (gridCache.getSortOrder() == GridCache.SortOrder.NUMBER_REVERSE) {
                        gridCache.setSortOrder(GridCache.SortOrder.ADDON);
                    } else {
                        gridCache.setSortOrder(GridCache.SortOrder.ALPHABETICAL);
                    }
                    getCacheMap().put(menu.getLocation(), gridCache);
                    updateDisplay(menu);
                    return false;
                });

                menu.replaceExistingItem(getFilterSlot(), getFilterStack());
                menu.addMenuClickHandler(getFilterSlot(), (p, slot, item, action) -> {
                    GridCache gridCache = getCacheMap().get(menu.getLocation());
                    setFilter(p, menu, gridCache, action);
                    return false;
                });

                menu.replaceExistingItem(getToggleModeSlot(), getModeStack(DisplayMode.DISPLAY));
                menu.addMenuClickHandler(getToggleModeSlot(), (p, slot, item, action) -> {
                    if (!action.isRightClicked()) {
                        GridCache gridCache = getCacheMap().get(menu.getLocation());
                        gridCache.toggleDisplayMode();
                        menu.replaceExistingItem(getToggleModeSlot(), getModeStack(gridCache));
                        updateDisplay(menu);
                    }
                    return false;
                });

                menu.replaceExistingItem(CRAFT_BUTTON_SLOT, Icon.CRAFT_BUTTON_NEW_STYLE);
                menu.addMenuClickHandler(CRAFT_BUTTON_SLOT, (p, slot, item, action) -> {
                    tryCraft(menu, p, action);
                    return false;
                });

                for (int displaySlot : getDisplaySlots()) {
                    menu.replaceExistingItem(displaySlot, ChestMenuUtils.getBackground());
                    menu.addMenuClickHandler(displaySlot, (p, slot, item, action) -> false);
                }

                ItemStack exist = menu.getItemInSlot(getAutoFilterSlot());
                if (exist != null
                        && exist.getType() != Material.AIR
                        && !StackUtils.itemsMatch(exist, ChestMenuUtils.getBackground())) {
                    // drop item
                    menu.getLocation().getWorld().dropItemNaturally(menu.getLocation(), exist);
                }

                for (int backgroundSlot : getBackgroundSlots()) {
                    menu.replaceExistingItem(backgroundSlot, ChestMenuUtils.getBackground());
                    menu.addMenuClickHandler(backgroundSlot, (p, slot, item, action) -> false);
                }

                menu.addPlayerInventoryClickHandler((p, s, i, a) -> {
                    if (!a.isShiftClicked() || a.isRightClicked()) {
                        return true;
                    }

                    // Shift+Left-click
                    receiveItem(p, i, a, menu);
                    return false;
                });

                addJEGButton(menu, JEG_SLOT);
            }
        };
    }

    @NotNull public Map<Location, GridCache> getCacheMap() {
        return CACHE_MAP;
    }

    public int[] getBackgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    public int[] getDisplaySlots() {
        return DISPLAY_SLOTS;
    }

    public int getChangeSort() {
        return CHANGE_SORT;
    }

    public int getPagePrevious() {
        return PAGE_PREVIOUS;
    }

    public int getPageNext() {
        return PAGE_NEXT;
    }

    @Deprecated
    public int getAutoFilterSlot() {
        return AUTO_FILTER_SLOT;
    }

    public int getToggleModeSlot() {
        return TOGGLE_MODE_SLOT;
    }

    @Override
    protected int getFilterSlot() {
        return FILTER;
    }

    @SuppressWarnings("deprecation")
    private synchronized void tryCraft(@NotNull BlockMenu menu, @NotNull Player player, @NotNull ClickAction action) {
        // Get node and, if it doesn't exist - escape
        final NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());
        if (definition == null || definition.getNode() == null) {
            return;
        }

        NetworkRoot root = definition.getNode().getRoot();
        root.refreshRootItems();

        if (!action.isRightClicked() && action.isShiftClicked()) {
            ItemStack output = menu.getItemInSlot(OUTPUT_SLOT);
            if (output != null && output.getType() != Material.AIR) {
                root.addItemStack0(menu.getLocation(), output);
            }
            return;
        }

        // Get the recipe input
        final ItemStack[] inputs = new ItemStack[INTEGRATION_SLOTS.length];
        int i = 0;
        for (int recipeSlot : INTEGRATION_SLOTS) {
            ItemStack stack = menu.getItemInSlot(recipeSlot);
            inputs[i] = stack;
            i++;
        }

        ItemStack crafted = null;
        Map.Entry<ItemStack[], ItemStack> matched = null;

        // Go through each slimefun recipe, test and set the ItemStack if found
        for (Map.Entry<ItemStack[], ItemStack> entry :
                SupportedCraftingTableRecipes.getRecipes().entrySet()) {
            if (SupportedCraftingTableRecipes.testRecipe(inputs, entry.getKey())) {
                crafted = entry.getValue().clone();
                matched = entry;
                break;
            }
        }

        // If no slimefun recipe found, try a vanilla one
        if (crafted == null) {
            ItemStack[] _inputs = Arrays.stream(inputs.clone())
                    .map(itemStack -> itemStack != null ? StackUtils.getAsQuantity(itemStack, 1) : null)
                    .toArray(ItemStack[]::new);
            crafted = Bukkit.craftItem(_inputs.clone(), player.getWorld(), player);
            Map<ItemStack[], ItemStack> v = new HashMap<>();
            v.put(_inputs, crafted);
            matched = v.entrySet().stream().findFirst().get();
        }

        // If no item crafted OR result doesn't fit, escape
        if (crafted.getType() == Material.AIR || !menu.fits(crafted, OUTPUT_SLOT)) {
            return;
        }

        if (crafted != null) {
            final SlimefunItem sfi2 = SlimefunItem.getByItem(crafted);
            if (sfi2 != null && sfi2.isDisabled()) {
                player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.disabled_output"));
                return;
            }
        }

        ItemStack output = menu.getItemInSlot(OUTPUT_SLOT);
        if (output != null && output.getType() != Material.AIR) {
            root.addItemStack0(menu.getLocation(), output);
        }

        if (!BlockMenuUtil.fits(menu, crafted, OUTPUT_SLOT)) {
            return;
        }

        int maxAmount = action.isRightClicked() ? 64 : 1;

        if (output != null && output.getType() != Material.AIR) {
            maxAmount = Math.min(maxAmount, (output.getMaxStackSize() - output.getAmount()) / crafted.getAmount());
        }

        Map<ItemStack, Integer> requiredItems = new HashMap<>();
        for (ItemStack input : matched.getKey()) {
            if (input != null && input.getType() != Material.AIR) {
                requiredItems.merge(input, input.getAmount(), Integer::sum);
            }
        }

        maxAmount = Math.min(maxAmount, crafted.getMaxStackSize() / crafted.getAmount());

        Map<ItemStack, Long> currentItems = root.getAllNetworkItemsLongType();

        // calculate the max amount can be crafted
        for (Map.Entry<ItemStack, Integer> entry : requiredItems.entrySet()) {
            ItemStack is = entry.getKey();
            if (is == null || is.getType() == Material.AIR) {
                continue;
            }
            int required = entry.getValue();
            Long current = currentItems.get(is);
            if (current == null) {
                maxAmount = 0;
                break;
            }
            maxAmount = Math.min(maxAmount, (int) (current / required));
        }

        Map<ItemStack, Integer> need = new HashMap<>();
        for (Map.Entry<ItemStack, Integer> entry : requiredItems.entrySet()) {
            int required = entry.getValue();
            int amount = maxAmount * required;
            need.put(entry.getKey(), amount);
        }

        // consume items
        for (Map.Entry<ItemStack, Integer> entry : need.entrySet()) {
            ItemStack itemStack = entry.getKey();
            GridItemRequest request = new GridItemRequest(itemStack, entry.getValue(), player);
            root.getItemStack0(menu.getLocation(), request);
            if (StackUtils.itemsMatch(itemStack, new ItemStack(itemStack.getType()))) {
                switch (itemStack.getType()) {
                    case WATER_BUCKET,
                            LAVA_BUCKET,
                            MILK_BUCKET,
                            COD_BUCKET,
                            SALMON_BUCKET,
                            PUFFERFISH_BUCKET,
                            TROPICAL_FISH_BUCKET,
                            AXOLOTL_BUCKET,
                            POWDER_SNOW_BUCKET,
                            TADPOLE_BUCKET -> root.addItemStack0(
                            menu.getLocation(), new ItemStack(Material.BUCKET, entry.getValue()));
                    case POTION, SPLASH_POTION, LINGERING_POTION, HONEY_BOTTLE, DRAGON_BREATH -> root.addItemStack0(
                            menu.getLocation(), new ItemStack(Material.GLASS_BOTTLE, entry.getValue()));
                    case MUSHROOM_STEW, BEETROOT_SOUP, RABBIT_STEW, SUSPICIOUS_STEW -> root.addItemStack0(
                            menu.getLocation(), new ItemStack(Material.BOWL, entry.getValue()));
                }
            }
        }

        // push items
        int outputAmount = crafted.getAmount() * maxAmount;
        crafted = StackUtils.getAsQuantity(crafted, outputAmount);

        // fire craft event
        NetworkCraftEvent event = new NetworkCraftEvent(player, this, inputs, crafted);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        crafted = event.getOutput();

        if (crafted != null) {
            BlockMenuUtil.pushItem(menu, crafted, OUTPUT_SLOT);
            menu.replaceExistingItem(
                    CRAFT_BUTTON_SLOT,
                    ItemStackUtil.getCleanItem(new CustomItemStack(
                            Icon.CRAFT_BUTTON_NEW_STYLE,
                            String.format(
                                    Lang.getString("messages.normal-operation.grid_new_style.crafted"),
                                    ItemStackHelper.getDisplayName(crafted),
                                    outputAmount))));
        }
    }

    @Override
    public int[] getIngredientSlots() {
        return INTEGRATION_SLOTS;
    }
}
