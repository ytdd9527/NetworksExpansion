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

@Data
@NullMarked
public class GridCache {
    private final List<ItemStack> pullItemHistory = new ArrayList<>();
    private int page;
    private int maxPages;
    private DisplayMode displayMode;
    private SortOrder sortOrder;

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

    @RequiredArgsConstructor
    public enum SortOrder {
        ALPHABETICAL(Sorters.ITEMSTACK_ALPHABETICAL_SORT),
        NUMBER(Sorters.ITEMSTACK_NUMERICAL_SORT.reversed()),
        NUMBER_REVERSE(Sorters.ITEMSTACK_NUMERICAL_SORT),
        ADDON(Sorters.ITEMSTACK_ADDON_SORT);

        final Comparator<Map.Entry<ItemStack, Long>> comparator;

        public SortOrder next(@Range(from = 1, to = 4) int limit) {
            if (this.next().ordinal() + 1 >= limit) {
                return ALPHABETICAL;
            }

            return this.next();
        }

        public SortOrder next() {
            return switch (this) {
                case ALPHABETICAL -> NUMBER;
                case NUMBER -> NUMBER_REVERSE;
                case NUMBER_REVERSE -> ADDON;
                case ADDON -> ALPHABETICAL;
            };
        }

        public SortOrder previous(@Range(from = 1, to = 4) int limit) {
            if (this.previous().ordinal() + 1 >= limit) {
                return values()[limit - 1];
            }

            return this.previous();
        }

        public SortOrder previous() {
            return switch (this) {
                case ALPHABETICAL -> ADDON;
                case NUMBER -> ALPHABETICAL;
                case NUMBER_REVERSE -> NUMBER;
                case ADDON -> NUMBER_REVERSE;
            };
        }

        public String getTranslationName() {
            return Lang.getString("messages.completed-operation.grid.sort_orders." + name().toLowerCase());
        }

        public Comparator<? super Map.Entry<ItemStack, Long>> comparator() {
            return comparator;
        }
    }

    public enum DisplayMode {
        DISPLAY,
        HISTORY
    }
}
