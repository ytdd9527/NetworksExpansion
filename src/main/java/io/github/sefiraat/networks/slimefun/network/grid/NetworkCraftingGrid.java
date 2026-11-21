package io.github.sefiraat.networks.slimefun.network.grid;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.api.helpers.SupportedCraftingTableRecipes;
import com.balugaq.netex.utils.BlockMenuUtil;
import com.balugaq.netex.utils.Lang;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.events.NetworkCraftEvent;
import io.github.sefiraat.networks.network.GridItemRequest;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@SuppressWarnings("DuplicatedCode")
@NullMarked
public class NetworkCraftingGrid extends AbstractGrid {

    private static final int[] BACKGROUND_SLOTS = {
        0, 1, 3, 4, 5, 14, 23, 32, 33, 35, 41, 42, 44, 45, 47, 49, 50, 51, 52, 53
    };

    private static final int[] DISPLAY_SLOTS = {
        9, 10, 11, 12, 13, 18, 19, 20, 21, 22, 27, 28, 29, 30, 31, 36, 37, 38, 39, 40
    };

    private static final int[] CRAFT_ITEMS = {6, 7, 8, 15, 16, 17, 24, 25, 26};

    private static final int INPUT_SLOT = 2;
    private static final int FILTER = 45;
    private static final int PAGE_PREVIOUS = 46;
    private static final int CHANGE_SORT = 47;
    private static final int PAGE_NEXT = 48;

    private static final int CRAFT_BUTTON_SLOT = 34;
    private static final int CRAFT_OUTPUT_SLOT = 43;

    public NetworkCraftingGrid(
        ItemGroup itemGroup,
        SlimefunItemStack item,
        RecipeType recipeType,
        ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        for (int craftItem : CRAFT_ITEMS) {
            this.getSlotsToDrop().add(craftItem);
        }
        this.getSlotsToDrop().add(CRAFT_OUTPUT_SLOT);
    }

    @Override
    public void menuNewInstance(BlockMenu menu, Block b) {
        super.menuNewInstance(menu, b);
        menu.replaceExistingItem(CRAFT_BUTTON_SLOT, Icon.CRAFT_BUTTON);
        menu.addMenuClickHandler(CRAFT_BUTTON_SLOT, (player, slot, item, action) -> {
            if (action.isShiftClicked()) {
                tryReturnItems(menu);
            } else {
                tryCraft(menu, player);
            }
            return false;
        });
    }

    @Override
    protected SlotLayout slotLayout() {
        return new SlotLayout(
            BACKGROUND_SLOTS, DISPLAY_SLOTS, INPUT_SLOT,
            CHANGE_SORT, PAGE_PREVIOUS, PAGE_NEXT, FILTER);
    }

    private void tryCraft(BlockMenu menu, Player player) {
        // Get node and, if it doesn't exist - escape
        final NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());
        if (definition == null || definition.getNode() == null) {
            return;
        }

        // Get the recipe input
        final ItemStack[] inputs = new ItemStack[CRAFT_ITEMS.length];
        int i = 0;
        for (int recipeSlot : CRAFT_ITEMS) {
            ItemStack stack = menu.getItemInSlot(recipeSlot);
            inputs[i] = stack;
            i++;
        }

        ItemStack crafted = null;

        // Go through each slimefun recipe, trigger and set the ItemStack if found
        for (Map.Entry<ItemStack[], ItemStack> entry :
            SupportedCraftingTableRecipes.getRecipes().entrySet()) {
            if (SupportedCraftingTableRecipes.testRecipe(inputs, entry.getKey())) {
                crafted = entry.getValue().clone();
                break;
            }
        }

        if (crafted != null) {
            final SlimefunItem sfi2 = SlimefunItem.getByItem(crafted);
            if (sfi2 != null && sfi2.isDisabled()) {
                player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.disabled_output"));
                return;
            }
        }

        // If no slimefun recipe found, try a vanilla one
        if (crafted == null) {
            crafted = Bukkit.craftItem(inputs, player.getWorld(), player);
        }

        // If no item crafted OR result doesn't fit, escape
        if (crafted.getType() == Material.AIR || !BlockMenuUtil.fits(menu, crafted, CRAFT_OUTPUT_SLOT)) {
            return;
        }

        // fire craft event
        NetworkCraftEvent event = new NetworkCraftEvent(player, this, inputs, crafted);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        crafted = event.getOutput();

        // Push item
        if (crafted != null) {
            BlockMenuUtil.pushItem(menu, crafted, CRAFT_OUTPUT_SLOT);
        }

        NetworkRoot root = definition.getNode().getRoot();

        // Let's clear down all the items
        for (int recipeSlot : CRAFT_ITEMS) {
            final ItemStack itemInSlot = menu.getItemInSlot(recipeSlot);
            if (itemInSlot != null) {
                // Grab a clone for potential retrieval
                final ItemStack itemInSlotClone = itemInSlot.clone();
                itemInSlotClone.setAmount(1);
                BlockMenuUtil.consumeItem(menu, recipeSlot, 1, true);
                // We have consumed a slot item and now the slot is empty - try to refill
                if (menu.getItemInSlot(recipeSlot) == null) {
                    // Process item request
                    final GridItemRequest request = new GridItemRequest(itemInSlotClone, 1, player);
                    final ItemStack requestingStack = root.getItemStack(menu.getLocation(), request);
                    if (requestingStack != null) {
                        menu.replaceExistingItem(recipeSlot, requestingStack);
                    }
                }
            }
        }
    }

    private void tryReturnItems(BlockMenu menu) {
        // Get node and, if it doesn't exist - escape
        final NodeDefinition definition = NetworkStorage.getNode(menu.getLocation());

        if (definition == null || definition.getNode() == null) {
            return;
        }

        for (int recipeSlot : CRAFT_ITEMS) {
            final ItemStack stack = menu.getItemInSlot(recipeSlot);

            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }
            definition.getNode().getRoot().addItemStack(menu.getLocation(), stack);
        }
    }
}
