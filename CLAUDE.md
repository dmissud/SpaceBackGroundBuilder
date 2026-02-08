# Regles de developpement

## Langage

- Quand tu me parles appel moi Daniel et non pas "L'utilisateur..."

## Demarche

- TDD
- BDD
- DDD
- Clean Code

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

## Phase D : RING
**Statut : A FAIRE**

Galaxie annulaire (type Hoag's Object).
- Parametres envisages : `ringRadius`, `ringWidth`, `ringIntensity`, `coreToRingRatio`
- Algorithme : anneau brillant a distance fixe du centre + noyau central + bruit Perlin + falloff

## Phase E : IRREGULAR
**Statut : A FAIRE**

Galaxie irreguliere sans structure definie (type Nuages de Magellan).
- Parametres envisages : `irregularity`, `clumpCount`, `clumpSize`
- Algorithme : distribution aleatoire de regions denses + bruit Perlin fort + pas de symetrie

## Phase F : LENTICULAR
**Statut : A FAIRE**

Galaxie lenticulaire intermediaire entre spirale et elliptique (type S0).
- Parametres envisages : `diskThickness`, `bulgeRatio`, `dustLaneIntensity`
- Algorithme : bulbe central (Sersic) + disque fin sans bras spiraux + eventuellement bandes de poussiere

## Phase G : INTERACTION
**Statut : A FAIRE**

Galaxies en interaction / collision (type Antennae).
- Parametres envisages : `separationDistance`, `interactionAngle`, `tidalStrength`, `secondGalaxyType`
- Algorithme : deux centres de gravite + queues de maree + deformation des structures + ponts de matiere

---

# Decisions techniques prises

- **FBM Perlin** : le code original utilisait de la reflexion cassee pour appeler `fbm()` sur JNoise. Corrige pour utiliser l'API `octavate()` de JNoise 4.1.0 avec `FractalFunction.FBM` / `FractalFunction.RIDGED_MULTI`
- **Validation nullable** : les champs specifiques a un type (spiral, voronoi, elliptical) sont des wrapper types (Integer, Double) dans `GalaxyRequestCmd` pour que Jakarta Bean Validation les ignore quand null
- **Duplicate name** : mecanisme de confirmation via 409 CONFLICT + `forceUpdate=true` (reutilise le UUID existant)
- **Spring Boot 3.5.0** : ecarte car springdoc-openapi 2.8.15 est incompatible (PatternParseException sur les resource handlers Swagger UI). On reste sur 3.4.13
- **JUnit BOM** : declare avant le Spring Boot BOM dans le pom.xml parent pour que Cucumber 7.34.2 ait acces a JUnit Platform 1.14.1
