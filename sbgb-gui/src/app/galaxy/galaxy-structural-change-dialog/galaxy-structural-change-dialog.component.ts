import {Component, Inject, Optional} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {MatDialogModule} from '@angular/material/dialog';

export enum GalaxyStructuralChangeChoice {
  CLEAR = 'CLEAR',
  REAPPLY = 'REAPPLY',
  CANCEL = 'CANCEL'
}

export interface GalaxyStructuralChangeDialogData {
  rendersCount: number;
}

@Component({
  selector: 'app-galaxy-structural-change-dialog',
  imports: [MatButtonModule, MatDialogModule],
  template: `
    <h2 mat-dialog-title>⚠️ Modification structurante détectée</h2>
    <mat-dialog-content>
      <p>{{ rendersCount }} rendu{{ rendersCount > 1 ? 's' : '' }} sauvegardé{{ rendersCount > 1 ? 's' : '' }}
        existe{{ rendersCount > 1 ? 'nt' : '' }} pour ce Modèle de Base.</p>
      <p>Que souhaitez-vous faire ?</p>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button mat-stroked-button (click)="selectClear()">
        A — Vider les rendus
      </button>
      <button mat-stroked-button (click)="selectReapply()">
        B — Ré-appliquer les cosmétiques
      </button>
      <button mat-button (click)="cancel()">Annuler</button>
    </mat-dialog-actions>
  `
})
export class GalaxyStructuralChangeDialogComponent {
  rendersCount: number;

  constructor(
    private dialogRef: MatDialogRef<GalaxyStructuralChangeDialogComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) data?: GalaxyStructuralChangeDialogData
  ) {
    this.rendersCount = data?.rendersCount ?? 0;
  }

  selectClear(): void {
    this.dialogRef.close(GalaxyStructuralChangeChoice.CLEAR);
  }

  selectReapply(): void {
    this.dialogRef.close(GalaxyStructuralChangeChoice.REAPPLY);
  }

  cancel(): void {
    this.dialogRef.close(GalaxyStructuralChangeChoice.CANCEL);
  }
}
