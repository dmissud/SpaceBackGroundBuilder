import {createActionGroup, emptyProps, props} from '@ngrx/store';
import {Sbgb} from "../sbgb.model";

export const SbgbPageActions = createActionGroup({
  source: 'Sbgbs Page',
  events: {
    'Build Sbgb': props<{ sbgb: Sbgb, build: boolean }>(),
    'Save Sbgb': props<{ sbgb: Sbgb }>(),
    'Update Note': props<{ id: string, note: number }>(),
    'Load Sbgbs': emptyProps(),
    'Select Sbgb': props<{ sbgb: Sbgb }>(),
    'Information': props<{ message: string, build: boolean }>()
  }
});

export const ImageApiActions = createActionGroup({
  source: 'Image API',
  events: {
    'Images Build Success': props<{ image: string | ArrayBuffer | null, build: boolean }>(),
    'Images Build Fail': props<{ message: string, build: boolean }>(),
    'Images Save Success': props<{ sbgb: Sbgb }>(),
    'Images Save Fail': props<{ message: string }>(),
    'Images Load Success': props<{ sbgbs: Sbgb[] }>(),
    'Images Load Fail': props<{ message: string }>(),
    'Images Update Note Success': props<{ id: string, note: number }>(),
    'Images Update Note Fail': props<{ message: string }>()
  }
})
