package com.ytdd9527.networksexpansion.api.storage.template;

import com.balugaq.netex.utils.Debug;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.storage.schema.DDLProvider;
import com.ytdd9527.networksexpansion.core.managers.ConnectionManager;
import com.ytdd9527.networksexpansion.utils.CRC32Utils;
import com.ytdd9527.networksexpansion.utils.SerializeUtils;
import io.github.sefiraat.networks.Networks;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ItemTemplateRegistry {

    private static final Logger LOGGER = Networks.getInstance().getLogger();
    private final ConcurrentHashMap<Long, Long> dataHashToTplId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ItemStack> tplIdToItem = new ConcurrentHashMap<>();
    private final ConnectionManager connMgr;
    private final DDLProvider ddl;

    public ItemTemplateRegistry(ConnectionManager connMgr, DDLProvider ddl) {
        this.connMgr = connMgr;
        this.ddl = ddl;
    }

    private static long dataHash(@NotNull String itemData) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] digest = sha256.digest(itemData.getBytes(StandardCharsets.UTF_8));
            long hash = 0;
            for (int i = 0; i < 8; i++) {
                hash = (hash << 8) | (digest[i] & 0xff);
            }
            return hash;
        } catch (NoSuchAlgorithmException e) {
            return CRC32Utils.compute(itemData) & 0xffffffffL;
        }
    }

    public long getOrRegister(@NotNull ItemStack itemStack) {
        String itemData = SerializeUtils.object2String(itemStack);
        long hash = dataHash(itemData);

        Long cached = dataHashToTplId.get(hash);
        if (cached != null) {
            return cached;
        }

        Long dbTplId = queryTplId(hash, itemData);
        if (dbTplId != null) {
            dataHashToTplId.put(hash, dbTplId);
            return dbTplId;
        }

        long newTplId = insertTemplate(itemData, hash);
        if (newTplId > 0) {
            dataHashToTplId.put(hash, newTplId);
            tplIdToItem.put(newTplId, itemStack.clone());
            return newTplId;
        }

        dbTplId = queryTplId(hash, itemData);
        if (dbTplId != null) {
            dataHashToTplId.put(hash, dbTplId);
            return dbTplId;
        }

        throw new IllegalStateException(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_TEMPLATE_REGISTER_FAILED));
    }

    private long insertTemplate(String itemData, long hash) {
        String sql = ddl.insertIgnore() + " INTO item_templates (item_data, item_data_hash, crc32, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, itemData);
            ps.setLong(2, hash);
            ps.setInt(3, CRC32Utils.compute(itemData));
            ps.setLong(4, System.currentTimeMillis());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                return -1;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_TEMPLATE_INSERT_FAILED, e.getMessage()));
            Debug.trace(e);
        }
        return -1;
    }

    @Nullable
    private Long queryTplId(long hash, String expectedItemData) {
        String sql = "SELECT tpl_id, item_data FROM item_templates WHERE item_data_hash = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, hash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && Objects.equals(rs.getString("item_data"), expectedItemData)) {
                    return rs.getLong("tpl_id");
                }
            }
        } catch (SQLException e) {
            LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_TEMPLATE_QUERY_FAILED, e.getMessage()));
            Debug.trace(e);
        }
        return null;
    }

    @Nullable
    public ItemStack resolveItem(long tplId) {
        ItemStack cached = tplIdToItem.get(tplId);
        if (cached != null) {
            return cached.clone();
        }
        String itemData = loadTemplateData(tplId);
        if (itemData == null) {
            return null;
        }
        ItemStack item = SerializeUtils.string2Object(itemData);
        if (item != null) {
            tplIdToItem.put(tplId, item.clone());
        }
        return item;
    }

    @Nullable
    private String loadTemplateData(long tplId) {
        String sql = "SELECT item_data FROM item_templates WHERE tpl_id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, tplId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("item_data");
                }
            }
        } catch (SQLException e) {
            LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_TEMPLATE_LOAD_FAILED, e.getMessage()));
            Debug.trace(e);
        }
        return null;
    }

    public void preloadAll() {
        String sql = "SELECT tpl_id, item_data FROM item_templates";
        try (Connection conn = connMgr.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                long tplId = rs.getLong("tpl_id");
                String itemData = rs.getString("item_data");
                ItemStack item = SerializeUtils.string2Object(itemData);
                if (item != null) {
                    tplIdToItem.put(tplId, item);
                    dataHashToTplId.put(dataHash(itemData), tplId);
                }
            }
        } catch (SQLException e) {
            LOGGER.warning(Networks.getLocalizationService().getString(CellnetText.PERSISTENCE_TEMPLATE_PRELOAD_FAILED, e.getMessage()));
            Debug.trace(e);
        }
    }
}
