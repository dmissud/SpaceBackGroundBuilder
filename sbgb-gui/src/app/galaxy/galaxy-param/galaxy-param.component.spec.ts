import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Store } from '@ngrx/store';
import { provideMockStore } from '@ngrx/store/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { of } from 'rxjs';
import { GalaxyParamComponent } from './galaxy-param.component';
import { GalaxyService } from '../galaxy.service';
import { Actions } from '@ngrx/effects';
import { Component, Input } from '@angular/core';
import {BasicInfoSectionComponent} from "./sections/basic-info-section.component";
import {PresetsSectionComponent} from "./sections/presets-section.component";
import {SpiralStructureSectionComponent} from "./sections/spiral-structure-section.component";
import {VoronoiStructureSectionComponent} from "./sections/voronoi-structure-section.component";
import {EllipticalStructureSectionComponent} from "./sections/elliptical-structure-section.component";
import {RingStructureSectionComponent} from "./sections/ring-structure-section.component";
import {IrregularStructureSectionComponent} from "./sections/irregular-structure-section.component";
import {CoreRadiusSectionComponent} from "./sections/core-radius-section.component";
import {NoiseTextureSectionComponent} from "./sections/noise-texture-section.component";
import {VisualEffectsSectionComponent} from "./sections/visual-effects-section.component";
import {ColorsSectionComponent} from "./sections/colors-section.component";

@Component({ selector: 'app-basic-info-section', template: '', standalone: true })
class MockBasicInfoSectionComponent { @Input() form: any; @Input() expanded: any; }
@Component({ selector: 'app-presets-section', template: '', standalone: true })
class MockPresetsSectionComponent { @Input() form: any; @Input() expanded: any; }
@Component({ selector: 'app-spiral-structure-section', template: '', standalone: true })
class MockSpiralStructureSectionComponent { @Input() form: any; @Input() expanded: any; }
@Component({ selector: 'app-voronoi-structure-section', template: '', standalone: true })
class MockVoronoiStructureSectionComponent { @Input() form: any; @Input() expanded: any; }
@Component({ selector: 'app-elliptical-structure-section', template: '', standalone: true })
class MockEllipticalStructureSectionComponent { @Input() form: any; @Input() expanded: any; }
@Component({ selector: 'app-ring-structure-section', template: '', standalone: true })
class MockRingStructureSectionComponent { @Input() form: any; @Input() expanded: any; }
@Component({ selector: 'app-irregular-structure-section', template: '', standalone: true })
class MockIrregularStructureSectionComponent { @Input() form: any; @Input() expanded: any; }
@Component({ selector: 'app-core-radius-section', template: '', standalone: true })
class MockCoreRadiusSectionComponent { @Input() form: any; @Input() expanded: any; }
@Component({ selector: 'app-noise-texture-section', template: '', standalone: true })
class MockNoiseTextureSectionComponent { @Input() form: any; @Input() expanded: any; }
@Component({ selector: 'app-visual-effects-section', template: '', standalone: true })
class MockVisualEffectsSectionComponent { @Input() form: any; @Input() expanded: any; }
@Component({ selector: 'app-colors-section', template: '', standalone: true })
class MockColorsSectionComponent { @Input() form: any; @Input() expanded: any; }

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
        ReactiveFormsModule,
        NoopAnimationsModule,
        MatIconModule,
        MatTooltipModule,
        MockBasicInfoSectionComponent,
        MockPresetsSectionComponent,
        MockSpiralStructureSectionComponent,
        MockVoronoiStructureSectionComponent,
        MockEllipticalStructureSectionComponent,
        MockRingStructureSectionComponent,
        MockIrregularStructureSectionComponent,
        MockCoreRadiusSectionComponent,
        MockNoiseTextureSectionComponent,
        MockVisualEffectsSectionComponent,
        MockColorsSectionComponent
      ],
      providers: [
        { provide: MatSnackBar, useValue: snackBar },
        { provide: MatDialog, useValue: dialog },
        { provide: GalaxyService, useValue: galaxyService },
        { provide: Actions, useValue: of() },
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
