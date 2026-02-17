package com.wavetrack.managers;

import com.wavetrack.WaveTrack;
import com.wavetrack.models.SoundTrack;
import com.wavetrack.models.TrackSound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manages all sound tracks, including loading, saving, and playing them.
 */
public class TrackManager {

    private final WaveTrack plugin;
    private final Map<String, SoundTrack> tracks; // Key is track name (lowercase)
    private final Map<String, String> trackCategories; // Track name -> category mapping

    // Default values
    private float defaultVolume = 1.0f;
    private float defaultPitch = 1.0f;
    private int defaultDelay = 0;
    private boolean defaultNearbyPlayers = false;
    private int defaultNearbyRadius = 10;
    private float defaultNearbyVolumeMultiplier = 0.5f;

    public TrackManager(WaveTrack plugin) {
        this.plugin = plugin;
        this.tracks = new HashMap<>();
        this.trackCategories = new HashMap<>();
    }

    /**
     * Loads all tracks from the configuration file.
     */
    public void loadTracks() {
        tracks.clear();
        trackCategories.clear();

        FileConfiguration config = plugin.getConfig();

        // Load defaults
        ConfigurationSection defaults = config.getConfigurationSection("defaults");
        if (defaults != null) {
            defaultVolume = (float) defaults.getDouble("volume", 1.0);
            defaultPitch = (float) defaults.getDouble("pitch", 1.0);
            defaultDelay = defaults.getInt("delay", 0);
            defaultNearbyPlayers = defaults.getBoolean("nearby-players", false);
            defaultNearbyRadius = defaults.getInt("nearby-radius", 10);
            defaultNearbyVolumeMultiplier = (float) defaults.getDouble("nearby-volume-multiplier", 0.5);
        }

        // Load tracks
        ConfigurationSection tracksSection = config.getConfigurationSection("tracks");
        if (tracksSection == null) {
            plugin.debugWarning("No tracks section found in config.yml");
            return;
        }

        // Iterate through categories
        for (String category : tracksSection.getKeys(false)) {
            ConfigurationSection categorySection = tracksSection.getConfigurationSection(category);
            if (categorySection == null) continue;

            // Iterate through tracks in this category
            for (String trackName : categorySection.getKeys(false)) {
                ConfigurationSection trackSection = categorySection.getConfigurationSection(trackName);
                if (trackSection == null) continue;

                SoundTrack track = new SoundTrack(trackName, category);

                // Load track settings
                track.setNearbyPlayers(trackSection.getBoolean("nearby-players", defaultNearbyPlayers));
                track.setNearbyRadius(trackSection.getInt("nearby-radius", defaultNearbyRadius));
                track.setNearbyVolumeMultiplier((float) trackSection.getDouble("nearby-volume-multiplier", defaultNearbyVolumeMultiplier));

                // Load sounds
                List<Map<?, ?>> soundsList = trackSection.getMapList("sounds");
                for (Map<?, ?> soundMap : soundsList) {
                    String soundName = (String) soundMap.get("sound");
                    if (soundName == null) continue;

                    float volume = getFloatValue(soundMap.get("volume"), defaultVolume);
                    float pitch = getFloatValue(soundMap.get("pitch"), defaultPitch);
                    int delay = getIntValue(soundMap.get("delay"), defaultDelay);

                    track.addSound(new TrackSound(soundName, volume, pitch, delay));
                }

                // Store track with lowercase name for case-insensitive lookup
                tracks.put(trackName.toLowerCase(), track);
                trackCategories.put(trackName.toLowerCase(), category);
            }
        }
    }

    /**
     * Saves a track to the configuration file.
     */
    public void saveTrack(SoundTrack track) {
        FileConfiguration config = plugin.getConfig();
        String path = "tracks." + track.getCategory() + "." + track.getName();

        config.set(path + ".nearby-players", track.isNearbyPlayers());
        config.set(path + ".nearby-radius", track.getNearbyRadius());
        config.set(path + ".nearby-volume-multiplier", track.getNearbyVolumeMultiplier());

        List<Map<String, Object>> soundsList = new ArrayList<>();
        for (TrackSound sound : track.getSounds()) {
            Map<String, Object> soundMap = new LinkedHashMap<>();
            soundMap.put("sound", sound.getSound());
            soundMap.put("volume", sound.getVolume());
            soundMap.put("pitch", sound.getPitch());
            soundMap.put("delay", sound.getDelay());
            soundsList.add(soundMap);
        }
        config.set(path + ".sounds", soundsList);

        plugin.saveConfig();

        // Update internal maps
        tracks.put(track.getName().toLowerCase(), track);
        trackCategories.put(track.getName().toLowerCase(), track.getCategory());
    }

    /**
     * Deletes a track from the configuration file.
     */
    public void deleteTrack(String trackName) {
        String lowerName = trackName.toLowerCase();
        SoundTrack track = tracks.get(lowerName);
        if (track == null) return;

        FileConfiguration config = plugin.getConfig();
        String path = "tracks." + track.getCategory() + "." + track.getName();
        config.set(path, null);

        // Check if category is now empty and remove it
        ConfigurationSection categorySection = config.getConfigurationSection("tracks." + track.getCategory());
        if (categorySection != null && categorySection.getKeys(false).isEmpty()) {
            config.set("tracks." + track.getCategory(), null);
        }

        plugin.saveConfig();

        tracks.remove(lowerName);
        trackCategories.remove(lowerName);
    }

    /**
     * Plays a track to a player.
     */
    public void playTrack(String trackName, Player targetPlayer) {
        SoundTrack track = tracks.get(trackName.toLowerCase());
        if (track == null) {
            plugin.getLogger().warning("Track not found: " + trackName);
            return;
        }

        Location location = targetPlayer.getLocation();

        plugin.debug("Playing track '" + trackName + "' to player " + targetPlayer.getName());
        plugin.debug("Track has " + track.getSounds().size() + " sound(s), nearby-players: " + track.isNearbyPlayers());

        // Play sounds to target player
        for (TrackSound sound : track.getSounds()) {
            plugin.debug("  Sound: " + sound.getSound() + " (vol=" + sound.getVolume() + ", pitch=" + sound.getPitch() + ", delay=" + sound.getDelay() + ")");
            if (sound.getDelay() > 0) {
                // Schedule delayed sound
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    targetPlayer.playSound(location, sound.getSound(), sound.getVolume(), sound.getPitch());
                }, sound.getDelay());
            } else {
                // Play immediately
                targetPlayer.playSound(location, sound.getSound(), sound.getVolume(), sound.getPitch());
            }
        }

        // Play to nearby players if enabled
        if (track.isNearbyPlayers()) {
            float volumeMultiplier = track.getNearbyVolumeMultiplier();
            int radius = track.getNearbyRadius();
            int radiusSquared = radius * radius;
            int nearbyCount = 0;

            for (Player nearbyPlayer : targetPlayer.getWorld().getPlayers()) {
                // Skip the target player
                if (nearbyPlayer.equals(targetPlayer)) continue;

                // Check if player is within radius
                if (nearbyPlayer.getLocation().distanceSquared(location) <= radiusSquared) {
                    nearbyCount++;
                    for (TrackSound sound : track.getSounds()) {
                        float nearbyVolume = sound.getVolume() * volumeMultiplier;

                        if (sound.getDelay() > 0) {
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                nearbyPlayer.playSound(location, sound.getSound(), nearbyVolume, sound.getPitch());
                            }, sound.getDelay());
                        } else {
                            nearbyPlayer.playSound(location, sound.getSound(), nearbyVolume, sound.getPitch());
                        }
                    }
                }
            }
            plugin.debug("Played to " + nearbyCount + " nearby player(s) within radius " + radius);
        }
    }

    /**
     * Gets a track by name (case-insensitive).
     */
    public SoundTrack getTrack(String name) {
        return tracks.get(name.toLowerCase());
    }

    /**
     * Checks if a track exists.
     */
    public boolean trackExists(String name) {
        return tracks.containsKey(name.toLowerCase());
    }

    /**
     * Gets all track names.
     */
    public Set<String> getTrackNames() {
        Set<String> names = new HashSet<>();
        for (SoundTrack track : tracks.values()) {
            names.add(track.getName());
        }
        return names;
    }

    /**
     * Gets all tracks organized by category.
     */
    public Map<String, List<SoundTrack>> getTracksByCategory() {
        Map<String, List<SoundTrack>> result = new LinkedHashMap<>();
        for (SoundTrack track : tracks.values()) {
            result.computeIfAbsent(track.getCategory(), k -> new ArrayList<>()).add(track);
        }
        return result;
    }

    /**
     * Gets the total number of tracks.
     */
    public int getTrackCount() {
        return tracks.size();
    }

    /**
     * Gets all category names.
     */
    public Set<String> getCategories() {
        Set<String> categories = new HashSet<>();
        for (SoundTrack track : tracks.values()) {
            categories.add(track.getCategory());
        }
        return categories;
    }

    // Helper methods for type conversion
    private float getFloatValue(Object value, float defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        try {
            return Float.parseFloat(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int getIntValue(Object value, int defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public float getDefaultVolume() {
        return defaultVolume;
    }

    public float getDefaultPitch() {
        return defaultPitch;
    }

    public int getDefaultDelay() {
        return defaultDelay;
    }

    public boolean isDefaultNearbyPlayers() {
        return defaultNearbyPlayers;
    }

    public int getDefaultNearbyRadius() {
        return defaultNearbyRadius;
    }

    public float getDefaultNearbyVolumeMultiplier() {
        return defaultNearbyVolumeMultiplier;
    }
}

