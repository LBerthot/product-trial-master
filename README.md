# ProductTrial - Backend

Ce projet est un exercice pour gérer un backend de produits et wishlist. Il contient uniquement le projet backend.

## Contenu du projet

- Backend Spring Boot (back/)
- Requêtes Postman pour tester les endpoints (dans le dossier postman/)
- Utilisation d’une vraie base PostgreSQL via Docker (base en conteneur, données persistantes)

## Lancement du projet avec Docker

Dans ce mode, ni PostgreSQL ni Java/Maven n’ont besoin d’être installés sur la machine cible : Docker se charge de tout via `docker-compose.yml`.

### 0. Préparation du fichier .env

Avant de lancer le projet, copier le fichier `.env.example` situé à la racine en `.env` :

```bash
cp .env.example .env
```

Sous Windows PowerShell :

```powershell
Copy-Item .env.example .env
```

Les valeurs par défaut conviennent pour un usage de test/local.

### 1. Prérequis

- **Windows**
  - Installer **Docker Desktop** depuis le site officiel
  - S’assurer que Docker Desktop est lancé et que le moteur **Linux containers** est actif

- **macOS**
  - Installer **Docker Desktop for Mac**
  - Lancer Docker Desktop avant d’exécuter les commandes

- **Linux**
  - Installer Docker Engine et Docker Compose (ou le plugin `docker compose`)
  - Vérifier que l’utilisateur a les droits suffisants (groupe `docker` si nécessaire)

### 2. Lancer l’environnement complet (API + PostgreSQL en conteneur)

À partir de la racine du projet :

```bash
docker compose up -d --build
```

Cette commande :

- construit l’image du backend à partir de `back/Dockerfile` ;
- télécharge et démarre un conteneur PostgreSQL `producttrial_db` (image `postgres:15`) ;
- crée un volume Docker `producttrial_pgdata` pour persister les données ;
- met les deux conteneurs sur le même réseau Docker ;
- expose l’API backend sur `http://localhost:8080`.

Une fois la commande terminée, l’API est accessible via :

- navigateur : `http://localhost:8080`
- Postman : `GET`/`POST` sur `http://localhost:8080/...` selon les endpoints définis.

### 3. Arrêter les conteneurs

Pour arrêter proprement l’environnement :

```bash
docker compose down
```

- Les conteneurs du backend et de PostgreSQL sont arrêtés et supprimés.
- Le volume `producttrial_pgdata` **n’est pas supprimé**, les données restent donc persistantes pour le prochain démarrage.

### 4. Relancer pour vérifier la persistance des données

Après avoir inséré des données (via Postman par exemple) :

1. Arrêter :

   ```bash
   docker compose down
   ```

2. Relancer :

   ```bash
   docker compose up -d
   ```

3. Re-tester les endpoints avec Postman : les données précédemment insérées doivent être toujours présentes grâce au volume Docker.


## Test avec Postman

Le dossier `postman` possède un json environnement et un json collection pour tester les endpoints.
Les requêtes permettent de tester la plupart des cas d'utilisation du backend.

À savoir : certaines requêtes pourraient devoir être légèrement modifiées s'il y a une persistance des données entre deux lancements de l'application.

## Résumé du mode de lancement

| Mode                  | BDD utilisée             | Instructions principales                               |
|-----------------------|--------------------------|---------------------------------------------------------|
| PostgreSQL via Docker | PostgreSQL en conteneur  | `docker compose up -d --build` à la racine du projet   |
