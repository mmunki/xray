#!/usr/bin/env bash
# Structural checks on the built jar plus a check that the mixin target
# methods still exist in the Minecraft version that was compiled against.
set -euo pipefail

cd "$(dirname "$0")/.."

jar="$(ls build/libs/xray-*.jar | grep -v sources | head -n1)"
[ -f "$jar" ] || { echo "error: built jar not found" >&2; exit 1; }

echo "checking $jar"

required=(
    fabric.mod.json
    xray.mixins.json
    xray.sodium.mixins.json
    dev/mmunki/xray/XrayClient.class
    dev/mmunki/xray/XrayState.class
    dev/mmunki/xray/mixin/RenderSectionRegionMixin.class
    dev/mmunki/xray/mixin/SodiumLevelSliceMixin.class
    assets/xray/icon.png
    assets/xray/lang/en_us.json
)

for entry in "${required[@]}"; do
    if ! unzip -l "$jar" | awk '{print $4}' | grep -qxF "$entry"; then
        echo "error: missing from jar: $entry" >&2
        exit 1
    fi
    echo "ok: $entry"
done

python3 - "$jar" <<'PY'
import json
import sys
import zipfile

jar = sys.argv[1]
with zipfile.ZipFile(jar) as zf:
    mod = json.loads(zf.read("fabric.mod.json"))
    vanilla = json.loads(zf.read("xray.mixins.json"))
    sodium = json.loads(zf.read("xray.sodium.mixins.json"))

assert mod["id"] == "xray", mod["id"]
assert mod["environment"] == "client", mod["environment"]
assert "dev.mmunki.xray.XrayClient" in mod["entrypoints"]["client"]
assert "fabric-api" in mod["depends"], mod["depends"]
assert "RenderSectionRegionMixin" in vanilla["client"], vanilla
assert "SodiumLevelSliceMixin" in sodium["client"], sodium
print("ok: fabric.mod.json and mixin configs")
PY

minecraft_jar="$(find "$HOME/.gradle/caches/fabric-loom/minecraftMaven" \
    -name 'minecraft-merged-deobf-*.jar' | sort | tail -n1)"
if [ -n "$minecraft_jar" ]; then
    javap -classpath "$minecraft_jar" -p net.minecraft.client.renderer.chunk.RenderSectionRegion \
        | grep -q 'getBlockState(net.minecraft.core.BlockPos)' \
        || { echo "error: RenderSectionRegion.getBlockState(BlockPos) not found" >&2; exit 1; }
    echo "ok: RenderSectionRegion.getBlockState(BlockPos)"
else
    echo "warning: Minecraft jar not found, skipped the signature check" >&2
fi

echo "verification passed"
