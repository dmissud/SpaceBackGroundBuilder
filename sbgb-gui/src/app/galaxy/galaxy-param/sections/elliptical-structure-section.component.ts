import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-elliptical-structure-section',
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
    MatButton
  ],
  template: `
    <mat-expansion-panel [formGroup]="formGroup">
      <mat-expansion-panel-header>
        <mat-panel-title>
          <mat-icon>circle</mat-icon>
          &nbsp; Structure elliptique
        </mat-panel-title>
      </mat-expansion-panel-header>
      <div style="display: flex; justify-content: flex-end; margin-bottom: 10px;">
        <button mat-button color="accent" (click)="onRandomize()" type="button">
          <mat-icon>shuffle</mat-icon> Aléatoire
        </button>
      </div>
      <div style="display: flex; gap: 10px; flex-wrap: wrap;">
        <mat-form-field>
          <mat-label>Indice de Sersic</mat-label>
          <input type="number" matInput formControlName="sersicIndex" step="0.5" min="0.5" max="10">
          <mat-icon matSuffix matTooltip="Profil de Sersic (1=exponentiel, 4=de Vaucouleurs)">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Rapport d'axes</mat-label>
          <input type="number" matInput formControlName="axisRatio" step="0.05" min="0.1" max="1">
          <mat-icon matSuffix matTooltip="Ellipticité b/a (1=cercle, 0.1=très plat)">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Angle d'orientation</mat-label>
          <input type="number" matInput formControlName="orientationAngle" step="5" min="0" max="360">
          <mat-icon matSuffix matTooltip="Angle de rotation en degrés (0-360)">help_outline</mat-icon>
        </mat-form-field>
      </div>
    </mat-expansion-panel>
  `,
  styles: ``
})
export class EllipticalStructureSectionComponent {
  @Input() formGroup!: FormGroup;
  @Input() onRandomize!: () => void;
}
