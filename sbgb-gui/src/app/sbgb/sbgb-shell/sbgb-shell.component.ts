import { Component, ViewChild, AfterViewInit, ChangeDetectorRef, OnInit, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { SbgbParamComponent } from "../sbgb-param/sbgb-param.component";
import { SbgbImageComponent } from "../sbgb-image/sbgb-image.component";
import { SbgbListComponent } from "../sbgb-list/sbgb-list.component";
import { Store } from "@ngrx/store";
import { selectImageBuild, selectImageIsBuilding, selectCurrentSbgb } from "../state/sbgb.selectors";
import { ActionBarComponent, ActionBarButton } from "../../shared/components/action-bar/action-bar.component";
import { GeneratorShellComponent } from "../../shared/components/generator-shell/generator-shell.component";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTooltipModule } from "@angular/material/tooltip";
import { filter, skip } from 'rxjs/operators';

import { Sbgb } from "../sbgb.model";

@Component({
  selector: 'app-sbgb-shell',
  imports: [
    GeneratorShellComponent,
    SbgbParamComponent,
    SbgbImageComponent,
    SbgbListComponent,
    ActionBarComponent,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule
  ],
  templateUrl: './sbgb-shell.component.html',
  styleUrl: './sbgb-shell.component.scss'
})
export class SbgbShellComponent implements AfterViewInit {
  @ViewChild(GeneratorShellComponent) shell!: GeneratorShellComponent;
  @ViewChild(SbgbParamComponent) paramComponent!: SbgbParamComponent;

  hasBuiltImage = this.store.selectSignal(selectImageBuild);
  isGenerating = this.store.selectSignal(selectImageIsBuilding);

  constructor(private store: Store, private cdr: ChangeDetectorRef, private destroyRef: DestroyRef) { }

  ngAfterViewInit() {
    this.cdr.detectChanges();

    this.store.select(selectCurrentSbgb).pipe(
      skip(1),
      filter((sbgb): sbgb is Sbgb => sbgb !== null),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => this.shell.switchToGenerator());
  }

  get actionBarButtons(): ActionBarButton[] {
    const param = this.paramComponent;
    if (!param) return [];

    return [
      {
        label: 'Générer aperçu',
        color: 'primary',
        disabled: !param.canBuild(),
        tooltip: param.getBuildTooltip(),
        action: () => param.computeImage()
      },
      {
        label: 'Télécharger',
        disabled: !param.canDownload(),
        tooltip: param.getDownloadTooltip(),
        action: () => param.downloadImage()
      }
    ];
  }

  get currentNote(): number {
    return this.paramComponent?.currentNote || 0;
  }

  get starValues(): number[] {
    return this.paramComponent?.starValues || [1, 2, 3, 4, 5];
  }

  canRate(): boolean {
    return this.paramComponent?.canRate() || false;
  }

  getRatingTooltip(): string {
    return this.paramComponent?.getRatingTooltip() || '';
  }

  onNoteSelected(note: number): void {
    this.paramComponent?.onNoteSelected(note);
  }

  getSummary(): string | null {
    const param = this.paramComponent;
    return this.hasBuiltImage() && param ? param.getParametersSummary() : null;
  }

}
