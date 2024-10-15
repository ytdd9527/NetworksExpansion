package com.ytdd9527.networksexpansion.implementation.machines.networks.advanced;

import com.balugaq.netex.api.helpers.Icon;
import com.ytdd9527.networksexpansion.core.items.unusable.AbstractBlueprint;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import com.balugaq.netex.utils.BlockMenuUtil;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.BlueprintInstance;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.sefiraat.networks.utils.datatypes.PersistentCraftingBlueprintType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Map;

public class NetworkBlueprintDecoder extends NetworkObject {
    private static final int[] BACKGROUND_SLOTS = {0, 1, 2, 3, 4, 5, 9, 11, 12, 14, 18, 19, 20, 21, 22, 23};
    private static final int[] OUTPUT_SLOTS = {6, 7, 8, 15, 16, 17, 24, 25, 26};
    private static final int INPUT_SLOT = 10;
    private static final int DECODE_SLOT = 13;

    public NetworkBlueprintDecoder(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.DECODER);
    }

    public static int[] getBackgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    public static int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }

    public static int getInputSlot() {
        return INPUT_SLOT;
    }

    public static int getDecodeSlot() {
        return DECODE_SLOT;
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                setSize(27);
                for (int slot : BACKGROUND_SLOTS) {
                    addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
                }
                addItem(DECODE_SLOT, Icon.DECODE_ITEM, ChestMenuUtils.getEmptyClickHandler());
            }

            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
                menu.addMenuClickHandler(DECODE_SLOT, (player, slot, clickedItem, clickAction) -> {
                    decode(player, menu);
                    return false;
                });
            }

            @Override
            public boolean canOpen(@Nonnull Block block, @Nonnull Player player) {
                return player.hasPermission("slimefun.inventory.bypass") || (ExpansionItems.ADVANCED_EXPORT.canUse(player, false)
                        && Slimefun.getProtectionManager().hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow == ItemTransportFlow.INSERT) {
                    return new int[]{getInputSlot()};
                }

                return getOutputSlots();
            }
        };
    }

    private void decode(Player player, BlockMenu menu) {
        ItemStack input = menu.getItemInSlot(getInputSlot());
        if (input == null || input.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "没有输入蓝图");
            return;
        }

        SlimefunItem item = SlimefunItem.getByItem(input);
        if (!(item instanceof AbstractBlueprint)) {
            player.sendMessage(ChatColor.RED + "该物品不是蓝图");
            return;
        }

        ItemMeta meta = input.getItemMeta();
        if (meta == null) {
            player.sendMessage(ChatColor.RED + "该物品没有蓝图信息");
            return;
        }

        BlueprintInstance blueprintInstance = DataTypeMethods.getCustom(meta, Keys.BLUEPRINT_INSTANCE, PersistentCraftingBlueprintType.TYPE);
        if (blueprintInstance == null) {
            blueprintInstance = DataTypeMethods.getCustom(meta, Keys.BLUEPRINT_INSTANCE2, PersistentCraftingBlueprintType.TYPE);
        }
        if (blueprintInstance == null) {
            blueprintInstance = DataTypeMethods.getCustom(meta, Keys.BLUEPRINT_INSTANCE3, PersistentCraftingBlueprintType.TYPE);
        }
        if (blueprintInstance == null) {
            player.sendMessage(ChatColor.RED + "该物品没有蓝图信息");
            return;
        }

        ItemStack[] inputs = blueprintInstance.getRecipeItems();

        if (!BlockMenuUtil.fits(menu, inputs, getOutputSlots())) {
            player.sendMessage(ChatColor.RED + "输出栏不足");
            return;
        }

        input.setAmount(input.getAmount() - 1);
        Map<ItemStack, Integer> left = BlockMenuUtil.pushItem(menu, inputs, getOutputSlots());
        if (left != null && !left.isEmpty()) {
            for (Map.Entry<ItemStack, Integer> entry : left.entrySet()) {
                player.sendMessage(ChatColor.RED + "没有足够的位置输出物品! ");
                menu.getLocation().getWorld().dropItem(menu.getLocation(), entry.getKey());
            }
        }

        player.sendMessage(ChatColor.GREEN + "蓝图解码成功!");
    }
}
