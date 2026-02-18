# TODO — Audit Clean Code

## 🔴 Haute priorité

- [ ] **Séparer les entités JPA du domaine** — `GalaxyImage`, `NoiseImage`, `GalaxyStructure`, `ImageStructure`,
  `ImageColor`, `ImageLayer` contiennent des annotations `jakarta.persistence` dans `domain/model`. Créer des entités
  JPA dédiées dans `sbgb-infrastructure` avec des mappers domaine ↔ JPA.
- [ ] **Décomposer `GalaxyStructure`** — God Object avec 30+ champs plats. Utiliser des Value Objects embarqués comme
  dans `GalaxyParameters`.
- [ ] **Ajouter les tests manquants** — `GalaxyService`, `GalaxyStructureMapper`, `GalaxyImageRenderer`,
  `GalaxyResource`, `GalaxyImagePersistenceAdapter` n'ont aucun test unitaire.

## 🟠 Priorité moyenne

- [ ] **Supprimer `PerlinNoiseImabeBuilder` + `NoiseImageBuilder`** — Code mort (classes vides) + typo ("Imabe" → "
  Image").
- [ ] **Renommer `GalaxyGenerator` → `SpiralGalaxyGenerator`** — Incohérent avec `EllipticalGalaxyGenerator`,
  `RingGalaxyGenerator`, etc.
- [ ] **Extraire les presets de `GalaxyParameters`** — 15 méthodes factory statiques (394 lignes). Déplacer dans une
  classe `GalaxyPresets`.
- [ ] **Résoudre les conflits de nommage** — `StarFieldParameters` et `MultiLayerNoiseParameters` existent dans
  `port.in` et `domain.model.parameters`, forçant des FQN dans `GalaxyStructureMapper`.
- [ ] **Remplacer les `null` par Null Object / Optional** — `createWarpCalculatorIfEnabled` retourne `null`, `findById`
  retourne `null`.

## 🟡 Basse priorité

- [ ] **Factoriser les Builders des générateurs** — Pattern Builder dupliqué dans les 5 générateurs (champs
  `width=4000`, `height=4000`, validation identique).
- [ ] **Extraire le code géométrique dupliqué** — Calcul distance/normalisation copié dans 5 générateurs. Créer
  `GalaxyGeometry` ou `AbstractGalaxyGenerator`.
- [ ] **Rendre les entités immutables** — Supprimer `@Setter` sur `GalaxyImage` et `GalaxyStructure`.
- [ ] **`Collectors.toList()` → `.toList()`** — Dans `GalaxyResource.java`.
- [ ] **`findById` : retourner `Optional`** — Dans `GalaxyImagePersistenceAdapter` et le port `GalaxyImageRepository`.
- [ ] **Supprimer les wildcard imports** — `import java.awt.*`, `import org.dbs.sbgb.domain.model.*`, etc.
- [ ] **Logger l'exception dans le mapper de couleur** — `GalaxyStructureMapper.createColorCalculator` avale l'
  `IllegalArgumentException` silencieusement.
- [ ] **`Clump` → `record`** — Dans `IrregularGalaxyGenerator`, convertir la inner class mutable en `record` comme
  `ClusterCenter` dans `VoronoiClusterGalaxyGenerator`.
