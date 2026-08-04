package com.cherrymakesgames.deathcleanup.integration;

import de.bluecolored.bluemap.api.BlueMapAPI;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

public final class BlueMapLifecycle {
    private volatile BlueMapAPI blueMap;
    private volatile MinecraftServer server;

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
        return blueMap != null && server != null;
    }

    private void onBlueMapEnabled(BlueMapAPI blueMap) {
        this.blueMap = blueMap;
    }

    private void onBlueMapDisabled(BlueMapAPI blueMap) {
        if (this.blueMap == blueMap) {
            this.blueMap = null;
        }
    }

    private void onServerStarted(MinecraftServer server) {
        this.server = server;
    }

    private void onServerStopped(MinecraftServer server) {
        if (this.server == server) {
            this.server = null;
        }
    }
}
