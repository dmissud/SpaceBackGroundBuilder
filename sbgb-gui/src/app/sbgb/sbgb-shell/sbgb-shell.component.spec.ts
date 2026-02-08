import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SbgbShellComponent} from './sbgb-shell.component';

describe('SbgbShellComponent', () => {
  let component: SbgbShellComponent;
  let fixture: ComponentFixture<SbgbShellComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SbgbShellComponent]
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
