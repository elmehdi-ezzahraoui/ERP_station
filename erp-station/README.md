# ERP Station-Service

PFA 5e année DSI, EMSI 2026-2027.
Mini ERP pour station-service, centré sur **le contrôle des pertes** de carburant et de caisse.
Valeur ajoutée : **le paramétrage** des tolérances par le gérant et **les statistiques** des pertes.

## Contenu du dossier

| Dossier | Contenu | Ouvrir avec |
|---|---|---|
| `backend/` | 8 modules Spring Boot : discovery-service, gateway-service, authentification-service, stock-service, achat-service, vente-service, client-service, statistiques-service | IntelliJ IDEA (ouvrir le dossier `backend`) |
| `frontend/` | Application Angular 20 reliée à la Gateway | VS Code ou IntelliJ IDEA (ouvrir le dossier `frontend`) |
| `conceptionUML/` | 15 diagrammes UML (sources Mermaid + images PNG) | n'importe quel lecteur d'images, plugin Mermaid |
| `rapportPFA/` | Rapport du PFA (.odt modifiable + .pdf) | LibreOffice Writer ou Word |

## Démarrage rapide

1. **Base de données** : dans `backend`, lancer `docker compose up -d postgres` (ou créer les 5 bases dans pgAdmin : voir `backend/docker/init-db.sql`). Docker publie PostgreSQL sur `localhost:5433` pour éviter un conflit avec un PostgreSQL local Windows.
2. **Backend** : ouvrir `backend` dans IntelliJ, puis lancer la configuration **« 0 - ERP : tout lancer »**. Vérifier sur http://localhost:8761 que les 7 services sont `UP`.
3. **Frontend** : dans `frontend`, lancer `npm install` puis `npm start`, et ouvrir http://localhost:4200.
4. **Connexion** : `gerant/gerant123`, `pompiste1/pompiste123`, `comptable/comptable123` ou `admin/admin123`.

Détails dans `backend/README.md` et `frontend/README.md`.

## Prérequis

- Java 17 ou plus récent
- Node.js 20 ou plus récent (pour Angular)
- PostgreSQL 16 ou Docker Desktop
