#!/usr/bin/env bash
# Build launcher-importable modpacks from an already built X-Ray jar.
#
# Cubiq (and most other launchers) import modpacks, not bare mod jars.  This
# script produces a Modrinth .mrpack and a CurseForge .zip that contain both
# X-Ray and Fabric API under overrides/mods, so the imported instance starts
# with everything it needs.
set -euo pipefail

cd "$(dirname "$0")/.."

usage() {
    cat <<'USAGE'
usage: scripts/build-modpack.sh --minecraft VERSION --fabric-api VERSION [options]

  --minecraft VERSION    Minecraft version of the built jar (required)
  --fabric-api VERSION   Fabric API version to bundle, e.g. 0.165.0+26.3 (required)
  --loader VERSION       Fabric Loader version (default: loader_version in gradle.properties)
  --jar FILE             mod jar to package (default: build/libs/xray-<mod>+<minecraft>.jar)
  --out DIR              output directory (default: dist)
USAGE
}

minecraft_version=""
fabric_api_version=""
loader_version="$(sed -n 's/^loader_version=//p' gradle.properties)"
mod_version="$(sed -n 's/^mod_version=//p' gradle.properties)"
jar=""
out_dir="dist"

while [ $# -gt 0 ]; do
    case "$1" in
        --minecraft) minecraft_version="${2:?missing value for $1}"; shift 2 ;;
        --fabric-api) fabric_api_version="${2:?missing value for $1}"; shift 2 ;;
        --loader) loader_version="${2:?missing value for $1}"; shift 2 ;;
        --jar) jar="${2:?missing value for $1}"; shift 2 ;;
        --out) out_dir="${2:?missing value for $1}"; shift 2 ;;
        -h|--help) usage; exit 0 ;;
        *) echo "error: unknown option: $1" >&2; usage >&2; exit 2 ;;
    esac
done

[ -n "$minecraft_version" ] || { echo "error: --minecraft is required" >&2; exit 2; }
[ -n "$fabric_api_version" ] || { echo "error: --fabric-api is required" >&2; exit 2; }
[ -n "$loader_version" ] || { echo "error: loader_version is empty" >&2; exit 2; }
[ -n "$mod_version" ] || { echo "error: mod_version is empty" >&2; exit 2; }

if [ -z "$jar" ]; then
    jar="build/libs/xray-${mod_version}+${minecraft_version}.jar"
fi
[ -f "$jar" ] || {
    echo "error: $jar not found" >&2
    echo "build it first: ./build.sh -Pminecraft_version=${minecraft_version} -Pfabric_api_version=${fabric_api_version}" >&2
    exit 1
}
jar_name="$(basename "$jar")"

cache_dir="build/modpack-cache"
fabric_api_jar="${cache_dir}/fabric-api-${fabric_api_version}.jar"
mkdir -p "$cache_dir"
if [ ! -f "$fabric_api_jar" ]; then
    url="https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/${fabric_api_version}/fabric-api-${fabric_api_version}.jar"
    echo "downloading ${url}"
    curl -fL --retry 3 --retry-delay 2 -o "${fabric_api_jar}.tmp" "$url"
    mv "${fabric_api_jar}.tmp" "$fabric_api_jar"
fi
fabric_api_jar_name="$(basename "$fabric_api_jar")"

work_dir="build/modpack/${minecraft_version}"
rm -rf "$work_dir"
mkdir -p "$work_dir/modrinth/overrides/mods" "$work_dir/curseforge/overrides/mods"

for pack in modrinth curseforge; do
    cp "$jar" "$work_dir/$pack/overrides/mods/$jar_name"
    cp "$fabric_api_jar" "$work_dir/$pack/overrides/mods/$fabric_api_jar_name"
done

cat > "$work_dir/modrinth/modrinth.index.json" <<JSON
{
  "formatVersion": 1,
  "game": "minecraft",
  "versionId": "${mod_version}+${minecraft_version}",
  "name": "X-Ray ${mod_version} (${minecraft_version})",
  "summary": "Client-side X-Ray for Minecraft ${minecraft_version} on Fabric. X-Ray and Fabric API are bundled in overrides/mods.",
  "files": [],
  "dependencies": {
    "minecraft": "${minecraft_version}",
    "fabric-loader": "${loader_version}"
  }
}
JSON

cat > "$work_dir/curseforge/manifest.json" <<JSON
{
  "manifestType": "minecraftModpack",
  "manifestVersion": 1,
  "name": "X-Ray ${mod_version} (${minecraft_version})",
  "version": "${mod_version}",
  "author": "mmunki",
  "overrides": "overrides",
  "minecraft": {
    "version": "${minecraft_version}",
    "modLoaders": [
      {
        "id": "fabric-${loader_version}",
        "primary": true
      }
    ],
    "recommendedRam": 2048
  },
  "files": []
}
JSON

mkdir -p "$out_dir"
mrpack="${out_dir}/xray-${mod_version}+${minecraft_version}.mrpack"
curseforge_pack="${out_dir}/xray-${mod_version}+${minecraft_version}-curseforge.zip"

rm -f "$mrpack" "$curseforge_pack"
(cd "$work_dir/modrinth" && zip -X -q -r "$OLDPWD/$mrpack" .)
(cd "$work_dir/curseforge" && zip -X -q -r "$OLDPWD/$curseforge_pack" .)

checksums="${out_dir}/SHA256SUMS-${minecraft_version}.txt"
rm -f "$checksums"
(cd "$out_dir" && sha256sum "$(basename "$mrpack")" "$(basename "$curseforge_pack")" > "$(basename "$checksums")")

echo "created $mrpack"
echo "created $curseforge_pack"
echo "created $checksums"
