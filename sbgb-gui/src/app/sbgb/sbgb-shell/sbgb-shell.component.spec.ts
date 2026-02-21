import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SbgbShellComponent } from './sbgb-shell.component';
import { provideRouter } from '@angular/router';
import { provideMockStore } from '@ngrx/store/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable, of } from 'rxjs';

describe('SbgbShellComponent', () => {
  let component: SbgbShellComponent;
  let fixture: ComponentFixture<SbgbShellComponent>;
  let actions$: Observable<any>;

  beforeEach(async () => {
    actions$ = of();
    await TestBed.configureTestingModule({
      imports: [SbgbShellComponent],
      providers: [
        provideRouter([]),
        provideMockStore({ initialState: { sbgbs: { building: false, builtSbgb: null } } }),
        provideMockActions(() => actions$)
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SbgbShellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
