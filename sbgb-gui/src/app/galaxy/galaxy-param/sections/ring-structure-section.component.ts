import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-ring-structure-section',
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
    <mat-expansion-panel [expanded]="expanded" [formGroup]="formGroup">
      <mat-expansion-panel-header>
        <mat-panel-title>
          <mat-icon>album</mat-icon>
          &nbsp; Structure en anneau
        </mat-panel-title>
      </mat-expansion-panel-header>
      <div style="display: flex; justify-content: flex-end; margin-bottom: 10px;">
        <button mat-button color="accent" (click)="onRandomize()" type="button">
          <mat-icon>shuffle</mat-icon> Aléatoire
        </button>
      </div>
      <div style="display: flex; gap: 10px; flex-wrap: wrap;">
        <mat-form-field>
          <mat-label>Rayon de l'anneau</mat-label>
          <input type="number" matInput formControlName="ringRadius" step="50" min="50">
          <mat-icon matSuffix matTooltip="Distance du centre à l'anneau (pixels)">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Largeur de l'anneau</mat-label>
          <input type="number" matInput formControlName="ringWidth" step="10" min="10">
          <mat-icon matSuffix matTooltip="Épaisseur de l'anneau (pixels)">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Intensité de l'anneau</mat-label>
          <input type="number" matInput formControlName="ringIntensity" step="0.1" min="0.1" max="2">
          <mat-icon matSuffix matTooltip="Luminosité de l'anneau (0.1-2.0)">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Ratio noyau/anneau</mat-label>
          <input type="number" matInput formControlName="coreToRingRatio" step="0.05" min="0" max="1">
          <mat-icon matSuffix matTooltip="Luminosité du noyau relative à l'anneau (0-1)">help_outline</mat-icon>
        </mat-form-field>
      </div>
    </mat-expansion-panel>
  `,
  styles: ``
})
export class RingStructureSectionComponent {
  @Input() expanded: boolean = false;
  @Input() formGroup!: FormGroup;
  @Input() onRandomize!: () => void;
}
