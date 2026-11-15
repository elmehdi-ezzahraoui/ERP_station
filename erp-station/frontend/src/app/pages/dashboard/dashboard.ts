import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { Dashboard } from '../../core/models';
import { messageErreur } from '../../core/utils';
import { StatistiquesService } from '../../services/statistiques.service';

@Component({
  selector: 'app-dashboard',
  imports: [DecimalPipe, DatePipe],
  templateUrl: './dashboard.html',
})
export class DashboardPage implements OnInit {
  private readonly stats = inject(StatistiquesService);

  protected readonly data = signal<Dashboard | null>(null);
  protected readonly erreur = signal('');

  ngOnInit(): void {
    this.charger();
  }

  protected charger(): void {
    this.erreur.set('');
    this.stats.dashboard().subscribe({
      next: (d) => this.data.set(d),
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }
}
