# Changelog

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
