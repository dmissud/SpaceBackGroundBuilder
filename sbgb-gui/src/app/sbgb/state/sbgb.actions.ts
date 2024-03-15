import {createActionGroup, props} from '@ngrx/store';
import {Sbgb} from "../sbgb.model";

export const SbgbPageActions = createActionGroup({
  source: 'Sbgbs Page',
  events: {
    'Build Sbgb': props<{sbgb: Sbgb}>(),
    'Information': props<{message: string}>()
  }
});

export const ImageApiActions = createActionGroup({
  source: 'Image API',
  events: {
    'Images Build Success': props<{image: string | ArrayBuffer | null}>(),
    'Images Build Fail': props<{message: string}>()
  }
})
