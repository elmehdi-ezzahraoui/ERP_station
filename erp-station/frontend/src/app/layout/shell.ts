import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { Role } from '../core/models';

interface Entree {
  lien: string;
  libelle: string;
  roles: Role[];
}

/** Cadre de l'application : menu lateral filtre selon le role + zone de contenu. */
@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.html',
})
export class Shell {
  protected readonly auth = inject(AuthService);

  private readonly entrees: Entree[] = [
    { lien: '/dashboard', libelle: 'Tableau de bord', roles: ['GERANT', 'COMPTABLE'] },
    { lien: '/poste', libelle: 'Poste pompiste', roles: ['GERANT', 'POMPISTE'] },
    { lien: '/cuves', libelle: 'Cuves et jaugeages', roles: ['GERANT'] },
    { lien: '/achats', libelle: 'Achats', roles: ['GERANT', 'COMPTABLE'] },
    { lien: '/clients', libelle: 'Clients et factures', roles: ['GERANT', 'COMPTABLE'] },
    { lien: '/statistiques', libelle: 'Statistiques des pertes', roles: ['GERANT', 'COMPTABLE'] },
    { lien: '/parametrage', libelle: 'Paramétrage', roles: ['GERANT', 'COMPTABLE'] },
    { lien: '/utilisateurs', libelle: 'Utilisateurs', roles: [] },
  ];

  protected readonly menu = computed(() => this.entrees.filter((e) => this.auth.aRole(...e.roles)));
}
