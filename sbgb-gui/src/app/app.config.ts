import {ApplicationConfig} from '@angular/core';
import {provideRouter} from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

import {routes} from './app.routes';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {provideStore} from "@ngrx/store";
import {provideRouterStore, routerReducer} from "@ngrx/router-store";
import {provideEffects} from "@ngrx/effects";
import {provideStoreDevtools} from "@ngrx/store-devtools";
import {environment} from "../environments/environment";

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptorsFromDi()),
    provideStore({
      router: routerReducer,
    }),
    provideRouterStore(),
    provideEffects(),
    provideStoreDevtools({
      maxAge: 25,
      logOnly: environment.production
    }),
    provideRouter(routes),
    provideAnimationsAsync()]
};
