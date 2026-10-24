#!/usr/bin/env bash
# Structural checks for the launcher-importable modpacks.  The checks mirror
# what Cubiq/Zalith reads: a manifest at the archive root plus overrides/mods.
set -euo pipefail

cd "$(dirname "$0")/.."

[ $# -gt 0 ] || { echo "usage: scripts/verify-modpack.sh PACK..." >&2; exit 2; }

mod_version="$(sed -n 's/^mod_version=//p' gradle.properties)"

for pack in "$@"; do
    [ -f "$pack" ] || { echo "error: $pack not found" >&2; exit 1; }
    echo "checking $pack"

    python3 - "$pack" "$mod_version" <<'PY'
import json
import sys
import zipfile

pack, mod_version = sys.argv[1], sys.argv[2]
with zipfile.ZipFile(pack) as zf:
    bad = zf.testzip()
    assert bad is None, f"corrupt zip entry: {bad}"
    names = set(zf.namelist())

    mod_jars = [n for n in names if n.startswith("overrides/mods/xray-") and n.endswith(".jar")]
    api_jars = [n for n in names if n.startswith("overrides/mods/fabric-api-") and n.endswith(".jar")]
    modmenu_jars = [n for n in names if n.startswith("overrides/mods/modmenu-") and n.endswith(".jar")]
    assert len(mod_jars) == 1, f"expected one X-Ray jar, got {mod_jars}"
    assert f"xray-{mod_version}" in mod_jars[0], mod_jars[0]
    assert len(api_jars) == 1, f"expected one Fabric API jar, got {api_jars}"
    assert len(modmenu_jars) == 1, f"expected one Mod Menu jar, got {modmenu_jars}"

    if "modrinth.index.json" in names:
        index = json.loads(zf.read("modrinth.index.json"))
        assert index["formatVersion"] == 1
        assert index["game"] == "minecraft"
        assert "minecraft" in index["dependencies"]
        assert "fabric-loader" in index["dependencies"]
        assert index["files"] == []
        print("ok: Modrinth pack")
    elif "manifest.json" in names:
        manifest = json.loads(zf.read("manifest.json"))
        assert manifest["manifestType"] == "minecraftModpack"
        assert manifest["manifestVersion"] == 1
        assert manifest["overrides"] == "overrides"
        ids = [m["id"] for m in manifest["minecraft"]["modLoaders"]]
        assert any(i.startswith("fabric-") for i in ids), ids
        assert manifest["files"] == []
        print("ok: CurseForge pack")
    else:
        raise AssertionError("no modrinth.index.json or manifest.json at the archive root")
PY
done

echo "modpack verification passed"
