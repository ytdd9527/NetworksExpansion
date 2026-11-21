package io.github.sefiraat.networks;

import com.balugaq.bim.BIMLoader;
import com.balugaq.netex.api.algorithm.ID;
import com.balugaq.netex.api.data.ItemFlowRecord;
import com.balugaq.netex.api.enums.MinecraftVersion;
import com.balugaq.netex.api.keybind.Keybinds;
import com.balugaq.netex.core.guide.GridNewStyleCustomAmountGuideOption;
import com.balugaq.netex.utils.Debug;
import com.balugaq.netex.utils.Scheduling;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.managers.ConfigManager;
import com.ytdd9527.networksexpansion.core.services.LocalizationService;
import com.ytdd9527.networksexpansion.implementation.machines.drive.CellDrive;
import com.ytdd9527.networksexpansion.api.storage.CellStorageDatabase;
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
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import lombok.Getter;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;
import net.guizhanss.guizhanlibplugin.updater.GuizhanUpdater;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.error.YAMLException;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class Networks extends JavaPlugin implements SlimefunAddon {
    private static final String DEFAULT_LANGUAGE = "zh-CN";
    private static Networks instance;

    @Getter
    private static DataSource dataSource;

    @Getter
    private static QueryQueue queryQueue;

    @Getter
    private static WrappedTask autoSaveThread;

    @Getter
    private static WrappedTask aeAutoSaveThread;

    @Getter
    private static CellStorageDatabase cellStorageDatabase;

    private static MinecraftVersion minecraftVersion = MinecraftVersion.UNKNOWN;
    private final @NotNull String username;
    private final @NotNull String repo;
    private final @NotNull String branch;
    private ConfigManager configManager;
    private ListenerManager listenerManager;
    private SupportedPluginManager supportedPluginManager;
    private LocalizationService localizationService;
    private BIMLoader bimLoader;
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

    @NotNull
    public static PluginManager getPluginManager() {
        return getInstance().getServer().getPluginManager();
    }

    public static SupportedPluginManager getSupportedPluginManager() {
        return getInstance().supportedPluginManager;
    }

    public static LocalizationService getLocalizationService() {
        return getInstance().localizationService;
    }

    public static ListenerManager getListenerManager() {
        return getInstance().listenerManager;
    }

    public static BIMLoader getBIMLoader() {
        return getInstance().bimLoader;
    }

    public static long getSlimefunTickCount() {
        return getInstance().slimefunTickCount;
    }

    @Override
    public void onEnable() {
        instance = this;
        Scheduling.initialize(this);

        loadLibraries();

        getLogger().info("Loading language");
        this.configManager = new ConfigManager();
        this.localizationService = new LocalizationService(this);
        String language = configManager.getLanguage();
        try {
            localizationService.addLanguage(language);
            getLogger().info("Language " + language + " loaded successfully.");
        } catch (ClassCastException | IllegalArgumentException | YAMLException e) {
            getLogger().log(Level.WARNING, "Failed to load language " + language, e);
        }

        localizationService.addDefaultLanguage(DEFAULT_LANGUAGE);
        getLogger().info("Default language " + DEFAULT_LANGUAGE + " loaded successfully.");

        superHead();
        environmentCheck();
        getLogger().info(getLocalizationService().getString("messages.startup.loaded-language"));
        getLogger().info(getLocalizationService().getString("messages.startup.getting-config"));
        saveDefaultConfig();

        getLogger().info(getLocalizationService().getString("messages.startup.trying-auto-update"));
        tryUpdate();

        this.bimLoader = new BIMLoader(this);

        this.supportedPluginManager = new SupportedPluginManager();

        // Try to connect database
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
        int seconds = getConfig().getInt("drawer-auto-save-period");
        seconds = seconds <= 0 ? 300 : seconds;
        long period = 20L * seconds;
        autoSaveThread = Scheduling.runTimerAsync(DataStorage::saveAmountChange, 2 * period, period);

        // 元件驱动器元件数据自动保存
        aeAutoSaveThread = Scheduling.runTimerAsync(CellDrive::saveAllDriveCells, 2 * period, period);

        getLogger().info(getLocalizationService().getString("messages.startup.initializing-cell-database"));
        try {
            cellStorageDatabase = new CellStorageDatabase();
            cellStorageDatabase.init();
        } catch (Exception e) {
            cellStorageDatabase = null;
            getLogger().warning(getLocalizationService().getString("messages.startup.failed-to-init-cell-database"));
            Debug.trace(e);
        }

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

        Scheduling.runTimer(() -> slimefunTickCount++, 1, Slimefun.getTickerTask().getTickRate());

        // Fix dupe bug where player breaks the network controller data without player interaction
        Scheduling.runTimer(() -> {
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
        }, 1, Slimefun.getTickerTask().getTickRate());

        Scheduling.runTimer(
            () -> NetworkController.getRecords().values().forEach(ItemFlowRecord::gc),
            1,
            Slimefun.getTickerTask().getTickRate());

        AdminDebuggable.load();
        SlimefunGuideSettings.addOption(GridNewStyleCustomAmountGuideOption.instance());
        Scheduling.runLaterAsync(Keybinds::distinctAll, 1L);
        ID.fetchId();
        Keybinds.fetchScripts();
        getLogger().info(getLocalizationService().getString("messages.startup.enabled-successfully"));
    }

    @Override
    public void onDisable() {
        try {
            getLogger().info(getLocalizationService().getString("messages.shutdown.saving-config"));
            ID.saveId();
            this.configManager.saveAll();
            getLogger().info(getLocalizationService().getString("messages.shutdown.disconnecting-database"));
        } catch (Exception e) {
            Debug.trace(e);
        }

        Scheduling.cancel(autoSaveThread);
        Scheduling.cancel(aeAutoSaveThread);
        DataStorage.saveAmountChange();
        CellDrive.saveAllDriveCells();
        if (cellStorageDatabase != null) {
            cellStorageDatabase.shutdown();
        }
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
            minecraftVersion = MinecraftVersion.current();
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

    private void loadLibraries() {
        LibraryManager libraryManager = new BukkitLibraryManager(this);
        libraryManager.addMavenCentral();

        getLogger().info("正在加载 Pinyin");
        Library pinyin = Library.builder()
            .groupId("com{}github{}houbb")
            .artifactId("pinyin")
            .version("0.4.0")
            .build();
        libraryManager.loadLibrary(pinyin);

        getLogger().info("正在加载 opencc4j");
        Library opencc4j = Library.builder()
            .groupId("com{}github{}houbb")
            .artifactId("opencc4j")
            .version("1.14.0")
            .build();
        libraryManager.loadLibrary(opencc4j);

        getLogger().info("正在加载 heaven");
        Library heaven = Library.builder()
            .groupId("com{}github{}houbb")
            .artifactId("heaven")
            .version("0.13.0")
            .build();
        libraryManager.loadLibrary(heaven);

        getLogger().info("正在加载 nlp-common");
        Library nlp = Library.builder()
            .groupId("com{}github{}houbb")
            .artifactId("nlp-common")
            .version("0.0.5")
            .build();
        libraryManager.loadLibrary(nlp);
    }

    public MinecraftVersion getMinecraftVersion() {
        return minecraftVersion;
    }

    public void setupMetrics() {
        final Metrics metrics = new Metrics(this, 34492);

        AdvancedPie networksChart = new AdvancedPie("networks", () -> {
            Map<String, Integer> networksMap = new HashMap<>();
            networksMap.put(
                "Number of networks", NetworkController.getNetworks().size());
            return networksMap;
        });

        metrics.addCustomChart(networksChart);
    }

    @NotNull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Nullable
    @Override
    public String getBugTrackerURL() {
        return MessageFormat.format("https://github.com/{0}/{1}/issues/", this.username, this.repo);
    }

    @NotNull
    public String getWikiURL() {
        return MessageFormat.format(
            "https://slimefun-addons-wiki.guizhanss.cn/networks/{0}/{1}", this.username, this.repo);
    }

    public void debug(String message) {
        if (getConfigManager().isDebug()) {
            getLogger().warning("[DEBUG] " + message);
        }
    }
}
