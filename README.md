# X-Ray (Fabric)

A small, client-side X-Ray mod for Minecraft Fabric. X-Ray starts
**switched off**. Press **X** to toggle it on and off, or open the pause menu
and click the small **X** button in the top-right corner; the X turns green
while X-Ray is active. While it is on, ores, chests,
spawners and fluids are shown exactly as they look in the world, and every
other block is drawn as a 25%-opaque glass overlay. The overlay is a full
cube, but the faces between two adjacent overlay blocks are culled, so only
the outer shell of the terrain is drawn.

## Requirements

| Component | Version |
| --- | --- |
| Minecraft | 26.2 or 26.3 (use the jar built for your version) |
| Fabric Loader | 0.19.5 or newer |
| Fabric API | required |
| Mod Menu | optional; bundled in the launcher packs |
| Java | 25 or newer |

Sodium is supported when it is installed. The mod does not need to be
installed on the server; it is purely client side.

## Install

1. Install Fabric Loader for Minecraft 26.2 or 26.3.
2. Put the matching `fabric-api` and `xray-1.7.0+26.2.jar` or `xray-1.7.0+26.3.jar` into `.minecraft/mods`.
3. Start the game, join a world, and press **X** or click the small **X**
   button in the top-right corner of the pause menu. Install
   [Mod Menu](https://modrinth.com/mod/modmenu) as well to open the settings
   from the mod list.
4. The key can be changed in **Options -> Controls -> Key Binds**.

The action bar shows `X-Ray enabled` / `X-Ray disabled` after a toggle, and the
chunks around the player are rebuilt immediately so the change is visible
without moving. The pause menu button is a small **X** icon: it is grey while
X-Ray is off and turns green while X-Ray is on.

## Cubiq / launcher import

Cubiq imports **modpacks**, not bare mod jars. Selecting `xray-1.7.0+*.jar`
in **Import Modpack** is what produces `This modpack format is not supported.`
Use the matching Modrinth pack instead:

1. Download `xray-1.7.0+26.2.mrpack` or `xray-1.7.0+26.3.mrpack` from the
   release.
2. In Cubiq, choose **Import Modpack** and select the `.mrpack`.
3. Start the imported instance and press **X** or use the small **X**
   button in the top-right corner of the pause menu.

The pack bundles X-Ray, Fabric API and Mod Menu under `overrides/mods`, and
pins the matching Minecraft and Fabric Loader versions, so no separate
download is needed. A CurseForge-format zip (`xray-1.7.0+<version>-curseforge.zip`) is
published for launchers that prefer that format.

## CurseForge

The release is laid out exactly like a CurseForge file upload:

| CurseForge field | Value |
| --- | --- |
| Project name | X-Ray |
| Project slug | `xray` |
| Game | Minecraft |
| Mod loader | Fabric |
| Game versions | 26.2, 26.3 |
| Java version | Java 25 |
| Release type | release |
| Files | `xray-1.7.0+26.2.jar`, `xray-1.7.0+26.3.jar` |
| Changelog | `CHANGELOG.md` |
| License | MIT (also inside the jar) |

Each jar is a normal Fabric mod jar with `fabric.mod.json`, the icon, the
license, the manifest fields and only `tw/mmunki/xray/**` classes.

## Blocks that stay visible

Coal, deepslate coal, iron, deepslate iron, copper, deepslate copper, gold,
deepslate gold, redstone, deepslate redstone, emerald, deepslate emerald,
lapis, deepslate lapis, diamond, deepslate diamond, nether gold, nether
quartz, ancient debris, budding amethyst, chests, trapped chests, spawners,
trial spawners, water and lava.

These blocks are returned unchanged, so they are rendered with their normal
textures. Every other block is replaced by `xray:overlay`, a glass-like full
cube whose texture is 25% opaque. Minecraft culls the faces shared by two
adjacent overlay blocks, so the terrain is drawn as a faint outer shell rather
than a solid wall. The categories live in
`src/main/java/tw/mmunki/xray/XrayState.java` and can be switched on and off
in the Mod Menu settings screen.

## How it works

`RenderSectionRegionMixin` intercepts `getBlockState` on the vanilla chunk
renderer, returns the original state for whitelisted blocks and the 25% glass
overlay for everything else. `SodiumLevelSliceMixin` does the same for
Sodium's `LevelSlice`; it is optional and only applies when Sodium is present.
`PauseScreenMixin` adds the small **X** button to the top-right corner of the
pause menu. The mod icon is a pixel-art diamond pickaxe.
`XrayModMenu` provides the settings screen when Mod Menu is installed, and
`XrayConfig` stores the settings in `config/xray.json`. X-Ray is off on every
launch unless **Enable at startup** is switched on.

## Build

The default target is Minecraft 26.3. The source also builds against 26.2 by
overriding the two Fabric properties:

```bash
./build.sh
# Minecraft 26.2
./build.sh -Pminecraft_version=26.2 -Pfabric_api_version=0.161.0+26.2
```

The jar is written to `build/libs/xray-1.7.0+26.3.jar` (or
`xray-1.7.0+26.2.jar` for the 26.2 build) and declares a Minecraft
dependency range that matches the version it was compiled against. A JDK 25 or
newer is required; `build.sh` finds one automatically on common Linux
installations and falls back to `JAVA_HOME`.

To build the launcher-importable packs after the jars exist:

```bash
./build.sh
scripts/build-modpack.sh --minecraft 26.3 --fabric-api 0.165.0+26.3
scripts/verify-modpack.sh dist/xray-1.7.0+26.3.mrpack \
    dist/xray-1.7.0+26.3-curseforge.zip
```

The `.mrpack` and CurseForge `.zip` are written to `dist/` (never committed).
The script downloads the matching Fabric API jar from the Fabric maven and the
matching Mod Menu jar from the Terraformers maven, then bundles both with
X-Ray in `overrides/mods`.

CI builds both versions as a matrix, verifies the mixin signatures against
each one, and verifies the generated packs.

## License

MIT. See `LICENSE`.
