package com.ytdd9527.networksexpansion.api.cell;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CellUseHandlerRegressionTest {

    private static final Material CELL_MATERIAL = Material.BARRIER;

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void offHandUseMustNotDuplicateCell() {
        PlayerMock player = server.addPlayer();
        ItemStack cell = newCell();
        player.getInventory().setItemInOffHand(cell);
        AtomicBoolean opened = new AtomicBoolean();

        fireRightClick(player, cell, EquipmentSlot.OFF_HAND, opened);

        assertTrue(opened.get(), "副手右键仍应正常打开元件菜单");
        assertEquals(1, countCells(player), "副手右键不能凭空多出一个元件");
        assertTrue(isCell(player.getInventory().getItemInOffHand()), "元件应留在副手");
        assertFalse(isCell(player.getInventory().getItemInMainHand()), "主手不应出现元件");
    }

    @Test
    void mainHandUseStillWritesBackToMainHand() {
        PlayerMock player = server.addPlayer();
        ItemStack cell = newCell();
        player.getInventory().setItemInMainHand(cell);
        AtomicBoolean opened = new AtomicBoolean();

        fireRightClick(player, cell, EquipmentSlot.HAND, opened);

        assertTrue(opened.get(), "主手右键仍应正常打开元件菜单");
        assertEquals(1, countCells(player), "主手右键不应改变元件总数");
        assertTrue(isCell(player.getInventory().getItemInMainHand()), "元件应留在主手");
        assertFalse(isCell(player.getInventory().getItemInOffHand()), "副手不应出现元件");
    }

    @Test
    void sneakingUseIsIgnored() {
        PlayerMock player = server.addPlayer();
        ItemStack cell = newCell();
        player.getInventory().setItemInOffHand(cell);
        player.setSneaking(true);
        AtomicBoolean opened = new AtomicBoolean();

        fireRightClick(player, cell, EquipmentSlot.OFF_HAND, opened);

        assertFalse(opened.get(), "潜行右键不应打开元件菜单");
        assertEquals(1, countCells(player), "潜行右键不应改变元件总数");
        assertFalse(isCell(player.getInventory().getItemInMainHand()), "主手不应出现元件");
    }

    private static void fireRightClick(
        Player player,
        ItemStack cell,
        EquipmentSlot hand,
        AtomicBoolean opened) {
        CellUseHandler.CellUse use = new CellUseHandler.CellUse() {
            @Override
            public boolean prepare(Player p, ItemStack c) {
                return true;
            }

            @Override
            public void open(Player p, ItemStack c) {
                opened.set(true);
            }
        };
        ItemUseHandler handler = CellUseHandler.create(use);
        PlayerInteractEvent interact = new PlayerInteractEvent(
            player, Action.RIGHT_CLICK_AIR, cell, null, BlockFace.SELF, hand);
        handler.onRightClick(new PlayerRightClickEvent(interact));
    }

    private static ItemStack newCell() {
        return new ItemStack(CELL_MATERIAL);
    }

    private static boolean isCell(ItemStack item) {
        return item != null && item.getType() == CELL_MATERIAL && item.getAmount() > 0;
    }

    private static long countCells(Player player) {
        PlayerInventory inventory = player.getInventory();
        long count = 0;
        for (int i = 0; i < 36; i++) {
            if (isCell(inventory.getItem(i))) {
                count++;
            }
        }
        if (isCell(inventory.getItemInOffHand())) {
            count++;
        }
        return count;
    }
}
