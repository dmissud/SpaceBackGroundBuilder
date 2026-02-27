import {Component, OnInit, DestroyRef} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {Store} from '@ngrx/store';
import {selectBases, selectRenders} from '../state/sbgb.selectors';
import {SbgbPageActions} from '../state/sbgb.actions';
import {NoiseBaseStructureDto, NoiseCosmeticRenderDto} from '../sbgb.model';
import {SbgbHistoryListComponent} from '../sbgb-history-list/sbgb-history-list.component';
import {groupRendersByBaseId, toSbgbFromRender} from './sbgb-render.mapper';

@Component({
  selector: 'app-sbgb-list',
  standalone: true,
  imports: [SbgbHistoryListComponent],
  template: `
    <app-sbgb-history-list
      [bases]="bases"
      [rendersByBaseId]="rendersByBaseId"
      (loadRendersRequested)="onLoadRendersForBase($event)"
      (deleteRenderRequested)="onDeleteRender($event)"
      (renderSelected)="onRenderSelected($event)">
    </app-sbgb-history-list>
  `
})
export class SbgbListComponent implements OnInit {

  bases: NoiseBaseStructureDto[] = [];
  rendersByBaseId: Record<string, NoiseCosmeticRenderDto[]> = {};

  constructor(private store: Store, private destroyRef: DestroyRef) {
    this.store.select(selectBases)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(bases => { this.bases = bases; });

    this.store.select(selectRenders)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(renders => { this.rendersByBaseId = groupRendersByBaseId(renders); });
  }

  ngOnInit(): void {
    this.store.dispatch(SbgbPageActions.loadSbgbs());
  }

  /** Charge les rendus d'une base lors du déploiement de son accordéon. */
  onLoadRendersForBase(baseId: string): void {
    this.store.dispatch(SbgbPageActions.loadRendersForBase({baseId}));
  }

  /** Supprime un rendu et met à jour la bibliothèque. */
  onDeleteRender(renderId: string): void {
    this.store.dispatch(SbgbPageActions.deleteRender({renderId}));
  }

  /** Charge un rendu dans le générateur et bascule sur l'onglet Générateur. */
  onRenderSelected(render: NoiseCosmeticRenderDto): void {
    const base = this.bases.find(b => b.id === render.baseStructureId);
    if (!base) return;

    const sbgb = toSbgbFromRender(base, render);
    this.store.dispatch(SbgbPageActions.selectSbgb({sbgb}));
    this.store.dispatch(SbgbPageActions.buildSbgb({sbgb, build: true}));
    this.store.dispatch(SbgbPageActions.selectRender({renderId: render.id}));
  }
}
