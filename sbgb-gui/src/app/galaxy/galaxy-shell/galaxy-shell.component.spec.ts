import { GalaxyShellComponent } from './galaxy-shell.component';
import { GalaxyWebglRendererComponent } from '../galaxy-webgl-renderer/galaxy-webgl-renderer.component';

describe('GalaxyShellComponent - downloadDensityWaveImage', () => {
  let component: GalaxyShellComponent;

  const mockStore = { selectSignal: jest.fn().mockReturnValue(() => []), dispatch: jest.fn() } as any;
  const mockCdr = { detectChanges: jest.fn() } as any;

  beforeEach(() => {
    component = new GalaxyShellComponent(mockStore, mockCdr);
  });

  it('should call exportPng on the webgl renderer when downloadDensityWaveImage is called', () => {
    const mockRenderer = { exportPng: jest.fn() } as unknown as GalaxyWebglRendererComponent;
    (component as any).webglRenderer = mockRenderer;

    component.downloadDensityWaveImage();

    expect(mockRenderer.exportPng).toHaveBeenCalled();
  });

  it('should not throw if webglRenderer is not available', () => {
    (component as any).webglRenderer = undefined;
    expect(() => component.downloadDensityWaveImage()).not.toThrow();
  });

  it('should use downloadDensityWaveImage in actionBarButtons when isDensityWave is true', () => {
    const mockExportPng = jest.fn();
    const mockRenderer = { exportPng: mockExportPng } as unknown as GalaxyWebglRendererComponent;
    (component as any).webglRenderer = mockRenderer;

    const mockParam = {
      canBuild: () => true,
      getBuildTooltip: () => '',
      canDownload: () => true,
      getDownloadTooltip: () => '',
      generateGalaxy: jest.fn(),
      downloadImage: jest.fn(),
      densityWaveParticles: [{ type: 'STAR' }],
    };
    (component as any).paramComponent = mockParam;

    const downloadBtn = component.actionBarButtons.find(b => b.label === 'Télécharger');
    downloadBtn!.action();

    expect(mockExportPng).toHaveBeenCalled();
    expect(mockParam.downloadImage).not.toHaveBeenCalled();
  });

  it('should use downloadImage in actionBarButtons when isDensityWave is false', () => {
    const mockParam = {
      canBuild: () => true,
      getBuildTooltip: () => '',
      canDownload: () => true,
      getDownloadTooltip: () => '',
      generateGalaxy: jest.fn(),
      downloadImage: jest.fn(),
      densityWaveParticles: [],
    };
    (component as any).paramComponent = mockParam;

    const downloadBtn = component.actionBarButtons.find(b => b.label === 'Télécharger');
    downloadBtn!.action();

    expect(mockParam.downloadImage).toHaveBeenCalled();
  });
});
