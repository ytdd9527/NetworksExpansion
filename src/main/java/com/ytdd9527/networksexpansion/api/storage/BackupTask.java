package com.ytdd9527.networksexpansion.api.storage;

import com.balugaq.netex.utils.Debug;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.core.managers.ConnectionManager;
import io.github.sefiraat.networks.Networks;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Logger;

public class BackupTask implements Runnable {

    private static final Logger LOGGER = Networks.getInstance().getLogger();
    private final StorageConfig config;
    private final ConnectionManager connMgr;

    public BackupTask(StorageConfig config, ConnectionManager connMgr) {
        this.config = config;
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        if (!config.isBackupEnabled()) {
            return;
        }
        try {
            doBackup();
        } catch (Exception e) {
            LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_BACKUP_FAILED, e.getMessage()));
            Debug.trace(e);
        }
    }

    public void doBackup() {
        File dbFile = config.getSqliteFile();
        if (!dbFile.exists()) {
            LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_BACKUP_FILE_MISSING, dbFile.getAbsolutePath()));
            return;
        }
        File backupDir = new File(dbFile.getParentFile(), "backups");
        backupDir.mkdirs();
        String timestamp = String.valueOf(System.currentTimeMillis());
        File backupFile = new File(backupDir, "cellnet_storage_" + timestamp + ".db");
        try {
            Files.deleteIfExists(backupFile.toPath());
        } catch (IOException e) {
            LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_BACKUP_COPY_FAILED, e.getMessage()));
            Debug.trace(e);
            return;
        }
        try (Connection conn = connMgr.getConnection();
             Statement st = conn.createStatement()) {
            st.execute("VACUUM INTO '" + backupFile.getAbsolutePath().replace("'", "''") + "'");
        } catch (SQLException e) {
            LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_BACKUP_COPY_FAILED, e.getMessage()));
            Debug.trace(e);
            return;
        }
        deleteOldBackups(backupDir, config.getBackupKeep());
    }

    private void deleteOldBackups(@NotNull File backupDir, int keep) {
        if (keep <= 0) {
            return;
        }
        File[] backups = backupDir.listFiles((dir, name) -> name.startsWith("cellnet_storage_") && name.endsWith(".db"));
        if (backups == null || backups.length <= keep) {
            return;
        }
        Arrays.sort(backups, Comparator.comparing(File::getName));
        for (int i = 0; i < backups.length - keep; i++) {
            try {
                Files.deleteIfExists(backups[i].toPath());
            } catch (IOException e) {
                LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_BACKUP_CLEANUP_FAILED, backups[i].getName(), e.getMessage()));
                Debug.trace(e);
            }
        }
    }
}
