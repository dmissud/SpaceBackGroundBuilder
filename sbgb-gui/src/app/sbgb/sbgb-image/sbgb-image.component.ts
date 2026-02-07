import {Component} from "@angular/core";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Store} from "@ngrx/store";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {selectImageBuild, selectImageIsBuilding} from "../state/sbgb.selectors";
import {AsyncPipe, NgIf, NgOptimizedImage} from "@angular/common";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatSlideToggle} from "@angular/material/slide-toggle";

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
    NgIf,
    MatSlideToggle
  ],
  styleUrls: ['./sbgb-image.component.scss']
})
export class SbgbImageComponent {

  // Fit mode: true = fit to window, false = scroll to see full image
  fitToWindowMode: boolean = false;

  computedImage = this.store.selectSignal(selectImageBuild);
  imageIsBuilding = this.store.selectSignal(selectImageIsBuilding);

  constructor(private store: Store) {
  }

  toggleFitMode() {
    this.fitToWindowMode = !this.fitToWindowMode;
  }

  getImageStyle() {
    return this.fitToWindowMode
      ? {'max-width': '100%', 'max-height': '100vh', 'object-fit': 'contain'}
      : {'max-width': '100%'};
  }
}
