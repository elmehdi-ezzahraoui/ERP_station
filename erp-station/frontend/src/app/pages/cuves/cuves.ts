import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Cuve, Jaugeage } from '../../core/models';
import { messageErreur } from '../../core/utils';
import { StockService } from '../../services/stock.service';

@Component({
  selector: 'app-cuves',
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './cuves.html',
})
export class CuvesPage implements OnInit {
  private readonly stock = inject(StockService);

  protected readonly cuves = signal<Cuve[]>([]);
  protected readonly historique = signal<Jaugeage[]>([]);
  protected readonly resultat = signal<Jaugeage | null>(null);
  protected readonly erreur = signal('');

  protected cuveId: number | null = null;
  protected volumeMesure: number | null = null;
  protected ajusterStock = true;

  ngOnInit(): void {
    this.chargerCuves();
  }

  private chargerCuves(): void {
    this.stock.cuves().subscribe({
      next: (c) => this.cuves.set(c),
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  protected choisirCuve(id: number | null): void {
    this.cuveId = id;
    this.resultat.set(null);
    this.historique.set([]);
    if (id !== null) {
      this.stock.jaugeages(id).subscribe({ next: (j) => this.historique.set(j), error: () => this.historique.set([]) });
    }
  }

  protected jauger(): void {
    this.erreur.set('');
    if (this.cuveId === null || this.volumeMesure === null || this.volumeMesure < 0) {
      this.erreur.set('Choisissez une cuve et saisissez le volume mesuré.');
      return;
    }
    this.stock.jauger(this.cuveId, this.volumeMesure, this.ajusterStock).subscribe({
      next: (j) => {
        this.resultat.set(j);
        this.volumeMesure = null;
        this.chargerCuves();
        this.choisirCuveSansReset(j.cuveId);
      },
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  private choisirCuveSansReset(id: number): void {
    this.stock.jaugeages(id).subscribe({ next: (j) => this.historique.set(j), error: () => this.historique.set([]) });
  }
}
