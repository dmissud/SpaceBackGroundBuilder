import {SbgbComparisonService} from './sbgb-comparison.service';
import {Sbgb} from './sbgb.model';

const aSbgb = (overrides: Partial<Sbgb> = {}): Sbgb => ({
  id: 'id-1',
  name: 'Test',
  description: 'desc',
  imageStructure: {
    width: 4000, height: 4000, seed: 42, octaves: 4,
    persistence: 0.5, lacunarity: 2.0, scale: 100,
    noiseType: 'FBM', preset: 'CUSTOM', useMultiLayer: false
  },
  imageColor: {
    back: '#000', middle: '#888', fore: '#FFF',
    backThreshold: 0.4, middleThreshold: 0.7,
    interpolationType: 'LINEAR', transparentBackground: false
  },
  ...overrides
});

describe('SbgbComparisonService', () => {
  let service: SbgbComparisonService;

  beforeEach(() => {
    service = new SbgbComparisonService();
  });

  it('should return false when both sbgbs are identical', () => {
    expect(service.isModified(aSbgb(), aSbgb())).toBe(false);
  });

  it('should return true when a structural field differs (seed)', () => {
    const modified = aSbgb({imageStructure: {...aSbgb().imageStructure, seed: 99}});
    expect(service.isModified(aSbgb(), modified)).toBe(true);
  });

  it('should return true when a cosmetic field differs (back color)', () => {
    const modified = aSbgb({imageColor: {...aSbgb().imageColor, back: '#FF0000'}});
    expect(service.isModified(aSbgb(), modified)).toBe(true);
  });

  it('should return true when name differs', () => {
    expect(service.isModified(aSbgb({name: 'Old'}), aSbgb({name: 'New'}))).toBe(true);
  });

  it('should return true when reference is null', () => {
    expect(service.isModified(aSbgb(), null as any)).toBe(true);
  });
});
