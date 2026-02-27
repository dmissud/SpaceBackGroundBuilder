import {Component, EventEmitter, Input, Output} from '@angular/core';
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

  readonly starValues = [1, 2, 3, 4, 5];

  @Input() bases: NoiseBaseStructureDto[] = [];
  @Input() rendersByBaseId: Record<string, NoiseCosmeticRenderDto[]> = {};

  @Output() loadRendersRequested = new EventEmitter<string>();
  @Output() deleteRenderRequested = new EventEmitter<string>();
  @Output() renderSelected = new EventEmitter<NoiseCosmeticRenderDto>();

  get visibleBases(): NoiseBaseStructureDto[] {
    return this.bases.filter(base => this.hasRenders(base.id));
  }

  isFilled(star: number, note: number): boolean {
    return star <= note;
  }

  rendersFor(baseId: string): NoiseCosmeticRenderDto[] {
    return this.rendersByBaseId[baseId] ?? [];
  }

  onBaseExpanded(baseId: string): void {
    this.loadRendersRequested.emit(baseId);
  }

  onDeleteRender(renderId: string): void {
    this.deleteRenderRequested.emit(renderId);
  }

  onRenderSelected(render: NoiseCosmeticRenderDto): void {
    this.renderSelected.emit(render);
  }

  private hasRenders(baseId: string | undefined): boolean {
    if (!baseId) return false;
    const renders = this.rendersByBaseId[baseId];
    return renders === undefined || renders.length > 0;
  }
}
