import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideMockStore } from '@ngrx/store/testing';

import { SbgbImageComponent } from './sbgb-image.component';

describe('SbgbImageComponent', () => {
  let component: SbgbImageComponent;
  let fixture: ComponentFixture<SbgbImageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SbgbImageComponent],
      providers: [provideMockStore({ initialState: { sbgbs: { building: false, builtSbgb: null } } })]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SbgbImageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
