package com.cherrymakesgames.deathcleanup.marker;

import com.cherrymakesgames.deathcleanup.data.DeathRecord;
import de.bluecolored.bluemap.api.markers.POIMarker;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DeathMarkerFactory {
    public POIMarker create(DeathRecord death) {
        long blockX = (long) Math.floor(death.x());
        long blockY = (long) Math.floor(death.y());
        long blockZ = (long) Math.floor(death.z());
        String playerName = escapeHtml(death.playerName());
        String dimensionId = escapeHtml(death.dimensionId());
        String diedAt = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(death.diedAtEpochMillis()));
        String detail = String.format(
                Locale.ROOT,
                "<strong>%s</strong><br>Dimension: %s<br>Coordinates: %d, %d, %d<br>Died: %s",
                playerName,
                dimensionId,
                blockX,
                blockY,
                blockZ,
                diedAt
        );

        return POIMarker.builder()
                .label(death.playerName() + "'s death")
                .position(death.x(), death.y(), death.z())
                .detail(detail)
                .defaultIcon()
                .build();
    }

    private static String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
