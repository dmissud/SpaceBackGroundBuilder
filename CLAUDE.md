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

- Utiliser le **GitFlow** automatisé (voir `docs/versioning-workflow.md` pour les détails)
- Branches : `master` (stable), `develop` (dev), `feature/*`, `fix/*`
- Versioning piloté par **Labels GitHub** (`major`/`breaking`, `minor`/`enhancement`, `patch`/`bug`)
- Commit format Angular (`feat:`, `fix:`, `chore:`, `test:`, `refactor:`)
- Push sur `master` déclenche une synchronisation automatique vers `develop`

---

# Contexte projet

## Description

SpaceBackGroundBuilder est un generateur d'images de fond d'ecran spatial. Il permet de creer des images de galaxies realistes avec differents algorithmes et parametres configurables.

## Stack technique

- **Backend** : Java 21, Spring Boot 3.4.13, Maven multi-modules
- **Frontend** : Angular 17, Angular Material, NgRx, TypeScript 5.3
- **BDD** : PostgreSQL, Liquibase, JPA/Hibernate
- **Libs** : JNoise 4.1.0, MapStruct 1.6.3, Lombok

## Architecture des modules Maven

- `sbgb-application` : domaine, services, ports (in/out), modeles
- `sbgb-infrastructure` : adapters persistence (JPA), migrations Liquibase
- `sbgb-exposition` : controllers REST, DTOs exposition
- `sbgb-configuration` : Spring Boot app, config
- `sbgb-cmd` : module CLI (images de bruit)
- `sbgb-gui` : frontend Angular

---

# Documentation

Pour toute tâche spécifique, consulte la documentation détaillée dans le répertoire `docs/` :

- **Architecture & Workflow** : `docs/PRESENTATION_TECHNIQUE.md`, `docs/versioning-workflow.md`
- **Guides de déploiement** : `docs/K8S.MD`
- **Roadmap & Décisions** : `docs/ROADMAP.md`
- **Historique du projet** : `docs/HISTORY.md`
- **Spécifications techniques** : `docs/sbgb-cmd-workflow-spec.md`, `docs/sbgb-cmd-workflow-plan.md`
- **Maintenance** : `docs/GALAXY_TROUBLESHOOTING.md`, `docs/TODO.md`
- **Directives IA** : `docs/directive.md`
