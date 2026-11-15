import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { Client, Cloture, Cuve, ModePaiement, Pistolet, Poste, ReleveIndex, Vente } from '../../core/models';
import { aujourdhui, messageErreur } from '../../core/utils';
import { ClientService } from '../../services/client.service';
import { StockService } from '../../services/stock.service';
import { VenteService } from '../../services/vente.service';

interface SaisieIndex {
  pistolet: Pistolet;
  utilise: boolean;
  index: number;
}

/**
 * Poste du pompiste, en 3 etapes :
 * 1. ouverture + index de debut de chaque pistolet utilise
 * 2. saisie des ventes par carte, cheque ou credit (les especes sont deduites des compteurs)
 * 3. cloture : index de fin + argent compte -> calcul de l'ecart
 */
@Component({
  selector: 'app-poste',
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './poste.html',
})
export class PostePage implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly venteSvc = inject(VenteService);
  private readonly clientSvc = inject(ClientService);
  private readonly stockSvc = inject(StockService);

  protected readonly postes = signal<Poste[]>([]);
  protected readonly posteId = signal<number | null>(null);
  protected readonly poste = computed(() => this.postes().find((p) => p.id === this.posteId()) ?? null);
  protected readonly monPosteOuvert = computed(() => this.postes().some((p) => p.pompiste === this.auth.username()));
  protected readonly pistolets = signal<Pistolet[]>([]);
  protected readonly cuves = signal<Cuve[]>([]);
  protected readonly releves = signal<ReleveIndex[]>([]);
  protected readonly ventes = signal<Vente[]>([]);
  protected readonly clients = signal<Client[]>([]);
  protected readonly cloture = signal<Cloture | null>(null);
  protected readonly message = signal('');
  protected readonly avertissement = signal('');
  protected readonly erreur = signal('');

  protected fondCaisse = 500;
  protected saisieDebut: SaisieIndex[] = [];
  protected readonly modes: ModePaiement[] = ['CARTE', 'CHEQUE', 'CREDIT', 'ESPECES'];
  protected venteForm = {
    pistoletId: null as number | null,
    mode: 'CARTE' as ModePaiement,
    type: 'montant' as 'montant' | 'volume',
    valeur: null as number | null,
    clientId: null as number | null,
  };
  protected indexFin: Record<number, number | null> = {};
  protected totalPhysique: number | null = null;

  ngOnInit(): void {
    this.chargerPistolets();
    this.stockSvc.cuves().subscribe({ next: (c) => this.cuves.set(c), error: () => this.cuves.set([]) });
    this.clientSvc.clients().subscribe({ next: (c) => this.clients.set(c.filter((x) => x.actif)), error: () => this.clients.set([]) });
    this.chargerPostes();
  }

  protected carburant(cuveId: number): string {
    return this.cuves().find((c) => c.id === cuveId)?.carburantNom ?? `cuve ${cuveId}`;
  }

  protected libellePistolet(pistoletId: number): string {
    const p = this.pistolets().find((x) => x.id === pistoletId);
    return p ? `Pompe ${p.pompeNumero} - pistolet ${p.numero} (${this.carburant(p.cuveId)})` : `Pistolet ${pistoletId}`;
  }

  protected chargerPostes(selection?: number): void {
    this.venteSvc.postesOuverts().subscribe({
      next: (ps) => {
        this.postes.set(ps);
        const mien = ps.find((p) => p.pompiste === this.auth.username());
        const actuel = this.posteId();
        const id = selection ?? (actuel !== null && ps.some((p) => p.id === actuel) ? actuel : mien?.id ?? null);
        this.selectionner(id);
      },
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  protected selectionner(id: number | null): void {
    this.posteId.set(id);
    this.releves.set([]);
    this.ventes.set([]);
    if (id !== null) {
      this.chargerReleves(id);
      this.chargerVentes();
    }
  }

  private chargerPistolets(): void {
    this.venteSvc.pistolets().subscribe({
      next: (ps) => {
        const actifs = ps.filter((p) => p.active);
        this.pistolets.set(actifs);
        this.saisieDebut = actifs.map((p) => ({ pistolet: p, utilise: false, index: p.dernierIndex }));
      },
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  private chargerReleves(posteId: number): void {
    this.venteSvc.releves(posteId).subscribe({
      next: (r) => {
        this.releves.set(r);
        this.indexFin = Object.fromEntries(r.map((x) => [x.pistoletId, null]));
      },
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  private chargerVentes(): void {
    this.venteSvc.ventesDuJour(aujourdhui()).subscribe({
      next: (v) => this.ventes.set(v.filter((x) => x.posteId === this.posteId())),
      error: () => this.ventes.set([]),
    });
  }

  private reinitMessages(): void {
    this.message.set('');
    this.avertissement.set('');
    this.erreur.set('');
  }

  // --------------------------------------------------------------- etape 1
  protected ouvrir(): void {
    this.reinitMessages();
    this.cloture.set(null);
    this.venteSvc.ouvrirPoste(this.fondCaisse).subscribe({
      next: (p) => {
        this.message.set(`Poste ouvert avec ${this.fondCaisse} DH de fond de caisse. Saisissez maintenant les index de début.`);
        this.chargerPostes(p.id);
      },
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  protected enregistrerIndexDebut(): void {
    this.reinitMessages();
    const posteId = this.posteId();
    const releves = this.saisieDebut
      .filter((s) => s.utilise)
      .map((s) => ({ pistoletId: s.pistolet.id, index: s.index }));
    if (posteId === null || releves.length === 0) {
      this.erreur.set('Cochez au moins un pistolet.');
      return;
    }
    this.venteSvc.saisirIndexDebut(posteId, releves).subscribe({
      next: (r) => {
        this.releves.set(r);
        this.indexFin = Object.fromEntries(r.map((x) => [x.pistoletId, null]));
        this.message.set('Index de début enregistrés.');
        const suspects = r.filter((x) => x.indexDiscontinu);
        if (suspects.length) {
          this.avertissement.set(
            'Index discontinu : ' +
              suspects.map((x) => `${this.libellePistolet(x.pistoletId)} (attendu ${x.dernierIndexConnu}, saisi ${x.indexDebut})`).join(' ; ') +
              '. Le gérant sera alerté.',
          );
        }
      },
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  // --------------------------------------------------------------- etape 2
  protected enregistrerVente(): void {
    this.reinitMessages();
    const posteId = this.posteId();
    const f = this.venteForm;
    if (posteId === null || f.pistoletId === null || !f.valeur || f.valeur <= 0) {
      this.erreur.set('Choisissez un pistolet et saisissez un montant ou un volume positif.');
      return;
    }
    if (f.mode === 'CREDIT' && f.clientId === null) {
      this.erreur.set('Choisissez le client pour une vente à crédit.');
      return;
    }
    this.venteSvc
      .enregistrerVente({
        pistoletId: f.pistoletId,
        posteId,
        volume: f.type === 'volume' ? f.valeur : null,
        montant: f.type === 'montant' ? f.valeur : null,
        modePaiement: f.mode,
        clientId: f.mode === 'CREDIT' ? f.clientId : null,
      })
      .subscribe({
        next: (v) => {
          this.message.set(`Vente enregistrée : ${v.volume} L de ${v.carburantNom}, ${v.montantTotal} DH (${v.modePaiement}).`);
          this.venteForm.valeur = null;
          this.chargerVentes();
        },
        error: (e) => this.erreur.set(messageErreur(e)),
      });
  }

  // --------------------------------------------------------------- etape 3
  protected cloturer(): void {
    this.reinitMessages();
    const posteId = this.posteId();
    if (posteId === null || this.totalPhysique === null || this.totalPhysique < 0) {
      this.erreur.set("Saisissez l'argent compté dans la caisse.");
      return;
    }
    const manquants = this.releves().filter((r) => this.indexFin[r.pistoletId] === null || this.indexFin[r.pistoletId] === undefined);
    if (manquants.length) {
      this.erreur.set("Saisissez l'index de fin de chaque pistolet.");
      return;
    }
    const indexFin = this.releves().map((r) => ({ pistoletId: r.pistoletId, index: this.indexFin[r.pistoletId] as number }));
    this.venteSvc.cloturer(posteId, this.totalPhysique, indexFin).subscribe({
      next: (c) => {
        this.cloture.set(c);
        this.totalPhysique = null;
        if (c.ecartHorsTolerance) {
          this.avertissement.set(`Écart de ${c.ecart} DH, au-delà des ${c.seuilEcartApplique} DH tolérés : le gérant est alerté.`);
        } else {
          this.message.set('Poste clôturé. Écart dans la tolérance.');
        }
        this.chargerPistolets();
        this.chargerPostes();
      },
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }
}
