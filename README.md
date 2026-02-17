# WaveTrack Plugin
### Open-source project for the AsianF4rmer Network and DataThorn Technologies family.

A Spigot/Bukkit plugin for Minecraft 1.8 that allows developers to play preconfigured sound tracks to players using a single command instead of multiple `/playsound` commands.

## Features

- **Track-based Sound System**: Create reusable sound tracks with multiple sounds
- **Delay Support**: Add delays between sounds in a track (measured in ticks, 20 ticks = 1 second)
- **Nearby Player Support**: Optionally play sounds to nearby players at a reduced volume
- **Category Organization**: Organize tracks into categories for better management
- **In-game Configuration**: Create and edit tracks using commands without editing config files
- **YAML Configuration**: All tracks are saved to `config.yml` for easy manual editing
- **Tab Completion**: Full tab completion support with common Minecraft 1.8 sounds
- **Permission System**: Granular permissions for different command actions

## Commands

All commands start with `/wavetrack` (alias: `/wt`)

### Playing Tracks
```
/wavetrack play <trackname> <player>
```
Plays a track to the specified player. If the track has `nearby-players` enabled, nearby players will also hear the sounds at a reduced volume.

### Creating Tracks
```
/wavetrack create <trackname> <category>
```
Creates a new empty track in the specified category.

### Deleting Tracks
```
/wavetrack delete <trackname>
```
Permanently deletes a track.

### Adding Sounds
```
/wavetrack addsound <trackname> <sound> [volume] [pitch] [delay]
```
Adds a sound to a track.
- `sound`: The Minecraft sound name (e.g., `random.levelup`, `mob.villager.yes`)
- `volume`: Sound volume (default: 1.0)
- `pitch`: Sound pitch (default: 1.0)
- `delay`: Delay in ticks before playing this sound (default: 0)

### Removing Sounds
```
/wavetrack removesound <trackname> <index>
```
Removes a sound from a track by its index. Use `/wavetrack info <trackname>` to see indices.

### Editing Sounds
```
/wavetrack editsound <trackname> <index> <property> <value>
```
Edit a specific property of an existing sound in a track.
- `index`: The sound index (use `/wavetrack info` to see indices)
- `property`: One of `sound`, `volume`, `pitch`, `delay`
- `value`: The new value for the property

**Examples:**
```
/wavetrack editsound mytrack 0 volume 0.5
/wavetrack editsound mytrack 1 sound random.levelup
/wavetrack editsound mytrack 2 delay 20
```

### Clearing All Sounds
```
/wavetrack clearsounds <trackname>
```
Removes all sounds from a track (keeps track settings).

### Renaming Tracks
```
/wavetrack rename <trackname> <newname>
```
Renames an existing track.

### Copying Tracks
```
/wavetrack copy <trackname> <newname> [newcategory]
```
Creates a copy of a track with a new name. Optionally place it in a different category.

### Moving Tracks to a Different Category
```
/wavetrack move <trackname> <newcategory>
```
Moves a track to a different category.

### Configuring Nearby Players
```
/wavetrack setnearby <trackname> <true/false>
```
Enable or disable playing the track to nearby players.

```
/wavetrack setradius <trackname> <radius>
```
Set the radius (in blocks) for nearby player detection.

```
/wavetrack setvolumemultiplier <trackname> <multiplier>
```
Set the volume multiplier for nearby players (0.0 to 1.0).

### Viewing Track Info
```
/wavetrack info <trackname>
```
Shows detailed information about a track including all sounds and settings.

### Listing Tracks
```
/wavetrack list [category]
```
Lists all tracks, optionally filtered by category.

### Reloading Configuration
```
/wavetrack reload
```
Reloads the configuration file without restarting the server.

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `wavetrack.*` | All WaveTrack permissions | op |
| `wavetrack.play` | Play tracks to players | op |
| `wavetrack.create` | Create new tracks | op |
| `wavetrack.edit` | Edit existing tracks (add/remove sounds, change settings) | op |
| `wavetrack.delete` | Delete tracks | op |
| `wavetrack.list` | List tracks and view track info | op |
| `wavetrack.reload` | Reload plugin configuration | op |

## Configuration

The plugin automatically generates a `config.yml` file with example tracks:

```yaml
# Default settings for sounds (used when not specified)
defaults:
  volume: 1.0
  pitch: 1.0
  delay: 0
  nearby-players: false
  nearby-radius: 10
  nearby-volume-multiplier: 0.5

# Tracks organized by category
tracks:
  examples:
    level-up:
      nearby-players: true
      nearby-radius: 15
      nearby-volume-multiplier: 0.4
      sounds:
        - sound: random.levelup
          volume: 1.0
          pitch: 1.0
          delay: 0
        - sound: random.orb
          volume: 0.8
          pitch: 1.2
          delay: 5
    rare-drop:
      nearby-players: true
      nearby-radius: 20
      nearby-volume-multiplier: 0.3
      sounds:
        - sound: random.levelup
          volume: 1.0
          pitch: 0.8
          delay: 0
        - sound: fireworks.blast
          volume: 1.0
          pitch: 1.0
          delay: 5
        - sound: fireworks.twinkle
          volume: 0.8
          pitch: 1.2
          delay: 15
```

## Minecraft 1.8 Sound Reference

Below is a complete list of all available sounds in Minecraft 1.8. Use these exact names when adding sounds to tracks.

### Ambient
- `ambient.cave.cave` - Cave ambient sounds
- `ambient.weather.rain` - Rain sound
- `ambient.weather.thunder` - Thunder sound

### Damage
- `damage.fallbig` - Large fall damage
- `damage.fallsmall` - Small fall damage
- `damage.hit` - Hit sound
- `damage.hurtflesh` - Flesh hurt sound

### Dig (Block Breaking)
- `dig.cloth` - Breaking cloth/wool
- `dig.glass` - Breaking glass
- `dig.grass` - Breaking grass
- `dig.gravel` - Breaking gravel
- `dig.sand` - Breaking sand
- `dig.snow` - Breaking snow
- `dig.stone` - Breaking stone
- `dig.wood` - Breaking wood

### Fire
- `fire.fire` - Fire burning
- `fire.ignite` - Ignite/flint and steel

### Fireworks
- `fireworks.blast` - Firework explosion
- `fireworks.blast_far` - Distant firework explosion
- `fireworks.largeBlast` - Large firework explosion
- `fireworks.largeBlast_far` - Distant large firework explosion
- `fireworks.launch` - Firework launch
- `fireworks.twinkle` - Firework twinkle
- `fireworks.twinkle_far` - Distant firework twinkle

### Liquid
- `liquid.lava` - Lava ambient
- `liquid.lavapop` - Lava pop
- `liquid.splash` - Splash sound
- `liquid.swim` - Swimming
- `liquid.water` - Water ambient

### Minecart
- `minecart.base` - Minecart rolling
- `minecart.inside` - Inside minecart

### Mob Sounds
| Category | Sounds |
|----------|--------|
| **Bat** | `mob.bat.death`, `mob.bat.hurt`, `mob.bat.idle`, `mob.bat.loop`, `mob.bat.takeoff` |
| **Blaze** | `mob.blaze.breathe`, `mob.blaze.death`, `mob.blaze.hit` |
| **Cat** | `mob.cat.hiss`, `mob.cat.hitt`, `mob.cat.meow`, `mob.cat.purr`, `mob.cat.purreow` |
| **Chicken** | `mob.chicken.hurt`, `mob.chicken.plop`, `mob.chicken.say`, `mob.chicken.step` |
| **Cow** | `mob.cow.hurt`, `mob.cow.say`, `mob.cow.step` |
| **Creeper** | `mob.creeper.death`, `mob.creeper.say` |
| **Ender Dragon** | `mob.enderdragon.end`, `mob.enderdragon.growl`, `mob.enderdragon.hit`, `mob.enderdragon.wings` |
| **Enderman** | `mob.endermen.death`, `mob.endermen.hit`, `mob.endermen.idle`, `mob.endermen.portal`, `mob.endermen.scream`, `mob.endermen.stare` |
| **Ghast** | `mob.ghast.affectionate_scream`, `mob.ghast.charge`, `mob.ghast.death`, `mob.ghast.fireball`, `mob.ghast.moan`, `mob.ghast.scream` |
| **Guardian** | `mob.guardian.attack`, `mob.guardian.curse`, `mob.guardian.death`, `mob.guardian.elder.death`, `mob.guardian.elder.hit`, `mob.guardian.elder.idle`, `mob.guardian.flop`, `mob.guardian.hit`, `mob.guardian.idle`, `mob.guardian.land.death`, `mob.guardian.land.hit`, `mob.guardian.land.idle` |
| **Horse** | `mob.horse.angry`, `mob.horse.armor`, `mob.horse.breathe`, `mob.horse.death`, `mob.horse.gallop`, `mob.horse.hit`, `mob.horse.idle`, `mob.horse.jump`, `mob.horse.land`, `mob.horse.leather`, `mob.horse.soft`, `mob.horse.wood` |
| **Donkey** | `mob.horse.donkey.angry`, `mob.horse.donkey.death`, `mob.horse.donkey.hit`, `mob.horse.donkey.idle` |
| **Skeleton Horse** | `mob.horse.skeleton.death`, `mob.horse.skeleton.hit`, `mob.horse.skeleton.idle` |
| **Zombie Horse** | `mob.horse.zombie.death`, `mob.horse.zombie.hit`, `mob.horse.zombie.idle` |
| **Iron Golem** | `mob.irongolem.death`, `mob.irongolem.hit`, `mob.irongolem.throw`, `mob.irongolem.walk` |
| **Magma Cube** | `mob.magmacube.big`, `mob.magmacube.jump`, `mob.magmacube.small` |
| **Pig** | `mob.pig.death`, `mob.pig.say`, `mob.pig.step` |
| **Rabbit** | `mob.rabbit.death`, `mob.rabbit.hurt`, `mob.rabbit.idle`, `mob.rabbit.hop` |
| **Sheep** | `mob.sheep.say`, `mob.sheep.shear`, `mob.sheep.step` |
| **Silverfish** | `mob.silverfish.hit`, `mob.silverfish.kill`, `mob.silverfish.say`, `mob.silverfish.step` |
| **Skeleton** | `mob.skeleton.death`, `mob.skeleton.hurt`, `mob.skeleton.say`, `mob.skeleton.step` |
| **Slime** | `mob.slime.attack`, `mob.slime.big`, `mob.slime.small` |
| **Spider** | `mob.spider.death`, `mob.spider.say`, `mob.spider.step` |
| **Villager** | `mob.villager.death`, `mob.villager.haggle`, `mob.villager.hit`, `mob.villager.idle`, `mob.villager.no`, `mob.villager.yes` |
| **Wither** | `mob.wither.death`, `mob.wither.hurt`, `mob.wither.idle`, `mob.wither.shoot`, `mob.wither.spawn` |
| **Wolf** | `mob.wolf.bark`, `mob.wolf.death`, `mob.wolf.growl`, `mob.wolf.howl`, `mob.wolf.hurt`, `mob.wolf.panting`, `mob.wolf.shake`, `mob.wolf.step`, `mob.wolf.whine` |
| **Zombie** | `mob.zombie.death`, `mob.zombie.hurt`, `mob.zombie.infect`, `mob.zombie.metal`, `mob.zombie.remedy`, `mob.zombie.say`, `mob.zombie.step`, `mob.zombie.unfect`, `mob.zombie.wood`, `mob.zombie.woodbreak` |
| **Zombie Pigman** | `mob.zombiepig.zpig`, `mob.zombiepig.zpigangry`, `mob.zombiepig.zpigdeath`, `mob.zombiepig.zpighurt` |

### Note Blocks
- `note.bass` - Bass note
- `note.bassattack` - Bass attack
- `note.bd` - Bass drum
- `note.harp` - Harp note
- `note.hat` - Hi-hat
- `note.pling` - Pling note
- `note.snare` - Snare drum

### Portal
- `portal.portal` - Portal ambient
- `portal.travel` - Portal travel
- `portal.trigger` - Portal activation

### Random
- `random.anvil_break` - Anvil break
- `random.anvil_land` - Anvil land
- `random.anvil_use` - Anvil use
- `random.bow` - Bow shoot
- `random.bowhit` - Arrow hit
- `random.break` - Item break
- `random.burp` - Burp
- `random.chestclosed` - Chest close
- `random.chestopen` - Chest open
- `random.click` - Click
- `random.door_close` - Door close
- `random.door_open` - Door open
- `random.drink` - Drinking
- `random.eat` - Eating
- `random.explode` - Explosion
- `random.fizz` - Fizz/extinguish
- `random.fuse` - TNT fuse
- `random.glass` - Glass break
- `random.levelup` - Level up
- `random.orb` - Experience orb
- `random.pop` - Item pop
- `random.splash` - Splash
- `random.successful_hit` - Successful hit
- `random.toast` - Toast/achievement
- `random.wood_click` - Wood click

### Records (Music Discs)
- `records.11`, `records.13`, `records.blocks`, `records.cat`, `records.chirp`
- `records.far`, `records.mall`, `records.mellohi`, `records.stal`, `records.strad`
- `records.wait`, `records.ward`

### Step (Walking)
- `step.cloth` - Walking on cloth/wool
- `step.grass` - Walking on grass
- `step.gravel` - Walking on gravel
- `step.ladder` - Climbing ladder
- `step.sand` - Walking on sand
- `step.snow` - Walking on snow
- `step.stone` - Walking on stone
- `step.wood` - Walking on wood

### Tile
- `tile.piston.in` - Piston retract
- `tile.piston.out` - Piston extend

## Example Usage

### Creating a Victory Track
```
/wavetrack create victory events
/wavetrack addsound victory random.levelup 1.0 1.0 0
/wavetrack addsound victory fireworks.blast 1.0 1.2 10
/wavetrack addsound victory fireworks.twinkle 0.8 1.0 20
/wavetrack setnearby victory true
/wavetrack setradius victory 25
/wavetrack setvolumemultiplier victory 0.3
```

### Playing the Track
```
/wavetrack play victory PlayerName
```

### Testing a Track on Yourself
```
/wavetrack play victory YourName
```

## Building from Source

1. Clone the repository
2. Run `./gradlew build`
3. The plugin JAR will be in `build/libs/`

## Running a Test Server

The project includes Gradle tasks to run a local Spigot 1.8.8 test server.

### First-Time Setup

Due to DMCA and Mojang's EULA, Spigot cannot distribute pre-built server JARs. You need to build it yourself using BuildTools:

**Option 1: Use the helper script (Recommended)**
```bash
./build-spigot.sh
```
This script will download BuildTools and compile Spigot 1.8.8 automatically.

**Option 2: Manual BuildTools**
1. Download BuildTools: https://hub.spigotmc.org/jenkins/job/BuildTools/
2. Run: `java -jar BuildTools.jar --rev 1.8.8`
3. Copy the resulting JAR to `run/spigot-1.8.8.jar`

### Running the Server

Once you have the server JAR:

```bash
# Run the test server with your plugin
./gradlew runServer
```

The server will start with your plugin automatically installed. Connect with Minecraft 1.8.8 to `localhost:25565`.

### Other Server Tasks

| Task | Description |
|------|-------------|
| `./gradlew setupServer` | Creates the server directory structure |
| `./gradlew copyPlugin` | Copies the built plugin to the server |
| `./gradlew deployPlugin` | Rebuilds and deploys the plugin |
| `./gradlew cleanServer` | Deletes world data and logs |
| `./gradlew resetServer` | Resets entire server (keeps JAR) |

### Development Workflow

1. Make changes to the plugin code
2. Run `./gradlew deployPlugin` to rebuild and copy
3. In the server console, type `reload` or restart the server
4. Test your changes in-game

## License

This plugin is provided as-is for use on Minecraft servers by the DataThorn Technologies family. Modifications to the content of this plugin are prohibited. Attempts to profit from this plugin without granted permission from the DataThorn Technologies family or the AsianF4rmer Network will be answered with legal action.
