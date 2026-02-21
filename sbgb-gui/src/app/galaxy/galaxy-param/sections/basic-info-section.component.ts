import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIconButton } from '@angular/material/button'; import { MatOption, MatSelect } from '@angular/material/select';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';

@Component({
  selector: 'app-basic-info-section',
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
    MatOption,
    MatIconButton
  ],
  template: `
    <mat-expansion-panel [expanded]="true" [formGroup]="formGroup">
      <mat-expansion-panel-header>
        <mat-panel-title>
          <mat-icon>info</mat-icon>
          &nbsp; Informations de base
        </mat-panel-title>
      </mat-expansion-panel-header>

      <div>
        <!-- Name -->
        <mat-form-field style="width: 100%; margin-bottom: 16px;">
          <mat-label>Nom de la galaxie</mat-label>
          <input matInput type="text" formControlName="name">
          <mat-icon matSuffix matTooltip="Nom pour identifier cette galaxie">help_outline</mat-icon>
        </mat-form-field>

        <!-- Galaxy Type -->
        <mat-form-field style="width: 100%; margin-bottom: 16px;">
          <mat-label>Type de galaxie</mat-label>
          <mat-select formControlName="galaxyType" (selectionChange)="onGalaxyTypeChange()">
            <mat-option value="SPIRAL">Spirale</mat-option>
            <mat-option value="VORONOI_CLUSTER">Amas Voronoi</mat-option>
            <mat-option value="ELLIPTICAL">Elliptique</mat-option>
            <mat-option value="RING">Anneau</mat-option>
            <mat-option value="IRREGULAR">Irrégulière</mat-option>
          </mat-select>
          <mat-icon matSuffix matTooltip="Choisissez la structure galactique">help_outline</mat-icon>
        </mat-form-field>

        <!-- Image Dimensions -->
        <div style="display: flex; gap: 10px; flex-wrap: wrap;">
          <mat-form-field style="flex: 1; min-width: 150px;">
            <mat-label>Largeur</mat-label>
            <input matInput type="number" formControlName="width">
            <mat-icon matSuffix matTooltip="Largeur en pixels">help_outline</mat-icon>
          </mat-form-field>

          <mat-form-field style="flex: 1; min-width: 150px;">
            <mat-label>Hauteur</mat-label>
            <input matInput type="number" formControlName="height">
            <mat-icon matSuffix matTooltip="Hauteur en pixels">help_outline</mat-icon>
          </mat-form-field>
        </div>

        <!-- Seed -->
        <mat-form-field style="width: 100%; margin-bottom: 16px;">
          <mat-label>Graine aléatoire</mat-label>
          <input matInput type="number" formControlName="seed">
          <button mat-icon-button matSuffix (click)="onRandomizeSeed()" matTooltip="Générer une nouvelle graine" color="accent" type="button">
            <mat-icon>shuffle</mat-icon>
          </button>
          <mat-icon matSuffix matTooltip="Graine pour la génération procédurale">help_outline</mat-icon>
        </mat-form-field>
      </div>
    </mat-expansion-panel>
  `,
  styles: ``
})
export class BasicInfoSectionComponent {
  @Input() formGroup!: FormGroup;
  @Input() onGalaxyTypeChange!: () => void;
  @Input() onRandomizeSeed!: () => void;
}
