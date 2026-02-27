import {createActionGroup, emptyProps, props} from '@ngrx/store';
import {NoiseBaseStructureDto, NoiseCosmeticRenderDto, Sbgb} from "../sbgb.model";

export const SbgbPageActions = createActionGroup({
  source: 'Sbgbs Page',
  events: {
    'Build Sbgb': props<{ sbgb: Sbgb, build: boolean }>(),
    'Rate Sbgb': props<{ sbgb: Sbgb, note: number }>(),
    'Load Sbgbs': emptyProps(),
    'Select Sbgb': props<{ sbgb: Sbgb }>(),
    'Information': props<{ message: string, build: boolean }>(),
    'Load Renders For Base': props<{ baseId: string }>(),
    'Delete Render': props<{ renderId: string }>(),
    'Select Render': props<{ renderId: string }>(),
    'Clear Selected Render': emptyProps()
  }
});

export const ImageApiActions = createActionGroup({
  source: 'Image API',
  events: {
    'Images Build Success': props<{ image: string | ArrayBuffer | null, build: boolean }>(),
    'Images Build Fail': props<{ message: string, build: boolean }>(),
    'Images Save Success': props<{ render: NoiseCosmeticRenderDto }>(),
    'Images Save Fail': props<{ message: string }>(),
    'Images Load Success': props<{ bases: NoiseBaseStructureDto[] }>(),
    'Images Load Fail': props<{ message: string }>(),
    'Images Renders Load Success': props<{ renders: NoiseCosmeticRenderDto[] }>(),
    'Images Renders Load Fail': props<{ message: string }>(),
    'Images Delete Render Success': props<{ renderId: string }>(),
    'Images Delete Render Fail': props<{ message: string }>()
  }
})
