import {Component, Inject, Optional} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';

export enum GalaxyStructuralChangeChoice {
  NEW_BASE = 'NEW_BASE',
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
      <ul style="font-size:0.9em; color: #555; padding-left: 1.2em;">
        <li><strong>A</strong> — Crée une nouvelle Structure de base (conserve les rendus existants)</li>
        <li><strong>B</strong> — Supprime tous les rendus et repart de zéro</li>
        <li><strong>C</strong> — Ré-applique les cosmétiques existants à la nouvelle structure</li>
      </ul>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button mat-stroked-button color="primary" (click)="selectNewBase()">
        A — Nouvelle Structure de base
      </button>
      <button mat-stroked-button (click)="selectClear()">
        B — Vider les rendus
      </button>
      <button mat-stroked-button (click)="selectReapply()">
        C — Ré-appliquer les cosmétiques
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

  selectNewBase(): void {
    this.dialogRef.close(GalaxyStructuralChangeChoice.NEW_BASE);
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
