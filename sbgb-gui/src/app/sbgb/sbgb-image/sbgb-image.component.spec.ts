import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SbgbImageComponent } from './sbgb-image.component';

describe('SbgbImageComponent', () => {
  let component: SbgbImageComponent;
  let fixture: ComponentFixture<SbgbImageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SbgbImageComponent]
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
