import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Store} from "@ngrx/store";
import {selectBases} from "../state/sbgb.selectors";
import {SbgbPageActions} from "../state/sbgb.actions";
import {NoiseBaseStructureDto, Sbgb} from "../sbgb.model";
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
        [showNameColumn]="false"
        (viewRequested)="onViewRequested($event)">
      </app-library-list>
    `,
    styles: ``
})
export class SbgbListComponent implements OnInit {

  bases: NoiseBaseStructureDto[] = [];
  libraryItems: LibraryItem[] = [];

  @Output() viewRequested = new EventEmitter<Sbgb>();

  constructor(private store: Store) {
    this.store.select(selectBases).pipe(
      map(bases => bases.map(b => ({
        id: b.id,
        name: '',
        description: b.description || '',
        width: b.width,
        height: b.height,
        seed: b.seed,
        note: b.maxNote ?? 0
      })))
    ).subscribe(items => {
      this.libraryItems = items;
    });

    this.store.select(selectBases).subscribe(bases => {
      this.bases = bases;
    });
  }

  ngOnInit(): void {
    this.store.dispatch(SbgbPageActions.loadSbgbs());
  }

  onViewRequested(item: LibraryItem): void {
    const base = this.bases.find(b => b.id === item.id);
    if (base) {
      this.viewRequested.emit(this.toSbgb(base));
    }
  }

  public confirmView(sbgb: Sbgb): void {
    this.store.dispatch(SbgbPageActions.selectSbgb({sbgb}));
    this.store.dispatch(SbgbPageActions.buildSbgb({sbgb, build: true}));
  }

  private toSbgb(base: NoiseBaseStructureDto): Sbgb {
    return {
      id: base.id,
      description: base.description,
      note: base.maxNote,
      imageStructure: {
        width: base.width,
        height: base.height,
        seed: base.seed,
        octaves: base.octaves,
        persistence: base.persistence,
        lacunarity: base.lacunarity,
        scale: base.scale,
        noiseType: base.noiseType,
        preset: 'CUSTOM',
        useMultiLayer: base.useMultiLayer
      },
      imageColor: {
        back: '#000000',
        middle: '#FFA500',
        fore: '#FFFFFF',
        backThreshold: 0.7,
        middleThreshold: 0.75,
        interpolationType: 'LINEAR'
      }
    };
  }
}
