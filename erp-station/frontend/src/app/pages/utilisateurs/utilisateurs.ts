import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Role, Utilisateur } from '../../core/models';
import { messageErreur } from '../../core/utils';
import { UtilisateurService } from '../../services/utilisateur.service';

@Component({
  selector: 'app-utilisateurs',
  imports: [FormsModule],
  templateUrl: './utilisateurs.html',
})
export class UtilisateursPage implements OnInit {
  private readonly svc = inject(UtilisateurService);

  protected readonly roles: Role[] = ['ADMIN', 'GERANT', 'POMPISTE', 'COMPTABLE'];
  protected readonly utilisateurs = signal<Utilisateur[]>([]);
  protected readonly message = signal('');
  protected readonly erreur = signal('');
  protected nouveau = { username: '', motDePasse: '', nomComplet: '', role: 'POMPISTE' as Role };

  ngOnInit(): void {
    this.charger();
  }

  private charger(): void {
    this.svc.lister().subscribe({ next: (u) => this.utilisateurs.set(u), error: (e) => this.erreur.set(messageErreur(e)) });
  }

  protected creer(): void {
    this.svc.creer(this.nouveau).subscribe({
      next: (u) => {
        this.erreur.set('');
        this.message.set(`Compte ${u.username} créé.`);
        this.nouveau = { username: '', motDePasse: '', nomComplet: '', role: 'POMPISTE' };
        this.charger();
      },
      error: (e) => {
        this.message.set('');
        this.erreur.set(messageErreur(e));
      },
    });
  }

  protected enregistrer(u: Utilisateur): void {
    this.svc.modifier(u).subscribe({
      next: () => {
        this.erreur.set('');
        this.message.set(`Compte ${u.username} mis à jour.`);
      },
      error: (e) => {
        this.message.set('');
        this.erreur.set(messageErreur(e));
      },
    });
  }
}
