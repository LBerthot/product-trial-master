# ProductTrial - Backend

Ce projet est un exercice pour gérer un backend de produits et wishlist. Il contient uniquement le projet backend.

## Contenu du projet

- Backend Spring Boot (back/)
- Requêtes Postman pour tester les endpoints (dans le dossier postman/)
- Deux branches principales :
  - main : simulation complète avec H2 
  - bdd-docker : utilisation d’une vraie base PostgreSQL (docker non implémenté)

## Lancement du projet

### 1. Utiliser la simulation avec H2 (branche main)
Cette branche ne nécessite aucune configuration de base de données. Idéal pour tester rapidement le projet.

``` powershell à partir de la racine du projet
cd .\back\
./mvnw spring-boot:run
```

- L’application démarre avec une BDD en mémoire H2. 
- Les données sont temporaires et seront perdues à l’arrêt de l’application. 
- Toutes les fonctionnalités peuvent être testées via Postman en important les json présent dans le dossier postman/.


### 2. Utiliser une vraie base PostgreSQL (branche bdd-docker)

Cette branche est connectée à PostgreSQL. Elle nécessite un utilisateur et une base PostgreSQL existants.

Étapes :
- Créer la base (si nécessaire)
``` dans dbeaver par exemple
CREATE DATABASE producttrial;
```
- Créer l’utilisateur (si nécessaire)
``` dans dbeaver par exemple
CREATE USER producttrial_user WITH PASSWORD 'NouveauMotDePasseTest';
GRANT ALL PRIVILEGES ON DATABASE producttrial TO producttrial_user;
```
- Lancer l’application sur powerShell (ou le terminal de IntelliJ)
``` powershell
cd .\back\
$Env:DB_USER="producttrial_user"
$Env:DB_PASSWORD="NouveauMotDePasseTest"
./mvnw spring-boot:run
```

L'application se connectera sur la bdd et les données seront persistantes.


## Test avec Postman

Le dossier postman possède un json environnement et un json collection pour tester les endpoints.
Les requetes permettent de tester la plupart des cas d'utilisation du backend.

À savoir de certains requetes pourraient de devoir être légèrement modifié s'il y a une persistance des données entre deux lancements de l'application.

## Résumé des branches
| Branche         | BDD utilisée | Instructions principales                                                     |
|-----------------| ------------ | ---------------------------------------------------------------------------- |
| main            | H2 (simulé)  | `./mvnw spring-boot:run`                                                     |
| bdd-docker      | PostgreSQL   | Créer DB et user si nécessaire, puis `$Env:DB_USER=… ./mvnw spring-boot:run` |
