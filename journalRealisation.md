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

**Statut** : ⏸ En attente (démarre après merge I1)

---

## Incrément 3 — Séparation Base / Cosmétique + dialogue de choix

**Statut** : ⏸ En attente (démarre après merge I2)

---

## Incrément 4 — Bibliothèque hiérarchique

**Statut** : ⏸ En attente (démarre après merge I2)

---

## Incrément 5 — Cache serveur (performance)

**Statut** : ⏸ En attente (démarre après merge I1)

---

## Légende
- ✅ Terminé
- 🔄 En cours
- ⏳ À faire (dans l'incrément courant)
- ⏸ En attente
- ❌ Bloqué
