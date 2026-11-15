import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { CaJournalier, Dashboard, PerformancePompiste, RapportMarges, RapportPertes } from '../core/models';

/** Appels vers statistiques-service (via la Gateway). */
@Injectable({ providedIn: 'root' })
export class StatistiquesService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/api/statistiques`;

  dashboard() {
    return this.http.get<Dashboard>(`${this.api}/dashboard`);
  }

  pertes(debut: string, fin: string) {
    return this.http.get<RapportPertes>(`${this.api}/pertes`, { params: { debut, fin } });
  }

  pompistes(debut: string, fin: string) {
    return this.http.get<PerformancePompiste[]>(`${this.api}/pompistes`, { params: { debut, fin } });
  }

  marges(debut: string, fin: string) {
    return this.http.get<RapportMarges>(`${this.api}/marges`, { params: { debut, fin } });
  }

  chiffreAffaires(debut: string, fin: string) {
    return this.http.get<CaJournalier[]>(`${this.api}/ca`, { params: { debut, fin } });
  }
}
