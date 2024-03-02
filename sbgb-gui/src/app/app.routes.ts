import {Routes} from '@angular/router';
import {HomeComponent} from "./home/home.component";

export const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'sbgb',
    loadChildren: () => import('./sbgb/sbgb.module').then(m => m.SbgbModule)
  },
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
];
