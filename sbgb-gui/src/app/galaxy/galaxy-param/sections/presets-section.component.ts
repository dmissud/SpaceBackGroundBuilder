import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from '@angular/material/expansion';
import {MatIcon} from '@angular/material/icon';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-presets-section',
  standalone: true,
  imports: [
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatIcon,
    MatButton
  ],
  template: `
    <mat-expansion-panel>
      <mat-expansion-panel-header>
        <mat-panel-title>
          <mat-icon>tune</mat-icon>
          &nbsp; Préconfigurations & Randomisation
        </mat-panel-title>
      </mat-expansion-panel-header>

      <!-- Presets -->
      <h4 style="margin-top: 0;">Préconfigurations</h4>
      @if (formGroup.controls['galaxyType'].value === 'SPIRAL') {
        <div style="display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 20px;">
          <button mat-raised-button (click)="onLoadPreset('CLASSIC')">Classic</button>
          <button mat-raised-button (click)="onLoadPreset('BARRED')">Barred</button>
          <button mat-raised-button (click)="onLoadPreset('MULTI_ARM')">Multi-Arm</button>
          <button mat-raised-button (click)="onLoadPreset('SPIRAL_GRAND_DESIGN')">Grand Design</button>
          <button mat-raised-button (click)="onLoadPreset('SPIRAL_FLOCCULENT')">Flocculent</button>
          <button mat-raised-button (click)="onLoadPreset('SPIRAL_TIGHTLY_WOUND')">Tightly Wound</button>
        </div>
      }
      @if (formGroup.controls['galaxyType'].value === 'VORONOI_CLUSTER') {
        <div style="display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 20px;">
          <button mat-raised-button (click)="onLoadPreset('VORONOI_DEFAULT')">Default</button>
          <button mat-raised-button (click)="onLoadPreset('VORONOI_DENSE')">Dense</button>
          <button mat-raised-button (click)="onLoadPreset('VORONOI_SPARSE')">Sparse</button>
          <button mat-raised-button (click)="onLoadPreset('VORONOI_GLOBULAR')">Globular</button>
        </div>
      }
      @if (formGroup.controls['galaxyType'].value === 'ELLIPTICAL') {
        <div style="display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 20px;">
          <button mat-raised-button (click)="onLoadPreset('ELLIPTICAL_DEFAULT')">E3 Classic</button>
          <button mat-raised-button (click)="onLoadPreset('ELLIPTICAL_ROUND')">E0 Round</button>
          <button mat-raised-button (click)="onLoadPreset('ELLIPTICAL_FLAT')">E6 Flat</button>
          <button mat-raised-button (click)="onLoadPreset('ELLIPTICAL_GIANT')">Giant</button>
        </div>
      }
      @if (formGroup.controls['galaxyType'].value === 'RING') {
        <div style="display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 20px;">
          <button mat-raised-button (click)="onLoadPreset('RING_DEFAULT')">Classic</button>
          <button mat-raised-button (click)="onLoadPreset('RING_WIDE')">Wide</button>
          <button mat-raised-button (click)="onLoadPreset('RING_BRIGHT')">Bright</button>
          <button mat-raised-button (click)="onLoadPreset('RING_THIN')">Thin</button>
          <button mat-raised-button (click)="onLoadPreset('RING_DOUBLE')">Double</button>
        </div>
      }
      @if (formGroup.controls['galaxyType'].value === 'IRREGULAR') {
        <div style="display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 20px;">
          <button mat-raised-button (click)="onLoadPreset('IRREGULAR_DEFAULT')">SMC Default</button>
          <button mat-raised-button (click)="onLoadPreset('IRREGULAR_CHAOTIC')">Chaotic</button>
          <button mat-raised-button (click)="onLoadPreset('IRREGULAR_DWARF')">Dwarf</button>
        </div>
      }

      <!-- Randomization -->
      <h4>Randomisation</h4>
      <div style="display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 16px;">
        <button mat-raised-button color="accent" (click)="onRandomizeStructure()">
          Randomiser la structure
        </button>
        <button mat-raised-button color="accent" (click)="onRandomizeColors()">
          Randomiser les couleurs
        </button>
        <button mat-raised-button color="accent" (click)="onRandomizeAll()">
          Tout randomiser
        </button>
      </div>
    </mat-expansion-panel>
  `,
  styles: ``
})
export class PresetsSectionComponent {
  @Input() formGroup!: FormGroup;
  @Input() onLoadPreset!: (preset: string) => void;
  @Input() onRandomizeStructure!: () => void;
  @Input() onRandomizeColors!: () => void;
  @Input() onRandomizeAll!: () => void;
}
