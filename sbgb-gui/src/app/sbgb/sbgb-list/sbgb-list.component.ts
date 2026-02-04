import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Store} from "@ngrx/store";
import {selectSbgbs} from "../state/sbgb.selectors";
import {SbgbPageActions} from "../state/sbgb.actions";
import {Observable} from "rxjs";
import {Sbgb} from "../sbgb.model";
import {AsyncPipe, NgIf} from "@angular/common";
import {MatTableModule} from "@angular/material/table";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";

@Component({
  selector: 'app-sbgb-list',
  standalone: true,
  imports: [
    AsyncPipe,
    NgIf,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule
  ],
  templateUrl: './sbgb-list.component.html',
  styleUrl: './sbgb-list.component.scss'
})
export class SbgbListComponent implements OnInit {

  sbgbs$: Observable<Sbgb[]>;
  displayedColumns: string[] = ['name', 'description', 'seed', 'actions'];

  @Output() viewRequested = new EventEmitter<Sbgb>();

  constructor(private store: Store) {
    this.sbgbs$ = this.store.select(selectSbgbs);
  }

  ngOnInit(): void {
    this.store.dispatch(SbgbPageActions.loadSbgbs());
  }

  viewImage(sbgb: Sbgb): void {
    this.viewRequested.emit(sbgb);
  }

  public confirmView(sbgb: Sbgb): void {
    this.store.dispatch(SbgbPageActions.selectSbgb({sbgb}));
    this.store.dispatch(SbgbPageActions.buildSbgb({sbgb, build: true}));
  }
}
