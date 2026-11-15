import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { Cuve, Fournisseur, Livraison } from '../../core/models';
import { aujourdhui, debutDuMois, messageErreur } from '../../core/utils';
import { AchatService } from '../../services/achat.service';
import { StockService } from '../../services/stock.service';

@Component({
  selector: 'app-achats',
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './achats.html',
})
export class AchatsPage implements OnInit {
  private readonly achat = inject(AchatService);
  private readonly stock = inject(StockService);
  private readonly auth = inject(AuthService);

  /** Le gerant enregistre les achats ; le comptable consulte les livraisons. */
  protected readonly peutModifier = this.auth.aRole('GERANT');
  protected readonly fournisseurs = signal<Fournisseur[]>([]);
  protected readonly livraisons = signal<Livraison[]>([]);
  protected readonly cuves = signal<Cuve[]>([]);
  protected readonly message = signal('');
  protected readonly erreur = signal('');

  protected debut = debutDuMois();
  protected fin = aujourdhui();
  protected nouveauFournisseur: Fournisseur = { id: null, nom: '', telephone: '', email: '', adresse: '' };
  protected livraison = {
    cuveId: null as number | null,
    fournisseurId: null as number | null,
    quantite: null as number | null,
    prixAchatUnitaire: null as number | null,
    numeroBon: '',
  };

  ngOnInit(): void {
    this.chargerLivraisons();
    if (this.peutModifier) {
      this.chargerFournisseurs();
      this.stock.cuves().subscribe({ next: (c) => this.cuves.set(c), error: () => this.cuves.set([]) });
    }
  }

  protected chargerLivraisons(): void {
    this.achat.livraisons(this.debut, this.fin).subscribe({
      next: (l) => this.livraisons.set(l),
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  private chargerFournisseurs(): void {
    this.achat.fournisseurs().subscribe({ next: (f) => this.fournisseurs.set(f), error: (e) => this.erreur.set(messageErreur(e)) });
  }

  protected creerFournisseur(): void {
    this.message.set('');
    this.erreur.set('');
    if (!this.nouveauFournisseur.nom.trim()) {
      this.erreur.set('Le nom du fournisseur est obligatoire.');
      return;
    }
    this.achat.creerFournisseur({ ...this.nouveauFournisseur, email: this.nouveauFournisseur.email || null }).subscribe({
      next: (f) => {
        this.message.set(`Fournisseur ${f.nom} ajouté.`);
        this.nouveauFournisseur = { id: null, nom: '', telephone: '', email: '', adresse: '' };
        this.chargerFournisseurs();
      },
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  protected receptionner(): void {
    this.message.set('');
    this.erreur.set('');
    const l = this.livraison;
    if (l.cuveId === null || l.fournisseurId === null || !l.quantite || !l.prixAchatUnitaire) {
      this.erreur.set('Remplissez la cuve, le fournisseur, la quantité et le prix.');
      return;
    }
    this.achat
      .receptionner({ cuveId: l.cuveId, fournisseurId: l.fournisseurId, quantite: l.quantite, prixAchatUnitaire: l.prixAchatUnitaire, numeroBon: l.numeroBon })
      .subscribe({
        next: (r) => {
          this.message.set(`Livraison enregistrée : ${r.quantite} L dans ${r.cuveCode}. Le stock de la cuve a été augmenté.`);
          this.livraison = { cuveId: null, fournisseurId: null, quantite: null, prixAchatUnitaire: null, numeroBon: '' };
          this.chargerLivraisons();
        },
        error: (e) => this.erreur.set(messageErreur(e)),
      });
  }
}
