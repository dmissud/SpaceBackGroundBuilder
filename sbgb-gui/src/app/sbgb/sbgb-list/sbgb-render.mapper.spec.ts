import {groupRendersByBaseId, toSbgbFromRender} from './sbgb-render.mapper';
import {NoiseBaseStructureDto, NoiseCosmeticRenderDto} from '../sbgb.model';

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

describe('sbgb-render.mapper', () => {
  describe('toSbgbFromRender', () => {
    it('should map base structure params to imageStructure', () => {
      const sbgb = toSbgbFromRender(BASE_1, RENDER_1);

      expect(sbgb.imageStructure.seed).toBe(42);
      expect(sbgb.imageStructure.width).toBe(1920);
      expect(sbgb.imageStructure.noiseType).toBe('FBM');
    });

    it('should map render cosmetic params to imageColor', () => {
      const sbgb = toSbgbFromRender(BASE_1, RENDER_1);

      expect(sbgb.imageColor.back).toBe('#6b2d8b');
      expect(sbgb.imageColor.middle).toBe('#ff9500');
      expect(sbgb.imageColor.interpolationType).toBe('LINEAR');
    });

    it('should carry render note', () => {
      const sbgb = toSbgbFromRender(BASE_1, RENDER_1);

      expect(sbgb.note).toBe(4);
    });
  });

  describe('groupRendersByBaseId', () => {
    it('should group renders by their baseStructureId', () => {
      const render2 = {...RENDER_1, id: 'render-2'};
      const result = groupRendersByBaseId([RENDER_1, render2]);

      expect(result['base-1'].length).toBe(2);
    });

    it('should return empty record for empty array', () => {
      const result = groupRendersByBaseId([]);

      expect(Object.keys(result).length).toBe(0);
    });

    it('should group renders from different bases separately', () => {
      const render2 = {...RENDER_1, id: 'render-2', baseStructureId: 'base-2'};
      const result = groupRendersByBaseId([RENDER_1, render2]);

      expect(result['base-1'].length).toBe(1);
      expect(result['base-2'].length).toBe(1);
    });
  });
});
