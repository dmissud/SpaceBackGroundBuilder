# SpaceBackGroundBuilder : Présentation Technique

## Vue d'ensemble

**SpaceBackGroundBuilder** est un générateur procédural d'images de galaxies réalistes, utilisant des algorithmes
mathématiques sophistiqués pour créer des fonds d'écran spatiaux.

## Stack technique

- **Backend** : Java 21, Spring Boot 3.4.13
- **Frontend** : Angular 17, Angular Material, NgRx
- **Librairie principale** : JNoise 4.1.0 (bruit de Perlin/FBM)
- **Architecture** : Hexagonale (ports/adapters)

---

## Modèles mathématiques par type de galaxie

### 1. SPIRAL (Galaxie spirale)

**Algorithme** : Bras spiraux logarithmiques + bruit de Perlin + atténuation radiale

**Formule des bras spiraux** :

```
θ = armRotation × ln(r/coreSize) + (2π × armIndex / numberOfArms)
d = distance_angulaire_au_bras_le_plus_proche
intensity = exp(-d² / (2 × armWidth²))
```

**Paramètres** :

- `numberOfArms` : nombre de bras spiraux
- `armWidth` : épaisseur des bras (Gaussienne)
- `armRotation` : angle d'enroulement logarithmique
- `coreSize` : rayon du noyau central
- `galaxyRadius` : rayon total

**Modulation du bruit** :

```java
intensity =(0.2+0.8 ×perlin_noise) ×geometric_intensity ×radial_falloff
```

**Presets** : CLASSIC, BARRED, MULTI_ARM

---

### 2. VORONOI_CLUSTER (Amas globulaire)

**Algorithme** : Distribution exponentielle de centres de clusters + contributions Gaussiennes

**Distribution des centres** :

```
distance_from_center = exponential_random(1.0 / clusterConcentration)
angle = random(0, 2π)
```

**Intensité par cluster** :

```
d = distance(pixel, cluster_center)
contribution = exp(-d² / (2 × clusterSize²))
intensity = sum(contributions_all_clusters)
```

**Paramètres** :

- `clusterCount` : nombre de centres d'amas (20-200)
- `clusterSize` : rayon Gaussien de chaque amas
- `clusterConcentration` : concentration vers le centre (distribution exponentielle)

**Modulation du bruit** :

```java
intensity =(0.3+0.7 ×perlin_noise) ×cluster_intensity ×radial_falloff
```

**Presets** : VORONOI_DEFAULT, VORONOI_DENSE, VORONOI_SPARSE

---

### 3. ELLIPTICAL (Galaxie elliptique)

**Algorithme** : Profil de Sérsic avec distance elliptique

**Profil de Sérsic** :

```
I(r) = I_e × exp(-b_n × ((r/r_e)^(1/n) - 1))

où :
- n = sersicIndex (0.5-10.0)
- b_n ≈ 2n - 1/3 (approximation de Ciotti)
- r_e = rayon effectif (contenant 50% de la lumière)
```

**Distance elliptique avec rotation** :

```java
// Rotation
x_rot =x ×

cos(θ) +y ×

sin(θ)

y_rot =-x ×

sin(θ) +y ×

cos(θ)

// Distance elliptique
r_elliptical =

sqrt(x_rot² +(y_rot/axisRatio)²)
```

**Paramètres** :

- `sersicIndex` : contrôle la concentration (n=1 → exponentiel, n=4 → de Vaucouleurs, n=0.5 → Gaussien)
- `axisRatio` : ratio axes mineur/majeur (1.0 = rond, 0.1 = très aplati)
- `orientationAngle` : angle de rotation (0-360°)

**Modulation du bruit** (subtile) :

```java
intensity =(0.7+0.3 ×perlin_noise) ×sersic_profile ×radial_falloff
```

**Presets** :

- ELLIPTICAL_DEFAULT (E3, n=4)
- ELLIPTICAL_ROUND (E0, n=4, axisRatio=1.0)
- ELLIPTICAL_FLAT (E6, n=4, axisRatio=0.4)

---

### 4. RING (Galaxie annulaire)

**Algorithme** : Profil Gaussien centré sur un rayon + variations angulaires

**Profil de l'anneau** :

```
d = |radius - ringRadius|
ring_intensity = exp(-d² / (2 × ringWidth²))
```

**Modulation angulaire** :

```java
angular_variation =1.0+0.3 ×

sin(3 ×θ) × (1-eccentricity)
```

**Noyau central** :

```java
if(radius<coreSize){
core_intensity =

exp(-radius² /(2 × coreSize²))
intensity =

max(ring_intensity, core_intensity)
}
```

**Paramètres** :

- `ringRadius` : rayon central de l'anneau
- `ringWidth` : épaisseur Gaussienne
- `ringEccentricity` : asymétrie de l'anneau (0.0-0.5)

**Modulation du bruit** :

```java
intensity =(0.4+0.6 ×perlin_noise) ×ring_intensity ×radial_falloff
```

---

### 5. IRREGULAR (Galaxie irrégulière)

**Algorithme** : Multiples centres asymétriques + domain warping fort

**Distribution des fragments** :

```java
fragments =

generate_random_centers(fragmentCount)

each centered_around = random_gaussian(center, galaxyRadius / 3)
```

**Intensité composite** :

```java
contributions =fragments.

map(f ->

exp(-distance(pixel, f)² /variance) ×random_weight
)
intensity =

weighted_sum(contributions)
```

**Paramètres** :

- `irregularity` : degré de chaos (0.0-1.0)
- `fragmentCount` : nombre de centres de densité (3-15)

**Modulation du bruit** (forte) :

```java
intensity =(0.1+0.9 ×perlin_noise) ×fragment_intensity ×radial_falloff
```

---

## Techniques de rendu avancées

### A. Bruit de Perlin (JNoise 4.1.0)

**Librairie** : `de.articdive:jnoise:4.1.0`

**Utilisation** :

```java
PerlinGenerator perlin = JNoise.newBuilder()
        .perlin(PerlinNoiseGenerator.newBuilder()
                .setSeed(seed)
                .setInterpolation(InterpolationType.LINEAR)
                .build())
        .build();

double noise = perlin.getNoise(x * scale, y * scale);
```

**FBM (Fractional Brownian Motion)** :

```java
PerlinGenerator perlin = JNoise.newBuilder()
        .perlin(...)
        .

octavate(4,0.5,2.0,FractalFunction.FBM, true)
    .

build();
```

- `octaves = 4` : 4 couches de bruit
- `persistence = 0.5` : amplitude divisée par 2 à chaque octave
- `lacunarity = 2.0` : fréquence doublée à chaque octave
- `FractalFunction.FBM` : sommation classique

---

### B. Domain Warping

**Principe** : Déformation de l'espace avant le calcul d'intensité

**Algorithme** :

```java
double offsetX = perlinX.getNoise(x, y) * warpStrength;
double offsetY = perlinY.getNoise(x, y) * warpStrength;

double warpedX = x + offsetX;
double warpedY = y + offsetY;

intensity =

calculate_intensity(warpedX, warpedY);
```

**Paramètre** :

- `warpStrength` : force de la déformation (0.0-300.0 pixels)

**Effet** : Structures filamentaires, asymétrie organique

---

### C. Atténuation radiale (Radial Falloff)

**Formule générale** :

```java
double normalizedRadius = radius / galaxyRadius;
double falloff = 1.0 / (1.0 + Math.pow(normalizedRadius, exponent));
```

**Exposants par type** :

- SPIRAL : `exponent = 4.0` (chute rapide)
- VORONOI_CLUSTER : `exponent = 3.0` (chute modérée)
- ELLIPTICAL : `exponent = 2.0` (chute douce pour profil Sérsic)
- RING : `exponent = 3.0`
- IRREGULAR : `exponent = 2.5`

**Objectif** : Éviter les bords nets, transition naturelle vers le noir

---

### D. Bruit multi-couches (Multi-Layer Noise)

**Principe** : Superposition de 3 échelles de bruit

**Configuration** :

```java
layers:
        -MACRO:scale × 0.3,weight 50%(
grandes structures)
        -MESO:scale × 1.0,weight 35%(
détails moyens)
        -MICRO:scale × 3.0,weight 15%(
texture fine)

combined_noise =

sum(layer_noise × layer_weight)
```

**Paramètre** :

- `enableMultiLayer` : boolean pour activer

**Effet** : Profondeur visuelle, variations à plusieurs échelles

---

### E. Gradient de couleur multi-points

**Classe** : `GradientGalaxyColorCalculator`

**Algorithme** :

```java
ColorStop[] stops = {
        {position:0.0,color:

RGB(0,0,64)},
        {position:0.3,color:

RGB(64,0,128)},
        {position:0.6,color:

RGB(128,0,255)},
        {position:1.0,color:

RGB(255,255,255)}
        };

        for(
i in
stops){
        if(
intensity between
stops[i]
and stops[
i+1]){
t =

smoothstep(intensity, stops[i].pos, stops[i+1].pos)

color =

lerp(stops[i].color, stops[i+1].color, t)
    }
            }
```

**Fonction smoothstep** :

```java
t =(x -edge0)/(edge1 -edge0)
smoothed =t ×t × (3-2 ×t)
```

**Palettes disponibles** :

- NEBULA : violet → magenta → cyan
- CLASSIC : bleu profond → cyan → blanc
- WARM : rouge → orange → jaune
- COLD : bleu foncé → cyan
- INFRARED : rouge → orange → jaune
- EMERALD : vert → cyan

---

### F. Couche d'étoiles (Star Field)

**Algorithme** : Distribution Poisson disk approximative + halo Gaussien

**Distribution spatiale** :

```java
expectedStars =width ×height ×
starDensity
        minDistance = 1.0 / sqrt(starDensity)

// Approche par cellules pour Poisson disk
cellSize =minDistance /

sqrt(2)

reject if distance_to_existing_star<minDistance
```

**Tailles des étoiles** :

- 70% : petites (1-2 px)
- 25% : moyennes (3-4 px)
- 5% : grandes (5-`maxStarSize` px)

**Halo Gaussien** (pour étoiles > 2px) :

```java
for(offset in[-radius,+radius]){
d =

sqrt(dx² +dy²)

intensity =

exp(-d² /(2 × (size/3)²))
        }
```

**Pointes de diffraction** :

```java
for(spike in 0..spikeCount){
angle =2π ×spike /
spikeCount
        length = size × 2.5
intensity_at_distance =1.0-(distance /length)²
```

**Couleurs** :

- 70% : blanc pur (255,255,255)
- 15% : bleu-blanc (200,220,255)
- 15% : jaune-blanc (255,250,220)

**Paramètres** :

- `starDensity` : densité (0.0-0.01, soit 0-1% de pixels)
- `maxStarSize` : taille max (1-10 pixels)
- `diffractionSpikes` : boolean pour pointes
- `spikeCount` : nombre de branches (4-8)

---

## Paramètres communs à tous les types

### Core Parameters

- `coreSize` : rayon du noyau central (pixels)
- `galaxyRadius` : rayon total de la galaxie (pixels)

### Noise Texture Parameters

- `noiseScale` : échelle du bruit de Perlin (0.001-0.1)
- `noiseSeed` : graine aléatoire pour reproductibilité
- `enableMultiLayer` : activation du bruit multi-couches

### Domain Warp Parameters

- `warpStrength` : force de la déformation spatiale (0-300)

### Star Field Parameters

- `starDensity` : densité du champ d'étoiles (0.0-0.01)
- `maxStarSize` : taille maximale des étoiles (1-10)
- `diffractionSpikes` : activation des pointes
- `spikeCount` : nombre de branches (4-8)

---

## Pipeline de rendu

**Étapes** :

1. **Domain Warping** (si `warpStrength > 0`) :
   ```
   (x, y) → (x + offsetX, y + offsetY)
   ```

2. **Calcul d'intensité géométrique** :
   ```
   intensity_geo = generator.calculateIntensity(warpedX, warpedY)
   ```

3. **Modulation par le bruit** :
   ```
   noise = perlin.getNoise(x * scale, y * scale)
   intensity_modulated = (base + range × noise) × intensity_geo
   ```

4. **Atténuation radiale** :
   ```
   intensity_final = intensity_modulated × radial_falloff(radius)
   ```

5. **Normalisation** :
   ```
   intensity_clamped = clamp(intensity_final, 0.0, 1.0)
   ```

6. **Application du gradient de couleur** :
   ```
   RGB color = gradient.getColor(intensity_clamped)
   ```

7. **Superposition du star field** :
   ```
   if (star_at_position) {
       color = blend(color, star_color, star_intensity)
   }
   ```

8. **Écriture pixel** :
   ```
   bufferedImage.setRGB(x, y, color.getRGB())
   ```

---

## Validation des paramètres

**Classe** : `GalaxyParametersValidator`

**Validations communes** :

- `coreSize > 0`
- `galaxyRadius > coreSize`
- `noiseScale ∈ [0.001, 0.1]`
- `warpStrength ∈ [0, 300]`

**Validations spécifiques** :

**SPIRAL** :

- `numberOfArms ∈ [2, 12]`
- `armWidth > 0`
- `armRotation > 0`

**VORONOI_CLUSTER** :

- `clusterCount ∈ [20, 200]`
- `clusterSize > 0`
- `clusterConcentration > 0`

**ELLIPTICAL** :

- `sersicIndex ∈ [0.5, 10.0]`
- `axisRatio ∈ [0.1, 1.0]`
- `orientationAngle ∈ [0, 360]`

**RING** :

- `ringRadius > coreSize`
- `ringRadius < galaxyRadius`
- `ringWidth > 0`
- `ringEccentricity ∈ [0.0, 0.5]`

**IRREGULAR** :

- `irregularity ∈ [0.0, 1.0]`
- `fragmentCount ∈ [3, 15]`

**STAR_FIELD** :

- `starDensity ∈ [0.0, 0.01]`
- `maxStarSize ∈ [1, 10]`
- `spikeCount ∈ [4, 8]`

---

## Points d'intérêt pour analyse

### 1. **Réalisme physique**

- Les modèles (Sérsic, Gaussien, logarithmique) sont-ils adaptés ?
- Manque-t-il des phénomènes astrophysiques (redshift, lentilles gravitationnelles) ?

### 2. **Performances**

- Bruit de Perlin calculé pour chaque pixel → coûteux ?
- Faut-il précomputer des textures de bruit ?
- Domain warping ajoute 2 appels de bruit → impact ?

### 3. **Améliorations visuelles**

- Bloom/Glow du noyau : post-traitement Gaussien sur zones lumineuses ?
- Dark lanes : couche de bruit soustractive pour absorption par la poussière ?
- Lentilles gravitationnelles : déformation de l'espace autour du noyau ?

### 4. **Architecture**

- Le pattern Strategy (via `GalaxyGeneratorFactory`) est-il optimal ?
- Les Value Objects (10 classes de paramètres) apportent-ils de la complexité inutile ?
- La séparation domaine/ports/adapters est-elle bien exploitée ?

### 5. **Librairie JNoise**

- Est-ce le meilleur choix pour le bruit de Perlin/FBM ?
- Alternatives : FastNoise, OpenSimplex2 ?
- Faut-il implémenter un cache de bruit ?

### 6. **Mathématiques**

- La fonction smoothstep suffit-elle pour les gradients (vs. cubic Hermite spline) ?
- Le Poisson disk approximatif est-il suffisant (vs. algorithme de Bridson) ?
- Le profil de Sérsic utilise une approximation de `b_n` → erreur acceptable ?

---

## Questions ouvertes

1. **Batch rendering** : générer plusieurs galaxies en parallèle (threading) ?
2. **Format de sortie** : PNG uniquement ou ajouter TIFF/EXR (HDR) ?
3. **Animation** : keyframe interpolation pour galaxies évolutives ?
4. **GPU acceleration** : migration vers compute shaders (OpenGL/Vulkan) ?
5. **Machine Learning** : entraîner un GAN sur des vraies images Hubble pour affiner les textures ?

---

**Document généré pour analyse externe par un autre moteur d'IA**
