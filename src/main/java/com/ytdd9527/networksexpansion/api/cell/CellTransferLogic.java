package com.ytdd9527.networksexpansion.api.cell;

/**
 * 元件转运机的纯结算逻辑：不依赖 Bukkit/Slimefun，便于单元测试。
 * 所有容量/单元上限均以参数传入（maxUnits 由调用方从 CellPersistence 取），
 * 转运本身不再增减存储元件的种类单元数。
 */
public final class CellTransferLogic {

    /** 一个槽位最多能叠放的量子存储数量（Minecraft 物品叠上限） */
    public static final int MAX_SHELLS_PER_SLOT = 64;

    private CellTransferLogic() {
    }

    public enum ImportResult {
        /** 已按计划转移 */
        ACCEPTED,
        /** 新种类但种类单元已满 */
        REJECT_UNITS_FULL,
        /** 该种类已达单格容量，无可转移数量 */
        REJECT_TYPE_FULL,
        /** 输入槽没有带内容的量子存储 */
        REJECT_EMPTY,
        /** 剩余壳无法在单个槽位表达且没有空槽可放零头 */
        REJECT_NO_SLOT,
        /** 剩余满壳数超过单槽叠放上限且无法转移更多 */
        REJECT_SHELL_OVERFLOW
    }

    /**
     * @param shellCount       输入槽量子存储叠放数量
     * @param perShellContent  每个量子存储的内容量（同槽叠放的量子存储内容必然相同）
     * @param existingInCell   元件中该种类已有存量（0 = 新种类）
     * @param perTypeLimit     元件单格容量
     * @param typeCount        元件当前实际占用的种类数
     * @param maxUnits         元件种类单元上限
     */
    public static ImportPlan planImport(
        long shellCount, long perShellContent,
        long existingInCell, long perTypeLimit,
        long typeCount, long maxUnits) {

        if (shellCount <= 0 || perShellContent <= 0) {
            return ImportPlan.reject(ImportResult.REJECT_EMPTY);
        }
        if (existingInCell <= 0 && typeCount >= maxUnits) {
            return ImportPlan.reject(ImportResult.REJECT_UNITS_FULL);
        }
        long remaining = perTypeLimit - existingInCell;
        if (remaining <= 0) {
            return ImportPlan.reject(ImportResult.REJECT_TYPE_FULL);
        }

        long total = shellCount * perShellContent;
        long transfer = Math.min(total, remaining);
        long rest = total - transfer;
        long fullShells = rest / perShellContent;
        long remainder = rest % perShellContent;
        // 剩余表达规则：0（清槽）/ 纯满壳叠（r==0）/ 单个不满壳（q==0）；
        // 其余情况需要额外空槽放零头壳
        boolean needExtraSlot = fullShells > 0 && remainder > 0;
        if (fullShells > MAX_SHELLS_PER_SLOT) {
            return ImportPlan.reject(ImportResult.REJECT_SHELL_OVERFLOW);
        }
        return new ImportPlan(ImportResult.ACCEPTED, transfer, fullShells, remainder,
            rest == 0, needExtraSlot);
    }

    public record ImportPlan(
        ImportResult result,
        long transferAmount,
        long fullShells,
        long remainder,
        boolean consumeAll,
        boolean needExtraSlot) {

        static ImportPlan reject(ImportResult result) {
            return new ImportPlan(result, 0, 0, 0, false, false);
        }

        public boolean accepted() {
            return result == ImportResult.ACCEPTED;
        }
    }

    public enum ExportResult {
        /** 匹配空壳恰有 1 个，原位灌成成品 */
        ACCEPTED_IN_PLACE,
        /** 匹配空壳叠放多个，消耗 1 个后需空槽放成品 */
        ACCEPTED_WITH_EXTRA_SLOT,
        /** 没有匹配等级的空壳 */
        REJECT_NO_SHELL,
        /** 有匹配空壳但叠放多个且没有空槽放成品 */
        REJECT_NO_SLOT
    }

    /**
     * @param matchedShellCount 匹配等级空壳所在槽位的叠放数量（0 = 没有匹配空壳）
     * @param hasFreeSlot       除壳槽位外是否还有空输出槽
     */
    public static ExportPlan planExport(long matchedShellCount, boolean hasFreeSlot) {
        if (matchedShellCount <= 0) {
            return new ExportPlan(ExportResult.REJECT_NO_SHELL, -1);
        }
        if (matchedShellCount == 1) {
            return new ExportPlan(ExportResult.ACCEPTED_IN_PLACE, -1);
        }
        return hasFreeSlot
            ? new ExportPlan(ExportResult.ACCEPTED_WITH_EXTRA_SLOT, -1)
            : new ExportPlan(ExportResult.REJECT_NO_SLOT, -1);
    }

    public record ExportPlan(ExportResult result, long reserved) {

        public boolean accepted() {
            return result == ExportResult.ACCEPTED_IN_PLACE
                || result == ExportResult.ACCEPTED_WITH_EXTRA_SLOT;
        }
    }
}
