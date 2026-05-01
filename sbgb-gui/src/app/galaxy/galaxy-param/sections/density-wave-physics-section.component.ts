import { Component, Input } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
  selector: 'app-density-wave-physics-section',
  standalone: true,
  imports: [DecimalPipe, ReactiveFormsModule, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatIcon, MatSlideToggle, MatTooltip],
  template: `
    <mat-expansion-panel [expanded]="expanded" [formGroup]="formGroup">
      <mat-expansion-panel-header>
        <mat-panel-title>
          <mat-icon>science</mat-icon>
          &nbsp; Physics
        </mat-panel-title>
      </mat-expansion-panel-header>

      <div class="slider-grid">

        <div class="slider-row">
          <label matTooltip="Rayon total de la galaxie">Rayon galaxie</label>
          <input type="range" formControlName="galaxyRadius" min="5000" max="30000" step="500">
          <span>{{ formGroup.get('galaxyRadius')?.value | number:'1.0-0' }}</span>
        </div>

        <div class="slider-row">
          <label matTooltip="Rayon du bulbe central">Rayon cœur</label>
          <input type="range" formControlName="coreRadius" min="500" max="15000" step="500">
          <span>{{ formGroup.get('coreRadius')?.value | number:'1.0-0' }}</span>
        </div>

        <div class="slider-row">
          <label matTooltip="Nombre total de particules générées">Nb étoiles</label>
          <input type="range" formControlName="starCount" min="5000" max="80000" step="5000">
          <span>{{ formGroup.get('starCount')?.value | number:'1.0-0' }}</span>
        </div>

        <div class="slider-row">
          <label matTooltip="Décalage angulaire entre bras spiraux">Δ angulaire</label>
          <input type="range" formControlName="angleOffset" min="0.0001" max="0.001" step="0.00005">
          <span>{{ formGroup.get('angleOffset')?.value | number:'1.0-5' }}</span>
        </div>

        <div class="slider-row">
          <label matTooltip="Excentricité des orbites intérieures">Excent. intér.</label>
          <input type="range" formControlName="eccentricityInner" min="0.5" max="1.5" step="0.01">
          <span>{{ formGroup.get('eccentricityInner')?.value | number:'1.2-2' }}</span>
        </div>

        <div class="slider-row">
          <label matTooltip="Excentricité des orbites extérieures">Excent. extér.</label>
          <input type="range" formControlName="eccentricityOuter" min="0.5" max="1.5" step="0.01">
          <span>{{ formGroup.get('eccentricityOuter')?.value | number:'1.2-2' }}</span>
        </div>

        <div class="slider-row">
          <label matTooltip="Modes de perturbation orbitale (0 = aucune)">Pert. N</label>
          <input type="range" formControlName="pertN" min="0" max="5" step="1">
          <span>{{ formGroup.get('pertN')?.value }}</span>
        </div>

        <div class="slider-row">
          <label matTooltip="Amplitude de la perturbation orbitale">Pert. Amp</label>
          <input type="range" formControlName="pertAmp" min="0" max="100" step="1">
          <span>{{ formGroup.get('pertAmp')?.value }}</span>
        </div>

        <div class="slider-row">
          <label matTooltip="Température stellaire de référence (K)">Temp. base (K)</label>
          <input type="range" formControlName="baseTemp" min="1000" max="10000" step="100">
          <span>{{ formGroup.get('baseTemp')?.value | number:'1.0-0' }}</span>
        </div>

        <div class="toggle-row">
          <mat-slide-toggle formControlName="hasDarkMatter"
            matTooltip="Active l'influence de la matière noire sur la courbe de rotation">
            Matière noire
          </mat-slide-toggle>
        </div>

      </div>
    </mat-expansion-panel>
  `,
  styles: [`
    .slider-grid { display: flex; flex-direction: column; gap: 8px; padding: 4px 0; }
    .slider-row {
      display: grid;
      grid-template-columns: 120px 1fr 56px;
      align-items: center;
      gap: 8px;
    }
    .slider-row label { font-size: 13px; color: rgba(0,0,0,0.7); white-space: nowrap; cursor: help; }
    .slider-row input[type=range] { width: 100%; }
    .slider-row span { font-size: 12px; text-align: right; font-family: monospace; color: rgba(0,0,0,0.6); }
    .toggle-row { padding-top: 4px; }
  `]
})
export class DensityWavePhysicsSectionComponent {
  @Input() formGroup!: FormGroup;
  @Input() expanded: boolean = true;
}
