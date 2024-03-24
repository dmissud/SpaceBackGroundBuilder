import {createActionGroup, props} from '@ngrx/store';
import {Sbgb} from "../sbgb.model";

export const SbgbPageActions = createActionGroup({
  source: 'Sbgbs Page',
  events: {
    'Build Sbgb': props<{ sbgb: Sbgb, build: boolean }>(),
    'Information': props<{ message: string, build: boolean }>()
  }
});

export const ImageApiActions = createActionGroup({
  source: 'Image API',
  events: {
    'Images Build Success': props<{ image: string | ArrayBuffer | null, build: boolean }>(),
    'Images Build Fail': props<{ message: string, build: boolean }>()
  }
})
