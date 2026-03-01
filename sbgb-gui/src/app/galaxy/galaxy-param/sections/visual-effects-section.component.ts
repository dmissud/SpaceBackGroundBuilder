import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MatSlideToggle } from '@angular/material/slide-toggle';

@Component({
  selector: 'app-visual-effects-section',
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
          <mat-icon>auto_awesome</mat-icon>
          &nbsp; Effets visuels avancés
        </mat-panel-title>
      </mat-expansion-panel-header>

      <!-- Domain Warping -->
      <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 10px;">
        <h4 style="margin: 0;">Déformation spatiale</h4>
        <button mat-icon-button color="accent" (click)="onRandomizeWarping()" type="button" matTooltip="Aléatoire (Déformation)">
          <mat-icon>shuffle</mat-icon>
        </button>
      </div>
      <mat-form-field style="width: 100%; margin-top: 10px; margin-bottom: 20px;">
        <mat-label>Force de warping</mat-label>
        <input type="number" matInput formControlName="warpStrength" step="10" min="0" max="300">
        <mat-icon matSuffix matTooltip="Déformation spatiale créant des filaments organiques (0=aucun, 50-200=filamentaire)">help_outline</mat-icon>
      </mat-form-field>

      <!-- Multi-Layer Noise -->
      <h4 formGroupName="multiLayerNoiseParameters">Bruit multi-couches</h4>
      <div formGroupName="multiLayerNoiseParameters" style="margin-bottom: 20px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
          <mat-slide-toggle formControlName="enabled">
            Activer le bruit multi-échelles (Macro + Meso + Micro)
          </mat-slide-toggle>
          
          @if (formGroup.get('multiLayerNoiseParameters')?.get('enabled')?.value) {
            <button mat-icon-button color="accent" (click)="onRandomizeMultiLayerNoise()" type="button" matTooltip="Aléatoire (Bruit multi-couches)">
              <mat-icon>shuffle</mat-icon>
            </button>
          }
        </div>

        @if (formGroup.get('multiLayerNoiseParameters')?.get('enabled')?.value) {
          <div style="display: flex; gap: 10px; flex-wrap: wrap; margin-top: 16px;">
            <mat-form-field style="min-width: 150px;">
              <mat-label>Échelle Macro</mat-label>
              <input type="number" matInput formControlName="macroLayerScale" step="0.1" min="0.1" max="5">
              <mat-icon matSuffix matTooltip="Structures larges (défaut 0.3)">help_outline</mat-icon>
            </mat-form-field>
            <mat-form-field style="min-width: 150px;">
              <mat-label>Poids Macro</mat-label>
              <input type="number" matInput formControlName="macroLayerWeight" step="0.05" min="0" max="1">
              <mat-icon matSuffix matTooltip="Poids de la couche macro (défaut 0.5)">help_outline</mat-icon>
            </mat-form-field>
            <mat-form-field style="min-width: 150px;">
              <mat-label>Échelle Meso</mat-label>
              <input type="number" matInput formControlName="mesoLayerScale" step="0.1" min="0.1" max="5">
              <mat-icon matSuffix matTooltip="Détails moyens (défaut 1.0)">help_outline</mat-icon>
            </mat-form-field>
            <mat-form-field style="min-width: 150px;">
              <mat-label>Poids Meso</mat-label>
              <input type="number" matInput formControlName="mesoLayerWeight" step="0.05" min="0" max="1">
              <mat-icon matSuffix matTooltip="Poids de la couche meso (défaut 0.35)">help_outline</mat-icon>
            </mat-form-field>
            <mat-form-field style="min-width: 150px;">
              <mat-label>Échelle Micro</mat-label>
              <input type="number" matInput formControlName="microLayerScale" step="0.1" min="0.1" max="10">
              <mat-icon matSuffix matTooltip="Grain fin (défaut 3.0)">help_outline</mat-icon>
            </mat-form-field>
            <mat-form-field style="min-width: 150px;">
              <mat-label>Poids Micro</mat-label>
              <input type="number" matInput formControlName="microLayerWeight" step="0.05" min="0" max="1">
              <mat-icon matSuffix matTooltip="Poids de la couche micro (défaut 0.15)">help_outline</mat-icon>
            </mat-form-field>
          </div>
        }
      </div>

    </mat-expansion-panel>
  `,
  styles: ``
})
export class VisualEffectsSectionComponent {
  @Input() expanded: boolean = false;
  @Input() formGroup!: FormGroup;
  @Input() onRandomizeWarping!: () => void;
  @Input() onRandomizeMultiLayerNoise!: () => void;
}
