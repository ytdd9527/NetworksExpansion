package com.ytdd9527.networksexpansion.implementation.items.cell;

import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.Scheduling;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import com.ytdd9527.networksexpansion.api.cell.LineState;
import com.ytdd9527.networksexpansion.api.chain.ChainBindingMenu;
import com.ytdd9527.networksexpansion.api.chain.ChainBindingStore;
import com.ytdd9527.networksexpansion.api.chain.ChainModule;
import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.core.machines.AbstractChainMachine;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class ChainBrush extends SpecialSlimefunItem {

    private static final int SUMMARY_TICKS = 100;
    private static final float SUMMARY_SCALE = 0.6F;

    public ChainBrush(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    private record LineHit(@NotNull Location origin, @NotNull BlockFace face, int distance) {
    }

    @Override
    public void preRegister() {
        addItemHandler((ItemUseHandler) e -> {
            e.cancel();
            Player player = e.getPlayer();
            Optional<Block> optional = e.getClickedBlock();
            if (optional.isEmpty()) {
                return;
            }
            Block block = optional.get();
            Location location = block.getLocation();
            if (!Slimefun.getProtectionManager()
                .hasPermission(player, block, Interaction.INTERACT_BLOCK)) {
                return;
            }
            AbstractChainMachine machine = AbstractChainMachine.machineAt(location);
            if (machine != null) {
                ChainBindingMenu.openDirections(player, location);
                return;
            }
            LineHit hit = resolveLineHit(location);
            if (hit == null) {
                player.sendMessage(Lang.getString(CellnetText.BRUSH_NO_LINE));
                return;
            }
            if (player.isSneaking()) {
                ChainBindingMenu.clearBinding(player, hit.origin(), hit.face(), hit.distance());
                showSummary(location, hit, List.of());
            } else {
                showSummary(location, hit, ChainBindingStore.load(hit.origin(), hit.face()).get(hit.distance()));
                ChainBindingMenu.openEdit(player, hit.origin(), hit.face(), hit.distance());
            }
        });
    }

    @Nullable
    private static LineHit resolveLineHit(@NotNull Location target) {
        int max = ChainBindingStore.maxDistance();
        for (BlockFace face : NetworkDirectional.VALID_FACES) {
            for (int distance = 1; distance <= max; distance++) {
                Location origin = target.getBlock()
                    .getRelative(face.getOppositeFace(), distance)
                    .getLocation();
                AbstractChainMachine machine = AbstractChainMachine.machineAt(origin);
                if (machine == null) {
                    continue;
                }
                LineState state = AbstractChainMachine.loadedState(origin);
                if (!state.effectiveDirections().contains(face)) {
                    continue;
                }
                if (distance > machine.effectiveDistance(state)) {
                    continue;
                }
                if (!AbstractChainMachine.hasModule(origin, ChainModule.BINDING)) {
                    continue;
                }
                return new LineHit(origin, face, distance);
            }
        }
        return null;
    }

    private static void showSummary(@NotNull Location target, @NotNull LineHit hit, @Nullable List<ItemStack> list) {
        Component text = Component.text()
            .append(TextUtil.legacy(Lang.getString(
                CellnetText.BINDING_TARGET_POSITION, hit.distance(), ChainBindingMenu.directionName(hit.face()))))
            .append(Component.newline())
            .append(TextUtil.legacy(Lang.getString(CellnetText.BRUSH_SUMMARY_LABEL)))
            .append(list == null || list.isEmpty()
                ? Component.newline().append(TextUtil.legacy(Lang.getString(CellnetText.BRUSH_SUMMARY_EMPTY)))
                : summaryItems(list))
            .build();
        TextDisplay display = target.getWorld().spawn(
            target.clone().add(0.5, 1.5, 0.5), TextDisplay.class);
        display.text(text);
        display.setBillboard(Display.Billboard.VERTICAL);
        display.setAlignment(TextDisplay.TextAlignment.CENTER);
        display.setTransformation(new Transformation(
            new Vector3f(),
            new AxisAngle4f(),
            new Vector3f(SUMMARY_SCALE, SUMMARY_SCALE, SUMMARY_SCALE),
            new AxisAngle4f()));
        display.setPersistent(false);
        Scheduling.runLater(display::remove, SUMMARY_TICKS);
    }

    @NotNull
    private static Component summaryItems(@NotNull List<ItemStack> list) {
        Component result = Component.empty();
        for (ItemStack item : list) {
            result = result.append(Component.newline());
            result = result.append(TextUtil.legacy(Lang.getString(CellnetText.BRUSH_SUMMARY_BULLET)));
            result = result.append(ChainBindingMenu.displayComponent(item));
        }
        return result;
    }
}