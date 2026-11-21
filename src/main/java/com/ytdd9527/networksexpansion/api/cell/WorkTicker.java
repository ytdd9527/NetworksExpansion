package com.ytdd9527.networksexpansion.api.cell;

import com.balugaq.netex.utils.Scheduling;

import com.balugaq.netex.api.enums.FeedbackType;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.ytdd9527.networksexpansion.core.machines.AbstractChainMachine;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class WorkTicker extends BlockTicker {

    private final AbstractChainMachine abstractChainMachine;

    public WorkTicker(AbstractChainMachine abstractChainMachine) {
        this.abstractChainMachine = abstractChainMachine;
    }

    @Override
    public boolean isSynchronized() {
        return false;
    }

    @Override
    public void tick(@NotNull Block block, SlimefunItem item, @NotNull SlimefunBlockData data) {
        abstractChainMachine.addToRegistry(block);
        BlockMenu menu = data.getBlockMenu();
        if (menu == null) {
            return;
        }
        LineState state = AbstractChainMachine.STATES.computeIfAbsent(menu.getLocation(), k -> new LineState());
        long now = System.currentTimeMillis();
        if (now < state.nextWorkMs) {
            return;
        }
        state.nextWorkMs = now + abstractChainMachine.gateIntervalMs();
        if (!state.wired) {
            state.wired = true;
            Scheduling.runTask(() -> abstractChainMachine.wireMenu(menu, state));
        }
        int power = abstractChainMachine.requiredPower();
        NetworkRoot root = abstractChainMachine.rootOf(menu);
        if (root == null) {
            abstractChainMachine.sendFeedback(menu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }
        if (root.getRootPower() < power) {
            abstractChainMachine.sendFeedback(menu.getLocation(), FeedbackType.NOT_ENOUGH_POWER);
            return;
        }
        Scheduling.runTask(() -> abstractChainMachine.runWorkBody(menu, state, root, power));
    }
}
