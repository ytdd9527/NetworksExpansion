package com.ytdd9527.networksexpansion.api.storage;

import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.storage.dao.CellDao;
import com.ytdd9527.networksexpansion.api.storage.journal.CheckpointTask;
import com.ytdd9527.networksexpansion.api.storage.journal.DirtyTracker;
import com.ytdd9527.networksexpansion.api.storage.journal.JournalWriter;
import com.ytdd9527.networksexpansion.api.storage.schema.DDLProvider;
import com.ytdd9527.networksexpansion.api.storage.template.ItemKeyTplIdBridge;
import com.ytdd9527.networksexpansion.api.storage.template.ItemTemplateRegistry;
import com.ytdd9527.networksexpansion.api.storage.write.WriteStrategy;
import com.ytdd9527.networksexpansion.core.managers.ConnectionManager;
import com.ytdd9527.networksexpansion.core.managers.SchemaManager;
import io.github.sefiraat.networks.Networks;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class CellStorageDatabase {

    private static final Logger LOGGER = Networks.getInstance().getLogger();
    private static final AtomicLong FLUSH_COUNTER = new AtomicLong(0);

    private final StorageConfig storageConfig;
    private final ConnectionManager connMgr;
    private final SchemaManager schemaManager;
    private final ItemTemplateRegistry templateRegistry;
    private final ItemKeyTplIdBridge bridge;
    private final DirtyTracker dirtyTracker;
    private final WriteStrategy writeStrategy;
    private final JournalWriter journalWriter;
    private final CheckpointTask checkpointTask;
    private final CellDao storageController;
    private final BackupTask backupTask;
    private ScheduledExecutorService checkpointExecutor;
    private ScheduledExecutorService backupExecutor;

    public CellStorageDatabase() {
        this.storageConfig = new StorageConfig();
        this.connMgr = new ConnectionManager(storageConfig);
        this.schemaManager = new SchemaManager(connMgr);
        DDLProvider ddl = schemaManager.getDDL();
        this.templateRegistry = new ItemTemplateRegistry(connMgr, ddl);
        this.bridge = new ItemKeyTplIdBridge(templateRegistry);
        this.dirtyTracker = new DirtyTracker();
        this.writeStrategy = new WriteStrategy();
        this.journalWriter = new JournalWriter(writeStrategy, dirtyTracker, connMgr);
        this.checkpointTask = new CheckpointTask(connMgr, writeStrategy, ddl, storageConfig);
        this.storageController = new CellDao(connMgr, bridge, dirtyTracker);
        this.backupTask = new BackupTask(storageConfig, connMgr);
    }

    public void init() {
        schemaManager.initSchema();
        schemaManager.markSchemaVersion();
        templateRegistry.preloadAll();
        checkpointTask.replayPendingJournal();

        checkpointExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "NetworksExpansion-Cellnet-Checkpoint");
            t.setDaemon(true);
            return t;
        });
        long checkpointInterval = storageConfig.getCheckpointInterval();
        if (checkpointInterval > 0) {
            checkpointExecutor.scheduleWithFixedDelay(
                checkpointTask,
                checkpointInterval,
                checkpointInterval,
                TimeUnit.SECONDS);
        }

        if (storageConfig.isBackupEnabled()) {
            backupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "NetworksExpansion-Cellnet-Backup");
                t.setDaemon(true);
                return t;
            });
            long backupIntervalSeconds = storageConfig.getBackupIntervalHours() * 3600L;
            if (backupIntervalSeconds > 0) {
                backupExecutor.scheduleWithFixedDelay(
                    backupTask,
                    backupIntervalSeconds,
                    backupIntervalSeconds,
                    TimeUnit.SECONDS);
            }
        }

        LOGGER.info(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_DB_INITIALIZED));
    }

    public void saveAllAsync() {
        journalWriter.flush();
        long count = FLUSH_COUNTER.incrementAndGet();
        if (storageConfig.getCheckpointThreshold() > 0 && count % storageConfig.getCheckpointThreshold() == 0 && checkpointExecutor != null) {
            checkpointExecutor.submit(checkpointTask);
        }
    }

    public void shutdown() {
        stopExecutor(backupExecutor, 5);
        stopExecutor(checkpointExecutor, 15);
        journalWriter.flush();
        checkpointTask.doCheckpoint();
        writeStrategy.shutdown();
        connMgr.shutdown();
        LOGGER.info(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_DB_CLOSED));
    }

    private static void stopExecutor(ScheduledExecutorService executor, long timeoutSeconds) {
        if (executor == null) {
            return;
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public CellDao getStorageController() {
        return storageController;
    }
}