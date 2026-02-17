package com.wavetrack.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sound track containing multiple sounds.
 * Tracks can be configured to play to nearby players as well.
 */
public class SoundTrack {

    private final String name;
    private final String category;
    private final List<TrackSound> sounds;
    private boolean nearbyPlayers;
    private int nearbyRadius;
    private float nearbyVolumeMultiplier;

    public SoundTrack(String name, String category) {
        this.name = name;
        this.category = category;
        this.sounds = new ArrayList<>();
        this.nearbyPlayers = false;
        this.nearbyRadius = 10;
        this.nearbyVolumeMultiplier = 0.5f;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public List<TrackSound> getSounds() {
        return sounds;
    }

    public void addSound(TrackSound sound) {
        sounds.add(sound);
    }

    public void removeSound(int index) {
        if (index >= 0 && index < sounds.size()) {
            sounds.remove(index);
        }
    }

    public void clearSounds() {
        sounds.clear();
    }

    public boolean isNearbyPlayers() {
        return nearbyPlayers;
    }

    public void setNearbyPlayers(boolean nearbyPlayers) {
        this.nearbyPlayers = nearbyPlayers;
    }

    public int getNearbyRadius() {
        return nearbyRadius;
    }

    public void setNearbyRadius(int nearbyRadius) {
        this.nearbyRadius = nearbyRadius;
    }

    public float getNearbyVolumeMultiplier() {
        return nearbyVolumeMultiplier;
    }

    public void setNearbyVolumeMultiplier(float nearbyVolumeMultiplier) {
        this.nearbyVolumeMultiplier = nearbyVolumeMultiplier;
    }

    @Override
    public String toString() {
        return "SoundTrack{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", sounds=" + sounds.size() +
                ", nearbyPlayers=" + nearbyPlayers +
                ", nearbyRadius=" + nearbyRadius +
                ", nearbyVolumeMultiplier=" + nearbyVolumeMultiplier +
                '}';
    }
}

