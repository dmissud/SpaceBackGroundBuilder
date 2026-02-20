import {Component} from "@angular/core";
import {Store} from "@ngrx/store";
import {selectImageBuild, selectImageIsBuilding} from "../state/sbgb.selectors";
import {ImagePreviewComponent} from "../../shared/components/image-preview/image-preview.component";

@Component({
    selector: 'app-sbgb-image',
    standalone: true,
    imports: [ImagePreviewComponent],
    template: `
      <app-image-preview
        [imageUrl]="computedImage()"
        [isGenerating]="imageIsBuilding()"
        emptyMessage="Aucun ciel étoilé généré"
        emptyHint="Cliquez sur &quot;Générer aperçu&quot; pour créer votre ciel étoilé"
        altText="Ciel étoilé généré">
      </app-image-preview>
    `,
    styles: ``
})
export class SbgbImageComponent {
  computedImage = this.store.selectSignal(selectImageBuild);
  imageIsBuilding = this.store.selectSignal(selectImageIsBuilding);

  constructor(private store: Store) {
  }
}
