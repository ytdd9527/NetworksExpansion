package io.github.sefiraat.networks.managers;

import com.balugaq.netex.integrations.logitech.LogitechIntegration;
import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.google.common.base.Preconditions;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import io.github.sefiraat.networks.Networks;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SupportedPluginManager {

    @Getter
    private static SupportedPluginManager instance;
    private @Nullable @Getter LogitechIntegration logitechIntegration = null;

    private final @Getter boolean infinityExpansion;
    private final @Getter boolean tsingshanTechnology;
    private final @Getter boolean fluffyMachines;
    private final @Getter boolean netheopoiesis;
    private final @Getter boolean slimeHud;
    private final @Getter boolean roseStacker;
    private final @Getter boolean wildStacker;
    private final @Getter boolean guguSlimefunLib;
    private final @Getter boolean simpleStorage;
    private @Getter RoseStackerAPI roseStackerAPI;
    private final @Getter boolean finalTECH;
    @Setter
    private @Getter boolean justEnoughGuide;

    // region First Tick Only Registrations
    private @Getter boolean mcMMO;
    private @Getter boolean wildChests;
    private @Getter boolean logitech;

    // endregion

    public SupportedPluginManager() {
        Preconditions.checkArgument(instance == null, "Cannot instantiate class");
        instance = this;
        this.infinityExpansion = Bukkit.getPluginManager().isPluginEnabled("InfinityExpansion");
        this.tsingshanTechnology =
            Bukkit.getPluginManager().isPluginEnabled("TsingshanTechnology-Fixed")
                || Bukkit.getPluginManager().isPluginEnabled("TsingshanTechnology");
        this.fluffyMachines = Bukkit.getPluginManager().isPluginEnabled("FluffyMachines");
        this.netheopoiesis = Bukkit.getPluginManager().isPluginEnabled("Netheopoiesis");
        this.slimeHud = Bukkit.getPluginManager().isPluginEnabled("SlimeHUD") || Bukkit.getPluginManager().isPluginEnabled("SlimeHUDPlus");

        this.roseStacker = Bukkit.getPluginManager().isPluginEnabled("RoseStacker");
        if (roseStacker) {
            this.roseStackerAPI = RoseStackerAPI.getInstance();
        }

        this.wildStacker = Bukkit.getPluginManager().isPluginEnabled("WildStacker");
        this.guguSlimefunLib = Bukkit.getPluginManager().isPluginEnabled("GuguSlimefunLib");
        this.simpleStorage = Bukkit.getPluginManager().isPluginEnabled("SimpleStorage");
        this.finalTECH = Bukkit.getPluginManager().isPluginEnabled("FinalTECH") || Bukkit.getPluginManager().isPluginEnabled("FinalTECH-Changed");

        Networks.getInstance()
            .getServer()
            .getScheduler()
            .runTaskLater(Networks.getInstance(), this::firstTickRegistrations, 1);
    }

    public static int getStackAmount(@NotNull Item item) {
        if (getInstance().isWildStacker()) {
            return WildStackerAPI.getItemAmount(item);
        } else if (getInstance().isRoseStacker()) {
            dev.rosewood.rosestacker.stack.StackedItem stackedItem = getInstance().getRoseStackerAPI().getStackedItem(item);
            return stackedItem == null ? item.getItemStack().getAmount() : stackedItem.getStackSize();
        } else {
            return item.getItemStack().getAmount();
        }
    }

    public static void setStackAmount(@NotNull Item item, int amount) {
        if (getInstance().isWildStacker()) {
            com.bgsoftware.wildstacker.api.objects.StackedItem stackedItem = WildStackerAPI.getStackedItem(item);
            if (stackedItem != null) {
                stackedItem.setStackAmount(amount, true);
            }
        }

        if (getInstance().isRoseStacker()) {
            dev.rosewood.rosestacker.stack.StackedItem stackedItem = getInstance().getRoseStackerAPI().getStackedItem(item);
            if (stackedItem != null) {
                stackedItem.setStackSize(amount);
            }
        }

        if (!getInstance().isWildStacker() && !getInstance().isRoseStacker()) {
            item.getItemStack().setAmount(amount);
        }
    }

    private void firstTickRegistrations() {
        this.wildChests = Bukkit.getPluginManager().isPluginEnabled("WildChests");
        this.mcMMO = Bukkit.getPluginManager().isPluginEnabled("mcMMO");
        this.justEnoughGuide = Bukkit.getPluginManager().isPluginEnabled("JustEnoughGuide");
        if (this.justEnoughGuide) {
            try {
                Class.forName("com.balugaq.jeg.api.objects.events.GuideEvents");
            } catch (ClassNotFoundException ignored) {
                this.justEnoughGuide = false;
            }
        }
        this.logitech = Bukkit.getPluginManager().isPluginEnabled("LogiTech");
        if (this.logitech) {
            logitechIntegration = new LogitechIntegration();
        }
    }
}
