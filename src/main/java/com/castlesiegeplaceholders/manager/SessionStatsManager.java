package com.castlesiegeplaceholders.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionStatsManager {

    private final Map<UUID, Integer> sessionKills = new HashMap<>();
    private final Map<UUID, Integer> sessionDeaths = new HashMap<>();

    public void addKill(UUID playerUUID) {
        sessionKills.put(playerUUID, sessionKills.getOrDefault(playerUUID, 0) + 1);
    }

    public void addDeath(UUID playerUUID) {
        sessionDeaths.put(playerUUID, sessionDeaths.getOrDefault(playerUUID, 0) + 1);
    }

    public int getKills(UUID playerUUID) {
        return sessionKills.getOrDefault(playerUUID, 0);
    }

    public int getDeaths(UUID playerUUID) {
        return sessionDeaths.getOrDefault(playerUUID, 0);
    }

    public void resetStats(UUID playerUUID) {
        sessionKills.put(playerUUID, 0);
        sessionDeaths.put(playerUUID, 0);
    }

    public void clearStats(UUID playerUUID) {
        sessionKills.remove(playerUUID);
        sessionDeaths.remove(playerUUID);
    }
}