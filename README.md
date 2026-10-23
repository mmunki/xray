# X-Ray (Fabric)

A small, client-side X-Ray mod for Minecraft Fabric. Press **X** to toggle it
on and off, or open the pause menu and click the X-Ray icon in the top-right
corner. The icon is drawn in green while X-Ray is active. While it is on,
everything except ores, chests, spawners and fluids is treated as air while
the chunks are built, so the ore layout is visible through stone.

## Requirements

| Component | Version |
| --- | --- |
| Minecraft | 26.3 |
| Fabric Loader | 0.19.5 or newer |
| Fabric API | required |
| Java | 25 or newer |

Sodium is supported when it is installed. The mod does not need to be
installed on the server; it is purely client side.

## Install

1. Install Fabric Loader for Minecraft 26.3.
2. Put `fabric-api` and `xray-1.1.0.jar` into `.minecraft/mods`.
3. Start the game, join a world, and press **X** or click the pause menu icon.
4. The key can be changed in **Options -> Controls -> Key Binds**.

The action bar shows `X-Ray enabled` / `X-Ray disabled` after a toggle, and the
chunks around the player are rebuilt immediately so the change is visible
without moving. The pause menu button shows the same state: the icon is green
while X-Ray is active.

## Blocks that stay visible

Coal, deepslate coal, iron, deepslate iron, copper, deepslate copper, gold,
deepslate gold, redstone, deepslate redstone, emerald, deepslate emerald,
lapis, deepslate lapis, diamond, deepslate diamond, nether gold, nether
quartz, ancient debris, budding amethyst, chests, trapped chests, spawners,
trial spawners, water and lava.

The list lives in `src/main/java/dev/mmunki/xray/XrayState.java`.

## How it works

`RenderSectionRegionMixin` intercepts `getBlockState` on the vanilla chunk
renderer and returns `air` for blocks outside the whitelist. Returning air does
two things at once: the hidden block gets no geometry, and it no longer
occludes the faces of the ore next to it. `SodiumLevelSliceMixin` does the same
for Sodium's `LevelSlice`; it is optional and only applies when Sodium is
present. `PauseScreenMixin` adds the toggle icon to the pause menu.

## Build

```bash
./build.sh
```

The jar is written to `build/libs/xray-1.1.0.jar`. A JDK 25 or newer is
required; `build.sh` finds one automatically on common Linux installations and
falls back to `JAVA_HOME`.

## License

MIT. See `LICENSE`.
