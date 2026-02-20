import {Component, ViewChild} from '@angular/core';
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {GalaxyParamComponent} from "../galaxy-param/galaxy-param.component";
import {GalaxyListComponent} from "../galaxy-list/galaxy-list.component";
import {GalaxyImageComponent} from "../galaxy-image/galaxy-image.component";
import {GalaxyImageDTO} from "../galaxy.model";
import {ActionBarComponent, ActionBarButton} from "../../shared/components/action-bar/action-bar.component";

@Component({
    selector: 'app-galaxy-shell',
    imports: [
        MatTabGroup,
        MatTab,
        GalaxyParamComponent,
        GalaxyListComponent,
        GalaxyImageComponent,
        ActionBarComponent
    ],
    templateUrl: './galaxy-shell.component.html',
    styleUrl: './galaxy-shell.component.scss'
})
export class GalaxyShellComponent {
  @ViewChild(MatTabGroup) tabGroup!: MatTabGroup;
  @ViewChild(GalaxyParamComponent) paramComponent!: GalaxyParamComponent;

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
        action: () => param.generateGalaxy()
      },
      {
        label: 'Sauvegarder',
        color: 'accent',
        disabled: !param.canSave(),
        tooltip: param.getSaveTooltip(),
        action: () => param.saveGalaxy()
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
    return param && param.generatedImageUrl ? param.getParametersSummary() : null;
  }

  onViewRequested(galaxy: GalaxyImageDTO): void {
    if (this.paramComponent) {
      this.paramComponent.loadGalaxy(galaxy);
    }
    this.tabGroup.selectedIndex = 0;
  }
}
