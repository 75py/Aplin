#!/bin/sh

set -eu

EXPECTED_PACKAGE='com.nagopy.android.aplin.foss'
EXPECTED_VERSION_CODE='46'
EXPECTED_VERSION_NAME='5.6.0'
EXPECTED_TARGET_SDK='36'
DEFAULT_APK='app/build/outputs/apk/foss/release/app-foss-release-unsigned.apk'
FORBIDDEN='com\.google\.android\.gms\.ads|com/google/android/gms/ads|com\.google\.android\.ump|com/google/android/ump|com\.google\.android\.gms\.oss\.licenses|com/google/android/gms/oss/licenses|com\.google\.android\.gms\.ads\.APPLICATION_ID|ad_app_id|google_mobile_ads|user-messaging-platform|oss_licenses|oss-licenses|Lcom/google/android/gms/|Lcom/google/android/ump/'

fail() {
    echo "FOSS APK smoke check failed: $*" >&2
    exit 1
}

[ "$#" -le 1 ] || fail "usage: $0 [apk]"
apk_path=${1:-$DEFAULT_APK}
[ -f "$apk_path" ] || fail "APK not found: $apk_path"

command -v awk >/dev/null 2>&1 || fail "awk is required"
command -v find >/dev/null 2>&1 || fail "find is required"
command -v grep >/dev/null 2>&1 || fail "grep is required"
command -v mktemp >/dev/null 2>&1 || fail "mktemp is required"
command -v strings >/dev/null 2>&1 || fail "strings is required"
command -v unzip >/dev/null 2>&1 || fail "unzip is required"

select_sdk_aapt2() {
    sdk_root=$1
    candidates=$(find "$sdk_root/build-tools" -type f -name aapt2 -perm -111 2>/dev/null) || fail "could not search $sdk_root/build-tools"
    [ -n "$candidates" ] || fail "aapt2 not found under $sdk_root/build-tools"
    selected=$(printf '%s\n' "$candidates" | awk -v target="$EXPECTED_TARGET_SDK" '
        function greater(a, b, aa, bb, na, nb, i, limit, av, bv) {
            na = split(a, aa, ".")
            nb = split(b, bb, ".")
            limit = (na > nb ? na : nb)
            for (i = 1; i <= limit; i++) {
                av = (aa[i] == "" ? 0 : aa[i]) + 0
                bv = (bb[i] == "" ? 0 : bb[i]) + 0
                if (av > bv) return 1
                if (av < bv) return 0
            }
            return 0
        }
        {
            version = $0
            sub(/^.*\/build-tools\//, "", version)
            sub(/\/aapt2$/, "", version)
            if (latest_version == "" || greater(version, latest_version) ||
                (version == latest_version && $0 < latest_path)) {
                latest_version = version
                latest_path = $0
            }
            if (version ~ ("^" target "\\.") &&
                (target_version == "" || greater(version, target_version) ||
                 (version == target_version && $0 < target_path))) {
                target_version = version
                target_path = $0
            }
        }
        END {
            if (target_path != "") print target_path
            else if (latest_path != "") print latest_path
            else exit 1
        }
    ') || fail "could not select an aapt2 build-tools version"
    [ -n "$selected" ] || fail "aapt2 selection returned no path"
    printf '%s\n' "$selected"
}

resolve_aapt2() {
    if [ -n "${AAPT2:-}" ]; then
        case "$AAPT2" in
            */*) printf '%s\n' "$AAPT2" ;;
            *) command -v "$AAPT2" || true ;;
        esac
        return
    fi

    sdk_root=${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}
    if [ -n "$sdk_root" ]; then
        select_sdk_aapt2 "$sdk_root"
        return
    fi
    command -v aapt2 || true
}

require_literal() {
    status=0
    grep -Fq "$1" "$2" || status=$?
    [ "$status" -eq 1 ] && fail "$3"
    [ "$status" -eq 0 ] && return 0
    fail "scanner failed while checking $3"
}

reject_forbidden() {
    status=0
    grep -Eiq "$FORBIDDEN" "$1" || status=$?
    [ "$status" -eq 1 ] && return 0
    [ "$status" -eq 0 ] && fail "$2"
    fail "scanner failed while checking $2"
}

require_member() {
    status=0
    grep -Fxq "$1" "$2" || status=$?
    [ "$status" -eq 0 ] && return 0
    [ "$status" -eq 1 ] && fail "$3"
    fail "scanner failed while checking $3"
}

reject_licensee_group() {
    group_pattern=$(printf '"groupId"[[:space:]]*:[[:space:]]*"%s"' "$1")
    status=0
    grep -Eiq "$group_pattern" "$2" || status=$?
    [ "$status" -eq 1 ] && return 0
    [ "$status" -eq 0 ] && fail "$3"
    fail "scanner failed while checking $3"
}

aapt2_path=$(resolve_aapt2)
[ -n "$aapt2_path" ] || fail "aapt2 not found; set AAPT2, ANDROID_SDK_ROOT, or ANDROID_HOME"
[ -x "$aapt2_path" ] || fail "aapt2 is not executable: $aapt2_path"
"$aapt2_path" version >/dev/null 2>&1 || fail "aapt2 validation failed: $aapt2_path"

tmp_dir=$(mktemp -d "${TMPDIR:-/tmp}/aplin-foss-apk.XXXXXX") || fail "could not create temporary directory"
cleanup() {
    rm -rf "$tmp_dir"
}
trap cleanup 0

badging="$tmp_dir/badging.txt"
permissions="$tmp_dir/permissions.txt"
manifest="$tmp_dir/manifest.txt"
resources="$tmp_dir/resources.txt"
members="$tmp_dir/members.txt"
dex_names="$tmp_dir/dex-names.txt"
dex_strings="$tmp_dir/dex-strings.txt"

"$aapt2_path" dump badging "$apk_path" > "$badging" || fail "aapt2 could not dump APK badging"
[ -s "$badging" ] || fail "aapt2 badging output is empty"
require_literal "package: name='$EXPECTED_PACKAGE'" "$badging" "unexpected package or missing package declaration"
require_literal "versionCode='$EXPECTED_VERSION_CODE'" "$badging" "expected versionCode $EXPECTED_VERSION_CODE"
require_literal "versionName='$EXPECTED_VERSION_NAME'" "$badging" "expected versionName $EXPECTED_VERSION_NAME"
require_literal "targetSdkVersion:'$EXPECTED_TARGET_SDK'" "$badging" "expected targetSdkVersion $EXPECTED_TARGET_SDK"

"$aapt2_path" dump permissions "$apk_path" > "$permissions" || fail "aapt2 could not dump APK permissions"
[ -s "$permissions" ] || fail "aapt2 permissions output is empty"
awk -F "'" '/^uses-permission/ && /name=/ { print $2 }' "$permissions" > "$tmp_dir/permission-names" || fail "permission scanner failed"
awk -v query='android.permission.QUERY_ALL_PACKAGES' \
    -v dynamic='com.nagopy.android.aplin.foss.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION' '
    { count++; if ($0 != query && $0 != dynamic) invalid=1
      if ($0 == query) query_count++
      if ($0 == dynamic) dynamic_count++ }
    END { exit !(count == 2 && query_count == 1 && dynamic_count == 1) }
' "$tmp_dir/permission-names" || fail "uses-permission set is not exactly the FOSS allowlist"

"$aapt2_path" dump xmltree "$apk_path" --file AndroidManifest.xml > "$manifest" || fail "aapt2 could not dump AndroidManifest.xml"
[ -s "$manifest" ] || fail "aapt2 manifest output is empty"
"$aapt2_path" dump resources "$apk_path" > "$resources" || fail "aapt2 could not dump resources"
[ -s "$resources" ] || fail "aapt2 resources output is empty"

unzip -tq "$apk_path" >/dev/null || fail "APK archive is invalid"
unzip -Z1 "$apk_path" > "$members" || fail "could not list APK archive members"
[ -s "$members" ] || fail "APK archive member list is empty"
reject_forbidden "$members" "forbidden advertising, UMP, or Google OSS license archive member found"

licensee_member='assets/app/cash/licensee/artifacts.json'
licensee_json="$tmp_dir/licensee-artifacts.json"
require_member "$licensee_member" "$members" "required Licensee catalog member is missing"
unzip -p "$apk_path" "$licensee_member" > "$licensee_json" || fail "could not extract Licensee catalog"
[ -s "$licensee_json" ] || fail "Licensee catalog is empty"
reject_licensee_group 'com\.google\.android\.gms' "$licensee_json" "forbidden Licensee groupId com.google.android.gms found"
reject_licensee_group 'com\.google\.android\.ump' "$licensee_json" "forbidden Licensee groupId com.google.android.ump found"

awk '$0 ~ /(^|\/)classes[^\/]*\.dex$/ { print }' "$members" > "$dex_names" || fail "DEX member scan failed"
[ -s "$dex_names" ] || fail "APK contains no DEX members"
: > "$dex_strings"
dex_number=0
while IFS= read -r dex_name; do
    dex_number=$((dex_number + 1))
    dex_file="$tmp_dir/classes-$dex_number.dex"
    unzip -p "$apk_path" "$dex_name" > "$dex_file" || fail "could not read DEX member $dex_name"
    strings -a "$dex_file" >> "$dex_strings" || fail "strings failed for DEX member $dex_name"
done < "$dex_names"
[ -s "$dex_strings" ] || fail "DEX strings output is empty"
reject_forbidden "$dex_strings" "forbidden advertising, UMP, or Google OSS license DEX content found"
reject_forbidden "$manifest" "forbidden advertising, UMP, or Google OSS license manifest content found"
reject_forbidden "$resources" "forbidden advertising, UMP, or Google OSS license resource content found"

echo "FOSS APK smoke check passed: $apk_path"
