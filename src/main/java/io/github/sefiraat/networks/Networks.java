package io.github.sefiraat.networks;

import com.balugaq.netex.api.data.ItemFlowRecord;
import com.balugaq.netex.api.enums.MinecraftVersion;
import com.balugaq.netex.utils.Debug;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.managers.ConfigManager;
import com.ytdd9527.networksexpansion.core.services.LocalizationService;
import com.ytdd9527.networksexpansion.setup.SetupUtil;
import com.ytdd9527.networksexpansion.utils.databases.DataSource;
import com.ytdd9527.networksexpansion.utils.databases.DataStorage;
import com.ytdd9527.networksexpansion.utils.databases.QueryQueue;
import io.github.sefiraat.networks.commands.NetworksMain;
import io.github.sefiraat.networks.integrations.HudCallbacks;
import io.github.sefiraat.networks.integrations.NetheoPlants;
import io.github.sefiraat.networks.managers.ListenerManager;
import io.github.sefiraat.networks.managers.SupportedPluginManager;
import io.github.sefiraat.networks.slimefun.NetworksSlimefunItemStacks;
import io.github.sefiraat.networks.slimefun.network.AdminDebuggable;
import io.github.sefiraat.networks.slimefun.network.NetworkController;
import io.github.sefiraat.networks.utils.NetworkUtils;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import lombok.Getter;
import net.guizhanss.guizhanlibplugin.updater.GuizhanUpdater;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.error.YAMLException;

public class Networks extends JavaPlugin implements SlimefunAddon {
    private static final String DEFAULT_LANGUAGE = "zh-CN";
    private static Networks instance;

    @Getter
    private static DataSource dataSource;

    @Getter
    private static QueryQueue queryQueue;

    @Getter
    private static BukkitRunnable autoSaveThread;

    private static MinecraftVersion minecraftVersion = MinecraftVersion.UNKNOWN;
    private final @NotNull String username;
    private final @NotNull String repo;
    private final @NotNull String branch;
    private ConfigManager configManager;
    private ListenerManager listenerManager;
    private SupportedPluginManager supportedPluginManager;
    private LocalizationService localizationService;
    private long slimefunTickCount;

    public Networks() {
        this.username = "ytdd9527";
        this.repo = "NetworksExpansion";
        this.branch = "master";
    }

    public static ConfigManager getConfigManager() {
        return Networks.getInstance().configManager;
    }

    public static Networks getInstance() {
        return Networks.instance;
    }

    @NotNull public static PluginManager getPluginManager() {
        return Networks.getInstance().getServer().getPluginManager();
    }

    public static SupportedPluginManager getSupportedPluginManager() {
        return Networks.getInstance().supportedPluginManager;
    }

    public static LocalizationService getLocalizationService() {
        return Networks.getInstance().localizationService;
    }

    public static ListenerManager getListenerManager() {
        return Networks.getInstance().listenerManager;
    }

    public static long getSlimefunTickCount() {
        return getInstance().slimefunTickCount;
    }

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("Loading language");
        this.configManager = new ConfigManager();
        localizationService = new LocalizationService(this);
        String language = configManager.getLanguage();
        try {
            localizationService.addLanguage(language);
            getLogger().info("Language " + language + " loaded successfully.");
        } catch (ClassCastException | IllegalArgumentException | YAMLException e) {
            getLogger().log(Level.WARNING, "Failed to load language " + language, e);
        }

        localizationService.addLanguage(DEFAULT_LANGUAGE);
        getLogger().info("Default language " + DEFAULT_LANGUAGE + " loaded successfully.");

        superHead();
        environmentCheck();
        getLogger().info(getLocalizationService().getString("messages.startup.loaded-language"));
        getLogger().info(getLocalizationService().getString("messages.startup.getting-config"));
        saveDefaultConfig();

        getLogger().info(getLocalizationService().getString("messages.startup.trying-auto-update"));
        tryUpdate();

        this.supportedPluginManager = new SupportedPluginManager();

        // Try connect database
        getLogger().info(getLocalizationService().getString("messages.startup.connecting-database"));
        try {
            dataSource = new DataSource();
        } catch (ClassNotFoundException | SQLException e) {
            getLogger().warning(getLocalizationService().getString("messages.startup.failed-to-connect-database"));
            Debug.trace(e);
            onDisable();
        }

        getLogger().info(getLocalizationService().getString("messages.startup.creating-query-queue"));
        queryQueue = new QueryQueue();
        queryQueue.startThread();

        getLogger().info(getLocalizationService().getString("messages.startup.creating-auto-save-thread"));
        autoSaveThread = new BukkitRunnable() {
            @Override
            public void run() {
                DataStorage.saveAmountChange();
            }
        };
        int seconds = getConfig().getInt("drawer-auto-save-period");
        seconds = seconds <= 0 ? 300 : seconds;
        long period = 20L * seconds;
        autoSaveThread.runTaskTimerAsynchronously(this, 2 * period, period);

        getLogger().info(getLocalizationService().getString("messages.startup.registering-items"));
        SetupUtil.setupAll();

        getLogger().info(getLocalizationService().getString("messages.startup.registering-listeners"));
        this.listenerManager = new ListenerManager();
        getLogger().info(getLocalizationService().getString("messages.startup.registering-commands"));
        PluginCommand c = this.getCommand("networks");
        if (c != null) {
            c.setExecutor(new NetworksMain());
        }

        setupMetrics();

        Bukkit.getScheduler()
                .runTaskTimer(
                        this,
                        () -> slimefunTickCount++,
                        1,
                        Slimefun.getTickerTask().getTickRate());

        // Fix dupe bug which break the network controller data without player interaction
        Bukkit.getScheduler()
                .runTaskTimer(
                        this,
                        () -> {
                            Set<Location> wrongs = new HashSet<>();
                            Set<Location> controllers = new HashSet<>(
                                    NetworkController.getNetworks().keySet());
                            for (Location controller : controllers) {
                                SlimefunBlockData data = StorageCacheUtils.getBlock(controller);
                                if (data == null
                                        || !NetworksSlimefunItemStacks.NETWORK_CONTROLLER
                                                .getItemId()
                                                .equals(data.getSfId())) {
                                    wrongs.add(controller);
                                }
                            }

                            for (Location wrong : wrongs) {
                                NetworkUtils.clearNetwork(wrong);
                            }
                        },
                        1,
                        Slimefun.getTickerTask().getTickRate());

        Bukkit.getScheduler()
                .runTaskTimer(
                        this,
                        () -> NetworkController.getRecords().values().forEach(ItemFlowRecord::gc),
                        1,
                        Slimefun.getTickerTask().getTickRate());

        AdminDebuggable.load();
        getLogger().info(getLocalizationService().getString("messages.startup.enabled-successfully"));
    }

    @Override
    public void onDisable() {
        getLogger().info(getLocalizationService().getString("messages.shutdown.saving-config"));
        this.configManager.saveAll();
        getLogger().info(getLocalizationService().getString("messages.shutdown.disconnecting-database"));
        if (autoSaveThread != null) {
            autoSaveThread.cancel();
        }
        DataStorage.saveAmountChange();
        if (queryQueue != null) {
            while (!queryQueue.isAllDone()) {
                getLogger()
                        .info(String.format(
                                getLocalizationService().getString("messages.shutdown.saving-data"),
                                queryQueue.getTaskAmount()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Debug.trace(e);
                }
            }
            queryQueue.scheduleAbort();
        }
        getLogger().info(getLocalizationService().getString("messages.shutdown.saved-all-data"));
        getLogger().info(getLocalizationService().getString("messages.shutdown.disabled-successfully"));
    }

    @SuppressWarnings("deprecation")
    public void tryUpdate() {
        if (configManager.isAutoUpdate() && getDescription().getVersion().startsWith("Build")) {
            GuizhanUpdater.start(this, getFile(), username, repo, branch);
        }
    }

    public void superHead() {
        List<String> superHead = getLocalizationService().getStringList("messages.super-head");
        for (String line : superHead) {
            getLogger().info(line);
        }
    }

    public void environmentCheck() {
        if (!getServer().getPluginManager().isPluginEnabled("GuizhanLibPlugin")) {
            getLogger().log(Level.SEVERE, getLocalizationService().getString("messages.depend.not-found-guizhanlib"));
            getLogger()
                    .log(
                            Level.SEVERE,
                            getLocalizationService().getString("messages.depend.suggest-download-guizhanlib"));
            return;
        }
        try {
            minecraftVersion = Slimefun.getMinecraftVersion()
                            .isAtLeast(io.github.thebusybiscuit.slimefun4.api.MinecraftVersion.MINECRAFT_1_20)
                    ? MinecraftVersion.of(20, 0)
                    : MinecraftVersion.UNKNOWN;
            minecraftVersion = Slimefun.getMinecraftVersion()
                            .isAtLeast(io.github.thebusybiscuit.slimefun4.api.MinecraftVersion.MINECRAFT_1_20_5)
                    ? MinecraftVersion.of(20, 5)
                    : minecraftVersion;
            minecraftVersion = Slimefun.getMinecraftVersion()
                            .isAtLeast(io.github.thebusybiscuit.slimefun4.api.MinecraftVersion.MINECRAFT_1_21)
                    ? MinecraftVersion.of(21, 0)
                    : minecraftVersion;
        } catch (NoClassDefFoundError | NoSuchFieldError e) {
            for (int i = 0; i < 20; i++) {
                getLogger()
                        .severe(getLocalizationService().getString("messages.depend.suggest-download-newer-slimefun"));
            }
        }

        if (minecraftVersion == MinecraftVersion.UNKNOWN) {
            final int major = PaperLib.getMinecraftVersion();
            final int minor = PaperLib.getMinecraftPatchVersion();
            minecraftVersion = MinecraftVersion.of(major, minor);
        }
    }

    public void setupIntegrations() {
        if (supportedPluginManager.isSlimeHud()) {
            getLogger().info(getLocalizationService().getString("messages.integrations.found-slimehud"));
            try {
                HudCallbacks.setup();
            } catch (NoClassDefFoundError e) {
                getLogger().warning(getLocalizationService().getString("messages.integrations.not-found-slimehud"));
            }
        }
        if (supportedPluginManager.isNetheopoiesis()) {
            getLogger().info(getLocalizationService().getString("messages.integrations.found-netheopoiesis"));
            try {
                NetheoPlants.setup();
            } catch (NoClassDefFoundError e) {
                getLogger()
                        .warning(getLocalizationService().getString("messages.integrations.not-found-netheopoiesis"));
            }
        }
    }

    public MinecraftVersion getMCVersion() {
        return minecraftVersion;
    }

    public void setupMetrics() {
        final Metrics metrics = new Metrics(this, 13644);

        AdvancedPie networksChart = new AdvancedPie("networks", () -> {
            Map<String, Integer> networksMap = new HashMap<>();
            networksMap.put(
                    "Number of networks", NetworkController.getNetworks().size());
            return networksMap;
        });

        metrics.addCustomChart(networksChart);
    }

    @NotNull @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Nullable @Override
    public String getBugTrackerURL() {
        return MessageFormat.format("https://github.com/{0}/{1}/issues/", this.username, this.repo);
    }

    @NotNull public String getWikiURL() {
        return MessageFormat.format(
                "https://slimefun-addons-wiki.guizhanss.cn/networks/{0}/{1}", this.username, this.repo);
    }

    public void debug(String message) {
        if (getConfigManager().isDebug()) {
            getLogger().warning("[DEBUG] " + message);
        }
    }
}
