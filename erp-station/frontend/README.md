# Frontend Angular de l'ERP Station-Service

Application Angular 20 (composants standalone, signals) reliée au backend par la Gateway (`http://localhost:8888`).

## Lancer

```bash
npm install
npm start
```

Puis ouvrir http://localhost:4200. Le backend doit être démarré (au minimum discovery-service, gateway-service et authentification-service).

Comptes de démo : `admin/admin123`, `gerant/gerant123`, `pompiste1/pompiste123`, `comptable/comptable123`.

## Organisation

```
src/app
 |- core/        modèles (interfaces = DTO du backend), authentification, intercepteur JWT, gardes, utilitaires
 |- services/    un service Angular par microservice (stock, achat, vente, client, statistiques, utilisateurs)
 |- layout/      cadre de l'application (menu filtré selon le rôle)
 |- pages/       un dossier par écran
```

| Écran | Rôles | Microservices appelés |
|---|---|---|
| Connexion | tous | authentification |
| Tableau de bord | gérant, comptable | statistiques |
| Poste pompiste (index, ventes, clôture) | pompiste, gérant | vente, client |
| Cuves et jaugeages | gérant | stock |
| Achats (fournisseurs, livraisons) | gérant, comptable (lecture) | achat, stock |
| Clients et factures | comptable, gérant (lecture) | client |
| Statistiques des pertes | gérant, comptable | statistiques |
| Paramétrage | gérant, comptable | vente, stock, client |
| Utilisateurs | admin | authentification |

L'administrateur a accès à tout.

## Sécurité

- `AuthService` appelle `/api/auth/login` et garde le jeton JWT dans le `localStorage`.
- `authInterceptor` ajoute `Authorization: Bearer <jeton>` à chaque requête et déconnecte l'utilisateur si le jeton est refusé (401).
- `authGuard` et `roleGuard` cachent les pages selon le rôle. La vraie sécurité reste dans la Gateway : le frontend ne fait que masquer ce qui n'est pas autorisé.
