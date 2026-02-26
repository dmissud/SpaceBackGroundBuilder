import {SbgbListComponent} from './sbgb-list.component';
import {NoiseBaseStructureDto, NoiseCosmeticRenderDto} from '../sbgb.model';
import {of, Subject} from 'rxjs';
import {DestroyRef} from '@angular/core';

const BASE_1: NoiseBaseStructureDto = {
  id: 'base-1', description: 'FBM 3oct', maxNote: 4,
  width: 1920, height: 1080, seed: 42,
  octaves: 3, persistence: 0.5, lacunarity: 2.0, scale: 1.0,
  noiseType: 'FBM', useMultiLayer: false
};

const RENDER_1: NoiseCosmeticRenderDto = {
  id: 'render-1', baseStructureId: 'base-1', description: 'Violet', note: 4,
  back: '#6b2d8b', middle: '#ff9500', fore: '#ffffff',
  backThreshold: 0.3, middleThreshold: 0.7,
  interpolationType: 'LINEAR', transparentBackground: false, thumbnail: null
};

function buildMocks(bases = [BASE_1], renders = [RENDER_1]) {
  const dispatched: any[] = [];
  const destroyRef = {onDestroy: (_fn: () => void) => {}} as DestroyRef;
  const store = {
    select: (selector: any) => {
      if (selector?.projector?.toString().includes('bases')) return of(bases);
      return of(renders);
    },
    dispatch: (action: any) => dispatched.push(action)
  };
  return {store, destroyRef, dispatched};
}

describe('SbgbListComponent', () => {
  describe('onLoadRendersForBase', () => {
    it('should dispatch LoadRendersForBase action', () => {
      const {store, destroyRef, dispatched} = buildMocks();
      const component = new SbgbListComponent(store as any, destroyRef);

      component.onLoadRendersForBase('base-1');

      expect(dispatched.find((a: any) => a.baseId === 'base-1')).toBeTruthy();
    });
  });

  describe('onDeleteRender', () => {
    it('should dispatch DeleteRender action', () => {
      const {store, destroyRef, dispatched} = buildMocks();
      const component = new SbgbListComponent(store as any, destroyRef);

      component.onDeleteRender('render-1');

      expect(dispatched.find((a: any) => a.renderId === 'render-1')).toBeTruthy();
    });
  });

  describe('onRenderSelected', () => {
    it('should dispatch selectSbgb with cosmetic params from render', () => {
      const {store, destroyRef, dispatched} = buildMocks();
      const component = new SbgbListComponent(store as any, destroyRef);
      component.bases = [BASE_1];

      component.onRenderSelected(RENDER_1);

      const action = dispatched.find((a: any) => a.sbgb?.imageColor?.back === '#6b2d8b');
      expect(action).toBeTruthy();
      expect(action.sbgb.imageColor.middle).toBe('#ff9500');
    });

    it('should not dispatch when base not found', () => {
      const {store, destroyRef, dispatched} = buildMocks([], []);
      const component = new SbgbListComponent(store as any, destroyRef);
      component.bases = [];

      component.onRenderSelected(RENDER_1);

      expect(dispatched.length).toBe(0);
    });
  });
});
