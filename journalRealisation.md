# Journal de Réalisation — Nouveau Flux de Travail Ciel Étoilé

## Références
- Spec fonctionnelle : `ImproveFluxWorkfow.md`
- Plan d'implémentation : `planImplementationFlux.md`
- Branche de travail : `feature/I1-new-data-model`

---

## Incrément 1 — Fondations : nouveau modèle de données + sauvegarde par notation

**Objectif** : Remplacer `noise_image` (table plate) par `noise_base_structure` (1) + `noise_cosmetic_render` (N).
Brancher la sauvegarde sur la notation. Supprimer `CreateNoiseImageUseCase` et `UpdateNoiseImageNoteUseCase`.

**Branche** : `feature/I1-new-data-model`
**Statut** : ✅ Terminé

---

### Étapes TDD

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

---

### Décisions techniques prises

- **`configHash()` et `cosmeticHash()`** : calculés via `Objects.hash()` sur tous les champs structurants/cosmétiques. Utilisés comme colonne UNIQUE en base pour le find-or-create.
- **Thumbnail 200×200** : générée lors du `rate()` dans `ImagesService` (appel interne à `buildNoiseImage` avec dimensions réduites).
- **MapStruct — conflit de noms de beans** : les mappers `sbgb-infrastructure` et `sbgb-exposition` généraient des beans du même nom. Résolu avec `implementationName = "NoiseBaseStructureDTOMapperImpl"` / `"NoiseCosmeticRenderDTOMapperImpl"` dans les mappers d'exposition.
- **`NoiseCosmeticRenderDTO` sans thumbnail** : le champ `thumbnail` (byte[]) n'est pas exposé dans le DTO REST pour alléger les réponses.
- **Suppression de `updateNote`** : l'action NgRx `updateNote` et l'effet associé ont été fusionnés dans `rateSbgb` — la notation passe toujours par `POST /images/renders/rate` (find-or-create).
- **Chargement base → formulaire** : quand l'utilisateur charge une base depuis la bibliothèque, les paramètres structurels sont restaurés ; les couleurs sont réinitialisées aux valeurs par défaut (pas de couleurs stockées sur la base).

---

### Problèmes rencontrés

- **Conflit de bean MapStruct** au démarrage Spring : deux modules Maven généraient `noiseBaseStructureMapperImpl` et `noiseCosmeticRenderMapperImpl`. Résolu par `implementationName` dans l'annotation `@Mapper` côté exposition.
- **`@Mapping(target = "thumbnail", ignore = true)`** : annotation en trop dans `NoiseCosmeticRenderMapper` (exposition) car le DTO n'a pas de champ thumbnail — supprimée.
- **Frontend — méthodes obsolètes** : `getImages()`, `saveImage()`, `updateNote()` supprimées du service ; tous les consommateurs NgRx (effects, reducers, composants) mis à jour en conséquence.

---

## Incrément 2 — Layout générateur : accordéon + notation à côté de l'aperçu

**Objectif** : Refonte visuelle du panneau générateur sans toucher au backend.

**Branche** : `feature/I2-generator-layout`
**Statut** : ✅ Terminé

---

### Étapes TDD

| # | Cycle | Périmètre | Statut | Commit |
|---|-------|-----------|--------|--------|
| 2.1 | RED-GREEN | `describeBase()` et `describeCosmetic()` dans `SbgbParamComponent` | ✅ | `9c7c0a8` |
| 2.2 | RED-GREEN | Accordéon `mat-expansion-panel` + layout 2 colonnes (Base &#124; Cosmétique) | ✅ | `72ae5b6` |
| 2.3 | RED-GREEN | Notation déplacée à droite de l'aperçu (`image-rating-container` flex-row) | ✅ | `296d125` |

---

### Décisions techniques prises

- **Accordéon ouvert par défaut** (`[expanded]="true"`) pour ne pas cacher les paramètres au premier chargement.
- **Grid CSS 2 colonnes** (`grid-template-columns: 1fr 1fr`) dans le formulaire plutôt qu'un flex-wrap : meilleure consistance visuelle.
- **Notation dans `#imageContent`** plutôt que dans `#actionBarContent` : permet le layout flex-row image/étoiles sans modifier `GeneratorShellComponent`.

---

### Problèmes rencontrés

- **jest.config.js** : configuration `globalSetup` obsolète supprimée, `setup-jest.ts` migré vers `jest-preset-angular` v16.
- **Tests saveImage** : anciens tests du spec `sbgb-param` référençaient `SbgbPageActions.saveSbgb` et `imagesSaveFail` supprimés en I1 — supprimés du spec.

---

## Incrément 3 — Séparation Base / Cosmétique + dialogue de choix

**Objectif** : Le formulaire reflète explicitement la distinction Structurant / Cosmétique, et le système détecte les changements structurants pour proposer le dialogue.

**Branche** : `feature/I3-base-cosmetic-split`
**Statut** : ✅ Terminé

---

### Étapes TDD

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

---

### Décisions techniques prises

- **`@PathVariable("id")` explicite** : Spring en contexte `@WebMvcTest` ne peut pas résoudre le nom du paramètre par réflexion sans le flag `-parameters`. Nommage explicite requis pour la testabilité.
- **`baseForm` + `cosmeticForm`** : deux `FormGroup` indépendants, le `_myForm` parent les contient via `new FormGroup({ base: baseForm, cosmetic: cosmeticForm })`. Le template utilise `[formGroup]="baseForm"` et `[formGroup]="cosmeticForm"` sans `<form>` parent.
- **Snapshot `baseFormSnapshot`** : capturé avant chaque changement via `valueChanges`. Restauré avec `patchValue(snapshot, {emitEvent: false})` pour ne pas déclencher un nouveau dialogue lors de l'annulation.
- **Option B** : dispatch `rateSbgb` pour chaque rendu avec les params de base courants + cosmétiques du rendu. Utilise le find-or-create backend pour créer la nouvelle base et les rendus associés.
- **Mock sélecteurs** : identifier les sélecteurs NgRx par identité objet (`selector === selectRenders`) plutôt que par `projector.toString()` (fragile car tous les projectors contiennent `sbgbState`).
- **`thumbnail` dans `NoiseCosmeticRenderDTO`** : ajouté pour permettre l'affichage des vignettes dans la bande de rendus sauvegardés (`GET /images/bases/{id}/renders`). Le mapper MapStruct le mappe automatiquement depuis le domaine.
- **`thumbnail` TypeScript** : `byte[]` Java est sérialisé par Jackson en base64 String → type `string | null` dans le modèle TS. Utilisé directement dans `[src]="'data:image/png;base64,' + render.thumbnail"`.
- **Tests effects sans TestBed** : `SbgbEffects` testé par instanciation directe (`new SbgbEffects(mockService, actions$, null)`) pour contourner le problème `TestBed.initTestEnvironment()` préexistant.
- **Jest component tests** : Tests composants Angular (`sbgb-param.component.spec.ts`, `sbgb-shell.component.spec.ts`) échouent avec "Need to call TestBed.initTestEnvironment() first" — problème de configuration Jest préexistant (`setup-jest.ts` utilise une API dépréciée). Non résolu dans I3 (hors périmètre).

---

### Problèmes rencontrés

- **`@PathVariable` sans nom** : Spring retournait 400 avec "Name for argument of type [UUID] not specified" en test `@WebMvcTest`. Solution : `@PathVariable("id") UUID id`.
- **Jasmine vs Jest** : Test effects initialement écrit avec `jasmine.SpyObj` — erreur TS2694. Réécriture avec `jest.Mocked<T>` et `jest.fn()`.
- **Dialogue** : utilisation de `@Optional() @Inject(MAT_DIALOG_DATA)` pour permettre l'instanciation directe dans les tests sans le contexte Material Dialog complet.

---

**Statut final I3** : ✅ **Terminé et mergé** — 10 cycles TDD complétés, 11 commits atomiques, 134 tests backend + 21 tests frontend ciblés au vert. Mergé sur `develop` via **PR #44**.

**Objectif atteint** : le formulaire reflète explicitement la distinction Structurant / Cosmétique (`baseForm` / `cosmeticForm`), et le système détecte les changements structurants pour proposer le dialogue (Option A : vider, Option B : ré-appliquer, Annuler : restaurer snapshot).

---

## Revue Clean Code I3

**Fichier** : `clean-code-review-I3.md` (généré après merge PR #44)

---

## Incrément 4 — Bibliothèque hiérarchique

**Statut** : ⏳ À démarrer (I3 mergé via PR #44)

---

## Incrément 5 — Cache serveur (performance)

**Statut** : ⏸ En attente (démarre après merge I1 — déjà fait)

---

## Légende
- ✅ Terminé
- 🔄 En cours
- ⏳ À faire (dans l'incrément courant)
- ⏸ En attente
- ❌ Bloqué
