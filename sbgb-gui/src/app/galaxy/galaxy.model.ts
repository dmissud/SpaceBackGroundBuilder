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

export interface ColorParameters {
  colorPalette?: string;
  spaceBackgroundColor?: string;
  coreColor?: string;
  armColor?: string;
  outerColor?: string;
}

export interface GalaxyImageDTO {
  id: string;
  name: string;
  description: string;
  note: number;
  galaxyStructure: GalaxyStructureDTO;
}

export interface GalaxyStructureDTO {
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
  colorParameters: ColorParameters;
}

export interface GalaxyRequestCmd {
  name: string;
  description: string;
  width: number;
  height: number;
  seed: number;
  forceUpdate?: boolean;
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
  colorParameters: ColorParameters;
}
