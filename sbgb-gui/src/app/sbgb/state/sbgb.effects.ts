import {Injectable} from "@angular/core";
import {catchError, concatMap, from, map, mergeMap, of} from "rxjs";
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {ImageApiActions, SbgbPageActions} from "./sbgb.actions";
import {Router} from "@angular/router";
import {ImagesService} from "../images.service";
import {HttpResponse} from "@angular/common/http";
import {HttpErrorHandlerService} from "../http-error-handler.service";

@Injectable()
export class SbgbEffects {
  constructor(
    private imagesService: ImagesService,
    private actions$: Actions,
    private router: Router,
    private errorHandler: HttpErrorHandlerService
  ) {
  }

  buildImage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SbgbPageActions.buildSbgb),
      concatMap(({sbgb}) =>
        this.imagesService.buildImage(sbgb).pipe(
          mergeMap((response) => from(this.blobToDataUrl(response))),
          map((image) => ImageApiActions.imagesBuildSuccess({build: false, image})),
          catchError((error) =>
            of(ImageApiActions.imagesBuildFail({build: false, message: this.errorHandler.extractMessage(error)}))
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
          catchError((error) => of(ImageApiActions.imagesLoadFail({message: this.errorHandler.extractMessage(error)})))
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
          catchError((error) => of(ImageApiActions.imagesSaveFail({message: this.errorHandler.extractMessage(error)})))
        )
      )
    )
  );

  reloadAfterSave$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ImageApiActions.imagesSaveSuccess),
      map(() => SbgbPageActions.loadSbgbs())
    )
  );

  reloadRendersAfterDelete$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ImageApiActions.imagesDeleteRenderSuccess),
      map(() => SbgbPageActions.loadSbgbs())
    )
  );

  loadRendersForBase$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SbgbPageActions.loadRendersForBase),
      mergeMap(({baseId}) =>
        this.imagesService.getRendersForBase(baseId).pipe(
          map((renders) => ImageApiActions.imagesRendersLoadSuccess({renders})),
          catchError((error) => of(ImageApiActions.imagesRendersLoadFail({message: this.errorHandler.extractMessage(error)})))
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
          catchError((error) => of(ImageApiActions.imagesDeleteRenderFail({message: this.errorHandler.extractMessage(error)})))
        )
      )
    )
  );

  private blobToDataUrl(response: HttpResponse<Blob>): Promise<string | ArrayBuffer | null> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => resolve(reader.result);
      reader.onerror = reject;
      reader.readAsDataURL(response.body as Blob);
    });
  }
}
