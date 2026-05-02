import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GalaxyWebglRendererComponent } from './galaxy-webgl-renderer.component';
import { StarParticleDto } from '../galaxy.model';

describe('GalaxyWebglRendererComponent', () => {
  let component: GalaxyWebglRendererComponent;
  let fixture: ComponentFixture<GalaxyWebglRendererComponent>;

  const mockParticles: StarParticleDto[] = [
    { theta0: 0, velTheta: 0.01, tiltAngle: 0.5, semiMajorAxis: 5000, semiMinorAxis: 4000, temperature: 6000, magnitude: 0.8, type: 'STAR' },
    { theta0: 1.57, velTheta: 0.02, tiltAngle: 1.0, semiMajorAxis: 8000, semiMinorAxis: 6000, temperature: 3500, magnitude: 0.4, type: 'DUST' },
    { theta0: 1.2, velTheta: 0.01, tiltAngle: 0.7, semiMajorAxis: 9000, semiMinorAxis: 7000, temperature: 8000, magnitude: 0.9, type: 'H2_OUTER' },
    { theta0: 1.2, velTheta: 0.01, tiltAngle: 0.7, semiMajorAxis: 9000, semiMinorAxis: 7000, temperature: 10000, magnitude: 1.0, type: 'H2_CORE' },
  ];

  beforeEach(async () => {
    HTMLCanvasElement.prototype.getContext = () => null;

    await TestBed.configureTestingModule({
      imports: [GalaxyWebglRendererComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(GalaxyWebglRendererComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('création et DOM', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should have a canvas element', () => {
      const canvas = fixture.nativeElement.querySelector('canvas');
      expect(canvas).toBeTruthy();
    });
  });

  describe('inputs par défaut', () => {
    it('should default to empty particles', () => {
      expect(component.particles).toEqual([]);
    });

    it('should default zoom to 0.9', () => {
      expect(component.zoom).toBe(0.9);
    });

    it('should default isRealSize to false', () => {
      expect(component.isRealSize).toBe(false);
    });

    it('should default isFullscreen to false', () => {
      expect(component.isFullscreen).toBe(false);
    });

    it('should default showStars to true', () => {
      expect(component.showStars).toBe(true);
    });

    it('should default showDust to true', () => {
      expect(component.showDust).toBe(true);
    });

    it('should default showFilaments to true', () => {
      expect(component.showFilaments).toBe(true);
    });

    it('should default showH2 to true', () => {
      expect(component.showH2).toBe(true);
    });
  });

  describe('input bindings', () => {
    it('should accept particles input', () => {
      component.particles = mockParticles;
      fixture.detectChanges();
      expect(component.particles.length).toBe(4);
    });

    it('should accept galaxyRadius input', () => {
      component.galaxyRadius = 15000;
      fixture.detectChanges();
      expect(component.galaxyRadius).toBe(15000);
    });

    it('should accept size input', () => {
      component.size = 1024;
      fixture.detectChanges();
      expect(component.size).toBe(1024);
    });

    it('should accept dustSize input', () => {
      component.dustSize = 50;
      fixture.detectChanges();
      expect(component.dustSize).toBe(50);
    });

    it('should accept pertN input', () => {
      component.pertN = 4;
      fixture.detectChanges();
      expect(component.pertN).toBe(4);
    });

    it('should accept pertAmp input', () => {
      component.pertAmp = 25;
      fixture.detectChanges();
      expect(component.pertAmp).toBe(25);
    });
  });

  describe('toggleRealSize', () => {
    it('should toggle isRealSize from false to true', () => {
      component.toggleRealSize();
      expect(component.isRealSize).toBe(true);
    });

    it('should toggle isRealSize back to false', () => {
      component.toggleRealSize();
      component.toggleRealSize();
      expect(component.isRealSize).toBe(false);
    });

    it('should apply is-real-size CSS class on canvas-scroll when enabled', () => {
      component.toggleRealSize();
      fixture.detectChanges();
      const scroll = fixture.nativeElement.querySelector('.canvas-scroll');
      expect(scroll.classList.contains('is-real-size')).toBe(true);
    });

    it('should remove is-real-size CSS class when disabled', () => {
      component.toggleRealSize();
      component.toggleRealSize();
      fixture.detectChanges();
      const scroll = fixture.nativeElement.querySelector('.canvas-scroll');
      expect(scroll.classList.contains('is-real-size')).toBe(false);
    });
  });

  describe('toggleFullscreen', () => {
    it('should call requestFullscreen on container when not in fullscreen', () => {
      const container = fixture.nativeElement.querySelector('.webgl-wrapper');
      const mockFn = jest.fn().mockResolvedValue(undefined);
      container.requestFullscreen = mockFn;
      component.toggleFullscreen();
      expect(mockFn).toHaveBeenCalled();
    });

    it('should call document.exitFullscreen when already in fullscreen', () => {
      component.isFullscreen = true;
      const mockFn = jest.fn().mockResolvedValue(undefined);
      Object.defineProperty(document, 'exitFullscreen', { value: mockFn, configurable: true, writable: true });
      component.toggleFullscreen();
      expect(mockFn).toHaveBeenCalled();
    });
  });

  describe('onFullscreenChange', () => {
    afterEach(() => {
      Object.defineProperty(document, 'fullscreenElement', { value: null, configurable: true });
    });

    it('should set isFullscreen to true when fullscreenElement is set', () => {
      Object.defineProperty(document, 'fullscreenElement', { value: document.body, configurable: true });
      component.onFullscreenChange();
      expect(component.isFullscreen).toBe(true);
    });

    it('should set isFullscreen to false when fullscreenElement is null', () => {
      component.isFullscreen = true;
      Object.defineProperty(document, 'fullscreenElement', { value: null, configurable: true });
      component.onFullscreenChange();
      expect(component.isFullscreen).toBe(false);
    });
  });

  describe('onZoomChange', () => {
    it('should not throw when WebGL context is unavailable', () => {
      expect(() => component.onZoomChange()).not.toThrow();
    });
  });

  describe('isParticleVisible', () => {
    it('should show STAR when showStars is true', () => {
      component.showStars = true;
      expect((component as any).isParticleVisible('STAR')).toBe(true);
    });

    it('should hide STAR when showStars is false', () => {
      component.showStars = false;
      expect((component as any).isParticleVisible('STAR')).toBe(false);
    });

    it('should show DUST when showDust is true', () => {
      component.showDust = true;
      expect((component as any).isParticleVisible('DUST')).toBe(true);
    });

    it('should hide DUST when showDust is false', () => {
      component.showDust = false;
      expect((component as any).isParticleVisible('DUST')).toBe(false);
    });

    it('should show FILAMENT when showFilaments is true', () => {
      component.showFilaments = true;
      expect((component as any).isParticleVisible('FILAMENT')).toBe(true);
    });

    it('should hide FILAMENT when showFilaments is false', () => {
      component.showFilaments = false;
      expect((component as any).isParticleVisible('FILAMENT')).toBe(false);
    });

    it('should show H2_OUTER when showH2 is true', () => {
      component.showH2 = true;
      expect((component as any).isParticleVisible('H2_OUTER')).toBe(true);
    });

    it('should hide H2_OUTER when showH2 is false', () => {
      component.showH2 = false;
      expect((component as any).isParticleVisible('H2_OUTER')).toBe(false);
    });

    it('should show H2_CORE when showH2 is true', () => {
      component.showH2 = true;
      expect((component as any).isParticleVisible('H2_CORE')).toBe(true);
    });

    it('should hide H2_CORE when showH2 is false', () => {
      component.showH2 = false;
      expect((component as any).isParticleVisible('H2_CORE')).toBe(false);
    });
  });

  describe('typeToFloat', () => {
    it('should map STAR to 0.0', () => {
      expect((component as any).typeToFloat('STAR')).toBe(0.0);
    });

    it('should map DUST to 1.0', () => {
      expect((component as any).typeToFloat('DUST')).toBe(1.0);
    });

    it('should map FILAMENT to 2.0', () => {
      expect((component as any).typeToFloat('FILAMENT')).toBe(2.0);
    });

    it('should map H2_OUTER to 3.0', () => {
      expect((component as any).typeToFloat('H2_OUTER')).toBe(3.0);
    });

    it('should map H2_CORE to 4.0', () => {
      expect((component as any).typeToFloat('H2_CORE')).toBe(4.0);
    });

    it('should map unknown type to 2.0 (filament fallback)', () => {
      expect((component as any).typeToFloat('UNKNOWN')).toBe(2.0);
    });
  });

  describe('animation loop', () => {
    let rafSpy: jest.SpyInstance;
    let cafSpy: jest.SpyInstance;

    beforeEach(() => {
      rafSpy = jest.spyOn(window, 'requestAnimationFrame').mockReturnValue(42 as any);
      cafSpy = jest.spyOn(window, 'cancelAnimationFrame').mockImplementation(() => {});
    });

    afterEach(() => {
      rafSpy.mockRestore();
      cafSpy.mockRestore();
    });

    it('should default isAnimating to false', () => {
      expect(component.isAnimating).toBe(false);
    });

    it('should set isAnimating to true when startAnimation is called', () => {
      component.startAnimation();
      expect(component.isAnimating).toBe(true);
    });

    it('should schedule a requestAnimationFrame when startAnimation is called', () => {
      component.startAnimation();
      expect(rafSpy).toHaveBeenCalled();
    });

    it('should not schedule a second RAF if animation already running', () => {
      component.startAnimation();
      component.startAnimation();
      expect(rafSpy).toHaveBeenCalledTimes(1);
    });

    it('should set isAnimating to false when stopAnimation is called', () => {
      component.startAnimation();
      component.stopAnimation();
      expect(component.isAnimating).toBe(false);
    });

    it('should call cancelAnimationFrame when stopAnimation is called', () => {
      component.startAnimation();
      component.stopAnimation();
      expect(cafSpy).toHaveBeenCalledWith(42);
    });

    it('should not throw when stopAnimation called without prior start', () => {
      expect(() => component.stopAnimation()).not.toThrow();
    });

    it('should stop animation on ngOnDestroy', () => {
      component.startAnimation();
      component.ngOnDestroy();
      expect(component.isAnimating).toBe(false);
      expect(cafSpy).toHaveBeenCalled();
    });
  });

  describe('exportPng', () => {
    let createElementSpy: jest.SpyInstance;
    let mockLink: { href: string; download: string; click: jest.Mock };

    beforeEach(() => {
      mockLink = { href: '', download: '', click: jest.fn() };
      createElementSpy = jest.spyOn(document, 'createElement').mockImplementation((tag: string) => {
        if (tag === 'a') return mockLink as unknown as HTMLElement;
        return document.createElement(tag);
      });
      HTMLCanvasElement.prototype.toDataURL = jest.fn().mockReturnValue('data:image/png;base64,ABC');
    });

    afterEach(() => {
      createElementSpy.mockRestore();
    });

    it('should call toDataURL on the canvas', () => {
      component.exportPng();
      expect(HTMLCanvasElement.prototype.toDataURL).toHaveBeenCalledWith('image/png');
    });

    it('should set link href to canvas data URL', () => {
      component.exportPng();
      expect(mockLink.href).toBe('data:image/png;base64,ABC');
    });

    it('should set default download filename to density-wave-galaxy.png', () => {
      component.exportPng();
      expect(mockLink.download).toBe('density-wave-galaxy.png');
    });

    it('should set custom download filename when provided', () => {
      component.exportPng('my-galaxy.png');
      expect(mockLink.download).toBe('my-galaxy.png');
    });

    it('should trigger click on the link', () => {
      component.exportPng();
      expect(mockLink.click).toHaveBeenCalled();
    });
  });

  describe('H2 geometry', () => {
    it('should default h2Scale to 1.0', () => {
      expect(component.h2Scale).toBe(1.0);
    });

    it('should accept h2Scale input', () => {
      component.h2Scale = 2.0;
      expect(component.h2Scale).toBe(2.0);
    });

    it('should clamp h2Scale to minimum 0.1 to avoid invisible H2 regions', () => {
      component.h2Scale = 0.0;
      fixture.detectChanges();
      expect(component.h2Scale).toBeGreaterThanOrEqual(0.1);
    });

    it('should not throw when particles include H2_OUTER type', () => {
      const h2Particles: StarParticleDto[] = [
        { theta0: 0.5, velTheta: 0.01, tiltAngle: 0.2, semiMajorAxis: 8000, semiMinorAxis: 6000, temperature: 7000, magnitude: 0.9, type: 'H2_OUTER' }
      ];
      expect(() => {
        component.particles = h2Particles;
        fixture.detectChanges();
      }).not.toThrow();
    });

    it('should not throw when particles include H2_CORE type', () => {
      const h2Particles: StarParticleDto[] = [
        { theta0: 0.5, velTheta: 0.01, tiltAngle: 0.2, semiMajorAxis: 8000, semiMinorAxis: 6000, temperature: 10000, magnitude: 1.0, type: 'H2_CORE' }
      ];
      expect(() => {
        component.particles = h2Particles;
        fixture.detectChanges();
      }).not.toThrow();
    });
  });
});
