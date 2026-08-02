package com.cherrymakesgames.deathcleanup;

import com.cherrymakesgames.deathcleanup.event.PlayerDeathEvents;
import com.cherrymakesgames.deathcleanup.service.DeathSynchronizationService;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DeathCleanup implements ModInitializer {
    public static final String MOD_ID = "deathcleanup";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final DeathSynchronizationService DEATHS = new DeathSynchronizationService();

    public static DeathSynchronizationService deaths() {
        return DEATHS;
    }

    @Override
    public void onInitialize() {
        PlayerDeathEvents.register(DEATHS);
        LOGGER.info("Death Cleanup initialized");
    }
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
