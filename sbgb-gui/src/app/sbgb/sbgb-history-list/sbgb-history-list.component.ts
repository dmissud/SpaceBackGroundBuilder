import {Component, EventEmitter, Input, Output} from '@angular/core';
import {STAR_RATING_VALUES} from "../sbgb.constants";
import {MatExpansionModule} from '@angular/material/expansion';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';
import {NoiseBaseStructureDto, NoiseCosmeticRenderDto} from '../sbgb.model';

@Component({
  selector: 'app-sbgb-history-list',
  standalone: true,
  imports: [MatExpansionModule, MatIconModule, MatButtonModule, MatTooltipModule],
  templateUrl: './sbgb-history-list.component.html',
  styleUrl: './sbgb-history-list.component.scss'
})
export class SbgbHistoryListComponent {

  readonly starValues = STAR_RATING_VALUES;

  @Input() bases: NoiseBaseStructureDto[] = [];
  @Input() rendersByBaseId: Record<string, NoiseCosmeticRenderDto[]> = {};

  @Output() loadRendersRequested = new EventEmitter<string>();
  @Output() deleteRenderRequested = new EventEmitter<string>();
  @Output() renderSelected = new EventEmitter<NoiseCosmeticRenderDto>();

  /** Bases ayant au moins un rendu chargé ou dont les rendus n'ont pas encore été demandés. */
  get visibleBases(): NoiseBaseStructureDto[] {
    return this.bases.filter(base => this.hasRenders(base.id));
  }

  /** Retourne true si l'étoile `star` doit être affichée pleine pour la note donnée. */
  isFilled(star: number, note: number): boolean {
    return star <= note;
  }

  /** Retourne les rendus chargés pour une base, ou un tableau vide si non encore chargés. */
  rendersFor(baseId: string): NoiseCosmeticRenderDto[] {
    return this.rendersByBaseId[baseId] ?? [];
  }

  /** Émet une demande de chargement des rendus lorsque l'accordéon d'une base est déployé. */
  onBaseExpanded(baseId: string): void {
    this.loadRendersRequested.emit(baseId);
  }

  /** Émet la demande de suppression d'un rendu. */
  onDeleteRender(renderId: string): void {
    this.deleteRenderRequested.emit(renderId);
  }

  /** Émet le rendu sélectionné pour le charger dans le générateur. */
  onRenderSelected(render: NoiseCosmeticRenderDto): void {
    this.renderSelected.emit(render);
  }

  private hasRenders(baseId: string | undefined): boolean {
    if (!baseId) return false;
    const renders = this.rendersByBaseId[baseId];
    return renders === undefined || renders.length > 0;
  }
}
