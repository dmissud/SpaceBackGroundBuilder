import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Store} from "@ngrx/store";
import {selectSbgbs} from "../state/sbgb.selectors";
import {SbgbPageActions} from "../state/sbgb.actions";
import {Sbgb} from "../sbgb.model";
import {map} from "rxjs/operators";
import {LibraryItem, LibraryListComponent} from "../../shared/components/library-list/library-list.component";

@Component({
    selector: 'app-sbgb-list',
    standalone: true,
    imports: [LibraryListComponent],
    template: `
      <app-library-list
        title="Ciels étoilés enregistrés"
        [items]="libraryItems"
        [isLoading]="false"
        emptyMessage="Aucun ciel étoilé enregistré. Créez votre premier ciel étoilé !"
        [showRefreshButton]="false"
        (viewRequested)="onViewRequested($event)">
      </app-library-list>
    `,
    styles: ``
})
export class SbgbListComponent implements OnInit {

  sbgbs: Sbgb[] = [];
  libraryItems: LibraryItem[] = [];

  @Output() viewRequested = new EventEmitter<Sbgb>();

  constructor(private store: Store) {
    this.store.select(selectSbgbs).pipe(
      map(sbgbs => sbgbs.map(s => ({
        id: s.id,
        name: s.name || '',
        description: s.description || '',
        width: s.imageStructure.width,
        height: s.imageStructure.height,
        seed: s.imageStructure.seed,
        note: s.note ?? 0
      })))
    ).subscribe(items => {
      this.libraryItems = items;
    });

    this.store.select(selectSbgbs).subscribe(sbgbs => {
      this.sbgbs = sbgbs;
    });
  }

  ngOnInit(): void {
    this.store.dispatch(SbgbPageActions.loadSbgbs());
  }

  onViewRequested(item: LibraryItem): void {
    const sbgb = this.sbgbs.find(s => s.id === item.id);
    if (sbgb) {
      this.viewRequested.emit(sbgb);
    }
  }

  public confirmView(sbgb: Sbgb): void {
    this.store.dispatch(SbgbPageActions.selectSbgb({sbgb}));
    this.store.dispatch(SbgbPageActions.buildSbgb({sbgb, build: true}));
  }
}
