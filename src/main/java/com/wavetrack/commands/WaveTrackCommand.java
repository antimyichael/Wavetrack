package com.wavetrack.commands;

import com.wavetrack.WaveTrack;
import com.wavetrack.managers.TrackManager;
import com.wavetrack.models.SoundTrack;
import com.wavetrack.models.TrackSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Main command executor for WaveTrack plugin.
 * Handles all /wavetrack subcommands.
 */
public class WaveTrackCommand implements CommandExecutor {

    private final WaveTrack plugin;
    private final TrackManager trackManager;

    private static final String PREFIX = ChatColor.GOLD + "[WaveTrack] " + ChatColor.RESET;

    public WaveTrackCommand(WaveTrack plugin) {
        this.plugin = plugin;
        this.trackManager = plugin.getTrackManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "play":
                return handlePlay(sender, args);
            case "create":
                return handleCreate(sender, args);
            case "delete":
                return handleDelete(sender, args);
            case "addsound":
                return handleAddSound(sender, args);
            case "removesound":
                return handleRemoveSound(sender, args);
            case "editsound":
                return handleEditSound(sender, args);
            case "clearsounds":
                return handleClearSounds(sender, args);
            case "rename":
                return handleRename(sender, args);
            case "copy":
                return handleCopy(sender, args);
            case "move":
                return handleMove(sender, args);
            case "setnearby":
                return handleSetNearby(sender, args);
            case "setradius":
                return handleSetRadius(sender, args);
            case "setvolumemultiplier":
                return handleSetVolumeMultiplier(sender, args);
            case "info":
                return handleInfo(sender, args);
            case "list":
                return handleList(sender, args);
            case "reload":
                return handleReload(sender);
            case "debug":
                return handleDebug(sender, args);
            case "help":
                sendHelp(sender);
                return true;
            default:
                sender.sendMessage(PREFIX + ChatColor.RED + "Unknown subcommand. Use /wavetrack help for a list of commands.");
                return true;
        }
    }

    /**
     * Handles /wavetrack play <trackname> <player>
     */
    private boolean handlePlay(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.play")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack play <trackname> <player>");
            return true;
        }

        String trackName = args[1];
        String playerName = args[2];

        if (!trackManager.trackExists(trackName)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + trackName + "' does not exist.");
            return true;
        }

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Player '" + playerName + "' is not online.");
            return true;
        }

        trackManager.playTrack(trackName, target);
        sender.sendMessage(PREFIX + ChatColor.GREEN + "Playing track '" + trackName + "' to " + target.getName() + ".");
        return true;
    }

    /**
     * Handles /wavetrack create <trackname> <category>
     */
    private boolean handleCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.create")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack create <trackname> <category>");
            return true;
        }

        String trackName = args[1];
        String category = args[2];

        if (trackManager.trackExists(trackName)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + trackName + "' already exists.");
            return true;
        }

        SoundTrack track = new SoundTrack(trackName, category);
        track.setNearbyPlayers(trackManager.isDefaultNearbyPlayers());
        track.setNearbyRadius(trackManager.getDefaultNearbyRadius());
        track.setNearbyVolumeMultiplier(trackManager.getDefaultNearbyVolumeMultiplier());
        trackManager.saveTrack(track);

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Track '" + trackName + "' created in category '" + category + "'.");
        sender.sendMessage(PREFIX + ChatColor.YELLOW + "Use /wavetrack addsound " + trackName + " <sound> to add sounds.");
        return true;
    }

    /**
     * Handles /wavetrack delete <trackname>
     */
    private boolean handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.delete")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack delete <trackname>");
            return true;
        }

        String trackName = args[1];

        if (!trackManager.trackExists(trackName)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + trackName + "' does not exist.");
            return true;
        }

        trackManager.deleteTrack(trackName);
        sender.sendMessage(PREFIX + ChatColor.GREEN + "Track '" + trackName + "' has been deleted.");
        return true;
    }

    /**
     * Handles /wavetrack addsound <trackname> <sound> [volume] [pitch] [delay]
     */
    private boolean handleAddSound(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.edit")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack addsound <trackname> <sound> [volume] [pitch] [delay]");
            return true;
        }

        String trackName = args[1];
        String soundName = args[2];

        SoundTrack track = trackManager.getTrack(trackName);
        if (track == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + trackName + "' does not exist.");
            return true;
        }

        float volume = trackManager.getDefaultVolume();
        float pitch = trackManager.getDefaultPitch();
        int delay = trackManager.getDefaultDelay();

        try {
            if (args.length >= 4) volume = Float.parseFloat(args[3]);
            if (args.length >= 5) pitch = Float.parseFloat(args[4]);
            if (args.length >= 6) delay = Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Invalid number format. Volume and pitch should be decimals, delay should be an integer.");
            return true;
        }

        track.addSound(new TrackSound(soundName, volume, pitch, delay));
        trackManager.saveTrack(track);

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Added sound '" + soundName + "' to track '" + trackName + "'.");
        sender.sendMessage(PREFIX + ChatColor.GRAY + "Volume: " + volume + ", Pitch: " + pitch + ", Delay: " + delay + " ticks");
        return true;
    }

    /**
     * Handles /wavetrack removesound <trackname> <index>
     */
    private boolean handleRemoveSound(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.edit")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack removesound <trackname> <index>");
            sender.sendMessage(PREFIX + ChatColor.GRAY + "Use /wavetrack info <trackname> to see sound indices.");
            return true;
        }

        String trackName = args[1];
        int index;

        try {
            index = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Index must be a number.");
            return true;
        }

        SoundTrack track = trackManager.getTrack(trackName);
        if (track == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + trackName + "' does not exist.");
            return true;
        }

        if (index < 0 || index >= track.getSounds().size()) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Invalid index. Track has " + track.getSounds().size() + " sounds (indices 0-" + (track.getSounds().size() - 1) + ").");
            return true;
        }

        TrackSound removed = track.getSounds().get(index);
        track.removeSound(index);
        trackManager.saveTrack(track);

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Removed sound '" + removed.getSound() + "' from track '" + trackName + "'.");
        return true;
    }

    /**
     * Handles /wavetrack editsound <trackname> <index> <property> <value>
     * Properties: sound, volume, pitch, delay
     */
    private boolean handleEditSound(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.edit")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 5) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack editsound <trackname> <index> <property> <value>");
            sender.sendMessage(PREFIX + ChatColor.GRAY + "Properties: sound, volume, pitch, delay");
            sender.sendMessage(PREFIX + ChatColor.GRAY + "Example: /wavetrack editsound mytrack 0 volume 0.5");
            return true;
        }

        String trackName = args[1];
        int index;
        String property = args[3].toLowerCase();
        String value = args[4];

        try {
            index = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Index must be a number.");
            return true;
        }

        SoundTrack track = trackManager.getTrack(trackName);
        if (track == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + trackName + "' does not exist.");
            return true;
        }

        if (index < 0 || index >= track.getSounds().size()) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Invalid index. Track has " + track.getSounds().size() + " sounds (indices 0-" + (track.getSounds().size() - 1) + ").");
            return true;
        }

        TrackSound oldSound = track.getSounds().get(index);
        String newSoundName = oldSound.getSound();
        float newVolume = oldSound.getVolume();
        float newPitch = oldSound.getPitch();
        int newDelay = oldSound.getDelay();

        try {
            switch (property) {
                case "sound":
                    newSoundName = value;
                    break;
                case "volume":
                    newVolume = Float.parseFloat(value);
                    if (newVolume < 0) {
                        sender.sendMessage(PREFIX + ChatColor.RED + "Volume cannot be negative.");
                        return true;
                    }
                    break;
                case "pitch":
                    newPitch = Float.parseFloat(value);
                    if (newPitch <= 0) {
                        sender.sendMessage(PREFIX + ChatColor.RED + "Pitch must be greater than 0.");
                        return true;
                    }
                    break;
                case "delay":
                    newDelay = Integer.parseInt(value);
                    if (newDelay < 0) {
                        sender.sendMessage(PREFIX + ChatColor.RED + "Delay cannot be negative.");
                        return true;
                    }
                    break;
                default:
                    sender.sendMessage(PREFIX + ChatColor.RED + "Unknown property '" + property + "'. Valid properties: sound, volume, pitch, delay");
                    return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Invalid number format for " + property + ".");
            return true;
        }

        // Remove old sound and add new one at same position
        track.getSounds().set(index, new TrackSound(newSoundName, newVolume, newPitch, newDelay));
        trackManager.saveTrack(track);

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Updated sound at index " + index + " in track '" + trackName + "'.");
        sender.sendMessage(PREFIX + ChatColor.GRAY + "Sound: " + newSoundName + ", Volume: " + newVolume + ", Pitch: " + newPitch + ", Delay: " + newDelay);
        return true;
    }

    /**
     * Handles /wavetrack clearsounds <trackname>
     */
    private boolean handleClearSounds(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.edit")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack clearsounds <trackname>");
            return true;
        }

        String trackName = args[1];

        SoundTrack track = trackManager.getTrack(trackName);
        if (track == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + trackName + "' does not exist.");
            return true;
        }

        int soundCount = track.getSounds().size();
        track.clearSounds();
        trackManager.saveTrack(track);

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Cleared " + soundCount + " sounds from track '" + trackName + "'.");
        return true;
    }

    /**
     * Handles /wavetrack rename <trackname> <newname>
     */
    private boolean handleRename(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.edit")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack rename <trackname> <newname>");
            return true;
        }

        String oldName = args[1];
        String newName = args[2];

        SoundTrack oldTrack = trackManager.getTrack(oldName);
        if (oldTrack == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + oldName + "' does not exist.");
            return true;
        }

        if (trackManager.trackExists(newName)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "A track named '" + newName + "' already exists.");
            return true;
        }

        // Create new track with new name but same settings
        SoundTrack newTrack = new SoundTrack(newName, oldTrack.getCategory());
        newTrack.setNearbyPlayers(oldTrack.isNearbyPlayers());
        newTrack.setNearbyRadius(oldTrack.getNearbyRadius());
        newTrack.setNearbyVolumeMultiplier(oldTrack.getNearbyVolumeMultiplier());
        for (TrackSound sound : oldTrack.getSounds()) {
            newTrack.addSound(new TrackSound(sound.getSound(), sound.getVolume(), sound.getPitch(), sound.getDelay()));
        }

        // Delete old track and save new one
        trackManager.deleteTrack(oldName);
        trackManager.saveTrack(newTrack);

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Renamed track '" + oldName + "' to '" + newName + "'.");
        return true;
    }

    /**
     * Handles /wavetrack copy <trackname> <newname> [newcategory]
     */
    private boolean handleCopy(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.create")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack copy <trackname> <newname> [newcategory]");
            return true;
        }

        String sourceName = args[1];
        String newName = args[2];

        SoundTrack sourceTrack = trackManager.getTrack(sourceName);
        if (sourceTrack == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + sourceName + "' does not exist.");
            return true;
        }

        if (trackManager.trackExists(newName)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "A track named '" + newName + "' already exists.");
            return true;
        }

        String category = args.length >= 4 ? args[3] : sourceTrack.getCategory();

        // Create copy with same settings
        SoundTrack newTrack = new SoundTrack(newName, category);
        newTrack.setNearbyPlayers(sourceTrack.isNearbyPlayers());
        newTrack.setNearbyRadius(sourceTrack.getNearbyRadius());
        newTrack.setNearbyVolumeMultiplier(sourceTrack.getNearbyVolumeMultiplier());
        for (TrackSound sound : sourceTrack.getSounds()) {
            newTrack.addSound(new TrackSound(sound.getSound(), sound.getVolume(), sound.getPitch(), sound.getDelay()));
        }

        trackManager.saveTrack(newTrack);

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Copied track '" + sourceName + "' to '" + newName + "' in category '" + category + "'.");
        return true;
    }

    /**
     * Handles /wavetrack move <trackname> <newcategory>
     */
    private boolean handleMove(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.edit")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack move <trackname> <newcategory>");
            return true;
        }

        String trackName = args[1];
        String newCategory = args[2];

        SoundTrack oldTrack = trackManager.getTrack(trackName);
        if (oldTrack == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + trackName + "' does not exist.");
            return true;
        }

        String oldCategory = oldTrack.getCategory();
        if (oldCategory.equalsIgnoreCase(newCategory)) {
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "Track '" + trackName + "' is already in category '" + newCategory + "'.");
            return true;
        }

        // Create new track in new category with same settings
        SoundTrack newTrack = new SoundTrack(trackName, newCategory);
        newTrack.setNearbyPlayers(oldTrack.isNearbyPlayers());
        newTrack.setNearbyRadius(oldTrack.getNearbyRadius());
        newTrack.setNearbyVolumeMultiplier(oldTrack.getNearbyVolumeMultiplier());
        for (TrackSound sound : oldTrack.getSounds()) {
            newTrack.addSound(new TrackSound(sound.getSound(), sound.getVolume(), sound.getPitch(), sound.getDelay()));
        }

        // Delete old track and save new one
        trackManager.deleteTrack(trackName);
        trackManager.saveTrack(newTrack);

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Moved track '" + trackName + "' from category '" + oldCategory + "' to '" + newCategory + "'.");
        return true;
    }

    /**
     * Handles /wavetrack setnearby <trackname> <true/false>
     */
    private boolean handleSetNearby(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.edit")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack setnearby <trackname> <true/false>");
            return true;
        }

        String trackName = args[1];
        String value = args[2].toLowerCase();

        SoundTrack track = trackManager.getTrack(trackName);
        if (track == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + trackName + "' does not exist.");
            return true;
        }

        if (!value.equals("true") && !value.equals("false")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Value must be 'true' or 'false'.");
            return true;
        }

        boolean nearbyPlayers = value.equals("true");
        track.setNearbyPlayers(nearbyPlayers);
        trackManager.saveTrack(track);

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Track '" + trackName + "' nearby players set to " + nearbyPlayers + ".");
        return true;
    }

    /**
     * Handles /wavetrack setradius <trackname> <radius>
     */
    private boolean handleSetRadius(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.edit")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack setradius <trackname> <radius>");
            return true;
        }

        String trackName = args[1];
        int radius;

        try {
            radius = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Radius must be a number.");
            return true;
        }

        if (radius <= 0) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Radius must be a positive number.");
            return true;
        }

        SoundTrack track = trackManager.getTrack(trackName);
        if (track == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + trackName + "' does not exist.");
            return true;
        }

        track.setNearbyRadius(radius);
        trackManager.saveTrack(track);

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Track '" + trackName + "' nearby radius set to " + radius + " blocks.");
        return true;
    }

    /**
     * Handles /wavetrack setvolumemultiplier <trackname> <multiplier>
     */
    private boolean handleSetVolumeMultiplier(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.edit")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack setvolumemultiplier <trackname> <multiplier>");
            return true;
        }

        String trackName = args[1];
        float multiplier;

        try {
            multiplier = Float.parseFloat(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Multiplier must be a number.");
            return true;
        }

        if (multiplier < 0 || multiplier > 1) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Multiplier must be between 0.0 and 1.0.");
            return true;
        }

        SoundTrack track = trackManager.getTrack(trackName);
        if (track == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + trackName + "' does not exist.");
            return true;
        }

        track.setNearbyVolumeMultiplier(multiplier);
        trackManager.saveTrack(track);

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Track '" + trackName + "' nearby volume multiplier set to " + multiplier + ".");
        return true;
    }

    /**
     * Handles /wavetrack info <trackname>
     */
    private boolean handleInfo(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.list")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack info <trackname>");
            return true;
        }

        String trackName = args[1];
        SoundTrack track = trackManager.getTrack(trackName);

        if (track == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Track '" + trackName + "' does not exist.");
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "=== Track Info: " + track.getName() + " ===");
        sender.sendMessage(ChatColor.YELLOW + "Category: " + ChatColor.WHITE + track.getCategory());
        sender.sendMessage(ChatColor.YELLOW + "Nearby Players: " + ChatColor.WHITE + track.isNearbyPlayers());
        sender.sendMessage(ChatColor.YELLOW + "Nearby Radius: " + ChatColor.WHITE + track.getNearbyRadius() + " blocks");
        sender.sendMessage(ChatColor.YELLOW + "Nearby Volume Multiplier: " + ChatColor.WHITE + track.getNearbyVolumeMultiplier());
        sender.sendMessage(ChatColor.YELLOW + "Sounds (" + track.getSounds().size() + "):");

        int index = 0;
        for (TrackSound sound : track.getSounds()) {
            sender.sendMessage(ChatColor.GRAY + "  [" + index + "] " + ChatColor.WHITE + sound.getSound() +
                    ChatColor.GRAY + " (vol: " + sound.getVolume() + ", pitch: " + sound.getPitch() + ", delay: " + sound.getDelay() + ")");
            index++;
        }

        return true;
    }

    /**
     * Handles /wavetrack list [category]
     */
    private boolean handleList(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.list")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        Map<String, List<SoundTrack>> tracksByCategory = trackManager.getTracksByCategory();

        if (tracksByCategory.isEmpty()) {
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "No tracks configured.");
            return true;
        }

        // If a specific category is requested
        if (args.length >= 2) {
            String category = args[1];
            List<SoundTrack> tracks = tracksByCategory.get(category);
            if (tracks == null || tracks.isEmpty()) {
                sender.sendMessage(PREFIX + ChatColor.RED + "No tracks found in category '" + category + "'.");
                return true;
            }

            sender.sendMessage(ChatColor.GOLD + "=== Tracks in '" + category + "' ===");
            for (SoundTrack track : tracks) {
                sender.sendMessage(ChatColor.YELLOW + "  - " + ChatColor.WHITE + track.getName() +
                        ChatColor.GRAY + " (" + track.getSounds().size() + " sounds)");
            }
            return true;
        }

        // List all tracks by category
        sender.sendMessage(ChatColor.GOLD + "=== All Tracks (" + trackManager.getTrackCount() + " total) ===");
        for (Map.Entry<String, List<SoundTrack>> entry : tracksByCategory.entrySet()) {
            sender.sendMessage(ChatColor.YELLOW + entry.getKey() + ":");
            for (SoundTrack track : entry.getValue()) {
                sender.sendMessage(ChatColor.GRAY + "  - " + ChatColor.WHITE + track.getName() +
                        ChatColor.GRAY + " (" + track.getSounds().size() + " sounds" +
                        (track.isNearbyPlayers() ? ", nearby: " + track.getNearbyRadius() + "b" : "") + ")");
            }
        }

        return true;
    }

    /**
     * Handles /wavetrack reload
     */
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("wavetrack.reload")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        plugin.reload();
        sender.sendMessage(PREFIX + ChatColor.GREEN + "Configuration reloaded! Loaded " + trackManager.getTrackCount() + " tracks.");
        return true;
    }

    /**
     * Handles /wavetrack debug [on/off]
     */
    private boolean handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wavetrack.reload")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            // Toggle current state
            boolean newState = !plugin.isDebugMode();
            plugin.setDebugMode(newState);
            if (newState) {
                sender.sendMessage(PREFIX + ChatColor.GREEN + "Debug mode " + ChatColor.YELLOW + "ENABLED" + ChatColor.GREEN + ". Verbose logging is now active.");
            } else {
                sender.sendMessage(PREFIX + ChatColor.GREEN + "Debug mode " + ChatColor.GRAY + "DISABLED" + ChatColor.GREEN + ". Console output reduced.");
            }
            return true;
        }

        String setting = args[1].toLowerCase();
        boolean enable;

        switch (setting) {
            case "on":
            case "true":
            case "enable":
            case "enabled":
                enable = true;
                break;
            case "off":
            case "false":
            case "disable":
            case "disabled":
                enable = false;
                break;
            default:
                sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /wavetrack debug [on/off]");
                return true;
        }

        plugin.setDebugMode(enable);
        if (enable) {
            sender.sendMessage(PREFIX + ChatColor.GREEN + "Debug mode " + ChatColor.YELLOW + "ENABLED" + ChatColor.GREEN + ". Verbose logging is now active.");
        } else {
            sender.sendMessage(PREFIX + ChatColor.GREEN + "Debug mode " + ChatColor.GRAY + "DISABLED" + ChatColor.GREEN + ". Console output reduced.");
        }
        return true;
    }

    /**
     * Sends help message to sender.
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== WaveTrack Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack play <trackname> <player>" + ChatColor.GRAY + " - Play a track to a player");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack create <trackname> <category>" + ChatColor.GRAY + " - Create a new track");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack delete <trackname>" + ChatColor.GRAY + " - Delete a track");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack copy <trackname> <newname> [category]" + ChatColor.GRAY + " - Copy a track");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack rename <trackname> <newname>" + ChatColor.GRAY + " - Rename a track");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack move <trackname> <newcategory>" + ChatColor.GRAY + " - Move track to category");
        sender.sendMessage(ChatColor.GOLD + "--- Sound Management ---");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack addsound <trackname> <sound> [vol] [pitch] [delay]" + ChatColor.GRAY + " - Add sound");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack removesound <trackname> <index>" + ChatColor.GRAY + " - Remove sound");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack editsound <trackname> <index> <prop> <value>" + ChatColor.GRAY + " - Edit sound");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack clearsounds <trackname>" + ChatColor.GRAY + " - Clear all sounds");
        sender.sendMessage(ChatColor.GOLD + "--- Track Settings ---");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack setnearby <trackname> <true/false>" + ChatColor.GRAY + " - Nearby playback");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack setradius <trackname> <radius>" + ChatColor.GRAY + " - Nearby radius");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack setvolumemultiplier <trackname> <mult>" + ChatColor.GRAY + " - Nearby volume");
        sender.sendMessage(ChatColor.GOLD + "--- Info & Admin ---");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack info <trackname>" + ChatColor.GRAY + " - View track details");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack list [category]" + ChatColor.GRAY + " - List all tracks");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack reload" + ChatColor.GRAY + " - Reload configuration");
        sender.sendMessage(ChatColor.YELLOW + "/wavetrack debug [on/off]" + ChatColor.GRAY + " - Toggle debug/verbose mode");
    }
}

