import {createActionGroup, emptyProps, props} from '@ngrx/store';
import {GalaxyCosmeticRenderDto} from "../galaxy.model";

export const GalaxyPageActions = createActionGroup({
  source: 'Galaxies Page',
  events: {
    'Load Renders For Base': props<{ baseId: string }>(),
    'Delete Render': props<{ renderId: string }>(),
    'Select Render': props<{ renderId: string }>(),
    'Clear Selected Render': emptyProps(),
    'Clear Renders': emptyProps(),
    'Apply Render Cosmetics': props<{ render: GalaxyCosmeticRenderDto }>()
  }
});

export const GalaxyApiActions = createActionGroup({
  source: 'Galaxy API',
  events: {
    'Renders Load Success': props<{ renders: GalaxyCosmeticRenderDto[] }>(),
    'Renders Load Fail': props<{ message: string }>(),
    'Delete Render Success': props<{ renderId: string }>(),
    'Delete Render Fail': props<{ message: string }>()
  }
})
