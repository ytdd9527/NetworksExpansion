package io.github.sefiraat.networks.utils.datatypes;

import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class DataTypeMethods {

    /**
     * Get an object based on the provided {@link PersistentDataType} in a {@link PersistentDataContainer}, if the key doesn't exist it returns null.
     *
     * @param holder The {@link PersistentDataHolder} to retrieve the data from
     * @param key    The key of the data to retrieve
     * @return An object associated with this key or null if it doesn't exist
     */
    @Nullable public static <T, Z> Z getCustom(
            @NotNull PersistentDataHolder holder, @NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
        return holder.getPersistentDataContainer().get(key, type);
    }

    /**
     * This method returns an {@link Optional} describing the object defined by the {@link PersistentDataType}
     * found under the given key. An empty {@link Optional} will be returned if no value has been found.
     *
     * @param holder The {@link PersistentDataHolder} to retrieve the data from
     * @param key    The key of the data to retrieve
     * @return An {@link Optional} describing the result
     * @see PersistentDataAPI#get(PersistentDataHolder, NamespacedKey, PersistentDataType)
     */
    @NotNull public static <T, Z> Optional<Z> getOptionalCustom(
            @NotNull PersistentDataHolder holder, @NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
        return Optional.ofNullable(getCustom(holder, key, type));
    }

    /**
     * Get an object based on the provided {@link PersistentDataType} in a {@link PersistentDataContainer} or the default value if the key doesn't exist.
     *
     * @param holder     The {@link PersistentDataHolder} to retrieve the data from
     * @param key        The key of the data to retrieve
     * @param defaultVal The default value to use if no key is found
     * @return The object associated with this key or the default value if it doesn't exist
     */
    public static <T, Z> @NotNull Z getCustom(
            @NotNull PersistentDataHolder holder,
            @NotNull NamespacedKey key,
            @NotNull PersistentDataType<T, Z> type,
            @NotNull Z defaultVal) {
        return holder.getPersistentDataContainer().getOrDefault(key, type, defaultVal);
    }

    /**
     * Checks if the specified {@link PersistentDataHolder} has a {@link PersistentDataContainer} with the specified
     * key.
     *
     * @param holder The {@link PersistentDataHolder} to check
     * @param key    The key to check for
     * @return {@code true} if the holder has a {@link PersistentDataContainer} with the specified key.
     */
    public static <T, Z> boolean hasCustom(
            @NotNull PersistentDataHolder holder, @NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
        return holder.getPersistentDataContainer().has(key, type);
    }

    /**
     * Set a custom {@link PersistentDataType} in a {@link PersistentDataContainer}
     *
     * @param holder The {@link PersistentDataHolder} to add the data to
     * @param key    The key of the data to set
     * @param type   The {@link PersistentDataType} to be used.
     * @param obj    The object to put in the container
     */
    public static <T, Z> void setCustom(
            @NotNull PersistentDataHolder holder,
            @NotNull NamespacedKey key,
            @NotNull PersistentDataType<T, Z> type,
            @NotNull Z obj) {
        holder.getPersistentDataContainer().set(key, type, obj);
    }

    /**
     * Remove a custom {@link PersistentDataType} from a {@link PersistentDataContainer}
     *
     * @param holder The {@link PersistentDataHolder} to remove the data from
     * @param key    The key of the data to remove
     */
    public static <T, Z> void removeCustom(@NotNull PersistentDataHolder holder, @NotNull NamespacedKey key) {
        holder.getPersistentDataContainer().remove(key);
    }
}
