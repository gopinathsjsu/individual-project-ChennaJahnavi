# Sync this folder with `individual-project-ChennaJahnavi`

The remote is:

`https://github.com/gopinathsjsu/individual-project-ChennaJahnavi.git`

(Use this exact URL in Git; do not include a trailing `#` from the browser.)

## A — Replace **this folder** with what is on GitHub (lose unpushed local-only commits)

Use when GitHub is the source of truth and you want your disk to match it.

1. Sign in so `git fetch` works, for example:
   - `gh auth login`, or  
   - macOS Keychain / VS Code GitHub sign-in for HTTPS.

2. From the project root:

```bash
chmod +x scripts/replace-with-remote.sh
./scripts/replace-with-remote.sh
```

Or manually:

```bash
git fetch origin
git checkout -B main origin/main   # or: git checkout main && git reset --hard origin/main
git reset --hard origin/main
```

If the default branch on GitHub is `master`, the script tries `origin/master` as a fallback.

## B — Replace **GitHub** with what you have here (push your code)

Use when your laptop is the source of truth.

```bash
git add -A
git status
git commit -m "Your message"   # skip if nothing to commit
git push -u origin main
```

If the remote already has commits you do not want, coordinate with your instructor before using `git push --force`.

## C — Start over: new clone, then copy work in

If `.git` is messy or you prefer a clean clone:

```bash
cd /Users/ceejayy/Documents
mv Project202_individual Project202_individual_backup
git clone https://github.com/gopinathsjsu/individual-project-ChennaJahnavi.git Project202_individual
# Copy any files you still need from Project202_individual_backup into Project202_individual
```

## Private org repo

If `git fetch` / `git clone` asks for a username and password, use a **Personal Access Token** as the password, or use **SSH** and:

```bash
git remote set-url origin git@github.com:gopinathsjsu/individual-project-ChennaJahnavi.git
```
