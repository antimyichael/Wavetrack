package com.wavetrack;

import com.wavetrack.commands.WaveTrackCommand;
import com.wavetrack.commands.WaveTrackTabCompleter;
import com.wavetrack.managers.TrackManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * WaveTrack - A Spigot plugin for playing preconfigured sound tracks to players.
 * Allows developers to create custom sound tracks with multiple sounds and play them with a single command.
 */
public class WaveTrack extends JavaPlugin {

    private static WaveTrack instance;
    private TrackManager trackManager;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Initialize track manager
        trackManager = new TrackManager(this);
        trackManager.loadTracks();

        // Register commands
        WaveTrackCommand commandExecutor = new WaveTrackCommand(this);
        getCommand("wavetrack").setExecutor(commandExecutor);
        getCommand("wavetrack").setTabCompleter(new WaveTrackTabCompleter(this));

        getLogger().info("WaveTrack has been enabled!");
        getLogger().info("Loaded " + trackManager.getTrackCount() + " sound tracks.");
    }

    @Override
    public void onDisable() {
        getLogger().info("WaveTrack has been disabled!");
    }

    public static WaveTrack getInstance() {
        return instance;
    }

    public TrackManager getTrackManager() {
        return trackManager;
    }

    public void reload() {
        reloadConfig();
        trackManager.loadTracks();
        getLogger().info("WaveTrack configuration reloaded! Loaded " + trackManager.getTrackCount() + " tracks.");
    }
}

