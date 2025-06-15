package io.github.sefiraat.networks.slimefun;

import com.balugaq.netex.utils.Lang;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.slimefun.groups.DummyItemGroup;
import io.github.sefiraat.networks.slimefun.groups.MainFlexGroup;
import io.github.sefiraat.networks.utils.Keys;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class NetworksItemGroups {

    public static final MainFlexGroup MAIN = new MainFlexGroup(
            Keys.newKey("main"),
            new CustomItemStack(new ItemStack(Material.BLACK_STAINED_GLASS), Lang.getString("groups.main")),
            0);

    public static final DummyItemGroup MATERIALS = new DummyItemGroup(
            Keys.newKey("materials"),
            new CustomItemStack(new ItemStack(Material.WHITE_STAINED_GLASS), Lang.getString("groups.materials")),
            0);

    public static final DummyItemGroup TOOLS = new DummyItemGroup(
            Keys.newKey("tools"),
            new CustomItemStack(new ItemStack(Material.PAINTING), Lang.getString("groups.tools")),
            0);

    public static final DummyItemGroup NETWORK_ITEMS = new DummyItemGroup(
            Keys.newKey("network_items"),
            new CustomItemStack(new ItemStack(Material.BLACK_STAINED_GLASS), Lang.getString("groups.network_items")),
            0);

    public static final DummyItemGroup NETWORK_QUANTUMS = new DummyItemGroup(
            Keys.newKey("network_quantums"),
            new CustomItemStack(new ItemStack(Material.WHITE_TERRACOTTA), Lang.getString("groups.network_quantums")),
            0);

    public static final ItemGroup DISABLED_ITEMS = new HiddenItemGroup(
            Keys.newKey("disabled_items"),
            new CustomItemStack(new ItemStack(Material.BARRIER), Lang.getString("groups.disabled_items")));

    static {
        final Networks plugin = Networks.getInstance();

        // Slimefun Registry
        NetworksItemGroups.MAIN.register(plugin);
        NetworksItemGroups.MATERIALS.register(plugin);
        NetworksItemGroups.TOOLS.register(plugin);
        NetworksItemGroups.NETWORK_ITEMS.register(plugin);
        NetworksItemGroups.NETWORK_QUANTUMS.register(plugin);
        NetworksItemGroups.DISABLED_ITEMS.register(plugin);
    }

    public static class HiddenItemGroup extends ItemGroup {

        public HiddenItemGroup(@NotNull NamespacedKey key, @NotNull ItemStack item) {
            super(key, item);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean isHidden(@NotNull Player p) {
            return true;
        }
    }
}
