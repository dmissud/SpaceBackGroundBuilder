# SpaceBackGroundBuilder

Générateur d'images de galaxies réalistes avec différents algorithmes et paramètres configurables.

## 🌌 Fonctionnalités

- **6 types de galaxies** : Spiral, Voronoi Cluster, Elliptical, Ring, Irregular, **Density Wave** (rendu WebGL temps réel)
- **Amélirations visuelles** : Domain Warping, Multi-layer Noise, Gradients de couleurs, Champ d'étoiles
- **Architecture hexagonale** : Clean Code, SOLID, DDD
- **Stack moderne** : Spring Boot 3.4, Angular 17, PostgreSQL 16

## 📋 Prérequis

- **Docker** >= 20.10
- **Docker Compose** >= 2.0
- **4 GB RAM** minimum (8 GB recommandé)
- **Ports libres** : 80 (frontend), 8080 (backend), 5432 (PostgreSQL)

## 🚀 Démarrage rapide

### 1. Cloner le projet

```bash
git clone https://github.com/dmissud/SpaceBackGroundBuilder.git
cd SpaceBackGroundBuilder
```

### 2. Lancer l'application

```bash
docker-compose up -d
```

Cette commande va :
- Construire les images Docker (première fois : ~10-15 minutes)
- Démarrer PostgreSQL, le backend Spring Boot et le frontend Angular avec Nginx
- Exécuter les migrations Liquibase
- Exposer l'application sur http://localhost

### 3. Accéder à l'application

- **Frontend** : http://localhost
- **Backend API** : http://localhost/api
- **Swagger UI** : http://localhost/api/swagger-ui.html
- **Health check** : http://localhost/api/actuator/health

### 4. Arrêter l'application

```bash
docker-compose down
```

Pour supprimer également les données de la base :
```bash
docker-compose down -v
```

## 🐳 Utilisation des images pré-construites (Sans build local)

Si vous souhaitez tester l'application rapidement sans avoir à compiler le code source ou construire les images localement, vous pouvez utiliser le fichier `docker-compose.prod.yml`.

Ce fichier présente deux avantages majeurs :
1. **Gain de temps** : Les images sont directement téléchargées depuis le **GitHub Container Registry (GHCR)**.
2. **Environnement de production** : Vous testez exactement les mêmes images que celles qui sont validées par la CI/CD.

### Lancer l'application avec les images publiées

```bash
# Pour être sûr d'utiliser les bonnes images (ex: dmissud)
export GITHUB_REPOSITORY_LOWER=dmissud/spacebackgroundbuilder

# Lancer l'application
docker-compose -f docker-compose.prod.yml up -d
```

### Pourquoi utiliser ce fichier ?
* **Rapidité** : Évite l'étape de build locale (10-15 minutes).
* **Fiabilité** : Utilise les images `latest` construites automatiquement lors des push sur la branche `master`.
* **Simplicité** : Idéal pour une démonstration ou pour faire tester le projet à quelqu'un d'autre sans configuration locale complexe.

## 📦 Architecture Docker

```
┌─────────────────────────────────────────────────────────────┐
│                        Docker Compose                        │
├─────────────────┬─────────────────┬─────────────────────────┤
│   Frontend      │    Backend      │      Database           │
│   (Nginx)       │  (Spring Boot)  │    (PostgreSQL)         │
│   Port 80       │   Port 8080     │     Port 5432           │
│                 │                 │                         │
│  Angular 17 SPA │  Java 21 + API  │  Data persistence       │
│  Proxy /api/ -> │  REST + Swagger │  Liquibase migrations   │
│    backend:8080 │  JPA/Hibernate  │  Volume: postgres_data  │
└─────────────────┴─────────────────┴─────────────────────────┘
```

### Services

#### Frontend (sbgb-frontend)
- **Image** : Nginx 1.25 Alpine
- **Build** : Multi-stage (Node 20 → Nginx)
- **Configuration** : `nginx.conf` avec proxy API, compression gzip, cache intelligent
- **Health check** : http://localhost/health

#### Backend (sbgb-backend)
- **Image** : Eclipse Temurin 21 JRE Alpine
- **Build** : Multi-stage (Maven 3.9 → JRE 21)
- **Configuration** : Variables d'environnement Spring Boot
- **Health check** : http://localhost:8080/actuator/health

#### Database (sbgb-postgres)
- **Image** : PostgreSQL 16 Alpine
- **Volumes** : Persistance des données via `postgres_data`
- **Credentials** : `sbgb_user` / `sbgb_password` (à changer en production)

## ☸️ Déploiement Kubernetes

Le projet est prêt à être déployé sur un cluster Kubernetes (testé avec MicroK8s).

- **Manifestes** : Les fichiers YAML et le **Chart Helm** se trouvent dans le dossier `k8s/`.
- **Guide complet** : Consultez le fichier [**K8S.MD**](K8S.MD) pour les instructions détaillées de déploiement (Helm ou kubectl), l'architecture des namespaces et les commandes de maintenance.
- **CI/CD** : Le déploiement est automatisé via GitHub Actions lors des push sur les branches principales. Le projet supporte le déploiement simultané sur plusieurs clusters (ex: `bree` en amd64 et `pi8` sur Raspberry Pi ARM64).

## 🛠️ Développement

### Prérequis supplémentaires

- **JDK 21**
- **Maven 3.9+**
- **Node.js 20+**
- **PostgreSQL 16** (ou utiliser le container Docker)

### Backend (Spring Boot)

```bash
# Compiler
mvn clean install -DskipTests

# Lancer le backend (avec PostgreSQL local ou Docker)
cd sbgb-configuration
mvn spring-boot:run

# Lancer les tests
mvn test
```

Configuration locale : `sbgb-configuration/src/main/resources/application.properties`

### Frontend (Angular)

```bash
cd sbgb-gui

# Installer les dépendances
npm install

# Lancer le dev server
npm start
# Accès : http://localhost:4200

# Build production
npm run build

# Tests
npm test
```

Le frontend utilise un proxy configuré dans `proxy.conf.json` pour rediriger `/api` vers le backend.

## 🎨 Utilisation

### Générer une galaxie

1. Accéder à http://localhost
2. Sélectionner un type de galaxie (Spiral, Voronoi, Elliptical, Ring, Irregular)
3. Choisir un preset ou personnaliser les paramètres :
   - Structure (bras spiraux, clusters, profil de Sersic...)
   - Texture (octaves, persistence, lacunarité, scale)
   - Améliorations visuelles (warping, multi-layer noise, étoiles)
   - Couleurs (palettes prédéfinies ou personnalisées)
4. Cliquer sur "Generate Preview" pour visualiser
5. Cliquer sur "Save Galaxy" pour enregistrer en base de données
6. Cliquer sur "Télécharger" pour sauvegarder l'image en local

### Types de galaxies

- **Spiral** : Galaxies spirales classiques (M31, M51)
- **Voronoi Cluster** : Amas globulaires / galaxies irrégulières clusterisées
- **Elliptical** : Galaxies elliptiques avec profil de Sersic (E0-E7)
- **Ring** : Galaxies annulaires (Hoag's Object, Cartwheel)
- **Irregular** : Galaxies irrégulières (Nuages de Magellan)

### Améliorations visuelles

- **Domain Warping** : Déformation spatiale créant des structures filamentaires organiques
- **Multi-Layer Noise** : 3 couches de bruit (macro/meso/micro) pour textures riches
- **Color Palettes** : 6 palettes (Nebula, Classic, Warm, Cold, Infrared, Emerald)
- **Star Field** : Champ d'étoiles avec pointes de diffraction optionnelles

## 🗂️ Structure du projet

```
SpaceBackGroundBuilder/
├── docker-compose.yml          # Orchestration Docker
├── Dockerfile.backend          # Image backend Spring Boot
├── Dockerfile.frontend         # Image frontend Angular + Nginx
├── nginx.conf                  # Configuration Nginx
├── .dockerignore               # Fichiers exclus des builds Docker
│
├── sbgb-application/           # Couche domaine
│   ├── domain/model/           # Modèles du domaine
│   ├── domain/service/         # Services métier
│   └── port/in|out/            # Ports hexagonaux
│
├── sbgb-infrastructure/        # Adaptateurs de persistence
│   ├── adapter/                # Repositories JPA
│   └── db/changelog/           # Migrations Liquibase
│
├── sbgb-exposition/            # Adaptateurs REST
│   └── resources/              # Controllers REST + DTOs
│
├── sbgb-configuration/         # Configuration Spring Boot
│   └── config/                 # Beans, CORS, Swagger
│
├── sbgb-cmd/                   # Module CLI (images de bruit)
│
└── sbgb-gui/                   # Frontend Angular
    ├── src/app/galaxy/         # Module galaxy
    └── src/app/sbgbs/          # Module liste galaxies
```

## 🧪 Tests

### Backend

```bash
# Tous les tests
mvn test

# Tests d'un module spécifique
mvn test -pl sbgb-application

# Tests Cucumber
mvn test -Dtest=RunCucumberTest
```

### Frontend

```bash
cd sbgb-gui
npm test                # Tests unitaires
npm run test:coverage   # Couverture de code
```

## 📊 Monitoring et logs

### Visualiser les logs

```bash
# Tous les services
docker-compose logs -f

# Service spécifique
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f db
```

### Health checks

```bash
# Frontend
curl http://localhost/health

# Backend
curl http://localhost/api/actuator/health

# PostgreSQL (depuis le host)
docker-compose exec db pg_isready -U sbgb_user -d sbgb
```

### Métriques Spring Boot Actuator

- http://localhost/api/actuator/health
- http://localhost/api/actuator/info
- http://localhost/api/actuator/metrics

## 🔧 Configuration

### Variables d'environnement

Modifier les variables dans `docker-compose.yml` :

```yaml
backend:
  environment:
    # Database
    SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/sbgb
    SPRING_DATASOURCE_USERNAME: sbgb_user
    SPRING_DATASOURCE_PASSWORD: sbgb_password

    # JVM
    JAVA_OPTS: "-Xms512m -Xmx2048m"

    # Spring profiles
    SPRING_PROFILES_ACTIVE: prod
```

### Sécurité (Production)

⚠️ **Important** : Avant de déployer en production :

1. Changer les credentials PostgreSQL
2. Activer HTTPS (Let's Encrypt + Nginx)
3. Configurer CORS restrictif
4. Activer Spring Security
5. Utiliser des secrets Docker/Kubernetes
6. Configurer le pare-feu
7. Activer les backups automatiques

## 🐛 Dépannage

### Le frontend ne se connecte pas au backend

Vérifier que le proxy Nginx est correctement configuré :
```bash
docker-compose exec frontend cat /etc/nginx/nginx.conf | grep proxy_pass
```

### Erreur de connexion PostgreSQL

```bash
# Vérifier que PostgreSQL est démarré
docker-compose ps db

# Vérifier les logs
docker-compose logs db

# Tester la connexion
docker-compose exec db psql -U sbgb_user -d sbgb -c "SELECT 1"
```

### Rebuild complet

```bash
# Arrêter et supprimer tout
docker-compose down -v

# Rebuild sans cache
docker-compose build --no-cache

# Redémarrer
docker-compose up -d
```

### Ports déjà utilisés

Si les ports 80, 8080 ou 5432 sont occupés, modifier dans `docker-compose.yml` :
```yaml
ports:
  - "8081:80"    # Frontend sur port 8081
  - "8082:8080"  # Backend sur port 8082
  - "5433:5432"  # PostgreSQL sur port 5433
```

## 📝 Licence

Ce projet est distribué sous licence **BSD 2-Clause**. Voir le fichier [LICENSE](LICENSE) pour les détails.

## 🙏 Références et crédits

Le type de galaxie **Density Wave** est un portage fidèle des travaux d'**Ingo Berg** (beltoforion), distribués sous licence BSD 2-Clause.

### Article de référence

- 🇬🇧 [Simulating a Galaxy with the Density Wave Theory](https://beltoforion.de/en/spiral_galaxy_renderer) — beltoforion.de
- 🇩🇪 [Rendern von Spiralgalaxien](https://beltoforion.de/de/rendern_von_spiralgalaxien) — beltoforion.de

### Projets sources

| Projet | Langage | Lien |
|---|---|---|
| Galaxy-Renderer | C++ / OpenGL | [github.com/beltoforion/Galaxy-Renderer](https://github.com/beltoforion/Galaxy-Renderer) |
| Galaxy-Renderer-Typescript | TypeScript / WebGL2 | [github.com/beltoforion/Galaxy-Renderer-Typescript](https://github.com/beltoforion/Galaxy-Renderer-Typescript) |

L'algorithme implémenté couvre : distribution radiale par CDF (méthode de Simpson), mécanique orbitale en 3 zones d'eccentricité, 4 types de particules (STAR, DUST, FILAMENT, H2), et rendu GPU via vertex/fragment shaders WebGL.

## 👤 Auteur

**Daniel Missud**
- GitHub: [@dmissud](https://github.com/dmissud)
- GitLab: [@dan.missud-group](https://gitlab.com/dan.missud-group)

## 🤝 Contribution

Ce projet suit les principes TDD, BDD, DDD et Clean Code.
Le workflow de versioning est automatisé (GitFlow + Labels GitHub). Pour plus de détails, consultez le document [*
*WORKFLOW.md**](WORKFLOW.md).

Pour contribuer :
1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/amazing-feature`)
3. Commit avec format Angular (`git commit -m 'feat: add amazing feature'`)
4. Push vers la branche (`git push origin feature/amazing-feature`)
5. Ouvrir une Pull Request

## 📚 Documentation technique

Voir `CLAUDE.md` pour :
- Architecture détaillée
- Règles de développement
- Roadmap des types de galaxies
- Améliorations visuelles
- Décisions techniques

---

🤖 Generated with [Claude Code](https://claude.com/claude-code)
