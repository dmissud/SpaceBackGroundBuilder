import {createFeature, createReducer, on} from '@ngrx/store';
import {GalaxyApiActions, GalaxyPageActions} from './galaxy.actions';
import {GalaxyCosmeticRenderDto} from "../galaxy.model";

export interface GalaxyState {
  renders: GalaxyCosmeticRenderDto[];
  selectedRenderId: string | null;
  errorMessage: string;
}

export const initialState: GalaxyState = {
  renders: [],
  selectedRenderId: null,
  errorMessage: ''
}

export const galaxiesFeature = createFeature({
  name: 'galaxies',
  reducer: createReducer(
    initialState,
    on(GalaxyPageActions.clearSelectedRender,
      (state) => ({
        ...state,
        selectedRenderId: null
      })),
    on(GalaxyApiActions.rendersLoadSuccess,
      (state, {renders}) => ({
        ...state,
        renders: renders
      })),
    on(GalaxyApiActions.deleteRenderSuccess,
      (state, {renderId}) => ({
        ...state,
        renders: state.renders.filter(r => r.id !== renderId),
        selectedRenderId: state.selectedRenderId === renderId ? null : state.selectedRenderId
      })),
    on(GalaxyApiActions.deleteRenderFail,
      (state, {message}) => ({
        ...state,
        errorMessage: message
      })),
    on(GalaxyPageActions.selectRender,
      (state, {renderId}) => ({
        ...state,
        selectedRenderId: renderId
      }))
  )
});
