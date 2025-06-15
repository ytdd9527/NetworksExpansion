package com.ytdd9527.networksexpansion.core.items.machines;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.api.interfaces.RecipeCompletableWithGuide;
import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import java.util.Map;
import java.util.Set;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractEncoder extends NetworkObject implements RecipeCompletableWithGuide {
    private static final int[] BACKGROUND = new int[] {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 15, 17, 18, 20, 24, 25, 26, 27, 28, 29, 33, 35, 36, 37, 38, 39, 40, 41,
        42, 43, 44
    };
    private static final int[] RECIPE_SLOTS = new int[] {12, 13, 14, 21, 22, 23, 30, 31, 32};
    private static final int[] BLUEPRINT_BACK = new int[] {10, 28};
    private static final int BLANK_BLUEPRINT_SLOT = 19;
    private static final int ENCODE_SLOT = 16;
    private static final int OUTPUT_SLOT = 34;
    private static final int JEG_SLOT = 4;
    private static final int CHARGE_COST = 2000;

    public AbstractEncoder(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.ENCODER);
        for (int recipeSlot : RECIPE_SLOTS) {
            this.getSlotsToDrop().add(recipeSlot);
        }
        this.getSlotsToDrop().add(BLANK_BLUEPRINT_SLOT);
        this.getSlotsToDrop().add(OUTPUT_SLOT);
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                drawBackground(BACKGROUND);
                drawBackground(Icon.BLUEPRINT_BACK_STACK, BLUEPRINT_BACK);

                addItem(ENCODE_SLOT, Icon.ENCODE_STACK, (player, i, itemStack, clickAction) -> false);
            }

            @Override
            public void newInstance(@NotNull BlockMenu menu, @NotNull Block b) {
                menu.addMenuClickHandler(ENCODE_SLOT, (player, i, itemStack, clickAction) -> {
                    tryEncode(player, menu);
                    return false;
                });
                addJEGButton(menu, JEG_SLOT);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                        || (this.getSlimefunItem().canUse(player, false)
                                && Slimefun.getProtectionManager()
                                        .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
    }

    public void tryEncode(@NotNull Player player, @NotNull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            player.sendMessage(Lang.getString("messages.feedback.no_network_found"));
            return;
        }

        final NetworkRoot root = definition.getNode().getRoot();
        final long networkCharge = root.getRootPower();

        if (networkCharge < CHARGE_COST) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.not_enough_power"));
            sendFeedback(blockMenu.getLocation(), FeedbackType.NOT_ENOUGH_POWER);
            return;
        }

        ItemStack blueprint = blockMenu.getItemInSlot(BLANK_BLUEPRINT_SLOT);

        if (!isValidBlueprint(blueprint)) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.invalid_blueprint"));
            sendFeedback(blockMenu.getLocation(), FeedbackType.INVALID_BLUEPRINT);
            return;
        }

        SlimefunItem sfi = SlimefunItem.getByItem(blueprint);
        if (sfi != null && sfi.isDisabled()) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.disabled_blueprint"));
            sendFeedback(blockMenu.getLocation(), FeedbackType.DISABLED_BLUEPRINT);
            return;
        }

        // Get the recipe input
        final ItemStack[] inputs = new ItemStack[RECIPE_SLOTS.length];
        int i = 0;
        for (int recipeSlot : RECIPE_SLOTS) {
            ItemStack stackInSlot = blockMenu.getItemInSlot(recipeSlot);
            if (stackInSlot != null) {
                inputs[i] = ItemStackUtil.getCleanItem(stackInSlot.clone());
            }
            i++;
        }

        ItemStack crafted = null;
        ItemStack[] inp = inputs.clone();
        for (int k = 0; k < inp.length; k++) {
            if (inp[k] != null) {
                inp[k] = ItemStackUtil.getCleanItem(inp[k]);
            }
        }

        for (Map.Entry<ItemStack[], ItemStack> entry : getRecipeEntries()) {
            if (getRecipeTester(inputs, entry.getKey())) {
                crafted = ItemStackUtil.getCleanItem(entry.getValue().clone());
                inp = entry.getKey().clone();
                for (int k = 0; k < inp.length; k++) {
                    if (inp[k] != null) {
                        inp[k] = ItemStackUtil.getCleanItem(inp[k]);
                    }
                }
                break;
            }
        }

        if (crafted != null) {
            final SlimefunItem sfi2 = SlimefunItem.getByItem(crafted);
            if (sfi2 != null && sfi2.isDisabled()) {
                player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.disabled_output"));
                sendFeedback(blockMenu.getLocation(), FeedbackType.DISABLED_OUTPUT);
                return;
            }
        }

        if (crafted == null && canTestVanillaRecipe(inputs)) {
            crafted = Bukkit.craftItem(inputs.clone(), player.getWorld(), player);
            for (int k = 0; k < RECIPE_SLOTS.length; k++) {
                if (inputs[k] != null) {
                    inp[k] = StackUtils.getAsQuantity(inputs[k], 1);
                }
            }
        }

        if (crafted == null || crafted.getType() == Material.AIR) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.invalid_recipe"));
            sendFeedback(blockMenu.getLocation(), FeedbackType.INVALID_RECIPE);
            return;
        }

        final ItemStack blueprintClone = StackUtils.getAsQuantity(blueprint, 1);

        blueprintSetter(blueprintClone, inp, crafted);
        if (blockMenu.fits(blueprintClone, OUTPUT_SLOT)) {
            blueprint.setAmount(blueprint.getAmount() - 1);
            int j = 0;
            for (int recipeSlot : RECIPE_SLOTS) {
                ItemStack slotItem = blockMenu.getItemInSlot(recipeSlot);
                if (slotItem != null) {
                    slotItem.setAmount(slotItem.getAmount() - inp[j].getAmount());
                }
                j++;
            }
            blockMenu.pushItem(blueprintClone, OUTPUT_SLOT);
            sendFeedback(blockMenu.getLocation(), FeedbackType.SUCCESS);
        } else {
            player.sendMessage(Lang.getString("messages.unsupported-operation.encoder.output_full"));
            sendFeedback(blockMenu.getLocation(), FeedbackType.OUTPUT_FULL);
            return;
        }

        root.removeRootPower(CHARGE_COST);
    }

    public abstract void blueprintSetter(ItemStack itemStack, ItemStack[] inputs, ItemStack crafted);

    public abstract boolean isValidBlueprint(ItemStack itemStack);

    public abstract Set<Map.Entry<ItemStack[], ItemStack>> getRecipeEntries();

    public abstract boolean getRecipeTester(ItemStack[] inputs, ItemStack[] recipe);

    public boolean canTestVanillaRecipe(ItemStack[] inputs) {
        return false;
    }

    @Override
    public int[] getIngredientSlots() {
        return RECIPE_SLOTS;
    }
}
