import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-voronoi-structure-section',
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
          <mat-icon>grain</mat-icon>
          &nbsp; Structure en clusters
        </mat-panel-title>
      </mat-expansion-panel-header>
      <div style="display: flex; justify-content: flex-end; margin-bottom: 10px;">
        <button mat-button color="accent" (click)="onRandomize()" type="button">
          <mat-icon>shuffle</mat-icon> Aléatoire
        </button>
      </div>
      <div style="display: flex; gap: 10px; flex-wrap: wrap;">
        <mat-form-field>
          <mat-label>Nombre de clusters</mat-label>
          <input type="number" matInput formControlName="clusterCount" min="5" max="500">
          <mat-icon matSuffix matTooltip="Nombre de clusters d'étoiles (5-500)">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Taille des clusters</mat-label>
          <input type="number" matInput formControlName="clusterSize" step="5" min="10">
          <mat-icon matSuffix matTooltip="Taille de chaque cluster">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Concentration</mat-label>
          <input type="number" matInput formControlName="clusterConcentration" step="0.05" min="0" max="1">
          <mat-icon matSuffix matTooltip="Concentration vers le centre (0=uniforme, 1=concentré)">help_outline</mat-icon>
        </mat-form-field>
      </div>
    </mat-expansion-panel>
  `,
  styles: ``
})
export class VoronoiStructureSectionComponent {
  @Input() expanded: boolean = false;
  @Input() formGroup!: FormGroup;
  @Input() onRandomize!: () => void;
}
