import {ImageApiActions, SbgbPageActions} from './sbgb.actions';
import {sbgbsFeature, initialState} from './sbgb.reducer';
import {NoiseCosmeticRenderDto} from '../sbgb.model';

const aRender = (): NoiseCosmeticRenderDto => ({
  id: 'render-1',
  baseStructureId: 'base-1',
  description: 'Colors: #000000 / #888888 / #FFFFFF',
  note: 3,
  back: '#000000',
  middle: '#888888',
  fore: '#FFFFFF',
  backThreshold: 0.4,
  middleThreshold: 0.7,
  interpolationType: 'LINEAR',
  transparentBackground: false,
  thumbnail: null
});

describe('Sbgb Reducer', () => {
  describe('an unknown action', () => {
    it('should return the previous state', () => {
      const action = {} as any;
      const result = sbgbsFeature.reducer(initialState, action);

      expect(result).toBe(initialState);
    });
  });

  describe('imagesRendersLoadSuccess', () => {
    it('should store renders in state', () => {
      const renders = [aRender()];
      const action = ImageApiActions.imagesRendersLoadSuccess({renders});

      const result = sbgbsFeature.reducer(initialState, action);

      expect(result.renders).toEqual(renders);
    });
  });

  describe('imagesDeleteRenderSuccess', () => {
    it('should remove deleted render from state', () => {
      const render = aRender();
      const stateWithRender = {...initialState, renders: [render]};
      const action = ImageApiActions.imagesDeleteRenderSuccess({renderId: 'render-1'});

      const result = sbgbsFeature.reducer(stateWithRender, action);

      expect(result.renders).toEqual([]);
    });
  });
});
