# Historique des Réalisations et Refactorings

Ce document compile l'historique du projet, incluant le journal de réalisation des nouvelles fonctionnalités et le suivi des refactorings Clean Code.

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
