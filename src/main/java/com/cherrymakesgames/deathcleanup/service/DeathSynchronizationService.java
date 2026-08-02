package com.cherrymakesgames.deathcleanup.service;

import com.cherrymakesgames.deathcleanup.data.DeathRecord;
import com.cherrymakesgames.deathcleanup.data.DeathSavedData;
import com.cherrymakesgames.deathcleanup.marker.DeathMarkerBackend;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;
import java.util.UUID;

public final class DeathSynchronizationService {
    private DeathMarkerBackend markerBackend;

    public void recordDeath(MinecraftServer server, DeathRecord death) {
        DeathSavedData.get(server).upsert(death);

        if (markerBackend != null) {
            markerBackend.upsert(death);
        }
    }

    public boolean removeDeath(MinecraftServer server, UUID playerId) {
        boolean removed = DeathSavedData.get(server).remove(playerId);
        if (removed && markerBackend != null) {
            markerBackend.remove(playerId);
        }

        return removed;
    }

    public void attachMarkerBackend(MinecraftServer server, DeathMarkerBackend markerBackend) {
        this.markerBackend = Objects.requireNonNull(markerBackend, "markerBackend");
        rebuildMarkers(server);
    }

    public void detachMarkerBackend(DeathMarkerBackend markerBackend) {
        if (this.markerBackend == markerBackend) {
            this.markerBackend = null;
        }
    }

    public void rebuildMarkers(MinecraftServer server) {
        if (markerBackend != null) {
            markerBackend.replaceAll(DeathSavedData.get(server).records());
        }
    }
}
