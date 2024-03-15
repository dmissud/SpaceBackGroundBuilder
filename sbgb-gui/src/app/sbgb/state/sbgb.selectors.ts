import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromSbgb from './sbgb.reducer'
export const selectSbgbState = createFeatureSelector<fromSbgb.SbgbState>('sbgbs');

export const selectImageBuild = createSelector(
  selectSbgbState,
  (sbgbState) => sbgbState.image
);

export const selectInfoMessage = createSelector(
  selectSbgbState,
  (sbgbState) => sbgbState.infoMessage
);

export const selectErrorMessage = createSelector(
  selectSbgbState,
  (sbgbState) => sbgbState.errorMessage
);

