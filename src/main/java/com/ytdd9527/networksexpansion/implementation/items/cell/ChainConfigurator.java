package com.ytdd9527.networksexpansion.implementation.items.cell;

import com.balugaq.netex.api.enums.TransportMode;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.cell.LineState;
import com.ytdd9527.networksexpansion.api.chain.ChainBindingMenu;
import com.ytdd9527.networksexpansion.api.chain.ChainBindingStore;
import com.ytdd9527.networksexpansion.api.chain.ChainTargetCache;
import com.ytdd9527.networksexpansion.api.chain.WorkMode;
import com.ytdd9527.networksexpansion.api.filter.BrowseUI;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.core.machines.AbstractChainMachine;
import com.ytdd9527.networksexpansion.utils.GhostItems;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ChainConfigurator extends SpecialSlimefunItem {

    private static final NamespacedKey CONFIG_KEY = new NamespacedKey(Networks.getInstance(), "chain_cfg");
    private static final int MAX_CONFIG_LENGTH = 100_000;
    private static final int INFO_SLOT = 0;
    private static final int CLOSE_SLOT = 8;

    public ChainConfigurator(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    private record StoredConfig(
        @NotNull BlockFace face,
        @NotNull Map<Integer, List<ItemStack>> bindings,
        @Nullable TransportMode mode) {
    }

    @Override
    public void preRegister() {
        addItemHandler((ItemUseHandler) e -> {
            e.cancel();
            Player player = e.getPlayer();
            ItemStack held = player.getInventory().getItemInMainHand();
            Optional<Block> optional = e.getClickedBlock();
            if (optional.isEmpty()) {
                if (player.isSneaking() && getStored(held) != null) {
                    setStored(held, null, null, null);
                    refreshLore(held);
                }
                return;
            }
            Block block = optional.get();
            Location location = block.getLocation();
            AbstractChainMachine machine = AbstractChainMachine.machineAt(location);
            if (machine == null) {
                return;
            }
            if (!Slimefun.getProtectionManager()
                .hasPermission(player, block, Interaction.INTERACT_BLOCK)) {
                return;
            }
            StoredConfig stored = getStored(held);
            if (stored != null) {
                apply(player, machine, location, stored);
            }
        });
    }

    public static void openSelect(@NotNull Player player, @NotNull Location location, @NotNull ItemStack held) {
        LineState state = AbstractChainMachine.loadedState(location);
        int totalBindings = 0;
        for (BlockFace face : ChainBindingMenu.SLOT_FACES) {
            totalBindings += ChainBindingStore.load(location, face).size();
        }
        if (totalBindings == 0) {
            return;
        }
        ChestMenu menu = new ChestMenu(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_TITLE_SELECT));
        menu.setPlayerInventoryClickable(false);
        menu.setEmptySlotsClickable(false);
        for (int slot = 0; slot < 27; slot++) {
            if (slot == INFO_SLOT || slot == CLOSE_SLOT || ChainBindingMenu.in(ChainBindingMenu.DIRECTION_SLOTS, slot)) {
                continue;
            }
            menu.addItem(slot, ChestMenuUtils.getBackground(), (p, s, i, a) -> false);
        }
        ItemStack info = Icon.CHAIN_BINDING_INFO.clone();
        info.editMeta(meta -> {
            meta.setDisplayName(Lang.getString(CellnetText.BINDING_INFO_NAME));
            meta.setLore(List.of(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_INFO_LORE)));
        });
        GhostItems.mark(info);
        menu.addItem(INFO_SLOT, info, (p, s, i, a) -> false);
        menu.addItem(CLOSE_SLOT, BrowseUI.backButton(), (p, s, i, a) -> false);
        for (int i = 0; i < ChainBindingMenu.SLOT_FACES.length; i++) {
            BlockFace face = ChainBindingMenu.SLOT_FACES[i];
            boolean enabled = state.effectiveDirections().contains(face);
            int boundCount = ChainBindingStore.load(location, face).size();
            TransportMode override = ChainBindingStore.loadDirMode(location, face);
            ItemStack icon = ChainBindingMenu.directionIcon(location, face, enabled);
            icon.editMeta(meta -> {
                meta.setDisplayName(Lang.getString(
                    CellnetText.BINDING_DIRECTION_NAME, ChainBindingMenu.directionName(face)));
                List<String> lore = new ArrayList<>();
                lore.add(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_DIR_COUNT, boundCount));
                lore.add(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_DIR_MODE, override == null
                    ? Lang.getString(CellnetText.BINDING_MODE_FOLLOW)
                    : Lang.getString(CellnetText.LINE_MODES_PREFIX + override.name())));
                lore.add("");
                lore.add(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_DIR_HINT));
                meta.setLore(lore);
            });
            GhostItems.mark(icon);
            menu.addItem(ChainBindingMenu.DIRECTION_SLOTS[i], icon, (p, s, it, a) -> {
                if (enabled) {
                    copyDirection(p, location, face, held);
                }
                return false;
            });
        }
        menu.open(player);
    }

    private static void copyDirection(
        @NotNull Player player, @NotNull Location location, @NotNull BlockFace face, @NotNull ItemStack held) {
        Map<Integer, List<ItemStack>> bindings = ChainBindingStore.load(location, face);
        TransportMode mode = ChainBindingStore.loadDirMode(location, face);
        if (bindings.isEmpty() && mode == null) {
            return;
        }
        String encoded = ChainBindingStore.encodeBindings(bindings);
        if (encoded == null || encodeStored(face, mode, encoded).length() > MAX_CONFIG_LENGTH) {
            return;
        }
        setStored(held, face, mode, encoded);
        refreshLore(held);
        player.closeInventory();
    }

    private static void apply(
        @NotNull Player player, @NotNull AbstractChainMachine machine,
        @NotNull Location location, @NotNull StoredConfig stored) {
        if (machine.getWorkMode() == WorkMode.GRAB) {
            return;
        }
        LineState state = AbstractChainMachine.loadedState(location);
        BlockMenu menu = StorageCacheUtils.getMenu(location);
        if (menu != null) {
            machine.recountModules(menu, state);
        }
        if (!state.effectiveDirections().contains(stored.face())) {
            return;
        }
        List<Location> targets = ChainTargetCache.targets(location, stored.face(), machine.effectiveDistance(state),
            state.vanilla, machine.workTtlTicks(), machine.idleTtlTicks());
        Map<Integer, List<ItemStack>> newTable = new LinkedHashMap<>();
        int applied = 0;
        int missed = 0;
        for (Map.Entry<Integer, List<ItemStack>> entry : stored.bindings().entrySet()) {
            if (entry.getKey() <= targets.size()) {
                newTable.put(entry.getKey(), entry.getValue());
                applied++;
            } else {
                missed++;
            }
        }
        if (applied > 0 && !ChainBindingStore.save(location, stored.face(), newTable)) {
            return;
        }
        if (!player.isSneaking() && stored.mode() != null) {
            ChainBindingStore.saveDirMode(location, stored.face(), stored.mode());
            Map<BlockFace, TransportMode> overrides = state.dirModes;
            if (overrides != null) {
                overrides.put(stored.face(), stored.mode());
            }
        }
        AbstractChainMachine.invalidateBindings(state, stored.face());
    }

    private static void setStored(
        @NotNull ItemStack held, @Nullable BlockFace face,
        @Nullable TransportMode mode, @Nullable String encoded) {
        if (held.getType().isAir()) {
            return;
        }
        ItemMeta meta = held.getItemMeta();
        if (meta == null) {
            return;
        }
        if (face == null) {
            PersistentDataAPI.remove(meta, CONFIG_KEY);
        } else {
            PersistentDataAPI.setString(meta, CONFIG_KEY,
                encodeStored(face, mode, encoded == null ? "" : encoded));
        }
        held.setItemMeta(meta);
    }

    @NotNull
    private static String encodeStored(
        @NotNull BlockFace face, @Nullable TransportMode mode, @NotNull String encoded) {
        return face.name() + ";" + (mode == null ? "" : mode.name()) + ";" + encoded;
    }

    @Nullable
    private static StoredConfig getStored(@NotNull ItemStack held) {
        if (!held.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = held.getItemMeta();
        String raw = meta == null ? null : PersistentDataAPI.getString(meta, CONFIG_KEY);
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        int first = raw.indexOf(';');
        int second = raw.indexOf(';', first + 1);
        if (first <= 0 || second < 0) {
            return null;
        }
        BlockFace face;
        try {
            face = BlockFace.valueOf(raw.substring(0, first));
        } catch (IllegalArgumentException e) {
            return null;
        }
        String modePart = raw.substring(first + 1, second);
        TransportMode mode = null;
        if (!modePart.isEmpty()) {
            try {
                mode = TransportMode.valueOf(modePart);
            } catch (IllegalArgumentException ignored) {
            }
        }
        Map<Integer, List<ItemStack>> bindings = ChainBindingStore.decodeBindings(raw.substring(second + 1));
        return new StoredConfig(face, bindings, mode);
    }

    private static void refreshLore(@NotNull ItemStack held) {
        if (held.getType().isAir()) {
            return;
        }
        ItemMeta meta = held.getItemMeta();
        if (meta == null) {
            return;
        }
        StoredConfig stored = getStored(held);
        List<String> lore = new ArrayList<>();
        if (stored == null) {
            lore.add(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_LORE_HINT_COPY));
            lore.add(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_LORE_HINT_APPLY));
            lore.add(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_LORE_HINT_CLEAR));
        } else {
            lore.add(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_LORE_FACE,
                ChainBindingMenu.directionName(stored.face())));
            lore.add(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_LORE_COUNT, stored.bindings().size()));
            if (stored.mode() == null) {
                lore.add(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_LORE_FOLLOW));
            } else {
                lore.add(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_LORE_MODE,
                    Lang.getString(CellnetText.LINE_MODES_PREFIX + stored.mode().name())));
            }
            lore.add("");
            lore.add(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_LORE_HINT_APPLY));
            lore.add(Lang.getString(CellnetText.CHAIN_CONFIGURATOR_LORE_HINT_CLEAR));
        }
        meta.setLore(lore);
        held.setItemMeta(meta);
    }
}
