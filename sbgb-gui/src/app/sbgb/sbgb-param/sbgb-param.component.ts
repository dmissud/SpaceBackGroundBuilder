import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";
import {NgIf} from "@angular/common";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {selectInfoMessage} from "../state/sbgb.selectors";
import {Subscription} from "rxjs";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Store} from "@ngrx/store";
import {SbgbPageActions} from "../state/sbgb.actions";

@Component({
  selector: 'app-sbgb-param',
  standalone: true,
  imports: [
    MatButton,
    MatCard,
    MatCardContent,
    MatFormField,
    MatInput,
    MatLabel,
    MatProgressSpinner,
    MatSlider,
    MatSliderThumb,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './sbgb-param.component.html',
  styleUrl: './sbgb-param.component.scss'
})
export class SbgbParamComponent implements OnInit, OnDestroy {

  private static readonly CONTROL_WIDTH = 'width';
  private static readonly CONTROL_HEIGHT = 'height';
  private static readonly CONTROL_SEED = 'seed';
  private static readonly BACKGROUND_COLOR = 'backgroundColor';
  private static readonly MIDDLE_COLOR = 'middleColor';
  private static readonly FOREGROUND_COLOR = 'foregroundColor';
  private static readonly BACK_THRESHOLD = 'backThreshold';
  private static readonly MIDDLE_THRESHOLD = 'middleThreshold';

  infoMessageSub: Subscription | undefined;

  protected _myForm: FormGroup;

  constructor(private _snackBar: MatSnackBar, private store: Store) {
    this._myForm = new FormGroup({
      [SbgbParamComponent.CONTROL_WIDTH]: new FormControl('4000'),
      [SbgbParamComponent.CONTROL_HEIGHT]: new FormControl('4000'),
      [SbgbParamComponent.CONTROL_SEED]: new FormControl('2569'),
      [SbgbParamComponent.BACKGROUND_COLOR]: new FormControl('#000000'),
      [SbgbParamComponent.MIDDLE_COLOR]: new FormControl('#FFA500'),
      [SbgbParamComponent.FOREGROUND_COLOR]: new FormControl('#FFFFFF'),
      [SbgbParamComponent.BACK_THRESHOLD]: new FormControl('0.7'),
      [SbgbParamComponent.MIDDLE_THRESHOLD]: new FormControl('0.75'),
    });
    this._myForm.get([SbgbParamComponent.BACK_THRESHOLD])?.valueChanges.subscribe(
      (backThreshold) => {
        let middleThreshold = this._myForm.get([SbgbParamComponent.MIDDLE_THRESHOLD])?.value;
        if (backThreshold >= middleThreshold) {
          console.log(backThreshold);
          this._myForm.get([SbgbParamComponent.MIDDLE_THRESHOLD])?.setValue(backThreshold + 0.01);
        }
      }
    )
    this._myForm.get([SbgbParamComponent.MIDDLE_THRESHOLD])?.valueChanges.subscribe(
      (middleThreshold) => {
        let backThreshold = this._myForm.get([SbgbParamComponent.BACK_THRESHOLD])?.value;
        if (backThreshold >= middleThreshold) {
          console.log(middleThreshold);
          this._myForm.get([SbgbParamComponent.BACK_THRESHOLD])?.setValue(middleThreshold - 0.01);
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
    let widthValue = this._myForm.controls[SbgbParamComponent.CONTROL_WIDTH].value;
    let heightValue = this._myForm.controls[SbgbParamComponent.CONTROL_HEIGHT].value;
    let seedValue = this._myForm.controls[SbgbParamComponent.CONTROL_SEED].value;
    return {widthValue, heightValue, seedValue};
  }

  private extractColorFormValues() {
    let backgroundColorValue = this._myForm.controls[SbgbParamComponent.BACKGROUND_COLOR].value;
    let foregroundColorValue = this._myForm.controls[SbgbParamComponent.FOREGROUND_COLOR].value;
    let middleColorValue = this._myForm.controls[SbgbParamComponent.MIDDLE_COLOR].value;
    let backThresholdValue = this._myForm.controls[SbgbParamComponent.BACK_THRESHOLD].value;
    let middleThresholdValue = this._myForm.controls[SbgbParamComponent.MIDDLE_THRESHOLD].value;
    return {backgroundColorValue, foregroundColorValue, middleColorValue, backThresholdValue, middleThresholdValue};
  }

}
