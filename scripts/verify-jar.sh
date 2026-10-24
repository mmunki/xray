#!/usr/bin/env bash
# Structural checks on the built jar plus a check that the mixin target
# methods still exist in the Minecraft version that was compiled against.
set -euo pipefail

cd "$(dirname "$0")/.."

expected_version="${1:-}"
mod_version="$(sed -n 's/^mod_version=//p' gradle.properties)"

jar=""
if [ -n "$expected_version" ]; then
    jar="$(ls build/libs/xray-"${mod_version}"+"${expected_version}"*.jar 2>/dev/null | grep -v sources | head -n1 || true)"
fi
if [ -z "$jar" ] && [ -n "$expected_version" ]; then
    jar="$(ls build/libs/xray-*"${expected_version}"*.jar 2>/dev/null | grep -v sources | head -n1 || true)"
fi
if [ -z "$jar" ]; then
    jar="$(ls build/libs/xray-"${mod_version}"*.jar 2>/dev/null | grep -v sources | head -n1 || true)"
fi
if [ -z "$jar" ]; then
    jar="$(ls build/libs/xray-*.jar | grep -v sources | head -n1)"
fi
[ -f "$jar" ] || { echo "error: built jar not found" >&2; exit 1; }

echo "checking $jar"

required=(
    fabric.mod.json
    xray.mixins.json
    xray.sodium.mixins.json
    tw/mmunki/xray/XrayClient.class
    tw/mmunki/xray/XrayState.class
    tw/mmunki/xray/XrayPauseButton.class
    tw/mmunki/xray/XrayConfig.class
    tw/mmunki/xray/XrayConfigScreen.class
    tw/mmunki/xray/XrayModMenu.class
    tw/mmunki/xray/XrayBlocks.class
    tw/mmunki/xray/mixin/RenderSectionRegionMixin.class
    tw/mmunki/xray/mixin/PauseScreenMixin.class
    tw/mmunki/xray/mixin/SodiumLevelSliceMixin.class
    assets/xray/icon.png
    assets/xray/lang/en_us.json
    assets/xray/blockstates/overlay.json
    assets/xray/models/block/overlay.json
    assets/xray/textures/block/overlay.png
    assets/xray/textures/gui/xray_icon.png
    assets/xray/textures/gui/xray_icon_green.png
)

for entry in "${required[@]}"; do
    if ! unzip -l "$jar" | awk '{print $4}' | grep -qxF "$entry"; then
        echo "error: missing from jar: $entry" >&2
        exit 1
    fi
    echo "ok: $entry"
done

python3 - "$jar" "$expected_version" <<'PY'
import json
import sys
import zipfile

jar = sys.argv[1]
expected = sys.argv[2] if len(sys.argv) > 2 else ""
with zipfile.ZipFile(jar) as zf:
    mod = json.loads(zf.read("fabric.mod.json"))
    vanilla = json.loads(zf.read("xray.mixins.json"))
    sodium = json.loads(zf.read("xray.sodium.mixins.json"))

assert mod["id"] == "xray", mod["id"]
assert mod["environment"] == "client", mod["environment"]
assert "tw.mmunki.xray.XrayClient" in mod["entrypoints"]["client"]
assert "tw.mmunki.xray.XrayBlocks" in mod["entrypoints"]["main"]
assert "tw.mmunki.xray.XrayModMenu" in mod["entrypoints"]["modmenu"]
assert "fabric-api" in mod["depends"], mod["depends"]
assert "modmenu" in mod["suggests"], mod["suggests"]
if expected:
    parts = expected.split("-")[0].split(".")
    wanted = f">={expected} <{parts[0]}.{int(parts[1]) + 1}"
    assert mod["depends"]["minecraft"] == wanted, (mod["depends"]["minecraft"], wanted)
assert "RenderSectionRegionMixin" in vanilla["client"], vanilla
assert "PauseScreenMixin" in vanilla["client"], vanilla
assert "SodiumLevelSliceMixin" in sodium["client"], sodium
print("ok: fabric.mod.json and mixin configs")
PY

minecraft_jar=""
if [ -n "$expected_version" ]; then
    minecraft_jar="$(find "$HOME/.gradle/caches/fabric-loom/minecraftMaven" \
        -path "*/${expected_version}/minecraft-merged-deobf-*.jar" | head -n1)"
fi
if [ -z "$minecraft_jar" ]; then
    minecraft_jar="$(find "$HOME/.gradle/caches/fabric-loom/minecraftMaven" \
        -name 'minecraft-merged-deobf-*.jar' | sort | tail -n1)"
fi
if [ -n "$minecraft_jar" ]; then
    javap -classpath "$minecraft_jar" -p net.minecraft.client.renderer.chunk.RenderSectionRegion \
        | grep -q 'getBlockState(net.minecraft.core.BlockPos)' \
        || { echo "error: RenderSectionRegion.getBlockState(BlockPos) not found" >&2; exit 1; }
    javap -classpath "$minecraft_jar" -p net.minecraft.client.renderer.chunk.RenderSectionRegion \
        | grep -q 'getFluidState(net.minecraft.core.BlockPos)' \
        || { echo "error: RenderSectionRegion.getFluidState(BlockPos) not found" >&2; exit 1; }
    echo "ok: RenderSectionRegion.getBlockState(BlockPos)"
    echo "ok: RenderSectionRegion.getFluidState(BlockPos)"
    javap -classpath "$minecraft_jar" -p net.minecraft.client.gui.screens.PauseScreen \
        | grep -q 'private void createPauseMenu()' \
        || { echo "error: PauseScreen.createPauseMenu() not found" >&2; exit 1; }
    echo "ok: PauseScreen.createPauseMenu()"
else
    echo "warning: Minecraft jar not found, skipped the signature check" >&2
fi

echo "verification passed"
