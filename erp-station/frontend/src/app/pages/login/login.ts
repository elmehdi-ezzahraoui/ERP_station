import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { messageErreur } from '../../core/utils';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
})
export class LoginPage {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected username = '';
  protected password = '';
  protected readonly erreur = signal('');
  protected readonly chargement = signal(false);

  constructor() {
    if (this.auth.connecte()) {
      this.router.navigateByUrl(this.auth.pageAccueil());
    }
  }

  protected seConnecter(): void {
    this.erreur.set('');
    this.chargement.set(true);
    this.auth.login(this.username, this.password).subscribe({
      next: () => this.router.navigateByUrl(this.auth.pageAccueil()),
      error: (e) => {
        this.erreur.set(e.status === 401 ? 'Identifiants incorrects' : messageErreur(e));
        this.chargement.set(false);
      },
    });
  }
}
