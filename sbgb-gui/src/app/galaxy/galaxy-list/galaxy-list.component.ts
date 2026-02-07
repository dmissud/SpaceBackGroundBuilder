import {Component, OnInit} from '@angular/core';
import {MatTableModule} from "@angular/material/table";
import {MatButton} from "@angular/material/button";
import {GalaxyService} from "../galaxy.service";
import {GalaxyImageDTO} from "../galaxy.model";
import {MatSnackBar} from "@angular/material/snack-bar";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-galaxy-list',
  standalone: true,
  imports: [
    MatTableModule,
    MatButton,
    CommonModule
  ],
  templateUrl: './galaxy-list.component.html',
  styleUrl: './galaxy-list.component.scss'
})
export class GalaxyListComponent implements OnInit {

  displayedColumns: string[] = ['name', 'description', 'dimensions', 'arms', 'seed', 'actions'];
  galaxies: GalaxyImageDTO[] = [];
  isLoading = false;

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

  getArmInfo(galaxy: GalaxyImageDTO): string {
    return `${galaxy.galaxyStructure.numberOfArms} arms`;
  }
}
