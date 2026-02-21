import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-spiral-structure-section',
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
          <mat-icon>cyclone</mat-icon>
          &nbsp; Structure spirale
        </mat-panel-title>
      </mat-expansion-panel-header>
      <div style="display: flex; justify-content: flex-end; margin-bottom: 10px;">
        <button mat-button color="accent" (click)="onRandomize()" type="button">
          <mat-icon>shuffle</mat-icon> Aléatoire
        </button>
      </div>
      <div style="display: flex; gap: 10px; flex-wrap: wrap;">
        <mat-form-field>
          <mat-label>Nombre de bras</mat-label>
          <input type="number" matInput formControlName="numberOfArms" min="1" max="6">
          <mat-icon matSuffix matTooltip="Nombre de bras spiraux (1-6)">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Largeur des bras</mat-label>
          <input type="number" matInput formControlName="armWidth" step="10" min="10">
          <mat-icon matSuffix matTooltip="Largeur des bras spiraux">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Rotation des bras</mat-label>
          <input type="number" matInput formControlName="armRotation" step="0.5" min="1">
          <mat-icon matSuffix matTooltip="Enroulement de la spirale">help_outline</mat-icon>
        </mat-form-field>
      </div>
    </mat-expansion-panel>
  `,
  styles: ``
})
export class SpiralStructureSectionComponent {
  @Input() formGroup!: FormGroup;
  @Input() onRandomize!: () => void;
}
