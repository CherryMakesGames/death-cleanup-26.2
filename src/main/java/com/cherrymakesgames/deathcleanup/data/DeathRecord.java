package com.cherrymakesgames.deathcleanup.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;
import java.util.UUID;

public record DeathRecord(
        UUID playerId,
        String playerName,
        String dimensionId,
        double x,
        double y,
        double z,
        long diedAtEpochMillis
) {
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);

    public static final Codec<DeathRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUID_CODEC.fieldOf("player_id").forGetter(DeathRecord::playerId),
            Codec.STRING.fieldOf("player_name").forGetter(DeathRecord::playerName),
            Codec.STRING.fieldOf("dimension_id").forGetter(DeathRecord::dimensionId),
            Codec.DOUBLE.fieldOf("x").forGetter(DeathRecord::x),
            Codec.DOUBLE.fieldOf("y").forGetter(DeathRecord::y),
            Codec.DOUBLE.fieldOf("z").forGetter(DeathRecord::z),
            Codec.LONG.fieldOf("died_at_epoch_millis").forGetter(DeathRecord::diedAtEpochMillis)
    ).apply(instance, DeathRecord::new));

    public DeathRecord {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(playerName, "playerName");
        Objects.requireNonNull(dimensionId, "dimensionId");
    }
}
