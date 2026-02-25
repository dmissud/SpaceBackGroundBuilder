import {of} from 'rxjs';
import {SbgbParamComponent} from './sbgb-param.component';

describe('SbgbParamComponent — baseForm / cosmeticForm split', () => {
  let component: SbgbParamComponent;
  let mockStore: any;
  let mockSnackBar: any;
  let mockDialog: any;

  beforeEach(() => {
    mockStore = {
      select: jest.fn().mockReturnValue(of(null)),
      selectSignal: jest.fn().mockReturnValue(() => null),
      dispatch: jest.fn()
    };
    mockSnackBar = {open: jest.fn()};
    mockDialog = {open: jest.fn()};
    const mockActions$ = {pipe: jest.fn().mockReturnValue(of())};
    component = new SbgbParamComponent(mockSnackBar, mockStore, mockActions$ as any, mockDialog);
  });

  afterEach(() => {
    component.ngOnDestroy();
  });

  describe('baseForm', () => {
    it('should contain all structural controls', () => {
      const structuralControls = ['width', 'height', 'seed', 'octaves', 'persistence', 'lacunarity', 'scale', 'noiseType', 'useMultiLayer'];
      structuralControls.forEach(ctrl => {
        expect(component.baseForm.contains(ctrl)).toBe(true);
      });
    });

    it('should not contain cosmetic controls', () => {
      expect(component.baseForm.contains('backgroundColor')).toBe(false);
      expect(component.baseForm.contains('interpolationType')).toBe(false);
    });
  });

  describe('cosmeticForm', () => {
    it('should contain all cosmetic controls', () => {
      const cosmeticControls = ['backgroundColor', 'middleColor', 'foregroundColor', 'backThreshold', 'middleThreshold', 'interpolationType', 'transparentBackground'];
      cosmeticControls.forEach(ctrl => {
        expect(component.cosmeticForm.contains(ctrl)).toBe(true);
      });
    });

    it('should not contain structural controls', () => {
      expect(component.cosmeticForm.contains('seed')).toBe(false);
      expect(component.cosmeticForm.contains('noiseType')).toBe(false);
    });
  });
});
