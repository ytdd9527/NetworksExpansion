package io.github.sefiraat.networks.network.barrel;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.network.stackcaches.BarrelIdentity;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.network.stackcaches.QuantumCache;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetworkStorage extends BarrelIdentity {

    public NetworkStorage(@NotNull Location location, ItemStack itemStack, long amount) {
        super(location, itemStack, amount, BarrelType.NETWORKS);
    }

    @Override
    @Nullable public ItemStack requestItem(@NotNull ItemRequest itemRequest) {
        final BlockMenu blockMenu = StorageCacheUtils.getMenu(this.getLocation());

        if (blockMenu == null) {
            return null;
        }

        final QuantumCache cache = NetworkQuantumStorage.getCaches().get(blockMenu.getLocation());

        if (cache == null) {
            return null;
        }

        return NetworkQuantumStorage.getItemStack(cache, blockMenu, itemRequest.getAmount());
    }

    @Override
    public void depositItemStack(ItemStack @NotNull [] itemsToDeposit) {
        if (StorageCacheUtils.getSfItem(this.getLocation()) instanceof NetworkQuantumStorage) {
            final BlockMenu blockMenu = StorageCacheUtils.getMenu(this.getLocation());
            if (blockMenu == null) {
                return;
            }
            final QuantumCache cache = NetworkQuantumStorage.getCaches().get(this.getLocation());
            if (cache != null) {
                NetworkQuantumStorage.tryInputItem(blockMenu.getLocation(), itemsToDeposit, cache);
            }
        }
    }

    @Override
    public int[] getInputSlot() {
        return new int[] {NetworkQuantumStorage.INPUT_SLOT};
    }

    @Override
    public int[] getOutputSlot() {
        return new int[] {NetworkQuantumStorage.OUTPUT_SLOT};
    }
}
