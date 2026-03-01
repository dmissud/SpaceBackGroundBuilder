# Roadmap Galaxy Types

## Phase A : SPIRAL (initial)
**Statut : TERMINE**

Galaxie spirale classique avec bras logarithmiques.
- Parametres : `numberOfArms`, `armWidth`, `armRotation`, `coreSize`, `galaxyRadius`
- Presets : CLASSIC, BARRED, MULTI_ARM
- Algorithme : bras spiraux logarithmiques + bruit Perlin + falloff radial

## Phase B : VORONOI_CLUSTER
**Statut : TERMINE**

Galaxie a amas type amas globulaire / galaxie irreguliere clusterisee.
- Parametres : `clusterCount`, `clusterSize`, `clusterConcentration`
- Presets : VORONOI_DEFAULT, VORONOI_DENSE, VORONOI_SPARSE
- Algorithme : centres de clusters distribues exponentiellement + contributions Gaussiennes + bruit Perlin + falloff radial

## Phase C : ELLIPTICAL
**Statut : TERMINE**

Galaxie elliptique lisse avec profil de Sersic.
- Parametres : `sersicIndex` (0.5-10.0), `axisRatio` (0.1-1.0), `orientationAngle` (0-360)
- Presets : ELLIPTICAL_DEFAULT (E3 de Vaucouleurs), ELLIPTICAL_ROUND (E0), ELLIPTICAL_FLAT (E6)
- Algorithme : profil de Sersic `I(r) = I_e * exp(-b_n * ((r/r_e)^(1/n) - 1))` + distance elliptique avec rotation + bruit Perlin subtil (0.7 + 0.3*noise) + falloff radial doux

## Phase D : STAR_FIELD
**Statut : TERMINE**

Couche d'etoiles superposee sur tous types de galaxies.
- Parametres : `starDensity` (0.0-0.01), `maxStarSize` (1-10), `diffractionSpikes` (boolean), `spikeCount` (4-8)
- Algorithme : distribution Poisson disk approximative + tailles variables (70% petites, 25% moyennes, 5% grandes) + halo Gaussien + pointes de diffraction optionnelles pour etoiles brillantes + couleurs variables (70% blanc, 15% bleu, 15% jaune)
- Migration Liquibase 08-05 pour colonnes DB

## Phase E : RING
**Statut : TERMINE**

Galaxie annulaire (type Hoag's Object).
- Parametres : `ringRadius`, `ringWidth`, `ringIntensity`, `coreToRingRatio`
- Presets : RING_DEFAULT, RING_WIDE, RING_BRIGHT, RING_THIN, RING_DOUBLE
- Algorithme : profil Gaussien centre sur ringRadius + noyau central + bruit Perlin + falloff

## Phase F : IRREGULAR
**Statut : TERMINE**

Galaxie irreguliere sans structure definie (type Nuages de Magellan).
- Parametres : `irregularity`, `irregularClumpCount`, `irregularClumpSize`
- Presets : IRREGULAR_DEFAULT (SMC), IRREGULAR_CHAOTIC, IRREGULAR_DWARF
- Algorithme : multiples centres de densite asymetriques + domain warping fort + bruit Perlin fort

## Phase G : LENTICULAR
**Statut : A FAIRE**

Galaxie lenticulaire intermediaire entre spirale et elliptique (type S0).
- Parametres envisages : `diskThickness`, `bulgeRatio`, `dustLaneIntensity`
- Algorithme : profil de Sersic (n ~ 2-3) avec disque aplati + pas de bras spiraux + disque de poussiere + bulbe central prooeminent

## Phase H : INTERACTION
**Statut : A FAIRE**

Galaxies en interaction / collision (type Antennae).
- Parametres envisages : `separation`, `massRatio`, `tidalStrength`, `interactionAngle`
- Algorithme : deux centres de masse avec parametres orbitaux + queues de maree (tidal tails) en forme de pont + deformation asymetrique des deux galaxies

---

# Ameliorations visuelles

## Ameliorations implementees

### Domain Warping
**Statut : TERMINE** (commit `aa7cf4f`)

Deformation spatiale des coordonnees pour structures filamentaires et organiques.
- Parametre : `warpStrength` (Double, 0.0-300.0, default 0.0)
- Implementation : `DomainWarpCalculator` avec deux `PerlinGenerator` independants (X et Y, seeds offsettees)
- Integration : warping des coordonnees avant calcul d'intensite dans `GalaxyImageCalculator.buildImage()`
- Applicable a tous les types de galaxie

### Gradient de couleur multi-points
**Statut : TERMINE** (commit `c07af98`)

Transitions de couleur riches et realistes avec 6 palettes pre-definies.
- Implementation : `GradientGalaxyColorCalculator` avec interpolation smoothstep entre N `ColorStop`
- Palettes : NEBULA (violet/magenta/cyan), CLASSIC (bleu/blanc), WARM (rouge/orange/jaune), COLD (bleu/cyan), INFRARED (rouge/jaune), EMERALD (vert/cyan)
- Usage : `ColorPalette.NEBULA.createCalculator()` ou `new GradientGalaxyColorCalculator(customStops)`

### Couche d'etoiles
**Statut : TERMINE** (commits `3783b6a`, `5358758`, `09614a9`, `a4d3026`)

Champ d'etoiles independant avec distribution Poisson disk.
- Parametres : `starDensity`, `maxStarSize`, `diffractionSpikes`, `spikeCount`
- Tailles variables : 70% petites (1-2px), 25% moyennes (3-4px), 5% grandes (5-maxStarSize)
- Halo Gaussien pour etoiles > 2px
- Pointes de diffraction optionnelles (4-8 branches) pour etoiles brillantes
- Couleurs : 70% blanc, 15% bleu-blanc, 15% jaune-blanc
- Integration complete : domaine -> ports -> JPA -> DTO -> frontend

## Ameliorations a implementer

### Multi-couches de noise (priorite haute)
**Statut : TERMINE**

3 couches de bruit a des echelles differentes pour profondeur et variation.
- Couches : Macro (x0.3, 50%), Meso (x1.0, 35%), Micro (x3.0, 15%)
- Implementation : `MultiLayerNoiseGenerator` wrappant 3 `PerlinGenerator` avec seeds offsettees
- Integre dans `NoiseGeneratorFactory`, active via `multiLayerNoiseParameters.enabled`
- Support complet : domaine → ports → JPA → DTO → frontend

### Bloom / Glow du noyau (priorite moyenne)
**Statut : TERMINE**

Post-traitement Gaussien sur zones a haute intensite pour effet de "debordement" lumineux.
- Parametres : `bloomRadius` (1-50), `bloomIntensity` (0.0-1.0), `bloomThreshold` (0.0-1.0)
- Algorithme : extraction masque pixels > seuil -> flou Gaussien (ConvolveOp) -> composition additive
- Implementation : `BloomPostProcessor` (domaine) + `BloomApplicator` (@Component)
- Pipeline : renderPixels → starField → bloom
- Support complet : domaine → ports → JPA (4 colonnes NOT NULL) → DTO → frontend (section "Effets visuels")

### Dark lanes / Absorption par la poussiere (priorite basse)
**Statut : A FAIRE**

Couche de bruit soustractive pour zones d'ombre realistes.
- Parametres : `dustThreshold` (0.0-1.0), `dustOpacity` (0.0-1.0)
- Algorithme : Perlin dedie avec scale basse -> soustraction conditionnelle d'intensite
- Application : apres intensite geometrique, avant calcul de couleur

---

# Refonte workflow Galaxy — Modèle Base + Rendu

Même pattern que le domaine SBGB (Ciels étoilés) : séparation des paramètres **structurants**
(forme de la galaxie) et **cosmétiques** (couleurs, bloom, étoiles). Relation 1 Base → N Rendus,
sauvegarde uniquement par notation.

## I1 — Fondations : nouveau modèle + sauvegarde par notation
**Statut : TERMINÉ** (commit `bb6b515`, branche `feature/galaxy-I1-new-data-model`)

- Tables : DROP `galaxy_image`, CREATE `galaxy_base_structure` + `galaxy_cosmetic_render`
- Domaine : records `GalaxyBaseStructure` (configHash) + `GalaxyCosmeticRender` (cosmeticHash)
- Use cases : `RateGalaxyCosmeticRenderUseCase` (findOrCreate Base + Rendu + recalcul maxNote),
  `FindGalaxyBaseStructuresUseCase`, `FindGalaxyCosmeticRendersUseCase`, `DeleteGalaxyCosmeticRenderUseCase`
- Endpoints : `POST /galaxy/renders/rate`, `GET /galaxy/bases`, `GET /galaxy/bases/{id}/renders`,
  `DELETE /galaxy/renders/{id}` — suppression de `/galaxies/create` et `/galaxies/{id}/note`
- Frontend : `rateGalaxy()` remplace `createGalaxy()` + `updateNote()`, liste affiche les bases

## I2 — Renders strip dans le panneau générateur
**Statut : TERMINÉ** (commit `62a3f0d`, branche `feature/galaxy-I2-renders-strip-gui`)

Afficher les rendus sauvegardés pour la base courante directement dans `galaxy-shell`.

- `galaxy-shell.component.html` : bande de vignettes (`#rendersContent`) — même pattern que `sbgb-shell`
- NgRx galaxy : actions `loadRendersForBase`, `deleteRender`, `selectRender`, `applyRenderCosmetics`
- Clic vignette → recharge paramètres cosmétiques dans `galaxy-param` + affiche l'image
- Synchronisation `currentNote` et `isModifiedSinceBuild` avec le store
- Rechargement automatique de la bande après notation (`rateGalaxy`)

## I3 — Détection changement structurant + dialogue
**Statut : TERMINÉ** (commit `a9e1b2c`, branche `feature/galaxy-I3-structural-change`)

Alerter l'utilisateur quand il modifie des paramètres structurants alors que des rendus existent.

- Séparation explicite `baseForm` / `cosmeticForm` dans `galaxy-param.component.ts` (via `isStructuralChange`)
- `MatDialog` de choix : **Vider** (DELETE tous les rendus) / **Ré-appliquer** (recalcul POST /galaxy/bases/{id}/reapply)
  / **Annuler** (restaurer snapshot formulaire)
- Indicateur visuel changement structurant dans le template (via `isStructuralChange` et snackbar)
- Verrouillage de l'UI pendant le recalcul massif (ngx-spinner)

## I4 — Bibliothèque hiérarchique
**Statut : TERMINÉ** (commit `7b8d9e2`, branche `feature/galaxy-I4-history-list`)

Nouveau composant `galaxy-history-list` remplaçant `galaxy-list` (tableau plat).

- `mat-accordion` : un `mat-expansion-panel` par Base (description + maxNote étoiles)
- Grille de vignettes par Rendu avec `[matTooltip]="render.description"`
- Corbeille par Rendu → `DELETE /galaxy/renders/{id}` ; Base disparaît si orpheline
- Clic Rendu → rechargement générateur (I2 requis)

## I5 — Cache serveur
**Statut : TERMINÉ**

Spring Cache (Caffeine, TTL 30 min, max 50 entrées) sur le calcul de la `BufferedImage` Galaxy.

- Clé de cache : hash des paramètres structurants (`GalaxyBaseStructure.configHash()`)
- Éviction sur nouveaux paramètres structurants lors d'un `rate()`
- Même pattern que le cache SBGB existant

---

# Decisions techniques prises

- **FBM Perlin** : le code original utilisait de la reflexion cassee pour appeler `fbm()` sur JNoise. Corrige pour utiliser l'API `octavate()` de JNoise 4.1.0 avec `FractalFunction.FBM` / `FractalFunction.RIDGED_MULTI`
- **Validation nullable** : les champs specifiques a un type (spiral, voronoi, elliptical, star field) sont des wrapper types (Integer, Double) dans `GalaxyRequestCmd` pour que Jakarta Bean Validation les ignore quand null
- **Duplicate name** : mecanisme de confirmation via 409 CONFLICT + `forceUpdate=true` (reutilise le UUID existant)
- **Spring Boot 3.5.0** : ecarte car springdoc-openapi 2.8.15 est incompatible (PatternParseException sur les resource handlers Swagger UI). On reste sur 3.4.13
- **JUnit BOM** : declare avant le Spring Boot BOM dans le pom.xml parent pour que Cucumber 7.34.2 ait acces a JUnit Platform 1.14.1
- **Domain Warping** : applicable a tous types via integration dans `GalaxyImageCalculator.buildImage()` avant le calcul d'intensite
- **Star Field** : couche independante post-traitement, compositable sur tous types de galaxies
- **Color Gradients** : `ColorPalette` enum avec palettes pre-definies, extensible avec `ColorStop` custom
