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
  echo "==> Resetting branch main to origin/main"
  git checkout -B main origin/main
  git reset --hard origin/main
elif git rev-parse --verify origin/master >/dev/null 2>&1; then
  echo "==> Resetting branch main to match origin/master"
  git checkout -B main origin/master
  git reset --hard origin/master
else
  echo "No origin/main or origin/master found. Is the repo empty or fetch failed?"
  exit 1
fi

echo "==> Done. Working tree matches remote default branch."
git status
git log -1 --oneline
