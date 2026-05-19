# InvSee-PNX

A PowerNukkitX plugin that lets operators view and edit any player's inventory in real-time through a familiar double chest interface — whether the player is online or offline.

Built on top of [InvMenuPNX](https://github.com/YnXahgkiO/InvMenuPNX).

## Features

Real-time bidirectional sync — changes made by the viewer are instantly reflected in the target player's inventory, and changes made by the target player are instantly reflected in the menu.

Full offline support — inventories of offline players can be viewed and edited. Changes are saved back to disk when the last viewer closes the menu.

Double chest layout — the main inventory menu displays all 36 inventory slots, armor slots, and the offhand slot in a single organized view.

Ender chest support — a separate `/enderinvsee` command opens the target's ender chest.

Permission-based access — view and modify permissions are separate, so you can grant read-only access to staff without giving them the ability to change items.

## Commands

| Command | Description |
|---|---|
| `/invsee <player>` | Open a player's inventory |
| `/enderinvsee <player>` | Open a player's ender chest |

## Permissions

| Permission | Description | Default |
|---|---|---|
| `invsee.inventory.view` | View other players' inventories | `op` |
| `invsee.inventory.modify` | Modify other players' inventories | `op` |
| `invsee.enderinventory.view` | View other players' ender chests | `op` |
| `invsee.enderinventory.modify` | Modify other players' ender chests | `op` |

## Inventory Layout

The double chest menu (54 slots) is organized as follows:

```
[ 0  1  2  3  4  5  6  7  8 ]   Hotbar
[ 9 10 11 12 13 14 15 16 17 ]
[18 19 20 21 22 23 24 25 26 ]   Main Inventory
[27 28 29 30 31 32 33 34 35 ]
[  ][  Helmet ][Chestplate][  Leggings ][  Boots  ][ Offhand ]
```

Armor and offhand slots are displayed in the bottom row with labeled separators.

## Installation

1. Download [InvMenuPNX](https://github.com/YnXahgkiO/InvMenuPNX/releases) and place it in your `plugins/` folder.
2. Download [InvSee-PNX](https://github.com/YnXahgkiO/InvSee-PNX/releases) and place it in your `plugins/` folder.
3. Restart your server.

## Building from source

```bash
gradlew.bat build
```

The compiled jar will be at `build/libs/InvSee-PNX-1.0.0.jar`.

Requires Java 21 and the [InvMenuPNX](https://github.com/YnXahgkiO/InvMenuPNX) jar in `../InvMenuPNX/build/libs/`.

## Requirements

PowerNukkitX with API `2.0.0` and Java 21.
