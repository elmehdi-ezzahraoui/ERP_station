import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { Fournisseur, Livraison } from '../core/models';

/** Appels vers achat-service (via la Gateway). */
@Injectable({ providedIn: 'root' })
export class AchatService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/api`;

  fournisseurs() {
    return this.http.get<Fournisseur[]>(`${this.api}/fournisseurs`);
  }

  creerFournisseur(f: Fournisseur) {
    return this.http.post<Fournisseur>(`${this.api}/fournisseurs`, f);
  }

  livraisons(debut: string, fin: string) {
    return this.http.get<Livraison[]>(`${this.api}/livraisons`, { params: { debut, fin } });
  }

  receptionner(l: { cuveId: number; fournisseurId: number; quantite: number; prixAchatUnitaire: number; numeroBon: string }) {
    return this.http.post<Livraison>(`${this.api}/livraisons`, l);
  }
}
