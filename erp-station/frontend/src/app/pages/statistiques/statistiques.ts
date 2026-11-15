import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CaJournalier, PerformancePompiste, RapportMarges, RapportPertes } from '../../core/models';
import { aujourdhui, debutDuMois, messageErreur } from '../../core/utils';
import { StatistiquesService } from '../../services/statistiques.service';

@Component({
  selector: 'app-statistiques',
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './statistiques.html',
})
export class StatistiquesPage implements OnInit {
  private readonly stats = inject(StatistiquesService);

  protected debut = debutDuMois();
  protected fin = aujourdhui();
  protected readonly pertes = signal<RapportPertes | null>(null);
  protected readonly pompistes = signal<PerformancePompiste[]>([]);
  protected readonly marges = signal<RapportMarges | null>(null);
  protected readonly ca = signal<CaJournalier[]>([]);
  protected readonly erreur = signal('');
  protected readonly caMax = computed(() => Math.max(1, ...this.ca().map((j) => j.chiffreAffaires)));

  ngOnInit(): void {
    this.charger();
  }

  protected charger(): void {
    this.erreur.set('');
    const err = (e: unknown) => this.erreur.set(messageErreur(e));
    this.stats.pertes(this.debut, this.fin).subscribe({ next: (p) => this.pertes.set(p), error: err });
    this.stats.pompistes(this.debut, this.fin).subscribe({ next: (p) => this.pompistes.set(p), error: err });
    this.stats.marges(this.debut, this.fin).subscribe({ next: (m) => this.marges.set(m), error: err });
    this.stats.chiffreAffaires(this.debut, this.fin).subscribe({ next: (c) => this.ca.set(c), error: err });
  }
}
