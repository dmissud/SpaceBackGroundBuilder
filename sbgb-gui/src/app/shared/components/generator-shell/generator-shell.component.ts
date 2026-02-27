import {Component, ContentChild, Input, TemplateRef, ViewChild} from '@angular/core';
import {MatTabGroup, MatTabsModule} from "@angular/material/tabs";
import {NgTemplateOutlet} from "@angular/common";

@Component({
  selector: 'app-generator-shell',
  standalone: true,
  imports: [
    MatTabsModule,
    NgTemplateOutlet
  ],
  templateUrl: './generator-shell.component.html',
  styleUrl: './generator-shell.component.scss'
})
export class GeneratorShellComponent {
  @Input() generatorTabLabel: string = 'Générateur';
  @Input() libraryTabLabel: string = 'Bibliothèque';

  @ViewChild(MatTabGroup) tabGroup!: MatTabGroup;

  @ContentChild('paramContent') paramContent!: TemplateRef<unknown>;
  @ContentChild('actionBarContent') actionBarContent!: TemplateRef<unknown>;
  @ContentChild('imageContent') imageContent!: TemplateRef<unknown>;
  @ContentChild('rendersContent') rendersContent!: TemplateRef<unknown>;
  @ContentChild('libraryContent') libraryContent!: TemplateRef<unknown>;

  /** Navigue programmatiquement vers l'onglet Générateur (ex. : après sélection depuis la bibliothèque). */
  switchToGenerator(): void {
    this.tabGroup.selectedIndex = 0;
  }
}
