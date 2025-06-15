package io.github.sefiraat.networks.network.stackcaches;

import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.utils.Theme;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class CardInstance extends ItemStackCache {

    private final int limit;

    @Setter
    private int amount;

    public CardInstance(@Nullable ItemStack itemStack, int amount, int limit) {
        super(itemStack);
        this.amount = amount;
        this.limit = limit;
    }

    @Nullable public ItemStack withdrawItem(int amount) {
        if (this.getItemStack() == null) {
            return null;
        }
        final ItemStack clone = this.getItemStack().clone();
        clone.setAmount(Math.min(this.amount, amount));
        reduceAmount(clone.getAmount());
        return clone;
    }

    @SuppressWarnings("unused")
    @Nullable public ItemStack withdrawItem() {
        if (this.getItemStack() == null) {
            return null;
        }
        return withdrawItem(this.getItemStack().getMaxStackSize());
    }

    @SuppressWarnings("unused")
    public void increaseAmount(int amount) {
        long total = (long) this.amount + (long) amount;
        if (total > this.limit) {
            this.amount = this.limit;
        } else {
            this.amount = this.amount + amount;
        }
    }

    public void reduceAmount(int amount) {
        this.amount = this.amount - amount;
    }

    @SuppressWarnings("deprecation")
    public void updateLore(@NotNull ItemMeta itemMeta) {
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            return;
        }
        lore.set(10, getLoreLine());
        itemMeta.setLore(lore);
    }

    public @NotNull String getLoreLine() {
        if (this.getItemStack() == null) {
            return Lang.getString("messages.normal-operation.memory_card.empty");
        }
        String name = TextUtil.stripColor(ItemStackHelper.getDisplayName(getItemStack()));
        return Theme.CLICK_INFO + name + ": " + Theme.PASSIVE + this.amount;
    }
}
