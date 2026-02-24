import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-noise-texture-section',
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
          <mat-icon>texture</mat-icon>
          &nbsp; Texture de bruit
        </mat-panel-title>
      </mat-expansion-panel-header>
      <div style="display: flex; justify-content: flex-end; margin-bottom: 10px;">
        <button mat-button color="accent" (click)="onRandomize()" type="button">
          <mat-icon>shuffle</mat-icon> Aléatoire
        </button>
      </div>
      <div style="display: flex; gap: 10px; flex-wrap: wrap;">
        <mat-form-field>
          <mat-label>Octaves</mat-label>
          <input type="number" matInput formControlName="octaves" min="1" max="10">
          <mat-icon matSuffix matTooltip="Couches de détails (plus = plus de détails)">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Persistence</mat-label>
          <input type="number" matInput formControlName="persistence" step="0.05" min="0" max="1">
          <mat-icon matSuffix matTooltip="Contribution des détails (0-1)">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Lacunarity</mat-label>
          <input type="number" matInput formControlName="lacunarity" step="0.1" min="1">
          <mat-icon matSuffix matTooltip="Multiplicateur de fréquence">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Échelle</mat-label>
          <input type="number" matInput formControlName="scale" step="10" min="50">
          <mat-icon matSuffix matTooltip="Échelle de la texture">help_outline</mat-icon>
        </mat-form-field>
      </div>
    </mat-expansion-panel>
  `,
  styles: ``
})
export class NoiseTextureSectionComponent {
  @Input() formGroup!: FormGroup;
  @Input() onRandomize!: () => void;
}
