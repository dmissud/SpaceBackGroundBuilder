# Taches en cours - SpaceBackGroundBuilder

## Branches terminees

### 1. `feature/elliptical-galaxy-generator` -> mergee dans `develop`

Phase B - Galaxie Elliptique avec profil de Sersic :
- `EllipticalGalaxyGenerator` avec profil de Sersic, distance elliptique, falloff radial
- 5 tests unitaires
- Enum `ELLIPTICAL`, routing, 3 presets (E3 Classic, E0 Round, E6 Flat)
- Params `sersicIndex`, `axisRatio`, `orientationAngle` dans tout le stack (Cmd -> Service -> Structure -> DTO)
- Migration Liquibase (3 colonnes)
- UI Angular complete (type selector, presets, section conditionnelle)
- Fix du FBM Perlin (remplacement reflexion cassee par API `octavate()` de JNoise 4.1.0)

## Branches en attente de merge

### 2. `chore/upgrade-dependencies` -> a merger dans `develop`

Upgrade de toutes les dependances backend :

| Library | Avant | Apres |
|---|---|---|
| Spring Boot | 3.2.4 | 3.4.13 |
| Mockito | 5.10.0 | 5.21.0 |
| Cucumber | 7.15.0 | 7.34.2 |
| JUnit | 5.10.2 | 5.14.1 |
| AssertJ | 3.25.3 | 3.27.6 |
| Maven Surefire | 3.2.5 | 3.5.4 |
| Logback | 1.4.14 | 1.5.27 |
| MapStruct | 1.5.5.Final | 1.6.3 |
| PostgreSQL | 42.7.3 | 42.7.9 |
| Springdoc | 2.5.0 | 2.8.15 |

Note : Spring Boot 3.5.0 ecarte (incompatible avec springdoc 2.8.15 - PatternParseException)

## A faire

### 3. Upgrade frontend Angular 17 -> 21

Migration majeure en plusieurs etapes (17->18->19->20->21) avec `ng update`.

| Package | Actuel | Cible |
|---------|--------|-------|
| @angular/* | 17.2.0 | 21.1.3 |
| @angular/material | 17.2.1 | 21.1.3 |
| @ngrx/* | 17.1.1 | 21.0.1 |
| rxjs | 7.8.0 | 7.8.2 |
| zone.js | 0.14.3 | 0.16.0 |
| typescript | 5.3.2 | 5.9.x |
| jasmine-core | 5.1.0 | 6.0.1 |
| express | 4.18.2 | 5.2.1 |
