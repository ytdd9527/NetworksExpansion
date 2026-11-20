package io.github.sefiraat.networks.slimefun.network.grid;

import org.jspecify.annotations.NullMarked;

/** 把网格固定槽位收敛成一次声明，替代每个实现散落的七个 getter。 */
@NullMarked
public record SlotLayout(
    int[] backgroundSlots,
    int[] displaySlots,
    int inputSlot,
    int changeSort,
    int pagePrevious,
    int pageNext,
    int filterSlot
) {
}
