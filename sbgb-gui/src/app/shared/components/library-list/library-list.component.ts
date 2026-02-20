import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatTableModule} from "@angular/material/table";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";

export interface LibraryItem {
  id?: string;
  name: string;
  description: string;
  width: number;
  height: number;
  seed: number;
}

@Component({
  selector: 'app-library-list',
  standalone: true,
  imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './library-list.component.html',
  styleUrl: './library-list.component.scss'
})
export class LibraryListComponent {
  @Input() title: string = 'Bibliothèque';
  @Input() items: LibraryItem[] = [];
  @Input() isLoading: boolean = false;
  @Input() emptyMessage: string = 'Aucun élément enregistré';
  @Input() showRefreshButton: boolean = true;

  @Output() viewRequested = new EventEmitter<LibraryItem>();
  @Output() refreshRequested = new EventEmitter<void>();

  displayedColumns: string[] = ['name', 'description', 'dimensions', 'seed', 'actions'];

  getDimensions(item: LibraryItem): string {
    return `${item.width}x${item.height}`;
  }

  viewItem(item: LibraryItem): void {
    this.viewRequested.emit(item);
  }

  refresh(): void {
    this.refreshRequested.emit();
  }
}
