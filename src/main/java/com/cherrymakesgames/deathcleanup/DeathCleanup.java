package com.cherrymakesgames.deathcleanup;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DeathCleanup implements ModInitializer {
    public static final String MOD_ID = "deathcleanup";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Death Cleanup initialized");
    }
}
