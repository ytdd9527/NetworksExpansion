package com.ytdd9527.networksexpansion.implementation.machines.manual;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import com.ytdd9527.networksexpansion.utils.ReflectionUtil;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.QuantumCache;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.sefiraat.networks.utils.datatypes.PersistentQuantumStorageType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("DuplicatedCode")
public class StorageCardConverter extends NetworkObject {
    private static final int[] BACKGROUND_SLOTS = {
        0,  1,  2,  3,  4,  5,  6,  7,  8,
        9,      11, 12,     14, 15,     17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
    };
    private static final int STORAGE_SLOT = 11;
    private static final int CONVERT_SLOT = 13;
    private static final int CARD_SLOT = 16;
    private static SlimefunItem card = null;

    static {
        try {
            if (Networks.getSupportedPluginManager().isFinalTECH()) {
                card = ReflectionUtil.getStaticValue(Class.forName("io.taraxacum.finaltech.setup.FinalTechItems"), "STORAGE_CARD", SlimefunItem.class);
            }
        } catch (ClassNotFoundException | ClassCastException ignored) {
        }
    }

    @ParametersAreNonnullByDefault
    public StorageCardConverter(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.STORAGE_CARD_CONVERTER);
        getSlotsToDrop().add(STORAGE_SLOT);
        getSlotsToDrop().add(CARD_SLOT);
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {
            @Override
            public void init() {
                setSize(27);
                for (int slot : BACKGROUND_SLOTS) {
                    addItem(slot, Icon.GRAY_BACKGROUND);
                }
                addItem(CONVERT_SLOT, Icon.STORAGE_CONVERT_BUTTON);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.hasPermission("slimefun.inventory.bypass")
                    || (ExpansionItems.STORAGE_CARD_CONVERTER.canUse(player, false)
                    && Slimefun.getProtectionManager()
                    .hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public void newInstance(@NotNull BlockMenu menu, @NotNull Block b) {
                menu.addMenuClickHandler(CONVERT_SLOT, (p, s, i, a) -> {
                    if (!Networks.getSupportedPluginManager().isFinalTECH()) {
                        p.sendMessage(Lang.getString(
                            "messages.unsupported-operation.storage_card_converter.final_tech_required"));
                        return false;
                    }
                    if (card == null) {
                        p.sendMessage(Lang.getString(
                            "messages.unsupported-operation.storage_card_converter.final_tech_required"));
                        return false;
                    }

                    var profile = PlayerProfile.find(p).orElse(null);
                    if (profile == null) return false;

                    var research = card.getResearch();
                    if (research != null && research.isEnabled() && !profile.hasUnlocked(research)) {
                        p.sendMessage(Lang.getString(
                            "messages.unsupported-operation.storage_card_converter.research_required"));
                        return false;
                    }
                    if (card.isDisabledIn(b.getWorld())) {
                        p.sendMessage(Lang.getString(
                            "messages.unsupported-operation.storage_card_converter.card_disabled"));
                        return false;
                    }
                    ItemStack quantumStorageItem = menu.getItemInSlot(STORAGE_SLOT);
                    ItemStack storageCardItem = menu.getItemInSlot(CARD_SLOT);
                    if (!(storageCardItem == null || storageCardItem.getType().isAir())) {
                        p.sendMessage(Lang.getString(
                            "messages.unsupported-operation.storage_card_converter.output_full"));
                        return false;
                    }

                    if (quantumStorageItem == null || quantumStorageItem.getType().isAir()) {
                        p.sendMessage(Lang.getString(
                            "messages.unsupported-operation.storage_card_converter.no_quantum_storage"));
                        return false;
                    }

                    if (quantumStorageItem.getAmount() != 1) {
                        p.sendMessage(Lang.getString(
                            "messages.unsupported-operation.storage_card_converter.quantum_storage_not_single"));
                        return false;
                    }

                    ItemMeta meta = quantumStorageItem.getItemMeta();
                    QuantumCache cache = DataTypeMethods.getCustom(meta, Keys.QUANTUM_STORAGE_INSTANCE, PersistentQuantumStorageType.TYPE);
                    if (cache == null) cache = DataTypeMethods.getCustom(meta, Keys.QUANTUM_STORAGE_INSTANCE2, PersistentQuantumStorageType.TYPE);
                    if (cache == null) cache = DataTypeMethods.getCustom(meta, Keys.QUANTUM_STORAGE_INSTANCE3, PersistentQuantumStorageType.TYPE);
                    if (cache == null) {
                        p.sendMessage(Lang.getString(
                            "messages.unsupported-operation.storage_card_converter.not_quantum_storage"));
                        return false;
                    }

                    if (cache.getAmountLong() < 4096) {
                        p.sendMessage(Lang.getString(
                            "messages.unsupported-operation.storage_card_converter.too_few_items"));
                        return false;
                    }

                    if (Boolean.FALSE.equals(ReflectionUtil.invokeMethod(card, "isTargetItem", cache.getItemStack()))) {
                        p.sendMessage(Lang.getString(
                            "messages.unsupported-operation.storage_card_converter.not_target_item"));
                        return false;
                    }
                    ItemStack crd = (ItemStack) ReflectionUtil.invokeMethod(card, "getValidItem", cache.getItemStack(), cache.getAmountLong());
                    if (crd != null) {
                        menu.replaceExistingItem(CARD_SLOT, crd);
                        menu.replaceExistingItem(STORAGE_SLOT, null);
                        p.sendMessage(Lang.getString(
                            "messages.completed-operation.storage_card_converter.success"));
                    }
                    return false;
                });
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
    }
}
