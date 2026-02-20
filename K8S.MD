### Résumé de la Configuration Déployée sur le Cluster `bree`

Le déploiement a été structuré pour isoler les composants tout en mutualisant les ressources lourdes (Base de données).

#### 1. Architecture des Namespaces

- **Namespace `infra`** : Héberge le serveur PostgreSQL mutualisé (`postgres-shared`).
- **Namespace `sbgb`** : Héberge l'application SpaceBackgroundBuilder (Frontend, Backend, Ingress).

#### 2. Composants Déployés

- **PostgreSQL (`infra`)** :
    - Service : `postgres-shared.infra.svc.cluster.local:5432`
    - Volume : Persistant (5Go).
- **Backend Spring Boot (`sbgb`)** :
    - Service : `sbgb-backend:8080`
    - Image : `ghcr.io/dmissud/spacebackgroundbuilder/backend:latest`
    - Connecté à la base `sbgb` dans `infra`.
- **Frontend Nginx (`sbgb`)** :
    - Service : `sbgb-frontend:80`
    - Image : `ghcr.io/dmissud/spacebackgroundbuilder/frontend:latest`
    - Configuré pour servir l'application sur le préfixe `/sbgb/`.
- **Ingress (`sbgb`)** :
    - Gère le trafic HTTPS sur `https://bree/`.
    - Routage : `/sbgb` -> Frontend, `/sbgb/api` -> Backend.
    - Certificat : Auto-signé (Secret `sbgb-tls-secret`).

#### 3. Points d'Accès

- **Application** : `https://bree/sbgb/`
- **Documentation API (Swagger)** : `https://bree/sbgb/api/swagger-ui.html`

#### 4. Emplacement des fichiers de configuration Kubernetes

Les fichiers YAML utilisés pour le déploiement sont organisés comme suit :

- **Infrastructure commune** : `k8s/infra/postgres-shared.yaml` (PostgreSQL).
- **Application SBGB** :
    - `k8s/sbgb/base/backend.yaml` (Deployment & Service API).
    - `k8s/sbgb/base/frontend.yaml` (Deployment, Service & ConfigMap Nginx).
    - `k8s/sbgb/base/ingress.yaml` (Règles de routage HTTPS et Ingress).

#### 5. Commandes de déploiement et maintenance

Voici les commandes principales pour gérer votre cluster `bree` :

**Déploiement initial / Mise à jour :**

```bash
# Appliquer l'infrastructure (PostgreSQL)
kubectl apply -f k8s/infra/postgres-shared.yaml

# Appliquer l'application (dans l'ordre)
kubectl apply -f k8s/sbgb/base/backend.yaml
kubectl apply -f k8s/sbgb/base/frontend.yaml
kubectl apply -f k8s/sbgb/base/ingress.yaml
```

**Vérification de l'état :**

```bash
# Voir tous les pods de l'application
kubectl get pods -n sbgb

# Voir les logs du backend (pour déboguer la génération)
kubectl logs -n sbgb -l app=sbgb-backend --tail=100 -f

# Voir les logs du frontend
kubectl logs -n sbgb -l app=sbgb-frontend --tail=50
```

**Redémarrage forcé (pour forcer le pull de la dernière image `latest`) :**

```bash
kubectl rollout restart deployment sbgb-backend -n sbgb
kubectl rollout restart deployment sbgb-frontend -n sbgb
```

---

### Erreurs Rencontrées et État de Résolution

#### A. Erreur de Redirections Infinies (`ERR_TOO_MANY_REDIRECTS`)

- **Cause** : Conflit entre l'Ingress (force HTTPS) et Nginx (redirection absolue vers HTTP).
- **Solution** : Ajout de `absolute_redirect off;` dans la configuration Nginx et passage en chemins relatifs.
- **Statut** : ✅ **Résolu**.

#### B. Ressources Statiques (JS/CSS) non trouvées (404)

- **Cause** : Le frontend cherchait ses fichiers à la racine `/` au lieu de `/sbgb/`.
- **Solution** : Correction du `<base href="/sbgb/">` et création d'un dossier physique `/sbgb` dans le pod frontend via
  un script `postStart`.
- **Statut** : ✅ **Résolu**.

#### C. Échec de Génération de Galaxie (`405 Method Not Allowed`)

- **Problème actuel** : Lorsqu'on tente de générer une galaxie (`POST /sbgb/api/galaxies/build`), le serveur renvoie une
  erreur 405.
- **Diagnostic technique** :
    - Le backend reçoit la requête mais prétend que seul `GET` est autorisé sur cet endpoint.
    - Étrangement, le `POST` fonctionne pour d'autres ressources (ex: `/images/build`).
    - L'Ingress a été mis à jour avec `rewrite-target` pour s'assurer que le backend reçoit bien `/galaxies/build` et
      non `/sbgb/api/galaxies/build`.
- **Hypothèse principale** : Incohérence entre l'image Docker `latest` déployée et le code source local, ou conflit de
  routage interne à Spring Boot spécifique à l'objet `Galaxy`.
- **Statut** : ⚠️ **En cours d'investigation**.
