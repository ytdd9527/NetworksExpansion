package io.github.sefiraat.networks.slimefun.network.grid;

import com.balugaq.netex.api.algorithm.Sorters;
import com.balugaq.netex.utils.Lang;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@NullMarked
public class GridCache {
    private final List<ItemStack> pullItemHistory = new ArrayList<>();
    private int page;
    private int maxPages;
    private DisplayMode displayMode;
    private SortOrder sortOrder;
    @Nullable
    private SortOrder secondarySortOrder;

    @Nullable
    private String filter;

    @Nullable
    private List<Map.Entry<ItemStack, Long>> entriesCache;

    public GridCache(int page, int maxPages, SortOrder sortOrder) {
        this.page = page;
        this.maxPages = maxPages;
        this.sortOrder = sortOrder;
        this.displayMode = DisplayMode.DISPLAY;
    }

    public void setSortOrder(SortOrder sortOrder) {
        if (this.sortOrder != sortOrder) {
            this.entriesCache = null;
        }
        this.sortOrder = sortOrder;
    }

    public void setSecondarySortOrder(@Nullable SortOrder secondarySortOrder) {
        if (this.secondarySortOrder != secondarySortOrder) {
            this.entriesCache = null;
        }
        this.secondarySortOrder = secondarySortOrder;
    }

    public void setFilter(@Nullable String filter) {
        if (!Objects.equals(this.filter, filter)) {
            this.entriesCache = null;
        }
        this.filter = filter;
    }

    public void addPullItemHistory(@Nullable ItemStack itemStack) {
        if (itemStack != null) {
            getPullItemHistory().remove(itemStack);
            getPullItemHistory().addFirst(itemStack);
        }
    }

    public void toggleDisplayMode() {
        if (this.displayMode == DisplayMode.DISPLAY) {
            this.displayMode = DisplayMode.HISTORY;
        } else {
            this.displayMode = DisplayMode.DISPLAY;
        }
    }

    public Comparator<Map.Entry<ItemStack, Long>> comparator() {
        if (secondarySortOrder == null || secondarySortOrder == sortOrder) {
            return sortOrder.comparator();
        }
        return sortOrder.comparator().thenComparing(secondarySortOrder.comparator());
    }

    @RequiredArgsConstructor
    public enum SortOrder {
        ALPHABETICAL(Sorters.ITEMSTACK_ALPHABETICAL_SORT),
        NUMBER(Sorters.ITEMSTACK_NUMERICAL_SORT.reversed()),
        NUMBER_REVERSE(Sorters.ITEMSTACK_NUMERICAL_SORT),
        ADDON(Sorters.ITEMSTACK_ADDON_SORT),
        MATERIAL(Sorters.ITEMSTACK_MATERIAL_SORT);

        final Comparator<Map.Entry<ItemStack, Long>> comparator;

        public SortOrder next(@Range(from = 1, to = 5) int limit) {
            int size = Math.max(1, Math.min(limit, values().length));
            return values()[(ordinal() + 1) % size];
        }

        public SortOrder next() {
            return switch (this) {
                case ALPHABETICAL -> NUMBER;
                case NUMBER -> NUMBER_REVERSE;
                case NUMBER_REVERSE -> ADDON;
                case ADDON -> MATERIAL;
                case MATERIAL -> ALPHABETICAL;
            };
        }

        public SortOrder previous(@Range(from = 1, to = 5) int limit) {
            int size = Math.max(1, Math.min(limit, values().length));
            return values()[(ordinal() - 1 + size) % size];
        }

        public SortOrder previous() {
            return switch (this) {
                case ALPHABETICAL -> MATERIAL;
                case NUMBER -> ALPHABETICAL;
                case NUMBER_REVERSE -> NUMBER;
                case ADDON -> NUMBER_REVERSE;
                case MATERIAL -> ADDON;
            };
        }

        public String getTranslationName() {
            return Lang.getString("messages.completed-operation.grid.sort_orders." + name().toLowerCase());
        }

        public Comparator<Map.Entry<ItemStack, Long>> comparator() {
            return comparator;
        }
    }

    public enum DisplayMode {
        DISPLAY,
        HISTORY
    }
}
