package com.cherrymakesgames.deathcleanup.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.cherrymakesgames.deathcleanup.DeathCleanup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class DeathSavedData extends SavedData {
    public static final Codec<DeathSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DeathRecord.CODEC.listOf().fieldOf("deaths").forGetter(DeathSavedData::records)
    ).apply(instance, DeathSavedData::new));

    public static final SavedDataType<DeathSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(DeathCleanup.MOD_ID, "deaths"),
            DeathSavedData::new,
            CODEC,
            null
    );

    private final Map<UUID, DeathRecord> deaths;

    public DeathSavedData() {
        this(List.of());
    }

    private DeathSavedData(List<DeathRecord> deaths) {
        this.deaths = new LinkedHashMap<>();
        for (DeathRecord death : deaths) {
            this.deaths.put(death.playerId(), death);
        }
    }

    public static DeathSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public Optional<DeathRecord> find(UUID playerId) {
        return Optional.ofNullable(deaths.get(playerId));
    }

    public List<DeathRecord> records() {
        return List.copyOf(deaths.values());
    }

    public void upsert(DeathRecord death) {
        deaths.put(death.playerId(), death);
        setDirty();
    }

    public boolean remove(UUID playerId) {
        if (deaths.remove(playerId) == null) {
            return false;
        }

        setDirty();
        return true;
    }
}
