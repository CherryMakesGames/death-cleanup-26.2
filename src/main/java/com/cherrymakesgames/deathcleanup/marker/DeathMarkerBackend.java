package com.cherrymakesgames.deathcleanup.marker;

import com.cherrymakesgames.deathcleanup.data.DeathRecord;

import java.util.Collection;
import java.util.UUID;

public interface DeathMarkerBackend {
    void upsert(DeathRecord death);

    void remove(UUID playerId);

    void replaceAll(Collection<DeathRecord> deaths);
}
