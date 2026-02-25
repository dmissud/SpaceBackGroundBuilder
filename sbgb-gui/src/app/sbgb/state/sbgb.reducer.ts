import {createFeature, createReducer, on} from '@ngrx/store';
import {ImageApiActions, SbgbPageActions} from './sbgb.actions';
import {NoiseBaseStructureDto, NoiseCosmeticRenderDto, Sbgb} from "../sbgb.model";

export interface SbgbState {
  sbgb: Sbgb | null;
  bases: NoiseBaseStructureDto[];
  renders: NoiseCosmeticRenderDto[];
  image: string | ArrayBuffer | null;
  building: boolean;
  infoMessage: string,
  errorMessage: string;
}

export const initialState: SbgbState = {
  sbgb: null,
  bases: [],
  renders: [],
  image: null,
  building: false,
  infoMessage: '',
  errorMessage: ''
}

export const sbgbsFeature = createFeature({
  name: 'sbgbs',
  reducer: createReducer(
    initialState,
    on(ImageApiActions.imagesBuildSuccess,
      (state, {image, build}) => ({
        ...state,
        image: image,
        building: build,
        infoMessage: 'Image generated successfully'
      })
    ),
    on(SbgbPageActions.information,
      (state, {message, build}) => ({
        ...state,
        infoMessage: message,
        building: build
      })),
    on(SbgbPageActions.buildSbgb,
      (state, {sbgb, build}) => ({
        ...state,
        sbgb: sbgb,
        building: build
      })),
    on(ImageApiActions.imagesLoadSuccess,
      (state, {bases}) => ({
        ...state,
        bases: bases
      })),
    on(ImageApiActions.imagesSaveSuccess,
      (state) => ({
        ...state,
        infoMessage: 'Ciel étoilé sauvegardé avec succès'
      })),
    on(SbgbPageActions.selectSbgb,
      (state, {sbgb}) => ({
        ...state,
        sbgb: sbgb
      })),
    on(ImageApiActions.imagesRendersLoadSuccess,
      (state, {renders}) => ({
        ...state,
        renders: renders
      })),
    on(ImageApiActions.imagesDeleteRenderSuccess,
      (state, {renderId}) => ({
        ...state,
        renders: state.renders.filter(r => r.id !== renderId)
      }))
  )
});
