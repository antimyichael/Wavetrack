package com.wavetrack.models;

/**
 * Represents a single sound entry within a track.
 * Contains the sound name, volume, pitch, and delay settings.
 */
public class TrackSound {

    private final String sound;
    private final float volume;
    private final float pitch;
    private final int delay; // Delay in ticks before playing this sound

    public TrackSound(String sound, float volume, float pitch, int delay) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.delay = delay;
    }

    public String getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public String toString() {
        return "TrackSound{" +
                "sound='" + sound + '\'' +
                ", volume=" + volume +
                ", pitch=" + pitch +
                ", delay=" + delay +
                '}';
    }
}

