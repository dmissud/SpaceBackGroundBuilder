# TODO - SpaceBackGroundBuilder

## 1. Amelioration du rendu visuel des galaxies

Analyse basee sur une image de reference nebuleuse realiste. Les ameliorations sont classees par priorite d'impact
visuel.

### 1.1 Domain Warping (priorite 1 - impact majeur) ✅ TERMINE

**Statut** : Implemente dans commit `aa7cf4f`

**Implementation** :
- Ajout parametre `warpStrength` (double, default 0.0) dans `GalaxyParameters`
- Creation `DomainWarpCalculator` avec deux `PerlinGenerator` independants (X et Y, seeds offsettees)
- Integration dans `GalaxyImageCalculator.buildImage()` : warping des coordonnees avant calcul d'intensite
- Tests complets : zero strength, warping actif, reproductibilite, scaling avec strength

**Resultat** : Structures filamentaires et organiques au lieu de geometriques. Applicable a tous les types de galaxie.

| Parametre      | Type   | Default | Min | Max   | Description                          |
|----------------|--------|---------|-----|-------|--------------------------------------|
| `warpStrength` | Double | 0.0     | 0.0 | 300.0 | Intensite de la deformation spatiale |

### 1.2 Gradient de couleur multi-points (priorite 2 - impact majeur) ✅ TERMINE

**Statut** : Implemente dans commit `c07af98`

**Implementation** :
- Creation `ColorStop` (value object : position 0.0-1.0 + Color)
- Creation `GradientGalaxyColorCalculator` implementant `GalaxyColorCalculator`
  - Interpolation lisse entre N color stops avec smoothstep
  - Tri automatique des stops par position
  - Clamping des intensites hors [0,1]
- Creation enum `ColorPalette` avec 6 palettes pre-definies :
  - **NEBULA** : violet/magenta/cyan/turquoise (8 stops) - emission nebulae style
  - **CLASSIC** : bleu/blanc (5 stops) - galaxie traditionnelle
  - **WARM** : rouge/orange/jaune (7 stops) - vieilles galaxies rouges
  - **COLD** : bleu/cyan (7 stops) - jeunes galaxies bleues
  - **INFRARED** : rouge/jaune (7 stops) - imagerie infrarouge
  - **EMERALD** : vert/cyan (7 stops) - apparence exotique/alien
- Tests complets : interpolation, clamping, palettes, sorting

**Resultat** : Transitions de couleur riches et realistes au lieu du scheme monotone 4 couleurs. Chaque palette cree une ambiance visuelle differente.

**Usage** : `ColorPalette.NEBULA.createCalculator()` ou `new GradientGalaxyColorCalculator(customStops)`

### 1.3 Couche d'etoiles (priorite 3 - impact visuel immediat)

**Probleme** : Aucune etoile individuelle, l'image parait vide entre les structures gazeuses.
**Solution** : Nouveau `StarFieldGenerator` independant, compose sur l'image finale.

Caracteristiques :

- Distribution aleatoire (Poisson disk pour espacement naturel)
- Tailles variables (majorite petites 1-2px, quelques grosses 3-6px)
- Halo Gaussien autour des etoiles brillantes
- Pointes de diffraction optionnelles (croix a 4 ou 6 branches)
- Couleur des etoiles legerement variable (blanc, bleu pale, jaune pale)

| Parametre           | Type    | Default | Min | Max  | Description                        |
|---------------------|---------|---------|-----|------|------------------------------------|
| `starDensity`       | Double  | 0.001   | 0.0 | 0.01 | Densite d'etoiles (ratio pixels)   |
| `maxStarSize`       | Integer | 4       | 1   | 10   | Taille max en pixels               |
| `diffractionSpikes` | Boolean | false   | -   | -    | Activer les pointes de diffraction |
| `spikeCount`        | Integer | 4       | 4   | 8    | Nombre de pointes                  |

- Couche independante, compositable sur n'importe quel type de galaxie
- Appliquee en post-traitement apres le calcul de couleur

### 1.4 Multi-couches de noise (priorite 4 - profondeur)

**Probleme** : 1 seule couche Perlin avec modulation simple (`0.3 + 0.7 * noise`). Les structures sont uniformes, pas de
variation d'echelle.
**Solution** : Empiler 2-3 couches a des echelles differentes.

| Couche | Scale | Poids | Role                      |
|--------|-------|-------|---------------------------|
| Macro  | x0.3  | 0.50  | Grandes structures de gaz |
| Meso   | x1.0  | 0.35  | Filaments, details moyens |
| Micro  | x3.0  | 0.15  | Grain, poussiere          |

```
combined = macro * 0.5 + meso * 0.35 + micro * 0.15
```

- Evolution du `PerlinGenerator` existant (ajout d'un mode multi-layer)
- Ou creation d'un `MultiLayerNoiseGenerator` wrapper
- Chaque couche a sa propre seed pour eviter la correlation

### 1.5 Bloom / Glow du noyau (priorite 5 - polish)

**Probleme** : Transition nette entre le core et les bras. Dans la realite, les zones tres lumineuses "debordent"
optiquement.
**Solution** : Post-traitement Gaussien sur les zones a haute intensite.

Algorithme :

1. Generer l'image normalement
2. Extraire un masque des pixels > seuil de bloom (`bloomThreshold`)
3. Appliquer un flou Gaussien sur ce masque
4. Compositer en mode additif sur l'image finale

| Parametre        | Type    | Default | Min | Max | Description                            |
|------------------|---------|---------|-----|-----|----------------------------------------|
| `bloomRadius`    | Integer | 20      | 0   | 100 | Rayon du flou Gaussien                 |
| `bloomIntensity` | Double  | 0.3     | 0.0 | 1.0 | Opacite du bloom                       |
| `bloomThreshold` | Double  | 0.7     | 0.1 | 1.0 | Seuil d'intensite declenchant le bloom |

- Opere sur le `BufferedImage` final, independant du type de galaxie
- Peut utiliser `java.awt.image.ConvolveOp` ou kernel Gaussien manuel

### 1.6 Dark lanes / Absorption par la poussiere (priorite 6 - realisme)

**Probleme** : Pas de zones d'ombre entre les structures. Dans la realite, la poussiere interstellaire absorbe la
lumiere et cree des silhouettes.
**Solution** : Couche de bruit soustractive.

```java
double dustNoise = dustPerlin.evaluate(x, y);
if(dustNoise >dustThreshold){
intensity *=(1.0-dustOpacity *(dustNoise -dustThreshold)/(1.0-dustThreshold));
        }
```

| Parametre       | Type   | Default | Min | Max | Description                        |
|-----------------|--------|---------|-----|-----|------------------------------------|
| `dustThreshold` | Double | 0.6     | 0.0 | 1.0 | Seuil d'apparition de la poussiere |
| `dustOpacity`   | Double | 0.5     | 0.0 | 1.0 | Opacite max de la poussiere        |

- Perlin dedie avec scale basse (grandes bandes de poussiere)
- Appliquee avant le calcul de couleur, apres l'intensite geometrique

### Ordre d'implementation recommande

Les ameliorations 1.1 et 1.2 a elles seules transforment radicalement le rendu :

1. **1.1 Domain Warping** - 1 parametre, applicable a tous les types, transformation maximale
2. **1.2 Gradient multi-couleurs** - Nouvelle implementation de `GalaxyColorCalculator`
3. **1.3 Star field** - Couche independante, compositable
4. **1.4 Multi-noise** - Evolution du `PerlinGenerator`
5. **1.5 Bloom** - Post-traitement sur `BufferedImage`
6. **1.6 Dark lanes** - Couche soustractive

### Comparatif actuel vs cible

| Aspect     | Actuel                             | Cible                                      |
|------------|------------------------------------|--------------------------------------------|
| Couleurs   | 4 zones fixes (bleu/blanc)         | Gradient N points, palettes multiples      |
| Structures | Geometriques, lisses               | Filamentaires, organiques (domain warping) |
| Noise      | 1 couche Perlin, modulation simple | Multi-couches (macro/meso/micro)           |
| Etoiles    | Aucune                             | Champ d'etoiles avec diffraction           |
| Profondeur | Surface 2D plate                   | Multi-couches superposees                  |
| Noyau      | Transition nette                   | Bloom/glow lumineux                        |
| Ombres     | Aucune                             | Dark lanes, poussiere absorbante           |
| Symetrie   | Radiale stricte                    | Organique, asymetrique                     |

---

## 2. Nouveaux types de galaxie (roadmap)

Types restants a implementer (meme pattern que SPIRAL/VORONOI/ELLIPTICAL) :

### 2.1 RING - Galaxie annulaire

Anneau brillant de formation d'etoiles autour d'un noyau :

- Profil Gaussien centre sur `ringRadius`
- Modulation angulaire pour des variations d'epaisseur
- Params : `ringRadius`, `ringWidth`, `ringEccentricity`
- Reference : Hoag's Object, Cartwheel Galaxy

### 2.2 IRREGULAR - Galaxie irreguliere

Forme amorphe sans symetrie, typique des galaxies naines :

- Multiples centres de densite (comme Voronoi mais asymetrique)
- Domain warping fort pour briser toute symetrie
- Frontiere fractale (pas de rayon net)
- Params : `irregularity`, `fragmentCount`
- Reference : Grand/Petit Nuage de Magellan

### 2.3 LENTICULAR - Galaxie lenticulaire (S0)

Intermediaire entre spirale et elliptique, disque sans bras :

- Profil de Sersic (n ~ 2-3) avec disque aplati
- Pas de bras spiraux mais un disque de poussiere
- Bulbe central prooeminent
- Params : `bulgeRatio`, `diskThickness`, `dustLaneIntensity`
- Reference : NGC 2787, Spindle Galaxy

### 2.4 INTERACTION - Galaxie en interaction

Deux galaxies deformees par interaction gravitationnelle :

- Deux centres de masse avec parametres orbitaux
- Queues de maree (tidal tails) en forme de pont
- Deformation asymetrique des deux galaxies
- Params : `separation`, `massRatio`, `tidalStrength`, `interactionAngle`
- Reference : Antennae Galaxies, Mice Galaxies
