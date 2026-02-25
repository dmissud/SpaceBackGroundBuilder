import {Component, Inject, Optional} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {MatDialogModule} from '@angular/material/dialog';

export enum StructuralChangeChoice {
  CLEAR = 'CLEAR',
  REAPPLY = 'REAPPLY',
  CANCEL = 'CANCEL'
}

export interface StructuralChangeDialogData {
  rendersCount: number;
}

@Component({
  selector: 'app-sbgb-structural-change-dialog',
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
export class SbgbStructuralChangeDialogComponent {
  rendersCount: number;

  constructor(
    private dialogRef: MatDialogRef<SbgbStructuralChangeDialogComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) data?: StructuralChangeDialogData
  ) {
    this.rendersCount = data?.rendersCount ?? 0;
  }

  selectClear(): void {
    this.dialogRef.close(StructuralChangeChoice.CLEAR);
  }

  selectReapply(): void {
    this.dialogRef.close(StructuralChangeChoice.REAPPLY);
  }

  cancel(): void {
    this.dialogRef.close(StructuralChangeChoice.CANCEL);
  }
}
