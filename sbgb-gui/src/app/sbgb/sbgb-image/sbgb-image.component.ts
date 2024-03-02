import {Observable, of, throwError} from 'rxjs';
import {catchError, switchMap} from 'rxjs/operators';
import {Component} from "@angular/core";
import {ImagesService} from "../images.service";
import {FormControl, FormGroup} from "@angular/forms";
import {HttpResponse} from '@angular/common/http';

@Component({
  selector: 'app-sbgb-image',
  templateUrl: './sbgb-image.component.html',
  styleUrls: ['./sbgb-image.component.scss']
})
export class SbgbImageComponent {
  computedImage: string | ArrayBuffer | null | undefined;

  private static readonly CONTROL_WIDTH = 'width';
  private static readonly CONTROL_HEIGHT = 'height';
  private static readonly CONTROL_SEED = 'seed';

  constructor(private imagesService: ImagesService) {
    this._myForm = new FormGroup({
      [SbgbImageComponent.CONTROL_WIDTH]: new FormControl(''),
      [SbgbImageComponent.CONTROL_HEIGHT]: new FormControl(''),
      [SbgbImageComponent.CONTROL_SEED]: new FormControl('')
    });
  }

  protected _myForm: FormGroup;

  computeImage() {
    const {widthValue, heightValue, seedValue} = this.extractFormValues();
    this.imagesService.buildImage(heightValue, widthValue, seedValue)
      .pipe(
        catchError(this.handleError),
        switchMap(this.handleSuccess)
      )
      .subscribe({
        next: (response) => {
          console.log('Image generated successfully');
        },
        error: (error) => {
          console.error('Error Information:', error);
        }
      });
  }

  private extractFormValues() {
    let widthValue = this._myForm.controls[SbgbImageComponent.CONTROL_WIDTH].value;
    let heightValue = this._myForm.controls[SbgbImageComponent.CONTROL_HEIGHT].value;
    let seedValue = this._myForm.controls[SbgbImageComponent.CONTROL_SEED].value;

    return {widthValue, heightValue, seedValue};
  }

  private handleError(error: any) {
    console.log('Handling error locally and rethrowing it...', error);
    return throwError(() => new Error('Impossible de construire une image'));
  }

  private handleSuccess = (response: HttpResponse<Blob>): Observable<Blob | null> => {
    let reader = new FileReader();
    reader.readAsDataURL(response.body as Blob);
    reader.onloadend = () => {
      this.computedImage = reader.result;
    };
    return of(response.body);
  }
}
