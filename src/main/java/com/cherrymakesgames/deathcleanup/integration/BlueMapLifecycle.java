package com.cherrymakesgames.deathcleanup.integration;

import com.cherrymakesgames.deathcleanup.marker.BlueMapDeathMarkerBackend;
import com.cherrymakesgames.deathcleanup.marker.DeathMarkerBackend;
import com.cherrymakesgames.deathcleanup.service.DeathSynchronizationService;
import de.bluecolored.bluemap.api.BlueMapAPI;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;
import java.util.Optional;

public final class BlueMapLifecycle {
    private final DeathSynchronizationService deaths;
    private volatile BlueMapAPI blueMap;
    private volatile MinecraftServer server;
    private volatile DeathMarkerBackend markerBackend;

    public BlueMapLifecycle(DeathSynchronizationService deaths) {
        this.deaths = Objects.requireNonNull(deaths, "deaths");
    }

    public void register() {
        BlueMapAPI.onEnable(this::onBlueMapEnabled);
        BlueMapAPI.onDisable(this::onBlueMapDisabled);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);
    }

    public Optional<BlueMapAPI> blueMap() {
        return Optional.ofNullable(blueMap);
    }

    public Optional<MinecraftServer> server() {
        return Optional.ofNullable(server);
    }

    public boolean isReady() {
        return markerBackend != null;
    }

    private void onBlueMapEnabled(BlueMapAPI blueMap) {
        this.blueMap = blueMap;
        connectIfReady();
    }

    private void onBlueMapDisabled(BlueMapAPI blueMap) {
        if (this.blueMap == blueMap) {
            disconnectMarkerBackend();
            this.blueMap = null;
        }
    }

    private void onServerStarted(MinecraftServer server) {
        this.server = server;
        connectIfReady();
    }

    private void onServerStopped(MinecraftServer server) {
        if (this.server == server) {
            disconnectMarkerBackend();
            this.server = null;
        }
    }

    private void connectIfReady() {
        BlueMapAPI blueMap = this.blueMap;
        MinecraftServer server = this.server;
        if (markerBackend != null || blueMap == null || server == null) {
            return;
        }

        DeathMarkerBackend markerBackend = new BlueMapDeathMarkerBackend(blueMap, server);
        this.markerBackend = markerBackend;
        deaths.attachMarkerBackend(server, markerBackend);
    }

    private void disconnectMarkerBackend() {
        DeathMarkerBackend markerBackend = this.markerBackend;
        this.markerBackend = null;
        if (markerBackend != null) {
            deaths.detachMarkerBackend(markerBackend);
        }
    }
}
