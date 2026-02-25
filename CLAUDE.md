# Regles de developpement

## Langage

- Quand tu me parles appel moi Daniel et non pas "L'utilisateur..."

## Demarche

### TDD STRICT (Test-Driven Development)

**OBLIGATOIRE : Respecter le cycle Red-Green-Refactor**

1. **RED** : Écrire le test AVANT le code de production
   - Le test doit échouer (red)
   - Ne jamais écrire de code de production sans test qui échoue d'abord

2. **GREEN** : Écrire le code minimal pour faire passer le test
   - Pas de sur-ingénierie
   - Le code peut être "sale" à ce stade

3. **REFACTOR** : Nettoyer le code en appliquant Clean Code
   - Éliminer la duplication (DRY)
   - Extraire les méthodes/classes (SRP)
   - Nommer clairement (Clean Code)
   - Appliquer SOLID, KISS, YAGNI
   - Les tests doivent rester verts pendant le refactoring

**Règles TDD strictes** :
- JAMAIS de code de production sans test qui échoue d'abord
- Commit après chaque cycle Red-Green-Refactor
- Les tests sont du code de production : ils doivent être propres et maintenables
- Privilégier les tests unitaires (rapides, isolés)
- Tests d'intégration pour les ports (in/out)

### Clean Code & Refactoring SYSTÉMATIQUE

**À appliquer PENDANT la phase REFACTOR du TDD** :

1. **Nommage explicite**
   - Variables : intention claire (pas de `temp`, `data`, `x`)
   - Méthodes : verbes d'action (pas de noms vagues)
   - Classes : noms de concept métier

2. **Fonctions courtes**
   - Max 15-20 lignes par méthode
   - Une seule responsabilité (SRP)
   - Pas plus de 3 paramètres

3. **Pas de duplication** (DRY)
   - Extraire en méthode/classe dès que répétition

4. **Pas de commentaires** (sauf Javadoc public API)
   - Le code doit s'auto-documenter
   - Si besoin de commentaire → refactorer le code

5. **SOLID systématique**
   - SRP : une classe = une responsabilité
   - OCP : ouvert à l'extension, fermé à la modification
   - LSP : substitution de Liskov
   - ISP : interfaces ségrégées
   - DIP : dépendre des abstractions

### Processus de développement

**Pour chaque nouvelle fonctionnalité** :

1. Écrire le test Cucumber (BDD) pour le use case
2. TDD strict pour implémenter le domaine :
   - Test unitaire → Code minimal → Refactor Clean Code
   - Répéter jusqu'à complétion
3. TDD pour les ports in/out
4. Implémenter le frontend avec tests
5. Refactoring global si nécessaire
6. Commit avec message descriptif

Implémente le domaine en premier et les ports in/out ensuite.
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

- Utiliser le **GitFlow** automatisé (voir `WORKFLOW.md` pour les détails et commandes Git)
- Branches : `master` (stable), `develop` (dev), `feature/*`, `fix/*`
- Versioning piloté par **Labels GitHub** (`major`, `minor`, `patch`)
- Commit format Angular (`feat:`, `fix:`, `chore:`, `test:`, `refactor:`)
- Push sur `master` déclenche une synchronisation automatique vers `develop`
- Utiliser `[skip ci]` pour les commits de montée de version automatisés

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

### ✅ 4. ImageSerializer extraction (PR #20 pending)
**Statut : TERMINE** (branch feature/extract-image-serializer)

Extraction de la serialisation d'images en composant dedie :
- `@Component ImageSerializer` avec `toByteArray(BufferedImage, String format)`
- `GalaxyService` injecte et utilise `ImageSerializer`
- Tests : `ImageSerializerTest` avec 6 scenarios
- Suppression de `convertToByteArray()` de GalaxyService

### ✅ 5. Decomposition GalaxyParameters en Value Objects (PR #21 pending)
**Statut : TERMINE** (branch feature/decompose-galaxy-parameters, commits 1e8f2c7, 6cbf706, 8f8c608)

Refactoring complet de GalaxyParameters en 5 phases :
- **Phase 1** : Creation de 10 Value Objects (CoreParameters, NoiseTextureParameters, SpiralStructureParameters, VoronoiClusterParameters, EllipticalShapeParameters, RingStructureParameters, IrregularStructureParameters, DomainWarpParameters, StarFieldParameters, MultiLayerNoiseParameters)
- **Phase 2** : Migration de 14 factory methods pour utiliser les builders Value Objects
- **Phase 3** : Migration de 5 generators + 5 strategies + 4 fichiers de tests
- **Phase 4** : Suppression de 63 champs legacy de GalaxyParameters (602 → 393 lignes, -35%)
- **Phase 5** : Suppression de 20+ methodes de compatibilite, mise a jour de tous les usages

Benefices obtenus :
- **SRP** : Chaque Value Object a une responsabilite unique
- **Encapsulation** : Parametres lies groupes ensemble
- **Simplification** : Strategy classes de 40 → 15 lignes (-62%)
- **Type Safety** : Acces via Value Objects au lieu de champs individuels
- **Maintenabilite** : Code plus clair, pas de duplication de logique fallback
- 82 tests passent, refactoring 100% termine

### ✅ 6. Simplification GalaxyService + Renaming + Validation (PR #22 pending)
**Statut : TERMINE** (branch feature/simplify-galaxy-service, commits 2d36dd9, a22baf2)

**Simplification GalaxyService.createGalaxyImage()** :
- Creation de `@Component GalaxyImageDuplicationHandler` avec `resolveId()` et `resolveNote()`
- Ajout de `GalaxyImageBuilder` inner class dans `GalaxyImage` (compatible JPA)
- `createGalaxyImage()` reduit de 27 → 18 lignes (-33%)

**Renommage pour clarte** :
- `GalaxyImageCalculator` → `GalaxyImageRenderer` (terme plus precis)
- `createIntensityCalculator()` → `selectGeneratorForType()` (plus expressif)
- `buildImage()` → `renderPixels()` (indique l'operation reelle)

**Validation** :
- Creation de `@Component GalaxyParametersValidator`
- Validation des parametres communs (core, noise texture)
- Validation type-specific pour les 5 types de galaxies
- Throws `IllegalArgumentException` avec messages detailles

Tous les 82 tests passent.

---

## Benefices obtenus

| Principe | Avant | Apres |
|----------|-------|-------|
| **SRP** | GalaxyImageCalculator = 7 responsabilites | ✅ 1 responsabilite par classe |
| **DRY** | Switch de 70 lignes avec duplication | ✅ Factory pattern 10 lignes |
| **Encapsulation** | 80+ champs plats dans GalaxyParameters | ✅ 10 Value Objects cohesifs |
| **Testabilite** | Dependances cachees dans `new` | ✅ Injection via constructeur |
| **Lisibilite** | Methodes de 50+ lignes, strategy 40 lignes | ✅ Methodes < 15 lignes, strategy 15 lignes |
| **Maintenabilite** | Ajout d'un type = 10 fichiers | ✅ Ajout = 1 strategy + config |
| **Nommage** | Termes vagues (Calculator, build) | ✅ Noms expressifs (Renderer, renderPixels) |
| **Validation** | Pas de validation metier | ✅ Validator component avec regles metier |

---

## Plan d'amelioration Clean Code : TERMINE ✅

Tous les refactorings Clean Code planifies ont ete realises avec succes :
- ✅ 6 refactorings majeurs completes
- ✅ 82 tests passent en continu
- ✅ Architecture hexagonale respectee
- ✅ Principes SOLID appliques
- ✅ Code maintenable et extensible
