import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SbgbTopmenuComponent } from './sbgb-topmenu.component';

describe('SbgbTopmenuComponent', () => {
  let component: SbgbTopmenuComponent;
  let fixture: ComponentFixture<SbgbTopmenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SbgbTopmenuComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SbgbTopmenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
