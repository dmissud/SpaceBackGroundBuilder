import {createFeature, createReducer, on} from '@ngrx/store';
import {ImageApiActions, SbgbPageActions} from './sbgb.actions';
import {Sbgb} from "../sbgb.model";
import {createEntityAdapter, EntityAdapter, EntityState} from "@ngrx/entity";


export interface SbgbState extends EntityState<Sbgb> {
  image: string | ArrayBuffer | null;
  building: boolean;
  infoMessage: string,
  errorMessage: string;
}

export const adapter: EntityAdapter<Sbgb> = createEntityAdapter<Sbgb>({})
export const initialState: SbgbState = adapter.getInitialState({
  image: null,
  building: false,
  infoMessage: '',
  errorMessage: ''
});

export const sbgbsFeature = createFeature({
  name: 'sbgbs',
  reducer: createReducer(
    initialState,
    on(ImageApiActions.imagesBuildSuccess,
      (state, {image}) => ({
        ...state,
        image: image,
        building: false,
        infoMessage: 'Image generated successfully'
      })
    ),
    on(SbgbPageActions.information,
      (state, {message}) => ({
        ...state,
        infoMessage: message
      }))
  )
});
