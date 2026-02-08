import {Component} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {Store} from "@ngrx/store";
import {selectImageBuild, selectImageIsBuilding} from "../state/sbgb.selectors";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatSlideToggle} from "@angular/material/slide-toggle";

@Component({
    selector: 'app-sbgb-image',
    templateUrl: './sbgb-image.component.html',
    imports: [
    FormsModule,
    MatProgressSpinner,
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
