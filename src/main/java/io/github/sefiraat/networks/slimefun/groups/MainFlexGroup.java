package io.github.sefiraat.networks.slimefun.groups;

import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.slimefun.NetworksItemGroups;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @noinspection deprecation
 */
public class MainFlexGroup extends FlexItemGroup {

    private static final ItemStack DOCS_ITEM_STACK =
            Theme.themedItemStack(Lang.getIcon("docs_icon", Material.BOOK), Theme.GUIDE);

    private static final int GUIDE_BACK = 1;
    private static final int DOCS = 9;
    private static final int MATERIALS = 10;
    private static final int TOOLS = 11;
    private static final int NETWORK_ITEMS = 12;
    private static final int NETWORK_QUANTUMS = 13;

    private static final int[] HEADER = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] FOOTER = new int[] {45, 46, 47, 48, 49, 50, 51, 52, 53};

    public MainFlexGroup(@NotNull NamespacedKey key, @NotNull ItemStack item, int tier) {
        super(key, item, tier);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isVisible(Player player, PlayerProfile playerProfile, SlimefunGuideMode guideMode) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void open(Player p, PlayerProfile profile, SlimefunGuideMode mode) {
        final ChestMenu chestMenu = new ChestMenu(Theme.MAIN.getColor() + "Networks");

        for (int slot : HEADER) {
            chestMenu.addItem(slot, ChestMenuUtils.getBackground(), (player1, i1, itemStack, clickAction) -> false);
        }

        for (int slot : FOOTER) {
            chestMenu.addItem(slot, ChestMenuUtils.getBackground(), (player1, i1, itemStack, clickAction) -> false);
        }

        chestMenu.setEmptySlotsClickable(false);
        setupPage(p, profile, mode, chestMenu);
        chestMenu.open(p);
    }

    @ParametersAreNonnullByDefault
    private void setupPage(Player player, PlayerProfile profile, SlimefunGuideMode mode, ChestMenu menu) {
        for (int slot : FOOTER) {
            menu.replaceExistingItem(slot, ChestMenuUtils.getBackground());
            menu.addMenuClickHandler(slot, ((player1, i, itemStack, clickAction) -> false));
        }

        // Sound
        menu.addMenuOpeningHandler((p) -> p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.0F));

        // Back
        menu.replaceExistingItem(
                GUIDE_BACK,
                ChestMenuUtils.getBackButton(
                        player, "", TextUtil.GRAY + Slimefun.getLocalization().getMessage(player, "guide.back.guide")));
        menu.addMenuClickHandler(GUIDE_BACK, (player1, slot, itemStack, clickAction) -> {
            SlimefunGuide.openMainMenu(profile, mode, 1);
            return false;
        });

        // Docs
        menu.replaceExistingItem(DOCS, DOCS_ITEM_STACK);
        menu.addMenuClickHandler(DOCS, (player1, i1, itemStack1, clickAction) -> {
            final TextComponent link = new TextComponent(Lang.getString("icons.docs_icon.click_to_visit_wiki"));
            link.setColor(ChatColor.YELLOW);
            link.setClickEvent(
                    new ClickEvent(ClickEvent.Action.OPEN_URL, "https://slimefun-addons-wiki.guizhanss.cn/networks/"));
            player.spigot().sendMessage(link);
            return false;
        });

        // Materials
        menu.replaceExistingItem(MATERIALS, NetworksItemGroups.MATERIALS.getItem(player));
        menu.addMenuClickHandler(
                MATERIALS,
                (player1, i1, itemStack1, clickAction) -> openPage(profile, NetworksItemGroups.MATERIALS, mode, 1));

        // Tools
        menu.replaceExistingItem(TOOLS, NetworksItemGroups.TOOLS.getItem(player));
        menu.addMenuClickHandler(
                TOOLS, (player1, i1, itemStack1, clickAction) -> openPage(profile, NetworksItemGroups.TOOLS, mode, 1));

        // Network Items
        menu.replaceExistingItem(NETWORK_ITEMS, NetworksItemGroups.NETWORK_ITEMS.getItem(player));
        menu.addMenuClickHandler(
                NETWORK_ITEMS,
                (player1, i1, itemStack1, clickAction) -> openPage(profile, NetworksItemGroups.NETWORK_ITEMS, mode, 1));

        // Network Quantums
        menu.replaceExistingItem(NETWORK_QUANTUMS, NetworksItemGroups.NETWORK_QUANTUMS.getItem(player));
        menu.addMenuClickHandler(
                NETWORK_QUANTUMS,
                (player1, i1, itemStack1, clickAction) ->
                        openPage(profile, NetworksItemGroups.NETWORK_QUANTUMS, mode, 1));
    }

    @ParametersAreNonnullByDefault
    public boolean openPage(PlayerProfile profile, ItemGroup itemGroup, SlimefunGuideMode mode, int page) {
        profile.getGuideHistory().add(this, 1);
        SlimefunGuide.openItemGroup(profile, itemGroup, mode, page);
        return false;
    }
}
