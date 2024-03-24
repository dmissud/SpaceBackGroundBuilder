import {Component, OnDestroy, OnInit} from "@angular/core";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Store} from "@ngrx/store";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {SbgbPageActions} from "../state/sbgb.actions";
import {selectImageBuild, selectImageIsBuilding, selectInfoMessage} from "../state/sbgb.selectors";
import {AsyncPipe, NgIf, NgOptimizedImage} from "@angular/common";
import {Subscription} from "rxjs";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

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
    AsyncPipe,
    MatSlider,
    MatLabel,
    MatCard,
    MatCardContent,
    MatSliderThumb,
    FormsModule,
    MatProgressSpinner,
    NgIf
  ],
  styleUrls: ['./sbgb-image.component.scss']
})
export class SbgbImageComponent implements OnInit, OnDestroy {

  private static readonly CONTROL_WIDTH = 'width';
  private static readonly CONTROL_HEIGHT = 'height';
  private static readonly CONTROL_SEED = 'seed';
  private static readonly BACKGROUND_COLOR = 'backgroundColor';
  private static readonly MIDDLE_COLOR = 'middleColor';
  private static readonly FOREGROUND_COLOR = 'foregroundColor';
  private static readonly BACK_THRESHOLD = 'backThreshold';
  private static readonly MIDDLE_THRESHOLD = 'middleThreshold';

  computedImage = this.store.selectSignal(selectImageBuild);
  imageIsBuilding = this.store.selectSignal(selectImageIsBuilding);

  infoMessageSub: Subscription | undefined;

  protected _myForm: FormGroup;

  constructor(private _snackBar: MatSnackBar, private store: Store) {
    this._myForm = new FormGroup({
      [SbgbImageComponent.CONTROL_WIDTH]: new FormControl('4000'),
      [SbgbImageComponent.CONTROL_HEIGHT]: new FormControl('4000'),
      [SbgbImageComponent.CONTROL_SEED]: new FormControl('2569'),
      [SbgbImageComponent.BACKGROUND_COLOR]: new FormControl('#000000'),
      [SbgbImageComponent.MIDDLE_COLOR]: new FormControl('#FFA500'),
      [SbgbImageComponent.FOREGROUND_COLOR]: new FormControl('#FFFFFF'),
      [SbgbImageComponent.BACK_THRESHOLD]: new FormControl('0.7'),
      [SbgbImageComponent.MIDDLE_THRESHOLD]: new FormControl('0.75'),
    });
    this._myForm.get([SbgbImageComponent.BACK_THRESHOLD])?.valueChanges.subscribe(
      (backThreshold) => {
        let middleThreshold = this._myForm.get([SbgbImageComponent.MIDDLE_THRESHOLD])?.value;
        if (backThreshold >= middleThreshold) {
          console.log(backThreshold);
          this._myForm.get([SbgbImageComponent.MIDDLE_THRESHOLD])?.setValue(backThreshold + 0.01);
        }
      }
    )
    this._myForm.get([SbgbImageComponent.MIDDLE_THRESHOLD])?.valueChanges.subscribe(
      (middleThreshold) => {
        let backThreshold = this._myForm.get([SbgbImageComponent.BACK_THRESHOLD])?.value;
        if (backThreshold >= middleThreshold) {
          console.log(middleThreshold);
          this._myForm.get([SbgbImageComponent.BACK_THRESHOLD])?.setValue(middleThreshold - 0.01);
        }
      }
    )
  }

  ngOnInit() {
    this.infoMessageSub = this.store.select(selectInfoMessage).subscribe((message: string) => {
      console.log(message);
      if (message && message.trim() !== "") {
        this._snackBar.open(message, 'Close', { // Utilisation du message reçu en tant que texte pour le Snackbar
          duration: 3000,
          verticalPosition: 'top'
        });
        this.store.dispatch(SbgbPageActions.information({message: '', build: false}));
      }
    });
  }

  ngOnDestroy() {
    if (this.infoMessageSub) {
      this.infoMessageSub.unsubscribe();
    }
  }

  computeImage() {
    const {widthValue, heightValue, seedValue} = this.extractImageFormValues();
    const {backgroundColorValue, middleColorValue, foregroundColorValue, backThresholdValue, middleThresholdValue}
      = this.extractColorFormValues();
    const sbgb = {
      image: {
        width: widthValue,
        height: heightValue,
        seed: seedValue
      },
      color: {
        back: backgroundColorValue,
        middle: middleColorValue,
        fore: foregroundColorValue,
        backThreshold: backThresholdValue,
        middleThreshold: middleThresholdValue
      }
    };
    this.store.dispatch(SbgbPageActions.buildSbgb({sbgb, build: true}));
  }

  private extractImageFormValues() {
    let widthValue = this._myForm.controls[SbgbImageComponent.CONTROL_WIDTH].value;
    let heightValue = this._myForm.controls[SbgbImageComponent.CONTROL_HEIGHT].value;
    let seedValue = this._myForm.controls[SbgbImageComponent.CONTROL_SEED].value;
    return {widthValue, heightValue, seedValue};
  }

  private extractColorFormValues() {
    let backgroundColorValue = this._myForm.controls[SbgbImageComponent.BACKGROUND_COLOR].value;
    let foregroundColorValue = this._myForm.controls[SbgbImageComponent.FOREGROUND_COLOR].value;
    let middleColorValue = this._myForm.controls[SbgbImageComponent.MIDDLE_COLOR].value;
    let backThresholdValue = this._myForm.controls[SbgbImageComponent.BACK_THRESHOLD].value;
    let middleThresholdValue = this._myForm.controls[SbgbImageComponent.MIDDLE_THRESHOLD].value;
    return {backgroundColorValue, foregroundColorValue, middleColorValue, backThresholdValue, middleThresholdValue};
  }
}
