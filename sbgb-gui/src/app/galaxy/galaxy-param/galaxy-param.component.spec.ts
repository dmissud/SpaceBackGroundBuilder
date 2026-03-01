import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { provideMockStore } from '@ngrx/store/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { of } from 'rxjs';
import { GalaxyParamComponent } from './galaxy-param.component';
import { GalaxyService } from '../galaxy.service';

describe('GalaxyParamComponent', () => {
  let component: GalaxyParamComponent;
  let fixture: ComponentFixture<GalaxyParamComponent>;
  let snackBar: jest.Mocked<MatSnackBar>;
  let galaxyService: jest.Mocked<GalaxyService>;
  let dialog: jest.Mocked<MatDialog>;
  let actions$ = of();

  afterEach(() => {
    jest.restoreAllMocks();
  });

  beforeEach(async () => {
    snackBar = { open: jest.fn() } as any;
    dialog = { open: jest.fn() } as any;
    galaxyService = {
      buildGalaxy: jest.fn(),
      rateGalaxy: jest.fn(),
      getAllBases: jest.fn(),
      getState: jest.fn().mockReturnValue(null),
      saveState: jest.fn(),
      galaxySaved$: { next: jest.fn(), pipe: jest.fn() }
    } as any;

    await TestBed.configureTestingModule({
      imports: [
        GalaxyParamComponent,
        ReactiveFormsModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: MatSnackBar, useValue: snackBar },
        { provide: MatDialog, useValue: dialog },
        { provide: GalaxyService, useValue: galaxyService },
        FormBuilder,
        provideMockStore(),
        provideMockActions(() => actions$)
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GalaxyParamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('UI State - Download button', () => {
    it('should have download button disabled when no image is generated', () => {
      component.generatedImageUrl = null;
      expect(component.canDownload()).toBeFalsy();
    });

    it('should have download button disabled while generating', () => {
      component.generatedImageUrl = 'blob:http://localhost/fake';
      component.isGenerating = true;
      expect(component.canDownload()).toBeFalsy();
    });

    it('should have download button enabled when image is generated and not generating', () => {
      component.generatedImageUrl = 'blob:http://localhost/fake';
      component.isGenerating = false;
      (component as any).isModifiedSinceBuild = false;
      expect(component.canDownload()).toBeTruthy();
    });
  });

  describe('downloadImage', () => {
    it('should create an anchor element and trigger download', () => {
      component.generatedImageUrl = 'blob:http://localhost/fake-blob-url';

      const clickSpy = jest.fn();
      const fakeLink = { href: '', download: '', click: clickSpy } as any;
      jest.spyOn(document, 'createElement').mockReturnValue(fakeLink);

      component.downloadImage();

      expect(document.createElement).toHaveBeenCalledWith('a');
      expect(fakeLink.href).toBe('blob:http://localhost/fake-blob-url');
      expect(fakeLink.download).toBe('galaxy-image.png');
      expect(clickSpy).toHaveBeenCalled();
    });

    it('should not trigger download when no image is available', () => {
      component.generatedImageUrl = null;
      const createElementSpy = jest.spyOn(document, 'createElement');

      component.downloadImage();

      expect(createElementSpy).not.toHaveBeenCalled();
    });
  });

  describe('canDownload', () => {
    it('should return false when no image is generated', () => {
      component.generatedImageUrl = null;
      component.isGenerating = false;
      expect(component.canDownload()).toBeFalsy();
    });

    it('should return false when generating', () => {
      component.generatedImageUrl = 'blob:http://localhost/fake';
      component.isGenerating = true;
      expect(component.canDownload()).toBeFalsy();
    });

    it('should return true when image exists and not generating', () => {
      component.generatedImageUrl = 'blob:http://localhost/fake';
      component.isGenerating = false;
      (component as any).isModifiedSinceBuild = false;
      expect(component.canDownload()).toBeTruthy();
    });
  });

  describe('getDownloadTooltip', () => {
    it('should return generating message when generating', () => {
      component.isGenerating = true;
      expect(component.getDownloadTooltip()).toContain('Generation en cours');
    });

    it('should return generate first message when no image', () => {
      component.isGenerating = false;
      component.generatedImageUrl = null;
      expect(component.getDownloadTooltip()).toContain('generer une image');
    });

    it('should return download message when image is ready', () => {
      component.isGenerating = false;
      component.generatedImageUrl = 'blob:http://localhost/fake';
      (component as any).isModifiedSinceBuild = false;
      expect(component.getDownloadTooltip()).toContain('Telecharger');
    });
  });
});
