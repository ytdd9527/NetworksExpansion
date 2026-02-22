package io.github.sefiraat.networks.slimefun.network.grid;

import com.balugaq.netex.utils.Lang;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;

public class GridCache {

    @NotNull
    private final List<ItemStack> pullItemHistory = new ArrayList<>();

    @Setter
    @Getter
    private int page;

    @Setter
    @Getter
    private int maxPages;

    @NotNull
    private DisplayMode displayMode;

    @NotNull
    private SortOrder sortOrder;

    @Nullable
    private String filter;

    public GridCache(int page, int maxPages, @NotNull SortOrder sortOrder) {
        this.page = page;
        this.maxPages = maxPages;
        this.sortOrder = sortOrder;
        this.displayMode = DisplayMode.DISPLAY;
    }

    @NotNull
    public SortOrder getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(@NotNull SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Nullable
    public String getFilter() {
        return this.filter;
    }

    public void setFilter(@Nullable String filter) {
        this.filter = filter;
    }

    @NotNull
    public List<ItemStack> getPullItemHistory() {
        return this.pullItemHistory;
    }

    public void addPullItemHistory(@Nullable ItemStack itemStack) {
        if (itemStack != null) {
            getPullItemHistory().remove(itemStack);

            getPullItemHistory().add(0, itemStack);
        }
    }

    public @NotNull DisplayMode getDisplayMode() {
        return this.displayMode;
    }

    public void toggleDisplayMode() {
        if (this.displayMode == DisplayMode.DISPLAY) {
            this.displayMode = DisplayMode.HISTORY;
        } else {
            this.displayMode = DisplayMode.DISPLAY;
        }
    }

    public enum SortOrder {
        ALPHABETICAL,
        NUMBER,
        NUMBER_REVERSE,
        ADDON;

        public @NotNull SortOrder next(@Range(from = 1, to = 4) int limit) {
            if (this.next().ordinal() + 1 >= limit) {
                return ALPHABETICAL;
            }

            return this.next();
        }

        public @NotNull SortOrder next() {
            return switch (this) {
                case ALPHABETICAL -> NUMBER;
                case NUMBER -> NUMBER_REVERSE;
                case NUMBER_REVERSE -> ADDON;
                case ADDON -> ALPHABETICAL;
            };
        }

        public @NotNull SortOrder previous(@Range(from = 1, to = 4) int limit) {
            if (this.previous().ordinal() + 1 >= limit) {
                return values()[limit - 1];
            }

            return this.previous();
        }

        public @NotNull SortOrder previous() {
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
    }

    public enum DisplayMode {
        DISPLAY,
        HISTORY
    }
}
