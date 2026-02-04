import {Component, ViewChild} from '@angular/core';
import {MatCard, MatCardContent} from "@angular/material/card";
import {SbgbParamComponent} from "../sbgb-param/sbgb-param.component";
import {SbgbImageComponent} from "../sbgb-image/sbgb-image.component";
import {SbgbListComponent} from "../sbgb-list/sbgb-list.component";
import {MatTabGroup, MatTabsModule} from "@angular/material/tabs";

import {Sbgb} from "../sbgb.model";

@Component({
  selector: 'app-sbgb-shell',
  standalone: true,
  imports: [
    MatCard,
    MatCardContent,
    SbgbParamComponent,
    SbgbImageComponent,
    SbgbListComponent,
    MatTabsModule
  ],
  templateUrl: './sbgb-shell.component.html',
  styleUrl: './sbgb-shell.component.scss'
})
export class SbgbShellComponent {
  @ViewChild(MatTabGroup) tabGroup!: MatTabGroup;
  @ViewChild(SbgbParamComponent) paramComponent!: SbgbParamComponent;
  @ViewChild(SbgbListComponent) listComponent!: SbgbListComponent;

  switchToGenerator() {
    this.tabGroup.selectedIndex = 0;
  }

  onViewRequested(sbgb: Sbgb) {
    if (this.paramComponent && this.paramComponent.hasUnsavedChanges()) {
      if (confirm('Vous avez des modifications non enregistrées. Voulez-vous vraiment charger une autre image ?')) {
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
    this.switchToGenerator();
  }
}
