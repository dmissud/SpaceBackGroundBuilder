# Workflow de Versioning Automatisé

Ce projet utilise un workflow GitFlow simplifié et automatisé par GitHub Actions (`bump-version.yml`). Ce système gère
l'incrémentation des versions (Maven & npm), la création de tags et la synchronisation entre les branches `master` et
`develop`.

## 🚀 Fonctionnement général

Le cycle de versioning est déclenché par la fermeture et la fusion (**merge**) d'une Pull Request (PR) vers les branches
`develop` ou `master`.

### 1. Stratégie de montée de version (Bump Type)

Le type de montée de version (`major`, `minor`, `patch`) est déterminé selon l'ordre de priorité suivant :

1. **Labels GitHub (Priorité Haute)** : Si vous ajoutez un label à la Pull Request avant de la merger :
    * Label `major` → `X.0.0`
    * Label `minor` → `0.X.0`
    * Label `patch` → `0.0.X`
2. **Branches de Fix** : Si le nom de la branche source commence par `fix/`, le système applique un `patch` par défaut.
3. **Défaut (Fallback)** :
    * Merge vers `develop` → `minor` (ex: 1.1.0 → 1.2.0)
    * Merge vers `master` → `minor` (ex: 1.1.0 → 1.2.0)

### 2. Synchronisation automatique (Back-merge)

Lorsqu'une PR est fusionnée sur `master` (mise en production) :

1. La version est incrémentée sur `master`.
2. Un commit de version et un tag sont créés sur `master`.
3. **Automatique** : Les modifications de `master` sont immédiatement fusionnées dans `develop` pour garantir que les
   deux branches partagent le même numéro de version officiel et les mêmes tags.

---

## 📖 Exemples concrets

### Scénario A : Ajout d'une nouvelle fonctionnalité sur `develop`

* **Action** : Vous créez une branche `feature/nouvelle-galerie`, vous travaillez et ouvrez une PR vers `develop`.
* **Cas 1 (Pas de label)** : Merge de la PR → Passage de `1.1.0` à **`1.2.0`**.
* **Cas 2 (Label `patch`)** : Si c'est un petit ajout cosmétique → Passage de `1.1.0` à **`1.1.1`**.

### Scénario B : Correction d'un bug urgent sur `develop`

* **Action** : Création d'une branche `fix/bug-affichage`, PR vers `develop`.
* **Résultat** : Merge de la PR → Détection automatique du préfixe `fix/` → Passage de `1.1.1` à **`1.1.2`**.

### Scénario C : Mise en production (Release)

* **Situation actuelle** : `develop` est en `1.2.0`, `master` est en `1.1.0`.
* **Action** : PR de `develop` vers `master`.
* **Merge** : Passage de `master` à **`1.2.0`** (ou `1.3.0` selon les changements).
* **Back-merge** : `develop` reçoit les changements de `master`, elle passe également en **`1.2.0`**.

---

## 🛠️ Guide d'utilisation pour Daniel

### Au quotidien (Développement)

1. Travaillez sur vos branches `feature/*` ou `fix/*`.
2. Ouvrez une PR vers `develop`.
3. **Posez un label** (`major`, `minor`, `patch`) si vous voulez contrôler précisément la version.
4. Fusionnez la PR.

### Pour une Release

1. Ouvrez une PR de `develop` vers `master`.
2. Si c'est une version majeure (changement cassant), ajoutez le label `major`.
3. Fusionnez la PR.
4. Vérifiez que le backend et le frontend affichent bien la nouvelle version (grâce à l'Actuator).

## ⌨️ Commandes Git types

### 1. Ajouter une fonctionnalité (Feature)

```bash
# Se mettre sur develop et récupérer le dernier état
git checkout develop
git pull origin develop

# Créer une branche feature
git checkout -b feature/nom-de-ma-feature

# Faire vos commits (format Angular)
git add .
git commit -m "feat: description de ma feature"

# Publier la branche
git push -u origin feature/nom-de-ma-feature
```

*Ensuite, rendez-vous sur GitHub pour ouvrir la Pull Request vers **develop**.*

### 2. Corriger un bug (Fix)

```bash
# Créer une branche fix (le workflow choisira 'patch' automatiquement)
git checkout -b fix/nom-du-bug develop
git add .
git commit -m "fix: description du fix"
git push -u origin fix/nom-du-bug
```

### 3. Préparer une mise en production (Release)

1. Sur GitHub, ouvrez une Pull Request de la branche **develop** vers la branche **master**.
2. Ajoutez éventuellement le label `major` si nécessaire.
3. Fusionnez la PR. Le back-merge vers `develop` sera automatique.

### 4. Synchroniser votre machine locale

Après une fusion sur GitHub (car le serveur a créé de nouveaux commits et tags) :

```bash
# Mettre à jour develop
git checkout develop
git pull origin develop --tags

# Mettre à jour master
git checkout master
git pull origin master --tags
```

## 🔍 Traçabilité (Actuator)

Grâce à l'intégration de `git-commit-id-maven-plugin`, chaque build affiche dans le frontend :
`v1.2.0 (master@a1b2c3d)`
Cela vous permet de savoir exactement quel commit est déployé, quelle que soit la branche.
