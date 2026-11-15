# ERP Station-Service

> Conception et développement d'un ERP modulaire pour station-service basé sur une architecture Microservices.

Projet de Fin d'Année (PFA) – 5e année DSI, EMSI, 2026-2027.

Mini ERP pour station-service, centré sur **le contrôle des pertes** de carburant et de caisse.

**Valeur ajoutée :**
- **Le paramétrage** : le gérant règle lui-même les tolérances d'écart.
- **Les statistiques** : chiffrage des pertes par cuve et par pompiste.

---

## Problématique

Des logiciels marocains de gestion de station existent déjà (Smart Station, Mastation, Station Maroc). Ils couvrent la gestion courante et annoncent des contrôles de caisse et de stock. En revanche, leurs présentations publiques ne montrent pas de croisement automatique des trois sources (cuves, pompes, caisse) avec des tolérances réglables par le gérant, ni de chiffrage des pertes par cuve et par pompiste. Ce projet vise à couvrir ce besoin.

## Fonctionnalités

- Gestion des stocks de carburant (cuves) et des approvisionnements
- Ventes et gestion de caisse
- Suivi des clients professionnels, crédits et facturation
- Détection automatique des écarts entre cuves, pompes et caisse
- Tolérances paramétrables par le gérant
- Tableau de bord et statistiques des pertes (par cuve, par pompiste)
- Authentification et gestion des rôles (gérant, pompiste, comptable, admin)

## Architecture

Architecture **microservices** : chaque service est autonome, enregistré dans un service de découverte et exposé via une passerelle unique.

| Module | Rôle |
|---|---|
| `discovery-service` | Service de découverte (Eureka) |
| `gateway-service` | Passerelle d'entrée unique (API Gateway) |
| `authentification-service` | Authentification et gestion des utilisateurs |
| `stock-service` | Stock de carburant et de produits |
| `achat-service` | Approvisionnements et achats |
| `vente-service` | Ventes et caisse |
| `client-service` | Clients, crédits et facturation |
| `statistiques-service` | Écarts, pertes et pilotage |

## Technologies

| Couche | Technologie |
|---|---|
| Backend | Java 17+, Spring Boot |
| Frontend | Angular 20 |
| Base de données | PostgreSQL 16 |
| Conteneurisation | Docker / Docker Compose |
| Modélisation | UML (Mermaid) |

## Structure du dépôt

| Dossier | Contenu | Ouvrir avec |
|---|---|---|
| `backend/` | 8 modules Spring Boot | IntelliJ IDEA (ouvrir le dossier `backend`) |
| `frontend/` | Application Angular 20 reliée à la Gateway | VS Code ou IntelliJ IDEA (ouvrir le dossier `frontend`) |
| `conceptionUML/` | diagrammes UML ( images PNG) | Lecteur d'images |
| `rapportPFA/` | Rapport du PFA (.odt modifiable + .pdf) | Word |

## Prérequis

- Java 17 ou plus récent
- Node.js 20 ou plus récent (pour Angular)
- PostgreSQL 16 ou Docker Desktop

## Démarrage rapide

### 1. Base de données

Dans `backend`, lancer :

```bash
docker compose up -d postgres
```

Docker publie PostgreSQL sur `localhost:5433` pour éviter un conflit avec un PostgreSQL local Windows.

Sans Docker : créer les 5 bases dans pgAdmin (voir `backend/docker/init-db.sql`).

### 2. Backend

1. Ouvrir le dossier `backend` dans IntelliJ IDEA.
2. Lancer la configuration **« 0 - ERP : tout lancer »**.
3. Vérifier sur http://localhost:8761 que les 7 services sont `UP`.

### 3. Frontend

```bash
cd frontend
npm install
npm start
```

Ouvrir http://localhost:4200.

### 4. Connexion

| Rôle | Identifiant | Mot de passe |
|---|---|---|
| Gérant | `gerant` | `gerant123` |
| Pompiste | `pompiste1` | `pompiste123` |
| Comptable | `comptable` | `comptable123` |
| Administrateur | `admin` | `admin123` |

> Comptes de démonstration, à changer avant tout déploiement réel.

## Accès rapides

| Ressource | URL |
|---|---|
| Application Angular | http://localhost:4200 |
| Tableau Eureka | http://localhost:8761 |
| PostgreSQL (Docker) | `localhost:5433` |

## Documentation

- Détails backend : `backend/README.md`
- Détails frontend : `frontend/README.md`
- Diagrammes UML : `conceptionUML/`
- Rapport complet : `rapportPFA/`

## Équipe

PFA réalisé à l'EMSI, 2026-2027.
Noms des membres de l'équipe :
- EZ-ZAHRAOUI El-Mehdi
- BOUICHNAD Hanae
