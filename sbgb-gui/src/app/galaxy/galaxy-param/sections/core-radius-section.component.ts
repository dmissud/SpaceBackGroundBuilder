import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from '@angular/material/expansion';
import {MatFormField, MatLabel, MatSuffix} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-core-radius-section',
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
    MatSuffix
  ],
  template: `
    <mat-expansion-panel>
      <mat-expansion-panel-header>
        <mat-panel-title>
          <mat-icon>brightness_high</mat-icon>
          &nbsp; Noyau et rayon
        </mat-panel-title>
      </mat-expansion-panel-header>
      <div [formGroup]="formGroup" style="display: flex; gap: 10px; flex-wrap: wrap;">
        <mat-form-field style="flex: 1;">
          <mat-label>Taille du noyau</mat-label>
          <input type="number" matInput formControlName="coreSize" step="0.01" min="0.01" max="0.3">
          <mat-icon matSuffix matTooltip="Taille du noyau lumineux (0.01-0.3)">help_outline</mat-icon>
        </mat-form-field>
        <mat-form-field style="flex: 1;">
          <mat-label>Rayon de la galaxie</mat-label>
          <input type="number" matInput formControlName="galaxyRadius" step="100" min="500">
          <mat-icon matSuffix matTooltip="Rayon total de la galaxie en pixels">help_outline</mat-icon>
        </mat-form-field>
      </div>
    </mat-expansion-panel>
  `,
  styles: ``
})
export class CoreRadiusSectionComponent {
  @Input() formGroup!: FormGroup;
}
