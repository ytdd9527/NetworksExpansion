package com.ytdd9527.networksexpansion.api.chain;

import com.balugaq.netex.utils.Scheduling;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ChainRangeParticles {

    private static final long FADE_DELAY_TICKS = 100L;
    private static final int PERIOD_TICKS = 5;
    private static final double EDGE_STEP = 0.5;
    private static final int[][] CORNERS = {{0, 0}, {1, 0}, {0, 1}, {1, 1}};

    private static final Map<Location, ParticleState> STATES = new ConcurrentHashMap<>();

    private static final class ParticleState {
        volatile WrappedTask active;
        volatile WrappedTask pendingFade;
    }

    private ChainRangeParticles() {
    }

    public static void show(
        @NotNull Location origin, @NotNull Set<BlockFace> directions, int distance) {
        ParticleState s = STATES.get(origin);
        if (s != null) {
            if (s.pendingFade != null) { s.pendingFade.cancel(); s.pendingFade = null; }
            if (s.active != null) { s.active.cancel(); s.active = null; }
        }
        if (directions.isEmpty()) {
            return;
        }
        List<BlockFace> dirs = List.copyOf(directions);
        int dist = Math.max(1, distance);
        World world = origin.getWorld();
        if (world == null) {
            return;
        }
        int[] spans = new int[dirs.size()];
        for (int i = 0; i < dirs.size(); i++) {
            BlockFace d = dirs.get(i);
            for (int j = 1; j <= dist; j++) {
                Block block = world.getBlockAt(
                    origin.getBlockX() + d.getModX() * j,
                    origin.getBlockY() + d.getModY() * j,
                    origin.getBlockZ() + d.getModZ() * j);
                if (block.getType().isAir()) {
                    break;
                }
                spans[i] = j;
            }
        }
        WrappedTask task = Scheduling.runTimerAsync(() -> draw(world, origin, dirs, spans, dist), 1L, PERIOD_TICKS);
        STATES.computeIfAbsent(origin, k -> new ParticleState()).active = task;
    }

    public static void fadeLater(@NotNull Location origin) {
        ParticleState s = STATES.get(origin);
        if (s == null || s.active == null) {
            return;
        }
        if (s.pendingFade != null) {
            return;
        }
        WrappedTask faded = s.active;
        s.pendingFade = Scheduling.runLaterAsync(() -> {
            ParticleState st = STATES.get(origin);
            if (st != null && st.active == faded) {
                if (st.active != null) { st.active.cancel(); st.active = null; }
                st.pendingFade = null;
            }
        }, FADE_DELAY_TICKS);
    }

    public static void stop(@NotNull Location origin) {
        ParticleState s = STATES.remove(origin);
        if (s != null) {
            if (s.active != null) s.active.cancel();
            if (s.pendingFade != null) s.pendingFade.cancel();
        }
    }

    private static void draw(
            @NotNull World world, @NotNull Location origin, @NotNull List<BlockFace> dirs,
            @NotNull int[] spans, int distance) {
        for (int i = 0; i < dirs.size(); i++) {
            drawTunnel(world, origin, dirs.get(i), spans[i]);
        }
    }

    private static void drawTunnel(
            @NotNull World world, @NotNull Location origin, @NotNull BlockFace direction, int span) {
        double dx = direction.getModX();
        double dy = direction.getModY();
        double dz = direction.getModZ();
        double ux;
        double uy;
        double uz;
        double vx;
        double vy;
        double vz;
        if (dy != 0) {
            ux = 1;
            uy = 0;
            uz = 0;
            vx = 0;
            vy = 0;
            vz = 1;
        } else if (dx != 0) {
            ux = 0;
            uy = 1;
            uz = 0;
            vx = 0;
            vy = 0;
            vz = 1;
        } else {
            ux = 1;
            uy = 0;
            uz = 0;
            vx = 0;
            vy = 1;
            vz = 0;
        }
        double ox = origin.getBlockX();
        double oy = origin.getBlockY();
        double oz = origin.getBlockZ();
        for (int[] corner : CORNERS) {
            double cx = ox + ux * corner[0] + vx * corner[1];
            double cy = oy + uy * corner[0] + vy * corner[1];
            double cz = oz + uz * corner[0] + vz * corner[1];
            for (double t = 1.0; t <= span; t += EDGE_STEP) {
                spawnEdge(world, cx + dx * t, cy + dy * t, cz + dz * t,
                    Math.abs(dx), Math.abs(dy), Math.abs(dz));
            }
        }
    }

    private static void spawnEdge(
        @NotNull World world, double x, double y, double z, double ax, double ay, double az) {
        world.spawnParticle(Particle.COMPOSTER, x, y, z, 3, ax * 0.33, ay * 0.33, az * 0.33, 0);
    }
}
