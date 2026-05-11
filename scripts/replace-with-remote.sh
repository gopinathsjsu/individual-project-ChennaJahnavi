#!/usr/bin/env bash
# Replace the contents of this repo with the remote branch (destructive to local commits
# that are not on the remote). Run AFTER you can access GitHub (gh auth login, or HTTPS
# credential / PAT).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

echo "==> Fetching from origin …"
git fetch origin

if git rev-parse --verify origin/main >/dev/null 2>&1; then
  REF=origin/main
elif git rev-parse --verify origin/master >/dev/null 2>&1; then
  REF=origin/master
else
  echo "No origin/main or origin/master found. Is the repo empty or fetch failed?"
  exit 1
fi

echo "==> Resetting current branch to match $REF (local-only changes not on remote will be lost)"
git checkout -B main "$REF" 2>/dev/null || git checkout main
git reset --hard "$REF"

echo "==> Done. Working tree now matches $REF"
git status
git log -1 --oneline
