import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { Client, Facture } from '../../core/models';
import { aujourdhui, debutDuMois, messageErreur } from '../../core/utils';
import { ClientService } from '../../services/client.service';

@Component({
  selector: 'app-clients',
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './clients.html',
})
export class ClientsPage implements OnInit {
  private readonly clientSvc = inject(ClientService);
  private readonly auth = inject(AuthService);

  /** Le comptable gere clients, factures et reglements ; le gerant consulte les clients. */
  protected readonly peutModifier = this.auth.aRole('COMPTABLE');
  protected readonly clients = signal<Client[]>([]);
  protected readonly factures = signal<Facture[]>([]);
  protected readonly message = signal('');
  protected readonly erreur = signal('');

  protected nouveauClient = { raisonSociale: '', ice: '', telephone: '', email: '', adresse: '', plafondCredit: 5000 };
  protected facturation = { clientId: null as number | null, debut: debutDuMois(), fin: aujourdhui() };
  protected reglement = { factureId: null as number | null, montant: null as number | null, mode: 'VIREMENT', reference: '' };
  protected readonly modesReglement = ['ESPECES', 'CHEQUE', 'VIREMENT', 'EFFET'];

  ngOnInit(): void {
    this.charger();
  }

  private charger(): void {
    this.clientSvc.clients().subscribe({ next: (c) => this.clients.set(c), error: (e) => this.erreur.set(messageErreur(e)) });
    if (this.peutModifier) {
      this.clientSvc.factures().subscribe({ next: (f) => this.factures.set(f), error: (e) => this.erreur.set(messageErreur(e)) });
    }
  }

  private ok(texte: string): void {
    this.erreur.set('');
    this.message.set(texte);
    this.charger();
  }

  private ko(e: unknown): void {
    this.message.set('');
    this.erreur.set(messageErreur(e));
  }

  protected creerClient(): void {
    const c = this.nouveauClient;
    this.clientSvc
      .creerClient({
        raisonSociale: c.raisonSociale,
        ice: c.ice || null,
        telephone: c.telephone || null,
        email: c.email || null,
        adresse: c.adresse || null,
        plafondCredit: c.plafondCredit,
      })
      .subscribe({
        next: (r) => {
          this.nouveauClient = { raisonSociale: '', ice: '', telephone: '', email: '', adresse: '', plafondCredit: 5000 };
          this.ok(`Client ${r.raisonSociale} créé.`);
        },
        error: (e) => this.ko(e),
      });
  }

  protected genererFacture(): void {
    const f = this.facturation;
    if (f.clientId === null) {
      this.erreur.set('Choisissez un client.');
      return;
    }
    this.clientSvc.genererFacture(f.clientId, f.debut, f.fin).subscribe({
      next: (r) => this.ok(`Facture ${r.numeroFacture} générée : ${r.montantTTC} DH TTC.`),
      error: (e) => this.ko(e),
    });
  }

  protected regler(): void {
    const r = this.reglement;
    if (r.factureId === null || !r.montant) {
      this.erreur.set('Choisissez une facture et un montant.');
      return;
    }
    this.clientSvc.regler(r.factureId, r.montant, r.mode, r.reference).subscribe({
      next: (res) => {
        this.reglement = { factureId: null, montant: null, mode: 'VIREMENT', reference: '' };
        this.ok(`Règlement enregistré sur ${res.numeroFacture} (${res.statutFacture}).`);
      },
      error: (e) => this.ko(e),
    });
  }

  protected facturesAPayer(): Facture[] {
    return this.factures().filter((f) => f.statut !== 'PAYEE');
  }
}
