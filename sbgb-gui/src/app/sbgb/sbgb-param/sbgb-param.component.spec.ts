import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBar } from '@angular/material/snack-bar';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable, of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { SbgbParamComponent } from './sbgb-param.component';
import { SbgbPageActions } from '../state/sbgb.actions';
import { Sbgb } from '../sbgb.model';

describe('SbgbParamComponent', () => {
  let component: SbgbParamComponent;
  let fixture: ComponentFixture<SbgbParamComponent>;
  let store: MockStore;
  let snackBar: jest.Mocked<MatSnackBar>;
  let actions$: Observable<any>;

  afterEach(() => {
    jest.restoreAllMocks();
  });

  const mockSbgb: Sbgb = {
    id: '1',
    name: 'Test Image',
    description: 'Test Description',
    imageStructure: {
      width: 4000,
      height: 4000,
      seed: 2569,
      octaves: 1,
      persistence: 0.5,
      lacunarity: 2.0,
      scale: 100,
      preset: 'CUSTOM',
      useMultiLayer: false
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

  beforeEach(async () => {
    actions$ = of();
    snackBar = { open: jest.fn() } as any;

    await TestBed.configureTestingModule({
      imports: [
        SbgbParamComponent,
        ReactiveFormsModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: MatSnackBar, useValue: snackBar },
        provideMockActions(() => actions$),
        provideMockStore({
          initialState: {
            sbgbs: {
              sbgb: null,
              infoMessage: '',
              errorMessage: ''
            }
          }
        })
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SbgbParamComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(MockStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('isModified', () => {
    beforeEach(() => {
      // @ts-ignore - Accès à la propriété privée pour le test
      component.loadedFromDbSbgb = mockSbgb;
      // @ts-ignore
      component.isBuilt = true;
      // @ts-ignore
      component.isModifiedSinceBuild = false;
    });

    it('should return false if nothing is modified', () => {
      const current = JSON.parse(JSON.stringify(mockSbgb));
      // @ts-ignore
      expect(component.isModified(current, mockSbgb)).toBeFalsy();
    });

    it('should return true if name is modified', () => {
      const current = JSON.parse(JSON.stringify(mockSbgb));
      current.name = 'New Name';
      // @ts-ignore
      expect(component.isModified(current, mockSbgb)).toBeTruthy();
    });

    it('should return false if description is modified', () => {
      const current = JSON.parse(JSON.stringify(mockSbgb));
      current.description = 'New Description';
      // @ts-ignore
      expect(component.isModified(current, mockSbgb)).toBeFalsy();
    });

    it('should return true if width is modified', () => {
      const current = JSON.parse(JSON.stringify(mockSbgb));
      current.imageStructure.width = 1000;
      // @ts-ignore
      expect(component.isModified(current, mockSbgb)).toBeTruthy();
    });

    it('should return true if color is modified', () => {
      const current = JSON.parse(JSON.stringify(mockSbgb));
      current.imageColor.back = '#123456';
      // @ts-ignore
      expect(component.isModified(current, mockSbgb)).toBeTruthy();
    });

    it('should return true if threshold is modified', () => {
      const current = JSON.parse(JSON.stringify(mockSbgb));
      current.imageColor.backThreshold = 0.1;
      // @ts-ignore
      expect(component.isModified(current, mockSbgb)).toBeTruthy();
    });
  });


  describe('UI State', () => {
    it('should return false for canSave if not built', () => {
      // @ts-ignore
      component.isBuilt = false;
      expect(component.canSave()).toBeFalsy();
    });

    it('should return false for canSave if modified since build', () => {
      // @ts-ignore
      component.isBuilt = true;
      // @ts-ignore
      component.isModifiedSinceBuild = true;
      expect(component.canSave()).toBeFalsy();
    });

    it('should return true for canSave if built and not modified since build', () => {
      // @ts-ignore
      component.isBuilt = true;
      // @ts-ignore
      component.isModifiedSinceBuild = false;
      // @ts-ignore - builtSbgb doit être défini pour que canSave() retourne true
      component.builtSbgb = mockSbgb;
      expect(component.canSave()).toBeTruthy();
    });

    it('should return false for canDownload if not built', () => {
      // @ts-ignore
      component.isBuilt = false;
      expect(component.canDownload()).toBeFalsy();
    });

    it('should return false for canDownload if modified since build', () => {
      // @ts-ignore
      component.isBuilt = true;
      // @ts-ignore
      component.isModifiedSinceBuild = true;
      expect(component.canDownload()).toBeFalsy();
    });

    it('should return true for canDownload if built and not modified since build', () => {
      // @ts-ignore
      component.isBuilt = true;
      // @ts-ignore
      component.isModifiedSinceBuild = false;
      expect(component.canDownload()).toBeTruthy();
    });
  });

  describe('downloadImage', () => {
    it('should create an anchor element and trigger download with form name', () => {
      const fakeDataUrl = 'data:image/png;base64,iVBORw0KGgo=';
      jest.spyOn(store, 'selectSignal').mockReturnValue((() => fakeDataUrl) as any);

      const clickSpy = jest.fn();
      const fakeLink = { href: '', download: '', click: clickSpy } as any;
      jest.spyOn(document, 'createElement').mockReturnValue(fakeLink);

      component['baseForm'].patchValue({ name: 'my-stars' }, { emitEvent: false });

      component.downloadImage();

      expect(document.createElement).toHaveBeenCalledWith('a');
      expect(fakeLink.href).toBe(fakeDataUrl);
      expect(fakeLink.download).toBe('my-stars.png');
      expect(clickSpy).toHaveBeenCalled();
    });

    it('should use default name when form name is empty', () => {
      const fakeDataUrl = 'data:image/png;base64,iVBORw0KGgo=';
      jest.spyOn(store, 'selectSignal').mockReturnValue((() => fakeDataUrl) as any);

      const clickSpy = jest.fn();
      const fakeLink = { href: '', download: '', click: clickSpy } as any;
      jest.spyOn(document, 'createElement').mockReturnValue(fakeLink);

      component['baseForm'].patchValue({ name: '' }, { emitEvent: false });

      component.downloadImage();

      expect(fakeLink.download).toBe('space-image.png');
      expect(clickSpy).toHaveBeenCalled();
    });

    it('should not trigger download when no image is available', () => {
      jest.spyOn(store, 'selectSignal').mockReturnValue((() => null) as any);
      const createElementSpy = jest.spyOn(document, 'createElement');

      component.downloadImage();

      expect(createElementSpy).not.toHaveBeenCalled();
    });
  });

  describe('describeBase', () => {
    it('should return base description from form values', () => {
      component['baseForm'].patchValue({ width: 1920, height: 1080, seed: 42, octaves: 3, noiseType: 'FBM' });
      expect(component.describeBase()).toBe('FBM 3oct — 1920×1080 — seed 42');
    });
  });

  describe('describeCosmetic', () => {
    it('should return opaque cosmetic description from form values', () => {
      component['cosmeticForm'].patchValue({
        backgroundColor: '#6b2d8b', middleColor: '#ff9500', foregroundColor: '#ffffff',
        backThreshold: 0.3, middleThreshold: 0.7, interpolationType: 'LINEAR', transparentBackground: false
      });
      expect(component.describeCosmetic()).toBe('#6b2d8b → #ff9500 → #ffffff, seuils 0.30/0.70, opaque');
    });

    it('should indicate transparent background in cosmetic description', () => {
      component['cosmeticForm'].patchValue({ transparentBackground: true, backThreshold: 0.3, middleThreshold: 0.7, interpolationType: 'LINEAR' });
      expect(component.describeCosmetic()).toContain('transparent');
    });
  });

  describe('accordion layout', () => {
    it('should render mat-expansion-panel', () => {
      const panel = fixture.debugElement.query(By.css('mat-expansion-panel'));
      expect(panel).toBeTruthy();
    });

    it('should have two parameter columns', () => {
      const columns = fixture.debugElement.queryAll(By.css('.param-column'));
      expect(columns.length).toBe(2);
    });
  });
});
