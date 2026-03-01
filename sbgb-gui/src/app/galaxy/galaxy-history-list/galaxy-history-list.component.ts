import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Subject, takeUntil} from 'rxjs';
import {GalaxyService} from '../galaxy.service';
import {GalaxyBaseStructureDto, GalaxyCosmeticRenderDto} from '../galaxy.model';

@Component({
  selector: 'app-galaxy-history-list',
  standalone: true,
  imports: [
    CommonModule,
    MatExpansionModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatTooltipModule
  ],
  templateUrl: './galaxy-history-list.component.html',
  styleUrls: ['./galaxy-history-list.component.scss']
})
export class GalaxyHistoryListComponent implements OnInit, OnDestroy {
  bases: GalaxyBaseStructureDto[] = [];
  rendersByBase: Map<string, GalaxyCosmeticRenderDto[]> = new Map();
  loadingBases = false;
  loadingRenders: Set<string> = new Set();

  @Output() viewRequested = new EventEmitter<GalaxyBaseStructureDto>();
  @Output() renderSelected = new EventEmitter<{base: GalaxyBaseStructureDto, render: GalaxyCosmeticRenderDto}>();

  private destroy$ = new Subject<void>();

  constructor(
    private galaxyService: GalaxyService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadBases();

    this.galaxyService.galaxySaved$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.loadBases());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadBases(): void {
    this.loadingBases = true;
    this.galaxyService.getAllBases().subscribe({
      next: (bases) => {
        this.bases = bases;
        this.loadingBases = false;
      },
      error: (error) => {
        console.error('Error loading galaxy bases:', error);
        this.snackBar.open('Erreur lors du chargement des galaxies', 'Fermer', {duration: 3000});
        this.loadingBases = false;
      }
    });
  }

  onPanelOpened(base: GalaxyBaseStructureDto): void {
    if (!this.rendersByBase.has(base.id)) {
      this.loadRenders(base.id);
    }
  }

  loadRenders(baseId: string): void {
    this.loadingRenders.add(baseId);
    this.galaxyService.getRendersForBase(baseId).subscribe({
      next: (renders) => {
        this.rendersByBase.set(baseId, renders);
        this.loadingRenders.delete(baseId);
      },
      error: (error) => {
        console.error(`Error loading renders for base ${baseId}:`, error);
        this.loadingRenders.delete(baseId);
      }
    });
  }

  onSelectBase(base: GalaxyBaseStructureDto): void {
    this.viewRequested.emit(base);
  }

  onSelectRender(base: GalaxyBaseStructureDto, render: GalaxyCosmeticRenderDto): void {
    this.renderSelected.emit({base, render});
  }

  onDeleteRender(event: Event, baseId: string, renderId: string): void {
    event.stopPropagation();
    if (confirm('Supprimer ce rendu ?')) {
      this.galaxyService.deleteRender(renderId).subscribe({
        next: () => {
          const renders = this.rendersByBase.get(baseId) || [];
          const updatedRenders = renders.filter(r => r.id !== renderId);
          if (updatedRenders.length === 0) {
             this.rendersByBase.delete(baseId);
             this.loadBases(); // Reload bases as the base might have been deleted if it was the last render
          } else {
            this.rendersByBase.set(baseId, updatedRenders);
          }
          this.snackBar.open('Rendu supprimé', 'Fermer', {duration: 2000});
        },
        error: (error) => {
          console.error('Error deleting render:', error);
          this.snackBar.open('Erreur lors de la suppression', 'Fermer', {duration: 3000});
        }
      });
    }
  }

  getStarValue(note: number): number[] {
    return Array(5).fill(0).map((_, i) => i < note ? 1 : 0);
  }
}
