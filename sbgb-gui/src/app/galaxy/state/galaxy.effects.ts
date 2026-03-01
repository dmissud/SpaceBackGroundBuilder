import {Injectable} from "@angular/core";
import {catchError, map, mergeMap, of} from "rxjs";
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {GalaxyApiActions, GalaxyPageActions} from "./galaxy.actions";
import {GalaxyService} from "../galaxy.service";
import {HttpErrorHandlerService} from "../../sbgb/http-error-handler.service";

@Injectable()
export class GalaxyEffects {
  constructor(
    private galaxyService: GalaxyService,
    private actions$: Actions,
    private errorHandler: HttpErrorHandlerService
  ) {
  }

  loadRendersForBase$ = createEffect(() =>
    this.actions$.pipe(
      ofType(GalaxyPageActions.loadRendersForBase),
      mergeMap(({baseId}) =>
        this.galaxyService.getRendersForBase(baseId).pipe(
          map((renders) => GalaxyApiActions.rendersLoadSuccess({renders})),
          catchError((error) => of(GalaxyApiActions.rendersLoadFail({message: this.errorHandler.extractMessage(error)})))
        )
      )
    )
  );

  deleteRender$ = createEffect(() =>
    this.actions$.pipe(
      ofType(GalaxyPageActions.deleteRender),
      mergeMap(({renderId}) =>
        this.galaxyService.deleteRender(renderId).pipe(
          map(() => GalaxyApiActions.deleteRenderSuccess({renderId})),
          catchError((error) => of(GalaxyApiActions.deleteRenderFail({message: this.errorHandler.extractMessage(error)})))
        )
      )
    )
  );
}
