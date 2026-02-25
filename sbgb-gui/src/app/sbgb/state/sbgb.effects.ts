import {Injectable} from "@angular/core";
import {catchError, concatMap, map, mergeMap, of} from "rxjs";
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {ImageApiActions, SbgbPageActions} from "./sbgb.actions";
import {Router} from "@angular/router";
import {ImagesService} from "../images.service";
import { HttpResponse } from "@angular/common/http";

@Injectable()
export class SbgbEffects {
  constructor(
    private imagesService: ImagesService,
    private actions$: Actions,
    private router: Router
  ) {
  }

  buildImage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SbgbPageActions.buildSbgb),
      concatMap(({sbgb}) =>
        this.imagesService.buildImage(sbgb).pipe(
          mergeMap((response) =>
            new Promise<string | ArrayBuffer | null>((resolve, reject) => {
              this.loadImage(resolve, reject, response);
            })
          ),
          map((image) =>
            ImageApiActions.imagesBuildSuccess({build: false, image})),
          catchError((error) =>
            of(ImageApiActions.imagesBuildFail({build: false, message: error}))
          )
        )
      )
    )
  );

  loadImages$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SbgbPageActions.loadSbgbs),
      mergeMap(() =>
        this.imagesService.getBases().pipe(
          map((bases) => ImageApiActions.imagesLoadSuccess({bases})),
          catchError((error) => of(ImageApiActions.imagesLoadFail({message: error.message})))
        )
      )
    )
  );

  rateImage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SbgbPageActions.rateSbgb),
      mergeMap(({sbgb, note}) =>
        this.imagesService.rateRender(sbgb, note).pipe(
          map((render) => ImageApiActions.imagesSaveSuccess({render})),
          catchError((error) => of(ImageApiActions.imagesSaveFail({message: error.error?.message || error.message})))
        )
      )
    )
  );

  loadRendersForBase$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SbgbPageActions.loadRendersForBase),
      mergeMap(({baseId}) =>
        this.imagesService.getRendersForBase(baseId).pipe(
          map((renders) => ImageApiActions.imagesRendersLoadSuccess({renders})),
          catchError((error) => of(ImageApiActions.imagesRendersLoadFail({message: error.message})))
        )
      )
    )
  );

  deleteRender$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SbgbPageActions.deleteRender),
      mergeMap(({renderId}) =>
        this.imagesService.deleteRender(renderId).pipe(
          map(() => ImageApiActions.imagesDeleteRenderSuccess({renderId})),
          catchError((error) => of(ImageApiActions.imagesDeleteRenderFail({message: error.message})))
        )
      )
    )
  );

  private loadImage(resolve: (value: (PromiseLike<string | ArrayBuffer | null> | string | ArrayBuffer | null)) => void, reject: (reason?: any) => void, response: HttpResponse<Blob>) {
    let reader = new FileReader();
    reader.onloadend = () => resolve(reader.result);
    reader.onerror = reject;
    reader.readAsDataURL(response.body as Blob);
    console.log('promise')
  }
}
