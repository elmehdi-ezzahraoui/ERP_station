import { HttpErrorResponse } from '@angular/common/http';

/** Message lisible a partir d'une erreur HTTP (format ApiError du backend ou de la Gateway). */
export function messageErreur(err: unknown): string {
  if (err instanceof HttpErrorResponse) {
    if (err.status === 0) {
      return 'Serveur injoignable : verifiez que la Gateway (port 8888) est demarree.';
    }
    const body = err.error as { message?: string; details?: string[] } | null;
    if (body?.message) {
      return body.details?.length ? `${body.message} : ${body.details.join(', ')}` : body.message;
    }
    if (err.status === 403) {
      return "Acces refuse : votre role ne permet pas cette action.";
    }
    return `Erreur ${err.status} ${err.statusText}`;
  }
  return 'Erreur inattendue';
}

/** Date du jour au format AAAA-MM-JJ (fuseau local). */
export function aujourdhui(): string {
  return formatDate(new Date());
}

export function debutDuMois(): string {
  const d = new Date();
  return formatDate(new Date(d.getFullYear(), d.getMonth(), 1));
}

function formatDate(d: Date): string {
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const jj = String(d.getDate()).padStart(2, '0');
  return `${d.getFullYear()}-${mm}-${jj}`;
}
