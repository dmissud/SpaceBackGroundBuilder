import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Subject, takeUntil} from "rxjs";
import {GalaxyService} from "../galaxy.service";
import {GalaxyImageDTO} from "../galaxy.model";
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
        (viewRequested)="onViewRequested($event)"
        (refreshRequested)="loadGalaxies()">
      </app-library-list>
    `,
  styles: ``
})
export class GalaxyListComponent implements OnInit {

  galaxies: GalaxyImageDTO[] = [];
  libraryItems: LibraryItem[] = [];
  isLoading = false;

  @Output() viewRequested = new EventEmitter<GalaxyImageDTO>();
  private destroy$ = new Subject<void>();

  constructor(
    private galaxyService: GalaxyService,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit(): void {
    this.loadGalaxies();

    this.galaxyService.galaxySaved$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.loadGalaxies());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadGalaxies(): void {
    this.isLoading = true;
    this.galaxyService.getAllGalaxies().subscribe({
      next: (galaxies) => {
        this.galaxies = galaxies;
        this.libraryItems = galaxies.map(g => ({
          id: g.id,
          name: g.name,
          description: g.description,
          width: g.galaxyStructure.width,
          height: g.galaxyStructure.height,
          seed: g.galaxyStructure.seed
        }));
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading galaxies:', error);
        this.snackBar.open('Erreur lors du chargement des galaxies', 'Fermer', {duration: 3000});
        this.isLoading = false;
      }
    });
  }

  onViewRequested(item: LibraryItem): void {
    const galaxy = this.galaxies.find(g => g.id === item.id);
    if (galaxy) {
      this.viewRequested.emit(galaxy);
    }
  }
}
