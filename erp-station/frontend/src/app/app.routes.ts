import { inject } from '@angular/core';
import { Routes } from '@angular/router';
import { AuthService } from './core/auth.service';
import { authGuard, roleGuard } from './core/guards';
import { Shell } from './layout/shell';
import { AchatsPage } from './pages/achats/achats';
import { ClientsPage } from './pages/clients/clients';
import { CuvesPage } from './pages/cuves/cuves';
import { DashboardPage } from './pages/dashboard/dashboard';
import { LoginPage } from './pages/login/login';
import { ParametragePage } from './pages/parametrage/parametrage';
import { PostePage } from './pages/poste/poste';
import { StatistiquesPage } from './pages/statistiques/statistiques';
import { UtilisateursPage } from './pages/utilisateurs/utilisateurs';

/** data.roles : roles autorises (ADMIN a toujours acces). Memes regles que la Gateway. */
export const routes: Routes = [
  { path: 'login', component: LoginPage },
  {
    path: '',
    component: Shell,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardPage, canActivate: [roleGuard], data: { roles: ['GERANT', 'COMPTABLE'] } },
      { path: 'poste', component: PostePage, canActivate: [roleGuard], data: { roles: ['GERANT', 'POMPISTE'] } },
      { path: 'cuves', component: CuvesPage, canActivate: [roleGuard], data: { roles: ['GERANT'] } },
      { path: 'achats', component: AchatsPage, canActivate: [roleGuard], data: { roles: ['GERANT', 'COMPTABLE'] } },
      { path: 'clients', component: ClientsPage, canActivate: [roleGuard], data: { roles: ['GERANT', 'COMPTABLE'] } },
      { path: 'statistiques', component: StatistiquesPage, canActivate: [roleGuard], data: { roles: ['GERANT', 'COMPTABLE'] } },
      { path: 'parametrage', component: ParametragePage, canActivate: [roleGuard], data: { roles: ['GERANT', 'COMPTABLE'] } },
      { path: 'utilisateurs', component: UtilisateursPage, canActivate: [roleGuard], data: { roles: [] } },
      { path: '', pathMatch: 'full', redirectTo: () => inject(AuthService).pageAccueil() },
    ],
  },
  { path: '**', redirectTo: '' },
];
