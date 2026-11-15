import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Role } from './models';

/** Bloque les pages si l'utilisateur n'est pas connecte. */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.connecte() ? true : inject(Router).parseUrl('/login');
};

/** Bloque une page si le role de l'utilisateur n'est pas dans data.roles (ADMIN passe toujours). */
export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const roles = (route.data['roles'] as Role[] | undefined) ?? [];
  return auth.aRole(...roles) ? true : inject(Router).parseUrl(auth.pageAccueil());
};
