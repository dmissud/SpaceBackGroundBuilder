import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {MatTableModule} from "@angular/material/table";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";
import {GalaxyService} from "../galaxy.service";
import {GalaxyImageDTO} from "../galaxy.model";
import {MatSnackBar} from "@angular/material/snack-bar";


@Component({
    selector: 'app-galaxy-list',
    imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule
],
    templateUrl: './galaxy-list.component.html',
    styleUrl: './galaxy-list.component.scss'
})
export class GalaxyListComponent implements OnInit {

  displayedColumns: string[] = ['name', 'description', 'dimensions', 'structure', 'seed', 'actions'];
  galaxies: GalaxyImageDTO[] = [];
  isLoading = false;

  @Output() viewRequested = new EventEmitter<GalaxyImageDTO>();

  constructor(
    private galaxyService: GalaxyService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadGalaxies();
  }

  loadGalaxies(): void {
    this.isLoading = true;
    this.galaxyService.getAllGalaxies().subscribe({
      next: (galaxies) => {
        this.galaxies = galaxies;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading galaxies:', error);
        this.snackBar.open('Error loading galaxies', 'Close', {duration: 3000});
        this.isLoading = false;
      }
    });
  }

  getDimensions(galaxy: GalaxyImageDTO): string {
    return `${galaxy.galaxyStructure.width}x${galaxy.galaxyStructure.height}`;
  }

  getStructureInfo(galaxy: GalaxyImageDTO): string {
    const type = galaxy.galaxyStructure.galaxyType || 'SPIRAL';
    if (type === 'VORONOI_CLUSTER') {
      return `Voronoi (${galaxy.galaxyStructure.voronoiParameters?.clusterCount || 80} clusters)`;
    } else if (type === 'ELLIPTICAL') {
      return `Elliptical (index ${galaxy.galaxyStructure.ellipticalParameters?.sersicIndex || 4.0})`;
    } else if (type === 'RING') {
      return `Ring (radius ${galaxy.galaxyStructure.ringParameters?.ringRadius || 900})`;
    } else if (type === 'IRREGULAR') {
      return `Irregular (${galaxy.galaxyStructure.irregularParameters?.irregularClumpCount || 15} clumps)`;
    }
    return `Spiral (${galaxy.galaxyStructure.spiralParameters?.numberOfArms || 2} arms)`;
  }

  viewGalaxy(galaxy: GalaxyImageDTO): void {
    this.viewRequested.emit(galaxy);
  }
}
