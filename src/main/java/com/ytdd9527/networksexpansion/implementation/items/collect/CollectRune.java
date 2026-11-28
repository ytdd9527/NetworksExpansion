package com.ytdd9527.networksexpansion.implementation.items.collect;

import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.core.services.CollectService;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemDropHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CollectRune extends SpecialSlimefunItem {

    private static final double RANGE = 1.5;

    public CollectRune(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        addItemHandler((ItemDropHandler) (PlayerDropItemEvent e, Player p, Item item) -> {
            if (!isItem(item.getItemStack())) {
                return false;
            }
            if (!canUse(p, true)) {
                return true;
            }
            Slimefun.runSync(() -> activate(p, item), 20L);
            return true;
        });
    }

    private void activate(@NotNull Player player, @NotNull Item rune) {
        if (!rune.isValid()) {
            return;
        }
        Location l = rune.getLocation();
        Item target = l.getWorld().getNearbyEntities(l, RANGE, RANGE, RANGE, this::findMarkableTool)
            .stream()
            .map(Item.class::cast)
            .findFirst()
            .orElse(null);
        if (target == null) {
            return;
        }
        ItemStack tool = target.getItemStack();
        l.getWorld().strikeLightningEffect(l);
        Slimefun.runSync(() -> {
            if (!rune.isValid() || !target.isValid() || tool.getAmount() != 1) {
                return;
            }
            target.remove();
            rune.remove();
            CollectService.markTool(tool);
            l.getWorld().dropItemNaturally(l, tool);
            player.sendMessage(Lang.getString(CellnetText.COLLECT_APPLY_SUCCESS));
        }, 10L);
    }

    private boolean findMarkableTool(@NotNull Entity entity) {
        if (entity instanceof Item item) {
            ItemStack stack = item.getItemStack();
            return item.getPickupDelay() <= 0
                && stack.getAmount() == 1
                && isMarkableTool(stack)
                && !CollectService.hasMark(stack)
                && !isItem(stack);
        }
        return false;
    }

    public static boolean isMarkableTool(@NotNull ItemStack stack) {
        Material type = stack.getType();
        return Tag.ITEMS_SWORDS.isTagged(type) || Tag.ITEMS_PICKAXES.isTagged(type)
            || Tag.ITEMS_AXES.isTagged(type) || Tag.ITEMS_HOES.isTagged(type)
            || Tag.ITEMS_SHOVELS.isTagged(type);
    }
}
