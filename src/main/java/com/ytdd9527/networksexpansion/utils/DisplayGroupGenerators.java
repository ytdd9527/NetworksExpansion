package com.ytdd9527.networksexpansion.utils;

import com.balugaq.netex.api.enums.Skins;
import com.balugaq.netex.api.enums.Transformations;
import com.ytdd9527.networksexpansion.implementation.ExpansionItemStacks;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import dev.sefiraat.sefilib.entity.display.builders.ItemDisplayBuilder;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class DisplayGroupGenerators {

    public static final CustomItemStack BRIDGE_STACK = new CustomItemStack(Skins.BRIDGE1.getPlayerHead(), "");
    public static final CustomItemStack BRIDGE_CORNER_RIM_STACK =
            new CustomItemStack(Skins.BRIDGE2.getPlayerHead(), "");

    public static @NotNull DisplayGroup generateStorageUnit_1(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_1",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_1_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateStorageUnit_2(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_2",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_2_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateStorageUnit_3(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_3",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_3_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateStorageUnit_4(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_4",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_4_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateStorageUnit_5(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_5",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_5_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateStorageUnit_6(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_6",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_6_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateStorageUnit_7(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_7",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_7_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateStorageUnit_8(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_8",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_8_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateStorageUnit_9(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_9",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_9_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateStorageUnit_10(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_10",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_10_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateStorageUnit_11(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_11",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_11_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateStorageUnit_12(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_12",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_12_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateStorageUnit_13(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "StorageUnit_13",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(ExpansionItemStacks.CARGO_STORAGE_UNIT_13_MODEL))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateCloche(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "purge",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(BRIDGE_STACK))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateCell(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1.1f, 0.5f);
        displayGroup.addDisplay(
                "cell",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(BRIDGE_STACK))
                        .setTransformation(Transformations.TWO.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generatePowerNode(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 2f, 0.5f);
        displayGroup.addDisplay(
                "powernode",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 0, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(BRIDGE_STACK))
                        .setTransformation(Transformations.NE_MODEL_CAPACITOR_5.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateBridge1(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1f, 0.5f);

        displayGroup.addDisplay(
                "bridge_1",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 0.65, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(BRIDGE_STACK))
                        .setTransformation(Transformations.BRIDGE_1.getTransformation())
                        .build(displayGroup));
        displayGroup.addDisplay(
                "bridge_3",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 0.9, 0.3))
                        .setItemStack(ItemStackUtil.getCleanItem(BRIDGE_CORNER_RIM_STACK))
                        .setTransformation(Transformations.BRIDGE_3.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateBridge2(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1f, 0.5f);
        displayGroup.addDisplay(
                "bridge_2",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0))
                        .setItemStack(ItemStackUtil.getCleanItem(BRIDGE_STACK))
                        .setTransformation(Transformations.BRIDGE_2.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateBridge3(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1f, 0.5f);
        displayGroup.addDisplay(
                "bridge_3",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0.4))
                        .setItemStack(ItemStackUtil.getCleanItem(BRIDGE_STACK))
                        .setTransformation(Transformations.BRIDGE_3.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }

    public static @NotNull DisplayGroup generateBridge4(@NotNull Location location) {
        final DisplayGroup displayGroup = new DisplayGroup(location, 1f, 0.5f);
        displayGroup.addDisplay(
                "bridge_4",
                new ItemDisplayBuilder()
                        .setGroupParentOffset(new Vector(0, 1, 0.25))
                        .setItemStack(ItemStackUtil.getCleanItem(BRIDGE_CORNER_RIM_STACK))
                        .setTransformation(Transformations.BRIDGE_3.getTransformation())
                        .build(displayGroup));
        return displayGroup;
    }
}
