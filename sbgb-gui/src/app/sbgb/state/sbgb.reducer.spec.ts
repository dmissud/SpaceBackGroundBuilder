import { sbgbsFeature, initialState } from './sbgb.reducer';

describe('Sbgb Reducer', () => {
  describe('an unknown action', () => {
    it('should return the previous state', () => {
      const action = {} as any;
      const result = sbgbsFeature.reducer(initialState, action);

      expect(result).toBe(initialState);
    });
  });
});
