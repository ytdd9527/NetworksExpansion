package io.github.sefiraat.networks;

import io.github.bakedlibs.dough.blocks.ChunkPosition;
import io.github.sefiraat.networks.network.NetworkNode;
import io.github.sefiraat.networks.network.NodeDefinition;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class NetworkStorage {
    private static final Map<ChunkPosition, Set<Location>> ALL_NETWORK_OBJECTS_BY_CHUNK = new ConcurrentHashMap<>();
    private static final Map<Location, NodeDefinition> ALL_NETWORK_OBJECTS = new ConcurrentHashMap<>();

    public static void removeNode(Location location) {
        synchronized (ALL_NETWORK_OBJECTS) {
            final NodeDefinition nodeDefinition = ALL_NETWORK_OBJECTS.remove(location);

            if (nodeDefinition == null) {
                return;
            }

            final NetworkNode node = nodeDefinition.getNode();

            if (node == null) {
                return;
            }

            for (NetworkNode childNode : nodeDefinition.getNode().getChildrenNodes()) {
                removeNode(childNode.getNodePosition());
            }
        }
    }

    public static boolean containsKey(Location location) {
        synchronized (ALL_NETWORK_OBJECTS) {
            return ALL_NETWORK_OBJECTS.containsKey(location);
        }
    }

    public static NodeDefinition getNode(Location location) {
        synchronized (ALL_NETWORK_OBJECTS) {
            return ALL_NETWORK_OBJECTS.get(location);
        }
    }

    public static void registerNode(@NotNull Location location, NodeDefinition nodeDefinition) {
        synchronized (ALL_NETWORK_OBJECTS) {
            ALL_NETWORK_OBJECTS.put(location, nodeDefinition);
            ChunkPosition unionKey = new ChunkPosition(location);
            Set<Location> locations = ALL_NETWORK_OBJECTS_BY_CHUNK.getOrDefault(unionKey, new HashSet<>());
            synchronized (locations) {
                locations.add(location);
            }
            ALL_NETWORK_OBJECTS_BY_CHUNK.put(unionKey, locations);
        }
    }

    public static void unregisterChunk(@NotNull Chunk chunk) {
        ChunkPosition chunkPosition = new ChunkPosition(chunk);
        Set<Location> locations = ALL_NETWORK_OBJECTS_BY_CHUNK.get(chunkPosition);
        if (locations == null) {
            return;
        }
        Set<Location> clone;
        synchronized (locations) {
            clone = new HashSet<>(locations);
        }
        for (Location location : clone) {
            removeNode(location);
        }
        synchronized (ALL_NETWORK_OBJECTS_BY_CHUNK) {
            ALL_NETWORK_OBJECTS_BY_CHUNK.remove(chunkPosition);
        }
    }

    public static Map<Location, NodeDefinition> getAllNetworkObjects() {
        return new HashMap<>(ALL_NETWORK_OBJECTS);
    }
}
