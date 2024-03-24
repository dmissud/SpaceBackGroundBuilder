import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SbgbParamComponent} from './sbgb-param.component';

describe('SbgbParamComponent', () => {
  let component: SbgbParamComponent;
  let fixture: ComponentFixture<SbgbParamComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SbgbParamComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SbgbParamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
