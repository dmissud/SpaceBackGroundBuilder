# Règles de développement

## Langage

- Quand tu me parles appel moi Daniel et non pas "L'utilisateur..."

## Démarche

- TDD
- BDD
- DDD
- Clean Code

## Style de code

- Utiliser des noms de variables en anglais
- Préférer les records aux classes pour les DTOs
- Toujours utiliser Lombok (@RequiredArgsConstructor, @Builder)

## Architecture

- Respecter l'architecture hexagonale (ports/adapters)
- Les services sont dans domain/service
- Les ports in/out sont des interfaces

## Tests

- Écrire des tests Cucumber pour les use cases
- Utiliser AssertJ pour les assertions

## Git
- Utiliser le gitflow
- Commence toujours par une nouvelle branche feature
- Ajoute les fichiers modifiés ou crée au repo git
- Commit avec un message clair et descriptif
- Push régulièrement sur la branche de travail
- Utiliser des commits avec le format angular
