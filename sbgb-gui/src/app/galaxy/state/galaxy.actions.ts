import {createActionGroup, emptyProps, props} from '@ngrx/store';
import {GalaxyBaseStructureDto, GalaxyCosmeticRenderDto} from "../galaxy.model";

export const GalaxyPageActions = createActionGroup({
  source: 'Galaxies Page',
  events: {
    'Load Renders For Base': props<{ baseId: string }>(),
    'Delete Render': props<{ renderId: string }>(),
    'Select Render': props<{ renderId: string }>(),
    'Clear Selected Render': emptyProps(),
    'Apply Render Cosmetics': props<{ render: GalaxyCosmeticRenderDto }>(),
    'Load Bases': emptyProps()
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
