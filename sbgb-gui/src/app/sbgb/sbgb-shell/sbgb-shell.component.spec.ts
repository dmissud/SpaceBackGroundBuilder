import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

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
    fixture.detectChanges(); // Resolve ExpressionChangedAfterItHasBeenCheckedError from child component updating actionButtons
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain image-rating-container class in image template area', () => {
    const container = fixture.debugElement.query(By.css('.image-rating-container'));
    expect(container).toBeTruthy();
  });
});
