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

  @ContentChild('paramContent') paramContent!: TemplateRef<any>;
  @ContentChild('actionBarContent') actionBarContent!: TemplateRef<any>;
  @ContentChild('imageContent') imageContent!: TemplateRef<any>;
  @ContentChild('libraryContent') libraryContent!: TemplateRef<any>;

  switchToGenerator(): void {
    this.tabGroup.selectedIndex = 0;
  }
}
