# Changelog

## 1.6.1

- Fixed the startup crash `Could not execute entrypoint stage 'main' ... Block id
  not set` (`NullPointerException` in `BlockBehaviour$Properties.effectiveDrops`).
  The overlay block is now created with an explicit registry id
  (`xray:overlay`) before registration, and it has no loot table.

## 1.6.0

- Ores, chests, spawners and fluids now render with their normal textures
  instead of being turned into glass.
- Every other block is replaced by a full-cube glass overlay that is 25%
  opaque; shared faces between overlay blocks are still culled, so the terrain
  is drawn as a faint outer shell.
- The pause-menu photo icon was replaced by a full-width, bold green
  **Minecraft X** button in the pause-menu grid next to Mod Menu.
- Added a Mod Menu settings screen and `config/xray.json`: toggle X-Ray,
  enable at startup, and choose whether ores, containers, spawners and fluids
  stay visible.
- Added an optional Mod Menu entrypoint; the modpacks now bundle Mod Menu,
  Fabric API and X-Ray.

## 1.5.0

- Fixed `java.lang.NoClassDefFoundError: Could not initialize class
  tw.mmunki.xray.XrayState`, caused by `INSTANCE` being created before
  `DEFAULT_WHITELIST`; the whitelist is now populated first and the instance is
  created afterwards.
- X-Ray is explicitly off by default; the player has to toggle it on.
- Whitelisted blocks are rendered as glass. Only the outer glass shell is
  drawn, because adjacent glass faces are culled, so veins have no interior
  faces.

## 1.4.0

- Added launcher-importable modpacks: a Modrinth `.mrpack` and a CurseForge
  `.zip` for Minecraft 26.2 and 26.3.
- The packs bundle X-Ray and Fabric API in `overrides/mods` and pin the
  matching Minecraft and Fabric Loader versions. This makes the release
  importable in Cubiq Launcher, which rejects a bare mod jar with
  "This modpack format is not supported".
- Added `scripts/build-modpack.sh` and `scripts/verify-modpack.sh`; CI now
  verifies both pack formats for both Minecraft versions.
- No behaviour change to the mod itself.

## 1.3.0

- Renamed the Java package from `dev.mmunki.xray` to `tw.mmunki.xray`
  (entrypoint, mixin configs, maven group and jar contents).
- Packaged the jar as a CurseForge-style release: stable file names for each
  Minecraft version, manifest implementation/specification fields, the MIT
  license inside the jar and this changelog.
- No behaviour change: the key bind, the pause-menu button and the block
  whitelist are unchanged.

## 1.2.0

- Added a Minecraft 26.2 build alongside 26.3.
- The Minecraft dependency range is generated for the version that is built.

## 1.1.0

- Added the X-Ray toggle icon to the pause menu; the icon turns green while
  X-Ray is active.

## 1.0.0

- First release: toggleable client-side X-Ray for Minecraft Fabric.
