package com.ytdd9527.networksexpansion.implementation.machines.assembly;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.BlockMenuUtil;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.Scheduling;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.assembly.AssemblyCardRecipes;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.drive.DriveOwnership;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import com.ytdd9527.networksexpansion.implementation.items.assembly.core.AssemblyCard;
import com.ytdd9527.networksexpansion.utils.NetworkUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AssemblyWorkshop extends SpecialSlimefunItem {

    private static final int[] RECIPE_SLOTS = new int[]{12, 13, 14, 21, 22, 23, 30, 31, 32};
    private static final int CARD_SLOT = 19;
    private static final int ENCODE_SLOT = 16;
    private static final int CLEAR_SLOT = 26;
    private static final int OUTPUT_SLOT = 34;
    private static final int[] BACKGROUND = new int[]{
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 15, 17, 18, 20, 24, 25, 27, 28, 29, 33, 35,
        36, 37, 38, 39, 40, 41, 42, 44
    };
    private static final long ENCODE_COST = 2000L;

    public AssemblyWorkshop(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
        Scheduling.runLater(this::registerRecipeCompletable, 1L);
    }

    private void registerRecipeCompletable() {
        if (Networks.getSupportedPluginManager().isJustEnoughGuide()) {
            try {
                com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry.registerRecipeCompletable(this, getIngredientSlots(), false);
            } catch (Exception e) {
                com.balugaq.netex.utils.Debug.trace(e);
            }
        }
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@NotNull BlockPlaceEvent event) {
                Location location = event.getBlock().getLocation();
                DriveOwnership.writeOwner(location, event.getPlayer().getUniqueId());
                ensureNetworkNode(location);
            }
        });

        addItemHandler(new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return false;
            }

            @Override
            public void tick(@NotNull Block b, SlimefunItem item, @NotNull SlimefunBlockData data) {
                ensureNetworkNode(b.getLocation());
            }
        });

        addItemHandler(new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(@NotNull BlockBreakEvent event, @NotNull ItemStack item, @NotNull List<ItemStack> drops) {
                Player breaker = event.getPlayer();
                Location location = event.getBlock().getLocation();
                if (!DriveOwnership.bypasses(breaker)
                        && !DriveOwnership.passesGates(breaker, location,
                            Interaction.BREAK_BLOCK, canUse(breaker, false))) {
                    event.setCancelled(true);
                    breaker.sendMessage(Lang.getString(CellnetText.DRIVE_BREAK_NOT_ALLOWED));
                    return;
                }
                NetworkStorage.removeNode(location);
                NetworkUtil.invalidate(location);
                DriveOwnership.clearOwnerCache(location);
                BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
                if (blockMenu != null) {
                    blockMenu.dropItems(location, RECIPE_SLOTS);
                    blockMenu.dropItems(location, new int[]{CARD_SLOT, OUTPUT_SLOT});
                }
            }
        });
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                setSize(45);
                drawBackground(BACKGROUND);
                addItem(ENCODE_SLOT, buildEncodeButton(), (player, i, itemStack, clickAction) -> false);
                addItem(CLEAR_SLOT, buildClearButton(), (player, i, itemStack, clickAction) -> false);
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return DriveOwnership.bypasses(player)
                        || DriveOwnership.passesGates(player, block.getLocation(),
                            Interaction.INTERACT_BLOCK, AssemblyWorkshop.this.canUse(player, false));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow == ItemTransportFlow.WITHDRAW) {
                    return new int[]{OUTPUT_SLOT};
                }
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack itemStack) {
                if (flow == ItemTransportFlow.WITHDRAW) {
                    return new int[]{OUTPUT_SLOT};
                }
                List<Integer> slots = new ArrayList<>();
                if (StackUtils.itemsMatch(itemStack, menu.getItemInSlot(CARD_SLOT))) {
                    slots.add(CARD_SLOT);
                }
                for (int slot : RECIPE_SLOTS) {
                    if (StackUtils.itemsMatch(itemStack, menu.getItemInSlot(slot))) {
                        slots.add(slot);
                    }
                }
                return slots.stream().mapToInt(Integer::intValue).toArray();
            }

            @Override
            public void newInstance(@NotNull BlockMenu menu, @NotNull Block block) {
                menu.addMenuClickHandler(ENCODE_SLOT, (player, s, itemStack, clickAction) -> {
                    int times = clickAction.isShiftClicked() ? 64 : 1;
                    for (int i = 0; i < times; i++) {
                        if (!tryEncode(player, menu)) {
                            break;
                        }
                    }
                    return false;
                });
                menu.addMenuClickHandler(CLEAR_SLOT, (player, s, itemStack, clickAction) -> {
                    tryClear(player, menu);
                    return false;
                });
            }
        };
    }

    @NotNull
    private static ItemStack buildEncodeButton() {
        return Icon.ASSEMBLY_WORKSHOP_ENCODE;
    }

    @NotNull
    private static ItemStack buildClearButton() {
        return Icon.ASSEMBLY_WORKSHOP_CLEAR;
    }

    private boolean tryEncode(@NotNull Player player, @NotNull BlockMenu menu) {
        Location location = menu.getLocation();
        NetworkRoot root = NetworkUtil.findRoot(location);
        if (root == null) {
            player.sendMessage(Lang.getString(CellnetText.WORKSHOP_NO_NETWORK));
            return false;
        }
        if (root.getRootPower() < ENCODE_COST) {
            player.sendMessage(Lang.getString(CellnetText.WORKSHOP_NO_POWER));
            return false;
        }

        ItemStack cardStack = menu.getItemInSlot(CARD_SLOT);
        if (cardStack == null || cardStack.getType().isAir() || AssemblyCard.readCard(cardStack) != null) {
            ItemStack pulled = root.getItemStack(location,
                new ItemRequest(ExpansionItems.ASSEMBLY_CARD.getItem(), 64));
            if (pulled == null || !AssemblyCard.isCard(pulled) || AssemblyCard.readCard(pulled) != null) {
                if (pulled != null) {
                    root.addItemStack(location, pulled);
                }
                player.sendMessage(Lang.getString(CellnetText.WORKSHOP_NO_CARD));
                return false;
            }
            menu.replaceExistingItem(CARD_SLOT, pulled);
            cardStack = pulled;
        }

        ItemStack[] grid = new ItemStack[RECIPE_SLOTS.length];
        boolean any = false;
        for (int i = 0; i < RECIPE_SLOTS.length; i++) {
            ItemStack slotItem = menu.getItemInSlot(RECIPE_SLOTS[i]);
            if (slotItem != null && !slotItem.getType().isAir()) {
                ItemStack clean = ItemStackUtil.getCleanItem(slotItem.clone());
                clean.setAmount(1);
                grid[i] = clean;
                any = true;
            }
        }
        if (!any) {
            player.sendMessage(Lang.getString(CellnetText.WORKSHOP_INVALID_RECIPE));
            return false;
        }

        AssemblyCardRecipes.MatchedRecipe matched = AssemblyCardRecipes.resolveRecipe(player, grid);
        if (matched == null) {
            player.sendMessage(Lang.getString(CellnetText.WORKSHOP_INVALID_RECIPE));
            return false;
        }
        List<ItemStack> ingredients = new ArrayList<>();
        for (ItemStack input : matched.canonicalInputs()) {
            if (input != null && !input.getType().isAir()) {
                ingredients.add(input);
            }
        }
        List<ItemStack> merged = AssemblyCard.mergeInputs(ingredients);
        if (!AssemblyCardRecipes.passesSaveLimits(player, merged, matched.output())) {
            return false;
        }

        for (int i = 0; i < RECIPE_SLOTS.length; i++) {
            ItemStack required = matched.canonicalInputs()[i];
            if (required == null || required.getType().isAir()) {
                continue;
            }
            ItemStack slotItem = menu.getItemInSlot(RECIPE_SLOTS[i]);
            if (slotItem == null || slotItem.getAmount() < required.getAmount()) {
                player.sendMessage(Lang.getString(CellnetText.WORKSHOP_MATERIAL_SHORT));
                return false;
            }
        }

        ItemStack encodedCard = cardStack.asQuantity(1);
        if (!AssemblyCard.writeCard(encodedCard, merged, matched.output().clone(),
            AssemblyCardRecipes.padToSlots(matched.canonicalInputs()))) {
            player.sendMessage(Lang.getString(CellnetText.WORKSHOP_INVALID_RECIPE));
            return false;
        }
        if (!BlockMenuUtil.fits(menu, encodedCard, OUTPUT_SLOT)) {
            player.sendMessage(Lang.getString(CellnetText.WORKSHOP_OUTPUT_FULL));
            return false;
        }

        menu.replaceExistingItem(CARD_SLOT,
            cardStack.getAmount() > 1 ? cardStack.asQuantity(cardStack.getAmount() - 1) : null);
        for (int i = 0; i < RECIPE_SLOTS.length; i++) {
            ItemStack required = matched.canonicalInputs()[i];
            if (required == null || required.getType().isAir()) {
                continue;
            }
            ItemStack slotItem = menu.getItemInSlot(RECIPE_SLOTS[i]);
            int left = slotItem.getAmount() - required.getAmount();
            menu.replaceExistingItem(RECIPE_SLOTS[i], left > 0 ? slotItem.asQuantity(left) : null);
        }
        BlockMenuUtil.pushItem(menu, encodedCard, OUTPUT_SLOT);
        root.removeRootPower(ENCODE_COST);
        return true;
    }

    private void tryClear(@NotNull Player player, @NotNull BlockMenu menu) {
        ItemStack cardStack = menu.getItemInSlot(CARD_SLOT);
        if (cardStack == null || !AssemblyCard.isCard(cardStack)) {
            player.sendMessage(Lang.getString(CellnetText.WORKSHOP_CLEAR_NO_RECIPE));
            return;
        }
        AssemblyCard.RecipeEntry entry = AssemblyCard.readCard(cardStack);
        if (entry == null) {
            player.sendMessage(Lang.getString(CellnetText.WORKSHOP_CLEAR_NO_RECIPE));
            return;
        }
        int copies = Math.max(1, cardStack.getAmount());
        for (ItemStack ingredient : entry.ingredients()) {
            ItemStack refund = ingredient.clone();
            refund.setAmount((int) Math.min((long) refund.getAmount() * copies, Integer.MAX_VALUE));
            ItemStack leftover = BlockMenuUtil.pushItem(menu, refund, RECIPE_SLOTS);
            if (leftover != null) {
                ItemStackUtil.giveOrDropItem(player, leftover);
            }
        }

        cardStack.editMeta(meta -> {
            PersistentDataAPI.remove(meta, Keys.ASSEMBLY_CARD_OUTPUT);
            PersistentDataAPI.remove(meta, Keys.ASSEMBLY_CARD_INGREDIENTS);
            PersistentDataAPI.remove(meta, AssemblyCard.ASSEMBLY_CARD_GRID);
            PersistentDataAPI.remove(meta, Keys.ASSEMBLY_CARD_HASH);
            List<String> blankLore = ExpansionItems.ASSEMBLY_CARD.getItem().getItemMeta().getLore();
            if (blankLore != null) {
                meta.setLore(blankLore);
            }
        });
        player.sendMessage(Lang.getString(CellnetText.WORKSHOP_CLEAR_OK));
    }

    private static void ensureNetworkNode(@NotNull Location location) {
        if (!NetworkStorage.containsKey(location)) {
            NetworkStorage.registerNode(location, new NodeDefinition(NodeType.BRIDGE));
        }
    }

    public int[] getIngredientSlots() {
        return RECIPE_SLOTS;
    }
}
