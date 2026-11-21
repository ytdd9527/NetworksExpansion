package com.balugaq.netex.utils;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public final class Scheduling {

    private static FoliaLib foliaLib;

    private Scheduling() {
    }

    public static void initialize(Plugin plugin) {
        foliaLib = new FoliaLib(plugin);
    }

    public static FoliaLib foliaLib() {
        return foliaLib;
    }

    // Global tasks run on Folia's global region, which may not touch blocks or entities.
    public static void runTask(Runnable task) {
        foliaLib.getScheduler().runNextTick(ignored -> task.run());
    }

    public static void runAsync(Runnable task) {
        foliaLib.getScheduler().runAsync(ignored -> task.run());
    }

    public static WrappedTask runLater(Runnable task, long delay) {
        return foliaLib.getScheduler().runLater(task, delay);
    }

    public static WrappedTask runLaterAsync(Runnable task, long delay) {
        return foliaLib.getScheduler().runLaterAsync(task, delay);
    }

    public static WrappedTask runTimer(Runnable task, long delay, long period) {
        return foliaLib.getScheduler().runTimer(task, delay, period);
    }

    public static WrappedTask runTimerAsync(Runnable task, long delay, long period) {
        return foliaLib.getScheduler().runTimerAsync(task, delay, period);
    }

    public static void runAt(Location location, Runnable task) {
        foliaLib.getScheduler().runAtLocation(location, ignored -> task.run());
    }

    public static WrappedTask runAtLater(Location location, Runnable task, long delay) {
        return foliaLib.getScheduler().runAtLocationLater(location, task, delay);
    }

    public static WrappedTask runAtTimer(Location location, Runnable task, long delay, long period) {
        return foliaLib.getScheduler().runAtLocationTimer(location, task, delay, period);
    }

    public static void runAtEntity(Entity entity, Runnable task) {
        foliaLib.getScheduler().runAtEntity(entity, ignored -> task.run());
    }

    public static WrappedTask runAtEntityLater(Entity entity, Runnable task, long delay) {
        return foliaLib.getScheduler().runAtEntityLater(entity, task, delay);
    }

    public static void cancel(WrappedTask task) {
        if (task != null) {
            foliaLib.getScheduler().cancelTask(task);
        }
    }

    public static void cancelAll() {
        foliaLib.getScheduler().cancelAllTasks();
    }
}
