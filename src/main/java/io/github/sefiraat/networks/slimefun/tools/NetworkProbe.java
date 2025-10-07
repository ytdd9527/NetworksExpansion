package io.github.sefiraat.networks.slimefun.tools;

import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.implementation.ExpansionItemStacks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.slimefun.NetworksSlimefunItemStacks;
import io.github.sefiraat.networks.slimefun.network.NetworkController;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class NetworkProbe extends SpecialSlimefunItem implements CanCooldown {

    public static final String SPACES = " ".repeat(32);
    private static final MessageFormat MESSAGE_FORMAT = new MessageFormat("{0}{1}: {2}{3}", Locale.ROOT);

    public NetworkProbe(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @SuppressWarnings("deprecation")
    private static void displayToPlayer(@NotNull Block block, @NotNull Player player) {
        final NetworkRoot root = NetworkController.getNetworks().get(block.getLocation());
        if (root != null) {
            final int bridges = root.getBridges().size();
            final int monitors = root.getMonitors().size();
            final int importers = root.getImporters().size();
            final int exporters = root.getExporters().size();
            final int grids = root.getGrids().size();
            final int cells = root.getCells().size();
            final int grabbers = root.getGrabbers().size();
            final int pushers = root.getPushers().size();
            final int cutters = root.getCutters().size();
            final int pasters = root.getPasters().size();
            final int vacuums = root.getVacuums().size();
            final int purgers = root.getPurgers().size();
            final int crafters = root.getCrafters().size();
            final int powerNodes = root.getPowerNodes().size();
            final int powerDisplays = root.getPowerDisplays().size();
            final int encoders = root.getEncoders().size();
            final int wirelessTransmitters = root.getWirelessTransmitters().size();
            final int wirelessReceivers = root.getWirelessReceivers().size();
            final int powerOutlets = root.getPowerOutlets().size();
            final int greedyBlocks = root.getGreedyBlocks().size();

            final int advancedImporters = root.getAdvancedImporters().size();
            final int advancedExporters = root.getAdvancedExporters().size();
            final int advancedGreedyBlocks = root.getAdvancedGreedyBlocks().size();
            final int advancedPurgers = root.getAdvancedPurgers().size();
            final int advancedVacuums = root.getAdvancedVacuums().size();
            final int transferPushers = root.getTransferPushers().size();
            final int transferGrabbers = root.getTransferGrabbers().size();
            final int transfers = root.getTransfers().size();
            final int lineTransferVanillaPushers =
                root.getLineTransferVanillaPushers().size();
            final int lineTransferVanillaGrabbers =
                root.getLineTransferVanillaGrabbers().size();
            final int inputOnlyMonitor = root.getInputOnlyMonitors().size();
            final int outputOnlyMonitor = root.getOutputOnlyMonitors().size();
            final int linePowerOutlets = root.getLinePowerOutlets().size();
            final int decoders = root.getDecoders().size();
            final int quantumManagers = root.getQuantumManagers().size();
            final int drawerManagers = root.getDrawerManagers().size();
            final int crafterManagers = root.getCrafterManagers().size();
            final int itemFlowViewers = root.getItemFlowViewers().size();
            final int advancedWirelessTransmitters = root.getAdvancedWirelessTransmitters().size();
            final int aeSwitchers = root.getAeSwitchers().size();

            final Map<ItemStack, Long> allNetworkItems = root.getAllNetworkItemsLongType();
            final int distinctItems = allNetworkItems.size();

            long totalItems = allNetworkItems.values().stream()
                .mapToLong(integer -> integer)
                .sum();

            final String nodeCount = root.getNodeCount() >= root.getMaxNodes()
                ? Theme.ERROR + String.valueOf(root.getNodeCount()) + "+"
                : String.valueOf(root.getNodeCount());

            final ChatColor c = Theme.CLICK_INFO.getColor();
            final ChatColor p = Theme.SUCCESS.getColor();

            player.sendMessage(Lang.getString("messages.completed-operation.probe.split"));
            player.sendMessage(Lang.getString("messages.completed-operation.probe.networks_title"));
            player.sendMessage(Lang.getString("messages.completed-operation.probe.split"));

            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_BRIDGE.getDisplayName(), bridges));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_MONITOR.getDisplayName(), monitors));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_IMPORT.getDisplayName(), importers));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_EXPORT.getDisplayName(), exporters));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_GRID.getDisplayName(), grids));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_CELL.getDisplayName(), cells));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_GRABBER.getDisplayName(), grabbers));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_PUSHER.getDisplayName(), pushers));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_PURGER.getDisplayName(), purgers));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_AUTO_CRAFTER.getDisplayName(), crafters));
            player.sendMessage(formatter(
                stringOrSpaces(NetworksSlimefunItemStacks.NETWORK_CAPACITOR_1.getDisplayName())
                    .substring(0, 4),
                powerNodes));
            player.sendMessage(
                formatter(NetworksSlimefunItemStacks.NETWORK_POWER_DISPLAY.getDisplayName(), powerDisplays));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_RECIPE_ENCODER.getDisplayName(), encoders));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_CONTROL_X.getDisplayName(), cutters));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_CONTROL_V.getDisplayName(), pasters));
            player.sendMessage(formatter(NetworksSlimefunItemStacks.NETWORK_VACUUM.getDisplayName(), vacuums));
            player.sendMessage(formatter(
                NetworksSlimefunItemStacks.NETWORK_WIRELESS_TRANSMITTER.getDisplayName(), wirelessTransmitters));
            player.sendMessage(formatter(
                NetworksSlimefunItemStacks.NETWORK_WIRELESS_RECEIVER.getDisplayName(), wirelessReceivers));
            player.sendMessage(formatter(
                stringOrSpaces(NetworksSlimefunItemStacks.NETWORK_POWER_OUTLET_1.getDisplayName())
                    .substring(0, 4),
                powerOutlets));
            player.sendMessage(
                formatter(NetworksSlimefunItemStacks.NETWORK_GREEDY_BLOCK.getDisplayName(), greedyBlocks));

            player.sendMessage(Lang.getString("messages.completed-operation.probe.split"));
            player.sendMessage(Lang.getString("messages.completed-operation.probe.expansion_title"));
            player.sendMessage(Lang.getString("messages.completed-operation.probe.split"));
            player.sendMessage(formatter(ExpansionItemStacks.ADVANCED_IMPORT.getDisplayName(), advancedImporters));
            player.sendMessage(formatter(ExpansionItemStacks.ADVANCED_EXPORT.getDisplayName(), advancedExporters));
            player.sendMessage(
                formatter(ExpansionItemStacks.ADVANCED_GREEDY_BLOCK.getDisplayName(), advancedGreedyBlocks));
            player.sendMessage(formatter(ExpansionItemStacks.ADVANCED_PURGER.getDisplayName(), advancedPurgers));
            player.sendMessage(formatter(ExpansionItemStacks.ADVANCED_VACUUM.getDisplayName(), advancedVacuums));
            player.sendMessage(formatter(ExpansionItemStacks.TRANSFER.getDisplayName(), transfers));
            player.sendMessage(formatter(ExpansionItemStacks.TRANSFER_GRABBER.getDisplayName(), transferGrabbers));
            player.sendMessage(formatter(ExpansionItemStacks.TRANSFER_PUSHER.getDisplayName(), transferPushers));
            player.sendMessage(formatter(
                ExpansionItemStacks.LINE_TRANSFER_VANILLA_PUSHER.getDisplayName(), lineTransferVanillaPushers));
            player.sendMessage(formatter(
                ExpansionItemStacks.LINE_TRANSFER_VANILLA_GRABBER.getDisplayName(), lineTransferVanillaGrabbers));
            player.sendMessage(
                formatter(ExpansionItemStacks.NETWORK_INPUT_ONLY_MONITOR.getDisplayName(), inputOnlyMonitor));
            player.sendMessage(
                formatter(ExpansionItemStacks.NETWORK_OUTPUT_ONLY_MONITOR.getDisplayName(), outputOnlyMonitor));
            player.sendMessage(formatter(
                stringOrSpaces(ExpansionItemStacks.LINE_POWER_OUTLET_1.getDisplayName())
                    .substring(0, 6),
                linePowerOutlets));
            player.sendMessage(formatter(ExpansionItemStacks.NETWORK_BLUEPRINT_DECODER.getDisplayName(), decoders));
            player.sendMessage(formatter(ExpansionItemStacks.QUANTUM_MANAGER.getDisplayName(), quantumManagers));
            player.sendMessage(formatter(ExpansionItemStacks.DRAWER_MANAGER.getDisplayName(), drawerManagers));
            player.sendMessage(formatter(ExpansionItemStacks.CRAFTER_MANAGER.getDisplayName(), crafterManagers));
            player.sendMessage(formatter(ExpansionItemStacks.ITEM_FLOW_VIEWER.getDisplayName(), itemFlowViewers));
            player.sendMessage(formatter(ExpansionItemStacks.ADVANCED_WIRELESS_TRANSMITTER.getDisplayName(), advancedWirelessTransmitters));
            player.sendMessage(formatter(Lang.getString("icons.ae_switcher.name"), aeSwitchers));
            player.sendMessage(Lang.getString("messages.completed-operation.probe.split"));
            player.sendMessage(
                formatter(Lang.getString("messages.completed-operation.probe.distinct_items"), distinctItems));
            player.sendMessage(formatter(Lang.getString("messages.completed-operation.probe.total_items"), totalItems));
            player.sendMessage(Lang.getString("messages.completed-operation.probe.split"));
            player.sendMessage(String.format(
                Lang.getString("messages.completed-operation.probe.total_nodes"), nodeCount, root.getMaxNodes()));
            if (root.isOverburdened()) {
                player.sendMessage(Lang.getString("messages.completed-operation.probe.overburdened"));
            }
        }
    }

    public static @NotNull String formatter(String name, long count) {
        return MESSAGE_FORMAT
            .format(
                new Object[]{Theme.CLICK_INFO.getColor(), name, Theme.SUCCESS.getColor(), count},
                new StringBuffer(),
                null)
            .toString();
    }

    public static @NotNull String formatter(String name, String s) {
        return MESSAGE_FORMAT
            .format(
                new Object[]{Theme.CLICK_INFO.getColor(), name, Theme.SUCCESS.getColor(), s},
                new StringBuffer(),
                null)
            .toString();
    }

    public static @NotNull String stringOrSpaces(@Nullable String s) {
        return s == null ? SPACES : s;
    }

    @Override
    public void preRegister() {
        addItemHandler((ItemUseHandler) this::onUse);
    }

    protected void onUse(@NotNull PlayerRightClickEvent e) {
        final Optional<Block> optional = e.getClickedBlock();
        if (optional.isPresent()) {
            final Block block = optional.get();
            final Player player = e.getPlayer();
            if (canBeUsed(player, e.getItem())) {
                SlimefunBlockData blockData = StorageCacheUtils.getBlock(block.getLocation());
                if (blockData == null) {
                    return;
                }

                SlimefunItem slimefunItem = SlimefunItem.getById(blockData.getSfId());
                if (slimefunItem instanceof NetworkController) {
                    e.cancel();
                    displayToPlayer(block, player);
                    putOnCooldown(e.getItem());
                }
            }
        }
    }

    @Override
    public int cooldownDuration() {
        return 10;
    }
}
