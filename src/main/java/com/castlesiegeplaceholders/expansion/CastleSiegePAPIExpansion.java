package com.castlesiegeplaceholders.expansion;

import com.castlesiegeplaceholders.CastleSiegePlaceholders;
import com.castlesiegeplaceholders.manager.ArenaGameManager;
import com.castlesiegeplaceholders.manager.SessionStatsManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.willofsteel.castlesiege.CastleSiege;
import me.willofsteel.castlesiege.api.CastleSiegeAPI;
import me.willofsteel.castlesiege.api.arena.IArena;
import me.willofsteel.castlesiege.api.game.IGame;
import me.willofsteel.castlesiege.api.game.IGameState;
import me.willofsteel.castlesiege.api.game.ITeam;
import me.willofsteel.castlesiege.manager.ArenaManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CastleSiegePAPIExpansion extends PlaceholderExpansion {

    private final CastleSiegePlaceholders plugin;
    private final CastleSiegeAPI api;
    private final ArenaGameManager arenaManager;
    private final SessionStatsManager sessionStatsManager;
    private final ArenaManager csArenaManager;

    private final Map<String, ArenaCache> arenaCache = new HashMap<>();
    private long lastCacheUpdate = 0;
    private static final long CACHE_DURATION = 1000;

    public CastleSiegePAPIExpansion(CastleSiegePlaceholders plugin, CastleSiegeAPI api,
                                    ArenaGameManager arenaManager, SessionStatsManager sessionStatsManager) {
        this.plugin = plugin;
        this.api = api;
        this.arenaManager = arenaManager;
        this.sessionStatsManager = sessionStatsManager;
        this.csArenaManager = CastleSiege.getInstance().getArenaManager();
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "csplaceholders";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "CastleSiegePlaceholders";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return handleNonPlayerPlaceholders(identifier);
        }

        return handlePlayerPlaceholders(player, identifier);
    }

    private String handlePlayerPlaceholders(Player player, String identifier) {
        CastleSiegeAPI.GameUtil gameUtil = api.getGameUtil();
        UUID playerUUID = player.getUniqueId();

        if (identifier.startsWith("arena_")) {
            return handleArenaPlaceholders(player, identifier.substring(6), gameUtil, playerUUID);
        }

        if (identifier.startsWith("session_")) {
            return handleSessionPlaceholders(playerUUID, identifier.substring(8));
        }

        switch (identifier.toLowerCase()) {
            case "is_playing":
                return gameUtil.isPlaying(player) ? "Yes" : "No";

            case "is_playing_bool":
                return String.valueOf(gameUtil.isPlaying(player));

            case "current_team":
                if (gameUtil.isPlaying(player)) {
                    ITeam team = gameUtil.getPlayerTeam(player);
                    return team != null ? team.getDisplayName() : "None";
                }
                return "Not Playing";

            case "current_team_color":
                if (gameUtil.isPlaying(player)) {
                    ITeam team = gameUtil.getPlayerTeam(player);
                    return team != null ? team.getColor().toString() : "";
                }
                return "";

            case "current_arena":
                IGame game = arenaManager.getPlayerGame(playerUUID);
                if (game != null) {
                    return api.getGameUtil().getArenaName(game);
                }
                return "None";

            case "current_arena_world":
                IGame arenaGame = arenaManager.getPlayerGame(playerUUID);
                if (arenaGame != null) {
                    IArena arena = api.getGameUtil().getArena(arenaGame);
                    return arena != null && arena.getWorld() != null ? arena.getWorld().getName() : "Unknown";
                }
                return "Not Playing";

            default:
                return null;
        }
    }

    private String handleArenaPlaceholders(Player player, String identifier, CastleSiegeAPI.GameUtil gameUtil, UUID playerUUID) {
        String[] parts = identifier.split("_", 2);
        if (parts.length < 2) {
            return null;
        }

        String arenaName = parts[0];
        String placeholder = parts[1];

        ArenaCache cache = getArenaCache(arenaName);
        if (cache == null) {
            return "Invalid Arena";
        }

        switch (placeholder.toLowerCase()) {
            case "status":
                return cache.status;

            case "players":
                return String.valueOf(cache.currentPlayers);

            case "maxplayers":
                return String.valueOf(cache.maxPlayers);

            case "minplayers":
                return String.valueOf(cache.minPlayers);

            case "state":
                return cache.gameState;

            case "attackers":
                return String.valueOf(cache.attackersCount);

            case "defenders":
                return String.valueOf(cache.defendersCount);

            case "spectators":
                return String.valueOf(cache.spectatorsCount);

            case "world":
                return cache.worldName;

            case "enabled":
                return cache.enabled ? "Yes" : "No";

            case "complete":
                return cache.complete ? "Yes" : "No";

            default:
                return null;
        }
    }

    private String handleSessionPlaceholders(UUID playerUUID, String identifier) {
        switch (identifier.toLowerCase()) {
            case "kills":
                return String.valueOf(sessionStatsManager.getKills(playerUUID));

            case "deaths":
                return String.valueOf(sessionStatsManager.getDeaths(playerUUID));

            case "kdr":
                int kills = sessionStatsManager.getKills(playerUUID);
                int deaths = sessionStatsManager.getDeaths(playerUUID);
                double kdr = deaths > 0 ? (double) kills / deaths : kills;
                return String.format("%.2f", kdr);

            default:
                return null;
        }
    }

    private String handleNonPlayerPlaceholders(String identifier) {
        if (identifier.startsWith("arena_")) {
            String[] parts = identifier.substring(6).split("_", 2);
            if (parts.length < 2) {
                return null;
            }

            String arenaName = parts[0];
            String placeholder = parts[1];

            ArenaCache cache = getArenaCache(arenaName);
            if (cache == null) {
                return "Invalid Arena";
            }

            switch (placeholder.toLowerCase()) {
                case "status":
                    return cache.status;
                case "players":
                    return String.valueOf(cache.currentPlayers);
                case "maxplayers":
                    return String.valueOf(cache.maxPlayers);
                case "state":
                    return cache.gameState;
                default:
                    return null;
            }
        }

        return null;
    }

    private ArenaCache getArenaCache(String arenaName) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastCacheUpdate > CACHE_DURATION) {
            arenaCache.clear();
            lastCacheUpdate = currentTime;
        }

        return arenaCache.computeIfAbsent(arenaName, name -> {
            IArena arena = csArenaManager.getArena(name);
            if (arena == null) {
                return null;
            }

            ArenaCache cache = new ArenaCache();
            cache.enabled = arena.isEnabled();
            cache.complete = arena.isComplete();
            cache.maxPlayers = arena.getMaxPlayers();
            cache.minPlayers = arena.getMinPlayers();
            cache.worldName = arena.getWorld() != null ? arena.getWorld().getName() : "Unknown";

            IGame game = arenaManager.getActiveGame(name);

            if (game != null) {
                IGameState state = api.getGameUtil().getGameState(game);
                List<Player> players = api.getGameUtil().getPlayers(game);

                cache.currentPlayers = players.size();
                cache.gameState = state.toString();

                if (state == IGameState.WAITING) {
                    cache.status = "Waiting";
                } else if (state == IGameState.STARTING) {
                    cache.status = "Starting";
                } else if (state == IGameState.ACTIVE) {
                    cache.status = "Playing";
                } else if (state == IGameState.ENDING) {
                    cache.status = "Ending";
                } else {
                    cache.status = "Unknown";
                }

                cache.attackersCount = 0;
                cache.defendersCount = 0;
                cache.spectatorsCount = 0;

                for (Player p : players) {
                    ITeam team = api.getGameUtil().getPlayerTeam(p);
                    if (team == ITeam.ATTACKERS) {
                        cache.attackersCount++;
                    } else if (team == ITeam.DEFENDERS) {
                        cache.defendersCount++;
                    } else if (team == ITeam.SPECTATORS) {
                        cache.spectatorsCount++;
                    }
                }
            } else {
                cache.currentPlayers = 0;
                cache.gameState = "OFFLINE";
                cache.status = "Offline";
                cache.attackersCount = 0;
                cache.defendersCount = 0;
                cache.spectatorsCount = 0;
            }

            return cache;
        });
    }

    private static class ArenaCache {
        String status;
        int currentPlayers;
        int maxPlayers;
        int minPlayers;
        String gameState;
        int attackersCount;
        int defendersCount;
        int spectatorsCount;
        String worldName;
        boolean enabled;
        boolean complete;
    }
}