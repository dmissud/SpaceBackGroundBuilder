import {Component, Inject, Optional} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef, MatDialogModule} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';

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
  standalone: true,
  imports: [MatButtonModule, MatDialogModule],
  template: `
    <h2 mat-dialog-title>⚠️ Modification structurante détectée</h2>
    <mat-dialog-content>
      <p>Il y a {{ rendersCount }} rendu{{ rendersCount > 1 ? 's' : '' }} sauvegardé{{ rendersCount > 1 ? 's' : '' }}
        pour cette galaxie.</p>
      <p>Modifier la structure invalide ces rendus. Que souhaitez-vous faire ?</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="cancel()">Annuler</button>
      <button mat-stroked-button color="warn" (click)="selectClear()">
        Vider les rendus
      </button>
      <button mat-raised-button color="primary" (click)="selectReapply()">
        Ré-appliquer sur les rendus
      </button>
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
