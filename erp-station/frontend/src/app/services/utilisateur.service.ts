import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { Role, Utilisateur } from '../core/models';

/** Appels vers authentification-service pour la gestion des comptes (ADMIN). */
@Injectable({ providedIn: 'root' })
export class UtilisateurService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/api/utilisateurs`;

  lister() {
    return this.http.get<Utilisateur[]>(this.api);
  }

  creer(u: { username: string; motDePasse: string; nomComplet: string; role: Role }) {
    return this.http.post<Utilisateur>(this.api, u);
  }

  modifier(u: Utilisateur) {
    return this.http.put<Utilisateur>(`${this.api}/${u.id}`, { nomComplet: u.nomComplet, role: u.role, actif: u.actif });
  }
}
