import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatSelect, MatOption } from '@angular/material/select';
import { MatIconButton } from '@angular/material/button';

@Component({
  selector: 'app-cosmetic-effects-section',
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
    MatSlideToggle,
    MatSelect,
    MatOption,
    MatIconButton
  ],
  template: `
    <mat-expansion-panel [expanded]="expanded" [formGroup]="formGroup">
      <mat-expansion-panel-header>
        <mat-panel-title>
          <mat-icon>auto_fix_high</mat-icon>
          &nbsp; Étoiles & Bloom
        </mat-panel-title>
      </mat-expansion-panel-header>

      <!-- Star Field -->
      <h4 formGroupName="starFieldParameters" style="margin-top: 10px;">Champ d'étoiles</h4>
      <div formGroupName="starFieldParameters" style="margin-bottom: 20px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
          <mat-slide-toggle formControlName="enabled">
            Activer le champ d'étoiles
          </mat-slide-toggle>

          @if (formGroup.get('starFieldParameters')?.get('enabled')?.value) {
            <button mat-icon-button color="accent" (click)="onRandomizeStarField()" type="button" matTooltip="Aléatoire (Champ d'étoiles)">
              <mat-icon>shuffle</mat-icon>
            </button>
          }
        </div>

        @if (formGroup.get('starFieldParameters')?.get('enabled')?.value) {
          <div style="display: flex; gap: 10px; flex-wrap: wrap;">
            <mat-form-field style="min-width: 180px;">
              <mat-label>Densité d'étoiles</mat-label>
              <input type="number" matInput formControlName="density" step="0.0001" min="0.0001" max="0.01">
              <mat-icon matSuffix matTooltip="Densité d'étoiles (ex: 0.001=typique, 0.01=max)">help_outline</mat-icon>
            </mat-form-field>
            <mat-form-field style="min-width: 150px;">
              <mat-label>Taille max des étoiles</mat-label>
              <input type="number" matInput formControlName="maxStarSize" min="1" max="10">
              <mat-icon matSuffix matTooltip="Taille max des étoiles en pixels (1-10)">help_outline</mat-icon>
            </mat-form-field>
            <mat-form-field style="min-width: 180px;">
              <mat-label>Pointes de diffraction</mat-label>
              <mat-select formControlName="diffractionSpikes">
                <mat-option [value]="false">Désactivées</mat-option>
                <mat-option [value]="true">Activées</mat-option>
              </mat-select>
              <mat-icon matSuffix matTooltip="Ajouter des pointes aux étoiles brillantes">help_outline</mat-icon>
            </mat-form-field>
            <mat-form-field style="min-width: 150px;">
              <mat-label>Nombre de pointes</mat-label>
              <input type="number" matInput formControlName="spikeCount" min="4" max="8" step="2">
              <mat-icon matSuffix matTooltip="Nombre de branches (4, 6, 8)">help_outline</mat-icon>
            </mat-form-field>
          </div>
        }
      </div>

      <!-- Bloom / Glow -->
      <h4 formGroupName="bloomParameters">Bloom / Halo lumineux</h4>
      <div formGroupName="bloomParameters" style="margin-bottom: 10px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
          <mat-slide-toggle formControlName="enabled">
            Activer le bloom (halo sur zones lumineuses)
          </mat-slide-toggle>
        </div>

        @if (formGroup.get('bloomParameters')?.get('enabled')?.value) {
          <div style="display: flex; gap: 10px; flex-wrap: wrap;">
            <mat-form-field style="min-width: 170px;">
              <mat-label>Rayon bloom (px)</mat-label>
              <input type="number" matInput formControlName="bloomRadius" min="1" max="50">
              <mat-icon matSuffix matTooltip="Rayon du flou gaussien en pixels (1-50)">help_outline</mat-icon>
            </mat-form-field>
            <mat-form-field style="min-width: 170px;">
              <mat-label>Intensité bloom</mat-label>
              <input type="number" matInput formControlName="bloomIntensity" step="0.05" min="0" max="1">
              <mat-icon matSuffix matTooltip="Intensité du halo (0=aucun, 1=max)">help_outline</mat-icon>
            </mat-form-field>
            <mat-form-field style="min-width: 170px;">
              <mat-label>Seuil bloom</mat-label>
              <input type="number" matInput formControlName="bloomThreshold" step="0.05" min="0" max="1">
              <mat-icon matSuffix matTooltip="Luminosité minimale pour déclencher le bloom (0.3-0.7 recommandé)">help_outline</mat-icon>
            </mat-form-field>
          </div>
        }
      </div>
    </mat-expansion-panel>
  `,
  styles: ``
})
export class CosmeticEffectsSectionComponent {
  @Input() expanded: boolean = false;
  @Input() formGroup!: FormGroup;
  @Input() onRandomizeStarField!: () => void;
}
