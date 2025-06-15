package io.github.sefiraat.networks.utils.datatypes;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.sefiraat.networks.network.stackcaches.CardInstance;
import io.github.sefiraat.networks.utils.Keys;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link PersistentDataType} for {@link CardInstance}
 * Creatively thieved from {@see <a href="https://github.com/baked-libs/dough/blob/main/dough-data/src/main/java/io/github/bakedlibs/dough/data/persistent/PersistentUUIDDataType.java">PersistentUUIDDataType}
 *
 * @author Sfiguz7
 * @author Walshy
 */
@Deprecated
public class PersistentCardInstanceType implements PersistentDataType<PersistentDataContainer, CardInstance> {

    public static final PersistentDataType<PersistentDataContainer, CardInstance> TYPE =
            new PersistentCardInstanceType();

    public static final NamespacedKey ITEM = Keys.newKey("item");
    public static final NamespacedKey AMOUNT = Keys.newKey("amount");
    public static final NamespacedKey LIMIT = Keys.newKey("limit");
    public static final NamespacedKey UNSTACK = Keys.newKey("time");

    @Override
    @NotNull public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    @NotNull public Class<CardInstance> getComplexType() {
        return CardInstance.class;
    }

    @Override
    @NotNull public PersistentDataContainer toPrimitive(
            @NotNull CardInstance complex, @NotNull PersistentDataAdapterContext context) {
        final PersistentDataContainer container = context.newPersistentDataContainer();

        if (complex.getItemStack() != null) {
            container.set(ITEM, DataType.ITEM_STACK, complex.getItemStack());
        }
        container.set(AMOUNT, DataType.INTEGER, complex.getAmount());
        container.set(LIMIT, DataType.INTEGER, complex.getLimit());
        container.set(UNSTACK, DataType.LONG, System.currentTimeMillis());
        return container;
    }

    @Override
    @NotNull public CardInstance fromPrimitive(
            @NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        final ItemStack item = primitive.get(ITEM, DataType.ITEM_STACK);
        final Integer amount = primitive.get(AMOUNT, DataType.INTEGER);
        final Integer limit = primitive.get(LIMIT, DataType.INTEGER);

        if (amount == null || limit == null) {
            return new CardInstance(null, 0, 0);
        }
        return new CardInstance(item, amount, limit);
    }
}
