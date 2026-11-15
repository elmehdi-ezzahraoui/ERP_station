import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/** Ajoute "Authorization: Bearer <jeton>" a chaque requete ; deconnecte si le jeton est refuse (401). */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token();
  const requete = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
  return next(requete).pipe(
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse && err.status === 401 && !req.url.endsWith('/api/auth/login')) {
        auth.logout();
      }
      return throwError(() => err);
    }),
  );
};
