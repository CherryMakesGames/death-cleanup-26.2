package com.cherrymakesgames.deathcleanup.marker;

import com.cherrymakesgames.deathcleanup.data.DeathRecord;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class BlueMapDeathMarkerBackend implements DeathMarkerBackend {
    public static final String MARKER_SET_ID = "deathcleanup.deaths";
    public static final String MARKER_SET_LABEL = "Player Deaths";

    private final BlueMapAPI blueMap;
    private final MinecraftServer server;
    private final DeathMarkerFactory markerFactory;

    public BlueMapDeathMarkerBackend(BlueMapAPI blueMap, MinecraftServer server) {
        this(blueMap, server, new DeathMarkerFactory());
    }

    BlueMapDeathMarkerBackend(
            BlueMapAPI blueMap,
            MinecraftServer server,
            DeathMarkerFactory markerFactory
    ) {
        this.blueMap = Objects.requireNonNull(blueMap, "blueMap");
        this.server = Objects.requireNonNull(server, "server");
        this.markerFactory = Objects.requireNonNull(markerFactory, "markerFactory");
    }

    @Override
    public void upsert(DeathRecord death) {
        String markerId = markerId(death.playerId());
        remove(death.playerId());

        for (BlueMapMap map : mapsForDimension(death.dimensionId())) {
            markerSet(map).getMarkers().put(markerId, markerFactory.create(death));
        }
    }

    @Override
    public void remove(UUID playerId) {
        String markerId = markerId(playerId);
        for (BlueMapMap map : blueMap.getMaps()) {
            MarkerSet markerSet = map.getMarkerSets().get(MARKER_SET_ID);
            if (markerSet != null) {
                markerSet.getMarkers().remove(markerId);
            }
        }
    }

    @Override
    public void replaceAll(Collection<DeathRecord> deaths) {
        for (BlueMapMap map : blueMap.getMaps()) {
            map.getMarkerSets().remove(MARKER_SET_ID);
        }

        for (DeathRecord death : deaths) {
            upsert(death);
        }
    }

    private Collection<BlueMapMap> mapsForDimension(String dimensionId) {
        for (ServerLevel level : server.getAllLevels()) {
            if (level.dimension().identifier().toString().equals(dimensionId)) {
                return blueMap.getWorld(level)
                        .map(world -> world.getMaps())
                        .orElseGet(List::of);
            }
        }

        return List.of();
    }

    private MarkerSet markerSet(BlueMapMap map) {
        return map.getMarkerSets().computeIfAbsent(MARKER_SET_ID, ignored -> MarkerSet.builder()
                .label(MARKER_SET_LABEL)
                .toggleable(true)
                .defaultHidden(false)
                .build());
    }

    private static String markerId(UUID playerId) {
        return playerId.toString();
    }
}
