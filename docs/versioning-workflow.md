# Workflow de Gestion de Version

## Vue d'ensemble

Le projet utilise un système de versioning automatique basé sur **Semantic Versioning** (MAJOR.MINOR.PATCH) avec GitHub Actions.

## Branches

- **`develop`** : branche de développement (défaut)
- **`master`** : branche de production stable
- **`feature/*`** : branches de fonctionnalités
- **`fix/*`** : branches de corrections
- **`release/*`** : branches de release temporaires

## Règles de Bump

Le workflow `.github/workflows/bump-version.yml` s'exécute automatiquement lors de la **fermeture d'une Pull Request mergée** :

| Type de merge | Source → Target | Bump | Exemple |
|---------------|-----------------|------|---------|
| **MAJOR** | `release/*` → `master` | +1.0.0 | 0.6.0 → 1.0.0 |
| **MINOR** | `feature/*` → `develop` | +0.1.0 | 1.0.0 → 1.1.0 |
| **PATCH** | `fix/*` → `develop` | +0.0.1 | 1.1.0 → 1.1.1 |

### Logique du workflow

```bash
if [[ "$SOURCE" == fix/* ]]; then
  # PATCH bump
elif [[ "$TARGET" == "master" ]]; then
  # MAJOR bump (indépendamment de la source)
else
  # MINOR bump (tout le reste vers develop)
fi
```

## Flux de Release (MAJOR bump)

### 1. Créer une branche release depuis master

```bash
git checkout master
git pull
git checkout -b release/v1.0.0
git merge develop --no-ff -m "chore: prepare release v1.0.0"
git push -u origin release/v1.0.0
```

### 2. Créer une PR vers master

```bash
gh pr create \
  --base master \
  --head release/v1.0.0 \
  --title "Release v1.0.0" \
  --body "Merge develop into master for MAJOR release"
```

### 3. Merger la PR

Lorsque la PR est mergée :
1. GitHub Actions détecte le merge vers `master`
2. Le workflow `bump-version.yml` s'exécute
3. Le script `scripts/bump-version.sh major` :
   - Bump `VERSION` file (0.6.0 → 1.0.0)
   - Bump tous les `pom.xml` Maven
   - Bump `sbgb-gui/package.json` Angular
   - Crée un commit `chore(version): bump to 1.0.0 [skip ci]`
   - Crée un tag `v1.0.0`
   - Push vers `master`

### 4. Sync master → develop

**IMPORTANT** : Après le bump sur master, synchroniser develop :

```bash
git checkout develop
git pull
git merge master --no-ff -m "chore: sync master v1.0.0 into develop"
git push
```

## Flux de Développement (MINOR bump)

### 1. Créer une feature branch depuis develop

```bash
git checkout develop
git pull
git checkout -b feature/my-awesome-feature
# ... développement ...
git commit -m "feat: add awesome feature"
git push -u origin feature/my-awesome-feature
```

### 2. Créer une PR vers develop

```bash
gh pr create \
  --base develop \
  --head feature/my-awesome-feature \
  --title "Add awesome feature"
```

### 3. Merger la PR

Lorsque la PR est mergée :
1. Workflow `bump-version.yml` détecte le merge vers `develop`
2. Bump MINOR : 1.0.0 → 1.1.0
3. Commit + tag automatiques sur `develop`

## Flux de Correction (PATCH bump)

### 1. Créer une fix branch depuis develop

```bash
git checkout develop
git pull
git checkout -b fix/critical-bug
# ... correction ...
git commit -m "fix: resolve critical bug"
git push -u origin fix/critical-bug
```

### 2. Créer une PR vers develop

```bash
gh pr create \
  --base develop \
  --head fix/critical-bug \
  --title "Fix critical bug"
```

### 3. Merger la PR

Lorsque la PR est mergée :
1. Workflow détecte `fix/*` pattern
2. Bump PATCH : 1.1.0 → 1.1.1
3. Commit + tag automatiques sur `develop`

## Hotfix sur master (cas exceptionnel)

Si un hotfix doit être appliqué directement sur master :

```bash
# 1. Créer fix branch depuis master
git checkout master
git pull
git checkout -b fix/hotfix-prod
# ... correction ...
git commit -m "fix: hotfix production issue"
git push -u origin fix/hotfix-prod

# 2. PR vers master (bump MAJOR car target=master)
gh pr create --base master --head fix/hotfix-prod

# 3. Après merge, sync vers develop
git checkout develop
git merge master
git push
```

⚠️ **Attention** : Un merge vers `master` déclenche TOUJOURS un bump MAJOR, même pour un hotfix. Si vous voulez un PATCH bump, mergez d'abord dans `develop`, puis faites une release vers `master`.

## État Actuel du Projet

- **`develop`** : v0.6.0
- **`master`** : v0.3.0 (stale)
- **PR #43** : `release/v1.0.0` → `master` (en attente)

### Pourquoi master est-il resté à 0.3.0 ?

Le PR #39 (merge `develop` → `master`) a été créé **avant** que le workflow `bump-version.yml` n'existe sur `master`. GitHub Actions ne peut déclencher un workflow que s'il existe sur la branche de base **avant** le merge.

Maintenant que le workflow est sur `master`, tous les futurs merges fonctionneront correctement.

## Commandes Utiles

```bash
# Voir la version actuelle
cat VERSION

# Voir les tags récents
git tag --sort=-v:refname | head -10

# Bump manuel (sans PR)
./scripts/bump-version.sh [major|minor|patch]
git push && git push --tags

# Vérifier les workflows GitHub
gh run list --workflow=bump-version.yml

# Voir les PRs mergées récentes
gh pr list --state merged --limit 10
```

## Troubleshooting

### Le workflow ne se déclenche pas

1. Vérifier que le workflow existe sur la branche **de base** (target) avant le merge
2. Vérifier que le secret `BUMP_VERSION_TOKEN` existe dans les Settings GitHub
3. Vérifier les logs : `gh run list --workflow=bump-version.yml`

### Versions désynchronisées entre master et develop

Après un bump sur `master`, toujours merger `master` → `develop` :

```bash
git checkout develop
git merge master
git push
```

### Conflit lors du merge master → develop

Si conflit sur `VERSION`, `pom.xml`, ou `package.json` :

```bash
# Garder la version de master (la plus récente)
git checkout --theirs VERSION pom.xml sbgb-gui/package.json
git add .
git commit
```

## Bonnes Pratiques

1. ✅ Toujours créer des PRs (pas de push direct sur `develop` ou `master`)
2. ✅ Utiliser les préfixes `feature/`, `fix/`, `release/` pour les branches
3. ✅ Suivre le format Angular pour les commits : `feat:`, `fix:`, `chore:`, `refactor:`, etc.
4. ✅ Après un merge vers `master`, synchroniser immédiatement `develop`
5. ✅ Vérifier que le workflow s'est bien exécuté après le merge d'une PR
6. ❌ Ne jamais bumper manuellement sauf en cas de problème avec le workflow
7. ❌ Ne pas créer de tags manuellement (le workflow s'en charge)

## Exemple de Timeline

```
0.6.0 (develop)
  ↓
feature/foo → develop (PR merge)
  ↓
0.7.0 (develop) [workflow: MINOR bump]
  ↓
feature/bar → develop (PR merge)
  ↓
0.8.0 (develop) [workflow: MINOR bump]
  ↓
release/v1.0.0 → master (PR merge)
  ↓
1.0.0 (master) [workflow: MAJOR bump]
  ↓
master → develop (manual merge)
  ↓
1.0.0 (develop) [synced]
  ↓
feature/baz → develop (PR merge)
  ↓
1.1.0 (develop) [workflow: MINOR bump]
```
