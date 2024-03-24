import {Injectable} from "@angular/core";
import {catchError, concatMap, map, mergeMap, of} from "rxjs";
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {ImageApiActions, SbgbPageActions} from "./sbgb.actions";
import {Router} from "@angular/router";
import {ImagesService} from "../images.service";
import {HttpResponse} from "@angular/common/http";

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

  private loadImage(resolve: (value: (PromiseLike<string | ArrayBuffer | null> | string | ArrayBuffer | null)) => void, reject: (reason?: any) => void, response: HttpResponse<Blob>) {
    let reader = new FileReader();
    reader.onloadend = () => resolve(reader.result);
    reader.onerror = reject;
    reader.readAsDataURL(response.body as Blob);
    console.log('promise')
  }
}
