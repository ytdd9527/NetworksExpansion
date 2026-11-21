package com.balugaq.netex.api.keybind;

import com.balugaq.netex.api.algorithm.ID;
import com.balugaq.netex.utils.Debug;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public @Data class KeybindsScript {
    private final OfflinePlayer player;
    private final Keybinds keybinds;
    private final String keybindsName;
    private final Map<Keybind, Action> code;
    private final long id;

    public KeybindsScript(OfflinePlayer player, Keybinds keybinds, String keybindsName, Map<Keybind, Action> code, long id) {
        this.player = player;
        this.keybinds = keybinds;
        this.keybindsName = keybindsName;
        this.code = code;
        this.id = id;
    }

    public static KeybindsScript warp(Player player, Keybinds keybinds, String keybindsName, long id) {
        return new KeybindsScript(player, keybinds, keybindsName, keybinds.getKeybinds(player), id);
    }

    @SuppressWarnings("DataFlowIssue")
    @Nullable
    public static KeybindsScript fromConfig(Config config) {
        try {
            Map<Keybind, Action> code = new HashMap<>();
            for (String key : config.getKeys("keybinds")) {
                code.put(Keybind.get(NamespacedKey.fromString(key)), Action.get(NamespacedKey.fromString(config.getString("keybinds." + key))));
            }
            return new KeybindsScript(
                Bukkit.getOfflinePlayer(config.getString("author-name")),
                Keybinds.get(NamespacedKey.fromString(config.getString("keybinds-type"))),
                config.getString("keybinds-name"),
                code,
                config.getLong("id")
            );
        } catch (Exception e) {
            Debug.trace(e);
            return null;
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public String getAuthorName() {
        return player.getName();
    }

    public String getKeybindsName() {
        return ChatUtils.removeColorCodes(keybindsName);
    }

    public String getKeybindsType() {
        return keybinds.getKey().toString();
    }

    public void save() {
        Config config =
            new Config("plugins/" + Networks.getInstance().getName() + "/keybinds/" + ID.nextId() + ".nkb");

        config.setValue("keybinds-type", getKeybindsType());
        config.setValue("author-name", getAuthorName());
        config.setValue("keybinds-name", getKeybindsName());

        for (Map.Entry<Keybind, Action> entry : code.entrySet()) {
            config.setValue("keybinds." + entry.getKey().getKey(), entry.getValue().getKey().toString());
        }
        config.setValue("id", id);

        config.save();
    }
}
