package com.ytdd9527.networksexpansion.api.cell;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 纯逻辑测试：不依赖 Bukkit/Slimefun，maxUnits 等上限均作为参数传入。
 */
class CellTransferLogicTest {

    @Nested
    class Import {

        @Test
        void rejectWhenUnitsFullOnNewType() {
            var plan = CellTransferLogic.planImport(1, 100, 0, 1000, 64, 64);
            assertEquals(CellTransferLogic.ImportResult.REJECT_UNITS_FULL, plan.result());
            assertFalse(plan.accepted());
        }

        @Test
        void acceptNewTypeWhenUnitsAvailable() {
            var plan = CellTransferLogic.planImport(1, 100, 0, 1000, 10, 64);
            assertTrue(plan.accepted());
            assertEquals(100, plan.transferAmount());
            assertTrue(plan.consumeAll());
        }

        @Test
        void newTypeCheckIgnoresUnitCounterAndUsesTypeCount() {
            // 旧的 currentPerTypeLimit 计数器已满但实际种类为空 → 应放行
            var plan = CellTransferLogic.planImport(1, 50, 0, 1000, 0, 64);
            assertTrue(plan.accepted());
        }

        @Test
        void rejectWhenTypeCapacityFull() {
            var plan = CellTransferLogic.planImport(1, 100, 1000, 1000, 1, 64);
            assertEquals(CellTransferLogic.ImportResult.REJECT_TYPE_FULL, plan.result());
        }

        @Test
        void partialTransferForExistingType() {
            // 已有 300/1000，壳内容 500 → 全部转走 500，壳转空清槽
            var plan = CellTransferLogic.planImport(1, 500, 300, 1000, 1, 64);
            assertTrue(plan.accepted());
            assertEquals(500, plan.transferAmount());
            assertTrue(plan.consumeAll());
        }

        @Test
        void transferCappedAtRemaining() {
            // 已有 800/1000，壳内容 500 → 只转 200，剩 300（< 单壳内容 → 1 个不满壳）
            var plan = CellTransferLogic.planImport(1, 500, 800, 1000, 1, 64);
            assertTrue(plan.accepted());
            assertEquals(200, plan.transferAmount());
            assertEquals(0, plan.fullShells());
            assertEquals(300, plan.remainder());
            assertFalse(plan.consumeAll());
        }

        @Test
        void stackedShellsTransferTotal() {
            // 叠 3 个内容 100 的壳 → 总量 300，全转
            var plan = CellTransferLogic.planImport(3, 100, 0, 1000, 1, 64);
            assertTrue(plan.accepted());
            assertEquals(300, plan.transferAmount());
            assertTrue(plan.consumeAll());
        }

        @Test
        void stackedShellsPartialTransferLeavesFullShells() {
            // 叠 5 个内容 100（总 500），已有 0/1000 → 转 500？remaining=1000>500 全转。
            // 改成容量 300：转 300，剩 200 → 2 个满壳
            var plan = CellTransferLogic.planImport(5, 100, 0, 300, 1, 64);
            assertTrue(plan.accepted());
            assertEquals(300, plan.transferAmount());
            assertEquals(2, plan.fullShells());
            assertEquals(0, plan.remainder());
            assertFalse(plan.consumeAll());
        }

        @Test
        void stackedShellsPartialTransferNeedsExtraSlotForRemainder() {
            // 容量 1000，已有 0 → remaining=1000；叠 5 个内容 300（总 1500）→ 转 1000，剩 500
            // 剩余 500 = 1 个满壳(300) + 200 零头 → 需要额外空槽
            var plan = CellTransferLogic.planImport(5, 300, 0, 1000, 1, 64);
            assertTrue(plan.accepted());
            assertEquals(1000, plan.transferAmount());
            assertEquals(1, plan.fullShells());
            assertEquals(200, plan.remainder());
            assertTrue(plan.needExtraSlot());
        }

        @Test
        void overflowWhenFullShellsExceedStackLimit() {
            // 叠 64 个内容 100（总 6400），容量 5000 → 转 5000，剩 1400 = 14 满壳，OK
            var ok = CellTransferLogic.planImport(64, 100, 0, 5000, 1, 64);
            assertTrue(ok.accepted());
            assertEquals(14, ok.fullShells());

            // 已有 200 → remaining 4800 → 转 4800，剩 1600 = 16 满壳，OK
            var ok2 = CellTransferLogic.planImport(64, 100, 200, 5000, 1, 64);
            assertTrue(ok2.accepted());

            // 直接构造剩余满壳 > 64：叠 100 个壳内容 100（总 10000），existing 7000 → remaining 3000
            // 转 3000 后剩 7000 = 70 个满壳 > 64 → 无法表达，拒绝
            var overflow = CellTransferLogic.planImport(100, 100, 7000, 10000, 1, 64);
            assertEquals(CellTransferLogic.ImportResult.REJECT_SHELL_OVERFLOW, overflow.result());
        }

        @Test
        void rejectEmptyInput() {
            assertEquals(CellTransferLogic.ImportResult.REJECT_EMPTY,
                CellTransferLogic.planImport(0, 100, 0, 1000, 0, 64).result());
            assertEquals(CellTransferLogic.ImportResult.REJECT_EMPTY,
                CellTransferLogic.planImport(1, 0, 0, 1000, 0, 64).result());
        }
    }

    @Nested
    class Export {

        @Test
        void rejectWhenNoMatchingShell() {
            var plan = CellTransferLogic.planExport(0, true);
            assertEquals(CellTransferLogic.ExportResult.REJECT_NO_SHELL, plan.result());
        }

        @Test
        void singleShellFilledInPlace() {
            var plan = CellTransferLogic.planExport(1, false);
            assertEquals(CellTransferLogic.ExportResult.ACCEPTED_IN_PLACE, plan.result());
            assertTrue(plan.accepted());
        }

        @Test
        void stackedShellsConsumeExactlyOne() {
            var withSlot = CellTransferLogic.planExport(64, true);
            assertEquals(CellTransferLogic.ExportResult.ACCEPTED_WITH_EXTRA_SLOT, withSlot.result());

            var noSlot = CellTransferLogic.planExport(64, false);
            assertEquals(CellTransferLogic.ExportResult.REJECT_NO_SLOT, noSlot.result());
            assertFalse(noSlot.accepted());
        }
    }
}
