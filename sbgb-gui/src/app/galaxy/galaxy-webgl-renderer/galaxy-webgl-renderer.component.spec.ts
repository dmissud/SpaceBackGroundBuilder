import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GalaxyWebglRendererComponent } from './galaxy-webgl-renderer.component';
import { StarParticleDto } from '../galaxy.model';

describe('GalaxyWebglRendererComponent', () => {
  let component: GalaxyWebglRendererComponent;
  let fixture: ComponentFixture<GalaxyWebglRendererComponent>;

  const mockParticles: StarParticleDto[] = [
    { theta0: 0, velTheta: 0.01, tiltAngle: 0.5, semiMajorAxis: 5000, semiMinorAxis: 4000, temperature: 6000, magnitude: 0.8, type: 'STAR' },
    { theta0: 1.57, velTheta: 0.02, tiltAngle: 1.0, semiMajorAxis: 8000, semiMinorAxis: 6000, temperature: 3500, magnitude: 0.4, type: 'DUST' },
  ];

  beforeEach(async () => {
    HTMLCanvasElement.prototype.getContext = () => null;

    await TestBed.configureTestingModule({
      imports: [GalaxyWebglRendererComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(GalaxyWebglRendererComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should have a canvas element', () => {
    fixture.detectChanges();
    const canvas = fixture.nativeElement.querySelector('canvas');
    expect(canvas).toBeTruthy();
  });

  it('should accept particles input', () => {
    component.particles = mockParticles;
    fixture.detectChanges();
    expect(component.particles.length).toBe(2);
  });

  it('should accept galaxyRadius input', () => {
    component.galaxyRadius = 15000;
    fixture.detectChanges();
    expect(component.galaxyRadius).toBe(15000);
  });

  it('should default to empty particles', () => {
    fixture.detectChanges();
    expect(component.particles).toEqual([]);
  });
});
