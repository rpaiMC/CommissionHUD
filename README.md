# CommissionHud

A simple Fabric mod for Minecraft 1.21.5 that displays your Hypixel SkyBlock mining commissions on screen.
Join the discord server!
https://discord.gg/u8b6cj2DED

## Features

- Shows active commissions from Dwarven Mines, Crystal Hollows, and Glacite Tunnels
- Progress bars with customizable colors
- Adjustable position (drag to move), scale, and text color
- Two display modes: show everywhere or only in mining areas (Commissions persist when leaving mining areas if enabled)

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.5
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Drop the mod jar into your `.minecraft/mods` folder
4. Launch the game

## Setup on Hypixel

The mod reads commission data from the tab list. You need to enable the Commissions widget in Hypixel:

1. Join Hypixel SkyBlock
2. Type `/widgets` or go to SkyBlock Menu → Settings → Personal → User Interface → Player List Info
3. Enable the "Commissions" widget

## Usage

Type `/chud` in-game to open the config screen.

From there you can:
- Toggle the HUD on/off
- Show or hide progress percentages
- Switch between "Everywhere" and "Mining Islands Only" display modes
- Adjust the scale
- Pick custom colors for text and progress bars
- Drag the preview to reposition the HUD

## Building from Source

```bash
./gradlew build
```

The compiled jar will be in `build/libs/`.

## Requirements

- Minecraft 1.21.5
- Fabric Loader 0.16.9+
- Fabric API

## License

MIT
