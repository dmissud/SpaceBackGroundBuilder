# Plan d'Implémentation — Nouveau Flux de Travail Ciel Étoilé

## Principes directeurs

- Chaque incrément est une **tranche verticale** (backend + frontend) livrable et testable de bout en bout.
- L'application reste **fonctionnelle entre chaque incrément** : pas de régression sur la génération d'images.
- Chaque incrément se termine par un **commit de merge** sur `develop` avant de passer au suivant.
- La règle TDD stricte s'applique : test RED → code GREEN → refactor sur chaque use case et composant.

## Règles métier fondamentales

### Identité d'un Modèle de Base
Un `NoiseBaseStructure` **est défini par l'unicité de ses paramètres structurants**. Ce n'est pas un UUID arbitraire qui l'identifie — c'est la combinaison de ses valeurs (`seed`, `width`, `height`, `octaves`, `persistence`, `lacunarity`, `scale`, `noiseType`, `useMultiLayer`, `layersConfig`).

Lors d'une notation, le système cherche d'abord une Base existante avec ces paramètres exacts (`findByParams`). S'il en trouve une, il la réutilise. Sinon, il en crée une nouvelle. **Jamais de doublon de Base.**

### Identité d'un Rendu Cosmétique
**La même règle s'applique symétriquement au `NoiseCosmeticRender`** : un Rendu est défini par l'unicité de ses paramètres cosmétiques **au sein d'une Base donnée**. Son identité est le couple `(baseId, back, middle, fore, backThreshold, middleThreshold, interpolationType, transparentBackground)`.

Lors d'une notation, le système cherche un Rendu existant correspondant à ce couple exact (`findByParamsAndBase`). S'il en trouve un, **il met à jour sa note** et son thumbnail. Sinon, il en crée un nouveau. **Jamais de doublon de Rendu pour une même Base.**

### Résumé de la logique de notation
```
noter(paramsBase, paramsCosmétique, note)
  └─ findByParams(paramsBase)
       ├─ trouvé  → Base existante (réutilisée)
       └─ absent  → INSERT NoiseBaseStructure (description générée)
            └─ findByParamsAndBase(paramsCosmétique, base.id)
                 ├─ trouvé  → UPDATE NoiseCosmeticRender.note + thumbnail
                 └─ absent  → INSERT NoiseCosmeticRender (description générée)
                      └─ UPDATE NoiseBaseStructure.maxNote = MAX(renders.note)
```

### Description
La `description` d'une Base et d'un Rendu est **générée automatiquement par le système** à partir des valeurs des paramètres. Elle est calculée côté backend au moment de la sauvegarde et stockée en base. L'utilisateur ne la saisit jamais.

- Exemple Base : `"FBM 3 octaves — 1920×1080 — seed 42"`
- Exemple Rendu : `"Violet #6b2d8b → Orange #ff9500 → Blanc, seuils 0.30/0.70, opaque"`

---

## Vue d'ensemble des incrément

| # | Nom | Backend | Frontend | Risque |
|---|-----|---------|----------|--------|
| I1 | Fondations DB + sauvegarde par notation | ✅ | ✅ | Moyen |
| I2 | Layout générateur : accordéon + notation | ✅ | ✅ | Faible |
| I3 | Séparation Base / Cosmétique + dialogue | ✅ | ✅ | Moyen |
| I4 | Bibliothèque hiérarchique | ✅ | ✅ | Faible |
| I5 | Cache serveur (performance) | ✅ | — | Faible |

---

## Incrément 1 — Fondations : nouveau modèle de données + sauvegarde par notation ✅

**Objectif** : Remplacer la table `noise_image` par les deux nouvelles tables et brancher la sauvegarde sur la notation.

### Backend

#### Domaine (sbgb-application)
- Supprimer l'entité `NoiseImage` et son use case `CreateNoiseImageUseCase`.
- Créer les entités de domaine `NoiseBaseStructure` et `NoiseCosmeticRender` (records ou classes avec Builder + Lombok).
- Créer les use cases (interfaces + implémentations) :
  - `BuildNoiseImageUseCase` — adapté : prend `NoiseBaseStructure` + `NoiseCosmeticRender`, retourne `byte[]` (pas d'INSERT).
  - `RateNoiseCosmeticRenderUseCase` — nouveau : reçoit (params Base, params Cosmétique, note).
      1. `findByParams(paramsBase)` → Base existante ou création d'une nouvelle (description générée côté service).
      2. `findByParamsAndBase(paramsCosmétique, baseId)` → Rendu existant (mise à jour de la note) ou création d'un nouveau (description générée + thumbnail).
      3. `maxNote` de la Base recalculée = `MAX(renders.note)`.
  - `FindNoiseBaseStructuresUseCase` — liste les Bases (avec vignette du rendu le mieux noté).
- Créer les ports OUT : `NoiseBaseStructureRepository`, `NoiseCosmeticRenderRepository`.
  - `NoiseBaseStructureRepository` doit exposer `findByParams(NoiseBaseStructure)` en plus du `save` / `findAll`.
  - `NoiseCosmeticRenderRepository` doit exposer `findByParamsAndBase(NoiseCosmeticRender, UUID baseId)` en plus du `save` / `findByBaseId`.

#### Infrastructure (sbgb-infrastructure)
- Liquibase : migration RAZ — drop `noise_image`, create `noise_base_structure` + `noise_cosmetic_render`.
- Entités JPA : `NoiseBaseStructureEntity`, `NoiseCosmeticRenderEntity` + mappers MapStruct.
- Adapters : `NoiseBaseStructurePersistenceAdapter`, `NoiseCosmeticRenderPersistenceAdapter`.

#### Exposition (sbgb-exposition)
- Adapter `POST /images/build` : accepte les nouveaux DTOs (`NoiseBaseStructureCmd` + `NoiseCosmeticRenderCmd`).
- Nouveau `POST /images/renders/rate` : reçoit (params Base, params Cosmétique, note `int`), retourne `NoiseCosmeticRenderDto` (HTTP 201).
- Nouveau `GET /images/bases` : retourne `List<NoiseBaseStructureDto>` (avec vignette du meilleur rendu).
- Supprimer `POST /images/create` et `PATCH /images/{id}/note`.

### Frontend (minimal — juste pour ne pas casser l'existant)
- Adapter `images.service.ts` : remplacer `saveImage()` par l'appel à `POST /images/renders/rate`.
- Adapter `getImages()` : consommer `GET /images/bases`.
- Adapter les modèles TypeScript : `NoiseBaseStructureDto`, `NoiseCosmeticRenderDto` (sans refonte visuelle).

### Tests de validation I1
- Générer une image → aucun INSERT en DB.
- Noter à 3 → 1 Base + 1 Rendu créés en DB. Description de la Base générée en français. `Base.maxNote = 3`.
- Deuxième notation (mêmes params Base, cosmétique différente) → 1 nouveau Rendu inséré sur la **même Base** (pas de nouvelle Base). `Base.maxNote = MAX(3, nouvelle_note)`.
- Troisième notation (mêmes params Base, **même cosmétique** que le 1er Rendu, note différente) → le Rendu existant est mis à jour (note + thumbnail), pas de doublon. `Base.maxNote` recalculée.
- Notation avec params Base différents → nouvelle Base créée. 2 Bases distinctes en DB.
- `GET /images/bases` retourne les Bases avec leur description générée et la vignette du meilleur Rendu.

---

## Incrément 2 — Layout générateur : accordéon + notation à côté de l'aperçu ✅

**Objectif** : Refonte visuelle du panneau générateur sans toucher au backend. L'UX correspond à la maquette 8.1.

### Frontend uniquement

#### `sbgb-param.component`
- Envelopper la zone de paramètres dans un **accordéon Angular Material** (`mat-expansion-panel`).
  - État ouvert (`▼`) : formulaire complet visible.
  - État fermé (`▶`) : seules les deux descriptions auto-générées sont visibles en résumé.
- Restructurer le template en **deux colonnes** dans l'accordéon : Modèle de Base (gauche) | Cosmétique (droite).
  - À ce stade, le découpage des champs entre les deux colonnes est purement visuel (pas de changement de FormGroup).
- Repositionner la **notation à droite de l'aperçu** (flex-row : aperçu 75% / notation 25%).
- Ajouter la **bande de vignettes** en bas (charger depuis `GET /images/bases/{id}/renders` une fois que la Base courante est connue).
  - Clic sur une vignette → recharge les paramètres dans le formulaire + affiche l'image.

#### Descriptions auto-générées
- Implémenter deux méthodes dans le composant :
  - `describeBase()` : construit ex. `"FBM 3oct (1920×1080, seed 42)"` depuis les valeurs du FormGroup.
  - `describeCosmetic()` : construit ex. `"Violet → Orange → Blanc, seuils 0.30/0.70, opaque"`.
- Affichées dans l'accordéon (ouvert et fermé).

### Tests de validation I2
- Accordéon s'ouvre et se ferme. Descriptions visibles dans les deux états.
- Notation cliquable uniquement après une génération. Étoiles à droite de l'aperçu.
- Clic sur une vignette recharge les paramètres et l'aperçu.
- Aucune régression sur la génération et la sauvegarde par notation (I1 intact).

---

## Incrément 3 — Séparation Base / Cosmétique + dialogue de choix ✅

**Objectif** : Le formulaire reflète explicitement la distinction Structurant / Cosmétique, et le système détecte les changements structurants pour proposer le dialogue.

### Backend

- Nouveau endpoint `GET /images/bases/{id}/renders` : liste les rendus d'une Base.
- Nouveau endpoint `DELETE /images/renders/{id}` : supprime un Rendu, recalcule `maxNote`, supprime la Base si orpheline.
- Nouveau use case `DeleteNoiseCosmeticRenderUseCase`.

### Frontend

#### Séparation FormGroup
- Éclater le FormGroup actuel en deux sous-groupes :
  - `baseForm` : `seed`, `width`, `height`, `octaves`, `persistence`, `lacunarity`, `scale`, `noiseType`, `useMultiLayer`, `layers`.
  - `cosmeticForm` : `back`, `middle`, `fore`, `backThreshold`, `middleThreshold`, `interpolationType`, `transparentBackground`.
- Les deux colonnes de l'accordéon sont maintenant pilotées par ces deux sous-groupes.

#### Détection des changements structurants
- Surveiller `baseForm.valueChanges`.
- Si des rendus sauvegardés existent pour la Base courante ET que `baseForm` a changé → déclencher le **dialogue de choix** (Angular Material `MatDialog`).
  - **Option A** — Vider : appeler `DELETE /images/renders/{id}` pour chaque rendu existant, vider la bande de vignettes.
  - **Option B** — Ré-appliquer : appeler `POST /images/build` séquentiellement pour chaque rendu (avec les nouveaux params Base + cosmétique existant), mettre à jour les vignettes, remettre toutes les notes à zéro.
  - **Annuler** : restaurer les valeurs de `baseForm` à leur état précédent (snapshot avant modification).
- Le dialogue n'apparaît que si des rendus existent ET que la modification est effective (pas un simple focus/blur).

#### Bande de vignettes — suppression
- Icône corbeille sur chaque vignette → `DELETE /images/renders/{id}` → retrait de la vignette, recalcul local de `maxNote`.

### Tests de validation I3
- Modifier `seed` avec 2 rendus existants → dialogue apparaît. Option A → rendus vidés. Option B → vignettes recalculées, notes à 0.
- Modifier une couleur → pas de dialogue.
- Supprimer une vignette → `maxNote` de la Base se met à jour.
- `Annuler` dans le dialogue → `baseForm` revient à sa valeur précédente.

---

## Incrément 4 — Bibliothèque hiérarchique

**Objectif** : Nouveau composant dédié affichant les Modèles de Base et leurs Rendus en liste accordion.

### Frontend — nouveau composant `sbgb-history-list`

- Charger `GET /images/bases` → liste les Bases avec `maxNote` et vignette du meilleur rendu.
- Chaque Base est un `mat-expansion-panel` :
  - En-tête : description de la Base + `maxNote` (étoiles).
  - Contenu déployé : vignettes de tous ses Rendus avec leur note individuelle.
- Clic sur une vignette de rendu → navigation vers le Panneau Générateur, paramètres Base + Cosmétique rechargés, aperçu affiché.
- Corbeille sur un Rendu → `DELETE /images/renders/{id}`.
- Si suppression du dernier Rendu → la Base disparaît de la liste.

### Tests de validation I4
- Liste des Bases affichée avec leurs vignettes.
- Déploiement d'une Base → ses Rendus visibles.
- Clic sur un Rendu → générateur chargé avec les bons paramètres.
- Suppression du dernier Rendu → Base disparaît.

---

## Incrément 5 — Cache serveur (performance)

**Objectif** : Éviter de recalculer la grille de bruit quand les paramètres structurants n'ont pas changé.

### Backend uniquement

- Ajouter Spring Cache (`@EnableCaching`) dans `sbgb-configuration` avec Caffeine (TTL 30 min, max 50 entrées).
- Annoter la méthode de calcul de la grille de bruit avec `@Cacheable(key = "#baseStructureHash")`.
- Calculer `baseStructureHash` : hash SHA-256 des champs de `NoiseBaseStructure` (seed + dimensions + tous les params Perlin + config layers).
- Annoter l'éviction du cache sur `RateNoiseCosmeticRenderUseCase` si les params Base changent.

### Tests de validation I5
- Deux appels successifs `POST /images/build` avec les mêmes params Base → le deuxième est plus rapide (pas de recalcul).
- Modifier un param structurant → le cache est invalidé → recalcul à l'appel suivant.

---

## Séquence de branches git

```
develop
  └── feature/I1-new-data-model
  └── feature/I2-generator-layout
  └── feature/I3-base-cosmetic-split
  └── feature/I4-history-library
  └── feature/I5-noise-cache
```

Chaque branche est mergée sur `develop` après validation des tests de l'incrément.
Les branches I2, I3, I4 peuvent démarrer dès que I1 est mergé (I2 et I3 en séquence, I4 peut commencer dès I2 mergé).
I5 est indépendant et peut se faire en parallèle de I4.

---

## Points de décision à valider avant chaque incrément

| Avant | Question |
|-------|----------|
| **I1** | Confirmer les noms des colonnes Liquibase (`noise_base_structure`, `noise_cosmetic_render`) et le type de `layersConfig` (JSON varchar ou colonnes dédiées ?). |
| **I2** | Confirmer le ratio aperçu / notation (75/25 ou autre). |
| **I3** | Confirmer le comportement "Annuler" dans le dialogue : restauration du FormGroup ou laisser les nouvelles valeurs sans régénérer ? |
| **I4** | Confirmer la navigation Bibliothèque → Générateur : routage Angular (route dédiée) ou state NgRx partagé ? |
| **I5** | Confirmer le TTL du cache et la stratégie d'éviction (TTL seul, ou éviction explicite sur POST rate). |
