package io.github.sefiraat.networks.integrations;

import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.implementation.machines.networks.advanced.AdvancedGreedyBlock;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.schntgaispock.slimehud.SlimeHUD;
import io.github.schntgaispock.slimehud.util.HudBuilder;
import io.github.schntgaispock.slimehud.waila.HudController;
import io.github.sefiraat.networks.network.stackcaches.QuantumCache;
import io.github.sefiraat.networks.slimefun.network.NetworkGreedyBlock;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HudCallbacks {

    private static final String EMPTY = Lang.getString("messages.integrations.slimehud.empty_quantum_storage");

    public static void setup() {
        HudController controller = SlimeHUD.getHudController();

        controller.registerCustomHandler(NetworkQuantumStorage.class, request -> {
            Location location = request.getLocation();
            QuantumCache cache = NetworkQuantumStorage.getCaches().get(location);
            if (cache == null || cache.getItemStack() == null) {
                return EMPTY;
            }

            return format(cache.getItemStack(), cache.getAmount(), cache.getLimit());
        });

        controller.registerCustomHandler(NetworkGreedyBlock.class, request -> {
            Location location = request.getLocation();
            BlockMenu menu = StorageCacheUtils.getMenu(location);
            if (menu == null) {
                return EMPTY;
            }

            ItemStack templateStack = menu.getItemInSlot(NetworkGreedyBlock.TEMPLATE_SLOT);
            if (templateStack == null || templateStack.getType() == Material.AIR) {
                return EMPTY;
            }

            ItemStack itemStack = menu.getItemInSlot(NetworkGreedyBlock.INPUT_SLOT);
            // Only check type to improve performance
            int amount =
                    itemStack == null || itemStack.getType() != templateStack.getType() ? 0 : itemStack.getAmount();
            return format(templateStack, amount, templateStack.getMaxStackSize());
        });

        controller.registerCustomHandler(AdvancedGreedyBlock.class, request -> {
            Location location = request.getLocation();
            BlockMenu menu = StorageCacheUtils.getMenu(location);
            if (menu == null) {
                return EMPTY;
            }

            ItemStack templateStack = menu.getItemInSlot(AdvancedGreedyBlock.TEMPLATE_SLOT);
            if (templateStack == null || templateStack.getType() == Material.AIR) {
                return EMPTY;
            }

            int amount = 0;
            for (int i : AdvancedGreedyBlock.INPUT_SLOTS) {
                ItemStack itemStack = menu.getItemInSlot(i);
                // Only check type to improve performance
                if (itemStack.getType() == templateStack.getType()) {
                    amount += itemStack.getAmount();
                }
            }

            return format(templateStack, amount, templateStack.getMaxStackSize());
        });
    }

    private static @NotNull String format(@NotNull ItemStack itemStack, long amount, int limit) {
        String amountStr = HudBuilder.getAbbreviatedNumber(amount);
        String limitStr = HudBuilder.getAbbreviatedNumber(limit);
        String itemName = ItemStackHelper.getDisplayName(itemStack);

        return TextUtil.GRAY + "| " + TextUtil.WHITE + itemName + " " + TextUtil.GRAY + "| " + amountStr + "/"
                + limitStr;
    }
}
