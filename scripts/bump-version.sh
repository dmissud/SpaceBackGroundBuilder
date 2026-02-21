#!/usr/bin/env bash
# bump-version.sh — Bumps project version across backend (Maven) and frontend (npm)
# Usage: ./scripts/bump-version.sh [major|minor|patch]
# Default: patch

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
VERSION_FILE="$REPO_ROOT/VERSION"
PACKAGE_JSON="$REPO_ROOT/sbgb-gui/package.json"

# ── helpers ─────────────────────────────────────────────────────────────────

current_version() {
    tr -d '[:space:]' < "$VERSION_FILE"
}

split_version() {
    local ver="$1"
    IFS='.' read -r MAJOR MINOR PATCH <<< "$ver"
}

# ── main ─────────────────────────────────────────────────────────────────────

BUMP_TYPE="${1:-patch}"

CURRENT=$(current_version)
split_version "$CURRENT"

case "$BUMP_TYPE" in
    major)
        MAJOR=$((MAJOR + 1))
        MINOR=0
        PATCH=0
        ;;
    minor)
        MINOR=$((MINOR + 1))
        PATCH=0
        ;;
    patch)
        PATCH=$((PATCH + 1))
        ;;
    *)
        echo "Usage: $0 [major|minor|patch]" >&2
        exit 1
        ;;
esac

NEW_VERSION="$MAJOR.$MINOR.$PATCH"

echo "Bumping $CURRENT → $NEW_VERSION ($BUMP_TYPE)"

# ── 1. Update VERSION file ────────────────────────────────────────────────────
echo "$NEW_VERSION" > "$VERSION_FILE"

# ── 2. Update all Maven POMs ─────────────────────────────────────────────────
cd "$REPO_ROOT"
./mvnw --quiet versions:set -DnewVersion="$NEW_VERSION" -DgenerateBackupPoms=false

# ── 3. Update Angular package.json ───────────────────────────────────────────
# Uses node if available, otherwise sed
if command -v node &>/dev/null; then
    node -e "
const fs = require('fs');
const path = '$PACKAGE_JSON';
const pkg = JSON.parse(fs.readFileSync(path, 'utf8'));
pkg.version = '$NEW_VERSION';
fs.writeFileSync(path, JSON.stringify(pkg, null, 2) + '\n');
"
else
    sed -i "s/\"version\": \"[^\"]*\"/\"version\": \"$NEW_VERSION\"/" "$PACKAGE_JSON"
fi

# ── 4. Git commit + tag ───────────────────────────────────────────────────────
git add "$VERSION_FILE" "$PACKAGE_JSON"
find "$REPO_ROOT" -name "pom.xml" -not -path "*/node_modules/*" -not -path "*/.git/*" | xargs git add

git commit -m "chore(version): bump to $NEW_VERSION"
git tag "v$NEW_VERSION"

echo "Done. Version is now $NEW_VERSION (tag v$NEW_VERSION created locally)."
echo "Push with: git push && git push --tags"
