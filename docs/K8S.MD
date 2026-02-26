### Guide de Déploiement Kubernetes

Ce document décrit comment déployer l'application SpaceBackgroundBuilder sur un cluster Kubernetes. Le déploiement est conçu pour être modulaire et configurable.

#### 1. Architecture du Déploiement

Le déploiement se compose généralement de deux parties :
- **Infrastructure** : Héberge les services mutualisés comme la base de données PostgreSQL.
- **Application SBGB** : Héberge les composants spécifiques de l'application (Frontend, Backend, Ingress).

Composants principaux :
- **PostgreSQL** : Base de données relationnelle pour la persistance des données.
- **Backend Spring Boot** : API REST gérant la logique métier et la génération d'images.
- **Frontend Nginx** : Application Angular servie par Nginx, incluant un proxy vers le backend.
- **Ingress** : Gère le routage du trafic externe (HTTPS) vers les services.

---

#### 2. Déploiement avec Helm (Recommandé)

Helm permet un déploiement en une seule étape et facilite la gestion des configurations.

**Pré-requis :**
- Helm installé.
- Accès à votre cluster Kubernetes.

**Installation :**
```bash
# Aller dans le répertoire du chart
cd k8s/helm/sbgb

# Installer ou mettre à jour la release
helm upgrade --install sbgb . \
  --namespace sbgb \
  --create-namespace \
  --set ingress.host="votre-domaine.com"
```

**Principales options de configuration (`values.yaml`) :**
| Paramètre | Description | Défaut |
|-----------|-------------|---------|
| `postgresql.enabled` | Déployer une instance Postgres avec le chart | `true` |
| `backend.image` | Image Docker du backend | `ghcr.io/dmissud/spacebackgroundbuilder/backend:latest` |
| `frontend.image` | Image Docker du frontend | `ghcr.io/dmissud/spacebackgroundbuilder/frontend:latest` |
| `ingress.host` | Nom d'hôte pour l'accès externe | `sbgb.example.com` |
| `ingress.basePath` | Préfixe d'URL pour l'application | `/sbgb` |

---

#### 3. Déploiement manuel avec Kubectl

Si vous ne souhaitez pas utiliser Helm, vous pouvez appliquer les manifestes YAML individuellement.

**Ordre de déploiement :**

```bash
# 1. Créer les namespaces
kubectl create namespace infra
kubectl create namespace sbgb

# 2. Déployer l'infrastructure (PostgreSQL)
kubectl apply -f k8s/infra/

# 3. Déployer l'application
kubectl apply -f k8s/sbgb/base/
```

---

#### 4. Commandes de Maintenance

**Vérification de l'état :**
```bash
# Voir les pods
kubectl get pods -n sbgb

# Voir les logs du backend
kubectl logs -n sbgb -l app=sbgb-backend --tail=100 -f
```

**Mise à jour des images :**
```bash
# Forcer le redémarrage pour récupérer la version 'latest'
kubectl rollout restart deployment sbgb-backend -n sbgb
kubectl rollout restart deployment sbgb-frontend -n sbgb
```

---

#### 5. Exemple : Configuration pour le Cluster `bree`

Le cluster `bree` (MicroK8s) utilise la configuration spécifique suivante :

- **Host** : `bree`
- **Namespaces** : `infra` (DB), `sbgb` (App)
- **Accès Application** : `https://bree/sbgb/`
- **Swagger UI** : `https://bree/sbgb/api/swagger-ui.html`
- **Ingress Class** : `public`
- **Certificat** : Auto-signé (Secret `sbgb-tls-secret`)

Pour déployer sur `bree` via Helm :
```bash
helm upgrade --install sbgb k8s/helm/sbgb \
  --namespace sbgb \
  --set ingress.host="bree" \
  --set ingress.className="public"
```

---

#### 7. Intégration avec IntelliJ IDEA (Alternative GUI)

Si vous utilisez IntelliJ IDEA Ultimate (ou avec les plugins **Kubernetes** et **Helm** installés), vous pouvez gérer le déploiement sans ligne de commande.

**A. Déploiement via Helm (GUI) :**
1. Faites un clic droit sur le dossier `k8s/helm/sbgb` ou sur le fichier `Chart.yaml`.
2. Sélectionnez **Helm** > **Install / Upgrade**.
3. Dans la boîte de dialogue, vous pouvez :
   - Choisir le **Namespace** (`sbgb`).
   - Cocher **Create Namespace** si nécessaire.
   - Ajouter des arguments `--set` (ex: `ingress.host=votre-domaine.com`) dans le champ **Arguments**.
4. Cliquez sur **OK**.

**B. Déploiement Kubectl (GUI) :**
1. Faites un clic droit sur le dossier `k8s/infra/` ou `k8s/sbgb/base/`.
2. Sélectionnez **Kubernetes** > **Apply to Cluster**.

**C. Surveillance (Tool Window "Services") :**
1. Ouvrez l'onglet **Services** (en bas d'IntelliJ).
2. Sous **Kubernetes**, connectez votre cluster.
3. Vous pouvez naviguer dans les **Namespaces** (`sbgb`, `infra`), voir les **Pods**, consulter les **Logs** en temps réel et même ouvrir un terminal dans un conteneur.

---

#### 9. Déploiement Multi-environnements (Bree & Pi8)

Le projet est configuré pour se déployer automatiquement sur plusieurs clusters (ex: `bree` et `pi8`) via une matrice dans GitHub Actions.

**A. Configuration des Runners :**
Chaque runner doit posséder les labels suivants pour être ciblé :
- **Bree** : `self-hosted`, `sbgb`, `bree`
- **Pi8** : `self-hosted`, `sbgb`, `pi8`

**B. Ajout d'un nouveau Runner (ex: Raspberry Pi) :**
1. Sur le Pi, téléchargez le runner ARM64.
2. Configurez-le : `./config.sh --url https://github.com/dmissud/SpaceBackGroundBuilder --labels self-hosted,sbgb,pi8`.
3. Installez-le en tant que service : `sudo ./svc.sh install && sudo ./svc.sh start`.

**C. Architecture Multi-Arch :**
Le workflow de build est configuré pour générer des images Docker compatibles `amd64` (Bree) et `arm64` (Raspberry Pi).

---

#### 10. Historique des Résolutions

##### A. Redirections Infinies (`ERR_TOO_MANY_REDIRECTS`)
- **Cause** : Conflit entre Ingress (HTTPS) et Nginx (redirection HTTP).
- **Solution** : `absolute_redirect off;` dans Nginx et chemins relatifs.
- **Statut** : ✅ Résolu.

##### B. Ressources Statiques (404)
- **Cause** : Le frontend cherchait `/` au lieu de `/sbgb/`.
- **Solution** : `<base href="/sbgb/">` et configuration du chemin dans le pod.
- **Statut** : ✅ Résolu.

##### C. Échec de Génération (405 Method Not Allowed)
- **Problème** : `POST /sbgb/api/galaxies/build` renvoie 405.
- **Diagnostic** : Problème de routage ou de version d'image.
- **Statut** : ⚠️ En cours d'investigation.
