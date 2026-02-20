import {Component, ViewChild} from '@angular/core';
import {SbgbParamComponent} from "../sbgb-param/sbgb-param.component";
import {SbgbImageComponent} from "../sbgb-image/sbgb-image.component";
import {SbgbListComponent} from "../sbgb-list/sbgb-list.component";
import {Store} from "@ngrx/store";
import {selectImageBuild, selectImageIsBuilding} from "../state/sbgb.selectors";
import {ActionBarComponent, ActionBarButton} from "../../shared/components/action-bar/action-bar.component";
import {GeneratorShellComponent} from "../../shared/components/generator-shell/generator-shell.component";

import {Sbgb} from "../sbgb.model";

@Component({
    selector: 'app-sbgb-shell',
    imports: [
        GeneratorShellComponent,
        SbgbParamComponent,
        SbgbImageComponent,
        SbgbListComponent,
        ActionBarComponent
    ],
    templateUrl: './sbgb-shell.component.html',
    styleUrl: './sbgb-shell.component.scss'
})
export class SbgbShellComponent {
  @ViewChild(GeneratorShellComponent) shell!: GeneratorShellComponent;
  @ViewChild(SbgbParamComponent) paramComponent!: SbgbParamComponent;
  @ViewChild(SbgbListComponent) listComponent!: SbgbListComponent;

  hasBuiltImage = this.store.selectSignal(selectImageBuild);
  isGenerating = this.store.selectSignal(selectImageIsBuilding);

  constructor(private store: Store) {}

  // Action bar buttons configuration
  get actionBarButtons(): ActionBarButton[] {
    const param = this.paramComponent;
    if (!param) return [];

    return [
      {
        label: 'Générer aperçu',
        color: 'primary',
        disabled: !param.canBuild(),
        tooltip: param.getBuildTooltip(),
        action: () => param.computeImage()
      },
      {
        label: 'Sauvegarder',
        color: 'accent',
        disabled: !param.canSave(),
        tooltip: param.getSaveTooltip(),
        action: () => param.saveImage()
      },
      {
        label: 'Télécharger',
        disabled: !param.canDownload(),
        tooltip: param.getDownloadTooltip(),
        action: () => param.downloadImage()
      }
    ];
  }

  getSummary(): string | null {
    const param = this.paramComponent;
    return this.hasBuiltImage() && param ? param.getParametersSummary() : null;
  }

  onViewRequested(sbgb: Sbgb) {
    if (this.paramComponent && this.paramComponent.hasUnsavedChanges()) {
      if (confirm('Vous avez des modifications non enregistrées. Voulez-vous vraiment charger un autre ciel étoilé ?')) {
        this.loadAndSwitch(sbgb);
      }
    } else {
      this.loadAndSwitch(sbgb);
    }
  }

  private loadAndSwitch(sbgb: Sbgb) {
    if (this.listComponent) {
      this.listComponent.confirmView(sbgb);
    }
    this.shell.switchToGenerator();
  }
}
