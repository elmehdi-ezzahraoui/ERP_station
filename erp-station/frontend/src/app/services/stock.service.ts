import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { Carburant, Cuve, Jaugeage } from '../core/models';

/** Appels vers stock-service (via la Gateway). */
@Injectable({ providedIn: 'root' })
export class StockService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/api`;

  carburants() {
    return this.http.get<Carburant[]>(`${this.api}/carburants`);
  }

  modifierCarburant(c: Carburant) {
    return this.http.put<Carburant>(`${this.api}/carburants/${c.id}`, c);
  }

  cuves() {
    return this.http.get<Cuve[]>(`${this.api}/cuves`);
  }

  modifierCuve(c: Cuve) {
    return this.http.put<Cuve>(`${this.api}/cuves/${c.id}`, {
      code: c.code,
      capaciteMax: c.capaciteMax,
      volumeActuel: c.volumeActuel,
      seuilAlerte: c.seuilAlerte,
      carburantId: c.carburantId,
    });
  }

  jaugeages(cuveId: number) {
    return this.http.get<Jaugeage[]>(`${this.api}/jaugeages`, { params: { cuveId } });
  }

  jauger(cuveId: number, volumeMesure: number, ajusterStock: boolean) {
    return this.http.post<Jaugeage>(`${this.api}/jaugeages`, { cuveId, volumeMesure, ajusterStock });
  }
}
