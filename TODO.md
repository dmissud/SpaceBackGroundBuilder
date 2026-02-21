# Plan de Refactoring Sécurisé & Anti-Régression

Ce plan est conçu pour prévenir toute régression en sanctuarisant le comportement actuel (via des tests de caractérisation) *avant* d'appliquer des modifications structurelles (Clean Architecture & Clean Code).

## 🛡️ Phase 1 : Sécurisation de l'existant (Tests de Caractérisation & Filet de sécurité)

Avant de modifier la structure métier et de casser les liens avec la base de données, nous devons geler les contrats (sorties / image / base).

- [ ] **Tests de Caractérisation Visuelle (`GalaxyImageRenderer`)** : Générer des images de référence (Golden Masters) pour chaque algorithme avec une `seed` fixe. Le test d'approbation comparera les nouveaux rendus avec cette image de référence, pixel par pixel.
- [ ] **Tests Unitaires du Service (`GalaxyService`)** : Mocker les adaptateurs et vérifier strictement l'orchestration de `createGalaxyImage`, `findGalaxyImageById`, etc. (Paramètres de validation ➝ Génération UUID ➝ Persistance ➝ DTO).
- [ ] **Tests d'Intégration Persistance (`GalaxyImagePersistenceAdapter`)** : Écrire des `DataJpaTest` sur les dépôts existants. Objectif : valider la bonne insertion des nombreux champs de `GalaxyStructure` et leur restitution exacte.
- [ ] **Tests d'Intégration Web (`GalaxyResource`)** : Valider via `WebMvcTest` le format JSON des requêtes REST (inputs, outputs, codes d'erreur HTTP 400/404/409).
- [x] **Couverture de tests globale (JaCoCo)** : Les rapports de test de tous les modules sont maintenant agrégés grâce à un module Maven dédié (`sbgb-coverage`). Le rapport unifié, combinant les résultats de l'application, l'infrastructure, et l'exposition, est disponible dans `sbgb-coverage/target/site/jacoco/index.html` après un `mvn clean verify`.

## 📦 Phase 2 : Assainissement du Domaine (Immutabilité & Composants)

Une fois les tests en place, on nettoie le code interne du Modèle de Données actuel, qui est un God Object avec trop de `[Setters]`. Les tests garantiront que le stockage et l'affichage restent corrects.

- [ ] **Décomposition de `GalaxyStructure` (God Object)** : Remplacer les champs plats par des Value Objects sémantiques complets (ex: `SpiralStructure`, `VoronoiCluster`, `StarField`). **Attention :** Garder temporairement les annotations `@Embedded` et `@AttributeOverrides` (ou équivalents) pour que JPA continue de mapper vers la même structure SQL.
- [ ] **Immutabilité des Entités** : Supprimer `@Setter` sur `GalaxyImage` et `GalaxyStructure`. Restreindre et solidifier la création via des `@Builder` (Lombok) protégés et valider la consistance.
- [ ] **Supprimer les retours `null`** : Remplacer par du Pattern Null Object ou de l'`Optional` (`findById`, `createWarpCalculatorIfEnabled`).

## 🧱 Phase 3 : Isolation du Domaine (Clean Architecture)

Le domaine est maintenant propre et testé, on coupe ses liens matériels avec le stockage (Base de Données). Cela va casser temporairement l'application, mais les tests de la *Phase 1* nous certifieront quand cela fonctionnera de nouveau parfaitement.

- [ ] **Création des Entités JPA (`sbgb-infrastructure`)** : Créer les entités miroirs (`GalaxyImageEntity`, `GalaxyStructureEntity`, etc.) annotées avec `@Entity` et `@Table`.
- [ ] **Création des Mappers Infra (MapStruct)** : Écrire les Mappers stricts et bidirectionnels entre les modèles du Domaine (`domain/model`) et les Entités JPA.
- [ ] **Purge du Domaine (Indépendance Technique)** : Supprimer sans exception les dépendances `jakarta.persistence` ou liées à Sring Data des dossiers du domaine métier. Le package redevient des objets Java Purs (POJOs limités à la logique métier).
- [ ] Refactorer l'implémentation de `GalaxyImagePersistenceAdapter` pour utiliser la conversion. Relancer toute la suite de tests pour validation totale.

## 🧹 Phase 4 : Améliorations Évolutives & Code Mort (DRY)

Nettoyage du code redondant entre les différentes implémentations. Validé automatiquement par les tests de Golden Master.

- [ ] **Factorisation des Générateurs de Galaxies (`AbstractGalaxyGenerator` ou `GalaxyGeometry`)** : Mutualiser le Builder dupliqué (validation des largeurs/hauteurs) et le code de géométrie/distances reproduit à l'identique dans les 5 générateurs.
- [ ] **Extraction des Presets de Galaxies (`GalaxyPresets`)** : Séparer (394 lignes) les 15 méthodes factories de presets stockées dans `GalaxyParameters` vers une classe dédiée distincte.
- [ ] **Cohérence et Nommage (FQN et Classes)** : Renommer `GalaxyGenerator` en `SpiralGalaxyGenerator` (symétrie avec les autres types). Supprimer les imports Wildcards (`*`).
- [ ] **Nettoyage du Code Mort ou Typo** : Supprimer purement et simplement `PerlinNoiseImabeBuilder` et `NoiseImageBuilder`. Mettre à jour `Collectors.toList()` vers `.toList()`.
- [ ] **Gestion Propre des Erreurs** : Logger ou propager l'erreur silencieusement ignorée (Exception "avalée" avec un catch vide) dans `GalaxyStructureMapper.createColorCalculator`.
