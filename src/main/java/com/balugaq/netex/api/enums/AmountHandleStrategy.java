package com.balugaq.netex.api.enums;

import com.balugaq.netex.api.algorithm.Calculator;
import com.balugaq.netex.api.interfaces.consumers.Consumer4;
import com.balugaq.netex.core.guide.GridNewStyleCustomAmountGuideOption;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.balugaq.netex.core.guide.GridNewStyleCustomAmountGuideOption.GRID_NEW_STYLE_MAX_CUSTOM_AMOUNT;

@NullMarked
public enum AmountHandleStrategy {
    ONE((player, itemStack) -> 1),
    STACK((player, itemStack) -> itemStack.getMaxStackSize()),
    CUSTOM((player, itemStack) -> GridNewStyleCustomAmountGuideOption.get(player)),
    ASK((location, player, itemStack, consumer) -> ChatUtils.awaitInput(player, input -> {
        player.sendMessage(ChatColors.color("&e输入取出数量"));
        try {
            int value = Calculator.calculate(input).intValue();
            if (value <= 0 || value >= GRID_NEW_STYLE_MAX_CUSTOM_AMOUNT) {
                player.sendMessage("请输入 1 ~ " + GRID_NEW_STYLE_MAX_CUSTOM_AMOUNT + " 之间的正整数");

                BlockMenu menu = StorageCacheUtils.getMenu(location);
                if (menu != null) {
                    menu.open(player);
                }

                return;
            }

            consumer.accept(value);
        } catch (NumberFormatException e) {
            player.sendMessage("请输入 1 ~ " + GRID_NEW_STYLE_MAX_CUSTOM_AMOUNT + " 之间的正整数");
            player.sendMessage(e.getMessage());
        }
    }));

    private final Consumer4<Location, Player, ItemStack, Consumer<Integer>> consumer;

    AmountHandleStrategy(BiFunction<Player, ItemStack, Integer> function) {
        this.consumer = (location, player, itemStack, consumer) -> consumer.accept(function.apply(player, itemStack));
    }

    AmountHandleStrategy(Consumer4<Location, Player, ItemStack, Consumer<Integer>> consumer) {
        this.consumer = consumer;
    }

    public void handle(Location location, Player player, ItemStack itemStack, Consumer<Integer> consumer) {
        this.consumer.accept(location, player, itemStack, consumer);
    }
}