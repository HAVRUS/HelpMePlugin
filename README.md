# HelpMe

[![Paper](https://img.shields.io/badge/Paper-26.1.2-blue)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-25-orange)](https://adoptium.net/)
[![Build](https://img.shields.io/badge/build-Maven-C71A36)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/license-MIT-green)](LICENSE)

A lightweight server-side plugin for [Paper](https://papermc.io/) that gives players a configurable, **translatable** help message with a single command: `/helpme`.

The message is read from `config.yml`, shown only to the player who ran the command, and automatically displayed in the player's own game language when you provide a translation for it.

---

## Table of contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Commands](#commands)
- [Permissions](#permissions)
- [Configuration](#configuration)
  - [Message format](#message-format)
  - [Color codes](#color-codes)
  - [Adding a language](#adding-a-language)
  - [How the language is chosen](#how-the-language-is-chosen)
- [Building from source](#building-from-source)
- [Project structure](#project-structure)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- **`/helpme`** sends a private message, visible only to the player who used it.
- **Multi-line messages**: write one YAML list entry per chat line.
- **Translation system**: add as many languages as you like; each player sees the one matching their client language.
- **Safe fallbacks**: if a player's language has no translation, the plugin uses your configured default language.
- **Live reload**: `/helpme reload` applies config changes without restarting the server.
- **Color codes**: classic `&` codes (`&a`, `&e`, `&l`, ...) are supported, using the modern Adventure API (no deprecated `ChatColor`).
- **Tab completion** for the `reload` subcommand (only for players who have permission).
- No dependencies and no database: just a jar and a config file.

## Requirements

| Requirement | Version |
|-------------|---------|
| Server      | [Paper](https://papermc.io/downloads/paper) 26.1.2 |
| Java        | 25 |

The plugin is compiled against Paper API `26.1.2.build.74-stable` and declares `api-version: 26.1.2`. It is Paper-only and is not tested on Spigot or Bukkit.

## Installation

1. Download `HelpMe-1.0.0.jar` (from the [Releases](../../releases) page, or [build it yourself](#building-from-source)).
2. Put the jar in your server's `plugins/` folder.
3. Start (or restart) the server.
4. Edit `plugins/HelpMe/config.yml` to your liking, then run `/helpme reload`.

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/helpme` | Shows the help message in your language. | none (everyone) |
| `/helpme reload` | Reloads `config.yml` from disk. | `helpme.reload` |

`/helpme` can only be used by players. If it is run from the console, the plugin replies with the `player-only` message instead.

`/helpme reload` can be used by players with permission **and** from the console.

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `helpme.reload` | Allows `/helpme reload`. | `op` |

Grant it with your permissions plugin, for example with LuckPerms:

```
/lp group admin permission set helpme.reload true
```

## Configuration

The default `config.yml` is generated on first start at `plugins/HelpMe/config.yml`:

```yaml
default-language: "en"

messages:
  en:
    helpme:
      - "&aNeed help? Contact a staff member!"
      - "&eOr check &b/wiki&e for guides."
    reload: "&aHelpMe configuration reloaded."
    no-permission: "&cYou don't have permission to do that."
    player-only: "&cThis command can only be used by a player."
  ru:
    helpme:
      - "&aНужна помощь? Обратитесь к администрации!"
      - "&eИли посмотрите &b/wiki&e для гайдов."
    reload: "&aКонфигурация HelpMe перезагружена."
    no-permission: "&cУ вас нет прав для этого."
    player-only: "&cЭту команду может использовать только игрок."
```

### Message format

Each language section under `messages` supports these keys:

| Key | Type | Used when |
|-----|------|-----------|
| `helpme` | **List** of lines | A player runs `/helpme`. |
| `reload` | Single line | `/helpme reload` succeeds. |
| `no-permission` | Single line | Someone without `helpme.reload` tries to reload. |
| `player-only` | Single line | `/helpme` is run from the console. |

`helpme` is a list: **every `-` entry is sent as its own chat line, in order.** Add or remove lines freely:

```yaml
helpme:
  - "Line 1 Text"
  - "Line 2 Text"
  - "Line 3 Text"
```

Wrap each line in quotes. This avoids YAML problems with characters like `&`, `:` and `#`.

### Color codes

Use `&` followed by a code:

| Code | Color | Code | Color |
|------|-------|------|-------|
| `&0` | Black | `&8` | Dark gray |
| `&1` | Dark blue | `&9` | Blue |
| `&2` | Dark green | `&a` | Green |
| `&3` | Dark aqua | `&b` | Aqua |
| `&4` | Dark red | `&c` | Red |
| `&5` | Dark purple | `&d` | Light purple |
| `&6` | Gold | `&e` | Yellow |
| `&7` | Gray | `&f` | White |

Formatting codes: `&l` bold, `&o` italic, `&n` underline, `&m` strikethrough, `&k` obfuscated, `&r` reset.

### Adding a language

Copy an existing block under `messages`, change the language code, and translate the values. No code changes or rebuild required:

```yaml
messages:
  de:
    helpme:
      - "&aBrauchst du Hilfe? Kontaktiere einen Admin!"
      - "&eOder schau dir &b/wiki&e an."
    reload: "&aHelpMe-Konfiguration neu geladen."
    no-permission: "&cDazu hast du keine Berechtigung."
    player-only: "&cDieser Befehl kann nur von Spielern benutzt werden."
```

Then run `/helpme reload`.

### How the language is chosen

1. The plugin reads the player's client language (set in Minecraft under *Options > Language*) and takes its language code, e.g. `en_us` becomes `en`, `ru_ru` becomes `ru`, `de_de` becomes `de`.
2. If `messages` contains a section with that code, it is used.
3. Otherwise the plugin falls back to `default-language`.
4. If a key is missing from the chosen language, it falls back to the same key in `default-language`, and finally to a built-in English message.

The console always uses `default-language`.

> Language codes are matched by language only (`en`), not by region (`en_us` vs `en_gb`). Make sure `default-language` points to a section that exists in `messages`.

## Building from source

**Prerequisites:** JDK 25 and [Maven](https://maven.apache.org/).

```bash
git clone https://github.com/<your-username>/HelpMe.git
cd HelpMe
mvn clean package
```

The compiled plugin is written to `target/HelpMe-1.0.0.jar`.

To build against a different Paper build, change the property in `pom.xml`:

```xml
<paper.api.version>26.1.2.build.74-stable</paper.api.version>
```

If you change the Minecraft version, also update `api-version` in `src/main/resources/plugin.yml`.

## Project structure

```
HelpMe/
├── pom.xml
├── LICENSE
├── README.md
└── src/main/
    ├── java/com/example/helpme/
    │   ├── HelpMePlugin.java     # plugin entry point, /helpme command and tab completion
    │   └── MessageManager.java   # language detection and message lookup
    └── resources/
        ├── plugin.yml            # plugin metadata, command, permission
        └── config.yml            # default configuration
```

## Troubleshooting

**The plugin doesn't load / "Unsupported API version".**
Your server version doesn't match `api-version` in `plugin.yml`. Rebuild against your server's version (see [Building from source](#building-from-source)).

**`UnsupportedClassVersionError` or "class file version" error.**
The plugin is compiled for Java 25. Run the server on Java 25 or newer.

**Players always see the default language.**
Check that `messages` has a section whose key matches the player's language code (`en`, `ru`, `de`, ...), and that the player's client language is set to it. Remember that region doesn't matter, only the two-letter language code.

**My changes to `config.yml` don't show up.**
Run `/helpme reload`. If the command says nothing or fails, check the console for YAML errors. Indentation must use spaces, not tabs.

**Colors show as literal `&a`.**
Make sure the value is inside quotes and uses `&`, not `§`.

**`/helpme reload` says I have no permission.**
Grant `helpme.reload` to your group, or `/op` yourself for testing.

## Contributing

Issues and pull requests are welcome. For code changes:

1. Fork the repository and create a branch.
2. Make your change and check that `mvn clean package` succeeds.
3. Open a pull request describing what changed and why.

New translations are especially welcome: add a language block to `config.yml` and mention it in your pull request.

## License

Released under the GPL-3.0 license.
