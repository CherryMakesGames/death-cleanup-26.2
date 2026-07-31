package com.cherrymakesgames.deathcleanup.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeathRecordPersistenceTest {
    @Test
    void deathRecordCodecRoundTripsEveryField() {
        DeathRecord original = new DeathRecord(
                UUID.fromString("a19e17b6-735c-45fb-aee0-f8cc7801d958"),
                "Gracz_Żółw",
                "minecraft:the_nether",
                -123.75,
                64.125,
                9876.5,
                1_786_000_123_456L
        );

        JsonElement encoded = DeathRecord.CODEC.encodeStart(JsonOps.INSTANCE, original).getOrThrow();
        DeathRecord decoded = DeathRecord.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertEquals(original, decoded);
    }

    @Test
    void savedDataCodecRoundTripsMultiplePlayers() {
        DeathRecord overworldDeath = death(
                "cc4bb0a0-8afe-4ae5-9671-e50d456f9108",
                "Cherry",
                "minecraft:overworld",
                10.25,
                -58.0,
                -30.75,
                1_786_000_000_000L
        );
        DeathRecord endDeath = death(
                "5a74309b-e9d2-4787-bbda-06fa953c57d7",
                "Cricket",
                "minecraft:the_end",
                -1.5,
                72.0,
                4_000.125,
                1_786_000_000_001L
        );
        DeathSavedData original = new DeathSavedData();
        original.upsert(overworldDeath);
        original.upsert(endDeath);

        JsonElement encoded = DeathSavedData.CODEC.encodeStart(JsonOps.INSTANCE, original).getOrThrow();
        DeathSavedData decoded = DeathSavedData.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertEquals(List.of(overworldDeath, endDeath), decoded.records());
        assertEquals(overworldDeath, decoded.find(overworldDeath.playerId()).orElseThrow());
        assertEquals(endDeath, decoded.find(endDeath.playerId()).orElseThrow());
    }

    @Test
    void upsertReplacesByUuidAndRemovalHandlesMissingRecords() {
        UUID playerId = UUID.fromString("f5a90e1a-f652-49ca-9a3c-ec7286373fe6");
        DeathRecord first = new DeathRecord(
                playerId,
                "Cherry",
                "minecraft:overworld",
                1.0,
                2.0,
                3.0,
                10L
        );
        DeathRecord replacement = new DeathRecord(
                playerId,
                "Cherry",
                "minecraft:the_nether",
                -4.0,
                5.5,
                6.0,
                20L
        );
        DeathSavedData data = new DeathSavedData();

        assertTrue(data.records().isEmpty());
        assertFalse(data.remove(playerId));

        data.upsert(first);
        data.upsert(replacement);

        assertEquals(List.of(replacement), data.records());
        assertEquals(replacement, data.find(playerId).orElseThrow());
        assertTrue(data.remove(playerId));
        assertTrue(data.records().isEmpty());
        assertFalse(data.remove(playerId));
    }

    @Test
    void emptySavedDataRoundTrips() {
        DeathSavedData empty = new DeathSavedData();

        JsonElement encoded = DeathSavedData.CODEC.encodeStart(JsonOps.INSTANCE, empty).getOrThrow();
        DeathSavedData decoded = DeathSavedData.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertTrue(decoded.records().isEmpty());
    }

    private static DeathRecord death(
            String playerId,
            String playerName,
            String dimensionId,
            double x,
            double y,
            double z,
            long diedAtEpochMillis
    ) {
        return new DeathRecord(
                UUID.fromString(playerId),
                playerName,
                dimensionId,
                x,
                y,
                z,
                diedAtEpochMillis
        );
    }
}
