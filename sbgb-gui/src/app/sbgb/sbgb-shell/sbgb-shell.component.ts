import { Component, ViewChild, AfterViewInit, ChangeDetectorRef, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { SbgbParamComponent } from "../sbgb-param/sbgb-param.component";
import { SbgbImageComponent } from "../sbgb-image/sbgb-image.component";
import { SbgbListComponent } from "../sbgb-list/sbgb-list.component";
import { Store } from "@ngrx/store";
import { selectImageBuild, selectImageIsBuilding, selectCurrentSbgb, selectRenders, selectSelectedRenderId } from "../state/sbgb.selectors";
import { NoiseCosmeticRenderDto, Sbgb } from "../sbgb.model";
import { STAR_RATING_VALUES } from "../sbgb.constants";
import { SbgbPageActions } from "../state/sbgb.actions";
import { ActionBarComponent, ActionBarButton } from "../../shared/components/action-bar/action-bar.component";
import { GeneratorShellComponent } from "../../shared/components/generator-shell/generator-shell.component";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTooltipModule } from "@angular/material/tooltip";
import { filter, skip } from 'rxjs/operators';

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
  renders = this.store.selectSignal(selectRenders);
  selectedRenderId = this.store.selectSignal(selectSelectedRenderId);

  constructor(private store: Store, private cdr: ChangeDetectorRef, private destroyRef: DestroyRef) { }

  /** Bascule automatiquement sur l'onglet Générateur quand un Sbgb est sélectionné depuis la bibliothèque. */
  ngAfterViewInit() {
    this.cdr.detectChanges();

    this.store.select(selectCurrentSbgb).pipe(
      skip(1),
      filter((sbgb): sbgb is Sbgb => sbgb !== null),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => this.shell.switchToGenerator());
  }

  /** Construit les boutons de la barre d'actions en déléguant les états et actions au composant paramètre. */
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

  get starValues(): readonly number[] {
    return this.paramComponent?.starValues || STAR_RATING_VALUES;
  }

  /** Indique si une génération peut être lancée (paramètres valides et modifiés depuis le dernier build). */
  canBuild(): boolean {
    return this.paramComponent?.canBuild() || false;
  }

  /** Indique si la notation est disponible (image générée et non modifiée depuis). */
  canRate(): boolean {
    return this.paramComponent?.canRate() || false;
  }

  /** Retourne le tooltip contextuel pour le composant de notation. */
  getRatingTooltip(): string {
    return this.paramComponent?.getRatingTooltip() || '';
  }

  /** Délègue la sélection d'une note au composant paramètre. */
  onNoteSelected(note: number): void {
    this.paramComponent?.onNoteSelected(note);
  }

  /** Sélectionne un rendu et demande au composant paramètre de charger ses cosmétiques via le store. */
  onSelectRender(render: NoiseCosmeticRenderDto): void {
    this.store.dispatch(SbgbPageActions.selectRender({renderId: render.id}));
    this.store.dispatch(SbgbPageActions.applyRenderCosmetics({render}));
  }

  /** Supprime un rendu via le store NgRx. */
  onDeleteRender(renderId: string): void {
    this.store.dispatch(SbgbPageActions.deleteRender({renderId}));
  }

  getSummary(): string | null {
    const param = this.paramComponent;
    return this.hasBuiltImage() && param ? param.getParametersSummary() : null;
  }

}
