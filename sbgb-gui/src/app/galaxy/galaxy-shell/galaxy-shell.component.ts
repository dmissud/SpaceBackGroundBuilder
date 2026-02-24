import { Component, ViewChild, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { GalaxyParamComponent } from "../galaxy-param/galaxy-param.component";
import { GalaxyListComponent } from "../galaxy-list/galaxy-list.component";
import { GalaxyImageComponent } from "../galaxy-image/galaxy-image.component";
import { GalaxyImageDTO } from "../galaxy.model";
import { ActionBarComponent, ActionBarButton } from "../../shared/components/action-bar/action-bar.component";
import { GeneratorShellComponent } from "../../shared/components/generator-shell/generator-shell.component";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTooltipModule } from "@angular/material/tooltip";

@Component({
  selector: 'app-galaxy-shell',
  imports: [
    GeneratorShellComponent,
    GalaxyParamComponent,
    GalaxyListComponent,
    GalaxyImageComponent,
    ActionBarComponent,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule
  ],
  templateUrl: './galaxy-shell.component.html',
  styleUrl: './galaxy-shell.component.scss'
})
export class GalaxyShellComponent implements AfterViewInit {
  @ViewChild(GeneratorShellComponent) shell!: GeneratorShellComponent;
  @ViewChild(GalaxyParamComponent) paramComponent!: GalaxyParamComponent;

  constructor(private cdr: ChangeDetectorRef) { }

  ngAfterViewInit() {
    this.cdr.detectChanges();
  }

  get isGenerating(): boolean {
    return this.paramComponent?.isGenerating || false;
  }

  get generatedImageUrl(): string | null {
    return this.paramComponent?.generatedImageUrl || null;
  }

  get currentNote(): number {
    return this.paramComponent?.currentNote || 0;
  }

  get starValues(): number[] {
    return this.paramComponent?.starValues || [1, 2, 3, 4, 5];
  }

  canRate(): boolean {
    return this.paramComponent?.canRate() || false;
  }

  getRatingTooltip(): string {
    return this.paramComponent?.getRatingTooltip() || '';
  }

  onNoteSelected(note: number): void {
    if (this.paramComponent) {
      this.paramComponent.onNoteSelected(note);
    }
  }

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
    this.shell.switchToGenerator();
  }
}
