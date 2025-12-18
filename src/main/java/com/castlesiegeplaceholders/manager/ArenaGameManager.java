package com.castlesiegeplaceholders.manager;

import me.willofsteel.castlesiege.CastleSiege;
import me.willofsteel.castlesiege.api.CastleSiegeAPI;
import me.willofsteel.castlesiege.api.arena.IArena;
import me.willofsteel.castlesiege.api.game.IGame;
import me.willofsteel.castlesiege.manager.ArenaManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArenaGameManager {

    private final CastleSiegeAPI api;
    private final ArenaManager arenaManager;
    private final Map<UUID, IGame> playerGameMap = new HashMap<>();
    private final Map<String, IGame> arenaGameMap = new HashMap<>();

    public ArenaGameManager(CastleSiegeAPI api) {
        this.api = api;
        this.arenaManager = CastleSiege.getInstance().getArenaManager();
    }

    public void registerPlayerGame(UUID playerUUID, IGame game) {
        playerGameMap.put(playerUUID, game);
        String arenaName = api.getGameUtil().getArenaName(game);
        if (arenaName != null) {
            arenaGameMap.put(arenaName, game);
        }
    }

    public void unregisterPlayer(UUID playerUUID) {
        playerGameMap.remove(playerUUID);
    }

    public IGame getPlayerGame(UUID playerUUID) {
        return playerGameMap.get(playerUUID);
    }

    public IGame getActiveGame(String arenaName) {
        return arenaGameMap.get(arenaName);
    }

    public IArena getArena(String arenaName) {
        return arenaManager.getArena(arenaName);
    }
}