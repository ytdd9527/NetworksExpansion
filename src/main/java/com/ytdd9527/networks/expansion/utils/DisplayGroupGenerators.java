package com.ytdd9527.networks.expansion.utils;

import com.ytdd9527.networks.expansion.setup.ExpansionItemStacks;
import com.ytdd9527.networks.expansion.setup.Skins;
import com.ytdd9527.networks.expansion.utils.itemstacks.Transformations;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import dev.sefiraat.sefilib.entity.display.builders.ItemDisplayBuilder;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;


public final class DisplayGroupGenerators {

    public static final CustomItemStack BRIDGE_STACK = new CustomItemStack(
            Skins.BRIDGE1.getPlayerHead(), ""
    );
    public static final CustomItemStack BRIDGE_CORNER_RIM_STACK = new CustomItemStack(
            Skins.BRIDGE2.getPlayerHead(), ""
    );

    private DisplayGroupGenerators() {
        throw new IllegalStateException("Utility class");
    }

    public static DisplayGroup generateStorageUnitUpgradeTable(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnitUpgradeTable",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.STORAGE_UNIT_UPGRADE_TABLE_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_1(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_1",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_1_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_2(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_2",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_2_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_3(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_3",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_3_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_4(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_4",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_4_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_5(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_5",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_5_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_6(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_6",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_6_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_7(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_7",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_7_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_8(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_8",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_8_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_9(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_9",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_9_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_10(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_10",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_10_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_11(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_11",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_11_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_12(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_12",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_12_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateStorageUnit_13(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_13",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(ExpansionItemStacks.CARGO_STORAGE_UNIT_13_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateCloche(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "purge",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(BRIDGE_STACK))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateCell(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "cell",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(BRIDGE_STACK))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generatePowerNode(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 2f, 0.5f);
        displayGroup.addDisplay(
                "powernode",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 0, 0))
                        .setItemStack(new ItemStack(BRIDGE_STACK))
                        .setTransformation(Transformations.NE_MODEL_CAPACITOR_5.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateBridge1(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1f, 0.5f);

        displayGroup.addDisplay(
                "bridge_1",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 0.65, 0))
                        .setItemStack(new ItemStack(BRIDGE_STACK))
                        .setTransformation(Transformations.BRIDGE_1.getTransformation())
                        .build(displayGroup)
        );
        displayGroup.addDisplay(
                "bridge_3",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 0.9, 0.3))
                        .setItemStack(new ItemStack(BRIDGE_CORNER_RIM_STACK))
                        .setTransformation(Transformations.BRIDGE_3.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;

    }

    public static DisplayGroup generateBridge2(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1f, 0.5f);
        displayGroup.addDisplay(
                "bridge_2",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(new ItemStack(BRIDGE_STACK))
                        .setTransformation(Transformations.BRIDGE_2.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateBridge3(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1f, 0.5f);
        displayGroup.addDisplay(
                "bridge_3",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0.4))
                        .setItemStack(new ItemStack(BRIDGE_STACK))
                        .setTransformation(Transformations.BRIDGE_3.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }

    public static DisplayGroup generateBridge4(@Nonnull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1f, 0.5f);
        displayGroup.addDisplay(
                "bridge_4",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0.25))
                        .setItemStack(new ItemStack(BRIDGE_CORNER_RIM_STACK))
                        .setTransformation(Transformations.BRIDGE_3.getTransformation())
                        .build(displayGroup)
        );
        return displayGroup;
    }
}
