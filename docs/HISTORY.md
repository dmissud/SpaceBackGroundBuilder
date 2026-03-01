# Historique des Réalisations et Refactorings

Ce document compile l'historique du projet, incluant le journal de réalisation des nouvelles fonctionnalités et le suivi des refactorings Clean Code.

---

## Feature — I5 : Cache serveur pour les images Galaxy (2026-03-01)

**Branche** : `feature/I5-galaxy-server-cache`
**Statut** : ✅ Terminé

### Actions réalisées
- **Port `GalaxyImageComputationPort`** : extraction de la logique lourde de rendu hors de `GalaxyService`.
- **Adaptateur Caffeine `CachedGalaxyImageAdapter`** : `@Cacheable(value = "galaxyImage", key = "#configHash")`.
- **Eviction de Cache** : Ajout de `@CacheEvict(value = "galaxyImage", allEntries = true)` lors de la notation si la base change.
- **Configuration Caffeine** : mise à jour de `CacheConfig` pour inclure ce nouveau cache (TTL 30 min, max 50).

## Fix — Routing K8s Nginx (2026-02-26)

**Branche** : `fix/k8s-nginx-routing`
**PR** : #51
**Statut** : ✅ Terminé

### Problème

La version Kubernetes ne fonctionnait pas (404 sur l'API, retour HTML au lieu de JSON) alors que Docker Compose fonctionnait correctement. Trois causes cumulées :

1. **`rewrite-target: /$2`** dans l'Ingress transformait `/sbgb/api/images` en `/api/images`, qui arrivait sur le Nginx frontend sans proxy configuré
2. **ConfigMap Nginx minimaliste** — aucun `proxy_pass` vers le backend, toutes les requêtes retournaient `index.html`
3. **Ordre des paths Ingress** — la règle générique `/sbgb(/|$)(.*)` capturait tout avant `/sbgb/api(/|$)(.*)`

### Solution

- Suppression du `rewrite-target` dans l'Ingress (base + Helm)
- Ingress simplifié : une seule règle `Prefix /sbgb/` → frontend
- ConfigMap remplacée par une config nginx complète alignée sur le `nginx.conf` Docker Compose
- Le Nginx du pod frontend proxy `/sbgb/api/` → `sbgb-backend:8080/api/`

### Flux après fix

```
Browser → Ingress (Prefix /sbgb/) → Frontend Nginx Pod
                                       ├── /sbgb/api/* → proxy_pass → sbgb-backend:8080/api/*
                                       └── /sbgb/*     → Angular SPA (try_files)
```

### Fichiers modifiés

- `k8s/sbgb/base/ingress.yaml`
- `k8s/sbgb/base/frontend.yaml`
- `k8s/helm/sbgb/templates/ingress.yaml`

---

## 1. Journal de Réalisation — Nouveau Flux de Travail Ciel Étoilé

### Références
- Spec fonctionnelle : `docs/sbgb-cmd-workflow-spec.md`
- Plan d'implémentation : `docs/sbgb-cmd-workflow-plan.md`

---

### Incrément 1 — Fondations : nouveau modèle de données + sauvegarde par notation

**Objectif** : Remplacer `noise_image` (table plate) par `noise_base_structure` (1) + `noise_cosmetic_render` (N).
Brancher la sauvegarde sur la notation. Supprimer `CreateNoiseImageUseCase` et `UpdateNoiseImageNoteUseCase`.

**Branche** : `feature/I1-new-data-model`
**Statut** : ✅ Terminé

#### Étapes TDD

| # | Cycle | Périmètre | Statut | Commit |
|---|-------|-----------|--------|--------|
| 1.1 | RED-GREEN-REFACTOR | Entité domaine `NoiseBaseStructure` (record, `configHash()`, `generateDescription()`) | ✅ | `test(domain): add NoiseBaseStructure with config hash and description generation` |
| 1.2 | RED-GREEN-REFACTOR | Entité domaine `NoiseCosmeticRender` (record, `cosmeticHash()`, `generateDescription()`) | ✅ | `test(domain): add NoiseCosmeticRender with cosmetic hash and description generation` |
| 1.3 | RED-GREEN-REFACTOR | Ports OUT complétés (`NoiseBaseStructureRepository`, `NoiseCosmeticRenderRepository`) | ✅ | `feat(domain): complete NoiseBaseStructureRepository and NoiseCosmeticRenderRepository ports` |
| 1.4 | RED-GREEN-REFACTOR | `RateNoiseCosmeticRenderUseCase` (find-or-create Base + Rendu, recalcul `maxNote`) | ✅ | `feat(domain): implement RateNoiseCosmeticRenderUseCase with find-or-create logic` |
| 1.5 | RED-GREEN-REFACTOR | `FindNoiseBaseStructuresUseCase` + `DeleteNoiseCosmeticRenderUseCase` (orphelin + recalcul) | ✅ | `feat(domain): implement FindNoiseBaseStructuresUseCase and DeleteNoiseCosmeticRenderUseCase` |
| 1.6 | RED-GREEN-REFACTOR | Entités JPA, adapters persistence, mappers MapStruct, migration Liquibase | ✅ | `feat(infra): add JPA entities, adapters and Liquibase migration for new noise model` |
| 1.7 | RED-GREEN-REFACTOR | Controller REST (`POST /images/build`, `POST /images/renders/rate`, `GET /images/bases`, `DELETE /images/renders/{id}`) | ✅ | `feat(exposition): rewrite ImageResource with new noise workflow endpoints` |
| 1.8 | RED-GREEN-REFACTOR | Frontend NgRx : actions, reducer, effects, selectors, composants adaptés | ✅ | `feat(ui): adapt NgRx state and components to new noise workflow API` |

#### Décisions techniques prises
- **`configHash()` et `cosmeticHash()`** : calculés via `Objects.hash()` sur tous les champs structurants/cosmétiques. Utilisés comme colonne UNIQUE en base pour le find-or-create.
- **Thumbnail 200×200** : générée lors du `rate()` dans `ImagesService` (appel interne à `buildNoiseImage` avec dimensions réduites).
- **MapStruct — conflit de noms de beans** : les mappers `sbgb-infrastructure` et `sbgb-exposition` généraient des beans du même nom. Résolu avec `implementationName = "NoiseBaseStructureDTOMapperImpl"` / `"NoiseCosmeticRenderDTOMapperImpl"` dans les mappers d'exposition.
- **`NoiseCosmeticRenderDTO` sans thumbnail** : le champ `thumbnail` (byte[]) n'est pas exposé dans le DTO REST pour alléger les réponses.
- **Suppression de `updateNote`** : l'action NgRx `updateNote` et l'effet associé ont été fusionnés dans `rateSbgb` — la notation passe toujours par `POST /images/renders/rate` (find-or-create).
- **Chargement base → formulaire** : quand l'utilisateur charge une base depuis la bibliothèque, les paramètres structurels sont restaurés ; les couleurs sont réinitialisées aux valeurs par défaut (pas de couleurs stockées sur la base).

---

### Incrément 2 — Layout générateur : accordéon + notation à côté de l'aperçu

**Objectif** : Refonte visuelle du panneau générateur sans toucher au backend.

**Branche** : `feature/I2-generator-layout`
**Statut** : ✅ Terminé

#### Étapes TDD

| # | Cycle | Périmètre | Statut | Commit |
|---|-------|-----------|--------|--------|
| 2.1 | RED-GREEN | `describeBase()` et `describeCosmetic()` dans `SbgbParamComponent` | ✅ | `9c7c0a8` |
| 2.2 | RED-GREEN | Accordéon `mat-expansion-panel` + layout 2 colonnes (Base | Cosmétique) | ✅ | `72ae5b6` |
| 2.3 | RED-GREEN | Notation déplacée à droite de l'aperçu (`image-rating-container` flex-row) | ✅ | `296d125` |

#### Décisions techniques prises
- **Accordéon ouvert par défaut** (`[expanded]="true"`) pour ne pas cacher les paramètres au premier chargement.
- **Grid CSS 2 colonnes** (`grid-template-columns: 1fr 1fr`) dans le formulaire plutôt qu'un flex-wrap : meilleure consistance visuelle.
- **Notation dans `#imageContent`** plutôt que dans `#actionBarContent` : permet le layout flex-row image/étoiles sans modifier `GeneratorShellComponent`.

---

### Incrément 3 — Séparation Base / Cosmétique + dialogue de choix

**Objectif** : Le formulaire reflète explicitement la distinction Structurant / Cosmétique, et le système détecte les changements structurants pour proposer le dialogue.

**Branche** : `feature/I3-base-cosmetic-split`
**Statut** : ✅ Terminé

#### Étapes TDD

| # | Cycle | Périmètre | Statut | Commit |
|---|-------|-----------|--------|--------|
| 3.1 | RED-GREEN-REFACTOR | Use case `FindNoiseCosmeticRendersUseCase` (port OUT + impl + test) | ✅ | `feat(domain): add FindNoiseCosmeticRendersUseCase to list renders by base id` |
| 3.2 | RED-GREEN-REFACTOR | Endpoint `GET /images/bases/{id}/renders` + `thumbnail` dans `NoiseCosmeticRenderDTO` | ✅ | `feat(exposition): add GET /images/bases/{id}/renders endpoint` |
| 3.3 | RED-GREEN-REFACTOR | NgRx : actions `loadRendersForBase`, `deleteRender` + reducer `renders[]` + selectors | ✅ | `feat(ui): add NgRx actions, reducer and selectors for renders management` |
| 3.4 | RED-GREEN-REFACTOR | Effect `loadRendersForBase$` + `deleteRender$` + `images.service.ts` update | ✅ | `feat(ui): add loadRendersForBase$ and deleteRender$ effects with service call` |
| 3.5 | RED-GREEN-REFACTOR | `sbgb-param.component` : subscribe `selectRenders`, dispatch `loadRendersForBase` + `deleteRenderById` | ✅ | `feat(ui): integrate renders strip in sbgb-param and shell components` |
| 3.6 | RED-GREEN-REFACTOR | Dialogue `SbgbStructuralChangeDialogComponent` (Option A vider / Option B ré-appliquer / Annuler) | ✅ | `feat(ui): add SbgbStructuralChangeDialogComponent with three choices` |
| 3.7 | RED-GREEN-REFACTOR | Bande de vignettes des rendus sauvegardés + corbeille par rendu | ✅ | (inclus dans commit 3.5) |
| 3.8 | RED-GREEN-REFACTOR | Séparation `baseForm` / `cosmeticForm` : deux sous-groupes distincts dans le FormGroup + template mis à jour | ✅ | `feat(ui): split form into baseForm and cosmeticForm sub-groups (I3 cycle 3.8)` |
| 3.9 | RED-GREEN-REFACTOR | Détection `baseForm.valueChanges` + snapshot + dialogue + Option A (vider) + Annuler (restaurer snapshot) | ✅ | `feat(ui): detect structural changes and open dialog with clear/cancel options (I3 cycle 3.9)` |
| 3.10 | RED-GREEN | Option B (ré-appliquer) : dispatch `rateSbgb` pour chaque rendu avec nouveaux params Base + cosmétiques existants | ✅ | `test(ui): add reapply renders spec validating Option B dispatches rateSbgb per render (I3 cycle 3.10)` |

#### Décisions techniques prises
- **`@PathVariable("id")` explicite** : Spring en contexte `@WebMvcTest` ne peut pas résoudre le nom du paramètre par réflexion sans le flag `-parameters`. Nommage explicite requis pour la testabilité.
- **`baseForm` + `cosmeticForm`** : deux `FormGroup` indépendants, le `_myForm` parent les contient via `new FormGroup({ base: baseForm, cosmetic: cosmeticForm })`. Le template utilise `[formGroup]="baseForm"` et `[formGroup]="cosmeticForm"` sans `<form>` parent.
- **Snapshot `baseFormSnapshot`** : capturé avant chaque changement via `valueChanges`. Restauré avec `patchValue(snapshot, {emitEvent: false})` pour ne pas déclencher un nouveau dialogue lors de l'annulation.
- **Option B** : dispatch `rateSbgb` pour chaque rendu avec les params de base courants + cosmétiques du rendu. Utilise le find-or-create backend pour créer la nouvelle base et les rendus associés.
- **`thumbnail` dans `NoiseCosmeticRenderDTO`** : ajouté pour permettre l'affichage des vignettes dans la bande de rendus sauvegardés (`GET /images/bases/{id}/renders`). Le mapper MapStruct le mappe automatiquement depuis le domaine.
- **`thumbnail` TypeScript** : `byte[]` Java est sérialisé par Jackson en base64 String → type `string | null` dans le modèle TS. Utilisé directement dans `[src]="'data:image/png;base64,' + render.thumbnail"`.

---

### Incrément 4 — Bibliothèque hiérarchique

**Objectif** : Nouveau composant `sbgb-history-list` affichant Modèles de Base et leurs Rendus en accordéon hiérarchique.
**Branche** : `feature/I4-history-library`
**Statut** : ✅ Terminé

#### Étapes TDD

| # | Cycle | Périmètre | Statut | Commit |
|---|-------|-----------|--------|--------|
| 4.1 | RED-GREEN | SbgbHistoryListComponent : accordéon, visibleBases, rendersFor, events | ✅ | `1ea64c6` |
| 4.2 | RED-GREEN | SbgbListComponent : intégration NgRx store + history-list | ✅ | `6674880` |
| 4.3 | REFACTOR | Extract mapper SRP, takeUntilDestroyed, starValues constant | ✅ | `58cb402` |

#### Composants créés/modifiés
- `sbgb-history-list.component.ts/html/scss` — nouveau composant accordéon hiérarchique (Bases → Rendus)
- `sbgb-list.component.ts` — refactorisé pour déléguer à sbgb-history-list et au store NgRx
- `sbgb-render.mapper.ts` — extraction SRP du mapping Base+Render → Sbgb

#### Tests
- 20 tests unitaires (`sbgb-history-list.component.spec`, `sbgb-list.component.spec`, `sbgb-render.mapper.spec`)

---

### Correctifs & Améliorations UX post-I4 — Bande de vignettes & Visualisation

**Branche** : `feature/I4-history-library` (suite)
**Statut** : ✅ Terminé

#### Correctifs bande de vignettes

| # | Description | Commit |
|---|-------------|--------|
| 1 | Ajout des styles CSS manquants pour la bande de vignettes (`renders-strip`, `render-card`, `render-thumbnail`, `render-stars`) | `b6f2997` |
| 2 | Passage du panneau preview de `sticky` à scrollable pour afficher la bande | `b6f2997` |
| 3 | Déplacement de la bande de vignettes au-dessus du bouton "Générer aperçu" | `3e03354` |
| 4 | Réduction des étoiles à 12px pour qu'elles tiennent dans la carte | `6d1a1bb` |

#### Sélection de vignette

| # | Description | Commit |
|---|-------------|--------|
| 5 | Surbrillance de la vignette correspondant à l'image affichée (`selectedRenderId` dans le store NgRx) | `5b40a83` |
| 6 | Clic sur une vignette → charge ses cosmétiques dans le formulaire | `0aab7a0` |
| 7 | Dispatch `selectRender` depuis la bibliothèque lors du chargement d'un rendu | `3e1ab6c` |
| 8 | Conservation de la sélection après génération ; effacement uniquement sur modification manuelle | `243d0a8` |
| 9 | Auto-sélection au démarrage si les paramètres par défaut correspondent à une base existante | `f0ed382` |

#### Feedback visuel génération disponible

| # | Description | Commit |
|---|-------------|--------|
| 10 | Point orange pulsant dans le titre de la bande quand la génération est disponible | `75aec91` |
| 11 | Fond pulsant sur le titre de l'accordéon des paramètres quand la génération est disponible | `608feb8` + `81862ed` |

#### Visualisation image

| # | Description | Commit |
|---|-------------|--------|
| 12 | Toggle taille réelle / ajusté à la fenêtre sur l'aperçu image | `73b4f87` |
| 13 | Mode plein écran (respect du mode fit ou taille réelle) via API Fullscreen | `fc280e4` |
| 14 | Boutons de contrôle toujours visibles en haut à droite (hors zone scrollable) | `b3f0ba9` |

#### Décisions techniques
- **`selectedRenderId` dans le store** : source de vérité unique pour la surbrillance, mise à jour par `selectRender`, `imagesSaveSuccess`, `clearSelectedRender`.
- **`cosmeticForm.valueChanges` + `{emitEvent: false}`** : permet de distinguer un chargement programmatique (pas de clear) d'une modification manuelle (clear de la sélection).
- **`pendingAutoSelectBaseId`** : flag local dans `sbgb-param` pour relier le chargement des bases au chargement des rendus lors de l'auto-sélection au démarrage.
- **Wrapper externe `image-wrapper`** : les boutons de contrôle sont positionnés sur le wrapper (hors du conteneur scrollable), restent visibles quel que soit le scroll. L'API Fullscreen est appelée sur ce wrapper.

---

### Refactoring Clean Code post-I4

**Branche** : `refactor/I4-clean-code`
**Statut** : ✅ Terminé

#### P1 — Quick wins (nommage, constantes, JSDoc)

| # | Description | Commit |
|---|-------------|--------|
| R1 | Renommage `_myForm` → `sbgbForm` | P1 |
| R2 | Renommage `f` → `formValues` dans `describeBase/describeCosmetic` | P1 |
| R3 | Renommage `realSize` → `isRealSize` dans `image-preview` | P1 |
| R4-R6 | Création `sbgb.constants.ts` : `STAR_RATING_VALUES`, `PresetName` enum, `INFO_MESSAGES` | P1 |
| R7 | JSDoc sur toutes les méthodes publiques (shell, param, list, history-list, image-preview, generator-shell) | P1 |
| R8 | Guard `containerRef?.` dans `toggleFullscreen()` | P1 |

#### P2 — DRY & méthodes longues

| # | Description | Commit |
|---|-------------|--------|
| R9 | Extraction `loadFormValuesFromSbgb(sbgb)` — fusionne les 2 blocs `patchValue` dupliqués | `b9826fb` |
| R10-R11 | Découpage de `ngOnInit` en 7 sous-méthodes : `setupThresholdSync`, `setupInfoMessageListener`, `setupSbgbLoader`, `setupErrorMessageListener`, `setupSaveResultListener`, `setupRendersLoader`, `setupBaseAutoSelect` | `b9826fb` |
| R12 | Subscriptions de `sbgb-list` déplacées du constructeur vers `ngOnInit` | `b9826fb` |

#### P3 — SOLID & découplage store

| # | Description | Commit |
|---|-------------|--------|
| R16 | Nouvelle action `applyRenderCosmetics({render})` — supprime l'appel direct `paramComponent.loadRenderCosmetics` depuis `sbgb-shell` | `a7b95f8` |
| — | `loadRenderCosmetics` devient `private` dans `sbgb-param` ; réagit via `setupRenderCosmeticsLoader()` (Actions ofType) | `a7b95f8` |
| — | Correction spec `sbgb-param` : `_myForm` → `baseForm`/`cosmeticForm` | `a7b95f8` |

#### Décisions techniques
- **R13 abandonné** : factory selector `createSbgbSelector` — les sélecteurs sont déjà optimaux sous forme explicite, une factory ajouterait de l'opacité sans valeur.
- **R14-R15 abandonnés** : extractions `SbgbFormExtractionService` / `SbgbPresetService` — nécessiteraient de passer `FormGroup` en paramètre, plus de complexité que de valeur (YAGNI).
- **R17 abandonné** : fusion des 3 dispatches de `onRenderSelected` — orchestration correcte, complexification non justifiée.

---

## 2. Plan d'amélioration Clean Code (Historique)

### Refactorings terminés

#### ✅ 1. NoiseGeneratorFactory + StarFieldApplicator
Extraction de la logique de creation de noise generator et d'application du star field.
- `NoiseGeneratorFactory` : gestion Perlin vs MultiLayer
- `StarFieldApplicator` : application independante du champ d'etoiles
- `GalaxyImageCalculator.create()` : 76 lignes → 15 lignes

#### ✅ 2. Strategy Pattern pour eliminer duplication
Remplacement du switch de 70 lignes par delegation a `GalaxyGeneratorFactory`.
- `GalaxyImageCalculator.createIntensityCalculator()` : 70 lignes → 10 lignes
- Suppression du pattern repetitif `param != null ? param : default`

#### ✅ 3. Extraction des Magic Numbers
Creation de 3 classes de constantes metier :
- `NoiseModulationConstants` : base/range pour chaque type de galaxie
- `RadialFalloffConstants` : exposants de falloff et denominateurs Gaussiens
- `CoreIntensityConstants` : luminosite du noyau et poids pour irregular
- 5 generators mis a jour (Spiral, Voronoi, Elliptical, Ring, Irregular)

#### ✅ 4. ImageSerializer extraction
Extraction de la serialisation d'images en composant dedie :
- `@Component ImageSerializer` avec `toByteArray(BufferedImage, String format)`
- `GalaxyService` injecte et utilise `ImageSerializer`
- Suppression de `convertToByteArray()` de GalaxyService

#### ✅ 5. Decomposition GalaxyParameters en Value Objects
Refactoring complet de GalaxyParameters en 5 phases :
- **Phase 1** : Creation de 10 Value Objects
- **Phase 2** : Migration de 14 factory methods
- **Phase 3** : Migration de 5 generators + 5 strategies
- **Phase 4** : Suppression de 63 champs legacy de GalaxyParameters
- **Phase 5** : Suppression de 20+ methodes de compatibilite

#### ✅ 6. Simplification GalaxyService + Renaming + Validation
- Creation de `GalaxyImageDuplicationHandler`
- Ajout de `GalaxyImageBuilder` inner class dans `GalaxyImage`
- Renommage : `GalaxyImageCalculator` → `GalaxyImageRenderer`, `buildImage()` → `renderPixels()`
- Creation de `GalaxyParametersValidator`
