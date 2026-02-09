# Regles de developpement

## Langage

- Quand tu me parles appel moi Daniel et non pas "L'utilisateur..."

## Demarche

- TDD
- BDD
- DDD
- Clean Code
- SOLID
- KISS
- YAGNI
- DRY
- GRASP

Implémente le domaine en premier et les ports in/out ensuite.
N'oublie pas de tester les ports in/out.
Implémente aussi le frontend.
Découpe le travail en itération fonctionnelle cohérente.

## Style de code

- Utiliser des noms de variables en anglais
- Preferer les records aux classes pour les DTOs
- Toujours utiliser Lombok (@RequiredArgsConstructor, @Builder)

## Architecture

- Respecter l'architecture hexagonale (ports/adapters)
- Les services sont dans domain/service
- Les ports in/out sont des interfaces

## Tests

- Ecrire des tests Cucumber pour les use cases
- Utiliser AssertJ pour les assertions

## Git

- Utiliser le gitflow
- Commence toujours par une nouvelle branche feature
- Ajoute les fichiers modifies ou cree au repo git
- Commit avec un message clair et descriptif
- Push regulierement sur la branche de travail
- Utiliser des commits avec le format angular

---

# Contexte projet

## Description

SpaceBackGroundBuilder est un generateur d'images de fond d'ecran spatial. Il permet de creer des images de galaxies realistes avec differents algorithmes et parametres configurables.

## Stack technique

- **Backend** : Java 21, Spring Boot 3.4.13, Maven multi-modules
- **Frontend** : Angular 17, Angular Material, NgRx, TypeScript 5.3
- **BDD** : PostgreSQL, Liquibase, JPA/Hibernate
- **Libs** : JNoise 4.1.0 (bruit Perlin/FBM), MapStruct 1.6.3, Lombok
- **Tests** : JUnit 5.14.1, AssertJ 3.27.6, Cucumber 7.34.2, Mockito 5.21.0

## Architecture des modules Maven

- `sbgb-application` : domaine, services, ports (in/out), modeles
- `sbgb-infrastructure` : adapters persistence (JPA), migrations Liquibase
- `sbgb-exposition` : controllers REST, DTOs exposition
- `sbgb-configuration` : Spring Boot app, config (CORS, Swagger, etc.)
- `sbgb-cmd` : module CLI (images de bruit)
- `sbgb-gui` : frontend Angular

## Pattern pour ajouter un nouveau type de galaxie

Chaque type de galaxie suit le meme pattern :

1. **Enum** : ajouter valeur dans `GalaxyType.java`
2. **Generator** : creer `XxxGalaxyGenerator.java` implementant `GalaxyIntensityCalculator` avec Builder
3. **Tests TDD** : creer `XxxGalaxyGeneratorTest.java` (5 tests : outside radius, near core, specifique au type, reproducible, between 0 and 1)
4. **Parametres** : ajouter champs nullable dans `GalaxyParameters.java` + factory methods presets
5. **Routing** : ajouter case dans le switch de `GalaxyImageCalculator.createIntensityCalculator()`
6. **Port in** : ajouter champs valides dans `GalaxyRequestCmd.java`
7. **Persistence** : ajouter champs dans `GalaxyStructure.java` (JPA Embeddable)
8. **Service** : passer les params dans `GalaxyService.java` (builder GalaxyParameters + builder GalaxyStructure)
9. **DTO** : ajouter champs dans `GalaxyStructureDTO.java` (exposition)
10. **Migration** : creer changelog Liquibase (colonnes DOUBLE NULLABLE)
11. **Frontend model** : ajouter champs optionnels dans `galaxy.model.ts` (les 2 interfaces)
12. **Frontend component** : ajouter FormControls, presets, enable/disable dans `galaxy-param.component.ts`
13. **Frontend template** : ajouter mat-option, presets conditionnels, section conditionnelle dans `galaxy-param.component.html`
14. **Commits** : 6 commits format angular (test, feat domain, feat domain routing, feat port, feat infra, feat ui)

---

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
**Statut : A FAIRE**

Galaxie annulaire (type Hoag's Object).
- Parametres envisages : `ringRadius`, `ringWidth`, `ringEccentricity`
- Algorithme : profil Gaussien centre sur ringRadius + modulation angulaire pour variations d'epaisseur + noyau central + bruit Perlin + falloff

## Phase F : IRREGULAR
**Statut : A FAIRE**

Galaxie irreguliere sans structure definie (type Nuages de Magellan).
- Parametres envisages : `irregularity`, `fragmentCount`
- Algorithme : multiples centres de densite asymetriques + domain warping fort pour briser symetrie + frontiere fractale + bruit Perlin fort

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
**Statut : A FAIRE**

Empiler 2-3 couches de bruit a des echelles differentes pour profondeur et variation.
- Couches : Macro (x0.3, 50%), Meso (x1.0, 35%), Micro (x3.0, 15%)
- Implementation : evolution du `PerlinGenerator` ou creation d'un `MultiLayerNoiseGenerator` wrapper
- Chaque couche avec seed independante

### Bloom / Glow du noyau (priorite moyenne)
**Statut : A FAIRE**

Post-traitement Gaussien sur zones a haute intensite pour effet de "debordement" lumineux.
- Parametres : `bloomRadius` (0-100), `bloomIntensity` (0.0-1.0), `bloomThreshold` (0.1-1.0)
- Algorithme : extraction masque pixels > seuil -> flou Gaussien -> composition additive
- Implementation : post-traitement sur `BufferedImage` final via `ConvolveOp`

### Dark lanes / Absorption par la poussiere (priorite basse)
**Statut : A FAIRE**

Couche de bruit soustractive pour zones d'ombre realistes.
- Parametres : `dustThreshold` (0.0-1.0), `dustOpacity` (0.0-1.0)
- Algorithme : Perlin dedie avec scale basse -> soustraction conditionnelle d'intensite
- Application : apres intensite geometrique, avant calcul de couleur

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

---

# Plan d'amelioration Clean Code

## Refactorings termines

### ✅ 1. NoiseGeneratorFactory + StarFieldApplicator (commits c47c777, 717b511)
**Statut : TERMINE**

Extraction de la logique de creation de noise generator et d'application du star field.
- `NoiseGeneratorFactory` : gestion Perlin vs MultiLayer
- `StarFieldApplicator` : application independante du champ d'etoiles
- `GalaxyImageCalculator.create()` : 76 lignes → 15 lignes

### ✅ 2. Strategy Pattern pour eliminer duplication (PR #18 pending)
**Statut : TERMINE** (branch feature/refactor-strategy-pattern-factory)

Remplacement du switch de 70 lignes par delegation a `GalaxyGeneratorFactory`.
- `GalaxyImageCalculator.createIntensityCalculator()` : 70 lignes → 10 lignes
- Suppression du pattern repetitif `param != null ? param : default`
- Tests : `GalaxyGeneratorFactoryTest` avec 6 scenarios

### ✅ 3. Extraction des Magic Numbers (PR #19 pending)
**Statut : TERMINE** (branch feature/extract-magic-numbers)

Creation de 3 classes de constantes metier :
- `NoiseModulationConstants` : base/range pour chaque type de galaxie
- `RadialFalloffConstants` : exposants de falloff et denominateurs Gaussiens
- `CoreIntensityConstants` : luminosite du noyau et poids pour irregular
- 5 generators mis a jour (Spiral, Voronoi, Elliptical, Ring, Irregular)

---

## Violations restantes

### 1. God Object

**GalaxyParameters** : 80+ champs avec 15 methodes factory statiques (lignes 84-343)

### 2. Feature Envy

**GalaxyService:72-87** connait trop de details sur la construction de `GalaxyImageCalculator` et delegue au mapper

---

## Ameliorations par priorite

### Priorite 1 : Decomposer GalaxyParameters

#### 1.1 Creer des Value Objects specialises
```java
@Value class CoreParameters { double coreSize; double galaxyRadius; }
@Value class NoiseTextureParameters { int octaves; double persistence; ... }
@Value class SpiralStructureParameters { int numberOfArms; double armWidth; ... }
```

#### 1.2 Refactorer GalaxyParameters
```java
@Value
@Builder
class GalaxyParameters {
    GalaxyType galaxyType;
    CoreParameters coreParameters;
    NoiseTextureParameters noiseParameters;

    // Type-specific (nullable)
    SpiralStructureParameters spiralParameters;
    VoronoiClusterParameters voronoiParameters;
    ...
}
```

### Priorite 2 : Ameliorer la testabilite

#### 2.1 Injecter les factories dans GalaxyService
```java
@UseCase
@RequiredArgsConstructor
class GalaxyService {
    private final GalaxyImageRepository repository;
    private final GalaxyImageCalculatorFactory calculatorFactory; // NEW
    private final ImageSerializer imageSerializer; // NEW (remplace convertToByteArray)
}
```

#### 2.2 Extraire convertToByteArray
Creer `@Component ImageSerializer` avec methode `byte[] toByteArray(BufferedImage, String format)`

### Priorite 3 : Simplifier GalaxyService.createGalaxyImage()

#### 3.1 Extraire la logique de duplication de nom
Creer `@Component GalaxyImageDuplicationHandler` avec methode `UUID handleDuplication(String name, boolean forceUpdate, repo)`

#### 3.2 Utiliser un Builder pour GalaxyImage
```java
GalaxyImage galaxyImage = GalaxyImage.builder()
    .id(duplicationHandler.resolveId(cmd.getName(), cmd.isForceUpdate()))
    .name(cmd.getName())
    .description(cmd.getDescription())
    .structure(structure)
    .image(imageBytes)
    .build();
```

### Priorite 4 : Renommer pour clarte

| Actuel | Propose | Raison |
|--------|---------|--------|
| `GalaxyImageCalculator` | `GalaxyImageRenderer` | "Calculator" est vague |
| `createIntensityCalculator` | `selectGeneratorForType` | Plus expressif |
| `buildImage` | `renderPixels` | Plus precis |
| `convertToByteArray` | `serializeToPng` | Indique le format |

### Priorite 5 : Ameliorer la validation

Creer `@Component GalaxyParametersValidator` pour valider la coherence des parametres selon le type de galaxie

---

## Ordre d'implementation recommande

1. ~~**NoiseGeneratorFactory + StarFieldApplicator** - TERMINE~~
2. ~~**Strategy Pattern** - TERMINE~~
3. ~~**Magic Numbers** - TERMINE~~
4. **Priorite 2.2** (ImageSerializer) - extraction simple
5. **Priorite 1** (Value Objects pour Parameters) - impact important
6. **Priorite 3** (DuplicationHandler)
7. **Priorite 5** (Validators)

---

## Benefices attendus

| Principe | Avant | Apres |
|----------|-------|-------|
| **SRP** | GalaxyImageCalculator = 7 responsabilites | 1 responsabilite par classe |
| **DRY** | Switch de 70 lignes avec duplication | Factory pattern 10 lignes |
| **Testabilite** | Dependances cachees dans `new` | Injection via constructeur |
| **Lisibilite** | Methodes de 50+ lignes | Methodes < 15 lignes |
| **Maintenabilite** | Ajout d'un type = 10 fichiers | Ajout = 1 strategy + config |
