package com.ytdd9527.networksexpansion.implementation.tools;

import com.balugaq.netex.utils.Lang;
import com.jeff_media.morepersistentdatatypes.DataType;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.implementation.ExpansionItemStacks;
import com.ytdd9527.networksexpansion.implementation.machines.unit.NetworksDrawer;
import io.github.mooy1.infinityexpansion.items.storage.StorageCache;
import io.github.mooy1.infinityexpansion.items.storage.StorageUnit;
import io.github.sefiraat.networks.managers.SupportedPluginManager;
import io.github.sefiraat.networks.network.barrel.FluffyBarrel;
import io.github.sefiraat.networks.network.barrel.InfinityBarrel;
import io.github.sefiraat.networks.network.barrel.NetworkStorage;
import io.github.sefiraat.networks.network.stackcaches.BarrelIdentity;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.network.stackcaches.QuantumCache;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DistinctiveItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemMover extends SpecialSlimefunItem implements DistinctiveItem {
    @NotNull public static final Interaction[] CHECK_INTERACTIONS =
            new Interaction[] {Interaction.PLACE_BLOCK, Interaction.BREAK_BLOCK, Interaction.INTERACT_BLOCK};

    @SuppressWarnings("deprecation")
    @NotNull public static final List<String> DEFAULT_LORE = ExpansionItemStacks.ITEM_MOVER.getItemMeta() == null
            ? new ArrayList<>()
            : (ExpansionItemStacks.ITEM_MOVER.getItemMeta().hasLore()
                            && ExpansionItemStacks.ITEM_MOVER.getItemMeta().getLore() != null
                    ? ExpansionItemStacks.ITEM_MOVER.getItemMeta().getLore()
                    : new ArrayList<>());

    public ItemMover(
            @NotNull ItemGroup itemGroup,
            @NotNull SlimefunItemStack item,
            @NotNull RecipeType recipeType,
            @NotNull ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemHandler((ItemUseHandler) e -> {
            final Player player = e.getPlayer();
            final Optional<Block> optional = e.getClickedBlock();
            if (optional.isPresent()) {
                final ItemStack itemStack = e.getItem();
                if (itemStack.getType() != Material.DEBUG_STICK) {
                    player.sendMessage(Lang.getString("messages.unsupported-operation.item_mover.invalid_item_mover"));
                    return;
                }
                if (itemStack.getAmount() != 1) {
                    player.sendMessage(
                            Lang.getString("messages.unsupported-operation.item_mover.invalid_item_mover_amount"));
                    return;
                }
                if (!itemStack.hasItemMeta() || itemStack.getItemMeta() == null) {
                    player.sendMessage(
                            Lang.getString("messages.unsupported-operation.item_mover.invalid_item_mover_meta"));
                    return;
                }
                final Location location = optional.get().getLocation();
                if (hasPermission(player, location)) {
                    if (!player.isSneaking()) {
                        // Right-click
                        tryDepositIntoMover(player, itemStack, location);
                    } else {
                        // Shift+Right-click
                        tryWithdrawFromMover(player, itemStack, location);
                    }
                }
            }
            e.cancel();
        });
    }

    @Nullable public static ItemStack getStoredItemStack(@Nullable PersistentDataHolder holder) {
        if (holder == null) {
            return null;
        }

        return DataTypeMethods.getCustom(holder, Keys.ITEM_MOVER_ITEM, DataType.ITEM_STACK);
    }

    @Nullable public static ItemStack getStoredItemStack(@Nullable ItemStack mover) {
        if (mover == null || mover.getType() == Material.AIR) {
            return null;
        }

        final ItemMeta itemMeta = mover.getItemMeta();
        if (!mover.hasItemMeta() || itemMeta == null) {
            return null;
        }

        return getStoredItemStack(itemMeta);
    }

    public static int getStoredAmount(@Nullable PersistentDataHolder holder) {
        if (holder == null) {
            return 0;
        }

        final Integer amount = DataTypeMethods.getCustom(holder, Keys.ITEM_MOVER_AMOUNT, PersistentDataType.INTEGER);
        if (amount == null) {
            return 0;
        }

        return amount;
    }

    public static int getStoredAmount(@Nullable ItemStack mover) {
        if (mover == null || mover.getType() == Material.AIR) {
            return 0;
        }

        final ItemMeta itemMeta = mover.getItemMeta();
        if (!mover.hasItemMeta() || itemMeta == null) {
            return 0;
        }

        return getStoredAmount(itemMeta);
    }

    public static void setStoredItemStack(@Nullable ItemStack mover, @Nullable ItemStack itemStack) {
        if (mover == null || mover.getType() == Material.AIR) {
            return;
        }

        final ItemMeta itemMeta = mover.getItemMeta();
        if (!mover.hasItemMeta() || itemMeta == null) {
            return;
        }

        if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getAmount() != 1) {
            clearPDC(mover);
            return;
        }

        DataTypeMethods.setCustom(itemMeta, Keys.ITEM_MOVER_ITEM, DataType.ITEM_STACK, itemStack);
        mover.setItemMeta(itemMeta);
    }

    public static void setStoredAmount(@Nullable ItemStack mover, int amount) {
        if (mover == null || mover.getType() == Material.AIR) {
            return;
        }

        final ItemMeta itemMeta = mover.getItemMeta();
        if (!mover.hasItemMeta() || itemMeta == null) {
            return;
        }

        if (amount <= 0) {
            clearPDC(mover);
            return;
        }

        DataTypeMethods.setCustom(itemMeta, Keys.ITEM_MOVER_AMOUNT, PersistentDataType.INTEGER, amount);
        mover.setItemMeta(itemMeta);
    }

    public static void depositItem(ItemStack mover, @Nullable ItemStack incoming) {
        if (incoming == null || incoming.getType() == Material.AIR) {
            return;
        }

        if (incoming.getAmount() > 0) {
            ItemStack stored = getStoredItemStack(mover);
            int storedAmount = getStoredAmount(mover);
            if (stored == null || stored.getType() == Material.AIR) {
                setStoredItemStack(mover, StackUtils.getAsQuantity(incoming, 1));
                setStoredAmount(mover, incoming.getAmount());
                incoming.setAmount(0);
            } else if (StackUtils.itemsMatch(stored, incoming)) {
                int maxCanReceive = Integer.MAX_VALUE - storedAmount;
                int incomingAmount = incoming.getAmount();
                int canReceive = Math.min(maxCanReceive, incomingAmount);
                setStoredAmount(mover, getStoredAmount(mover) + canReceive);
                incoming.setAmount(incomingAmount - canReceive);
            }
        }
    }

    private static void clearPDC(@Nullable ItemStack mover) {
        if (mover == null || mover.getType() == Material.AIR) {
            return;
        }

        final ItemMeta itemMeta = mover.getItemMeta();
        if (!mover.hasItemMeta() || itemMeta == null) {
            return;
        }

        DataTypeMethods.removeCustom(itemMeta, Keys.ITEM_MOVER_ITEM);
        DataTypeMethods.removeCustom(itemMeta, Keys.ITEM_MOVER_AMOUNT);

        mover.setItemMeta(itemMeta);
    }

    @SuppressWarnings("deprecation")
    public static void updateLore(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        if (!itemStack.hasItemMeta() || itemStack.getItemMeta() == null) {
            return;
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();
        final ItemStack storedItemStack = getStoredItemStack(itemMeta);
        final int amount = getStoredAmount(itemMeta);

        List<String> lore = cloneDefaultLore();
        if (storedItemStack != null && amount > 0) {
            lore.add(String.format(
                    Lang.getString("messages.normal-operation.item_mover.stored_item"),
                    ItemStackHelper.getDisplayName(storedItemStack)));
            lore.add(String.format(Lang.getString("messages.normal-operation.item_mover.stored_amount"), amount));
        } else {
            clearPDC(itemStack);
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    @Nullable public static BarrelIdentity getBarrel(@NotNull Player player, @NotNull Location location) {
        final SlimefunItem sfitem = StorageCacheUtils.getSfItem(location);

        if (sfitem == null) {
            return null;
        }

        final boolean infinityEnabled = SupportedPluginManager.getInstance().isInfinityExpansion();
        final boolean fluffyEnabled = SupportedPluginManager.getInstance().isFluffyMachines();

        /*if (infinityEnabled && sfitem instanceof StorageUnit unit) {
            return getInfinityBarrel(location, unit);
        } else */
        if (fluffyEnabled && sfitem instanceof Barrel barrel) {
            return getFluffyBarrel(location, barrel);
        } else if (sfitem instanceof NetworkQuantumStorage) {
            return getNetworkStorage(location);
        } else if (sfitem instanceof NetworksDrawer) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.item_mover.suggest_use_drawers"));
            return null;
        }

        return null;
    }

    @Nullable public static InfinityBarrel getInfinityBarrel(@NotNull Location location, @NotNull StorageUnit unit) {
        StorageCache cache = unit.getCache(location);
        if (cache == null) {
            return null;
        }

        BlockMenu blockMenu = StorageCacheUtils.getMenu(location);
        if (blockMenu == null) {
            return null;
        }

        final SlimefunBlockData data = StorageCacheUtils.getBlock(blockMenu.getLocation());
        if (data == null) {
            return null;
        }

        final String storedString = data.getData("stored");
        if (storedString == null || storedString.isEmpty()) {
            return null;
        }

        final int stored;
        try {
            stored = Integer.parseInt(storedString);
        } catch (NumberFormatException e) {
            return null;
        }

        if (stored <= 0) {
            return null;
        }

        final ItemStack rawDisplayItem = blockMenu.getItemInSlot(13);
        final ItemStack displayItem = rawDisplayItem.clone();
        if (!displayItem.hasItemMeta()) {
            return null;
        }
        final ItemMeta displayItemMeta = displayItem.getItemMeta();
        if (displayItemMeta == null) {
            return null;
        }

        Byte correct = DataTypeMethods.getCustom(displayItemMeta, Keys.INFINITY_DISPLAY, PersistentDataType.BYTE);
        if (correct == null || correct != 1) {
            return null;
        }

        displayItemMeta.getPersistentDataContainer().remove(Keys.INFINITY_DISPLAY);
        displayItem.setItemMeta(displayItemMeta);

        return new InfinityBarrel(location, displayItem, stored, cache);
    }

    @Nullable private static FluffyBarrel getFluffyBarrel(@NotNull Location location, @NotNull Barrel barrel) {
        Block block = location.getBlock();
        ItemStack itemStack;
        try {
            itemStack = barrel.getStoredItem(block);
        } catch (NullPointerException ignored) {
            return null;
        }

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }

        final ItemStack clone = itemStack.clone();

        int stored = barrel.getStored(block);

        if (stored <= 0) {
            return null;
        }
        int limit = barrel.getCapacity(block);
        boolean voidExcess = Boolean.parseBoolean(StorageCacheUtils.getData(location, "trash"));

        return new FluffyBarrel(location, clone, stored, limit, voidExcess);
    }

    @Nullable public static NetworkStorage getNetworkStorage(@NotNull Location location) {
        QuantumCache cache = NetworkQuantumStorage.getCaches().get(location);
        if (cache == null) {
            return null;
        }

        final ItemStack stored = cache.getItemStack();
        if (stored == null || stored.getType() == Material.AIR) {
            return null;
        }

        final long storedAmount = cache.getAmount();
        if (storedAmount < 0) {
            return null;
        }

        return new NetworkStorage(location, stored, storedAmount);
    }

    private static void tryDepositIntoMover(
            @NotNull Player player, ItemStack mover, @NotNull Location clickedLocation) {
        if (!hasPermission(player, clickedLocation)) {
            return;
        }
        int storedAmount = getStoredAmount(mover);
        ItemStack storedItemStack = getStoredItemStack(mover);
        BarrelIdentity barrel = getBarrel(player, clickedLocation);
        if (barrel == null || barrel.getItemStack() == null || barrel.getAmount() <= 0) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.item_mover.invalid_storage"));
            return;
        }

        int have = barrel.getAmount() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) barrel.getAmount();
        if (have <= 0) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.item_mover.empty_storage"));
            return;
        }

        ItemRequest itemRequest = new ItemRequest(barrel.getItemStack(), 0);
        if (storedItemStack == null && storedAmount <= 0) {
            itemRequest.setAmount(have);
        } else if (StackUtils.itemsMatch(storedItemStack, barrel.getItemStack())) {
            int maxCanReceive = Integer.MAX_VALUE - storedAmount;
            itemRequest.setAmount(Math.min(maxCanReceive, have));
        }

        ItemStack fetched = barrel.requestItem(itemRequest);

        if (fetched == null || fetched.getType() == Material.AIR) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.item_mover.invalid_item"));
            return;
        }

        int before = fetched.getAmount();
        String name = ItemStackHelper.getDisplayName(fetched);
        depositItem(mover, fetched);
        int after = fetched.getAmount();
        player.sendMessage(String.format(
                Lang.getString("messages.completed-operation.item_mover.deposit_success"), name, before - after));
        updateLore(mover);
    }

    private static void tryWithdrawFromMover(
            @NotNull Player player, ItemStack mover, @NotNull Location clickedLocation) {
        if (!hasPermission(player, clickedLocation)) {
            return;
        }
        int storedAmount = getStoredAmount(mover);
        ItemStack storedItemStack = getStoredItemStack(mover);
        BarrelIdentity barrel = getBarrel(player, clickedLocation);
        if (barrel == null) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.item_mover.invalid_storage"));
            return;
        }

        int have = barrel.getAmount() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) barrel.getAmount();

        if (storedItemStack == null || storedAmount <= 0) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.item_mover.empty_mover"));
            return;
        }

        if (have == Integer.MAX_VALUE) {
            player.sendMessage(Lang.getString("messages.unsupported-operation.item_mover.full_storage"));
            return;
        }

        ItemStack clone = StackUtils.getAsQuantity(storedItemStack, storedAmount);
        String name = ItemStackHelper.getDisplayName(clone);
        int before = clone.getAmount();
        barrel.depositItemStack(clone);
        int after = clone.getAmount();
        setStoredAmount(mover, clone.getAmount());
        player.sendMessage(String.format(
                Lang.getString("messages.completed-operation.item_mover.withdraw_success"), name, before - after));
        updateLore(mover);
    }

    private static @NotNull List<String> cloneDefaultLore() {
        return new ArrayList<>(DEFAULT_LORE);
    }

    public static boolean hasPermission(@NotNull Player player, @NotNull Location location) {
        for (Interaction interaction : CHECK_INTERACTIONS) {
            if (!Slimefun.getProtectionManager().hasPermission(player, location, interaction)) {
                return false;
            }
        }

        SlimefunItem sfitem = StorageCacheUtils.getSfItem(location);
        return Slimefun.getPermissionsService().hasPermission(player, sfitem);
    }

    @Override
    public boolean canStack(@NotNull ItemMeta itemMeta, @NotNull ItemMeta itemMeta1) {
        return itemMeta.getPersistentDataContainer().equals(itemMeta1.getPersistentDataContainer());
    }
}
