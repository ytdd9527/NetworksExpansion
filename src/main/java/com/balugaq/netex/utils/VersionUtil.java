package com.balugaq.netex.utils;

import com.ytdd9527.networksexpansion.utils.ReflectionUtil;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class VersionUtil {
    public static Inventory getTopInventory(InventoryEvent event) {
        return (Inventory) ReflectionUtil.invokeMethod(ReflectionUtil.invokeMethod(event, "getView"), "getTopInventory");
    }

    public static Inventory setCursor(InventoryEvent event, @Nullable ItemStack stack) {
        return (Inventory) ReflectionUtil.invokeMethod(ReflectionUtil.invokeMethod(event, "getView"), "setCursor", stack);
    }
}
