import {createFeature, createReducer, on} from '@ngrx/store';
import {ImageApiActions, SbgbPageActions} from './sbgb.actions';
import {Sbgb} from "../sbgb.model";


// export interface SbgbState extends EntityState<Sbgb> {
//   image: string | ArrayBuffer | null;
//   building: boolean;
//   infoMessage: string,
//   errorMessage: string;
// }
export interface SbgbState {
  sbgb: Sbgb | null;
  sbgbs: Sbgb[];
  image: string | ArrayBuffer | null;
  building: boolean;
  infoMessage: string,
  errorMessage: string;
}

// export const adapter: EntityAdapter<Sbgb> = createEntityAdapter<Sbgb>({})
// export const initialState: SbgbState = adapter.getInitialState({
//   image: null,
//   building: false,
//   infoMessage: '',
//   errorMessage: ''
// });

export const initialState: SbgbState = {
  sbgb: null,
  sbgbs: [],
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
      (state, {sbgbs}) => ({
        ...state,
        sbgbs: sbgbs
      })),
    on(ImageApiActions.imagesSaveSuccess,
      (state, {sbgb}) => ({
        ...state,
        sbgb: sbgb,
        sbgbs: [...state.sbgbs, sbgb],
        infoMessage: 'Image saved successfully'
      })),
    on(SbgbPageActions.selectSbgb,
      (state, {sbgb}) => ({
        ...state,
        sbgb: sbgb
      }))
  )
});
