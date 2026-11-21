package com.ytdd9527.networksexpansion.api.drive;

import com.balugaq.netex.utils.Scheduling;

import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.cell.CellnetText;
import io.github.sefiraat.networks.Networks;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DriveGuide {

    private static final long MAX_DURATION_MS = 120_000L;
    private static final double ARRIVE_DISTANCE = 2.5D;
    private static final int PERIOD_TICKS = 2;
    private static final double ARROW_LEAD = 2.0D;
    private static final double ARROW_HEAD = 0.7D;
    private static final double ARROW_HEAD_HALF_WIDTH = 0.4D;
    private static final double SHAFT_LENGTH = 1.2D;
    private static final double ARROW_Y_OFFSET = 0.2D;
    private static final int LINE_POINTS = 6;
    private static final long ACTIONBAR_INTERVAL_MS = 500L;
    private static final Particle.DustOptions ARROW_DUST =
        new Particle.DustOptions(Color.fromRGB(0xFFAA00), 1.4F);

    private static final class GuideSession {
        private final WrappedTask task;
        private long lastActionBar;

        private GuideSession(@NotNull WrappedTask task) {
            this.task = task;
        }
    }

    private static final Map<UUID, GuideSession> ACTIVE = new ConcurrentHashMap<>();

    private DriveGuide() {
    }

    public static void start(@NotNull Player player, @NotNull Location target) {
        boolean replaced = stop(player);
        Location anchor = target.clone().add(0.5D, 0.5D, 0.5D);
        UUID playerId = player.getUniqueId();
        long startedAt = System.currentTimeMillis();
        WrappedTask task = Scheduling.runTimer(() -> tick(player, anchor, startedAt), 0L, PERIOD_TICKS);
        ACTIVE.put(playerId, new GuideSession(task));
        player.sendMessage(Lang.getString(replaced
            ? CellnetText.MANAGER_GUIDE_REPLACED
            : CellnetText.MANAGER_GUIDE_STARTED));
    }

    public static boolean stop(@NotNull Player player) {
        GuideSession session = ACTIVE.remove(player.getUniqueId());
        if (session == null) {
            return false;
        }
        session.task.cancel();
        return true;
    }

    private static void tick(@NotNull Player player, @NotNull Location target, long startedAt) {
        if (!player.isOnline() || player.isDead()
            || System.currentTimeMillis() - startedAt > MAX_DURATION_MS
            || DriveType.of(StorageCacheUtils.getSfItem(target)) == null
            || !player.getWorld().equals(target.getWorld())) {
            stop(player);
            return;
        }
        Location feet = player.getLocation();
        double distance = feet.distance(target);
        if (distance <= ARRIVE_DISTANCE) {
            stop(player);
            player.sendMessage(Lang.getString(CellnetText.MANAGER_GUIDE_ARRIVED));
            return;
        }
        drawArrow(player, feet, target);
        sendDistance(player, feet, target, distance);
    }

    private static void drawArrow(@NotNull Player player, @NotNull Location feet, @NotNull Location target) {
        Vector direction = target.toVector().subtract(feet.toVector());
        direction.setY(0);
        if (direction.lengthSquared() < 1.0E-4D) {
            return;
        }
        direction.normalize();
        Vector side = new Vector(-direction.getZ(), 0, direction.getX());

        double baseY = feet.getY() + ARROW_Y_OFFSET;
        Vector base = feet.toVector().add(direction.clone().multiply(ARROW_LEAD));
        base.setY(baseY);
        Vector tip = base.clone().add(direction.clone().multiply(ARROW_HEAD));
        Vector cornerLeft = base.clone().add(side.clone().multiply(ARROW_HEAD_HALF_WIDTH));
        Vector cornerRight = base.clone().subtract(side.clone().multiply(ARROW_HEAD_HALF_WIDTH));
        Vector shaftEnd = base.clone().subtract(direction.clone().multiply(SHAFT_LENGTH));

        drawLine(player, cornerLeft, tip);
        drawLine(player, cornerRight, tip);
        drawLine(player, base, shaftEnd);
    }

    private static void drawLine(@NotNull Player player, @NotNull Vector from, @NotNull Vector to) {
        for (int i = 0; i < LINE_POINTS; i++) {
            double ratio = (double) i / LINE_POINTS;
            Vector point = from.clone().multiply(1.0D - ratio).add(to.clone().multiply(ratio));
            player.spawnParticle(Particle.DUST, point.getX(), point.getY(), point.getZ(),
                1, 0, 0, 0, 0, ARROW_DUST);
        }
    }

    private static void sendDistance(@NotNull Player player, @NotNull Location feet,
                                     @NotNull Location target, double distance) {
        GuideSession session = ACTIVE.get(player.getUniqueId());
        if (session == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - session.lastActionBar < ACTIONBAR_INTERVAL_MS) {
            return;
        }
        session.lastActionBar = now;
        double heightDelta = target.getY() - feet.getY();
        String heightHint = heightDelta >= 2.0D
            ? Lang.getString(CellnetText.MANAGER_GUIDE_UP)
            : heightDelta <= -2.0D ? Lang.getString(CellnetText.MANAGER_GUIDE_DOWN) : "";
        Lang.get().sendActionbarMessage(player, "cellnet.manager.guide_distance",
            Math.round(distance), heightHint);
    }
}
