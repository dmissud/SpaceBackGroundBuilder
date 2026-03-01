import { Component, ViewChild, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { GalaxyParamComponent } from "../galaxy-param/galaxy-param.component";
import { GalaxyHistoryListComponent } from "../galaxy-history-list/galaxy-history-list.component";
import { GalaxyBaseStructureDto, GalaxyCosmeticRenderDto } from "../galaxy.model";
import { ActionBarComponent, ActionBarButton } from "../../shared/components/action-bar/action-bar.component";
import { GeneratorShellComponent } from "../../shared/components/generator-shell/generator-shell.component";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTooltipModule } from "@angular/material/tooltip";
import { Store } from "@ngrx/store";
import { selectRenders, selectSelectedRenderId } from "../state/galaxy.selectors";
import { GalaxyPageActions } from "../state/galaxy.actions";

@Component({
  selector: 'app-galaxy-shell',
  imports: [
    GeneratorShellComponent,
    GalaxyParamComponent,
    GalaxyHistoryListComponent,
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

  renders = this.store.selectSignal(selectRenders);
  selectedRenderId = this.store.selectSignal(selectSelectedRenderId);

  constructor(private store: Store, private cdr: ChangeDetectorRef) { }

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

  onSelectRender(render: GalaxyCosmeticRenderDto): void {
    this.store.dispatch(GalaxyPageActions.selectRender({renderId: render.id}));
    this.store.dispatch(GalaxyPageActions.applyRenderCosmetics({render}));
  }

  onHistoryRenderSelected(event: {base: GalaxyBaseStructureDto, render: GalaxyCosmeticRenderDto}): void {
    if (this.paramComponent) {
      this.paramComponent.loadBase(event.base);
      this.store.dispatch(GalaxyPageActions.loadRendersForBase({baseId: event.base.id}));
    }
    this.onSelectRender(event.render);
    this.shell.switchToGenerator();
  }

  onDeleteRender(renderId: string): void {
    this.store.dispatch(GalaxyPageActions.deleteRender({renderId}));
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

  canBuild(): boolean {
    return this.paramComponent?.canBuild() || false;
  }

  getSummary(): string | null {
    const param = this.paramComponent;
    return param && param.generatedImageUrl ? param.getParametersSummary() : null;
  }

  onViewRequested(base: GalaxyBaseStructureDto): void {
    if (this.paramComponent) {
      this.paramComponent.loadBase(base);
    }
    this.shell.switchToGenerator();
  }
}
