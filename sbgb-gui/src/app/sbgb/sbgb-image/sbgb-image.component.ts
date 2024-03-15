import {Component, OnDestroy, OnInit} from "@angular/core";
import {ImagesService} from "../images.service";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Store} from "@ngrx/store";
import {MatFormField} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {SbgbPageActions} from "../state/sbgb.actions";
import {selectImageBuild, selectInfoMessage} from "../state/sbgb.selectors";
import {AsyncPipe, NgOptimizedImage} from "@angular/common";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-sbgb-image',
  templateUrl: './sbgb-image.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatInput,
    MatButton,
    NgOptimizedImage,
    AsyncPipe
  ],
  styleUrls: ['./sbgb-image.component.scss']
})
export class SbgbImageComponent implements OnInit, OnDestroy {

  private static readonly CONTROL_WIDTH = 'width';
  private static readonly CONTROL_HEIGHT = 'height';
  private static readonly CONTROL_SEED = 'seed';

  computedImage = this.store.selectSignal(selectImageBuild);
  infoMessageSub: Subscription | undefined;

  protected _myForm: FormGroup;

  constructor(private imagesService: ImagesService, private _snackBar: MatSnackBar, private store: Store) {
    this._myForm = new FormGroup({
      [SbgbImageComponent.CONTROL_WIDTH]: new FormControl(''),
      [SbgbImageComponent.CONTROL_HEIGHT]: new FormControl(''),
      [SbgbImageComponent.CONTROL_SEED]: new FormControl('')
    });
    this.store.subscribe((store) => console.log(store));
  }


  ngOnInit() {
    this.infoMessageSub = this.store.select(selectInfoMessage).subscribe((message: string) => {
      console.log(message);
      if (message && message.trim() !== "") {
        this._snackBar.open(message, 'Close', { // Utilisation du message reçu en tant que texte pour le Snackbar
          duration: 3000,
          verticalPosition: 'top'
        });
        this.store.dispatch(SbgbPageActions.information({message: ''}));
      }
    });
  }

  ngOnDestroy() {
    if (this.infoMessageSub) {
      this.infoMessageSub.unsubscribe();
    }
  }

  computeImage() {
    const {widthValue, heightValue, seedValue} = this.extractFormValues();
    const sbgb = {
      width: widthValue,
      height: heightValue,
      seed: seedValue
    };
    this.store.dispatch(SbgbPageActions.buildSbgb({sbgb}));
  }

  private extractFormValues() {
    let widthValue = this._myForm.controls[SbgbImageComponent.CONTROL_WIDTH].value;
    let heightValue = this._myForm.controls[SbgbImageComponent.CONTROL_HEIGHT].value;
    let seedValue = this._myForm.controls[SbgbImageComponent.CONTROL_SEED].value;

    return {widthValue, heightValue, seedValue};
  }

}
