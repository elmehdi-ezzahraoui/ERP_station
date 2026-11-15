import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginResponse, Role } from './models';

interface Session {
  token: string;
  username: string;
  nomComplet: string | null;
  role: Role;
  expireLe: number;
}

const CLE = 'erp-station-session';

/** Connexion, jeton JWT et role de l'utilisateur connecte. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly session = signal<Session | null>(this.lireSession());

  readonly connecte = computed(() => this.session() !== null);
  readonly username = computed(() => this.session()?.username ?? '');
  readonly nomAffiche = computed(() => this.session()?.nomComplet || this.session()?.username || '');
  readonly role = computed(() => this.session()?.role ?? null);

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/api/auth/login`, { username, password })
      .pipe(
        tap((r) => {
          const s: Session = {
            token: r.accessToken,
            username: r.username,
            nomComplet: r.nomComplet,
            role: r.role,
            expireLe: Date.now() + r.expiresIn * 1000,
          };
          localStorage.setItem(CLE, JSON.stringify(s));
          this.session.set(s);
        }),
      );
  }

  logout(): void {
    localStorage.removeItem(CLE);
    this.session.set(null);
    this.router.navigate(['/login']);
  }

  token(): string | null {
    return this.session()?.token ?? null;
  }

  aRole(...roles: Role[]): boolean {
    const r = this.role();
    return r !== null && (r === 'ADMIN' || roles.includes(r));
  }

  /** Page d'arrivee apres connexion : le pompiste va directement a son poste. */
  pageAccueil(): string {
    const r = this.role();
    if (r === null) {
      return '/login';
    }
    return r === 'POMPISTE' ? '/poste' : '/dashboard';
  }

  private lireSession(): Session | null {
    try {
      const brut = localStorage.getItem(CLE);
      if (!brut) {
        return null;
      }
      const s = JSON.parse(brut) as Session;
      return s.expireLe > Date.now() ? s : null;
    } catch {
      return null;
    }
  }
}
