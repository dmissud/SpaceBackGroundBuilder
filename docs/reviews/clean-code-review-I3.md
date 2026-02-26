# Rapport Clean Code — Incrément 3

> Généré le 2026-02-25 · Branche : `develop` (I3 mergé via PR #44)

---

## Résumé exécutif

| Aspect | Violations | Priorité |
|--------|-----------|----------|
| SRP (Single Responsibility) | 2 critiques | 🔴 Critique |
| DRY (Don't Repeat Yourself) | 4 | 🔴 Critique / 🟠 Haute |
| Méthodes trop longues | 2 | 🟠 Haute |
| Gestion d'erreur incohérente | 1 | 🟠 Haute |
| Subscription leaks | 1 | 🟡 Moyenne |
| Magic numbers/strings | 3 | 🟡 Moyenne |
| Naming / typage faible | 2 | 🟢 Basse |

---

## 1. `sbgb-param.component.ts` — CRITIQUE

### V1.1 — Violation SRP : 10+ responsabilités dans un seul composant
**Sévérité :** 🔴 Critique

Le composant mélange : gestion des formulaires, détection de changements structurants, notation, rendus sauvegardés, comparaison d'objets, extraction de `Sbgb`, application de presets, états UI.

**Correction :** Extraire 4 services dédiés :
```typescript
LayerFormStateService      // Gestion des layers + threshold logic
SbgbComparisonService      // isModified()
SbgbFormExtractorService   // getSbgbFromForm(), extract*FormValues()
PresetApplicationService   // applySbgbPreset()
```

---

### V1.2 — DRY : 40+ chaînes constantes dupliquées
**Sévérité :** 🟠 Haute

Les `private static readonly CONTROL_WIDTH = 'width'` etc. sont redéfinis et utilisés en double partout.

**Correction :** Créer un `enum FormControlName { WIDTH = 'width', SEED = 'seed', ... }` partagé.

---

### V1.3 — Méthode trop longue : `extractLayersFromForm()` (39 lignes)
**Sévérité :** 🔴 Critique

Répétition du motif `layer{N}_*` × 9 propriétés × 3 layers = 27 lignes dupliquées.

**Correction :**
```typescript
private extractLayersFromForm(): LayerConfig[] {
  return [
    this.extractLayerConfig('0', 'background'),
    this.extractLayerConfig('1', 'nebula'),
    this.extractLayerConfig('2', 'stars')
  ];
}

private extractLayerConfig(index: string, name: string): LayerConfig { ... }
```

---

### V1.4 — Magic numbers : valeurs par défaut des layers sans sens métier
**Sévérité :** 🟡 Moyenne

`layer0_octaves: new FormControl(3)`, `layer1_persistence: new FormControl(0.6)`, etc.

**Correction :** Créer `LAYER_DEFAULTS: Record<string, LayerDefaults>` avec les valeurs nommées.

---

### V1.5 — Subscription leaks : 2 subscriptions internes non nettoyées
**Sévérité :** 🟡 Moyenne

`this._myForm.valueChanges.subscribe(...)` et `this.cosmeticForm.get(...).valueChanges.subscribe(...)` ne sont pas unsubscribées dans `ngOnDestroy`.

**Correction :** Pattern `takeUntil(this.destroy$)` sur toutes les subscriptions.

---

### V1.6 — Gestion d'état complexe : conditions imbriquées dans `ngOnInit`
**Sévérité :** 🟡 Moyenne

```typescript
if (sbgb && sbgb.id) {
  if (isDifferentImage) { /* 25 lignes */ }
}
```

**Correction :** Extraire `loadSbgbIfDifferent(sbgb)` avec early returns.

---

### V1.7 — DRY : `isModified()` fragile (27 comparaisons manuelles)
**Sévérité :** 🟠 Haute

Chaque nouveau champ de `Sbgb` doit être ajouté manuellement.

**Correction :** Extraire dans `SbgbComparisonService` avec décomposition `structuresEqual()` / `colorsEqual()`.

---

### V1.8 — Naming / typage faible
**Sévérité :** 🟢 Basse

- `protected _myForm` → `private combinedForm`
- `private baseFormSnapshot: any` → `private baseFormSnapshot: Record<string, unknown>`
- `protected isModifiedSinceBuild` → `private isDirtyAfterBuild`

---

### V1.9 — Trop de getters redondants (`can*` + `get*Tooltip`)
**Sévérité :** 🟢 Basse

8 getters `canRate()`, `canBuild()`, `canSave()`, `canDownload()` + leurs tooltips. Pattern répétitif.

**Correction :** `SbgbActionStateService` ou Angular Signals (`computed()`).

---

## 2. `sbgb-structural-change-dialog.component.ts` — BON

### V2.1 — Template inline avec logique de pluralisation
**Sévérité :** 🟢 Basse

```html
{{ rendersCount }} rendu{{ rendersCount > 1 ? 's' : '' }}
```

**Correction :** Extraire dans un fichier `.html` ou créer un `PluralPipe`.

---

## 3. `sbgb-shell.component.html` — BON

### V3.1 — Couplage direct avec le composant enfant
**Sévérité :** 🟢 Basse

`paramComponent!.renders` et `paramComponent!.deleteRenderById(render.id)` accèdent directement à l'implémentation du composant enfant.

**Correction :** Passer `renders` via `@Input` et `renderDeleted` via `@Output` sur `SbgbParamComponent`.

---

## 4. `sbgb.effects.ts` — CRITIQUE

### V4.1 — SRP : logique `FileReader` mélangée dans les effects + `console.log` de debug
**Sévérité :** 🟠 Haute

```typescript
console.log('promise')  // DEBUG LOG OUBLIÉ en production !
```

Type `resolve` : `(value: (PromiseLike<string | ArrayBuffer | null> | string | ArrayBuffer | null)) => void` — illisible.

**Correction :** Extraire `ImageBlobConverterService.convertBlobToBase64(blob: Blob): Observable<string>`.

---

### V4.2 — KISS : double conversion async inutile (Promise wrapper sur Observable)
**Sévérité :** 🟡 Moyenne

`mergeMap(response => new Promise(...))` ajoute une couche inutile.

**Correction :**
```typescript
concatMap(response => this.imageConverter.convertBlobToBase64(response.body!)),
```

---

### V4.3 — Gestion d'erreur incohérente sur les 5 effects
**Sévérité :** 🟠 Haute

| Effect | Pattern |
|--------|---------|
| `buildImage$` | `message: error` (objet entier) |
| `loadImages$` | `message: error.message` |
| `rateImage$` | `message: error.error?.message \|\| error.message` |
| `loadRendersForBase$` | `message: error.message` |
| `deleteRender$` | `message: error.message` |

**Correction :** `HttpErrorHandlerService.extractMessage(error): string` utilisé partout.

---

### V4.4 — Pattern `action.type ===` au lieu de `ofType` séparés
**Sévérité :** 🟢 Basse

```typescript
ofType(imagesSaveSuccess, imagesSaveFail).subscribe(action => {
  if (action.type === imagesSaveSuccess.type) { ... }
})
```

**Correction :** Deux subscriptions `ofType(imagesSaveSuccess)` et `ofType(imagesSaveFail)` distinctes.

---

## 5. `sbgb.reducer.ts` — BON

### V5.1 — Magic string dans le reducer
**Sévérité :** 🟢 Basse

```typescript
infoMessage: 'Image generated successfully'  // dans le reducer
```

**Correction :** Passer le message dans l'action (`props<{..., message?: string}>()`), le reducer lit `message ?? 'Image generated successfully'`.

---

## 6. `images.service.ts` (Angular) — BON

### V6.1 — Construction d'URLs incohérente
**Sévérité :** 🟢 Basse

Mélange de `this.appUrl + this.basesApiUrl`, `\`${this.appUrl}/images/bases/${baseId}/renders\`` et concatenation directe.

**Correction :** Objet `endpoints` centralisé + méthode `getFullUrl(endpoint)`.

---

## 7. `ImagesService.java` — CRITIQUE

### V7.1 — SRP : 17 méthodes, 5+ responsabilités
**Sévérité :** 🔴 Critique

Mélange : génération d'images, validation, thumbnails, gestion bases, gestion rendus, sérialisation, construction calculateurs couleur.

**Correction :** Extraire 4 services + 1 orchestrateur :
```java
ImageGenerationService        // buildSingleLayer / buildMultiLayer
ImageSerializationService     // toByteArray, buildThumbnail
NoiseBaseStructureService     // findOrCreateBase, computeConfigHash
NoiseCosmeticRenderService    // findOrCreateRender, computeCosmeticHash
ImagesOrchestrationService    // implémente les use cases, orchestre les 4 services
```

---

### V7.2 — Méthode trop longue : `buildMultiLayerImage()` (27 lignes)
**Sévérité :** 🟠 Haute

Mapping `Layer → LayerConfig` inline dans la méthode.

**Correction :** Extraire `toLayerConfig(layer)` + `toLayerConfigs(layers)`.

---

### V7.3 — DRY : duplication de construction `NoiseBaseStructure` temporaire
**Sévérité :** 🟠 Haute

`computeConfigHash()` et `buildBaseStructure()` construisent tous les deux une instance temporaire identique juste pour appeler `configHash()`.

**Correction :** Builder statique sur `NoiseBaseStructure` exposant `computeConfigHash()`.

---

### V7.4 — DRY : duplication dans `findOrCreateRender()`
**Sévérité :** 🟡 Moyenne

Pattern `new NoiseCosmeticRender(...)` répété deux fois avec 12 paramètres chacun.

**Correction :** Extraire `updateWithNewNote(existing, note, thumbnail)` et `createNewRender(cmd, base, thumbnail)`.

---

### V7.5 — Nullable check fragmenté
**Sévérité :** 🟢 Basse

`layersToString()` : `Optional.ofNullable(sizeCmd.getLayers()).map(Object::toString).orElse(null)` est plus expressif.

---

### V7.6 — Validation isolée non centralisée
**Sévérité :** 🟡 Moyenne

`validateNote()` privée dans `ImagesService` — devrait être dans un `@Component ImagesServiceValidator`.

---

## 8. `ImageResource.java` — TRÈS BON

### V8.1 — HTTP status magic number
**Sévérité :** 🟢 Basse

`.status(201)` → `.status(HttpStatus.CREATED)`.

---

## Actions recommandées par priorité

### Phase 1 — Critique
- Refactorer `SbgbParamComponent` : extraire 4 services dédiés
- Supprimer le `console.log('promise')` et extraire `ImageBlobConverterService`
- Refactorer `ImagesService.java` : créer 4 services + 1 orchestrateur

### Phase 2 — Haute
- Créer `enum FormControlName` (remplacer 40+ strings)
- Implémenter `takeUntil(destroy$)` sur toutes les subscriptions
- Créer `HttpErrorHandlerService` (Angular) pour uniformiser les `catchError`
- Extraire `toLayerConfig()` de `buildMultiLayerImage()`

### Phase 3 — Moyenne
- `SbgbComparisonService` : centraliser `isModified()`
- `LAYER_DEFAULTS` : remplacer les magic numbers
- `ImagesServiceValidator` (Java) : extraire la validation

### Phase 4 — Basse
- Améliorer le naming (`_myForm`, `baseFormSnapshot: any`)
- Passer `renders` / `renderDeleted` via `@Input` / `@Output`
- HTTP status via `HttpStatus.CREATED`

---

## Score global I3

| Domaine | Score | Verdict |
|---------|-------|---------|
| Frontend Angular | 5/10 | SbgbParamComponent surchargé, SbgbEffects problématique |
| Backend Java | 6/10 | ImagesService.java surchargé, ImageResource très bon |
| Architecture | 6/10 | SRP violé à plusieurs niveaux |
| SOLID | 5/10 | SRP violé (V1.1, V7.1), autres principes OK |

**Verdict :** I3 est fonctionnel mais `SbgbParamComponent` et `ImagesService.java` nécessitent un refactoring avant d'aborder I4.
