import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {MatSnackBar} from '@angular/material/snack-bar';
import {provideMockStore, MockStore} from '@ngrx/store/testing';
import {provideMockActions} from '@ngrx/effects/testing';
import {Observable, of} from 'rxjs';
import {SbgbParamComponent} from './sbgb-param.component';
import {SbgbPageActions} from '../state/sbgb.actions';
import {Sbgb} from '../sbgb.model';

describe('SbgbParamComponent', () => {
  let component: SbgbParamComponent;
  let fixture: ComponentFixture<SbgbParamComponent>;
  let store: MockStore;
  let snackBar: jasmine.SpyObj<MatSnackBar>;
  let actions$: Observable<any>;

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
    snackBar = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [
        SbgbParamComponent,
        ReactiveFormsModule,
        NoopAnimationsModule
      ],
      providers: [
        {provide: MatSnackBar, useValue: snackBar},
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
      expect(component.isModified(current, mockSbgb)).toBeFalse();
    });

    it('should return true if name is modified', () => {
      const current = JSON.parse(JSON.stringify(mockSbgb));
      current.name = 'New Name';
      // @ts-ignore
      expect(component.isModified(current, mockSbgb)).toBeTrue();
    });

    it('should return true if description is modified', () => {
      const current = JSON.parse(JSON.stringify(mockSbgb));
      current.description = 'New Description';
      // @ts-ignore
      expect(component.isModified(current, mockSbgb)).toBeTrue();
    });

    it('should return true if width is modified', () => {
      const current = JSON.parse(JSON.stringify(mockSbgb));
      current.imageStructure.width = 1000;
      // @ts-ignore
      expect(component.isModified(current, mockSbgb)).toBeTrue();
    });

    it('should return true if color is modified', () => {
      const current = JSON.parse(JSON.stringify(mockSbgb));
      current.imageColor.back = '#123456';
      // @ts-ignore
      expect(component.isModified(current, mockSbgb)).toBeTrue();
    });

    it('should return true if threshold is modified', () => {
      const current = JSON.parse(JSON.stringify(mockSbgb));
      current.imageColor.backThreshold = 0.1;
      // @ts-ignore
      expect(component.isModified(current, mockSbgb)).toBeTrue();
    });
  });

  describe('saveImage', () => {
    beforeEach(() => {
      spyOn(store, 'dispatch');
      // @ts-ignore
      component.isBuilt = true;
      // @ts-ignore
      component.isModifiedSinceBuild = true;
    });

    it('should dispatch saveSbgb with forceUpdate=false when no image is loaded', () => {
      // @ts-ignore
      component.loadedFromDbSbgb = null;
      component['_myForm'].patchValue({name: 'New Image'});

      component.saveImage();

      expect(store.dispatch).toHaveBeenCalledWith(
        SbgbPageActions.saveSbgb({sbgb: jasmine.anything() as any, forceUpdate: false})
      );
    });

    it('should show snackbar and not dispatch when nothing is modified', () => {
      // @ts-ignore
      component.loadedFromDbSbgb = mockSbgb;
      // @ts-ignore - builtSbgb identique à loadedFromDbSbgb = pas de modification
      component.builtSbgb = JSON.parse(JSON.stringify(mockSbgb));
      // @ts-ignore
      component._myForm.patchValue({
        name: mockSbgb.name,
        description: mockSbgb.description,
        width: mockSbgb.imageStructure.width,
        height: mockSbgb.imageStructure.height,
        seed: mockSbgb.imageStructure.seed,
        octaves: mockSbgb.imageStructure.octaves,
        persistence: mockSbgb.imageStructure.persistence,
        lacunarity: mockSbgb.imageStructure.lacunarity,
        backgroundColor: mockSbgb.imageColor.back,
        middleColor: mockSbgb.imageColor.middle,
        foregroundColor: mockSbgb.imageColor.fore,
        backThreshold: mockSbgb.imageColor.backThreshold,
        middleThreshold: mockSbgb.imageColor.middleThreshold
      });

      component.saveImage();

      expect(snackBar.open).toHaveBeenCalledWith(jasmine.stringMatching(/n'a pas été modifiée/), 'OK', jasmine.any(Object));
      expect(store.dispatch).not.toHaveBeenCalled();
    });

    it('should ask for confirmation and dispatch with forceUpdate=true when name is same but content modified', () => {
      const modifiedSbgb = JSON.parse(JSON.stringify(mockSbgb));
      modifiedSbgb.description = 'Modified Description';
      // @ts-ignore
      component.loadedFromDbSbgb = mockSbgb;
      // @ts-ignore - builtSbgb avec description modifiée
      component.builtSbgb = modifiedSbgb;
      // @ts-ignore
      component._myForm.patchValue({
        name: mockSbgb.name,
        description: 'Modified Description'
      });
      spyOn(window, 'confirm').and.returnValue(true);

      component.saveImage();

      expect(window.confirm).toHaveBeenCalledWith(jasmine.stringMatching(/existe déjà et a été modifiée/));
      expect(store.dispatch).toHaveBeenCalledWith(
        SbgbPageActions.saveSbgb({sbgb: jasmine.anything() as any, forceUpdate: true})
      );
    });

    it('should ask for confirmation and dispatch with forceUpdate=false when name is modified', () => {
      const modifiedSbgb = JSON.parse(JSON.stringify(mockSbgb));
      modifiedSbgb.name = 'New Name';
      // @ts-ignore
      component.loadedFromDbSbgb = mockSbgb;
      // @ts-ignore - builtSbgb avec nom modifié
      component.builtSbgb = modifiedSbgb;
      // @ts-ignore
      component._myForm.patchValue({
        name: 'New Name',
        description: mockSbgb.description
      });
      spyOn(window, 'confirm').and.returnValue(true);

      component.saveImage();

      expect(window.confirm).toHaveBeenCalledWith(jasmine.stringMatching(/va être enregistrée/));
      expect(store.dispatch).toHaveBeenCalledWith(
        SbgbPageActions.saveSbgb({sbgb: jasmine.anything() as any, forceUpdate: false})
      );
    });
  });

  describe('UI State', () => {
    it('should have save button disabled if not built', () => {
      // @ts-ignore
      component.isBuilt = false;
      fixture.detectChanges();
      const saveButton = fixture.nativeElement.querySelector('button[color="accent"]');
      expect(saveButton.disabled).toBeTrue();
    });

    it('should have save button disabled if modified since build', () => {
      // @ts-ignore
      component.isBuilt = true;
      // @ts-ignore
      component.isModifiedSinceBuild = true;
      fixture.detectChanges();
      const saveButton = fixture.nativeElement.querySelector('button[color="accent"]');
      expect(saveButton.disabled).toBeTrue();
    });

    it('should have save button enabled if built and not modified since build', () => {
      // @ts-ignore
      component.isBuilt = true;
      // @ts-ignore
      component.isModifiedSinceBuild = false;
      // @ts-ignore - builtSbgb doit être défini pour que canSave() retourne true
      component.builtSbgb = mockSbgb;
      fixture.detectChanges();
      const saveButton = fixture.nativeElement.querySelector('button[color="accent"]');
      expect(saveButton.disabled).toBeFalse();
    });

    it('should have download button disabled if not built', () => {
      // @ts-ignore
      component.isBuilt = false;
      fixture.detectChanges();
      const buttons = fixture.nativeElement.querySelectorAll('button');
      const downloadButton = Array.from(buttons).find((b: any) => b.textContent.trim() === 'Telecharger') as HTMLButtonElement;
      expect(downloadButton).toBeTruthy();
      expect(downloadButton.disabled).toBeTrue();
    });

    it('should have download button disabled if modified since build', () => {
      // @ts-ignore
      component.isBuilt = true;
      // @ts-ignore
      component.isModifiedSinceBuild = true;
      fixture.detectChanges();
      const buttons = fixture.nativeElement.querySelectorAll('button');
      const downloadButton = Array.from(buttons).find((b: any) => b.textContent.trim() === 'Telecharger') as HTMLButtonElement;
      expect(downloadButton.disabled).toBeTrue();
    });

    it('should have download button enabled if built and not modified since build', () => {
      // @ts-ignore
      component.isBuilt = true;
      // @ts-ignore
      component.isModifiedSinceBuild = false;
      fixture.detectChanges();
      const buttons = fixture.nativeElement.querySelectorAll('button');
      const downloadButton = Array.from(buttons).find((b: any) => b.textContent.trim() === 'Telecharger') as HTMLButtonElement;
      expect(downloadButton.disabled).toBeFalse();
    });
  });

  describe('downloadImage', () => {
    it('should create an anchor element and trigger download with form name', () => {
      const fakeDataUrl = 'data:image/png;base64,iVBORw0KGgo=';
      spyOn(store, 'selectSignal').and.returnValue((() => fakeDataUrl) as any);

      const clickSpy = jasmine.createSpy('click');
      const fakeLink = {href: '', download: '', click: clickSpy} as any;
      spyOn(document, 'createElement').and.returnValue(fakeLink);

      component['_myForm'].patchValue({name: 'my-stars'}, {emitEvent: false});

      component.downloadImage();

      expect(document.createElement).toHaveBeenCalledWith('a');
      expect(fakeLink.href).toBe(fakeDataUrl);
      expect(fakeLink.download).toBe('my-stars.png');
      expect(clickSpy).toHaveBeenCalled();
    });

    it('should use default name when form name is empty', () => {
      const fakeDataUrl = 'data:image/png;base64,iVBORw0KGgo=';
      spyOn(store, 'selectSignal').and.returnValue((() => fakeDataUrl) as any);

      const clickSpy = jasmine.createSpy('click');
      const fakeLink = {href: '', download: '', click: clickSpy} as any;
      spyOn(document, 'createElement').and.returnValue(fakeLink);

      component['_myForm'].patchValue({name: ''}, {emitEvent: false});

      component.downloadImage();

      expect(fakeLink.download).toBe('space-image.png');
      expect(clickSpy).toHaveBeenCalled();
    });

    it('should not trigger download when no image is available', () => {
      spyOn(store, 'selectSignal').and.returnValue((() => null) as any);
      const createElementSpy = spyOn(document, 'createElement');

      component.downloadImage();

      expect(createElementSpy).not.toHaveBeenCalled();
    });
  });
});
