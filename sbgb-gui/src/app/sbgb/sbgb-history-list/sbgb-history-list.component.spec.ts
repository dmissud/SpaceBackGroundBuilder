import {SbgbHistoryListComponent} from './sbgb-history-list.component';
import {NoiseBaseStructureDto, NoiseCosmeticRenderDto} from '../sbgb.model';

const BASE_1: NoiseBaseStructureDto = {
  id: 'base-1',
  description: 'FBM 3oct (1920×1080, seed 42)',
  maxNote: 4,
  width: 1920, height: 1080, seed: 42,
  octaves: 3, persistence: 0.5, lacunarity: 2.0, scale: 1.0,
  noiseType: 'FBM', useMultiLayer: false
};

const BASE_2: NoiseBaseStructureDto = {
  id: 'base-2',
  description: 'Perlin 5oct (1280×720, seed 7)',
  maxNote: 3,
  width: 1280, height: 720, seed: 7,
  octaves: 5, persistence: 0.6, lacunarity: 2.0, scale: 1.0,
  noiseType: 'PERLIN', useMultiLayer: false
};

const RENDER_1: NoiseCosmeticRenderDto = {
  id: 'render-1', baseStructureId: 'base-1',
  description: 'Violet → Orange → Blanc', note: 4,
  back: '#6b2d8b', middle: '#ff9500', fore: '#ffffff',
  backThreshold: 0.3, middleThreshold: 0.7,
  interpolationType: 'LINEAR', transparentBackground: false,
  thumbnail: null
};

const RENDER_2: NoiseCosmeticRenderDto = {
  id: 'render-2', baseStructureId: 'base-1',
  description: 'Bleu → Cyan → Blanc', note: 3,
  back: '#001080', middle: '#00bcd4', fore: '#ffffff',
  backThreshold: 0.3, middleThreshold: 0.7,
  interpolationType: 'LINEAR', transparentBackground: false,
  thumbnail: null
};

describe('SbgbHistoryListComponent', () => {
  let component: SbgbHistoryListComponent;

  beforeEach(() => {
    component = new SbgbHistoryListComponent();
  });

  describe('visibleBases', () => {
    it('should return all bases when renders are not yet loaded', () => {
      component.bases = [BASE_1, BASE_2];
      component.rendersByBaseId = {};

      expect(component.visibleBases.length).toBe(2);
    });

    it('should exclude base with empty renders array', () => {
      component.bases = [BASE_1, BASE_2];
      component.rendersByBaseId = {'base-1': [], 'base-2': [RENDER_2]};

      expect(component.visibleBases.map(b => b.id)).toEqual(['base-2']);
    });

    it('should return empty array when no bases', () => {
      component.bases = [];
      component.rendersByBaseId = {};

      expect(component.visibleBases).toEqual([]);
    });
  });

  describe('rendersFor', () => {
    it('should return renders for known baseId', () => {
      component.rendersByBaseId = {'base-1': [RENDER_1, RENDER_2]};

      expect(component.rendersFor('base-1')).toEqual([RENDER_1, RENDER_2]);
    });

    it('should return empty array for unknown baseId', () => {
      component.rendersByBaseId = {};

      expect(component.rendersFor('unknown')).toEqual([]);
    });
  });

  describe('isFilled', () => {
    it('should return true when star is within note', () => {
      expect(component.isFilled(3, 4)).toBe(true);
    });

    it('should return false when star exceeds note', () => {
      expect(component.isFilled(5, 4)).toBe(false);
    });
  });

  describe('onBaseExpanded', () => {
    it('should emit loadRendersRequested with baseId', () => {
      const emitted: string[] = [];
      component.loadRendersRequested.subscribe((id: string) => emitted.push(id));

      component.onBaseExpanded('base-1');

      expect(emitted).toContain('base-1');
    });
  });

  describe('onDeleteRender', () => {
    it('should emit deleteRenderRequested with renderId', () => {
      const emitted: string[] = [];
      component.deleteRenderRequested.subscribe((id: string) => emitted.push(id));

      component.onDeleteRender('render-1');

      expect(emitted).toContain('render-1');
    });
  });

  describe('onRenderSelected', () => {
    it('should emit renderSelected with the render', () => {
      const emitted: NoiseCosmeticRenderDto[] = [];
      component.renderSelected.subscribe((r: NoiseCosmeticRenderDto) => emitted.push(r));

      component.onRenderSelected(RENDER_1);

      expect(emitted[0]).toBe(RENDER_1);
    });
  });
});
