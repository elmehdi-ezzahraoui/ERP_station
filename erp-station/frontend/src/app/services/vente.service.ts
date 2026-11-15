import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { Cloture, ModePaiement, ParametreControle, Pistolet, Poste, ReleveIndex, Vente } from '../core/models';

/** Appels vers vente-service (via la Gateway). */
@Injectable({ providedIn: 'root' })
export class VenteService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/api`;

  pistolets() {
    return this.http.get<Pistolet[]>(`${this.api}/pistolets`);
  }

  postesOuverts() {
    return this.http.get<Poste[]>(`${this.api}/postes/ouverts`);
  }

  ouvrirPoste(fondCaisse: number) {
    return this.http.post<Poste>(`${this.api}/postes/ouvrir`, { fondCaisse });
  }

  saisirIndexDebut(posteId: number, releves: { pistoletId: number; index: number }[]) {
    return this.http.post<ReleveIndex[]>(`${this.api}/postes/${posteId}/index-debut`, { releves });
  }

  releves(posteId: number) {
    return this.http.get<ReleveIndex[]>(`${this.api}/postes/${posteId}/releves`);
  }

  ventesDuJour(date: string) {
    return this.http.get<Vente[]>(`${this.api}/ventes`, { params: { debut: date, fin: date } });
  }

  enregistrerVente(v: {
    pistoletId: number;
    posteId: number;
    volume: number | null;
    montant: number | null;
    modePaiement: ModePaiement;
    clientId: number | null;
  }) {
    return this.http.post<Vente>(`${this.api}/ventes`, v);
  }

  cloturer(posteId: number, totalPhysique: number, indexFin: { pistoletId: number; index: number }[]) {
    return this.http.post<Cloture>(`${this.api}/postes/${posteId}/cloturer`, { totalPhysique, indexFin });
  }

  parametresControle() {
    return this.http.get<ParametreControle>(`${this.api}/parametres/controle`);
  }

  modifierParametresControle(p: ParametreControle) {
    return this.http.put<ParametreControle>(`${this.api}/parametres/controle`, p);
  }
}
