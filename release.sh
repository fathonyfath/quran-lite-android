#!/usr/bin/env bash
# Builds, signs and verifies the release APK.
#
# Usage: ./release.sh <keystore> [key-alias]
#
# The keystore and alias can also be given as QURAN_KEYSTORE and QURAN_KEY_ALIAS.
# apksigner asks for the passwords, unless QURAN_KS_PASS (and QURAN_KEY_PASS when the
# key has its own password) are set.
set -euo pipefail

cd "$(dirname "$0")"

KEYSTORE="${1:-${QURAN_KEYSTORE:-}}"
KEY_ALIAS="${2:-${QURAN_KEY_ALIAS:-}}"

if [ -z "$KEYSTORE" ] || [ ! -f "$KEYSTORE" ]; then
    echo "Usage: $0 <keystore> [key-alias]" >&2
    exit 1
fi

SDK_DIR="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [ -z "$SDK_DIR" ] && [ -f local.properties ]; then
    SDK_DIR="$(sed -n 's/^sdk\.dir=//p' local.properties)"
fi

APKSIGNER="$(ls "$SDK_DIR"/build-tools/*/apksigner 2>/dev/null | sort -V | tail -n 1)"
if [ -z "$APKSIGNER" ]; then
    echo "apksigner not found, set ANDROID_HOME or install the Android SDK build-tools." >&2
    exit 1
fi

version_part() {
    sed -n "s/.*'$1': *\([0-9][0-9]*\).*/\1/p" app/build.gradle | head -n 1
}
VERSION="$(version_part major).$(version_part minor).$(version_part patch)"

UNSIGNED="app/build/outputs/apk/release/app-release-unsigned.apk"
OUTPUT="release/quran-lite-v$VERSION.apk"
# Needed to read stack traces of the obfuscated build.
MAPPING="release/quran-lite-v$VERSION-mapping.txt"

./gradlew clean lint test assembleRelease

SIGN_ARGS=(--ks "$KEYSTORE")
if [ -n "$KEY_ALIAS" ]; then
    SIGN_ARGS+=(--ks-key-alias "$KEY_ALIAS")
fi
if [ -n "${QURAN_KS_PASS:-}" ]; then
    SIGN_ARGS+=(--ks-pass env:QURAN_KS_PASS)
fi
if [ -n "${QURAN_KEY_PASS:-}" ]; then
    SIGN_ARGS+=(--key-pass env:QURAN_KEY_PASS)
fi

mkdir -p release
"$APKSIGNER" sign "${SIGN_ARGS[@]}" --out "$OUTPUT" "$UNSIGNED"
rm -f "$OUTPUT.idsig"
cp app/build/outputs/mapping/release/mapping.txt "$MAPPING"

"$APKSIGNER" verify --print-certs "$OUTPUT"

echo
echo "Version: $VERSION"
echo "APK:     $OUTPUT ($(stat -c %s "$OUTPUT") bytes)"
echo "SHA-256: $(sha256sum "$OUTPUT" | cut -d ' ' -f 1)"
echo "Mapping: $MAPPING"
