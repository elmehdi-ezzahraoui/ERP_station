import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { Carburant, Cuve, ParametreControle, ParametreFinancier } from '../../core/models';
import { messageErreur } from '../../core/utils';
import { ClientService } from '../../services/client.service';
import { StockService } from '../../services/stock.service';
import { VenteService } from '../../services/vente.service';

/**
 * Parametrage du controle des pertes et des finances.
 * Chaque parametre est stocke dans le microservice qui l'utilise :
 * vente-service (ecart de caisse), stock-service (tolerances, seuils), client-service (TVA).
 */
@Component({
  selector: 'app-parametrage',
  imports: [FormsModule, DecimalPipe],
  templateUrl: './parametrage.html',
})
export class ParametragePage implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly venteSvc = inject(VenteService);
  private readonly stockSvc = inject(StockService);
  private readonly clientSvc = inject(ClientService);

  protected readonly estGerant = this.auth.aRole('GERANT');
  protected readonly estComptable = this.auth.aRole('COMPTABLE');

  protected controle: ParametreControle | null = null;
  protected financier: ParametreFinancier | null = null;
  protected carburants: Carburant[] = [];
  protected cuves: Cuve[] = [];
  protected readonly message = signal('');
  protected readonly erreur = signal('');

  ngOnInit(): void {
    const err = (e: unknown) => this.erreur.set(messageErreur(e));
    this.venteSvc.parametresControle().subscribe({ next: (p) => (this.controle = p), error: err });
    this.clientSvc.parametresFinanciers().subscribe({ next: (p) => (this.financier = p), error: err });
    this.stockSvc.carburants().subscribe({ next: (c) => (this.carburants = c), error: err });
    if (this.estGerant) {
      this.stockSvc.cuves().subscribe({ next: (c) => (this.cuves = c), error: err });
    }
  }

  private ok(texte: string): void {
    this.erreur.set('');
    this.message.set(texte);
  }

  private ko(e: unknown): void {
    this.message.set('');
    this.erreur.set(messageErreur(e));
  }

  protected enregistrerControle(): void {
    if (!this.controle) return;
    this.venteSvc.modifierParametresControle(this.controle).subscribe({
      next: (p) => {
        this.controle = p;
        this.ok('Paramètres de contrôle de caisse enregistrés.');
      },
      error: (e) => this.ko(e),
    });
  }

  protected enregistrerFinancier(): void {
    if (!this.financier) return;
    this.clientSvc.modifierParametresFinanciers(this.financier).subscribe({
      next: (p) => {
        this.financier = p;
        this.ok('Paramètres financiers enregistrés.');
      },
      error: (e) => this.ko(e),
    });
  }

  protected enregistrerCarburant(c: Carburant): void {
    this.stockSvc.modifierCarburant(c).subscribe({
      next: () => this.ok(`${c.nom} : prix et tolérance enregistrés.`),
      error: (e) => this.ko(e),
    });
  }

  protected enregistrerCuve(c: Cuve): void {
    this.stockSvc.modifierCuve(c).subscribe({
      next: () => this.ok(`${c.code} : seuil d'alerte enregistré.`),
      error: (e) => this.ko(e),
    });
  }
}
