package io.github.sefiraat.networks;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.balugaq.netex.api.enums.MCVersion;
import com.ytdd9527.networksexpansion.core.managers.ConfigManager;
import com.balugaq.netex.api.guide.CheatGuideImpl;
import com.balugaq.netex.api.guide.SurvivalGuideImpl;
import com.ytdd9527.networksexpansion.core.services.LocalizationService;
import com.ytdd9527.networksexpansion.setup.SetupUtil;
import com.ytdd9527.networksexpansion.utils.ReflectionUtil;
import com.ytdd9527.networksexpansion.utils.databases.DataSource;
import com.ytdd9527.networksexpansion.utils.databases.DataStorage;
import com.ytdd9527.networksexpansion.utils.databases.QueryQueue;
import io.github.sefiraat.networks.commands.NetworksMain;
import io.github.sefiraat.networks.integrations.HudCallbacks;
import io.github.sefiraat.networks.integrations.NetheoPlants;
import io.github.sefiraat.networks.managers.ListenerManager;
import io.github.sefiraat.networks.managers.SupportedPluginManager;
import io.github.sefiraat.networks.slimefun.NetworksSlimefunItemStacks;
import io.github.sefiraat.networks.slimefun.network.NetworkController;
import io.github.sefiraat.networks.utils.NetworkUtils;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.CheatSheetSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import net.guizhanss.guizhanlibplugin.updater.GuizhanUpdater;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Networks extends JavaPlugin implements SlimefunAddon {
    private static Networks instance;
    private static DataSource dataSource;
    private static QueryQueue queryQueue;
    private static BukkitRunnable autoSaveThread;
    private static MCVersion mcVersion = MCVersion.UNKNOWN;
    private final String username;
    private final String repo;
    private final String branch;
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

    public static QueryQueue getQueryQueue() {
        return queryQueue;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static BukkitRunnable getAutoSaveThread() {
        return autoSaveThread;
    }

    public static Networks getInstance() {
        return Networks.instance;
    }

    @Nonnull
    public static PluginManager getPluginManager() {
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

        this.configManager = new ConfigManager();
        localizationService = new LocalizationService(this);
        localizationService.addLanguage(configManager.getLanguage());
        localizationService.addLanguage("zh-CN");

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
            e.printStackTrace();
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
        this.getCommand("networks").setExecutor(new NetworksMain());

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
                            for (Location controller : NetworkController.getNetworks().keySet()) {
                                SlimefunBlockData data = StorageCacheUtils.getBlock(controller);
                                if (data == null || !NetworksSlimefunItemStacks.NETWORK_CONTROLLER.getItemId().equals(data.getSfId())) {
                                    NetworkUtils.clearNetwork(controller);
                                }
                            }
                        },
                        1,
                        Slimefun.getTickerTask().getTickRate());

        final boolean survivalOverride = getConfig().getBoolean("integrations.guide.survival-override");
        final boolean cheatOverride = getConfig().getBoolean("integrations.guide.cheat-override");
        if (survivalOverride || cheatOverride) {
            getLogger().info(getLocalizationService().getString("messages.startup.found-enabled-replacing-guide"));
            getLogger().info(getLocalizationService().getString("messages.startup.replacing-guide"));
            Field field = ReflectionUtil.getField(Slimefun.getRegistry().getClass(), "guides");
            if (field != null) {
                field.setAccessible(true);

                Map<SlimefunGuideMode, SlimefunGuideImplementation> newGuides = new EnumMap<>(SlimefunGuideMode.class);
                newGuides.put(SlimefunGuideMode.SURVIVAL_MODE, survivalOverride ? new SurvivalGuideImpl() : new SurvivalSlimefunGuide());
                newGuides.put(SlimefunGuideMode.CHEAT_MODE, cheatOverride ? new CheatGuideImpl() : new CheatSheetSlimefunGuide());
                try {
                    field.set(Slimefun.getRegistry(), newGuides);
                } catch (IllegalAccessException ignored) {

                }
            }
            getLogger().info(survivalOverride ? getLocalizationService().getString("messages.startup.enabled-replacing-survival-guide") : getLocalizationService().getString("messages.startup.disabled-replacing-survival-guide"));
            getLogger().info(cheatOverride ? getLocalizationService().getString("messages.startup.enabled-replacing-cheat-guide") : getLocalizationService().getString("messages.startup.disabled-replacing-cheat-guide"));
            getLogger().info(getLocalizationService().getString("messages.startup.guide-risk-warning"));
        }


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
                getLogger().info(String.format(getLocalizationService().getString("messages.shutdown.saving-data"), queryQueue.getTaskAmount()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queryQueue.scheduleAbort();
        }
        getLogger().info(getLocalizationService().getString("messages.shutdown.saved-all-data"));
        getLogger().info(getLocalizationService().getString("messages.shutdown.disabled-successfully"));
    }

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
            getLogger().log(Level.SEVERE, getLocalizationService().getString("messages.depend.suggest-download-guizhanlib"));
            return;
        }
        try {
            mcVersion = Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_20) ? MCVersion.of(20, 0) : MCVersion.UNKNOWN;
            mcVersion = Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? MCVersion.of(20, 5) : mcVersion;
            mcVersion = Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_21) ? MCVersion.of(21, 0) : mcVersion;
        } catch (NoClassDefFoundError | NoSuchFieldError e) {
            for (int i = 0; i < 20; i++) {
                getLogger().severe(getLocalizationService().getString("messages.depend.suggest-download-newer-slimefun"));
            }
        }

        if (mcVersion == MCVersion.UNKNOWN) {
            final int major = PaperLib.getMinecraftVersion();
            final int minor = PaperLib.getMinecraftPatchVersion();
            mcVersion = MCVersion.of(major, minor);
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
                getLogger().warning(getLocalizationService().getString("messages.integrations.not-found-netheopoiesis"));
            }
        }
    }

    public MCVersion getMCVersion() {
        return mcVersion;
    }

    public void setupMetrics() {
        final Metrics metrics = new Metrics(this, 13644);

        AdvancedPie networksChart = new AdvancedPie("networks", () -> {
            Map<String, Integer> networksMap = new HashMap<>();
            networksMap.put("Number of networks", NetworkController.getNetworks().size());
            return networksMap;
        });

        metrics.addCustomChart(networksChart);
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Nullable
    @Override
    public String getBugTrackerURL() {
        return MessageFormat.format("https://github.com/{0}/{1}/issues/", this.username, this.repo);
    }

    @Nonnull
    public String getWikiURL() {
        return MessageFormat.format("https://slimefun-addons-wiki.guizhanss.cn/networks/{0}/{1}", this.username, this.repo);
    }
}