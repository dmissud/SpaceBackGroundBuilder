# Configuration Kubernetes pour SpaceBackGroundBuilder

Ce répertoire contient les manifests nécessaires pour déployer l'application sur un cluster Kubernetes (comme MicroK8s
sur BREE).

## Structure du répertoire

- `base/` : Contient les manifests de base (Postgres, Backend, Frontend).

## Pré-requis

1. Un cluster Kubernetes (ex: `microk8s`).
2. `kubectl` configuré pour pointer sur votre cluster.

## Déploiement

Pour déployer l'ensemble de l'application :

```bash
kubectl apply -f k8s/base/
```

## Accès à l'application

Le service Frontend est configuré en type `NodePort` sur le port **30080**.
Vous pouvez y accéder via : `http://<IP-DE-BREE>:30080`

## Remarques

- Les images Docker sont récupérées depuis GitHub Container Registry (GHCR).
- Assurez-vous que le cluster a les permissions nécessaires pour tirer les images privées si nécessaire (via un
  `imagePullSecret`).
- La base de données utilise un `PersistentVolumeClaim` pour la persistance des données.
