import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBar } from '@angular/material/snack-bar';
import { GalaxyParamComponent } from './galaxy-param.component';
import { GalaxyService } from '../galaxy.service';

describe('GalaxyParamComponent', () => {
  let component: GalaxyParamComponent;
  let fixture: ComponentFixture<GalaxyParamComponent>;
  let snackBar: jest.Mocked<MatSnackBar>;
  let galaxyService: jest.Mocked<GalaxyService>;

  beforeEach(async () => {
    snackBar = { open: jest.fn() } as any;
    galaxyService = {
      buildGalaxy: jest.fn(),
      createGalaxy: jest.fn(),
      getAllGalaxies: jest.fn()
    } as any;

    await TestBed.configureTestingModule({
      imports: [
        GalaxyParamComponent,
        ReactiveFormsModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: MatSnackBar, useValue: snackBar },
        { provide: GalaxyService, useValue: galaxyService }
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
    function findDownloadButton(): HTMLButtonElement {
      const buttons = fixture.nativeElement.querySelectorAll('button');
      return Array.from(buttons).find((b: any) => b.textContent.trim() === 'Telecharger') as HTMLButtonElement;
    }

    it('should have download button disabled when no image is generated', () => {
      component.generatedImageUrl = null;
      fixture.detectChanges();
      const downloadButton = findDownloadButton();
      expect(downloadButton).toBeTruthy();
      expect(downloadButton.disabled).toBeTruthy();
    });

    it('should have download button disabled while generating', () => {
      component.generatedImageUrl = 'blob:http://localhost/fake';
      component.isGenerating = true;
      fixture.detectChanges();
      const downloadButton = findDownloadButton();
      expect(downloadButton.disabled).toBeTruthy();
    });

    it('should have download button enabled when image is generated and not generating', () => {
      component.generatedImageUrl = 'blob:http://localhost/fake';
      component.isGenerating = false;
      fixture.detectChanges();
      const downloadButton = findDownloadButton();
      expect(downloadButton.disabled).toBeFalsy();
    });
  });

  describe('downloadImage', () => {
    it('should create an anchor element and trigger download with form name', () => {
      component.generatedImageUrl = 'blob:http://localhost/fake-blob-url';

      const clickSpy = jasmine.createSpy('click');
      const fakeLink = { href: '', download: '', click: clickSpy } as any;
      spyOn(document, 'createElement').and.returnValue(fakeLink);

      component.galaxyForm.patchValue({ name: 'my-galaxy' });

      component.downloadImage();

      expect(document.createElement).toHaveBeenCalledWith('a');
      expect(fakeLink.href).toBe('blob:http://localhost/fake-blob-url');
      expect(fakeLink.download).toBe('my-galaxy.png');
      expect(clickSpy).toHaveBeenCalled();
    });

    it('should use default name when form name is empty', () => {
      component.generatedImageUrl = 'blob:http://localhost/fake-blob-url';

      const clickSpy = jasmine.createSpy('click');
      const fakeLink = { href: '', download: '', click: clickSpy } as any;
      spyOn(document, 'createElement').and.returnValue(fakeLink);

      component.galaxyForm.patchValue({ name: '' });

      component.downloadImage();

      expect(fakeLink.download).toBe('galaxy-image.png');
      expect(clickSpy).toHaveBeenCalled();
    });

    it('should not trigger download when no image is available', () => {
      component.generatedImageUrl = null;
      const createElementSpy = spyOn(document, 'createElement');

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
      expect(component.getDownloadTooltip()).toContain('Telecharger');
    });
  });
});
