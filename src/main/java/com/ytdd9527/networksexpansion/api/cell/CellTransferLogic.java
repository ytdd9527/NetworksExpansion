package com.ytdd9527.networksexpansion.api.cell;

/**
 * 元件转运台的纯结算逻辑：不依赖 Bukkit/Slimefun，便于单元测试。
 * 转运只搬运物品数量，不改变元件种类单元数，也不消耗除升级外的量子储存本体。
 */
public final class CellTransferLogic {

    private CellTransferLogic() {
    }

    /**
     * 量子储存 -&gt; 元件：本次最多可转入的数量。
     *
     * @param quantumAmount  量子储存内部数量
     * @param existingInCell 元件中该种类已有存量（0 = 新种类）
     * @param perTypeLimit   元件单格容量
     * @param typeCount      元件当前实际占用的种类数
     * @param maxUnits       元件种类单元上限
     */
    public static long importable(
        long quantumAmount,
        long existingInCell,
        long perTypeLimit,
        long typeCount,
        long maxUnits) {
        if (quantumAmount <= 0) {
            return 0;
        }
        if (existingInCell <= 0 && typeCount >= maxUnits) {
            return 0;
        }
        return Math.max(0, Math.min(quantumAmount, perTypeLimit - existingInCell));
    }

    /**
     * 元件 -&gt; 量子储存：本次最多可转出的数量。
     *
     * @param cellAmount      元件中该种类的存量
     * @param quantumLimit    目标量子储存的容量上限
     * @param quantumExisting 目标量子储存已有的内部数量
     */
    public static long exportable(long cellAmount, long quantumLimit, long quantumExisting) {
        if (cellAmount <= 0) {
            return 0;
        }
        return Math.max(0, Math.min(cellAmount, quantumLimit - quantumExisting));
    }

    /**
     * 从候选量子储存中挑一个：优先能一次装下全部，否则取剩余容量最大的。
     *
     * @param freeCapacities 每个候选的剩余容量，小于等于 0 表示不可用
     * @param requested       本次希望转出的数量
     * @return 选中的下标，全部不可用时返回 -1
     */
    public static int pickStorage(long[] freeCapacities, long requested) {
        int best = -1;
        long bestFree = 0;
        for (int i = 0; i < freeCapacities.length; i++) {
            long free = freeCapacities[i];
            if (free <= 0) {
                continue;
            }
            if (free >= requested) {
                return i;
            }
            if (free > bestFree) {
                bestFree = free;
                best = i;
            }
        }
        return best;
    }

    /**
     * 元件升级后新的种类单元数。
     */
    public static long upgradedUnits(long current, long maxUnits) {
        return Math.min(maxUnits, current + 1);
    }

    public static boolean isUnlimited(long perTypeLimit) {
        return perTypeLimit >= Long.MAX_VALUE / 2;
    }

    /**
     * 元件 -&gt; 量子储存新建输出时是否需要消耗空壳；无限元件无对应等级壳，凭空产出。
     */
    public static boolean consumesShellOnOutput(long perTypeLimit) {
        return !isUnlimited(perTypeLimit);
    }
}
