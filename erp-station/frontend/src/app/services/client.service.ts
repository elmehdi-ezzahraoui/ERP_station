import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { Client, Facture, ParametreFinancier, Reglement } from '../core/models';

/** Appels vers client-service (via la Gateway). */
@Injectable({ providedIn: 'root' })
export class ClientService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/api`;

  clients() {
    return this.http.get<Client[]>(`${this.api}/clients`);
  }

  creerClient(c: { raisonSociale: string; ice: string | null; telephone: string | null; email: string | null; adresse: string | null; plafondCredit: number }) {
    return this.http.post<Client>(`${this.api}/clients`, c);
  }

  factures() {
    return this.http.get<Facture[]>(`${this.api}/factures`);
  }

  genererFacture(clientId: number, debut: string, fin: string) {
    return this.http.post<Facture>(`${this.api}/factures/generer`, { clientId, debut, fin });
  }

  regler(factureId: number, montant: number, modeReglement: string, reference: string) {
    return this.http.post<Reglement>(`${this.api}/reglements`, { factureId, montant, modeReglement, reference });
  }

  parametresFinanciers() {
    return this.http.get<ParametreFinancier>(`${this.api}/parametres/financiers`);
  }

  modifierParametresFinanciers(p: ParametreFinancier) {
    return this.http.put<ParametreFinancier>(`${this.api}/parametres/financiers`, p);
  }
}
