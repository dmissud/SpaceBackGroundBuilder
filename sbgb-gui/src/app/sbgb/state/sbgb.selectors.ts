import {createFeatureSelector, createSelector} from '@ngrx/store';
import * as fromSbgb from './sbgb.reducer'

export const selectSbgbState = createFeatureSelector<fromSbgb.SbgbState>('sbgbs');

export const selectImageBuild = createSelector(
  selectSbgbState,
  (sbgbState) => sbgbState.image
);

export const selectImageIsBuilding = createSelector(
  selectSbgbState,
  (sbgbState) => sbgbState.building
);

export const selectInfoMessage = createSelector(
  selectSbgbState,
  (sbgbState) => sbgbState.infoMessage
);

export const selectErrorMessage = createSelector(
  selectSbgbState,
  (sbgbState) => sbgbState.errorMessage
);

export const selectBases = createSelector(
  selectSbgbState,
  (sbgbState) => sbgbState.bases
);

export const selectCurrentSbgb = createSelector(
  selectSbgbState,
  (sbgbState) => sbgbState.sbgb
);

export const selectRenders = createSelector(
  selectSbgbState,
  (sbgbState) => sbgbState.renders
);

export const selectSelectedRenderId = createSelector(
  selectSbgbState,
  (sbgbState) => sbgbState.selectedRenderId
);
