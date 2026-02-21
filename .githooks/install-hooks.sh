#!/usr/bin/env bash
# install-hooks.sh — Configures git to use project hooks
# Run once after cloning: ./scripts/install-hooks.sh  (or .githooks/install-hooks.sh)

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"

git -C "$REPO_ROOT" config core.hooksPath .githooks
chmod +x "$REPO_ROOT/.githooks/post-merge"

echo "Git hooks installed. core.hooksPath = .githooks"
