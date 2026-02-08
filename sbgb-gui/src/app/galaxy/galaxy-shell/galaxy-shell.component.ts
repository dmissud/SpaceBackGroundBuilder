import {Component, ViewChild} from '@angular/core';
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {GalaxyParamComponent} from "../galaxy-param/galaxy-param.component";
import {GalaxyListComponent} from "../galaxy-list/galaxy-list.component";
import {GalaxyImageDTO} from "../galaxy.model";

@Component({
    selector: 'app-galaxy-shell',
    imports: [
        MatTabGroup,
        MatTab,
        GalaxyParamComponent,
        GalaxyListComponent
    ],
    templateUrl: './galaxy-shell.component.html',
    styleUrl: './galaxy-shell.component.scss'
})
export class GalaxyShellComponent {
  @ViewChild(MatTabGroup) tabGroup!: MatTabGroup;
  @ViewChild(GalaxyParamComponent) paramComponent!: GalaxyParamComponent;

  onViewRequested(galaxy: GalaxyImageDTO): void {
    if (this.paramComponent) {
      this.paramComponent.loadGalaxy(galaxy);
    }
    this.tabGroup.selectedIndex = 0;
  }
}
