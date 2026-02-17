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
    private boolean debugMode = false;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Load debug mode from config
        debugMode = getConfig().getBoolean("debug", false);

        // Initialize track manager
        trackManager = new TrackManager(this);
        trackManager.loadTracks();

        // Register commands
        WaveTrackCommand commandExecutor = new WaveTrackCommand(this);
        getCommand("wavetrack").setExecutor(commandExecutor);
        getCommand("wavetrack").setTabCompleter(new WaveTrackTabCompleter(this));

        getLogger().info("WaveTrack has been enabled!");
        getLogger().info("Loaded " + trackManager.getTrackCount() + " sound tracks.");
        if (debugMode) {
            getLogger().info("Debug mode is ENABLED - verbose logging active.");
        }
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
        debugMode = getConfig().getBoolean("debug", false);
        trackManager.loadTracks();
        getLogger().info("WaveTrack configuration reloaded! Loaded " + trackManager.getTrackCount() + " tracks.");
        if (debugMode) {
            getLogger().info("Debug mode is ENABLED - verbose logging active.");
        }
    }

    /**
     * Checks if debug mode is enabled.
     * @return true if debug mode is active
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Sets debug mode on or off.
     * @param enabled true to enable debug mode
     */
    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
        getConfig().set("debug", enabled);
        saveConfig();
    }

    /**
     * Logs a debug message to console only if debug mode is enabled.
     * @param message the message to log
     */
    public void debug(String message) {
        if (debugMode) {
            getLogger().info("[DEBUG] " + message);
        }
    }

    /**
     * Logs a debug message with a warning level only if debug mode is enabled.
     * @param message the warning message to log
     */
    public void debugWarning(String message) {
        if (debugMode) {
            getLogger().warning("[DEBUG] " + message);
        }
    }
}

