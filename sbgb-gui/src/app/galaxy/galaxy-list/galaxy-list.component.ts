import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Subject, takeUntil} from "rxjs";
import {GalaxyService} from "../galaxy.service";
import {GalaxyBaseStructureDto} from "../galaxy.model";
import {MatSnackBar} from "@angular/material/snack-bar";
import {LibraryItem, LibraryListComponent} from "../../shared/components/library-list/library-list.component";


@Component({
  selector: 'app-galaxy-list',
  standalone: true,
  imports: [LibraryListComponent],
  template: `
      <app-library-list
        title="Galaxies enregistrées"
        [items]="libraryItems"
        [isLoading]="isLoading"
        emptyMessage="Aucune galaxie enregistrée. Créez votre première galaxie !"
        [showRefreshButton]="true"
        [showNameColumn]="false"
        (viewRequested)="onViewRequested($event)"
        (refreshRequested)="loadBases()">
      </app-library-list>
    `,
  styles: ``
})
export class GalaxyListComponent implements OnInit {

  bases: GalaxyBaseStructureDto[] = [];
  libraryItems: LibraryItem[] = [];
  isLoading = false;

  @Output() viewRequested = new EventEmitter<GalaxyBaseStructureDto>();
  private destroy$ = new Subject<void>();

  constructor(
    private galaxyService: GalaxyService,
    private snackBar: MatSnackBar
  ) {
  }

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
    this.isLoading = true;
    this.galaxyService.getAllBases().subscribe({
      next: (bases) => {
        this.bases = bases;
        this.libraryItems = bases.map(b => ({
          id: b.id,
          name: '',
          description: b.description,
          width: b.width,
          height: b.height,
          seed: b.seed,
          note: b.maxNote ?? 0
        }));
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading galaxy bases:', error);
        this.snackBar.open('Erreur lors du chargement des galaxies', 'Fermer', {duration: 3000});
        this.isLoading = false;
      }
    });
  }

  onViewRequested(item: LibraryItem): void {
    const base = this.bases.find(b => b.id === item.id);
    if (base) {
      this.viewRequested.emit(base);
    }
  }
}
