package io.github.sefiraat.networks.network.barrel;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.mooy1.infinityexpansion.items.storage.StorageCache;
import io.github.sefiraat.networks.network.stackcaches.BarrelIdentity;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InfinityBarrel extends BarrelIdentity {

    @NotNull private final StorageCache cache;

    @ParametersAreNonnullByDefault
    public InfinityBarrel(Location location, @Nullable ItemStack itemStack, long amount, StorageCache cache) {
        super(location, itemStack, amount, BarrelType.INFINITY);
        this.cache = cache;
    }

    @Nullable @Override
    public ItemStack requestItem(@NotNull ItemRequest itemRequest) {
        BlockMenu blockMenu = StorageCacheUtils.getMenu(this.getLocation());
        return blockMenu == null ? null : blockMenu.getItemInSlot(this.getOutputSlot()[0]);
    }

    @Override
    public void depositItemStack(ItemStack[] itemsToDeposit) {
        cache.depositAll(itemsToDeposit, true);
    }

    @Override
    public int[] getInputSlot() {
        return new int[] {10};
    }

    @Override
    public int[] getOutputSlot() {
        return new int[] {16};
    }
}
