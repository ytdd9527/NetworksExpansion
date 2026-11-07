package com.ytdd9527.networksexpansion.api.cell;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 纯逻辑测试：不依赖 Bukkit/Slimefun，容量与单元上限均作为参数传入。
 */
class CellTransferLogicTest {

    @Nested
    class Import {

        @Test
        void rejectNewTypeWhenUnitsFull() {
            assertEquals(0, CellTransferLogic.importable(100, 0, 1000, 64, 64));
        }

        @Test
        void acceptNewTypeWhenUnitsAvailable() {
            assertEquals(100, CellTransferLogic.importable(100, 0, 1000, 0, 64));
        }

        @Test
        void capAtRemainingTypeCapacity() {
            assertEquals(200, CellTransferLogic.importable(500, 800, 1000, 1, 64));
        }

        @Test
        void existingTypeIgnoresUnitLimit() {
            assertEquals(500, CellTransferLogic.importable(500, 300, 1000, 64, 64));
        }

        @Test
        void rejectWhenTypeCapacityFull() {
            assertEquals(0, CellTransferLogic.importable(100, 1000, 1000, 1, 64));
        }

        @Test
        void rejectNonPositiveAmount() {
            assertEquals(0, CellTransferLogic.importable(0, 0, 1000, 0, 64));
            assertEquals(0, CellTransferLogic.importable(-1, 0, 1000, 0, 64));
        }
    }

    @Nested
    class Export {

        @Test
        void transferWholeAmountIntoEmptyStorage() {
            assertEquals(500, CellTransferLogic.exportable(500, 1000, 0));
        }

        @Test
        void capAtStorageRemainingCapacity() {
            assertEquals(200, CellTransferLogic.exportable(500, 1000, 800));
        }

        @Test
        void rejectWhenStorageFull() {
            assertEquals(0, CellTransferLogic.exportable(500, 1000, 1000));
        }

        @Test
        void rejectNonPositiveAmount() {
            assertEquals(0, CellTransferLogic.exportable(0, 1000, 0));
            assertEquals(0, CellTransferLogic.exportable(-1, 1000, 0));
        }
    }

    @Nested
    class PickStorage {

        @Test
        void preferStorageThatFitsEverything() {
            long[] free = {100, 300, 1000, 800};
            assertEquals(2, CellTransferLogic.pickStorage(free, 500));
        }

        @Test
        void fallBackToLargestWhenNoneFits() {
            long[] free = {100, 300, 0, 250};
            assertEquals(1, CellTransferLogic.pickStorage(free, 500));
        }

        @Test
        void returnMinusOneWhenNothingAvailable() {
            assertEquals(-1, CellTransferLogic.pickStorage(new long[]{0, 0, -5}, 500));
            assertEquals(-1, CellTransferLogic.pickStorage(new long[0], 500));
        }
    }

    @Nested
    class Upgrade {

        @Test
        void incrementByOneAndClampAtMax() {
            assertEquals(2, CellTransferLogic.upgradedUnits(1, 64));
            assertEquals(64, CellTransferLogic.upgradedUnits(64, 64));
        }
    }

    @Nested
    class UnlimitedOutput {

        @Test
        void normalCellsConsumeShell() {
            assertTrue(CellTransferLogic.consumesShellOnOutput(64));
            assertTrue(CellTransferLogic.consumesShellOnOutput(1_024));
            assertTrue(CellTransferLogic.consumesShellOnOutput(140_737_488_355_328L));
        }

        @Test
        void unlimitedCellDoesNotConsumeShell() {
            assertFalse(CellTransferLogic.consumesShellOnOutput(Long.MAX_VALUE));
        }

        @Test
        void thresholdMatchesLedgerRule() {
            long threshold = Long.MAX_VALUE / 2;
            assertTrue(CellTransferLogic.consumesShellOnOutput(threshold - 1));
            assertFalse(CellTransferLogic.consumesShellOnOutput(threshold));
            assertTrue(CellTransferLogic.isUnlimited(threshold));
            assertFalse(CellTransferLogic.isUnlimited(threshold - 1));
        }
    }
}
