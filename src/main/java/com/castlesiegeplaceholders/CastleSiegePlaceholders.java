package com.castlesiegeplaceholders;

import com.castlesiegeplaceholders.expansion.CastleSiegePAPIExpansion;
import com.castlesiegeplaceholders.listener.GameEventListener;
import com.castlesiegeplaceholders.manager.ArenaGameManager;
import com.castlesiegeplaceholders.manager.SessionStatsManager;
import me.willofsteel.castlesiege.CastleSiege;
import me.willofsteel.castlesiege.api.CastleSiegeAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CastleSiegePlaceholders extends JavaPlugin {

    private CastleSiegeAPI api;
    private ArenaGameManager arenaManager;
    private SessionStatsManager sessionStatsManager;

    @Override
    public void onEnable() {
        if (!setupCastleSiege()) {
            getLogger().severe("Castle Siege not found! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.arenaManager = new ArenaGameManager(api);
        this.sessionStatsManager = new SessionStatsManager();

        Bukkit.getPluginManager().registerEvents(new GameEventListener(arenaManager, sessionStatsManager), this);

        if (setupPlaceholderAPI()) {
            getLogger().info("PlaceholderAPI expansion registered successfully!");
        } else {
            getLogger().warning("PlaceholderAPI not found - placeholders will not work!");
        }

        getLogger().info("CastleSiegePlaceholders has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CastleSiegePlaceholders has been disabled!");
    }

    private boolean setupCastleSiege() {
        if (Bukkit.getPluginManager().getPlugin("CastleSiege") == null) {
            return false;
        }

        try {
            this.api = Bukkit.getServicesManager()
                    .getRegistration(CastleSiegeAPI.class)
                    .getProvider();
            return api != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean setupPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return false;
        }

        try {
            new CastleSiegePAPIExpansion(this, api, arenaManager, sessionStatsManager).register();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public CastleSiegeAPI getApi() {
        return api;
    }

    public ArenaGameManager getArenaManager() {
        return arenaManager;
    }

    public SessionStatsManager getSessionStatsManager() {
        return sessionStatsManager;
    }
}