package com.wavetrack.commands;

import com.wavetrack.WaveTrack;
import com.wavetrack.managers.TrackManager;
import com.wavetrack.models.SoundTrack;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Tab completer for WaveTrack commands.
 * Provides auto-completion suggestions for command arguments.
 */
public class WaveTrackTabCompleter implements TabCompleter {

    private final WaveTrack plugin;
    private final TrackManager trackManager;

    // Complete Minecraft 1.8 sounds for auto-completion
    // These are the exact sound names used in Minecraft 1.8.x
    private static final List<String> MC_1_8_SOUNDS = Arrays.asList(
            // ========== AMBIENT ==========
            "ambient.cave.cave",
            "ambient.weather.rain",
            "ambient.weather.thunder",

            // ========== DAMAGE ==========
            "damage.fallbig",
            "damage.fallsmall",
            "damage.hit",
            "damage.hurtflesh",

            // ========== DIG ==========
            "dig.cloth",
            "dig.glass",
            "dig.grass",
            "dig.gravel",
            "dig.sand",
            "dig.snow",
            "dig.stone",
            "dig.wood",

            // ========== FIRE ==========
            "fire.fire",
            "fire.ignite",

            // ========== FIREWORKS ==========
            "fireworks.blast",
            "fireworks.blast_far",
            "fireworks.largeBlast",
            "fireworks.largeBlast_far",
            "fireworks.launch",
            "fireworks.twinkle",
            "fireworks.twinkle_far",

            // ========== LIQUID ==========
            "liquid.lava",
            "liquid.lavapop",
            "liquid.splash",
            "liquid.swim",
            "liquid.water",

            // ========== MINECART ==========
            "minecart.base",
            "minecart.inside",

            // ========== MOB - BAT ==========
            "mob.bat.death",
            "mob.bat.hurt",
            "mob.bat.idle",
            "mob.bat.loop",
            "mob.bat.takeoff",

            // ========== MOB - BLAZE ==========
            "mob.blaze.breathe",
            "mob.blaze.death",
            "mob.blaze.hit",

            // ========== MOB - CAT ==========
            "mob.cat.hiss",
            "mob.cat.hitt",
            "mob.cat.meow",
            "mob.cat.purr",
            "mob.cat.purreow",

            // ========== MOB - CHICKEN ==========
            "mob.chicken.hurt",
            "mob.chicken.plop",
            "mob.chicken.say",
            "mob.chicken.step",

            // ========== MOB - COW ==========
            "mob.cow.hurt",
            "mob.cow.say",
            "mob.cow.step",

            // ========== MOB - CREEPER ==========
            "mob.creeper.death",
            "mob.creeper.say",

            // ========== MOB - ENDERDRAGON ==========
            "mob.enderdragon.end",
            "mob.enderdragon.growl",
            "mob.enderdragon.hit",
            "mob.enderdragon.wings",

            // ========== MOB - ENDERMEN ==========
            "mob.endermen.death",
            "mob.endermen.hit",
            "mob.endermen.idle",
            "mob.endermen.portal",
            "mob.endermen.scream",
            "mob.endermen.stare",

            // ========== MOB - GHAST ==========
            "mob.ghast.affectionate_scream",
            "mob.ghast.charge",
            "mob.ghast.death",
            "mob.ghast.fireball",
            "mob.ghast.moan",
            "mob.ghast.scream",

            // ========== MOB - GUARDIAN ==========
            "mob.guardian.attack",
            "mob.guardian.curse",
            "mob.guardian.death",
            "mob.guardian.elder.death",
            "mob.guardian.elder.hit",
            "mob.guardian.elder.idle",
            "mob.guardian.flop",
            "mob.guardian.hit",
            "mob.guardian.idle",
            "mob.guardian.land.death",
            "mob.guardian.land.hit",
            "mob.guardian.land.idle",

            // ========== MOB - HORSE ==========
            "mob.horse.angry",
            "mob.horse.armor",
            "mob.horse.breathe",
            "mob.horse.death",
            "mob.horse.donkey.angry",
            "mob.horse.donkey.death",
            "mob.horse.donkey.hit",
            "mob.horse.donkey.idle",
            "mob.horse.gallop",
            "mob.horse.hit",
            "mob.horse.idle",
            "mob.horse.jump",
            "mob.horse.land",
            "mob.horse.leather",
            "mob.horse.skeleton.death",
            "mob.horse.skeleton.hit",
            "mob.horse.skeleton.idle",
            "mob.horse.soft",
            "mob.horse.wood",
            "mob.horse.zombie.death",
            "mob.horse.zombie.hit",
            "mob.horse.zombie.idle",

            // ========== MOB - IRONGOLEM ==========
            "mob.irongolem.death",
            "mob.irongolem.hit",
            "mob.irongolem.throw",
            "mob.irongolem.walk",

            // ========== MOB - MAGMACUBE ==========
            "mob.magmacube.big",
            "mob.magmacube.jump",
            "mob.magmacube.small",

            // ========== MOB - PIG ==========
            "mob.pig.death",
            "mob.pig.say",
            "mob.pig.step",

            // ========== MOB - RABBIT ==========
            "mob.rabbit.death",
            "mob.rabbit.hurt",
            "mob.rabbit.idle",
            "mob.rabbit.hop",

            // ========== MOB - SHEEP ==========
            "mob.sheep.say",
            "mob.sheep.shear",
            "mob.sheep.step",

            // ========== MOB - SILVERFISH ==========
            "mob.silverfish.hit",
            "mob.silverfish.kill",
            "mob.silverfish.say",
            "mob.silverfish.step",

            // ========== MOB - SKELETON ==========
            "mob.skeleton.death",
            "mob.skeleton.hurt",
            "mob.skeleton.say",
            "mob.skeleton.step",

            // ========== MOB - SLIME ==========
            "mob.slime.attack",
            "mob.slime.big",
            "mob.slime.small",

            // ========== MOB - SPIDER ==========
            "mob.spider.death",
            "mob.spider.say",
            "mob.spider.step",

            // ========== MOB - VILLAGER ==========
            "mob.villager.death",
            "mob.villager.haggle",
            "mob.villager.hit",
            "mob.villager.idle",
            "mob.villager.no",
            "mob.villager.yes",

            // ========== MOB - WITHER ==========
            "mob.wither.death",
            "mob.wither.hurt",
            "mob.wither.idle",
            "mob.wither.shoot",
            "mob.wither.spawn",

            // ========== MOB - WOLF ==========
            "mob.wolf.bark",
            "mob.wolf.death",
            "mob.wolf.growl",
            "mob.wolf.howl",
            "mob.wolf.hurt",
            "mob.wolf.panting",
            "mob.wolf.shake",
            "mob.wolf.step",
            "mob.wolf.whine",

            // ========== MOB - ZOMBIE ==========
            "mob.zombie.death",
            "mob.zombie.hurt",
            "mob.zombie.infect",
            "mob.zombie.metal",
            "mob.zombie.remedy",
            "mob.zombie.say",
            "mob.zombie.step",
            "mob.zombie.unfect",
            "mob.zombie.wood",
            "mob.zombie.woodbreak",

            // ========== MOB - ZOMBIE PIGMAN ==========
            "mob.zombiepig.zpig",
            "mob.zombiepig.zpigangry",
            "mob.zombiepig.zpigdeath",
            "mob.zombiepig.zpighurt",

            // ========== NOTE BLOCKS ==========
            "note.bass",
            "note.bassattack",
            "note.bd",
            "note.harp",
            "note.hat",
            "note.pling",
            "note.snare",

            // ========== PORTAL ==========
            "portal.portal",
            "portal.travel",
            "portal.trigger",

            // ========== RANDOM ==========
            "random.anvil_break",
            "random.anvil_land",
            "random.anvil_use",
            "random.bow",
            "random.bowhit",
            "random.break",
            "random.burp",
            "random.chestclosed",
            "random.chestopen",
            "random.click",
            "random.door_close",
            "random.door_open",
            "random.drink",
            "random.eat",
            "random.explode",
            "random.fizz",
            "random.fuse",
            "random.glass",
            "random.levelup",
            "random.orb",
            "random.pop",
            "random.splash",
            "random.successful_hit",
            "random.toast",
            "random.wood_click",

            // ========== RECORDS (MUSIC DISCS) ==========
            "records.11",
            "records.13",
            "records.blocks",
            "records.cat",
            "records.chirp",
            "records.far",
            "records.mall",
            "records.mellohi",
            "records.stal",
            "records.strad",
            "records.wait",
            "records.ward",

            // ========== STEP ==========
            "step.cloth",
            "step.grass",
            "step.gravel",
            "step.ladder",
            "step.sand",
            "step.snow",
            "step.stone",
            "step.wood",

            // ========== TILE ==========
            "tile.piston.in",
            "tile.piston.out"
    );

    public WaveTrackTabCompleter(WaveTrack plugin) {
        this.plugin = plugin;
        this.trackManager = plugin.getTrackManager();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Subcommand completion
            List<String> subCommands = Arrays.asList(
                    "play", "create", "delete", "addsound", "removesound", "editsound", "clearsounds",
                    "rename", "copy", "move", "setnearby", "setradius", "setvolumemultiplier",
                    "info", "list", "reload", "help"
            );
            String partial = args[0].toLowerCase();
            for (String sub : subCommands) {
                if (sub.startsWith(partial)) {
                    completions.add(sub);
                }
            }
        } else if (args.length >= 2) {
            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "play":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getTrackNames(), args[1]));
                    } else if (args.length == 3) {
                        completions.addAll(getOnlinePlayerNames(args[2]));
                    }
                    break;

                case "create":
                    if (args.length == 3) {
                        // Suggest existing categories
                        completions.addAll(filterByPrefix(trackManager.getCategories(), args[2]));
                    }
                    break;

                case "delete":
                case "info":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getTrackNames(), args[1]));
                    }
                    break;

                case "addsound":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getTrackNames(), args[1]));
                    } else if (args.length == 3) {
                        // Sound name - suggest common sounds
                        completions.addAll(filterByPrefix(MC_1_8_SOUNDS, args[2]));
                    } else if (args.length == 4) {
                        // Volume suggestions
                        completions.addAll(Arrays.asList("0.5", "1.0", "1.5", "2.0"));
                    } else if (args.length == 5) {
                        // Pitch suggestions
                        completions.addAll(Arrays.asList("0.5", "1.0", "1.5", "2.0"));
                    } else if (args.length == 6) {
                        // Delay suggestions (in ticks)
                        completions.addAll(Arrays.asList("0", "5", "10", "20", "40"));
                    }
                    break;

                case "removesound":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getTrackNames(), args[1]));
                    } else if (args.length == 3) {
                        // Suggest valid indices for the track
                        SoundTrack track = trackManager.getTrack(args[1]);
                        if (track != null) {
                            for (int i = 0; i < track.getSounds().size(); i++) {
                                completions.add(String.valueOf(i));
                            }
                        }
                    }
                    break;

                case "editsound":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getTrackNames(), args[1]));
                    } else if (args.length == 3) {
                        // Suggest valid indices for the track
                        SoundTrack editTrack = trackManager.getTrack(args[1]);
                        if (editTrack != null) {
                            for (int i = 0; i < editTrack.getSounds().size(); i++) {
                                completions.add(String.valueOf(i));
                            }
                        }
                    } else if (args.length == 4) {
                        // Property suggestions
                        completions.addAll(filterByPrefix(Arrays.asList("sound", "volume", "pitch", "delay"), args[3]));
                    } else if (args.length == 5) {
                        // Value suggestions based on property
                        String property = args[3].toLowerCase();
                        switch (property) {
                            case "sound":
                                completions.addAll(filterByPrefix(MC_1_8_SOUNDS, args[4]));
                                break;
                            case "volume":
                                completions.addAll(Arrays.asList("0.5", "1.0", "1.5", "2.0"));
                                break;
                            case "pitch":
                                completions.addAll(Arrays.asList("0.5", "1.0", "1.5", "2.0"));
                                break;
                            case "delay":
                                completions.addAll(Arrays.asList("0", "5", "10", "20", "40"));
                                break;
                        }
                    }
                    break;

                case "clearsounds":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getTrackNames(), args[1]));
                    }
                    break;

                case "rename":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getTrackNames(), args[1]));
                    }
                    // No suggestions for new name - user enters custom name
                    break;

                case "copy":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getTrackNames(), args[1]));
                    } else if (args.length == 4) {
                        // Suggest existing categories for the copy
                        completions.addAll(filterByPrefix(trackManager.getCategories(), args[3]));
                    }
                    break;

                case "move":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getTrackNames(), args[1]));
                    } else if (args.length == 3) {
                        // Suggest existing categories
                        completions.addAll(filterByPrefix(trackManager.getCategories(), args[2]));
                    }
                    break;

                case "setnearby":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getTrackNames(), args[1]));
                    } else if (args.length == 3) {
                        completions.addAll(filterByPrefix(Arrays.asList("true", "false"), args[2]));
                    }
                    break;

                case "setradius":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getTrackNames(), args[1]));
                    } else if (args.length == 3) {
                        completions.addAll(Arrays.asList("5", "10", "15", "20", "30", "50"));
                    }
                    break;

                case "setvolumemultiplier":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getTrackNames(), args[1]));
                    } else if (args.length == 3) {
                        completions.addAll(Arrays.asList("0.1", "0.25", "0.5", "0.75", "1.0"));
                    }
                    break;

                case "list":
                    if (args.length == 2) {
                        completions.addAll(filterByPrefix(trackManager.getCategories(), args[1]));
                    }
                    break;
            }
        }

        return completions;
    }

    private List<String> filterByPrefix(Iterable<String> items, String prefix) {
        List<String> result = new ArrayList<>();
        String lowerPrefix = prefix.toLowerCase();
        for (String item : items) {
            if (item.toLowerCase().startsWith(lowerPrefix)) {
                result.add(item);
            }
        }
        return result;
    }

    private List<String> filterByPrefix(Set<String> items, String prefix) {
        List<String> result = new ArrayList<>();
        String lowerPrefix = prefix.toLowerCase();
        for (String item : items) {
            if (item.toLowerCase().startsWith(lowerPrefix)) {
                result.add(item);
            }
        }
        return result;
    }

    private List<String> getOnlinePlayerNames(String prefix) {
        List<String> names = new ArrayList<>();
        String lowerPrefix = prefix.toLowerCase();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(lowerPrefix)) {
                names.add(player.getName());
            }
        }
        return names;
    }
}

