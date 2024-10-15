package io.github.sefiraat.networks.slimefun.tools;

import io.github.sefiraat.networks.network.stackcaches.CardInstance;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.Theme;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.sefiraat.networks.utils.datatypes.PersistentCardInstanceType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DistinctiveItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;

@Deprecated
public class NetworkCard extends SlimefunItem implements DistinctiveItem {

    private static final int[] SIZES = new int[]{
            4096,
            32768,
            262144,
            2097152,
            16777216,
            134217728,
            1073741824,
            Integer.MAX_VALUE
    };

    private static final String WIKI_PAGE = "Network-Memory-Card";

    private final int size;

    public NetworkCard(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int size) {
        super(itemGroup, item, recipeType, recipe);
        this.size = size;
        addItemHandler((ItemUseHandler) e -> {
            final Player player = e.getPlayer();
            final ItemStack card = player.getInventory().getItemInMainHand();
            final ItemStack stackToSet = player.getInventory().getItemInOffHand().clone();

            e.cancel();
            if (card.getAmount() > 1) {
                player.sendMessage(Theme.WARNING + "请单独拿出一张内存卡，不要堆叠");
                return;
            }

            if (isBlacklisted(stackToSet)) {
                player.sendMessage(Theme.WARNING + "该物品无法存储至内存卡中");
                return;
            }

            final SlimefunItem cardItem = SlimefunItem.getByItem(card);
            if (cardItem instanceof NetworkCard networkCard) {
                final ItemMeta cardMeta = card.getItemMeta();
                final CardInstance cardInstance = DataTypeMethods.getCustom(
                        cardMeta,
                        Keys.CARD_INSTANCE,
                        PersistentCardInstanceType.TYPE,
                        new CardInstance(null, 0, networkCard.getSize())
                );

                if (cardInstance.getAmount() > 0) {
                    e.getPlayer().sendMessage(Theme.WARNING + "只有空内存卡才能分配物品");
                    return;
                }

                cardInstance.setItemStack(stackToSet);
                DataTypeMethods.setCustom(cardMeta, Keys.CARD_INSTANCE, PersistentCardInstanceType.TYPE, cardInstance);
                cardInstance.updateLore(cardMeta);
                card.setItemMeta(cardMeta);
            }
        });
    }

    public static int[] getSizes() {
        return SIZES;
    }

    private boolean isBlacklisted(@Nonnull ItemStack itemStack) {
        return itemStack.getType() == Material.AIR
                || itemStack.getType().getMaxDurability() < 0
                || Tag.SHULKER_BOXES.isTagged(itemStack.getType());
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public void postRegister() {
        addWikiPage(WIKI_PAGE);
    }

    @Override
    public boolean canStack(@Nonnull ItemMeta sfItemMeta, @Nonnull ItemMeta itemMeta) {
        return sfItemMeta.getPersistentDataContainer().equals(itemMeta.getPersistentDataContainer());
    }
}
