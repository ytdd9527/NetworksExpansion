package com.ytdd9527.networksexpansion.core.managers;

import com.balugaq.netex.utils.Debug;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.storage.schema.DDLProvider;
import io.github.sefiraat.networks.Networks;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class SchemaManager {

    private static final Logger LOGGER = Networks.getInstance().getLogger();
    private final ConnectionManager connMgr;
    private final DDLProvider ddl = new DDLProvider();

    public SchemaManager(ConnectionManager connMgr) {
        this.connMgr = connMgr;
    }

    public DDLProvider getDDL() {
        return ddl;
    }

    public void initSchema() {
        try (Connection conn = connMgr.getConnection()) {
            exec(conn, ddl.createSchemaInfoTable());
            initItemTemplatesTable(conn);
            exec(conn, ddl.createCellMetaTable());
            exec(conn, ddl.createCellItemsTable());
            exec(conn, ddl.createJournalTable());
            exec(conn, ddl.createJournalIndex());
            exec(conn, ddl.createJournalArchiveTable());
            exec(conn, ddl.createArchiveIndexes());
            exec(conn, ddl.createArchiveTimestampIndex());

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT config_value FROM schema_info WHERE config_key = 'created_at'")) {
                if (!rs.next()) {
                    long now = System.currentTimeMillis();
                    exec(conn, "INSERT INTO schema_info (config_key, config_value) VALUES ('created_at', '" + now + "')");
                    exec(conn, "INSERT INTO schema_info (config_key, config_value) VALUES ('last_migrated', '" + now + "')");
                }
            }
        } catch (SQLException e) {
            LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_SCHEMA_INIT_FAILED, e.getMessage()));
            Debug.trace(e);
            throw new RuntimeException("Schema initialization failed", e);
        }
    }

    private void initItemTemplatesTable(Connection conn) throws SQLException {
        exec(conn, ddl.createItemTemplatesTable());
        exec(conn, ddl.createItemTemplatesDedup());
    }

    public void markSchemaVersion() {
        try (Connection conn = connMgr.getConnection()) {
            exec(conn, "INSERT OR REPLACE INTO schema_info (config_key, config_value) VALUES ('schema_version', '1')");
        } catch (SQLException e) {
            LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_SCHEMA_MARK_VERSION_FAILED, e.getMessage()));
            Debug.trace(e);
        }
    }

    private void exec(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}
