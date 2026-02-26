# Rapport Clean Code — Incréments 1 et 2

> Généré le 2026-02-25 · Branche : `develop` (I1 + I2 mergés)

---

## Résumé exécutif

| Aspect | Violations | Priorité |
|--------|-----------|----------|
| SRP (Single Responsibility) | 2 majeures | 🔴 Critique |
| DRY (Don't Repeat Yourself) | 5 | 🟠 Haute |
| Méthodes trop longues | 4 | 🟠 Haute |
| Magic numbers/strings | 5 | 🟡 Moyenne |
| Logs non supprimés | 3 | 🟡 Moyenne |
| Type safety | 2 | 🟡 Moyenne |

---

## Backend — `sbgb-application`

### `ImagesService.java` (238 lignes)

**SRP — violation majeure** (ligne 22)
```java
public class ImagesService implements BuildNoiseImageUseCase, RateNoiseCosmeticRenderUseCase,
        FindNoiseBaseStructuresUseCase, DeleteNoiseCosmeticRenderUseCase, FindNoiseCosmeticRendersUseCase {
```
ImagesService implémente 5 use cases. Recommandation : extraire 5 services séparés, chacun n'implémentant qu'un seul use case.

**DRY — construction temporaire pour hash** (lignes 132-143)
```java
private int computeConfigHash(ImageRequestCmd.SizeCmd sizeCmd) {
    // Crée une instance temporaire uniquement pour appeler .configHash()
    return new NoiseBaseStructure(null, null, 0, ...).configHash();
}
```
Même pattern pour `computeCosmeticHash()` et `buildCosmeticDescription()`. Recommandation : méthodes statiques dans les records ou factory methods.

**Magic number** (ligne 81)
```java
.width(200).height(200)
```
Dimension de thumbnail hardcodée. Recommandation : constante `private static final int THUMBNAIL_SIZE = 200;`

**Méthodes trop longues** — `buildMultiLayerImage()` (lignes 193-219, 26 lignes) et `buildThumbnailCmd()` (lignes 78-94, 16 lignes).

---

### `ImageRequestCmd.java`

**Classe surchargée** — 3 classes imbriquées (`SizeCmd`, `ColorCmd`, `LayerConfig`) avec validation Jakarta Bean. Recommandation : fichiers séparés.

---

## Backend — `sbgb-exposition`

### `ImageResource.java` (95 lignes)

**DRY — `@LogExecutionTime` répété** sur 5 endpoints. Acceptable avec AOP mais l'annotation est du bruit visuel.

**Naming** — `deleteRender(@PathVariable UUID id)` : le paramètre `id` sans `("id")` explicite est fragile (incompatible avec tests `@WebMvcTest` sans flag `-parameters`). Découvert en I3 et corrigé pour `getRendersForBase`.

---

## Frontend — `sbgb-gui`

### `sbgb-param.component.ts` (627 lignes) — VIOLATION CRITIQUE

**SRP — violation majeure** : le composant gère 10+ responsabilités :
- Gestion du FormGroup (63 lignes)
- Validation des seuils (lignes 127-144)
- Extraction des valeurs vers domaine (lignes 536-575)
- Logique de notation (lignes 347-361)
- Téléchargement image (lignes 439-447)
- État `modified/built`
- Multiples subscriptions NgRx

Recommandation : découper en composants spécialisés :
- `SbgbBaseParamsComponent` — paramètres structurants
- `SbgbCosmeticParamsComponent` — couleurs + seuils
- `SbgbLayersComponent` — configuration multi-couches
- `SbgbFormStateService` — état formulaire injectable

**Méthodes trop longues** :
- `getSbgbFromForm()` : 57 lignes (lignes 478-534)
- `extractLayersFromForm()` : 41 lignes (lignes 536-575)
- `isModified()` : 26 lignes (lignes 450-476)

**DRY** — deux subscriptions quasi-identiques pour synchroniser les seuils (lignes 127-135 et 136-144). Recommandation : directive `ThresholdSync` ou méthode commune.

**Magic strings** — presets hardcodés `'DEEP_SPACE'`, `'STARFIELD'`, `'NEBULA'` (ligne ~253). Recommandation : `enum SbgbPreset`.

**Console.log non supprimés** — lignes 149, 67. Recommandation : supprimer ou utiliser logger injectable.

**Type safety** — `.value` sur `FormControl` sans typage fort (lignes 307, 311, etc.). Recommandation : `FormGroup.getRawValue()` avec interface typée.

---

### `sbgb.effects.ts` (70 lignes)

**Naming** — paramètres `resolve`, `reject` dans `loadImage()` (ligne 62) : noms trop génériques.

**Gestion erreur** — `catchError((error)` sans typage (ligne 31). Recommandation : `(error: HttpErrorResponse)`.

**Console.log** — ligne 67 : `console.log('promise')` — résidu de debug.

---

### `sbgb.reducer.ts` (64 lignes)

**Magic strings** — lignes 32, 55 : `'Image generated successfully'`, `'Ciel étoilé sauvegardé'`. Recommandation : enum `NotificationMessage`.

**État incomplet** — `errorMessage` non réinitialisé dans les reducers d'erreur (`imagesBuildFail`, `imagesLoadFail`).

---

### `images.service.ts`

**DRY** — payload de `buildImage()` et `rateRender()` sont quasi-identiques. Recommandation : méthode privée `buildImagePayload(sbgb)`.

**URLs hardcodées** — 3 constantes d'URL dans le service. Recommandation : `api.constants.ts` centralisé.

---

## Plan de refactoring recommandé

### 🔴 Critique (bloquer I3)

Aucun — les violations critiques n'empêchent pas la livraison mais dégradent la maintenabilité.

### 🟠 Haute (à planifier en post-I3)

1. **`ImagesService`** → 5 services séparés (1 use case = 1 service)
2. **`SbgbParamComponent`** → 4 composants spécialisés + `SbgbFormStateService`
3. **Suppression console.log** dans tous les fichiers frontend

### 🟡 Moyenne (backlog technique)

4. **`ImageRequestCmd`** → fichiers séparés pour `SizeCmd`, `ColorCmd`
5. **Magic numbers/strings** → constantes + enums dédiés
6. **`@PathVariable`** → nommage explicite sur tous les endpoints (`deleteRender`)

---

## Bénéfices attendus après refactoring

| Métrique | Avant | Cible |
|----------|-------|-------|
| LOC par classe (max) | 627 | < 200 |
| Responsabilités par classe | 5-10 | 1 |
| DRY violations | 5 | 0 |
| Magic strings | 8 | 0 |
