# ERP Station-Service : backend (microservices)

PFA 5e année DSI, EMSI 2026-2027.
Mini ERP web pour les stations-service au Maroc, centré sur **le contrôle des pertes** : à chaque fin de poste, l'application croise les index des compteurs, le stock réel des cuves et l'argent encaissé, signale les écarts qui dépassent les tolérances fixées par le gérant, et chiffre les pertes en litres et en dirhams.

Valeur ajoutée : **le paramétrage** (tolérances de coulage par carburant, écart de caisse toléré, contrôle de continuité des index) et **les statistiques** (pertes par cuve, bilan par pompiste, alertes).

Fonctions de base : cuves, livraisons, ventes et caisse des pompistes, clients professionnels à crédit, facturation, tableau de bord.

Backend : Java 17, Spring Boot 3.3, Spring Cloud 2023.0 (Eureka, Gateway, OpenFeign), Spring Data JPA, PostgreSQL, JWT, Docker.
Frontend : application Angular dans le dossier `../frontend` (la Gateway autorise `http://localhost:4200` en CORS).

## 1. Architecture

```
                            Frontend Angular (:4200)        
                                          |
                                          v
                 +-----------------------------------------------+
                 |  gateway-service :8888                        |      discovery-service :8761
                 |  vérifie le JWT et le rôle, route les appels  | <--> (Eureka : tous les services
                 +-----------------------------------------------+       s'y enregistrent)
                    |        |         |          |         |         |
                    v        v         v          v         v         v
     authentification   stock   achat  vente   client   statistiques
          :8081         :8082        :8083         :8084   :8085      :8086
        [db_auth]    [db_stock] [db_achat] [db_vente] [db_client] (pas de BD)

   OpenFeign :  achat --> stock   (augmenter le stock à la livraison)
                vente             --> stock   (prix du carburant + diminuer le stock)
                vente             --> client  (plafond de crédit, ventes CREDIT seulement)
                statistiques      --> vente, stock, achat, client (lecture)
```

| Module | Port | Base | Rôle |
|---|---|---|---|
| `discovery-service` | 8761 | - | Annuaire Eureka |
| `gateway-service` | 8888 | - | Point d'entrée unique : vérifie le JWT, contrôle les rôles, CORS |
| `authentification-service` | 8081 | `db_auth` | Utilisateurs et rôles, login, création du JWT |
| `stock-service` | 8082 | `db_stock` | Carburants (prix), cuves, seuils d'alerte, jaugeages |
| `achat-service` | 8083 | `db_achat` | Fournisseurs, réception des livraisons, prix d'achat moyen |
| `vente-service` | 8084 | `db_vente` | Pompes, pistolets, postes des pompistes, ventes, clôture de caisse |
| `client-service` | 8085 | `db_client` | Clients pro, plafonds de crédit, factures, règlements, paramètres financiers (TVA) |
| `statistiques-service` | 8086 | - | Pilotage : tableau de bord, CA journalier, marges |

### Organisation du code dans chaque microservice

```
ma.emsi.station.<service>
 |- entities/      classes JPA = tables de la base
 |- enums/         valeurs fixes (Role, ModePaiement, StatutPoste, StatutFacture)
 |- repositories/  accès à la base avec Spring Data JPA
 |- dtos/          objets échangés avec le frontend (records Java)
 |- mappers/       conversion entité -> DTO
 |- services/      logique métier (calculs, règles, appels aux autres services)
 |- controllers/   API REST : reçoit les requêtes HTTP et appelle les services
 |- feign/         clients OpenFeign pour appeler les autres microservices
 |- exceptions/    BusinessException + GlobalExceptionHandler
 |- config/        configuration et données de démo
```

Chemin d'une requête : `controller -> service -> repository -> base`, puis le `mapper` transforme l'entité en DTO pour la réponse.

### Contrôle des pertes : comment ça marche

1. **Ouverture du poste** : le pompiste saisit l'index de chaque pistolet qu'il utilise. Si un index ne correspond pas à l'index de fin du poste précédent, il est marqué `indexDiscontinu` (oubli, erreur ou fraude).
2. **Pendant le poste** : seules les ventes par carte, chèque ou crédit sont saisies une par une.
3. **Clôture** : le pompiste saisit les index de fin et l'argent compté.
   - volume vendu = index fin - index début, valorisé au prix de vente ;
   - le volume non saisi devient une vente en espèces et sort du stock ;
   - espèces attendues = montant des compteurs - carte - chèque - crédit ;
   - écart = argent compté - (fond de caisse + espèces attendues), comparé à l'écart toléré.
4. **Jaugeage** : le gérant mesure la cuve. L'écart avec le stock théorique est comparé à la tolérance de coulage du carburant.
5. **Statistiques** : pertes de carburant (litres et DH au prix d'achat moyen), manquants de caisse, bilan par pompiste, alertes sur le tableau de bord.

### Paramétrage et pilotage

| Paramètre | Service | Endpoint | Modifié par |
|---|---|---|---|
| Écart de caisse toléré, contrôle de continuité des index | vente-service | `/api/parametres/controle` | Gérant |
| Tolérance de coulage par carburant, prix de vente | stock-service | `/api/carburants/{id}` | Gérant |
| Seuil d'alerte de chaque cuve | stock-service | `/api/cuves/{id}` | Gérant |
| TVA, seuil d'alerte des encours clients | client-service | `/api/parametres/financiers` | Comptable |
| Plafond de crédit de chaque client | client-service | `/api/clients/{id}` | Comptable |
| Comptes et rôles | authentification-service | `/api/utilisateurs` | Admin |

Chaque paramètre est stocké dans le service qui l'utilise (une base par service). Le **pilotage** est assuré par `statistiques-service`.

### Sécurité

1. Le frontend appelle `POST /api/auth/login`. `authentification-service` vérifie le mot de passe (BCrypt) et renvoie un JWT.
2. Le frontend envoie ce JWT dans chaque requête : `Authorization: Bearer <token>`.
3. `gateway-service` vérifie le JWT avec la même clé secrète et contrôle le rôle (`AccessRules.java`), puis transmet `X-User` et `X-Role` au service.

| Rôle | Accès principal |
|---|---|
| `POMPISTE` | ouvrir/clôturer son poste, saisir des ventes, consulter pompes, cuves, clients |
| `GERANT` | stock, jaugeages, fournisseurs, livraisons, paramétrage de la piste, ventes, statistiques |
| `COMPTABLE` | clients, factures, règlements, paramètres financiers, statistiques |
| `ADMIN` | tout, y compris la gestion des utilisateurs (`/api/utilisateurs`) |

Les endpoints internes (`/api/cuves/{id}/sortie`, `/api/cuves/{id}/entree`, `POST /api/clients/{id}/consommations`) sont bloqués par la Gateway : seuls les services peuvent les appeler.

Comptes de démo (créés au premier démarrage) : `admin/admin123`, `gerant/gerant123`, `pompiste1/pompiste123`, `comptable/comptable123`.

### Isolation des pannes

- Une vente en espèces, carte ou chèque n'appelle jamais `client-service` : si la facturation tombe, la piste continue de vendre.
- Le tableau de bord reste affiché si un service est arrêté, et liste les services indisponibles dans `servicesIndisponibles`.

## 2. Ouvrir dans IntelliJ IDEA

1. **File > Open** et choisir le dossier `backend` (celui qui contient le `pom.xml` parent). Cliquer sur **Trust project**.
2. **File > Project Structure > SDK** : Java 17 ou plus récent.
3. Attendre la fin de l'import Maven (barre de progression en bas à droite).

## 3. Lancer le projet

**PostgreSQL** :
- Avec Docker : `docker compose up -d postgres` (les 5 bases sont créées par `docker/init-db.sql`). PostgreSQL est publié sur `localhost:5433` pour éviter un conflit avec un PostgreSQL déjà installé sur Windows ; les services lancés dans Docker utilisent toujours le port interne `5432`.
- Sans Docker : créer `db_auth`, `db_stock`, `db_achat`, `db_vente`, `db_client` dans pgAdmin. Si le mot de passe n'est pas `postgres`, le changer dans les `application.yml` ou définir la variable `DB_PASSWORD`.

**Microservices** : en haut à droite d'IntelliJ, choisir **`0 - ERP : tout lancer`** puis Run. Pour un seul service, toujours démarrer `discovery-service` en premier.

**Vérifications** :
- Eureka : http://localhost:8761 (les 7 services en `UP` après ~30 s)
- Swagger de chaque service : `http://localhost:<port>/swagger-ui.html` (ports 8081 à 8086)

**Scénario de démo** : ouvrir `http/scenario-demo.http` et exécuter les requêtes dans l'ordre (IntelliJ Ultimate). Il montre un poste complet avec un index discontinu, un écart de caisse de -48 DH, une anomalie de coulage, puis les statistiques des pertes. Les résultats attendus sont en commentaire. Avec IntelliJ Community, utiliser Postman avec les mêmes URL.

**Tout en Docker** :
```bash
mvn clean package -DskipTests
docker compose up --build
```

## 4. Données de démonstration

- Carburants : Gasoil 11,20 DH/L, Essence Sans Plomb 13,10 DH/L (prix indicatifs)
- Cuves : `CUVE-GO-01` (30 000 L, 18 000 L), `CUVE-SP-01` (20 000 L, 4 500 L, déjà proche du seuil)
- 1 fournisseur et 2 livraisons passées (pour le calcul des marges)
- 2 pompes avec 2 pistolets chacune (pistolet 1 = gasoil, pistolet 2 = sans plomb)
- Clients : Transport Atlas SARL (plafond 20 000 DH), Taxi Express Mohammedia (plafond 1 000 DH)
- Paramètres financiers : TVA 10 %, alerte encours à 80 % du plafond
- Contrôle : écart de caisse toléré 20 DH ; tolérance de coulage 0,3 % (gasoil) et 0,5 % (sans plomb). Valeurs indicatives, à ajuster par le gérant.
- Index de départ des pistolets : 10000, 20000, 30000, 40000

## 5. Principaux endpoints (via la Gateway :8888)

| Méthode | URL | Service | Description |
|---|---|---|---|
| POST | `/api/auth/login` | authentification | Obtenir un JWT (public) |
| GET | `/api/auth/moi` | authentification | Profil de l'utilisateur connecté |
| GET/POST/PUT | `/api/utilisateurs` | authentification | Gestion des comptes (ADMIN) |
| GET | `/api/cuves`, `/api/cuves/alertes` | stock | Niveaux des cuves, alertes |
| POST | `/api/jaugeages` | stock | Jaugeage : écart théorique / mesuré, anomalie si au-delà de la tolérance |
| GET/POST | `/api/fournisseurs` | achat | Fournisseurs |
| POST | `/api/livraisons` | achat | Réception d'un camion-citerne (stock +) |
| POST | `/api/pompes`, `/api/pompes/{id}/pistolets` | vente | Paramétrage de la piste |
| POST | `/api/postes/ouvrir`, `/api/postes/{id}/index-debut` | vente | Prise de service et index de début |
| POST | `/api/postes/{id}/cloturer` | vente | Clôture : index de fin, argent compté, calcul de l'écart |
| GET/PUT | `/api/parametres/controle` | vente | Écart de caisse toléré, contrôle des index |
| POST | `/api/ventes` | vente | Vente (volume ou montant ; ESPECES, CARTE, CHEQUE, CREDIT) |
| GET | `/api/clients/{id}/autorisation?montant=` | client | Contrôle du plafond |
| POST | `/api/factures/generer`, `/api/reglements` | client | Facturation et encaissements |
| GET/PUT | `/api/parametres/financiers` | client | TVA et seuil d'alerte des encours |
| GET | `/api/statistiques/dashboard` | statistiques | Chiffres du jour et alertes (stock bas, coulage, écart de caisse, index) |
| GET | `/api/statistiques/pertes` | statistiques | Pertes de carburant et manquants de caisse, en litres et en DH |
| GET | `/api/statistiques/pompistes` | statistiques | Bilan par pompiste (écarts, manquants, index discontinus) |
| GET | `/api/statistiques/ca`, `/marges` | statistiques | Évolution du CA, marges par carburant |

## 6. Limites connues et perspectives

- **Cohérence entre services** : si un service tombe au milieu d'une opération qui en touche deux (vente à crédit, livraison), il n'y a pas d'annulation automatique. Solution cible : pattern Saga avec un broker (Kafka ou RabbitMQ).
- **Config centralisée** : `config-service` (Spring Cloud Config) pourra regrouper tous les `application.yml`.
- **Résilience** : ajouter Resilience4j (circuit breaker) sur les clients Feign.
- **Changement de prix en cours de poste** : le volume d'un poste est valorisé au prix du moment de la clôture. Perspective : clôturer automatiquement les index au moment du changement de prix.
- **Photo du compteur**, **boutique** et **factures PDF** : non implémentées.
