package io.github.sefiraat.networks.network.barrel;

import io.github.mooy1.infinityexpansion.items.storage.StorageCache;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class InfinityBarrel extends BarrelIdentity {

    private final StorageCache cache;

    public InfinityBarrel(Location location, ItemStack itemStack, int amount, StorageCache cache) {
        super(location, itemStack, amount, BarrelType.INFINITY);
        this.cache = cache;
    }

    @Nullable
    @Override
    public ItemStack requestItem(ItemStack similarStack) {
        BlockMenu blockMenu = BlockStorage.getInventory(this.getLocation());
        return blockMenu == null ? null : blockMenu.getItemInSlot(this.getOutputSlot());
    }

    @Override
    public void depositItemStack(ItemStack[] itemsToDeposit) {
        cache.depositAll(itemsToDeposit, true);
    }

    @Override
    public int getInputSlot() {
        return 10;
    }

    @Override
    public int getOutputSlot() {
        return 16;
    }
}