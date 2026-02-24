# ☸️ Déploiement Kubernetes pour SpaceBackGroundBuilder

Ce répertoire contient tout le nécessaire pour déployer l'application sur un cluster Kubernetes.

## ⚠️ Guide de référence
**Le guide de déploiement complet et détaillé est disponible dans le fichier [K8S.MD](../K8S.MD) à la racine du projet.**

---

## 🏗️ Structure du répertoire

- `helm/sbgb/` : **(Recommandé)** Chart Helm pour une installation complète en une étape.
- `infra/` : Manifestes pour l'infrastructure mutualisée (PostgreSQL).
- `sbgb/base/` : Manifestes Kubernetes standards pour le Backend, le Frontend et l'Ingress.

---

## 🚀 Méthodes de Déploiement

### 1. Via Helm (Plus simple)
Idéal pour un déploiement rapide avec une configuration centralisée.

```bash
# Aller dans le répertoire du chart
cd k8s/helm/sbgb

# Installer ou mettre à jour la release
helm upgrade --install sbgb . \
  --namespace sbgb \
  --create-namespace \
  --set ingress.host="votre-domaine.com"
```

### 2. Via Kubectl (Manuel)
Pour ceux qui préfèrent appliquer les manifestes séparément.

```bash
# 1. Déployer l'infrastructure (Database PostgreSQL)
kubectl apply -f k8s/infra/

# 2. Déployer l'application SBGB (Backend, Frontend, Ingress)
kubectl apply -f k8s/sbgb/base/
```

---

## 🛠️ Accès et Maintenance

- **Images Docker** : Récupérées automatiquement depuis le GitHub Container Registry (GHCR).
- **Ingress** : L'accès externe se fait via un contrôleur Ingress. Par défaut, l'application est configurée pour utiliser un Ingress.
- **Maintenance** : Consultez [K8S.MD](../K8S.MD) pour les commandes de vérification de logs, de redémarrage des déploiements et de résolution de problèmes connus (comme les redirections infinies).

---

## 🏗️ Architecture des Namespaces

Par défaut, le déploiement est organisé ainsi :
- `infra` : Namespace pour la base de données PostgreSQL mutualisée.
- `sbgb` : Namespace pour les composants de l'application SpaceBackGroundBuilder.
