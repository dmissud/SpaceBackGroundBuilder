import {Routes} from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {ImagesService} from "./sbgb/images.service";
import {provideState} from "@ngrx/store";
import {provideEffects} from "@ngrx/effects";
import {sbgbsFeature} from "./sbgb/state/sbgb.reducer";
import {SbgbEffects} from "./sbgb/state/sbgb.effects";
import {GalaxyService} from "./galaxy/galaxy.service";

export const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'sbgb',
    loadChildren: () => import('./sbgb/sbgbs.routes').then(module => module.routes),
    providers: [
      ImagesService,
      provideState(sbgbsFeature),
      provideEffects(SbgbEffects)
    ]
  },
  {
    path: 'galaxy',
    loadChildren: () => import('./galaxy/galaxy.routes').then(module => module.GALAXY_ROUTES),
    providers: [
      GalaxyService
    ]
  },
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
];
