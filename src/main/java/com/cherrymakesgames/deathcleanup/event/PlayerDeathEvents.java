package com.cherrymakesgames.deathcleanup.event;

import com.cherrymakesgames.deathcleanup.data.DeathRecord;
import com.cherrymakesgames.deathcleanup.service.DeathSynchronizationService;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerDeathEvents {
    private PlayerDeathEvents() {
    }

    public static void register(DeathSynchronizationService deaths) {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof ServerPlayer player)) {
                return;
            }

            recordDeath(player, deaths);
        });
    }

    private static void recordDeath(ServerPlayer player, DeathSynchronizationService deaths) {
        ServerLevel level = player.level();
        DeathRecord death = new DeathRecord(
                player.getUUID(),
                player.getName().getString(),
                level.dimension().identifier().toString(),
                player.getX(),
                player.getY(),
                player.getZ(),
                System.currentTimeMillis()
        );

        deaths.recordDeath(level.getServer(), death);
    }
}
