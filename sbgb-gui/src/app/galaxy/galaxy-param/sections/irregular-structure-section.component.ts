import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-irregular-structure-section',
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
          <mat-icon>scatter_plot</mat-icon>
          &nbsp; Structure irrégulière
        </mat-panel-title>
      </mat-expansion-panel-header>
      <div style="display: flex; justify-content: flex-end; margin-bottom: 10px;">
        <button mat-button color="accent" (click)="onRandomize()" type="button">
          <mat-icon>shuffle</mat-icon> Aléatoire
        </button>
      </div>
      <div style="display: flex; gap: 10px; flex-wrap: wrap;">
        <mat-form-field>
          <mat-label>Irrégularité</mat-label>
          <input type="number" matInput formControlName="irregularity" step="0.05" min="0" max="1">
          <mat-icon matSuffix matTooltip="Degré de chaos (0=lisse, 1=très chaotique)">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Nombre d'amas</mat-label>
          <input type="number" matInput formControlName="irregularClumpCount" step="1" min="5" max="50">
          <mat-icon matSuffix matTooltip="Nombre d'amas de formation d'étoiles (5-50)">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Taille des amas</mat-label>
          <input type="number" matInput formControlName="irregularClumpSize" step="10" min="20">
          <mat-icon matSuffix matTooltip="Taille de chaque amas en pixels (min 20)">help_outline</mat-icon>
        </mat-form-field>
      </div>
    </mat-expansion-panel>
  `,
  styles: ``
})
export class IrregularStructureSectionComponent {
  @Input() formGroup!: FormGroup;
  @Input() onRandomize!: () => void;
}
