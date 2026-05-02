import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';

export interface DensityWaveDisplayConfig {
  dustSize: number;
  showStars: boolean;
  showDust: boolean;
  showFilaments: boolean;
  showH2: boolean;
}

@Component({
  selector: 'app-density-wave-display-section',
  standalone: true,
  imports: [FormsModule, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatIcon, MatSlideToggle, MatTooltip],
  template: `
    <mat-expansion-panel [expanded]="expanded">
      <mat-expansion-panel-header>
        <mat-panel-title>
          <mat-icon>palette</mat-icon>
          &nbsp; Display
        </mat-panel-title>
      </mat-expansion-panel-header>

      <div class="slider-grid">

        <div class="slider-row">
          <label matTooltip="Taille de rendu des particules de poussière">Taille poussière</label>
          <input type="range" [(ngModel)]="config.dustSize" min="0" max="200" step="1"
                 (ngModelChange)="configChange.emit(config)">
          <span>{{ config.dustSize }}</span>
        </div>

        <div class="toggles-row">
          <mat-slide-toggle [(ngModel)]="config.showStars"
            (ngModelChange)="configChange.emit(config)"
            matTooltip="Afficher les étoiles">
            Étoiles
          </mat-slide-toggle>

          <mat-slide-toggle [(ngModel)]="config.showDust"
            (ngModelChange)="configChange.emit(config)"
            matTooltip="Afficher les nuages de poussière">
            Poussière
          </mat-slide-toggle>

          <mat-slide-toggle [(ngModel)]="config.showFilaments"
            (ngModelChange)="configChange.emit(config)"
            matTooltip="Afficher les filaments de poussière">
            Filaments
          </mat-slide-toggle>

          <mat-slide-toggle [(ngModel)]="config.showH2"
            (ngModelChange)="configChange.emit(config)"
            matTooltip="Afficher les régions H2 (nébuleuses)">
            H2
          </mat-slide-toggle>
        </div>

      </div>
    </mat-expansion-panel>
  `,
  styles: [`
    .slider-grid { display: flex; flex-direction: column; gap: 12px; padding: 4px 0; }
    .slider-row {
      display: grid;
      grid-template-columns: 120px 1fr 40px;
      align-items: center;
      gap: 8px;
    }
    .slider-row label { font-size: 13px; color: rgba(0,0,0,0.7); white-space: nowrap; cursor: help; }
    .slider-row input[type=range] { width: 100%; }
    .slider-row span { font-size: 12px; text-align: right; font-family: monospace; color: rgba(0,0,0,0.6); }
    .toggles-row { display: flex; flex-wrap: wrap; gap: 16px; padding-top: 4px; }
  `]
})
export class DensityWaveDisplaySectionComponent {
  @Input() expanded: boolean = true;
  @Input() config: DensityWaveDisplayConfig = { dustSize: 70, showStars: true, showDust: true, showFilaments: true, showH2: true };
  @Output() configChange = new EventEmitter<DensityWaveDisplayConfig>();
}
