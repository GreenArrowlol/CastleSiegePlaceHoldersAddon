package com.castlesiegeplaceholders.listener;

import com.castlesiegeplaceholders.manager.ArenaGameManager;
import com.castlesiegeplaceholders.manager.SessionStatsManager;
import me.willofsteel.castlesiege.api.events.player.PlayerJoinGameEvent;
import me.willofsteel.castlesiege.api.events.player.PlayerKillEvent;
import me.willofsteel.castlesiege.api.events.player.PlayerLeaveGameEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class GameEventListener implements Listener {

    private final ArenaGameManager arenaManager;
    private final SessionStatsManager sessionStatsManager;

    public GameEventListener(ArenaGameManager arenaManager, SessionStatsManager sessionStatsManager) {
        this.arenaManager = arenaManager;
        this.sessionStatsManager = sessionStatsManager;
    }

    @EventHandler
    public void onPlayerJoinGame(PlayerJoinGameEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        arenaManager.registerPlayerGame(uuid, event.getGame());
        sessionStatsManager.resetStats(uuid);
    }

    @EventHandler
    public void onPlayerLeaveGame(PlayerLeaveGameEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        arenaManager.unregisterPlayer(uuid);
        sessionStatsManager.clearStats(uuid);
    }

    @EventHandler
    public void onPlayerKill(PlayerKillEvent event) {
        UUID killerUUID = event.getKiller().getUniqueId();
        UUID victimUUID = event.getVictim().getUniqueId();

        sessionStatsManager.addKill(killerUUID);
        sessionStatsManager.addDeath(victimUUID);
    }
}