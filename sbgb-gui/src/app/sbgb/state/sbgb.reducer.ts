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
      (state, {sbgb}) => {
        const index = state.sbgbs.findIndex(s => s.id === sbgb.id);
        const newSbgbs = index >= 0
          ? [...state.sbgbs.slice(0, index), sbgb, ...state.sbgbs.slice(index + 1)]
          : [...state.sbgbs, sbgb];

        return {
          ...state,
          sbgb: sbgb,
          sbgbs: newSbgbs,
          infoMessage: 'Image saved successfully'
        };
      }),
    on(SbgbPageActions.selectSbgb,
      (state, {sbgb}) => ({
        ...state,
        sbgb: sbgb
      })),
    on(ImageApiActions.imagesUpdateNoteSuccess,
      (state, {id, note}) => ({
        ...state,
        sbgbs: state.sbgbs.map(s => s.id === id ? {...s, note} : s),
        sbgb: state.sbgb?.id === id ? {...state.sbgb, note} : state.sbgb
      }))
  )
});
