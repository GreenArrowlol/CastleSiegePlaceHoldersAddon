# CastleSiegeAddon

Advanced PlaceholderAPI expansion for CastleSiege plugin providing comprehensive arena-specific placeholders.

## Installation

1. Place CastleSiege.jar in your libs folder (required for compilation)
2. Build the project with Gradle
3. Place the compiled JAR in your plugins folder
4. Requires: CastleSiege, PlaceholderAPI

## Available Placeholders

### Arena-Specific Placeholders
Replace `<arena>` with the arena name (e.g., Castle_Main)

- `%csaddon_status_<arena>%` - Arena status (WAITING/STARTING/ACTIVE/ENDING/OFFLINE)
- `%csaddon_min_<arena>%` - Minimum players required
- `%csaddon_max_<arena>%` - Maximum players allowed
- `%csaddon_current_<arena>%` - Current player count
- `%csaddon_name_<arena>%` - Arena display name
- `%csaddon_enabled_<arena>%` - Arena enabled status (true/false)
- `%csaddon_world_<arena>%` - World name
- `%csaddon_complete_<arena>%` - Arena setup complete (true/false)
- `%csaddon_attackers_<arena>%` - Number of attackers
- `%csaddon_defenders_<arena>%` - Number of defenders
- `%csaddon_spectators_<arena>%` - Number of spectators
- `%csaddon_player_in_<arena>%` - If player is in this arena (true/false)
- `%csaddon_player_team_<arena>%` - Player's team in this arena

### Player-Specific Placeholders

- `%csaddon_current_arena%` - Arena player is currently in
- `%csaddon_current_team%` - Team player is currently on
- `%csaddon_is_playing%` - If player is in any game (true/false)

## DeluxeMenus Example

```yaml
menu_title: '&6Castle Siege Arenas'
size: 27

items:
  castle_main:
    material: DIAMOND_SWORD
    slot: 13
    display_name: '&6Castle Main Arena'
    lore:
      - '&7Status: &e%csaddon_status_Castle_Main%'
      - '&7Players: &a%csaddon_current_Castle_Main%&7/&a%csaddon_max_Castle_Main%'
      - '&7Minimum: &e%csaddon_min_Castle_Main%'
      - ''
      - '&7Attackers: &c%csaddon_attackers_Castle_Main%'
      - '&7Defenders: &9%csaddon_defenders_Castle_Main%'
      - ''
      - '&eClick to join!'
    left_click_commands:
      - '[player] csa join Castle_Main'
```

## Build Instructions

```bash
./gradlew build
```

Output: `build/libs/CastleSiegeAddon-1.0.0.jar`
