package com.ytdd9527.networksexpansion.api.storage.journal;

import com.balugaq.netex.utils.Debug;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.storage.write.WriteStrategy;
import com.ytdd9527.networksexpansion.core.managers.ConnectionManager;
import io.github.sefiraat.networks.Networks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JournalWriter {

    private static final Logger LOGGER = Networks.getInstance().getLogger();
    private static final int FLUSH_BATCH_SIZE = 2000;
    private final WriteStrategy writeStrategy;
    private final DirtyTracker dirtyTracker;
    private final ConnectionManager connMgr;

    public JournalWriter(WriteStrategy writeStrategy, DirtyTracker dirtyTracker, ConnectionManager connMgr) {
        this.writeStrategy = writeStrategy;
        this.dirtyTracker = dirtyTracker;
        this.connMgr = connMgr;
    }

    // Auto-save thread and main-thread menu paths may flush concurrently; serializing the
    // drain→write→commit/rollback cycle is what keeps pendingFlush from being clobbered
    // (a clobbered snapshot silently drops failed rows forever).
    public synchronized void flush() {
        List<JournalRow> allRows = dirtyTracker.drainPhase1();
        if (allRows.isEmpty()) {
            return;
        }

        List<List<JournalRow>> batches = partition(allRows, FLUSH_BATCH_SIZE);
        List<JournalRow> failedRows = new ArrayList<>();

        for (List<JournalRow> batch : batches) {
            try {
                writeStrategy.runExclusive(() -> {
                    try {
                        batchInsertJournal(batch);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                });
            } catch (Exception e) {
                failedRows.addAll(batch);
                LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_JOURNAL_FLUSH_FAILED, e.getMessage()));
                Debug.trace(e);
            }
        }

        if (failedRows.isEmpty()) {
            dirtyTracker.commitFlush();
        } else {
            dirtyTracker.rollbackFlush(failedRows);
            LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_JOURNAL_FLUSH_PARTIAL, failedRows.size()));
        }
    }

    private void batchInsertJournal(List<JournalRow> rows) throws SQLException {
        String sql = "INSERT INTO journal (cell_uuid, op, tpl_id, new_amount, crc32, timestamp, applied) "
            + "VALUES (?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = connMgr.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (JournalRow row : rows) {
                    ps.setString(1, row.cellUuid());
                    ps.setString(2, String.valueOf(row.op()));
                    if (row.tplId() != null) {
                        ps.setLong(3, row.tplId());
                    } else {
                        ps.setNull(3, Types.BIGINT);
                    }
                    if (row.newAmount() != null) {
                        ps.setLong(4, row.newAmount());
                    } else {
                        ps.setNull(4, Types.BIGINT);
                    }
                    ps.setInt(5, row.crc32());
                    ps.setLong(6, row.timestamp());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private static <T> List<List<T>> partition(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }
}