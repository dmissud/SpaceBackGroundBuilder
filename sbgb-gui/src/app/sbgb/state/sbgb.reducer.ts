import {createFeature, createReducer, on} from '@ngrx/store';
import {INFO_MESSAGES} from "../sbgb.constants";
import {ImageApiActions, SbgbPageActions} from './sbgb.actions';
import {NoiseBaseStructureDto, NoiseCosmeticRenderDto, Sbgb} from "../sbgb.model";

export interface SbgbState {
  sbgb: Sbgb | null;
  bases: NoiseBaseStructureDto[];
  renders: NoiseCosmeticRenderDto[];
  selectedRenderId: string | null;
  image: string | ArrayBuffer | null;
  building: boolean;
  infoMessage: string,
  errorMessage: string;
}

export const initialState: SbgbState = {
  sbgb: null,
  bases: [],
  renders: [],
  selectedRenderId: null,
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
        infoMessage: INFO_MESSAGES.IMAGE_GENERATED
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
    on(SbgbPageActions.clearSelectedRender,
      (state) => ({
        ...state,
        selectedRenderId: null
      })),
    on(ImageApiActions.imagesLoadSuccess,
      (state, {bases}) => ({
        ...state,
        bases: bases
      })),
    on(ImageApiActions.imagesSaveSuccess,
      (state, {render}) => ({
        ...state,
        selectedRenderId: render.id,
        infoMessage: INFO_MESSAGES.RENDER_SAVED
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
      })),
    on(SbgbPageActions.selectRender,
      (state, {renderId}) => ({
        ...state,
        selectedRenderId: renderId
      }))
  )
});
