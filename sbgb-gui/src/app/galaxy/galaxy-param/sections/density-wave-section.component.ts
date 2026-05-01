import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MatSlideToggle } from '@angular/material/slide-toggle';

@Component({
  selector: 'app-density-wave-section',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatFormField,
    MatLabel,
    MatInput,
    MatIcon,
    MatTooltip,
    MatSuffix,
    MatSlideToggle
  ],
  template: `
    <mat-expansion-panel [expanded]="expanded" [formGroup]="formGroup">
      <mat-expansion-panel-header>
        <mat-panel-title>
          <mat-icon>blur_on</mat-icon>
          &nbsp; Paramètres Density Wave
        </mat-panel-title>
      </mat-expansion-panel-header>

      <div style="display: flex; gap: 10px; flex-wrap: wrap;">
        <mat-form-field>
          <mat-label>Rayon de la galaxie</mat-label>
          <input type="number" matInput formControlName="galaxyRadius" min="5000" max="50000" step="1000">
          <mat-icon matSuffix matTooltip="Rayon total de la galaxie en unités arbitraires">help_outline</mat-icon>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Rayon du cœur</mat-label>
          <input type="number" matInput formControlName="coreRadius" min="500" max="10000" step="500">
          <mat-icon matSuffix matTooltip="Rayon du bulbe central">help_outline</mat-icon>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Nombre d'étoiles</mat-label>
          <input type="number" matInput formControlName="starCount" min="5000" max="100000" step="5000">
          <mat-icon matSuffix matTooltip="Nombre total de particules générées">help_outline</mat-icon>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Décalage angulaire</mat-label>
          <input type="number" matInput formControlName="angleOffset" step="0.005" min="0" max="0.1">
          <mat-icon matSuffix matTooltip="Décalage entre les bras spiraux (structure de densité d'onde)">help_outline</mat-icon>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Excentricité intérieure</mat-label>
          <input type="number" matInput formControlName="eccentricityInner" step="0.01" min="0.5" max="1.0">
          <mat-icon matSuffix matTooltip="Excentricité des orbites proches du centre">help_outline</mat-icon>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Excentricité extérieure</mat-label>
          <input type="number" matInput formControlName="eccentricityOuter" step="0.01" min="0.5" max="1.0">
          <mat-icon matSuffix matTooltip="Excentricité des orbites lointaines">help_outline</mat-icon>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Perturbation N</mat-label>
          <input type="number" matInput formControlName="pertN" min="0" max="10" step="1">
          <mat-icon matSuffix matTooltip="Nombre de modes de perturbation des orbites (0 = aucune)">help_outline</mat-icon>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Amplitude perturbation</mat-label>
          <input type="number" matInput formControlName="pertAmp" min="0" max="500" step="10">
          <mat-icon matSuffix matTooltip="Amplitude de la perturbation orbitale en unités arbitraires">help_outline</mat-icon>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Température de base (K)</mat-label>
          <input type="number" matInput formControlName="baseTemp" min="1000" max="30000" step="500">
          <mat-icon matSuffix matTooltip="Température stellaire de référence pour la colorisation">help_outline</mat-icon>
        </mat-form-field>

        <div style="display:flex; align-items:center; gap:8px; min-width:160px; padding-top:8px;">
          <mat-slide-toggle formControlName="hasDarkMatter">Matière noire</mat-slide-toggle>
          <mat-icon matTooltip="Active l'influence de la matière noire sur la courbe de rotation">help_outline</mat-icon>
        </div>
      </div>
    </mat-expansion-panel>
  `
})
export class DensityWaveSectionComponent {
  @Input() formGroup!: FormGroup;
  @Input() expanded: boolean = true;
}
