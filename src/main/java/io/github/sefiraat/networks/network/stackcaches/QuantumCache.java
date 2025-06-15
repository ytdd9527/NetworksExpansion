package io.github.sefiraat.networks.network.stackcaches;

import com.balugaq.netex.utils.Lang;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class QuantumCache extends ItemStackCache {

    @Nullable private final ItemMeta storedItemMeta;

    private final boolean supportsCustomMaxAmount;

    @Setter
    @Getter
    private int limit;

    @Getter
    private long amount;

    @Setter
    @Getter
    private boolean voidExcess;

    public QuantumCache(
            @Nullable ItemStack storedItem,
            long amount,
            int limit,
            boolean voidExcess,
            boolean supportsCustomMaxAmount) {
        super(storedItem);
        this.storedItemMeta = storedItem == null ? null : storedItem.getItemMeta();
        this.amount = amount;
        this.limit = limit;
        this.voidExcess = voidExcess;
        this.supportsCustomMaxAmount = supportsCustomMaxAmount;
    }

    @Nullable public ItemMeta getStoredItemMeta() {
        return this.storedItemMeta;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean supportsCustomMaxAmount() {
        return this.supportsCustomMaxAmount;
    }

    public int increaseAmount(int amount) {
        long total = this.amount + (long) amount;
        if (total > this.limit) {
            this.amount = this.limit;
            if (!this.voidExcess) {
                return (int) (total - this.limit);
            }
        } else {
            this.amount = this.amount + amount;
        }
        return 0;
    }

    public void reduceAmount(int amount) {
        this.amount = this.amount - amount;
    }

    @Nullable public ItemStack withdrawItem(int amount) {
        if (this.getItemStack() == null) {
            return null;
        }
        final ItemStack clone = this.getItemStack().clone();
        clone.setAmount((int) Math.min(this.amount, amount));
        reduceAmount(clone.getAmount());
        return clone;
    }

    @Nullable public ItemStack withdrawItem() {
        if (this.getItemStack() == null) {
            return null;
        }
        return withdrawItem(this.getItemStack().getMaxStackSize());
    }

    public void addMetaLore(@NotNull ItemMeta itemMeta) {
        List<String> old = itemMeta.getLore();
        final List<String> lore = old != null ? new ArrayList<>(old) : new ArrayList<>();
        String itemName = Lang.getString("messages.normal-operation.quantum_cache.empty");
        if (getItemStack() != null) {
            itemName = ItemStackHelper.getDisplayName(this.getItemStack());
        }
        lore.add("");
        lore.add(String.format(Lang.getString("messages.normal-operation.quantum_cache.stored_item"), itemName));
        lore.add(String.format(
                Lang.getString("messages.normal-operation.quantum_cache.stored_amount"), this.getAmount()));
        if (this.supportsCustomMaxAmount) {
            lore.add(String.format(
                    Lang.getString("messages.normal-operation.quantum_cache.custom_max_limit"), this.getLimit()));
        }

        itemMeta.setLore(lore);
    }

    public void updateMetaLore(@NotNull ItemMeta itemMeta) {
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        String itemName = Lang.getString("messages.normal-operation.quantum_cache.empty");
        if (getItemStack() != null) {
            itemName = ItemStackHelper.getDisplayName(this.getItemStack());
        }
        final int loreIndexModifier = this.supportsCustomMaxAmount ? 1 : 0;
        lore.set(
                lore.size() - 2 - loreIndexModifier,
                String.format(Lang.getString("messages.normal-operation.quantum_cache.stored_item"), itemName));
        lore.set(
                lore.size() - 1 - loreIndexModifier,
                String.format(
                        Lang.getString("messages.normal-operation.quantum_cache.stored_amount"), this.getAmount()));
        if (this.supportsCustomMaxAmount) {
            lore.set(
                    lore.size() - loreIndexModifier,
                    String.format(
                            Lang.getString("messages.normal-operation.quantum_cache.custom_max_limit"),
                            this.getLimit()));
        }

        itemMeta.setLore(lore);
    }
}
