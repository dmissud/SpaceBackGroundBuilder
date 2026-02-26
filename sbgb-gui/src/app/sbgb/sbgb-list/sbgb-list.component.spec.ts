import {SbgbListComponent} from './sbgb-list.component';
import {NoiseBaseStructureDto, NoiseCosmeticRenderDto} from '../sbgb.model';
import {of} from 'rxjs';
import {SbgbPageActions} from '../state/sbgb.actions';

const BASE_1: NoiseBaseStructureDto = {
  id: 'base-1',
  description: 'FBM 3oct (1920×1080, seed 42)',
  maxNote: 4,
  width: 1920, height: 1080, seed: 42,
  octaves: 3, persistence: 0.5, lacunarity: 2.0, scale: 1.0,
  noiseType: 'FBM', useMultiLayer: false
};

const RENDER_1: NoiseCosmeticRenderDto = {
  id: 'render-1', baseStructureId: 'base-1',
  description: 'Violet → Orange', note: 4,
  back: '#6b2d8b', middle: '#ff9500', fore: '#ffffff',
  backThreshold: 0.3, middleThreshold: 0.7,
  interpolationType: 'LINEAR', transparentBackground: false,
  thumbnail: 'abc123'
};

function buildStoreMock(bases: NoiseBaseStructureDto[] = [], renders: NoiseCosmeticRenderDto[] = []) {
  const dispatched: any[] = [];
  return {
    select: (selector: any) => {
      if (selector.projector) {
        const name = selector.projector.toString();
        if (name.includes('bases')) return of(bases);
      }
      return of(renders);
    },
    dispatch: (action: any) => dispatched.push(action),
    _dispatched: dispatched
  };
}

describe('SbgbListComponent', () => {
  let store: ReturnType<typeof buildStoreMock>;
  let component: SbgbListComponent;

  beforeEach(() => {
    store = buildStoreMock([BASE_1], [RENDER_1]);
    component = new SbgbListComponent(store as any);
  });

  describe('onLoadRendersForBase', () => {
    it('should dispatch LoadRendersForBase action', () => {
      component.onLoadRendersForBase('base-1');

      const action = store._dispatched.find((a: any) => a.baseId === 'base-1');
      expect(action).toBeTruthy();
    });
  });

  describe('onDeleteRender', () => {
    it('should dispatch DeleteRender action', () => {
      component.onDeleteRender('render-1');

      const action = store._dispatched.find((a: any) => a.renderId === 'render-1');
      expect(action).toBeTruthy();
    });
  });

  describe('onRenderSelected', () => {
    it('should dispatch selectSbgb with cosmetic params from render', () => {
      component.bases = [BASE_1];

      component.onRenderSelected(RENDER_1);

      const selectAction = store._dispatched.find((a: any) => a.sbgb?.imageColor?.back === '#6b2d8b');
      expect(selectAction).toBeTruthy();
      expect(selectAction.sbgb.imageColor.middle).toBe('#ff9500');
    });

    it('should not dispatch when base is not found', () => {
      component.bases = [];
      const initialCount = store._dispatched.length;

      component.onRenderSelected(RENDER_1);

      expect(store._dispatched.length).toBe(initialCount);
    });
  });

  describe('rendersByBaseId grouping', () => {
    it('should group renders by baseStructureId', () => {
      const renders = [RENDER_1, {...RENDER_1, id: 'render-2', baseStructureId: 'base-1'}];
      store = buildStoreMock([BASE_1], renders);
      component = new SbgbListComponent(store as any);

      expect(component.rendersByBaseId['base-1'].length).toBe(2);
    });
  });
});
