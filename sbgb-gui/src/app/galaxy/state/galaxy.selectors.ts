import {createFeatureSelector, createSelector} from '@ngrx/store';
import * as fromGalaxy from './galaxy.reducer'

export const selectGalaxyState = createFeatureSelector<fromGalaxy.GalaxyState>('galaxies');

export const selectRenders = createSelector(
  selectGalaxyState,
  (galaxyState) => galaxyState.renders
);

export const selectSelectedRenderId = createSelector(
  selectGalaxyState,
  (galaxyState) => galaxyState.selectedRenderId
);

export const selectErrorMessage = createSelector(
  selectGalaxyState,
  (galaxyState) => galaxyState.errorMessage
);
