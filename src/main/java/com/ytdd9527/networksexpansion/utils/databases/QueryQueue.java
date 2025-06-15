package com.ytdd9527.networksexpansion.utils.databases;

import com.balugaq.netex.utils.Debug;
import com.balugaq.netex.utils.Lang;
import io.github.sefiraat.networks.Networks;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class QueryQueue {

    private final @NotNull BlockingQueue<QueuedTask> updateTasks;
    private final @NotNull BlockingQueue<QueuedTask> queryTasks;
    private boolean threadStarted;

    public QueryQueue() {
        // Create database query processing thread
        updateTasks = new LinkedBlockingDeque<>();
        queryTasks = new LinkedBlockingDeque<>();

        threadStarted = false;
    }

    public synchronized void scheduleUpdate(@NotNull QueuedTask task) {
        if (!updateTasks.offer(task)) {
            throw new IllegalStateException(
                    Lang.getString("messages.unsupported-operation.comprehensive.invalid_queue"));
        }
    }

    public synchronized void scheduleQuery(@NotNull QueuedTask task) {
        if (!queryTasks.offer(task)) {
            throw new IllegalStateException(
                    Lang.getString("messages.unsupported-operation.comprehensive.invalid_queue"));
        }
    }

    public void startThread() {
        if (!threadStarted) {
            getProcessor(queryTasks).runTaskAsynchronously(Networks.getInstance());
            getProcessor(updateTasks).runTaskAsynchronously(Networks.getInstance());
            threadStarted = true;
        }
    }

    public int getTaskAmount() {
        return updateTasks.size() + queryTasks.size();
    }

    public boolean isAllDone() {
        return !threadStarted || getTaskAmount() == 0;
    }

    public void scheduleAbort() {
        QueuedTask abortTask = new QueuedTask() {
            @Override
            public boolean execute() {
                return true;
            }

            @Override
            public boolean callback() {
                return true;
            }
        };
        queryTasks.offer(abortTask);
        updateTasks.offer(abortTask);
    }

    private @NotNull BukkitRunnable getProcessor(@NotNull BlockingQueue<QueuedTask> queue) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        QueuedTask task = queue.take();
                        if (task.execute() && task.callback()) {
                            break;
                        }
                    } catch (Exception e) {
                        Debug.trace(e);
                    }
                }
            }
        };
    }
}
