# Règles de développement

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