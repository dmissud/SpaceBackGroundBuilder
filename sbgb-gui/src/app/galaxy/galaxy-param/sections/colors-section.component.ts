import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from '@angular/material/expansion';
import {MatFormField, MatLabel, MatSuffix} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';
import {MatTooltip} from '@angular/material/tooltip';
import {MatOption, MatSelect} from '@angular/material/select';

@Component({
  selector: 'app-colors-section',
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
    MatSelect,
    MatOption
  ],
  template: `
    <mat-expansion-panel>
      <mat-expansion-panel-header>
        <mat-panel-title>
          <mat-icon>palette</mat-icon>
          &nbsp; Couleurs
        </mat-panel-title>
      </mat-expansion-panel-header>

      <!-- Color Palette -->
      <div formGroupName="colorParameters">
        <mat-form-field style="width: 100%; margin-bottom: 20px;">
          <mat-label>Palette de couleurs</mat-label>
          <mat-select formControlName="colorPalette">
            <mat-option value="CLASSIC">Classic (Bleu/Blanc)</mat-option>
            <mat-option value="NEBULA">Nebula (Violet/Cyan)</mat-option>
            <mat-option value="WARM">Warm (Rouge/Orange/Jaune)</mat-option>
            <mat-option value="COLD">Cold (Bleu/Cyan)</mat-option>
            <mat-option value="INFRARED">Infrared (Rouge/Jaune)</mat-option>
            <mat-option value="EMERALD">Emerald (Vert/Cyan)</mat-option>
          </mat-select>
          <mat-icon matSuffix matTooltip="Palette de couleurs pour le rendu">help_outline</mat-icon>
        </mat-form-field>

        <!-- Legacy Colors -->
        <h4>Couleurs personnalisées (Legacy)</h4>
        <p style="color: #666; font-size: 0.9em; margin-bottom: 16px;">
          Ces paramètres sont remplacés par la palette ci-dessus
        </p>
        <div style="display: flex; gap: 10px; flex-wrap: wrap;">
          <mat-form-field>
            <mat-label>Fond spatial</mat-label>
            <input type="color" matInput formControlName="spaceBackgroundColor">
            <mat-icon matSuffix matTooltip="Couleur de fond de l'espace">help_outline</mat-icon>
          </mat-form-field>
          <mat-form-field>
            <mat-label>Couleur du noyau</mat-label>
            <input type="color" matInput formControlName="coreColor">
            <mat-icon matSuffix matTooltip="Couleur du centre lumineux">help_outline</mat-icon>
          </mat-form-field>
          <mat-form-field>
            <mat-label>Couleur des bras</mat-label>
            <input type="color" matInput formControlName="armColor">
            <mat-icon matSuffix matTooltip="Couleur des bras spiraux">help_outline</mat-icon>
          </mat-form-field>
          <mat-form-field>
            <mat-label>Couleur extérieure</mat-label>
            <input type="color" matInput formControlName="outerColor">
            <mat-icon matSuffix matTooltip="Couleur des régions externes">help_outline</mat-icon>
          </mat-form-field>
        </div>
      </div>
    </mat-expansion-panel>
  `,
  styles: ``
})
export class ColorsSectionComponent {
  @Input() formGroup!: FormGroup;
}
