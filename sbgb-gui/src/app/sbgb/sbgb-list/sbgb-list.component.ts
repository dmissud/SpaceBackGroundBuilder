import {Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {selectBases, selectRenders} from '../state/sbgb.selectors';
import {SbgbPageActions} from '../state/sbgb.actions';
import {NoiseBaseStructureDto, NoiseCosmeticRenderDto, Sbgb} from '../sbgb.model';
import {SbgbHistoryListComponent} from '../sbgb-history-list/sbgb-history-list.component';

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

  constructor(private store: Store) {
    this.store.select(selectBases).subscribe(bases => {
      this.bases = bases;
    });

    this.store.select(selectRenders).subscribe(renders => {
      this.rendersByBaseId = this.groupByBaseId(renders);
    });
  }

  ngOnInit(): void {
    this.store.dispatch(SbgbPageActions.loadSbgbs());
  }

  onLoadRendersForBase(baseId: string): void {
    this.store.dispatch(SbgbPageActions.loadRendersForBase({baseId}));
  }

  onDeleteRender(renderId: string): void {
    this.store.dispatch(SbgbPageActions.deleteRender({renderId}));
  }

  onRenderSelected(render: NoiseCosmeticRenderDto): void {
    const base = this.bases.find(b => b.id === render.baseStructureId);
    if (!base) return;

    const sbgb = this.toSbgbFromRender(base, render);
    this.store.dispatch(SbgbPageActions.selectSbgb({sbgb}));
    this.store.dispatch(SbgbPageActions.buildSbgb({sbgb, build: true}));
  }

  private groupByBaseId(renders: NoiseCosmeticRenderDto[]): Record<string, NoiseCosmeticRenderDto[]> {
    return renders.reduce((acc, render) => {
      const baseId = render.baseStructureId;
      return {...acc, [baseId]: [...(acc[baseId] ?? []), render]};
    }, {} as Record<string, NoiseCosmeticRenderDto[]>);
  }

  private toSbgbFromRender(base: NoiseBaseStructureDto, render: NoiseCosmeticRenderDto): Sbgb {
    return {
      id: base.id,
      description: base.description,
      note: render.note,
      imageStructure: {
        width: base.width,
        height: base.height,
        seed: base.seed,
        octaves: base.octaves,
        persistence: base.persistence,
        lacunarity: base.lacunarity,
        scale: base.scale,
        noiseType: base.noiseType,
        preset: 'CUSTOM',
        useMultiLayer: base.useMultiLayer
      },
      imageColor: {
        back: render.back,
        middle: render.middle,
        fore: render.fore,
        backThreshold: render.backThreshold,
        middleThreshold: render.middleThreshold,
        interpolationType: render.interpolationType,
        transparentBackground: render.transparentBackground
      }
    };
  }
}
