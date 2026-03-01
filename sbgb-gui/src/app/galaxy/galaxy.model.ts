export interface NoiseParameters {
  octaves: number;
  persistence: number;
  lacunarity: number;
  scale: number;
}

export interface SpiralParameters {
  numberOfArms?: number;
  armWidth?: number;
  armRotation?: number;
  darkLaneOpacity?: number;
}

export interface VoronoiParameters {
  clusterCount?: number;
  clusterSize?: number;
  clusterConcentration?: number;
}

export interface EllipticalParameters {
  sersicIndex?: number;
  axisRatio?: number;
  orientationAngle?: number;
}

export interface RingParameters {
  ringRadius?: number;
  ringWidth?: number;
  ringIntensity?: number;
  coreToRingRatio?: number;
}

export interface IrregularParameters {
  irregularity?: number;
  irregularClumpCount?: number;
  irregularClumpSize?: number;
}

export interface StarFieldParameters {
  density: number;
  maxStarSize: number;
  diffractionSpikes: boolean;
  spikeCount: number;
}

export interface MultiLayerNoiseParameters {
  enabled: boolean;
  macroLayerScale: number;
  macroLayerWeight: number;
  mesoLayerScale: number;
  mesoLayerWeight: number;
  microLayerScale: number;
  microLayerWeight: number;
}

export interface BloomParameters {
  enabled: boolean;
  bloomRadius: number;
  bloomIntensity: number;
  bloomThreshold: number;
}

export interface ColorParameters {
  colorPalette?: string;
  spaceBackgroundColor?: string;
  coreColor?: string;
  armColor?: string;
  outerColor?: string;
}

export interface GalaxyRequestCmd {
  id?: string;
  description: string;
  note: number;
  width: number;
  height: number;
  seed: number;
  galaxyType?: string;
  coreSize: number;
  galaxyRadius: number;
  warpStrength: number;
  noiseParameters: NoiseParameters;
  spiralParameters?: SpiralParameters;
  voronoiParameters?: VoronoiParameters;
  ellipticalParameters?: EllipticalParameters;
  ringParameters?: RingParameters;
  irregularParameters?: IrregularParameters;
  starFieldParameters: StarFieldParameters;
  multiLayerNoiseParameters: MultiLayerNoiseParameters;
  bloomParameters: BloomParameters;
  colorParameters: ColorParameters;
}

export interface GalaxyBaseStructureDto {
  id: string;
  description: string;
  maxNote: number;
  width: number;
  height: number;
  seed: number;
  galaxyType: string;
  coreSize: number;
  galaxyRadius: number;
  warpStrength: number;
  noiseOctaves: number;
  noisePersistence: number;
  noiseLacunarity: number;
  noiseScale: number;
  multiLayerEnabled: boolean;
  macroLayerScale: number;
  macroLayerWeight: number;
  mesoLayerScale: number;
  mesoLayerWeight: number;
  microLayerScale: number;
  microLayerWeight: number;
  structureParams: string;
  // Spiral
  numberOfArms?: number;
  armWidth?: number;
  armRotation?: number;
  darkLaneOpacity?: number;
  // Voronoi
  clusterCount?: number;
  clusterSize?: number;
  clusterConcentration?: number;
  // Elliptical
  sersicIndex?: number;
  axisRatio?: number;
  orientationAngle?: number;
  // Ring
  ringRadius?: number;
  ringWidth?: number;
  ringIntensity?: number;
  coreToRingRatio?: number;
  // Irregular
  irregularity?: number;
  irregularClumpCount?: number;
  irregularClumpSize?: number;
}

export interface GalaxyCosmeticRenderDto {
  id: string;
  baseStructureId: string;
  description: string;
  note: number;
  thumbnail: string;
  colorPalette: string;
  spaceBackgroundColor: string;
  coreColor: string;
  armColor: string;
  outerColor: string;
  bloomEnabled: boolean;
  bloomRadius: number;
  bloomIntensity: number;
  bloomThreshold: number;
  starFieldEnabled: boolean;
  starDensity: number;
  maxStarSize: number;
  diffractionSpikes: boolean;
  spikeCount: number;
}

export interface GalaxyPersistedState {
  formValue: any;
  generatedImageUrl: string | null;
  isModifiedSinceBuild: boolean;
  builtGalaxyParams: GalaxyRequestCmd | null;
}
