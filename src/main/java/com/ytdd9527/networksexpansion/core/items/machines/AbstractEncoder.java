package com.ytdd9527.networksexpansion.core.items.machines;

import com.balugaq.netex.api.helpers.Icon;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public abstract class AbstractEncoder extends NetworkObject {

    private static final int[] BACKGROUND = new int[]{
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 15, 17, 18, 20, 24, 25, 26, 27, 28, 29, 33, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44
    };
    private static final int[] RECIPE_SLOTS = new int[]{
            12, 13, 14, 21, 22, 23, 30, 31, 32
    };
    private static final int[] BLUEPRINT_BACK = new int[]{
            10, 28
    };
    private static final int BLANK_BLUEPRINT_SLOT = 19;
    private static final int ENCODE_SLOT = 16;
    private static final int OUTPUT_SLOT = 34;
    private static final int CHARGE_COST = 2000;

    public AbstractEncoder(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
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
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
                menu.addMenuClickHandler(ENCODE_SLOT, (player, i, itemStack, clickAction) -> {
                    tryEncode(player, menu);
                    return false;
                });
            }

            @Override
            public boolean canOpen(@Nonnull Block block, @Nonnull Player player) {
                return player.hasPermission("slimefun.inventory.bypass") || (this.getSlimefunItem().canUse(player, false)
                        && Slimefun.getProtectionManager().hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
    }

    public void tryEncode(@Nonnull Player player, @Nonnull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            sendDebugMessage(blockMenu.getLocation(), "No network found");
            return;
        }

        final NetworkRoot root = definition.getNode().getRoot();
        final long networkCharge = root.getRootPower();

        if (networkCharge < CHARGE_COST) {
            player.sendMessage(Theme.WARNING + "网络中的电力不足，无法完成该任务");
            sendDebugMessage(blockMenu.getLocation(), "Network charge is not enough");
            return;
        }

        ItemStack blueprint = blockMenu.getItemInSlot(BLANK_BLUEPRINT_SLOT);

        if (!isValidBlueprint(blueprint)) {
            player.sendMessage(Theme.WARNING + "你需要提供一个正确的空白的蓝图");
            sendDebugMessage(blockMenu.getLocation(), "Invalid blueprint");
            return;
        }

        SlimefunItem sfi = SlimefunItem.getByItem(blueprint);
        if (sfi != null && sfi.isDisabled()) {
            player.sendMessage(Theme.WARNING + "该蓝图已被禁用");
            sendDebugMessage(blockMenu.getLocation(), "Blueprint is disabled");
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
                crafted = new ItemStack(entry.getValue().clone());
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
                player.sendMessage(Theme.WARNING + "该配方的输出已被禁用");
                sendDebugMessage(blockMenu.getLocation(), "Output is disabled");
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
            player.sendMessage(Theme.WARNING + "这似乎不是一个有效的配方");
            sendDebugMessage(blockMenu.getLocation(), "Invalid recipe");
            return;
        }

        if (crafted.getType() == Material.AIR) {
            player.sendMessage(Theme.WARNING + "编码的结果是空气，这不是一个有效的配方。");
            sendDebugMessage(blockMenu.getLocation(), "Encoded result is air");
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
        } else {
            player.sendMessage(Theme.WARNING + "需要清空输出烂");
            sendDebugMessage(blockMenu.getLocation(), "Output slot is full");
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
}
